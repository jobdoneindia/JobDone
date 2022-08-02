package com.jobdoneindia.jobdone.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.databinding.ActivityRegistrationBinding
import com.jobdoneindia.jobdone.fragment.FragmentChooseMode
import com.jobdoneindia.jobdone.fragment.FragmentEnterName

class RegistrationActivity : AppCompatActivity() {


    private lateinit var binding : ActivityRegistrationBinding
    private lateinit var database : DatabaseReference
    private lateinit var inputName:EditText
    private lateinit var nextBtn:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child("Users").child(uid.toString())
//        val inputName = findViewById<EditText>(R.id.input_name).text.toString().trim()
        val phoneNumber = intent.getStringExtra("phoneNumber")
//        reference.child("Username").setValue(inputName)
        reference.child("Phone Number").setValue(phoneNumber)


    }
}