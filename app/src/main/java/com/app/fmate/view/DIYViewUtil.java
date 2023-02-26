package com.app.fmate.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class DIYViewUtil {

    /**
     * 像素密度
     *
     * @param resources
     * @return
     */
    public static float getScreenDensity(Resources resources) {
        return resources.getDisplayMetrics().density;
    }

    /**
     * 屏幕宽
     *
     * @param resources
     * @return
     */
    public static int getScreenWidth(Resources resources) {
        return resources.getDisplayMetrics().widthPixels;
    }

    /**
     * 屏幕高
     *
     * @param resources
     * @return
     */
    public static int getScreenHeight(Resources resources) {
        return resources.getDisplayMetrics().heightPixels;
    }

    public static Paint createDefPaint(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        return paint;
    }

    public static Paint createFillPaint(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    public static Paint createStrokePaint(int color, float strokeWidth) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        return paint;
    }

    public static Paint createTextPaint(int color, float textSize) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    public static void drawBitmapFill(Canvas canvas, Bitmap bitmap, View rootView, Paint paint) {
        canvas.drawBitmap(bitmap, createRect(bitmap), createRect(rootView), paint);
    }

    public static void drawBitmapFill(Canvas canvas, Bitmap bitmap, Rect rect, Paint paint) {
        canvas.drawBitmap(bitmap, createRect(bitmap), rect, paint);
    }

    public static void drawBitmapFill(Canvas canvas, Bitmap bitmap, RectF rect, Paint paint) {
        canvas.drawBitmap(bitmap, createRect(bitmap), rect, paint);
    }

    public static Rect createRect(Bitmap bitmap) {
        return new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public static Rect createRect(View view) {
        return new Rect(0, 0, view.getWidth(), view.getHeight());
    }

    public static void createRect(Rect rect, View view) {
        setRectValue(rect, 0, 0, view.getWidth(), view.getHeight());
    }

    public static void createRectAdaptWidth(Rect rect, View view) {
        setRectValue(rect, 0, 0, view.getWidth(), view.getWidth());
    }

    public static void createRectAdaptHeight(Rect rect, View view) {
        setRectValue(rect, 0, 0, view.getHeight(), view.getHeight());
    }

    public static void createRect(RectF rect, View view) {
        setRectValue(rect, 0, 0, view.getWidth(), view.getHeight());
    }

    public static void createRectAdaptWidth(RectF rect, View view) {
        setRectValue(rect, 0, 0, view.getWidth(), view.getWidth());
    }

    public static void createRectAdaptHeight(RectF rect, View view) {
        setRectValue(rect, 0, 0, view.getHeight(), view.getHeight());
    }

    public static void paddingRect(Rect rect, int padding) {
        rect.left += padding;
        rect.right -= padding;
        rect.top += padding;
        rect.bottom -= padding;
    }

    public static void paddingRect(RectF rect, float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
        rect.left += paddingLeft;
        rect.right -= paddingRight;
        rect.top += paddingTop;
        rect.bottom -= paddingBottom;
    }
    public static void paddingRect(Rect rect, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        rect.left += paddingLeft;
        rect.right -= paddingRight;
        rect.top += paddingTop;
        rect.bottom -= paddingBottom;
    }

    public static void paddingRect(RectF rect, int padding) {
        rect.left += padding;
        rect.right -= padding;
        rect.top += padding;
        rect.bottom -= padding;
    }

    public static void paddingRect(RectF rect, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        rect.left += paddingLeft;
        rect.right -= paddingRight;
        rect.top += paddingTop;
        rect.bottom -= paddingBottom;
    }

    public static Rect copyRect(Rect rect) {
        return new Rect(rect.left, rect.top, rect.right, rect.bottom);
    }

    public static RectF copyRect(RectF rect) {
        return new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }

    /**
     * 用赋值的方式去复制值,不用每次都new Rect() , 更省内存
     *
     * @param rect
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public static void setRectValue(RectF rect, float left, float top, float right, float bottom) {
        rect.left = left;
        rect.top = top;
        rect.right = right;
        rect.bottom = bottom;
    }

    public static void setRectValue(RectF dst, RectF src) {
        dst.left = src.left;
        dst.top = src.top;
        dst.right = src.right;
        dst.bottom = src.bottom;
    }

    public static void setRectValue(Rect rect, int left, int top, int right, int bottom) {
        rect.left = left;
        rect.top = top;
        rect.right = right;
        rect.bottom = bottom;
    }
    public static void setRectValue(Rect dst, Rect src) {
        dst.left =   src.left;
        dst.top =    src.top;
        dst.right =  src.right;
        dst.bottom = src.bottom;
    }

    public static RectF Rect2RectF(Rect rect) {
        return new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }

    public static Rect RectF2Rect(RectF rect) {
        return new Rect(Math.round(rect.left), Math.round(rect.top), Math.round(rect.right), Math.round(rect.bottom));
    }

    public static Rect getTextRect(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    public static int getTextHeight(String text, Paint paint) {
        return getTextRect(text, paint).height();
    }

    public static int getTextWidth(String text, Paint paint) {
        return getTextRect(text, paint).width();
    }

    /**
     * @param paint 画笔
     * @param str   字符串
     * @return 返回指定字符串的长度
     */
    public static float getFontLength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @param paint 画笔
     * @return 返回指定的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * @param paint 画笔
     * @return 返回指定离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }
}
