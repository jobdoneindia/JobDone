package com.jobdoneindia.jobdone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.fragment.SearchItem

class SearchResultsAdapter(val searchItems: MutableList<SearchItem>): RecyclerView.Adapter<SearchResultsAdapter.MyViewHolder>(){

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
        holder.name.text = searchItems[position].name
        holder.bio.text = searchItems[position].bio
        holder.overall_rating.text = searchItems[position].overall_rating
        holder.level.text = searchItems[position].distance
        holder.description.text = searchItems[position].description
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

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
            expandButton.setOnClickListener {
                if (expandableView.visibility == View.VISIBLE) {
                    expandableView.visibility = View.GONE
                    expandButton.rotation = -90F
                } else {
                    expandableView.visibility = View.VISIBLE
                    expandButton.rotation = 0F
                }
            }
        }
    }
}