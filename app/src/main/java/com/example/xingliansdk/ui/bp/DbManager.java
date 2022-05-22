package com.example.xingliansdk.ui.bp;


import android.util.Log;

import com.example.xingliansdk.Config;
import com.example.xingliansdk.utils.Utils;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import org.litepal.LitePal;
import java.text.DecimalFormat;
import java.util.List;


/**
 * Created by Admin
 * Date 2021/9/12
 */
public class DbManager {

    private static final String TAG = "DbManager";

    private static DbManager dbManager = null;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static DbManager getDbManager(){
        synchronized (DbManager.class){
            if(dbManager == null)
                dbManager = new DbManager();
        }
        return dbManager;
    }


    //保存PPG 夜间血压数据
    public boolean savePPBBpData(PPG1CacheDb ppg1CacheDb){
        try {
           return ppg1CacheDb.save();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    //查询对应的时间点是否有数据
    public PPG1CacheDb getCurrTimePPGData(String userId,String mac,String timeStr){
        try {
            String whereStr = "userId = ? and deviceMac = ? and ppgTimeStr = ?";
            List<PPG1CacheDb> list = LitePal.where(whereStr,userId,mac,timeStr).find(PPG1CacheDb.class);
            return list == null || list.isEmpty() ? null : list.get(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


}
