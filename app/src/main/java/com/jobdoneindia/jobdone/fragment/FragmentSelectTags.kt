package com.jobdoneindia.jobdone.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.jobdoneindia.jobdone.R


class FragmentSelectTags : Fragment() {

    private val items = mutableListOf("Plumber", "Ac Repairer", "Fridge Repairer","Fan Repairer", "Socket Change", "Teacher", "Dance Teacher", "Artist", "RO Repairer")

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var adapterItems: ArrayAdapter<String>
    private lateinit var chipGroup: ChipGroup
    private lateinit var btnAdd: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_select_tags, container, false)

        // variables init
        autoCompleteTextView = root.findViewById(R.id.auto_complete_tag)
        chipGroup = root.findViewById(R.id.chip_group_tags)
        btnAdd = root.findViewById(R.id.btnAdd)

        // autocompleteTextView
        var tagList = mutableListOf<String>()
        adapterItems = ArrayAdapter(requireContext(), R.layout.dropdown_item,items)
        autoCompleteTextView.setAdapter(adapterItems)
        autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
            val item: String = adapterView.getItemAtPosition(position).toString()
            Toast.makeText(requireContext(), "Item: $item", Toast.LENGTH_SHORT).show()
        }

        // onClickListener for btnAdd
        btnAdd.setOnClickListener {
            if (autoCompleteTextView.text.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a tag!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (tagList.size < 3) {
                tagList.add(autoCompleteTextView.text.toString())
                updateTags(tagList)
                autoCompleteTextView.text = null
            } else {
                Toast.makeText(requireContext(), "Limit exceeded.",Toast.LENGTH_LONG).show()
            }
        }

        // Next Button
        root.findViewById<Button>(R.id.btnNext).setOnClickListener {
                view: View ->
            Navigation.findNavController(view).navigate(R.id.action_fragmentSelectTags_to_fragmentSetDP)
        }

        return root
    }

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
            chip.isCloseIconEnabled = true
            //Added click listener on close icon to remove tag from ChipGroup
            chip.setOnCloseIconClickListener {
                tagList.remove(tagName)
                chipGroup.removeView(chip)
            }
            chipGroup.addView(chip)
        }
    }
}