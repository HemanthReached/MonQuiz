package com.monquiz.customviews

import android.annotation.SuppressLint
import android.content.Context
import com.monquiz.R
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.lang.Exception

class CustomTextView : AppCompatTextView {
    /**
     * @param context context as parameter
     */
    constructor(context: Context?) : super(context!!) {}

    /**
     * @param context context as parameter
     * @param attrs   AttributeSet as parameter
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setCustomFont(context, attrs)
    }

    /**
     * @param context      context as parameter
     * @param attrs        AttributeSet as parameter
     * @param defStyleAttr attribute style as parameter
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setCustomFont(context, attrs)
    }

    /**
     * @param ctx   context
     * @param attrs attributes
     */
    @SuppressLint("CustomViewStyleable")
    private fun setCustomFont(ctx: Context, attrs: AttributeSet?) {
        val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.moneyGame)
        val customFont = typedArray.getString(R.styleable.moneyGame_custom_font)
        setCustomFont(ctx, customFont)
        typedArray.recycle()
    }

    /**
     * to set custom font to textview
     *
     * @param ctx   context as parameter
     * @param asset custom font style from assets
     * @return custom font
     */
    fun setCustomFont(ctx: Context, asset: String?): Boolean {
        var typeface: Typeface? = null
        typeface = try {
            Typeface.createFromAsset(ctx.assets, asset)
        } catch (e: Exception) {
            return false
        }
        setTypeface(typeface)
        return true
    }
}