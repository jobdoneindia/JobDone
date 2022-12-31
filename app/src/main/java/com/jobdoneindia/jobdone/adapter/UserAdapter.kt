package com.jobdoneindia.jobdone.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.ChatActivity
import com.jobdoneindia.jobdone.activity.inboxItem
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(val context: Context, val userList: ArrayList<inboxItem>, var ischat: Boolean ):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        this.ischat = ischat
        return UserViewHolder(view)
    }




    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        val image: CircleImageView? = null
        val img_on: CircleImageView? = null
        val img_off: CircleImageView?= null
        image?.setImageResource(R.drawable.ic_account)
        img_on?.setImageResource(R.drawable.ic_account)
        img_off?.setImageResource(R.drawable.ic_account)
        holder.textName.text = currentUser.username.toString()
        holder.imageOn.findViewById<CircleImageView>(R.id.img_on)
        holder.imageOff.findViewById<CircleImageView>(R.id.img_off)

        /*FirebaseDatabase.getInstance().getReference().child("chats")
            .child(FirebaseAuth.getInstance().uid + currentUser.uid).child("messages")
            .orderByChild("timestamp")
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {


          // TO SET LAST MESSAGE


                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()){
                        for (postSnapshot in snapshot.children)
                        {


                            holder.lastMessage.setText(postSnapshot.child("message").getValue().toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })*/



        //To SHOW VISIBILITY OF ONLINE OR OFFLINE

        if (ischat){
            if (currentUser.status == "online"){
                holder.imageOn.visibility = VISIBLE
                holder.imageOff.visibility = GONE

            }else{
                holder.imageOn.visibility = GONE
                holder.imageOff.visibility = VISIBLE
            }
        }else{
            holder.imageOn.visibility = GONE
            holder.imageOff.visibility = GONE
        }



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

            intent.putExtra("username",currentUser.username)
            intent.putExtra("uid",currentUser.uid)
            intent.putExtra("url",userList[position].url)

            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {

        return userList.size

    }

    class  UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val userDP = itemView.findViewById<CircleImageView>(R.id.user_dp)
        val imageOn = itemView.findViewById<CircleImageView>(R.id.img_on)
        val imageOff = itemView.findViewById<CircleImageView>(R.id.img_off)
        val lastMessage = itemView.findViewById<TextView>(R.id.last_msg)



    }
}

