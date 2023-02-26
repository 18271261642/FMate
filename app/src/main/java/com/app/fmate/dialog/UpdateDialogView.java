package com.app.fmate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.fmate.R;


/**
 * Created by Admin
 * Date 2022/4/26
 */
public class UpdateDialogView extends Dialog implements View.OnClickListener {

    private TextView dialog_cancel;
    private TextView dialog_confirm;

    private TextView dialog_content;

    private onUpdateDialogListener onUpdateDialogListener;

    public void setOnUpdateDialogListener(UpdateDialogView.onUpdateDialogListener onUpdateDialogListener) {
        this.onUpdateDialogListener = onUpdateDialogListener;
    }

    public UpdateDialogView(Context context) {
        super(context);
    }

    public UpdateDialogView(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete);

        dialog_content = findViewById(R.id.dialog_content);
        dialog_cancel = findViewById(R.id.dialog_cancel);
        dialog_confirm = findViewById(R.id.dialog_confirm);
        dialog_cancel.setOnClickListener(this);
        dialog_confirm.setOnClickListener(this);

    }


    //强制更新就只有一个确认按钮
    public void setIsFocus(boolean isFocus){
        dialog_cancel.setVisibility(isFocus ? View.GONE : View.VISIBLE);
    }

    public void setContentTxt(String txt){
        dialog_content.setText(txt);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.dialog_cancel){    //取消
            if(onUpdateDialogListener != null)
                onUpdateDialogListener.onCancelClick();
        }

        if(v.getId() == R.id.dialog_confirm){   //确定
            if(onUpdateDialogListener != null)
                onUpdateDialogListener.onSureClick();
        }
    }


    public interface onUpdateDialogListener{
        void onSureClick();
        void onCancelClick();
    }
}
