package com.jobdoneindia.jobdone.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import de.hdodenhof.circleimageview.CircleImageView

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
        val txtProfession = findViewById<TextView>(R.id.txtProfession)
        val tag1 = findViewById<Chip>(R.id.tag1)
        val tag2 = findViewById<Chip>(R.id.tag2)
        val tag3 = findViewById<Chip>(R.id.tag3)

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid  = FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child(uid.toString())

        // fetching data from local DB
        val sharedPreferences: SharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val sharedName: String? = sharedPreferences.getString("name_key", "Siraj Alarm")
        val sharedAddress: String? = sharedPreferences.getString("location_key", "India, Earth")
        val sharedProfession: String? = sharedPreferences.getString("profession_key", "null")
        val sharedTags: String? = sharedPreferences.getString("tags_key", "null")?.drop(1)?.dropLast(1)

        userName.text = sharedName.toString()
        userAddress.text = sharedAddress.toString()

        if (sharedProfession.toString() != "null") {
            txtProfession.text = sharedProfession.toString()
        }

        if (sharedTags.toString() != "ul") {
            tag1.text = sharedTags!!.split(",")[0]
            if (sharedTags!!.split(",").size >= 2) {
                tag2.text = sharedTags!!.split(",")[1]
            }
            if (sharedTags!!.split(",").size == 3) {
                tag3.text = sharedTags!!.split(",")[2]
            }
        }

        // workerToggle switch on
        workerToggle.isChecked = true

        // get image url from local database
        val imageUrl:  String? = sharedPreferences.getString("dp_url_key", "not found")

        // Set DP using Glide
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(this.findViewById<CircleImageView>(R.id.profile_pic))

        // Logout button function
        logoutBtn.setOnClickListener{

            reference.child("Worker-Details").removeValue()
            FirebaseAuth.getInstance().signOut()
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

                R.id.menuWorkerRewards -> {
                    startActivity(
                        Intent(applicationContext, WorkerReward::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
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
            finish()
            startActivity(Intent(applicationContext, EditWorkerProfileActivity::class.java))
        }

        // Worker toggle
        workerToggle.setOnClickListener {
            // store data locally
            val sharedPreferences: SharedPreferences = this.getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("mode_key", "customer")
            editor.apply()
            editor.commit()

            /*workerProfileLayout.startAnimation(animation)*/
            finish()
            startActivity(Intent(applicationContext, ProfileActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(applicationContext, WorkerDashboardActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        onSuperBackPressed()
    }
    fun onSuperBackPressed() {
        super.onBackPressed()
    }
}