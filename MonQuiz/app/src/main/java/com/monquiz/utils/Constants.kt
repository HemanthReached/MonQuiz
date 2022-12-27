package com.monquiz.utils

object Constants {
    const val SPLASH_TIME_OUT: Long = 5000
    const val SPLASH_TIME_OUT1: Long = 3000

    const val Otp:String="OTP"
    // Firebase references
    const val REFERENCE_TIMER = "manageGames/nextGame"

    //image save folder
    const val IMAGE_FILE_FOLDER = "/monquiz"
    const val REQUEST_CODE_FOR_EXTERNAL_STORAGE_PERMISSION = 1
    const val REQUEST_CODE_FOR_CAMERA_PERMISSION = 2
    const val REQUEST_CODE_FOR_LOCATION = 3

    // download url's
    const val DEFAULT_IMAGE_URL = "https://owlizz.page.link/av11"
    const val APK_SHARE_URL_FREE = "bit.ly/MonQuizFree"
    const val APK_SHARE_URL_PRO = "bit.ly/MonQuizPro"
    const val APK_WEBSITE_URL_PRO = "https://MonQuiz.in"
    const val APK_DOWNLOAD_URL_FREE = "https://MonQuiz.page.link/apk"
    const val APK_DOWNLOAD_URL_PRO = "http://storage.googleapis.com/apk-uploads/pro-apk/MonQuizPro.apk"

    // daily play limit
    const val DAILY_LIMIT: Long = 1000
    const val TOTALMILISECONDSINDAY: Long = 86400000

    // initial amount given after successful registration
    const val INITIAL_REWARD = 500.0
    const val DAILY_REWARD = 200.0
    const val MINUS_GAME_TIME = 7

    // lock the table at 7 sec's remaining
    const val TABLE_LOCK_TIME = 7

    // paytm Constants
    // public static final String M_ID = "SWSqWu20953395991075";
    // Paytm Merchant Id we got it in paytm credentials
    // public static final String M_ID = "estepl68196834211251";
    // Paytm Merchant Id we got it in paytm credentials
    // public static final String M_ID = "ESTDTe90087842413887";
    // Paytm Merchant Id we got it in paytm credentials changed

    const val M_ID = "WuERci82208696795292" //Paytm Merchant Id we got it in paytm credentials Testing
    const val M_ID1 = "htVDAP98268679956710" //Paytm Merchant Id we got it in paytm credentials Testing
    const val Prod_MID = "VPPunV55275025454934" //Paytm Merchant Id we got it in paytm credentials
    const val CHANNEL_ID = "WAP" //Paytm Channel Id, got it in paytm credentials
    const val INDUSTRY_TYPE_ID = "Retail" //Paytm industry type got it in paytm credential
    const val AMOUNT = "10.01" //Paytm amount
    const val WEBSITE = "WEBSTAGING"

    // public static final String WEBSITE = "APPPROD"; // orginl
    const val callbackpaytm = "https://merchant.com/callback"

    // public static final String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
    // public static final String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=";
    const val endpoint = "&orderId="
    const val CALLBACK_URL =
        "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=%s" //orginsl
    const val MOBILE = "7777777777"
}