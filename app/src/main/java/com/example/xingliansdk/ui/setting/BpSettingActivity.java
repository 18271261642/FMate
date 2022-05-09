package com.example.xingliansdk.ui.setting;

import android.os.Bundle;

import com.example.xingliansdk.R;
import com.example.xingliansdk.base.BaseActivity;
import com.example.xingliansdk.base.viewmodel.BaseViewModel;
import com.example.xingliansdk.utils.HawkUtil;
import com.example.xingliansdk.widget.TitleBarLayout;
import com.github.iielse.switchbutton.SwitchView;
import com.gyf.barlibrary.ImmersionBar;
import com.luck.picture.lib.tools.ToastUtils;
import com.shon.HawConstant;
import com.shon.connector.BleWrite;
import com.shon.connector.bean.AutoBpStatusBean;
import com.shon.connector.call.listener.CommBackListener;
import com.shon.connector.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 血压自动测量状态设置页面
 * Created by Admin
 * Date 2022/5/7
 */
public class BpSettingActivity extends BaseActivity<BaseViewModel> {

    private TitleBarLayout titleBar;

    //夜间测量
    private SwitchView autoBpNightSwitch;
    //正常测量
    private SwitchView autoBpNormalSwitch;

    private AutoBpStatusBean autoBpStatusBean;

    @Override
    public int layoutId() {
        return R.layout.activity_bp_set_layout;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        findViews();
        ImmersionBar.with(this)
                .titleBar(titleBar)
                .init();


        titleBar.setTitleBarListener(new TitleBarLayout.TitleBarListener() {
            @Override
            public void onBackClick() {
                finish();
            }

            @Override
            public void onActionImageClick() {

            }

            @Override
            public void onActionClick() {
                setBpStatus();
            }
        });
    }

    private void findViews(){
        titleBar = findViewById(R.id.titleBar);
        autoBpNightSwitch = findViewById(R.id.autoBpNightSwitch);
        autoBpNormalSwitch = findViewById(R.id.autoBpNormalSwitch);

        autoBpNormalSwitch.setOnStateChangedListener(onStateChangedListener);
        autoBpNightSwitch.setOnStateChangedListener(onStateChangedListener);

        initData();
    }


    private void initData(){
        if(autoBpStatusBean == null)
         autoBpStatusBean = new AutoBpStatusBean();

        List<Boolean> saveList = HawConstant.getAutoBpStatus();
        if(saveList != null){
            autoBpNightSwitch.setOpened(saveList.get(1));
            autoBpNormalSwitch.setOpened(saveList.get(0));
        }

    }

    private final SwitchView.OnStateChangedListener onStateChangedListener = new SwitchView.OnStateChangedListener() {
        @Override
        public void toggleToOn(SwitchView view) {
            if(view.getId() == R.id.autoBpNightSwitch){ //夜间
                autoBpNightSwitch.setOpened(true);
                autoBpStatusBean.setNightBpStatus((byte) 0x02);
            }
            if(view.getId() == R.id.autoBpNormalSwitch){    //正常
                autoBpNormalSwitch.setOpened(true);
               autoBpStatusBean.setNormalBpStatus((byte) 0x02);
            }
        }

        @Override
        public void toggleToOff(SwitchView view) {
            if(view.getId() == R.id.autoBpNightSwitch){ //夜间
                autoBpNightSwitch.setOpened(false);
                autoBpStatusBean.setNightBpStatus((byte) 0x01);
            }
            if(view.getId() == R.id.autoBpNormalSwitch){    //正常
                autoBpNormalSwitch.setOpened(false);
                autoBpStatusBean.setNormalBpStatus((byte) 0x01);
            }
        }
    };


    private void setBpStatus(){
        if(autoBpNormalSwitch.isOpened()){
            autoBpStatusBean.setNormalBpStatus((byte) 0x02);
            autoBpStatusBean.setStartHour(0x08);
            autoBpStatusBean.setStartMinute(0x00);
            autoBpStatusBean.setEndHour(0x00);
            autoBpStatusBean.setEndMinute(0x00);
            autoBpStatusBean.setBpInterval(0x30);
        }


        //夜间
        if(autoBpNightSwitch.isOpened()){
            autoBpStatusBean.setNightBpStatus((byte) 0x02);
            autoBpStatusBean.setStartHour(0x00);
            autoBpStatusBean.setStartMinute(0x00);
            autoBpStatusBean.setEndHour(0x08);
            autoBpStatusBean.setEndMinute(0x00);
            autoBpStatusBean.setBpInterval(0x30);
        }

        BleWrite.writeSetAutoBpMeasureStatus(true, autoBpStatusBean, new CommBackListener() {
            @Override
            public void commWriteBackData(Object object) {
                if((Boolean) object){
                    List<Boolean> booleanList = new ArrayList<>();
                    booleanList.add(autoBpNormalSwitch.isOpened());
                    booleanList.add(autoBpNightSwitch.isOpened());
                    HawConstant.saveAutoBpStatus(booleanList);
                    ShowToast.INSTANCE.showToast(BpSettingActivity.this,"设置成功",2);
                }
            }
        });
    }
}