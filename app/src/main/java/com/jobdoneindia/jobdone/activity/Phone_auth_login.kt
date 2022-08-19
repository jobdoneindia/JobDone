package com.jobdoneindia.jobdone.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
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


        //phone Continue button listener
        binding.otpSendBtn.setOnClickListener(){

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
        binding.resendOtpTV.setOnClickListener(){

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
        binding.otpContinueBtn.setOnClickListener(){

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

                reference.child("Phone Number").setValue(phone)

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