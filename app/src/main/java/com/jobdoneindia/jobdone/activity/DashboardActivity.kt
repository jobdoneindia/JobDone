package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import java.lang.Exception
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragment: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.hide()

        // database
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("Users")

        // Bottom Nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationDrawer)

        // fragment NavHost
        fragment = findViewById(R.id.navhostfragment)

        // Set Home selected (in bottom nav bar)
        bottomNavigationView.selectedItemId = R.id.menuHome

        // Perform item selected listener (bottom nav bar)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    // Navigating fragments
                    if (fragment.findNavController().currentDestination?.id == R.id.fragmentTags) {
                        fragment.findNavController().navigate(R.id.action_fragmentTags_to_fragmentMainButton)
                    } else if (fragment.findNavController().currentDestination?.id == R.id.fragmentSearchResults) {
                        fragment.findNavController().navigate(R.id.fragmentMainButton)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.menuRewards -> {
                    startActivity(Intent(applicationContext, RewardsActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuChat -> {
                    startActivity(Intent(applicationContext, ChatUserList::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.menuAccount -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

    }


}