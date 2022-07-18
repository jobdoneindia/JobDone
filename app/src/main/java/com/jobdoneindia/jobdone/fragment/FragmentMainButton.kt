package com.jobdoneindia.jobdone.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jobdoneindia.jobdone.R

class FragmentMainButton: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate layout for this fragment
        val root = inflater.inflate(R.layout.fragment_mainbutton, container, false)
        val transitionInflater = TransitionInflater.from(requireContext())

        // Exit Transition
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)

        // Main Button Onclick listener
        root.findViewById<ImageButton>(R.id.main_button).setOnClickListener {
            view: View -> Navigation.findNavController(view).navigate(R.id.action_fragmentMainButton_to_fragmentTags)
        }

        return root
    }
}