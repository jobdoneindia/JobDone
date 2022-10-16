package com.jobdoneindia.jobdone.activity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.geofire.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.UserAdapter
import com.jobdoneindia.jobdone.firebase.FirebaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

data class inboxItem(val uid: String, val username: String, val url: String, var lastMsg: String, var timestamp: Long)


class ChatUserList : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<inboxItem>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef : DatabaseReference
    private lateinit var mAuth:FirebaseAuth
    private var radius: Double = 1.0
    private var workerFound = false
    private var workerFoundID: String = ""

    private lateinit var timestamps: ArrayList<Long>
    private lateinit var sorted_timestamps: ArrayList<Long>
    private lateinit var recievers: ArrayList<String>

    private lateinit var unsortedUserList: ArrayList<inboxItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user_list)

       val firebase : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val receiverUid = intent.getStringExtra("uid")
        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")

        // Add back button in Action Bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        /*getClosestWorkers()*/

        mDbRef = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()

        userList = ArrayList()
        unsortedUserList = ArrayList()

        timestamps = ArrayList()
        sorted_timestamps = ArrayList()

        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)


        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        /*mDbRef.child("latest-messages").child(mAuth.currentUser?.uid.toString()).addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    latestMessages[postSnapshot.child("msg").child("senderId").value.toString()] = postSnapshot.child("msg").child("message").value.toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })*/

        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                /*unsortedUserList.clear()
                timestamps.clear()
                sorted_timestamps.clear()*/
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid && currentUser?.uid != null) {
                        /*if (workerFoundID == currentUser?.uid.toString()) {*/
                        /*senderRoom.add(mAuth.currentUser?.uid.toString() + currentUser.uid.toString())*/
                        var location = currentUser.Location
                        var t = snapshot.child("latest-messages").child(mAuth.uid.toString()).child(currentUser?.uid.toString()).child("msg").child("timestamp").value
                        if (t == null) {
                            t = 0
                        }
                        /*if (t != null) {
                            timestamps.add(t.toString().toLong())
                        } else {
                            timestamps.add(0)
                        }*/

                        var lastMsg = snapshot.child("latest-messages").child(mAuth.uid.toString()).child(currentUser?.uid.toString()).child("msg").child("message").value
                        userList.add(inboxItem(currentUser?.uid.toString(), currentUser?.username.toString(), currentUser?.url.toString(),
                            lastMsg.toString(),
                            t.toString().toLong()
                        ))


                        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$receiverUid")

/*                        }*/
                    }
                }
                userList.sortByDescending {
                    it.timestamp
                }

                /*for (i in 0..sorted_timestamps.lastIndex) {
                    for (j in 0..timestamps.lastIndex) {
                        if (timestamps[j] == sorted_timestamps[i]) {
                            userList.add(unsortedUserList[j])
                        }
                    }
                }

                Toast.makeText(this@ChatUserList,sorted_timestamps.toString(),Toast.LENGTH_SHORT).show()*/

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@ChatUserList,"No records found!",Toast.LENGTH_SHORT).show()

            }

        })

    }

    class StackWithList{
        val elements: MutableList<inboxItem> = mutableListOf()

        fun isEmpty() = elements.isEmpty()

        fun size() = elements.size

        fun push(item: Any) = elements.add(item as inboxItem)

        fun pop() : Any? {
            val item = elements.lastOrNull()
            if (!isEmpty()){
                elements.removeAt(elements.size -1)
            }
            return item
        }
        fun peek() : Any? = elements.lastOrNull()

        override fun toString(): String = elements.toString()
    }

    // Set up function for back button in Action Bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*private fun getClosestWorkers() {
        // retrieving location in firebase db
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val georeference : DatabaseReference = database.reference.child("geofire")
        val geoFire = GeoFire(georeference)
        val geoQuery = geoFire.queryAtLocation(GeoLocation(26.7174121,88.3878191), radius)
        geoQuery.removeAllListeners()

        //geoquery to find closest worker
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                if (!workerFound && key != mAuth.currentUser?.uid.toString()) {
                    workerFound = true
                    workerFoundID = key.toString()
                    Toast.makeText(this@ChatUserList, workerFoundID+" is ${radius}km away", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onKeyExited(key: String?) {
                TODO("Not yet implemented")
            }
            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                TODO("Not yet implemented")
            }
            override fun onGeoQueryReady() {
                if (!workerFound) {
                    radius++
                    getClosestWorkers()
                }
            }
            override fun onGeoQueryError(error: DatabaseError?) {
                TODO("Not yet implemented")
            }

        })

    }*/



}


