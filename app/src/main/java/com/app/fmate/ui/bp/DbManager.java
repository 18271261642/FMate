package com.app.fmate.ui.bp;


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


    //查询所有的pppg
    public List<PPG1CacheDb> getAllPPGData(){
        List<PPG1CacheDb> list = LitePal.findAll(PPG1CacheDb.class);
        return list;
    }


    public List<PPG1CacheDb> getDayPPGData(String userId,String mac,String day){
        try {
            String whereStr = "userId = ? and deviceMac = ? and dayStr = ?";
            List<PPG1CacheDb> list = LitePal.where(whereStr,userId,mac,day).find(PPG1CacheDb.class);
            return list == null || list.isEmpty() ? null : list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public List<PPG1CacheDb> getDayPPGData(String userId,String mac,String day,String stauts){
        try {
            String whereStr = "userId = ? and deviceMac = ? and dayStr = ? and dbStatus = ?";
            List<PPG1CacheDb> list = LitePal.where(whereStr,userId,mac,day,stauts).find(PPG1CacheDb.class);
            return list == null || list.isEmpty() ? null : list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public void updatePPGStatus(String userId,String mac,String itemTime,int status){
        String whereStr = "userId = ? and deviceMac = ? and ppgTimeStr = ?";
        PPG1CacheDb ppg1CacheDb = getCurrTimePPGData(userId,mac,itemTime);

        if(ppg1CacheDb != null){
            ppg1CacheDb.setDbStatus(String.valueOf(status));

            boolean isS = ppg1CacheDb.saveOrUpdate("dbStatus=?",String.valueOf(status));
        }
    }


}
