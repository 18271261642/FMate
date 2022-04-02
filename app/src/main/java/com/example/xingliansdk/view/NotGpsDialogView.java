package com.example.xingliansdk.view;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.xingliansdk.R;

import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Created by Admin
 * Date 2022/4/2
 */
public class NotGpsDialogView extends AppCompatDialog {

    private ConstraintLayout dialogParentLayout;

    public NotGpsDialogView(Context context) {
        super(context);
    }

    public NotGpsDialogView(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_open_gps_layout);

        dialogParentLayout = findViewById(R.id.dialogParentLayout);



        Window window = this.getWindow();
        window.setGravity(Gravity.TOP | Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.horizontalMargin = 10;
        window.setAttributes(layoutParams);
        window.getDecorView().setMinimumWidth(getContext().getResources().getDisplayMetrics().widthPixels);


    }
}
