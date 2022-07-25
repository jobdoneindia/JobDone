package com.jobdoneindia.jobdone.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
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

        // workerToggle switch on
        workerToggle.isChecked = true

        // TODO: Logout button function

        // animation init
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)

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
                        Intent(applicationContext, ChatsActivity::class.java).setFlags(
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