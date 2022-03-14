package com.example.xingliansdk.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialog;

import com.example.xingliansdk.R;

public class CusDfuAlertDialog extends AppCompatDialog implements View.OnClickListener {

    private Button cancelBtn,sureBtn;

    private  OnCusDfuClickListener onCusDfuClickListener;

    public void setOnCusDfuClickListener(OnCusDfuClickListener onCusDfuClickListener) {
        this.onCusDfuClickListener = onCusDfuClickListener;
    }

    public CusDfuAlertDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cus_dfu_dialog_layout);

        initViews();
    }

    private void initViews() {
        cancelBtn = findViewById(R.id.dialogDfuCancelBtn);
        sureBtn = findViewById(R.id.dialogDfuSureBtn);

        cancelBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
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
    }

    public interface OnCusDfuClickListener{
        void onCancelClick();
        void onSUreClick();
    }
}
