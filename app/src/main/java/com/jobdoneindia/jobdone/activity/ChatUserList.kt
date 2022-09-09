package com.jobdoneindia.jobdone.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.geofire.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.UserAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ChatUserList : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef : DatabaseReference
    private lateinit var mAuth:FirebaseAuth
    private var radius: Double = 1.0
    private var workerFound = false
    private var workerFoundID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user_list)

        /*getClosestWorkers()*/

        mDbRef = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)


        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        /*if (workerFoundID == currentUser?.uid.toString()) {*/
                            userList.add(currentUser!!)
/*                        }*/
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@ChatUserList,"No records found!",Toast.LENGTH_SHORT).show()

            }

        })

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


