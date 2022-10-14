package com.monquiz.ui.games.superover


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.monquiz.R
import com.monquiz.interfaces.SuperOver_QtnDataInterface
import com.monquiz.network.EndPoints
import com.monquiz.network.InternetStateChecker
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.leftroom.input.LeftLobyy_Input
import com.monquiz.response.superover.exit.GameExit_Response
import com.monquiz.response.superover.qtns.Question
import com.monquiz.response.superover.qtns.submit.SuperOverQtn_Submit
import com.monquiz.response.superover.qtns.submit.resp.SuperOver_QtnSubmitResponse
import com.monquiz.services.SoundService
import com.monquiz.ui.BaseActivity
import com.monquiz.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.TimeUnit


class SuperOverQuestionActivity : BaseActivity(),SuperOver_QtnDataInterface{

    var ll:SuperOver_QtnDataInterface?=null
    var qtnsresponsedata:com.monquiz.response.superover.join.ResponseData?=null
    var qtnslists = mutableListOf<com.monquiz.response.superover.join.Question>()

    private var rlBall1: RelativeLayout? = null
    private var rlBall2: RelativeLayout? = null
    private var rlBall3: RelativeLayout? = null
    private var rlBall4: RelativeLayout? = null
    private var rlBall5: RelativeLayout? = null
    private var rlBall6: RelativeLayout? = null
    private var listOfBallsRl: LinkedList<RelativeLayout?>? = null
    private var tvBall1: TextView? = null
    private var tvBall2: TextView? = null
    private var tvBall3: TextView? = null
    private var tvBall4: TextView? = null
    private var tvBall5: TextView? = null
    private var tvBall6: TextView? = null
    private var ivBall1: ImageView? = null
    private var ivBall2: ImageView? = null
    private var ivBall3: ImageView? = null
    private var ivBall4: ImageView? = null
    private var ivBall5: ImageView? = null
    private var ivBall6: ImageView? = null
    private var tvUserScore1: TextView? = null
    private var tvOpponentScore1: TextView? = null
    //private var tvOpponentScore2: TextView? = null
    private var tvOpponentName: TextView? = null
    private var tvYourName : TextView? = null
    private var tvQuestion: TextView? = null
    private var tvOptionOne: TextView? = null
    private var tvOptionTwo: TextView? = null
    private var tvOptionThree: TextView? = null
    private var tvOptionFour: TextView? = null
    private var tvReadyDesc : TextView?= null
    private var IvPitch : ImageView? = null
    private var tvTimer : TextView? = null
    private var usl : LinearLayout? = null
    private var osl : LinearLayout? = null
    private var answerfgview : View? = null
    private var ivLobbyUserPic: CircleImageView? = null
    private var ivLobbyOpponentPic: CircleImageView? = null
    private var listOfTextViewOptions: LinkedList<TextView?>? = null
    private var listOfUserScores: LinkedList<TextView?>? = null
    private var selectedTextView: TextView? = null
    private var ivStumps: ImageView? = null
    private lateinit var questions: Array<Question>
    private var pgTimeLeft: ProgressBar? = null
    private var questionNumber = 0
    private var questionStartTime: Long = 0
    private var timertime : Int = 0
    private var answeredtime : Int = 0
    private var queTimer: CountDownTimer? = null
    private var timerAnimator: ObjectAnimator? = null
    private var isSelected = false
    private var userScore: Long = 0
    private var opponentScore: Long = 0
    private var progressDialog: Dialog? = null
    private var correctAnswerGiven : Boolean = false
    private var isbackpressed : Boolean = false
    var questionAnsweredTime : Long  = 0
    var userPhoto = ""
    var oppPhoto = ""
    private lateinit var internetchecker : InternetStateChecker
    var roomID="0"
    var stake = "0"
    var gameid = "0"
    var stakeId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_super_over_question)
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
            roomID = intent.getStringExtra(OwlizConstants.SuperOVer_RoomID).toString()
        }
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        ll = this
        initializeUi()
        initializeListeners()

        qtnslists = SuperOverLobbyActivity.qtnslistdata
        qtnsresponsedata = SuperOverLobbyActivity.responsedata
       /* Log.i("TAG","qtnslist:${qtnslists.size}")*/
        prepareOptionsTextViewOrder()
        prepareUserScoresListOrder()
        animateQuestion(qtnsresponsedata!!)
    }

    private fun initializeListeners() {
        userPhoto = PreferenceConnector.readString(this, getString(R.string.user_profile_pic),
            "")
        oppPhoto = PrefsHelper().getPref(OwlizConstants.oponentProfilepic)
        Glide.with(this).load(userPhoto).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.ic_default_icon).into(ivLobbyUserPic!!)
        Glide.with(applicationContext).load(EndPoints.Base_Urlimage+oppPhoto)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.ic_default_icon).into(ivLobbyOpponentPic!!)
        //  listenToJoinedTable()
        //  getTableScoreBoard()
    }

    private fun initializeUi() {
        ivStumps = findViewById(R.id.ivStumps)
        rlBall1 = findViewById(R.id.rlBall1)
        rlBall2 = findViewById(R.id.rlBall2)
        rlBall3 = findViewById(R.id.rlBall3)
        rlBall4 = findViewById(R.id.rlBall4)
        rlBall5 = findViewById(R.id.rlBall5)
        rlBall6 = findViewById(R.id.rlBall6)
        tvBall1 = findViewById(R.id.tvBall1)
        tvBall2 = findViewById(R.id.tvBall2)
        tvBall3 = findViewById(R.id.tvBall3)
        tvBall4 = findViewById(R.id.tvBall4)
        tvBall5 = findViewById(R.id.tvBall5)
        tvBall6 = findViewById(R.id.tvBall6)
        ivBall1 = findViewById(R.id.ivball1bgview)
        ivBall2 = findViewById(R.id.ivball2bgview)
        ivBall3 = findViewById(R.id.ivball3bgview)
        ivBall4 = findViewById(R.id.ivball4bgview)
        ivBall5 = findViewById(R.id.ivball5bgview)
        ivBall6 = findViewById(R.id.ivball6bgview)
        usl = findViewById(R.id.userscorelayout)
        osl = findViewById(R.id.opponentscorelayout)
        answerfgview = findViewById(R.id.answerfgview)
        ivLobbyUserPic = findViewById(R.id.ivUserPic)
        ivLobbyOpponentPic = findViewById(R.id.ivOpponentPic)
        rlBall1!!.visibility = View.INVISIBLE
        rlBall2!!.visibility = View.INVISIBLE
        rlBall3!!.visibility = View.INVISIBLE
        rlBall4!!.visibility = View.INVISIBLE
        rlBall5!!.visibility = View.INVISIBLE
        rlBall6!!.visibility = View.INVISIBLE
        tvReadyDesc = findViewById(R.id.tvreadydesc)
        tvTimer = findViewById(R.id.timerview)
        IvPitch = findViewById(R.id.ivPitch)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvOptionOne = findViewById(R.id.tvOptionOne)
        tvOptionTwo = findViewById(R.id.tvOptionTwo)
        tvOptionThree = findViewById(R.id.tvOptionThree)
        tvOptionFour = findViewById(R.id.tvOptionFour)
        pgTimeLeft = findViewById(R.id.pgTimeLeft)
        tvYourName = findViewById(R.id.tvYour)
        tvOpponentName = findViewById(R.id.tvOpponentName)
        tvUserScore1 = findViewById(R.id.tvUserScore1)
        tvOpponentScore1 = findViewById(R.id.tvOpponentScore1)

        tvYourName!!.text = PrefsHelper().getPref(OwlizConstants.user_name)
        tvOpponentName!!.text= PrefsHelper().getPref(OwlizConstants.oponentname)
    }

    private fun prepareOptionsTextViewOrder() {
        listOfTextViewOptions = LinkedList()
        listOfTextViewOptions!!.add(tvOptionOne)
        listOfTextViewOptions!!.add(tvOptionTwo)
        listOfTextViewOptions!!.add(tvOptionThree)
        listOfTextViewOptions!!.add(tvOptionFour)
    }

    private fun prepareUserScoresListOrder() {
        listOfUserScores = LinkedList()
        listOfUserScores!!.add(tvBall1)
        listOfUserScores!!.add(tvBall2)
        listOfUserScores!!.add(tvBall3)
        listOfUserScores!!.add(tvBall4)
        listOfUserScores!!.add(tvBall5)
        listOfUserScores!!.add(tvBall6)
        listOfBallsRl = LinkedList()
        listOfBallsRl!!.add(rlBall1)
        listOfBallsRl!!.add(rlBall2)
        listOfBallsRl!!.add(rlBall3)
        listOfBallsRl!!.add(rlBall4)
        listOfBallsRl!!.add(rlBall5)
        listOfBallsRl!!.add(rlBall6)
    }

    override fun qtnsdata(qtnsdata: com.monquiz.response.superover.join.ResponseData,
                          qtnslist: List<com.monquiz.response.superover.join.Question>) {
        Log.i("TAG","fdgQtnsData:$qtnslist")
        qtnslists = qtnslist.toMutableList()
        qtnsresponsedata = qtnsdata
        animateQuestion(qtnsresponsedata!!)
    }

    private fun startQuestionTimer() {
        if (queTimer != null) {
            queTimer!!.cancel()
        }
        queTimer = object : CountDownTimer(8000, 1) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timertime = millisUntilFinished.toInt()
            }
            override fun onFinish() {
                timertime = 0
                val intent = Intent(this@SuperOverQuestionActivity, SoundService::class.java)
                this@SuperOverQuestionActivity.stopService(intent)
                if (!isSelected){ updateSelectedAnswer(false) }
                submitResult(correctAnswerGiven)
                if(questionNumber < 5){
                    loadNextQuestion(correctAnswerGiven)
                }else{
                    if (queTimer != null) { queTimer!!.cancel() }
                    showTempProgressDialog(getString(R.string.computing_results))
                    Handler(Looper.getMainLooper()).postDelayed({
                            if (checkInternet()){ launchScoreBoardActivity() }else{
                                if (progressDialog != null) { progressDialog!!.dismiss() }
                                if (queTimer != null) { queTimer!!.cancel() }
                                Toasty.error(this@SuperOverQuestionActivity,
                                "No Internet!.Please Check Your Connection",Toasty.LENGTH_SHORT).show()
                                finish()
                            } }, 2000)
                }
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun loadNextQuestion(correctAnswerGiven: Boolean){
        val timer = object : CountDownTimer(3000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                tvTimer!!.text  = "${seconds+1}"
            }
            override fun onFinish() {
                questionNumber++
                showUI()
                animateQuestion(qtnsresponsedata!!)
            }
        }
        pgTimeLeft!!.progress = 0
        answerfgview!!.visibility = View.VISIBLE
        if (correctAnswerGiven){ tvQuestion!!.text = "Great shot!" }else{ tvQuestion!!.text = "Bowled!" }
        tvReadyDesc!!.visibility = View.VISIBLE
        val handler1 = Handler()
        handler1.postDelayed({ hideUI();timer.start(); },1000)
    }

    private fun hideUI() {
        /*rlBall1!!.visibility = View.INVISIBLE
        rlBall2!!.visibility = View.INVISIBLE
        rlBall3!!.visibility = View.INVISIBLE
        rlBall4!!.visibility = View.INVISIBLE
        rlBall5!!.visibility = View.INVISIBLE
        rlBall6!!.visibility = View.INVISIBLE

        ivBall1!!.visibility = View.INVISIBLE
        ivBall2!!.visibility = View.INVISIBLE
        ivBall3!!.visibility = View.INVISIBLE
        ivBall4!!.visibility = View.INVISIBLE
        ivBall5!!.visibility = View.INVISIBLE
        ivBall6!!.visibility = View.INVISIBLE*/
        tvQuestion!!.visibility = View.INVISIBLE
        tvReadyDesc!!.visibility = View.INVISIBLE
        IvPitch!!.visibility = View.VISIBLE
        tvTimer!!.visibility = View.VISIBLE
    }

    private fun showUI() {
        /*rlBall1!!.visibility = View.VISIBLE
        rlBall2!!.visibility = View.VISIBLE
        rlBall3!!.visibility = View.VISIBLE
        rlBall4!!.visibility = View.VISIBLE
        rlBall5!!.visibility = View.VISIBLE
        rlBall6!!.visibility = View.VISIBLE

        ivBall1!!.visibility = View.VISIBLE
        ivBall2!!.visibility = View.VISIBLE
        ivBall3!!.visibility = View.VISIBLE
        ivBall4!!.visibility = View.VISIBLE
        ivBall5!!.visibility = View.VISIBLE
        ivBall6!!.visibility = View.VISIBLE*/

        tvQuestion!!.visibility = View.VISIBLE
        tvQuestion!!.text = ""
        tvReadyDesc!!.visibility = View.GONE
        IvPitch!!.visibility = View.GONE
        tvTimer!!.visibility = View.GONE
    }

    private fun updateProgressBar() {
        if (timerAnimator != null) {
            timerAnimator!!.cancel()
        }
        val propName = "progress"
        pgTimeLeft!!.max = 8000
        timerAnimator = ObjectAnimator.ofInt(pgTimeLeft, propName!!, 0, 8000)
        timerAnimator!!.duration = 8000 // in milliseconds
        timerAnimator!!.interpolator = LinearInterpolator()
        timerAnimator!!.start()
    }

    private fun updateSelectedAnswer(AnswerGiven: Boolean) {
        val timetaken1: Int = 8000 - answeredtime
        val fScore1 = ScoreComputationUtils.getScoreSuperOver(timetaken1.toLong())
        Log.e("timetaken1 : ", "### $timetaken1")
        Log.e("fscore1", "*** $fScore1")

        val ball = listOfUserScores!![questionNumber]
        listOfBallsRl!![questionNumber]!!.visibility = View.VISIBLE
        ball!!.visibility = View.VISIBLE
        ball.setTextColor(resources.getColor(R.color.white))
        if (AnswerGiven) {
            ball.text = fScore1.toString()
        } else {
            ball.text = "0"
            ivStumps!!.setImageResource(R.drawable.ic_stumpsbroken_small)
        }

    }

    private fun submitResult(AnswerGiven: Boolean){
        val timetaken: Int = 8000 - answeredtime
        val fScore = ScoreComputationUtils.getScoreSuperOver(timetaken.toLong())
        if (checkInternet()){
            submitQtn(AnswerGiven,fScore)
        }else{
            Toasty.error(this,"No Internet!.Please Check Your Connection",
                Toasty.LENGTH_SHORT).show()
        }
    }

    private fun animateQuestion(res:com.monquiz.response.superover.join.ResponseData) {
        isSelected = false
        correctAnswerGiven = false
        for (option in listOfTextViewOptions!!) {
            option!!.visibility = View.INVISIBLE
        }
        if (questionNumber >= res.questions!!.size) {
                if (queTimer != null) {
                    queTimer!!.cancel()
                }
                showTempProgressDialog(getString(R.string.computing_results))
                Handler(Looper.getMainLooper()).postDelayed({
                    if (checkInternet()){ launchScoreBoardActivity() }else{
                        if (progressDialog != null) { progressDialog!!.dismiss() }
                        if (queTimer != null) { queTimer!!.cancel() }
                        Toasty.error(this,"No Internet!.Please Check Your Connection",
                            Toasty.LENGTH_SHORT).show()
                        finish()
                    }
                }, 2000)
                return
            }

        val optionss: Map<String, String>?
        val qutionss: Question?
       // for (i in res.questions!!.indices){ }
        val questionsss: String? = qtnslists[questionNumber].question
        val answers: String? = qtnslists[questionNumber].answer
        val optns = qtnslists[questionNumber].options
        if (optns.isNullOrEmpty()){
            optionss = mapOf("A" to "","B" to "","C" to "","D" to "").toMap()
        }else{
            optionss = optns.map { it.valX!! to it.text!! }.toMap()
        }

        qutionss = if (queTimer != null){
            Question(questionsss,optionss,answers,"","",
                "","",questionStartTime)
        } else{
            Question(questionsss,optionss,answers,"","","",
                "",0)
        }
        questions = arrayOf(qutionss)
        when(questionNumber){
            0 -> {ivBall1?.setImageDrawable(AppCompatResources.getDrawable(this,
                R.drawable.ic_redball_smallselected))}
            1 -> {ivBall2?.setImageDrawable(AppCompatResources.getDrawable(this,
                R.drawable.ic_redball_smallselected))}
            2 -> {ivBall3?.setImageDrawable(AppCompatResources.getDrawable(this,
                R.drawable.ic_redball_smallselected))}
            3 -> {ivBall4?.setImageDrawable(AppCompatResources.getDrawable(this,
                R.drawable.ic_redball_smallselected))}
            4 -> {ivBall5?.setImageDrawable(AppCompatResources.getDrawable(this,
                R.drawable.ic_redball_smallselected))}
            5 -> {ivBall6?.setImageDrawable(AppCompatResources.getDrawable(this,
                R.drawable.ic_redball_smallselected))}
        }

        populateQuestion(qutionss)
        Handler().postDelayed({
            answerfgview!!.visibility = View.GONE
            for (tv in listOfTextViewOptions!!) {
                val animate = TranslateAnimation(0f, 0f, (5 * tv!!.height).toFloat(), 0f)
                animate.duration = 800
                animate.fillAfter = true
                animate.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) { tv.isClickable = true }
                    override fun onAnimationRepeat(animation: Animation) {}
                })
                tv.isClickable = false
                tv.startAnimation(animate)
            }
        },1000)
        pgTimeLeft!!.progress = 0
        val handler = Handler()
        handler.postDelayed({ updateProgressBar() }, 1500)
    }

    private fun showTempProgressDialog(message: String) {
        progressDialog = Dialog(this)
        if (progressDialog!!.window != null) {
            progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            progressDialog!!.setContentView(R.layout.layout_progress_dialog)
            val titleTV = progressDialog!!.findViewById<TextView>(R.id.progressMessage)
            titleTV.text = message
            DialogUtils().blinkDot(titleTV, this, message)
            progressDialog!!.show()
            progressDialog!!.setOnDismissListener { dialog: DialogInterface? -> }
        }
    }

    private fun submitQtn(answerStatus:Boolean, fScore:Int){
        // showProgressBar(getString(R.string.loading_please_wait))
        val userdata = SuperOverQtn_Submit(answerStatus,roomID,
            PrefsHelper().getPref(OwlizConstants.user_id),fScore)
        Log.e("scoreData:","$fScore ,,,, $roomID")
        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.superOverSubmitQuestions(userdata)
        requestCall.enqueue(object : Callback<SuperOver_QtnSubmitResponse> {
            override fun onFailure(call: Call<SuperOver_QtnSubmitResponse>, t: Throwable) {
                // closeProgressbar()
                Log.e("submitQuestion","FailureResponse $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<SuperOver_QtnSubmitResponse>, response: Response<SuperOver_QtnSubmitResponse>) {
                if(response.isSuccessful) {
                    val res = response.body()!!

                    Log.i("TAG","submitQtn:$res")
                    try {
                        if (res.responseData!!.players!!.isNotEmpty()) {
                            if(res.responseData!!.players!![0].userId == PrefsHelper().getPref(OwlizConstants.user_id)){
                                PrefsHelper().savePref(OwlizConstants.Op_id, res.responseData!!.players!![1].userId)
                                PrefsHelper().savePref(OwlizConstants.urs_id, res.responseData!!.players!![0].userId)
                            }else{
                                PrefsHelper().savePref(OwlizConstants.Op_id, res.responseData!!.players!![0].userId)
                                PrefsHelper().savePref(OwlizConstants.urs_id, res.responseData!!.players!![1].userId)
                            }
                            var score: Double = res.responseData!!.players!![0].score!!.toDouble()
                            //   score = if (answerstatus) fScore.toDouble() else 0.0

                            score += if (answerStatus) fScore.toDouble() else 0.0

                            if (res.message == "score updated") {
                                val uScore: Long
                                val oScore: Long

                                //   tvUserScore1!!.text= res!!.error!!.players!![0]!!.score.toString()
                                // tvUserScore2!!.text= res!!.error!!.players!![0]!!.score.toString()
                                if(res.responseData!!.players!![0].userId == PrefsHelper().getPref(OwlizConstants.user_id)){
                                    uScore = res.responseData!!.players!![0].score!!.toLong()
                                    oScore = res.responseData!!.players!![1].score!!.toLong()
                                }else{
                                    uScore = res.responseData!!.players!![1].score!!.toLong()
                                    oScore = res.responseData!!.players!![0].score!!.toLong()
                                }
                                // tvOpponentScore1!!.text= res!!.error!!.players!![1]!!.score.toString()
                                // tvOpponentScore2!!.text= res!!.error!!.players!![1]!!.score.toString()
                                if (uScore > userScore) {
                                    increaseScore(userScore, uScore, tvUserScore1!!)
                                    userScore = uScore
                                }
                                if (oScore > opponentScore) {
                                    increaseScore(opponentScore, oScore, tvOpponentScore1!!)
                                    opponentScore = oScore
                                }
                            }
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                        Log.e("responseException",e.toString())
                        Toasty.warning(applicationContext, "Something Went Wrong",
                            Toasty.LENGTH_SHORT).show()
                    }
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> { Toasty.error(applicationContext, "not found",
                            Toasty.LENGTH_SHORT).show() }
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

    private fun launchScoreBoardActivity() {
       // notifyServer()
        if (progressDialog != null) { progressDialog!!.dismiss() }
        if (queTimer != null) { queTimer!!.cancel() }
        val intent= Intent(this,SuperOverScoreboardActivity::class.java)
        intent.putExtra("OpScore",opponentScore)
        intent.putExtra("youScore",userScore)
        intent.putExtra(OwlizConstants.stake_amount, stake)
        Log.e("Stake $this",stake)
        intent.putExtra(OwlizConstants.stake_Id,stakeId)
        intent.putExtra(OwlizConstants.game_ID,gameid)
        startActivity(intent)
        finish()
    }

    private fun populateQuestion(question: Question) {
    Log.i("TAG","listOfOptionKeys:${question.options}")

       // try {
            if (queTimer != null) { queTimer!!.cancel() }
            if (question.question != null) { tvQuestion!!.text = question.question }
            ivStumps!!.setImageResource(R.drawable.ic_stumps_small)

            /*tvQuestionCount.setText("" + (globalIndex + 1));

            Code for Image questions
            if (question.question_type != null) {
                String type = question.question_type;
                if (type.equals("image1")) {
                    ivQuestion.setVisibility(View.VISIBLE);
                    Glide.with(QuestionActivity.this).load(question.url)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .thumbnail(0.5f).into(ivQuestion);
                } else {
                    ivQuestion.setVisibility(View.GONE);
                }
            }

        Code for Image questions
            if (question.question_type != null) {
                String type = question.question_type;
                if (type.equals("image1")) {
                    ivQuestion.setVisibility(View.VISIBLE);
                    Glide.with(QuestionActivity.this).load(question.url)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .thumbnail(0.5f).into(ivQuestion);
                } else {
                    ivQuestion.setVisibility(View.GONE);
                }
            }*/

        val mapOfQuestionOptions:Map<String,String> = question.options!!
        Log.i("TAg","listOfOptions:$mapOfQuestionOptions")
        val listOfOptionsKeys: List<String> = ArrayList(mapOfQuestionOptions.keys)
        Log.i("TAg","listOfOptionsKeyskeys:$listOfOptionsKeys")

        Handler().postDelayed({
            for (tv in listOfTextViewOptions!!) {
                tv!!.isEnabled = true
                tv.background = AppCompatResources.getDrawable(this,R.drawable.bg_options_superover)
            }
            for (index in listOfOptionsKeys.indices) {
                listOfTextViewOptions!![index]!!.visibility = View.VISIBLE
                listOfTextViewOptions!![index]!!.id = index
                listOfTextViewOptions!![index]!!.tag = listOfOptionsKeys[index]
                // CommonMethods.infoLog("options: " + listOfOptionsKeys.get(index));
                listOfTextViewOptions!![index]!!.text =
                    mapOfQuestionOptions[listOfOptionsKeys[index]] // all options

                listOfTextViewOptions!![index]!!.setOnClickListener { view: View ->
                    selectedTextView = view as TextView
                    selectedTextView!!.setBackgroundResource(R.drawable.bg_options_superover_selected)
                    correctAnswerGiven = false
                    isSelected = true
                    questionAnsweredTime = currentTimeMillis()
                    answeredtime = timertime
                    question.options
                    Log.e("TAG,","selectedQtn's:${selectedTextView!!.tag },${question.answer}")
                    val selectedAnswer = selectedTextView!!.tag
                    for (i in listOfTextViewOptions!!.indices){
                        val viewtag = listOfTextViewOptions!![i]!!.tag
                        val isplaysound = PreferenceConnector.readBoolean(this,
                            getString(R.string.is_audio_mode_enable), true)
                        if (viewtag == selectedAnswer){
                            if (viewtag == question.answer){
                                correctAnswerGiven = true
                                listOfTextViewOptions!![i]!!.setBackgroundResource(R.drawable.bg_normal_quiz_correct_option)
                                if (isplaysound){ playSound(correctAnswerGiven) }
                                updateSelectedAnswer(correctAnswerGiven)
                            }else{
                                correctAnswerGiven = false
                                listOfTextViewOptions!![i]!!.setBackgroundResource(R.drawable.bg_normal_quiz_slected_option)
                                updateSelectedAnswer(correctAnswerGiven)
                                if (isplaysound){ playSound(correctAnswerGiven) }
                            }
                            break
                        }
                    }
                    /*if (selectedTextView!!.tag == question.answer) {
                        correctAnswerGiven = true
                        selectedTextView!!.setBackgroundResource(R.drawable.bg_normal_quiz_correct_option)
                    } else {
                        // logic for showing correct answer
                        for (option in listOfTextViewOptions!!) {
                            val correctAnswer = mapOfQuestionOptions1!![option!!.tag]
                            if (listOfCorrectAnswer.isNotEmpty()){
                                listOfCorrectAnswer.clear()
                            }
                            listOfCorrectAnswer.add(question.answer!!)
                            Log.i("TAG","listofAnswers:$listOfCorrectAnswer")
                            if (correctAnswer.equals((listOfCorrectAnswer[0]))) {
                                option.setBackgroundResource(R.drawable.bg_normal_quiz_correct_option)
                                // correct answer given
                               // playsound(correctAnswerGiven)
                                break
                            }
                        }
                    }*/
                    questionStartTime = currentTimeMillis()

                    for (option in listOfTextViewOptions!!) {
                        option!!.isClickable = false
                    }
                }
            }
        },1000)
        Handler().postDelayed({ startQuestionTimer() }, 1500)
       // startQuestionTimer()
    }

    private fun playSound(correctAnswerGiven: Boolean) {
        val intent = Intent(this, SoundService::class.java)
        intent.putExtra("sound",correctAnswerGiven)
        this.startService(intent)
    }

    private fun leftLobby(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= LeftLobyy_Input(roomID,PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.leftLobby(userdata)
        requestCall.enqueue(object : Callback<GameExit_Response> {
            override fun onFailure(call: Call<GameExit_Response>, t: Throwable) {
              // closeProgressbar()
                Log.e("leftlobby","Response $t")
                Toasty.error(applicationContext,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<GameExit_Response>, response: Response<GameExit_Response>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    Log.e("leftlobby", "Response:// ${resp.toString()}")
                    if (isbackpressed){
                        isbackpressed = false
                        finish()
                    }
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    Toasty.error(applicationContext, "Something Went Wrong",
                        Toasty.LENGTH_SHORT).show()
                    /*when (response.code()) {
                        404 -> Toasty.error(applicationContext, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(applicationContext, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(applicationContext, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(applicationContext, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }*/
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun increaseScore(from: Long, to: Long, tvScore1: TextView) {
        val animator: ValueAnimator = ValueAnimator.ofInt(from.toInt(), to.toInt())
        animator.duration = 250 * ((to - from) % 12)
        animator.addUpdateListener { animation ->
            val animVal = animation.animatedValue as Int
            tvScore1.text = (animVal / 10).toString() + ""+(animVal % 10).toString() + ""
        }
        animator.start()
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        internetchecker.start()
    }
    override fun onPause() {
        super.onPause()
        internetchecker.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        //  EventBus.getDefault().unregister(this)
        // leftloby()
        internetchecker.stop()
    }
    override fun onStop() {
        super.onStop()
        internetchecker.stop()
    }
    override fun onBackPressed() {
        // super.onBackPressed()
        isbackpressed = true
        if (queTimer != null) { queTimer!!.cancel() }
        leftLobby()
    }

}