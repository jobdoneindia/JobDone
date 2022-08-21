package com.jobdoneindia.jobdone.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.geofire.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.UserAdapter

class ChatUserList : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef : DatabaseReference
    private lateinit var mAuth:FirebaseAuth
    private lateinit var locationCallback : LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user_list)


        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        val geoFire : GeoFire = GeoFire(mDbRef)

        geoFire.getLocation("locationUser", locationCallback)



        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)


        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        val uid = FirebaseAuth.getInstance().uid




        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {


                userList.clear()
                for (postSnapshot in snapshot.children) {


                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid) {

                        val geoQuery : GeoQuery = geoFire.queryAtLocation(object : GeoLocation(locationCallback),5.0)

                        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener{

                            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                                userList.add(currentUser!!)
                            }

                            override fun onKeyExited(key: String?) {
                                TODO("Not yet implemented")
                            }

                            override fun onGeoQueryError(error: DatabaseError?) {
                                TODO("Not yet implemented")
                            }

                            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                                TODO("Not yet implemented")
                            }

                            override fun onGeoQueryReady() {
                                TODO("Not yet implemented")
                            }


                        })








                    }

                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@ChatUserList,"No records found!",Toast.LENGTH_SHORT).show()

            }

        })

    }



}


