package com.app.fmate.utils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 */
public class HeartAxisValueFormatter implements IAxisValueFormatter {

    private final BarLineChartBase<?> chart;

    public HeartAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }




//    @Override
//    public String getFormattedValue(float value) {
//        switch ((int) value) {
//            case 0:
//                return "02:00";
//            case 240:
//                return "06:00";
//            case 480:
//                return "10:00";
//            case 720:
//                return "14:00";
//            case 960:
//                return "18:00";
//            case 1200:
//                return "22:00";
//            case 1440:
//                return "24:00";
//            default:
//                break;
//
//        }
////        if(value<mMonths.length&&value>=0) {
////            String monthName = mMonths[((int) value )];
////            return monthName;
////        }
////        else
////            return " ";
//        return "";
//    }



    @Override
    public String getFormattedValue(float value, AxisBase axis) {


//        TLog.Companion.error("heart value+"+value);
        switch ((int) value) {
            case 0:
                return "00:00";
            case 72:
                return "06:00";
            case 144:
                return "12:00";
            case 216:
                return "18:00";
            case 288:
                return "24:00";
            default:
                break;

        }
//        if(value<mMonths.length&&value>=0) {
//            String monthName = mMonths[((int) value )];
//            return monthName;
//        }
//        else
//            return " ";
        return "";
    }
}
