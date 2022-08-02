package com.jobdoneindia.jobdone.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jobdoneindia.jobdone.R

class LoginActivity: AppCompatActivity() {

    private var skipButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()



//        if(firebaseUser != null){
//            val intent = Intent(this,DashboardActivity::class.java)
//            startActivity(intent)
//        }

        skipButton = findViewById<View>(R.id.skip_button) as Button
        skipButton!!.setOnClickListener{
            startActivity(Intent(this,ChatUserList::class.java))
        }

        val phoneButton = findViewById<Button>(R.id.phn_button)
        phoneButton.setOnClickListener{
            val intent = Intent(this, Phone_auth_login::class.java)
            startActivity(intent)
        }



    }
}