package com.jobdoneindia.jobdone.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryDataEventListener
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.User
import com.jobdoneindia.jobdone.adapter.SearchResultsAdapter

data class SearchItem(val name: String, val bio: String, val overall_rating: String, var distance: String, val description: String, val uid: String, val url: String)

class FragmentSearchResults: Fragment()  {

    private val mySearchItems = mutableListOf<SearchItem>()
    private val unsortedSearchItems = mutableListOf<SearchItem>()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var radius = 1.0
    private var distance: Int = 0
    private var workerFound = false
    private lateinit var workerFoundID: String

    private lateinit var userLocation: ArrayList<Double>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.fragment_searchresults, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        // get lat and long from local database
        sharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        userLocation = mutableListOf(sharedPreferences.getFloat("latitude", 0F).toDouble(),sharedPreferences.getFloat("longitude", 0F).toDouble()) as ArrayList<Double>

        // TODO: Add a "Schedule a job" Button and create ActivityPostAJob.kt


        //  TODO: Search the database using tag received from previous fragment and location of user


        // OnClick listener for recyclerview items
        var adapter = SearchResultsAdapter(requireContext(),mySearchItems)
        adapter.setOnItemClickListener(object : SearchResultsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context, "You Clicked on item no. $position", Toast.LENGTH_SHORT).show()
            }
        })

        // Exit Transition
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)

        // RecyclerView Setup
        val searchItemList: RecyclerView = root.findViewById(R.id.SearchItemList)
        searchItemList.adapter = adapter
        // TODO: See SearchResultsAdapter.kt
        searchItemList.layoutManager = LinearLayoutManager(activity)

        // detect scroll

        // TODO: Read data from worker profiles from database and store it in mySearchItems array
        /*mySearchItems.add(SearchItem("Ramesh Kumar","Electrician hu mai bol","4.5 / 5","5km","I check for defects, assemble products, monitor manufacturing equipment, and closely follow safety procedures to prevent accidents in environments where materials may be hazardous."))
        mySearchItems.achdd(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))*/

        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                mySearchItems.clear()
                unsortedSearchItems.clear()
                /*getClosestWorkers()*/
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    /*if (mAuth.currentUser?.uid != currentUser?.uid) {*/
                    if (currentUser?.uid.toString() in sharedPreferences.getString("closestworker", "null").toString().split(":")) {
                        /*userList.add(currentUser!!)*/
                        /*distance = (distance(currentUser?.Location!![0], currentUser?.Location!![1], userLocation[0], userLocation[1])/0.621371).toInt()*/
                        unsortedSearchItems.add(SearchItem(
                            currentUser!!.username.toString(),
                            null.toString(), "null", "${distance}km", "null", currentUser.uid.toString(), currentUser.url.toString()
                        ))
                    }
                    /*}*/
                }
                for (i in 0..sharedPreferences.getString("closestworker", "null").toString().split(":").lastIndex) {
                    for (j in 0..unsortedSearchItems.lastIndex) {
                        if (sharedPreferences.getString("closestworker", "null").toString().split(":")[i] == unsortedSearchItems[j].uid) {
                            unsortedSearchItems[j].distance = sharedPreferences.getString("closeness", "null").toString().split(":")[i] + " km"
                            mySearchItems.add(unsortedSearchItems[j])
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

                /*Toast.makeText(this@FragmentSearchResults,"No records found!",Toast.LENGTH_SHORT).show()*/

            }

        })

        // Back button
        val backButton = root.findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            view:View ->
            Navigation.findNavController(view).navigate(R.id.action_fragmentSearchResults_to_fragmentTags)
        }

        // FAB i.e schedule your work
        val btnScheduleJob = root.findViewById<FloatingActionButton>(R.id.btnScheduleJob)
        btnScheduleJob.setOnClickListener {
            view:View ->
            run {
                Navigation.findNavController(view)
                    .navigate(R.id.action_fragmentSearchResults_to_fragmentScheduleThisJob)
            }
        }

        return root
    }

    // overriding the back button
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Navigation.findNavController(view!!).navigate(R.id.action_fragmentSearchResults_to_fragmentTags)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
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
                    Toast.makeText(requireContext(),radius.toString(),Toast.LENGTH_SHORT).show()
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
        }
        */

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}