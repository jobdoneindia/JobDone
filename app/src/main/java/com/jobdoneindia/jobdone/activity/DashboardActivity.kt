package com.jobdoneindia.jobdone.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.support.annotation.NonNull
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import java.util.Objects

class DashboardActivity : AppCompatActivity() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragment: FragmentContainerView
    private var mListener: FirebaseAuth.AuthStateListener? = null
    val firebase : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        supportActionBar?.hide()

        // database
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()

        // Bottom Nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationDrawer)

        // fragment NavHost
        fragment = findViewById(R.id.navhostfragment)

        // Set Home selected (in bottom nav bar)
        bottomNavigationView.selectedItemId = R.id.menuHome

        var counter = sharedPref.getInt("counter",0)

        setBadge(counter)

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
                    finishAffinity()
                    return@setOnItemSelectedListener true
                }

                R.id.menuChats ->

                {
                    editor.remove("counter")
                    editor.apply()

                    startActivity(Intent(applicationContext, ChatUserList::class.java))
                    finishAffinity()
                    return@setOnItemSelectedListener true
                }

                R.id.menuAccount -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    finishAffinity()
                    return@setOnItemSelectedListener true
                }

            }
            true
        }


    }


    //notification badge counter
    fun setBadge(counter: Int) {

        if(counter == 0) {
            bottomNavigationView.removeBadge(R.id.menuChats)
        }else {

            val badge = bottomNavigationView.getOrCreateBadge(R.id.menuChats)  //show badge
            badge.number = counter

        }


    }
    // TO SHOW STATUS
    fun status(status : String) {
       mDbRef = FirebaseDatabase.getInstance().getReference("Users").child(firebase.uid)

       var map : HashMap<String , Any>
       = HashMap<String ,Any>()

        map.put("status" , status)
        mDbRef.updateChildren(map as Map<String, Any>)

    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

    override fun onBackPressed() {
        onSuperBackPressed()
    }
    fun onSuperBackPressed() {
        super.onBackPressed()
    }
    override fun onDestroy() {
        super.onDestroy()

        val mAuth = FirebaseAuth.getInstance()
/*
        mAuth?.removeAuthStateListener(mListener!!)
*/
    }

    // APP REMEMBERS THE USER WHO LOGIN-ED PREVIOUSLY
    override fun onStart() {
        super.onStart()

        val sharedPreferences: SharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val sharedName: String? = sharedPreferences.getString("mode_key", "customer")

        if (sharedName == "worker") {
            val intent = Intent(this,WorkerDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}