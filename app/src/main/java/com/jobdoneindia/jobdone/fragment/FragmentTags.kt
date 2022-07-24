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
}