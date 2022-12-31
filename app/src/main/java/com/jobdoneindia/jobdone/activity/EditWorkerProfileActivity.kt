package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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

class EditWorkerProfileActivity : AppCompatActivity() {

    private lateinit var btnSetDP: ImageButton
    private lateinit var profilePic: CircleImageView
    private lateinit var doneButton: FloatingActionButton

    private lateinit var imageURL: String
    var imageuri: Uri? = null

    var firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference : StorageReference = firebaseStorage.reference

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = database.reference.child("Users")

    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    private val items = mutableListOf(
        "Electrician",
        "Appliances",
        "Plumber",
        "Carpenter",
        "Painter",
        "Driver",
        "Home Tutor",
        "Freelancer",
        "Pest Control",
        "Pandit",
        "Laundry",
        "RO Service",
        "House Maid",
        "Water Supplier",
        "Photographer",
        "Videographer",
        "Vehicle Service",
        "Vehicle Washing",
    )
    private val tags = mapOf(
        "Electrician" to arrayListOf("AC Repair", "Plug", "Socket", "Wires"),
        "Appliances" to arrayListOf(
            "TV Repair",
            "AC Repair",
            "Washing machine repair",
            "Gizzer Repair",
            "Air Cooler Repair",
            "Fridge Repair",
            "Mixer Grinder Repair",
            "Speaker Repair"
        ),
        "Plumber" to arrayListOf(
            "Install Water Supply System",
            "Install Waste Disposal System",
            "Repair Pipeline Issues"
        ),
        "Carpenter" to arrayListOf(
            "New Furniture Making",
            "Old Furniture Repair",
            "Install Modular Kichen"
        ),
        "Painter" to arrayListOf(
            "Furniture Painting",
            "Contract Works(Office, Home, Cafe)",
            "Wall Painting"
        ),
        "Driver" to arrayListOf("Personal Car", "Loading Van", "JCB", "Dumper", "Truck"),
        "Home Tutor" to arrayListOf(
            "Class 1-5",
            "Class 6-10",
            "Class 11-12",
            "Graduates",
            "Science Teacher",
            "Maths Teacher",
            "Commerce Teacher",
            "Arts Teacher"
        ),
        "Freelancer" to arrayListOf(
            "Graphic Designer",
            "Content Writer",
            "Web Designer",
            "Virtual Assistant",
            "App Developer",
            "Video Editor",
            "Social Media Manager",
            "Transcriber"
        ),
        "Pest Control" to arrayListOf("Complete Pest Control", "Sanitisation"),
        "Pandit" to arrayListOf(
            "Bhagwat Katha",
            "Bhoomi Pooja",
            "Antim Sanskar",
            "Satya Narayan Katha",
            "Shaadi",
            "Ganesh Utsav",
            "Hawan",
            "Vaastu Pooja"
        ),
        "Laundry" to arrayListOf("Washing CLothes", "Dry Clean", "Ironing Clothes"),
        "RO Service" to arrayListOf("RO Repair", "RO Install", "RO Service", "RO Parts Change"),
        "House Maid" to arrayListOf(
            "House Cleaning",
            "Cooking",
            "Washing Clothes",
            "Baby Care",
            "Utensil Cleaning"
        ),
        "Water Supplier" to arrayListOf("Drinking Water", "Tanker"),
        "Photographer" to arrayListOf(
            "Model",
            "Pre Wedding",
            "Wedding",
            "Album",
            "Birthday",
            "Freelance"
        ),
        "Videographer" to arrayListOf("Wedding", "Birthday", "Freelance"),
        "Vehicle Service" to arrayListOf(
            "Bike Repair",
            "Car Repair",
            "Loading Van Repair",
            "Truck Repair",
            "Servicing"
        ),
        "Vehicle Washing" to arrayListOf("Bike Wash", "Car Wash", "Loading Van Wash", "Truck Wash"),
        "Goods Transport Vehicle" to arrayListOf(
            "Three Wheeler",
            " Four Wheeler",
            "Six Wheeler",
            "Eight Wheeler",
            "Ten Wheeler"
        ),
    )

    private var selectedTags = mutableListOf<String>()

    private lateinit var adapterItems: ArrayAdapter<String>
    private lateinit var chipGroup: ChipGroup
    private lateinit var selectProfessionSpinner: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_worker_profile)

        // Add back button in Action Bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        profileActivityForResult()

        //database reference
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("Users")

        val editTextName = findViewById<EditText>(R.id.workerName)

        val sharedPreferences: SharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)

        // get image url from local database
        val imageUrl:  String? = sharedPreferences.getString("dp_url_key", "not found")

        val sharedName: String? = sharedPreferences.getString("name_key", "Your Name")
        editTextName.setText(sharedName.toString())

        // tags and profession selection
        chipGroup = findViewById(R.id.chip_group_tags)

        // autocompleteTextView
        var tagList = mutableListOf<String>()
        adapterItems = ArrayAdapter(this, R.layout.dropdown_item,items)


        val currentTags = sharedPreferences.getString("tags_key", "null")?.drop(1)?.dropLast(1)?.split(",")
        val currentProfession: String? = sharedPreferences.getString("profession_key", "null")
        // Spinner Logic
        selectProfessionSpinner = findViewById(R.id.select_profession_spinner)
        selectProfessionSpinner.setText(currentProfession.toString())
        selectProfessionSpinner.setAdapter(adapterItems)
        selectProfessionSpinner.setOnItemClickListener { adapterView, view, position, id ->
            tagList.clear()
            selectedTags.clear()
            val item: String = adapterView.getItemAtPosition(position).toString()
            for (x in tags[item]!!) {
                tagList.add(x)
            }
            updateTags(tagList)
        }

        tagList.clear()
        selectedTags.clear()
        for (x in currentTags!!) {
            tagList.add(x)
        }
        updateTags(tagList)

        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isChecked = true
        }

        // Set DP using Glide
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(this.findViewById<CircleImageView>(R.id.profile_pic))

        // Done fab button
        doneButton = findViewById(R.id.done_button)
        doneButton.setOnClickListener {

            val workerName = findViewById<EditText>(R.id.workerName).text.toString().trim()

            // Store data locally
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("name_key", workerName)
            editor.putString("profession_key", selectProfessionSpinner.text.toString())
            editor.putString("tags_key", selectedTags.toString())
            editor.apply()
            editor.commit()

            // upload photo to db
            if (imageuri != null) {
                Toast.makeText(this, "Uploading Image...", Toast.LENGTH_LONG).show()
                uploadPhoto()
            } else {
                finish()
                startActivity(Intent(applicationContext, WorkerProfileActivity::class.java))
            }

            // Store data in firebase
            reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("username").setValue(workerName)

            // store profession and tags in realtime database
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            reference.child(uid.toString()).child("Profession").setValue(selectProfessionSpinner.text.toString())
            reference.child(uid.toString()).child("Tags").setValue(selectedTags)
        }

        // Tags Selectors
        val tags = resources.getStringArray(R.array.tags)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tags)

        profilePic = findViewById(R.id.profile_pic)
        btnSetDP = findViewById(R.id.btnSetDP)
        btnSetDP.setOnClickListener {
            pickfromGallery()
        }



    }

    // on back pressed
    override fun onBackPressed() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Discard Changes?").setCancelable(true)
            // positive button text and action
            .setPositiveButton("Discard", DialogInterface.OnClickListener {
                    dialog, id ->
                finish()
                startActivity(Intent(applicationContext, WorkerProfileActivity::class.java))

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
                            dialog, id ->
                                finish()
                                startActivity(Intent(applicationContext, WorkerProfileActivity::class.java))

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

    // select an image from Gallery
    private fun pickfromGallery() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )


        }

        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        activityResultLauncher.launch(galleryIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(galleryIntent)

        }

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

    fun uploadPhoto(){

        doneButton.isClickable = false

        val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()

        //UUID
        val imageName = FirebaseAuth.getInstance().uid.toString()

        val imageReference = storageReference.child("profilepictures").child(imageName)


        reducedImage?.let { uri ->

            imageReference.putBytes(uri).addOnSuccessListener {
                Toast.makeText(this, "Image uploaded" , Toast.LENGTH_SHORT).show()

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

                Toast.makeText(this, it.localizedMessage , Toast.LENGTH_SHORT).show()

            }

        }

    }

    // Update tags in Chip Group
    private fun updateTags(tagList: MutableList<String>) {
        chipGroup.removeAllViews()
        for (index in tagList.indices) {
            val tagName = tagList[index]
            val chip = Chip(this)
            val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
            ).toInt()
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
            chip.text = tagName
            chip.isCheckable = true
            chip.isCheckedIconVisible = true
            chip.setCheckedIconResource(R.drawable.ic_done)

            //Added click listener on close icon to remove tag from ChipGroup
/*            chip.setOnCloseIconClickListener {
                tagList.remove(tagName)
                chipGroup.removeView(chip)
            }*/

            chip.setOnCheckedChangeListener { button, b ->
                if (chip.isChecked == true) {
                    if (selectedTags.size < 3) {
                        selectedTags.add(chip.text.toString())
                    } else {
                        chip.isChecked = false
                    }

                } else {
                    selectedTags.remove(chip.text.toString())
                }
                Toast.makeText(this, "Selected: ${selectedTags}", Toast.LENGTH_SHORT).show()
            }

            chipGroup.addView(chip)
        }
        val newChip = Chip(this)

        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,
            resources.displayMetrics
        ).toInt()
        newChip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        newChip.text = "+ Add New"
        newChip.isCheckable = false

        newChip.setOnClickListener {
            selectedTags.clear()
            updateTags(tagList)

            // get alert_dialog.xml view
            val li = LayoutInflater.from(this)
            val promptsView = li.inflate(R.layout.alert_dialog, null)
            val alertDialogBuilder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)

            // set alert_dialog.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView)
            val userInput = promptsView.findViewById<View>(R.id.etUserInput) as EditText

            // set dialog message
            alertDialogBuilder
                .setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener { dialog, id -> // get user input and set it to result
                        // edit text
                        Toast.makeText(
                            this,
                            "Entered: " + userInput.text.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        tagList.add(userInput.text.toString())
                        updateTags(tagList)
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

            // create alert dialog
            val alertDialog: android.app.AlertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.show()
        }

        chipGroup.addView(newChip)

    }

    // Start galleryIntent for result
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageuri = result.data!!.data!!
            profilePic.setImageURI(imageuri)
        }
    }

}