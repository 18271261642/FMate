
package com.example.xingliansdk.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.xingliansdk.R;
import com.example.xingliansdk.utils.HelpUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.shon.connector.utils.TLog;

import java.math.BigDecimal;

@SuppressLint("ViewConstructor")
public class WeightMarkerView extends MarkerView {

    private final TextView tvContent;

    public WeightMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
           BigDecimal y= HelpUtil.INSTANCE.setNumber(ce.getHigh(),1);
            tvContent.setText(""+y);
        } else {
            BigDecimal y= HelpUtil.INSTANCE.setNumber(e.getY(),1);
            tvContent.setText(""+y);
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
