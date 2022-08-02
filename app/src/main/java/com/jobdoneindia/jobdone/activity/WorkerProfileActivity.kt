package com.jobdoneindia.jobdone.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R

class WorkerProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_profile)
        supportActionBar?.hide()

        // Initialize and assign variables
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationDrawer)
        val editButton = findViewById<FloatingActionButton>(R.id.edit_button)
        val workerProfileLayout = findViewById<ConstraintLayout>(R.id.worker_profile_layout)
        val workerToggle = findViewById<SwitchMaterial>(R.id.worker_toggle)
        val logoutBtn = findViewById<Button>(R.id.logout_button)

        val userName = findViewById<TextView>(R.id.user_name)
        val userAddress = findViewById<TextView>(R.id.user_address)

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid  = FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child(uid.toString())

        // fetching data from local DB
        val sharedPreferences: SharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val sharedName: String? = sharedPreferences.getString("name_key", "Siraj Alarm")
        val sharedAddress: String? = sharedPreferences.getString("location_key", "India, Earth")
        userName.text = sharedName.toString()
        userAddress.text = sharedAddress.toString()

        // workerToggle switch on
        workerToggle.isChecked = true

        // Logout button function
        logoutBtn.setOnClickListener{

            reference.child("Worker-Details").removeValue()

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        // animation init
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Set Accounts selected
        bottomNavigationView.selectedItemId = R.id.menuAccount

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    startActivity(
                        Intent(applicationContext, WorkerDashboardActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                }

                R.id.menuRewards -> {
                    startActivity(
                        Intent(applicationContext, RewardsActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuChat -> {
                    startActivity(
                        Intent(applicationContext, DashboardActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuAccount -> {
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

        // edit button (fab) onClick listener
        editButton.setOnClickListener {
            startActivity(Intent(applicationContext, EditWorkerProfileActivity::class.java))
        }

        // Worker toggle
        workerToggle.setOnClickListener {
            workerProfileLayout.startAnimation(animation)
            startActivity(Intent(applicationContext, ProfileActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }
}