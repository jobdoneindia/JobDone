package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.jobdoneindia.jobdone.R
import android.provider.Settings

class LoginActivity: AppCompatActivity() {

    private var skipButton: Button? = null
    private var mListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private val permissionId = 2

    // Inside LoginActivity class
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JobDone)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        // Check permissions first
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            // Check for internet connectivity
            if (!isNetworkAvailable()) {
                showInternetDialog()
            } else {
                // Check user status
                checkUserStatus()
            }
        }

        skipButton = findViewById<View>(R.id.skip_button) as Button
        skipButton!!.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        val phoneButton = findViewById<Button>(R.id.phn_button)
        phoneButton.setOnClickListener {
            val intent = Intent(this, Phone_auth_login::class.java)
            startActivity(intent)
        }

    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun showInternetDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("No Internet Connection")
        alertDialogBuilder.setMessage("Please check your internet connection and try again.")
        alertDialogBuilder.setPositiveButton("Open Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
            startActivity(intent)
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
            finish()
        }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val intent = Intent(this, LoginActivity::class.java)

//                finish()
                /*            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
//                    finish()*/
//                }, 2000)

                if (!isNetworkAvailable()) {
                    showInternetDialog()
                } else {
                    checkUserStatus()
                }
            }
        }

    }

    private fun checkUserStatus() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Checking status...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog?.dismiss() // Dismiss the dialog when user data is retrieved

                if (snapshot.exists()) {
                    val role = snapshot.child("role").getValue(String::class.java)
                    val verificationStatus =
                        snapshot.child("verificationStatus").getValue(String::class.java)

                    if (role == "customer") {
                        // Role is "customer", go to DashboardActivity
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish() // Finish LoginActivity
                    } else if (role == "customer&worker") {
                        // Role is "customer&worker", check verification status
                        when (verificationStatus) {
                            "approved" -> {
                                // Verification is approved, go to WorkerDashboardActivity
                                val intent = Intent(
                                    this@LoginActivity,
                                    WorkerDashboardActivity::class.java
                                )
                                startActivity(intent)
                                finish() // Finish LoginActivity
                            }

                            "pending" -> {
                                // Verification is pending, go to PendingActivity
                                val intent =
                                    Intent(this@LoginActivity, PendingActivity::class.java)
                                startActivity(intent)
                                finish() // Finish LoginActivity
                            }

                            else -> {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Your verification is denied.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Unknown user role",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "User data not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog?.dismiss()
            }
        })
    }

    override fun onBackPressed() {
        /*  AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {

                override fun onClick(arg0: DialogInterface, arg1: Int) {
                    onSuperBackPressed()
                }
            }).create().show()*/
    }

    override fun onDestroy() {
        super.onDestroy()
        /* val mAuth = FirebaseAuth.getInstance()
        mAuth?.removeAuthStateListener(mListener!!)*/

    }
}
