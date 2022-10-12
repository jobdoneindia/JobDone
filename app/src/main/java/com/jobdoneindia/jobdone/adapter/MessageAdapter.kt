package com.jobdoneindia.jobdone.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.Message

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    val ITEM_SENT_LOCATION = 3


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1){
            // inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        }

        else if  (viewType == 2) {
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        } else {
            //inflate sent location
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent_location, parent, false)
            return SentLocationViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if (holder.javaClass == SentViewHolder::class.java) {
            //do the stuff for sent view holder

            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.message


        }else if (holder.javaClass == ReceiveViewHolder::class.java){
            //do stuff for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as SentLocationViewHolder
            viewHolder.btnViewMap.setOnClickListener {
                // Create a Uri from an intent string. Use the result to create an Intent.
                val gmmIntentUri = Uri.parse("google.navigation:q=26.7174197,88.3878048&mode=w")

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps")

                // Attempt to start an activity that can handle the Intent
                context.startActivity(mapIntent)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            if (currentMessage.message_type == "location"){
                return ITEM_SENT_LOCATION
            } else {
                return  ITEM_SENT
            }

        }else {
            return ITEM_RECEIVE
        }

    }

    override fun getItemCount(): Int {
        return  messageList.size
    }
    class  SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }
    class  ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
    }
    class SentLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnViewMap = itemView.findViewById<Button>(R.id.btnViewMap)
    }
}