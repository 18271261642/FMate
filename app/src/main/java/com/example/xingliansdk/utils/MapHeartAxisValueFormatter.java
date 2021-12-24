package com.example.xingliansdk.utils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.shon.connector.utils.TLog;

/**
 */
public class MapHeartAxisValueFormatter implements IAxisValueFormatter {

    private final BarLineChartBase<?> chart;

    public MapHeartAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
       // TLog.Companion.error("=="+chart.getXChartMax());
    }



    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        TLog.Companion.error("heart value+"+value+"  AxisBase=="+axis.mAxisMaximum);
        int avgTime=0,maxTime=0;
        if(axis.mAxisMaximum>0) {
            int max=  (int) axis.mAxisMaximum;
            avgTime=max/2;
            maxTime=max;
        }
      //  TLog.Companion.error();
        if((int) value==0)  //第0为显示0分钟
        {
            return "0";
        }
        else if(avgTime!=0&&avgTime==value)//中间为显示取中间值
        {
            TLog.Companion.error("中间值=="+((int)value/6));
            return ""+((int)value/6);
        }
        else if(maxTime!=0&&maxTime>avgTime&&maxTime==value)
        {
            TLog.Companion.error("最大值=="+((int)value/6));
            return ""+((int)value/6);
        }
        return "";
    }
}
