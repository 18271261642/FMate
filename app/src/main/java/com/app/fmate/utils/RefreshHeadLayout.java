//package com.example.xingliansdk.utils;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Color;
//import android.graphics.drawable.AnimationDrawable;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.example.xingliansdk.R;
//import com.scwang.smartrefresh.layout.api.RefreshHeader;
//
//
///**
// * @author :  mzl
// * describe: 自定义刷新头部  smartRefreshLayout 下拉事件 松手等事件
// * @date :2020.6.23
// */
//public class RefreshHeadLayout extends SimpleComponent implements RefreshHeader {
//
//    private static final String TAG = "RefreshHeadLayout";
//    private TextView textView;
//    private AnimationDrawable animationDrawable;
//
//    public RefreshHeadLayout(Context context) {
//        this(context, null);
//    }
//
//    public RefreshHeadLayout(Context context, AttributeSet attrs) {
//        super(context, attrs, 0);
//        init(context, attrs);
//    }
//
//    private void init(Context context, AttributeSet attrs) {
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RefreshHeadLayout);
//        int textColor = typedArray.getColor(R.styleable.RefreshHeadLayout_textColor, Color.BLACK);
//        typedArray.recycle();
//
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.refresh_head_layout, null);
//        textView = view.findViewById(R.id.head_text);
//        textView.setTextColor(textColor);
//
//        ImageView refreshImage = view.findViewById(R.id.refresh_image);
//        //获取背景
//        animationDrawable = (AnimationDrawable) refreshImage.getBackground();
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        addView(view, layoutParams);
//    }
//
//    /**
//     * 设置字体颜色
//     *
//     * @param color 颜色
//     */
//    public void setTextColor(int color) {
//        textView.setTextColor(color);
//    }
//
//    @Override
//    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
//        LogUtil.log(TAG + " onStateChanged" + newState);
//        switch (newState) {
//            case None:
//            case ReleaseToRefresh:
//                textView.setText("松开刷新");
//                break;
//            case RefreshReleased:
//            case Refreshing:
//                textView.setText("正在加载");
//                break;
//            default:
//                break;
//        }
//        super.onStateChanged(refreshLayout, oldState, newState);
//    }
//
//    @Override
//    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//        LogUtil.log(TAG + " onReleased");
//        if (animationDrawable != null && !animationDrawable.isRunning()) {
//            animationDrawable.start();
//        }
//    }
//
//
//    /**
//     * @param refreshLayout 刷新view
//     * @param success       刷新成功和失败
//     * @return 刷新完成 时间 500ms回弹
//     */
//    @Override
//    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//        LogUtil.log(TAG + " onFinish");
//        if (animationDrawable != null && animationDrawable.isRunning()) {
//            animationDrawable.stop();
//        }
//        if (success) {
//            textView.setText("刷新成功");
//        } else {
//            textView.setText("刷新失败");
//        }
//        return 500;
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (animationDrawable != null && !animationDrawable.isRunning()) {
//            animationDrawable.stop();
//            animationDrawable = null;
//        }
//    }
//}
