package com.jobdoneindia.jobdone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.chaos.view.PinView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker


class PhoneLoginActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {

    private var ccp: CountryCodePicker? = null
    private var selected_country_code: String = "+91"
    private var firstPinView: PinView? = null
    private var phoneLayout: ConstraintLayout? = null
    private var nameLayout: ConstraintLayout? = null
    private var otpLayout: ConstraintLayout? = null
    private var phoneEditText: EditText? = null
    private var skipButton: Button? = null
    private var backButtonOtp: LinearLayout? = null
    private var fabButton: FloatingActionButton? = null
    private var progressBar : ProgressBar? = null

    ////firebase auth////
    lateinit var mVerificationId : String
    lateinit var mResentToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    ////////////////////



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)


        supportActionBar?.hide()

        ccp = findViewById(R.id.ccp)
        phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        firstPinView = findViewById<View>(R.id.firstPinView) as PinView

        phoneLayout = findViewById<View>(R.id.phoneLayout) as ConstraintLayout
        nameLayout = findViewById<View>(R.id.nameLayout) as ConstraintLayout
        otpLayout = findViewById<View>(R.id.otp_layout) as ConstraintLayout

        skipButton = findViewById<View>(R.id.skip_button) as Button
        backButtonOtp = findViewById<View>(R.id.back_button_otp) as LinearLayout
        fabButton = findViewById<View>(R.id.fab_button) as FloatingActionButton?
       // ProgressBar = findViewById<View>(R.id.progressBar)
        mAuth=FirebaseAuth.getInstance()

        // country code picker
        ccp!!.setOnCountryChangeListener(this)

        // Text Watcher
        phoneEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {





            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().length == 10) {
                    Toast.makeText(this@PhoneLoginActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                    phoneLayout!!.visibility = View.GONE
                    otpLayout!!.visibility = View.VISIBLE
                    skipButton!!.visibility = View.GONE
                    sendOtp();

                    // hide keyboard
                    phoneEditText!!.onEditorAction(EditorInfo.IME_ACTION_NEXT)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // OTP Watcher
        firstPinView!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 6) {
                    Toast.makeText(this@PhoneLoginActivity, "Verified", Toast.LENGTH_SHORT).show()
                    otpLayout!!.visibility = View.GONE
                    nameLayout!!.visibility = View.VISIBLE

                    // hide keyboard
                    firstPinView!!.onEditorAction(EditorInfo.IME_ACTION_NEXT)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // Skip button
        skipButton!!.setOnClickListener{
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        //Back button in OTP screen
        backButtonOtp!!.setOnClickListener {
            phoneLayout!!.visibility = View.VISIBLE
            otpLayout!!.visibility = View.GONE
            skipButton!!.visibility = View.VISIBLE
            phoneEditText!!.text = null
        }

        // FAB
        fabButton!!.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        /////////////otp callbacks///////////////
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
               var code = PhoneAuthCredential.CREATOR
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                mVerificationId = mVerificationId
                mResentToken = token

                Toast.makeText(applicationContext, "6 digit otp sent", Toast.LENGTH_LONG).show()


                Log.d("TAG","onCodeSent:$verificationId")
              //  var intent = Intent(applicationContext,Verify::class.java)
              //  intent.putExtra("storedVerificationId",storedVerificationId)
                startActivity(intent)
            }
        }

        /////////////otp callbacks///////////////

    }

    private fun sendOtp() {
       // var phoneNumber = selected_country_code+phoneEditText.getText().toString()

      // PhoneAuthOptions options =
            PhoneAuthOptions.newBuilder()
        }

    override fun onCountrySelected() {
        TODO("Not yet implemented")
    }
}


    //override fun onCountrySelected() {
       // selected_country_code = ccp!!.selectedCountryCodeWithPlus
    //}

//}