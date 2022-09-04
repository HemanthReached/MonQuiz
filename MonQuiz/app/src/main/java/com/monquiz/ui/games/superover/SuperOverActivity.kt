package com.monquiz.ui.games.superover

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.monquiz.BuildConfig
import com.monquiz.R
import com.monquiz.network.InternetStateChecker
import com.monquiz.network.Retrofitapi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.getstakesresp.GetAll_StakesResponse
import com.monquiz.response.getstakesresp.ResponseData
import com.monquiz.response.wallet.input.WalletInput
import com.monquiz.response.wallet.res.Wallet_Response
import com.monquiz.ui.BaseActivity
import com.monquiz.ui.DashboardActivity
import com.monquiz.ui.SuperOverRulesActivity
import com.monquiz.utils.DialogUtils
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuperOverActivity : BaseActivity() {

    private var tvPlayGame : TextView? = null
    private var tvStake1 : TextView? = null
    private var tvStake2 : TextView? = null
    private var tvStake3 : TextView? = null
    private var tvStake4 : TextView? = null
    private var tvStake5 : TextView? = null

    private var playForAmount : TextView? = null
    private var gameWalletPercentage : TextView? = null
    private var gameWalletAmount : TextView? = null
    private var bonusWalletPercentage : TextView? = null
    private var bonusWalletAmount : TextView? = null

    private var sspercentage1 : LinearLayout? = null
    private var sspercentage2 : LinearLayout? = null
    private var sspercentage3 : LinearLayout? = null
    private var sspercentage4 : LinearLayout? = null
    private var sspercentage5 : LinearLayout? = null
    private var sspercentage6 : LinearLayout? = null
    private var zeroPercentAmount : TextView? = null
    private var tenPercentAmount : TextView? = null
    private var twentyPercentAmount : TextView? = null
    private var thirtyPercentAmount : TextView? = null
    private var fortyPercentAmount : TextView? = null
    private var fiftyPercentAmount : TextView? = null

    private var selectionView : View? = null
    private var walletContinueBtn : TextView? = null
    private var winningAmount : TextView? = null
    private var bonusWalletBalance : TextView? = null

    private var tvStakeSelection : TextView? = null
    private var stakesLayout : LinearLayout? = null
    private var stakeselction_layout : ConstraintLayout? = null
    private var walletselection_layout : ConstraintLayout? = null
    private var stake = "0"
    private var position = 0
    private var walletBalance = 0.0
    var walletAmount = 0.0
    var bonuswallet = 0.0

    var gWalletPercentage = 0
    var bWalletPercentage = 0

    var gWalletAmount = 0
    var bWalletAmount = 0
    private var dialogInsufficientBalance: Dialog? = null

    private var Ivgametype : ImageView? = null
    private var tvGameName : TextView? = null
    private var Ivclose : ImageView?= null
    private var stakeslist= mutableListOf<ResponseData>()
    var gameid = "0"
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var ivClose :ImageView? = null
    private var tvSuperOverRules : TextView?= null
    private var ivInfoIcon : ImageView? = null
    private lateinit var internetchecker : InternetStateChecker
    var selectedview = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_superover)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        val bundle = intent.extras
        if (bundle != null) { gameid = bundle.getString(OwlizConstants.game_ID).toString() }

        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        initializeUI()
        initializeListeners()
        stakeselction_layout!!.visibility = View.GONE
        getStakes()
        getWalletBalance()
    }

    private fun initializeUI() {
        tvPlayGame = findViewById(R.id.tvplaygame)
        tvStake1 = findViewById(R.id.tvstake1)
        tvStake2 = findViewById(R.id.tvstake2)
        tvStake3 = findViewById(R.id.tvstake3)
        tvStake4 = findViewById(R.id.tvstake4)
        tvStake5 = findViewById(R.id.tvstake5)

        val bottomsheet = findViewById<ConstraintLayout>(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        ivClose = findViewById(R.id.ivCloserules)
        tvSuperOverRules = findViewById(R.id.tvsuperoverRules)
        val content = SpannableString("Super Over Rules")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        tvSuperOverRules!!.text = content
      //  tvSuperOverRules!!.text = Html.fromHtml(getString(R.string.your_string_here))
        ivInfoIcon = findViewById(R.id.info_icon)

        playForAmount = findViewById(R.id.playfor_amount)
        gameWalletPercentage = findViewById(R.id.gamewallet_percentage)
        gameWalletAmount = findViewById(R.id.gamewallet_amount)
        bonusWalletPercentage = findViewById(R.id.bonuswallet_percentage)
        bonusWalletAmount = findViewById(R.id.bonuswallet_amount)

        sspercentage1 = findViewById(R.id.linearLayout2)
        sspercentage2 = findViewById(R.id.linearLayout)
        sspercentage3 = findViewById(R.id.linearLayout3)
        sspercentage4 = findViewById(R.id.linearLayout4)
        sspercentage5 = findViewById(R.id.linearLayout5)
        sspercentage6 = findViewById(R.id.linearLayout6)
        zeroPercentAmount = findViewById(R.id.zero_percent_amount)
        tenPercentAmount = findViewById(R.id.ten_percent_amount)
        twentyPercentAmount = findViewById(R.id.twenty_percent_amount)
        thirtyPercentAmount = findViewById(R.id.thirty_percent_amount)
        fortyPercentAmount = findViewById(R.id.forty_percent_amount)
        fiftyPercentAmount = findViewById(R.id.fifty_percent_amount)

        walletContinueBtn = findViewById(R.id.walletContinue_btn)
        selectionView = findViewById(R.id.selection_view)
        winningAmount = findViewById(R.id.winning_amount)
        bonusWalletBalance = findViewById(R.id.bonuswallet_balance)

        tvStakeSelection = findViewById(R.id.stakeselctiondesc)
        Ivclose = findViewById(R.id.iv_close)
        stakesLayout = findViewById(R.id.stakeslayout)
        stakeselction_layout = findViewById(R.id.stakeselction_layout)
        walletselection_layout = findViewById(R.id.walletselection_layout)

    }

    @SuppressLint("SetTextI18n")
    private fun initializeListeners(){
        Ivclose!!.setOnClickListener { super.onBackPressed() }
        ivClose!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        ivInfoIcon!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        tvSuperOverRules!!.setOnClickListener {
            startActivity(Intent(this,SuperOverRulesActivity::class.java))
        }
        tvPlayGame?.setOnClickListener {
            val stake  = stakeslist[position].amount!!.toDouble()
            val totalbalance = walletAmount+bonuswallet
            if (totalbalance >= stake){
                stakeselction_layout!!.visibility = View.GONE
                playForAmount!!.text = "₹ ${stakeslist[position].amount}"
                val stkamount = stakeslist[position].amount!!.toInt()
                winningAmount!!.text = "₹${stakeslist[position].amount!!.toInt() + ((stakeslist[position].amount!!.toInt() * 80) / 100)}"

                gWalletPercentage = 100 - bWalletPercentage
                gameWalletPercentage!!.text = "$gWalletPercentage %"
                bonusWalletPercentage!!.text = "$bWalletPercentage %"

                zeroPercentAmount!!.text = "₹0"
                tenPercentAmount!!.text = "₹${(stkamount * 10) /100}"
                twentyPercentAmount!!.text = "₹${(stkamount * 20) /100}"
                thirtyPercentAmount!!.text = "₹${(stkamount * 30) /100}"
                fortyPercentAmount!!.text = "₹${(stkamount * 40) /100}"
                fiftyPercentAmount!!.text = "₹${(stkamount * 50) /100}"

                val gWAmount = ((stkamount * gWalletPercentage) / 100)
                gWalletAmount = gWAmount
                gameWalletAmount!!.text = "₹ $gWAmount"
                if (bWalletPercentage != 0){
                    val bWAmount = ((stkamount * bWalletPercentage) / 100)
                    bWalletAmount = bWAmount
                    bonusWalletAmount!!.text = "₹ $bWAmount"
                }else{
                    val bWAmount = 0
                    bWalletAmount = bWAmount
                    bonusWalletAmount!!.text = "₹ $bWAmount"
                }
                walletselection_layout!!.visibility = View.VISIBLE
                val anim = AnimationUtils.loadAnimation(applicationContext,R.anim.slide_up)
                val animate = TranslateAnimation(0f, 0f,
                    walletselection_layout!!.height.toFloat(), 0f)
                animate.duration = 300
                animate.fillAfter = true
                walletselection_layout!!.startAnimation(anim)
            }else{
                showDialogInsufficientBalance()
               // showToast(getString(R.string.insufficient_balance2))
            }
            /*if (stakeslist.isNotEmpty() && checkWalletBalance(stake.toString())){
                var stakeid = "0"
                try {
                    stakeid = stakeslist[position].stakeId.toString()
                    PrefsHelper().savePref(OwlizConstants.stake_Id,stakeid)
                }catch (e : Exception){
                   Log.e("Exception",e.toString())
                    e.printStackTrace()
                }
                val i= Intent(this,SuperOverLobbyActivity::class.java)
                i.putExtra(OwlizConstants.stake_amount,stake.toString())
                i.putExtra(OwlizConstants.stake_Id,stakeid)
                i.putExtra(OwlizConstants.game_ID,gameid)
                startActivity(i)
            }*/
        }
        tvStake1?.setOnClickListener {
            if (stakeslist.isNotEmpty() && stakeslist.size >= 4) {
                stake = /*10*/ stakeslist[0].amount!!
                position = 0
                tvStake1!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_selected)
                tvStake2!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake3!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake4!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
              /*  tvStake5!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)*/
                tvStake1!!.setTextColor(resources.getColor(R.color.white))
                tvStake2!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake3!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake4!!.setTextColor(resources.getColor(R.color.colorgray1))
             //   tvStake5!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStakeSelection!!.text = "Playing Super Over for $stake ₹"
            }
        }
        tvStake2?.setOnClickListener {
            if (stakeslist.isNotEmpty() && stakeslist.size >= 4) {
                stake = /*20*/ stakeslist[1].amount!!
                position = 1
                tvStake1!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake2!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_selected)
                tvStake3!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake4!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
              /*  tvStake5!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)*/
                tvStake1!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake2!!.setTextColor(resources.getColor(R.color.white))
                tvStake3!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake4!!.setTextColor(resources.getColor(R.color.colorgray1))
              //  tvStake5!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStakeSelection!!.text = "Playing Super Over for $stake ₹"
            }
        }
        tvStake3?.setOnClickListener {
            if (stakeslist.isNotEmpty() && stakeslist.size >= 4) {
                stake = /*30*/ stakeslist[2].amount!!
                position = 2
                tvStake1!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake2!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake3!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_selected)
                tvStake4!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
              /*  tvStake5!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)*/
                tvStake1!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake2!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake3!!.setTextColor(resources.getColor(R.color.white))
                tvStake4!!.setTextColor(resources.getColor(R.color.colorgray1))
              //  tvStake5!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStakeSelection!!.text = "Playing Super Over for $stake ₹"
            }
        }
        tvStake4?.setOnClickListener {
            if (stakeslist.isNotEmpty() && stakeslist.size >= 4) {
                stake = /*50*/ stakeslist[3].amount!!
                position = 3
                tvStake1!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake2!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake3!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake4!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_selected)
               /* tvStake5!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)*/
                tvStake1!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake2!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake3!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake4!!.setTextColor(resources.getColor(R.color.white))
             //   tvStake5!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStakeSelection!!.text = "Playing Super Over for $stake ₹"
            }
        }
        tvStake5?.setOnClickListener {
            if (stakeslist.isNotEmpty() && stakeslist.size >= 5) {
                stake = /*100*/ stakeslist[4].amount!!
                position = 4
                tvStake1!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake2!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake3!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake4!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_unselected)
                tvStake5!!.background =
                    AppCompatResources.getDrawable(this, R.drawable.ic_stake_selected)
                tvStake1!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake2!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake3!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake4!!.setTextColor(resources.getColor(R.color.colorgray1))
                tvStake5!!.setTextColor(resources.getColor(R.color.white))
                tvStakeSelection!!.text = "Playing Super Over for $stake ₹"
            }
        }

        walletContinueBtn!!.setOnClickListener {
            val gamount = (stakeslist[position].amount!!.toInt() * gWalletPercentage) /100
            val bamount = (stakeslist[position].amount!!.toInt() * bWalletPercentage) /100
            if (checkInternet()){
                if (stakeslist.isNotEmpty() && checkWalletBalance(gamount) && checkReferralBalance(bamount)){
                    var stakeid = "0"
                    try {
                        stakeid = stakeslist[position].stakeId.toString()
                        PrefsHelper().savePref(OwlizConstants.stake_Id,stakeid)
                    }catch (e : Exception){
                        Log.e("Exception",e.toString())
                        e.printStackTrace()
                    }
                    val i= Intent(this,SuperOverLobbyActivity::class.java)
                    i.putExtra(OwlizConstants.stake_amount, stake)
                    Log.e("Stake $this",stake)
                    i.putExtra(OwlizConstants.stake_Id,stakeid)
                    i.putExtra(OwlizConstants.game_ID,gameid)
                    i.putExtra(OwlizConstants.bonus_stakeamount,bWalletPercentage)
                    startActivity(i)
                }
            }else{
                Toasty.error(this,"No Internet!.Please Check Your Connection",
                    Toasty.LENGTH_SHORT).show()
            }
        }

        sspercentage1!!.setOnClickListener {
            if (selectedview!=1){
                selectedview = 1
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                val stkamount = stakeslist[position].amount!!.toInt()
                bWalletPercentage = 0
                gWalletPercentage = 100 - bWalletPercentage
                gameWalletPercentage!!.text = "$gWalletPercentage %"
                bonusWalletPercentage!!.text = "$bWalletPercentage %"

                val gWAmount = ((stkamount * gWalletPercentage) / 100)
                gWalletAmount = gWAmount
                gameWalletAmount!!.text = "₹ $gWAmount"
                if (bWalletPercentage != 0){
                    val bWAmount = ((stkamount * bWalletPercentage) / 100)
                    bWalletAmount = bWAmount
                    bonusWalletAmount!!.text = "₹ $bWAmount"
                }else{
                    val bWAmount = 0
                    bWalletAmount = bWAmount
                    bonusWalletAmount!!.text = "₹ $bWAmount"
                }
                selectionView!!.animate().x(sspercentage1!!.x + 6).y(sspercentage1!!.y)
                    .setDuration(500).withEndAction {
                        selectionView!!.x = sspercentage1!!.x
                        selectionView!!.y = sspercentage1!!.y
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }.start()
            }
        }
        sspercentage2!!.setOnClickListener {
            if (selectedview != 2){
                val stkamount = stakeslist[position].amount!!.toInt()
                val bstakeamount = (stkamount * 10) / 100
                if (checkReferralBalance(bstakeamount)){
                    selectedview = 2
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    bWalletPercentage = 10
                    gWalletPercentage = 100 - bWalletPercentage
                    gameWalletPercentage!!.text = "$gWalletPercentage %"
                    bonusWalletPercentage!!.text = "$bWalletPercentage %"

                    val gWAmount = ((stkamount * gWalletPercentage) / 100)
                    gWalletAmount = gWAmount
                    gameWalletAmount!!.text = "₹ $gWAmount"

                    if (bWalletPercentage != 0){
                        val bWAmount = ((stkamount * bWalletPercentage) / 100)
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }else{
                        val bWAmount = 0
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }
                    selectionView!!.animate().x(sspercentage2!!.x + 7).y(sspercentage2!!.y)
                        .setDuration(500).withEndAction {
                            selectionView!!.x = sspercentage2!!.x
                            selectionView!!.y = sspercentage2!!.y
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }.start()
                }
            }
        }
        sspercentage3!!.setOnClickListener {
            if (selectedview!=3){
                val stkamount = stakeslist[position].amount!!.toInt()
                val bstakeamount = (stkamount * 20) / 100
                if (checkReferralBalance(bstakeamount)){
                    selectedview = 3
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    bWalletPercentage = 20
                    gWalletPercentage = 100 - bWalletPercentage
                    gameWalletPercentage!!.text = "$gWalletPercentage %"
                    bonusWalletPercentage!!.text = "$bWalletPercentage %"
                    val gWAmount = ((stkamount * gWalletPercentage) / 100)
                    gWalletAmount = gWAmount
                    gameWalletAmount!!.text = "₹ $gWAmount"
                    if (bWalletPercentage != 0){
                        val bWAmount = ((stkamount * bWalletPercentage) / 100)
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }else{
                        val bWAmount = 0
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }
                    selectionView!!.animate().x(sspercentage3!!.x + 7).y(sspercentage3!!.y)
                        .setDuration(500).withEndAction {
                            selectionView!!.x = sspercentage3!!.x
                            selectionView!!.y = sspercentage3!!.y
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }.start()
                }
            }
        }
        sspercentage4!!.setOnClickListener {
            if (selectedview!=4){
                val stkamount = stakeslist[position].amount!!.toInt()
                val bstakeamount = (stkamount * 30) / 100
                if (checkReferralBalance(bstakeamount)){
                    selectedview = 4
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    bWalletPercentage = 30
                    gWalletPercentage = 100 - bWalletPercentage
                    gameWalletPercentage!!.text = "$gWalletPercentage %"
                    bonusWalletPercentage!!.text = "$bWalletPercentage %"
                    val gWAmount = ((stkamount * gWalletPercentage) / 100)
                    gWalletAmount = gWAmount
                    gameWalletAmount!!.text = "₹ $gWAmount"
                    if (bWalletPercentage != 0){
                        val bWAmount = ((stkamount * bWalletPercentage) / 100)
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }else{
                        val bWAmount = 0
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }
                    selectionView!!.animate().x(sspercentage4!!.x + 7).y(sspercentage4!!.y)
                        .setDuration(500).withEndAction {
                            selectionView!!.x = sspercentage4!!.x
                            selectionView!!.y = sspercentage4!!.y
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }.start()
                }
            }
        }
        sspercentage5!!.setOnClickListener {
            if (selectedview!=5){
                val stkamount = stakeslist[position].amount!!.toInt()
                val bstakeamount = (stkamount * 40) / 100
                if (checkReferralBalance(bstakeamount)){
                    selectedview = 5
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    bWalletPercentage = 40
                    gWalletPercentage = 100 - bWalletPercentage
                    gameWalletPercentage!!.text = "$gWalletPercentage %"
                    bonusWalletPercentage!!.text = "$bWalletPercentage %"
                    val gWAmount = ((stkamount * gWalletPercentage) / 100)
                    gWalletAmount = gWAmount
                    gameWalletAmount!!.text = "₹ $gWAmount"
                    if (bWalletPercentage != 0){
                        val bWAmount = ((stkamount * bWalletPercentage) / 100)
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }else{
                        val bWAmount = 0
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }
                    selectionView!!.animate().x(sspercentage5!!.x + 7).y(sspercentage5!!.y)
                        .setDuration(500).withEndAction {
                            selectionView!!.x = sspercentage5!!.x
                            selectionView!!.y = sspercentage5!!.y
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }.start()
                }
            }
        }
        sspercentage6!!.setOnClickListener {
            if (selectedview!=6){
                val stkamount = stakeslist[position].amount!!.toInt()
                val bstakeamount = (stkamount * 50) / 100
                if (checkReferralBalance(bstakeamount)){
                    selectedview = 6
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    bWalletPercentage = 50
                    gWalletPercentage = 100 - bWalletPercentage
                    gameWalletPercentage!!.text = "$gWalletPercentage %"
                    bonusWalletPercentage!!.text = "$bWalletPercentage %"
                    val gWAmount = ((stkamount * gWalletPercentage) / 100)
                    gWalletAmount = gWAmount
                    gameWalletAmount!!.text = "₹ $gWAmount"
                    if (bWalletPercentage != 0){
                        val bWAmount = ((stkamount * bWalletPercentage) / 100)
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }else{
                        val bWAmount = 0
                        bWalletAmount = bWAmount
                        bonusWalletAmount!!.text = "₹ $bWAmount"
                    }
                    selectionView!!.animate().x(sspercentage6!!.x + 7).y(sspercentage6!!.y)
                        .setDuration(500).withEndAction {
                            selectionView!!.x = sspercentage6!!.x
                            selectionView!!.y = sspercentage6!!.y
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }.start()
                }
            }
        }
    }

    private fun getStakes(){
        showProgressBar(getString(R.string.loading_please_wait))
        //val userdata = GetUserProfileInput(PrefsHelper().getPref(OwlizConstants.user_id))
        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getAllStacks()
        requestCall.enqueue(object : Callback<GetAll_StakesResponse> {
            override fun onFailure(call: Call<GetAll_StakesResponse>, t: Throwable) {
                closeProgressbar()
                Log.i("SuperOverStakes","FailureResponse $t")
                Toasty.error(this@SuperOverActivity,"Request Failed", Toasty.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<GetAll_StakesResponse>, response: Response<GetAll_StakesResponse>) {
                if(response.isSuccessful){
                    val resp= response.body()
                    Log.i("SuperOverStakes","ResponseLang:// ${resp.toString()}")
                    if(resp!!.responseData!!.isNotEmpty()){
                        stakeslist = resp.responseData!!.toMutableList()
                      //  stakeslist.add(0, ResponseData("Select"))
                        // setAdapter(stakeslist)
                        setStakes()
                    } else{
                        closeProgressbar()
                        Toasty.info(this@SuperOverActivity,resp.message!!, Toasty.LENGTH_SHORT).show()
                    }
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(this@SuperOverActivity, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(this@SuperOverActivity, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(this@SuperOverActivity, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(this@SuperOverActivity, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setStakes() {
        stakeselction_layout!!.visibility = View.VISIBLE
        closeProgressbar()
        if (stakeslist.isNotEmpty() && stakeslist.size >= 4){
            tvStake1!!.text = "${stakeslist[0].amount!!} ₹"
            tvStake2!!.text = "${stakeslist[1].amount!!} ₹"
            tvStake3!!.text = "${stakeslist[2].amount!!} ₹"
            tvStake4!!.text = "${stakeslist[3].amount!!} ₹"
            stake = stakeslist[0].amount!!
        }
        val anim = AnimationUtils.loadAnimation(applicationContext,R.anim.slide_up)
        val animate = TranslateAnimation(0f, 0f,
            stakeselction_layout!!.height.toFloat(), 0f)
        animate.duration = 300
        animate.fillAfter = true
        stakeselction_layout!!.startAnimation(anim)
    }

    @SuppressLint("SetTextI18n")
    private fun getWalletBalance(){
        showProgressBar(getString(R.string.loading_please_wait))
        val userdata= WalletInput(PrefsHelper().getPref(OwlizConstants.user_id))

        val destinationService = ServiceBuilderForLocalHost.buildService(Retrofitapi::class.java)
        val requestCall = destinationService.getWalletData(userdata)
        requestCall.enqueue(object : Callback<Wallet_Response> {
            override fun onFailure(call: Call<Wallet_Response>, t: Throwable) {
                closeProgressbar()
                Log.i("customerSettingData","FailureResponse $t")
                Toasty.error(this@SuperOverActivity,"Request Failed", Toasty.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Wallet_Response>, response: Response<Wallet_Response>) {
                if(response.isSuccessful) {
                    closeProgressbar()
                    val resp = response.body()
                    if (resp!!.status==200){
                        walletAmount = resp.responseData!!.walletBalance!!.toDouble()
                        bonuswallet = resp.responseData!!.referralBalance!!.toDouble()
                        bonusWalletBalance!!.text = "₹ $bonuswallet"
                    }
                    Log.i("getAllGames", "GateWalletResponse:// $resp")
                } else{
                    closeProgressbar()
                    Log.i("CustomerSetting","ResponseLangError:// ${response.errorBody().toString()}")
                    when (response.code()) {
                        404 -> Toasty.error(this@SuperOverActivity, "not found",
                            Toasty.LENGTH_SHORT).show()
                        500 -> Toasty.warning(this@SuperOverActivity, "server broken",
                            Toasty.LENGTH_SHORT).show()
                        502 -> Toasty.warning(this@SuperOverActivity, "Bad GateWay",
                            Toasty.LENGTH_SHORT).show()
                        else -> Toasty.warning(this@SuperOverActivity, "unknown error",
                            Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun checkWalletBalance(selectedStake: Int) : Boolean {
        val walletBalance = walletAmount
        return if (walletBalance  >= selectedStake.toDouble()) {
            true
        } else {
            if (BuildConfig.PRO_VERSION) { showDialogInsufficientBalance() } else {
                showToast(getString(R.string.insufficient_balance))
            }
            false
        }
    }

    private fun checkReferralBalance(amount: Int) : Boolean {
        val bonusAmount = bonuswallet
        return if (bonusAmount  >= amount.toDouble()) {
            true
        } else {
            if (BuildConfig.PRO_VERSION) {
                showToast(getString(R.string.insufficient_balance1))
               // showDialogInsufficientBalance()
            } else {
                showToast(getString(R.string.insufficient_balance1))
            }
            false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogInsufficientBalance() {
            dialogInsufficientBalance = Dialog(this)
            if (dialogInsufficientBalance!!.window != null) {
                DialogUtils.setDialogAttributes(dialogInsufficientBalance!!)
                dialogInsufficientBalance!!.setContentView(R.layout.layout_dialog_insufficient_balance)
                val tvDialogInsufficientAddMoney =
                    dialogInsufficientBalance!!.findViewById<TextView>(R.id.tvDialogInsufficientAddMoney)
                if (!BuildConfig.PRO_VERSION) {
                    tvDialogInsufficientAddMoney.text = "Add Money"
                }
                val rlDialogInsufficientBalanceButton =
                    dialogInsufficientBalance!!.findViewById<RelativeLayout>(R.id.rlDialogInsufficientBalanceButton)
                val rlDialogInsufficientBalanceClose =
                    dialogInsufficientBalance!!.findViewById<RelativeLayout>(R.id.rlDialogInsufficientBalanceClose)
                rlDialogInsufficientBalanceButton.setOnClickListener { view: View? ->
                    dialogInsufficientBalance!!.dismiss()
                    val ii= Intent(this, DashboardActivity::class.java)
                    ii.putExtra("showbalance",true)
                    startActivity(ii)
                }
                rlDialogInsufficientBalanceClose.setOnClickListener { view: View? -> dialogInsufficientBalance!!.dismiss() }
                dialogInsufficientBalance!!.show()
                DialogUtils.removeDialogAttributes(dialogInsufficientBalance!!)
                hideStatusBarForDialog(dialogInsufficientBalance!!)
            }
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

    override fun onBackPressed() {
        if (walletselection_layout!!.isVisible){
            walletselection_layout!!.visibility = View.GONE
            stakeselction_layout!!.visibility = View.VISIBLE
        }else{
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        stakeselction_layout!!.visibility = View.VISIBLE
        walletselection_layout!!.visibility = View.GONE
        sspercentage1!!.performClick()
    }

}