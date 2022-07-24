package com.jobdoneindia.jobdone.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jobdoneindia.jobdone.R

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

    }
}