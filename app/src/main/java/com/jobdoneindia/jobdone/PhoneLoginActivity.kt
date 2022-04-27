package com.jobdoneindia.jobdone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hbb20.CountryCodePicker

class PhoneLoginActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {

    private var ccp: CountryCodePicker? = null
    private var selected_country_code: String = "+91"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        supportActionBar?.hide()

        ccp = findViewById(R.id.ccp)
        ccp!!.setOnCountryChangeListener(this)
    }

    override fun onCountrySelected() {
        selected_country_code = ccp!!.selectedCountryCodeWithPlus
    }

}