package com.example.xingliansdk.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteController;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.example.xingliansdk.XingLianApplication;
import com.example.xingliansdk.base.BaseApp;
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
        try {
            if(!fastClick())
                return;

//            RemoteControlService remoteControlService = XingLianApplication.mXingLianApplication.getRemoteMusic();
//            if(remoteControlService != null)
//                remoteControlService.sendMusicKeyEvent(keyCode);


            if(audioManager == null)
                audioManager =  (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int resultCode = audioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            long eventTime = SystemClock.uptimeMillis();
            TLog.Companion.error("mAudioManager+="+audioManager.isMusicActive());
            if (audioManager != null) {
                KeyEvent downEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    audioManager.dispatchMediaKeyEvent(downEvent);
                }else {
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    context.sendBroadcast(downIntent, null);
                }
                KeyEvent upEvent = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    audioManager.dispatchMediaKeyEvent(upEvent);
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
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static AudioManager audioManager;


    public static void playOrPauseMusic(Context context){
        if(!fastClick())
            return;
        try {
            if(audioManager == null)
                audioManager =  (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            assert audioManager != null;
            int resultCode = audioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if(audioManager.isMusicActive()){
                pauseMusic(context);
            }else{
                playMusic(context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //暂停音乐
    private static void pauseMusic(Context context){
        try {

           RemoteControlService remoteControlService = XingLianApplication.mXingLianApplication.getRemoteMusic();
           if(remoteControlService != null)
               remoteControlService.sendMusicKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
//
//            if(audioManager != null){
//                long eventTime2 = SystemClock.uptimeMillis() - 1;
//                KeyEvent downEvent2 = new KeyEvent(eventTime2,eventTime2,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
//                audioManager.dispatchMediaKeyEvent(downEvent2);
//
//                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
//                KeyEvent upEvent = new KeyEvent(eventTime2, eventTime2, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
//                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
//                context.sendOrderedBroadcast(upIntent, null);
//                audioManager.dispatchMediaKeyEvent(upEvent);
//
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //播放音乐
    private static void playMusic(Context context){
        try {
            if(audioManager != null){
                long eventTime2 = SystemClock.uptimeMillis() - 1;
                KeyEvent downEvent2 = new KeyEvent(eventTime2,eventTime2,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                audioManager.dispatchMediaKeyEvent(downEvent2);

                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent upEvent = new KeyEvent(eventTime2, eventTime2, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                context.sendOrderedBroadcast(upIntent, null);
                audioManager.dispatchMediaKeyEvent(upEvent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static final AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
        }
    };


    public static boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

}
