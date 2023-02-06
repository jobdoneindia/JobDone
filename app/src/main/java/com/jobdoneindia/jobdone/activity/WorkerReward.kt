package com.jobdoneindia.jobdone.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jobdoneindia.jobdone.R

class WorkerReward : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_reward)

        // Initialize and assign variables
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationDrawer)

        // Set Accounts selected
        bottomNavigationView.selectedItemId = R.id.menuWorkerRewards

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    startActivity(
                        Intent(applicationContext, WorkerDashboardActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                }

                R.id.menuWorkerRewards -> {
                    return@setOnItemSelectedListener true
                }

                R.id.menuChats -> {
                    startActivity(
                        Intent(applicationContext, ChatUserList::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuAccount -> {
                    startActivity(
                        Intent(applicationContext, WorkerProfileActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

    }
}