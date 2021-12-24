package com.example.xingliansdk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.xingliansdk.R;

public class DialProgressBar extends View {
        private Paint mPaint = null ;
        private int max=100;
        private int progress=0;
        private int type=1;
        private int NORMAL_TYPE=1;
        private int ALERT_TYPE=2;
        public DialProgressBar(Context context, AttributeSet attrs) {
            super(context, attrs);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DialProgressBar); //与属性名称一致
            max = array.getInteger(R.styleable.DialProgressBar_dial_max, 100);//第一个是传递参数，第二个是默认值
            progress = array.getInteger(R.styleable.DialProgressBar_dial_progress, 0);
            type=array.getInt(R.styleable.DialProgressBar_dial_type, 1);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Point left_top=new Point(0,0);
            Point right_bottom=new Point(getWidth(),getHeight());
            double rate=(double)progress/(double)max;
            drawProgressBar(canvas, left_top, right_bottom, rate);
        }
        public void setProgress(int progress){
            this.progress=progress;
            invalidate();//使得onDraw重绘
        }
        private void drawProgressBar(Canvas canvas,Point left_top,Point right_bottom,double rate){
            int width=2;
            int rad=16;
            mPaint.setColor(getResources().getColor(R.color.color_main_green));//画边框
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);//设置空心
            mPaint.setStrokeWidth(width);
            RectF rectF = new RectF(left_top.x,left_top.y,right_bottom.x,right_bottom.y);
            canvas.drawRoundRect(rectF, rad, rad, mPaint);

            mPaint.setColor(Color.GREEN);//画progress bar

            if (type==ALERT_TYPE) {
                if (rate>0.9)
                    mPaint.setColor(Color.RED);
            }
            mPaint.setStyle(Paint.Style.FILL);
            int x_end=(int)(right_bottom.x*rate);
            RectF rectF2 = new RectF(left_top.x+width,left_top.y+width,x_end-width,right_bottom.y-width);
            canvas.drawRoundRect(rectF2, rad, rad, mPaint);
        }
    }

