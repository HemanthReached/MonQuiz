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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}