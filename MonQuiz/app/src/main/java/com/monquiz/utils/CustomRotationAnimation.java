package com.monquiz.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CustomRotationAnimation extends Animation {

    private View view;
    private float cx, cy;           // center x,y position of circular path
    private float prevX, prevY;     // previous x,y position of image during animation
    private float r;                // radius of circle
    private float prevDx, prevDy;
    private float pivotX, pivotY;
    private float startAngle;


    /**
     * @param view - View that will be animated
     * @param pivotX
     * @param pivotY
     */
    public CustomRotationAnimation(View view, float pivotX, float pivotY, float startAngle){
        this.view = view;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.startAngle = startAngle;
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        // calculate position of image center
        int cxImage = width / 2;
        int cyImage = height / 2;
        cx = view.getX() + cxImage;
        cy = view.getY() + cyImage;

        this.r = (float) Math.sqrt((pivotX-cx)*(pivotX-cx)+(pivotY-cy)*(pivotY-cy));

        // set previous position to center
        prevX = cx;
        prevY = cy;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(interpolatedTime == 0){
            t.getMatrix().setTranslate(prevDx, prevDy);
            return;
        }

        float angleDeg = (interpolatedTime * 360f + startAngle) % 360;
        float angleRad = (float) Math.toRadians(angleDeg);

        // r = radius, cx and cy = center point, a = angle (radians)
        float x = (float) (pivotX + r * Math.cos(angleRad));
        float y = (float) (pivotY + r * Math.sin(angleRad));


        float dx = - prevX + x;
        float dy = - prevY + y;

        prevX = x;
        prevY = y;

        prevDx = dx;
        prevDy = dy;


        t.getMatrix().setTranslate(dx, dy);
    }
}