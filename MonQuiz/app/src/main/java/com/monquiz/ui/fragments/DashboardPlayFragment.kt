package com.monquiz.ui.fragments

import com.monquiz.interfaces.TimerCallBack
import android.animation.ObjectAnimator
import android.os.Bundle
import com.monquiz.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.animation.DecelerateInterpolator
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Spannable
import android.text.style.RelativeSizeSpan
import android.view.animation.LinearInterpolator
import android.view.View.OnTouchListener
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.monquiz.BuildConfig
import com.monquiz.eventbus.GameSelectEvent
import com.monquiz.listeners.DetectSwipeGestureListener
import com.monquiz.model.inputdata.updateprofile.GetUserProfileInput
import com.monquiz.ui.BaseActivity.Companion.hideStatusBarForDialog
import com.monquiz.utils.*
import com.monquiz.utils.Constants.TOTALMILISECONDSINDAY
import com.monquiz.utils.DialogUtils.Companion.removeDialogAttributes
import com.monquiz.utils.DialogUtils.Companion.setDialogAttributes
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.getGamesResponse.Get_GamesResponse
import com.monquiz.response.getdailylimit.GetDailyLimit_INput
import com.monquiz.response.getdailylimit.GetDailyLimit_Response
import com.monquiz.response.getstakesresp.GetAll_StakesResponse
import com.monquiz.response.getstakesresp.ResponseData
import com.monquiz.response.wallet.input.WalletInput
import com.monquiz.response.wallet.res.GameDetails_Response
import com.monquiz.response.wallet.res.Wallet_Response
import com.monquiz.ui.AddMoneyActivity
import com.monquiz.ui.SuperOverRulesActivity
import com.monquiz.ui.games.BannerModel
import com.monquiz.ui.games.adapters.PageClickListener
import com.monquiz.ui.games.adapters.ViewPagerAdapter
import com.monquiz.ui.games.superover.SuperOverActivity
import com.monquiz.ui.games.superover.SuperOverLobbyActivity
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode

import org.greenrobot.eventbus.Subscribe
import kotlin.collections.ArrayList


class DashboardPlayFragment : BaseFragment(), TimerCallBack ,PageClickListener{

    private var progressBarFragPlay: ProgressBar? = null
    private var tvFragPlayPlay: TextView? = null
    private var tvFragPlayClockHook: TextView? = null
    private var rlFragPlayPlayBtn: RelativeLayout? = null
    private var ivFragPlaySelect: ImageView? = null
    private var llFragPlayFree: LinearLayout? = null
    private var llFragPlayTen: LinearLayout? = null
    private var llFragPlayTwenty: LinearLayout? = null
    private var llFragPlayFifty: LinearLayout? = null
    private var llFragPlayHundred: LinearLayout? = null
    private var llFragPlayStakeLayout: LinearLayout? = null
    private var tvFragPlayFree: TextView? = null
    private var tvFragPlayCategoryTitle: TextView? = null
    var tvFragPlayTimer: TextView? = null
    private var listOfCategoryTitles: LinkedList<String>? = null
    private var listOfCategoryImages: LinkedList<String>? = null

    private var getallgameslist= mutableListOf<com.monquiz.response.getGamesResponse.ResponseData>()
    private var handlerBlink: Handler? = null
    private var runnableBlink: Runnable? = null
    private var isTimeRunning = true
    private var timerAnimatorForBtn: ObjectAnimator? = null

    // dailyQuiz vars
    private var dialogDailyQuiz: GestureDialog? = null
    private var dailyQuizProgressBar: ProgressBar? = null
    private var tvDailyQuizTimer: TextView? = null
    private var tvDailyQuizPlayQuiz: TextView? = null
    private var tvFragPlayTimer2: TextView? = null
    private var tvDailyQuizDesc: TextView? = null
    private var rlDailyQuizButtonPlayQuiz: RelativeLayout? = null
    private var dailyQuizDialogDismiss: View? = null
    private var ivDailyQuizShowResult: ImageView? = null
    private var imgoneshot: ImageView? = null

    private var initialState = true
    private var handlerflag = false

    var gamesarray = ArrayList<BannerModel>()
    var pager : ViewPager? = null
    var tvUserDesc : TextView? =  null
    var tvGamename : TextView? = null
    var tvGamedesc : TextView? = null
    var tvGameinfo : TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_dashboard_play_fragment, container, false)
        initializeUi(view)
        CommonMethods.infoLog("tableid Frag:" + PreferenceConnector.readString(
                activity, getString(R.string.normalquiz_table_id), ""))
        initializeListeners()
        getAllGames()

        if (!BuildConfig.PRO_VERSION) {
            timerForFreeVersion()
            handlerflag = true
        }

        // for open the dailyQuiz after click on push notification.
        val keyPlayDailyQuiz = PreferenceConnector.readBoolean(activity, "notifyPlayQuiz", false)
        if (keyPlayDailyQuiz) {
            if (!BuildConfig.PRO_VERSION) {
                openDailyQuizDialog()
            }
            PreferenceConnector.writeBoolean(activity, "notifyPlayQuiz", false)
        }
        return view
    }

    private fun initializeUi(view: View) {
        progressBarFragPlay = view.findViewById(R.id.progressBarFragPlay)
        tvFragPlayClockHook = view.findViewById(R.id.tvFragPlayClockHook)
        rlFragPlayPlayBtn = view.findViewById(R.id.rlFragPlayPlayBtn)
        tvFragPlayFree = view.findViewById(R.id.tvFragPlayFree)
        tvGameinfo = view.findViewById(R.id.tvgameinfo)
        ivFragPlaySelect = view.findViewById(R.id.ivFragPlaySelect)

        pager = view.findViewById(R.id.gameviewpager)
        pager!!.clipToPadding = false
        pager!!.setPadding(96, 0, 96, 0)
        pager!!.pageMargin = 24
        tvUserDesc = view.findViewById(R.id.userdesc)
        tvGamename = view.findViewById(R.id.tvgamename)
        tvGamedesc = view.findViewById(R.id.tvgamedesc)
        val username = PrefsHelper().getPref(OwlizConstants.user_name,"").toString()
        tvUserDesc!!.text = "Hello $username"
        addTempData()

        listOfCategoryTitles = LinkedList()
        listOfCategoryImages = LinkedList()
    }

    private fun addTempData() {
        gamesarray.clear()
        gamesarray.add(
            BannerModel(R.drawable.super_over_category,"SuperOver", R.drawable.batsmen_icon,
            "6 balls of cricketing questions."))
        gamesarray.add(BannerModel(R.drawable.crossword_category,"Cross Word",R.drawable.ic_cw_art,
            "War of words,be ready to battle"))
        gamesarray.add(BannerModel(R.drawable.dailyhit_category,"Daily Hit",R.drawable.ic_dailyhit_icon,
            "One time in a day."))
        gamesarray.add(BannerModel(R.drawable.team_up_category,"Team Up",R.drawable.ic_group_3442,
        "Bring your buddies together"))
        val gamesAdapter = ViewPagerAdapter(requireContext(),gamesarray,this)
        pager!!.adapter = gamesAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeListeners() {
        pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                if (gamesarray.isNotEmpty()) {
                    tvGamename!!.text = gamesarray[position].gamename
                    tvGamedesc!!.text = gamesarray[position].gamedescription
                }
                if (position == 0){
                    tvGameinfo!!.visibility = View.VISIBLE
                }else{
                   tvGameinfo!!.visibility = View.GONE
                }
            }
            override fun onPageScrollStateChanged(state: Int) { }
        })
        tvGameinfo!!.setOnClickListener {
          if (pager!!.currentItem == 0)  {
              startActivity(Intent(requireContext(),SuperOverRulesActivity::class.java))
          }
        }
    }


    val carouselFragmentInstance: CarouselFragment
    get() {
            val carouselFragment = CarouselFragment()
            val titleCallback: CarouselFragment.CarouselTitle =
                CarouselFragment.CarouselTitle { position ->
                    tvFragPlayCategoryTitle!!.text = "" + listOfCategoryTitles!![position]
                    //HIDE/SHOW STAKE LAYOUT
                    if (listOfCategoryTitles!![position].equals(getString(R.string.category_one_shot),
                            ignoreCase = true)) {
                        llFragPlayStakeLayout!!.alpha = 0.4.toFloat()
                        disableClicks()
                    } else {
                        llFragPlayStakeLayout!!.alpha = 1.0.toFloat()
                        //enableClicks()
                    }
                }
            carouselFragment.setParams(activity, listOfCategoryImages, listOfCategoryTitles, titleCallback)
            return carouselFragment
        }

    private fun timerForFreeVersion() {
        handlerBlink = Handler()
        runnableBlink = Runnable {
            doTask()
            timerForFreeVersion()
        }
        handlerBlink!!.postDelayed(runnableBlink!!, 1000)
    }

    override fun onPause() {
        super.onPause()
        handlerflag = false
        if (!BuildConfig.PRO_VERSION) {
            handlerBlink!!.removeCallbacks(runnableBlink!!)
        }
    }

    override fun onResume() {
        super.onResume()
       // showDailyPlayLimit()
       // getDailyLimit()
       // getwalletbalance()
        //AppController.getInstance().setFragment(this)
       // AppController.getInstance().setTimerContext(activity)
        if (!BuildConfig.PRO_VERSION) {
            if (!handlerflag) {
                timerForFreeVersion()
            }
        }
    }

    private fun doTask() {
        val text1 = SimpleDateFormat("EEEE", Locale.US).format(Date())
        val text2 = SimpleDateFormat("HH:mm", Locale.US).format(Date())
        tvFragPlayTimer2!!.text = text1
        tvFragPlayTimer2!!.letterSpacing = 0.1.toFloat()
        if (initialState) {
            // Reverse the boolean
            initialState = false
            // Change the TextView color to initial State
            val spannableColorString = SpannableString(text2)
            spannableColorString.setSpan(ForegroundColorSpan(Color.TRANSPARENT),
                text2.length - 3, text2.length - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableColorString.setSpan(RelativeSizeSpan(3f), 0, text2.length, 0) // set size
            tvFragPlayTimer!!.text = spannableColorString
        } else {
            // Reverse the boolean
            initialState = true
            // Change the TextView color to initial State
            val spannableColorString = SpannableString(text2)
            spannableColorString.setSpan(ForegroundColorSpan(Color.WHITE),
                text2.length - 3, text2.length - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableColorString.setSpan(RelativeSizeSpan(3f), 0, text2.length, 0) // set size
            tvFragPlayTimer!!.text = spannableColorString
        }
    }

    @SuppressLint("SetTextI18n")
    override fun timerCount(time: String?, millisUntilFinished: Long) {
        if (BuildConfig.PRO_VERSION) {
            if (activity != null) {
                var timer = ""
                val gameId = PreferenceConnector.readString(activity,
                    getString(R.string.current_game_id), "")
                val gameStarts = "Game #$gameId Starts in\n"
                timer = if (millisUntilFinished / 1000 < 10) {
                    "00:0" + millisUntilFinished / 1000
                } else {
                    "00:" + millisUntilFinished / 1000
                }
                val spannableString = SpannableString(gameStarts + "" + timer)
                spannableString.setSpan(RelativeSizeSpan(3f), gameStarts.length,
                    gameStarts.length + timer.length, 0) // set size
                tvFragPlayTimer!!.text = spannableString
                if (PreferenceConnector.readString(activity, getString(R.string.play_selected_category_name),
                        "") == getString(R.string.category_one_shot)) {
                    progressBarFragPlay!!.visibility = View.INVISIBLE
                    tvFragPlayClockHook!!.visibility = View.INVISIBLE
                    tvFragPlayTimer!!.visibility = View.INVISIBLE
                    rlFragPlayPlayBtn!!.background = resources.getDrawable(R.drawable.button_style)
                    tvFragPlayPlay!!.text = getString(R.string.play_game)
                    tvFragPlayPlay!!.isEnabled = true
                } else {
                    if (isTimeRunning) {
                        if (millisUntilFinished - 7000 > 0) {
                            showProgressButtonPlay(millisUntilFinished - 7000)
                            isTimeRunning = false
                        }
                    }
                    /*if (millisUntilFinished / 1000 < TABLE_LOCK_TIME) {
                        progressBarFragPlay!!.visibility = View.INVISIBLE
                        tvFragPlayClockHook!!.visibility = View.INVISIBLE
                        rlFragPlayPlayBtn!!.background =
                            resources.getDrawable(R.drawable.button_disabled_style)
                        tvFragPlayPlay!!.text = getString(R.string.join_next_game_in_play) + timer
                        tvFragPlayPlay!!.isEnabled = false
                    } else {
                        isTimeRunning = true
                        progressBarFragPlay!!.visibility = View.VISIBLE
                        tvFragPlayClockHook!!.visibility = View.VISIBLE
                        tvFragPlayTimer!!.visibility = View.VISIBLE
                        rlFragPlayPlayBtn!!.background =
                            resources.getDrawable(R.drawable.button_style)
                        tvFragPlayPlay!!.text = getString(R.string.play_game)
                        tvFragPlayPlay!!.isEnabled = true
                    }*/
                }
            }
        }
    }

    override fun transitionOnUserDetailsOrTimerUpdate(
        millisUntilFinished: Long, enoughUsersAndGotQuestion: Boolean) {
        /* code is for push user to table he joined.
        if (millisUntilFinished <= 10000 && enoughUsersAndGotQuestion) {
            Intent intent = new Intent(getActivity(), TableActivity.class);
            intent.putExtra("skipAnimation", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun showProgressButtonPlay(millisUntilFinished: Long) {
        if (timerAnimatorForBtn != null) { timerAnimatorForBtn!!.cancel() }
        val currentProgress = (53000 - millisUntilFinished) / 53
        val int_currentProgress = currentProgress.toInt()
        timerAnimatorForBtn = ObjectAnimator.ofInt(progressBarFragPlay,
            "progress", int_currentProgress, 1000)
        timerAnimatorForBtn!!.duration = millisUntilFinished
        timerAnimatorForBtn!!.interpolator = LinearInterpolator()
        timerAnimatorForBtn!!.start()
    }

    @SuppressLint("ClickableViewAccessibility", "ObjectAnimatorBinding")
    private fun openDailyQuizDialog() {
        dialogDailyQuiz = GestureDialog(activity, R.style.Theme_Dialog_daily_quiz)
        setDialogAttributes(dialogDailyQuiz!!)
        if (dialogDailyQuiz!!.window != null) {
            val detectSwipeUpGestureListener = DetectSwipeGestureListener(
                { dialogDailyQuiz!!.cancel() }
            ) {}
            dialogDailyQuiz!!.setContentView(R.layout.layout_dialog_daily_quiz)
            dialogDailyQuiz!!.window!!.setGravity(Gravity.TOP)
            dialogDailyQuiz!!.window!!.setWindowAnimations(R.style.DailyQuizDialogAnimation)
            dialogDailyQuiz!!.setSwipeListener(detectSwipeUpGestureListener)
            dialogDailyQuiz!!.show()
            removeDialogAttributes(dialogDailyQuiz!!)
            hideStatusBarForDialog(dialogDailyQuiz!!)
            initDailyQuizUiEle()
            val difference = nextDate
            val currentProgress: Long = TOTALMILISECONDSINDAY - difference
            val int_currentProgress = currentProgress.toInt()
            val animation = ObjectAnimator.ofInt(dailyQuizProgressBar, "progress",
                0, int_currentProgress)
            animation.duration = 2000
            animation.interpolator = DecelerateInterpolator()
            animation.start()
            dailyQuizDialogDismiss!!.setOnClickListener { v: View? -> dialogDailyQuiz!!.dismiss() }
            val gestureDetectorCompat = GestureDetectorCompat(requireContext(),
                detectSwipeUpGestureListener)
            val touchListener = OnTouchListener { v: View?, event: MotionEvent? ->
                val event1 : MotionEvent = event!!
                gestureDetectorCompat.onTouchEvent(event1)
            }
            dailyQuizDialogDismiss!!.setOnTouchListener(touchListener)
           /* if (dailyQuizScoreSummary != null) {
                tvDailyQuizPlayQuiz!!.text = "VIEW SUMMARY"
                rlDailyQuizButtonPlayQuiz!!.setOnClickListener { v: View? ->
                    val intent = Intent(activity, DailyQuizSummaryActivity::class.java)
                    intent.putExtra("summary", Gson().toJson(dailyQuizScoreSummary))
                    startActivity(intent)
                    dialogDailyQuiz!!.dismiss()
                }
            } else {
                tvDailyQuizPlayQuiz!!.text = "PLAY DAILY QUIZ"
                rlDailyQuizButtonPlayQuiz!!.setOnClickListener { v: View? ->
                    if (AppController.getInstance().getMapOfDailyChallenges() != null) {
                        val intent = Intent(activity, DailyQuizConfirmation::class.java)
                        startActivity(intent)
                        dialogDailyQuiz!!.dismiss()
                    } else {
                        showToast("Quiz is closed for now!")
                        //tvDailyQuizDesc.setText("");
                    }
                }
            }*/
            ivDailyQuizShowResult!!.setOnClickListener { v: View? ->
                dialogDailyQuiz!!.dismiss()
               /* val intent = Intent(activity, DailyQuizResultsActivity::class.java)
                startActivity(intent)*/
            }
            object : CountDownTimer(difference, 1000) {
                // adjust the milli seconds here
                @SuppressLint("DefaultLocale", "SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    var millisUntilFinished = millisUntilFinished
                    val secondsInMilli: Long = 1000
                    val minutesInMilli = secondsInMilli * 60
                    val hoursInMilli = minutesInMilli * 60
                    val elapsedHours = millisUntilFinished / hoursInMilli
                    millisUntilFinished %= hoursInMilli
                    val elapsedMinutes = millisUntilFinished / minutesInMilli
                    tvDailyQuizTimer!!.text =
                        "Time left: " + String.format("%dhr %dmins", elapsedHours, elapsedMinutes)
                }
                override fun onFinish() {}
            }.start()
        }
    }

    // get a date to represent "today"
    private val nextDate: Long
    get() {
            val diff: Long
            val calendar = Calendar.getInstance()
            // get a date to represent "today"
            val today = calendar.time

            // add one day to the date/calendar
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            // now get "tomorrow"
            val tomorrow = calendar.time
            // difference in millis
            diff = tomorrow.time - today.time
            return diff
        }

    private fun initDailyQuizUiEle() {
        tvDailyQuizDesc = dialogDailyQuiz!!.findViewById(R.id.tvDailyQuizDesc)
        imgoneshot = dialogDailyQuiz!!.findViewById(R.id.imgoneshot)
        ivDailyQuizShowResult = dialogDailyQuiz!!.findViewById(R.id.ivDailyQuizShowResult)
        dailyQuizProgressBar = dialogDailyQuiz!!.findViewById(R.id.dailyQuizProgressBar)
        tvDailyQuizTimer = dialogDailyQuiz!!.findViewById(R.id.tvDailyQuizTimer)
        dailyQuizDialogDismiss = dialogDailyQuiz!!.findViewById(R.id.dailyQuizDialogDismiss)
        rlDailyQuizButtonPlayQuiz = dialogDailyQuiz!!.findViewById(R.id.rlDailyQuizButtonPlayQuiz)
        tvDailyQuizPlayQuiz = dialogDailyQuiz!!.findViewById(R.id.tvDailyQuizPlayQuiz)
    }

    inner class GestureDialog(context: Context?, theme: Int) : Dialog(context!!, theme) {
        private var gestureDetectorCompat: GestureDetectorCompat? = null

        fun setSwipeListener(detectSwipeGestureListener: DetectSwipeGestureListener) {
            gestureDetectorCompat = GestureDetectorCompat(context, detectSwipeGestureListener)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            return gestureDetectorCompat != null && gestureDetectorCompat!!.onTouchEvent(event)
        }
    }

    private fun disableClicks() {
        llFragPlayFree!!.setOnClickListener(null)
        llFragPlayTen!!.setOnClickListener(null)
        llFragPlayTwenty!!.setOnClickListener(null)
        llFragPlayFifty!!.setOnClickListener(null)
        llFragPlayHundred!!.setOnClickListener(null)
    }

    private fun getAllGames(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getAllGames()
        requestCall.enqueue(object : Callback<Get_GamesResponse> {
            override fun onFailure(call: Call<Get_GamesResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("customersettingdata","FailureResponse $t")
                Toasty.error(requireContext(),"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<Get_GamesResponse>, response: Response<Get_GamesResponse>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.e("getallGames", "GamesResponseLang:// ${resp.toString()}")
                    if (resp!!.responseData!!.isNotEmpty()) {
                        getallgameslist = resp.responseData!!.toMutableList()
                        for (i in getallgameslist.indices) {
                            listOfCategoryTitles!!.add(getallgameslist[i].name!!)
                        }
                        for (j in getallgameslist.indices) {
                            listOfCategoryImages!!.add(getallgameslist[j].icon!!)
                        }
                        Log.i("TAG","listOfCategoryTitles:${listOfCategoryTitles}/$$listOfCategoryImages")
                    } else{
                        closeProgressbar()
                        Toasty.info(requireContext(),resp.message!!, Toasty.LENGTH_SHORT).show()
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

    override fun onStart() {
        super.onStart()
//        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
       // EventBus.getDefault().unregister(this)
    }


    override fun OnPageClicked(item: BannerModel,position : Int) {
        if (position == 0){
            if (checkInternet()){
                if (getallgameslist.isNotEmpty()){
                    var gameid = "0"
                    for (i in getallgameslist.indices){
                        if (getallgameslist[i].name == "Super Over"){
                            gameid = getallgameslist[i].id.toString()
                            break
                        }
                    }
                    PrefsHelper().savePref(OwlizConstants.game_ID,gameid)
                    val intent = Intent(requireContext(),SuperOverActivity::class.java)
                    intent.putExtra(OwlizConstants.game_ID,gameid)
                    startActivity(intent)
                }else{
                    Toasty.warning(requireContext(), "Failed to Fetch Game Details",
                        Toasty.LENGTH_SHORT).show()
                }
            }else{
                Toasty.error(requireContext(),"No Internet!.Please Check Your Connection",
                    Toasty.LENGTH_SHORT).show()
            }
        }else{
            Toasty.warning(requireContext(), "Coming soon...",
                Toasty.LENGTH_SHORT).show()
        }
    }

}