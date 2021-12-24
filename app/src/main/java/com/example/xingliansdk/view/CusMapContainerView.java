package com.example.xingliansdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import androidx.core.widget.NestedScrollView;

/**
 * Created by Admin
 * Date 2021/9/13
 */
public class CusMapContainerView extends RelativeLayout {

    private NestedScrollView nestedScrollView;

    public void setNestedScrollView(NestedScrollView nestedScrollView) {
        this.nestedScrollView = nestedScrollView;
    }

    public CusMapContainerView(Context context) {
        super(context);
    }

    public CusMapContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusMapContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CusMapContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            nestedScrollView.requestDisallowInterceptTouchEvent(false);
        } else {
            nestedScrollView.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
