package com.jobdoneindia.jobdone.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.SearchResultsAdapter
import com.jobdoneindia.jobdone.adapter.TagsAdapter

data class SearchItem(val name: String, val bio: String, val overall_rating: String, val distance: String, val description: String)

class FragmentSearchResults: Fragment()  {

    private val mySearchItems = mutableListOf<SearchItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_searchresults, container, false)

        // storing data
        // TODO: Read data from worker profiles from database and store it in mySearchItems array
        mySearchItems.add(SearchItem("Ramesh Kumar","Electrician hu mai bol","4.5 / 5","5km","I check for defects, assemble products, monitor manufacturing equipment, and closely follow safety procedures to prevent accidents in environments where materials may be hazardous."))
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
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))
        mySearchItems.add(SearchItem("Ramesh Kumar","nice guy","4.5 / 5","5km","very nice guy"))

        // OnClick listener for recyclerview items
        var adapter = SearchResultsAdapter(mySearchItems)
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

        // Back button
        val backButton = root.findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            view:View ->
            Navigation.findNavController(view).navigate(R.id.action_fragmentSearchResults_to_fragmentTags)
        }

        return root
    }
}