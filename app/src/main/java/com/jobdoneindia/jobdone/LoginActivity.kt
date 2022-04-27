package com.jobdoneindia.jobdone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val phoneButton = findViewById<Button>(R.id.phn_button)
        phoneButton.setOnClickListener{
            val intent = Intent(this, PhoneLoginActivity::class.java)
            startActivity(intent)
        }

    }
}