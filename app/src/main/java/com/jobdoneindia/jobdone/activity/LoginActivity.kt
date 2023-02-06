package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.jobdoneindia.jobdone.R

class LoginActivity: AppCompatActivity() {

    private var skipButton: Button? = null
    private var mListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JobDone)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        if (!checkPermissions()) {
            requestPermissions()
        }


        skipButton = findViewById<View>(R.id.skip_button) as Button
        skipButton!!.setOnClickListener{
            startActivity(Intent(this,DashboardActivity::class.java))
        }

        val phoneButton = findViewById<Button>(R.id.phn_button)
        phoneButton.setOnClickListener{
            val intent = Intent(this, Phone_auth_login::class.java)
            startActivity(intent)
        }

    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), permissionId)
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
                startActivity(intent)

            }
        }

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
    // APP REMEMBERS THE USER WHO LOGIN-ED PREVIOUSLY
    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser

        val sharedPreferences: SharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val sharedName: String? = sharedPreferences.getString("mode_key", "customer")

        if (user != null){
            if (sharedName == "customer") {
                val intent = Intent(this,DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this,WorkerDashboardActivity::class.java)
                startActivity(intent)
                finish()
            }

//           intent.putExtra("phoneNumber",phone)

        }
    }
}