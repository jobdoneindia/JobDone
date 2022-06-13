package com.jobdoneindia.jobdone

import android.app.ProgressDialog
import android.content.Intent
import android.media.tv.TvContract
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.jobdoneindia.jobdone.databinding.ActivityDashboardBinding
import com.jobdoneindia.jobdone.databinding.ActivityPhoneAuthLoginBinding
import java.util.concurrent.TimeUnit

class Phone_auth_login : AppCompatActivity() {

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

            override fun onVerificationFailed(e: FirebaseException) {
                //This callback is invoked when invalid code is entered
                progressDialog.dismiss()
                Toast.makeText(this@Phone_auth_login,"{e.message}", Toast.LENGTH_SHORT).show()
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
                binding.codeDescriptionTv.text = "Please type the verification code sent to ${binding.phonEt.text.toString().trim()}"

            }

        }

        //phone Continue button listener
        binding.phoneContinueBtn.setOnClickListener(){

            //input phone number
            val phone = binding.phonEt.text.toString().trim()

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

        //code submit button
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
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(it)
                .build()
        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun resendVerificationCode(phone:String,token:PhoneAuthProvider.ForceResendingToken){

        progressDialog.setMessage("Resending Code....")
        progressDialog.show()

        val options = mCallBacks?.let {
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(it)
                .setForceResendingToken(token)
                .build()
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
                val intent = Intent(this,DashboardActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this,"{e.message}", Toast.LENGTH_SHORT).show()
            }

    }


}