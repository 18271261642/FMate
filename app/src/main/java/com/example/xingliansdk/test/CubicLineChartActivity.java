
package com.example.xingliansdk.test;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import com.example.xingliansdk.R;
import com.example.xingliansdk.base.BaseActivity;
import com.example.xingliansdk.viewmodel.MainViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
public class CubicLineChartActivity extends BaseActivity<MainViewModel> {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart1);
        setTitle("CubicLineChartActivity");
        chart = findViewById(R.id.chart1);
        // no description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        XAxis x = chart.getXAxis();
        x.setAxisMinimum(0);
        x.setAxisMaximum(1440);
//        x.setGranularity(1f);
//        x.setAxisMaxLabels(5);
      //  x.setAxisMinLabels(1);
        x.setAxisMinValue(1);
        x.setLabelCount(5, true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setTextSize(12);
        x.setTextColor(Color.BLACK);


        MyMarkerView1 mv = new MyMarkerView1(this, R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);


        final String[] times = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"};
        //设置X轴值为字符串
//        xAxis.valueFormatter = XAxisLabelValueFormatter(times)
//        x.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                int hour = (int) (value / 60);
//                if (hour < 0) {
//                    return "error1";
//                }
//                if (hour >= times.length) {
//                    return "error2";
//                }
//                return times[hour];
//            }
//        });

        YAxis y = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.BLACK);
        y.setAxisMinimum(0);
        y.setAxisMaximum(120);

//        chart.getAxisRight().setEnabled(false);

        chart.getLegend().setEnabled(false);

//        chart.animateXY(500, 500);

        setData(1440, 100);
        // don't forget to refresh the drawing
        chart.invalidate();
    }

    private void setData(int count, float range) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float val;
            if (i > 660 && i < 780) {
                val = (float) (Math.random() * (range + 1)) - (float) (Math.random() * (range + 1)) - 5;
            } else {
                val = (float) (Math.random() * (range + 1));
            }

            if (val <= 0) {
                values.add(new Entry(i, 0));
            } else {
                values.add(new Entry(i, val));
            }
        }

        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        LineData data = new LineData(set1);
        data.setValueTextSize(9f);
        data.setDrawValues(false);
        chart.setData(data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }



    @Override
    public int layoutId() {
        return R.layout.activity_linechart1;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

    }
}
