package com.monquiz.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.monquiz.R;
import com.monquiz.ui.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

   // DatabaseReference databaseReference;
////    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
/*

    public void setNewUserDailyDetails(Context context) {
        final String refSetDailyRewardDetails = String.format(context.getString(R.string.api_set_daily_reward_details), fbAuth.getCurrentUser().getUid());
        long date = currentTimeMillis();
        UsersDailyDetails setDailyRewardDetails = new UsersDailyDetails(date,
                0, false, false);
        databaseReference = FirebaseDatabase.getInstance().getReference(refSetDailyRewardDetails);
        databaseReference.setValue(setDailyRewardDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(e -> {
            CommonMethods.errorLog("exception setDailyRewardDetails " + e.toString());
        });
    }
*/

    public boolean downloadApk(Context context, String downloadUrl, String title, BaseActivity activity) {
        //code for download apk.
        try {
            activity.showToast("Downloading file...");
            Uri uri;
            DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            // dynamic link
            uri = Uri.parse(downloadUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(title);
            request.setDescription("Downloading...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(false);
            String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sdcard/Download";
            request.setDestinationUri(Uri.parse("file://" + folder + "/" + title));
            if (downloadmanager != null) {
                downloadmanager.enqueue(request);
            }
//            File file = new File(folder, "OwlizzPro.apk");
//            installApk(file, context);

        } catch (Exception exception) {
            CommonMethods.errorLog("Excep Download error");
            activity.showToast("Something went wrong..");
          /*  if (!PreferenceConnector.readString(context, activity.getString(R.string.user_id), "").isEmpty()) {
                //activity.sendCrashlyticsDetails(exception);
            }*/
        }
        return true;
    }

    private File createApkFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = "Owl";
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Owlizz");
        File apk = File.createTempFile(
                imageFileName,  /* prefix */
                ".apk",         /* suffix */
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return apk;
    }

    public boolean downloadApk(Context context, String downloadUrl) throws IOException{
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
//        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
//        String fileName = "OwlizzPro1.apk";
//        destination += fileName;
////        final Uri uri = Uri.parse("file://" + destination);
//        //Delete update file if exists
//        File file = new File(destination);
//        if (file.exists())
//            file.delete();
//        final Uri uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider",new File("content://" + destination));
        File file = createApkFile();
        final Uri uri = (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) ?
                FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file) :
                Uri.fromFile(file);
//        final Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        //get url of app on server
//        Toast toast = ((BaseActivity) context).showToastCancellable("Downloading please wait....",20000);
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            CommonMethods.infoLog("apk file download started.");
            URL sUrl = new URL(downloadUrl);
            connection = (HttpURLConnection) sUrl.openConnection();
            connection.setRequestMethod("GET");
//            connection.setDoOutput(true);
            connection.connect();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(file);

            byte[] data = new byte[4096];
            int count;
            CommonMethods.infoLog("apk file download here1.");
            while ((count = input.read(data)) != -1) {
                CommonMethods.infoLog("apk file download here.");
                // allow canceling with back button
//                if (isCancelled()) {
//                    input.close();
//                    return null;
//                }

                output.write(data, 0, count);
            }
        } catch (Exception e) {
            CommonMethods.infoLog("apk download error occured"+e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        CommonMethods.infoLog("apk file download done.");
        Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE)
                .setData(uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(install);

        return false;

//        //get url of app on server
//        String url = downloadUrl;
//
//        //set downloadmanager
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
//        request.setDescription("Downloading...");;
////        request.setDescription(context.getString(R.string.notification_description));
////        request.setTitle(context.getString(R.string.app_name));
//        request.setTitle(title);
//        //set destination
//        request.setDestinationUri(uri);
//
//        // get download service and enqueue file
//        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        final long downloadId = manager.enqueue(request);
//
//        //set BroadcastReceiver to install app when .apk is downloaded
//        BroadcastReceiver onComplete = new BroadcastReceiver() {
//            public void onReceive(Context ctxt, Intent intent) {
//                Intent install = new Intent(Intent.ACTION_VIEW);
//                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                install.setDataAndType(uri,
//                        manager.getMimeTypeForDownloadedFile(downloadId));
//                ctxt.startActivity(install);
//
//                ctxt.unregisterReceiver(this);
//                ((Activity) context).finish();
//            }
//        };
//        //register receiver for when .apk download is compete
//        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//        return false;
    }

//    private void installApk(File file, Context context) {
//        try {
//            if (file.exists()) {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                    file.setReadable(true, false);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
//                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android" + ".package-archive");
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Uri fileUri = FileProvider.getUriForFile(context,
//                            getResources().getString(R.string.file_provider_authority), file);
//                    grantUriPermission(getResources().getString(R.string.file_provider_authority), fileUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.setData(fileUri);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
//                    intent.setDataAndType(fileUri, "application/vnd.android.package-archive");  // to open install intent.
//                    startActivity(intent);
//                }
//            } else {
//                CommonMethods.infoLog("file not downloaded.");
//            }
//
//        } catch (Exception e) {
//            CommonMethods.errorLog("excep install apk: " + e.toString());
//        }
//    }
/*
    public void setUsersWalletBalance(final Context context, final BaseActivity activity) {
        if(activity!=null) {
            activity.showProgressBar("saving your profile...");
        }
        final double balance;
        if (BuildConfig.PRO_VERSION) {
            balance = 0;
        } else {
            balance = INITIAL_REWARD;
        }
        final String urlBalance = String.format(context.getString(R.string.api_user_balance), fbAuth.getCurrentUser().getUid());
//        UsersWalletBalance usersWalletBalance = new UsersWalletBalance(balance);
        databaseReference = FirebaseDatabase.getInstance().getReference(urlBalance);
        CommonMethods.infoLog("setting balance if null: " + balance);
        PreferenceConnector.writeDouble(context, context.getString(R.string.user_wallet_balance), balance);
        if(activity!=null) {
            activity.closeProgressbar();
        }
//        databaseReference.setValue(balance).addOnSuccessListener(aVoid -> {
//            closeProgressbar();
//            AppController.getInstance().setWalletBalance(balance);
//        }).addOnFailureListener(e -> {
//            closeProgressbar();
//            CommonMethods.errorLog("Exception storing balance " + e.toString());
//        });
    }*/

    /*public static void clearAllTheData(Context context){
        PreferenceConnector.writeString(context, context.getString(R.string.normalquiz_table_id), "");
        PreferenceConnector.writeInteger(context, context.getString(R.string.play_selected_amount), 0);
        PreferenceConnector.writeString(context, context.getString(R.string.play_selected_category_name), "");
        PreferenceConnector.writeString(context, context.getString(R.string.play_selected_category_number), "");

        AppController.getInstance().setFragment(null);
        AppController.getInstance().setTimerContext(null);
        AppController.getInstance().setSelectedTableId(null);
        AppController.getInstance().setSelectedCategory(null);
        AppController.getInstance().setSelectedStake(null);
        AppController.getInstance().setQuestionLobby(null);
        AppController.getInstance().setMapOfUserDetails(null);
        AppController.getInstance().setListOfCorrectAnswer(null);
        AppController.getInstance().setListOfSubAnswers(null);
        AppController.getInstance().setScoreBoardLeaderDetails(null);

        PreferenceConnector.writeString(context, context.getString(R.string.quiz_master_table_id), "");
    }*/
}
