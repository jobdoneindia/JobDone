package com.jobdoneindia.jobdone.adapter;

import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.ScheduledJobsPreview

class ScheduledJobsPreviewAdapter( val scheduledJobs: MutableList<ScheduledJobsPreview>): RecyclerView.Adapter<ScheduledJobsPreviewAdapter.MyViewHolder>() {

     override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): MyViewHolder {
         val inflater = LayoutInflater.from(parent.context)
         val view = inflater.inflate(R.layout.scheduled_job_items, parent,false)
         return MyViewHolder(view)

     }

    override  fun  onBindViewHolder( holder: MyViewHolder, position: Int ){
        holder.userNameScheduled.text = scheduledJobs[position].workers_name
        holder.dateScheduled.text = scheduledJobs[position].schedule_date.toString()
        holder.locationScheduled.text = scheduledJobs[position].schedule_location.toString()
        holder.timeScheduled.text = scheduledJobs[position].time
    }

    override fun getItemCount(): Int {
        return scheduledJobs.size
    }

    inner class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        var userNameScheduled = itemView.findViewById<TextView>(R.id.username_forschedulejobs)
        var dateScheduled = itemView.findViewById<TextView>(R.id.schedule_date)
        var locationScheduled = itemView.findViewById<TextView>(R.id.schedule_location)
        var timeScheduled = itemView.findViewById<TextView>(R.id.schedule_time)
    }
}
