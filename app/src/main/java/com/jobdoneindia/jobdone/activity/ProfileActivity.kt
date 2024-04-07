package com.jobdoneindia.jobdone.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jobdoneindia.jobdone.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

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
        val userName = findViewById<TextView>(R.id.user_name)
        val userAddress = findViewById<TextView>(R.id.user_address)

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid  = getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child(uid.toString())

        // fetching data from local DB
        val sharedPreferences: SharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val sharedName: String? = sharedPreferences.getString("name_key", "Siraj Alarm")
        val sharedAddress: String? = sharedPreferences.getString("location_key", "India, Earth")
        userName.text = sharedName.toString()
        userAddress.text = sharedAddress.toString()

        // get image url from local database
        val imageUrl:  String? = sharedPreferences.getString("dp_url_key", "not found")

        // Set DP using Glide
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(this.findViewById<CircleImageView>(R.id.profile_pic))


        //logout Button listener
        logoutBtn.setOnClickListener{

            reference.child(uid.toString()).removeValue()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@ProfileActivity,LoginActivity::class.java)
            startActivity(intent)
        }


        // workerToggle switch on
        workerToggle.isChecked = false

        // TODO: Fetch data and display Name and Address

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
                    finishAffinity()
                }

                R.id.menuRewards -> {
                    startActivity(
                        Intent(applicationContext, RewardsActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    finishAffinity()
                    return@setOnItemSelectedListener true
                }

                R.id.menuChats -> {
                    startActivity(
                        Intent(applicationContext, ChatUserList::class.java).setFlags(
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
            // Show progress dialog
            val progressDialog = ProgressDialog(this@ProfileActivity)
            progressDialog.setMessage("Checking status...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            // Fetch user data from Firebase
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Dismiss progress dialog
                    progressDialog.dismiss()
                    if (snapshot.exists()) {
                        val verificationStatus = snapshot.child("verificationStatus").getValue(String::class.java)

                        when (verificationStatus) {
                            "approved" -> {
                                // Verification is approved, go to WorkerDashboardActivity
                                updateUserRoleToCustomerAndWorker()
                                startActivity(Intent(applicationContext, WorkerDashboardActivity::class.java))
                                finish() // Finish current activity
                            }
                            "pending" -> {
                                // Verification is pending, go to PendingActivity
                                startActivity(Intent(applicationContext, PendingActivity::class.java))
                                finish() // Finish current activity
                            }
                            else -> {
                                // Handle other verification status, if needed
                                // For now, let's show a toast message
                                startActivity(Intent(applicationContext, AdhaarValidation::class.java))
                                finish() // Finish current activity
                                Toast.makeText(this@ProfileActivity, "Adhaar verification needed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Show message indicating that user data doesn't exist
                        Toast.makeText(this@ProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Dismiss progress dialog
                    progressDialog.dismiss()

                    // Handle onCancelled
                    Toast.makeText(this@ProfileActivity, "Error retrieving user data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun updateUserRoleToCustomerAndWorker() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        val roleUpdate = hashMapOf<String, Any>("role" to "customer&worker")
        userRef.updateChildren(roleUpdate).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Role updated successfully, go to WorkerDashboardActivity
                startActivity(Intent(applicationContext, WorkerDashboardActivity::class.java))
                finish() // Finish current activity
            } else {
                // Failed to update role, show error message or handle accordingly
                Toast.makeText(this, "Failed to update role", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, DashboardActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        onSuperBackPressed()
    }
    fun onSuperBackPressed() {
        super.onBackPressed()
    }
}