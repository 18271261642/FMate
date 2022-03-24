package com.example.xingliansdk.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.example.xingliansdk.R;

public class CusDfuAlertDialog extends AppCompatDialog implements View.OnClickListener {

    private TextView cancelBtn,sureBtn;

    private  OnCusDfuClickListener onCusDfuClickListener;
    private TextView dialogDufContentTv;

    private TextView normalTv;

    private int countTime = 3;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x00){
                countTime--;
                normalTv.setText(countTime == 0 ?"知道了":"知道了("+countTime+"s)");

                if(countTime == 0){
                    dismiss();
                    return;
                }

                setStartNormalTime();
            }
        }
    };


    public void setOnCusDfuClickListener(OnCusDfuClickListener onCusDfuClickListener) {
        this.onCusDfuClickListener = onCusDfuClickListener;
    }

    public CusDfuAlertDialog(Context context) {
        super(context);
    }

    public CusDfuAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    //    style="@style/edit_AlertDialog_style"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cus_dfu_dialog_layout);

        initViews();
    }

    private void initViews() {
        dialogDufContentTv = findViewById(R.id.dialogDufContentTv);
        normalTv = findViewById(R.id.dialogDfuNormalTv);
        cancelBtn = findViewById(R.id.dialogDfuCancelBtn);
        sureBtn = findViewById(R.id.dialogDfuSureBtn);

        cancelBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
        normalTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dialogDfuCancelBtn){
            if(onCusDfuClickListener != null)
                onCusDfuClickListener.onCancelClick();
        }

        if(view.getId() == R.id.dialogDfuSureBtn){
            if(onCusDfuClickListener != null)
                onCusDfuClickListener.onSUreClick();
        }

        if(view.getId() == R.id.dialogDfuNormalTv){
            dismiss();
        }
    }

    public interface OnCusDfuClickListener{
        void onCancelClick();
        void onSUreClick();
    }


    public void setNormalShow(boolean isNormal){
        normalTv.setVisibility(isNormal ? View.VISIBLE : View.GONE);
        cancelBtn.setVisibility(isNormal ? View.GONE : View.VISIBLE);
        sureBtn.setVisibility(isNormal ? View.GONE : View.VISIBLE);
        dialogDufContentTv.setText(isNormal ? getContext().getResources().getString(R.string.string_dfu_low_battery_desc):getContext().getResources().getString(R.string.string_dfu_normal_desc));
        if(isNormal){
            setStartNormalTime();
        }
    }





    public void setStartNormalTime(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x00);
            }
        },1000);

    }
}
