package com.monquiz.ui

import android.app.Activity
import android.app.Dialog

import com.monquiz.interfaces.AdapterCallback
import android.os.Bundle
import com.monquiz.R
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.view.inputmethod.EditorInfo
import android.text.TextWatcher
import android.text.Editable
import android.util.Log

import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.adapter.AdapterAvatars
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.model.inputdata.updateprofile.UpdateProfileInputBody
import com.monquiz.network.InternetStateChecker
import com.monquiz.response.login.LoginResponseOtp
import com.monquiz.response.updateprofile.Update_ProfileResponse
import com.monquiz.utils.*
import com.monquiz.network.RetrofitApi
import com.monquiz.network.ServiceBuilderForLocalHost
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.StringBuilder
import java.util.*

class UsernameActivity : BaseActivity(), View.OnClickListener,/* SelectedImageCallBack,*/
    AdapterCallback {
    private var filePath: File? = null
    private var btnUsernameConfirm: Button? = null
  //  private var ivUsernameProfilePic: ImageView? = null
  //  private var rlUsernameImageLayout: RelativeLayout? = null
    // private var storageRef: StorageReference? = null
    // private var databaseReference: DatabaseReference? = null
    // private val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var etUsernameEnterYourFullName: EditText? = null
    private var etUsernameEnterUserName: EditText? = null

    private var selectedImage: String? = ""
    private var userName = ""
    private var fullName = ""
    private var mobileNumber: String? = ""
    private var isReferralCodeEntered = false
    private var referralCode: String? = ""
    private var user_referral_code = ""
    private lateinit var avatars: Array<String>
    private var dialogPickAvatar: Dialog? = null
    private var fromStorage = false
    private lateinit var internetchecker : InternetStateChecker

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result:ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val bundle = data?.extras
            if (bundle != null){
                val imageUrl: String? = bundle.getString("imageUrl")
                val isFromGallery: Boolean = bundle.getBoolean("isFromGallery")
                val  comingFrom: String? = bundle.getString("comingFrom")
                selectedImage(imageUrl,isFromGallery,comingFrom)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)
      //  storageRef = FirebaseStorage.getInstance().getReference()
        //AppController.getInstance().setUsernameContext(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        val intent = intent
        if (intent != null) {
            mobileNumber = intent.getStringExtra(getString(R.string.intent_mobile_number))
            CommonMethods.infoLog("mobile: $mobileNumber")
            referralCode = intent.getStringExtra(getString(R.string.user_referral_code))
            isReferralCodeEntered = intent.getBooleanExtra(getString(R.string.is_valid_referral), false)
        }
        initializeUi()
        prepareDetails()
       // checkReferralBonus()
      // openSelectAvatarDialog()
    }

    private fun checkReferralBonus() {
        if (isReferralCodeEntered) {
          //  sendReferralCode()
        }
    }

   /* private fun sendReferralCode() {
        if (fbAuth.getCurrentUser() != null) {
            val reference = java.lang.String.format(
                getString(R.string.api_set_referral_to_db),
                fbAuth.getCurrentUser().getUid()
            )
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference().child(reference)
            databaseReference.setValue(referralCode)
        }
    }*/

    private fun initializeUi() {
        avatars = resources.getStringArray(R.array.avatars_urls)
        btnUsernameConfirm = findViewById(R.id.btnUsernameConfirm)
        btnUsernameConfirm!!.alpha = 0.6f
        btnUsernameConfirm!!.isEnabled = false
        etUsernameEnterUserName = findViewById(R.id.etUsernameEnterUserName)
        etUsernameEnterYourFullName = findViewById(R.id.etUsernameEnterYourFullName)
        /*ivUsernameProfilePic = findViewById(R.id.ivUsernameProfilePic)
        rlUsernameImageLayout = findViewById(R.id.rlUsernameImageLayout)
        inputLayoutUsernameEnterUserName = findViewById(R.id.inputLayoutUsernameEnterUserName)
        inputLayoutUsernameEnterYourFullName = findViewById(R.id.inputLayoutUsernameEnterYourFullName)*/
        selectedImage = Constants.DEFAULT_IMAGE_URL
        etUsernameEnterUserName!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkDetails()
                handled = true
            }
            handled
        }
        etUsernameEnterUserName!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !etUsernameEnterYourFullName!!.isFocused) { AppUtils.hideKeyboard(this,v) }
        }
        etUsernameEnterYourFullName!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !etUsernameEnterUserName!!.isFocused) { AppUtils.hideKeyboard(this,v) }
        }
        enableSubmitButton()
        getProfileData()
    }

    private fun enableSubmitButton() {
        etUsernameEnterUserName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val usernameTextLength = Objects.requireNonNull(
                    etUsernameEnterUserName!!.text).length
                val fullnameTextlength = Objects.requireNonNull(
                    etUsernameEnterYourFullName!!.text).length
                if (usernameTextLength > 0 && fullnameTextlength > 0) {
                    btnUsernameConfirm!!.alpha = 1f
                    btnUsernameConfirm!!.isEnabled = true
                } else {
                    btnUsernameConfirm!!.alpha = 0.6f
                    btnUsernameConfirm!!.isEnabled = false
                }
            }
        })
        etUsernameEnterYourFullName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val usernameTextLength = Objects.requireNonNull(
                    etUsernameEnterUserName!!.text).length
                val fullnameTextLength = Objects.requireNonNull(
                    etUsernameEnterYourFullName!!.text).length
                if (usernameTextLength > 0 && fullnameTextLength > 0) {
                    btnUsernameConfirm!!.alpha = 1f
                    btnUsernameConfirm!!.isEnabled = true
                } else {
                    btnUsernameConfirm!!.alpha = 0.6f
                    btnUsernameConfirm!!.isEnabled = false
                }
            }
        })
    }

    private fun prepareDetails() {
        btnUsernameConfirm!!.setOnClickListener(this)
      //  rlUsernameImageLayout!!.setOnClickListener(this)
       // generateReferralCode()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnUsernameConfirm -> checkDetails()
         //   R.id.rlUsernameImageLayout -> openSelectAvatarDialog()
            R.id.btnAvatarChooseFromGallary -> {
                if (dialogPickAvatar != null) dialogPickAvatar!!.dismiss()
                val intent = Intent(this, AddProfilePicActivity::class.java)
               // startActivity(intent)
                resultLauncher.launch(intent)
            }
            else -> {}
        }
    }

    private fun checkDetails() {
        userName = etUsernameEnterUserName!!.text.toString()
        fullName = etUsernameEnterYourFullName!!.text.toString()
        if (userName.isNotEmpty()) {
            if (fullName.isNotEmpty()) {
                if (fullName.length >= 4){
                    if (fromStorage){
                        PreferenceConnector.writeString(this,
                            getString(R.string.user_profile_pic), selectedImage)
                    }
                    upDateProfile(fullName,userName)
                    //gotoDashBoardActivity()
                    //serviceCall(userName, fullName)
                } else {
                    Toasty.error(this,
                        getString(R.string.please_enter_valid_full_name1),
                        Toasty.LENGTH_SHORT).show()
                  //  showToast(getString(R.string.please_enter_valid_full_name1))
                }
            }else{
                Toasty.error(this,
                    getString(R.string.please_enter_valid_full_name),
                    Toasty.LENGTH_SHORT).show()
              //  showToast(getString(R.string.please_enter_valid_full_name))
            }
        } else {
            Toasty.error(this,
                getString(R.string.please_enter_valid_user_name),
                Toasty.LENGTH_SHORT).show()
           // showToast(getString(R.string.please_enter_valid_user_name))
        }
    }

   /* private fun serviceCall(userName: String, fullName: String) {
        showProgressBar(getString(R.string.loading_please_wait))
        val checkUsernameQuery: Query =
            FirebaseDatabase.getInstance().getReference(getString(R.string.api_base_url_api_users))
                .orderByChild(getString(R.string.api_base_url_username)).equalTo(userName)
        checkUsernameQuery.addListenerForSingleValueEvent(object : ValueEventListener() {
            fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.getValue() == null) {
                        setUserImage()
                    } else {
                        closeProgressbar()
                        showToast(getString(R.string.user_name_already_exists))
                        etUsernameEnterUserName!!.setText("")
                    }
                } catch (exception: Exception) {
                    closeProgressbar()
                    exception.printStackTrace()
                    CommonMethods.errorLog("exception check username: " + exception.message)
                }
            }

            fun onCancelled(databaseError: DatabaseError) {
                closeProgressbar()
                CommonMethods.errorLog("databaseError check username: " + databaseError.getMessage())
            }
        })
    }

    private fun setUserImage() {
        try {
            if (filePath != null && !fromStorage) {
                CommonMethods.infoLog("image from gallry selected")
                if (fbAuth.getCurrentUser() != null) {
                    val reference = java.lang.String.format(
                        getString(R.string.api_displayPictures),
                        fbAuth.getCurrentUser().getUid()
                    )
                    val mountainsRef: StorageReference = storageRef.child(reference)
                    CommonMethods.infoLog("filePath: " + filePath.toString())
                    var uploadTask: UploadTask = mountainsRef.putFile(Uri.fromFile(filePath))
                    uploadTask.addOnFailureListener { exception -> CommonMethods.errorLog("exception uploadtask userimage: " + exception.getMessage()) }
                        .addOnSuccessListener { taskSnapshot -> CommonMethods.infoLog("username image uploadtask done:") }
                    val ref: StorageReference = storageRef.child(reference)
                    uploadTask = ref.putFile(Uri.fromFile(filePath))
                    val urlTask: Task<Uri> = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException())
                        }
                        ref.getDownloadUrl()
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful()) {
                            val downloadUri: Uri = task.getResult()
                            openLauncherLayout(downloadUri)
                            PreferenceConnector.writeBoolean(
                                this@UsernameActivity,
                                getString(R.string.is_first_time_login),
                                false
                            )
                        }
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        CommonMethods.errorLog("failure upload image please try again" + e.getMessage())
                    }
                } else {
                    showToast("Something went wrong, please try again..")
                }
            } else {
                CommonMethods.infoLog("avatar selected")
                CommonMethods.infoLog("selected image 2: $selectedImage")
                openLauncherLayout(Uri.parse(selectedImage))
                PreferenceConnector.writeBoolean(
                    this@UsernameActivity,
                    getString(R.string.is_first_time_login),
                    false
                )
            }
        } catch (e: Exception) {
            openLauncherLayout(Uri.parse(Constants.DEFAULT_IMAGE_URL))
            CommonMethods.errorLog("setuserimage: $e")
            sendCrashlyticsDetails(e)
        }
    }

    private fun openLauncherLayout(downloadUri: Uri?) {
        var profilePicUrl = ""
        profilePicUrl = downloadUri?.toString() ?: Constants.DEFAULT_IMAGE_URL
        val finalUrlData = profilePicUrl
        // final double balance = INITIAL_REWARD;
        if (fbAuth.getCurrentUser() != null) {
            val user = User(
                mobileNumber, fbAuth.getCurrentUser().getUid(),
                userName, profilePicUrl, fullName, "", "", "", user_referral_code
            )
            val refSetUserData = java.lang.String.format(
                getString(R.string.api_set_users),
                fbAuth.getCurrentUser().getUid()
            )
            databaseReference = FirebaseDatabase.getInstance().getReference(refSetUserData)
            databaseReference.setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    Utils().setUsersWalletBalance(this@UsernameActivity, this@UsernameActivity)
                    Utils().setNewUserDailyDetails(this@UsernameActivity)
                    storeDetails(finalUrlData)
                }
            }
                .addOnFailureListener { e -> CommonMethods.errorLog("user details stored failed " + e.getMessage()) }
            sendReferralCodeToDB()
        }
    }*/

    private fun gotoDashBoardActivity() {
        val dashboardIntent = Intent(this@UsernameActivity, DashboardActivity::class.java)
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        // TODO these intent for notifying user to get referral code bonus in dashboard
        val bundle = Bundle()
        bundle.putBoolean(getString(R.string.is_valid_referral), isReferralCodeEntered)
        bundle.putString(getString(R.string.intent_referral_code), referralCode)
        dashboardIntent.putExtras(bundle)
        startActivity(dashboardIntent)
        finish()
       // showToast(getString(R.string.account_created_successfully))
    }

   /* private fun sendReferralCodeToDB() {
        if (fbAuth.getCurrentUser() == null) {
            return
        }
        val refSendReferralCode = java.lang.String.format(
            getString(R.string.api_send_referral_code),
            fbAuth.getCurrentUser().getUid()
        )
        databaseReference = FirebaseDatabase.getInstance().getReference(refSendReferralCode)
        databaseReference.setValue(referralCode).addOnCompleteListener { task ->
            CommonMethods.infoLog(
                "username referral code send $referralCode"
            )
        }
    }

    private fun storeDetails(profilePicUrl: String) {
        CommonMethods.infoLog("mobile number saved:$mobileNumber")
        if (fbAuth.getCurrentUser() != null) {
            PreferenceConnector.writeString(
                this,
                getString(R.string.user_id),
                fbAuth.getCurrentUser().getUid()
            )
            PreferenceConnector.writeString(
                this,
                getString(R.string.user_profile_pic),
                profilePicUrl
            )
            PreferenceConnector.writeString(this, getString(R.string.user_name), userName)
            PreferenceConnector.writeString(this, getString(R.string.user_full_name), fullName)
            PreferenceConnector.writeString(
                this,
                getString(R.string.user_mobile_number),
                mobileNumber
            )
            PreferenceConnector.writeString(this, getString(R.string.user_dob), "")
            PreferenceConnector.writeString(this, getString(R.string.user_gender), "")
            PreferenceConnector.writeString(this, getString(R.string.user_pan_number), "")
            PreferenceConnector.writeString(
                this,
                getString(R.string.user_referral_code),
                user_referral_code
            )
            PreferenceConnector.writeBoolean(
                this,
                getString(R.string.is_valid_referral),
                isReferralCodeEntered
            )
            PreferenceConnector.writeLong(this, getString(R.string.user_daily_play_limit), 0)
            PreferenceConnector.writeBoolean(this, getString(R.string.is_reward_collected), false)
            PreferenceConnector.writeBoolean(
                this,
                getString(R.string.is_daily_limit_changed),
                false
            )
            gotoDashBoardActivity()
        } else {
            CommonMethods.infoLog("something went wrong..")
        }
    }*/

    private fun selectedImage(imageUrl: String?, isFromGallery: Boolean, comingFrom: String?) {
        Log.i("UsernameActivity","imageUrl$imageUrl :::comingFrom:/$comingFrom")
        fromStorage = false
        filePath = File(imageUrl)
        selectedImage = imageUrl
     /*   ivUsernameProfilePic!!.isDrawingCacheEnabled = true
        Glide.with(this).asBitmap().load(filePath).placeholder(R.drawable.ic_default_icon)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(ivUsernameProfilePic!!)*/
    }

    // for generating 5 num's and 3 alphanumeric referralCode
    private fun generateReferralCode() {
        val numbers = "1234567890"
        val numericStringBuilder = StringBuilder()
        val randomNumbers = Random()
        while (numericStringBuilder.length < 5) {
            val index = (randomNumbers.nextFloat() * numbers.length).toInt()
            numericStringBuilder.append(numbers[index])
        }
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val charactersStringBuilder = StringBuilder()
        val randomCharacters = Random()
        while (charactersStringBuilder.length < 3) {
            val index = (randomCharacters.nextFloat() * characters.length).toInt()
            charactersStringBuilder.append(characters[index])
        }
        user_referral_code = charactersStringBuilder.toString() + numericStringBuilder.toString()
        PreferenceConnector.writeString(this, getString(R.string.user_referral_code),
            user_referral_code)
        CommonMethods.infoLog("created referral code for user $user_referral_code")
        CommonMethods.infoLog("intent referral code for user $referralCode")
    }

    private fun openSelectAvatarDialog() {
        dialogPickAvatar = Dialog(this@UsernameActivity)
        if (dialogPickAvatar!!.window != null) {
            DialogUtils.setDialogAttributes(dialogPickAvatar!!)
            dialogPickAvatar!!.setContentView(R.layout.layout_dialog_pick_avatar)
            initDialogSelectAvatarUiElements()
            dialogPickAvatar!!.window!!.decorView.setBackgroundResource(R.drawable.bg_white_curve)
            dialogPickAvatar!!.show()
            DialogUtils.removeDialogAttributes(dialogPickAvatar!!)
            hideStatusBarForDialog(dialogPickAvatar!!)
        }
    }

    private fun initDialogSelectAvatarUiElements() {
        val btnAvatarChooseFromGallary =
            dialogPickAvatar!!.findViewById<Button>(R.id.btnAvatarChooseFromGallary)
        btnAvatarChooseFromGallary.visibility = View.VISIBLE
        btnAvatarChooseFromGallary.setOnClickListener(this)
        val recyclerViewAvatar: RecyclerView =
            dialogPickAvatar!!.findViewById(R.id.recyclerViewAvatar)
        val adapterAvatars = AdapterAvatars(this, avatars,this)
        val layoutManager = GridLayoutManager(this, 3)
        recyclerViewAvatar.layoutManager = layoutManager
        recyclerViewAvatar.itemAnimator = DefaultItemAnimator()
        recyclerViewAvatar.adapter = adapterAvatars
    }

    override fun onMethodCallback(urlValue: String?, position: Int) {
        fromStorage = true
        setDefaultUserIcon(position)
        filePath = File(urlValue)
        selectedImage = urlValue
    }

    private fun setDefaultUserIcon(position: Int) {
     /*   ivUsernameProfilePic!!.isDrawingCacheEnabled = true
        Glide.with(this).asBitmap().load(avatars[position])
            .placeholder(R.drawable.ic_default_icon)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(ivUsernameProfilePic!!)*/
        dialogPickAvatar!!.dismiss()
    }

    private fun getProfileData(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id))
        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.getProfile(userdata)
        requestCall.enqueue(object : Callback<LoginResponseOtp> {
            override fun onFailure(call: Call<LoginResponseOtp>, t: Throwable) {
                closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponseOtp>, response: Response<LoginResponseOtp>) {
                if(response.isSuccessful){
                    closeProgressbar()
                    val resp= response.body()
                    Log.i("customerSettingData","ResponseLang:// ${resp.toString()}")

                    if(resp!!.status==200){
                        if (resp.responseData.firstName!=""){
                            PrefsHelper().savePref(OwlizConstants.user_name, resp.responseData.userName)
                            etUsernameEnterYourFullName!!.setText(resp.responseData.firstName)
                        } else{ etUsernameEnterYourFullName!!.setText("") }

                        if (resp.responseData.userName!=""){
                            PrefsHelper().savePref(OwlizConstants.user_name, resp.responseData.userName)
                            etUsernameEnterUserName!!.setText(resp.responseData.userName)
                        } else{ etUsernameEnterUserName!!.setText("") }

                        /*if (resp.responseData.photo!=""){
                            PreferenceConnector.writeString(this@UsernameActivity,
                                getString(R.string.user_profile_pic), EndPoints.Base_Urlimage+resp.responseData.photo)
                            Glide.with(applicationContext).asBitmap().load(EndPoints.Base_Urlimage+resp.responseData.photo)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(ivUsernameProfilePic!!)
                        }*/
                        if (resp.responseData.referralCode != null){
                            PreferenceConnector.writeString(this@UsernameActivity,
                                getString(R.string.user_referral_code),
                                resp.responseData.referralCode)
                        }else{
                            PreferenceConnector.writeString(this@UsernameActivity,
                                getString(R.string.user_referral_code), "")
                        }
                    } else{
                        closeProgressbar()
                        Toasty.info(applicationContext,resp.message, Toasty.LENGTH_SHORT).show()
                    }
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

    //update profile

    private fun upDateProfile(name:String, username:String){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata = UpdateProfileInputBody("",name,"", "","",
            username,PrefsHelper().getPref(OwlizConstants.user_id,""))

        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.updateProfile(userdata)
        requestCall.enqueue(object : Callback<Update_ProfileResponse> {
            override fun onFailure(call: Call<Update_ProfileResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("updateProfile","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<Update_ProfileResponse>, response: Response<Update_ProfileResponse>) {
                if(response.isSuccessful){
                    closeProgressbar()
                    val resp= response.body()
                    Log.i("updateProfile","ResponseLang:// ${resp.toString()}")
                    if(resp!!.status==200){
                        if (resp.message == "updated") {
                            Toasty.success(applicationContext, "Updated", Toasty.LENGTH_SHORT).show()
                            PrefsHelper().savePref(OwlizConstants.username,true)
                            PrefsHelper().savePref(OwlizConstants.user_name, username)
                           /* if (!fromStorage && filePath != null){
                                PreferenceConnector.writeString(this@UsernameActivity,
                                    getString(R.string.user_profile_pic), selectedImage)
                                postImages()
                            }else{
                                PreferenceConnector.writeString(this@UsernameActivity,
                                    getString(R.string.user_profile_pic), selectedImage)
                                Handler().postDelayed({ gotoDashBoardActivity() },1000)
                            }*/
                            Handler().postDelayed({ gotoDashBoardActivity() },1000)
                        } else {
                            Toasty.info(applicationContext,resp.message!!, Toasty.LENGTH_SHORT).show()
                        }
                    } else{
                        closeProgressbar()
                        Toasty.info(applicationContext,resp.message!!, Toasty.LENGTH_SHORT).show()
                    }
                } else{
                    closeProgressbar()
                    Log.i("updateProfile","ResponseLangError:// ${response.errorBody().toString()}")
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

    private fun postImages(){
        try {
            showProgressBar(getString(R.string.loading_please_wait))
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            val file = filePath
            // builder.addFormDataPart("UserId",UserId)
            builder.addFormDataPart("photo", file!!.name,
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
            val requestBody: MultipartBody = builder.build()
            val service = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
            val call: Call<ResponseBody> = service.uploadDp(PrefsHelper().getPref(OwlizConstants.user_id,""), requestBody)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        val result: String = response.body()?.charStream()?.readText().toString()
                        val res = response.errorBody()?.charStream()?.readText().toString()
                        if (result != "null") {
                            val jsonobj = JSONObject(result)
                            if (jsonobj.getString("status") == "200") {
                                PrefsHelper().savePref(OwlizConstants.username,true)
                                Handler().postDelayed({ gotoDashBoardActivity() },1000)
                            }
                        } else {
                            val jsonobj = JSONObject(res)
                            val mes = jsonobj.getString("message")
                            Toasty.warning(applicationContext, mes,
                                Toasty.LENGTH_SHORT).show()
                            closeProgressbar()
                        }
                    } catch (e: Exception) {
                        Log.e("profilePicUpload :", e.toString())
                        Toasty.warning(applicationContext, "Oops , Something went wrong",
                            Toasty.LENGTH_SHORT).show()
                        closeProgressbar()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("profilePicUpload", t.toString())
                    Toasty.warning(applicationContext, "Request Failed",
                        Toasty.LENGTH_SHORT).show()
                    closeProgressbar()
                }
            })
        }catch (e : Exception){
            Log.e("Exception",e.toString())
            e.printStackTrace()
            closeProgressbar()
        }
    }

    private fun convertMediaUriToPath(uri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, proj, null, null, null)
        val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path: String = cursor.getString(columnIndex)
        cursor.close()
        return path
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