package com.jobdoneindia.jobdone.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jobdoneindia.jobdone.R

class PendingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending)

        val textViewPendingMessage = findViewById<TextView>(R.id.textViewPendingMessage)
        val backToCustomerButton = findViewById<Button>(R.id.backToCustomerButton)
        backToCustomerButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        textViewPendingMessage.text = "Your verification is pending."
    }
}
