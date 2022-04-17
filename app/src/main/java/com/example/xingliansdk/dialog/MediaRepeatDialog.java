package com.example.xingliansdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.example.xingliansdk.R;
import com.example.xingliansdk.utils.ShowToast;
import com.hjq.shape.view.ShapeEditText;
import com.hjq.shape.view.ShapeTextView;

public  class MediaRepeatDialog extends AppCompatDialog implements View.OnClickListener {

    private OnMediaRepeatInputListener onMediaRepeatInputListener;

    public void setOnMediaRepeatInputListener(OnMediaRepeatInputListener onMediaRepeatInputListener) {
        this.onMediaRepeatInputListener = onMediaRepeatInputListener;
    }


    //输入框
    private ShapeEditText dialogMediaRepeatEdit;
    //确认按钮
    private ShapeTextView dialogMediaRepeatTv;

    public MediaRepeatDialog(Context context) {
        super(context);
    }

    public MediaRepeatDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_media_repeat_layout);

        dialogMediaRepeatEdit = findViewById(R.id.dialogMediaRepeatEdit);
        dialogMediaRepeatTv = findViewById(R.id.dialogMediaRepeatTv);
        dialogMediaRepeatTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dialogMediaRepeatTv){
            Editable inputStr = dialogMediaRepeatEdit.getText();

            String str = inputStr.toString();
            if(TextUtils.isEmpty(str))
                return;

            if(Integer.parseInt(str) == 0){
                Toast.makeText(getContext(),"请输入正确的时间间隔!",Toast.LENGTH_SHORT).show();
                return;
            }

            if(Integer.parseInt(str) > 255){
                Toast.makeText(getContext(),"   周期天数不大于255天!",Toast.LENGTH_SHORT).show();
                return;
            }

            if(onMediaRepeatInputListener != null)
                onMediaRepeatInputListener.backInputData(Integer.parseInt(str));
        }
    }

    public void setRepeatValue(String repeatValue) {
        dialogMediaRepeatEdit.setText(repeatValue);
    }

    public interface OnMediaRepeatInputListener{
        void backInputData(int day);
    }
}
