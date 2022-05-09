package com.example.xingliansdk.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xingliansdk.R;
import com.example.xingliansdk.view.BpMeasureView;
import com.example.xingliansdk.widget.TitleBarLayout;

import androidx.appcompat.app.AppCompatDialog;

/**
 * 血压测量的dialog
 * Created by Admin
 * Date 2022/5/7
 */
public class MeasureBpDialogView extends AppCompatDialog {

    private TitleBarLayout measureBpTitleBar;
    //外圆进度条
    private BpMeasureView bpMeasureView;
    //状态
    private TextView dialogMeasureStatusTv;

    public MeasureBpDialogView(Context context) {
        super(context);
    }

    public MeasureBpDialogView(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_measure_bp_ing_layout);

        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        findViews();
    }


    private void findViews(){
        bpMeasureView = findViewById(R.id.bpMeasureView);
        dialogMeasureStatusTv = findViewById(R.id.dialogMeasureStatusTv);
        measureBpTitleBar = findViewById(R.id.measureBpTitleBar);

        assert measureBpTitleBar != null;
        measureBpTitleBar.setTitleBarListener(new TitleBarLayout.TitleBarListener() {
            @Override
            public void onBackClick() {
                dismiss();
            }

            @Override
            public void onActionImageClick() {

            }

            @Override
            public void onActionClick() {

            }
        });

       // bpMeasureView.setProgress(70f);
    }



    public void setMiddleSchedule(){
        bpMeasureView.setProgressX(60 * 1000,false);
    }
}
