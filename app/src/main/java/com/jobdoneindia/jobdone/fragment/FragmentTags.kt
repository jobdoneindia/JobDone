package com.jobdoneindia.jobdone.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.adapter.TagsAdapter
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.LoginActivity
import com.jobdoneindia.jobdone.adapter.SearchResultsAdapter
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.runBlocking
import java.text.FieldPosition

data class Categories( val service_name: String, val services_count: Int)

class FragmentTags: Fragment() {

    /*private val myCategories = mutableListOf<Categories>()*/
    private val items = mutableListOf("Appliances", "Plumber", "Carpenter", "Home Tutor", "Pandit", "Laundry", "Electrician", "Videographer", "Vehicle Service")

    private val tags = mapOf(
        "Electrician" to arrayListOf("AC Repair", "Plug", "Socket", "Wires"),
        "Appliances" to arrayListOf("TV Repair", "AC Repair", "Washing machine repair", "Gizzer Repair", "Air Cooler Repair", "Fridge Repair", "Mixer Grinder Repair", "Speaker Repair"),
        "Plumber" to arrayListOf("Install Water Supply System", "Install Waste Disposal System", "Repair Pipeline Issues" ),
        "Carpenter" to arrayListOf("New Furniture Making", "Old Furniture Repair", "Install Modular Kichen"),
        "Painter" to arrayListOf("Furniture Painting", "Contract Works(Office, Home, Cafe)", "Wall Painting"),
        "Driver" to arrayListOf("Personal Car", "Loading Van", "JCB", "Dumper", "Truck"),
        "Home Tutor" to arrayListOf("Class 1-5", "Class 6-10", "Class 11-12", "Graduates", "Science Teacher", "Maths Teacher", "Commerce Teacher", "Arts Teacher"),
        "Freelancer" to arrayListOf("Graphic Designer", "Content Writer","Web Designer", "Virtual Assistant","App Developer", "Video Editor", "Social Media Manager", "Transcriber"),
        "Pest Control" to arrayListOf("Complete Pest Control", "Sanitisation"),
        "Pandit" to arrayListOf("Bhagwat Katha", "Bhoomi Pooja", "Antim Sanskar", "Satya Narayan Katha","Shaadi", "Ganesh Utsav", "Hawan", "Vaastu Pooja"),
        "Laundry" to arrayListOf("Washing CLothes", "Dry Clean", "Ironing Clothes"),
        "RO Service" to arrayListOf("RO Repair", "RO Install", "RO Service", "RO Parts Change"),
        "House Maid" to arrayListOf("House Cleaning", "Cooking", "Washing Clothes", "Baby Care","Utensil Cleaning"),
        "Water Supplier" to arrayListOf("Drinking Water", "Tanker"),
        "Photographer" to arrayListOf("Model", "Pre Wedding", "Wedding", "Album","Birthday", "Freelance"),
        "Videographer" to arrayListOf("Wedding", "Birthday", "Freelance"),
        "Vehicle Service" to arrayListOf("Bike Repair", "Car Repair", "Loading Van Repair", "Truck Repair", "Servicing" ),
        "Vehicle Washing" to arrayListOf("Bike Wash", "Car Wash", "Loading Van Wash", "Truck Wash"),
        "Goods Transport Vehicle" to arrayListOf( "Three Wheeler"," Four Wheeler", "Six Wheeler", "Eight Wheeler", "Ten Wheeler"))

    private lateinit var scrollRight: ImageButton
    private lateinit var scrollLeft: ImageButton
    private lateinit var horizontalScrollView: HorizontalScrollView

    private lateinit var chipGroup: ChipGroup

    private lateinit var dialog: AlertDialog

    private lateinit var sharedPreferences: SharedPreferences

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

        // chip group
        chipGroup = root.findViewById(R.id.chipGroup)
        var tagList = mutableListOf<String>()
        var selectedTags = mutableListOf<String>()

        // onClick for list items

        /*btnNext.setOnClickListener {
            Navigation.findNavController(view!!).navigate(R.id.action_fragmentTags_to_fragmentSearchResults)
        }*/

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
        sharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val geoFire = GeoFire(georeference)
        val geoQuery = geoFire.queryAtLocation(GeoLocation(/*26.7174121*/sharedPreferences.getFloat("latitude", 0F)!!
            .toDouble(), sharedPreferences.getFloat("longitude",0F)!!.toDouble()/*88.3878191*/), radius)
        geoQuery.removeAllListeners()

        //geoquery to find closest worker
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                if (key !in workerFoundID.split(":") && key != mAuth.currentUser?.uid.toString()) {
                    workerFoundID += key.toString() + ":"
                    workerDistances += radius.toInt().toString() + ":"

/*
                    Toast.makeText(requireContext(), workerFoundID.split(":").toString(), Toast.LENGTH_SHORT).show()
*/

                    /*val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)*/
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
                if (!workerFound && radius<20) {
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
        Navigation.findNavController(requireView()).navigate(R.id.action_fragmentTags_to_fragmentSearchResults)
    }

    // Update tags in Chip Group
    private fun updateTags(tagList: MutableList<String>) {
        chipGroup.removeAllViews()
        for (index in tagList.indices) {
            val tagName = tagList[index]
            val chip = Chip(requireContext())
            val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
            ).toInt()
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
            chip.text = tagName
            chip.isCheckable = true
            chip.isCheckedIconVisible = true
            chip.setCheckedIconResource(R.drawable.ic_done)

            //Added click listener on close icon to remove tag from ChipGroup
/*            chip.setOnCloseIconClickListener {
                tagList.remove(tagName)
                chipGroup.removeView(chip)
            }*/

            chip.setOnCheckedChangeListener { button, b ->

                /*Toast.makeText(requireContext(), "Selected: ${chip.text}", Toast.LENGTH_SHORT)
                    .show()*/
            }

            chipGroup.addView(chip)
        }
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

// Scroll
/*
horizontalScrollView = root.findViewById(R.id.list_items)
scrollLeft = root.findViewById(R.id.scrollLeft)
scrollRight = root.findViewById(R.id.scrollRight)

scrollLeft.setOnClickListener {
    horizontalScrollView.fullScroll(ScrollView.FOCUS_LEFT)
}

scrollRight.setOnClickListener {
    horizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT)
}

val distanceOptions = resources.getStringArray(R.array.distanceOptions)

distanceSpinner = root.findViewById(R.id.distanceSpinner)
if (distanceSpinner != null) {
    val spinnerAdapter = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_item,
        distanceOptions
    )
    distanceSpinner.adapter = spinnerAdapter
}

// All buttons
*/
/*val btnNext = root.findViewById<Button>(R.id.btnNext)*//*

val btnAppliances = root.findViewById<CircleImageView>(R.id.btnAppliances)
val btnPlumber = root.findViewById<CircleImageView>(R.id.btnPlumber)
val btnCarpenter = root.findViewById<CircleImageView>(R.id.btnCarpenter)
val btnHomeTutor = root.findViewById<CircleImageView>(R.id.btnHomeTutor)
val btnPandit = root.findViewById<CircleImageView>(R.id.btnPandit)
val btnLaundry = root.findViewById<CircleImageView>(R.id.btnLaundry)
val btnElectrician = root.findViewById<CircleImageView>(R.id.btnElectrician)
val btnVideographer = root.findViewById<CircleImageView>(R.id.btnVideographer)
val btnVehicleService = root.findViewById<CircleImageView>(R.id.btnVehicleService)

*/
/*tagList.clear()
selectedTags.clear()
for (x in tags["Appliances"]!!) {
    tagList.add(x)
}
updateTags(tagList)*//*


// onClick for all buttons
btnAppliances.setOnClickListener {
    btnAppliances.setBackgroundResource(R.drawable.shp_roundorange)

    */
/*btnAppliances.setBackgroundResource(R.drawable.transparent_gradient)*//*

    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnPlumber.setOnClickListener {
    btnPlumber.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnCarpenter.setOnClickListener {
    btnCarpenter.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnHomeTutor.setOnClickListener {
    btnHomeTutor.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnPandit.setOnClickListener {
    btnPandit.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnLaundry.setOnClickListener {
    btnLaundry.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnElectrician.setOnClickListener {
    btnElectrician.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnVideographer.setOnClickListener {
    btnVideographer.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVehicleService.setBackgroundResource(R.drawable.transparent)


}

btnVehicleService.setOnClickListener {
    btnVehicleService.setBackgroundResource(R.drawable.shp_roundorange)

    btnAppliances.setBackgroundResource(R.drawable.transparent)
    btnPlumber.setBackgroundResource(R.drawable.transparent)
    btnCarpenter.setBackgroundResource(R.drawable.transparent)
    btnHomeTutor.setBackgroundResource(R.drawable.transparent)
    btnPandit.setBackgroundResource(R.drawable.transparent)
    btnLaundry.setBackgroundResource(R.drawable.transparent)
    btnElectrician.setBackgroundResource(R.drawable.transparent)
    btnVideographer.setBackgroundResource(R.drawable.transparent)


}*/
