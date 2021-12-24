package com.example.xingliansdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BrokenLineChart extends View {
        private static final String TAG = "BrokenLineChart";
        /**View宽度*/
        private int mViewWidth;
        /** View高度*/
        private int mViewHeight;
        /**边框线画笔*/
        private Paint mBorderLinePaint;
        /**文本画笔*/
        private Paint mTextPaint;
        /**要绘制的折线线画笔*/
        private Paint mBrokenLinePaint;
        /**圆画笔*/
        private Paint mCirclePaint;
        /**圆的半径*/
        private float radius=5;
        /**边框的左边距*/
        private float mBrokenLineLeft=40;
        /**边框的上边距*/
        private float mBrokenLineTop=40;
        /**边框的下边距*/
        private float mBrokenLineBottom=40;
        /**边框的右边距*/
        private float mBrokenLinerRight=20;
        /**需要绘制的宽度*/
        private float mNeedDrawWidth;
        /**需要绘制的高度*/
        private float mNeedDrawHeight;
        /**边框文本*/
        private int[] valueText =new int[]{40,30,20,10,0};
        /**数据值*/
        private int[] value=new int[]{11,10,15,12,34,12,22,23,33,13};

        /**图表的最大值*/
        private int maxVlaue=4000;
        public BrokenLineChart(Context context) {
            super(context);
        }

        public BrokenLineChart(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mViewHeight = getMeasuredHeight();
            mViewWidth = getMeasuredWidth();
            initNeedDrawWidthAndHeight();
            initPaint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            /**绘制边框线和边框文本*/
            DrawBorderLineAndText(canvas);

            /**根据数据绘制线*/
            DrawBrokenLine(canvas);
    //        DrawLineCircle(canvas);



        }
        /**绘制线上的圆*/
        private void DrawLineCircle(Canvas canvas) {
            Point[] points= getPoints(value,mNeedDrawHeight,mNeedDrawWidth,maxVlaue,mBrokenLineLeft,mBrokenLineTop);
            for (int i = 2; i <points.length ; i++) {
                Point point=points[i];
                mCirclePaint.setColor(Color.WHITE);
                mCirclePaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(point.x,point.y,radius,mCirclePaint);
                mCirclePaint.setColor(Color.BLUE);
                mCirclePaint.setStyle(Paint.Style.STROKE);
                mCirclePaint.setStrokeWidth(3);
                /**
                 * drawCircle(float cx, float cy, float radius, Paint paint)
                 * cx 中间x坐标
                 * xy 中间y坐标
                 * radius 圆的半径
                 * paint 绘制圆的画笔
                 * */
                canvas.drawCircle(point.x,point.y,radius,mCirclePaint);

            }
        }

        /**根据值绘制折线*/
        private void DrawBrokenLine(Canvas canvas) {
            Path mPath=new Path();
            mBrokenLinePaint.setColor(Color.BLUE);
            mBrokenLinePaint.setStrokeWidth(2);
            Point[] points= getPoints(value,mNeedDrawHeight,mNeedDrawWidth,maxVlaue,mBrokenLineLeft,mBrokenLineTop);
            for (int i = 2; i < points.length; i++) {
                Point point=points[i];
                if(i==2){
                    mPath.moveTo(point.x,point.y);
                }
              else if(i==5)
                {
                    mPath.moveTo(point.x,point.y);
                }
                else {
                    mPath.lineTo(point.x,point.y);
                }

            }
            canvas.drawPath(mPath,mBrokenLinePaint);
        }

        /**绘制边框线和边框文本*/
        private void DrawBorderLineAndText(Canvas canvas) {
            /**对应的属性
             * drawLine(float startX, float startY, float stopX, float stopY, Paint paint);
             * startX   开始的x坐标
             * startY   开始的y坐标
             * stopX    结束的x坐标
             * stopY    结束的y坐标
             * paint    绘制该线的画笔
             * */
            mBorderLinePaint.setColor(Color.BLACK);
            /**绘制边框竖线*/
            canvas.drawLine(mBrokenLineLeft,mBrokenLineTop-10,mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mBorderLinePaint);
            /**绘制边框横线*/
            canvas.drawLine(mBrokenLineLeft,mViewHeight-mBrokenLineBottom,mViewWidth,mViewHeight-mBrokenLineBottom,mBorderLinePaint);


            /**绘制边框分段横线与分段文本*/
            float averageHeight=mNeedDrawHeight/(valueText.length-1);
            mBorderLinePaint.setTextAlign(Paint.Align.RIGHT);
            mBorderLinePaint.setColor(Color.GRAY);
            for (int i = 0; i < valueText.length; i++) {
                float nowadayHeight= averageHeight*i;
                canvas.drawLine(mBrokenLineLeft,nowadayHeight+mBrokenLineTop,mViewWidth-mBrokenLinerRight,nowadayHeight+mBrokenLineTop,mBorderLinePaint);
                canvas.drawText(valueText[i]+"",mBrokenLineLeft-5,nowadayHeight+mBrokenLineTop,mBorderLinePaint);
            }

        }

        /**初始化画笔*/
        private void initPaint() {

            /**初始化文本画笔*/
            if(mTextPaint==null){
                mTextPaint=new Paint();
            }
            initPaint(mTextPaint);

            /**初始化边框线画笔*/
            if(mBorderLinePaint==null){
                mBorderLinePaint=new Paint();
                mBorderLinePaint.setTextSize(20);
            }
            initPaint(mBorderLinePaint);

            /**初始化折线画笔*/
            if(mBrokenLinePaint==null){
                mBrokenLinePaint=new Paint();
            }
            initPaint(mBrokenLinePaint);

            if(mCirclePaint==null){
                mCirclePaint=new Paint();
            }
            initPaint(mCirclePaint);
        }

        /**初始化画笔默认属性*/
        private void initPaint(Paint paint){
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
        }


        /**初始化绘制折线图的宽高*/
        private void initNeedDrawWidthAndHeight(){
            mNeedDrawWidth = mViewWidth-mBrokenLineLeft-mBrokenLinerRight;
            mNeedDrawHeight = mViewHeight-mBrokenLineTop-mBrokenLineBottom;
        }
        /**根据值计算在该值的 x，y坐标*/
        public Point[] getPoints(int[] values, float height, float width, int max , float left,float top) {
            float leftPading = width / (values.length-1);//绘制边距
            Point[] points = new Point[values.length];
            for (int i = 0; i < values.length; i++) {
                float value = values[i];
                //计算每点高度所以对应的值
                double mean = (double) max/height;
                //获取要绘制的高度
                float drawHeight = (float) (value / mean);
                int pointY = (int) (height+top  - drawHeight);
                int pointX = (int) (leftPading * i + left);
                Point point = new Point(pointX, pointY);
                points[i] = point;
            }
            return points;
        }
}