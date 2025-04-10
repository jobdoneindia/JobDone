package com.jobdoneindia.jobdone.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
        holder.tag1.text = searchItems[position].tag1
        holder.tag2.text = searchItems[position].tag2
        holder.tag3.text = searchItems[position].tag3
        holder.overall_rating.text = searchItems[position].overall_rating
        holder.level.text = searchItems[position].distance
        holder.description.text = searchItems[position].profession

        holder.tag1.visibility = if (holder.tag1.text == "Empty" || holder.tag1.text == "null") View.GONE else View.VISIBLE
        holder.tag2.visibility = if (holder.tag2.text == "Empty" || holder.tag2.text == "null") View.GONE else View.VISIBLE
        holder.tag3.visibility = if (holder.tag3.text == "Empty" || holder.tag3.text == "null") View.GONE else View.VISIBLE

        // Set DP using Glide
        Glide.with(context)
            .load(currentUser.url)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.userDP)


        holder.btnMsg.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)

            // store data locally
            val sharedPreferences: SharedPreferences =context.getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            var userList = sharedPreferences.getString("chat_user_list", "null")

            if (userList == "null") {
                editor.putString("chat_user_list", currentUser.uid)
                editor.apply()
                editor.commit()
            } else if (!userList!!.contains(currentUser.uid)) {
                userList += ":" + currentUser.uid
                editor.putString("chat_user_list", userList)
                editor.apply()
                editor.commit()
            }

            val database : FirebaseDatabase = FirebaseDatabase.getInstance()
            val mAuth = FirebaseAuth.getInstance()
            val reference : DatabaseReference = database.reference.child("Users").child(currentUser.uid)
            var chatListWorker = ""

            reference.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatListWorker = snapshot.child("chat_user_list").value.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            if (!chatListWorker.contains(mAuth.currentUser?.uid.toString())) {
                chatListWorker += ":" + mAuth.currentUser?.uid
                reference.child("chat_user_list").setValue(chatListWorker)
            }


            intent.putExtra("username",currentUser.name)
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

        var divTags = itemView.findViewById<HorizontalScrollView>(R.id.divTags)
        var tag1 = itemView.findViewById<Chip>(R.id.tag1)
        var tag2 = itemView.findViewById<Chip>(R.id.tag2)
        var tag3 = itemView.findViewById<Chip>(R.id.tag3)

        var btnMsg = itemView.findViewById<ImageButton>(R.id.btnMessage)
        var userDP = itemView.findViewById<CircleImageView>(R.id.circleImage)

        var btnCall = itemView.findViewById<ImageButton>(R.id.btnCall)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

            // TODO: Call-button will make a call

        }
    }

}