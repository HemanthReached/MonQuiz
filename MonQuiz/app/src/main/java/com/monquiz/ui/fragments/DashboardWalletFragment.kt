package com.monquiz.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.monquiz.BuildConfig
import com.monquiz.R
import com.monquiz.adapter.TransactionsHistoryAdapter
import com.monquiz.adapter.DetailsArrowClickListener
import com.monquiz.customviews.CustomButton
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.bankdetails.input.BankDetailsInput
import com.monquiz.response.bankdetails.response.BankDetailsResponse
import com.monquiz.response.bankdetails.response.GetBankDetailsResponse
import com.monquiz.response.transactionaapi.ResponseData
import com.monquiz.response.transactionaapi.TransactionApi_Response
import com.monquiz.response.transactionaapi.input.TransactionApi_Input
import com.monquiz.response.wallet.input.WalletInput
import com.monquiz.response.wallet.input.verifyInput
import com.monquiz.response.wallet.res.GameDetails_Response
import com.monquiz.response.wallet.res.Wallet_Response
import com.monquiz.response.wallet.response.VerifyResponse
import com.monquiz.ui.DashboardActivity
import com.monquiz.utils.*
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DashboardWalletFragment : BaseFragment(), View.OnClickListener,
    PaymentResultWithDataListener,DetailsArrowClickListener {

    private var fbAuth: FirebaseAuth? = null
    private var total_balance : TextView? = null
    private var arrow_view : ImageView? = null
    private var total_balance_desc : TextView? = null
    private var balanceDetails_layout : ConstraintLayout? = null
    private var game_balance : TextView? = null
    private var redeem_view : TextView? = null
    private var bonus_balance : TextView? = null
    private var play_view : TextView? = null
    private var isDetailsVisible = false
    private var tvFragWalletError: TextView? = null
    private var btnFragWalletAddMoney: Button? = null
    private var recyclerViewRecentTransactions: RecyclerView? = null
    private var tvProfileStatsPlayed: TextView? = null
    private var tvProfileStatsWon: TextView? = null
    private var tvProfileStatsEarned: TextView? = null
    private var btnAddMoneyNext: Button? = null
    private var ivAddMoneyClose: ImageView? = null
    private var etAddMoneyAddedMoney: EditText? = null
    private var tvAddMoneyWalletBalance: TextView? = null
    private var tvAddMoneyRs10: TextView? = null
    private var tvAddMoneyRs20: TextView? = null
    private var tvAddMoneyRs30: TextView? = null
    private var tvAddMoneyRs50: TextView? = null
    private var tvAddMoneyRs100: TextView? = null
    private var addMoneyLayout : ConstraintLayout? = null
    private var panDetailsLayout : ConstraintLayout? = null
    private var redeemMoneyLayout : ConstraintLayout? = null
    private var filterLayout : ConstraintLayout? = null

    private var submitBtnRedeem : TextView? = null
    private var closeViewRedeem : ImageView? = null
    private var amountET : EditText? = null
    private var accountNumberET : EditText? = null
    private var ifscNumberET : EditText? = null

    private var submitBtn: TextView? = null
    private var closeView : ImageView? = null
    private var panNameEt : EditText? = null
    private var panNumberEt : EditText? = null
    private var panSavedLayout : LinearLayout? = null

    private var ivFiltersClose : ImageView? = null
    private var dateFilterGroup : RadioGroup? = null
    private var trnsFilterGroup : RadioGroup? = null
    private var lastMonthRB : RadioButton? = null
    private var last3MonthRB : RadioButton? = null
    private var customDateRB : RadioButton? = null
    private var moneyAddedRB : RadioButton? = null
    private var trnsRefundRB : RadioButton? = null
    private var trnsRedeemedRB : RadioButton? = null
    private var promoRB : RadioButton? = null

    private var dateSelectionLayout : LinearLayout? = null
    private var startDateView : TextView? = null
    private var endDateView : TextView? = null
    private var btnApplyFilters : CustomButton? = null
    private var filterCancel : TextView? = null
    private var filtersDateBtn : TextView? = null
    private var filterTransactionBtn : TextView? = null
    private var dateFilterLayout : LinearLayout? = null
    private var trnsFilterLayout : LinearLayout? = null

    private var walletBalance = 0.0
    private var walletCredits = 0.0
    private var referralBalance = 0.0
    private var isPanVerified = false
    var amount = ""
    var orderId1 = ""
    var transactionId = ""
  //  private val dialogInsufficientBalance: Dialog? = null
    private var dialogPickAvatar: View? = null
    private var dialogAccountDetails: View? = null
    private var mobileNumber : String?  = null
    private var accountDetails : com.monquiz.response.bankdetails.response.ResponseData? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var loadingServices = 0
    private var tvWinLoose : TextView? = null
    private var filter_view : ImageView? = null
    private var listOfUserHistory : LinkedList<ResponseData>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_dashboard_wallet_fragment, container, false)
        initializeUi(view)
        initUi(view)
        initializeListeners()
        initUiListeners()
        initDialogSelectAvatarUiElements(view)
        initFilterUI(view)
        initAccountDetailsUi(view)
        if (checkInternet()) {
            try {
                getTransactions()
                getGamePlayDetails()
                getWalletBalance()
                getBankDetails()
            } catch (e : Exception){ Log.e("TAG",e.toString()) }
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun initializeUi(view: View) {
        fbAuth = FirebaseAuth.getInstance()
        val bottomsheet = view.findViewById<ConstraintLayout>(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        addMoneyLayout = view.findViewById(R.id.addmoney_layout)
        panDetailsLayout = view.findViewById(R.id.pandetais_layout)
        redeemMoneyLayout = view.findViewById(R.id.redeem_layout)
        filterLayout = view.findViewById(R.id.filter_layout)
        filter_view = view.findViewById(R.id.filter_view)

        tvFragWalletError = view.findViewById(R.id.tvFragWalletError)
        btnFragWalletAddMoney = view.findViewById(R.id.btnFragWalletAddMoney)
     //   btnFragWalletRedeemCash = view.findViewById(R.id.btnFragWalletRedeemCash)
        total_balance = view.findViewById(R.id.total_balance)
        arrow_view =  view.findViewById(R.id.arrow_view)
        total_balance_desc = view.findViewById(R.id.total_balance_desc)
        balanceDetails_layout = view.findViewById(R.id.balanceDetails_layout)
        tvProfileStatsWon = view.findViewById(R.id.tvProfileStatsWon)
        tvProfileStatsPlayed = view.findViewById(R.id.tvProfileStatsPlayed)
        tvProfileStatsEarned = view.findViewById(R.id.tvProfileStatsEarned)
        game_balance = view.findViewById(R.id.game_balance)
        redeem_view = view.findViewById(R.id.redeem_view)
        bonus_balance = view.findViewById(R.id.bonus_balance)
        play_view = view.findViewById(R.id.play_view)
        recyclerViewRecentTransactions = view.findViewById(R.id.recyclerViewRecentTransactions)
        recyclerViewRecentTransactions!!.recycledViewPool.setMaxRecycledViews(0, 0)
        tvWinLoose = view.findViewById(R.id.winorlosetextview)

        /*val ivFragWalletProfilePic = view.findViewById<ImageView>(R.id.ivFragWalletProfilePic)
        val profilePic = PreferenceConnector.readString(activity, getString(R.string.user_profile_pic), "")
        Glide.with(requireActivity()).load(profilePic).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.ic_default_icon).into(ivFragWalletProfilePic)*/

        mobileNumber = PreferenceConnector.readString(activity, getString(R.string.user_mobile_number), "")
        if (!BuildConfig.PRO_VERSION) {
            btnFragWalletAddMoney!!.visibility = View.GONE
            redeem_view!!.visibility = View.GONE
        } else {
            total_balance!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun initializeListeners() {
        btnFragWalletAddMoney!!.setOnClickListener(this)
        filter_view!!.setOnClickListener(this)
        redeem_view!!.setOnClickListener(this)
        play_view!!.setOnClickListener {
            val ndv = activity as DashboardActivity
            ndv.selectPlayFragment()
        }
        arrow_view!!.setOnClickListener {
            if (isDetailsVisible){
                isDetailsVisible = false
                total_balance!!.alpha = 1f
                total_balance_desc!!.alpha = 1f
                arrow_view!!.scaleY = 1f
                balanceDetails_layout!!.visibility = View.GONE
            }else{
                isDetailsVisible = true
                total_balance!!.alpha = 0.5f
                total_balance_desc!!.alpha = 0.5f
                arrow_view!!.scaleY = -1f
                balanceDetails_layout!!.visibility = View.VISIBLE
              //  game_balance!!.text = "\u20B9\u0020" + walletBalance
            }
        }
        if (DashboardActivity.showBalanceDialog){
            DashboardActivity.showBalanceDialog = false
            btnFragWalletAddMoney!!.performClick()
        }
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

    private fun initUi(view: View) {
        ivAddMoneyClose = view.findViewById(R.id.ivAddMoneyClose)
        btnAddMoneyNext = view.findViewById(R.id.btnAddMoneyNext)
        tvAddMoneyRs100 = view.findViewById(R.id.tvAddMoneyRs100)
        tvAddMoneyRs50 = view.findViewById(R.id.tvAddMoneyRs50)
        tvAddMoneyRs30 = view.findViewById(R.id.tvAddMoneyRs30)
        tvAddMoneyRs20 = view.findViewById(R.id.tvAddMoneyRs20)
        tvAddMoneyRs10 = view.findViewById(R.id.tvAddMoneyRs10)
        tvAddMoneyWalletBalance = view.findViewById(R.id.tvAddMoneyWalletBalance)
        etAddMoneyAddedMoney = view.findViewById(R.id.etAddMoneyAddedMoney)
        etAddMoneyAddedMoney!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_GO) {
                goNext()
                handled = true
            }
            handled
        }
        etAddMoneyAddedMoney!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                AppUtils.hideKeyboard(requireActivity(),v)
            }
        }
        enableButtonNext()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initFilterUI(view: View) {
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters)
        ivFiltersClose = view.findViewById(R.id.ivFiltersClose)
        filterCancel = view.findViewById(R.id.filter_cancel)
        lastMonthRB = view.findViewById(R.id.last_month_btn)
        last3MonthRB = view.findViewById(R.id.last_3_month_btn)
        customDateRB = view.findViewById(R.id.custom_date_btn)
        moneyAddedRB = view.findViewById(R.id.money_added_btn)
        trnsRefundRB = view.findViewById(R.id.trns_refund_btn)
        trnsRedeemedRB = view.findViewById(R.id.trns_redeemed_btn)
        promoRB = view.findViewById(R.id.promo_btn)
        dateSelectionLayout = view.findViewById(R.id.date_selection_layout)
        startDateView = view.findViewById(R.id.start_date_view)
        endDateView = view.findViewById(R.id.end_date_view)
        dateFilterGroup = view.findViewById(R.id.datefilter_group)
        trnsFilterGroup = view.findViewById(R.id.trns_filter_group)
        dateFilterLayout = view.findViewById(R.id.date_filter_layout)
        trnsFilterLayout = view.findViewById(R.id.transaction_filter_layout)
        filtersDateBtn = view.findViewById(R.id.date_btn)
        filterTransactionBtn = view.findViewById(R.id.transaction_btn)
        filtersDateBtn!!.setOnClickListener{
            if (!dateFilterLayout!!.isVisible){
                dateFilterLayout!!.visibility = View.VISIBLE
                trnsFilterLayout!!.visibility = View.GONE
                filtersDateBtn!!.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                filterTransactionBtn!!.setTextColor(ContextCompat.getColor(requireContext(),R.color.black_40))
            }
        }
        filterTransactionBtn!!.setOnClickListener{
            if (!trnsFilterLayout!!.isVisible){
                dateFilterLayout!!.visibility = View.GONE
                trnsFilterLayout!!.visibility = View.VISIBLE
                filtersDateBtn!!.setTextColor(ContextCompat.getColor(requireContext(),R.color.black_40))
                filterTransactionBtn!!.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            }
        }
        btnApplyFilters!!.setOnClickListener{
            var previousMonth1 = ""
            var previousMonth = ""
            var transactiontype = ""
            if (lastMonthRB!!.isChecked){
                val pmonth = Calendar.getInstance()
                 previousMonth = AppUtils.convertDateFormat(pmonth.time.toString(),
                    "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val pmonth1 = Calendar.getInstance()
                pmonth1.add(Calendar.MONTH,-1)
                previousMonth1 = AppUtils.convertDateFormat(pmonth1.time.toString(),
                    "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

            }else if (last3MonthRB!!.isChecked){
                val pmonth = Calendar.getInstance()
                previousMonth = AppUtils.convertDateFormat(pmonth.time.toString(),
                    "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val pmonth1 = Calendar.getInstance()
                pmonth1.add(Calendar.MONTH,-3)
                previousMonth1 = AppUtils.convertDateFormat(pmonth1.time.toString(),
                    "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            }else if (customDateRB!!.isChecked){
                if (startDateView!!.text.toString().isEmpty()){
                    Toasty.error(requireContext(),"Please select start date",Toasty.LENGTH_SHORT).show()
                }else if (endDateView!!.text.toString().isEmpty()){
                    Toasty.error(requireContext(),"Please select end date",Toasty.LENGTH_SHORT).show()
                }else{
                    previousMonth = AppUtils.convertDateFormat(startDateView!!.text.toString(),"dd-MM-yyyy",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    previousMonth1 = AppUtils.convertDateFormat(endDateView!!.text.toString(),"dd-MM-yyyy",
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                }
            }
            if (moneyAddedRB!!.isChecked){
                transactiontype = "deposit"
            }else if (trnsRefundRB!!.isChecked){
                transactiontype = "refunded"
            }else if (trnsRedeemedRB!!.isChecked){
                transactiontype = "withdraw"
            }else if (promoRB!!.isChecked){
                transactiontype = "REFFERRAL_BONUS"
            }
            if (previousMonth1.isEmpty() && previousMonth.isEmpty() && transactiontype.isEmpty()){
                filter_view!!.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.noun_filter))
            }else{
                filter_view!!.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.filtered_icon))
            }
            initializeAdapter(filterList(listOfUserHistory!!,previousMonth1,previousMonth,transactiontype))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        ivFiltersClose!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        filterCancel!!.setOnClickListener {
            initializeAdapter(filterList(listOfUserHistory!!, "", "", ""))
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            filter_view!!.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.noun_filter))
            when(dateFilterGroup!!.checkedRadioButtonId){
                R.id.last_month_btn -> { lastMonthRB!!.isChecked = false }
                R.id.last_3_month_btn -> { last3MonthRB!!.isChecked = false }
                R.id.custom_date_btn -> { customDateRB!!.isChecked = false }
                else -> {}
            }
            when(trnsFilterGroup!!.checkedRadioButtonId){
                R.id.money_added_btn -> { moneyAddedRB!!.isChecked = false }
                R.id.trns_refund_btn -> { trnsRefundRB!!.isChecked = false }
                R.id.trns_redeemed_btn -> { trnsRedeemedRB!!.isChecked = false }
                R.id.promo_btn -> { promoRB!!.isChecked = false }
                else -> {}
            }
        }
        customDateRB!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                dateSelectionLayout!!.visibility = View.VISIBLE
            }else{
                dateSelectionLayout!!.visibility = View.GONE
            }
        }
        startDateView!!.setOnClickListener {
            val c = Calendar.getInstance()
            val myYear = c[Calendar.YEAR]
            val myMonth = c[Calendar.MONTH]
            val myDay = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                requireContext(), { view, year, monthOfYear, dayOfMonth ->
                    val day = if (dayOfMonth.toString().length > 1) dayOfMonth.toString() else "0$dayOfMonth"
                    val month = if ((monthOfYear + 1).toString().length > 1) (monthOfYear + 1) else "0${(monthOfYear + 1)}"
                    val selectedDate = ("$day-$month-$year")
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy")//old format
                    val dateFormat2 = SimpleDateFormat("dd-MM-yyyy")//require new format
                    val objDate = dateFormat.parse(selectedDate)
                    val fromDate = dateFormat2.format(objDate!!)
                    startDateView!!.text = fromDate
                }, myYear, myMonth, myDay)
            datePickerDialog.show()
        }
        endDateView!!.setOnClickListener {
            val c = Calendar.getInstance()
            val myYear = c[Calendar.YEAR]
            val myMonth = c[Calendar.MONTH]
            val myDay = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                requireContext(), { view, year, monthOfYear, dayOfMonth ->
                    val day = if (dayOfMonth.toString().length > 1) dayOfMonth.toString() else "0$dayOfMonth"
                    val month = if ((monthOfYear + 1).toString().length > 1) (monthOfYear + 1) else "0${(monthOfYear + 1)}"
                    val selectedDate = ("$day-$month-$year")
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy")//old format
                    val dateFormat2 = SimpleDateFormat("dd-MM-yyyy")//require new format
                    val objDate = dateFormat.parse(selectedDate)
                    val fromDate = dateFormat2.format(objDate!!)
                    endDateView!!.text = fromDate
                }, myYear, myMonth, myDay)
            datePickerDialog.show()
        }
    }

  /*  override fun onClick(v: View) {
        when (v.id) {
            R.id.tvAddMoneyRs10 -> { setDefinedButtonValueToEditText(10.0) }
            R.id.tvAddMoneyRs20 -> { setDefinedButtonValueToEditText(20.0) }
            R.id.tvAddMoneyRs30 -> { setDefinedButtonValueToEditText(30.0) }
            R.id.tvAddMoneyRs50 -> { setDefinedButtonValueToEditText(50.0) }
            R.id.tvAddMoneyRs100 -> { setDefinedButtonValueToEditText(100.0) }
            R.id.ivAddMoneyClose -> { finish() }
            R.id.btnAddMoneyNext -> { goNext() }
        }
    }*/

    @SuppressLint("SetTextI18n")
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

    override fun onResume() {
        super.onResume()
        if (checkInternet()){
            try {
            } catch (e : Exception){ Log.e("TAG",e.toString()) }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnFragWalletAddMoney -> goToAddMoney()
            R.id.redeem_view -> if (BuildConfig.PRO_VERSION) {
                if (!isPanVerified){
                    configurePanUI()
                }else{
                    if (walletBalance.toInt() > 1){ configureAccountUI() }
                }
            } else {
                Toasty.error(requireContext(), "cannot redeem",
                    Toasty.LENGTH_SHORT).show()
               // showToast("cannot redeem")
            }
            R.id.tvAddMoneyRs10 -> { setDefinedButtonValueToEditText(10.0) }
            R.id.tvAddMoneyRs20 -> { setDefinedButtonValueToEditText(20.0) }
            R.id.tvAddMoneyRs30 -> { setDefinedButtonValueToEditText(30.0) }
            R.id.tvAddMoneyRs50 -> { setDefinedButtonValueToEditText(50.0) }
            R.id.tvAddMoneyRs100 -> { setDefinedButtonValueToEditText(100.0) }
            R.id.ivAddMoneyClose -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                if (etAddMoneyAddedMoney!!.isFocused){
                    AppUtils.hideKeyboard(requireActivity(),etAddMoneyAddedMoney!!)
                }
              //  requireActivity().finish()
            }
            R.id.btnAddMoneyNext -> { goNext() }
            R.id.filter_view -> {
                if (listOfUserHistory.isNullOrEmpty()){
                    Toasty.warning(requireContext(),"No transactions to filter",Toasty.LENGTH_SHORT).show()
                }else {
                    configureFiltersUI()
                }
            }
            else -> {}
        }
    }

    private fun configureAccountUI() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        addMoneyLayout!!.visibility = View.GONE
        panDetailsLayout!!.visibility = View.GONE
        redeemMoneyLayout!!.visibility = View.VISIBLE
        filterLayout!!.visibility = View.GONE
        amountET!!.setText("")
        val balance = walletBalance.toInt()
        amountET!!.filters = arrayOf(InputFilterMinMax(1, balance))
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
        addMoneyLayout!!.visibility = View.GONE
        panDetailsLayout!!.visibility = View.VISIBLE
        redeemMoneyLayout!!.visibility =View.GONE
        filterLayout!!.visibility = View.GONE
        panNameEt!!.setText("")
        panNameEt!!.setText("")
    }

    private fun configureFiltersUI() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        addMoneyLayout!!.visibility = View.GONE
        panDetailsLayout!!.visibility = View.GONE
        redeemMoneyLayout!!.visibility =View.GONE
        filterLayout!!.visibility = View.VISIBLE
        filtersDateBtn!!.performClick()
    }

    @SuppressLint("SetTextI18n")
    private fun goToAddMoney() {
        /*val addMoneyIntent = Intent(activity, AddMoneyActivity::class.java)
        resultLauncher.launch(addMoneyIntent)*/
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        addMoneyLayout!!.visibility = View.VISIBLE
        panDetailsLayout!!.visibility = View.GONE
        redeemMoneyLayout!!.visibility =View.GONE
        etAddMoneyAddedMoney!!.setText("")
        tvAddMoneyWalletBalance!!.text = "\u20B9\u0020" + walletBalance
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
                        Toasty.warning(requireContext(), mes, Toasty.LENGTH_SHORT).show()
                        Log.e("orderCreate :", jsonObj.toString())
                    }
                } catch (e: Exception) {
                    Log.e("orderCreate :", e.toString())
                    closeProgressbar()
                    Toasty.warning(requireContext(),
                        "something went wrong", Toasty.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("orderCreate :", t.toString())
                closeProgressbar()
                Toasty.warning(requireContext(), "Request Failed", Toasty.LENGTH_SHORT).show()
            }
        })
    }

    fun onSuccess(){
        val sAmount: String = amount
        val amount = (sAmount.toFloat() * 100).roundToInt()
        var mobileNumber = PreferenceConnector.readString(requireContext(),
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
            options.put("theme.color", "#006C8E")
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
            Log.e("options",options.toString())
            checkout.open(requireActivity(), options)

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("checkout Exception",e.toString())
        }
    }

     fun updatePaymentStatus(paymentId: String) {

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
                        if (jsonObj.getString("status") == "200") {
                            onPaymentPostSuccess() }
                    } else {
                        val jsonObj = JSONObject(res)
                        val mes = jsonObj.getString("message")
                        closeProgressbar()
                        Toasty.warning(requireContext(), mes, Toasty.LENGTH_SHORT).show()
                        Log.e("paymentUpdate :", jsonObj.toString())
                    }
                } catch (e: Exception) {
                    Log.e("paymentUpdate :", e.toString())
                    closeProgressbar()
                    Toasty.warning(requireContext(), "something went wrong",
                        Toasty.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("paymentUpdate :", t.toString())
                closeProgressbar()
                Toasty.warning(requireContext(), "Request Failed",
                    Toasty.LENGTH_SHORT).show()
            }
        })
    }

    private fun onPaymentPostSuccess() {
        closeProgressbar()
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN }
        getWalletBalance()
        getTransactions()

       /* val intent = Intent()
        requireActivity().setResult(AppCompatActivity.RESULT_OK,intent)
        requireActivity().finish()*/

    }

    private fun initDialogSelectAvatarUiElements(view: View) {
        dialogPickAvatar = view
         submitBtn = dialogPickAvatar!!.findViewById(R.id.submit_btn)
         closeView = dialogPickAvatar!!.findViewById(R.id.close_popup)
         panNameEt = dialogPickAvatar!!.findViewById(R.id.nameet)
         panNumberEt = dialogPickAvatar!!.findViewById(R.id.cardnumberet)
        panSavedLayout = dialogPickAvatar!!.findViewById(R.id.saved_layout)
        val popupdesctv = dialogPickAvatar!!.findViewById<TextView>(R.id.popupdesctv)
        val string = popupdesctv.text
        val spannable = SpannableString(string)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)),
            0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        popupdesctv.movementMethod = LinkMovementMethod.getInstance()
        popupdesctv.text = spannable
        submitBtn!!.setOnClickListener {
            if (dialogPickAvatar != null){
                if (panNameEt!!.text.trim().toString().isNotEmpty()){
                    if (panNumberEt!!.text.trim().toString().isNotEmpty()){
                        verifyPanDetails(panNameEt!!.text.trim().toString(),
                            panNumberEt!!.text.trim().toString())
                    }else{
                        Toasty.warning(requireContext(),"please enter Pan Number",
                            Toasty.LENGTH_SHORT).show()
                    }
                }else{
                    Toasty.warning(requireContext(),"please enter name on Pan Card",
                        Toasty.LENGTH_SHORT).show()
                }
            }
        }
        panNameEt!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !panNumberEt!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),v)
            }
        }
        panNumberEt!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !panNameEt!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),v)
            }
        }
        closeView!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (panNameEt!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),panNameEt!!)
            }else if (panNumberEt!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),panNumberEt!!)
            }
        }
    }

    private fun initAccountDetailsUi(view: View) {
        dialogAccountDetails = view
         submitBtnRedeem = dialogAccountDetails!!.findViewById(R.id.submit_btn_redeem)
         closeViewRedeem = dialogAccountDetails!!.findViewById(R.id.close_popup_redeem)
         amountET = dialogAccountDetails!!.findViewById(R.id.amountet)
         accountNumberET = dialogAccountDetails!!.findViewById(R.id.accnumberet)
         ifscNumberET = dialogAccountDetails!!.findViewById(R.id.ifscnumberet)
        val balance = walletBalance.toInt()
        amountET!!.filters = arrayOf(InputFilterMinMax(1, balance))
        submitBtnRedeem!!.setOnClickListener {
            if (dialogAccountDetails != null) {
                val amount  = amountET!!.text.trim().toString()
                val accnumber = accountNumberET!!.text.trim().toString()
                val ifsccode  = ifscNumberET!!.text.trim().toString()
                if (isValidForm(amount,accnumber,ifsccode)){
                    if (accountDetails!!.bankDetails.isNullOrEmpty()){ saveBankDetails(accnumber,ifsccode) }
                   redeemAmount(amountET!!.text.trim().toString(),accountNumberET!!.text.trim().toString(),
                       ifscNumberET!!.text.trim().toString())
                  //  dialogAccountDetails!!.dismiss()
                }
            }
        }
        amountET!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !accountNumberET!!.isFocused && !ifscNumberET!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),v)
            }
        }
        accountNumberET!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !amountET!!.isFocused && !ifscNumberET!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),v)
            }
        }
        ifscNumberET!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !accountNumberET!!.isFocused && !amountET!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),v)
            }
        }
        closeViewRedeem!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (amountET!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),amountET!!)
            }else if (accountNumberET!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),accountNumberET!!)
            }else if (ifscNumberET!!.isFocused){
                AppUtils.hideKeyboard(requireActivity(),ifscNumberET!!)
            }
           // if (dialogAccountDetails != null) dialogAccountDetails!!.dismiss()
        }
    }

    private fun isValidForm(amount: String,accountNumber : String,ifscCode: String): Boolean {
        if (amount == ""){
            Toasty.error(requireContext(),"Please Enter redeem amount", Toasty.LENGTH_SHORT).show()
            return false
        }
        else if (accountNumber == ""){
            Toasty.error(requireContext(),"Please Enter Account Number", Toasty.LENGTH_SHORT).show()
            return false
        }
        else if (accountNumber.length < 10){
            Toasty.error(requireContext(),"Account Number must have minimum of 10 digits",
                Toasty.LENGTH_SHORT).show()
            return false
        }
        else if (ifscCode == ""){
            Toasty.error(requireContext(),"Please Enter IFSC Code", Toasty.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun redeemAmount (amount : String,accountNumber: String,ifscCode : String) {

        showProgressBar(getString(R.string.loading_please_wait))
        val userid = GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id)).userId
        val name = PreferenceConnector.readString(activity, getString(R.string.user_full_name), "")

        /*val accNumber = "1121431121541121"
        val ifsc = "HDFC0001234"*/
        val jsonObject = JSONObject()
        jsonObject.put("userId",userid)
        jsonObject.put("name", name)
        jsonObject.put("amount", amount)
        jsonObject.put("contact", mobileNumber)
        jsonObject.put("email","")
        jsonObject.put("account_number", accountNumber)
        jsonObject.put("ifsc", ifscCode)

        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.RedeemCash(requestBody)

        requestCall.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val result: String = response.body()?.charStream()?.readText().toString()
                    val res = response.errorBody()?.charStream()?.readText().toString()
                    if (result != "null") {
                        val jsonObj = JSONObject(result)
                        Log.e("redeemAmountResponse:",jsonObj.toString())
                        if (jsonObj.getString("status") == "200") {
                          //  val jObj1 = jsonObj.getJSONObject("responseData")
                            val message = jsonObj.getString("message")
                            Toasty.success(activity!!.applicationContext, AppUtils.toTitleCase(message)!!,
                                Toasty.LENGTH_SHORT).show()
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                          //  if (dialogAccountDetails != null) dialogAccountDetails!!.dismiss()
                            closeProgressbar()
                            getWalletBalance()
                            getTransactions()
                        }
                    } else {
                        val jsonObj = JSONObject(res)
                        val mes = jsonObj.getString("message")
                        closeProgressbar()
                        Toasty.warning(activity!!.applicationContext, mes, Toasty.LENGTH_SHORT).show()
                        Log.e("redeemAmount :", jsonObj.toString())
                    }
                } catch (e: Exception) {
                    Log.e("redeemAmount :", e.toString())
                    closeProgressbar()
                    Toasty.warning(activity!!.applicationContext, "something went wrong",
                        Toasty.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("redeemAmount :", t.toString())
                closeProgressbar()
                Toasty.warning(activity!!.applicationContext, "Request Failed",
                    Toasty.LENGTH_SHORT).show()
            }
        })
    }

    private fun getWalletBalance(){
        showProgressBar(getString(R.string.loading_please_wait))
        loadingServices++
        val userdata= WalletInput(PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getWalletData(userdata)
        requestCall.enqueue(object : Callback<Wallet_Response> {
            override fun onFailure(call: Call<Wallet_Response>, t: Throwable) {
                loadingServices--
                if (loadingServices == 0){ closeProgressbar() }
                Log.i("getWalletBalance","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Wallet_Response>, response: Response<Wallet_Response>) {
                if(response.isSuccessful) {
                    val resp = response.body()
                    Log.i("getWalletBalance", "GetWalletResponse:// $resp")
                    if (resp!!.status==200){
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.DOWN
                        walletBalance = df.format(resp.responseData!!.walletBalance!!.toDouble()).toDouble()
                        referralBalance = df.format(resp.responseData!!.referralBalance!!).toDouble()
                        if (!BuildConfig.PRO_VERSION) {
                            total_balance!!.text = "\u20B9\u0020" + (walletBalance + referralBalance)
                            game_balance!!.text = "\u20B9\u0020" + walletBalance
                            bonus_balance!!.text = "\u20B9\u0020" + referralBalance
                        } else {
                            total_balance!!.text = "\u20B9\u0020" + (walletBalance + walletCredits + referralBalance)
                            game_balance!!.text = "\u20B9\u0020" + (walletBalance + walletCredits)
                            bonus_balance!!.text = "\u20B9\u0020" + referralBalance
                        }
                        tvAddMoneyWalletBalance!!.text = "\u20B9\u0020" + walletBalance
                        loadingServices--
                        if (loadingServices == 0){
                            closeProgressbar()
                        }
                    }
                } else{
                    loadingServices--
                    if (loadingServices == 0){ closeProgressbar() }
                    Log.i("getWalletBalance","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(requireContext(), "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(requireContext(), "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(requireContext(), "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(requireContext(), "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun verifyPanDetails(name : String,panNumber : String){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata : String = PrefsHelper().getPref(OwlizConstants.user_id)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.verifyPan(verifyInput(userdata,name ,panNumber))
        requestCall.enqueue(object : Callback<VerifyResponse> {
            override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("verifyPan","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("verifyPan", "Response:// $resp")
                    if (resp!!.status==200){
                        isPanVerified = if (resp.responseData != null){ resp.responseData!![0].panStatus!! }
                        else{ true }
                        //isPanVerified = resp.responseData!!.panStatus!!
                        panSavedLayout!!.visibility = View.VISIBLE
                        /*bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        configureAccountUI()*/
                        getBankDetails()
                        // if (dialogPickAvatar!=null){ dialogPickAvatar!!.dismiss() }
                    }
                } else{
                    closeProgressbar()
                    Log.i("verifyPan","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(requireContext(), "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(requireContext(), "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(requireContext(), "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(requireContext(), "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun saveBankDetails(accNumber : String,ifscCode : String){
       // showProgressBar(getString(R.string.loading_please_wait))
        val userdata : String = PrefsHelper().getPref(OwlizConstants.user_id)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.saveBankDetails(BankDetailsInput(userdata,"",accNumber ,ifscCode))
        requestCall.enqueue(object : Callback<BankDetailsResponse> {
            override fun onFailure(call: Call<BankDetailsResponse>, t: Throwable) {
             //   closeProgressbar()
                Log.i("saveBankDetails","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<BankDetailsResponse>, response: Response<BankDetailsResponse>) {
                if(response.isSuccessful) {
               //     closeProgressbar()
                    val resp = response.body()
                    Log.i("saveBankDetails", "Response:// $resp")
                    if (resp!!.status==200){
                        getBankDetails()
                    }
                } else{
                 //   closeProgressbar()
                    Log.i("saveBankDetails","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(requireContext(), "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(requireContext(), "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(requireContext(), "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(requireContext(), "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getBankDetails(){
        // showProgressBar(getString(R.string.loading_please_wait))
        val userdata : String = PrefsHelper().getPref(OwlizConstants.user_id)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getBankDetails(WalletInput(userdata))
        requestCall.enqueue(object : Callback<GetBankDetailsResponse> {
            override fun onFailure(call: Call<GetBankDetailsResponse>, t: Throwable) {
                //   closeProgressbar()
                Log.i("getBackDetails","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<GetBankDetailsResponse>, response: Response<GetBankDetailsResponse>) {
                if(response.isSuccessful) {
                    //     closeProgressbar()
                    val resp = response.body()
                    Log.i("getBackDetails", "Response:// $resp")
                    if (resp!!.status == 200){
                        accountDetails = resp.responseData
                    //    isPanVerified = resp.responseData!!.panDetails!![0].panStatus!!
                        if (!accountDetails!!.panDetails.isNullOrEmpty()){
                            isPanVerified = accountDetails!!.panDetails!![0].panStatus!!
                        }
                    }
                } else{
                    //   closeProgressbar()
                    Log.i("getBackDetails","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(requireContext(), "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(requireContext(), "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(requireContext(), "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(requireContext(), "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // transaction
    private fun getTransactions(){
        showProgressBar(getString(R.string.loading_please_wait))
        loadingServices++
        val userdata= TransactionApi_Input(PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getTransactions(userdata)
        requestCall.enqueue(object : Callback<TransactionApi_Response> {
            override fun onFailure(call: Call<TransactionApi_Response>, t: Throwable) {
                loadingServices--
                if (loadingServices == 0){ closeProgressbar() }
                Log.i("getTransactions","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<TransactionApi_Response>, response: Response<TransactionApi_Response>) {
                if(response.isSuccessful) {
                    val resp = response.body()
                    if (resp!!.status==200){
                        if (resp.responseData!!.isNotEmpty()) {
                            listOfUserHistory = LinkedList(resp.responseData!!)
                            initializeAdapter(listOfUserHistory!!)
                        } else {
                            recyclerViewRecentTransactions!!.visibility = View.GONE
                            tvFragWalletError!!.visibility = View.VISIBLE
                            loadingServices--
                            if (loadingServices == 0){
                                closeProgressbar()
                            }
                        }
                    }
                    Log.i("getTransactions", "GateWalletResponse:// $resp")
                } else{
                    loadingServices--
                    if (loadingServices == 0){ closeProgressbar() }
                    Log.i("getTransactions","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(requireContext(), "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(requireContext(), "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(requireContext(), "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(requireContext(), "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getGamePlayDetails(){
        showProgressBar(getString(R.string.loading_please_wait))
        loadingServices++
        val userid : String = PrefsHelper().getPref(OwlizConstants.user_id)
        val jsonObject = JSONObject()
        jsonObject.put("userId", userid)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getPlayDetails(requestBody)

        requestCall.enqueue(object : Callback<GameDetails_Response> {
            override fun onFailure(call: Call<GameDetails_Response>, t: Throwable) {
                loadingServices--
                if (loadingServices == 0){ closeProgressbar() }
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<GameDetails_Response>, response: Response<GameDetails_Response>) {
                if(response.isSuccessful) {
                    val resp = response.body()
                    Log.i("getAllGames", "GateWalletResponse:// $resp")
                    if (resp!!.status == 200){
                        val totalGames = resp.responseData!!.gamesCount!!.toString()
                        val weekWinnings = resp.responseData!!.weeklyprofit.toString()
                        val winnings = resp.responseData!!.profit!!.toString()
                        onSuccess(totalGames,winnings,weekWinnings)
                    }
                } else{
                    loadingServices--
                    if (loadingServices == 0){ closeProgressbar() }
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(requireContext(), "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(requireContext(), "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(requireContext(), "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(requireContext(), "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun onSuccess(totalGames: String,winnings: String,weekWinnings: String) {
        tvProfileStatsPlayed!!.text = totalGames
        if (winnings.startsWith("-")){
            tvProfileStatsWon!!.text = "-\u20B9 ${winnings.substring(1)}"
        }else{
            tvProfileStatsWon!!.text = "+\u20B9 $winnings"
        }
        if (weekWinnings.startsWith("-")){
            tvProfileStatsEarned!!.text = "-\u20B9 ${weekWinnings.substring(1)}"
        }else{
            tvProfileStatsEarned!!.text = "+\u20B9 $weekWinnings"
        }
       // tvProfileStatsEarned!!.text = "\u20B9 $winnings"
        loadingServices--
        if (loadingServices == 0){
            closeProgressbar()
        }
    }

    private fun initializeAdapter(listOfUserHistory: LinkedList<ResponseData>) {
        if (listOfUserHistory.isEmpty()){
            recyclerViewRecentTransactions!!.visibility = View.GONE
            tvFragWalletError!!.visibility = View.VISIBLE
        }else{
            recyclerViewRecentTransactions!!.visibility = View.VISIBLE
            tvFragWalletError!!.visibility = View.GONE
            try {
                for (index in listOfUserHistory.indices){
                    val  date = AppUtils.convertDateFormat(listOfUserHistory[index].createdAt!!,
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMM yyyy")
                    listOfUserHistory[index].monthName = date
                }
            }
            catch (e : Exception){ Log.e("DateConversion",e.toString()) }

            if (activity != null){
                val transactionsHistoryAdapter = TransactionsHistoryAdapter(requireActivity(),
                    listOfUserHistory,this)
                val manager = LinearLayoutManager(activity)
                val layoutManager: RecyclerView.LayoutManager = manager
                recyclerViewRecentTransactions!!.layoutManager = layoutManager
                recyclerViewRecentTransactions!!.itemAnimator = DefaultItemAnimator()
                recyclerViewRecentTransactions!!.adapter = transactionsHistoryAdapter
                recyclerViewRecentTransactions!!.runWhenReady {
                    loadingServices--
                    if (loadingServices == 0){ closeProgressbar() }
                }
            }
        }

      /*  loadingServices--
        if (loadingServices == 0){
            closeProgressbar()
        }*/
    }

    private fun RecyclerView.runWhenReady(action: () -> Unit) {
        val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                action()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    @SuppressLint("SimpleDateFormat")
    fun filterList(list: LinkedList<ResponseData>, date1: String, date2: String,
                   transactionType: String/*,gameType : String*/) : LinkedList<ResponseData> {
        val transactions: LinkedList<ResponseData> = LinkedList<ResponseData>()
        for (events in list) {
            if (date1.isEmpty() && date2.isEmpty() && transactionType.isEmpty() /*&& gameType.isEmpty()*/){
                transactions.add(events)
            }
            /*else if (date1.isEmpty() && date2.isEmpty() && transactionType.isEmpty() && gameType.isNotEmpty()){
                if (events.gameStatus == gameType){ transactions.add(events) }
            }*/
            else if (date1.isEmpty() && date2.isEmpty() && transactionType.isNotEmpty() /*&& gameType.isEmpty()*/){
                if (transactionType == "refunded"){
                    if (events.transactionType == "deposit" && events.transactionStatus == "refunded"){
                        transactions.add(events)
                    }
                }else if (transactionType == "withdraw"){
                    if (events.transactionType == "withdraw"){ transactions.add(events) }
                }else if (transactionType == "deposit"){
                    if (events.transactionType == "deposit"){ transactions.add(events) }
                }else if (transactionType == "REFFERRAL_BONUS"){
                    if (events.transactionType == "REFFERRAL_BONUS"){ transactions.add(events) }
                }
            }
            /*else if (date1.isEmpty() && date2.isEmpty() && transactionType.isNotEmpty() && gameType.isNotEmpty()){
                if (transactionType == "refunded"){
                    if (events.transactionType == "deposit" && events.transactionStatus == "refunded"
                        && events.gameStatus == gameType){
                        transactions.add(events)
                    }
                }else if (transactionType == "withdraw"){
                    if (events.transactionType == "withdraw" && events.gameStatus == gameType){ transactions.add(events) }
                }else if (transactionType == "deposit"){
                    if (events.transactionType == "deposit" && events.gameStatus == gameType){ transactions.add(events) }
                }else if (transactionType == "REFFERRAL_BONUS"){
                    if (events.transactionType == "REFFERRAL_BONUS"){ transactions.add(events) }
                }
            }*/
            else if (date1.isNotEmpty() && date2.isNotEmpty() && transactionType.isEmpty() /*&& gameType.isEmpty()*/){
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val formatter1 = SimpleDateFormat("dd MM yyyy")
                var d0: Date? = null
                var d1 : Date? = null
                var d2 : Date? = null
                try {
                    val date0 = formatter.parse(events.createdAt!!)
                    val dat1 = formatter.parse(date1)
                    val dat2 = formatter.parse(date2)
                    d0 = formatter1.parse(formatter1.format(date0!!))
                    d1 = formatter1.parse(formatter1.format(dat1!!))
                    d2 = formatter1.parse(formatter1.format(dat2!!))
                } catch (e: ParseException) {
                }
                if (d2!!.after(d1)){
                    if ((d0!!.after(d1) || d0 == d1 ) && ( d0.before(d2) || d0 == d2) ) {
                        transactions.add(events)
                    }
                }else{
                    if ((d0!!.before(d1) || d0 == d1 ) &&(d0.after(d2) || d0 == d2)) {
                        transactions.add(events)
                    }
                }
            }
            /*else if (date1.isNotEmpty() && date2.isNotEmpty() && transactionType.isEmpty() && gameType.isNotEmpty()){
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val formatter1 = SimpleDateFormat("dd MM yyyy")
                var d0: Date? = null
                var d1 : Date? = null
                var d2 : Date? = null
                try {
                    val date0 = formatter.parse(events.createdAt!!)
                    val dat1 = formatter.parse(date1)
                    val dat2 = formatter.parse(date2)
                    d0 = formatter1.parse(formatter1.format(date0))
                    d1 = formatter1.parse(formatter1.format(dat1))
                    d2 = formatter1.parse(formatter1.format(dat2))
                } catch (e: ParseException) {
                }
                if (d2!!.after(d1)){
                    if ((d0!!.after(d1) || d0 == d1 ) && ( d0.before(d2) || d0 == d2) ) {
                        if (events.gameStatus == gameType){
                            transactions.add(events)
                        }
                    }
                }else{
                    if ((d0!!.before(d1) || d0 == d1 ) &&(d0.after(d2) || d0 == d2)) {
                        if (events.gameStatus == gameType){
                            transactions.add(events)
                        }
                    }
                }
            }*/
            else if (date1.isNotEmpty() && date2.isNotEmpty() && transactionType.isNotEmpty() /*&& gameType.isEmpty()*/){
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val formatter1 = SimpleDateFormat("dd MM yyyy")
                var d0: Date? = null
                var d1 : Date? = null
                var d2 : Date? = null
                try {
                    val date0 = formatter.parse(events.createdAt!!)
                    val dat1 = formatter.parse(date1)
                    val dat2 = formatter.parse(date2)
                    d0 = formatter1.parse(formatter1.format(date0!!))
                    d1 = formatter1.parse(formatter1.format(dat1!!))
                    d2 = formatter1.parse(formatter1.format(dat2!!))
                } catch (e: ParseException) {
                }
                if (d2!!.after(d1)){
                    if ((d0!!.after(d1) || d0 == d1 ) && ( d0.before(d2) || d0 == d2) ) {
                        if (transactionType == "refunded"){
                            if (events.transactionType == "deposit" && events.transactionStatus == "refunded"){
                                transactions.add(events)
                            }
                        }else if (transactionType == "withdraw"){
                            if (events.transactionType == "withdraw"){ transactions.add(events) }
                        }else if (transactionType == "deposit"){
                            if (events.transactionType == "deposit"){ transactions.add(events) }
                        }else if (transactionType == "REFFERRAL_BONUS"){
                            if (events.transactionType == "REFFERRAL_BONUS"){ transactions.add(events) }
                        }
                    }
                }else{
                    if ((d0!!.before(d1) || d0 == d1 ) &&(d0.after(d2) || d0 == d2)) {
                        if (transactionType == "refunded"){
                            if (events.transactionType == "deposit" && events.transactionStatus == "refunded"){
                                transactions.add(events)
                            }
                        }else if (transactionType == "withdraw"){
                            if (events.transactionType == "withdraw"){ transactions.add(events) }
                        }else if (transactionType == "deposit"){
                            if (events.transactionType == "deposit"){ transactions.add(events) }
                        }else if (transactionType == "REFFERRAL_BONUS"){
                            if (events.transactionType == "REFFERRAL_BONUS"){ transactions.add(events) }
                        }
                    }
                }
            }
           /* else if (date1.isNotEmpty() && date2.isNotEmpty() && transactionType.isNotEmpty() && gameType.isNotEmpty()){
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val formatter1 = SimpleDateFormat("dd MM yyyy")
                var d0: Date? = null
                var d1 : Date? = null
                var d2 : Date? = null
                try {
                    val date0 = formatter.parse(events.createdAt!!)
                    val dat1 = formatter.parse(date1)
                    val dat2 = formatter.parse(date2)
                    d0 = formatter1.parse(formatter1.format(date0))
                    d1 = formatter1.parse(formatter1.format(dat1))
                    d2 = formatter1.parse(formatter1.format(dat2))
                } catch (e: ParseException) {
                }
                if (d2!!.after(d1)){
                    if ((d0!!.after(d1) || d0 == d1 ) && ( d0.before(d2) || d0 == d2) ) {
                        if (transactionType == "refunded"){
                            if (events.transactionType == "deposit" && events.transactionStatus == "refunded" && events.gameStatus == gameType){
                                transactions.add(events)
                            }
                        }else if (transactionType == "withdraw"){
                            if (events.transactionType == "withdraw" && events.gameStatus == gameType ){ transactions.add(events) }
                        }else if (transactionType == "deposit"){
                            if (events.transactionType == "deposit" && events.gameStatus == gameType){ transactions.add(events) }
                        }else if (transactionType == "REFFERRAL_BONUS" && events.gameStatus == gameType){
                            if (events.transactionType == "REFFERRAL_BONUS"){ transactions.add(events) }
                        }
                    }
                }else{
                    if ((d0!!.before(d1) || d0 == d1 ) &&(d0.after(d2) || d0 == d2)) {
                        if (transactionType == "refunded"){
                            if (events.transactionType == "deposit" && events.transactionStatus == "refunded" && events.gameStatus == gameType){
                                transactions.add(events)
                            }
                        }else if (transactionType == "withdraw"){
                            if (events.transactionType == "withdraw" && events.gameStatus == gameType ){ transactions.add(events) }
                        }else if (transactionType == "deposit"){
                            if (events.transactionType == "deposit" && events.gameStatus == gameType ){ transactions.add(events) }
                        }else if (transactionType == "REFFERRAL_BONUS"){
                            if (events.transactionType == "REFFERRAL_BONUS"&& events.gameStatus == gameType){ transactions.add(events) }
                        }else{
                            if (events.gameStatus == gameType){
                                if (events.gameStatus ==  gameType){ transactions.add(events) }
                            }
                        }
                    }
                }
            }*/
        }
       return transactions
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toasty.success(requireContext(), "Payment is successful", Toasty.LENGTH_SHORT).show()
        if (p1 != null) {
            val bundle = p1.data
            Log.e("paymentSuccess","Data: $bundle")
            Log.e("paymentData",p1.toString())
            updatePaymentStatus(p1.paymentId)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toasty.warning(requireContext(), "Payment failed", Toasty.LENGTH_SHORT).show()
    }

    override fun onDetailsArrowClick(item: ResponseData, position: Int) {
        item.IsDetailsVisible = !item.IsDetailsVisible
        recyclerViewRecentTransactions!!.adapter?.notifyItemChanged(position)
    }

}

