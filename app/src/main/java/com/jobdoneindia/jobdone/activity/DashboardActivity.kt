package com.jobdoneindia.jobdone.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class DashboardActivity : AppCompatActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE
    private val REQUEST_CODE = 100
    private lateinit var mDbRef: DatabaseReference
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragment: FragmentContainerView
    private var mListener: FirebaseAuth.AuthStateListener? = null
    val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //appUpdate
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
        checkForUpdate()

        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        supportActionBar?.hide()

        // database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        // Bottom Nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationDrawer)

        // fragment NavHost
        fragment = findViewById(R.id.navhostfragment)

        // Set Home selected (in bottom nav bar)
        bottomNavigationView.selectedItemId = R.id.menuHome

        var counter = sharedPref.getInt("counter", 0)

        /* setBadge(counter)*/

        // Perform item selected listener (bottom nav bar)
        bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menuHome -> {
                    // Navigating fragments
                    if (fragment.findNavController().currentDestination?.id == R.id.fragmentTags) {
                        fragment.findNavController()
                            .navigate(R.id.action_fragmentTags_to_fragmentMainButton)
                    } else if (fragment.findNavController().currentDestination?.id == R.id.fragmentSearchResults) {
                        fragment.findNavController().navigate(R.id.fragmentMainButton)

                    }
                    return@setOnItemSelectedListener true
                }

                R.id.menuRewards -> {
                    startActivity(
                        Intent(applicationContext, RewardsActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION
                        )
                    )
                    finishAffinity()
                    return@setOnItemSelectedListener true
                }

                R.id.menuChats -> {
                    editor.remove("counter")
                    editor.apply()

                    startActivity(Intent(applicationContext, ChatUserList::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.menuAccount -> {
                    startActivity(
                        Intent(applicationContext, ProfileActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION
                        )
                    )
                    finishAffinity()
                    return@setOnItemSelectedListener true
                }

            }
            true
        }


    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds.",
                Toast.LENGTH_LONG
            ).show()
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    //APP UPDATE
    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            val isUpdateAvailable =
                updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> updateInfo.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> updateInfo.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    updateInfo,
                    updateType,
                    this,
                    123
                )
            }

//            if (updateInfo.updateAvailability()==UpdateAvailability.UPDATE_AVAILABLE
//                && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
//                appUpdate?.startUpdateFlowForResult(updateInfo,AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
//            }
        }
    }

    fun inProgressUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->

            if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    updateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    REQUEST_CODE
                )
            }
        }
    }


    //notification badge counter
    /* fun setBadge(counter: Int) {

        if(counter == 0) {
            bottomNavigationView.removeBadge(R.id.menuChats)
        }else {

            val badge = bottomNavigationView.getOrCreateBadge(R.id.menuChats)  //show badge
            badge.number = counter

        }


    }*/
    // TO SHOW STATUS
    private fun status(status: String) {
        val firebase: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        firebase?.let { user ->
            mDbRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
            val map: HashMap<String, Any> = hashMapOf("status" to status)
            mDbRef.updateChildren(map as Map<String, Any>)
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
                if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        updateInfo,
                        updateType,
                        this,
                        123
                    )
                }
            }
            status("online")
        }



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

        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
        val mAuth = FirebaseAuth.getInstance()
        /*
    mAuth?.removeAuthStateListener(mListener!!)
*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode != RESULT_OK) {
                println("Updating...")
            }
        }
    }

    // APP REMEMBERS THE USER WHO LOGIN-ED PREVIOUSLY
    override fun onStart() {
        super.onStart()

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val sharedName: String? = sharedPreferences.getString("mode_key", "customer")

        if (sharedName == "worker") {
            val intent = Intent(this, WorkerDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}