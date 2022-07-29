package com.jobdoneindia.jobdone.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Api
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.CustomerPreviewAdapter
import com.jobdoneindia.jobdone.adapter.ScheduledJobsPreviewAdapter
import com.jobdoneindia.jobdone.databinding.ActivityWorkerDashboardBinding
import java.util.*
import java.util.zip.Inflater

data class CustomersPreview( val customers_name: String, val customer_message: String)
data class ScheduledJobsPreview(val workers_name: String, val schedule_date: String, val schedule_location: String, val time: String)

class WorkerDashboardActivity : AppCompatActivity() {
    private val customersPreview = mutableListOf<CustomersPreview>()
    private val scheduledJobsPreview = mutableListOf<ScheduledJobsPreview>()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mainBinding: ActivityWorkerDashboardBinding
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityWorkerDashboardBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        supportActionBar?.hide()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mainBinding.btnSetLocation.setOnClickListener {
            getLocation()
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
        scheduledJobsPreview.add(ScheduledJobsPreview("Yuvraj Chettri", "28-07-2022", "Matigara, Sukti Godown", "01:30 PM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Subhajit Chowdhury", "28-07-2022", "Matigara, Sukti Godown", "11:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Avinash Prasad", "28-07-2022", "Matigara, Sukti Godown", "10:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Siraj Alam", "30-07-2022", "Matigara, Sukti Godown", "10:00 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Yuvraj Chettri", "28-07-2022", "Matigara, Sukti Godown", "01:30 PM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Subhajit Chowdhury", "28-07-2022", "Matigara, Sukti Godown", "11:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Avinash Prasad", "28-07-2022", "Matigara, Sukti Godown", "10:30 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Siraj Alam", "30-07-2022", "Matigara, Sukti Godown", "10:00 AM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Yuvraj Chettri", "28-07-2022", "Matigara, Sukti Godown", "01:30 PM"))
        scheduledJobsPreview.add(ScheduledJobsPreview("Subhajit Chowdhury", "28-07-2022", "Matigara, Sukti Godown", "11:30 AM"))

        val workersList: RecyclerView = findViewById((R.id.recyclerview_workers_preview))
        workersList.adapter = ScheduledJobsPreviewAdapter(scheduledJobsPreview)
        workersList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            mainBinding.apply {
                                txtAddress.text = "${list[0].getAddressLine(0)}"
                            }
                    }
                }
            }
        } else {
            requestPermissions()
        }
    }

}
