package com.jobdoneindia.jobdone.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.Message
import de.hdodenhof.circleimageview.CircleImageView
import com.jobdoneindia.jobdone.activity.PaymentActivity

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    val ITEM_SENT_LOCATION = 3
    val ITEM_RECEIVE_LOCATION = 4
    val ITEM_SENT_PAYMENT = 5
    val ITEM_RECEIVE_PAYMENT = 6
    val ITEM_RECEIVE_REVIEW = 7
    val ITEM_SENT_REVIEW = 8


    val mDbRef : DatabaseReference = FirebaseDatabase.getInstance().reference
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 1){
            // inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        }

        else if  (viewType == 2) {
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
        else if (viewType == 4) {
            // inflate receive location
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_location, parent, false)
            return ReceiveLocationViewHolder(view)
        }
        else if(viewType == 3){
            //inflate sent location
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent_location, parent, false)
            return SentLocationViewHolder(view)
        }
        else if(viewType == 5){
            //inflate sent payment
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent_payment, parent, false)
            return SentPaymentViewHolder(view)

        }
        else if(viewType == 6){
            //inflate receive payment
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_payment, parent, false)
            return ReceivePaymentViewHolder(view)

        }
        else if(viewType == 7){
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_review, parent, false)
            return ReceiveReviewViewHolder(view)
        }
        else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent_review, parent, false)
            return SentReviewViewHolder(view)
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
        } else if (holder.javaClass==SentViewHolder::class.java){
        }

        else if (holder.javaClass == SentLocationViewHolder::class.java){
            val viewHolder = holder as SentLocationViewHolder

            if (currentMessage.status == "decline") {
                holder.btnViewMap.visibility = View.GONE
                holder.btnRequested.visibility = View.GONE
                holder.btnDeclined.visibility = View.VISIBLE
            } else if (currentMessage.status == "accept") {
                holder.btnViewMap.visibility = View.VISIBLE
                holder.btnRequested.visibility = View.GONE
                holder.btnDeclined.visibility = View.GONE
                holder.txtLocation.text = "Location Request"
            }

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

        else if (holder.javaClass == ReceiveLocationViewHolder::class.java) {
            val viewHolder = holder as ReceiveLocationViewHolder

            // check message status
            if (currentMessage.status == "accept") {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnDeclined.visibility = View.GONE
                holder.btnViewMap.visibility = View.VISIBLE
            }

            else if (currentMessage.status == "decline") {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnViewMap.visibility = View.GONE
                holder.btnDeclined.visibility = View.VISIBLE
            }

            // on click for Decline Button
            viewHolder.btnDecline.setOnClickListener {
                val receiverRoom = currentMessage.senderId + currentMessage.receiverId
                val senderRoom = currentMessage.receiverId + currentMessage.senderId
                mDbRef.child("chats").child(senderRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                    .setValue("decline").addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                            .setValue("decline")
                    }}

            viewHolder.btnAccept.setOnClickListener {
                val receiverRoom = currentMessage.senderId + currentMessage.receiverId
                val senderRoom = currentMessage.receiverId + currentMessage.senderId
                mDbRef.child("chats").child(senderRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                    .setValue("accept").addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                            .setValue("accept")
                    }
            }

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


        /*if (holder.javaClass == SentViewHolder::class.java){
            val ViewHolder = holder as SentViewHolder

            if (position==messageList.size-1){
                if (currentMessage.isseen == true){
                    holder.is_seen.text = "Seen"
                }else if (currentMessage.isseen==false){
                    holder.is_seen.text = "Delivered"
                }
                holder.is_seen.visibility = View.VISIBLE
            }else{
                holder.is_seen.visibility = View.GONE
            }
        }*/

        else if (holder.javaClass == SentPaymentViewHolder::class.java){
            val viewHolder = holder as SentPaymentViewHolder

            if (currentMessage.status == "decline") {
                holder.btnRequested.visibility = View.GONE
                holder.btnDeclined.visibility = View.VISIBLE
            } else if (currentMessage.status == "accept") {
                holder.btnRequested.visibility = View.GONE
                holder.btnPaid.visibility = View.VISIBLE
                holder.txtPayment.text = "Payment Requested"
            }

        }

        else if(holder.javaClass == ReceivePaymentViewHolder::class.java){

            val viewHolder = holder as ReceivePaymentViewHolder

            // check message status
            if (currentMessage.status == "accept") {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnDeclined.visibility = View.GONE
                holder.btnAccepted.visibility = View.VISIBLE
            }

            else if (currentMessage.status == "decline") {
                holder.btnAccept.visibility = View.GONE
                holder.btnDecline.visibility = View.GONE
                holder.btnDeclined.visibility = View.VISIBLE
            }

            // on click for Decline Button
            viewHolder.btnDecline.setOnClickListener {
                val receiverRoom = currentMessage.senderId + currentMessage.receiverId
                val senderRoom = currentMessage.receiverId + currentMessage.senderId
                mDbRef.child("chats").child(senderRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                    .setValue("decline").addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                            .setValue("decline")
                    }}

            viewHolder.btnAccept.setOnClickListener {
                val receiverRoom = currentMessage.senderId + currentMessage.receiverId
                val senderRoom = currentMessage.receiverId + currentMessage.senderId
                mDbRef.child("chats").child(senderRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                    .setValue("accept").addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(currentMessage.ukey.toString()).child("status")/*.push()*/
                            .setValue("accept")
                    }
                context.startActivity(Intent(context, PaymentActivity::class.java))
            }

        }

        else if(holder.javaClass == SentReviewViewHolder::class.java){

        }

        else if(holder.javaClass == ReceiveViewHolder::class.java){

        }

    }


    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            if (currentMessage.message_type == "location"){
                return ITEM_SENT_LOCATION
            } else if(currentMessage.message_type == "payment") {
                return ITEM_SENT_PAYMENT
            } else if(currentMessage.message_type == "review"){
                return ITEM_SENT_REVIEW
            } else {
                return  ITEM_SENT
            }

        }else{
            return if (currentMessage.message_type == "location"){
                ITEM_RECEIVE_LOCATION
            } else if(currentMessage.message_type == "payment"){
                ITEM_RECEIVE_PAYMENT
            } else if(currentMessage.message_type == "review"){
                ITEM_RECEIVE_REVIEW
            } else {
                ITEM_RECEIVE
            }
        }

    }

    override fun getItemCount(): Int {
        return  messageList.size
    }

    class  SentViewHolder(itemView: View) : ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
/*
        val is_seen = itemView.findViewById<TextView>(R.id.txt_seen)
*/
    }
    class  ReceiveViewHolder(itemView: View) : ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
       /* val is_seen = itemView.findViewById<TextView>(R.id.txt_seen)*/
    }
    class SentLocationViewHolder(itemView: View) : ViewHolder(itemView) {
        val btnViewMap = itemView.findViewById<Button>(R.id.btnViewMap)
        val btnRequested = itemView.findViewById<Button>(R.id.btnRequested)
        val btnDeclined = itemView.findViewById<Button>(R.id.btnDeclined)
        val txtLocation = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }
    class ReceiveLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnAccept = itemView.findViewById<Button>(R.id.btnAccept)
        val btnDecline = itemView.findViewById<Button>(R.id.btnDecline)
        val btnDeclined = itemView.findViewById<Button>(R.id.btnDeclined)
        val btnViewMap = itemView.findViewById<Button>(R.id.btnViewMap)
    }
    class ReceivePaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val btnAccept = itemView.findViewById<Button>(R.id.btnAccept)
        val btnDecline = itemView.findViewById<Button>(R.id.btnDecline)
        val btnDeclined = itemView.findViewById<Button>(R.id.btnDeclined)
        val btnAccepted = itemView.findViewById<Button>(R.id.btnAccepted)
    }
    class SentPaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val btnRequested = itemView.findViewById<Button>(R.id.btnRequested)
        val btnDeclined = itemView.findViewById<Button>(R.id.btnDeclined)
        val txtPayment = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val btnPaid = itemView.findViewById<Button>(R.id.btnPaid)
    }

    class SentReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val btnViewReview = itemView.findViewById<Button>(R.id.btnViewReview)
        val btnReviewRequested = itemView.findViewById<Button>(R.id.btnReviewRequested)
        val btnReviewDeclined = itemView.findViewById<Button>(R.id.btnReviewDeclined)
        val btnTextReview = itemView.findViewById<Button>(R.id.txt_sent_message)
    }
    class ReceiveReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val btnRequested = itemView.findViewById<Button>(R.id.btnRequested)
        val btnDeclined = itemView.findViewById<Button>(R.id.btnDeclined)
        val txtPayment = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val btnPaid = itemView.findViewById<Button>(R.id.btnPaid)
    }



}