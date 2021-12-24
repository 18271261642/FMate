package com.example.xingliansdk.widget.bean.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.List;

/**
 *
 * 功能:底部绘制重定义
 */

public interface IBottomLabel {
    void onDraw(View v, Canvas canvas, Rect windowsRect, List<?> data, Paint labelScaleLinePaint, Rect windowsBottomScaleLineRect);
}
