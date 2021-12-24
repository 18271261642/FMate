package com.example.xingliansdk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.example.xingliansdk.R;


public class TasksRopeCompletedView extends View {

    private static final String TAG = "TasksRopeCompletedView";


    private Paint mCirclePaint;
    // 画圆环的画笔
    private Paint mRingPaint;
   // private Paint nRingPaint;
    // 画字体的画笔
    private Paint mTextPaint;
    // 圆形颜色
    private int mCircleColor;
    // 圆环颜色
    private int mRingColor;
    private int nRingColor;

    // 半径
    private float mRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆环宽度
    private float mStrokeWidth;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 字的长度
    private float mTxtWidth;
    // 字的高度
    private float mTxtHeight;
    // 总进度
    private int mTotalProgress = 100;
    // 当前进度
    private float progress;
    Bitmap bitmap = null;
    int imgWidth = 0;
    int ingHight = 0;
    Context context;

    Drawable icon;


    private String txtStr;
    private int txtColor;

    public TasksRopeCompletedView(Context context, AttributeSet attrs) {
        super(context, attrs);
      // 获取自定义的属性
        this.context = context;
        initAttrs(context, attrs);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TasksCompletedView, 0, 0);
// mRadius =
// typeArray.getDimension(R.styleable.TasksCompletedView_radius,
// 80);
//
        icon = typeArray.getDrawable(R.styleable.TasksCompletedView_certreIcon);
        boolean isLock = typeArray.getBoolean(R.styleable.TasksCompletedView_completedview_lock, false);
        /*if (isLock) {

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sport_lock);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sport_stop);

        }*/
      /*  imgWidth = bitmap.getWidth();
        ingHight = bitmap.getHeight();*/

        mStrokeWidth = typeArray.getDimension(
                R.styleable.TasksCompletedView_strokeWidth, 10);
        // mCircleColor = 0xffffffff;
        mCircleColor = typeArray.getColor(
                R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
        mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);

        txtColor = typeArray.getColor(R.styleable.TasksCompletedView_taskView_txt_color, Color.WHITE);
        typeArray.recycle();

        initVariable();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRadius = getWidth() / 2 - dip2px(context, 8);
        mRingRadius = mRadius + mStrokeWidth / 2;


    }

    public void setRingColor(int color) {
        mRingColor = color;
        invalidate();
    }

    private void initVariable() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);

       /* nRingPaint = new Paint();
        nRingPaint.setAntiAlias(true);
        nRingPaint.setColor(mRingColor);
        nRingPaint.setStyle(Paint.Style.STROKE);
        nRingPaint.setStrokeWidth(mStrokeWidth);
*/
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(dip2px(getContext(),3f));
        mTextPaint.setTextSize(dip2px(getContext(),25f));

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;


        //mCirclePaint.setColor(Color.RED);
        mCirclePaint.setColor(mCircleColor);
        canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

        if (progress > 0) {
            RectF oval = new RectF();
            oval.left = mXCenter - mRingRadius;
            oval.top = mYCenter - mRingRadius;
            oval.right = mXCenter + mRingRadius;
            oval.bottom = mYCenter + mRingRadius;
            canvas.drawArc(oval, -90, 1.0f * progress * 3.6f, false, mRingPaint);
        } else {
            RectF oval = new RectF();
            oval.left = (mXCenter - mRingRadius);
            oval.top = (mYCenter - mRingRadius);
            oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
            oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
            canvas.drawArc(oval, -90, 0, false, mRingPaint); //
        }

        if(!TextUtils.isEmpty(txtStr)){
            mTextPaint.setColor(Color.WHITE);
            //-DIYViewUtil.getTextWidth(txtStr,mTextPaint)/2
            //-DIYViewUtil.getTextHeight(txtStr,mTextPaint)
            canvas.drawText(txtStr,mXCenter- DIYViewUtil.getTextWidth(txtStr,mTextPaint)/2,mYCenter+ DIYViewUtil.getTextHeight(txtStr,mTextPaint)/2,mTextPaint);
        }
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;// invalidate();
        invalidate();


    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public void setTxtStr(String txtStr) {
        this.txtStr = txtStr;
        postInvalidate();
    }

    public void setTxtColor(int txtColor) {
        this.txtColor = txtColor;
        postInvalidate();
    }

    public void setmCircleColor(int mCircleColor) {
        this.mCircleColor = mCircleColor;
        postInvalidate();
    }
}
