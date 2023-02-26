package com.app.fmate.ui;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fmate.R;
import com.app.fmate.adapter.MotionAdapter;
import com.app.fmate.base.BaseActivity;
import com.app.fmate.base.viewmodel.BaseViewModel;
import com.app.fmate.bean.BloodPressureBean;
import com.shon.connector.utils.TLog;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Random;

public class MotionActivity extends BaseActivity<BaseViewModel> {
    RecyclerView mRecyclerView;
    LinkedList<BloodPressureBean> mList=new LinkedList<>();
    MotionAdapter mMotionAdapter;

    @Override
    public int layoutId() {
        return R.layout.activity_motion;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mRecyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mList = new LinkedList<>();
        BloodPressureBean mBloodPressureBean;
        Hawk.put("motionMax",0);
        for (int i = 0; i < 10; i++) {
            mBloodPressureBean = new BloodPressureBean();
            Random rand = new Random();
            mBloodPressureBean.setHeight(100 + rand.nextInt(50));
            mBloodPressureBean.setLow(80 + rand.nextInt(30));
            if(Hawk.get("motionMax",0)<mBloodPressureBean.getHeight())
            {
                Hawk.put("motionMax",mBloodPressureBean.getHeight());
                TLog.Companion.error("最新存储值+"+Hawk.get("motionMax",0));
            }
//            mBloodPressureBean.setTime((6 + i) + ":00");
            mList.add(mBloodPressureBean);
        }
        TLog.Companion.error("list=="+new Gson().toJson(mList));
//        mMotionAdapter = new MotionAdapter(mList);
        mRecyclerView.setAdapter(mMotionAdapter);
//        mBloodPressureAdapter.addData(mList);
//        mBloodPressureAdapter.notifyDataSetChanged();
    }
}