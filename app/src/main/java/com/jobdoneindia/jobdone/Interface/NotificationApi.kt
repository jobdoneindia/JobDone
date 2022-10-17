package com.jobdoneindia.jobdone.Interface

import com.jobdoneindia.jobdone.Constants.Constants.Companion.CONTENT_TYPE
import com.jobdoneindia.jobdone.Constants.Constants.Companion.SERVER_KEY
import com.jobdoneindia.jobdone.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ):Response<ResponseBody>
}