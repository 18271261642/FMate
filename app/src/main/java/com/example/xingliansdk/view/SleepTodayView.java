package com.example.xingliansdk.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.RequiresApi;

import com.example.xingliansdk.R;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;
import java.util.List;

public class SleepTodayView
        extends View {
    private Context context;
    public static OnTouch onTouch = null;
    List<List<SleepViewData>> ALLdata = new ArrayList<>();

    // 底部色块
    private final int bottomColor = Color.parseColor("#012E3D");
    // 文字颜色
    private final int textGrayColor = Color.parseColor("#778899");
    //4.清醒
    private final int wakeColor = Color.parseColor("#FFD96D");
    private final int BGone = Color.parseColor("#E1EFFF");
    //3.快速眼动
    private final int remSleepColor = Color.parseColor("#FF7E89");
    private final int BGtwo = Color.parseColor("#E1EFFF");
    //2.浅睡
    private final int lightSleepColor = Color.parseColor("#FF3FF3");
    private final int BGthere = Color.parseColor("#E1EFFF");
    //1.深睡
    private final int deepSleepColor = Color.parseColor("#9030EA");
    private final int BGfour = Color.parseColor("#E1EFFF");

    //入睡时长
    private final int wakeUpTimeColor = Color.parseColor("#FD801F");
    private final int BGFive = Color.parseColor("#E1EFFF");


    private Paint backPaint;
    private Paint backimgline;
    private Paint backimgshowdow;
    private Paint textPaint;
    //无数据时的布局
    private StaticLayout noDataLayout;
    // 每分钟占用的宽度
    float avgWidth;
    //睡眠时长分钟
    int duration;
    // 动画值
    float mAnimateValue;
    // 单个睡眠矩形块高度
    float mSleepRectHeight;

    private Paint linePaint;
    //线宽
    float lineWith;
//    //睡眠位图
//    Bitmap sleepBitmap;
////    //起床位图
//    Bitmap getupBitmap;
////    //位图左侧位置
    float mBitmapLeft;
//    //位图右侧位置
    float mBitmapRight;
    //底部滑块用
//    Bitmap seekBarBitmap;
    PaintFlagsDrawFilter mDrawFilter;
    //
    ObjectAnimator objectAnimator = null;
    //顶部预留高度
    float reservedTop;
    //底部预留高度
    float reservedBottom;


    public SleepTodayView(Context context) {
        this(context, null);
        this.context = context;
    }

    public SleepTodayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public SleepTodayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SleepTodayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    /**
     * dip转px
     */
    public static int dip2px(Context context, float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5F);
    }

    /**
     * drawable转bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config btConfig;
        if (drawable.getOpacity() != PixelFormat.OPAQUE) {
            btConfig = Bitmap.Config.ARGB_8888;
        } else {
            btConfig = Bitmap.Config.RGB_565;
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, btConfig);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
//        float density = dm.density;
        //        int height = dm.heightPixels;
        return dm.widthPixels;
    }

    /**
     * 获取文字宽度
     */
    private static int getTextWith(Paint paint, String text) {
        paint.measureText(text);
        Rect localRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), localRect);
        return localRect.width();
    }

    /**
     * 获取文字高度
     */
    private static int getTexthight(Paint paint, String text) {
        paint.measureText(text);
        Rect localRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), localRect);
        return localRect.height();
    }

    //初始化
    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        //线刷
        this.linePaint = new Paint();
        this.linePaint.setAntiAlias(true);
        this.linePaint.setDither(true);
        this.linePaint.setStyle(Paint.Style.FILL);
        //文本刷
        this.textPaint = new Paint();
        this.textPaint.setDither(true);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setStrokeWidth(1.0F);
        this.textPaint.setTextSize(dip2px(this.context, 9.0F));
        this.textPaint.setColor(this.textGrayColor);
        this.textPaint.setTextAlign(Paint.Align.LEFT);
        //背景刷
        this.backPaint = new Paint();
        this.backPaint.setDither(true);
        this.backPaint.setAntiAlias(true);
        this.backPaint.setStyle(Paint.Style.FILL);
        //阴影刷
        this.backimgshowdow = new Paint();
        this.backimgshowdow.setDither(true);
        this.backimgshowdow.setAntiAlias(true);
        this.backimgshowdow.setStyle(Paint.Style.FILL);
        //背景线
        this.backimgline = new Paint();
        this.backimgline.setDither(true);
        this.backimgline.setAntiAlias(true);
        //四种线条位图
//        this.lineBitmap1 = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line1));
//        this.lineBitmap2 = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line1));
//        this.lineBitmap3 = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line1));
//        this.lineBitmap4 = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line1));

        //无数据时的布局
        TextPaint noDataPaint = new TextPaint();
        noDataPaint.setDither(true);
        noDataPaint.setAntiAlias(true);
        noDataPaint.setStrokeWidth(1.0F);
        noDataPaint.setTextAlign(Paint.Align.CENTER);
        noDataPaint.setTextSize(dip2px(this.context, 14.0F));
        noDataPaint.setColor(Color.parseColor("#86878C"));
//        noDataPaint.setStyle(Paint.Style.FILL);
        String txt = "无睡眠数据";
//        String txt = getResources().getString(R.string.sleep_nodata);
        this.noDataLayout = new StaticLayout(txt, noDataPaint, getTextWith(noDataPaint, txt)+10, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        // 底部滑块
       // this.seekBarBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.seekbar), dip2px(this.context, 61.0F), dip2px(this.context, 31.0F), false);
        this.mDrawFilter = new PaintFlagsDrawFilter(0, 3);
        //睡眠位图
     //   this.sleepBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sleep), dip2px(this.context, 19.0F), dip2px(this.context, 14.0F), false);
        //起床
    //    this.getupBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.getup), dip2px(this.context, 19.0F), dip2px(this.context, 14.0F), false);
        //顶部预留高度
        this.reservedTop = dip2px(this.context, 15.0F);
        //底部预留高度
        this.reservedBottom = dip2px(this.context, 25.0F);
        //线宽
        this.lineWith = dip2px(this.context, 0.43F);
        //顶部位图右侧，由于现在布局不是整个屏幕，只能使用getWidth计算，移到onLayout中处理
//        this.mBitmapRight = (getScreenWidth(this.context) - dip2px(this.context, 30.5F));
        //顶部位图左侧
        this.mBitmapLeft = dip2px(this.context, 30.5F);
    }

    //清除画布
    public void clearView() {
        this.ALLdata = null;
        this.linePaint.setShader(null);
        this.textPaint.setShader(null);
        this.backimgshowdow.setShader(null);
        this.backimgline.setShader(null);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * 画背景
     */
    private void drawBackground(Canvas canvas) {
        // 是否有数据
        if (this.ALLdata != null && this.ALLdata.size() > 0) {
            this.backPaint.setColor(this.bottomColor);
            //底部画矩形
//            canvas.drawRect(0.0F, getHeight() - dip2px(this.context, 17.5F), getWidth(), getHeight(), this.backPaint);
        } else {
            canvas.save();
            canvas.translate(getWidth() / 2.0F, getHeight() / 2.0F);
            this.noDataLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 画底部滑块
     */
    private void drawSeekBar(Canvas canvas) {
        if (this.ALLdata != null && this.ALLdata.size() > 0) {
            canvas.setDrawFilter(mDrawFilter);
      //      canvas.drawBitmap(this.seekBarBitmap, this.mBitmapRight - this.mBitmapLeft, getHeight() - dip2px(this.context, 30.5F), this.backPaint);
        }
    }

    /**
     * 画下一条线
     * 当前睡眠状态到下一个睡眠状态的连线
     */
    private void drawNext(Canvas canvas, int nextStatus, int curStatus, Float startX, Float stopX) {
        float y1 = 0.0F;
        float y2 = 0.0F;
        int lineColor = this.wakeColor;
        switch (curStatus) {
            case 5:
            case 4:
                lineColor = this.wakeColor;
                switch (nextStatus) {
                    case 1:
                        y1 = this.reservedTop + this.mSleepRectHeight;
                        y2 = this.reservedTop + this.mSleepRectHeight * 6.0F;
                        break;
                    case 2: //改好了并测试
                        y1 = this.reservedTop + this.mSleepRectHeight;
                        y2 = this.reservedTop + this.mSleepRectHeight * 4.0F;
                        break;
                    case 3:
                        y1 = this.reservedTop + this.mSleepRectHeight;
                        y2 = this.reservedTop + this.mSleepRectHeight * 2.0F;
                        break;
                    default:
                        y1 = this.reservedTop + this.mSleepRectHeight ;
                        y2 = y1;
                }

                break;
            case 3:
                lineColor = this.remSleepColor;
                switch (nextStatus) {
                    case 4://改好的
                        y1 = this.reservedTop + this.mSleepRectHeight ;
                        y2 = this.reservedTop + this.mSleepRectHeight *2F;
                        break;
                    case 2:
                        y1 = this.reservedTop + this.mSleepRectHeight * 3.0F;
                        y2 = this.reservedTop + this.mSleepRectHeight * 4.0F;
                        break;
                    case 1:
                        y1 = this.reservedTop + this.mSleepRectHeight * 3.0F;
                        y2 = this.reservedTop + this.mSleepRectHeight * 6.0F;
                        break;
                    default:
                        y1 = this.reservedTop + this.mSleepRectHeight * 3.0F;
                        y2 = y1;
                }

                break;
            case 2:
                lineColor = this.lightSleepColor;
                switch (nextStatus) {
                    case 1:  //改好了
                        y1 = this.reservedTop + this.mSleepRectHeight * 5.0F;
                        y2 = this.reservedTop + this.mSleepRectHeight * 6.0F;
                        break;
                    case 3:
                        y1 = this.reservedTop + this.mSleepRectHeight * 3.0F;
                        y2 = this.reservedTop + this.mSleepRectHeight * 4.0F;
                        break;
                    case 4:
                        y1 = this.reservedTop + this.mSleepRectHeight * 1.0F;
                        y2 = this.reservedTop + this.mSleepRectHeight * 4.0F;
                        break;
                    default:
                        y1 = this.reservedTop + this.mSleepRectHeight * 5.0F;
                        y2 = y1;
                        break;
                }
                break;
            case 1:
                lineColor = this.deepSleepColor;
                switch (nextStatus) {
                    case 3://1-3
                        y1 = this.reservedTop + this.mSleepRectHeight *3F;
                        y2 = this.reservedTop + this.mSleepRectHeight * 6.0F;
                        break;
                    case 2: //改好了
                        y1 = this.reservedTop + this.mSleepRectHeight * 5.0F;
                        y2 = this.reservedTop + this.mSleepRectHeight * 6.0F;
                        break;
                    case 4://改好了1-4跑先
                        y1 = this.reservedTop + this.mSleepRectHeight;
                        y2 = this.reservedTop + this.mSleepRectHeight * 6.0F;
                        break;
                    default:
                        y1 = this.reservedTop + this.mSleepRectHeight*7F;
                        y2 = y1;
                }
                break;
            default:
                break;
        }
        this.textPaint.setColor(lineColor);
        this.textPaint.setStrokeWidth(this.lineWith);
        canvas.drawLine(startX, y1 - this.mSleepRectHeight / 2.0F, stopX, y2 + this.mSleepRectHeight / 2.0F, this.textPaint);
    }

    /**
     * 计算总时长
     */
    private void calcDuration() {
        int duration = 0;
        if (this.ALLdata != null && this.ALLdata.size() > 0) {
            for (int i = 0; i < this.ALLdata.size(); i++) {
                for (int j = 0; j < this.ALLdata.get(i).size(); j++) {
                    SleepViewData data = this.ALLdata.get(i).get(j);
                    //todo 总时长是否包含清醒时长。
                    duration = duration + data.getDuration();
                }
            }
        }
        this.duration = duration;
    }

    /**
     * 处理数据的开始结束位置
     */
    private void handleData() {
        //根据原始数据=>计算startx,startY,stopX,stopY
        //计算每分钟平均宽度需要mSleepRectHeight(需要得到控件高度)和avgWidth(由于添加了动画需要动画参数=>动画参数获取又是有滞后的)
        if (this.ALLdata == null || this.ALLdata.size() == 0) {
            return;
        }
        if (this.ALLdata.size() == 1) {
            //一条数据
//            this.avgWidth = Integer.valueOf(getScreenWidth(this.context) - dip2px(this.context, 61.0F)).floatValue() / this.duration;
            this.avgWidth = Integer.valueOf(getWidth() - dip2px(this.context, 61.0F)).floatValue() / this.duration;
        } else {
            //多条数据
            int w = dip2px(this.context, 30.5F);
            int count = this.ALLdata.size();
//            this.avgWidth = Integer.valueOf(getScreenWidth(this.context) - dip2px(this.context, 61.0F) - w * (count - 1)).floatValue() / this.duration;
            this.avgWidth = Integer.valueOf(getWidth() - dip2px(this.context, 61.0F) - w * (count - 1)).floatValue() / this.duration;
        }
// 如果在ondraw里面处理会有动画效果，由于mAnimateValue最开始是0，动画产生后会变成1，造成重画
        this.avgWidth *= this.mAnimateValue;

        //这里getHeight获取要在view布局后才有值
        this.mSleepRectHeight = (getHeight() - (dip2px(this.context, 17.5F) + this.reservedBottom + this.reservedTop)) / 7.0F;
        float x, base = this.reservedTop;
        float h = this.mSleepRectHeight;
        float startX = 0, stopX = 0;
        float startY = 0, stopY = 0;

        for (int i = 0; i < this.ALLdata.size(); i++) {
            for (int j = 0; j < this.ALLdata.get(i).size(); j++) {
                SleepViewData data = this.ALLdata.get(i).get(j);
                int status = data.getStatus();
                if (status == 4 || status == 5) {
                    //清醒
                    startY = base;
                    stopY = base + h;
                } else if (status == 3) {
                    //快速眼动
                    startY = base + h * 2.0F;
                    stopY = base + h * 3.0F;
                } else if (status == 2) {
                    //浅睡
                    startY = base + h * 4.0F;
                    stopY = base + h * 5.0F;
                } else if (status == 1) {
                    //深睡
                    startY = base + h * 6.0F;
                    stopY = base + h * 7.0F;
                }
                //
                if (i == 0) {
                    if (j == 0) {
                        x = dip2px(this.context, 30.5F);
                    } else {
                        x = stopX;
                    }
                } else {
                    if (j == 0) {
                        x = stopX + dip2px(this.context, 30.5F);
                    } else {
                        x = stopX;
                    }
                }
                startX = x;
                stopX = x + this.ALLdata.get(i).get(j).getDuration() * this.avgWidth;
                data.setStartX(startX);
                data.setStartY(startY);
                data.setStopX(stopX);
                data.setStopY(stopY);
            }
        }
    }

    //画开始时间，开始图片，中间连接线 结束时间，结束图片
    private void drawBeginEnd(Canvas canvas) {
        int dataSize = this.ALLdata.size();
        //画开始时间，开始图片，中间连接线 结束时间，结束图片
        for (int i = 0; i < this.ALLdata.size(); i++) {
            for (int j = 0; j < this.ALLdata.get(i).size(); j++) {
                int curDataSize = this.ALLdata.get(i).size();
                SleepViewData curData = this.ALLdata.get(i).get(j);
                SleepViewData firstData = this.ALLdata.get(i).get(0);
                SleepViewData lastData = this.ALLdata.get(i).get(curDataSize - 1);
                String beginTime = curData.getBeginTime();
                String endTime = lastData.getEndTime();
                //睡眠 开始 的时间和图片
//                if (j == 0) {
//                    canvas.save();
//                    canvas.translate(0.0F, 0.0F);
//                    if (dataSize == 1) {
//                        this.textPaint.setTextSize(dip2px(this.context, 10.0F));
//                    } else {
//                        this.textPaint.setTextSize(dip2px(this.context, 8.0F));
//                    }
//                    this.textPaint.setColor(this.textGrayColor);
//                    this.textPaint.setTextAlign(Paint.Align.LEFT);
//                    canvas.drawText(beginTime, curData.getStartX(), Integer.valueOf(getTexthight(this.textPaint, beginTime)).floatValue(), this.textPaint);
//                //    canvas.drawBitmap(this.sleepBitmap, curData.getStartX() + getTextWith(this.textPaint, beginTime) / 2.0F - dip2px(this.context, 19.0F) / 2.0F, getTexthight(this.textPaint, beginTime) + dip2px(this.context, 3.0F), null);
//                    canvas.restore();
//                }
//                //起床 末尾 的图片
//                if (j + 1 == curDataSize) {
//                    canvas.save();
//                    // 是否超过右侧边距
//                    boolean isOver = firstData.getStartX() + getTextWith(this.textPaint, beginTime) >= lastData.getStopX() - getTextWith(this.textPaint, endTime);
//                    canvas.translate(0.0F, 0.0F);
//                    if (dataSize == 1) {
//                        this.textPaint.setTextSize(dip2px(this.context, 10.0F));
//                    } else {
//                        this.textPaint.setTextSize(dip2px(this.context, 8.0F));
//                    }
//                    this.textPaint.setColor(this.textGrayColor);
//                    this.textPaint.setTextAlign(isOver ? Paint.Align.LEFT : Paint.Align.RIGHT);
//                    float x = isOver ? lastData.getStartX() + getTextWith(this.textPaint, beginTime) / 2.0F : lastData.getStopX();
//                    canvas.drawText(endTime, x, Integer.valueOf(getTexthight(this.textPaint, endTime)).floatValue(), this.textPaint);
//                    if (!isOver) {
//                        x -= getTextWith(this.textPaint, endTime);
//                    }
//                //    canvas.drawBitmap(this.getupBitmap, x, getTexthight(this.textPaint, endTime) + dip2px(this.context, 3.0F), null);
//                    canvas.restore();
//                }
                // 中间部分 画连接线
                if (j + 1 != curDataSize) {
                    int curSleepState = curData.getStatus();
                    SleepViewData nextData = this.ALLdata.get(i).get(j + 1);
                    int nextSleepState = nextData.getStatus();
                    drawNext(canvas, nextSleepState, curSleepState, curData.getStopX(), curData.getStopX());
                }
            }
        }
    }

    /**
     * 画移动时候的背景线条
     */
    private void drawBackLine(Canvas canvas) {
        for (int i = 0; i < this.ALLdata.size(); i++) {
            for (int j = 0; j < this.ALLdata.get(i).size(); j++) {
                 if ((this.ALLdata.get(i).get(j).getStartX() <= this.mBitmapRight) && (this.ALLdata.get(i).get(j).getStopX() >=this.mBitmapRight)) {
                    int status = this.ALLdata.get(i).get(j).getStatus();
                    if (status == 1) {
                        this.backimgshowdow.setColor(this.BGone);
                    } else if (status == 2) {
                        this.backimgshowdow.setColor(this.BGtwo);
                    } else if (status == 3) {
                        this.backimgshowdow.setColor(this.BGthere);
                    } else if (status == 4 || status == 5) {
                        this.backimgshowdow.setColor(this.BGfour);
                    }
                    float startX = this.ALLdata.get(i).get(j).getStartX();
                    float startY = this.ALLdata.get(i).get(j).getStopY();
                    canvas.drawRect(this.lineWith + startX, startY - this.mSleepRectHeight / 2.0F, this.ALLdata.get(i).get(j).getStopX() - this.lineWith / 2.0F, getHeight() - dip2px(this.context, 35F), this.backimgshowdow);
                    canvas.setDrawFilter(mDrawFilter);
                } else {
                    this.backimgshowdow.setShader(null);
                    this.backimgline.setShader(null);
                }
            }
        }
    }

    /**
     * 画睡眠矩形块
     */
    private void drawSleepRect(Canvas canvas) {
        for (int i = 0; i < this.ALLdata.size(); i++) {
            for (int j = 0; j < (this.ALLdata.get(i)).size(); j++) {
                int status = (this.ALLdata.get(i)).get(j).getStatus();
                this.linePaint.setShader(null);
                if (status ==4) {
                    //清醒
                    this.linePaint.setColor(this.wakeColor);
                } else if (status == 3) {
                    //快速眼动
                    this.linePaint.setColor(this.remSleepColor);
                } else if (status == 2) {
                    //浅睡
                    this.linePaint.setColor(this.lightSleepColor);
                } else if (status == 1) {
                    //深睡
                    this.linePaint.setColor(this.deepSleepColor);
                }else if(status == 5){
                    this.linePaint.setColor(this.wakeUpTimeColor);
                }
                canvas.drawRoundRect(new RectF(this.ALLdata.get(i).get(j).getStartX() - this.lineWith, this.ALLdata.get(i).get(j).getStartY(), this.ALLdata.get(i).get(j).getStopX() + this.lineWith, this.ALLdata.get(i).get(j).getStopY()), dip2px(this.context, 4.0F), dip2px(this.context, 4.0F), this.linePaint);
            }
        }
    }

    /**
     * 画内容
     */
    private void drawContent(Canvas canvas) {
        // 没有数据退出
//        TLog.Companion.error("this.ALLdata+="+this.ALLdata);
        if (this.ALLdata == null || this.ALLdata.size() <= 0) {
            return;
        }
       // TLog.Companion.error("this.ALLdata+="+this.ALLdata.size());
        //画开始时间，开始图片，中间连接线 结束时间，结束图片
        drawBeginEnd(canvas);
        //画移动时候的背景线条
        drawBackLine(canvas);
        //画睡眠矩形块
        drawSleepRect(canvas);
    }

    /**
     * 画图
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //组件大小为0不处理
        if (getHeight() == 0 || getWidth() == 0) {
            return;
        }
        //这个考虑可以放到外面，不在画图中处理
        //根据原始数据=>计算startx,startY,stopX,stopY
        handleData();
        //画背景
        drawBackground(canvas);
        //画内容
        drawContent(canvas);
        //底部滑块
        //drawSeekBar(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //布局后可以获取到高度，数据处理在布局后完成，由于数据处理需要得到view高度
        //在这里处理就会丢失动画，但是在ondraw处理就会丢失性能，造成多余计算
//        handleData();
        //顶部位图右侧
       this.mBitmapRight = (getScreenWidth(this.context) - dip2px(this.context, 30.5F));
       this.mBitmapRight = (getWidth() - dip2px(this.context, 30.5F));
        //顶部位图左侧
        this.mBitmapLeft = dip2px(this.context, 30.5F);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
        }
        //非移动退出
        if (event.getAction() != MotionEvent.ACTION_MOVE) {
            return true;
        }
        int x = (int) event.getX();
        boolean isInclude;
//        if (this.seekBarBitmap != null) {
            this.mBitmapRight = x;
            invalidate();
            isInclude = false;
            if ((this.ALLdata != null) && (this.ALLdata.size() > 0)) {
                for (int i = 0; i < this.ALLdata.size(); i++) {
                    for (int j = 0; j < this.ALLdata.get(i).size(); j++) {
                        if ((this.ALLdata.get(i).get(j).getStartX() <= x) && (x <= this.ALLdata.get(i).get(j).getStopX())) {
                            OnTouch onTouch = SleepTodayView.onTouch;
                            if (onTouch != null) {
                                onTouch.handleData((this.ALLdata.get(i)).get(j));
                            }
                            isInclude = true;
                            break;
                        }
                    }
                }
            }
            if (!isInclude) {
                OnTouch onTouch = SleepTodayView.onTouch;
                if (onTouch != null) {
                    onTouch.handleData(null);
                }
            }
//        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    public void setDataSource(List<List<SleepViewData>> dataSource, OnTouch listener) {
        onTouch = listener;
        this.mBitmapRight = dip2px(this.context, 25.0F);
        if (dataSource != null) {
            this.ALLdata = dataSource;
            calcDuration();
            //这里计算是最好的。
            if (this.objectAnimator == null) {
                @SuppressLint("ObjectAnimatorBinding") ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "phaseX", 0.0F, 1.0F);
                this.objectAnimator = objectAnimator;
                objectAnimator.setDuration(600L);
                this.objectAnimator.addUpdateListener(paramAnonymousValueAnimator -> {
                    SleepTodayView.this.mAnimateValue = (Float) paramAnonymousValueAnimator.getAnimatedValue();
                    SleepTodayView.this.invalidate();
                });
            }
            ObjectAnimator objectAnimator = this.objectAnimator;
            if (objectAnimator != null) {
                objectAnimator.start();
            }
        }
    }

    public interface OnTouch {
        void handleData(SleepViewData data);
    }
}
