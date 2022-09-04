package com.monquiz.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.monquiz.R;

/**
 * Created by Chetan on 4/5/2019
 */
public class CustomToggleButton extends ToggleButton {

    /**
     * @param context context as parameter
     */
    public CustomToggleButton(Context context) {
        super(context);
    }

    /**
     * @param context context as parameter
     * @param attrs   AttributeSet as parameter
     */
    public CustomToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    /**
     * @param context      context as parameter
     * @param attrs        AttributeSet as parameter
     * @param defStyleAttr attribute style as parameter
     */
    public CustomToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    /**
     * @param ctx   context
     * @param attrs attributes
     */
    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.moneyGame);
        String customFont = typedArray.getString(R.styleable.moneyGame_custom_font);
        setCustomFontAsset(ctx, customFont);
        typedArray.recycle();
    }

    /**
     * to set custom font to button
     *
     * @param ctx   context as parameter
     * @param asset custom font style from assets
     * @return custom font
     */
    public boolean setCustomFontAsset(Context ctx, String asset) {
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            return false;
        }
        setTypeface(typeface);
        return true;
    }
}