package com.app.fmate.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * 功能:振动工具~
 */

public class VibratorUtil {
    private static Vibrator vib;

    private static Vibrator getVibrator(Context context) {
        if (vib == null) {
            vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return vib;
    }

    public static void vibrate(final Context context, long milliseconds) {
        getVibrator(context);
        if (vib != null) {
            vib.vibrate(milliseconds);
        }
    }

    public static void vibrate(final Context context, long[] pattern, boolean isRepeat) {
        getVibrator(context);
        if (vib != null) {
            vib.vibrate(pattern, isRepeat ? 1 : -1);
        }
    }

    public static void cancel() {
        if (vib != null) {
            vib.cancel();
        }

    }
}
