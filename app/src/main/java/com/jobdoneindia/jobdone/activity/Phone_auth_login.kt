package com.jobdoneindia.jobdone.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions

import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jobdoneindia.jobdone.databinding.ActivityPhoneAuthLoginBinding
import java.util.concurrent.TimeUnit

class Phone_auth_login : AppCompatActivity() {

    lateinit var editTextPhone: EditText
    lateinit var otpContinueBtn : Button

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = database.reference.child("Users").child(uid.toString())

    //binding
    private lateinit var binding: ActivityPhoneAuthLoginBinding

    //if code sending failed
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String?=null
    private lateinit var firebaseAuth: FirebaseAuth

    private val TAG = "MAIN_TAG"

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.phoneLl.visibility= View.VISIBLE
        binding.otpLl.visibility=View.GONE
        firebaseAuth = FirebaseAuth.getInstance()


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                //This callback will be invoked in two situation
                //1.Instant-Verification
                //2.Auto-Retrieval
                signInWithPhoneCredential(phoneAuthCredential)

            }



            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                //SMS verification code is sent to the input phone number
                Log.d(TAG,"oncodesent:$verificationId")
                mVerificationId=verificationId
                forceResendingToken=token
                progressDialog.dismiss()

                //hide phone layout and show code layout
                binding.phoneLl.visibility = View.GONE
                binding.otpLl.visibility = View.VISIBLE
                Toast.makeText(this@Phone_auth_login,"Verification code sent", Toast.LENGTH_SHORT).show()


            }

            override fun onVerificationFailed(e: FirebaseException) {
                //This callback is invoked when invalid code is entered
                progressDialog.dismiss()
                Toast.makeText(this@Phone_auth_login,"Error try again!", Toast.LENGTH_SHORT).show()
            }

        }

        //checkbox verification
        binding.checkBox.setOnCheckedChangeListener { button, b ->
            binding.otpSendBtn.isEnabled = binding.checkBox.isChecked==true
        }


        //Terms&Conditions DialogBox Button

        binding.tcinfo.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Terms & Conditions")
                .setMessage("A Terms and Conditions agreement acts as a legal contract between you (the company) and the user. It's where you maintain your rights to exclude users from your app in the event that they abuse your website/app, set out the rules for using your service and note other important details and disclaimers.\n" +
                        "\n" +
                        "Having a Terms and Conditions agreement is completely optional. No laws require you to have one. Not even the super-strict and wide-reaching General Data Protection Regulation (GDPR).\n" +
                        "\n" +
                        "Your Terms and Conditions agreement will be uniquely yours. While some clauses are standard and commonly seen in pretty much every Terms and Conditions agreement, it's up to you to set the rules and guidelines that the user must agree to.\n" +
                        "\n" +
                        "Terms and Conditions agreements are also known as Terms of Service or Terms of Use agreements. These terms are interchangeable, practically speaking." +
                        "A Terms and Conditions is not required and it's not mandatory by law.\n" +
                        "\n" +
                        "Unlike Privacy Policies, which are required by laws such as the GDPR, CalOPPA and many others, there's no law or regulation on Terms and Conditions.\n" +
                        "\n" +
                        "However, having a Terms and Conditions gives you the right to terminate the access of abusive users or to terminate the access to users who do not follow your rules and guidelines, as well as other desirable business benefits.\n" +
                        "\n" +
                        "It's extremely important to have this agreement if you operate a SaaS app.\n" +
                        "\n" +
                        "Here are a few examples of how this agreement can help you:\n" +
                        "\n" +
                        "If users abuse your website or mobile app in any way, you can terminate their account. Your \"Termination\" clause can inform users that their accounts would be terminated if they abuse your service.\n" +
                        "If users can post content on your website or mobile app (create content and share it on your platform), you can remove any content they created if it infringes copyright. Your Terms and Conditions will inform users that they can only create and/or share content they own rights to. Similarly, if users can register for an account and choose a username, you can inform users that they are not allowed to choose usernames that may infringe trademarks, i.e. usernames like Google, Facebook, and so on.\n" +
                        "If you sell products or services, you could cancel specific orders if a product price is incorrect. Your Terms and Conditions can include a clause to inform users that certain orders, at your sole discretion, can be canceled if the products ordered have incorrect prices due to various errors.\n" +
                        "And many more examples.\n" +
                        "In summary, while you do not legally need a Terms and Conditions agreement, there are many many reasons for you to have one. Not only will it make your business look more professional and trustworthy, but you'll also be maintaining more control over how your users are able to interact with your platforms and content." +
                        "If you sell products (physical or digital), you'll want Terms and Conditions for your store. Having this agreement in place will help you:\n" +
                        "\n" +
                        "Dictate how different aspects of transactions will be handled\n" +
                        "Inform users about acceptable payment terms\n" +
                        "Inform users about your shipping policies\n" +
                        "Inform users about your returns and refunds policies. You can also do this through a separate agreement, called a Return and Refund Policy, that you can reference in the Terms & Conditions agreement.\n" +
                        "Let's look at an example: the Limitation of Liability of Your Products clause.\n" +
                        "\n" +
                        "No matter what kind of goods you sell, best practices direct you to present any warranties you are disclaiming and liabilities you are limiting in a way that your customers will notice.\n" +
                        "\n" +
                        "You've probably noticed that these clauses in contracts are always in blocks of all-caps text and really do stand out from the rest of the document.\n" +
                        "\n" +
                        "Apple Logo Icon\n" +
                        "\n" +
                        "Apple iTunes, which probably isn't dealing with high-liability goods, includes the following boilerplate language in its Terms agreement to deal with limiting liability and disclaiming warranties.\n" +
                        "\n" +
                        "This exact language is used across multiple industries, businesses, and apps in order to legally disclaim warranties and limit liability.\n" +
                        "\n" +
                        "Include the words \"AS IS\" for items and \"AS AVAILABLE\" if you provide any sort of service that may not be available 100% of the time.\n" +
                        "\n" +
                        "Apple Media Services Terms and Conditions: No Warranty clause\n" +
                        "\n" +
                        "Here's a list of questions that can help you determine what to add in your own Terms and Conditions agreement:\n" +
                        "\n" +
                        "Can users create an account on your website or app?\n" +
                        "Can users create or publish content on your website or app?\n" +
                        "Is the content published by users available publicly?\n" +
                        "Can users send you copyright infringement notices?\n" +
                        "Is your website or app an ecommerce store?")
            val alert = dialog.create()
            alert.show()
        }



        //phone Continue button listener
        binding.otpSendBtn.setOnClickListener {

            //input phone number
            val phone = "+91" + binding.phonEt.text.toString().trim()

            //validate phone number
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@Phone_auth_login,"Please enter phone number", Toast.LENGTH_SHORT).show()
            }else{
                startPhoneNumberVerification(phone)
            }

        }

        //resend code listener
        binding.resendOtpTV.setOnClickListener {

            //input phone number
            val phone = binding.phonEt.text.toString().trim()

            //validate phone number
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this@Phone_auth_login,"Please enter phone number", Toast.LENGTH_SHORT).show()
            }else{
                forceResendingToken?.let { it1 -> resendVerificationCode(phone, it1) }
            }
        }

        //code submit button002sa2\
        binding.otpContinueBtn.setOnClickListener {

            //input verification code
            val code = binding.otpEt.text.toString().trim()
            if(TextUtils.isEmpty(code)){
                Toast.makeText(this@Phone_auth_login,"Please enter verification code", Toast.LENGTH_SHORT).show()
            }else{
                verifyPhoneNumberWithCode(mVerificationId,code)
            }
        }



    }

    private fun startPhoneNumberVerification(phone: String){
        progressDialog.setMessage("Verifying Phone Number....")
        progressDialog.show()

        val options = mCallBacks?.let {
            mCallBacks?.let { it1 ->
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L,TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(it1)
                    .build()
            }

        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun resendVerificationCode(phone:String,token:PhoneAuthProvider.ForceResendingToken){

        progressDialog.setMessage("Resending Code....")
        progressDialog.show()

        val options = mCallBacks.let {
            mCallBacks?.let { it1 ->
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L,TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(it1)
                    .setForceResendingToken(token)
                    .build()
            }

        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code:String){
        progressDialog.setMessage("Verifying Code....")
        progressDialog.show()

        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it,code) }
        if (credential != null) {
            signInWithPhoneCredential(credential)
        }

    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging In")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(this,"Logged In as $phone", Toast.LENGTH_SHORT).show()



                //start profile activity
                val intent = Intent(this,RegistrationActivity::class.java)
                /*intent.putExtra("phoneNumber",phone)*/
                startActivity(intent)

            }
            .addOnFailureListener {e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this,"Error try again", Toast.LENGTH_SHORT).show()
            }

    }


}