package com.monquiz.adapter

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.R
import com.bumptech.glide.Glide
import java.util.*

class GalleryAdapter(private val context: Context, private val listOfImages: LinkedList<String?>?) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var clickListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onClick(view: View?, position: Int)
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        clickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_gallery_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imgUrl = listOfImages!![position]
        holder.ivPic.visibility = View.VISIBLE
        Glide.with(context).load("file://$imgUrl").into(holder.ivPic)
    }

    override fun getItemCount(): Int {
        return listOfImages!!.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val ivPic: ImageView
        override fun onClick(view: View) {
            clickListener!!.onClick(view, layoutPosition)
        }

        init {
            ivPic = itemView.findViewById(R.id.ivPic)
            itemView.setOnClickListener(this)
        }
    }
}