package com.jobdoneindia.jobdone.fragment

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R

class FragmentEnterName : Fragment() {

    private lateinit var inputName:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_enter_name, container, false)


        // Exit Transition
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)

        val bundle = Bundle()





//        // Next Button
        root.findViewById<Button>(R.id.btnNext).setOnClickListener {
                view: View ->

            val database : FirebaseDatabase = FirebaseDatabase.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val reference : DatabaseReference = database.reference.child(uid.toString())
            inputName = root.findViewById<EditText>(R.id.input_name).text.toString().trim()

            bundle.putString("username",inputName)

            val fragment = FragmentMainButton()
            fragment.arguments = bundle

            reference.child("Username").setValue(inputName)
            Navigation.findNavController(view).navigate(R.id.action_fragmentEnterName_to_fragmentChooseMode)
        }

        return root
    }


}


