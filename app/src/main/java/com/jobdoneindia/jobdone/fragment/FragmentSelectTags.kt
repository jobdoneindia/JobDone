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
import com.jobdoneindia.jobdone.activity.WorkerDashboardActivity


class FragmentSelectTags : Fragment() {

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
        "Goods Transport Vehicle" to arrayListOf( "Three Wheeler"," Four Wheeler", "Six Wheeler", "Eight Wheeler", "Ten Wheeler"),)

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
        /*selectProfessionSpinner.setOnItemClickListener { adapterView, view, position, id ->
            tagList.clear()
            selectedTags.clear()
            val item: String = adapterView.getItemAtPosition(position).toString()
            for (x in tags[item]!!) {
                tagList.add(x)
            }
            updateTags(tagList)
        }*/

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
            reference.child("Tags").setValue(selectedTags)

            val intent = Intent(requireContext(), WorkerDashboardActivity::class.java)
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