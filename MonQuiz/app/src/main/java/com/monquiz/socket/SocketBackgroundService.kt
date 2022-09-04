package com.monquiz.socket

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class SocketBackgroundService : Service() {
    private var isRunning = false
    private var backgroundThread: Thread? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        isRunning = false
        backgroundThread = Thread(myTask)
    }

    private val myTask = Runnable {
        // do something in here
        Log.i("INFO", "SOCKET BACKGROUND SERVICE IS RUNNING")

        //TODO - how to handle socket events here?
        //How do I do something like mSocket.on(Socket.EVENT_CONNECT,onConnect); here?
    }

    override fun onDestroy() {
        isRunning = false
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            backgroundThread!!.start()
        }
        return START_STICKY
    }
}