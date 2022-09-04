package com.monquiz.ui

import android.content.BroadcastReceiver
import android.os.Bundle
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import com.monquiz.utils.NetworkChangeReceiver
import com.monquiz.R
import com.monquiz.utils.CommonMethods
import com.monquiz.utils.NetworkDetection
import com.monquiz.utils.DialogUtils
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Spannable
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.lang.Exception

open class BaseActivity : AppCompatActivity() {

    private var progressDialog: Dialog? = null
    private var networkReceiver: BroadcastReceiver? = null
    // public FirebaseAnalytics firebaseAnalytics;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    //    hideStatusBar()
        //        activity = BaseActivity.this;
        if (activityManager == null) {
            activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        }
        networkReceiver = NetworkChangeReceiver()
        registerNetworkBroadcastForNougat()
        // firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Fabric.with(this, new Crashlytics());
    }

    /**
     * Hide both the navigation bar and the status bar.
     */
  /*  private fun hideStatusBar() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }*/

    /**
     * @param message message
     */
    fun showToast(message: String?) {
        val li = this.layoutInflater
        val layout =
            li.inflate(R.layout.layout_custom_toast, findViewById(R.id.customToastRootView))
        val text = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        val x = CommonMethods.getWidth(this)
        val y = (CommonMethods.getHeight(this) / 4.5).toInt()
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y)
        toast.view = layout
        toast.show()
    }

    /**
     * @param message message
     */
    fun showToast(message: String?, duration: Int) {
        val li = this.layoutInflater
        val layout =
            li.inflate(R.layout.layout_custom_toast, findViewById(R.id.customToastRootView))
        val text = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        val x = CommonMethods.getWidth(this)
        val y = (CommonMethods.getHeight(this) / 4.5).toInt()
        val toast = Toast(this)
        toast.duration = duration
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y)
        toast.view = layout
        toast.show()
    }

    /**
     * @param message message
     */
    fun showToastCancellable(message: String?, duration: Int): Toast {
        val li = this.layoutInflater
        val layout =
            li.inflate(R.layout.layout_custom_toast, findViewById(R.id.customToastRootView))
        val text = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        val x = CommonMethods.getWidth(this)
        val y = (CommonMethods.getHeight(this) / 4.5).toInt()
        val toast = Toast(this)
        toast.duration = duration
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y)
        toast.view = layout
        toast.show()
        return toast
    }

    /**
     * @param message  message
     * @param fontSize font size
     * @param yOffset  yoffset
     * @return toast
     */
    fun showToast(message: String?, fontSize: Float, yOffset: Float): Toast {
        val li = this.layoutInflater
        val layout =
            li.inflate(R.layout.layout_custom_toast_1, findViewById(R.id.customToastRootView))
        //        final ViewGroup.LayoutParams layoutParams =  layout.getLayoutParams();
//        layoutParams.height = (int) (getHeight(this)*heightFactor);
//        layoutParams.width = (int) (getWidth(this)*widthFactor);
//        layout.setLayoutParams(layoutParams);
        val text = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        text.textSize = fontSize
        val x = CommonMethods.getWidth(this)
        val y = (CommonMethods.getHeight(this) * yOffset).toInt()
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y)
        //        toast.setMargin(100,100);
        toast.view = layout
        toast.show()
        return toast
    }

    /**
     * @param message  message
     * @param fontSize font size
     * @param yOffset  yoffset
     * @param color    color
     * @return toast
     */
    fun showToast(message: String?, fontSize: Float, yOffset: Float, color: Int): Toast {
        val li = this.layoutInflater
        val layout =
            li.inflate(R.layout.layout_custom_toast_1, findViewById(R.id.customToastRootView))
        //        final ViewGroup.LayoutParams layoutParams =  layout.getLayoutParams();
//        layoutParams.height = (int) (getHeight(this)*heightFactor);
//        layoutParams.width = (int) (getWidth(this)*widthFactor);
//        layout.setLayoutParams(layoutParams);
        val text = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        text.textSize = fontSize
        text.setTextColor(color)
        val x = CommonMethods.getWidth(this)
        val y = (CommonMethods.getHeight(this) * yOffset).toInt()
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y)
        //        toast.setMargin(100,100);
        toast.view = layout
        toast.show()
        return toast
    }

    fun checkInternet(): Boolean {
        val isMobileNetworkAvail = NetworkDetection().isMobileNetworkAvailable(this@BaseActivity)
        val isWifiAvail = NetworkDetection().isWifiAvailable(this@BaseActivity)
        //Toast.makeText(this, "network avail", Toast.LENGTH_SHORT).show();
        return isMobileNetworkAvail || isWifiAvail
    }

    fun updateTheme() {
        if (CommonMethods.getTheme(applicationContext) <= THEME_BLUE) {
            setTheme(R.style.AppTheme_Blue)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.primaryColorDark_blue)
        } else if (CommonMethods.getTheme(applicationContext) == THEME_RED) {
            setTheme(R.style.AppTheme_Red)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.primaryColorDark_red)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //        removed the below condition check because, still want to hide System UI even if activity
//        loses focus.Such as when spinner, dropdown list, dialog box is displayed, etc.
     //   if (hasFocus) hideStatusBar()
    }

    fun showProgressBar(message: String?) {
        if (progressDialog != null && !progressDialog!!.isShowing) {
            progressDialog = DialogUtils.dialogUtilsInstance!!.progressDialog(this, message!!)
         //   hideStatusBar()
        } else {
            closeProgressbar()
            progressDialog = null
            progressDialog = DialogUtils.dialogUtilsInstance!!.progressDialog(this, message!!)
        }
    }

    fun closeProgressbar() {
        if (progressDialog != null) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
            }
        }
    }

    /**
     * @param completeString complete string
     * @param color          color need to give
     * @param stringStart    starting point
     * @param stringEnd      end point
     * @return spannable color string
     */
    fun getColorSpannable(completeString: String?, color: Int, stringStart: Int,
        stringEnd: Int): SpannableString {
        val spannableColorString = SpannableString(completeString)
        spannableColorString.setSpan(
            ForegroundColorSpan(color),
            stringStart, stringEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableColorString
    }

    fun dialog(value: Boolean, context: Context?) {
        try {
            if (!value) {
                val dialogNoInternet = Dialog(context!!)
                if (dialogNoInternet.window != null) {
                    DialogUtils.setDialogAttributes(dialogNoInternet)
                    dialogNoInternet.setContentView(R.layout.layout_custom_dialog)
                    val rlCustomDialog =
                        dialogNoInternet.findViewById<RelativeLayout>(R.id.rlCustomDialog)
                    rlCustomDialog.background = resources.getDrawable(R.drawable.bg_white_curve)
                    val btnCustomDialogCancel =
                        dialogNoInternet.findViewById<Button>(R.id.btnCustomDialogCancel)
                    btnCustomDialogCancel.visibility = View.GONE
                    val ivCustomDialogIcon =
                        dialogNoInternet.findViewById<ImageView>(R.id.ivCustomDialogIcon)
                    ivCustomDialogIcon.visibility = View.GONE
                    val btnCustomDialogOk =
                        dialogNoInternet.findViewById<Button>(R.id.btnCustomDialogOk)
                    val tvCustomDialogHeading =
                        dialogNoInternet.findViewById<TextView>(R.id.tvCustomDialogHeading)
                    tvCustomDialogHeading.text = "Network problem!"
                    val tvCustomDialogDesc =
                        dialogNoInternet.findViewById<TextView>(R.id.tvCustomDialogDesc)
                    tvCustomDialogDesc.text = "Internet connection is not available"
                    btnCustomDialogOk.setOnClickListener { v: View? -> dialogNoInternet.dismiss() }
                    dialogNoInternet.show()
                    DialogUtils.removeDialogAttributes(dialogNoInternet)
                    hideStatusBarForDialog(dialogNoInternet)
                }
            }
        } catch (ex: Exception) {
            CommonMethods.errorLog("dialogNoInternet excep:$ex")
            //new BaseActivity().sendCrashlyticsDetails(ex);
        }
    }

    /**
     * method for running broadcast receiver > M
     */
    private fun registerNetworkBroadcastForNougat() {
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    /**
     * unregister broadcast receiver.
     */

    private fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(networkReceiver)
        } catch (e: Exception) {
            CommonMethods.errorLog("exception network: $e")
        }
    }

    override fun onResume() {
        super.onResume()
      //  hideStatusBar()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }

    /*
    public void sendCrashlyticsDetails(Exception e) {
        Crashlytics.setUserIdentifier(PreferenceConnector.readString(this, getString(R.string.user_id), ""));
        Crashlytics.setString("game_number", PreferenceConnector.readString(this, getString(R.string.current_game_id), ""));
        Crashlytics.logException(e);
        Crashlytics.setUserName(PreferenceConnector.readString(this, getString(R.string.user_full_name), ""));
    }

    public void sendAnalyticsDetails(String eventType, Bundle bundle) {
        firebaseAnalytics.logEvent(eventType, bundle);
        firebaseAnalytics.setUserId(PreferenceConnector.readString(this, getString(R.string.user_id), ""));
        firebaseAnalytics.setUserProperty("user_name", PreferenceConnector.readString(this, getString(R.string.user_full_name), ""));
        firebaseAnalytics.setUserProperty("game_number", PreferenceConnector.readString(this, getString(R.string.current_game_id), ""));
    }*/

    companion object {
        private const val THEME_BLUE = 1
        private const val THEME_RED = 2
        protected var activityManager: ActivityManager? = null

        /**
         * Hide both the navigation bar and the status bar.
         *
         * @param dialog object
         */
        fun hideStatusBarForDialog(dialog: Dialog) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val window = dialog.window
                if (window != null) {
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                }
            }
        }
    }
}