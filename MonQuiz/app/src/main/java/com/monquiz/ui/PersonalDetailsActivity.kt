package com.monquiz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.monquiz.R
import com.monquiz.network.RetrofitApi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.bankdetails.input.BankDetailsInput
import com.monquiz.response.bankdetails.response.BankDetailsResponse
import com.monquiz.response.bankdetails.response.GetBankDetailsResponse
import com.monquiz.response.bankdetails.response.ResponseData
import com.monquiz.response.wallet.input.WalletInput
import com.monquiz.response.wallet.input.verifyInput
import com.monquiz.response.wallet.response.VerifyResponse
import com.monquiz.utils.AppUtils
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalDetailsActivity : BaseActivity() {

    private var ivDetailsClose: ImageView? = null
    private var rlPanDetails: RelativeLayout? = null
    private var tvPanStatus : TextView? = null
    private var rlBankDetails: RelativeLayout? = null
    private var rlUserDetails : RelativeLayout? = null

    private var accountDetails : ResponseData? = null
    private var isPanVerified : Boolean = false
    private var dialogPickAvatar: View? = null
    private var dialogAccountDetails: View? = null

    private var submitBtnRedeem : TextView? = null
    private var closeViewRedeem : ImageView? = null
    private var amountET : EditText? = null
    private var accountNumberET : EditText? = null
    private var ifscNumberET : EditText? = null
    private var amountDescTv : TextView? = null

    private var submitBtn: TextView? = null
    private var closeView : ImageView? = null
    private var panNameEt : EditText? = null
    private var panNumberEt : EditText? = null

    private var panDetailsLayout : ConstraintLayout? = null
    private var redeemMoneyLayout : ConstraintLayout? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.layout_personaldetails)

        ivDetailsClose = findViewById(R.id.ivDetailsClose)
        rlBankDetails = findViewById(R.id.rlBankDetails)
        tvPanStatus = findViewById(R.id.tvPanStatus)
        rlPanDetails = findViewById(R.id.rlPanDetails)
        rlUserDetails = findViewById(R.id.rlUserDetails)
        val bottomsheet = findViewById<ConstraintLayout>(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        panDetailsLayout = findViewById(R.id.pandetais_layout)
        redeemMoneyLayout = findViewById(R.id.redeem_layout)

        ivDetailsClose!!.setOnClickListener { onBackPressed() }

        rlPanDetails!!.setOnClickListener { configurePanUI() }
        rlBankDetails!!.setOnClickListener { configureAccountUI() }
        rlUserDetails!!.setOnClickListener {
            val userNameIntent = Intent(this, UsernameActivity::class.java)
            startActivity(userNameIntent)
        }
        initDialogSelectAvatarUiElements()
        initAccountDetailsUi()
        getBankDetails()
    }

    private fun configureAccountUI() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        panDetailsLayout!!.visibility = View.GONE
        redeemMoneyLayout!!.visibility =View.VISIBLE
        amountET!!.setText("")
        if (!accountDetails!!.bankDetails.isNullOrEmpty()){
            if (accountDetails!!.bankDetails!![0].accountNumber!!.isNotEmpty()){
                accountNumberET!!.setText(accountDetails!!.bankDetails!![0].accountNumber!!)
            }
            if (accountDetails!!.bankDetails!![0].ifscCode!!.isNotEmpty()){
                ifscNumberET!!.setText(accountDetails!!.bankDetails!![0].ifscCode!!)
            }
        }
    }

    private fun configurePanUI() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        panDetailsLayout!!.visibility = View.VISIBLE
        redeemMoneyLayout!!.visibility =View.GONE
        panNameEt!!.setText("")
        panNameEt!!.setText("")
        if (!accountDetails!!.panDetails.isNullOrEmpty()){
            if (accountDetails!!.panDetails!![0].fullName!!.isNotEmpty()){
                panNameEt!!.setText(accountDetails!!.panDetails!![0].fullName!!)
            }
            if (accountDetails!!.panDetails!![0].panNumber!!.isNotEmpty()){
                panNumberEt!!.setText(accountDetails!!.panDetails!![0].panNumber!!)
            }
        }
    }

    private fun getBankDetails(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata : String = PrefsHelper().getPref(OwlizConstants.user_id)

        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.getBankDetails(WalletInput(userdata))
        requestCall.enqueue(object : Callback<GetBankDetailsResponse> {
            override fun onFailure(call: Call<GetBankDetailsResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("getBackDetails","FailureResponse $t")
                Toasty.error(this@PersonalDetailsActivity,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<GetBankDetailsResponse>, response: Response<GetBankDetailsResponse>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("getBackDetails", "Response:// $resp")
                    if (resp!!.status == 200){
                        accountDetails = resp.responseData
                      //  isPanVerified = resp.responseData!!.panDetails!![0].panStatus!!
                        onSuccess()
                    }
                } else{
                    closeProgressbar()
                    Log.i("getBackDetails","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(this@PersonalDetailsActivity, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(this@PersonalDetailsActivity, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(this@PersonalDetailsActivity, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(this@PersonalDetailsActivity, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun onSuccess() {
        if (accountDetails != null){
            if (!accountDetails!!.panDetails.isNullOrEmpty()){
                isPanVerified = accountDetails!!.panDetails!![0].panStatus!!
            }
        }
        if (isPanVerified) {
            tvPanStatus!!.text = "Verified"
            tvPanStatus!!.setTextColor(resources.getColor(R.color.colorGreen))
            tvPanStatus!!.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,
                null,null)
        }else{
            tvPanStatus!!.text = "Not Verified"
            tvPanStatus!!.setTextColor(resources.getColor(R.color.colorAlert))
            tvPanStatus!!.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,
                AppCompatResources.getDrawable(this,R.drawable.alert_icon),null)
        }
    }

    private fun initDialogSelectAvatarUiElements() {
         submitBtn = findViewById(R.id.submit_btn)
         closeView = findViewById(R.id.close_popup)
         panNameEt = findViewById(R.id.nameet)
         panNumberEt = findViewById(R.id.cardnumberet)
        val popupdesctv = findViewById<TextView>(R.id.popupdesctv)
        val string = popupdesctv.text
        val spannable = SpannableString(string)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
            0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        popupdesctv.movementMethod = LinkMovementMethod.getInstance()
        popupdesctv.text = spannable
        panNameEt!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !panNumberEt!!.isFocused){
                AppUtils.hideKeyboard(this,v)
            }
        }
        panNumberEt!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !panNameEt!!.isFocused){
                AppUtils.hideKeyboard(this,v)
            }
        }
        submitBtn!!.setOnClickListener {
            if (panNameEt!!.text.trim().toString().isNotEmpty()){
                    if (panNumberEt!!.text.trim().toString().isNotEmpty()){
                        verifyPanDetails(panNameEt!!.text.trim().toString(),
                            panNumberEt!!.text.trim().toString())
                    }else{
                        Toasty.warning(this,"please enter Pan Number",
                            Toasty.LENGTH_SHORT).show()
                    }
                }else{
                    Toasty.warning(this,"please enter name on Pan Card",
                        Toasty.LENGTH_SHORT).show()
                }
        }
        closeView!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (panNameEt!!.isFocused){
                AppUtils.hideKeyboard(this,panNameEt!!)
            }else if (panNumberEt!!.isFocused){
                AppUtils.hideKeyboard(this,panNumberEt!!)
            }
        }
    }

    private fun initAccountDetailsUi() {
        submitBtnRedeem = findViewById<TextView>(R.id.submit_btn_redeem1)
        closeViewRedeem = findViewById<ImageView>(R.id.close_popup_redeem)
        amountDescTv = findViewById<TextView>(R.id.amountdesctv)
        amountET = findViewById<EditText>(R.id.amountet)
        accountNumberET = findViewById<EditText>(R.id.accnumberet)
        ifscNumberET = findViewById<EditText>(R.id.ifscnumberet)
        amountDescTv!!.visibility = View.GONE
        amountET!!.visibility = View.GONE
        accountNumberET!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !ifscNumberET!!.isFocused){
                AppUtils.hideKeyboard(this,v)
            }
        }
        ifscNumberET!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !accountNumberET!!.isFocused){
                AppUtils.hideKeyboard(this,v)
            }
        }
        submitBtnRedeem!!.setOnClickListener {
            val accnumber = accountNumberET!!.text.trim().toString()
            val ifsccode  = ifscNumberET!!.text.trim().toString()
            if (isValidForm(accnumber,ifsccode)){
               // if (accountDetails!!.bankDetails.isNullOrEmpty()){
                    saveBankDetails(accnumber,ifsccode)
            //}
            }
        }
        closeViewRedeem!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (accountNumberET!!.isFocused){
                AppUtils.hideKeyboard(this,accountNumberET!!)
            }else if (ifscNumberET!!.isFocused){
                AppUtils.hideKeyboard(this,ifscNumberET!!)
            }
        }
    }

    private fun isValidForm(accountNumber : String,ifscCode: String): Boolean {
        if (accountNumber == ""){
            Toasty.error(this,"Please Enter Account Number", Toasty.LENGTH_SHORT).show()
            return false
        }
        else if (accountNumber.length < 10){
            Toasty.error(this,"Account Number must have minimum of 10 digits",
                Toasty.LENGTH_SHORT).show()
            return false
        }
        else if (ifscCode == ""){
            Toasty.error(this,"Please Enter IFSC Code", Toasty.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun verifyPanDetails(name : String,panNumber : String){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata : String = PrefsHelper().getPref(OwlizConstants.user_id)

        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.verifyPan(verifyInput(userdata,name ,panNumber))
        requestCall.enqueue(object : Callback<VerifyResponse> {
            override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("verifyPan","FailureResponse $t")
                Toasty.error(this@PersonalDetailsActivity,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("verifyPan", "Response:// $resp")
                    if (resp!!.status==200){
                        isPanVerified = if (resp.responseData != null){ resp.responseData!![0].panStatus!! }else{ true }
                        //isPanVerified = resp.responseData!!.panStatus!!
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        getBankDetails()
                    }
                } else{
                    closeProgressbar()
                    Log.i("verifyPan","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(this@PersonalDetailsActivity, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(this@PersonalDetailsActivity, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(this@PersonalDetailsActivity, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(this@PersonalDetailsActivity, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun saveBankDetails(accNumber : String,ifscCode : String){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata : String = PrefsHelper().getPref(OwlizConstants.user_id)

        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.saveBankDetails(BankDetailsInput(userdata,"",accNumber ,ifscCode))
        requestCall.enqueue(object : Callback<BankDetailsResponse> {
            override fun onFailure(call: Call<BankDetailsResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("saveBankDetails","FailureResponse $t")
                Toasty.error(this@PersonalDetailsActivity,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<BankDetailsResponse>, response: Response<BankDetailsResponse>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("saveBankDetails", "Response:// $resp")
                    if (resp!!.status==200){
                        /*Toasty.success(this@PersonalDetailsActivity,"Account Details Saved",
                            Toasty.LENGTH_SHORT).show()*/
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                } else{
                    closeProgressbar()
                    Log.i("saveBankDetails","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(this@PersonalDetailsActivity, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(this@PersonalDetailsActivity, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(this@PersonalDetailsActivity, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(this@PersonalDetailsActivity, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }

}