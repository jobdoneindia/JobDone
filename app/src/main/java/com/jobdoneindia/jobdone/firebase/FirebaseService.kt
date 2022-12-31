package com.jobdoneindia.jobdone.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jobdoneindia.jobdone.R
import com.jobdoneindia.jobdone.activity.ChatUserList
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"
class FirebaseService : FirebaseMessagingService() {


    var count = 0

    companion object{
        var sharedPref:SharedPreferences? = null

        var token:String?
        get() {
            return sharedPref?.getString("token","")
        }
        set(value){
            sharedPref?.edit()?.putString("token",value)?.apply()
        }
    }

    override fun onNewToken(p0: String){
        super.onNewToken(p0)
        token = p0
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)





        val intent = Intent(this,ChatUserList::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
              createNotificationChannel(notificationManager)
          }

       /* intent.putExtra("username",currentUser.username)
        intent.putExtra("uid",currentUser.uid)*/


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_MUTABLE)
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["message"])
            .setSmallIcon(R.drawable.img_jobdone_round)

            .setTicker("Notification Listener")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId,notification)

        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()



        count++.toString().toInt()

        var counter = count++
        editor.apply {
           putInt("counter", counter)
            apply()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){

        val channelName = "ChannelName"
        val channel = NotificationChannel(CHANNEL_ID,channelName, IMPORTANCE_HIGH).apply {
            description = "MY CHANNEL DESCRIPTION"
            enableLights(true)
            lightColor = Color.WHITE
        }
        notificationManager.createNotificationChannel(channel)

    }
}