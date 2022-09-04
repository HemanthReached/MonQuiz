package com.monquiz.ui

import android.content.Intent
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.monquiz.R
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.monquiz.ui.fragments.RuleInAMinitFragment
import com.monquiz.ui.fragments.RuleDailyQuizFragment
import com.monquiz.ui.fragments.RuleQuizMasterFragment
import java.util.*

class RuleBookActivity : BaseActivity(), View.OnClickListener {
    private var rlRuleBookBack: ImageView? = null
    private var rlSuperOver : RelativeLayout? = null
    /*private var viewPagerRuleBook: ViewPager? = null
    private var tabLayoutRuleBook: TabLayout? = null*/
 var titleNumbers: Array<String>?=null
 var arrayTabText: Array<String>?=null
    val ICONS = intArrayOf(R.drawable.ic_inaminit, R.drawable.ic_gk, R.drawable.ic_oneshot)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_rule_book)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        initializeUi()
    }

    private fun initializeUi() {
       /* viewPagerRuleBook = findViewById(R.id.viewPagerRuleBook)
        tabLayoutRuleBook = findViewById(R.id.tabLayoutRuleBook)*/
        rlRuleBookBack = findViewById(R.id.rlRuleBookBack)
        rlSuperOver = findViewById(R.id.rlSuperOver)
        rlRuleBookBack!!.setOnClickListener(this)
        rlSuperOver!!.setOnClickListener {
            startActivity(Intent(this,SuperOverRulesActivity::class.java))
        }
      /*  tabLayoutRuleBook!!.setupWithViewPager(viewPagerRuleBook)
        loadNewFragments(viewPagerRuleBook)
        Objects.requireNonNull(tabLayoutRuleBook!!.getTabAt(0))!!.setIcon(ICONS[0])
        Objects.requireNonNull(tabLayoutRuleBook!!.getTabAt(1))!!.setIcon(ICONS[1])
        Objects.requireNonNull(tabLayoutRuleBook!!.getTabAt(2))!!.setIcon(ICONS[2])

        //change Tab selection when swipe ViewPager
        viewPagerRuleBook!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayoutRuleBook))*/
    }

    private fun loadNewFragments(viewPagerRuleBook: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(RuleInAMinitFragment(this, "inaminit"), "In A Minute")
        adapter.addFragment(RuleDailyQuizFragment(this, "quiz_master"), "QuizMaster")
        adapter.addFragment(RuleDailyQuizFragment(this, "daily_quiz"), "One Shot")
        viewPagerRuleBook!!.adapter = adapter
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rlRuleBookBack -> {
                onBackPressed()
            }
        }
    }

    private inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentStatePagerAdapter(
        manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return if (position == 0) {
                RuleInAMinitFragment()
            } else if (position == 1) {
                RuleQuizMasterFragment()
            } else RuleDailyQuizFragment()
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getItemPosition(position: Any): Int {
            return POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }
}