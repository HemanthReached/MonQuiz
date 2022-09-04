package com.monquiz.utils;

public class ForceUpdateChecker {

   /* public static final String KEY_UPDATE_MESSAGE_FREE = "update_message_free";
    public static final String KEY_UPDATE_MESSAGE_PRO = "update_message_pro";

    public static final String KEY_FORCE_UPDATE_MESSAGE_FREE = "force_update_message_free";
    public static final String KEY_FORCE_UPDATE_MESSAGE_PRO = "force_update_message_pro";

    public static final String KEY_UPDATE_VERSION_FREE = "update_version_free";
    public static final String KEY_UPDATE_VERSION_PRO = "update_version_pro";

    public static final String KEY_LAST_FORCE_UPDATE_FREE = "last_force_update_free";
    public static final String KEY_LAST_FORCE_UPDATE_PRO = "last_force_update_pro";

    public static final String KEY_UPDATE_URL_FREE = "force_update_store_url_free";
    public static final String KEY_UPDATE_URL_PRO = "force_update_store_url_pro";

    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl, double lastForceUpdateNumber, double VersionNumber, String updateMessage);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void checkUpdate() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        if (BuildConfig.PRO_VERSION) {
            String updateMessage = "";
            double appCurrentVersion = Double.parseDouble(getAppVersion(context));
            double versionNumber = BuildConfig.versionNumber;

            // remote config var's
            double remoteVersion = remoteConfig.getDouble(KEY_UPDATE_VERSION_PRO);

            String updateUrl = remoteConfig.getString(KEY_UPDATE_URL_PRO);

            double lastForceUpdateNumber = remoteConfig.getDouble(KEY_LAST_FORCE_UPDATE_PRO);
            if (versionNumber < lastForceUpdateNumber) {
                updateMessage = remoteConfig.getString(KEY_FORCE_UPDATE_MESSAGE_PRO);
            } else {
                updateMessage = remoteConfig.getString(KEY_UPDATE_MESSAGE_PRO);
            }

//            CommonMethods.infoLog(" versionNumber: " + versionNumber);
//            CommonMethods.infoLog(" lastForceUpdateNumber: " + lastForceUpdateNumber);
//            CommonMethods.infoLog("remote remoteVersion: " + remoteVersion);
//            CommonMethods.infoLog("remote currentVersion: " + currentVersion);
//            CommonMethods.infoLog("remote updateUrl: " + updateUrl);

            if (remoteVersion > appCurrentVersion && onUpdateNeededListener != null) {
                onUpdateNeededListener.onUpdateNeeded(updateUrl, lastForceUpdateNumber, versionNumber, updateMessage);
            }
        } else {
            String updateMessage = "";
            double appCurrentVersion = Double.parseDouble(getAppVersion(context));
            double versionNumber = BuildConfig.versionNumber;

            double remoteVersion = remoteConfig.getDouble(KEY_UPDATE_VERSION_FREE);
            String updateUrl = remoteConfig.getString(KEY_UPDATE_URL_FREE);
            double lastForceUpdateNumber = remoteConfig.getDouble(KEY_LAST_FORCE_UPDATE_FREE);
            if (versionNumber < lastForceUpdateNumber) {
                updateMessage = remoteConfig.getString(KEY_FORCE_UPDATE_MESSAGE_FREE);
            } else {
                updateMessage = remoteConfig.getString(KEY_UPDATE_MESSAGE_FREE);
            }

//               CommonMethods.infoLog("remote remoteVersion: " + remoteVersion);
//               CommonMethods.infoLog("remote appCurrentVersion: " + currentVersion);
//               CommonMethods.infoLog("remote updateUrl: " + updateUrl);
            if (remoteVersion > appCurrentVersion && onUpdateNeededListener != null) {
                onUpdateNeededListener.onUpdateNeeded(updateUrl, lastForceUpdateNumber, versionNumber, updateMessage);
            }
        }
    }

    private String getAppVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            CommonMethods.errorLog(e.getMessage());
        }

        return result;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker checkUpdate() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.checkUpdate();

            return forceUpdateChecker;
        }
    }*/
}
