package com.example.xingliansdk.broadcast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.example.xingliansdk.BuildConfig;
import com.example.xingliansdk.Config;
import com.example.xingliansdk.R;
import com.example.xingliansdk.network.api.login.LoginBean;
import com.example.xingliansdk.utils.ContactsUtil;
import com.shon.connector.utils.TLog;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.BleWrite;

/**
 * 功能：来电提醒广播接收器
 * 接收系统广播，获取来电手机号码
 */

public class CallReminderReceive extends BroadcastReceiver implements BleWrite.MessageInterface{
    int call=2;
    @Override
    public void onReceive(Context context, Intent intent) {
        String contactName;
        String action = intent.getAction();
        TLog.Companion.error("通话信息++"+intent.getAction().toString());
        LoginBean userInfo=Hawk.get(Config.database.USER_INFO,new LoginBean());

        if(userInfo==null||userInfo.getUserConfig()==null||userInfo.getUserConfig().getCallReminder().isEmpty())
        call=  Hawk.get(Config.database.INCOMING_CALL,2);
        else
            call=Integer.parseInt(userInfo.getUserConfig().getCallReminder());
      if (call!=2)
          return;
        if (action != null && !action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                int state = telephonyManager.getCallState();
                String callPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (callPhoneNumber != null){
                    contactName = ContactsUtil.lookForContacts(context, callPhoneNumber);
                }else {
                    contactName = context.getString(R.string.content_unknown_number);
                }
              TLog.Companion.log("查询联系人:" + contactName);
                if (!BuildConfig.isGooglePlayVersion&&TextUtils.isEmpty(callPhoneNumber)&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    Android P 之后, 读取来电号码需要请求READ_CALL_LOG权限,同时当前的onReceive会接收两次(详情看官网文档)
//                    第一次是null,第二次才会拿到电话号码, 于是凡是9.0+null 都return掉 等待第二次调用.
                    return;
//                    谷歌新规要求去掉READ_CALL_LOG 权限
//                  contactName = context.getString(R.string.content_call);
                }
                TLog.Companion.error("state++"+state);
                    switch (state) {
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            TLog.Companion.error("接电话");
                            BleWrite.writeIncomingCallCall(1);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                                ContactsUtil.rejectCall(context);
//                            }
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            TLog.Companion.error("来电提醒 ,发送电话号码");
                            BleWrite.writeMessageCall(0,contactName,context.getString(R.string.Incoming_call),this);
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            TLog.Companion.error("挂电话");
                            BleWrite.writeIncomingCallCall(2);
                            break;
                    }

            }

        }
    }

    @Override
    public void onResult() {

    }
}
