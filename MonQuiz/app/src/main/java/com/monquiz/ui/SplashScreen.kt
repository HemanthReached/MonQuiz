package com.monquiz.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.BuildConfig
import com.monquiz.R
import com.monquiz.adapter.AdapterAvatars
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.network.InternetStateChecker
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.leaderboard.resp.GetLeaderBoardPositionResponse
import com.monquiz.response.updatecheck.UpdateResponse
import com.monquiz.utils.Constants
import com.monquiz.utils.Constants.APK_WEBSITE_URL_PRO
import com.monquiz.utils.DialogUtils
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.xml.transform.OutputKeys.VERSION

class SplashScreen : BaseActivity() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var dialogAppUpdate: Dialog? = null
    private lateinit var internetchecker : InternetStateChecker
    var version = 0
    private lateinit var inflator : LayoutInflater
    lateinit var alertdialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        if (internetchecker.isConnected){ checkForUpdate() }
        else{ Toasty.error(this,"please check your internet connection",
            Toasty.LENGTH_SHORT).show()
            val handler1 = Handler()
            val runnable1 = Runnable { finish() }
            handler1.postDelayed(runnable1, Constants.SPLASH_TIME_OUT)
        }
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
        handler!!.postDelayed(runnable!!, Constants.SPLASH_TIME_OUT1)
    }

    private fun openUpdateDialog() {
        dialogAppUpdate = Dialog(this)
        if (dialogAppUpdate!!.window != null) {
            DialogUtils.setDialogAttributes(dialogAppUpdate!!)
            dialogAppUpdate!!.setContentView(R.layout.update_popup)
            val btnUpdateNow = dialogAppUpdate!!.findViewById<TextView>(R.id.updateBtn)
            btnUpdateNow.visibility = View.VISIBLE
            btnUpdateNow.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(APK_WEBSITE_URL_PRO))
                val chooser = Intent.createChooser(intent, "Choose Your Browser")
                try {
                    startActivity(chooser)
                }catch (e : Exception){
                    startActivity(intent)
                }
            }
            dialogAppUpdate!!.window!!.decorView.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent))
            dialogAppUpdate!!.setCanceledOnTouchOutside(false)
            dialogAppUpdate!!.show()
            DialogUtils.removeDialogAttributes(dialogAppUpdate!!)
           // hideStatusBarForDialog(dialogAppUpdate!!)
        }
    }

    private fun showDialog() {
        val updateBtn : TextView
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        inflator = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogLayout: View = inflator.inflate(R.layout.update_popup, null)
        builder.setView(dialogLayout)
        updateBtn = dialogLayout.findViewById<View>(R.id.updateBtn) as TextView

        updateBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(APK_WEBSITE_URL_PRO))
            val chooser = Intent.createChooser(intent, "Choose Your Browser")
            try {
                startActivity(chooser)
            }catch (e : Exception){
                startActivity(intent)
            }
        }

        val layoutParams = WindowManager.LayoutParams()
        alertdialog = builder.create()
        alertdialog.setCancelable(false)
        layoutParams.copyFrom(alertdialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        alertdialog.window!!.attributes = layoutParams
        alertdialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertdialog.show()
    }

    private fun checkForUpdate(){
        showProgressBar("Checking for updates")
        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.updateCheck()

        requestCall.enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("updateCheck","FailureResponse $t")
                Toasty.error(this@SplashScreen,"Request Failed", Toasty.LENGTH_SHORT).show()
                val handler3 = Handler()
                val runnable3 = Runnable { finish() }
                handler3.postDelayed(runnable3, Constants.SPLASH_TIME_OUT1)
            }
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("updateCheck", "GateWalletResponse:// $resp")
                    if (resp!!.status == 200){
                        if (resp.responseData != null){
                            version = resp.responseData!!.version!!
                            if (version == BuildConfig.VERSION_CODE){
                                startHandler()
                            }else{
                               // openUpdateDialog()
                                showDialog()
                            }
                        }
                    }
                } else{
                    closeProgressbar()
                    Log.i("updateCheck","ResponseLangError:// ${response.errorBody().toString()}")
                    Toasty.error(this@SplashScreen, "Something went wrong",
                        Toasty.LENGTH_SHORT).show()
                    val handler2 = Handler()
                    val runnable2 = Runnable { finish() }
                    handler2.postDelayed(runnable2, Constants.SPLASH_TIME_OUT1)
                }
            }
        })
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