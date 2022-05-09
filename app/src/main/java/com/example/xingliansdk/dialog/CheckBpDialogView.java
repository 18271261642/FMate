package com.example.xingliansdk.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.example.xingliansdk.R;
import com.example.xingliansdk.widget.TitleBarLayout;
import com.hjq.shape.view.ShapeTextView;
import androidx.appcompat.app.AppCompatDialog;

/**
 * 校准血压
 * Created by Admin
 * Date 2022/5/7
 */
public class CheckBpDialogView extends AppCompatDialog implements View.OnClickListener {

    //开始校准按钮
    private ShapeTextView checkBpStartTv;
    private TitleBarLayout bpCheckTitleBar;

    private OnCheckBpDialogListener onCheckBpDialogListener;

    public void setOnCheckBpDialogListener(OnCheckBpDialogListener onCheckBpDialogListener) {
        this.onCheckBpDialogListener = onCheckBpDialogListener;
    }

    public CheckBpDialogView(Context context) {
        super(context);
    }

    public CheckBpDialogView(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_bp_layout);
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        findViews();
    }

    private void findViews(){
        checkBpStartTv = findViewById(R.id.checkBpStartTv);
        bpCheckTitleBar = findViewById(R.id.bpCheckTitleBar);

        checkBpStartTv.setOnClickListener(this);
        bpCheckTitleBar.setTitleBarListener(new TitleBarLayout.TitleBarListener() {
            @Override
            public void onBackClick() {
                if(onCheckBpDialogListener != null)
                    onCheckBpDialogListener.backImgClick();
            }

            @Override
            public void onActionImageClick() {

            }

            @Override
            public void onActionClick() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.checkBpStartTv){
            if(onCheckBpDialogListener != null)
                onCheckBpDialogListener.startCheckClick();
        }
    }


    public interface OnCheckBpDialogListener{
        void backImgClick();

        void startCheckClick();
    }
}
