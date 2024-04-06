package com.jobdoneindia.jobdone.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.DashboardActivity

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
        root.findViewById<Button>(R.id.customer_mode_btn).setOnClickListener { view: View ->
            // Update user role to "customer"
            updateUserRole("customer")

            // Proceed to DashboardActivity
            val intent = Intent(requireContext(), DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            requireActivity().finishAffinity()
            startActivity(intent)
        }

        // Worker Button
        root.findViewById<Button>(R.id.worker_mode_btn).setOnClickListener { view: View ->
            // Update user role to "worker"
            updateUserRole("customer&worker")

            // Navigate to fragmentSelectTags
            Navigation.findNavController(view).navigate(R.id.action_fragmentChooseMode_to_fragmentSelectTags)
        }

        return root
    }

    private fun updateUserRole(role: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userRef.child("role").setValue(role)
            .addOnSuccessListener {
                // Role updated successfully
            }
            .addOnFailureListener { e ->
                // Handle failure
                // You can show an error message or log the error here
            }
    }
}