package com.monquiz.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.monquiz.R
import com.monquiz.network.InternetStateChecker
import com.monquiz.utils.Constants
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper
import es.dmoral.toasty.Toasty

class SplashScreen : BaseActivity() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private lateinit var internetchecker : InternetStateChecker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        startHandler()
    }

    private fun startHandler() {
        handler = Handler()
        runnable = Runnable {
            val intentMove: Intent = if(!PrefsHelper().getPref<Boolean>(OwlizConstants.login,false)){
                Intent(this, SignUpActivity::class.java)
            } else if(!PrefsHelper().getPref<Boolean>(OwlizConstants.username,false)){
                Intent(this, UsernameActivity::class.java)
            } else {
                Intent(this, DashboardActivity::class.java)
            }

            if (internetchecker.isConnected){ startActivity(intentMove) }
            else{ Toasty.error(this,"please check your internet connection",
                    Toasty.LENGTH_SHORT).show() }
            finish()
        }
        handler!!.postDelayed(runnable!!, Constants.SPLASH_TIME_OUT)
    }

    override fun onResume() {
        super.onResume()
        internetchecker.start()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }

    override fun onDestroy() {
        super.onDestroy()
        internetchecker.stop()
    }

    override fun onStop() {
        super.onStop()
        internetchecker.stop()
    }

    override fun onPause() {
        super.onPause()
        internetchecker.stop()
    }

}