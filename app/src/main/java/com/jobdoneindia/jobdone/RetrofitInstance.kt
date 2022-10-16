package com.jobdoneindia.jobdone

import com.jobdoneindia.jobdone.Constants.Constants
import com.jobdoneindia.jobdone.Constants.Constants.Companion.BASE_URL
import com.jobdoneindia.jobdone.Interface.NotificationApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationApi::class.java)
        }
    }

}