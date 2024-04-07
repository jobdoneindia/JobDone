package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.utils.AadhaarUtil
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class AdhaarValidation : AppCompatActivity() {

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageCardView1: CardView
    private lateinit var imageCardView2: CardView
    private lateinit var proceedButton: Button
    private lateinit var validateButton: Button
    private lateinit var validationLayout: RelativeLayout
    private lateinit var editTextAadhaar: EditText
    private var isImage1Captured = false
    private var isImage2Captured = false
    private var isImage1Uploaded = false
    private var isImage2Uploaded = false

    private lateinit var imageUri1: Uri
    private lateinit var imageUri2: Uri

    private var currentImageView: ImageView? = null
    private lateinit var currentImageUri: Uri
    private var firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference : StorageReference = firebaseStorage.reference

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = database.reference.child("Users")

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_CROP = 2
        private const val REQUEST_PERMISSION_CODE = 101
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adhaar_validation)
        supportActionBar?.hide()

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
        imageCardView1 = findViewById(R.id.imageCardView1)
        imageCardView2 = findViewById(R.id.imageCardView2)
        proceedButton = findViewById(R.id.proceedButton)
//        validateButton = findViewById(R.id.validateButton)
//        validationLayout = findViewById(R.id.validationLayout)
//        editTextAadhaar = findViewById(R.id.editTextAadhaar)

        // Disable proceedbtn initially
        proceedButton.isEnabled = false

        imageView1.setOnClickListener {
            currentImageView = imageView1
            checkPermissionAndDispatchTakePictureIntent()
        }

        imageView2.setOnClickListener {
            currentImageView = imageView2
            checkPermissionAndDispatchTakePictureIntent()
        }

        proceedButton.setOnClickListener {
            // Hide ImageView, Button, and CardView
            imageView1.visibility = View.GONE
            imageView2.visibility = View.GONE
            imageCardView1.visibility = View.GONE
            imageCardView2.visibility = View.GONE
            proceedButton.visibility = View.GONE
//            validationLayout.visibility = View.VISIBLE

            // Check if verification status is approved before opening WorkerDashboardActivity
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
            userRef.child("verificationStatus").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)
                    if (status == "approved") {
                        // Open WorkerDashboardActivity when status is approved
                        val intent = Intent(this@AdhaarValidation, WorkerDashboardActivity::class.java)
                        startActivity(intent)
                        // Finish this activity
                        finish()
                    } else {
                        // Open PendingActivity when status is not approved
                        val intent = Intent(this@AdhaarValidation, PendingActivity::class.java)
                        startActivity(intent)
                        // Finish this activity
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                    Toast.makeText(this@AdhaarValidation, "Error retrieving verification status: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }

        val sharedPreferences = getSharedPreferences("Validadhaar", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("validateAadhaarId", false)
        editor.apply() // Don't forget to apply changes


        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userRef.child("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java)
                if (status == "pending") {
                    // Show pending status
                    Toast.makeText(this@AdhaarValidation, "Your status is pending", Toast.LENGTH_SHORT).show()
                } else if (status == "approved") {
                    // Open WorkerDashboardActivity when status changes to approved
                    val intent = Intent(this@AdhaarValidation, WorkerDashboardActivity::class.java)
                    startActivity(intent)
                    // Finish this activity
                    finish()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
                Toast.makeText(this@AdhaarValidation, "Error retrieving verification status: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


//        val validateAadhaarId = sharedPreferences.getBoolean("validateAadhaarId", true)
//
//        validateButton.setOnClickListener {
//            val aadhaarNumber = editTextAadhaar.text.toString()
//            if (validateAadhaarId(aadhaarNumber)) {
//                // Show Aadhar ID validation UI
//                Toast.makeText(this, "valid Aadhar ID", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, WorkerDashboardActivity::class.java)
//                startActivity(intent)
//            } else {
//                // Show error message if Aadhar ID is invalid
//                Toast.makeText(this, "Invalid Aadhar ID, to continue please provide valid ADHAAR Number", Toast.LENGTH_SHORT).show()
//            }
//        }
    }


//    private fun validateAadhaarId(aadhaarNumber: String): Boolean {
//        // Call the validate function from AadhaarUtil class
//        return AadhaarUtil.isAadhaarNumberValid(aadhaarNumber)
//    }

    private fun checkPermissionAndDispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CODE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val imageFile = createImageFile()
            currentImageUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                imageFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${UUID.randomUUID()}",
            ".jpg",
            storageDir
        )
    }

    // Inside onActivityResult method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentImageUri?.let { uri ->
                val options = UCrop.Options()
                options.setCompressionQuality(70)
                options.setHideBottomControls(true)
                options.setToolbarTitle("Crop Image")
                val aspectRatioWidth = 4f
                val aspectRatioHeight = 3f
                UCrop.of(uri, Uri.fromFile(createImageFile()))
                    .withAspectRatio(aspectRatioWidth, aspectRatioHeight)
                    .withOptions(options)
                    .start(this, REQUEST_IMAGE_CROP)

                if (currentImageView == imageView1) {
                    imageUri1 = uri
                    isImage1Captured = true
                } else if (currentImageView == imageView2) {
                    imageUri2 = uri
                    isImage2Captured = true
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
            val croppedUri = UCrop.getOutput(data!!)
            currentImageView?.setImageURI(croppedUri)
            currentImageView?.alpha = 1f
            if (currentImageView == imageView1) {
                imageUri1 = croppedUri!!
                if (!isImage1Uploaded) {
                    // Call the method to upload image1 if it's not already uploaded
                    uploadPhoto(imageUri1, imageView1)
                    isImage1Uploaded = true
                }
            } else if (currentImageView == imageView2) {
                imageUri2 = croppedUri!!
                if (!isImage2Uploaded) {
                    // Call the method to upload image2 if it's not already uploaded
                    uploadPhoto(imageUri2, imageView2)
                    isImage2Uploaded = true
                }
            }
            if (isImage1Captured && isImage2Captured) {
                proceedButton.isEnabled = true
            }
        }
    }

    private fun uploadPhoto(imageUri: Uri, imageView: ImageView) {
        if (imageUri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
            val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()
            val imageId = UUID.randomUUID().toString()
            val imageName = "${FirebaseAuth.getInstance().uid}_$imageId.jpg"
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val userDirectoryReference = storageReference.child("documents").child(userId)
            val imageReference = userDirectoryReference.child(imageName)
            imageReference.putBytes(reducedImage).addOnSuccessListener { uploadTask ->
                uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                    val imageURL = uri.toString()
                    val sharedPreferences: SharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    val previousImageURL1 = sharedPreferences.getString("document_url_key1", null)
                    val previousImageURL2 = sharedPreferences.getString("document_url_key2", null)
                    // Delete the previous image if it exists
                    if (imageView == imageView1) {
                        if (!previousImageURL1.isNullOrEmpty()) {
                            FirebaseStorage.getInstance().getReferenceFromUrl(previousImageURL1).delete()
                        }
                        editor.putString("document_url_key1", imageURL)
                    } else if (imageView == imageView2) {
                        if (!previousImageURL2.isNullOrEmpty()) {
                            FirebaseStorage.getInstance().getReferenceFromUrl(previousImageURL2).delete()
                        }
                        editor.putString("document_url_key2", imageURL)
                    }
                    editor.apply()

                    // Set verification status to pending after uploading Aadhaar
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                    userRef.child("verificationStatus").setValue("pending")
                        .addOnSuccessListener {
                            Toast.makeText(this@AdhaarValidation, "Aadhaar uploaded successfully. Pending for verification.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@AdhaarValidation, "Error setting verification status: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    // Check if both images are uploaded, then call addDocuments function
                    val imageURL1 = sharedPreferences.getString("document_url_key1", null)
                    val imageURL2 = sharedPreferences.getString("document_url_key2", null)
                    if (!imageURL1.isNullOrEmpty() && !imageURL2.isNullOrEmpty()) {
                        addDocuments(imageURL1, imageURL2)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addDocuments(imageURL1: String, imageURL2: String) {
        // Assuming you have a Firebase Realtime Database reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Create a reference to the user node in the Realtime Database
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        // Create a reference to the image files in Firebase Storage
        val imageName1 = "${userId}_adhaar_front"
        val imageReference1 = FirebaseStorage.getInstance().reference
            .child("documents")
            .child(imageName1)

        val imageName2 = "${userId}_adhaar_back"
        val imageReference2 = FirebaseStorage.getInstance().reference
            .child("documents")
            .child(imageName2)

        // Create a map containing the URLs for adhaar front and back images
        val data = hashMapOf<String, Any>(
            "adhaar front" to imageURL1,
            "adhaar back" to imageURL2
            // Add any additional data you want to store
        )

        // Update the user node in the Realtime Database with the image URLs
        userRef.updateChildren(data)
            .addOnSuccessListener {
                // Image URLs added successfully
                // You can add any additional actions you want to perform here
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Toast.makeText(this, "Error updating image URLs: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            }
        }
    }
}