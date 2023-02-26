package com.app.fmate.utils;

import com.app.fmate.view.DateUtil;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 *
 */
public class DeviceSportAxisValueFormatter implements IAxisValueFormatter {

    private final BarLineChartBase<?> chart;
    int position = 0;
    Long time = System.currentTimeMillis();

    public DeviceSportAxisValueFormatter(BarLineChartBase<?> chart, int position) {
        this.chart = chart;
        this.position = position;
    }

    public DeviceSportAxisValueFormatter(BarLineChartBase<?> chart, int position,long time ) {
        this.chart = chart;
        this.position = position;
        this.time=time;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        switch (position) {
            case 0: {
//                TLog.Companion.error("value=="+value);
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
            }
            case 1: {
                long index = (long) value;
                long date=time+86400*1000*index;
//                TLog.Companion.error("改变下标"+DateUtil.getDate(DateUtil.MM_AND_DD, date));
                return   DateUtil.getDate(DateUtil.MM_DD, date);
            }
            case 2: {
                long index = (long) value;
                long date=time+86400*1000*index;
                return   DateUtil.getDate(DateUtil.MM_AND_DD, date);
            }
            case 3: {
                switch ((int) value) {
                    case 0:
                        return "1月";
                    case 1:
                        return "2月";
                    case 2:
                        return "3月";
                    case 3:
                        return "4月";
                    case 4:
                        return "5月";
                    case 5:
                        return "6月";
                    case 6:
                        return "7月";
                    case 7:
                        return "8月";
                    case 8:
                        return "9月";
                    case 9:
                        return "10月";
                    case 10:
                        return "11月";
                    case 11:
                        return "12月";

                    default:
                        break;

                }
            }
        }
        return "";
    }
}
