package com.jobdoneindia.jobdone.fragment

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
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
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
import java.util.*


class FragmentSetDP : Fragment() {

    private lateinit var profilePic: ImageView
    private lateinit var nextButton: Button
    private lateinit var imageURL: String
    var imageuri: Uri? = null
    var firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference : StorageReference = firebaseStorage.reference
    lateinit var imageView : CircleImageView
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()

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
            if (imageuri != null){
                uploadPhoto()
            } else {
                // Store image url locally
                val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("dp_url_key", "https://secondchancetinyhomes.org/wp-content/uploads/2016/09/empty-profile.png")
                editor.apply()
                editor.commit()
                // move to next frag
                Navigation.findNavController(view).navigate(R.id.action_fragmentSetDP_to_fragmentChooseMode)
            }
             // and go to next activity
            /*val intent = Intent(requireContext(), DashboardActivity::class.java)
            startActivity(intent)*/
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





            /*storageReference.putBytes(reducedImage)
                .addOnSuccessListener {

                    Log.i("xxx", "Success uploading Image to Firebase!!!")

                    storageReference.downloadUrl.addOnSuccessListener {

                        //getting image url
                        Log.i("xxx",it.toString())

                    }.addOnFailureListener {

                        Log.i("xxx", "Error getting image download url")
                    }

                }.addOnFailureListener {

                    Log.i("xxx", "Failed uploading image to server")

                }*/


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

        nextButton.isClickable = false


        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageuri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()

        //UUID
        val imageName = FirebaseAuth.getInstance().uid.toString()

        val imageReference = storageReference.child("images").child(imageName)


        reducedImage?.let { uri ->

            imageReference.putBytes(uri).addOnSuccessListener {
                Toast.makeText(requireContext(), "Image uploaded" ,Toast.LENGTH_SHORT).show()

                //downloadable url
                val myUploadImageReference = storageReference.child("images").child(imageName)
                myUploadImageReference.downloadUrl.addOnSuccessListener { url ->

                    imageURL = url.toString()
                    addProfilePicUrlToDatabase(imageURL)

                    // Store image url locally
                    val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("dp_url_key", imageURL)
                    editor.apply()
                    editor.commit()


                    view?.let { view: View -> Navigation.findNavController(view).navigate(R.id.action_fragmentSetDP_to_fragmentChooseMode) }
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