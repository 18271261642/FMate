package com.example.xingliansdk.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.collection.ArraySet;
import androidx.core.app.NotificationManagerCompat;

import com.example.xingliansdk.service.reminder.SNNotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PermissionUtils {
    public static final String MANUFACTURER_XIAOMI = "XIAOMI";//小米
    public static final String MANUFACTURER_VIVO = "VIVO";
    public static final String MANUFACTURER_MEIZU = "MEIZU";//魅族
    //上面的原生权限和AppOps权限明明显示被允许,但实际上没有权限, 蛋疼!, 此时开发者的你可以强制设置认为没有权限,通常是解决第三方权限管理工具 比如360卫士,腾讯管家, i管家等
    private static List<String> mIthinkDeniedList = new ArrayList<>();

    public static void addDeniedPermission(Context context, String permission) {
        if (!mIthinkDeniedList.contains(permission)) {
            mIthinkDeniedList.add(permission);
            //存放
            PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putStringSet("permissions", new ArraySet<>(mIthinkDeniedList)).apply();
        }
    }
    public static void clearDeniedPermission(Context context) {
        mIthinkDeniedList.clear();
        //存放
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putStringSet("permissions", new ArraySet<>(new ArrayList<String>())).apply();

    }
    /**
     * 跳转到通知内容读取权限设置
     *
     * @param context
     */
    public static void startToNotificationListenSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * 跳转到位置设置
     *
     * @param context
     */
    public static void startToLocationSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }
    /**
     * 判断服务是否运行
     *
     * @param context
     * @param cls
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<? extends Service> cls) {
        boolean isServiceRunning = false;
        ComponentName collectorComponent = new ComponentName(context, cls);
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
            if (runningServices != null) {
                for (ActivityManager.RunningServiceInfo service : runningServices) {
                    if (service.service.equals(collectorComponent)) {
                        if (service.pid == Process.myPid()) {
                            isServiceRunning = true;
                        }
                    }
                }
            }
        }
        return isServiceRunning;
    }


    /**
     * 有通知/监听读取权限
     * 迁移到SNNotificationListener依赖库
     *
     * @param context
     * @return
     */
    public static boolean hasNotificationListenPermission(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        return !packageNames.isEmpty() && packageNames.contains(context.getPackageName());
    }
    public static boolean isPhone(String MANUFACTURER_TYPE) {
        return Build.MANUFACTURER.equalsIgnoreCase(MANUFACTURER_TYPE);
    }
    public static boolean isNotificationListenerHavePermissionButIsDead(Context context){
        boolean b = hasNotificationListenPermission(context);
        boolean serviceRunning = isServiceRunning(context, SNNotificationService.class);
//        TLog.Companion.log("权限="+b+",服务状态="+serviceRunning);
        return b && !serviceRunning;
    }
    /**
     * 请求刷新通知监听服务
     * 迁移到SNNotificationListener依赖库
     *
     * @param context
     * @return
     */
    public static void requestRebindNotificationListenerService(final Context context) {
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, SNNotificationService.class);

        if (isNotificationListenerHavePermissionButIsDead(context)) {
            //如果服务没在运行 重新请求绑定服务
            //有些手机的通知服务开启较慢

            pm.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        }

    }
    /**
     * 是否有通知可见权限
     *
     * @param context
     * @return
     */
    public static boolean hasNotificationEnablePermission(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();

    }
    /**
     * 位置权限是否开启
     *
     * @param context
     * @return
     */
    public static boolean hasLocationEnablePermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Exception ignored) {
        }
        if (locationMode != Settings.Secure.LOCATION_MODE_OFF) {
            return true;
        }
        return false;
    }
    /**
     * 跳转到app通知推送显示权限设置
     *
     * @param context
     * @param channelId
     */

    public static void startToNotificationEnableSetting(Context context, String channelId) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                //参考https://stackoverflow.com/questions/48853948/intent-to-open-the-notification-channel-settings-from-my-app
                if (channelId != null) {
                    intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
                } else {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                }

                context.startActivity(intent);
            } catch (Exception e) {
                startToDefaultAppInfoSetting(context);
            }
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //参考https://stackoverflow.com/questions/32366649/any-way-to-link-to-the-android-notification-settings-for-my-app
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            try {
                context.startActivity(intent);
            } catch (Exception e1) {
                startToDefaultAppInfoSetting(context);
            }
        }
    }

    /**
     * 应用信息界面
     *
     * @param context
     */
    private static void startToDefaultAppInfoSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);


    }

}
