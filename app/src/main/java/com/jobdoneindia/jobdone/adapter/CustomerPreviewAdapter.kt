package com.jobdoneindia.jobdone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.CustomersPreview
import de.hdodenhof.circleimageview.CircleImageView

class CustomerPreviewAdapter(val context: Context, val customer: MutableList<CustomersPreview>): RecyclerView.Adapter<CustomerPreviewAdapter.MyViewHolder>() {

    inner class MyViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){
        var customerName = itemView.findViewById<TextView>(R.id.customername)
        var customerlastmessage = itemView.findViewById<TextView>(R.id.cuslastmessage)
        val customerDP = itemView.findViewById<CircleImageView>(R.id.workerscustomerdp)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.customer_items, parent, false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.customerName.text = customer[position].customers_name
        holder.customerlastmessage.text = customer[position].customer_message.toString()

        val image:CircleImageView? = null
        image?.setImageResource(R.drawable.ic_account)

        // get image url from local database
        val imageUrl: String = customer[position].dp_url.toString()

        // Set DP using Glide
        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.customerDP)

    }

    override fun getItemCount(): Int {
        return customer.size
    }

}