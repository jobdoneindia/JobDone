package com.jobdoneindia.jobdone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.ChatActivity
import com.jobdoneindia.jobdone.fragment.SearchItem
import de.hdodenhof.circleimageview.CircleImageView

class SearchResultsAdapter(val context: Context, val searchItems: MutableList<SearchItem>): RecyclerView.Adapter<SearchResultsAdapter.MyViewHolder>(){

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_item_view, parent, false)
        return MyViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: SearchResultsAdapter.MyViewHolder, position: Int) {
        val currentUser = searchItems[position]

        holder.name.text = searchItems[position].name
        holder.bio.text = searchItems[position].profession
        holder.overall_rating.text = searchItems[position].overall_rating
        holder.level.text = searchItems[position].distance
        holder.description.text = searchItems[position].profession

        // Set DP using Glide
        Glide.with(context)
            .load(currentUser.url)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.userDP)

        holder.itemView.setOnClickListener {
            if (holder.expandableView.visibility == View.VISIBLE) {
                holder.expandableView.visibility = View.GONE
                holder.expandButton.rotation = -90F
            } else {
                holder.expandableView.visibility = View.VISIBLE
                holder.expandButton.rotation = 0F
            }
        }

        holder.expandButton.setOnClickListener {
            if (holder.expandableView.visibility == View.VISIBLE) {
                holder.expandableView.visibility = View.GONE
                holder.expandButton.rotation = -90F
            } else {
                holder.expandableView.visibility = View.VISIBLE
                holder.expandButton.rotation = 0F
            }
        }

        holder.btnMsg.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("name",currentUser.name)
            intent.putExtra("uid",currentUser.uid)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return searchItems.size
    }



    inner class MyViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        var name = itemView.findViewById<TextView>(R.id.worker_name)
        var bio = itemView.findViewById<TextView>(R.id.bio)
        var overall_rating = itemView.findViewById<TextView>(R.id.rating)
        var level = itemView.findViewById<TextView>(R.id.distance_from_user)
        var description = itemView.findViewById<TextView>(R.id.desc)
        var expandButton = itemView.findViewById<ImageButton>(R.id.btnExpand)
        var expandableView = itemView.findViewById<ConstraintLayout>(R.id.expandableView)

        var btnMsg = itemView.findViewById<ImageButton>(R.id.btnMessage)
        var userDP = itemView.findViewById<CircleImageView>(R.id.circleImage)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

            // TODO: Onclick listener for message-button will Add worker to inbox and redirect to ChatActivity

            // TODO: Call-button will make a call
        }
    }

}