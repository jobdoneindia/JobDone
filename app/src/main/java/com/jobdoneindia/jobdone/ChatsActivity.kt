package com.jobdoneindia.jobdone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        // Initialize and assign variables
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationDrawer)

        // Set Accounts selected
        bottomNavigationView.selectedItemId = R.id.menuChat

        // Perform item selected listener
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    startActivity(
                        Intent(applicationContext, DashboardActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                }

                R.id.menuFreelance -> {
                    startActivity(
                        Intent(applicationContext, FreelanceActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuRewards -> {
                    startActivity(
                        Intent(applicationContext, RewardsActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuChat -> {
                    return@setOnItemSelectedListener true
                }

                R.id.menuAccount -> {
                    startActivity(
                        Intent(applicationContext, AccountsActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

    }
}