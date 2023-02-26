package com.app.fmate.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.app.fmate.R;
import com.hjq.shape.view.ShapeTextView;
import com.shon.connector.BleWrite;
import com.shon.connector.bean.SpecifySleepSourceBean;
import com.shon.connector.call.CmdUtil;

/**
 * 到点后自动弹窗测量弹窗
 * Created by Admin
 * Date 2022/5/9
 */
public class MeasureBpPromptDialog extends AppCompatDialog implements View.OnClickListener {

    //上方的内容
    private TextView topTxtTv;
    //下方内容
    private TextView botTxtTv;

    private ShapeTextView dialogCheckBpNoTv;
    private ShapeTextView dialogCheckBpYetTv;

    //是否是10分钟的
    private boolean isHalfHour;

    private OnCommDialogClickListener onCommDialogClickListener;

    public void setOnCommDialogClickListener(OnCommDialogClickListener onCommDialogClickListener) {
        this.onCommDialogClickListener = onCommDialogClickListener;
    }

    public MeasureBpPromptDialog(Context context) {
        super(context);
    }

    public MeasureBpPromptDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_measure_bp_prompt_layout);

        findViews();

    }


    private void findViews(){
        topTxtTv = findViewById(R.id.dialogCheckBpTopTxtTv);
        botTxtTv = findViewById(R.id.dialogCheckBpBotTxtTv);
        dialogCheckBpNoTv = findViewById(R.id.dialogCheckBpNoTv);
        dialogCheckBpYetTv = findViewById(R.id.dialogCheckBpYetTv);

        topTxtTv.setText(getContext().getResources().getString(R.string.string_dialog_bp_content));
        botTxtTv.setText("");

        dialogCheckBpNoTv.setOnClickListener(this);
        dialogCheckBpYetTv.setOnClickListener(this);

        setBotBtnTxt(getContext().getResources().getString(R.string.string_not_measure),getContext().getResources().getString(R.string.string_go_measure));
    }

    public boolean isHalfHour() {
        return isHalfHour;
    }

    public void setHalfHour(boolean halfHour) {
        isHalfHour = halfHour;
    }

    //设置两个按钮文字
    public void setBotBtnTxt(String rightTxt,String leftTxt){
        dialogCheckBpNoTv.setText(rightTxt);
        dialogCheckBpYetTv.setText(leftTxt);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.dialogCheckBpNoTv){  //否
            dismiss();
            if(isHalfHour)
           {
               stopMeasure();
            }
        }

        if(vId == R.id.dialogCheckBpYetTv){ //是
            dismiss();
            if(onCommDialogClickListener != null)
                onCommDialogClickListener.onConfirmClick(0);
        }
    }

    private void stopMeasure(){

        byte[] cmdArray = new byte[]{0x0B,0x01,0x01,0x00,0x01,0x0c};

        byte[] resultArray = CmdUtil.getFullPackage(cmdArray);
        BleWrite.writeCommByteArray(resultArray, true, new BleWrite.SpecifySleepSourceInterface() {
            @Override
            public void backSpecifySleepSourceBean(SpecifySleepSourceBean specifySleepSourceBean) {

            }

            @Override
            public void backStartAndEndTime(byte[] startTime, byte[] endTime) {

            }
        });
    }
}
