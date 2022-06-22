package com.example.xingliansdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatDialog;

import com.example.xingliansdk.R;
import com.hjq.shape.view.ShapeTextView;
import com.shon.connector.utils.TLog;

import java.util.Calendar;
import java.util.Date;

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

    }



    //设置当前需要显示的日期
    public void setCurrentShowDate(Date date){
        if(dialogTimePicker == null)
            return;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,22);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dialogTimePicker.init(year, month, day, onDateChangedListener);
    }


    private void initViews(){
        dialogTimePicker = findViewById(R.id.dialogTimePicker);

        foreverTv = findViewById(R.id.foreverTv);

        ((LinearLayout) ((ViewGroup)dialogTimePicker.getChildAt(0)).getChildAt(0)).setVisibility(View.GONE);
    }


    private final DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
            TLog.Companion.error("----222-日期选择="+i+" "+i1+" "+i2);
        }
    };
}
