
package com.example.xingliansdk.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.xingliansdk.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView1 extends MarkerView {

    private final TextView tvContent;

    public MyMarkerView1(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {


//        String x = Utils.formatNumber(e.getX(), 0, true);
        int hour = (int) (e.getX() / 60);
        int minute = (int) (e.getX() % 60);
        String x = hour + ":" + minute;
        String y = Utils.formatNumber(e.getY(), 0, true);

        tvContent.setText(String.format("x:%s y:%s", x, y));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
