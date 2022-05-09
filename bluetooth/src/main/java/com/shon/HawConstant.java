package com.shon;

import com.orhanobut.hawk.Hawk;

import java.util.List;

/**
 * 用于Haw存储的key
 * Created by Admin
 * Date 2022/5/9
 */
public class HawConstant {

    //保存夜间自动测量血压的状态，正常在前，夜间在后
    private static final String AUTO_BP_KEY = "auto_bp_key";

    public static void saveAutoBpStatus(List<Boolean> autoBpStatus){
        Hawk.put(AUTO_BP_KEY,autoBpStatus);
    }

    public static List<Boolean> getAutoBpStatus(){
        return Hawk.get(AUTO_BP_KEY);
    }
}
