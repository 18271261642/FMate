package com.app.fmate.utils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 */
public class BloodOxygenValueFormatter implements IAxisValueFormatter {

    private final BarLineChartBase<?> chart;

    public BloodOxygenValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }



    @Override
    public String getFormattedValue(float value, AxisBase axis) {
//        TLog.Companion.error("heart value+"+value);
        switch ((int) value) {
            case 0:
                return "00:00";
            case 12:
                return "06:00";
            case 24:
                return "12:00";
            case 36:
                return "18:00";
            case 48:
                return "24:00";
            default:
                break;

        }

        return "";
    }
}
