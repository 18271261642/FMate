package com.app.fmate.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.app.fmate.bean.MessageBean;
import com.app.fmate.service.reminder.SNNotificationPushHelper;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.BleWrite;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;
import java.util.List;

import static com.app.fmate.Config.database.MESSAGE_CALL;


/**
 * 辅助服务，在notification服务不起作用的情况下监听通知
 */

public class SNAccessibilityService extends AccessibilityService implements BleWrite.MessageInterface{

    @Override
    public void onCreate() {
        super.onCreate();
        TLog.Companion.error("通知监听:MyAccessibilityService#onCreate");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        try {
            CharSequence packageName = event.getPackageName();
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                List<CharSequence> msgList = event.getText();
                StringBuilder builder = new StringBuilder();
                for (CharSequence charSequence : msgList) {
                    builder.append(charSequence);
                }
                String content = builder.toString();
                 SNNotificationPushHelper.getInstance().handleMessage(SNNotificationPushHelper.TYPE_ACCESSIBILITY_SERVICE, packageName.toString(), null, content,this);
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        TLog.Companion.error("通知监听:MyAccessibilityService#onServiceConnected");
    }

    @Override
    public void onInterrupt() {
        TLog.Companion.error("通知监听:MyAccessibilityService#onInterrupt");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TLog.Companion.error("通知监听:MyAccessibilityService#onDestroy");
        SNNotificationPushHelper.getInstance().recycle();
    }


    @Override
    public void onResult() {
        ArrayList<MessageBean> messageList = Hawk.get(MESSAGE_CALL, new ArrayList<>());
        messageList.remove(0);
        Hawk.put(MESSAGE_CALL,messageList);
        if (messageList.size() > 0) {
            String mContent = messageList.get(0).getContent();
            String mTitle = messageList.get(0).getTitle();
            int mType = messageList.get(0).getType();
            if ((mContent.length() + mTitle.length()) >= 100) {
                if (mTitle.length() >= 100) {
                    BleWrite.writeMessageCall(mType, mTitle, "", this);
                } else {
                    BleWrite.writeMessageCall(mType, mTitle, mContent.substring(0, 100 - mTitle.length()), this);
                }
            } else {
                BleWrite.writeMessageCall(mType, mTitle, mContent, this);
            }
        }
    }
}
