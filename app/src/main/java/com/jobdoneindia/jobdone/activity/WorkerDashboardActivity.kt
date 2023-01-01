package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.CustomerPreviewAdapter
import com.jobdoneindia.jobdone.databinding.ActivityWorkerDashboardBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

data class CustomersPreview( val customers_name: String, val customer_message: String, val dp_url: String)
data class ScheduledJobsPreview(val workers_name: String, val schedule_date: String, val schedule_location: String, val time: String)

class WorkerDashboardActivity : AppCompatActivity() {
    private val customersPreview = mutableListOf<CustomersPreview>()
    private val scheduledJobsPreview = mutableListOf<ScheduledJobsPreview>()

    private lateinit var adapter: CustomerPreviewAdapter

    private lateinit var mDbRef : DatabaseReference
    private lateinit var mAuth:FirebaseAuth

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var btnLinkBank: Button

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mainBinding: ActivityWorkerDashboardBinding
    private val permissionId = 2
    private val REQUEST_CHECK_SETTINGS = 0x1

    private lateinit var editor: SharedPreferences.Editor

    private val userSharedPreferences = "usersharedpreference"
    private lateinit var sharedPreferences: SharedPreferences

    val firebase : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityWorkerDashboardBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        supportActionBar?.hide()

        // Display Earnings Spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(this, R.array.earningsDisplayOptions, R.layout.spinner_item)
            spinner.adapter = adapter
        }

        // Link Bank Account
        btnLinkBank = findViewById(R.id.btnLinkBank)
        btnLinkBank.setOnClickListener {
            val intent = Intent(this, LinkBankActivity::class.java)
            startActivity(intent)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = getSharedPreferences(userSharedPreferences, Context.MODE_PRIVATE)

        // Fetch location from local database and display
        val sharedLocation: String? = sharedPreferences.getString("location_key", "DefaultLocation")
        val sharedName: String? = sharedPreferences.getString("name_key", "Siraj Alarm")
        val sharedTags: String? = sharedPreferences.getString("tags_key", "NotFound")
        Toast.makeText(this, sharedTags, Toast.LENGTH_LONG).show()
        mainBinding.txtAddress.text = sharedLocation.toString()
        mainBinding.name.text = sharedName.toString()

        if (sharedLocation == "DefaultLoaction" || !checkPermissions()) {
            checkGpsStatus()
            getLocation(sharedPreferences)
            saveLocationLocally(sharedPreferences)
        }

        // get image url from local database
        val imageUrl:  String? = sharedPreferences.getString("dp_url_key", "not found")

        // Set DP using Glide
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(this.findViewById<CircleImageView>(R.id.user_dp))

        mainBinding.btnSetLocation.setOnClickListener {
            checkGpsStatus()
            getLocation(sharedPreferences)
            saveLocationLocally(sharedPreferences)
        }

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        /*customersPreview.add(CustomersPreview("Avinash Prasad", "asfsfsd"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "sdfsdf"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "sdfsdf"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dsfsdf"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "sdfsdf"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "sdfsdf"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "sdfsdf"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfdg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))*/

        /*adapter = CustomerPreviewAdapter(this, customersPreview)
        val customersList: RecyclerView = findViewById(R.id.recyclerview_customers_preview)
        customersList.adapter = adapter
        customersList.layoutManager = LinearLayoutManager(this)

        mDbRef.child("Users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        customersPreview.add(CustomersPreview(currentUser?.username.toString(), "Last Message", currentUser?.url.toString()))
                    }
                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        scheduledJobsPreview.add(ScheduledJobsPreview("Avinash Prasad", "28-07-2022", "Matigara, Sukti Godown", "10:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Siraj Alam", "30-07-2022", "Matigara, Sukti Godown", "10:00 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Yuvraj Chhetri", "28-07-2022", "Matigara, Sukti Godown", "01:30 PM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Subhajit Chowdhury", "28-07-2022", "Matigara, Sukti Godown", "11:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Avinash Prasad", "28-07-2022", "Matigara, Sukti Godown", "10:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Siraj Alam", "30-07-2022", "Matigara, Sukti Godown", "10:00 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Yuvraj Chhetri", "28-07-2022", "Matigara, Sukti Godown", "01:30 PM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Subhajit Chowdhury", "28-07-2022", "Matigara, Sukti Godown", "11:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Avinash Prasad", "28-07-2022", "Matigara, Sukti Godown", "10:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Siraj Alam", "30-07-2022", "Matigara, Sukti Godown", "10:00 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Yuvraj Chhetri", "28-07-2022", "Matigara, Sukti Godown", "01:30 PM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Subhajit Chowdhury", "28-07-2022", "Matigara, Sukti Godown", "11:30 AM"))

        val workersList: RecyclerView = findViewById((R.id.recyclerview_workers_preview))
        workersList.adapter = ScheduledJobsPreviewAdapter(scheduledJobsPreview)
        workersList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)*/

        // Set Home selected (in bottom nav bar)
        bottomNavigationView = findViewById(R.id.bottomNavigationDrawer)
        bottomNavigationView.selectedItemId = R.id.menuHome

        // Perform item selected listener (bottom nav bar)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    return@setOnItemSelectedListener true
                }

                R.id.menuRewards -> {
                    startActivity(
                        Intent(applicationContext, RewardsActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuChats -> {

                    startActivity(
                        Intent(applicationContext, ChatUserList::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

                R.id.menuAccount -> {
                    startActivity(
                        Intent(applicationContext, WorkerProfileActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

    }

    // store location locally
    private fun saveLocationLocally(sharedPreferences: SharedPreferences) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("location_key", mainBinding.txtAddress.text.toString())
        editor.apply()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), permissionId)
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
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        mainBinding.txtAddress.text = list[0].getAddressLine(0)

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
        val locationManager: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show()
        } else {
            displayLocationSettingsRequest(this)
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show()
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

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)

        val result: Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(builder.build())

        result.addOnSuccessListener(this, object : OnSuccessListener<LocationSettingsResponse>{
            override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                checkGpsStatus()
            }
        })

        result.addOnFailureListener(this, object : OnFailureListener{
            override fun onFailure(e: Exception) {
                if (e is ResolvableApiException) {
                    try {
                        val resolvableApiException: ResolvableApiException = e
                        resolvableApiException.startResolutionForResult(this@WorkerDashboardActivity, REQUEST_CHECK_SETTINGS)

                    } catch (sendIntentException: IntentSender.SendIntentException) {
                        sendIntentException.printStackTrace()
                    }
                }
            }

        })

    }

    // TO SHOW STATUS
    fun status(status : String) {
        mDbRef = FirebaseDatabase.getInstance().getReference("Users").child(firebase.uid)

        var map : HashMap<String , Any>
                = HashMap<String ,Any>()

        map.put("status" , status)
        mDbRef.updateChildren(map as Map<String, Any>)

    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

    override fun onBackPressed() {


        onSuperBackPressed()

    }
    fun onSuperBackPressed() {
        super.onBackPressed()
    }
    override fun onDestroy() {
        super.onDestroy()

        val mAuth = FirebaseAuth.getInstance()
/*
        mAuth?.removeAuthStateListener(mListener!!)
*/
    }
}
