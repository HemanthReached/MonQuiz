package com.monquiz.ui

import android.annotation.SuppressLint
import android.content.Intent
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import android.os.Bundle
import android.view.WindowManager
import com.monquiz.R
import android.view.inputmethod.EditorInfo
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.network.InternetStateChecker
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.leaderboard.input.ChecksumData
import com.monquiz.response.leaderboard.resp.CheckSum_Resp
import com.monquiz.response.wallet.input.WalletInput
import com.monquiz.response.wallet.res.GameDetails_Response
import com.monquiz.response.wallet.res.Wallet_Response
import com.monquiz.utils.*
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.TransactionManager
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.get
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class AddMoneyActivity : BaseActivity(),PaytmPaymentTransactionCallback, View.OnClickListener,
    PaymentResultWithDataListener {
    private var btnAddMoneyNext: Button? = null
    private var ivAddMoneyClose: ImageView? = null
    private var etAddMoneyAddedMoney: EditText? = null
    private var tvAddMoneyWalletBalance: TextView? = null
    private var tvAddMoneyRs10: TextView? = null
    private var tvAddMoneyRs20: TextView? = null
    private var tvAddMoneyRs30: TextView? = null
    private var tvAddMoneyRs50: TextView? = null
    private var tvAddMoneyRs100: TextView? = null
    private var orderId: String? = null
    private var transactionid :String? = null
    private lateinit var internetchecker : InternetStateChecker

    var amount = ""
    var orderId1 = ""
    var receiptId = ""
    var transactionId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.layout_add_money)
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        initUi()
        initUiListeners()
        getWalletBalance()
    }

    private fun initUiListeners() {
        ivAddMoneyClose!!.setOnClickListener(this)
        btnAddMoneyNext!!.setOnClickListener(this)
        tvAddMoneyRs10!!.setOnClickListener(this)
        tvAddMoneyRs20!!.setOnClickListener(this)
        tvAddMoneyRs30!!.setOnClickListener(this)
        tvAddMoneyRs50!!.setOnClickListener(this)
        tvAddMoneyRs100!!.setOnClickListener(this)
    }

    private fun initUi() {
        ivAddMoneyClose = findViewById(R.id.ivAddMoneyClose)
        btnAddMoneyNext = findViewById(R.id.btnAddMoneyNext)
        tvAddMoneyRs100 = findViewById(R.id.tvAddMoneyRs100)
        tvAddMoneyRs50 = findViewById(R.id.tvAddMoneyRs50)
        tvAddMoneyRs30 = findViewById(R.id.tvAddMoneyRs30)
        tvAddMoneyRs20 = findViewById(R.id.tvAddMoneyRs20)
        tvAddMoneyRs10 = findViewById(R.id.tvAddMoneyRs10)
        tvAddMoneyWalletBalance = findViewById(R.id.tvAddMoneyWalletBalance)
        etAddMoneyAddedMoney = findViewById(R.id.etAddMoneyAddedMoney)
        etAddMoneyAddedMoney!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                goNext()
                handled = true
            }
            handled
        }
        enableButtonNext()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvAddMoneyRs10 -> { setDefinedButtonValueToEditText(10.0) }
            R.id.tvAddMoneyRs20 -> { setDefinedButtonValueToEditText(20.0) }
            R.id.tvAddMoneyRs30 -> { setDefinedButtonValueToEditText(30.0) }
            R.id.tvAddMoneyRs50 -> { setDefinedButtonValueToEditText(50.0) }
            R.id.tvAddMoneyRs100 -> { setDefinedButtonValueToEditText(100.0) }
            R.id.ivAddMoneyClose -> { finish() }
            R.id.btnAddMoneyNext -> { goNext() }
        }
    }

    private fun setDefinedButtonValueToEditText(value: Double) {
        etAddMoneyAddedMoney!!.setText("" + value)
        etAddMoneyAddedMoney!!.requestFocus()
        etAddMoneyAddedMoney!!.setSelection(etAddMoneyAddedMoney!!.text.length)
    }

    private fun goNext() {
        val money = etAddMoneyAddedMoney!!.text.toString()
        if (!money.equals("", ignoreCase = true)) {
           // checksumget(money)
            amount = money
            postOrder()
        }
    }

    private fun checksumget(money: String) {
        showProgressBar(getString(R.string.loading_please_wait))

        val userid = GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id)).userId
        val checksumdata = ChecksumData(userid,money,true,"",
            "","")
        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getChecksum(checksumdata)

        requestCall.enqueue(object : Callback<CheckSum_Resp> {
            override fun onFailure(call: Call<CheckSum_Resp>, t: Throwable) {
                closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<CheckSum_Resp>, response: Response<CheckSum_Resp>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    val data = resp!!.responseData
                    Log.i("getChecksum", "checksumResponse:// $resp")
                    Log.e("ResponseData @#$$%^^^", data.toString())
                    val jsonobj = JSONObject(data.toString())
                    val orderid = jsonobj.getString("orderId")
                    transactionid = jsonobj.getString("_id")
                    val token = jsonobj.getString("transactionToken")
                    payment(token,orderid,Constants.Prod_MID,money,userid,)
                  //  Log.e("params:","Token : $token , orerid : $orderid , MerchantId : ${Constants.M_ID} , Amount : $money")
                } else{
                    closeProgressbar()
                    Log.i("getchecksum","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(applicationContext,
                            "not found", Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(applicationContext,
                            "server broken", Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(applicationContext,
                            "Bad GateWay", Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(applicationContext,
                            "unknown error", Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun enableButtonNext() {
        etAddMoneyAddedMoney!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text = etAddMoneyAddedMoney!!.text.toString()
                val textLength = text.length
                if (textLength > 0) {
                    if (text[0] == '0') {
                        etAddMoneyAddedMoney!!.setText("")
                    }
                    btnAddMoneyNext!!.alpha = 1f
                    btnAddMoneyNext!!.isEnabled = true
                } else {
                    btnAddMoneyNext!!.alpha = 0.5f
                    btnAddMoneyNext!!.isEnabled = false
                }
            }
        })
    }

    fun payment(token: String, orderid: String, mId: String, money: String, userid: String){

        val callbackUrl = "https://merchant.com/callback"
        val order = PaytmOrder(orderid,mId,token,money,callbackUrl)
        val transactionmanager = TransactionManager(order,this)
        transactionmanager.setAppInvokeEnabled(true)
       // transactionmanager.setShowPaymentUrl("https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage")
        transactionmanager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage")
        transactionmanager.startTransaction(this, 200)
    }

    private fun postResponse(money: String,paytmTranId :String ,status : String) {
        showProgressBar(getString(R.string.loading_please_wait))

        val userid = GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id)).userId
        val checksumdata = ChecksumData(userid,money,false,transactionid,paytmTranId,status)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getChecksum(checksumdata)

        requestCall.enqueue(object : Callback<CheckSum_Resp> {
            override fun onFailure(call: Call<CheckSum_Resp>, t: Throwable) {
                closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<CheckSum_Resp>, response: Response<CheckSum_Resp>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    val data = resp!!.responseData
                    Log.i("getCheckSum", "checksumResponse:// ${resp.toString()}")
                    Log.e("ResponseData @#$$%^^^", data.toString())
                } else{
                    closeProgressbar()
                    Log.i("getChecksum","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(applicationContext,
                            "not found", Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(applicationContext,
                            "server broken", Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(applicationContext,
                            "Bad GateWay", Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(applicationContext,
                            "unknown error", Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("ActivityResult ::", " Result Code $resultCode")
        if (requestCode == 200 && data != null) {
            val bundle = data.extras
            if (bundle != null) {
                val response = data.getStringExtra("response")
                val obj = JSONObject(response.toString())
                val tranamount = obj.getString("TXNAMOUNT")
                val tranid = obj.getString("TXNID")
                val transtatus = obj.getString("STATUS")
              //  if (Build.VERSION.SDK_INT >= 29){
                   // Toasty.info(this,"ActivityResult",Toasty.LENGTH_SHORT).show()
                    postResponse(tranamount,tranid,transtatus)
              //  }
                for (key in bundle.keySet()) {
                    Log.e("ActivityResult", key + " : " +
                            if (bundle[key] != null) bundle[key] else "NULL")
                }
            }
            Log.e("ActivityResult", " data " + data.getStringExtra("nativeSdkForMerchantMessage"))
            Log.e("ActivityResult", " data response - " + data.getStringExtra("response"))
        } else {
            Log.e("ActivityResult", " payment failed")
        }
    }

    override fun onTransactionResponse(p0: Bundle?) {
        Log.e("TransactionResponse ::", p0.toString())
        if (p0 != null){
            val response = p0.get("response")
            val obj = JSONObject(response.toString())
            val tranamount = obj.getString("TXNAMOUNT")
            val tranid = obj.getString("TXNID")
            val transtatus = obj.getString("STATUS")
            //Toasty.info(this,"TResp",Toasty.LENGTH_SHORT).show()
           // if (Build.VERSION.SDK_INT < 29){
            postResponse(tranamount,tranid,transtatus) // }
        }
    }

    override fun networkNotAvailable() {
        Toasty.warning(applicationContext,
            "NetworkNotAvailable", Toasty.LENGTH_SHORT).show()
        Log.e("TAG", "networkNotAvailable")
    }

    override fun onErrorProceed(p0: String?) {
        Log.e("TAG", "Error: $p0")
    }

    override fun clientAuthenticationFailed(p0: String?) {
        Toasty.warning(applicationContext,
            "clientAuthenticationFailed", Toasty.LENGTH_SHORT).show()
        Log.e("TAG", "clientAuthenticationFailed: $p0")
    }

    override fun someUIErrorOccurred(p0: String?) {
        Log.e("TAG", "someUIErrorOccurred: $p0")
    }

    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
        Log.e("TAG", "onErrorLoadingWebPage: $p0 -- $p1 -- $p2")
    }

    override fun onBackPressedCancelTransaction() {
        Log.e("TAG", "onBackPressedCancelTransaction ")
    }

    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
        Toasty.warning(applicationContext, "$p0", Toasty.LENGTH_SHORT).show()
        Log.e("TAG", "onTransactionCancel: $p0 -- $p1")
    }

    private fun postOrder(){
        showProgressBar(getString(R.string.loading_please_wait))

        val userid = GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id)).userId

        val jsonObject = JSONObject()
        jsonObject.put("amount", amount)
        jsonObject.put("userId", userid)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.createOrder(requestBody)

        requestCall.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val result: String = response.body()?.charStream()?.readText().toString()
                    val res = response.errorBody()?.charStream()?.readText().toString()
                    if (result != "null") {

                        val jsonObj = JSONObject(result)
                        if (jsonObj.getString("status") == "200") {
                            val jsonObj1 = jsonObj.getJSONObject("responseData")
                            orderId1 = jsonObj1.getString("orderId")
                            transactionId = jsonObj1.getString("_id")
                          //  receiptId = jsonObj1.getString("receiptId")
                            onSuccess()
                            closeProgressbar()
                            Log.e("response:",jsonObj.toString())
                        }
                    } else {
                        val jsonObj = JSONObject(res)
                        val mes = jsonObj.getString("message")
                        closeProgressbar()
                        Toasty.warning(applicationContext, mes, Toasty.LENGTH_SHORT).show()
                        Log.e("orderCreate :", jsonObj.toString())
                    }
                } catch (e: Exception) {
                    Log.e("orderCreate :", e.toString())
                    closeProgressbar()
                    Toasty.warning(applicationContext,
                        "something went wrong", Toasty.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("orderCreate :", t.toString())
               closeProgressbar()
                Toasty.warning(applicationContext, "Request Failed", Toasty.LENGTH_SHORT).show()
            }
        })
    }

    fun onSuccess(){

        val sAmount: String = amount
        val amount = (sAmount.toFloat() * 100).roundToInt()
        var mobileNumber = PreferenceConnector.readString(this,
            getString(R.string.user_mobile_number), "")
        if (mobileNumber.isNullOrEmpty()){
            mobileNumber = DashboardActivity.mobilenumber
        }
        val checkout = Checkout()
        checkout.setKeyID("rzp_live_nastejQfZCOgFR")
        try {
            val options = JSONObject()
            options.put("name", "MonQuiz")
            options.put("description", "Wallet Balance")
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png")
            options.put("order_id", orderId1)
            options.put("currency", "INR")
            options.put("amount", amount)
            val preFill = JSONObject()
            preFill.put("contact", "+91$mobileNumber")
            options.put("prefill", preFill)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 2)
            options.put("retry", retryObj)

            checkout.open(this, options)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updatePaymentStatus(paymentId: String) {

        showProgressBar(getString(R.string.loading_please_wait))
        val userid = GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id)).userId

        val jsonObject = JSONObject()
        jsonObject.put("userId",userid)
        jsonObject.put("orderId", orderId1)
        jsonObject.put("paymentId", paymentId)
        jsonObject.put("currency", "INR")
        jsonObject.put("amount", amount)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.updateOrder(requestBody)

        requestCall.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val result: String = response.body()?.charStream()?.readText().toString()
                    val res = response.errorBody()?.charStream()?.readText().toString()
                    if (result != "null") {
                        val jsonObj = JSONObject(result)
                        Log.e("response:",jsonObj.toString())
                        if (jsonObj.getString("status") == "200") { onPaymentPostSuccess() }
                    } else {
                        val jsonObj = JSONObject(res)
                        val mes = jsonObj.getString("message")
                        closeProgressbar()
                        Toasty.warning(applicationContext, mes, Toasty.LENGTH_SHORT).show()
                        Log.e("paymentUpdate :", jsonObj.toString())
                    }
                } catch (e: Exception) {
                    Log.e("paymentUpdate :", e.toString())
                    closeProgressbar()
                    Toasty.warning(applicationContext, "something went wrong",
                        Toasty.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("paymentUpdate :", t.toString())
                closeProgressbar()
                Toasty.warning(applicationContext, "Request Failed",
                    Toasty.LENGTH_SHORT).show()
            }
        })
    }

    private fun onPaymentPostSuccess() {
        closeProgressbar()
        val intent = Intent()
        setResult(RESULT_OK,intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    fun getWalletBalance(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= WalletInput(PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getWalletData(userdata)
        requestCall.enqueue(object : Callback<Wallet_Response> {
            override fun onFailure(call: Call<Wallet_Response>, t: Throwable) {
                closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Wallet_Response>, response: Response<Wallet_Response>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    if (resp!!.status==200){
                        val walletBalance = resp!!.responseData!!.walletBalance
                        tvAddMoneyWalletBalance!!.text = getString(R.string.ruppee) + StringUtil.getDecimalValue(walletBalance!!.toDouble()!!)
                    }
                    Log.i("getAllGames", "GateWalletResponse:// $resp")
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
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
       // getWalletBalance()
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

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toasty.success(applicationContext, "Payment is successful", Toasty.LENGTH_SHORT).show()
        if (p1 != null) {
            val bundle = p1.data
            Log.e("paymentSuccess","Data: $bundle")
            Log.e("paymentData",p1.toString())
            updatePaymentStatus(p1.paymentId)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toasty.warning(applicationContext, "Payment failed", Toasty.LENGTH_SHORT).show()
    }

}