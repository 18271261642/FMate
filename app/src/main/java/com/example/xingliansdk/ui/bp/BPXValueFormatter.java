package com.example.xingliansdk.ui.bp;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.shon.connector.utils.TLog;

import java.util.List;

/**
 * 格式X轴的数据
 */
public class BPXValueFormatter extends ValueFormatter {


    //x轴的集合数据
    private List<String> xValueList;

    public BPXValueFormatter(List<String> xValueList) {
        this.xValueList = xValueList;
    }

    @Override
    public String getFormattedValue(float value) {
        TLog.Companion.error("_----format="+value+" "+(value % xValueList.size()));


        return super.getFormattedValue(value);
    }

    @Override
    public String getPointLabel(Entry entry) {
        return super.getPointLabel(entry);
    }
}
