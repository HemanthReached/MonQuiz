package com.monquiz.ui.games.superover

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.monquiz.R
import com.monquiz.eventbus.SuperOver_Game_Event
import com.monquiz.interfaces.SuperOver_QtnDataInterface
import com.monquiz.network.EndPoints
import com.monquiz.network.InternetStateChecker
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.superover.exit.GameExit_Response
import com.monquiz.response.superover.exit.Game_Lobby_Exit_Input
import com.monquiz.response.superover.join.GameJoin_Response
import com.monquiz.response.superover.join.Question
import com.monquiz.response.superover.join.ResponseData
import com.monquiz.response.superover.join.input.SuperOver_JoinLobby_Input
import com.monquiz.ui.BaseActivity
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PreferenceConnector
import com.monquiz.utils.PrefsHelper
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.layout_super_over_lobby.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SuperOverLobbyActivity : BaseActivity(),SuperOver_QtnDataInterface {
    companion object{
        var responsedata: ResponseData?=null
        var qtnslistdata= mutableListOf<Question>()
    }
    var statuscode = 0
    private val firebaseAuth = FirebaseAuth.getInstance()
    var Game_RoomId=""
    var listnerqtn:SuperOver_QtnDataInterface?=null
    private var userId: String? = null
    private var ivLobbyUserPic: CircleImageView? = null
    private var ivLobbyOpponentPic: CircleImageView? = null
    private var ivusercap : ImageView? = null
    private var ivopponentcap : ImageView? = null
    private var ivBall: ImageView? = null
    private var ivEndAnim: ImageView? = null
    private var tvWaitingForUsers: TextView? = null
    private var tvLobbyUserName: TextView? = null
    private var tvLobbyOpponentName: TextView? = null
    private var tvQuestionMark: TextView? = null
    private var tvtimerview : TextView? = null
    private var tvVsview : TextView? = null
    private lateinit var internetchecker : InternetStateChecker
    private var bwalletpercentage = 0
    private var isExiting  = false
    private var isConnectedToUser = false
    var hits : Int = 0
    var stake = "0"
    var gameid = "0"
    var stakeId = "0"
    val handler = Handler()
    val  runnable = Runnable {
        if (statuscode != 1001){
            if(hits < 6){
                hits++;
                lobby()
            }else{
                exitLobby(true)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_super_over_lobby)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        val bundle = intent.extras
        if (bundle != null) {
            stake = bundle.getString(OwlizConstants.stake_amount).toString()
            stakeId = bundle.getString(OwlizConstants.stake_Id).toString()
            gameid = bundle.getString(OwlizConstants.game_ID).toString()
            bwalletpercentage = bundle.getInt(OwlizConstants.bonus_stakeamount)
        }
        Log.e("Intent Values ::::","$stake ,,,, $stakeId ,,,,$gameid")
        userId = firebaseAuth.uid
        listnerqtn= this
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        initializeUi()
    }

    private fun initializeUi() {
        ivLobbyUserPic = findViewById(R.id.ivLobbyUserPic)
        ivLobbyOpponentPic = findViewById(R.id.ivLobbyOpponentPic)
        ivusercap = findViewById(R.id.usercapview)
        ivopponentcap = findViewById(R.id.opponentcapview)
        ivBall = findViewById(R.id.ivBall)
        ivEndAnim = findViewById(R.id.ivEndAnim)
        tvWaitingForUsers = findViewById(R.id.tvWaitingForUsers)
        tvWaitingForUsers!!.text = String.format(getString(R.string.waiting_users_to_join), "..")
        val userName = PrefsHelper().getPref(OwlizConstants.user_name,"")
        val displayPicture = PrefsHelper().getPref(OwlizConstants.userprofile,"")
        val displayPicture1 = PreferenceConnector.readString(this,
            getString(R.string.user_profile_pic), "")
        tvLobbyUserName = findViewById(R.id.tvLobbyUserName)
        tvLobbyOpponentName = findViewById(R.id.tvLobbyOpponentName)
        tvtimerview = findViewById(R.id.timerview)
        tvVsview = findViewById(R.id.tv_vsview)
        tvLobbyUserName!!.text = userName
        Glide.with(this).load(displayPicture1).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.ic_default_icon).into(ivLobbyUserPic!!)
        tvQuestionMark = findViewById(R.id.tvQuestionMark)
    }

    private fun goToQuestionScreen() {
        if (!isExiting){
            listnerqtn!!.qtnsdata(responsedata!!,qtnslistdata)
            PrefsHelper().savePref(OwlizConstants.SuperOVer_RoomIDs,Game_RoomId)
            EventBus.getDefault().post(SuperOver_Game_Event(responsedata!!,qtnslistdata))
            hits = 0
            isConnectedToUser = false
            val i=Intent(applicationContext,SuperOverQuestionActivity::class.java)
            i.putExtra(OwlizConstants.SuperOVer_RoomID,Game_RoomId)
            i.putExtra(OwlizConstants.stake_amount, stake)
            Log.e("Stake $this",stake)
            i.putExtra(OwlizConstants.stake_Id,stakeId)
            i.putExtra(OwlizConstants.game_ID,gameid)
            startActivity(i)
            finish()
        }
    }

    private fun lobby(){
        //showProgressBar(getString(R.string.loading_please_wait))
        val stakeamount:Int = stake.toInt()
        var addbot = false
        if (hits >= 4){ addbot = true }

        val userdata = SuperOver_JoinLobby_Input(stakeamount, gameid , stakeId ,bwalletpercentage,
            PrefsHelper().getPref(OwlizConstants.user_id), addbot)
        Log.e("Input",userdata.toString())

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.GameJoin_SuperOver(userdata)
        requestCall.enqueue(object : Callback<GameJoin_Response> {
            override fun onFailure(call: Call<GameJoin_Response>, t: Throwable) {
             //   closeProgressbar()
                Log.e("SuperOverLobby","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
                exitLobby(false)
            }
            override fun onResponse(call: Call<GameJoin_Response>, response: Response<GameJoin_Response>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("getAllGames", "GamesResponseLangLobby:// ${resp.toString()}")
                    Log.e("StatusCode : ","${resp!!.code}")
                    if (resp!!.code == 1001){
                        statuscode = 1001
                        isConnectedToUser = true
                        handler.removeCallbacks(runnable)
                        responsedata= resp!!.responseData
                        qtnslistdata=resp!!.responseData!!.questions!!.toMutableList()
                        for(i in qtnslistdata.indices){
                            Log.e("Answer","${i+1} @#$%^&*   ${qtnslistdata[i].answer}")
                        }
                        Game_RoomId = resp.responseData!!.id.toString()
                        if (resp.responseData!!.players!!.isNotEmpty()){
                            val userdss = resp.responseData!!.players!!.find {
                                it.userId == PrefsHelper().getPref(OwlizConstants.user_id)}!!.userId
                            val oppdss= resp.responseData!!.players!!.find {
                                it.userId != PrefsHelper().getPref(OwlizConstants.user_id)}!!.userId

                            val userdss_nam = resp.responseData!!.players!!.find {
                                it.userId == PrefsHelper().getPref(OwlizConstants.user_id)}!!.username
                            val oppdss_name = resp.responseData!!.players!!.find {
                                it.userId != PrefsHelper().getPref(OwlizConstants.user_id)}!!.username
                            val oppdss_photo = resp.responseData!!.players!!.find{
                                it.userId != PrefsHelper().getPref(OwlizConstants.user_id)}!!.photo

                            Log.i("TAG","USerrID:$userdss")

                            PrefsHelper().savePref(OwlizConstants.Op_id,oppdss)
                            PrefsHelper().savePref(OwlizConstants.urs_id,userdss)
                            tvLobbyUserName!!.text = userdss_nam
                            val defaultprofile = PreferenceConnector.readString(this@SuperOverLobbyActivity,
                                getString(R.string.user_profile_pic), "")
                            Glide.with(applicationContext).load(resp!!.responseData!!.players!![0]!!.photo)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .error(defaultprofile).into(ivLobbyUserPic!!)
                            tvQuestionMark!!.visibility = View.INVISIBLE
                            tvLobbyOpponentName!!.text = oppdss_name
                           // String dp = mapOfUserDetails.get(key).displayPicture;
                            Glide.with(applicationContext).load(EndPoints.Base_UrlImage+oppdss_photo)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .error(R.drawable.ic_default_icon).into(ivLobbyOpponentPic!!)
                            PrefsHelper().savePref(OwlizConstants.oponentname,oppdss_name)
                           // PrefsHelper().savePref(OwlizConstants.username,userdss_nam)
                            PrefsHelper().savePref(OwlizConstants.oponentProfilepic,oppdss_photo)
                        }

                        // startBallAnimation()
                        if (!isExiting){
                            val handler1 = Handler()
                            handler1.postDelayed({ updateUI() }, 2000)
                        }
                    } else if(resp!!.code == 1002){
                        statuscode = 1002
                        handler.postDelayed(runnable, 3000)
                    } else if(resp!!.code == 1003){
                        statuscode = 1003
                        handler.postDelayed(runnable, 3000)
                    }else if(resp!!.code == 1006){
                        statuscode = 1006
                        Toasty.warning(this@SuperOverLobbyActivity,
                            "You are Playing In Another Game ,Please Try Again Later",
                            Toasty.LENGTH_SHORT).show()
                        onBackPressed()
                    }else{
                        statuscode = resp!!.code!!
                        onBackPressed()
                    }
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
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
                    onBackPressed()
                }
            }
        })
    }

    private fun updateUI() {
        ivStumps.setImageDrawable(AppCompatResources.getDrawable(this@SuperOverLobbyActivity,
            R.drawable.ic_stumps_big))
        val str = "Match is set." + System.getProperty("line.separator") + "Be ready."
        tvWaitingForUsers?.text = str
        ivLobbyUserPic?.visibility = View.GONE
        ivLobbyOpponentPic?.visibility = View.GONE
        tvLobbyUserName?.visibility = View.GONE
        tvLobbyOpponentName?.visibility = View.GONE
        tvQuestionMark?.visibility = View.GONE
        ivusercap?.visibility = View.GONE
        ivopponentcap?.visibility = View.GONE
        tvVsview?.visibility = View.GONE
        tvtimerview?.visibility = View.VISIBLE

        val timer = object : CountDownTimer(3000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                tvtimerview?.text  = "${seconds+1}"
            }
            override fun onFinish() {
                if (checkInternet()){ goToQuestionScreen()
                }else{
                   Toasty.error(this@SuperOverLobbyActivity,"No Internet!.Please Check Your Connection",
                       Toasty.LENGTH_SHORT).show()
                }
            }
        }
        timer.start()
    }

    fun exitLobby(b: Boolean) {
       showProgressBar(getString(R.string.loading_please_wait))
        val userdata= Game_Lobby_Exit_Input(PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.ExitFrom_Game(userdata)
        requestCall.enqueue(object : Callback<GameExit_Response> {
            override fun onFailure(call: Call<GameExit_Response>, t: Throwable) {
                 closeProgressbar()
                Log.i("exitLobby","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<GameExit_Response>, response: Response<GameExit_Response>) {
                if(response.isSuccessful) {
                    isExiting = true
                    closeProgressbar()
                    val resp = response.body()
                    Log.i("exitLobby", "GamesResponseLangLobby:// ${resp.toString()}")
                    if (b){
                        Toasty.warning(this@SuperOverLobbyActivity,
                            "No players available to connect",
                            Toasty.LENGTH_SHORT).show()
                    }
                    finish()
                } else{
                    closeProgressbar()
                    Log.i("exitLobby","ResponseLangError:// ${response.errorBody().toString()}")
                    Toasty.error(applicationContext, "Something Went Wrong",
                        Toasty.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun qtnsdata(qtnsdata: ResponseData, qtnslist: List<Question>) {
      Log.i("TAF","lobbyqtnsData:$qtnslist")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // super.onBackPressed()
        handler.removeCallbacks(runnable)
        if (!isConnectedToUser){
            exitLobby(false)
        }else{
            Toasty.warning(applicationContext,"Match started cannot exit now",
                Toasty.LENGTH_SHORT).show()
        }
        //leftloby()
    }
    override fun onResume() {
        super.onResume()
        if (hits == 0){ lobby() }
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