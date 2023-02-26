package com.app.otalib.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.app.otalib.NotificationActivity;

import no.nordicsemi.android.dfu.DfuBaseService;



public class DfuService extends DfuBaseService {
    public DfuService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                initServiceFrontDesk();
            }
        });

    }

    String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";

    /**
     * 初始化服务前台
     */
    private void initServiceFrontDesk() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel;
            channel = new NotificationChannel(CHANNEL_ONE_ID, "APP更新服务", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false);//设置提示灯
            channel.setShowBadge(true);//显示logo
            channel.setDescription("APP更新服务");//设置描述
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, CHANNEL_ONE_ID)
                    .setContentTitle("APP更新服务")//标题
                    .setContentText("运行中...")//内容
                    .setWhen(System.currentTimeMillis())
                   // .setSmallIcon(R.mipmap.ic_launcher)
                  //  .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .build();
            startForeground(102, notification);
        }
    }

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return NotificationActivity.class;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
