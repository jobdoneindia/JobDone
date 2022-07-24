package com.jobdoneindia.jobdone.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.jobdoneindia.jobdone.R

class FragmentChooseMode : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_choose_mode, container, false)

        // Exit Transition
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)

        // Next Button
        root.findViewById<Button>(R.id.next_button).setOnClickListener {
                view: View ->
            Navigation.findNavController(view).navigate(R.id.action_fragmentEnterName_to_fragmentChooseMode)
        }

        return root
    }

}