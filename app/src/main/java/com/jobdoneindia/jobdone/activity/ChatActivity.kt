package com.jobdoneindia.jobdone.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.RetrofitInstance
import com.jobdoneindia.jobdone.adapter.MessageAdapter
import com.jobdoneindia.jobdone.firebase.FirebaseService
import com.jobdoneindia.jobdone.models.NotificationData
import com.jobdoneindia.jobdone.models.PushNotification
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var btnGetLocation: Button
    private lateinit var btnAskPayment: Button
    private lateinit var btnGetReview: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var scrollViewCustom: HorizontalScrollView
    private lateinit var scrollButton: ImageButton
    private lateinit var scrollBackButton: ImageButton
    private lateinit var mDbRef: DatabaseReference

    var receiverPhoneNum:String? = null
    var receiverUid:String? = null
    var senderUid:String?=null

    private lateinit var self_name: String
    var topic =""
    var receiverRoom: String? = null
    var senderRoom: String? = null
    val firebase : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    val TAG = "ChatActivity"
    var seenListener: ValueEventListener? = null

    val REQUEST_PHONE_CALL = 1


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val intent = getIntent()
        val receiverUid = intent.getStringExtra("uid")
        var name = intent.getStringExtra("username").toString()




        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
       FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {

            FirebaseService.token = it.token

        }


        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")

        val sharedPreferences = this.getSharedPreferences("usersharedpreference", MODE_PRIVATE)
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
        btnGetLocation = findViewById(R.id.btnAskLocation)
        btnAskPayment = findViewById(R.id.btnAskPayment)
        btnGetReview = findViewById(R.id.btnAskRating)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter


        // Logic for retrieving phone number of receiver from firebase database
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("Users").child(receiverUid.toString()).child("phoneNumber")

            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    receiverPhoneNum = snapshot.value.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity,"Can't make a call", Toast.LENGTH_SHORT).show()
                }
            })

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
/*
                        mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(message)
*/
                        messageList.add(message!!)
                        chatRecyclerView.scrollToPosition(messageList.size-1)
                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        /*seenMessage(userid)*/

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

            if (message != ""){

                val ukey = mDbRef.child("chats").child(senderRoom!!).child("messages").push().key
                val messageObject = Message(message, senderUid, receiverUid, Date().time, "text", "", ukey.toString() , false)
                mDbRef.child("chats").child(senderRoom!!).child("messages").child(ukey.toString())/*.push()*/
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(ukey.toString())/*.push()*/
                            .setValue(messageObject)
                    }
/*
                mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(messageObject)
*/
                messageBox.setText("")
                topic = "/topics/$receiverUid"
                PushNotification(NotificationData(self_name,message),
                    topic
                ).also {
                    sendNotification(it)
                }

            }else{
                Toast.makeText(this,"Enter some message ", Toast.LENGTH_SHORT).show()
            }
        }

        // Ask Location
        btnGetLocation.setOnClickListener {

            val ukey = mDbRef.child("chats").child(senderRoom!!).child("messages").push().key
            val messageObject = Message("Location", senderUid, receiverUid, Date().time, "location", "default", ukey.toString(), false)

            mDbRef.child("chats").child(senderRoom!!).child("messages").child(ukey.toString())/*.push()*/
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").child(ukey.toString())/*.push()*/
                        .setValue(messageObject)
                }
/*
            mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(messageObject)
*/
            messageBox.setText("")
            topic = "/topics/$receiverUid"
            PushNotification(NotificationData(self_name,"Location"),
                topic
            ).also {
                sendNotification(it)
            }
        }
      }
           val exceptionHandler = CoroutineExceptionHandler{_ , throwable->
           throwable.printStackTrace()
       }
           private fun sendNotification(notification: PushNotification) = CoroutineScope(IO + exceptionHandler).launch {
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



//FUNCTION FOR SEEN MESSAGE//

    fun seenMessage(userid : String){
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid =  FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child("chats").child(uid.toString())

      seenListener  = reference.addValueEventListener(object: ValueEventListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messageAdapter.notifyDataSetChanged()
                for (postSnapshot in snapshot.children){

                    val message: Message?
                    message = postSnapshot.getValue(Message::class.java)
                    if (message?.receiverId.equals(userid) && message?.senderId.equals(userid)){

                        var map : HashMap<String , Any>
                                = HashMap<String,Any>()

                        map.put("isseen" , true)
                        snapshot.ref.updateChildren(map)
                    }

                }
                  messageAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        //Ask Payment
        btnAskPayment.setOnClickListener {

            val ukey = mDbRef.child("chats").child(senderRoom!!).child("messages").push().key
            val messageObject = Message("Payment", senderUid, receiverUid, Date().time, "payment", "default", ukey.toString() , false)

            mDbRef.child("chats").child(senderRoom!!).child("messages").child(ukey.toString())/*.push()*/
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").child(ukey.toString())/*.push()*/
                        .setValue(messageObject)
                }
/*
            mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(messageObject)
*/
            messageBox.setText("")

        }

        // Ask Review
/*        btnGetReview.setOnClickListener {
            val ukey = mDbRef.child( "chats").child(senderRoom!!).child("messages").push().key
            val messageObject = Message("Review", senderUid, receiverUid, Date().time, "review", "default", ukey.toString())

            mDbRef.child("chats").child(senderRoom!!).child("messages").child(ukey.toString())
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").child(ukey.toString())
                        .setValue(messageObject)
                }
            mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(messageObject)
            messageBox.setText("")
        }*/

    }




    // TO SHOW STATUS
    fun status(status : String) {

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid =  FirebaseAuth.getInstance().currentUser?.uid
    val reference : DatabaseReference = database.reference.child("Users").child(uid.toString())

        var map : HashMap<String , Any>
                = HashMap<String,Any>()

        map.put("status" , status)
        reference.updateChildren(map)

    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
       mDbRef.removeEventListener(seenListener!!)
        status("offline")

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

            //Checks calling permission
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            }else{
                val dialIntent = Intent(Intent.ACTION_CALL)
                dialIntent.data = Uri.parse("tel:" + (receiverPhoneNum?.substring(3)))
                startActivity(dialIntent)
            }
            true
        } else super.onOptionsItemSelected(item)
    }



}
