package com.app.fmate.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.app.fmate.R;
import com.app.fmate.utils.ChartUtils;
import com.app.fmate.utils.DensityUtil;

/**
 * 血压测量刻度进度条
 */
public class BpMeasureView extends View {

    private Context mContext;
    private Paint mPaint; // 画笔对象的引用
    private float mRoundWidth = DensityUtil.dp2px(2); // 圆环的宽度


    //背景的刻度画笔
    private Paint bgPaint;

    private int centerX, centerY;
    private int radius, roundRadius;
    private int paddingOuterThumb;//外边距

    private int minValidateTouchArcRadius; // 最小有效点击半径
    private int maxValidateTouchArcRadius; // 最大有效点击半径

    private int mCenterTxtColor; //圆中心 字体 颜色、大小
    private float mCenterTxtSize, mCenterBottomSize;
    private String mCenterStatu = "检测中"; //中间状态文字

    private int mMainColor; //主题颜色

    private int mInnerRoundColor; //内圆 宽度 、颜色
    private float mInnerRoundWidth;


    private int mTxtProgress = 1; // 显示进度
    private int max = 72; // 最大进度 -- 总共72个刻度 所以这样定义
    private float progress = 1;

    private long mOuterRoundTime = 2000;//毫秒
    private double mOuterRoundProgress = 0f;//外圈进度
    private boolean mOuterSences = true; //true 正向----false方向
    private float mCount = 0f;
    private double mCountG = 0f;
    private int mIntCount = 0;

    public BpMeasureView(Context context) {
        this(context, null);
    }

    public BpMeasureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpMeasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(attrs);
    }


    private void initView(AttributeSet attrs){
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);  // 关闭硬件加速
        this.setWillNotDraw(false);                    // 调用此方法后，才会执行 onDraw(Canvas) 方法
        mPaint = new Paint();

        //获取自定义属性和默认值
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.BpMeasureViewStyleable);

        mCenterTxtColor =  typedArray.getColor(R.styleable.BpMeasureViewStyleable_center_txt_color, getResources().getColor(R.color.white));
        mCenterTxtSize = typedArray.getDimension(R.styleable.BpMeasureViewStyleable_center_txt_size, DensityUtil.dp2px(47));
        mCenterBottomSize = typedArray.getDimension(R.styleable.BpMeasureViewStyleable_center_txt_bottom_size, DensityUtil.dp2px(13));
        mCenterStatu = typedArray.getString(R.styleable.BpMeasureViewStyleable_center_txt_status);

        mRoundWidth = typedArray.getDimension(R.styleable.BpMeasureViewStyleable_round_width, DensityUtil.dp2px(3));
        mMainColor = typedArray.getColor(R.styleable.BpMeasureViewStyleable_round_color, getResources().getColor(R.color.bp_schedule_bg_color));

        mInnerRoundWidth = typedArray.getDimension(R.styleable.BpMeasureViewStyleable_inner_round_width, DensityUtil.dp2px(2));
        mInnerRoundColor = typedArray.getColor(R.styleable.BpMeasureViewStyleable_inner_round_color, getResources().getColor(R.color.bp_schedule_color));

        typedArray.recycle();
        paddingOuterThumb = DensityUtil.dp2px(10);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        centerX = width / 2;
        centerY = height / 2;
        int minCenter = Math.min(centerX, centerY);

        radius = (int) (minCenter - mRoundWidth / 2 - paddingOuterThumb); //圆环的半径
        roundRadius = radius - (int)(3 * mRoundWidth);
        minValidateTouchArcRadius = (int) (radius - paddingOuterThumb * 1.5f);
        maxValidateTouchArcRadius = (int) (radius + paddingOuterThumb * 1.5f);
        super.onSizeChanged(width, height, oldw, oldh);
    }

    @Override
    public void onDraw(Canvas canvas) {
      //  setLayerType(LAYER_TYPE_SOFTWARE, null);//对单独的View在运行时阶段禁用硬件加速
        initOnDraw(canvas);
    }

    /** start circle -*/
    private void initOnDraw(Canvas canvas) {
        //背景
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStrokeWidth(5f);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bgPaint.setColor(getResources().getColor(R.color.bp_schedule_bg_color));
        bgPaint.setAntiAlias(true);

        /** 画文字 start */
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mCenterTxtColor);
        mPaint.setAntiAlias(true);  //消除锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mCenterTxtSize);

        String textPrgress = mTxtProgress + "%" ;
        float txtWidth = mPaint.measureText(textPrgress);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        float txtHeight = ChartUtils.getTxtHeight(textPrgress, mPaint);
        canvas.drawText(textPrgress, centerX - txtWidth / 2, centerY + txtHeight / 4, mPaint);

        /******************* 进度下方文字 start */
        mPaint.setTextSize(mCenterBottomSize);
        float textWidth2 = mPaint.measureText(mCenterStatu);
        float detetionHeight = ChartUtils.getTxtHeight(mCenterStatu, mPaint);
        canvas.drawText(mCenterStatu, centerX - textWidth2 / 2, centerY + txtHeight * 3 / 4  + detetionHeight, mPaint);
        /******************* 进度下方文字 end */


        /** 画圆 start */
        mPaint.setShadowLayer(0, 0, 0, mMainColor);
        mPaint.setColor(mMainColor);


        /** 画圆 end  */

        for(int k = 0;k<72;k++){
            float mProgress = (k)* 1.0f/ 72 * max;
            PointF mProgressPoint = ChartUtils.calcArcEndPointXY(centerX, centerY, radius, 360 * mProgress / max, 270);
            //圆上到圆心
            float scale1 = radius * 1.0F / mRoundWidth;
            float scale2 = radius * 1.0F / (radius - mRoundWidth);
            //计算内圆上的点
            float disX = (scale1*mProgressPoint.x + scale2*centerX)/(scale1+ scale2);
            float disY =  (scale1*mProgressPoint.y + scale2*centerY)/(scale1+ scale2);
            //计算外圆上的点
            float disX2 = mProgressPoint.x*2 - disX;
            float disY2 =  mProgressPoint.y*2 - disY;
            mPaint.setColor(Color.parseColor("#331d8bee"));
            mPaint.setStrokeWidth(5f);
            canvas.drawLine(disX2 ,disY2,disX,disY, mPaint);

        }


        /** 画刻度-72份- 还分正反切换---start */
        mPaint.setStrokeWidth(DensityUtil.dp2px(2));
        for (int i = 0; i < 72; i++){
            //radius:模糊半径，radius越大越模糊，越小越清晰，但是如果radius设置为0，则阴影消失不见
            //dx:阴影的横向偏移距离，正值向右偏移，负值向左偏移
            //dy:阴影的纵向偏移距离，正值向下偏移，负值向上偏移
            //color: 绘制阴影的画笔颜色，即阴影的颜色（对图片阴影无效）

            if (i < mOuterRoundProgress) {
                if (mOuterSences) {
                    // mPaint.setShadowLayer(30, 0, 0, mMainColor);
                    mPaint.setColor(getResources().getColor(R.color.bp_schedule_color));
                } else
                    mPaint.setColor(getResources().getColor(R.color.bp_schedule_bg_color));
            } else {
                if (mOuterSences)
                    mPaint.setColor(getResources().getColor(R.color.bp_schedule_bg_color));
                else {
                    // mPaint.setShadowLayer(30, 0, 0, mMainColor);
                    mPaint.setColor(getResources().getColor(R.color.bp_schedule_color));
                }
            }
            float mProgress = (i)* 1.0f/ 72 * max;
            PointF mProgressPoint = ChartUtils.calcArcEndPointXY(centerX, centerY, radius, 360 * mProgress / max, 270);
            //圆上到圆心
            float scale1 = radius * 1.0F / mRoundWidth;
            float scale2 = radius * 1.0F / (radius - mRoundWidth);
            //计算内圆上的点
            float disX = (scale1*mProgressPoint.x + scale2*centerX)/(scale1+ scale2);
            float disY =  (scale1*mProgressPoint.y + scale2*centerY)/(scale1+ scale2);
            //计算外圆上的点
            float disX2 = mProgressPoint.x*2 - disX;
            float disY2 =  mProgressPoint.y*2 - disY;

            canvas.drawLine(disX2 ,disY2,disX,disY, mPaint);
        }
        /** 画刻度-72份- 还分正反切换---end */

    }


    public void setProgressX(long time, boolean mSence){
        this.mOuterSences = mSence;
        this.mOuterRoundTime = time;
        startProgressX();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (mIntCount < mCount) {
                        //需要执行的代码
                        if (mOuterSences) {
                            mOuterRoundProgress = getOuterProger(mIntCount);
                        } else {
                            mOuterRoundProgress = getOuterProger2(mIntCount);
                        }
                        Log.e("tag", mOuterRoundProgress + "");
                        mIntCount++;
                        postInvalidate();
                        Thread.sleep(10);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void startProgressX() {
        //使用自由落體的公式---h=1/2*g*t2
        mCount = 0f;
        mCountG = 0f;
        mCount = (int) mOuterRoundTime / 10; //默认每50毫秒执行一次方法
        //mOuterRoundProgress；
        mOuterRoundProgress = 0f;
        mCountG = 200f / (mCount * mCount);
        //mCountG = 0.12f;
        mIntCount = 0;
    }

    private double getOuterProger(int counts){
        double progress = 0.5f * mCountG * counts * counts;
        return  progress;
    }

    private double getOuterProger2(int count){
        double progress = 100 - 0.5f * mCountG * (mCount - count) * (mCount - count);
        return progress * 72 / 100;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(float progress) {
        if (progress < 0) {
            mTxtProgress = 1;
            progress = 0;
        }

        mTxtProgress =  Math.round(progress);
        float ss = progress * 72 / 100;
        progress = (int) ss;
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
            mOuterRoundProgress = progress + 1;
        }
        if (progress <= max) {
            this.progress = progress;
            mOuterRoundProgress = progress + 1;
            postInvalidate();
        }

    }
}
