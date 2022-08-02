package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.CustomerPreviewAdapter
import com.jobdoneindia.jobdone.adapter.ScheduledJobsPreviewAdapter
import com.jobdoneindia.jobdone.databinding.ActivityWorkerDashboardBinding
import java.lang.Exception
import java.util.*

data class CustomersPreview( val customers_name: String, val customer_message: String)
data class ScheduledJobsPreview(val workers_name: String, val schedule_date: String, val schedule_location: String, val time: String)

class WorkerDashboardActivity : AppCompatActivity() {
    private val customersPreview = mutableListOf<CustomersPreview>()
    private val scheduledJobsPreview = mutableListOf<ScheduledJobsPreview>()

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mainBinding: ActivityWorkerDashboardBinding
    private val permissionId = 2
    private val REQUEST_CHECK_SETTINGS = 0x1

    private val userSharedPreferences = "usersharedpreference"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityWorkerDashboardBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        supportActionBar?.hide()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = getSharedPreferences(userSharedPreferences, Context.MODE_PRIVATE)

        if (!checkPermissions()) {
            checkGpsStatus()
            getLocation()
            saveLocationLocally(sharedPreferences)
        }

        // Fetch location from local database and display
        val sharedLocation: String? = sharedPreferences.getString("location_key", "DefaultLocation")
        val sharedName: String? = sharedPreferences.getString("name_key", "Siraj Alarm")
        Toast.makeText(this, sharedLocation, Toast.LENGTH_LONG)
        mainBinding.txtAddress.text = sharedLocation.toString()
        mainBinding.name.text = sharedName.toString()

        mainBinding.btnSetLocation.setOnClickListener {
            checkGpsStatus()
            getLocation()
            saveLocationLocally(sharedPreferences)
        }

        customersPreview.add(CustomersPreview("Avinash Prasad", "asfsfsd"))
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
        customersPreview.add(CustomersPreview("Avinash Prasad", "dfgdfg"))

        val customersList: RecyclerView = findViewById(R.id.recyclerview_customers_preview)
        customersList.adapter = CustomerPreviewAdapter(customersPreview)
        customersList.layoutManager = LinearLayoutManager(this)


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
        workersList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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

                R.id.menuChat -> {
                    startActivity(
                        Intent(applicationContext, WorkerDashboardActivity::class.java).setFlags(
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
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if(isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        mainBinding.txtAddress.text = list[0].getAddressLine(0)
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

}
