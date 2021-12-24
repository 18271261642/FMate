package com.example.xingliansdk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.example.xingliansdk.Config;
import com.example.xingliansdk.network.api.login.LoginBean;
import com.example.xingliansdk.utils.ContactsUtil;
import com.shon.connector.utils.TLog;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.BleWrite;

/**
 * 功能：短信接收广播监听器
 * 监听收到短信的
 */

public class SmsReminderReceive extends BroadcastReceiver implements BleWrite.MessageInterface {
    int sms =2;
    @Override
    public void onReceive(Context context, Intent intent) {
        LoginBean userInfo=Hawk.get(Config.database.USER_INFO,new LoginBean());

        if(userInfo==null||userInfo.getUserConfig()==null||userInfo.getUserConfig().getCallReminder().isEmpty())
            sms=  Hawk.get(Config.database.SMS,2);
        else
            sms=Integer.parseInt(userInfo.getUserConfig().getSmsReminder());
        if (sms != 2)
            return;
        Bundle bundle = intent.getExtras();
        SmsMessage message = null;
        if (null != bundle) {
            Object[] smsObject = (Object[]) bundle.get("pdus");
            if (smsObject == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Object object : smsObject) {
                message = SmsMessage.createFromPdu((byte[]) object);
                if (message != null) {
                    sb.append(message.getMessageBody());
                }
            }
            String number = message == null ? " " : message.getOriginatingAddress();
            String content = sb.toString();
            String contactName = ContactsUtil.lookForContacts(context, number);
            if (TextUtils.isEmpty(contactName)) {
                contactName = number;
            }
            pushSMS(contactName, content);
        }
    }

    /**
     * 推送短信
     *
     * @param name    姓名
     * @param content 内容
     */
    private void pushSMS(String name, String content) {
        TLog.Companion.error(String.format("短信++ name++%1$s  content++%2$s", name, content));
        if((content.length()+name.length())>56)
            BleWrite.writeMessageCall(1, name, content.substring(0,56-name.length()),this);
        else
        BleWrite.writeMessageCall(1, name, content,this);
    }

    @Override
    public void onResult() {

    }
}
