package com.jobdoneindia.jobdone.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        sharedPreferences = getSharedPreferences("usersharedpreference", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name_key", "Jobdone").toString()
        val payTxt = findViewById<TextView>(R.id.payText)

        paybtn = findViewById(R.id.payBtn)

        paybtn.setOnClickListener{
            makePayment(name)
        }


    }

    private fun makePayment(name: String){

        val co = Checkout()
        co.setKeyID("rzp_test_ZD2zdSYkMXWqur")
        val orderId = generateOrderId()

        try {
            val options = JSONObject()
            options.put("name","JobDone India")
            options.put("description","Paying Registration Fee")
            options.put("theme.color", "#3399cc")
            options.put("currency","INR")
            options.put("order_id", orderId)
            options.put("amount","9900")

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 3)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("contact",phoneNumber)

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
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
        finish()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(activity, "Payment Failure \n Retry Again!", Toast.LENGTH_SHORT).show()
        finish()
    }


}