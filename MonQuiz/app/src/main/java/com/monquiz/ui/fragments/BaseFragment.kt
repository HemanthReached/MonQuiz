package com.monquiz.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.monquiz.R
import com.monquiz.utils.CommonMethods
import com.monquiz.utils.DialogUtils
import com.monquiz.utils.NetworkDetection
import java.lang.Exception

open class BaseFragment : Fragment() {
    private var progressDialog: Dialog? = null

    //  public FirebaseAnalytics firebaseAnalytics;
    private var mContext: Context? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    fun showProgressBar(message: String?) {
        if (progressDialog != null && !progressDialog!!.isShowing) {
            progressDialog =
                activity?.let { DialogUtils.dialogUtilsInstance!!.progressDialog(it, message!!) }
        } else {
            closeProgressbar()
            progressDialog = null
            progressDialog =
                activity?.let { DialogUtils.dialogUtilsInstance!!.progressDialog(it, message!!) }
        }
    }

    fun closeProgressbar() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    fun checkInternet(): Boolean {
        val isMobileNetworkAvail: Boolean = NetworkDetection().isMobileNetworkAvailable(activity)
        val isWifiAvail: Boolean = NetworkDetection().isWifiAvailable(activity)
        //Toast.makeText(getActivity(), "network avail", Toast.LENGTH_SHORT).show();
        return isMobileNetworkAvail || isWifiAvail
    }

    /**
     * @param message message
     */
    fun showToast(message: String?) {
        if (activity == null) { return }
        val li: LayoutInflater = requireActivity().layoutInflater
        val layout: View = li.inflate(R.layout.layout_custom_toast,
            requireActivity().findViewById(R.id.customToastRootView))
        val text: TextView = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        val x: Int = CommonMethods.getWidth(activity)
        val y = (CommonMethods.getHeight(activity) / 4.5).toInt() as Int
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y)
        toast.view = layout
        toast.show()
    }

    /**
     * @param message message
     */
    fun showToast(message: String?, duration: Int) {
        if (activity == null) { return }
        val li: LayoutInflater = requireActivity()!!.layoutInflater
        val layout: View = li.inflate(R.layout.layout_custom_toast,
            requireActivity().findViewById(R.id.customToastRootView))
        val text: TextView = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        val x: Int = CommonMethods.getWidth(activity)
        val y = (CommonMethods.getHeight(activity) / 4.5) as Int
        val toast = Toast(activity)
        toast.duration = duration
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, y)
        toast.view = layout
        toast.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun sendCrashlyticsDetails(e: Exception?) {
        /* //   Crashlytics.setUserIdentifier(PreferenceConnector.readString(mContext, getString(R.string.user_id), ""));
         // Crashlytics.setString("game_number", PreferenceConnector.readString(mContext, getString(R.string.current_game_id), ""));
         // Crashlytics.logException(e);
         // Crashlytics.setUserName(PreferenceConnector.readString(mContext, getString(R.string.user_full_name), ""));
       public void sendAnalyticsDetails(String eventType, Bundle bundle) {
         firebaseAnalytics.logEvent(eventType, bundle);
         firebaseAnalytics.setUserId(PreferenceConnector.readString(mContext, getString(R.string.user_id), ""));
         firebaseAnalytics.setUserProperty("user_name", PreferenceConnector.readString(mContext, getString(R.string.user_full_name), ""));
         firebaseAnalytics.setUserProperty("game_number", PreferenceConnector.readString(mContext, getString(R.string.current_game_id), ""));
     }*/
    }
}