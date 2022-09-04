package com.monquiz.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.content.PermissionChecker;

import com.google.android.gms.maps.model.LatLng;

public class NetworkDetection {

  private static final String TAG = NetworkDetection.class.getSimpleName();
  private static Location location;
  private static LatLng latLng;

  public boolean isMobileNetworkAvailable(Context ctx) {
    ConnectivityManager connectionManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connectionManager != null;
    NetworkInfo myNetworkInfo = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    try {
      return myNetworkInfo != null && (myNetworkInfo.isConnected());
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    return false;
  }

  public boolean isWifiAvailable(Context ctx) {
    ConnectivityManager myConnManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    assert myConnManager != null;
    NetworkInfo myNetworkInfo = myConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    return myNetworkInfo.isConnected();
  }

  public boolean isGpsEnabled(Context ctx) {
    LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      if (PermissionChecker.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        return false;
      }
      Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

      if (location != null) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        latLng = new LatLng(latitude, longitude);
      }
      return true;
    } else {
      return false;
    }
  }
}
