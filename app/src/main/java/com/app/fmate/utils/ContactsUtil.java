package com.app.fmate.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

/**
 * 类型：来电提醒和短信提醒工具类
 */

public class ContactsUtil {
    /**
     * 号码查询手机联系人
     *
     * @param context     上下文参数
     * @param phoneNumber 电话号码
     * @return
     */
    public static String lookForContacts(Context context, String phoneNumber) {
        String contactName = "";
        String[] project = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER};
        //oppo等手机不知道为何拿到联系人名称为乱码，导致了查询异常，这里try catch住，不让崩溃
        try {
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));
            Cursor cursor = context.getContentResolver().query(uri, project, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            } else {
                contactName = phoneNumber;
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            contactName = phoneNumber;
        }
        return contactName;
    }

    /**
     * 拒接电话
     *
     * @param context 上下文参数
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    public static void rejectCall(final Context context) {
//        Class<TelephonyManager> managerClass = TelephonyManager.class;
//        try {
//            Method getITelephony = managerClass.getDeclaredMethod("getITelephony", (Class[]) null);
//            getITelephony.setAccessible(true);
//            TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
//
//            ITelephony iTelephony = (ITelephony) getITelephony.invoke(manager, (Object[]) null);
//            iTelephony.endCall();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        boolean isEndCalled = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) {
                    isEndCalled = tm.endCall();
                    if (isEndCalled) {
                        return;
                    }
                }
            } catch (Throwable e) {
                //万一以后有别的异常呢...

                e.printStackTrace();
            }
        }


        try {
            final TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("SoonBlockedPrivateApi")
            Method getITelephony = TelephonyManager.class.getDeclaredMethod("getITelephony");
            getITelephony.setAccessible(true);
            Object iTelephony = getITelephony.invoke(manager);


            //参考SubscriptionController.java
            //取得手机[使用哪张卡打电话], 其中还有[使用哪张卡的移动网络],[使用哪张卡接收短信]等等参数 可自己查看Settings.Global里 multi_sim_开头的字符串常量
            //int subId= Settings.Global.getInt(context.getContentResolver(), "multi_sim_voice_call");


            //想要理解下面说的内容 请参看安卓系统源码: PhoneInterfaceManager.java,SubscriptionController.java

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //修复VIVO/OPPO双卡手机 可能无法挂断电话的问题, 因为这是个系统bug (VIVO/OPPO工程师辣鸡)
                // 可能把  getDefaultSubscription 误判了,  明明是sim卡1来电 调用endcall挂断的是sim卡2, 所以无法挂电话!
                //我看了endcall的源码,发现可以直接反射 取得endCallForSubscriber 传入sim卡的subid即可,
                //阅读Settings.Global.MULTI_SIM_VOICE_CALL_SUBSCRIPTION 的常量注释可得 这个sim卡 的subid为 0和1,  代表卡1和卡2,
                // 很好,为了避免vivo的误判问题 我们直接帮他执行两张卡的挂断即可,  完美!
                Method endCallForSubscriber = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        //源码中看到,在安卓5.1.0及以后时候 这个subid参数是int
                        endCallForSubscriber = iTelephony.getClass().getDeclaredMethod("endCallForSubscriber", int.class);
                        endCallForSubscriber.setAccessible(true);
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                        //源码中看到,安卓5.0.1的时候 这个subid参数是long
                        endCallForSubscriber = iTelephony.getClass().getDeclaredMethod("endCallForSubscriber", long.class);
                        endCallForSubscriber.setAccessible(true);
                    }
                } catch (Exception ignored) {
                }

                try {
                    if (endCallForSubscriber != null) {
                        //挂断卡1
                        boolean endCallSim1 = (Boolean) endCallForSubscriber.invoke(iTelephony, 0);
                        //挂断卡2
                        boolean endCallSim2 = (Boolean) endCallForSubscriber.invoke(iTelephony, 1);
                        isEndCalled = endCallSim1 || endCallSim2;//只要有一张卡能挂断 算它挂断成功!
                        //System.out.println("挂断sim卡1=" + endCallSim1 + ",挂断sim卡2=" + endCallSim2);
                    }
                } catch (Exception ignored) {
                    isEndCalled = false;
                }
            }
            //如果挂不断,再试试endcall
            if (!isEndCalled) {
                try {
                    // 如果找不到endCallForSubscriber,可能是某些系统改动了 或者系统版本过低,  于是调用endCall (endCall 在安卓2.2就有了)
                    Method endCall = iTelephony.getClass().getDeclaredMethod("endCall");
                    endCall.setAccessible(true);
                    endCall.invoke(iTelephony);
                } catch (Exception ignored) {
                }
            }


            //vivo 权限适配
            if (PermissionUtils.isPhone(PermissionUtils.MANUFACTURER_VIVO)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) {
                        }
                        if (manager != null) {
                            //按理说到这里,电话已经挂断了,但如果状态仍然是在响铃或接电话中,说明无法挂电话,可能是权限等问题 此时可能要提醒用户开启权限
                            int state = manager.getCallState();
                            if (state != TelephonyManager.CALL_STATE_IDLE) {
                                PermissionUtils.addDeniedPermission(context, Manifest.permission.CALL_PHONE);
                            }
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int previousMuteMode = -1;

    /**
     * 修改系统音量，实现来电静音
     *
     * @param context  上下文参数
     * @param isSilent 是否静音
     */
    public static void modifyingVolume(Context context, boolean isSilent) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            return;
        }
        if (isSilent) {
            previousMuteMode = am.getRingerMode();
            Log.i("来电", "静音" + previousMuteMode);
            //TODO 一加5T 来电静音只支持 am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            am.getStreamVolume(AudioManager.STREAM_RING);
        } else {
            Log.i("来电", "正常" + previousMuteMode);
            am.setRingerMode(previousMuteMode);
            am.getStreamVolume(AudioManager.STREAM_RING);
            previousMuteMode = -1;
        }
    }

}
