package com.monquiz.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.hbb20.CountryCodePicker
import com.monquiz.R
import com.monquiz.model.inputdata.LoginOTpSend_Data
import com.monquiz.network.InternetStateChecker
import com.monquiz.response.login.LoginResponseOtp
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.utils.AppUtils
import com.monquiz.utils.FirebaseToken
import es.dmoral.toasty.Toasty
import org.jetbrains.annotations.Nullable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignUpActivity : BaseActivity(), View.OnClickListener {
    private var btnSignUpNext: Button? = null
    private var etSignUpMobileNumber: EditText? = null
    private var tvSignUpReferralCode: TextView? = null
    private var tickView : ImageView? = null
  //  private var tvSignUpTerms: TextView? = null
    private var etSignUpReferralCode: EditText? = null
    private var llSignUpReferralCode: LinearLayout? = null
    private var ivSignUpCancelReferralCode: ImageView? = null
    private var signUpCountryCodePicker: CountryCodePicker? = null
    private lateinit var internetchecker : InternetStateChecker
    var token = ""

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
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
        checkReferralCode()
        addFilters()
       /* tvSignUpTerms = findViewById(R.id.tvSignUpTerms)
        val ss = SpannableString((getString(R.string.by_clicking_next)
                    + getString(R.string.terms_conditions)).toString() + " and " + getString(R.string.privacy_policy))
        ss.setSpan(myClickableSpan(1), getString(R.string.by_clicking_next).length,
            getString(R.string.by_clicking_next).length + getString(R.string.terms_conditions).length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(myClickableSpan(2), ss.length - getString(R.string.privacy_policy).length,
            ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSignUpTerms!!.text = ss
        tvSignUpTerms!!.movementMethod = LinkMovementMethod.getInstance()*/
    }

    private fun addFilters() {
        val filterArray: Array<InputFilter?> = arrayOfNulls<InputFilter>(2)
        filterArray[0] = InputFilter.LengthFilter(8)
        filterArray[1] = InputFilter.AllCaps()
        etSignUpReferralCode!!.filters = filterArray
    }

    private fun checkReferralCode() {
        etSignUpReferralCode!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                val textLength: Int = etSignUpReferralCode!!.text.length
                if (textLength >= 6) {
                    if (!isReferalValid(etSignUpReferralCode!!.text.toString())) {
                        Toasty.error(this@SignUpActivity,
                            "Referral code should contain only alphabets and numbers",
                            Toasty.LENGTH_SHORT).show()
                       // showToast("code should be in alphanumric")
                    }
                }
            }
        })
        etSignUpMobileNumber!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val textLength: Int = etSignUpMobileNumber!!.text.length
                if (textLength == 10) {
                    btnSignUpNext!!.alpha = 1f
                    btnSignUpNext!!.isEnabled = true
                    tickView!!.visibility = View.VISIBLE
                    AppUtils.hideKeyboard(this@SignUpActivity,etSignUpMobileNumber!!)
                } else {
                    btnSignUpNext!!.alpha = 0.6f
                    btnSignUpNext!!.isEnabled = false
                    tickView!!.visibility = View.GONE
                }
            }
        })
    }

    private fun isReferalValid(num: String): Boolean {
        val EMAIL_PATTERN = "^[a-zA-Z0-9]+$"
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(num)
        return matcher.matches()
    }

    private fun initializeUi() {
        btnSignUpNext = findViewById(R.id.btnSignUpNext)
        btnSignUpNext!!.isEnabled = false
        etSignUpMobileNumber = findViewById(R.id.etSignUpMobileNumber)
        tvSignUpReferralCode = findViewById(R.id.tvSignUpReferralCode)
        llSignUpReferralCode = findViewById(R.id.llSignUpReferralCode)
        etSignUpReferralCode = findViewById(R.id.etSignUpReferralCode)
        signUpCountryCodePicker = findViewById(R.id.signUpCountryCodePicker)
        ivSignUpCancelReferralCode = findViewById(R.id.ivSignUpCancelReferralCode)
        tickView = findViewById(R.id.tickIcon)
        etSignUpMobileNumber!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                checkMobileNumber()
                handled = true
            }
            handled
        }
        etSignUpMobileNumber!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !etSignUpReferralCode!!.isFocused){
                AppUtils.hideKeyboard(this,v)
            }
        }
        etSignUpReferralCode!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !etSignUpMobileNumber!!.isFocused){
                AppUtils.hideKeyboard(this,v)
            }
        }
    }

    private fun initializeListeners() {
        btnSignUpNext!!.setOnClickListener(this)
        tvSignUpReferralCode!!.setOnClickListener(this)
        ivSignUpCancelReferralCode!!.setOnClickListener(this)
        etSignUpMobileNumber!!.hint = getString(R.string.enter_mobile_number)
      //  signUpCountryCodePicker!!.contentColor = resources.getColor(R.color.white)
        //   signUpCountryCodePicker.registerCarrierNumberEditText(etSignUpMobileNumber);
        //   signUpCountryCodePicker.setPhoneNumberValidityChangeListener(isValidNumber ->
        //   isValidMobileNumber = isValidNumber);
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSignUpNext -> checkMobileNumber()
            R.id.tvSignUpReferralCode -> {
                llSignUpReferralCode!!.visibility = View.VISIBLE
                tvSignUpReferralCode!!.visibility = View.GONE
                etSignUpReferralCode!!.isFocusable = true
                etSignUpReferralCode!!.isFocusableInTouchMode = true
                etSignUpReferralCode!!.isClickable = true
                etSignUpReferralCode!!.requestFocus()
            }
            R.id.ivSignUpCancelReferralCode -> {
                etSignUpReferralCode!!.setText("")
                llSignUpReferralCode!!.visibility = View.GONE
                tvSignUpReferralCode!!.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

     private fun checkMobileNumber() {
        val mobileNumber: String = etSignUpMobileNumber!!.text.toString()
        val countryCode: String = signUpCountryCodePicker!!.selectedCountryCode
        val referralCode: String = etSignUpReferralCode!!.text.toString()
        if (mobileNumber.isEmpty()) {
            Toasty.warning(this,
                getString(R.string.please_enter_valid_mobile_number),
                Toasty.LENGTH_SHORT).show()
         //   showToast(getString(R.string.please_enter_valid_mobile_number))
        } else if (mobileNumber.length < 10 || mobileNumber.length > 10) {
            Toasty.warning(this,
                getString(R.string.please_enter_valid_mobile_number),
                Toasty.LENGTH_SHORT).show()
          //  showToast(getString(R.string.please_enter_valid_mobile_number))
        } else {
            if (referralCode.isEmpty()) {
        /*        var mobileNumber = mobileNumber
                mobileNumber = "$mobileNumber"
                val otpVerificationIntent = Intent(applicationContext, OTPVerificationActivity::class.java)
                val bundle = Bundle()
                bundle.putString(getString(R.string.intent_mobile_number), mobileNumber)
                bundle.putString(getString(R.string.intent_referral_code), referralCode)
              //  bundle.putString(Constants.Otp,resp!!.responseData.otp.toString())
               // bundle.putString("countrycode",resp!!.responseData.countryCode.toString())

                otpVerificationIntent.putExtras(bundle)
                startActivity(otpVerificationIntent)*/
                    login(countryCode, mobileNumber, referralCode)
            } else {
                if (referralCode.length < 6 || referralCode.length > 8) {
                    Toasty.error(this,
                        getString(R.string.please_enter_valid_referral_code),
                        Toasty.LENGTH_SHORT).show()
                   // showToast(getString(R.string.please_enter_valid_referral_code))
                } else {
               /*     var mobileNumber = mobileNumber
                    mobileNumber = "$mobileNumber"
                    val otpVerificationIntent = Intent(applicationContext, OTPVerificationActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString(getString(R.string.intent_mobile_number), mobileNumber)
                    bundle.putString(getString(R.string.intent_referral_code), referralCode)
                    //  bundle.putString(Constants.Otp,resp!!.responseData.otp.toString())
                    // bundle.putString("countrycode",resp!!.responseData.countryCode.toString())

                    otpVerificationIntent.putExtras(bundle)
                    startActivity(otpVerificationIntent)*/
                        login(countryCode, mobileNumber, referralCode)
                }
            }
        }
    }

    private fun gotoOTPVerificationScreen(countryCode: String,
        mobileNumber: String, referralCode: String) {
        val mobNumber = "+$countryCode$mobileNumber"
        val otpVerificationIntent = Intent(this, OTPVerificationActivity::class.java)
        val bundle = Bundle()
        bundle.putString(getString(R.string.intent_mobile_number), mobNumber)
        bundle.putString(getString(R.string.intent_referral_code), referralCode)
        otpVerificationIntent.putExtras(bundle)
        startActivity(otpVerificationIntent)
    }

    private fun openTermsAndConditions(key: String) {
        val intent = Intent(this, ShowTextDocumentActivity::class.java)
        intent.putExtra("keyTermsAndCondition", key)
        startActivity(intent)
    }

    fun login(countryCode:String,mobileNumber:String, referralCode: String){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= LoginOTpSend_Data(countryCode, mobileNumber,token,referralCode)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.login_phone(userdata)
        requestCall.enqueue(object : Callback<LoginResponseOtp> {
            override fun onFailure(call: Call<LoginResponseOtp>, t: Throwable) {
                closeProgressbar()
                Log.i("logIn","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponseOtp>, response: Response<LoginResponseOtp>) {
                if(response.isSuccessful){
                    closeProgressbar()
                    val resp= response.body()
                    Log.i("logIn","Response:// ${resp.toString()}")

                    if(resp!!.status == 200){
                        if (resp.message == "otp sent to your mobile number, please verify to login") {
                            Toasty.success(applicationContext,
                                "OTP sent to your mobile number, please verify to login",
                                Toasty.LENGTH_SHORT).show()

                            Handler().postDelayed({
                                //checkMobileNumber()
                                val mobNumber = mobileNumber
                                val otpVerificationIntent = Intent(applicationContext, OTPVerificationActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString(getString(R.string.intent_mobile_number), mobileNumber)
                                bundle.putString(getString(R.string.intent_referral_code), referralCode)
                                bundle.putString("c_Code",countryCode)
                               // bundle.putString(Constants.Otp,resp!!.responseData.otp.toString())
                                bundle.putString("countrycode", resp!!.responseData.countryCode)
                                otpVerificationIntent.putExtras(bundle)
                                startActivity(otpVerificationIntent)

                               /*val mobileNumber = "+${resp!!.data.countryCode}${resp!!.data.mobileNumber}"
                                val otpVerificationIntent =
                                    Intent(applicationContext, OTPVerificationActivity::class.java)
                                val bundle = Bundle()
                                bundle.putString(getString(R.string.intent_mobile_number), mobileNumber)
                                bundle.putString(getString(R.string.intent_referral_code), referralCode)
                                bundle.putString(Constants.Otp,resp!!.data.otp.toString())
                                otpVerificationIntent.putExtras(bundle)
                                startActivity(otpVerificationIntent)*/
                                   // var userdat= resp.data.userLogin.toMutableList()
    /*
                                        val intent = Intent(applicationContext, OTPVerificationActivity::class.java)
                                intent.putExtra(Constants.Otp,resp!!.data.otp)
                                        startActivity(intent)
                                        finish()*/

                            },1000)
                        } else {
                            Toasty.info(applicationContext,resp.message, Toasty.LENGTH_SHORT).show()
                        }
                    } else{ Toasty.info(applicationContext,resp.message, Toasty.LENGTH_SHORT).show() }
                } else{
                    Log.i("logIn","ErrorResponse:// ${response.errorBody().toString()}")
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
    override fun onDestroy() {
        super.onDestroy()
        internetchecker.stop()
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