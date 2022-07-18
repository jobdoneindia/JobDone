package com.jobdoneindia.jobdone.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jobdoneindia.jobdone.R

class LoginActivity: AppCompatActivity() {

    private var skipButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        skipButton = findViewById<View>(R.id.skip_button) as Button

        val phoneButton = findViewById<Button>(R.id.phn_button)
        phoneButton.setOnClickListener{
            val intent = Intent(this, Phone_auth_login::class.java)
            startActivity(intent)
        }

        skipButton!!.setOnClickListener{
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

    }
}