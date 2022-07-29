package com.jobdoneindia.jobdone.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    lateinit var editTextName : EditText
    lateinit var editTextLocation : EditText
    lateinit var doneButton : Button
    lateinit var editTextPhone: EditText
    lateinit var otpContinueBtn : Button
    private lateinit var btnSetDP: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var imageuri: Uri

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = database.reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Add back button in Action Bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // TODO: Feature to select DP and update in database
        btnSetDP = findViewById(R.id.btnSetDP)
        profilePic = findViewById(R.id.profile_pic)

        btnSetDP.setOnClickListener {
            pickfromGallery()
        }

        // TODO: Feature to set current location

        // Done fab button
        var doneButton: FloatingActionButton = findViewById(R.id.done_button)
        // TODO: Editing And Saving To Database
        editTextName = findViewById(R.id.editTextName)
        editTextLocation = findViewById(R.id.editTextLocation)
        doneButton = findViewById(R.id.done_button)

        //TODO: sending data to dtabase

            doneButton.setOnClickListener{
            val userName: String = editTextName.text.toString()
            val location: String = editTextLocation.text.toString()

            reference.child("UserName").setValue(userName)
            reference.child("Location").setValue(location)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // TODO: Update Database - OnClick Listener for FAB button




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
                        .setNegativeButton("Cancel",DialogInterface.OnClickListener{
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

    // Start galleryIntent for result
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageuri = result.data!!.data!!
            profilePic.setImageURI(imageuri)
        }
    }

    // select an image from Gallery
    private fun pickfromGallery() {
        val galleryIntent: Intent = Intent(Intent.ACTION_PICK)
        galleryIntent.setType("image/*")
        resultLauncher.launch(galleryIntent)
    }

}
