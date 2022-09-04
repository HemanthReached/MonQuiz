package com.monquiz.ui.fragments

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.monquiz.R
import com.monquiz.customviews.CustomTextView
import com.monquiz.response.getstakesresp.ResponseData
import com.monquiz.utils.OwlizConstants
import com.monquiz.utils.PrefsHelper

class StackesAdapter(var context: Context, list:List<ResponseData>, listner:OnItemListnerr):
    RecyclerView.Adapter<StackesAdapter.ViewHolder> (){

var stake_amont="Select"
    var stakeId="0"
    var  selectedposition=0
    var modelitem= mutableListOf<ResponseData>()
    init {
        modelitem= list as MutableList<ResponseData>
    }
    var listner:OnItemListnerr?=listner
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.stackes_recy_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       holder.stkestv.setText(modelitem[position].amount)

/*
        if (modelitem[position].amount=="Select"){
            Log.i("TAG","amountfirrstpositioon:${stakeamouunt}")
            holder.imageViews.visibility=View.VISIBLE
            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.clr_bg_gradient_start))

        }
        else{
            holder.imageViews.visibility=View.INVISIBLE
            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.blue_100))

        }*/
      /*  if (stakeamouunt=="Select"){
            holder.imageViews.visibility==View.GONE
            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.clr_bg_gradient_start))

        }
        else{
            holder.imageViews.visibility==View.GONE

            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.blue_100))

        }
*/



holder.consilay.setOnClickListener {

    selectedposition=position
    listner!!.onAddclassClick(position)
    stake_amont= modelitem[position].amount!!
    try {
        stakeId=modelitem[position].stakeId!!
    } catch (e: Exception) {
    }
    PrefsHelper().savePref(OwlizConstants.stake_Id,stakeId)
    PrefsHelper().savePref(OwlizConstants.stake_amount,stake_amont)

    notifyDataSetChanged()

}
        if (selectedposition==position&&modelitem[selectedposition].amount=="Select"){

            Log.i("TAG","amountfirrstpositioon:$stake_amont")
            holder.stkestv.setTextColor(ContextCompat.getColor(context,R.color.clr_bg_gradient_start))
            holder.imageViews.visibility=View.VISIBLE

            Glide.with(context).load(R.drawable.ic_play_free)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .centerCrop()
                .placeholder(R.drawable.ic_play_free).into(holder.imageViews)
        } else if (selectedposition==position&&modelitem[selectedposition].amount=="10"){
            Log.i("TAG","amountfirrstpositioon:${stake_amont}")
            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.clr_bg_gradient_start))
            holder.imageViews.visibility=View.VISIBLE

            Glide.with(context!!).load(R.drawable.ic_play_fifty)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .centerCrop()
                .into(holder.imageViews)
        } else if (selectedposition==position&&modelitem[selectedposition].amount=="20"){
            Log.i("TAG","amountfirrstpositioon:${stake_amont}")
            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.clr_bg_gradient_start))
            holder.imageViews.visibility=View.VISIBLE

            Glide.with(context!!).load(R.drawable.ic_play_twenty)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .centerCrop()
                .into(holder.imageViews)
        } else if (selectedposition==position&&modelitem[selectedposition].amount=="50"){
            Log.i("TAG","amountfirrstpositioon:${stake_amont}")

            holder.imageViews.visibility=View.VISIBLE
            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.clr_bg_gradient_start))

            Glide.with(context!!).load(R.drawable.ic_play_fifty)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .centerCrop()
                .into(holder.imageViews)
        } else if (selectedposition==position&&modelitem[selectedposition].amount=="100"){
            Log.i("TAG","amountfirrstpositioon:${stake_amont}")
            holder.imageViews.visibility=View.VISIBLE

            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.clr_bg_gradient_start))
            Glide.with(context!!).load(R.drawable.ic_play_fifty)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .centerCrop()
                .into(holder.imageViews)
        } else{
            holder.imageViews.visibility=View.INVISIBLE
            holder.stkestv!!.setTextColor(ContextCompat.getColor(context,R.color.blue_100))
        }
    }


    override fun getItemCount(): Int {
        Log.i("adapter","Size:${modelitem.size}")
        return modelitem.size
    }
    class ViewHolder (itemView: View?): RecyclerView.ViewHolder(itemView!!){
        var imageViews= itemView?.findViewById(R.id.ivFragPlaySelect) as ImageView
        var stkestv= itemView?.findViewById(R.id.tvFragPlayFree) as CustomTextView
        var consilay= itemView?.findViewById(R.id.consi) as ConstraintLayout
    }
    interface OnItemListnerr {
        fun onIntemClick(postion:Int)
        fun onAddclassClick(postion:Int)
    }
}