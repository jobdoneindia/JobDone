package com.jobdoneindia.jobdone.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.R
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import kotlin.random.Random

class FirstPaymentActivity : AppCompatActivity(), PaymentResultListener {

    val activity: Activity = this

    lateinit var paybtn : Button
    lateinit var payTxt : TextView
    lateinit var sharedPreferences: SharedPreferences
    val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_payment)

//        sharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
//        val name = sharedPreferences.getString("name_key", "Jobdone").toString()
        val payTxt = findViewById<TextView>(R.id.payText)


        paybtn = findViewById(R.id.payBtn)

        paybtn.setOnClickListener{
            makePayment("jobdone")
        }


    }

    private fun makePayment(name: String){

        val co = Checkout()
        co.setKeyID("rzp_live_g5dTsAhwOP0aSG")
        val orderId = generateOrderId()

        try {
            val options = JSONObject()
            options.put("name","JobDone")
            options.put("description","Paying...")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#9ecc33");
            options.put("currency","INR")
//            options.put("order_id", orderId)
            options.put("amount","9900")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("contact", FirebaseAuth.getInstance().currentUser?.phoneNumber )

            options.put("prefill",prefill)
            co.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun generateOrderId(): String {
        val timestamp = System.currentTimeMillis()
        val random = Random.nextInt(999)
        return "order_$timestamp$random"
    }

    override fun onPaymentSuccess(paymentID: String) {
        Toast.makeText(activity, "Payment Successful!", Toast.LENGTH_SHORT).show()
        paybtn.visibility = View.GONE
        payTxt.text = "Order ID: $paymentID \n Amount: 99 Rs."

        // save the payment details to firebase
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val reference : DatabaseReference = database.reference.child("Users").child(uid.toString()).child("Payment Details")

        reference.child("Initial").setValue("99 Rs. Order Id: $paymentID")


        val intent = Intent(this, WorkerDashboardActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(activity, "Payment Failure \n Retry Again!", Toast.LENGTH_SHORT).show()
    }

}