package com.jobdoneindia.jobdone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity: AppCompatActivity() {

    private var skipButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        skipButton = findViewById<View>(R.id.skip_button) as Button

        val phoneButton = findViewById<Button>(R.id.phn_button)
        phoneButton.setOnClickListener{
            val intent = Intent(this, PhoneLoginActivity::class.java)
            startActivity(intent)
        }

        skipButton!!.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}