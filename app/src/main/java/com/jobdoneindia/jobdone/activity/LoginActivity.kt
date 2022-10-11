package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jobdoneindia.jobdone.R

class LoginActivity: AppCompatActivity() {

    private var skipButton: Button? = null

    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        if (!checkPermissions()) {
            requestPermissions()
        }



//        if(firebaseUser != null){
//            val intent = Intent(this,DashboardActivity::class.java)
//            startActivity(intent)
//        }

        skipButton = findViewById<View>(R.id.skip_button) as Button
        skipButton!!.setOnClickListener{
            startActivity(Intent(this,PaymentActivity::class.java))
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
                startActivity(intent)
                finish()
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            }
        }
    }
}