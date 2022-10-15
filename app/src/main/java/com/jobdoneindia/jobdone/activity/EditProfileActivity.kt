package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.Transliterator
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jobdoneindia.jobdone.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    lateinit var editTextName : EditText
    lateinit var doneButton : FloatingActionButton
    lateinit var editTextPhone: EditText
    lateinit var otpContinueBtn : Button
    private lateinit var imageURL: String
    private lateinit var btnSetDP: ImageButton
    private lateinit var profilePic: CircleImageView
    var imageuri: Uri? = null
    var firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference : StorageReference = firebaseStorage.reference

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = database.reference.child("Users")

    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profileActivityForResult()

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
        doneButton = findViewById(R.id.done_button)
        editTextName = findViewById(R.id.editTextName)


        // fetching data from local database
        var sharedPreferences: SharedPreferences = this.getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val sharedLocation: String? = sharedPreferences.getString("location_key", "DefaultLocation")
        val sharedName: String? = sharedPreferences.getString("name_key", "Siraj Alarm")
        editTextName.setText(sharedName)


        // get image url from local database
        val imageUrl:  String? = sharedPreferences.getString("dp_url_key", "not found")

        // Set DP using Glide
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(this.findViewById<CircleImageView>(R.id.profile_pic))


        doneButton.setOnClickListener{
        // get image url from local database
        val imageUrl:  String? = sharedPreferences.getString("dp_url_key", "not found")

        // Set DP using Glide
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(this.findViewById<CircleImageView>(R.id.profile_pic))


            doneButton.setOnClickListener{

            val name: String = editTextName.text.toString().trim()

            // Store data locally
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("name_key", name)
            editor.apply()
            editor.commit()



            // upload photo to db
            if (imageuri != null) {
                Toast.makeText(this, "Uploading Image...", Toast.LENGTH_LONG).show()
                updatePhoto()
            } else {
                finish()
            }

            // Store data in firebase
            reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("username").setValue(name)
        }


    }

    // select an image from Gallery
    fun pickfromGallery() {

        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        activityResultLauncher.launch(galleryIntent)
    }



    fun addProfilePicUrlToDatabase(url : String){

        // store profession and tags in realtime database
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child("Users").child(uid.toString())

        reference.child("url").setValue(url)

    }

    // Start galleryIntent for result
    fun profileActivityForResult() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

                val resultCode = result.resultCode
                val imageData = result.data

                if (resultCode == RESULT_OK && imageData != null) {

                    imageuri = imageData.data

                    //Picasso

                    imageuri?.let {

                        Picasso.get().load(it).into(profilePic)
                    }

                }else{
                    imageuri = null
                }

            })


    }

    fun updatePhoto(){

        doneButton.isClickable = false



        //Image Compressor
        val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()

        //UUID
        val imageName = FirebaseAuth.getInstance().uid.toString()

        val imageReference = storageReference.child("profilepictures").child(imageName)


        imageuri?.let { uri ->

            imageReference.putBytes(reducedImage).addOnSuccessListener {
                Toast.makeText(this, "Image updated" ,Toast.LENGTH_SHORT).show()

                //downloadable url
                val myUploadImageReference = storageReference.child("profilepictures").child(imageName)
                myUploadImageReference.downloadUrl.addOnSuccessListener { url ->

                    imageURL = url.toString()
                    addProfilePicUrlToDatabase(imageURL)

                    // Store image url locally
                    val sharedPreferences: SharedPreferences = this.getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("dp_url_key", imageURL)
                    editor.apply()
                    editor.commit()

                    Toast.makeText(this, "Uploading profile pic...", Toast.LENGTH_SHORT)
                    finish()
                }

            }.addOnFailureListener{

                Toast.makeText(this, it.localizedMessage ,Toast.LENGTH_SHORT).show()

            }

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
}