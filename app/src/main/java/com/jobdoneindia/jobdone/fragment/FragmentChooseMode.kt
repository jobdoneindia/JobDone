package com.jobdoneindia.jobdone.fragment

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.DashboardActivity
import com.jobdoneindia.jobdone.activity.WorkerDashboardActivity
import com.jobdoneindia.jobdone.activity.WorkerProfileActivity

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



        // Customer Button
        root.findViewById<Button>(R.id.customer_mode_btn).setOnClickListener {
                view: View ->
            val intent = Intent(this@FragmentChooseMode.requireContext(),DashboardActivity::class.java)

            val database : FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference : DatabaseReference = database.reference.child("Users")

            reference.child("Customer").setValue("YES")
            reference.child("Worker").setValue("NO")

          startActivity(intent)

        }

        //Worker Button
        root.findViewById<Button>(R.id.worker_mode_btn).setOnClickListener {
                view: View ->

            val intent = Intent(this@FragmentChooseMode.requireContext(),WorkerDashboardActivity::class.java)

            startActivity(intent)

        }

        return root
    }

}