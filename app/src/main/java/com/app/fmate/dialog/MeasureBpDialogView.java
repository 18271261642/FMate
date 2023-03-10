package com.app.fmate.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.fmate.R;
import com.app.fmate.view.BpMeasureView;
import com.app.fmate.widget.TitleBarLayout;
import com.hjq.shape.view.ShapeTextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;

/**
 * 血压测量的dialog
 * Created by Admin
 * Date 2022/5/7
 */
public class MeasureBpDialogView extends AppCompatDialog implements View.OnClickListener {

    private TitleBarLayout measureBpTitleBar;
    //外圆进度条
    private BpMeasureView bpMeasureView;
    //状态
    private TextView dialogMeasureStatusTv;
    //测量失败按钮
    private ShapeTextView dialogMeasureFailTv;
    private TextView dialogMTmp1,dialogMTmp2;


    private AlertDialog.Builder alert;


    private OnCommDialogClickListener onCommDialogClickListener;

    public void setOnCommDialogClickListener(OnCommDialogClickListener onCommDialogClickListener) {
        this.onCommDialogClickListener = onCommDialogClickListener;
    }

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
        dialogMTmp1 = findViewById(R.id.dialogMTmp1);
        dialogMTmp2 = findViewById(R.id.dialogMTmp2);
        dialogMeasureFailTv = findViewById(R.id.dialogMeasureFailTv);

        dialogMeasureFailTv.setOnClickListener(this);


        assert measureBpTitleBar != null;
        measureBpTitleBar.setTitleBarListener(new TitleBarLayout.TitleBarListener() {
            @Override
            public void onBackClick() {
                if(onCommDialogClickListener != null)
                    onCommDialogClickListener.onCancelClick(0);
            }

            @Override
            public void onActionImageClick() {

            }

            @Override
            public void onActionClick() {

            }
        });

        //bpMeasureView.setProgress(70f);
    }


    //设置测量状态，测量中和测量失败
    public void setMeasureStatus(boolean isSuccess){
        dialogMeasureFailTv.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
        dialogMTmp1.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        dialogMTmp2.setVisibility(isSuccess ? View.VISIBLE : View.GONE);

        dialogMeasureStatusTv.setText(isSuccess ? "测量中" : "测量失败");

        if(onCommDialogClickListener != null)
            onCommDialogClickListener.onCancelClick(isSuccess ? 0 : 1);
    }


    public void setMeasureStatus(boolean isSuccess,boolean again){
        dialogMeasureFailTv.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
        dialogMTmp1.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        dialogMTmp2.setVisibility(isSuccess ? View.VISIBLE : View.GONE);

        dialogMeasureStatusTv.setText(isSuccess ? "测量中" : "测量失败");

    }


    public void setMiddleSchedule(float progress){
        bpMeasureView.setProgress(progress);
    }


    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.dialogMeasureFailTv){
            setMeasureStatus(true,true);
            if(onCommDialogClickListener != null)
                onCommDialogClickListener.onConfirmClick(0);
        }
    }


    private void showAlertDialog(){
        alert = new AlertDialog.Builder(getContext());
        alert.setTitle("提醒");
        alert.setMessage("是否终止测量？");
        alert.setPositiveButton("是", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(onCommDialogClickListener != null)
                    onCommDialogClickListener.onCancelClick(1);
            }
        }).setNegativeButton("否", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }
}
