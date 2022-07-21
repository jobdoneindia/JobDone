package com.jobdoneindia.jobdone.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobdoneindia.jobdone.adapter.TagsAdapter
import com.jobdoneindia.jobdone.R
import java.text.FieldPosition

data class Categories( val service_name: String, val services_count: Int)

class FragmentTags: Fragment() {

    private val myCategories = mutableListOf<Categories>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate layout for this fragment
        val root = inflater.inflate(R.layout.fragment_choosetags, container, false)

        // Enter Transition
        val transitionInflater = TransitionInflater.from(requireContext())
        enterTransition = transitionInflater.inflateTransition(R.transition.fade)

        // Storing Data
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

        // OnClick listener for recyclerview items
        var adapter = TagsAdapter(myCategories)
        adapter.setOnItemClickListener(object : TagsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context, "You Clicked on item no. $position", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view!!).navigate(R.id.action_fragmentTags_to_fragmentSearchResults)
            }
        })

        // Services list RecyclerView Setup
        val servicesList: RecyclerView = root.findViewById(R.id.ServicesList)
        servicesList.adapter = adapter
        servicesList.layoutManager = LinearLayoutManager(activity)

        // OnClick for back button
        root.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            view: View -> Navigation.findNavController(view).navigate(R.id.action_fragmentTags_to_fragmentMainButton)
        }

        return root
    }
}