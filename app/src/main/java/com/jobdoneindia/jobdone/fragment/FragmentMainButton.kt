package com.jobdoneindia.jobdone.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import android.renderscript.ScriptGroup
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.LocationCallback
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jobdoneindia.jobdone.R
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class FragmentMainButton: Fragment() {

    private lateinit var mainButton: ImageButton
    private lateinit var userName : TextView
    private lateinit var txtAddress: TextView

    private var mDbRef = FirebaseDatabase.getInstance().reference.child("Users")

    private val userSharedPreferences = "usersharedpreference"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private val REQUEST_CHECK_SETTINGS = 0x1


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate layout for this fragment
        val root = inflater.inflate(R.layout.fragment_mainbutton, container, false)

        // Exit Transition
        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.fade)

        // Enter Transition
        enterTransition = transitionInflater.inflateTransition(R.transition.explode)

        YoYo.with(Techniques.Bounce)
            .duration(700)
            .repeat(1)
            .playOn(root.findViewById(R.id.main_button))

        // user name
        userName = root.findViewById(R.id.user_name)
        val args = this.arguments
        val inputData = args?.get("username")
        userName.text = inputData.toString()

        // fetching data from local database
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        sharedPreferences = requireContext().getSharedPreferences(userSharedPreferences, Context.MODE_PRIVATE)
        val sharedLocation: String? = sharedPreferences.getString("location_key", "DefaultLocation")
        val sharedName: String? = sharedPreferences.getString("name_key", "Siraj Alarm")
        txtAddress = root.findViewById(R.id.txtAddress)
        txtAddress.text = sharedLocation.toString()
        val userName: TextView = root.findViewById(R.id.user_name)
        userName.text = sharedName.toString()

        val userDP: CircleImageView = root.findViewById(R.id.user_dp)






        // get image url from local database
        val imageUrl:  String? = sharedPreferences.getString("dp_url_key", "not found")
//        Toast.makeText(requireContext(), imageUrl, Toast.LENGTH_LONG).show()

        // Set DP using Glide
        Glide.with(requireContext())
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(userDP)

        // get location if location permission is not allowed
        if (sharedLocation == "DefaultLocation" || !checkPermissions()) {
            checkGpsStatus()
            getLocation(sharedPreferences)
            saveLocationLocally(sharedPreferences)
        }

        // update location onClick listener
        root.findViewById<TextView>(R.id.btnSetLocation).setOnClickListener {
            checkGpsStatus()
            getLocation(sharedPreferences)
            saveLocationLocally(sharedPreferences)
        }

        // Main Button Onclick listener
        mainButton = root.findViewById<ImageButton>(R.id.main_button)
        mainButton.setOnClickListener { view: View ->
            mainButton.setImageResource(R.drawable.img_jobdone_round_plain)
            Navigation.findNavController(view)
                .navigate(R.id.action_fragmentMainButton_to_fragmentTags)
        }
        return root
    }

    // store location locally
    private fun saveLocationLocally(sharedPreferences: SharedPreferences) {
        editor = sharedPreferences.edit()
        editor.putString("location_key", txtAddress.text.toString())
        editor.apply()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), permissionId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation(sharedPreferences)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(sharedPreferences: SharedPreferences) {
        if (checkPermissions()) {
            if(isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())

                        val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        txtAddress.text = list[0].getAddressLine(0)

                        // save latitude and longitude locally
                        editor = sharedPreferences.edit()
                        editor.putFloat("latitude", location.latitude.toFloat())
                        editor.putFloat("longitude", location.longitude.toFloat())
                        editor.apply()

                        // saving location in firebase db
                        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        val reference : DatabaseReference = database.reference.child("Users").child(uid.toString())
                        reference.child("Location").setValue(arrayListOf(location.latitude, location.longitude))

                        val geoFire = GeoFire(database.reference.child("geofire"))
                        geoFire.setLocation(uid, GeoLocation(location.latitude, location.longitude))

                        saveLocationLocally(sharedPreferences)

                    }
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkGpsStatus() {
        val locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {
            Toast.makeText(requireContext(), "GPS Enabled", Toast.LENGTH_SHORT).show()
        } else {
            displayLocationSettingsRequest(requireContext())
            Toast.makeText(requireContext(), "Please enable GPS", Toast.LENGTH_SHORT).show()
        }
    }

    // function to display dialog box to turn on GPS
    private fun displayLocationSettingsRequest(context: Context) {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(10000/2)

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest   .Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(requireContext())

        val result = settingsClient.checkLocationSettings(builder.build())

        result.addOnSuccessListener(requireActivity(), object : OnSuccessListener<LocationSettingsResponse>{
            override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                checkGpsStatus()
            }
        })

        result.addOnFailureListener(requireActivity(), object : OnFailureListener{
            override fun onFailure(e: Exception) {
                if (e is ResolvableApiException) {
                    try {
                        val resolvableApiException: ResolvableApiException = e
                        resolvableApiException.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)

                    } catch (sendIntentException: IntentSender.SendIntentException) {
                        sendIntentException.printStackTrace()
                    }
                }
            }

        })

    }

}