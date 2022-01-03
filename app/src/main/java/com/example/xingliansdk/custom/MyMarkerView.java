
package com.example.xingliansdk.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.xingliansdk.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.shon.connector.utils.TLog;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView tvContent;
    private final NumberFormat numberFormat = new DecimalFormat("#,###");

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            int step=(int)ce.getHigh();
            tvContent.setText(""+numberFormat.format(step));
        } else {
            int step=(int)e.getY();
            tvContent.setText(""+numberFormat.format(step));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
