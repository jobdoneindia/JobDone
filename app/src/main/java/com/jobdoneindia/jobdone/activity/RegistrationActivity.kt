package com.jobdoneindia.jobdone.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewbinding.ViewBinding
import com.google.firebase.database.DatabaseReference
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegistrationBinding
    private lateinit var database : DatabaseReference
    private var customerButton : Button? = null
    private var workerButton : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        customerButton = findViewById(R.id.customer_mode_btn)
        workerButton = findViewById(R.id.worker_mode_btn)

//        customerButton.setOnClickListener{
//            val intent = Intent(this,DashboardActivity::class.java)
//            startActivity(intent)
//        }






    }
}