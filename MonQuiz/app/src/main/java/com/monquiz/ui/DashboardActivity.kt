package com.monquiz.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.monquiz.BuildConfig
import com.monquiz.R
import com.monquiz.interfaces.TimerCallBack
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.network.EndPoints
import com.monquiz.network.InternetStateChecker
import com.monquiz.network.RetrofitApi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.login.LoginResponseOtp
import com.monquiz.ui.fragments.DashboardPlayFragment
import com.monquiz.ui.fragments.DashboardProfileFragment
import com.monquiz.ui.fragments.DashboardWalletFragment
import com.monquiz.utils.*
import com.monquiz.utils.Constants.APK_DOWNLOAD_URL_FREE
import com.monquiz.utils.Constants.APK_DOWNLOAD_URL_PRO
import com.monquiz.utils.Constants.REQUEST_CODE_FOR_LOCATION
import com.monquiz.utils.DialogUtils.Companion.removeDialogAttributes
import com.monquiz.utils.DialogUtils.Companion.setDialogAttributes
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


class DashboardActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    TimerCallBack, PaymentResultWithDataListener {
/*
    , ForceUpdateChecker.OnUpdateNeededListener
*/
    private var fragment: Fragment? = null
    private var fbAuth: FirebaseAuth? = null
 /*   private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var databaseReference: DatabaseReference? = null*/
    private var bottomNavigation: BottomNavigationView? = null
    private var timer = ""
    private var selectedMenuTitle = ""

    // dialog vars
    private var dialogClaimReward: Dialog? = null
    private var dialogShowLocation: Dialog? = null
    private var dialogNotification: Dialog? = null
    private var setBalance = 0.0
    private var tvClaimedPopupGetCoins: TextView? = null
    private var rlDialogClaimedPopUpPlayGame: RelativeLayout? = null
    private var rlDialogClaimedPopUp: RelativeLayout? = null
    private var ivClaimedPopupSpiral: ImageView? = null
    private var a = false
    private var b = false
    private var c = false
    var backCount = 0
    private var index = 0

    // download vars
    // category_location dialog vars
    private var tvDialogLocationStateAssam: TextView? = null
    private var tvDialogLocationStateNagaland: TextView? = null
    private var tvDialogLocationStateOdisha: TextView? = null
    private var tvDialogLocationStateTamilNadu: TextView? = null
    private var tvDialogLocationStateTelangana: TextView? = null
    private var rlDialogLocationDamn: RelativeLayout? = null
    private val statesNames = arrayOf("Assam", "Nagaland", "Tamil Nadu", "Odisha", "Telangana")
    private var ivProfile : ImageView? = null
    private var tvProfile : TextView? = null
    private var tvPlay : TextView? = null
    private var ivPlay : ImageView? = null
    private var ivWallet : ImageView? = null
    private var tvWallet : TextView? = null
    private var profileLayout : LinearLayout? = null
    private var playLayout : LinearLayout? = null
    private var walletLayout : LinearLayout? = null
    private var timerNotificationShow: CountDownTimer? = null
    private var mapOfNotification: LinkedHashMap<String, Notification>? = null
    private var listNotificationsKey: List<String>? = null
    private var notification: String? = ""
    var showDialog = true
    private lateinit var internetchecker : InternetStateChecker
    companion object{
        var mobilenumber = ""
        var showBalanceDialog = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
       // window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        setContentView(R.layout.layout_dashboard)
        checkLocation()
        initializeUi()
        initializeListeners()
        selectedMenuTitle = getString(R.string.play)
        val bundle = intent.extras
        if (bundle!= null){ showBalanceDialog = bundle.getBoolean("showbalance") }
        if (showBalanceDialog){ walletLayout!!.performClick() }
        else{ playLayout!!.performClick() }
        val apiToken: String = "Bearer " + PrefsHelper().getPref(OwlizConstants.token)
        val userid : String = PrefsHelper().getPref(OwlizConstants.user_id)
        Log.e("@@@ApiToken -->","$apiToken  ###userId  --> $userid")
        if (!BuildConfig.PRO_VERSION) { checkDailyReward() }
        getProfileData()
    }

    private fun checkLocation() {
        if (BuildConfig.PRO_VERSION) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_FOR_LOCATION)
            } else {
                getLocationOfUser(true)
            }
        }
    }

    private fun checkPermissionsStorage(): Boolean {
        var downloadUrl = ""
        var title = ""
        if (BuildConfig.PRO_VERSION) {
            downloadUrl = APK_DOWNLOAD_URL_PRO
            title = "MonQuizPro.apk"
        } else {
            downloadUrl = APK_DOWNLOAD_URL_FREE
            title = "MonQuiz.apk"
        }
        if (Permissions.checkPermissionForAccessExternalStorage(this)) {
            if (backCount == 0) {
                val durl = downloadUrl
                val ct: Context = this
                showProgressBar("Downloading,Please Wait...")
                RetrieveApkTask().execute("")

//                if (isSuccess) {
//                    finish();
//                }
            }
            return true
        } else { Permissions.requestPermissionForAccessExternalStorage(this) }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        if (permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            when (requestCode) {
                Constants.REQUEST_CODE_FOR_EXTERNAL_STORAGE_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (backCount == 0) {
                        checkPermissionsStorage()
                    } else {
                        val fragment = supportFragmentManager.findFragmentById(R.id.dashboardFragmentContainer)
                        fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    }
                    // Permission Granted
                } else {
                  //  finish()
                }
                REQUEST_CODE_FOR_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationOfUser(true)
                    // Permission Granted
                } else {
                  //  finish()
                }
                else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun getLocationOfUser(result: Boolean) {
        if (result) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this@DashboardActivity)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                CommonMethods.infoLog("permission return ")
                return
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val addresses: List<Address>
                    val geocoder: Geocoder = Geocoder(this@DashboardActivity, Locale.getDefault())
                    try {
                        addresses = geocoder.getFromLocation(location.latitude,
                            location.longitude, 1)!!
                        // Here 1 represent max category_location result to returned, by documents it recommended 1 to 5
                       // val address = addresses[0].getAddressLine(0)
                        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        val city = addresses[0].locality
                        val state = addresses[0].adminArea
                        val country = addresses[0].countryName
                        val postalCode = addresses[0].postalCode
                        val knownName = addresses[0].featureName
                        if (state != null) {
                            CommonMethods.infoLog("location: $state")
                            PreferenceConnector.writeString(this@DashboardActivity,
                                getString(R.string.state), state)
                            checkUserLocation()
                        }
                        //  CommonMethods.infoLog("location details " + address + " " + city + " " +
                    //  state + " " + country + " " + postalCode + " " + knownName);
                    } catch (e: IOException) {
                        e.printStackTrace()
                        CommonMethods.errorLog("location details exception $e")
                    }
                }
            }.addOnFailureListener { e: Exception -> CommonMethods.errorLog("location exception $e") }
        } else {
            Permissions.requestPermissionForAccessLocation(this@DashboardActivity)
        }
    }

    private fun checkUserLocation(): Boolean {
        val isValidLocation: Boolean
        val listStateNames = listOf(*statesNames)
        val state = PreferenceConnector.readString(this, getString(R.string.state), "")
        isValidLocation = if (listStateNames.contains(state)) {
            openLocationDialog(state)
            false
        } else { true }
        return isValidLocation
    }

    private fun openLocationDialog(stateName: String) {
        dialogShowLocation = Dialog(this@DashboardActivity)
        if (dialogShowLocation!!.window != null) {
            dialogShowLocation!!.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)
            dialogShowLocation!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogShowLocation!!.setContentView(R.layout.layout_dialog_location_details)
            //dialogShowLocation.getWindow().setGravity(Gravity.TOP);
            dialogShowLocation!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialogShowLocation!!.window!!.setDimAmount(0.70f)
            dialogShowLocation!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogShowLocation!!.setCancelable(false)
            dialogShowLocation!!.show()
            initDialogCheckLocationUi()
            hideStatusBarForDialog(dialogShowLocation!!)
            val assam = tvDialogLocationStateAssam!!.text.toString()
            val odisha = tvDialogLocationStateOdisha!!.text.toString()
            val nagaland = tvDialogLocationStateNagaland!!.text.toString()
            val tamilnadu = tvDialogLocationStateTamilNadu!!.text.toString()
            val telangana = tvDialogLocationStateTelangana!!.text.toString()
            if (stateName.equals(assam, ignoreCase = true)) {
                tvDialogLocationStateAssam!!.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_place, 0, 0, 0)
            } else if (stateName.equals(odisha, ignoreCase = true)) {
                tvDialogLocationStateOdisha!!.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_place, 0, 0, 0)
            } else if (stateName.equals(nagaland, ignoreCase = true)) {
                tvDialogLocationStateNagaland!!.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_place, 0, 0, 0)
            } else if (stateName.equals(tamilnadu, ignoreCase = true)) {
                tvDialogLocationStateTamilNadu!!.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_place, 0, 0, 0)
            } else if (stateName.equals(telangana, ignoreCase = true)) {
                tvDialogLocationStateTelangana!!.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_place, 0, 0, 0)
            }
            rlDialogLocationDamn!!.setOnClickListener { v: View? ->
                dialogShowLocation!!.dismiss()
                finish()
            }
        }
    }

    private fun initDialogCheckLocationUi() {
        rlDialogLocationDamn = dialogShowLocation!!.findViewById(R.id.rlDialogLocationDamn)
        tvDialogLocationStateAssam =
            dialogShowLocation!!.findViewById(R.id.tvDialogLocationStateAssam)
        tvDialogLocationStateNagaland =
            dialogShowLocation!!.findViewById(R.id.tvDialogLocationStateNagaland)
        tvDialogLocationStateOdisha =
            dialogShowLocation!!.findViewById(R.id.tvDialogLocationStateOdisha)
        tvDialogLocationStateTamilNadu =
            dialogShowLocation!!.findViewById(R.id.tvDialogLocationStateTamilNadu)
        tvDialogLocationStateTelangana =
            dialogShowLocation!!.findViewById(R.id.tvDialogLocationStateTelangana)
    }

    private fun checkDailyReward() {
        if (!PreferenceConnector.readBoolean(this@DashboardActivity,
                getString(R.string.is_reward_collected), false)) {
        //    openRewardClaimPopUp()
        }
    }

    private fun initializeUi() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation!!.itemIconTintList = null
        ivProfile = findViewById(R.id.ivProfile)
        ivPlay = findViewById(R.id.ivPlay)
        ivWallet = findViewById(R.id.ivWallet)
        tvProfile = findViewById(R.id.tvProfile)
        tvPlay = findViewById(R.id.tvPlay)
        tvWallet = findViewById(R.id.tvWallet)
        profileLayout = findViewById(R.id.profile_layout)
        playLayout = findViewById(R.id.play_layout)
        walletLayout = findViewById(R.id.wallet_layout)
    }

    private fun initializeListeners() {
        bottomNavigation!!.setOnNavigationItemSelectedListener(this)
        profileLayout!!.setOnClickListener{
            ivProfile!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.profile_icon))
            ivPlay!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.monkey_headnav1))
            ivWallet!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.wallet_icon1))
            tvProfile!!.background = AppCompatResources.getDrawable(this,R.drawable.bluebg_curved)
            tvProfile!!.setTextColor(ContextCompat.getColor(this,R.color.white))
            tvPlay!!.background = null
            tvPlay!!.setTextColor(ContextCompat.getColor(this,R.color.black))
            tvWallet!!.background = null
            tvWallet!!.setTextColor(ContextCompat.getColor(this,R.color.black))
            bottomNavigation!!.selectedItemId = R.id.action_profile
        }
        playLayout!!.setOnClickListener {
            ivProfile!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.profile_icon1))
            ivPlay!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.monkey_headnav))
            ivWallet!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.wallet_icon1))
            tvProfile!!.background = null
            tvProfile!!.setTextColor(ContextCompat.getColor(this,R.color.black))
            tvPlay!!.background = AppCompatResources.getDrawable(this,R.drawable.bluebg_curved)
            tvPlay!!.setTextColor(ContextCompat.getColor(this,R.color.white))
            tvWallet!!.background = null
            tvWallet!!.setTextColor(ContextCompat.getColor(this,R.color.black))
            bottomNavigation!!.selectedItemId = R.id.action_play
        }
        walletLayout!!.setOnClickListener {
            ivProfile!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.profile_icon1))
            ivPlay!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.monkey_headnav1))
            ivWallet!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.wallet_icon))
            tvProfile!!.background = null
            tvProfile!!.setTextColor(ContextCompat.getColor(this,R.color.black))
            tvPlay!!.background = null
            tvPlay!!.setTextColor(ContextCompat.getColor(this,R.color.black))
            tvWallet!!.background = AppCompatResources.getDrawable(this,R.drawable.bluebg_curved)
            tvWallet!!.setTextColor(ContextCompat.getColor(this,R.color.white))
            bottomNavigation!!.selectedItemId = R.id.action_wallet
        }
    }

   override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when (item.itemId) {
            R.id.action_profile -> if (!a) {
                selectedMenuTitle = getString(R.string.profile)
                fragment = DashboardProfileFragment()
                a = true
                b = false
                c = false
                backCount = 1
            }
            R.id.action_play -> if (!b) {
                selectedMenuTitle = getString(R.string.play)
                fragment = DashboardPlayFragment()
                a = false
                b = true
                c = false
                backCount = 0
            }
            R.id.action_wallet -> if (!c) {
                selectedMenuTitle = getString(R.string.wallet)
                fragment =  DashboardWalletFragment();
                a = false
                b = false
                c = true
                backCount = 1
            }
            else -> {}
        }
        if (fragment != null) {
            openFragment(fragment!!)
        }
        return true
    }

    fun selectWalletFragment() { bottomNavigation!!.selectedItemId = R.id.action_wallet }

    fun selectPlayFragment() { playLayout!!.performClick() }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashboardFragmentContainer, fragment,selectedMenuTitle)
        fragmentTransaction.setTransitionStyle(R.style.Theme_Transparent)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (backCount == 0) {
            val s =
                PreferenceConnector.readString(this, getString(R.string.normalquiz_table_id), "")
            if (!s.equals("", ignoreCase = true)) {
                val dialogExitApp = Dialog(this@DashboardActivity)
                if (dialogExitApp.window != null) {
                    setDialogAttributes(dialogExitApp)
                    dialogExitApp.setContentView(R.layout.layout_custom_dialog)
                    hideStatusBarForDialog(dialogExitApp)
                    val rlCustomDialog = dialogExitApp.findViewById<RelativeLayout>(R.id.rlCustomDialog)
                    rlCustomDialog.background = resources.getDrawable(R.drawable.bg_white_curve)
                    val btnCustomDialogCancel =
                        dialogExitApp.findViewById<Button>(R.id.btnCustomDialogCancel)
                    btnCustomDialogCancel.visibility = View.VISIBLE
                    btnCustomDialogCancel.setOnClickListener { v: View? -> dialogExitApp.dismiss() }
                    val btnCustomDialogOk =
                        dialogExitApp.findViewById<Button>(R.id.btnCustomDialogOk)
                    btnCustomDialogOk.text = "EXIT"
                    btnCustomDialogOk.setTextColor(resources.getColor(R.color.dark_orange))
                    btnCustomDialogOk.setOnClickListener { v: View? ->
                        dialogExitApp.dismiss()
                        finish()
                    }
                    val tvCustomDialogHeading = dialogExitApp.findViewById<TextView>(R.id.tvCustomDialogHeading)
                    tvCustomDialogHeading.text = "Exit from app"
                    tvCustomDialogHeading.setTextColor(resources.getColor(R.color.dark_orange))
                    tvCustomDialogHeading.visibility = View.INVISIBLE
                    val tvCustomDialogDesc = dialogExitApp.findViewById<TextView>(R.id.tvCustomDialogDesc)
                    tvCustomDialogDesc.text = getString(R.string.close_application)
                    tvCustomDialogDesc.setTextColor(resources.getColor(R.color.dark_orange))
                    dialogExitApp.show()
                    removeDialogAttributes(dialogExitApp)
                    hideStatusBarForDialog(dialogExitApp)
                }
            } else {
                finish()
            }
        } else {
            bottomNavigation!!.selectedItemId = R.id.action_play
            backCount = 0
        }
    }

    override fun timerCount(time: String?, millisUntilFinished: Long) {
        val playItem: MenuItem = bottomNavigation!!.menu.findItem(R.id.action_play)
        if (BuildConfig.PRO_VERSION) {
            if (selectedMenuTitle != "Play") {
                timer = if (millisUntilFinished / 1000 < 10) {
                    "00:0" + millisUntilFinished / 1000
                } else {
                    "00:" + millisUntilFinished / 1000
                }
                playItem.title = timer
            } else {
                playItem.title = getString(R.string.play)
            }
        } else {
            playItem.title = getString(R.string.play)
        }
    }

    override fun transitionOnUserDetailsOrTimerUpdate(
        millisUntilFinished: Long, enoughUsersAndGotQuestion: Boolean) {}

    private fun getProfileData(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id))
        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.getProfile(userdata)
        requestCall.enqueue(object : Callback<LoginResponseOtp> {
            override fun onFailure(call: Call<LoginResponseOtp>, t: Throwable) {
                closeProgressbar()
                Log.i("getProfile","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponseOtp>, response: Response<LoginResponseOtp>) {
                if(response.isSuccessful){
                    closeProgressbar()
                    val resp= response.body()
                    Log.i("getProfile","ResponseLang:// ${resp.toString()}")

                    if(resp!!.status == 200){
                        mobilenumber = resp.responseData.mobileNumber
                        val name = resp.responseData.firstName
                        val userName = resp.responseData.userName
                        PreferenceConnector.writeString(this@DashboardActivity,
                            getString(R.string.user_mobile_number), mobilenumber)
                        PreferenceConnector.writeString(this@DashboardActivity,
                            getString(R.string.user_full_name), name)
                        PreferenceConnector.writeString(this@DashboardActivity,
                            getString(R.string.user_name), userName)
                        if (resp.responseData.photo!=""){
                            PreferenceConnector.writeString(this@DashboardActivity,
                                getString(R.string.user_profile_pic), EndPoints.Base_UrlImage+resp.responseData.photo)
                        }
                        if (resp.responseData.isReferredUser){ referralCheck() }
                    } else{
                        closeProgressbar()
                        Toasty.info(applicationContext,resp.message, Toasty.LENGTH_SHORT).show()
                    }
                } else{
                    closeProgressbar()
                    Log.i("getProfile","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(applicationContext, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(applicationContext, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(applicationContext, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(applicationContext, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun referralCheck(){
            val userdata= GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id))
            val service = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
            val call: Call<ResponseBody> = service.Referral(userdata)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        val result: String = response.body()?.charStream()?.readText().toString()
                        val res = response.errorBody()?.charStream()?.readText().toString()
                        if (result != "null") {
                            val jsonobj = JSONObject(result)
                            Log.i("referral check :", "Response $result")
                        } else {
                            val jsonobj = JSONObject(res)
                            val mes = jsonobj.getString("message")
                            if (mes != "User must add money and play atleast two games ") {
                                Toasty.warning(applicationContext, mes, Toasty.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("referral check :", e.toString())
                        Toasty.warning(applicationContext, "Oops , Something went wrong",
                            Toasty.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("referral check :", t.toString())
                    Toasty.warning(applicationContext, "Request Failed",
                        Toasty.LENGTH_SHORT).show()
                }
            })
    }

    private fun redirectStore(updateUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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
    override fun onPause() {
        super.onPause()
        internetchecker.stop()
        if (timerNotificationShow != null) {
            timerNotificationShow!!.cancel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("StaticFieldLeak")
    internal open inner class RetrieveApkTask : AsyncTask<String?, Void?, Void?>() {
        override fun doInBackground(vararg params: String?): Void? {
            return try {
                val downloadUrl: String = if (BuildConfig.PRO_VERSION) { APK_DOWNLOAD_URL_PRO
                } else { APK_DOWNLOAD_URL_FREE }
                Utils().downloadApk(this@DashboardActivity, downloadUrl)
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toasty.success(this, "Payment is successful", Toasty.LENGTH_SHORT).show()
        if (p1 != null) {
            val bundle = p1.data
            Log.e("paymentSuccess","Data: $bundle")
            Log.e("paymentData",p1.toString())
            val  fragment : DashboardWalletFragment = this.supportFragmentManager.findFragmentByTag("Wallet") as DashboardWalletFragment
            fragment.updatePaymentStatus(p1.paymentId)
            // plistener.onPaymentSuccess(p1)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        if (p2 != null) {
            val bundle = p2.data
            Log.e("paymentSuccess","Data: $bundle")
            Log.e("paymentData",p2.toString())
            val  fragment : DashboardWalletFragment = this.supportFragmentManager.findFragmentByTag("Wallet") as DashboardWalletFragment
            fragment.updatePaymentStatus("")
            // plistener.onPaymentSuccess(p1)
        }
        Toasty.warning(this, "Payment failed", Toasty.LENGTH_SHORT).show()
    }

}

