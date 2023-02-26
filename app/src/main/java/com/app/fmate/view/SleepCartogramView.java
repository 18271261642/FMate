package com.app.fmate.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.app.fmate.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 睡眠条形图
 */
public class SleepCartogramView extends View {
    private int height;
    private int width;
    private Paint timeTextPaint;

    private float minuteWidth;
    private float contentEndY;

    //ATTR
    // private float attrSpacing;
    private int attrTextColor;
    private int attrBodyColor;
    private int attrXLineColor;
    private int attrChooseColor;
    private float attrTextSize;
    private float attrTextMarginTop;
    private float attrTextMarginBottom;
    private float attrPaddingTop;
    private float attrXLineHeight;

    private Paint stripePaint;
    private Paint xLinePaint;
    private Paint bodyBGPaint;

    private List<Long[]> values;

    private String beginTimeLabel;
    private String endTimeLabel;

    private float touchX = 0;

    private OnSelectedChanged onSelectedChanged;

    private ViewGroup parentViewGroup;

    public SleepCartogramView(Context context) {
        super(context);
    }

    public SleepCartogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SleepCartogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SleepCartogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SleepCartogramView);
        attrTextColor = typedArray.getColor(R.styleable.SleepCartogramView_sc_textColor, Color.GRAY);
        attrBodyColor = typedArray.getColor(R.styleable.SleepCartogramView_sc_bodyColor, Color.GRAY);
        attrXLineColor = typedArray.getColor(R.styleable.SleepCartogramView_sc_xLineColor, Color.GRAY);
        attrChooseColor = typedArray.getColor(R.styleable.SleepCartogramView_sc_chooseColor, Color.WHITE);
        attrTextSize = typedArray.getDimension(R.styleable.SleepCartogramView_sc_textSize, 10);
        attrTextMarginTop = typedArray.getDimension(R.styleable.SleepCartogramView_sc_textMarginTop, 10);
        attrTextMarginBottom = typedArray.getDimension(R.styleable.SleepCartogramView_sc_textMarginBottom, 10);
        attrPaddingTop = typedArray.getDimension(R.styleable.SleepCartogramView_sc_paddingTop, 10);
        attrXLineHeight = typedArray.getDimension(R.styleable.SleepCartogramView_sc_xLineHeight, 1);
        typedArray.recycle();

        timeTextPaint = new Paint();
        timeTextPaint.setTextSize(attrTextSize);
        timeTextPaint.setColor(attrTextColor);

        stripePaint = new Paint();
        stripePaint.setAntiAlias(true);
        stripePaint.setStyle(Paint.Style.FILL);

        xLinePaint = new Paint();
        xLinePaint.setColor(attrXLineColor);
        xLinePaint.setStrokeWidth(attrXLineHeight);

        bodyBGPaint = new Paint();
        bodyBGPaint.setColor(attrBodyColor);
        bodyBGPaint.setAntiAlias(true);
        bodyBGPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        contentEndY = height - attrTextMarginBottom - attrTextMarginTop - measureTextHeight(timeTextPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, width, contentEndY, bodyBGPaint);
        if (values != null && values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                Long[] valueArray = values.get(i);
                long beginTime = valueArray[0];
                long endTime = valueArray[1];
                int color = valueArray[2].intValue();
                float left = ((beginTime - values.get(0)[0]) / 60000) * minuteWidth;
                float top = attrPaddingTop;
                float right = ((endTime - values.get(0)[0]) / 60000) * minuteWidth;
                float bottom = contentEndY;
                if ((touchX == 0 && i == 0) || (touchX >= left && touchX <= right)) {
                    stripePaint.setColor(attrChooseColor);
                    if (onSelectedChanged != null) {
                        onSelectedChanged.onChanged(i);
                    }
                } else {
                    stripePaint.setColor(color);
                }
                canvas.drawRect(left, top, right, bottom, stripePaint);
            }
        }
        canvas.drawLine(0, contentEndY + attrXLineHeight, width, contentEndY + attrXLineHeight, xLinePaint);
        if (beginTimeLabel != null && beginTimeLabel.length() > 0) {
            canvas.drawText(beginTimeLabel, 0, height - attrTextMarginBottom, timeTextPaint);
        }

        if (endTimeLabel != null && endTimeLabel.length() > 0) {
            canvas.drawText(endTimeLabel, width - timeTextPaint.measureText(endTimeLabel), height - attrTextMarginBottom, timeTextPaint);
        }
    }

    private String dateFormat(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public void setValues(List<Long[]> values) {
        if (values != null && values.size() > 0) {
            beginTimeLabel = dateFormat(new Date(values.get(0)[0]), "HH:mm");
            endTimeLabel = dateFormat(new Date(values.get(values.size() - 1)[1]), "HH:mm");
            long unitMinute = (values.get(values.size() - 1)[1] - values.get(0)[0]) / 60000;
            minuteWidth = (float) width / (float) unitMinute;
        } else {
            beginTimeLabel = "00:00";
            endTimeLabel = "08:00";
            minuteWidth = 0;
        }
        this.values = values;
        touchX = 0;
        postInvalidate();
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 测量文字的高度
     **/
    public static float measureTextHeight(Paint paint) {
        float height = 0f;
        if (null == paint) {
            return height;
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        height = fontMetrics.descent - fontMetrics.ascent;
        return height;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                if (parentViewGroup != null) {
                    parentViewGroup.requestDisallowInterceptTouchEvent(true);
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - touchX) > 1) {
                    touchX = event.getX();
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                touchX = event.getX();
                postInvalidate();
                if (parentViewGroup != null) {
                    parentViewGroup.requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return true;
    }

    public interface OnSelectedChanged {
        void onChanged(int position);
    }

    public void setParentViewGroup(ViewGroup parentViewGroup) {
        this.parentViewGroup = parentViewGroup;
    }

    public OnSelectedChanged getOnSelectedChanged() {
        return onSelectedChanged;
    }

    public void setOnSelectedChanged(OnSelectedChanged onSelectedChanged) {
        this.onSelectedChanged = onSelectedChanged;
    }
}
