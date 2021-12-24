package com.example.xingliansdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class InonePowerSaveUtil {
    public static final boolean IS_CHARGE_DISABLE = true;

    public static boolean isChargingDisable(Context context) {
        return IS_CHARGE_DISABLE && isCharging(context);
    }

    public static boolean isCharging(Context context) {
        Intent batteryBroadcast = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // 0 means we are discharging, anything else means charging
        boolean isCharging = batteryBroadcast.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0;
        Log.e(InonePowerSaveUtil.class.getSimpleName(),"isCharging = " + isCharging );
        return isCharging;
    }

}
