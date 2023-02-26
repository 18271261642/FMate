package com.app.fmate.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;


import com.app.fmate.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * 血压图表
 */
public class BloodPreesureChartView extends View {

    private float barInterval = dp2Px(30);
    private float barWidth =  dp2Px(20);
    private int top_text_size = dp2Px(12);
    private int bottom_text_size = dp2Px(12);

    private Paint mTopTextPaint;
    private Paint mBottomTextPaint;
    private Paint mBarShadowPaint;
    private Paint mBarPaint;
    private Paint mBottomLinePaint;
    private Paint mMarketPaint;
    private Path mMarketPath;

    private int minValue = 50;
    private int maxValue = 150;

    private Rect mMarketMaxValueRect = new Rect();
    private Rect mMarketMinValueRect = new Rect();
    private Rect mMarketMsgRect = new Rect();

    private ArrayList<BarData> innerData = new ArrayList<>();
    private float paddingTop = 10;
    private float paddingLeft = 10;
    private float paddingBottom = 10;
    private float paddingRight = 10;
    private float marketWidth = 0;//market 宽度
    private float marketHeight =  dp2Px(50);//market高度
    private float barHeight = 0;

    private float defaultHeight = dp2Px(180);
    private float bottom_view_height = dp2Px(30);
    private float lastX = 0;
    private float lastY = 0;
    private float measureWidth = 0;
    //这是最初的的位置
    private float startOriganalX = 0;
    private HorizontalScrollRunnable horizontalScrollRunnable;
    //临时滑动的距离
    private float tempLength = 0;
    private long startTime = 0;
    private boolean isFling = false;
    private float dispatchTouchX = 0;
    private float dispatchTouchY = 0;
    //是否到达边界
    private boolean isBoundary = false;
    private boolean isMove = false;

    private float mSelectTop = 0;
    private float mSelectBottom = 0;
    private float marketStart = 0;

    private int mSelectIndex = -1;
    private float allSizeLength = 0;
    float sizeHeight;

    public  int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public  int sp2px(Context context,float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public BloodPreesureChartView(Context context) {
        this(context, null);
    }

    public BloodPreesureChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BloodPreesureChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Random mRandom=new Random();
        for (int i = 0; i <50 ; i++) {
//            TLog.Companion.error("循环");
            int height=   mRandom.nextInt(50);
            int low=   mRandom.nextInt(20);
            String time="12:10:"+i;
            innerData.add(new  BloodPreesureChartView.BarData(height+50,low+30,time));
//            TLog.Companion.error("宽高度++"+new Gson().toJson(innerData));
        }
        barWidth = dp2px(context,20);
        initPaint(context);
    }

    private int dp2Px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int sp2Px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @SuppressLint("ResourceAsColor")
    private void initPaint(Context context) {
        mTopTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTopTextPaint.setTextSize(top_text_size);
        mTopTextPaint.setColor(Color.GREEN);
        mTopTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTopTextPaint.setStyle(Paint.Style.FILL);
        mTopTextPaint.setDither(true);

        mBottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomTextPaint.setTextSize(bottom_text_size);
        mBottomTextPaint.setColor(context.getResources().getColor(R.color.teal_700));
        mBottomTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mBottomTextPaint.setStyle(Paint.Style.FILL);
        mBottomTextPaint.setDither(true);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setTextSize(top_text_size);
        mBarPaint.setColor(context.getResources().getColor(R.color.teal_200));
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setStrokeWidth(1);
        mBarPaint.setDither(true);

        mMarketPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarketPaint.setTextSize(top_text_size);
        mMarketPaint.setColor(context.getResources().getColor(R.color.purple_200));
        mMarketPaint.setStrokeCap(Paint.Cap.ROUND);
        mMarketPaint.setStyle(Paint.Style.STROKE);
        mMarketPaint.setStrokeWidth(dp2Px(1));
        mMarketPaint.setTextSize(dp2Px(12));
        mMarketPaint.setDither(true);
        mMarketPath = new Path();

        mBarShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarShadowPaint.setTextSize(top_text_size);
        mBarShadowPaint.setColor(Color.RED);
        mBarShadowPaint.setStrokeCap(Paint.Cap.ROUND);
        mBarShadowPaint.setStyle(Paint.Style.FILL);
        mBarShadowPaint.setDither(true);

        mBottomLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomLinePaint.setTextSize(top_text_size);
        mBottomLinePaint.setColor(context.getResources().getColor(R.color.black));
        mBottomLinePaint.setStyle(Paint.Style.FILL);
        mBottomLinePaint.setDither(true);
        //设置底部线的宽度
        mBottomLinePaint.setStrokeWidth(dp2Px(1f));
    }

    public void setBarChartData(ArrayList<BarData> innerData) {
        mSelectIndex = -1;
        this.innerData.clear();
        this.innerData.addAll(innerData);
        invalidate();
    }

    public void setMaxMin(int max,int min){
        maxValue = max;
        minValue = min;
        sizeHeight = (defaultHeight-marketHeight-bottom_view_height)/(float) (maxValue-minValue);
    }

    //进行滑动的边界处理
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int dispatchCurrX = (int) ev.getX();
        int dispatchCurrY = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //父容器不拦截点击事件，子控件拦截点击事件。如果不设置为true,外层会直接拦截，从而导致motionEvent为cancle
                getParent().requestDisallowInterceptTouchEvent(true);
                dispatchTouchX = getX();
                dispatchTouchY = getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float deltaX = dispatchCurrX - dispatchTouchX;
                float deltaY = dispatchCurrY - dispatchTouchY;
                if (Math.abs(deltaY) - Math.abs(deltaX) > 0) {//竖直滑动的父容器拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                //这是向右滑动，如果是滑动到边界，那么就让父容器进行拦截
                if ((dispatchCurrX - dispatchTouchX) > 0 && startOriganalX == 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if ((dispatchCurrX - dispatchTouchX) < 0 && startOriganalX == -getMoveLength()) {//这是向右滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        dispatchTouchX = dispatchCurrX;
        dispatchTouchY = dispatchCurrY;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isBoundary = false;
        isMove = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                startTime = System.currentTimeMillis();
                //当点击的时候，判断如果是在fling的效果的时候，就停止快速滑动
                if (isFling) {
                    removeCallbacks(horizontalScrollRunnable);
                    isFling = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float currX = event.getX();
                float currY = event.getY();
                startOriganalX += currX - lastX;
                //这是向右滑动
                if ((currX - lastX) > 0) {
                    Log.e("TAG", "向右滑动");
                    if (startOriganalX > 0) {
                        startOriganalX = 0;
                        isBoundary = true;
                    }

                } else {//这是向右滑动
                    Log.e("TAG", "向左滑动");
                    if (-startOriganalX > getMoveLength()) {
                        startOriganalX = -getMoveLength();
                        isBoundary = true;
                    }
                }
                tempLength = currX - lastX;
                //如果数据量少，根本没有充满横屏，就没必要重新绘制，
                if (measureWidth < innerData.size() * (barWidth + barInterval)) {
                    invalidate();
                }
                lastX = currX;
                lastY = currY;

                //滑动时弹起market
                float realStartX = barInterval/2;
                for (int i = 0; i <innerData.size() ; i++) {
                    if ((realStartX <= (event.getX()-startOriganalX)) && ((event.getX() -startOriganalX) <= (realStartX+barWidth))){
                        mSelectIndex = i;
                    }
                    realStartX = realStartX+barWidth+barInterval;
                }

                if (allSizeLength <= getWidth()){
                    //防止不够一屏幕时，滑动出现错乱
                    startOriganalX = 0;
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                //计算猛滑动的速度，如果是大于某个值，并且数据的长度大于整个屏幕的长度，那么就允许有flIng后逐渐停止的效果
                float speed = tempLength / (endTime - startTime) * 1000;
                if (Math.abs(speed) > 100 && !isFling && measureWidth < innerData.size() * (barWidth + barInterval)) {
                    this.post(horizontalScrollRunnable = new HorizontalScrollRunnable(speed));
                }
                isMove = false;

                //滑动时弹起market
                float realStartX2 = barInterval/2;
                for (int i = 0; i <innerData.size() ; i++) {
                    if ((realStartX2-barInterval/2 <= (event.getX()-startOriganalX)) && ((event.getX() -startOriganalX) <= (realStartX2+barWidth)+barInterval/2)){
                        mSelectIndex = i;
                    }
                    realStartX2 = realStartX2+barWidth+barInterval;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                isMove = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBottomLine(canvas);
        //如果没有数据 绘制loading...
        if (innerData.size() <= 0) {
        } else {
            allSizeLength = barInterval/2;
            for (int i = 0; i < innerData.size(); i++) {
                allSizeLength = allSizeLength + barWidth + barInterval;
            }
            float startX =  barInterval/2 + startOriganalX;
            float sizeheight = (defaultHeight-bottom_view_height-marketHeight)/(maxValue-minValue);//每份的高度
            for (int i = 0; i < innerData.size(); i++) {
                //绘制bar
                BarData itemInfo = innerData.get(i);
                float top = 0;
                float bottom = 0;
                if (itemInfo.hightValue >= maxValue){
                    top =marketHeight;
                }else {
                    if (itemInfo.hightValue <= minValue){
                        top = defaultHeight-bottom_view_height;
                    }else {
                        top = marketHeight+(maxValue-itemInfo.hightValue)*sizeheight;
                    }
                }

                if (itemInfo.lowValue <= minValue){
                    bottom = defaultHeight-bottom_view_height;
                }else {
                    if (itemInfo.lowValue >= maxValue ){
                        bottom = marketHeight;
                    }else {
                        bottom = marketHeight + (maxValue-itemInfo.lowValue)*sizeheight;
                    }
                }
                /**
                 * 防止 low  比  hight  大的情况
                 */
                float temp = 0;
                if (bottom < top){
                    temp = top;
                    top = bottom;
                    bottom = temp;
                }

                mBarPaint.setStyle(Paint.Style.FILL);
                mBarPaint.setStrokeWidth(1);
                if (mSelectIndex == i){
                    marketStart = startX;
                    mSelectTop = top;
                    mSelectBottom = bottom;

                    if (itemInfo.lowValue < 60 || itemInfo.hightValue < 90 || itemInfo.hightValue > 140 || itemInfo.lowValue > 90){
                        //选中的样子
                        mBarPaint.setColor(getResources().getColor(R.color.red));
                        //绘制柱状
                        drawBar(canvas, startX,top,bottom);
                        mBarPaint.setColor(getResources().getColor(R.color.redShallow));

                        //绘制柱状圆圈
                        drawBarCircle(canvas,startX,top,bottom,itemInfo);
                    }else {
                        //选中的样子
                        mBarPaint.setColor(getResources().getColor(R.color.green));
                        //绘制柱状
                        drawBar(canvas, startX,top,bottom);

                        mBarPaint.setColor(getResources().getColor(R.color.white));
                        //绘制柱状圆圈
                        drawBarCircle(canvas,startX,top,bottom,itemInfo);
                    }
                }else {

                    if (itemInfo.lowValue < 60 || itemInfo.hightValue < 90 || itemInfo.hightValue > 140 || itemInfo.lowValue > 90) {
                        //未选中的样子
                        mBarPaint.setColor(getResources().getColor(R.color.redShallow));
                        //绘制柱状
                        drawBar(canvas, startX, top, bottom);

                        mBarPaint.setColor(getResources().getColor(R.color.red));
                        //绘制柱状圆圈
                        drawBarCircle(canvas, startX, top, bottom,itemInfo);
                    }else {
                        //未选中的样子
                        mBarPaint.setColor(getResources().getColor(R.color.greenCCF9EC));
                        //绘制柱状
                        drawBar(canvas, startX, top, bottom);
                        mBarPaint.setColor(getResources().getColor(R.color.green));
                        //绘制柱状圆圈
                        drawBarCircle(canvas, startX, top, bottom,itemInfo);
                    }
                }
                //绘制下面的文字
                float bottomTextWidth = mBottomTextPaint.measureText(innerData.get(i).bottomText);
                float bottomStartX = startX + barWidth / 2 - bottomTextWidth / 2;
                Rect rect = new Rect();
                mBottomTextPaint.getTextBounds(innerData.get(i).getBottomText(), 0, innerData.get(i).getBottomText().length(), rect);
                float bottomStartY = defaultHeight - bottom_view_height + 10 + rect.height();//rect.height()是获取文本的高度;
                if (i%2 == 0){
                    //绘制底部的文字
                    drawBottomText(canvas, innerData.get(i).getBottomText(), bottomStartX, bottomStartY);
                }else {
                    //绘制底部的文字
                    drawBottomText(canvas, "", bottomStartX, bottomStartY);
                }

                startX = startX + barWidth + barInterval;
            }
            if (mSelectIndex >= 0 && innerData.size() >0){
                if (mSelectTop < mSelectBottom){
                    drawMarket(canvas,marketStart,mSelectTop);
                }else {
                    drawMarket(canvas,marketStart,mSelectBottom);
                }
            }
        }
    }

    /**
     * 底部横线
     * @param canvas
     */
    private void drawBottomLine(Canvas canvas) {
        if (innerData.size() * (barWidth + barInterval) < getWidth()){
            canvas.drawLine(paddingLeft, defaultHeight - bottom_view_height, getWidth(), defaultHeight - bottom_view_height, mBottomLinePaint);
        }else {
            canvas.drawLine(paddingLeft, defaultHeight - bottom_view_height, innerData.size() * (barWidth + barInterval), defaultHeight - bottom_view_height, mBottomLinePaint);
        }
    }

    /**
     * 绘制柱状
     * @param canvas
     * @param startX
     * @param top
     * @param bottom
     */
    private void drawBar(Canvas canvas, float startX,float top ,float bottom) {
        RectF mRect = new RectF(startX, top , startX + barWidth,bottom);
        canvas.drawRoundRect(mRect,barWidth/2,barWidth/2,mBarPaint);
    }

    /**
     * 绘制柱状图的小圆圈
     * @param canvas
     * @param startX
     * @param top
     * @param bottom
     */
    private void drawBarCircle(Canvas canvas,float startX,float top, float bottom,BarData data){
        if (data.lowValue <= 50){
            canvas.drawCircle(startX+barWidth/2,(defaultHeight-bottom_view_height)-barWidth/2f,barWidth/2-dp2Px(1),mBarPaint);
        }else {
            canvas.drawCircle(startX+barWidth/2,bottom-barWidth/2f,barWidth/2-dp2Px(1),mBarPaint);
        }

        if (data.hightValue <= 50){
            canvas.drawCircle(startX+barWidth/2,(defaultHeight-bottom_view_height)-barWidth/2f,barWidth/2-dp2Px(1),mBarPaint);
        }else {
            canvas.drawCircle(startX+barWidth/2,top+barWidth/2f,barWidth/2-dp2Px(1),mBarPaint);
        }
    }

    /**
     * 绘制market
     * @param canvas
     * @param startX
     * @param endY
     */
    @SuppressLint("ResourceAsColor")
    private void drawMarket(Canvas canvas, float startX, float endY){
        BarData data = innerData.get(mSelectIndex);
        mMarketPaint.setTextSize(dp2Px(14));
        mMarketPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mMarketPaint.getTextBounds(data.hightValueStr,0,data.hightValueStr.length(),mMarketMaxValueRect);
        mMarketPaint.getTextBounds(data.lowValueStr,0,data.lowValueStr.length(),mMarketMinValueRect);
        mMarketPaint.setTypeface(Typeface.DEFAULT);
        mMarketPaint.getTextBounds(data.bottomText+" "+data.state,0,(data.bottomText+" "+data.state).length(),mMarketMsgRect);

        marketWidth = 0;
        if ((mMarketMinValueRect.right-mMarketMinValueRect.left)>(mMarketMaxValueRect.right-mMarketMaxValueRect.left)){
            if ((mMarketMinValueRect.right-mMarketMinValueRect.left) > (mMarketMsgRect.right-mMarketMsgRect.left)){
                marketWidth += mMarketMinValueRect.right-mMarketMinValueRect.left;
            }else {
                marketWidth += mMarketMsgRect.right-mMarketMsgRect.left;
            }

        }else {
            if ((mMarketMaxValueRect.right-mMarketMaxValueRect.left) > (mMarketMsgRect.right-mMarketMsgRect.left)){
                marketWidth += mMarketMaxValueRect.right-mMarketMaxValueRect.left;
            }else {
                marketWidth += mMarketMsgRect.right-mMarketMsgRect.left;
            }
        }

        marketWidth += HelpUtil.INSTANCE.dp2px(15);//添加左右padding;

        int mHeight = 0;
        mHeight += (mMarketMaxValueRect.bottom-mMarketMaxValueRect.top)+(mMarketMinValueRect.bottom-mMarketMinValueRect.top)+(mMarketMsgRect.bottom-mMarketMsgRect.top)+dp2Px(35);
        RectF rectF;
        RectF rectF2;
        if (startX+barWidth/2-marketWidth/2 <= 0){
            rectF = new RectF(startX-barWidth/2,endY+dp2Px(2),startX+barWidth/2+marketWidth-barWidth/2,endY+mHeight+dp2Px(2));
            rectF2 = new RectF(rectF.left-dp2Px(1),rectF.top-dp2Px(1),rectF.right+dp2Px(1),rectF.bottom+dp2Px(1));

        } else if (startX+barWidth/2+marketWidth/2 >=  startX+barWidth+barInterval){
            rectF = new RectF(startX+barWidth/2+barInterval-marketWidth,endY+dp2Px(2),startX+barWidth/2+barInterval,endY+mHeight+dp2Px(2));
            rectF2 = new RectF(rectF.left-dp2Px(1),rectF.top-dp2Px(1),rectF.right+dp2Px(1),rectF.bottom+dp2Px(1));
        }else {
            rectF = new RectF(startX+barWidth/2-marketWidth/2,endY+dp2Px(2),startX+barWidth/2+marketWidth/2,endY+mHeight+dp2Px(2));
            rectF2 = new RectF(rectF.left-dp2Px(1),rectF.top-dp2Px(1),rectF.right+dp2Px(1),rectF.bottom+dp2Px(1));
        }


        if (rectF2.bottom >= (defaultHeight-bottom_view_height)){
            rectF.bottom = defaultHeight-bottom_view_height-dp2Px(10);
            rectF.top = defaultHeight-bottom_view_height-dp2Px(10) - mHeight;

            rectF2.bottom = rectF.bottom+dp2Px(1);
            rectF2.top = rectF.top-dp2Px(1);
        }

        mMarketPath.reset();
        mMarketPath.addRoundRect(rectF2,dp2Px(5),dp2Px(5), Path.Direction.CCW);
        mMarketPaint.setColor(data.barColor);
        mMarketPaint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(mMarketPath,mMarketPaint);

        mMarketPaint.setColor(getResources().getColor(R.color.text_hint_color));
        mMarketPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rectF,dp2Px(5),dp2Px(5),mMarketPaint);

        mMarketPaint.setColor(getResources().getColor(R.color.white));
        mMarketPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        float valueY = rectF2.top+dp2Px(10)+(mMarketMaxValueRect.bottom-mMarketMaxValueRect.top);
        canvas.drawText(data.hightValueStr,rectF2.left+dp2Px(10),valueY,mMarketPaint);

        float value2Y = valueY+dp2Px(5)+(mMarketMinValueRect.bottom-mMarketMinValueRect.top);
        canvas.drawText(data.lowValueStr,rectF2.left+dp2Px(10),value2Y,mMarketPaint);

        float value3Y = value2Y+dp2Px(10)+(mMarketMsgRect.bottom-mMarketMsgRect.top);
        mMarketPaint.setTypeface(Typeface.DEFAULT);

        mBarPaint.setStyle(Paint.Style.STROKE);

        mBarPaint.setStrokeWidth(5);
        canvas.drawCircle(startX+barWidth/2,endY-dp2Px(5),dp2Px(3),mBarPaint);
        RectF rectF1 = new RectF();
        mMarketPaint.getTextBounds(data.bottomText,0,data.bottomText.length(),mMarketMsgRect);
        canvas.drawText(data.bottomText,rectF2.left+dp2Px(10),value3Y,mMarketPaint);

        if (data.lowValue < 60 || data.hightValue < 90){
            mBarPaint.setColor(getResources().getColor(R.color.red));
            mMarketPaint.setColor(getContext().getResources().getColor(R.color.red));
            data.state = "低位";//getContext().getResources().getString(R.string.low);
        }else if (data.hightValue > 140 || data.lowValue > 90){
            mBarPaint.setColor(getResources().getColor(R.color.red));
            mMarketPaint.setColor(getContext().getResources().getColor(R.color.red));
            data.state = "血四";//getContext().getResources().getString(R.string.blood_four);
        }else if (data.hightValue > 140 && data.lowValue < 60){
            mBarPaint.setColor(getResources().getColor(R.color.red));
            mMarketPaint.setColor(getContext().getResources().getColor(R.color.red));
            data.state ="异常";// getContext().getResources().getString(R.string.unusual);
        } else {
            mBarPaint.setColor(getResources().getColor(R.color.green));
            mMarketPaint.setColor(getContext().getResources().getColor(R.color.green));
            data.state = "正常";//getContext().getString(R.string.normal);
        }
        canvas.drawText(" "+data.state,rectF2.left+dp2Px(10)+(mMarketMsgRect.right-mMarketMsgRect.left),value3Y,mMarketPaint);
    }

    private void drawBottomText(Canvas canvas, String text, float bottomStartX, float bottomStartY) {
        canvas.drawText(text, bottomStartX, bottomStartY, mBottomTextPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        innerData.clear();
        innerData = null;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float width = 0;
        float height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = width = widthSize;
        } else {
            width = getAndroiodScreenProperty().get(0);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            defaultHeight = height = heightSize;
        } else {
            height = defaultHeight;
        }
        setMeasuredDimension((int)width, (int)height);
        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingRight = getPaddingRight();
        barHeight = defaultHeight-marketHeight-bottom_view_height;
        barInterval = (width-7*barWidth)/7-dp2Px(1);
        sizeHeight = barHeight/(float) (maxValue-minValue);//barheight 的每份高度
//        TLog.Companion.error(BloodPreesureChartView.class.getSimpleName()+barHeight+"    "+sizeHeight);
        //MyLogger.e(BloodPreesureChartView.class.getSimpleName(),barHeight+"    "+sizeHeight);
    }

    @SuppressLint("ResourceAsColor")
    public void setLineColor( int color){
        mBottomLinePaint.setColor(color);
    }
    @SuppressLint("ResourceAsColor")
    public void setLineTextColor( int color){
        mBottomTextPaint.setColor(color);
    }

    @SuppressLint("ResourceAsColor")
    public void setBar_color(int color){
        mBarPaint.setColor(color);
    }


    private ArrayList<Integer> getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)

        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(screenWidth);
        integers.add(screenHeight);
        return integers;
    }

    private float getMoveLength() {
        return (barWidth + barInterval) * innerData.size() - measureWidth+barInterval;
    }

    public boolean isBoundary() {
        return isBoundary;
    }

    public boolean isMove() {
        return isMove;
    }

    public static class BarData {
        private String hightValueStr;
        private String lowValueStr;
        private int hightValue;
        private int lowValue;

        private int barColor;
        private String state = "";
        private String bottomText = "";

        public int getHightValue() {
            return hightValue;
        }

        public void setHightValue(int hightValue) {
            this.hightValue = hightValue;
        }

        public int getLowValue() {
            return lowValue;
        }

        public void setLowValue(int lowValue) {
            this.lowValue = lowValue;
        }

        public BarData(int hightValue, int lowValue, String bottomText) {
            this.hightValue = hightValue;
            this.lowValue = lowValue;
            this.bottomText = bottomText;
            hightValueStr = String.valueOf("收缩压 "+hightValue + "mmHg");
            lowValueStr = String.valueOf("舒张压 "+lowValue + "mmHg");
        }

        public BarData(int hightValue,int lowValue, String bottomText,String msg) {
            this.hightValue = hightValue;
            this.lowValue = lowValue;
            this.bottomText = bottomText;
            state = msg;
            hightValueStr = String.valueOf("收缩压 "+hightValue + "mmHg");
            lowValueStr = String.valueOf("舒张压 "+lowValue + "mmHg");
        }

        public void setState(String msg){
            state = msg;
        }

        public void setBarColor(int color){
            barColor = color;
        }

        public String getBottomText() {
            return bottomText == null ? "" : bottomText;
        }

        public void setBottomText(String bottomText) {
            this.bottomText = bottomText;
        }
    }

    private class HorizontalScrollRunnable implements Runnable {

        private float speed = 0;

        public HorizontalScrollRunnable(float speed) {
            this.speed = speed;
        }
        @Override
        public void run() {
            if (Math.abs(speed) < 30) {
                isFling = false;
                return;
            }
            isFling = true;
            startOriganalX += speed / 15;
            speed = speed / 1.15f;
            //这是向右滑动
            if ((speed) > 0) {
                Log.e("TAG", "向右滑动");
                if (startOriganalX > 0) {
                    startOriganalX = 0;
                }

            } else {//这是向右滑动
                Log.e("TAG", "向左滑动");
                if (-startOriganalX > getMoveLength()) {
                    startOriganalX = -getMoveLength();
                }
            }
            postDelayed(this, 20);
            invalidate();
        }
    }
}