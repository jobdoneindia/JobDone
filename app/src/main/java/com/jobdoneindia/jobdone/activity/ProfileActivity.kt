package com.jobdoneindia.jobdone.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

            // store data locally
            val sharedPreferences: SharedPreferences = this.getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("mode_key", "worker")
            editor.apply()
            editor.commit()

            profileLayout.startAnimation(animation)
            finish()
            startActivity(Intent(applicationContext, WorkerProfileActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
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