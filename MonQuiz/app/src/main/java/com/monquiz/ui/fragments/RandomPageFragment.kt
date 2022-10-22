package com.monquiz.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.monquiz.R
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.monquiz.utils.Metrics
import com.monquiz.network.EndPoints
import java.util.*

class RandomPageFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView(activity, 100, 150, "https://images.app.goo.gl/2Dgue9h42PANxGAB8")
    }

    companion object {
        fun createView(context: Context?, widthDp: Int, heightDp: Int, source: String): View {
//		FrameLayout l = new FrameLayout(context);
//		l.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //CommonMethods.infoLog("page Zw = " + widthDp + ", h = " + heightDp);
            val imageView = ImageView(context)
            imageView.id = R.id.carousel_child_container
            imageView.layoutParams = FrameLayout.LayoutParams(
                if (widthDp > 0) Metrics.convertDpToPixel(widthDp.toFloat(), context)
                    .toInt() else ViewGroup.LayoutParams.MATCH_PARENT,
                if (heightDp > 0) Metrics.convertDpToPixel(heightDp.toFloat(), context)
                    .toInt() else ViewGroup.LayoutParams.MATCH_PARENT
            )

            Glide
                .with(context!!)
                .load(source)
                .centerCrop()
                .placeholder(R.drawable.ic_superover)
                .into(imageView);
            imageView.setImageResource(R.drawable.ic_gk)
            //		imageView.setGravity(Gravity.CENTER);

//		initializeTextView(textView, Integer.parseInt(text));

//		((FrameLayout.LayoutParams) textView.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
//		l.addView(textView);

//		return l;
            return imageView
        }

        fun initializeImageView(imageView: ImageView, id: String,context: Context?) {
var id= EndPoints.Base_UrlImage+id
            Log.i("TAG","IDimage:$id")
            Glide
                .with(context!!)
                .load(id)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .placeholder(R.drawable.ic_superover)
                .into(imageView);

           // imageView.setImageResource(id)
        }

        private fun randomColor(seed: Int): Int {
            return Random(seed.toLong()).nextInt(0x1000000) + -0x1000000
        }

        private fun getContrastColor(color: Int): Int {
            return Color.rgb(
                255 - Color.red(color),
                255 - Color.green(color),
                255 - Color.blue(color)
            )
        }
    }
}