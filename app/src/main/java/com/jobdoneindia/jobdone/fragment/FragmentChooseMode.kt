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
        root.findViewById<Button>(R.id.customer_mode_btn).setOnClickListener {
                view: View ->

            // store data locally
            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("mode_key", "customer")
            editor.apply()
            editor.commit()

            val intent = Intent(requireContext(),DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            this@FragmentChooseMode.activity?.finish()
        }

        root.findViewById<Button>(R.id.worker_mode_btn).setOnClickListener{
                view: View ->
            // store data locally
            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("mode_key", "worker")
            editor.apply()
            editor.commit()

            Navigation.findNavController(view).navigate(R.id.action_fragmentChooseMode_to_fragmentSelectTags)
        }

        return root
    }

}