package com.example.xyzreader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Radha_acharya on 11/9/2016.
 */
public class DynamicImageGradient extends View {
    private float mAspectRatio = 1.5f;

    public DynamicImageGradient(Context context) {
        super(context);
    }

    public DynamicImageGradient(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicImageGradient(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) (measuredWidth / mAspectRatio));
    }
}
