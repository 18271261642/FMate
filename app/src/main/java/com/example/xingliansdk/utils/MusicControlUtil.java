package com.example.xingliansdk.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.shon.connector.utils.TLog;

/**
 * 功能：控制音乐播放器播放、暂停、上一首、下一首
 */
public class MusicControlUtil {

    /**
     * 控制第三方音乐播放器的播放、暂停、上一首、下一首功能
     * 通过发送模拟按键的广播实现
     * @param context 上下文参数
     * @param keyCode 按键码
     */
    public static void sendKeyEvents(Context context, int keyCode) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        long eventTime = SystemClock.uptimeMillis();
        TLog.Companion.error("mAudioManager+="+mAudioManager);
        if (mAudioManager != null) {
            KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAudioManager.dispatchMediaKeyEvent(downEvent);
            }else {
                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                context.sendBroadcast(downIntent, null);
            }
            KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAudioManager.dispatchMediaKeyEvent(upEvent);
            }else {
                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                context.sendBroadcast(upIntent, null);
            }
        }
        else
        {
            TLog.Companion.error("傻逼 开音乐播放器强开");
            Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
            context.startActivity(intent);
        }
    }
}
