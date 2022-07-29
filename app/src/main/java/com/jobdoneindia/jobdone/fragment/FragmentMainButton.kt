package com.jobdoneindia.jobdone.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.renderscript.ScriptGroup
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.databinding.FragmentMainbuttonBinding
import java.util.*

class FragmentMainButton: Fragment() {

    private lateinit var mainButton: ImageButton
    private lateinit var userName : TextView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate layout for this fragment
        val root = inflater.inflate(R.layout.fragment_mainbutton, container, false)


        // Exit Transition
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)

        // Enter Transition
        enterTransition = transitionInflater.inflateTransition(R.transition.explode)

        // user name
        userName = root.findViewById(R.id.user_name)
        val args = this.arguments
        val inputData = args?.get("username")
        userName.text = inputData.toString()





        // Main Button Onclick listener
        mainButton = root.findViewById<ImageButton>(R.id.main_button)
        mainButton.setOnClickListener { view: View ->
            mainButton.setImageResource(R.drawable.img_jobdone_round_plain)
            Navigation.findNavController(view)
                .navigate(R.id.action_fragmentMainButton_to_fragmentTags)
        }

        return root


    }
}