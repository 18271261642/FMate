package com.example.xingliansdk.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xingliansdk.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {
    //下拉并提示 释放的动画
    private AnimationDrawable mAnimPull;
    //正在刷新时候的动画
    private AnimationDrawable mAnimRefresh;
    TextView refreshText;
    ImageView mImage;
    private String pullText;
    private String releaseText;
    private String refreshingText;

    public CustomRefreshHeader(Context context) {
        super(context);
        init(context, null);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setGravity(Gravity.CENTER);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.m_refresh_header, this);
        mImage = view.findViewById(R.id.iv_refresh_header);
        refreshText = view.findViewById(R.id.refreshText);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.customRefreshHeader);
            pullText = typedArray.getString(R.styleable.customRefreshHeader_cus_pullText);
            releaseText = typedArray.getString(R.styleable.customRefreshHeader_cus_ReleaseText);
            refreshingText = typedArray.getString(R.styleable.customRefreshHeader_cus_RefreshText);
            int textColor = typedArray.getColor(R.styleable.customRefreshHeader_cus_TextColor, Color.DKGRAY);
            float textSize = typedArray.getDimension(R.styleable.customRefreshHeader_cus_TextSize, 13);
            float icon_width = typedArray.getDimension(R.styleable.customRefreshHeader_cus_iconWidth, 30);
            float icon_height = typedArray.getDimension(R.styleable.customRefreshHeader_cus_iconHeight, 30);
            mImage.setLayoutParams(new LayoutParams((int) icon_width, (int) icon_height));
            boolean isDisplayText = typedArray.getBoolean(R.styleable.customRefreshHeader_cus_isDisplayText, true);
            refreshText.setTextColor(textColor);
            refreshText.setTextSize(textSize);
            if (isDisplayText) {
                refreshText.setVisibility(View.VISIBLE);
            } else {
                refreshText.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        //关闭动画
        if (mAnimPull != null && mAnimPull.isRunning()) {
            mAnimPull.stop();
        }
        if (mAnimRefresh != null && mAnimRefresh.isRunning()) {
            mAnimRefresh.stop();
        }
        return 500;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            //下拉刷新的开始状态：下拉可以刷新
            case PullDownToRefresh:
                mImage.setImageResource(R.drawable.dialog_loading);//初始动画也可以是一张图片
                refreshText.setText(pullText);
                break;
            //下拉到最底部的状态：释放立即刷新
            case ReleaseToRefresh:
             //   mImage.setImageResource(R.drawable.eyes_anim_pull_end);
//                mAnimPull = (AnimationDrawable) mImage.getDrawable();
//                mAnimPull.start();
                refreshText.setText(releaseText);
                break;//正在刷新
            case Refreshing:
             //   mImage.setImageResource(R.drawable.eyes_anim_pull_refreshing);
//                mAnimRefresh = (AnimationDrawable) mImage.getDrawable();
//                mAnimRefresh.start();
                refreshText.setText(refreshingText);
                break;
        }
    }

    public void setReleaseText(String content) {
        if (null != refreshText) {
            refreshText.setText(content);
        }
    }
}
