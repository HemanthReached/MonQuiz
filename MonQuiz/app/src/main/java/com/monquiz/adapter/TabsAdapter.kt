package com.monquiz.adapter

import android.content.Context
import com.google.android.material.tabs.TabLayout
import android.widget.TextView
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import android.os.Bundle

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.monquiz.R
import com.monquiz.utils.CommonMethods
import com.monquiz.utils.TabInfo
import java.util.ArrayList

class TabsAdapter(
    private val context: Context,
    fragmentManager: FragmentManager?,
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager,
    private val tvGalleryDone: TextView
) : FragmentStatePagerAdapter(fragmentManager!!), OnTabSelectedListener,
    ViewPager.OnPageChangeListener {
    private val mTabs = ArrayList<TabInfo>()
    fun addTab(tabSpec: TabLayout.Tab, clss: Class<*>?, args: Bundle?) {
        val tag = tabSpec.tag as String?
        val info = TabInfo(tag, clss, args)
        mTabs.add(info)
        tabLayout.addTab(tabSpec)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mTabs.size
    }

    override fun getItem(position: Int): Fragment {
        val info = mTabs[position]
        return Fragment.instantiate(
            context, info.clss.name, info.args
        )
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        val position = tab.position
        val tabName = (tab.tag as String?)!!
        if (tabName.equals(context.getString(R.string.photo), ignoreCase = true)) {
            tvGalleryDone.visibility = View.GONE
        }
        //tabLayout.setTabTextColors(Color.BLACK, context.getResources().getColor(R.color.blue_100));
        viewPager.currentItem = position
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        val position = tab.position
        val tabName = tab.tag as String?
        viewPager.currentItem = position
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        CommonMethods.infoLog("onTabReselected")
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        viewPager.currentItem = position
    }

    override fun onPageScrollStateChanged(state: Int) {}

    init {
        viewPager.adapter = this
        tabLayout.addOnTabSelectedListener(this)
        viewPager.addOnPageChangeListener(this)
    }
}