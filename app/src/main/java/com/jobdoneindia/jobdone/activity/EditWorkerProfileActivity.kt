package com.jobdoneindia.jobdone.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jobdoneindia.jobdone.R
import de.hdodenhof.circleimageview.CircleImageView

class EditWorkerProfileActivity : AppCompatActivity() {

    private lateinit var btnSetDP: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var imageuri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_worker_profile)

        // Add back button in Action Bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Done fab button
        val doneButton: FloatingActionButton = findViewById(R.id.done_button)
        doneButton.setOnClickListener {
            startActivity(Intent(applicationContext, WorkerProfileActivity::class.java))
        }

        // Tags Selectors
        val selectorTag1 = findViewById<AutoCompleteTextView>(R.id.selector_tag1)
        val selectorTag2 = findViewById<AutoCompleteTextView>(R.id.selector_tag2)
        val selectorTag3 = findViewById<AutoCompleteTextView>(R.id.selector_tag3)
        val tags = resources.getStringArray(R.array.tags)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tags)
        selectorTag1.setAdapter(adapter)
        selectorTag2.setAdapter(adapter)
        selectorTag3.setAdapter(adapter)

        // TODO: Feature to select DP and update in database
        profilePic = findViewById(R.id.profile_pic)
        btnSetDP = findViewById(R.id.btnSetDP)
        btnSetDP.setOnClickListener {
            pickfromGallery()
        }

        // TODO: Feature to set current location

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