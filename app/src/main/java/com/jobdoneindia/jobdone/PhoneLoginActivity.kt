package com.jobdoneindia.jobdone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.chaos.view.PinView
import com.hbb20.CountryCodePicker

class PhoneLoginActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {

    private var ccp: CountryCodePicker? = null
    private var selected_country_code: String = "+91"
    private var firstPinView: PinView? = null
    private var phoneLayout: ConstraintLayout? = null
    private var phoneEditText: EditText? = null
    private var skipButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        supportActionBar?.hide()

        ccp = findViewById(R.id.ccp)
        phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        firstPinView = findViewById<View>(R.id.firstPinView) as PinView
        phoneLayout = findViewById<View>(R.id.phoneLayout) as ConstraintLayout

        skipButton = findViewById<View>(R.id.skip_button) as Button

        // country code picker
        ccp!!.setOnCountryChangeListener(this)

        // Text Watcher
        phoneEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 10) {
                    Toast.makeText(this@PhoneLoginActivity, "10 digits detected", Toast.LENGTH_SHORT).show()
                    phoneLayout!!.visibility = View.GONE
                    firstPinView!!.visibility = View.VISIBLE
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
                if (s.toString().length == 5) {
                    Toast.makeText(this@PhoneLoginActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
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

    }

    override fun onCountrySelected() {
        selected_country_code = ccp!!.selectedCountryCodeWithPlus
    }

}