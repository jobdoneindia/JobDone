package com.jobdoneindia.jobdone.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.WorkerDashboardActivity


class FragmentSelectTags : Fragment() {

    private val items = mutableListOf("Electrician" /*0*/, "Appliances" /*1*/, "Home service" /*2*/)
    private val tags = mapOf(
        items[0] to arrayListOf("Wiring", "Plug", "Socket", "Wires"),
        items[1] to arrayListOf("TV Repair", "AC Repair", "Washing machine repair", "Computer Repair"),
        items[2] to arrayListOf("Cleaning", "Washing", "Cooking", "Bulldozer")
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
        adapterItems = ArrayAdapter(requireContext(), R.layout.dropdown_item,items)

        // Spinner Logic
        selectProfessionSpinner = root.findViewById(R.id.select_profession_spinner)
        selectProfessionSpinner.setAdapter(adapterItems)
        selectProfessionSpinner.setOnItemClickListener { adapterView, view, position, id ->
            tagList.clear()
            selectedTags.clear()
            val item: String = adapterView.getItemAtPosition(position).toString()
            for (x in tags[item]!!) {
                tagList.add(x)
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
                Toast.makeText(requireContext(), "Selected: ${selectedTags}", Toast.LENGTH_SHORT).show()
            }

            chipGroup.addView(chip)
        }
    }


}