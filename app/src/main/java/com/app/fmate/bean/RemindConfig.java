package com.app.fmate.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 功能:消息推送设置
 */
public class RemindConfig implements Serializable {


    /**
     * 防丢提醒
     */
    private boolean remindLost = false;

    /**
     * 来电
     */
    private boolean remindCall = true;

    /**
     * 短信
     */
    private boolean remindSMS = true;

    /**
     * 应用推送提醒设置
     */
    private boolean remindAppPush = true;


    /**
     * 总应用推送列表
     */
    public List<Apps> remindAppPushList = new ArrayList<Apps>() {
        {
            add(new Apps("Email", Arrays.asList(
                    "com.microsoft.office.outlook",//Outlook
                    "com.google.android.gm",//Gmail
                    "com.google.android.email",//谷歌安卓自带邮件
                    "com.samsung.android.email.provider",//Samsung Email
                    "com.yahoo.mobile.client.android.mail",//Yahoo Email
                    "com.tencent.androidqqmail",//QQ邮箱
                    "com.netease.mobimail",//网易邮箱
                    "cn.cj.pe"//139邮箱
            ), "file:///android_asset/icon_email_reminder.png", true, 3));

            add(new Apps("FaceBook", Arrays.asList("com.facebook.katana", "com.facebook.orca"), "file:///android_asset/icon_facebook_reminder.png", true, 4));
            add(new Apps("Wechat", Arrays.asList("com.tencent.mm"), "file:///android_asset/icon_wechat_reminder.png", true, 5));
            add(new Apps("Line", Arrays.asList("jp.naver.line.android", "com.linecorp.linelite", "com.linecorp.lineat.android"), "file:///android_asset/icon_line_reminder.png", true, 6));
            add(new Apps("Weibo", Arrays.asList("com.sina.weibo"), "file:///android_asset/icon_weibo_reminder.png", true, 7));
            add(new Apps("Linkedln", Arrays.asList("com.linkedin.android"), "file:///android_asset/icon_linkedln_reminder.png", true, 8));
            add(new Apps("QQ", Arrays.asList("com.tencent.mobileqq", "com.tencent.tim", "com.tencent.minihd.qq", "com.tencent.qqlite", "com.tencent.mobileqqi", "com.tencent.qq.kddi", "com.tencent.eim"), "file:///android_asset/icon_qq_reminder.png", true, 9));
            add(new Apps("WhatsApp", Arrays.asList("com.whatsapp"), "file:///android_asset/icon_whatsapp_reminder.png", true, 10));
            add(new Apps("Viber", Arrays.asList("com.viber.voip"), "file:///android_asset/icon_viber_reminder.png", true, 11));
            add(new Apps("Instagram", Arrays.asList("com.instagram.android"), "file:///android_asset/icon_instagram_reminder.png", true, 12));
//            Hawk.put("RemindList", remindAppPushList);
//            TLog.Companion.error("什么情况");
        }

    };


    /**
     * 通过包名寻找推送的app对象
     *
     * @param packageName
     * @return
     */
    public Apps findRemindAppPush(String packageName) {
        List<Apps> remindAppPushList = getRemindAppPushList();
        for (Apps apps : remindAppPushList) {
            if (apps.isThisApp(packageName)) {
                return apps;
            }
        }
        return null;
    }

    public List<Apps>
    getRemindAppPushList() {

        return remindAppPushList;
    }

    public void setRemindAppPushList(List<Apps> remindAppPushList) {
        this.remindAppPushList = remindAppPushList;
    }

    public boolean isRemindCall() {
        return remindCall;
    }

    public void setRemindCall(boolean remindCall) {
        this.remindCall = remindCall;
    }

    public boolean isRemindLost() {
        return remindLost;
    }

    public void setRemindLost(boolean remindLost) {
        this.remindLost = remindLost;
    }

    public boolean isRemindSMS() {
        return remindSMS;
    }

    public void setRemindSMS(boolean remindSMS) {
        this.remindSMS = remindSMS;
    }

    public boolean isRemindAppPush() {
        return remindAppPush;
    }

    public void setRemindAppPush(boolean remindAppPush) {
        this.remindAppPush = remindAppPush;
    }

    /**
     * 应用推送
     */
    public class Apps implements Serializable {

        /**
         * 应用包名(为什么是list? 因为有多开 或国内/国际版)
         */
        private List<String> packageNames = new ArrayList<>();
        private String appName;
        private String appIconFile;
        private boolean on;
        private int type;

//        public Apps(String appName, String packageName,String appIconFile, boolean on,int type) {
//            this.appIconFile = appIconFile;
//            this.packageNames.clear();
//            this.packageNames.add(packageName);
//            this.appName = appName;
//            this.on = on;
//            this.type=type; //协议
//        }

        public Apps(String appName, List<String> packageName, String appIconFile, boolean on, int type) {
            this.appIconFile = appIconFile;
            this.packageNames.clear();
            this.packageNames.addAll(packageName);
            this.appName = appName;
            this.on = on;
            this.type = type;
        }

        public List<String> getPackageNames() {
            return packageNames;
        }


        public String getAppIconFile() {
            return appIconFile;
        }


        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        /**
         * 是这个app吗
         *
         * @param packageName
         * @return
         */
        public boolean isThisApp(String packageName) {
            return this.packageNames.contains(packageName);
        }

        public String getAppName() {
            return appName;
        }


        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }
    }
}
