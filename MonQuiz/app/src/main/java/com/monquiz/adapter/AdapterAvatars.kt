package com.monquiz.adapter

import android.content.Context
import com.monquiz.interfaces.AdapterCallback
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.monquiz.R
import com.bumptech.glide.Glide
import java.lang.ClassCastException

class AdapterAvatars(var context: Context, var avatars: Array<String>, var callback : AdapterCallback) :
    RecyclerView.Adapter<AdapterAvatars.ViewHolder>() {
    private var mAdapterCallback: AdapterCallback? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_pick_avatar_items,
            viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(avatars[position]).into(viewHolder.ivAvatar)
        viewHolder.ivAvatar.setOnClickListener { v: View? ->
            val s = avatars[viewHolder.position]
            mAdapterCallback!!.onMethodCallback(s, viewHolder.position)
        }
    }

    override fun getItemCount(): Int {
        return avatars.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
    }

    init {
        try {
            mAdapterCallback = callback
        } catch (e: ClassCastException) {
            throw ClassCastException("exception: $e")
        }
    }
}