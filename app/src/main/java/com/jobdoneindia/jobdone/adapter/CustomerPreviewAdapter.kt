package com.jobdoneindia.jobdone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.CustomersPreview

class CustomerPreviewAdapter( val customer: MutableList<CustomersPreview>): RecyclerView.Adapter<CustomerPreviewAdapter.MyViewHolder>() {

    inner class MyViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){
        var customerName = itemView.findViewById<TextView>(R.id.customername)
        var customerlastmessage = itemView.findViewById<TextView>(R.id.cuslastmessage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.customer_items, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.customerName.text = customer[position].customers_name
        holder.customerlastmessage.text = customer[position].customer_message.toString()

    }

    override fun getItemCount(): Int {
        return customer.size
    }

}