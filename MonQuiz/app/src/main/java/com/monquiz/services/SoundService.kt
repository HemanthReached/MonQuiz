package com.monquiz.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.monquiz.R


class SoundService : Service() {
    private lateinit var mp: MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val s = intent?.getBooleanExtra("sound",false)
        if (s == true){
            mp = MediaPlayer.create(this,R.raw.sound_yay)
        }else{
            mp = MediaPlayer.create(this,R.raw.sound_ahh)
        }
        mp.isLooping = false
       if (!mp.isPlaying){
        mp.start()
       }
        return START_STICKY
    }

    override fun onCreate() {
        mp = MediaPlayer.create(this, R.raw.sound_yay)
        mp.isLooping = false
       // mp.setVolume(50F, 50F)
    }

    override fun onDestroy() {
        mp.stop()
        mp.release()
    }

    override fun onStart(intent: Intent?, startid: Int) {
        Log.d("tag", "On start")
        if (!mp.isPlaying){
            mp.start()
        }
    }

}