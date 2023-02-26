package com.app.fmate.widget.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;


import com.app.fmate.view.DIYViewUtil;
import com.app.fmate.widget.bean.base.IBottomLabel;

import java.util.List;
import java.util.Locale;

/**
 * 功能: 天24小时,刻度
 */
public class Label24H implements IBottomLabel {

    @Override
    public void onDraw(View v, Canvas canvas, Rect windowsRect, List<?> data, Paint labelScaleLinePaint, Rect windowsBottomScaleLineRect) {
        labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.035f);
        int time = 24 * 60;//1440分
        float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
        for (int i = 0; i <= time; i++) {
            if (i == 0 || i % (4 * 60) == 0) {//每4小时 显示一个刻度
                int left = windowsBottomScaleLineRect.left;
                float startX = xOffset * (i) + left;
//                canvas.drawLine(startX, windowsBottomScaleLineRect.top, startX, windowsBottomScaleLineRect.bottom - windowsBottomScaleLineRect.height() * 0.7f, labelScaleLinePaint);
                String mBottomScaleLineText = null;
                int h = i / 60;
                if(h==24) {
                    mBottomScaleLineText = String.format(Locale.ENGLISH, "%02d:%02d", 23,59);
                }else{
                    mBottomScaleLineText = String.format(Locale.ENGLISH, "%02d:%02d", h, i % 60);
                }
                Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomScaleLineText), labelScaleLinePaint);
                canvas.drawText(mBottomScaleLineText/*值*/, startX - (mBottomScaleLineTextRect.width() / 2), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
            }
        }

    }
}
