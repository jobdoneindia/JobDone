package com.jobdoneindia.jobdone.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.ChatActivity
import com.jobdoneindia.jobdone.activity.inboxItem
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(val context: Context, val userList: ArrayList<inboxItem>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }




    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        val image: CircleImageView? = null
        image?.setImageResource(R.drawable.ic_account)

        holder.textName.text = currentUser.username.toString()

        holder.lastMessage.text = currentUser.lastMsg

        // get image url from local database
        val imageUrl:  String? = currentUser.url.toString()

        // Set DP using Glide
        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.userDP)

        // onClick Listener for DP
        holder.userDP.setOnClickListener {
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(context)

            // set message of alert dialog
            dialogBuilder.setMessage("Name: ${currentUser.username.toString()} \nLast msg: ${currentUser.lastMsg.toString()}").setCancelable(true)
                // positive button text and action
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Details")
            // show
            alert.show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)

            intent.putExtra("name",currentUser.username)
            intent.putExtra("uid",currentUser.uid)

            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {

        return userList.size

    }

    class  UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val userDP = itemView.findViewById<CircleImageView>(R.id.user_dp)
        val lastMessage = itemView.findViewById<TextView>(R.id.last_msg)
    }
}

