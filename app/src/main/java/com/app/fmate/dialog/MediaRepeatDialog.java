package com.app.fmate.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.app.fmate.R;
import com.hjq.shape.view.ShapeEditText;
import com.hjq.shape.view.ShapeTextView;

public  class MediaRepeatDialog extends AppCompatDialog implements View.OnClickListener {

    private OnMediaRepeatInputListener onMediaRepeatInputListener;

    public void setOnMediaRepeatInputListener(OnMediaRepeatInputListener onMediaRepeatInputListener) {
        this.onMediaRepeatInputListener = onMediaRepeatInputListener;
    }

    //标题
    private TextView mediaDialogTitleTv;
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

        mediaDialogTitleTv = findViewById(R.id.mediaDialogTitleTv);
        dialogMediaRepeatEdit = findViewById(R.id.dialogMediaRepeatEdit);
        dialogMediaRepeatTv = findViewById(R.id.dialogMediaRepeatTv);
        dialogMediaRepeatTv.setOnClickListener(this);
    }

    //设置标题
    public void setTitleTxt(String txt){
        if(mediaDialogTitleTv != null)
            mediaDialogTitleTv.setText(txt);
    }


    //设置输入框hit显示
    public void setContentHitTxt(String txt){
        if(dialogMediaRepeatEdit != null)
            dialogMediaRepeatEdit.setHint(txt);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dialogMediaRepeatTv){
            Editable inputStr = dialogMediaRepeatEdit.getText();

            String str = inputStr.toString();
            if(TextUtils.isEmpty(str))
                return;

//            if(Integer.parseInt(str) == 0){
//                Toast.makeText(getContext(),"请输入正确的时间间隔!",Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if(Integer.parseInt(str) > 255){
//                Toast.makeText(getContext(),"   周期天数不大于255天!",Toast.LENGTH_SHORT).show();
//                return;
//            }

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
