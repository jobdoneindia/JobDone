package com.jobdoneindia.jobdone.activity

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.adapter.CustomerPreviewAdapter
import com.jobdoneindia.jobdone.adapter.ScheduledJobsPreviewAdapter
import java.util.*
import java.util.zip.Inflater

data class CustomersPreview( val customers_name: String, val customer_message: String)
data class ScheduledJobsPreview(val workers_name: String, val schedule_date: String, val schedule_location: String, val time: String)
class WorkerDashboardActivity : AppCompatActivity() {
    private val customersPreview = mutableListOf<CustomersPreview>()
    private val scheduledJobsPreview = mutableListOf<ScheduledJobsPreview>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_dashboard)
        supportActionBar?.hide()


        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))
        customersPreview.add(CustomersPreview("Avinash Prasad", "chutiya"))

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



}
