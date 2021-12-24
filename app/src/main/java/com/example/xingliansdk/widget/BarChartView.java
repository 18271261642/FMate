package com.example.xingliansdk.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import com.example.xingliansdk.R;
import com.example.xingliansdk.view.DIYViewUtil;
import com.example.xingliansdk.widget.bean.base.IBottomLabel;

import java.util.Arrays;
import java.util.List;

/**
 * 功能: 图表
 */
public class BarChartView extends View {

    private Paint limitLinePaint;
    private Paint labelPaint;
    private float[] mLimitLines;
    private float min;
    private float max;
    private Paint labelScaleLinePaint;
    private List<Integer> data;
    private Paint barPaint;
    private float bar_width;
    private IBottomLabel label;
    private RectF barRect = new RectF();//柱状图
    private Rect windowsBottomScaleLineRect = new Rect();//底部刻度线RECT范围
    private Rect windowsRect = new Rect();
    private RectF standards_rect = new RectF(); // 达标图片的 RECT位置
    private RectF barPreviewRect = new RectF();   //实际 柱状可视范围
    private boolean isDrawBorder = true;//画边框 包括0刻度线
    private boolean isDrawLimitLine = true;//画limit刻度线
    private boolean isDrawLabelLimit = true;//画label刻度线
    private boolean isDrawZeroLimitLine = true;//画0刻度线
    private boolean isDrawLabel = true;//画label (该参数影响着)isDrawLabelLimit
    private boolean hasDefLabel;
    private String[] mLimitLinesLabel;
    private String maxTextString = "100";
    private Bitmap mBitmapStandards;
    private int standards_size = 40;//standards_size达标图片大小
    private float screenDensity;
    private LinearGradient linearGradient;


    /**
     * 小尺寸
     */
    public static float BAR_WIDTH_SMALL = 1.5f;
    /**
     * 正常尺寸
     */
    public static float BAR_WIDTH_NORMAL = 2;
    /**
     * 中等尺寸
     */
    public static float BAR_WIDTH_MEDIUM = 5;
    /**
     * 大尺寸
     */
    public static float BAR_WIDTH_LARGER = 8;
    private List<Boolean> standardsPosData;


    /**
     * 0 刻度分割线
     *
     * @param drawZeroLimitLine
     */
    public void setDrawZeroLimitLine(boolean drawZeroLimitLine) {
        isDrawZeroLimitLine = drawZeroLimitLine;
        invalidate();
    }

    /**
     * 画左边和底部边框包括0 刻度分割线
     *
     * @param isDrawBorder
     */
    public void setDrawBorder(boolean isDrawBorder) {
        this.isDrawBorder = isDrawBorder;
        invalidate();
    }

    /**
     * 画分割线
     *
     * @param isDrawLimitLine
     */
    public void setDrawLimitLine(boolean isDrawLimitLine) {
        this.isDrawLimitLine = isDrawLimitLine;
        invalidate();
    }


    /**
     * 画左边 刻度文字
     *
     * @param isDrawLabel
     */
    public void setDrawLabel(boolean isDrawLabel) {
        this.isDrawLabel = isDrawLabel;
        invalidate();
    }


    /**
     * 画左边刻度文字的 分割线
     *
     * @param isDrawLabelLimit
     */
    public void setDrawLabelLimit(boolean isDrawLabelLimit) {
        this.isDrawLabelLimit = isDrawLabelLimit;
        invalidate();
    }

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenDensity = DIYViewUtil.getScreenDensity(getResources());

        BAR_WIDTH_SMALL = screenDensity / 1.34f;
        BAR_WIDTH_NORMAL = screenDensity;
        BAR_WIDTH_MEDIUM = screenDensity * 2.5f;
        BAR_WIDTH_LARGER = screenDensity * 4;

        barPaint = DIYViewUtil.createFillPaint(0xFF000000);

        limitLinePaint = DIYViewUtil.createFillPaint(0xFFD9D9D9);
        limitLinePaint.setStrokeWidth(screenDensity);

        labelPaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint.setStrokeWidth(3);

        mBitmapStandards = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_crown);
//        standards_size =
    }


    public void setScaleLineLabelColor(int color){
        labelScaleLinePaint.setColor(color);
        invalidate();
    }
    public void setLabelColor(int color){
        labelPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置底部 刻度类型
     *
     * @param label
     */
    public void setDataType(IBottomLabel label) {
        this.label = label;
        invalidate();
    }

    /**
     * 设置 条形图的条的宽度
     *
     * @param bar_width
     */
    public void setBarWidth(float bar_width) {
        this.bar_width = bar_width;
        invalidate();
    }

    /**
     * 达标位置
     *
     * @param standardsPosData
     */
    public void setStandardsPosData(List<Boolean> standardsPosData) {
        this.standardsPosData = standardsPosData;
        invalidate();
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<Integer> data) {
        this.data = data;
        //如果设置了自定义的Label 所以不需要计算最大最小值 反之,如果没设置自定义的Label 则需要计算
        if (!hasDefLabel) {
            min = Float.MAX_VALUE;
            max = -Float.MAX_VALUE;
            for (Integer line : data) {
                min = Math.min(min, line);
                max = Math.max(max, line);
            }
            if (min == Float.MAX_VALUE || max == -Float.MAX_VALUE) {
                min = 0;
                max = 0;
            }
            setLimitLine(min, max);
        }
        invalidate();
    }

    /**
     * 柱状图颜色, 一个则
     *
     * @param colors
     */
    public void setBarColor(final int... colors) {
        post(new Runnable() {
            @Override
            public void run() {
                //柱状图颜色
                if (colors.length == 1) {
                    barPaint.setColor(colors[0]);
                } else if (windowsRect != null) {
                    linearGradient = new LinearGradient(0, 0, 0, windowsRect.height(), colors, null, LinearGradient.TileMode.CLAMP);
                    barPaint.setShader(linearGradient);
                }
                invalidate();
            }
        });


    }

    /**
     * 设置分割线值
     *
     * @param lines
     */
    public void setLimitLine(float... lines) {
        this.mLimitLines = lines;
        min = Float.MAX_VALUE;
        max = -Float.MAX_VALUE;
        for (float line : lines) {
            min = Math.min(min, line);
            max = Math.max(max, line);
        }
        if (min == Float.MAX_VALUE || max == -Float.MAX_VALUE) {
            min = 0;
            max = 0;
        }
        maxTextString = String.valueOf(max);
        if (hasDefLabel) {
            int i = Arrays.binarySearch(mLimitLines, max);
            if (i >= 0) {
                maxTextString = String.valueOf(mLimitLinesLabel[i]);
            }
        }
        invalidate();
    }

    /**
     * 设置分割线的值 对应的显示的文字label
     *
     * @param lines
     * @param label
     */
    public void setLimitLine(float[] lines, String[] label) {
        this.mLimitLinesLabel = label;
        if (mLimitLinesLabel != null) {
            if(label.length!=lines.length){
                throw new IndexOutOfBoundsException("float[] lines和String[] label这两个参数数量必须一致!");
            }
            hasDefLabel = true;
        }
        setLimitLine(lines);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null) {
            return;
        }

        //==============================================================================================================================================
        //====================================================================窗体大小计算================================================================
        //==============================================================================================================================================

        DIYViewUtil.createRect(windowsRect, this);

        Rect textRect = DIYViewUtil.getTextRect(maxTextString, labelPaint);
        int padding = 30;
        int labelTextWidth = isDrawLabel ? textRect.width() : padding / 2;

        int mScaleLineRectHeight = (textRect.height() * 2);//底部刻度线高度 设置为文字高度两倍

        DIYViewUtil.paddingRect(windowsRect, labelTextWidth + padding + (isDrawLabel ? padding : 0), padding, padding + padding, mScaleLineRectHeight + padding/*底部*/);//窗体padding  为了左右上下有padding 好看点

        //==============================================================================================================================================
        //====================================================================画分割线和右侧分割线的文本================================================================
        //==============================================================================================================================================


        int limitLineNum = Math.round(max - min) + 1;
        float limitLineYOffset = windowsRect.height() * 1.0f / limitLineNum;
        for (int i = 0; i <= limitLineNum; i++) {
            float val = max - i;
            //采用过滤的方式 图表中有 max - min+1 个点 按窗口高度分等分 再过滤使用者传入的分割线值
            // 这样做的原因是 可以实现不按比例的分割线的显示 比如 10-20-30是传统方式,而现在可以支持10-15-16-17-30 这种乱序方式 于是更好的兼容使用者的需求变动
            int searchArrayIndex = mLimitLines == null ? -1 : Arrays.binarySearch(mLimitLines, val);//高性能搜索数组
            if (searchArrayIndex < 0) {//不是包含指定分割线的 忽略不画
                continue;//忽略
            }
            int half = padding / 2;

            float limitLineY = windowsRect.top + (limitLineYOffset * (i + 1)) + (limitLinePaint.getStrokeWidth() / 2);//分割线Y偏移  这个是一直按等分累加的
            //画limit分割线
            if (isDrawLimitLine) {
                //画limit分割线
                canvas.drawLine(windowsRect.left - padding/*恢复全部*/, limitLineY, windowsRect.right + padding/*恢复全部*/, limitLineY, limitLinePaint);
            } else if (isDrawBorder || isDrawZeroLimitLine) {//如要求只画(边框)0刻度 就画一条
                if (i == limitLineNum - 1) {
                    //画limit分割线
                    canvas.drawLine(windowsRect.left - padding/*恢复全部*/, limitLineY, windowsRect.right + padding/*恢复全部*/, limitLineY, limitLinePaint);
                }
            }

            if (isDrawBorder) {
                //画label垂直分割线(边框)
                canvas.drawLine(windowsRect.left - padding/*恢复全部*/, windowsRect.top, windowsRect.left - padding/*恢复全部*/, windowsRect.bottom, limitLinePaint);
            }
            if (isDrawLabel) {
                if (isDrawLabelLimit) {
                    //画limit分割刻度线
                    canvas.drawLine((windowsRect.left - padding/*恢复全部*/) - (half / 2/*一半的一半.为了居中..不知道咋解释,注释掉这个括号里的你就知道了*/), limitLineY, windowsRect.left - padding/*恢复全部*/, limitLineY, limitLinePaint);
                }

                String value;
                if (hasDefLabel) {//如果定义了自定义的Label 则使用Label
                    value = String.valueOf(mLimitLinesLabel[searchArrayIndex]);
                } else {
                    value = String.valueOf(Math.round(val));
                }

                Rect valRect = DIYViewUtil.getTextRect(value, labelPaint);
                //画Y轴左边文本
                canvas.drawText(value/*值*/, (windowsRect.left - padding) - valRect.width() - (/*线误差*/limitLinePaint.getStrokeWidth() / 2) - half, limitLineY + (textRect.height() / 2)/*文字高/2是为了居中于分割线 为了好看*/, labelPaint);
            }
        }
        //==============================================================================================================================================
        //====================================================================画主体曲线折线柱状图等================================================================
        //==============================================================================================================================================

        int size = data.size();
//        float stroke_width = barPaint.getStrokeWidth();
        float maxOffset = (windowsRect.width() * 1.0f) / ((size - 1 == 0) ? size : size - 1)/*格*/;

        if (bar_width <= 0) {
            //至少是2
            bar_width = maxOffset < 0 ? 2 : Math.round(maxOffset) + 1;
        }
//            float lx = -1, ly = -1;
        for (int i = 0; i < size; i++) {
            Integer value = data.get(i);
            if (value == null || value == 0) {//需要过滤掉0  华为会因为这个导致图表变乱 和底部白屏
                continue;
            }
            float y = ((value - min) * 1.0f) / (Math.round(max - min) + 1);

            float cy = (windowsRect.height() * (1 - y)) + windowsRect.top/*顶部是有偏移的*/;

            float cx = (maxOffset * i) + windowsRect.left;
            //下面的绘制原理
            //barRect为图中的整体柱子范围,包括超出部分.
            //standards_rect为 '++'(达标图片) 的范围
            //barPreviewRect是++和--的整体可视范围, 则不超过分割线的可视范围
            //            ++
            //            --
            //      ++    --
            //      --    -- ++
            //      -- ++ -- --
            //      -- -- -- --
            //      -- -- -- --
            //      -- -- -- --
            //---------------------- 底部0刻度分割线
            //      -- -- -- --     <-图表可能会超出的部分(比如传入负数值) ,所以下面用到clipRect 切掉多余的. 为什么要切? 因为需求是圆角的'线'  但rect无法画线 换成线的方式去画图表 那整个图表都得整改!蛋疼么,同时安卓api只有圆角矩形 圆角矩形是上和下都是圆角 所以为了只保留上圆角 底部不圆角,需要制造一个超过分界线的偏移  然后切掉底部 这样就是想要的效果
            //      -- -- -- --
            //这里多次使用setRectValue 是为了节省对象内存消耗 而不用每次都 new Rect()
            DIYViewUtil.setRectValue(barRect, cx - bar_width, cy, cx + bar_width, windowsRect.bottom + standards_size);
            //达标图片的 RECT位置
            DIYViewUtil.setRectValue(standards_rect, barRect.left - standards_size / 2 + barRect.width() / 2, barRect.top - standards_size, barRect.right + standards_size / 2 - barRect.width() / 2, barRect.top);
            //实际 柱状可视范围
            DIYViewUtil.setRectValue(barPreviewRect, barRect.left, barRect.top, barRect.right, barRect.bottom - standards_size);
            //达标图片的内容若超过底部的可视范围(超过刻度线) 则不画 不然很难看
            if (!(standards_rect.bottom > barPreviewRect.bottom)) {
                //达标位置数据 需要和实际数据数量相同 不然不画
                if (standardsPosData != null && standardsPosData.size() == size) {
                    //如果是true 则画
                    if (standardsPosData.get(i)) {
                        DIYViewUtil.drawBitmapFill(canvas, mBitmapStandards, standards_rect, barPaint);
                    }
                }
            }
            canvas.save();
            //画 达标图片
            //切掉 超出负数部分 只保留可视范围
            canvas.clipRect(barPreviewRect);
            //画柱状图
            canvas.drawRoundRect(barRect, barRect.width() / 2, barRect.width() / 2, barPaint);
            canvas.restore();
            //画折线图 曲线 ,写到这里 王腾说换成柱状图... 于是注释掉 以后如果要用回来折线图 恢复这个注释即可
//                canvas.drawCircle(cx, cy, bar_width, barPaint);
//                canvas.save();
//                if (lx != -1 && ly != -1) {   //画线
////                    canvas.drawLine(lx, ly, cx, cy, barPaint);
//                    float wt = (lx + cx) / 2;
//                    PointF p3 = new PointF();
//                    PointF p4 = new PointF();
//                    p3.y = ly;
//                    p3.x = wt;
//                    p4.y = cy;
//                    p4.x = wt;
//
//                    Path path = new Path();
//                    path.moveTo(lx, ly);
//                    path.cubicTo(p3.x, p3.y, p4.x, p4.y, cx, cy);
//                    canvas.drawPath(path, barPaint);
//                }
//                lx = cx;
//                ly = cy;

//                canvas.restore();

        }

        //==============================================================================================================================================
        //====================================================================画底部刻度等================================================================
        //==============================================================================================================================================
        //确定底部 刻度位置的大小 供子类 随意自定义
        DIYViewUtil.setRectValue(windowsBottomScaleLineRect, windowsRect.left, windowsRect.bottom, windowsRect.right, windowsRect.bottom + mScaleLineRectHeight);
        if (label != null) {
            //供子类 随意自定义
            label.onDraw(this, canvas, windowsRect, data, labelScaleLinePaint, windowsBottomScaleLineRect);
        }

//        switch (sleepType) {
//            case TYPE_DAY: //一天
//            {
//                labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.035f);
//                int time = 24 * 60;//1440分
//                float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
//                for (int d = 0; d <= time; d++) {
//                    if (d == 0 || d % (4 * 60) == 0) {//每4小时 显示一个刻度
//                        int left = windowsBottomScaleLineRect.left;
//                        float startX = xOffset * (d) + left;
//                        canvas.drawLine(startX, windowsBottomScaleLineRect.top, startX, windowsBottomScaleLineRect.bottom - windowsBottomScaleLineRect.height() * 0.7f, labelScaleLinePaint);
//                        String mBottomScaleLineText = String.valueOf(d / 60);
//                        Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomScaleLineText), labelScaleLinePaint);
//                        canvas.drawText(mBottomScaleLineText/*值*/, startX - (mBottomScaleLineTextRect.width() / 2), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
//                    }
//                }
//            }
//            break;
//            case TYPE_WEEK: {
//                labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.030f);
//                String[] weeks = getResources().getStringArray(R.array.week_day);
//                int time = size==0?7:size;//7天
//                float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
//                for (int d = 0; d < time; d++) {
//
//                    int left = windowsBottomScaleLineRect.left;
//                    float startX = xOffset * (d) + left;
//                    canvas.drawLine(startX, windowsBottomScaleLineRect.top, startX, windowsBottomScaleLineRect.bottom - windowsBottomScaleLineRect.height() * 0.7f, labelScaleLinePaint);
//                    String mBottomScaleLineText = weeks[d];
//                    Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomScaleLineText), labelScaleLinePaint);
//                    canvas.drawText(mBottomScaleLineText/*值*/, startX - (mBottomScaleLineTextRect.width() / 2), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
//
//                }
//            }
//            break;
//            case TYPE_MONTH: {
//                labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.025f);
//                int time = size==0?30:size;
//                //不能按比例,比如i%5==0则画,错误的,因为需求是要求乱序的
//                List<Integer> labels = Arrays.asList(1, 5, 10, 15, 20, 25, 30);
//                float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
//                for (int d = 0; d < time; d++) {
//                    if (labels.contains(d+1)) {
//                        int left = windowsBottomScaleLineRect.left;
//                        float startX = xOffset * (d) + left;
//                        canvas.drawLine(startX, windowsBottomScaleLineRect.top, startX, windowsBottomScaleLineRect.bottom - windowsBottomScaleLineRect.height() * 0.7f, labelScaleLinePaint);
//                        String mBottomScaleLineText = String.valueOf(d + 1);
//                        Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomScaleLineText), labelScaleLinePaint);
//                        canvas.drawText(mBottomScaleLineText/*值*/, startX - (mBottomScaleLineTextRect.width() / 2), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
//                    }
//                }
//            }
//
//            break;
//            case TYPE_YEAR: {
//                labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.035f);
//                int time = size == 0 ? 12 : size;//12个月
//                float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
//                for (int d = 0; d < time; d++) {
//
//                    int left = windowsBottomScaleLineRect.left;
//                    float startX = xOffset * (d) + left;
//                    canvas.drawLine(startX, windowsBottomScaleLineRect.top, startX, windowsBottomScaleLineRect.bottom - windowsBottomScaleLineRect.height() * 0.7f, labelScaleLinePaint);
//                    String mBottomScaleLineText = String.valueOf(d + 1);
//                    Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomScaleLineText), labelScaleLinePaint);
//                    canvas.drawText(mBottomScaleLineText/*值*/, startX - (mBottomScaleLineTextRect.width() / 2), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
//                }
//            }
//
//            break;
//        }
    }


    /**
     * 查找是否包含值, 已遗弃 通过 Arrays.binarySearch()替代 二进制特征查找 查找数组更快
     *
     * @param val
     * @return
     */
    @Deprecated
    private boolean isContiansLimit(float val) {

        for (float v : mLimitLines) {
            if (Math.round(v) == Math.round(val)) {
                return true;
            }
        }
        return false;
    }
}
