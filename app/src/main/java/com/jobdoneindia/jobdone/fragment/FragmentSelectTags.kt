package com.jobdoneindia.jobdone.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.AdhaarValidation
import com.jobdoneindia.jobdone.activity.WorkerDashboardActivity


class FragmentSelectTags : Fragment() {

    private val tags = mapOf(
        "AC Repairer" to arrayListOf("Installing","Maintaining","Repairing"),
        "Beautician" to arrayListOf("Bridal Makeup","Eyebrow Waxing","Makeup"),
        "Carpenter" to arrayListOf("New Furniture Making","Old Furniture Repair","Install Modular Kichen"),
        "Catering Service" to arrayListOf("Marriage Caremony","Birthday Party","Office Party"),
        "Computer Repair" to arrayListOf("CPU Repair", "Computer Assembling", "Parts Change", "Monitor Repair"),
        "Dance Choreographer" to arrayListOf("Hiphop","Classical"),
        "Digital Marketer" to arrayListOf("Social Media Marketing", "Content Research", "Marketing", "Content Creation", "Analysing Data"),
        "Driver" to arrayListOf("Personal Car", "Loading Van", "JCB", "Dumper", "Truck"),
        "Electrician" to arrayListOf( "Installing", "Maintaining", "Repairing"),
        "Event Management" to arrayListOf("Marriage Caremony","Birthday Party","Office Party","Rice Caremony","Anniversary"),
        "Graphic Designer" to arrayListOf("Logos", "Posters","Billboards","UI Design","Web Design","Illustration"),
        "Home Tutor" to arrayListOf("Class 1-5","Class 6-10","Class 11-12","Graduates","Science Teacher","Maths Teacher","Commerce Teacher","Arts Teacher"),
        "House Maid" to arrayListOf("House Cleaning","Cooking","Washing Clothes","Baby Care","Utensil Cleaning"),
        "Laundry" to arrayListOf("Washing CLothes", "Dry Clean", "Ironing Clothes"),
        "Mechanic" to arrayListOf("Chimney Repairer", "Gizzer Repairer", "Washing Machine Repairer", "Mixer Grinder Repairer", "Gas Repairer"),
        "Painter" to arrayListOf("Furniture Painting","Contract Works(Office, Home, Cafe)","Wall Painting"),
        "Pandal Maker" to arrayListOf("Marriage Caremony", "Festival Pandal", "Rice Caremony", "Anniversary Party"),
        "Pandit" to arrayListOf("Bhagwat Katha","Bhoomi Pooja","Antim Sanskar","Satya Narayan Katha","Shaadi","Ganesh Utsav","Hawan","Vaastu Pooja"),
        "Pest Control" to arrayListOf("Complete Pest Control", "Sanitisation"),
        "Pet Care" to arrayListOf( "Feeding", "Watering", "Cleaning", "Walking", "Bathing", "Medicating", "Monitoring"),
        "Photographer" to arrayListOf("Model","Pre Wedding","Wedding","Album","Birthday","Freelance"),
        "Plumber" to arrayListOf("Install Water Supply System","Install Waste Disposal System","Repair Pipeline Issues"),
        "RO Service" to arrayListOf("RO Repair", "RO Install", "RO Service", "RO Parts Change"),
        "Social Media Manager" to arrayListOf("Developing strategies to increase followers", "Creating and overseeing social campaigns", "Producing content", "Reviewing analytics", "Communicating with key stakeholders in a company"),
        "Tailor" to arrayListOf("Gents", "Ladies","Cloth Alteration", "Garment Design","Fitting"),
        "Vehicle Service" to arrayListOf("Bike Repair","Car Repair","Loading Van Repair","Truck Repair","Servicing"),
        "Vehicle Washing" to arrayListOf("Bike Wash", "Car Wash", "Loading Van Wash", "Truck Wash"),
        "Videographer" to arrayListOf("Wedding", "Birthday", "Freelance"),
        "Water Supplier" to arrayListOf("Drinking Water", "Tanker","Home Services","Marrige Caremony"),
        "Web Developer" to arrayListOf("Frontend","Backend", "Fullstack"),
        )


    private var selectedTags = mutableListOf<String>()

    private lateinit var adapterItems: ArrayAdapter<String>
    private lateinit var chipGroup: ChipGroup
    private lateinit var selectProfessionSpinner: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_select_tags, container, false)

        // variables init
        chipGroup = root.findViewById(R.id.chip_group_tags)

        // autocompleteTextView
        var tagList = mutableListOf<String>()
        val items = resources.getStringArray(R.array.categoryOptions).drop(1)
        adapterItems = ArrayAdapter(requireContext(), R.layout.dropdown_item,items)

        // Spinner Logic
        selectProfessionSpinner = root.findViewById(R.id.select_profession_spinner)
        selectProfessionSpinner.setAdapter(adapterItems)
        selectProfessionSpinner.setOnItemClickListener { adapterView, view, position, id ->
            tagList.clear()
            selectedTags.clear()
            val item: String = adapterView.getItemAtPosition(position).toString()
            if (item in tags){
                for (x in tags[item]!!) {
                    tagList.add(x)
                }
            }
            updateTags(tagList)
        }

        // Next Button
        root.findViewById<Button>(R.id.btnNext).setOnClickListener {
                view: View ->

            // store data locally
            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("profession_key", selectProfessionSpinner.text.toString())
            editor.putString("tags_key", selectedTags.toString())
            editor.apply()
            editor.commit()

            // store profession and tags in realtime database
            val database : FirebaseDatabase = FirebaseDatabase.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val reference : DatabaseReference = database.reference.child("Users").child(uid.toString())
            reference.child("Profession").setValue(selectProfessionSpinner.text.toString())
            reference.child("tag1").setValue(if (selectedTags.size >= 1) selectedTags[0] else "Empty")
            reference.child("tag2").setValue(if(selectedTags.size >= 2) selectedTags[1] else "Empty")
            reference.child("tag3").setValue(if(selectedTags.size >=3) selectedTags[2] else "Empty")

            val intent = Intent(requireContext(), AdhaarValidation::class.java)
            this.activity?.finishAffinity()
            startActivity(intent)
            // Navigation.findNavController(view).navigate(R.id.action_fragmentSelectTags_to_fragmentSetDP)
        }

        return root
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
                if (chip.isChecked == true) {
                    if (selectedTags.size < 3) {
                        selectedTags.add(chip.text.toString())
                    } else {
                        chip.isChecked = false
                    }

                } else {
                    selectedTags.remove(chip.text.toString())
                }
                /*Toast.makeText(requireContext(), "Selected: ${selectedTags}", Toast.LENGTH_SHORT).show()*/
            }

            chipGroup.addView(chip)
        }
        val newChip = Chip(requireContext())

        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,
            resources.displayMetrics
        ).toInt()
        newChip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        newChip.text = "+ Add New"
        newChip.isCheckable = false

        newChip.setOnClickListener {
            selectedTags.clear()
            updateTags(tagList)

            // get alert_dialog.xml view
            val li = LayoutInflater.from(requireContext())
            val promptsView = li.inflate(R.layout.alert_dialog, null)
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

            // set alert_dialog.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView)
            val userInput = promptsView.findViewById<View>(R.id.etUserInput) as EditText

            // set dialog message
            alertDialogBuilder
                .setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, id -> // get user input and set it to result
                        // edit text
                        Toast.makeText(
                            requireContext(),
                            "Entered: " + userInput.text.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        tagList.add(userInput.text.toString())
                        updateTags(tagList)
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

            // create alert dialog
            val alertDialog: AlertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.show()
        }

        chipGroup.addView(newChip)

    }


}