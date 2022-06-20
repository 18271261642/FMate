package com.example.xingliansdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatDialog;

import com.example.xingliansdk.R;
import com.hjq.shape.view.ShapeTextView;

public class DateSelectDialogView extends AppCompatDialog {

    protected DatePicker dialogTimePicker;
    protected ShapeTextView foreverTv;


    public DateSelectDialogView(Context context) {
        super(context);
    }

    public DateSelectDialogView(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_select_layout);

        initViews();

        initData();
    }


    private void initData(){
        dialogTimePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

            }
        });
    }



    private void initViews(){
        dialogTimePicker = findViewById(R.id.dialogTimePicker);

        foreverTv = findViewById(R.id.foreverTv);
    }
}
