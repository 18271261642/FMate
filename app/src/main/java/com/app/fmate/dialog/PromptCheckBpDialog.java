package com.app.fmate.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.app.fmate.R;
import com.hjq.shape.view.ShapeTextView;
import androidx.appcompat.app.AppCompatDialog;

/**
 * 校准血压的dialog提示
 * Created by Admin
 * Date 2022/5/9
 */
public class PromptCheckBpDialog extends AppCompatDialog implements View.OnClickListener {

    //上方的内容
    private TextView topTxtTv;
    //下方内容
    private TextView botTxtTv;

    private ShapeTextView dialogCheckBpNoTv;
    private ShapeTextView dialogCheckBpYetTv;


    private OnCommDialogClickListener onCommDialogClickListener;

    public void setOnCommDialogClickListener(OnCommDialogClickListener onCommDialogClickListener) {
        this.onCommDialogClickListener = onCommDialogClickListener;
    }

    public PromptCheckBpDialog(Context context) {
        super(context);
    }

    public PromptCheckBpDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bp_prompt_layout);
        //底部显示
        getWindow().setGravity(Gravity.BOTTOM);

        findViews();

    }


    private void findViews(){
        topTxtTv = findViewById(R.id.dialogCheckBpTopTxtTv);
        botTxtTv = findViewById(R.id.dialogCheckBpBotTxtTv);
        dialogCheckBpNoTv = findViewById(R.id.dialogCheckBpNoTv);
        dialogCheckBpYetTv = findViewById(R.id.dialogCheckBpYetTv);

        dialogCheckBpNoTv.setOnClickListener(this);
        dialogCheckBpYetTv.setOnClickListener(this);
    }

    //设置上方的TXT内容
    public void setTopTxtValue(String txt){
        if(topTxtTv != null)
            topTxtTv.setText(txt);
    }


    //设置上方的TXT内容
    public void setTopTxtValue(int resId){
        if(topTxtTv != null)
            topTxtTv.setText(resId);
    }

    public void setVisibilityBotTv(boolean isShow){
        if(botTxtTv != null)
            botTxtTv.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    //设置两个按钮文字
    public void setBotBtnTxt(String rightTxt,String leftTxt){
        dialogCheckBpNoTv.setText(rightTxt);
        dialogCheckBpYetTv.setText(leftTxt);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if(vId == R.id.dialogCheckBpNoTv){  //不校准
            dismiss();
            if(onCommDialogClickListener != null)
                onCommDialogClickListener.onCancelClick(0);
        }

        if(vId == R.id.dialogCheckBpYetTv){ //校准
            if(onCommDialogClickListener != null)
                onCommDialogClickListener.onConfirmClick(0);
        }
    }
}
