package com.jobdoneindia.jobdone.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.adapter.TagsAdapter
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.LoginActivity
import kotlinx.coroutines.runBlocking
import java.text.FieldPosition

data class Categories( val service_name: String, val services_count: Int)

class FragmentTags: Fragment() {

    private val myCategories = mutableListOf<Categories>()

    private lateinit var dialog: AlertDialog

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var radius = 1.0
    private var distance: Int = 0
    private var workerFound = false
    private var workerFoundID: String = ""
    private var workerDistances: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate layout for this fragment
        val root = inflater.inflate(R.layout.fragment_choosetags, container, false)

        mAuth = FirebaseAuth.getInstance()

        startAlertDialog()
        /*getClosestWorkers()*/

        // Exit Transition
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)

        // Storing Data
        // TODO: Read tags and number of users in those tags from database and store it in myCategories array
        myCategories.add(Categories( "Plumber", 20))
        myCategories.add(Categories( "Ac Repairer", 20))
        myCategories.add(Categories( "Fan Repairer", 21))
        myCategories.add(Categories( "Socket Change", 82))
        myCategories.add(Categories( "Teacher", 62))
        myCategories.add(Categories( "Dance Teacher", 47))
        myCategories.add(Categories( "Artist", 90))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))
        myCategories.add(Categories( "RO Repairer", 30))

        // TODO: Use search bar to filter search results

        // TODO: "Add a new tag" button and it's function

        // TODO: Fix Duplicating recyclerview items on restart
        // OnClick listener for recyclerview items
        var adapter = TagsAdapter(myCategories)

        // Services list RecyclerView Setup
        val servicesList: RecyclerView = root.findViewById(R.id.ServicesList)
        servicesList.adapter = adapter
        servicesList.layoutManager = LinearLayoutManager(activity)

        adapter.setOnItemClickListener(object : TagsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context, "You Clicked on item no. $position", Toast.LENGTH_SHORT).show()
                // TODO: Pass data (which item was selected) to the next fragment
                Navigation.findNavController(view!!).navigate(R.id.action_fragmentTags_to_fragmentSearchResults)
            }
        })

        // OnClick for back button
        root.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            view: View -> Navigation.findNavController(view).navigate(R.id.action_fragmentTags_to_fragmentMainButton)
        }

        return root
    }

    private fun getClosestWorkers() {
        // retrieving location in firebase db
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val georeference: DatabaseReference = database.reference.child("geofire")
        val geoFire = GeoFire(georeference)
        val geoQuery = geoFire.queryAtLocation(GeoLocation(26.7174121, 88.3878191), radius)
        geoQuery.removeAllListeners()

        //geoquery to find closest worker
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                if (key !in workerFoundID.split(":") && key != mAuth.currentUser?.uid.toString()) {
                    workerFoundID += key.toString() + ":"
                    workerDistances += radius.toInt().toString() + ":"

                    Toast.makeText(requireContext(), workerFoundID.split(":").toString(), Toast.LENGTH_SHORT).show()

                    val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
                    var editor = sharedPreferences.edit()
                    editor.putString("closestworker", workerFoundID)
                    editor.putString("closeness", workerDistances)
                    editor.apply()
                }
            }

            override fun onKeyExited(key: String?) {
                TODO("Not yet implemented")
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                TODO("Not yet implemented")
            }

            override fun onGeoQueryReady() {
                if (!workerFound && radius<500) {
                    radius++
                    getClosestWorkers()
                } else {
                    closeAlertDilog()
                }
            }

            override fun onGeoQueryError(error: DatabaseError?) {
                TODO("Not yet implemented")
            }

        })
    }

    // Loading Dialog
    fun startAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(layoutInflater.inflate(R.layout.loading_workers_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
        getClosestWorkers()
    }

    fun closeAlertDilog() {
        dialog.dismiss()
    }

    // overriding the back button
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Navigation.findNavController(view!!).navigate(R.id.action_fragmentTags_to_fragmentMainButton)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}