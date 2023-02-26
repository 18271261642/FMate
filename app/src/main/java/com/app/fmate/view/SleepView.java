package com.app.fmate.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.app.fmate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 渐变心电图
 */
public class SleepView extends View {

    /* ********************* 外部使用的属性 *********************** */
    public static final float TYPE_DEEP = 1f;//坐标为deep类型
    public static final float TYPE_SHALLOW = 2f;//坐标为shallowe类型
    public static final float TYPE_EYE_MOVEMENT = 3f;//眼动
    public static final float TYPE_WAKE = 4f;//坐标为wake类型
    /* ********************* 外部设置的属性 *********************** */
    //颜色属性
    private int deepColor = getResources().getColor(R.color.deepColor);//深度睡眠的颜色
    private int shallowColor = getResources().getColor(R.color.shallowColor);//浅度睡眠的颜色
    private int wakeColor = getResources().getColor(R.color.wakeColor);//清醒的颜色
    private int eyeMovementColor = getResources().getColor(R.color.black);//眼动的颜色
    private int textGrayColor = getResources().getColor(R.color.bottom_nav_icon_dim);//文字灰色
    //线条属性
    private float widthRatio = 0.05f;//线条宽度百分比
    private float vWidthRatio = 0.005f;//竖线宽度百分比
    private float marginYRatio = 0.005f;//线条距离y轴的距离
    //画板四周边距

    private float marginLeftRatio = 0.05f;//画板左边距百分比
    private float marginRightRatio = 0.005f;//画板右边距百分比
    private float marginTopRatio = 0.005f;//画板上边距百分比
    private float marginBottomRatio = 0.05f;//画板下边距百分比
    //文字属性
    private float xTextRatio = 0.05f;//x轴文字的大小百分比
    private float yTextRatio = 0.05f;//y轴文字的大小百分比
    //y坐标的描述语
//    private String eyeMovementString = "眼动";
//    private String wakeString = "清醒";
//    private String shallowString = "浅睡";
//    private String deepString = "深睡";

    //动画
    private int animationTime = 1000;//动画持续时间

    /**
     * 睡眠数据列表,规则如下
     * float[0]:类型,1f：醒来 2f：浅睡 3f：深睡
     * float[1]:距离开始时间的百分比(开始时间/横坐标总时长)
     * float[2]:持续时间长的百分比(持续时间/横坐标总时长)
     */
    private List<float[]> timeArray = new ArrayList<>();
    //x坐标轴的文字描述列表
    private List<String> xAxisArray = new ArrayList<>();
    //获取x轴睡眠的起始位置块和结束位置块
    private List<Float>startList=new ArrayList();
    private List<Float>endList=new ArrayList();

    /* ********************* 内部使用的属性 *********************** */
    private Paint linePaint;//画线的画笔
    private Paint textPaint;//画文字画笔
    private Paint moveLinePaint;//移动光标
    private float wakeYAxis;//睡醒坐标点的y坐标值
    private float shallowYAxis;//浅睡坐标点的y坐标值
    private float deepYAxis;//深睡坐标点的y坐标值
    private float eyeMovementYAxis;//眼动坐标点的y坐标值

    private float canvasWidth; //线条区域的宽度
    float xDistcance;//X轴的起始位置
    //动画监听
    private LineAnimator lineAnimator = new LineAnimator(animation -> postInvalidate());

    public SleepView(Context context) {
        this(context, null);
    }

    public SleepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //View被窗体移除的时候释放动画资源
        lineAnimator.release();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFoucus) {
        super.onWindowFocusChanged(hasFoucus);
        //View焦点变化
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        lineAnimator.start(animationTime);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() > 0 && getHeight() > 0) {
            //线条区域的高度
            float canvasHeight = (1f - marginTopRatio - marginBottomRatio - xTextRatio) * getHeight();
            eyeMovementYAxis = (yTextRatio + marginTopRatio) * getHeight();
            wakeYAxis = (yTextRatio + marginTopRatio) * getHeight() + canvasHeight / 4;
            shallowYAxis = (yTextRatio + marginTopRatio) * getHeight() + 2 * canvasHeight / 4;
            deepYAxis = (yTextRatio + marginTopRatio) * getHeight() + 3 * canvasHeight / 4;
            //画y坐标
            textPaint.setTextSize(yTextRatio * getHeight());
            textPaint.setColor(Color.BLACK);
            textPaint.setTextAlign(Paint.Align.CENTER);

//            canvas.drawText(eyeMovementString, marginLeftRatio * getWidth(), eyeMovementYAxis, textPaint);
//            canvas.drawText(wakeString, marginLeftRatio * getWidth(), wakeYAxis, textPaint);
//            canvas.drawText(shallowString, marginLeftRatio * getWidth(), shallowYAxis, textPaint);
//            canvas.drawText(deepString, marginLeftRatio * getWidth(), deepYAxis, textPaint);
           // float yWidth = getMaxYWidth(textPaint);
            //伸展动画
            float mPhaseX = lineAnimator.getPhaseX();

            canvasWidth = ((1f - marginLeftRatio - marginRightRatio - marginYRatio) * getWidth() /*- yWidth*/) * mPhaseX;
         //   Log.e("onDraw: ", "canvasWidth++" + canvasWidth);
            xDistcance = /*yWidth +*/ (marginLeftRatio + marginYRatio) * getWidth();
          //  Log.e("onDraw: ", "xDistcance++" + xDistcance);
            //画x坐标
            if (this.xAxisArray != null && this.xAxisArray.size() > 0) {
                textPaint.setTextSize(xTextRatio * getHeight());
                textPaint.setColor(textGrayColor);
                textPaint.setTextAlign(Paint.Align.CENTER);
                for (int i = 0; i < xAxisArray.size(); i++) {
                    if(i==(xAxisArray.size()-1)) {
                        canvas.drawText(xAxisArray.get(i), getWidth() - 0.1f * getWidth(), (1 - marginBottomRatio) * getHeight(), textPaint);
                    }
                    else {
                        canvas.drawText(xAxisArray.get(i), xDistcance + i * canvasWidth / xAxisArray.size(), (1 - marginBottomRatio) * getHeight(), textPaint);
                    }
                    }
            }
            //画线
            if (this.timeArray != null && this.timeArray.size() > 0) {
                linePaint.setStrokeWidth(widthRatio * getHeight());
                startList=new ArrayList<>();
                endList=new ArrayList<>();
                for (int i = 0; i < timeArray.size(); i++) {
                    float[] selectValue = timeArray.get(i);
                    if (selectValue.length >= 3) {
                        float sleepType = selectValue[0];//类型  默认 不存在的情况就是这个类型?

                        float startXAxis = xDistcance + selectValue[1] * canvasWidth;//开始时间
                        float endXAxis = startXAxis + selectValue[2] * canvasWidth;//持续时间
                        //X轴底部长线
                        //  if(timeArray.get(0)!=null)
                        //    canvas.drawLine(xDistcance+timeArray.get(0)[1]*canvasWidth,  deepYAxis+20, getWidth(), deepYAxis+20, moveLinePaint);
                        startList.add(startXAxis);
                        endList.add(endXAxis);
                        float yAxis = eyeMovementYAxis;
                        //求坐标点Y轴坐标
                        if (sleepType == TYPE_EYE_MOVEMENT) {
                            yAxis = eyeMovementYAxis;
                            linePaint.setColor(eyeMovementColor);
                        } else if (sleepType == TYPE_WAKE) {
                            yAxis = wakeYAxis;
                            linePaint.setColor(wakeColor);
                        } else if (sleepType == TYPE_SHALLOW) {
                            yAxis = shallowYAxis;
                            linePaint.setColor(shallowColor);
                        } else if (sleepType == TYPE_DEEP) {
                            yAxis = deepYAxis;
                            linePaint.setColor(deepColor);
                        }
                        linePaint.setStrokeCap(Paint.Cap.ROUND);
                        linePaint.setStrokeJoin(Paint.Join.ROUND);
                        //设置横线宽度
                        linePaint.setStrokeWidth(widthRatio * canvasWidth);
                        float vWidth = vWidthRatio * canvasWidth-1f;
                        linePaint.setShader(null);
                        float flo = (widthRatio * canvasWidth) / 2;
                        float start = startXAxis - vWidth / 2 + flo;
                        float end = endXAxis + vWidth / 2 - flo;
//                        startList.add(start);
//                        endList.add(end);
                        if (end - start <= flo / 2) {
                            float width = endXAxis + vWidth - startXAxis;
                            linePaint.setStrokeWidth(width);
                            canvas.drawLine(startXAxis - vWidth / 2 + width / 2, yAxis - flo, startXAxis - vWidth / 2 + width / 2, yAxis + flo, linePaint);
//                            startList.add(startXAxis - vWidth / 2 + width / 2);
//                            endList.add(startXAxis - vWidth / 2 + width / 2);
                        } else {
                            canvas.drawLine(start, yAxis, end, yAxis, linePaint);
//                            startList.add(start);
//                            endList.add(end);
                        }
                        if (i < timeArray.size() - 1) {
                            drawNext(canvas, linePaint, sleepType, endXAxis, yAxis, xDistcance, canvasWidth, timeArray.get(i + 1));
                        }
                    }
                }
            }
        }

      //  Log.e("onDraw: ", "startX++" + startX + " endX==  " + endX);
        canvas.drawLine(startX, startY, endX, endY, moveLinePaint);
    }

    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {
        //初始化属性
        if (attrs != null) {
            //初始化布局属性
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SleepQualityView, 0, 0);
            marginLeftRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_marginLeftRatio, marginLeftRatio);
            marginRightRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_marginRightRatio, marginRightRatio);
            marginTopRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_marginTopRatio, marginTopRatio);
            marginBottomRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_marginBottomRatio, marginBottomRatio);
            widthRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_widthRatio, widthRatio);
            vWidthRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_vwidthRatio, vWidthRatio);
            marginYRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_marginYRatio, marginYRatio);
            xTextRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_xTextRatio, xTextRatio);
            yTextRatio = typedArray.getFloat(R.styleable.SleepQualityView_sq_yTextRatio, yTextRatio);
            animationTime = typedArray.getInteger(R.styleable.SleepQualityView_sq_animationTime, animationTime);
        }
        //初始化画笔
        linePaint = new Paint();
        linePaint.setAntiAlias(true); // 抗锯齿
        linePaint.setDither(true); // 防抖动
        // linePaint.setStrokeCap(Paint.Cap.ROUND); // 把每段圆弧改成圆角的
        linePaint.setStyle(Paint.Style.STROKE);
        //文字画笔
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setDither(true); // 防抖动
        textPaint.setAntiAlias(true);// 抗锯齿
        textPaint.setStrokeWidth(1f);//画笔宽度
        //移动画线
        moveLinePaint = new Paint();
        moveLinePaint.setColor(Color.parseColor("#00B7EE"));
        moveLinePaint.setAntiAlias(true);//消除锯齿
        moveLinePaint.setDither(true); // 防抖动
        moveLinePaint.setStrokeWidth(1f);
        startX = getWidth() / 2;
     //   Log.e("init: ", "startX+=" + startX);
    }

    /**
     * 读取x轴开始距离
     */
//    private float getMaxYWidth(Paint paint) {
//        float defaultValue;
////        float wakeDistance = paint.measureText(wakeString);
////        float shallowDistance = paint.measureText(shallowString);
////        float deepDistance = paint.measureText(deepString);
////        float eyeMovementDistance = paint.measureText(eyeMovementString);
//        defaultValue = Math.max(wakeDistance, shallowDistance);
//        defaultValue = Math.max(defaultValue, deepDistance);
//        defaultValue = Math.max(defaultValue, eyeMovementDistance);
//        return defaultValue;
//    }

    /**
     * 绘制两点间隔线
     */
    private void drawNext(Canvas canvas, Paint paint, float sleepType, float endXAxis, float yAxis, float xDistcance, float canvasWidth, float[] nextValue) {
        float sleepNextType = nextValue[0];//类型
        float startNextXAxis = xDistcance + nextValue[1] * canvasWidth;//开始时间
        float yNextAxis = eyeMovementYAxis;
//        int beginColor = wakeColor;
        int beginColor = eyeMovementColor;
        int endColor = shallowColor;
        if (sleepType == TYPE_EYE_MOVEMENT) {
//            Log.e("drawNext: ", "beginColor==" + beginColor);
            beginColor = eyeMovementColor;
        } else if (sleepType == TYPE_WAKE) {
            beginColor = wakeColor;
        } else if (sleepType == TYPE_SHALLOW) {
            beginColor = shallowColor;
        } else if (sleepType == TYPE_DEEP) {
            beginColor = deepColor;
        }
        if (sleepNextType == TYPE_EYE_MOVEMENT) {
            endColor = eyeMovementColor;
            yNextAxis = eyeMovementYAxis;
            //    Log.e("drawNext: ", "endColor==" + endColor + " ,  yNextAxis==" + yNextAxis);
        } else if (sleepNextType == TYPE_WAKE) {
            endColor = wakeColor;
            yNextAxis = wakeYAxis;
        } else if (sleepNextType == TYPE_SHALLOW) {
            endColor = shallowColor;
            yNextAxis = shallowYAxis;
        } else if (sleepNextType == TYPE_DEEP) {
            endColor = deepColor;
            yNextAxis = deepYAxis;
        }
        Shader mShader = new LinearGradient(endXAxis, yAxis, startNextXAxis, yNextAxis, new int[]{beginColor, endColor}, null, Shader.TileMode.MIRROR);
        paint.setShader(mShader);
        //设置竖线宽度
//        paint.setStrokeWidth(vWidthRatio * canvasWidth);
        paint.setStrokeWidth(1f);
//        TLog.Companion.error("vWidthRatio * canvasWidth+="+(vWidthRatio * canvasWidth));
        canvas.drawLine(endXAxis, yAxis, startNextXAxis, yNextAxis, paint);
    }

    /**
     * 设置数据源
     *
     * @param timeArray  高度比
     * @param xAxisArray x坐标值
     */
    public void setDataSource(List<float[]> timeArray, List<String> xAxisArray) {
        if (timeArray != null && xAxisArray != null) {
            List<float[]> newTimeArray = new ArrayList<>();
            float[] firstTimeValue = timeArray.get(0);
            if (firstTimeValue.length > 2) {
                float begin = firstTimeValue[1];
                if (begin != 0f) {
                    //补上头
                    newTimeArray.add(new float[]{1f, 0f, begin});
                }
            }
            //加上第一个
            newTimeArray.add(firstTimeValue);
            //补中间
            for (int i = 1; i < timeArray.size(); i++) {
                float[] lastValue = timeArray.get(i - 1);
                float[] timeValue = timeArray.get(i);
                if (timeValue.length > 2 && lastValue.length > 2) {
                    float begin = timeValue[1];
                    float lastBegin = lastValue[1];
                    float lastProgress = lastValue[2];
                    //误差大于五位小数就表示中间要插值
                    if (begin - (lastBegin + lastProgress) > 0.00001) {
                        newTimeArray.add(new float[]{1f, lastBegin + lastProgress, begin - (lastBegin + lastProgress)});
                    }
                }
                newTimeArray.add(timeValue);
            }
            float[] lastTimeValue = timeArray.get(timeArray.size() - 1);
            if (lastTimeValue.length > 2) {
                float begin = lastTimeValue[1];
                float progress = lastTimeValue[2];
                if ((begin + progress) != 1f) {
                    //补上尾
                    newTimeArray.add(new float[]{1f, (begin + progress), (1f - begin - progress)});
                }
            }
            this.timeArray = newTimeArray;
            this.xAxisArray = xAxisArray;
            lineAnimator.start(animationTime);
        }
    }

    /**
     * 设置Y坐标描述符
     */
    public void setYAxisString(String wakeString, String shallowString, String deepString) {
//        this.wakeString = wakeString;
//        this.shallowString = shallowString;
//        this.deepString = deepString;
        invalidate();
    }

    /**
     * 设置横线的宽度
     */
    public void setWidthRatio(float widthRatio) {
        if (this.widthRatio != widthRatio) {
            this.widthRatio = widthRatio;
            invalidate();
        }
    }

    /**
     * 设置顔色
     */
    public void setLineColor(int wakeColor, int shallowColor, int deepColor, int eyeMovementColor) {
        this.wakeColor = wakeColor;
        this.shallowColor = shallowColor;
        this.deepColor = deepColor;
        this.eyeMovementColor = eyeMovementColor;
        invalidate();
    }

    /**
     * 清空画布
     */
    public void clearView() {
        linePaint.setShader(null);
        textPaint.setShader(null);
    }

    /**
     * 直线绘制持续的动画类
     */
    private class LineAnimator {

        private float mPhaseX = 1f; //默认动画值0f-1f
        private ValueAnimator.AnimatorUpdateListener mListener;//监听
        private ObjectAnimator objectAnimator;

        private LineAnimator(ValueAnimator.AnimatorUpdateListener listener) {
            mListener = listener;
        }

        private float getPhaseX() {
            return mPhaseX;
        }

        private void setPhaseX(float phase) {
            mPhaseX = phase;
        }

        /**
         * Y轴动画
         *
         * @param durationMillis 持续时间
         */
        private void start(int durationMillis) {
            release();
            objectAnimator = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
            objectAnimator.setDuration(durationMillis);
            objectAnimator.addUpdateListener(mListener);
            objectAnimator.start();
        }

        /**
         * 释放动画
         */
        private void release() {
            if (objectAnimator != null) {
                objectAnimator.end();
                objectAnimator.cancel();
                objectAnimator = null;
            }
        }
    }

    private float startX = getWidth() / 2, startY;
    private float endX = getWidth() / 2, endY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取手势当下坐标
        float crtX = event.getX();
        float crtY = event.getY();
     //   Log.e( "onTouchEvent: ","startList 长度=="+startList.size() );
//        Log.e( "onTouchEvent: ","timeArray =="+ new Gson().toJson(timeArray));
//        Log.e("onTouchEvent: ", "  crtX+==" + crtX + ",   crtY+==" + crtY);
//        Log.e( "onTouchEvent: ","startList+="+new  Gson().toJson(startList));
//        Log.e( "onTouchEvent: ","endList+="+new  Gson().toJson(endList) );
        if (crtX >= xDistcance && crtX <= (canvasWidth + xDistcance)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    //按下时，开始  位置 和 结束位置 都是在一个点就是当前的点，
                    startX = crtX;
                    endX = crtX;
                    invalidate();//重新执行onDraw
                    for (int i = 0; i <timeArray.size() ; i++) {
                        if(startList.get(i)<=crtX&&endList.get(i)>crtX)
                        {
                           // Log.e("onTouchEvent: ","返回坐标++"+i);
                        }
                    }
                    if(mOnTouchListener!=null)
                    mOnTouchListener.onTouch((int) startX,crtX/(canvasWidth + xDistcance));
                    break;
            }
            startY = 40;
            endY = getHeight() - 80;
        } else {
            startX = 0;
            startY = 0;
            endX = 0;
            endY = 0;
            invalidate();
        }

        return true;
    }

    private OnTouchListener mOnTouchListener;

    public void setOnTouchHandler(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    public interface OnTouchListener {
        void onTouch(int position,float percentage);
    }
}
