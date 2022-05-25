package com.shon;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.bean.AutoBpStatusBean;

import java.util.List;

/**
 * 用于Haw存储的key
 * Created by Admin
 * Date 2022/5/9
 */
public class HawConstant {

    //app是否停止测量血压
    public static  boolean IS_APP_STOP_MEASURE_BP = false;

    //保存夜间自动测量血压的状态，正常在前，夜间在后
    private static final String AUTO_BP_KEY = "auto_bp_key";
    //把整个夜间血压测量的对象序列化保存在本地
    private static final String SAVE_AUTO_BP_KEY = "save_auto_bp_key";

    public static void saveAutoBpStatus(List<Boolean> autoBpStatus){
        Hawk.put(AUTO_BP_KEY,autoBpStatus);
    }

    public static List<Boolean> getAutoBpStatus(){
        return Hawk.get(AUTO_BP_KEY);
    }


    public static void saveAutoBpData(AutoBpStatusBean autoBpStatusBean){
        Hawk.put(SAVE_AUTO_BP_KEY,new Gson().toJson(autoBpStatusBean));
    }

    public static AutoBpStatusBean getAutoBpStatusData(){
        String str = Hawk.get(SAVE_AUTO_BP_KEY);
        if(str == null)
            return null;
        AutoBpStatusBean ab =  new Gson().fromJson(str,AutoBpStatusBean.class);
        return ab;
    }
}
