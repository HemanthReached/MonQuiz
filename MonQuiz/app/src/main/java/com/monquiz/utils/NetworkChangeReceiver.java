package com.monquiz.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.monquiz.ui.BaseActivity;


public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                new BaseActivity().dialog(true,context);
                // CommonMethods.infoLog("Online Connect Intenet ");
            } else {
                new BaseActivity().dialog(false,context);
                //  CommonMethods.infoLog("Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            CommonMethods.errorLog("netwrk onReceive excep:" + e.toString());
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            // check NetworkInfo subtype
            if (networkInfo != null && networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS) {
                // Bandwidth between 100 kbps and below
                CommonMethods.infoLog("Bandwidth between 100 kbps and below");
            } else if (networkInfo != null && networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                // Bandwidth between 50-100 kbps
                CommonMethods.infoLog("Bandwidth between 50-100 kbps");
            } else if (networkInfo != null && networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0) {
                CommonMethods.infoLog("Bandwidth between 400-1000 kbps");
                // Bandwidth between 400-1000 kbps
            } else if (networkInfo != null && networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                CommonMethods.infoLog("Bandwidth between 600-1400 kbps");
                // Bandwidth between 600-1400 kbps
            }
            return networkInfo != null && networkInfo.isConnected();
        } catch (NullPointerException e) {
            CommonMethods.errorLog("excep networkreceiver:" + e);
            return false;
        }
    }
}