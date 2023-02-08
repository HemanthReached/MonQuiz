package com.monquiz.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.monquiz.R
import com.monquiz.network.RetrofitApi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.review.input.CreateFeedBackInput
import com.monquiz.response.review.response.CreateFeedBackResponse
import com.monquiz.utils.*
import com.monquiz.utils.DialogUtils.Companion.removeDialogAttributes
import com.monquiz.utils.DialogUtils.Companion.setDialogAttributes
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_welcome.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : BaseActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {
    private var ivSettingClose: ImageView? = null
    private var llpersonalDetails: RelativeLayout? = null
    private var llAudioSettings: RelativeLayout? = null
    private var llNotifications: RelativeLayout? = null
    private var llRuleBook: RelativeLayout? = null
    private var llFeedBack: RelativeLayout? = null
    private var llSignOut: LinearLayout? = null
    private var switchAudio: ImageView? = null
    private var switchNotification: ImageView? = null

    //private FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private var isAudioChecked = false
    private var isNotificationsChecked = false
    private var tvSettingsTerms: TextView? = null
    private var tvSettingsPolicy: TextView? = null
    private var dialogFeedback: Dialog? = null
  //  private var etQuery: EditText? = null
  //  private var ivQueryDialogClose: ImageView? = null
    private var btnSubmitQuery: TextView? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var ivQueryClose :ImageView? = null
    private var etQuery : EditText?= null
    private var submitLayout : LinearLayout? = null
    private var inputLayout : LinearLayout? = null
   // private var rlFragPlayPlayBtn : RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.layout_settings)
        initializeUi()
        initializeListeners()
    }

    private fun initializeUi() {
        val bottomsheet = findViewById<ConstraintLayout>(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        ivSettingClose = findViewById(R.id.ivSettingClose)
        llpersonalDetails = findViewById(R.id.llPersonalDetails)
        llAudioSettings = findViewById(R.id.llAudioSettings)
        llNotifications = findViewById(R.id.llNotifications)
        llRuleBook = findViewById(R.id.llRuleBook)
        llFeedBack = findViewById(R.id.llFeedBack)
        llSignOut = findViewById(R.id.llSignOut)
        tvSettingsTerms = findViewById(R.id.tvSettingsTerms)
        tvSettingsPolicy = findViewById(R.id.tvSettingsPolicy)
        switchAudio = findViewById(R.id.switchAudio)
        switchNotification = findViewById(R.id.switchNotification)
        isAudioChecked = PreferenceConnector.readBoolean(this,
            getString(R.string.is_audio_mode_enable), false)
        CommonMethods.infoLog("checked: $isAudioChecked")
        isNotificationsChecked = PreferenceConnector.readBoolean(this,
            getString(R.string.notification_on_off), false)
        if (isAudioChecked){
            switchAudio!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_on1))
        }else{
            switchAudio!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_off1))
        }

        if (isNotificationsChecked){
            switchNotification!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_on1))
        }else{
            switchNotification!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_off1))
        }
    }

    private fun initializeListeners() {
        ivSettingClose!!.setOnClickListener(this)
        llpersonalDetails!!.setOnClickListener(this)
        switchAudio!!.setOnClickListener(this)
        llAudioSettings!!.setOnClickListener(this)
        llNotifications!!.setOnClickListener(this)
        llRuleBook!!.setOnClickListener(this)
        llFeedBack!!.setOnClickListener(this)
        llSignOut!!.setOnClickListener(this)
        tvSettingsPolicy!!.setOnClickListener(this)
        tvSettingsTerms!!.setOnClickListener(this)
    }

    private fun openFeedback() {
        etQuery = findViewById(R.id.etQuery)
        ivQueryClose = findViewById(R.id.ivQueryClose)
        btnSubmitQuery = findViewById(R.id.btnSubmitQuery)
        submitLayout = findViewById(R.id.submit_layout)
        inputLayout = findViewById(R.id.input_layout)
        etQuery!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                AppUtils.hideKeyboard(this,v)
            }
        }
        inputLayout!!.visibility = View.VISIBLE
        submitLayout!!.visibility = View.GONE
        etQuery!!.setText("")
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        ivQueryClose!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (etQuery!!.isFocused){
                AppUtils.hideKeyboard(this,etQuery!!)
            }
        }
        btnSubmitQuery!!.setOnClickListener {
            if (etQuery!!.text.trim().toString().isNotEmpty()){
                saveFeedBack(etQuery!!.text.trim().toString())
            }else{
                Toasty.warning(this,"Please enter feedBack",Toasty.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivSettingClose -> onBackPressed()
            R.id.llPersonalDetails -> {
                startActivity(Intent(this, PersonalDetailsActivity::class.java))
            }
            R.id.switchAudio -> changeSoundSetting()
            R.id.llAudioSettings -> changeSoundSetting()
            R.id.llNotifications -> if (isNotificationsChecked) {
                isNotificationsChecked = false
             //   switchNotification!!.isChecked = false
                switchNotification!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_off1))
                PreferenceConnector.writeBoolean(this, getString(R.string.notification_on_off), false)
            } else {
                isNotificationsChecked = true
              //  switchNotification!!.isChecked = true
                switchNotification!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_on1))
                PreferenceConnector.writeBoolean(this, getString(R.string.notification_on_off), true)
            }
            R.id.llRuleBook -> {
                val intent = Intent(this, RuleBookActivity::class.java)
                startActivity(intent)
            }
            R.id.llFeedBack -> { openFeedback() } // openFeedbackDialog()
            R.id.llSignOut -> signOutAlertDialog()
            R.id.tvSettingsTerms -> openTextDocument("terms")
            R.id.tvSettingsPolicy -> openTextDocument("policy")
        }
    }

    private fun openTextDocument(key: String) {
        val intent = Intent(this, ShowTextDocumentActivity::class.java)
        intent.putExtra("keyTermsAndCondition", key)
        startActivity(intent)
    }

    private fun changeSoundSetting() {
        if (isAudioChecked) {
            isAudioChecked = false
          //  switchAudio!!.isChecked = false
            switchAudio!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_off1))
            PreferenceConnector.writeBoolean(this, getString(R.string.is_audio_mode_enable),
                isAudioChecked)
        } else {
            isAudioChecked = true
          //  switchAudio!!.isChecked = true
            switchAudio!!.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.switch_on1))
            PreferenceConnector.writeBoolean(this, getString(R.string.is_audio_mode_enable),
                isAudioChecked)
        }
    }

    // after changing theme refresh activity.
    private fun recreateActivity() {
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun signOutAlertDialog() {
        val dialogSignOut = Dialog(this@SettingsActivity)
        if (dialogSignOut.window != null) {
            setDialogAttributes(dialogSignOut)
            dialogSignOut.setContentView(R.layout.layout_custom_dialog)
            val rlCustomDialog = dialogSignOut.findViewById<RelativeLayout>(R.id.rlCustomDialog)
            rlCustomDialog.background = AppCompatResources.getDrawable(this,R.drawable.bg_white_curve)
            val btnCustomDialogOk = dialogSignOut.findViewById<Button>(R.id.btnCustomDialogOk)
            btnCustomDialogOk.setOnClickListener { v: View? ->
                dialogSignOut.dismiss()
                PrefsHelper().clearAllPref()
                val intent = Intent(this, SplashScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //logOutFromApplication()
            }
            btnCustomDialogOk.setTextColor(resources.getColor(R.color.dark_orange))
            btnCustomDialogOk.text = "LOGOUT"
            val btnCustomDialogCancel = dialogSignOut.findViewById<Button>(R.id.btnCustomDialogCancel)
            btnCustomDialogCancel.visibility = View.VISIBLE
            btnCustomDialogCancel.setOnClickListener { dialogSignOut.dismiss() }
            val tvCustomDialogHeading = dialogSignOut.findViewById<TextView>(R.id.tvCustomDialogHeading)
            tvCustomDialogHeading.setTextColor(resources.getColor(R.color.dark_orange))
            tvCustomDialogHeading.text = "Don't leave us!"
            val tvCustomDialogDesc = dialogSignOut.findViewById<TextView>(R.id.tvCustomDialogDesc)
            tvCustomDialogDesc.text = getString(R.string.are_you_sure_do_you_want_to_sign_out_from_application)
            dialogSignOut.show()
            removeDialogAttributes(dialogSignOut)
            hideStatusBarForDialog(dialogSignOut)
        }
    }

   /* private fun logOutFromApplication() {
        try {
            fbAuth.signOut()
            PreferenceConnector.clearData(this)
            PreferenceConnector.writeBoolean(this,getString(R.string.is_first_time_welcome_screen)
            ,false)
            val intent = Intent(this, SplashScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
             Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: NullPointerException) {
            CommonMethods.errorLog("error while SignOut$e")
            showToast("Something went wrong")
            if (!PreferenceConnector.readString(this, getString(R.string.user_id), "").isEmpty()) {
                sendCrashlyticsDetails(e)
            }
        }
    }*/

    private fun saveFeedBack(feedback : String){
         showProgressBar(getString(R.string.loading_please_wait))
        val userdata : String = PrefsHelper().getPref(OwlizConstants.user_id)
        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.createReview(CreateFeedBackInput(userdata,"",feedback))
        requestCall.enqueue(object : Callback<CreateFeedBackResponse> {
            override fun onFailure(call: Call<CreateFeedBackResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("CreateFeedBack","FailureResponse $t")
                Toasty.error(this@SettingsActivity,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<CreateFeedBackResponse>, response: Response<CreateFeedBackResponse>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("CreateFeedBack", "Response:// $resp")
                    Toasty.success(this@SettingsActivity,"FeedBack Submitted",
                        Toasty.LENGTH_SHORT).show()
                    submitLayout!!.visibility = View.VISIBLE
                    inputLayout!!.visibility = View.GONE
                } else{
                    closeProgressbar()
                    Log.i("CreateFeedBack","ResponseError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(this@SettingsActivity, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(this@SettingsActivity, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(this@SettingsActivity, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(this@SettingsActivity, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {}

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }
}