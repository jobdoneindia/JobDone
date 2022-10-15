package com.jobdoneindia.jobdone.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.jobdoneindia.jobdone.R
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    lateinit var pay: Button
    lateinit var enterAmount:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        pay = findViewById(R.id.payBtn)
        enterAmount = findViewById(R.id.enterAmountBtn)


        pay.setOnClickListener{
            makePayment()
        }
    }

    private fun makePayment() {


        val co = Checkout()
        co.setKeyID("rzp_live_x8enD8hPgKcmQq")

        val amount = enterAmount.text.toString() + "00"

        if(amount == "00"){

            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
        }else {

            try {
                val options = JSONObject()
                options.put("name", "JobDone")
                options.put("description", "Paying...")
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
                options.put("theme.color", "#9ecc33")
                options.put("currency", "INR")
//              options.put("order_id", "order_DBJOWzybf0sJbb");
                options.put("amount", amount)//pass amount in currency subunits

                val retryObj = JSONObject()
                retryObj.put("enabled", true)
                retryObj.put("max_count", 4)
                options.put("retry", retryObj)

                val prefill = JSONObject()
                prefill.put("contact", FirebaseAuth.getInstance().currentUser?.phoneNumber)

                options.put("prefill", prefill)
                co.open(this, options)
            } catch (e: Exception) {
                Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Successful",Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed, Try again",Toast.LENGTH_SHORT).show()
    }
}