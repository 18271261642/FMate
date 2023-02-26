package com.app.fmate.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatDialog;

import com.app.fmate.R;
import com.app.fmate.view.DateUtil;
import com.hjq.shape.view.ShapeTextView;
import com.shon.connector.utils.TLog;

import java.util.Calendar;
import java.util.Date;

public class DateSelectDialogView extends AppCompatDialog {

    protected DatePicker dialogTimePicker;
    protected ShapeTextView foreverTv;
    private View tmpSelectView;

    private OnDateSelectListener onDateSelectListener;

    public void setOnDateSelectListener(OnDateSelectListener onDateSelectListener) {
        this.onDateSelectListener = onDateSelectListener;
    }

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
        foreverTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onDateSelectListener != null){
                    onDateSelectListener.foreverSelect();
                }
            }
        });
    }



    //设置当前需要显示的日期
    public void setCurrentShowDate(long showDate){
        TLog.Companion.error("-----回填时间="+showDate+" "+ DateUtil.getDate(showDate));
        if(dialogTimePicker == null)
            return;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(showDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dialogTimePicker.init(year, month, day, onDateChangedListener);
    }

    //是否显示永久按钮
    public void isShowForeverBtn(boolean isShow){
        tmpSelectView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        foreverTv.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    private void initViews(){
        dialogTimePicker = findViewById(R.id.dialogTimePicker);
        tmpSelectView = findViewById(R.id.tmpSelectView);
        foreverTv = findViewById(R.id.foreverTv);

        ((LinearLayout) ((ViewGroup)dialogTimePicker.getChildAt(0)).getChildAt(0)).setVisibility(View.GONE);
    }


    private final DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
            TLog.Companion.error("----222-日期选择="+i+" "+i1+" "+i2);
            String dayStr = i +"-"+(i1+1)+"-"+i2;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR,i);
            calendar.set(Calendar.MONTH,i1);
            calendar.set(Calendar.DAY_OF_MONTH,i2);

            long time = calendar.getTimeInMillis();
            Date date = new Date(time);
            if(onDateSelectListener != null)
                onDateSelectListener.onDateSelect(date);
        }
    };


    public interface OnDateSelectListener{
        void onDateSelect(Date date);
        void foreverSelect();
    }
}
