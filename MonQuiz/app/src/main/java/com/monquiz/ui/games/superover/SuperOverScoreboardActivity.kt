package com.monquiz.ui.games.superover

import android.annotation.SuppressLint
import com.monquiz.ui.BaseActivity
import android.graphics.Bitmap
import android.os.Bundle
import com.monquiz.utils.PreferenceConnector
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.view.animation.RotateAnimation
import android.view.animation.LinearInterpolator
import android.content.Intent
import com.monquiz.ui.DashboardActivity
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import com.airbnb.lottie.LottieAnimationView
import com.github.jinatonic.confetti.ConfettiSource
import com.github.jinatonic.confetti.ConfettoGenerator
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.confetto.BitmapConfetto
import com.monquiz.R
import com.monquiz.network.InternetStateChecker
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.leaderboard.FinalScore_Input
import com.monquiz.response.leaderboard.input.LeaderBoard_InputData
import com.monquiz.response.leaderboard.resp.CheckSum_Resp
import com.monquiz.response.leaderboard.resp.LeaderBoard_Resp
import com.monquiz.response.superover.exit.GameExit_Response
import com.monquiz.response.superover.exit.Game_Lobby_Exit_Input
import com.monquiz.utils.Constants
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SuperOverScoreboardActivity : BaseActivity(), View.OnClickListener {

    private var headerBanner: ImageView? = null
    private var headerSunrise: ImageView? = null
    private var resultsClose: ImageView? = null
    private var ivUserPic: ImageView? = null

    private var tvScoreLostMargin: TextView? = null
    private var tvUserScore: TextView? = null
    private var tvUserName: TextView? = null
    private var tvMessage1: TextView? = null
    private var tvMessage2: TextView? = null
    private var tvPlayAmount: TextView? = null
    private var tvResultamount: TextView? = null

    private var tvGoToHome: TextView? = null
    private var tvSuperoverNextGameTimer: TextView? = null
    private var llOpponentDetails : LinearLayout? = null
    private var animation : LottieAnimationView? = null
    private var headerLayout : RelativeLayout? = null

    var ursscore:Long = 0
    private var opponentScore:Long = 0
    var username = ""
    var oponent:String = ""
    var stake = "0"
    var gameid = "0"
    var stakeId = "0"

    private var userId : String? = null
    private var opponentId : String? = null
    private var userWon = false
    private val allPossibleConfetti : MutableList<Bitmap> = ArrayList()
    private var clSuperOverParent : RelativeLayout? = null
    private lateinit var internetchecker : InternetStateChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_super_over_scoreboard)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        val bundle = intent.extras
        if (bundle != null) {
            stake = bundle.getString(OwlizConstants.stake_amount).toString()
            Log.e("Stake $this",stake)
            stakeId = bundle.getString(OwlizConstants.stake_Id).toString()
            gameid = bundle.getString(OwlizConstants.game_ID).toString()
        }
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        initializeUi()
        // initializeFields();
        initializeListeners()
        loadUsersDetails()
       // prepareCelebratoryBitmaps()
        getScoresFromApi()

      //  startSpiralAnimation()
    }

    private fun initializeUi() {
        headerBanner = findViewById(R.id.header_banner)
        headerSunrise = findViewById(R.id.header_sunrise)
        resultsClose = findViewById(R.id.results_close)
        ivUserPic = findViewById(R.id.ivUserPic)

        tvScoreLostMargin = findViewById(R.id.tvScoreLostMargin)
        tvUserScore = findViewById(R.id.tvUserScore)
        tvUserName = findViewById(R.id.tvUserName)
        tvResultamount = findViewById(R.id.tvResultamount)
        tvMessage1 = findViewById(R.id.tvmessage1)
        tvMessage2 = findViewById(R.id.tvmessage2)
        tvPlayAmount = findViewById(R.id.play_amount)

        tvSuperoverNextGameTimer = findViewById(R.id.tvSuperoverNextGameTimer)
        tvGoToHome = findViewById(R.id.tvGoToHome)
        clSuperOverParent = findViewById(R.id.clSuperOverParent)
        llOpponentDetails = findViewById(R.id.llOpponentDetails)
        animation = findViewById(R.id.animationView)
        headerLayout = findViewById(R.id.header_layout)
    }

    private fun loadUsersDetails() {
        opponentId = PrefsHelper().getPref(OwlizConstants.Op_id)
        userId = PrefsHelper().getPref(OwlizConstants.urs_id)
        ursscore = intent.getLongExtra("youScore",0)
        opponentScore = intent.getLongExtra("OpScore",0)
        oponent = PrefsHelper().getPref(OwlizConstants.oponentname)
        tvPlayAmount!!.text = "₹ $stake"
        val userName = PrefsHelper().getPref<String>(OwlizConstants.user_name)
        val displayPicture1 = PreferenceConnector.readString(this,
            getString(R.string.user_profile_pic), "")
        tvUserName!!.text = userName
        Glide.with(this).load(displayPicture1).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.ic_default_icon).into(ivUserPic!!)
        tvUserScore!!.text = "$ursscore Runs"
    }

    private fun initializeListeners() {
        tvGoToHome!!.setOnClickListener(this)
        tvSuperoverNextGameTimer!!.setOnClickListener(this)
        resultsClose!!.setOnClickListener { onBackPressed() }
    }

    private fun startSpiralAnimation() {
        val rotateAnimation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        if (userWon) { rotateAnimation.duration = 10000 }
        else { rotateAnimation.duration = 30000 }
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.startOffset = 0
        headerSunrise!!.startAnimation(rotateAnimation)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvSuperoverNextGameTimer -> {
                var moneyCount = 0.0
                val selectedStake = PreferenceConnector.readInteger(this, getString(R.string.play_selected_amount), 0).toDouble()
                val walletBalance = PreferenceConnector.readDouble(this, getString(R.string.user_wallet_balance), 0.0)
                val walletCredits = PreferenceConnector.readDouble(this, getString(R.string.user_wallet_credits), 0.0)
                moneyCount = PreferenceConnector.readLong(this, getString(R.string.user_daily_play_limit), 0).toDouble()
                val dailyCount = moneyCount + selectedStake
                if (dailyCount <= Constants.DAILY_LIMIT) {
                    if (walletBalance + walletCredits < selectedStake) { showDialogInsufficientBalance() }
                    else { yesRejoin() }
                } else {
                    Toasty.warning(this, getString(R.string.your_daily_limit_of_playing_for_500_points_exceeds),
                        Toasty.LENGTH_SHORT).show()
                }
            }
            R.id.tvGoToHome -> launchDashboardActivity()
        }
    }

    private fun showDialogInsufficientBalance() { launchDashboardActivity() }
    private fun launchDashboardActivity() { exitLobby("dashboard") }
    private fun yesRejoin() { exitLobby("rejoin") }

    private fun prepareCelebratoryBitmaps() {
        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inDither = false
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitti1 = BitmapFactory.decodeResource(resources, R.drawable.confetti_blue1, options)
        val bitti2 = BitmapFactory.decodeResource(resources, R.drawable.confetti_blue2, options)
        val bitti3 = BitmapFactory.decodeResource(resources, R.drawable.confetti_green1, options)
        val bitti4 = BitmapFactory.decodeResource(resources, R.drawable.confetti_green2, options)
        val bitti5 = BitmapFactory.decodeResource(resources, R.drawable.confetti_pink1, options)
        val bitti6 = BitmapFactory.decodeResource(resources, R.drawable.confetti_pink2, options)
        val bitti7 = BitmapFactory.decodeResource(resources, R.drawable.confetti_red1, options)
        val bitti8 = BitmapFactory.decodeResource(resources, R.drawable.confetti_red2, options)
        val bitti9 = BitmapFactory.decodeResource(resources, R.drawable.confetti_yellow1, options)
        val bitti10 = BitmapFactory.decodeResource(resources, R.drawable.confetti_yellow2, options)
        allPossibleConfetti.add(bitti1)
        allPossibleConfetti.add(bitti2)
        allPossibleConfetti.add(bitti3)
        allPossibleConfetti.add(bitti4)
        allPossibleConfetti.add(bitti5)
        allPossibleConfetti.add(bitti6)
        allPossibleConfetti.add(bitti7)
        allPossibleConfetti.add(bitti8)
        allPossibleConfetti.add(bitti9)
        allPossibleConfetti.add(bitti10)
    }

    @SuppressLint("SetTextI18n")
    private fun findTheWinnerFromUserDetails(winnerid: String, winnerscore: String,looserid: String,
                                             looserscore: String) {
        var userScore = 0
        var opponentScore = 0
        if (winnerid == PrefsHelper().getPref(OwlizConstants.user_id)){
            userScore = winnerscore.toInt()
            opponentScore = looserscore.toInt()
        }else{
            userScore = looserscore.toInt()
            opponentScore = winnerscore.toInt()
        }
        val margin = userScore - opponentScore

        Log.i("TAG","Scores:$userScore, $opponentScore,$margin")
        headerLayout!!.visibility = View.VISIBLE
        when {
            userScore > opponentScore -> {
                leaderBoardApi(looserid,looserscore.toInt(),winnerid,winnerscore.toInt())
             //   blastConfettiAtCenter()
                llOpponentDetails!!.visibility = View.VISIBLE
                headerBanner!!.setImageDrawable(AppCompatResources.getDrawable(this,
                    R.drawable.hero_banner_green))
                animation!!.visibility = View.VISIBLE
                llOpponentDetails!!.background = AppCompatResources.getDrawable(this,
                    R.drawable.bg_opponent_card_superover)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                tvScoreLostMargin!!.visibility = View.VISIBLE
                tvScoreLostMargin!!.text = "Won by $margin Runs "
                String.format(getString(R.string.won_super_over), margin.toString() + "")
                tvMessage1!!.text = "Congrats!"
                tvMessage2!!.text = "That was spectacular."
                val amt: Double = /*stake.toDouble()+*/ ((stake.toDouble() * 80) /100)
                tvResultamount!!.text = "₹ $amt Earned"
                userWon = true
            }
            userScore == opponentScore -> {
                headerBanner!!.setImageDrawable(AppCompatResources.getDrawable(this,
                    R.drawable.hero_banner_black))
                llOpponentDetails!!.visibility = View.VISIBLE
                llOpponentDetails!!.background = AppCompatResources.getDrawable(this,
                    R.drawable.bg_opponent_card_superover1)
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                animation!!.visibility = View.GONE
                tvScoreLostMargin!!.visibility = View.VISIBLE
                tvScoreLostMargin!!.text ="It's a Tie"
                tvMessage1!!.text = "Hard Luck"
                tvMessage2!!.text = "You gave your best."
                val amt: Double = ((stake.toDouble() * 90) /100)
                tvResultamount!!.text = "₹$amt Refunded"
                leaderBoardApi(looserid,looserscore.toInt(),winnerid,winnerscore.toInt())
            }
            else -> {
                headerBanner!!.setImageDrawable(AppCompatResources.getDrawable(this,
                    R.drawable.hero_banner_black))
                llOpponentDetails!!.visibility = View.VISIBLE
                llOpponentDetails!!.background = AppCompatResources.getDrawable(this,
                    R.drawable.bg_opponent_card_superover1)
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                animation!!.visibility = View.GONE
                tvScoreLostMargin!!.visibility = View.VISIBLE
                tvScoreLostMargin!!.text ="Missed by ${-margin} Runs "
                tvMessage1!!.text = "Hard Luck"
                tvMessage2!!.text = "You gave your best."
                tvResultamount!!.text = "₹$stake Spent"
                leaderBoardApi(looserid,looserscore.toInt(),winnerid,winnerscore.toInt())
            }
        }
        closeProgressbar()
    }

    private fun blastConfettiAtCenter() {
        clSuperOverParent!!.postDelayed({
            val containerMiddleX = clSuperOverParent!!.width
            val containerMiddleY = clSuperOverParent!!.height
            val confettiSourceMiddle = ConfettiSource(0, 0, containerMiddleX, 0)
            val confettoGenerator: ConfettoGenerator = getConfettiGenerator()
            val confettiManager = ConfettiManager(this, confettoGenerator,
                confettiSourceMiddle, clSuperOverParent)
                .setEmissionDuration(1000).setNumInitialCount(10)
                .setEmissionRate(100f).setVelocityX(0f, 40f)
                .setAccelerationX(0f, 0f)
                .setVelocityY(500f, 0f)
                .setAccelerationY(-300f, 300f)
                .setTargetVelocityY(50f)
                .setRotationalVelocity(0f, 50f)
            confettiManager.animate()
        }, 100)
        clSuperOverParent!!.postDelayed({
            val containerMiddleX = clSuperOverParent!!.width
            val containerMiddleY = clSuperOverParent!!.height
            val confettiSourceMiddle = ConfettiSource(0, 0, containerMiddleX / 4, 0)
            val confettoGenerator: ConfettoGenerator = getConfettiGenerator()
            val confettiManager = ConfettiManager(
                this, confettoGenerator, confettiSourceMiddle, clSuperOverParent)
                .setEmissionDuration(1000).setNumInitialCount(10)
                .setEmissionRate(30f).setVelocityX(0f, 40f)
                .setAccelerationX(0f, 0f)
                .setVelocityY(550f, 0f)
                .setAccelerationY(-300f, 50f).setTargetVelocityY(50f)
                .setRotationalVelocity(0f, 50f)
            confettiManager.animate()
        }, 100)
        clSuperOverParent!!.postDelayed({
            val containerMiddleX = clSuperOverParent!!.width
            val containerMiddleY = clSuperOverParent!!.height
            val confettiSourceMiddle = ConfettiSource(0, 0, 3 * containerMiddleX / 4, 0)
            val confettoGenerator: ConfettoGenerator = getConfettiGenerator()
            val confettiManager = ConfettiManager(
                this, confettoGenerator, confettiSourceMiddle, clSuperOverParent)
                .setEmissionDuration(1000).setNumInitialCount(10).setEmissionRate(30f)
                .setVelocityX(0f, 40f)
                .setAccelerationX(0f, 0f)
                .setVelocityY(550f, 0f)
                .setAccelerationY(-300f, 50f).setTargetVelocityY(50f)
                .setRotationalVelocity(0f, 50f)
            confettiManager.animate()
        }, 100)
        clSuperOverParent!!.postDelayed({
            val containerMiddleX = clSuperOverParent!!.width
            val containerMiddleY = clSuperOverParent!!.height
            val confettiSourceMiddle = ConfettiSource(0, 0, 50, 0)
            val confettoGenerator: ConfettoGenerator = getConfettiGenerator()
            val confettiManager = ConfettiManager(
                this, confettoGenerator, confettiSourceMiddle, clSuperOverParent)
                .setEmissionDuration(1000).setNumInitialCount(10).setEmissionRate(30f)
                .setVelocityX(0f, 40f)
                .setAccelerationX(0f, 0f)
                .setVelocityY(600f, 0f)
                .setAccelerationY(-300f, 50f).setTargetVelocityY(50f)
                .setRotationalVelocity(0f, 50f)
            confettiManager.animate()
        }, 100)
        clSuperOverParent!!.postDelayed({
            val containerMiddleX = clSuperOverParent!!.width
            val containerMiddleY = clSuperOverParent!!.height
            val confettiSourceMiddle =
                ConfettiSource(containerMiddleX - 50, 0, containerMiddleX, 0)
            val confettoGenerator: ConfettoGenerator = getConfettiGenerator()
            val confettiManager = ConfettiManager(
                this, confettoGenerator, confettiSourceMiddle, clSuperOverParent)
                .setEmissionDuration(1000).setNumInitialCount(10).setEmissionRate(30f)
                .setVelocityX(0f, 40f)
                .setAccelerationX(0f, 0f)
                .setVelocityY(600f, 0f)
                .setAccelerationY(-300f, 50f).setTargetVelocityY(50f)
                .setRotationalVelocity(0f, 50f)
            confettiManager.animate()
        }, 100)
    }

    private fun blastConfetti() {
        clSuperOverParent!!.postDelayed({
            val source = ConfettiSource(0, clSuperOverParent!!.height / 2,
                clSuperOverParent!!.width, clSuperOverParent!!.height / 2)
            val confettoGenerator = getConfettiGenerator()
            val confettiManager =
                ConfettiManager(this, confettoGenerator, source, clSuperOverParent)
                    .setVelocityX(0f, 20f)
                    .setVelocityY(-100f, -30f)
                    .setRotationalVelocity(0f, 20f)
                    .setTouchEnabled(true)
            confettiManager.setNumInitialCount(50).setEmissionDuration(500).animate()
        }, 100)
    }

    private fun getConfettiGenerator(): ConfettoGenerator {
        return ConfettoGenerator { random: Random ->
            val bitmap = allPossibleConfetti[random.nextInt(allPossibleConfetti.size)]
            BitmapConfetto(bitmap)
        }
    }

    override fun onBackPressed() {
       // super.onBackPressed()
        exitLobby("finish")
    }

    private fun exitLobby(goto : String){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= Game_Lobby_Exit_Input(PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.ExitFrom_Game(userdata)
        requestCall.enqueue(object : Callback<GameExit_Response> {
            override fun onFailure(call: Call<GameExit_Response>, t: Throwable) {
                closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<GameExit_Response>, response: Response<GameExit_Response>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("getAllGames", "GamesResponseLangLobby:// ${resp.toString()}")
                    if (goto == "dashboard"){
                        val i= Intent(applicationContext,DashboardActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                    }else if (goto == "rejoin"){
                        val i= Intent(applicationContext,SuperOverLobbyActivity::class.java)
                        i.putExtra(OwlizConstants.stake_amount, stake)
                        i.putExtra(OwlizConstants.stake_Id,stakeId)
                        i.putExtra(OwlizConstants.game_ID,gameid)
                        //i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                        finish()
                    }
                    else{
                        val intent = Intent(this@SuperOverScoreboardActivity,SuperOverActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                       // finish()
                    }
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(applicationContext, "not found",
                            Toasty.LENGTH_SHORT).show()
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

    private fun leaderBoardApi(looserId:String, looserScore:Int, WinnerId:String, WinnerScore:Int){
        //showProgressBar(getString(R.string.loading_please_wait))
        val stakes=PrefsHelper().getPref(OwlizConstants.stake_amount,"0").toInt()
        val userdata= LeaderBoard_InputData(looserId,looserScore,PrefsHelper().getPref(
            OwlizConstants.SuperOVer_RoomIDs),WinnerId,WinnerScore,stakes)

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.leaderBoard(userdata)
        requestCall.enqueue(object : Callback<LeaderBoard_Resp> {
            override fun onFailure(call: Call<LeaderBoard_Resp>, t: Throwable) {
             //   closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LeaderBoard_Resp>, response: Response<LeaderBoard_Resp>) {
                if(response.isSuccessful) {
                  //  closeProgressbar()
                    val resp = response.body()
                    Log.i("getAllGames", "GamesResponseLangLobby:// ${resp.toString()}")
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    /*when (response.code()) {
                        404 -> Toasty.error(applicationContext,
                            "not found", Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(applicationContext,
                            "server broken", Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(applicationContext,
                            "Bad GateWay", Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(applicationContext,
                            "unknown error", Toasty.LENGTH_SHORT).show()
                    }*/
                }
            }
        })
    }

    private fun getScoresFromApi() {
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata = FinalScore_Input(PrefsHelper().getPref(OwlizConstants.user_id),
            PrefsHelper().getPref(OwlizConstants.SuperOVer_RoomIDs))
        val token = "Bearer " + PrefsHelper().getPref(OwlizConstants.token)
        Log.e("inputData","$token roomID ;;; ${userdata.playRoomId}  userId ::: ${userdata.userId}")
        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getscores(userdata)
        requestCall.enqueue(object : Callback<CheckSum_Resp> {
            override fun onFailure(call: Call<CheckSum_Resp>, t: Throwable) {
                closeProgressbar()
                Log.e("scoreboard","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<CheckSum_Resp>, response: Response<CheckSum_Resp>) {
                if(response.isSuccessful) {
                    //  closeProgressbar()
                    val resp = response.body()
                    val data = resp!!.responseData
                    Log.e("Scoreboard", "GamesResponse:// $resp")
                    if (data != null){
                        val jsonObj = JSONObject(data.toString())
                        val winnerId = jsonObj.getString("winnerId")
                        val winnerScore = jsonObj.getString("winnerScore")
                        val looserId = jsonObj.getString("looserId")
                        val looserScore = jsonObj.getString("looserScore")
                        /*val winbyscore = jsonobj.getString("winByScore")
                        val losebyscore = jsonobj.getString("looseByScore")*/

                        findTheWinnerFromUserDetails(winnerId,winnerScore,looserId,looserScore)
                    }else{
                        Toasty.error(applicationContext, "Something Went Wrong",
                            Toasty.LENGTH_SHORT).show()
                    }
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    Toasty.error(applicationContext, "Something Went Wrong",
                        Toasty.LENGTH_SHORT).show()
                   /* when (response.code()) {
                        404 -> Toasty.error(applicationContext,
                            "not found", Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(applicationContext,
                            "server broken", Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(applicationContext,
                            "Bad GateWay", Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(applicationContext,
                            "unknown error", Toasty.LENGTH_SHORT).show()
                    }*/
                }
            }
        })
    }

    override fun onDestroy() {
      //  exitLobby("finish")
        internetchecker.stop()
        super.onDestroy()
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
    override fun onStop() {
        super.onStop()
        internetchecker.stop()
    }
    override fun onPause() {
        super.onPause()
        internetchecker.stop()
    }

}
