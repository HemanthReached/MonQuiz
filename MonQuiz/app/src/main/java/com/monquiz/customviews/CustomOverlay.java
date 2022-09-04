package com.monquiz.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.monquiz.R;

public class CustomOverlay extends LinearLayout {
    private Bitmap windowFrame;

    private int coLeft;
    private int coRight;
    private int coTop;
    private int coBottom;

    public void setDimensions(int coLeft,  int coTop, int coRight, int coBottom) {
        this.coLeft = coLeft;
        this.coRight = coRight;
        this.coTop = coTop;
        this.coBottom = coBottom;
    }

    public CustomOverlay(Context context) {
        super(context);
    }

    public CustomOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomOverlay);
        this.coLeft = a.getDimensionPixelSize(R.styleable.CustomOverlay_co_left,0);
        this.coRight = a.getDimensionPixelSize(R.styleable.CustomOverlay_co_right,0);
        this.coTop = a.getDimensionPixelSize(R.styleable.CustomOverlay_co_top,0);
        this.coBottom = a.getDimensionPixelSize(R.styleable.CustomOverlay_co_bottom,0);

        //do something with str

        a.recycle();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomOverlay(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (windowFrame == null) {
            createWindowFrame();
        }
        canvas.drawBitmap(windowFrame, 0, 0, null);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isClickable() {
        return false;
    }

    protected void createWindowFrame() {
        windowFrame = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas osCanvas = new Canvas(windowFrame);

        RectF outerRectangle = new RectF(0, 0, getWidth(), getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(250, 0, 0, 0));
        osCanvas.drawRect(outerRectangle, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        RectF innerRectangle = new RectF(this.coLeft, this.coTop, this.coRight, this.coBottom);
        osCanvas.drawRect(innerRectangle, paint);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        osCanvas.drawRect(innerRectangle, paint);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        windowFrame = null;
    }
}
