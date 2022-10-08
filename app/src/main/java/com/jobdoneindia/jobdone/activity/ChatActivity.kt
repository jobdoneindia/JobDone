package com.jobdoneindia.jobdone.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.RetrofitInstance
import com.jobdoneindia.jobdone.adapter.MessageAdapter
import java.util.*

import com.jobdoneindia.jobdone.activity.Message
import com.jobdoneindia.jobdone.firebase.FirebaseService
import com.jobdoneindia.jobdone.models.NotificationData
import com.jobdoneindia.jobdone.models.PushNotification
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.lang.Exception

const val TOPIC = "/topics/myTopic"
class ChatActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var scrollViewCustom: HorizontalScrollView
    private lateinit var scrollButton: ImageButton
    private lateinit var scrollBackButton: ImageButton
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var self_name: String
    var topic =""
    var receiverRoom: String? = null
    var senderRoom: String? = null


    val TAG = "ChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
       FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {

           FirebaseService.token = it.token

       }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        val intent = getIntent()
        val receiverUid = intent.getStringExtra("uid")
        var name = intent.getStringExtra("username").toString()

        val sharedPreferences = this.getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        self_name = sharedPreferences.getString("name_key", "Siraj Alarm").toString()

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById<RecyclerView?>(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        scrollViewCustom = findViewById(R.id.scrollViewCustomButtons)
        scrollButton = findViewById(R.id.btnScroll)
        scrollBackButton = findViewById(R.id.btnScrollBack)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // logic for adding data to recyclerView
        mDbRef.child("chats").child(senderRoom!!).child("messages")

            .addValueEventListener(object: ValueEventListener {

                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    chatRecyclerView.scrollToPosition(messageList.size)

                    messageList.clear()

                    for (postSnapshot in snapshot.children){
                        val message: Message?
                        message = postSnapshot.getValue(Message::class.java)
                        mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(message)
                        messageList.add(message!!)
                        chatRecyclerView.scrollToPosition(messageList.size-1)
                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        // scroll button
        scrollButton.setOnClickListener {
            scrollViewCustom.fullScroll(ScrollView.FOCUS_RIGHT)
            scrollButton.visibility = View.GONE
            scrollBackButton.visibility = View.VISIBLE
        }
        scrollBackButton.setOnClickListener {
            scrollViewCustom.fullScroll(ScrollView.FOCUS_LEFT)
            scrollButton.visibility = View.VISIBLE
            scrollBackButton.visibility = View.GONE
        }

        // adding the message to database
        sendButton.setOnClickListener {


            val message = messageBox.text.toString()
            /*if (message.isNotEmpty()){
            PushNotification(
                NotificationData(user,message),
                    TOPIC
            ).also {
               sendNotification(it)
            }

            }*/

//TODO: message
            if (message != ""){
                val messageObject = Message(message, senderUid, Date().time)

                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(messageObject)
                messageBox.setText("")
                topic = "/topics/$receiverUid"
                PushNotification(NotificationData(self_name,message),
                topic
                ).also {
                    sendNotification(it)
                }
            }
        }
    }
    val exceptionHandler = CoroutineExceptionHandler{_ , throwable->
        throwable.printStackTrace()
    }
private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        if (response.isSuccessful){
          Log.d(TAG, "Response: ${Gson().toJson(response)}")
        }else{
            Log.e(TAG, response.errorBody().toString())
        }
    }catch (e:Exception){
        Log.e(TAG, e.toString())
        /*Toast.makeText(this@ChatActivity,e.message.toString(),Toast.LENGTH_SHORT).show()*/
    }
}

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.chat_actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.getItemId()
        return if (id == R.id.action_call) {
            // TODO: Yuvichh idhar code daal call ka
            true
        } else super.onOptionsItemSelected(item)
    }



}
