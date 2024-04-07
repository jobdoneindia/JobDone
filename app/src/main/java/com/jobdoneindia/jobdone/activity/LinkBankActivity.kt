package com.jobdoneindia.jobdone.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R

lateinit var acc_num_1 : EditText
lateinit var acc_num_2 : EditText
lateinit var input_ifsc : EditText
lateinit var acc_name : EditText
lateinit var nxtBtn : Button

var database : FirebaseDatabase = FirebaseDatabase.getInstance()
var reference : DatabaseReference = database.reference.child("Users")

class LinkBankActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_bank)

        //Firebase Database Reference
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child("Users").child(uid.toString())

        // Add back button in Action Bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        acc_num_1 = findViewById(R.id.acc_num_1)
        acc_num_2 = findViewById(R.id.acc_num_2)
        input_ifsc = findViewById(R.id.input_ifsc)
        acc_name = findViewById(R.id.input_accname)

        nxtBtn = findViewById(R.id.nextButton)
        nxtBtn.setOnClickListener{
            saveToFirebase(acc_num_1.text.toString(), acc_num_2.text.toString())
        }

    }


    //Verify Account Number
    private fun verifyAccNum(num1: Long, num2: Long): Boolean{
        return num1 == num2
    }

    private fun saveToFirebase(acc_num_1: String, acc_num_2: String){
        if(verifyAccNum(acc_num_1.toLong(), acc_num_2.toLong())){
            reference.child("Account Details").setValue("Acc Name", acc_name)
            reference.child("Account Details").setValue("Acc Name", acc_num_1)
            reference.child("Account Details").setValue("Ifsc", input_ifsc)

            Toast.makeText(this, "Account Saved Successfully", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Account Number doesn't match!", Toast.LENGTH_SHORT).show()
        }
    }


    // Set up function for back button in Action Bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // build alert dialog
                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Discard Changes?").setCancelable(true)
                    // positive button text and action
                    .setPositiveButton("Discard", DialogInterface.OnClickListener {
                            dialog, id -> finish()
                    })
                    // negative button text and action
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener{
                            dialog, id -> dialog.cancel()
                    })
                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Edit Profile")
                // show
                alert.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}