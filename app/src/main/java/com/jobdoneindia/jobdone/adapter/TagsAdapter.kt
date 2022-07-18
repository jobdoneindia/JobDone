package com.jobdoneindia.jobdone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jobdoneindia.jobdone.fragment.Categories
import com.jobdoneindia.jobdone.R

class TagsAdapter(val category: MutableList<Categories>): RecyclerView.Adapter<TagsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textButton.text = category[position].service_name
        holder.workerCount.text = category[position].services_count.toString()
    }

    override fun getItemCount(): Int {
        return category.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textButton = itemView.findViewById<TextView>(R.id.tagButton)
        var workerCount = itemView.findViewById<TextView>(R.id.worker_count)
    }
}