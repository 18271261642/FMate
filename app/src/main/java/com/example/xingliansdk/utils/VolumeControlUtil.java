package com.example.xingliansdk.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * 功能:音量控制
 */

public class VolumeControlUtil {

    public static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;

    public static void setVolumeUp(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.adjustStreamVolume(STREAM_TYPE,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//            int streamMaxVolume = audioManager.getStreamMaxVolume(STREAM_TYPE);
//            int streamCurVolume = audioManager.getStreamVolume(STREAM_TYPE);
//            if(streamCurVolume<streamMaxVolume){
//                streamCurVolume++;
//            }
//            audioManager.setStreamVolume(STREAM_TYPE,streamCurVolume,AudioManager.FLAG_SHOW_UI);
        }

    }

    public static void setVolumeDown(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.adjustStreamVolume(STREAM_TYPE,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//            int streamCurVolume = audioManager.getStreamVolume(STREAM_TYPE);
//            if(streamCurVolume>0){
//                streamCurVolume--;
//            }
//            audioManager.setStreamVolume(STREAM_TYPE,streamCurVolume,AudioManager.FLAG_SHOW_UI);
        }
    }
}
