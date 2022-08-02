package com.jobdoneindia.jobdone.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)
        supportActionBar?.hide()

        // animation init
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Initialize and assign variables
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationDrawer)
        val editButton = findViewById<FloatingActionButton>(R.id.edit_button)
        val workerToggle = findViewById<SwitchMaterial>(R.id.worker_toggle)
        val profileLayout = findViewById<ConstraintLayout>(R.id.constraint_layout_profile)
        val logoutBtn = findViewById<Button>(R.id.logout_button)

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid  = FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child(uid.toString())

        //logout Button listener
        logoutBtn.setOnClickListener{

            reference.child(uid.toString()).removeValue()

            val intent = Intent(this@ProfileActivity,LoginActivity::class.java)
            startActivity(intent)
        }


        // workerToggle switch on
        workerToggle.isChecked = false

        // TODO: Fetch data and display Name and Address

        // TODO: Logout button function

        // TODO: Setup a recyclerview for sheduled jobs
        // TODO: Fetch data from database and display in scheduled jobs

        // Set Accounts selected
        bottomNavigationView.selectedItemId = R.id.menuAccount

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    startActivity(
                        Intent(applicationContext, DashboardActivity::class.java).setFlags(
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
            startActivity(Intent(applicationContext, EditProfileActivity::class.java))
        }

        // Worker toggle
        workerToggle.setOnClickListener {
            profileLayout.startAnimation(animation)
            startActivity(Intent(applicationContext, WorkerProfileActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))


        }

    }
}