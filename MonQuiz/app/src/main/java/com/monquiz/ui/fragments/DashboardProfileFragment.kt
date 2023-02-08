package com.monquiz.ui.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.monquiz.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.monquiz.ui.SettingsActivity
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.monquiz.BuildConfig
import com.monquiz.adapter.AdapterAvatars
import com.monquiz.interfaces.AdapterCallback
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.network.RetrofitApi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.leaderboard.resp.GetLeaderBoardPositionResponse
import com.monquiz.response.wallet.res.GameDetails_Response
import com.monquiz.ui.AddProfilePicActivity
import com.monquiz.ui.BaseActivity.Companion.hideStatusBarForDialog
import com.monquiz.ui.FAQActivity
import com.monquiz.utils.*
import com.monquiz.utils.Constants.APK_SHARE_URL_FREE
import com.monquiz.utils.Constants.APK_WEBSITE_URL_PRO
import com.monquiz.utils.DialogUtils.Companion.removeDialogAttributes
import com.monquiz.utils.DialogUtils.Companion.setDialogAttributes
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception
import androidx.core.content.ContextCompat.checkSelfPermission


class DashboardProfileFragment : BaseFragment(), View.OnClickListener, AdapterCallback {
    private var ivProfileUserImage: CircleImageView? = null
    private var rlProfileImageLayout: RelativeLayout? = null
    private var rlProfileLeaderBoard: RelativeLayout? = null
    private var llProfileMyGameStats: LinearLayout? = null
    private var ivProfileQuestionMark: ImageView? = null
    private var ivProfileSetting: ImageView? = null
    private var ivProfileShareAppLink: ImageView? = null
    private var tvProfileUserName: TextView? = null
    private var tvProfileUserMobileNumber: TextView? = null
    private var tvProfileReferralCode: TextView? = null

    private var tvProfileStatsPlayed: TextView? = null
    private var tvProfileStatsWon: TextView? = null
    private var tvProfileStatsEarned: TextView? = null
    private var tvWinLoose : TextView? = null

    private var tvProfilePositioned: TextView? = null
    private var frmt = ""
    private lateinit var avatars: Array<String>
    private var dialogPickAvatar: Dialog? = null
    private var fromStorage = false
    private var filePath: File? = null
    private var selectedImage: String? = ""
    private var imageEditView : ImageView? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var IvShareClose :ImageView? = null
    private var tvRefferalshare : TextView?= null
    private var llShareWhatsApp : LinearLayout? = null
    private var llShareMessage : LinearLayout? = null
    private var copyRefferalView : ImageView? = null
    private var view1 : View? = null
    lateinit var permissionPreference : Permission

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        view1 = inflater.inflate(R.layout.layout_dashboard_profile_fragment, container, false)
        permissionPreference = Permission(requireContext())
        initializeUi(view1!!)
        initializeListeners()
        prepareDetails()
        getGamePlayDetails()
        getLeaderBoardDetails()
        return view1
    }

    @SuppressLint("SetTextI18n")
    private fun initializeUi(view: View) {

        avatars = resources.getStringArray(R.array.avatars_urls)
        val bottomsheet = view.findViewById<ConstraintLayout>(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        ivProfileSetting = view.findViewById(R.id.ivProfileSetting)
        imageEditView = view.findViewById(R.id.edit_view)
        tvProfileUserName = view.findViewById(R.id.tvProfileUserName)
        ivProfileUserImage = view.findViewById(R.id.ivProfileUserImage)

        tvProfileStatsWon = view.findViewById(R.id.tvProfileStatsWon)
        tvProfileStatsPlayed = view.findViewById(R.id.tvProfileStatsPlayed)
        tvProfileStatsEarned = view.findViewById(R.id.tvProfileStatsEarned)

        rlProfileImageLayout = view.findViewById(R.id.rlProfileImageLayout)
        rlProfileLeaderBoard = view.findViewById(R.id.rlProfileLeaderBoard)
        ivProfileQuestionMark = view.findViewById(R.id.ivProfileQuestionMark)
        ivProfileShareAppLink = view.findViewById(R.id.ivProfileShareAppLink)
        tvProfileUserMobileNumber = view.findViewById(R.id.tvProfileUserMobileNumber)
        tvProfileReferralCode = view.findViewById(R.id.tvProfileReferralCode)
        llProfileMyGameStats = view.findViewById(R.id.llProfileMyGameStats)
        tvProfilePositioned = view.findViewById(R.id.tvProfilePositioned)
        tvProfilePositioned!!.visibility = View.INVISIBLE
        tvWinLoose = view.findViewById(R.id.winorlosetextview)
    }

    private fun initializeListeners() {
        llProfileMyGameStats!!.setOnClickListener(this)
        ivProfileSetting!!.setOnClickListener(this)
        rlProfileLeaderBoard!!.setOnClickListener(this)
        ivProfileShareAppLink!!.setOnClickListener(this)
        ivProfileQuestionMark!!.setOnClickListener(this)
        imageEditView!!.setOnClickListener(this)
        tvProfileReferralCode!!.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun prepareDetails() {
        val won = PreferenceConnector.readString(activity, getString(R.string.user_stats_won_games), "0")
        val earned = PreferenceConnector.readDouble(activity, getString(R.string.user_stats_earned), 0.0)
        val played = PreferenceConnector.readString(activity,
            getString(R.string.user_stats_played_games), "0")
        val userName = PreferenceConnector.readString(activity, getString(R.string.user_name), "")
        val mobileNumber = PreferenceConnector.readString(activity, getString(R.string.user_mobile_number), "")
        val profilePic = PreferenceConnector.readString(activity, getString(R.string.user_profile_pic), "")
        val referralCode = PreferenceConnector.readString(activity, getString(R.string.user_referral_code), "")
        if (profilePic.isEmpty()){
            ivProfileUserImage!!.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.pro_pic))
            /*Glide.with(requireContext()).load(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_pro_pic))
                .placeholder(R.drawable.ic_pro_pic)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(ivProfileUserImage!!)*/
        }else{
            Glide.with(requireContext()).load(profilePic).placeholder(R.drawable.pro_pic)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(ivProfileUserImage!!)
        }
        tvProfileStatsWon!!.text = won
        tvProfileUserName!!.text = userName
        tvProfileStatsEarned!!.text = getString(R.string.ruppee) + StringUtil.getDecimalValue(earned)
        tvProfileStatsPlayed!!.text = played
        tvProfileReferralCode!!.text = referralCode
    }

    override fun onResume() {
        super.onResume()
        /*val profilePic = PreferenceConnector.readString(activity, getString(R.string.user_profile_pic), "")
        Glide.with(this).load(profilePic).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(
            ivProfileUserImage!!)*/
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivProfileSetting -> {
                val settingsIntent = Intent(activity, SettingsActivity::class.java)
                startActivity(settingsIntent)
            }
            R.id.ivProfileQuestionMark -> {
                val faqIntent = Intent(activity, FAQActivity::class.java)
                startActivity(faqIntent)
            }
            R.id.tvProfileReferralCode -> {
                val cm = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.text = tvProfileReferralCode!!.text
                Toasty.success(requireContext(), "Copied to clipboard",
                    Toasty.LENGTH_SHORT).show()
              //  showToast("Copied to clipboard")
            }
            R.id.ivProfileShareAppLink -> initializeShare()
            R.id.llProfileMyGameStats -> {}
            R.id.edit_view -> { openSelectAvatarDialog() }
            R.id.rlProfileLeaderBoard -> {}
            R.id.btnAvatarChooseFromGallary -> {
                if (dialogPickAvatar != null) dialogPickAvatar!!.dismiss()
                val intent = Intent(requireContext(), AddProfilePicActivity::class.java)
                // startActivity(intent)
                resultLauncher.launch(intent)
            }
            else -> { }
        }
    }

    private fun selectedImage(imageUrl: String?, isFromGallery: Boolean, comingFrom: String?) {
        Log.i("UsernameActivity","imageUrl$imageUrl :::comingFrom:/$comingFrom")
        fromStorage = false
        filePath = File(imageUrl)
        selectedImage = imageUrl
        ivProfileUserImage!!.isDrawingCacheEnabled = true
        Glide.with(this).asBitmap().load(filePath).placeholder(R.drawable.ic_default_icon)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(ivProfileUserImage!!)

        if (!fromStorage && filePath != null){
            PreferenceConnector.writeString(requireContext(), getString(R.string.user_profile_pic), selectedImage)
            postImages()
        }
    }

    private fun openDialogShare() {
        val activity: Activity? = activity
        if (activity != null && isAdded) {
            val dialogShare = Dialog(activity)
            if (dialogShare.window != null) {
                setDialogAttributes(dialogShare)
                dialogShare.setContentView(R.layout.layout_dialog_share)
                hideStatusBarForDialog(dialogShare)
                val ivShareDialogMessage = dialogShare.findViewById<ImageView>(R.id.ivShareDialogMessage)
                val ivShareDialogWhatsApp = dialogShare.findViewById<ImageView>(R.id.ivShareDialogWhatsApp)
                val ivShareDialogFacebook = dialogShare.findViewById<ImageView>(R.id.ivShareDialogFacebook)
                ivShareDialogFacebook.visibility = View.GONE
                ivShareDialogFacebook.setOnClickListener { shareApp("facebook", activity) }
                ivShareDialogWhatsApp.setOnClickListener { shareApp("whatsapp", activity) }
                ivShareDialogMessage.setOnClickListener { shareApp("messaging", activity) }
                dialogShare.show()
                removeDialogAttributes(dialogShare)
                hideStatusBarForDialog(dialogShare)
            }
        }
    }

    private fun openSelectAvatarDialog() {
        dialogPickAvatar = Dialog(requireContext())
        if (dialogPickAvatar!!.window != null) {
            setDialogAttributes(dialogPickAvatar!!)
            dialogPickAvatar!!.setContentView(R.layout.layout_dialog_pick_avatar)
            initDialogSelectAvatarUiElements()
            dialogPickAvatar!!.window!!.decorView.setBackgroundResource(R.drawable.bg_white_curve)
            dialogPickAvatar!!.show()
            removeDialogAttributes(dialogPickAvatar!!)
            hideStatusBarForDialog(dialogPickAvatar!!)
        }
    }

    private fun initDialogSelectAvatarUiElements() {
        val btnAvatarChooseFromGallery = dialogPickAvatar!!.findViewById<Button>(R.id.btnAvatarChooseFromGallary)
        btnAvatarChooseFromGallery.visibility = View.VISIBLE
         btnAvatarChooseFromGallery.setOnClickListener(this)
        val recyclerViewAvatar: RecyclerView = dialogPickAvatar!!.findViewById(R.id.recyclerViewAvatar)
        val adapterAvatars = AdapterAvatars(requireContext(), avatars,this)
        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerViewAvatar.layoutManager = layoutManager
        recyclerViewAvatar.itemAnimator = DefaultItemAnimator()
        recyclerViewAvatar.adapter = adapterAvatars
    }

    override fun onMethodCallback(urlValue: String?, position: Int) {
        PreferenceConnector.writeString(requireContext(), getString(R.string.user_profile_pic), urlValue)
        Glide.with(this).load(urlValue).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(ivProfileUserImage!!)
        dialogPickAvatar!!.dismiss()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        if (permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            if (requestCode == Constants.REQUEST_CODE_FOR_EXTERNAL_STORAGE_PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configureShare()
                }
            }else if (requestCode == 2){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) configureShare()
                    else Toasty.error(requireContext(),"Permissions Denied",Toasty.LENGTH_SHORT).show()
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun initializeShare() {
        if (isStoragePermissionGranted()){
            configureShare()
        }
    }

    private fun configureShare() {
        IvShareClose = view1!!.findViewById(R.id.ivShareClose)
        tvRefferalshare = view1!!.findViewById(R.id.tvReferralCode)
        llShareWhatsApp = view1!!.findViewById(R.id.llShareWhatsApp)
        llShareMessage  = view1!!.findViewById(R.id.llShareMessage)
        copyRefferalView = view1!!.findViewById(R.id.copyRefferalView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        val referralCode = PreferenceConnector.readString(activity, getString(R.string.user_referral_code), "")
        tvRefferalshare!!.text = referralCode
        copyRefferalView!!.setOnClickListener {
            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("ReferralCode", tvRefferalshare!!.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toasty.success(requireContext(), "Copied", Toasty.LENGTH_SHORT).show()
        }
        IvShareClose!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        llShareWhatsApp!!.setOnClickListener {
            shareApp("whatsapp", requireContext())
        }
        llShareMessage!!.setOnClickListener {
            shareApp("messaging", requireContext())
        }
    }

    private fun shareApp(type: String, activity: Context) {
        val referralCode = PreferenceConnector.readString(activity, getString(R.string.user_referral_code), "")
        frmt = if (BuildConfig.PRO_VERSION) {
            String.format(getString(R.string.invitation_message_free), APK_WEBSITE_URL_PRO)
        } else {
            String.format(getString(R.string.invitation_message_free), APK_SHARE_URL_FREE)
        }
        val shareText = frmt + " " + referralCode + " " + getString(R.string.to_get_bonus)
        var imageUri: Uri? = null
        imageUri = Uri.parse(MediaStore.Images.Media.insertImage(activity.contentResolver,
                BitmapFactory.decodeResource(resources, R.drawable.referral_poster1),
                "shareimage", null))
        when {
            type.equals("messaging", ignoreCase = true) -> { shareMessaging(shareText) }
            type.equals("whatsapp", ignoreCase = true) -> { shareWhatsApp(shareText, imageUri) }
            type.equals("facebook", ignoreCase = true) -> { shareFacebook(shareText, imageUri) }
        }
    }

    private fun shareMessaging(shareText: String) {
        try {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            sendIntent.putExtra("sms_body", shareText)
            sendIntent.type = "vnd.android-dir/mms-sms"
            startActivity(sendIntent)
        } catch (e: Exception) {
            CommonMethods.errorLog("shareMessaging err:$e")
        }
    }

    private fun shareFacebook(shareText: String, imageUri: Uri?) {
        try {
            val facebookIntent = Intent()
            facebookIntent.action = Intent.ACTION_SEND
            facebookIntent.setPackage("com.facebook.katana")
            facebookIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            facebookIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            facebookIntent.type = "image/*"
            facebookIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(facebookIntent)
            } catch (ex: ActivityNotFoundException) {
                Toasty.error(requireContext(), "Facebook have not been installed.",
                    Toasty.LENGTH_SHORT).show()
              //  showToast("Facebook have not been installed.")
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
                startActivity(i)
            }
        } catch (e: Exception) {
            CommonMethods.errorLog("shareFacebook err: $e")
                //BaseActivity().sendCrashlyticsDetails(e)
        }
    }

    private fun shareWhatsApp(shareText: String, imageUri: Uri?) {
        try {
            val whatsAppIntent = Intent()
            whatsAppIntent.action = Intent.ACTION_SEND
            whatsAppIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            whatsAppIntent.setPackage("com.whatsapp")
            whatsAppIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            whatsAppIntent.type = "image/*"
            whatsAppIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(whatsAppIntent)
                requireContext().cacheDir.delete()
            } catch (ex: ActivityNotFoundException) {
                Toasty.error(requireContext(),
                    "Whatsapp have not been installed.",
                    Toasty.LENGTH_SHORT).show()
               // showToast("Whatsapp have not been installed.")
               // BaseActivity().sendCrashlyticsDetails(ex)
            }
        } catch (e: Exception) {
            CommonMethods.errorLog("shareWhatsApp err: $e")
           // BaseActivity().sendCrashlyticsDetails(e)
        }
    }

    private fun postImages(){
        try {
            showProgressBar(getString(R.string.loading_please_wait))
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            val file = filePath
            // builder.addFormDataPart("UserId",UserId)
            builder.addFormDataPart("photo", file!!.name,
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
            val requestBody: MultipartBody = builder.build()
            val service = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
            val call: Call<ResponseBody> = service.uploadDp(PrefsHelper().getPref(OwlizConstants.user_id,""), requestBody)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        val result: String = response.body()?.charStream()?.readText().toString()
                        val res = response.errorBody()?.charStream()?.readText().toString()
                        if (result != "null") {
                            val jsonObj = JSONObject(result)
                            if (jsonObj.getString("status") == "200") {
                                PrefsHelper().savePref(OwlizConstants.username,true)
                                closeProgressbar()
                            }
                        } else {
                            val jsonObj = JSONObject(res)
                            val mes = jsonObj.getString("message")
                            Toasty.warning(requireContext(), mes, Toasty.LENGTH_SHORT).show()
                            closeProgressbar()
                        }
                    } catch (e: Exception) {
                        Log.e("profilePicUpload :", e.toString())
                        Toasty.warning(requireContext(), "Oops , Something went wrong",
                            Toasty.LENGTH_SHORT).show()
                        closeProgressbar()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("profilePicUpload", t.toString())
                    Toasty.warning(requireContext(), "Request Failed",
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

    private fun getGamePlayDetails(){
        showProgressBar(getString(R.string.loading_please_wait))

        val userid : String = PrefsHelper().getPref(OwlizConstants.user_id)
        val jsonObject = JSONObject()
        jsonObject.put("userId", userid)
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.getPlayDetails(requestBody)

        requestCall.enqueue(object : Callback<GameDetails_Response> {
            override fun onFailure(call: Call<GameDetails_Response>, t: Throwable) {
                closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<GameDetails_Response>, response: Response<GameDetails_Response>) {
                if(response.isSuccessful) {
                    val resp = response.body()
                    Log.i("getAllGames", "GateWalletResponse:// $resp")
                    if (resp!!.status == 200){
                        val totalGames = resp.responseData!!.gamesCount!!.toString()
                        val wonGames = resp.responseData!!.winCount!!.toString()
                        val winnings = resp.responseData!!.profit!!.toString()
                        onSuccess(totalGames,wonGames,winnings)
                    }
                } else{
                    closeProgressbar()
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

    private fun getLeaderBoardDetails(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userid : String = PrefsHelper().getPref(OwlizConstants.user_id)
        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
        val requestCall = destinationService.getLeaderBoard(GetUserProfileInput(userid))

        requestCall.enqueue(object : Callback<GetLeaderBoardPositionResponse> {
            override fun onFailure(call: Call<GetLeaderBoardPositionResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("GetLeaderBoard","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<GetLeaderBoardPositionResponse>,
                                    response: Response<GetLeaderBoardPositionResponse>) {
                if(response.isSuccessful) {
                    val resp = response.body()
                    Log.i("GetLeaderBoard", "GateWalletResponse:// $resp")
                    if (resp!!.status == 200){
                        var position = "001"
                        if (!resp.responseData.isNullOrEmpty()){
                            position = resp.responseData!!
                        }
                        onSuccess1(position)
                    }
                } else{
                    closeProgressbar()
                    Log.i("GetLeaderBoard","ResponseLangError:// ${response.errorBody().toString()}")
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
    private fun onSuccess(totalGames: String, wonGames: String, winnings: String) {
        tvProfileStatsPlayed!!.text = totalGames
        tvProfileStatsWon!!.text = wonGames
        if (winnings.startsWith("-")){
            tvProfileStatsEarned!!.text = "\u20B9 ${winnings.substring(1)}"
            tvWinLoose!!.text = "Lost"
        }else{
            tvProfileStatsEarned!!.text = "\u20B9 $winnings"
            tvWinLoose!!.text = "Earned"
        }
        closeProgressbar()
    }

    @SuppressLint("SetTextI18n")
    private fun onSuccess1(position: String) {
        tvProfilePositioned!!.visibility = View.VISIBLE
        tvProfilePositioned!!.text = "#$position"
        closeProgressbar()
    }

    private fun isStoragePermissionGranted(): Boolean {
        val isAskedPermissionBefore = permissionPreference.
        isPermissionRequestedBefore(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val isShowRational = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Log.e("permissions",isAskedPermissionBefore.toString())
        Log.e("permissions",isShowRational.toString())
        return if (checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG", "Permission is granted2")
            permissionPreference.setPermissionAllowed(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionPreference.setPermissionRequested(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            true
        } else {
            Log.v("TAG", "Permission is revoked2")
            if (isAskedPermissionBefore){
                if (isShowRational){
                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                    permissionPreference.setPermissionRequested(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }else{
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    this.startActivity(intent)
                }
            }else{
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                permissionPreference.setPermissionRequested(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            false
        }
    }

}