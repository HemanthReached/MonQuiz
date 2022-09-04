package com.monquiz.ui

import android.widget.LinearLayout
import com.mukesh.OtpView
import android.widget.TextView
import android.os.Bundle
import android.view.WindowManager
import com.monquiz.R
import android.text.TextWatcher
import android.text.Editable
import android.annotation.SuppressLint
import android.view.inputmethod.EditorInfo
import android.os.CountDownTimer
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.monquiz.model.inputdata.LoginOTpSend_Data
import com.monquiz.model.verifyotpmodel.VerifyOTP_Data
import com.monquiz.network.InternetStateChecker
import com.monquiz.response.verifyotp_packageresponse.VerifyOtp_Response
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.login.LoginResponseOtp
import com.monquiz.utils.*
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.util.*

class OTPVerificationActivity : BaseActivity(), View.OnClickListener {
  //  private var llOtpVerify: LinearLayout? = null
    private var btnOtpVerifyDone: Button? = null

    //private FirebaseAuth fbAuth;
    //private DatabaseReference databaseReference;
    private var ivOtpVerifyBack: ImageView? = null
    private var otpViewOtpVerify: OtpView? = null
    private var tvOtpVerifyTimer: TextView? = null
   // private var tvOtpVerifyResend: TextView? = null
 //  private var tvOtpVerifyCallMe: TextView? = null
    private var mobileNumber: String? = ""
    private var referralCode: String? = ""
    private var otp: String? = ""
    private var countrycode: String? = ""
    var token = ""
    private lateinit var timer : CountDownTimer
    private val verificationCode = ""
    private lateinit var internetchecker : InternetStateChecker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions*/
        setContentView(R.layout.activity_otpverification)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        dataFromIntent
        token = FirebaseToken.getKey(this)
        if (token.isEmpty()){
            if (internetchecker.isConnected){
                token = AppUtils.getFirebaseToken()
            }else{
                Toasty.error(this, "Sorry! Not connected to internet",
                    Toasty.LENGTH_SHORT).show()
            }
        }
        initializeUi()
        initializeListeners()
        otpTimer()
        val bundle = intent.extras
        if (bundle != null){
            mobileNumber = bundle.getString(getString(R.string.intent_mobile_number))
            referralCode = bundle.getString(getString(R.string.intent_referral_code))
            countrycode = bundle.getString("c_Code")
        }
         //sendVerificationCode();
        //verificationCode(otp)
    }

    private fun enableButtonNext() {
        otpViewOtpVerify!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val textLength = Objects.requireNonNull(otpViewOtpVerify!!.text)!!.length
                if (textLength == 6) {
                    btnOtpVerifyDone!!.alpha = 1f
                    btnOtpVerifyDone!!.isEnabled = true
                    AppUtils.hideKeyboard(this@OTPVerificationActivity,otpViewOtpVerify!!)
                } else {
                    btnOtpVerifyDone!!.alpha = 0.6f
                    btnOtpVerifyDone!!.isEnabled = false
                }
            }
        })
    }

    private val dataFromIntent: Unit
        get() {
            val bundle: Bundle = intent.extras!!
            mobileNumber = bundle.getString(getString(R.string.intent_mobile_number))
            referralCode = bundle.getString(getString(R.string.intent_referral_code))
            // otp = bundle.getString(Constants.Otp)
        }

    @SuppressLint("SetTextI18n")
    private fun initializeUi() {
        btnOtpVerifyDone = findViewById(R.id.btnOtpVerifyDone)
        tvOtpVerifyTimer = findViewById(R.id.tvOtpVerifyTimer)
        ivOtpVerifyBack = findViewById(R.id.ivOtpVerifyBack)
        otpViewOtpVerify = findViewById(R.id.otpViewOtpVerify)
        btnOtpVerifyDone!!.isEnabled = false
        btnOtpVerifyDone!!.alpha = 0.6f
        otpViewOtpVerify!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val verificationCode = Objects.requireNonNull(otpViewOtpVerify!!.text).toString()
                if (verificationCode.isNotEmpty()) verifyVerificationCode(otpViewOtpVerify!!.text.toString())
                    handled = true
            }
            handled
        }
        otpViewOtpVerify!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                AppUtils.hideKeyboard(this,v)
            }
        }
        val tvOtpVerifyVerificationText: TextView = findViewById(R.id.tvOtpVerifyVerificationText)
        val stringBuilderText = StringBuilder(mobileNumber)
        stringBuilderText.insert(3, "\u0020")
        tvOtpVerifyVerificationText.text = getString(R.string.verification_code_text) + " " + stringBuilderText
        enableButtonNext()
    }

    private fun initializeListeners() {
        ivOtpVerifyBack!!.setOnClickListener(this)
      //  tvOtpVerifyResend!!.setOnClickListener(this)
      //  tvOtpVerifyCallMe!!.setOnClickListener(this)
        btnOtpVerifyDone!!.setOnClickListener(this)
        tvOtpVerifyTimer!!.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun otpTimer() {
        tvOtpVerifyTimer!!.visibility = View.VISIBLE
      //  llOtpVerify!!.visibility = View.GONE
        timer = object : CountDownTimer(30000, 1) {
            override fun onTick(millisUntilFinished: Long) {
                tvOtpVerifyTimer!!.text = "Re-send code in " + millisUntilFinished / 1000
                tvOtpVerifyTimer!!.isEnabled = false
            }
            override fun onFinish() {
                tvOtpVerifyTimer!!.text = "Re-send Code"
                tvOtpVerifyTimer!!.isEnabled = true
               // llOtpVerify!!.visibility = View.VISIBLE
            }
        }
        timer.start()
    }

    private fun goToUserNameActivity(mobileNumber: String, isReferralCodeEntered: Boolean) {
        closeProgressbar()
        val userNameIntent = Intent(this@OTPVerificationActivity, UsernameActivity::class.java)
        userNameIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        userNameIntent.putExtra(getString(R.string.intent_mobile_number), mobileNumber)
        userNameIntent.putExtra(getString(R.string.is_valid_referral), isReferralCodeEntered)
        userNameIntent.putExtra(getString(R.string.user_referral_code), referralCode)
        userNameIntent.putExtra("cCode",countrycode)
        startActivity(userNameIntent)
        finish()
    }

    private fun launchDashboardActivity() {
        closeProgressbar()
        val bottomNavigationIntent = Intent(this, DashboardActivity::class.java)
        bottomNavigationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(bottomNavigationIntent)
        finish()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivOtpVerifyBack -> onBackPressed()
            R.id.tvOtpVerifyTimer -> {
                if (countrycode != null && mobileNumber != null && referralCode != null){
                    login(countrycode!!,mobileNumber!!,referralCode!!)
                    otpTimer()
                }else{ Toasty.error(this,"Failed to resend OTP",Toasty.LENGTH_SHORT).show() }
            }
            R.id.btnOtpVerifyDone -> {
                val verificationCode = Objects.requireNonNull(otpViewOtpVerify!!.text).toString()
                if (verificationCode.isNotEmpty()) verifyVerificationCode(otpViewOtpVerify!!.text.toString())
            }
            else -> {}
        }
    }
    private fun verifyVerificationCode(code: String) {
        try {
         signInWithPhoneAuthCredential(code)
        } catch (exception: IllegalArgumentException) {
            Toasty.warning(this,getString(R.string.invalid_otp_please_try_after_some_time)
                ,Toasty.LENGTH_SHORT).show()
            CommonMethods.errorLog("verifyVerificationCode " + exception.message)
        }
    }

    private fun signInWithPhoneAuthCredential(code:String) {
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= VerifyOTP_Data(countrycode!!, mobileNumber!!,code!!)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.Verify_Otp(userdata)
        requestCall.enqueue(object : Callback<VerifyOtp_Response> {
            override fun onFailure(call: Call<VerifyOtp_Response>, t: Throwable) {
                closeProgressbar()
                Log.i("otpverify","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<VerifyOtp_Response>, response: Response<VerifyOtp_Response>) {
                if(response.isSuccessful){
                    closeProgressbar()
                    val resp= response.body()
                    Log.i("otpverify","ResponseLang:// ${resp.toString()}")
                    if(resp!!.status==200){
                        if (resp.message=="OTP verified"){
                            Toasty.success(applicationContext, resp.message!!, Toasty.LENGTH_SHORT).show()
                            Handler().postDelayed({
                                // checkMobileNumber()
                                PrefsHelper().savePref(OwlizConstants.login,true)
                                PrefsHelper().savePref(OwlizConstants.token,resp!!.token)
                                PrefsHelper().savePref(OwlizConstants.user_id,resp!!.responseData!!.userId)
                                goToUserNameActivity(mobileNumber!!,false)

                            },1000)
                        }else{
                            Toasty.info(applicationContext,resp.message!!, Toasty.LENGTH_SHORT).show()
                        }
                        /*if (resp!!.message=="otp sent to your mobile number, please verify to login") {
                            Toasty.success(applicationContext, resp.message, Toasty.LENGTH_SHORT).show()
                            Handler().postDelayed({
                               // checkMobileNumber()
                                goToUserNameActivity(mobileNumber!!,false)
                            },1000)
                        } else {
                            Toasty.info(applicationContext,resp.message, Toasty.LENGTH_SHORT).show()
                        }*/
                    } else{
                        closeProgressbar()
                        Toasty.info(applicationContext,resp.message!!, Toasty.LENGTH_SHORT).show()
                    }
                } else{
                    closeProgressbar()
                    Log.i("otpverify","ResponseLangError:// ${response.errorBody().toString()}")
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

    fun login(countryCode:String,mobileNumber:String, referralCode: String){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= LoginOTpSend_Data(countryCode, mobileNumber,token,referralCode)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.login_phone(userdata)
        requestCall.enqueue(object : Callback<LoginResponseOtp> {
            override fun onFailure(call: Call<LoginResponseOtp>, t: Throwable) {
                closeProgressbar()
                Log.i("login","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponseOtp>, response: Response<LoginResponseOtp>) {
                if(response.isSuccessful){
                    closeProgressbar()
                    val resp= response.body()
                    Log.i("login","ResponseLang:// ${resp.toString()}")
                    if(resp!!.status == 200){
                        if (resp.message=="otp sent to your mobile number, please verify to login") {
                            Toasty.success(applicationContext, getString(R.string.new_otp_send_to_your_number),
                                Toasty.LENGTH_SHORT).show()
                            tvOtpVerifyTimer!!.isEnabled = false
                            timer.start()
                        } else {
                            Toasty.info(applicationContext,resp.message, Toasty.LENGTH_SHORT).show()
                        }
                    } else{ Toasty.info(applicationContext,resp.message, Toasty.LENGTH_SHORT).show() }
                } else{
                    Log.i("login","ResponseLangError:// ${response.errorBody().toString()}")
                    closeProgressbar()
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

    override fun onResume() {
        super.onResume()
        internetchecker.start()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onDestroy() {
        super.onDestroy()
        internetchecker.stop()
        if (this::timer.isInitialized){ timer.cancel() }
    }

    override fun onStop() {
        super.onStop()
        internetchecker.stop()
    }
    override fun onPause() {
        super.onPause()
        internetchecker.stop()
    }

}