package com.example.xingliansdk.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.example.xingliansdk.MainHomeActivity;
import com.example.xingliansdk.R;

/**
 * 功能：手环寻找手机工具类
 */

public class BandCallPhoneNotifyUtil {


    private static final int FOUND_PHONE_NOTIFY_ID = 0x230001;
    public static final String FOUND_PHONE_CHANNEL_ID = "CHANNEL_" + FOUND_PHONE_NOTIFY_ID;
    private static Ringtone ringtone;
    private static NotificationManager manager;


    @SuppressLint("WrongConstant")
    public static void startNotification(Context context) {

        //getSystemDefultRingtoneUri(context) ;//
        long[] pattern = {1000, 3000, 1000, 3000, 1000, 3000 };
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.aihealth);
          manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.content_find_phone);
        String text = context.getString(R.string.content_band_call_phone);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager != null) {
                NotificationChannel mChannel = new NotificationChannel(FOUND_PHONE_CHANNEL_ID, context.getString(R.string.content_use_for_find_phone), NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);

                //没错! 8.0的通知就是这么蛋疼!
                //参考https://stackoverflow.com/questions/46019496/notification-sound-on-api-26/46192246
                //参考https://stackoverflow.com/questions/45081815/android-o-notification-channels-change-vibration-pattern-or-sound-type

                //屏蔽自带的铃声和振动
                mChannel.enableVibration(false);
                mChannel.setSound(null, null);
                builder.setVibrate(new long[]{});
                builder.setSound(null);


                manager.createNotificationChannel(mChannel);
                builder.setChannelId(FOUND_PHONE_CHANNEL_ID);
            }
        } else {
            builder.setVibrate(new long[]{});
            builder.setSound(null);
        }
        VibratorUtil.vibrate(context, pattern, false);
        try {
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }
            ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), sound);
            ringtone.play();
        } catch (Exception ignored) {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSmallIcon(R.mipmap.icon_search_band);
        builder.setContentTitle(title);
        if (PermissionUtils.isPhone(PermissionUtils.MANUFACTURER_MEIZU)) {
            //魅族flyme 不能设置setContentText 含有感叹号,过长等 会导致通知无法发送
        } else {
            builder.setContentText(text);
        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_search_band));
        builder.setAutoCancel(true);
        builder.setTicker(context.getString(R.string.content_band_call_phone));
        Intent intent = new Intent(context, MainHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        builder.setContentIntent(PendingIntent.getActivity(context,0, intent,0));

        Notification notification = builder.build();
        if (manager != null) {
            manager.notify(FOUND_PHONE_NOTIFY_ID, notification);
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        try {
            PowerManager.WakeLock wakeLock;
            if (pm != null) {
                wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "neoon:bright");
                wakeLock.acquire(15000);
            }
        } catch (Exception w) {
            w.printStackTrace();
        }
    }


    public static void closeAlert(){
        try {
            if (manager != null){
                manager.cancel(FOUND_PHONE_NOTIFY_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            VibratorUtil.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取系统默认铃声的Uri
    private static Uri getSystemDefultRingtoneUri(Context context) {
        return RingtoneManager.getActualDefaultRingtoneUri(context,
                RingtoneManager.TYPE_RINGTONE);
    }

}
