package com.example.xingliansdk.ui.bp;

import com.example.xingliansdk.utils.TimeUtil;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
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
        TLog.Companion.error("----传递过来的x轴集合="+new Gson().toJson(xValueList));
    }

    @Override
    public String getFormattedValue(float value) {
        //TLog.Companion.error("_----format="+value+" "+(value % xValueList.size())+" "+((int)value));

        int v = (int) value;

        return v<xValueList.size()? (TimeUtil.getSpecifyHour(xValueList.get(v)))+"" : "";
    }

    @Override
    public String getPointLabel(Entry entry) {
        return super.getPointLabel(entry);
    }
}
