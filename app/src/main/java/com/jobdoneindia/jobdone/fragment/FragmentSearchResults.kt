package com.jobdoneindia.jobdone.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.User
import com.jobdoneindia.jobdone.adapter.SearchResultsAdapter

data class SearchItem(val name: String, val bio: String, val overall_rating: String, var distance: String, val profession: String, val uid: String, val url: String)

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

    private lateinit var distanceSpinner: Spinner
    private lateinit var categorySpinner: Spinner

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

        val txtNotFound: TextView = root.findViewById(R.id.txtNotFound)

        // Select radius
        val distanceOptions = resources.getStringArray(R.array.distanceOptions)
        distanceSpinner = root.findViewById(R.id.distanceSpinner)
        if (distanceSpinner != null) {
            val distanceAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                distanceOptions
            )
            distanceSpinner.adapter = distanceAdapter

        }

        // Select Category
        val categoryOptions = resources.getStringArray(R.array.categoryOptions)
        categorySpinner = root.findViewById(R.id.categorySpinner)
        if (categorySpinner != null) {
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryOptions
            )
            categorySpinner.adapter = categoryAdapter

        }

        // OnClick listener for recyclerview items
        var adapter = SearchResultsAdapter(requireContext(),mySearchItems)
        adapter.setOnItemClickListener(object : SearchResultsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                /*Toast.makeText(context, "You Clicked on item no. $position", Toast.LENGTH_SHORT).show()*/
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

        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                mySearchItems.clear()
                unsortedSearchItems.clear()
                /*getClosestWorkers()*/
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    /*if (mAuth.currentUser?.uid != currentUser?.uid) {*/
                    if (currentUser?.uid.toString() in sharedPreferences.getString("closestworker", "null").toString().split(":") && currentUser?.Profession != null) {
                        /*userList.add(currentUser!!)*/
                        /*distance = (distance(currentUser?.Location!![0], currentUser?.Location!![1], userLocation[0], userLocation[1])/0.621371).toInt()*/
                        unsortedSearchItems.add(SearchItem(
                            currentUser!!.username.toString(),
                            null.toString(), "null", "${distance}km", currentUser.Profession.toString(), currentUser.uid.toString(), currentUser.url.toString()
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

        // DistanceSpinner on item selected
        distanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(distanceOptions[position]) {
                    "5km" -> {
                        mySearchItems.clear()
                        for (i in 0..sharedPreferences.getString("closestworker", "null").toString().split(":").lastIndex) {
                            for (j in 0..unsortedSearchItems.lastIndex) {
                                if (sharedPreferences.getString("closestworker", "null").toString().split(":")[i] == unsortedSearchItems[j].uid) {
                                    unsortedSearchItems[j].distance = sharedPreferences.getString("closeness", "null").toString().split(":")[i] + " km"
                                    if (unsortedSearchItems[j].distance.split(" ")[0].toInt() <= 5){
                                        mySearchItems.add(unsortedSearchItems[j])
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    "20km" -> {
                        mySearchItems.clear()
                        for (i in 0..sharedPreferences.getString("closestworker", "null").toString().split(":").lastIndex) {
                            for (j in 0..unsortedSearchItems.lastIndex) {
                                if (sharedPreferences.getString("closestworker", "null").toString().split(":")[i] == unsortedSearchItems[j].uid) {
                                    unsortedSearchItems[j].distance = sharedPreferences.getString("closeness", "null").toString().split(":")[i] + " km"
                                    if (unsortedSearchItems[j].distance.split(" ")[0].toInt() <= 20){
                                        mySearchItems.add(unsortedSearchItems[j])
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    "500km" -> {
                        mySearchItems.clear()
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
                }

                if (adapter.itemCount == 0) {
                    txtNotFound.visibility = View.VISIBLE
                } else {
                    txtNotFound.visibility = View.GONE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // CategorySpinner on item selected
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mySearchItems.clear()
                for (i in 0..sharedPreferences.getString("closestworker", "null").toString().split(":").lastIndex) {
                    for (j in 0..unsortedSearchItems.lastIndex) {
                        if (sharedPreferences.getString("closestworker", "null").toString().split(":")[i] == unsortedSearchItems[j].uid) {
                            unsortedSearchItems[j].distance = sharedPreferences.getString("closeness", "null").toString().split(":")[i] + " km"
                            if (unsortedSearchItems[j].profession.toString() == categoryOptions[position] || categoryOptions[position] == "All") {
                                mySearchItems.add(unsortedSearchItems[j])
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()

                if (adapter.itemCount == 0) {
                    txtNotFound.visibility = View.VISIBLE
                } else {
                    txtNotFound.visibility = View.GONE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // Back button
        val backButton = root.findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
                view:View ->
            Navigation.findNavController(view).navigate(R.id.action_fragmentSearchResults_to_fragmentMainButton)
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
}