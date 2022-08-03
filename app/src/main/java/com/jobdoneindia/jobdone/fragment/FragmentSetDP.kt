package com.jobdoneindia.jobdone.fragment

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.DashboardActivity
import com.jobdoneindia.jobdone.models.User
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import java.util.jar.Manifest


class FragmentSetDP : Fragment() {

    private lateinit var profilePic: ImageView
    private lateinit var nextButton: Button
    private lateinit var imageURL: String
    var imageuri: Uri? = null
    var firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference : StorageReference = firebaseStorage.reference
    lateinit var imageView : CircleImageView
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference : DatabaseReference = database.reference.child("MyUsers")

    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_set_dp, container, false)



        profileActivityForResult()
        // onClickListener for btnEditDP
        profilePic = root.findViewById<ImageView>(R.id.profile_pic)

        val btnEditDP = root.findViewById<FloatingActionButton>(R.id.btnEditDP)
        btnEditDP.setOnClickListener {
            pickfromGallery()

        }
        nextButton = root.findViewById(R.id.nextButton)
        root.findViewById<Button>(R.id.nextButton).setOnClickListener {
            view: View ->

            nextButton.text = "Loading..."
            uploadPhoto() // and go to next activity
            /*val intent = Intent(requireContext(), DashboardActivity::class.java)
            startActivity(intent)*/

            Navigation.findNavController(view).navigate(R.id.action_fragmentSetDP_to_fragmentChooseMode)
        }



        return root
    }





    // select an image from Gallery
    private fun pickfromGallery() {

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
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

    fun addProfileToDatabase(url : String){



        val id : String = myReference.push().key.toString()

        val user = User(id,url)

        myReference.child(id).setValue(user).addOnCompleteListener { task ->

            if (task.isSuccessful){

                Toast.makeText(requireContext(), "Welcome to JobDone", Toast.LENGTH_SHORT).show()

                nextButton.isClickable = true



            }else{
                Toast.makeText(requireContext(),task.exception.toString(),Toast.LENGTH_SHORT).show()

            }

        }

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

                }

            })


    }

    fun uploadPhoto(){

        nextButton.isClickable = false

        //UUID
        val imageName = UUID.randomUUID().toString()

        val imageReference = storageReference.child("images").child(imageName)


        imageuri?.let { uri ->

            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(requireContext(), "Image uploaded" ,Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), DashboardActivity::class.java)
                startActivity(intent)

                //downloadable url
                val myUploadImageReference = storageReference.child("images").child(imageName)
                myUploadImageReference.downloadUrl.addOnSuccessListener { url ->

                    imageURL = url.toString()
                    addProfileToDatabase(imageURL)

                    // Store image url locally
                    val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("dp_url_key", imageURL)
                    editor.apply()
                    editor.commit()
                }

            }.addOnFailureListener{

                Toast.makeText(requireContext(), it.localizedMessage ,Toast.LENGTH_SHORT).show()

            }

        }

    }

    /*{
        result ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageuri = result.data!!.data!!
                profilePic.setImageURI(imageuri)
            }
    }*/


}