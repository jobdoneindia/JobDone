package com.jobdoneindia.jobdone.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.DashboardActivity
import java.util.jar.Manifest

class FragmentSetDP : Fragment() {

    private lateinit var profilePic: ImageView
    private lateinit var imageuri: Uri


    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_set_dp, container, false)

        //firebase to store image
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val storageReference = FirebaseStorage.getInstance().reference.child(uid.toString())



        // onClickListener for btnEditDP
        profilePic = root.findViewById<ImageView>(R.id.profile_pic)

        val btnEditDP = root.findViewById<FloatingActionButton>(R.id.btnEditDP)
        btnEditDP.setOnClickListener {

            pickfromGallery()

        }

        root.findViewById<Button>(R.id.nextButton).setOnClickListener{
                view: View ->

            storageReference.putFile(imageuri)
            Navigation.findNavController(view).navigate(R.id.action_fragmentSetDP_to_fragmentChooseMode)
        }

        return root
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