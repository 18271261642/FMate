package com.example.xingliansdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class BPView extends View {
    private final static String TAG = "HeartRateView";
    private Paint mLineHorPiant, mLineVerPiant, mTextPaint, mCirclePaint, mNullDataPaint;
    private float mBarWidth = 10f, mBarMaginLeft = 10f, mBarMaxHeight = 150f, mBarMaginTop = 10f;
    private float mWidth, mHeight;
    private float mTextHeight = 15f, mCirlceTextRaduis = 5f, mCirlceHeartRaduis = 5f;
    private int[] mBPHight = new int[]{150, 120, 110, 150, 120, 110, 150, 120, 110, 200, 135};
    private int[] mBPLow = new int[]{60, 80, 70, 60, 80, 60, 80, 70, 50, 30, 80};
    private final static float BP_MAX = 200;
    private final static float BP_MIN = 30;
    int lineStoke = 5;
    private int mHeartMaxValue;
    private int _xDelta,_yDelta;
    public BPView(Context context) {
        this(context, null);

    }

    public BPView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initPiant();
    }

    private void initPiant() {
        int lineHorcolor = Color.BLUE;
        int lineVercolor = Color.WHITE;
        int cicleColor = Color.RED;

        mLineHorPiant = new Paint();
        mLineHorPiant.setColor(lineHorcolor);
        mLineHorPiant.setStrokeCap(Paint.Cap.SQUARE);
        mLineHorPiant.setStrokeWidth(lineStoke);

        mLineVerPiant = new Paint();
        mLineVerPiant.setColor(lineVercolor);
        mLineVerPiant.setStrokeCap(Paint.Cap.SQUARE);
        mLineVerPiant.setStrokeWidth(lineStoke);


        mTextPaint = new Paint();
        mTextPaint.setColor(lineVercolor);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setTextSize(16);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(cicleColor);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setStrokeWidth(5);


        mNullDataPaint = new Paint();
        mNullDataPaint.setColor(lineVercolor);
        mNullDataPaint.setStyle(Paint.Style.FILL);
        mNullDataPaint.setStrokeWidth(15);
        mNullDataPaint.setTextSize(40);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mBarMaxHeight = mHeight - mBarMaginTop * 2 - mTextHeight;

    }

    public void setHeartData(int[] heart) {
        if (heart != null && heart.length != 0) {
            this.mBPHight = heart;
            for (int i = 0; i < heart.length; i++) {
                if (heart[i] > mHeartMaxValue) {
                    mHeartMaxValue = heart[i];
                }
            }
        }
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        if (mBPHight == null || mBPHight.length == 0) {
            drawNodata(canvas);
            return;
        }
        int count = mBPHight.length > 10 ? mBPHight.length - 1 : 9;

        String str = "0200";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(str, 0, str.length(), bounds);
        int textWidth = bounds.width();
        // drawNightBlock(canvas);
        drawline(canvas, bounds);
        drawdata(canvas, count, textWidth);
        //       drawNodata(canvas);

    }

    public void drawNodata(Canvas canvas) {
        String str = "No Data";
        Rect bounds = new Rect();
        mNullDataPaint.getTextBounds(str, 0, str.length(), bounds);
        canvas.drawText(str, (mWidth - bounds.width()) / 2, (mHeight - bounds.height()) / 2, mNullDataPaint);


    }

    private void drawdata(Canvas canvas, int count, float textWidth) {
        float bpWidth = mWidth - 2 * mCirlceTextRaduis - textWidth;
        float bpHeigth = mHeight;
        for (int i = 0; i < mBPHight.length; i++) {
            float x =200 * i;
            x = x + textWidth;
            if (i == 0) x = x + mCirlceHeartRaduis;
            float hightY = getY(mBPHight[i], bpHeigth);
            float lowY = getY(mBPLow[i], bpHeigth);

            canvas.drawCircle(x, hightY, mCirlceTextRaduis, mLineHorPiant);
            canvas.drawCircle(x, lowY, mCirlceTextRaduis, mLineHorPiant);
            canvas.drawLine(x, hightY + mCirlceTextRaduis + 2, x, lowY - mCirlceTextRaduis - 2, mCirclePaint);
        }

    }

    private void drawline(Canvas canvas,   Rect bounds ) {
        //30,80,130,180,230
        String[] str = new String[]{"230", "180", "130", "80", "30"};
        float lineWidth = mWidth;
        float lineHeigth = mHeight;
        int linecount = 5;
        for (int i = 0; i < linecount; i++) {
            float height = lineHeigth / (linecount - 1) * i;
            if (height == 0) {
                height = height + lineStoke*2;
            }
            if (i == linecount - 1) {
                height = height - lineStoke*2;
            }
            canvas.drawText(str[i], 0, height+bounds.height()/2, mTextPaint);
            canvas.drawLine(0 + bounds.width(), height, lineWidth, height, mLineVerPiant);
        }

    }

    private float getY(float value, float height) {
        if (value == BP_MAX) {
            value = 0 + mCirlceTextRaduis;
        } else if (value == BP_MIN) {
            value = height - mCirlceTextRaduis;
        } else {
            value = (1 - value / (BP_MAX - BP_MIN)) * height;
        }
        return value;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int X = (int) event.getRawX();
        //  final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) this.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                //   _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                // layoutParams.topMargin = Y - 0;
                layoutParams.rightMargin = 0;
                // layoutParams.bottomMargin = -250;
                this.setLayoutParams(layoutParams);
                break;
        }
        // root.invalidate(); //There is a NullPointerException on this line.
        return true;
    }
}