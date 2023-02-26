package com.app.fmate.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.shon.connector.utils.TLog;

import java.lang.reflect.Method;

/**
 * 功能:系统工具
 */

public class SystemUtil {


    public static boolean isMIUI12(){
        try {
            String properties = getSystemProperties("ro.miui.ui.version.name", "");
            if("V12".equalsIgnoreCase(properties)){
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getSystemProperties(String ro, String def) {
        try {
            Class<?> mSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = mSystemProperties.getDeclaredMethod("get", String.class, String.class);
            getMethod.setAccessible(true);
            return (String) getMethod.invoke(null, ro, def);
        } catch (Exception unused) {
            return def;
        }
    }



    /**
     * 获取设备唯一标识
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getUniqueId(Context context) {
        String mUniqueId = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (telephonyManager != null) {
                    mUniqueId = telephonyManager.getDeviceId();
                }
            }
        } catch (Exception ignored) {
        }
        if (TextUtils.isEmpty(mUniqueId)||mUniqueId.contains("000000")) {
            String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    String serial = Build.getSerial();
                    if(TextUtils.isEmpty(serial)||serial.equals("unknown")){
                        mUniqueId =  androidID;
                    }else{
                        mUniqueId = serial ;
                    }

                } catch (Exception e) {
                    mUniqueId = androidID + Build.SERIAL;
                }
            }else{
                mUniqueId = androidID + Build.SERIAL;
            }
        }
            TLog.Companion.error("mUniqueId++"+mUniqueId);
        return MD5Util.md5(mUniqueId);
    }
}
