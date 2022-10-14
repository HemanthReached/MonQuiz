package com.monquiz.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.monquiz.R
import com.monquiz.services.ActionReceiver
import com.monquiz.ui.SplashScreen
import com.monquiz.ui.SplashScreen1
import java.util.concurrent.atomic.AtomicInteger


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebase", "onNewToken: $token")
        FirebaseToken.putKey(applicationContext,token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("MyFirebase", "onMessageReceived: FROM: " + remoteMessage.from.toString() +
                " \n DATA: " + remoteMessage.notification?.title)
        remoteMessage.notification?.let { Log.d("Message","${it.body}") }
        sendNotification(remoteMessage)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val channelId = "MonQuiz"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(channelId, "MonQuizNotifications",
            NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.vibrationPattern = longArrayOf(500, 500, 500)
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, SplashScreen1::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags =  PendingIntent.FLAG_IMMUTABLE
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getActivity(this, 0, intent, flags)
        }else{
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notificationBuilder = NotificationCompat.Builder(applicationContext,
            channelId).apply {
            setSmallIcon(R.mipmap.launchericon1)
            setLights(Color.BLUE, 500, 500)
            setVibrate(longArrayOf(500, 500, 500))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentTitle(remoteMessage.notification?.title)
            setContentText(remoteMessage.notification?.body)
            setChannelId(channelId)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }
        notificationManager.notify(NotificationID.iD, notificationBuilder.build())
    }
}

internal object NotificationID {
    private val c = AtomicInteger(100)
    val iD: Int = c.incrementAndGet()
}