package com.example.xingliansdk.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;

import com.example.xingliansdk.R;
import com.example.xingliansdk.view.NumberPicker;

/**
 * 功能：用户信息通用弹框
 * 适用于个人信息，设置生日、身高等的点击弹框
 */

public class UserCommonDialog {
    public static final String FORMAT_3 = "%d000";
    public static final String FORMAT_2 = "%d00";
    public static final String FORMAT_1 = "%d0";

    public static final String FORMAT_ZERO_2 = "%02d";
    public static final String FORMAT_ZERO_3 = "%03d";
    public static final String FORMAT_ZERO_4 = "%04d";
    public static final String FORMAT_ZERO_5 = "%05d";
    public static final String FORMAT_ZERO_6 = "%06d";

    private static BaseDialog dialog;

    public static BaseDialog create(Context context,
                                    String unit,
                                    String formatter,
                                    int minValue,
                                    int maxValue,
                                    int selectValue,
                                    NumberPicker.OnValueChangeListener changeListener,
                                    DialogInterface.OnClickListener onCancelListener,
                                    DialogInterface.OnClickListener onConfirmListener) {
        return create(context, unit, formatter, minValue, maxValue, selectValue, ViewGroup.GONE, ViewGroup.GONE, changeListener, onCancelListener, onConfirmListener);
    }

    public static BaseDialog create(Context context,
                                    String unit,
                                    String formatter,
                                    int minValue,
                                    int maxValue,
                                    int selectValue,
                                    CharSequence title,
                                    CharSequence content,
                                    NumberPicker.OnValueChangeListener changeListener,
                                    DialogInterface.OnClickListener onCancelListener,
                                    DialogInterface.OnClickListener onConfirmListener) {
        BaseDialog dialog = create(context, unit, formatter, minValue, maxValue, selectValue, ViewGroup.VISIBLE, ViewGroup.VISIBLE, changeListener, onCancelListener, onConfirmListener);
        dialog.setText(R.id.tvUserCommonTitle, title);
        dialog.setText(R.id.tvUserCommonContent, content);
        return dialog;
    }

    public static BaseDialog create(Context context,
                                    String unit,
                                    String formatter,
                                    int minValue,
                                    int maxValue,
                                    int selectValue,
                                    int titleVisible,
                                    int contentVisible,
                                    NumberPicker.OnValueChangeListener changeListener,
                                    DialogInterface.OnClickListener onCancelListener,
                                    DialogInterface.OnClickListener onConfirmListener) {
        dialog = new BaseDialog.Builder(context)
                .setContentView(R.layout.dialog_user_common)
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .fullWidth()
                .setViewVisible(R.id.tvUserCommonTitle, titleVisible)
                .setViewVisible(R.id.tvUserCommonContent, contentVisible)
                .setText(R.id.tvUserCommonUnit, unit)
                .setOnClickListener(R.id.tvUserCommonExit, onCancelListener)
                .setOnClickListener(R.id.tvUserCommonConfirm, onConfirmListener)
                .create();
        NumberPicker numberPicker = dialog.findViewById(R.id.npUserCommonPicker);
        if (numberPicker != null) {
            numberPicker.setMinValue(minValue);
            numberPicker.setMaxValue(maxValue);
            numberPicker.setValue(selectValue);
            numberPicker.setFormatter(formatter);
            numberPicker.setOnValueChangedListener(changeListener);
        }
        return dialog;
    }
}
