package com.monquiz.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.monquiz.R


class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun onStart(intent: Intent?, startid: Int) {
        Log.d("tag", "On start")

    }

}