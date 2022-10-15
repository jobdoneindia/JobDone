package com.jobdoneindia.jobdone.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jobdoneindia.jobdone.adapter.MessageAdapter
import com.jobdoneindia.jobdone.R
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var btnGetLocation: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var scrollViewCustom: HorizontalScrollView
    private lateinit var scrollButton: ImageButton
    private lateinit var scrollBackButton: ImageButton
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
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

            if (message != ""){
                val messageObject = Message(message, senderUid, Date().time, "text")

                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(messageObject)
                messageBox.setText("")
            }
            else{
                Toast.makeText(this,"Enter some message ", Toast.LENGTH_SHORT).show()
            }
        }

        // Ask Location
        btnGetLocation.setOnClickListener {
            val messageObject = Message("Location", senderUid, Date().time, "location")

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            mDbRef.child("Users").child("latest-messages").child(senderUid.toString()).child(receiverUid.toString()).child("msg").setValue(messageObject)
            messageBox.setText("")
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
