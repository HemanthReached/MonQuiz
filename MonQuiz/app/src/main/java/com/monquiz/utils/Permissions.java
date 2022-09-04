package com.monquiz.utils;



import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;


public class Permissions {

    public static boolean checkPermissionForAccessExternalStorage(Context context) {
        return checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissionForAccessExternalStorage(Activity activity) {
        requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_FOR_EXTERNAL_STORAGE_PERMISSION);
    }

    public static boolean checkPermissionForAccessCamera(Context context) {
        return checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissionForAccessCamera(Activity activity) {
        requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_FOR_CAMERA_PERMISSION);
    }

    public static boolean checkPermissionForAccessLocation(Context context) {
        return checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissionForAccessLocation(Activity activity) {
        requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_FOR_LOCATION);
    }
}
