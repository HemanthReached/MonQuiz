package com.monquiz.ui.games.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.PagerAdapter
import com.monquiz.R
import com.monquiz.ui.games.BannerModel


class ViewPagerAdapter(var context: Context,var  images: ArrayList<BannerModel>,
                       var listener : PageClickListener) : PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
       // super.destroyItem(container, position, `object`)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val itemview : View  = inflater.inflate(R.layout.item_gamepager, view, false)

        val imageView: ImageView = itemview.findViewById(R.id.ivgamedesc)
        val imageview1 : ImageView = itemview.findViewById(R.id.ivgametype)
        val playout : ConstraintLayout = itemview.findViewById(R.id.pagerlayout)
        val headingText : TextView = itemview.findViewById(R.id.heading_text)
        if (images[position].gameimage != 0){
            imageView.visibility = View.VISIBLE
            imageView.setImageDrawable(AppCompatResources.getDrawable(context,images[position].gameimage))
        }else{
            imageView.visibility = View.INVISIBLE
        }
        imageview1.setImageDrawable(AppCompatResources.getDrawable(context,images[position].gametypeImage))
        view.addView(itemview)
        when(position){
            0 -> {
                playout.background = AppCompatResources.getDrawable(context,R.drawable.bg_pageritem_curved)
                headingText.visibility = View.GONE
                headingText.setTextColor(context.resources.getColor(R.color.white))
            }
            1 -> {
                playout.background = AppCompatResources.getDrawable(context,R.drawable.bg_crossword)
                headingText.visibility = View.VISIBLE
                headingText.setTextColor(context.resources.getColor(R.color.white))
            }
            2 -> {
                playout.background = AppCompatResources.getDrawable(context,R.drawable.bg_dailyhit)
                headingText.visibility = View.VISIBLE
                headingText.setTextColor(context.resources.getColor(R.color.black))
            }
            3 -> {
                playout.background = AppCompatResources.getDrawable(context,R.drawable.bg_teamup)
                headingText.visibility = View.VISIBLE
                headingText.setTextColor(context.resources.getColor(R.color.black))
            }
        }
        playout.setOnClickListener {
            listener.OnPageClicked(images[position],position)
        }
        return itemview
    }
}

interface PageClickListener {
    fun OnPageClicked(item : BannerModel,position: Int)
}
