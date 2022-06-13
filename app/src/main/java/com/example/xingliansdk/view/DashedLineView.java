package com.example.xingliansdk.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

//虚线显示
public class DashedLineView extends View {

    private float mWidth;
    public DashedLineView(Context context) {
        super(context);
    }

    public DashedLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DashedLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(20f);

        paint.setColor(Color.parseColor("#71FBEE"));//颜色可以自己设置

        Path path = new Path();

        path.moveTo(0, 0);//起始坐标

        path.lineTo(mWidth, 0);//终点坐标

        PathEffect effects = new DashPathEffect(new float[]{10f,10f}, 1);//设置虚线的间隔和点的长度

        paint.setPathEffect(effects);

        canvas.drawPath(path, paint);

    }
}
