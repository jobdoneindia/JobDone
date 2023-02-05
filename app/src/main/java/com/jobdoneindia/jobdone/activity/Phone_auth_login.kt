package com.jobdoneindia.jobdone.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
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

    //fetch number
    private lateinit var googleApiClient: GoogleApiClient


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
                /*Toast.makeText(this@Phone_auth_login,"Verification code sent", Toast.LENGTH_SHORT).show()*/


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
            dialog.setTitle("Terms & Conditions of JobDoneIndia")
                .setMessage("\n" +
                        "Welcome to JobDone India!" +
                        "These terms and conditions outline the rules and regulations for the use of JobDoneIndia's Application\n" +
                        "By accessing this application we assume you accept these terms and conditions. Do not continue to use JobDoneIndia if you do not agree to take all of the terms and conditions stated on this page.\n" +
                        "The following terminology applies to these Terms and Conditions, Privacy Statement and Disclaimer Notice and all Agreements: \"Client\", \"You\" and \"Your\" refers to you, the person log on this application and compliant to the Company’s terms and conditions. \"The Company\", \"Ourselves\", \"We\", \"Our\" and \"Us\", refers to our Company. \"Party\", \"Parties\", or \"Us\", refers to both the Client and ourselves. All terms refer to the offer, acceptance and consideration of payment necessary to undertake the process of our assistance to the Client in the most appropriate manner for the express purpose of meeting the Client’s needs in respect of provision of the Company’s stated services, in accordance with and subject to, prevailing law of India. Any use of the above terminology or other words in the singular, plural, capitalization and/or he/she or they, are taken as interchangeable and therefore as referring to same.\n" +
                        "Limitation of Liability\n" +
                        "JobDone is not responsible for any losses incurred by the use of the app, including but not limited to damage or loss of property or injury caused by fake workers registered in the application.\n" +
                        "Unless otherwise stated, JobDoneIndia and/or its licensors own the intellectual property rights for all material on JobDoneIndia. All intellectual property rights are reserved. You may access this from JobDoneIndia for your own personal use subjected to restrictions set in these terms and conditions.\n" +
                        "You must not:\n" +
                        "Republish material from JobDone India\n" +
                        "Sell, rent or sub-license material from JobDone India\n" +
                        "Reproduce, duplicate or copy material from JobDoneIndia\n" +
                        "Redistribute content from JobDoneIndia" +
                        "Parts of this application offer an opportunity for users to post and exchange data and information in certain areas of the application. JobDoneIndia does not filter, edit, publish or review information prior to their presence on the application. Information do not reflect the views and opinions of JobDoneIndia,its agents and/or affiliates. Information reflect the characteristic of the person who post their views and opinions. To the extent permitted by applicable laws, JobDoneIndia shall not be liable for the information or for any liability, damages or expenses caused and/or suffered as a result of any use of and/or posting of and/or appearance of the information on this application.\n" +
                        "JobDoneIndia reserves the right to monitor all data and to remove any data which can be considered inappropriate, offensive or causes breach of these Terms and Conditions.\n" +
                        "You warrant and represent that:\n" +
                        "You are entitled share information among users and have all necessary licenses and consents to do so;\n" +
                        "The information do not invade any intellectual property right, including without limitation copyright, patent or trademark of any third party;\n" +
                        "The information do not contain any defamatory, libelous, offensive, indecent or otherwise unlawful material which is an invasion of privacy\n" +
                        "The information will not be used to solicit or promote business or custom or present commercial activities or unlawful activity.\n" +
                        "You hereby grant JobDoneIndia a non-exclusive license to use, reproduce, edit and authorize others to use, reproduce and edit any of your information in any and all forms, formats or media.\n" +
                        "User Verification\n" +
                        "JobDoneIndia does not verify the identity or qualifications of workers registered in the application and does not guarantee their work. It is the user's responsibility to verify the identity and qualifications of the worker before engaging their services.\n" +
                        "User Agreement\n" +
                        "By using the JobDoneIndia app, the user agrees to indemnify and hold JobDone harmless from any claims, damages, or expenses arising from the use of the app or from the services provided by the workers registered in the application.\n" +
                        "User Conduct\n" +
                        "Users are prohibited from engaging in any illegal or unethical activities while using the JobDoneIndia app, including but not limited to, posting false or misleading information, or engaging in fraud or exploitation of workers or other users.\n" +
                        "Dispute Resolution\n" +
                        "Any disputes arising from the use of the JobDoneIndia app or from the services provided by the workers registered in the application will be resolved through binding arbitration in accordance with the laws of the jurisdiction where the dispute arose." +
                        "Governing Law\n" +
                        "This agreement shall be governed by and construed in accordance with the laws of the jurisdiction where the dispute arose.\n" +
                        "Modification of Terms\n" +
                        "JobDone reserves the right to modify these terms and conditions at any time without prior notice.\n" +
                        "Disclaimer\n" +
                        "To the maximum extent permitted by applicable law, we exclude all representations, warranties and conditions relating to our JobDoneIndia and the use of this application. Nothing in this disclaimer will:\n" +
                        "\n" +
                        "limit or exclude our or your liability for death or personal injury;\n" +
                        "limit or exclude our or your liability for fraud or fraudulent misrepresentation;\n" +
                        "limit any of our or your liabilities in any way that is not permitted under applicable law; \n" +
                        "exclude any of our or your liabilities that may not be excluded under applicable law.\n" +
                        "The limitations and prohibitions of liability set in this Section and elsewhere in this disclaimer: (a) are subject to the preceding paragraph; and (b) govern all liabilities arising under the disclaimer, including liabilities arising in contract, in tort and for breach of statutory duty.\n" +
                        "\n" +
                        "As long as the applications and the information and services on the application are provided free of charge, we will not be liable for any loss or damage of any nature."
                )
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
            val phone = "+91" + binding.phonEt.text.toString().trim()

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

        /*//Fetch Phone Number To Login

        tryGetCurrentUserPhoneNumber(this)
        googleApiClient = GoogleApiClient.Builder(this).addApi(Auth.CREDENTIALS_API).build()
        if (phoneNumber.isEmpty()) {
            val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
            val intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest)
            try {
                startIntentSenderForResult(intent.intentSender, REQUEST_PHONE_NUMBER, null, 0, 0, 0);
            } catch (e: IntentSender.SendIntentException) {
                Toast.makeText(this, "failed to show phone picker", Toast.LENGTH_SHORT).show()
            }
        } else
            onGotPhoneNumberToSendTo()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PHONE_NUMBER) {
            if (resultCode == Activity.RESULT_OK) {
                val cred: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                phoneNumber = cred?.id ?: ""

                if (phoneNumber.isEmpty())

                    Toast.makeText(this, "failed to get phone number", Toast.LENGTH_SHORT).show()
                else

                    onGotPhoneNumberToSendTo()
            }
        }
    }

    private fun onGotPhoneNumberToSendTo() {
        Toast.makeText(this, "got number:$phoneNumber", Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val REQUEST_PHONE_NUMBER = 1
        private var phoneNumber = ""

        @SuppressLint("MissingPermission", "HardwareIds")
        private fun tryGetCurrentUserPhoneNumber(context: Context): String {
            if (phoneNumber.isNotEmpty())
                return phoneNumber

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                try {
                    subscriptionManager.activeSubscriptionInfoList?.forEach {
                        val number: String? = it.number
                        if (!number.isNullOrBlank()) {
                            phoneNumber = number
                            return number
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
            try {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val number = telephonyManager.line1Number ?: ""
                if (!number.isBlank()) {
                    phoneNumber = number
                    return number
                }
            } catch (e: Exception) {
            }
            return ""
        }*/
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
                /*Toast.makeText(this,"Logged In as $phone", Toast.LENGTH_SHORT).show()*/



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
    // APP REMEMBERS THE USER WHO LOGIN-ED PREVIOUSLY
   /* override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser

        if (user != null){
            val intent = Intent(this@Phone_auth_login,DashboardActivity::class.java)
            *//* intent.putExtra("phoneNumber",phone)*//*
            startActivity(intent)
            finish()
        }
    }*/
}
