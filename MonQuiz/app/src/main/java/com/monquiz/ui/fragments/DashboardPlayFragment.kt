package com.monquiz.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.monquiz.R
import com.monquiz.network.RetrofitApi
import com.monquiz.network.ServiceBuilderForLocalHost
import com.monquiz.response.getGamesResponse.Get_GamesResponse
import com.monquiz.ui.SuperOverRulesActivity
import com.monquiz.ui.games.BannerModel
import com.monquiz.ui.games.adapters.PageClickListener
import com.monquiz.ui.games.adapters.ViewPagerAdapter
import com.monquiz.ui.games.superover.SuperOverActivity
import com.monquiz.utils.*
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class DashboardPlayFragment : BaseFragment()/*, TimerCallBack*/ ,PageClickListener{

    private var rlFragPlayPlayBtn: RelativeLayout? = null
    private var listOfCategoryTitles: LinkedList<String>? = null
    private var listOfCategoryImages: LinkedList<String>? = null

    private var getallgameslist= mutableListOf<com.monquiz.response.getGamesResponse.ResponseData>()

    var gamesarray = ArrayList<BannerModel>()
    private var pager : ViewPager? = null
    private var tvUserDesc : TextView? =  null
    var tvGameName : TextView? = null
    var tvGameDesc : TextView? = null
    var tvGameInfo : TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_dashboard_play_fragment, container, false)
        initializeUi(view)
        CommonMethods.infoLog("tableid Frag:" + PreferenceConnector.readString(
                activity, getString(R.string.normalquiz_table_id), ""))
        initializeListeners()
        getAllGames()

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun initializeUi(view: View) {
        rlFragPlayPlayBtn = view.findViewById(R.id.rlFragPlayPlayBtn)
        tvGameInfo = view.findViewById(R.id.tvgameinfo)

        pager = view.findViewById(R.id.gameviewpager)
        pager!!.clipToPadding = false
        pager!!.setPadding(96, 0, 96, 0)
        pager!!.pageMargin = 24
        tvUserDesc = view.findViewById(R.id.userdesc)
        tvGameName = view.findViewById(R.id.tvgamename)
        tvGameDesc = view.findViewById(R.id.tvgamedesc)
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
                    tvGameName!!.text = gamesarray[position].gamename
                    tvGameDesc!!.text = gamesarray[position].gamedescription
                }
                if (position == 0){
                    tvGameInfo!!.visibility = View.VISIBLE
                }else{
                   tvGameInfo!!.visibility = View.GONE
                }
            }
            override fun onPageScrollStateChanged(state: Int) { }
        })
        tvGameInfo!!.setOnClickListener {
          if (pager!!.currentItem == 0)  {
              startActivity(Intent(requireContext(),SuperOverRulesActivity::class.java))
          }
        }
    }

    private fun getAllGames(){
        showProgressBar(getString(R.string.loading_please_wait))
        val destinationService = ServiceBuilderForLocalHost.buildService(RetrofitApi::class.java)
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