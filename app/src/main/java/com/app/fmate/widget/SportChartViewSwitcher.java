package com.app.fmate.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.app.fmate.R;
import com.app.fmate.utils.ResUtil;
import com.app.fmate.widget.bean.Label24H;

import java.util.ArrayList;
import java.util.List;


public class SportChartViewSwitcher extends RelativeLayout implements View.OnClickListener {

    public static final int TYPE_CIRCLE_PROGRESS = 0;
    public static final int TYPE_BAR_CHART = 1;
    private ViewSwitcher vsChartSwitcher;
    private ImageView ivShowCircleProgressChart;
    private ImageView ivShowBarChart;
    private BarChartView bcvStepChart;
    private CircleProgressBar cpbProgressBar;
    private TextView tvStep;
    //全局
    private static int type = TYPE_CIRCLE_PROGRESS;
    private TextView tvDistance;
    private TextView tvDistanceUnit;
    private TextView tvTargetStep;
    private TextView tvCalories;


    public int getSwitchType() {
        return type;
    }

    public void setSwitchType(int type) {
        setSwitchType(type, false);
    }

    private void setSwitchType(int type, boolean needCallback) {
        SportChartViewSwitcher.type = type;
        switch (type) {
            case TYPE_CIRCLE_PROGRESS:

                ivShowBarChart.setVisibility(VISIBLE);
                vsChartSwitcher.setDisplayedChild(0);
                break;
            case TYPE_BAR_CHART:

                ivShowBarChart.setVisibility(GONE);
                vsChartSwitcher.setDisplayedChild(1);

                break;
        }
        if (needCallback && switchTypeChangeListener != null) {
            switchTypeChangeListener.onSwitchTypeChange(type);
        }

    }

    public SportChartViewSwitcher(Context context) {
        super(context);
        init();
    }

    public SportChartViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_home_chart, this);
        vsChartSwitcher = (ViewSwitcher) findViewById(R.id.vsChartSwitcher);
        ivShowCircleProgressChart = (ImageView) findViewById(R.id.ivShowCircleProgressChart);
        ivShowBarChart = (ImageView) findViewById(R.id.ivShowBarChart);
        bcvStepChart = (BarChartView) findViewById(R.id.bcvStepChart);
        cpbProgressBar = (CircleProgressBar) findViewById(R.id.cpbProgressBar);
        tvStep = (TextView) findViewById(R.id.tvStepTotal);

        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDistanceUnit = (TextView) findViewById(R.id.tvDistanceUnit);
        tvTargetStep = (TextView) findViewById(R.id.tvTargetStep);
        tvCalories = (TextView) findViewById(R.id.tvCalories);

        setSwitchType(TYPE_CIRCLE_PROGRESS);
        ivShowCircleProgressChart.setOnClickListener(this);
        ivShowBarChart.setOnClickListener(this);
        cpbProgressBar.setOnClickListener(this);
        bcvStepChart.setOnClickListener(this);

        bcvStepChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        //4小时显示一次的刻度尺
        bcvStepChart.setDataType(new Label24H());
        bcvStepChart.setDrawLabel(false);
        bcvStepChart.setDrawBorder(false);
        bcvStepChart.setDrawLabelLimit(false);
        bcvStepChart.setDrawLimitLine(false);
        bcvStepChart.setDrawZeroLimitLine(true);
        bcvStepChart.setBarColor(0xFF000000);
//         bcvStepChart.setLimitLine(test);

    }


    /**
     * 设置步数详情
     *
     * @param targetStepValue
     * @param currentStepValue
     * @param data
     */
    public void setStepBarChartData(int targetStepValue, int currentStepValue, List<Integer> data) {
        if (bcvStepChart != null) {
//            TLog.Companion.error("进入");
            if(data==null) {
         //       TLog.Companion.error("data==null");
                List<Integer> mData=new ArrayList<>();
                mData.add(0);
                data=mData;
            }
            bcvStepChart.setData(data);
        }
        if (tvStep != null) {
            tvStep.setText(ResUtil.format(ResUtil.getString(R.string.content_total_number_of_steps), currentStepValue));
        }
        if (tvTargetStep != null) {
            tvTargetStep.setText(ResUtil.format("%02d", targetStepValue));
        }

    }

    /**
     * 设置圆形进度条
     * @param targetStepValue  目标值
     * @param currentStepValue 当前步数
     * @param distanceTotal  距离
     * @param caloriesTotal 卡路里
     * @param distanceUnit  单位
     */
    public void setStepCircleProgressChartData(int targetStepValue, int currentStepValue, float distanceTotal, float caloriesTotal, String distanceUnit) {
        if (cpbProgressBar != null) {
            cpbProgressBar.setMaxProgress(targetStepValue);

            cpbProgressBar.setProgress(currentStepValue);
        }
        if (tvTargetStep != null) {
            tvTargetStep.setText(ResUtil.format("%d", targetStepValue));
        }
        if (tvCalories != null) {
            tvCalories.setText(ResUtil.format("%.0f", caloriesTotal));
        }
        if (tvDistance != null) {
            tvDistance.setText(ResUtil.format("%.2f", distanceTotal));
        }
        if (tvDistanceUnit != null) {
            tvDistanceUnit.setText(distanceUnit);
        }

    }
    public void setTvTargetStep(int targetStepValue)
    {
        if (tvTargetStep != null) {
            tvTargetStep.setText(ResUtil.format("%d", targetStepValue));
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivShowCircleProgressChart:
            case R.id.bcvStepChart:
                setSwitchType(TYPE_CIRCLE_PROGRESS, true);
                break;
            case R.id.ivShowBarChart:
            case R.id.cpbProgressBar:
                setSwitchType(TYPE_BAR_CHART, true);
                break;
        }
    }

    private OnSwitchTypeChangeListener switchTypeChangeListener;

    public void setSwitchTypeChangeListener(OnSwitchTypeChangeListener switchTypeChangeListener) {
        this.switchTypeChangeListener = switchTypeChangeListener;
    }

    public interface OnSwitchTypeChangeListener {
        void onSwitchTypeChange(int type);
    }

}
