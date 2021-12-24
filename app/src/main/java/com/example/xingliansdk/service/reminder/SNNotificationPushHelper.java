package com.example.xingliansdk.service.reminder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Telephony;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.xingliansdk.BuildConfig;
import com.example.xingliansdk.XingLianApplication;
import com.example.xingliansdk.bean.MessageBean;
import com.example.xingliansdk.bean.RemindConfig;
import com.example.xingliansdk.utils.PermissionUtils;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.utils.TLog;
import com.example.xingliansdk.view.IF;
import com.google.gson.Gson;
import com.shon.LanguageUtil;
import com.shon.connector.BleWrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import static com.example.xingliansdk.Config.database.MESSAGE_CALL;

/**
 * 功能:通知监听助手
 */

public class SNNotificationPushHelper {
    public static final int TYPE_NONE = 0;
    public static final int TYPE_NOTIFICATION_LISTENER_SERVICE = 1;
    public static final int TYPE_ACCESSIBILITY_SERVICE = 2;
    private static volatile SNNotificationPushHelper instance = null;
    private boolean isRunning;
    private final LinkedBlockingDeque<Object[]> blockingDeque = new LinkedBlockingDeque<>();
    private int selectedType;
    private Thread thread;
    private String contentLast;

    private SNNotificationPushHelper() {
        startThreadRunning();
    }

    public static SNNotificationPushHelper getInstance() {
        if (instance == null) {
            synchronized (SNNotificationPushHelper.class) {
                if (instance == null) {
                    instance = new SNNotificationPushHelper();
                }
            }
        }
        return instance;
    }

    public void recycle() {
        isRunning = false;
        blockingDeque.clear();
    }

    public Thread getThread() {
        return thread;
    }

    public int getType() {
        return selectedType;
    }

    private void startThreadRunning() {
        if (!isRunning) {
            thread = new Thread() {
                public void run() {
                    isRunning = true;
                    while (isRunning) {
                        try {
                            Object[] take = blockingDeque.take();
                            if (take == null) continue;
                            selectedType = (int) take[0];
                            if (selectedType == TYPE_ACCESSIBILITY_SERVICE) {
                                boolean isNotificationServiceRunning = PermissionUtils.isServiceRunning(XingLianApplication.mXingLianApplication.getContext(), SNNotificationService.class);
                                if (isNotificationServiceRunning) {
                                    //因为辅助服务和通知监听服务都正常运行,所以不处理辅助服务的消息,优先处理通知监听 因为通知监听服务这个更稳定,也是谷歌推荐的
                                    continue;
                                }
                            }

                            String packageName = (String) take[1];
                            String title = (String) take[2];
                            String content = (String) take[3];
                            Context context = XingLianApplication.mXingLianApplication.getContext();
                            String defaultSmsAppPackageName = getDefaultSmsAppPackageName(context);
                            if (BuildConfig.isGooglePlayVersion && title != null && (packageName.equalsIgnoreCase("com.android.mms") || packageName.equalsIgnoreCase(defaultSmsAppPackageName))) {
                                PackageManager pm = context.getPackageManager();
                                CharSequence SMSAppName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));
                                if (!title.toLowerCase().contains(SMSAppName.toString().toLowerCase())) {
                                    sendMessageToDevice(1, title, content);
                                    return;
                                }
                            }

                            title = TextUtils.isEmpty(title) ? "" : title.trim();
                            content = TextUtils.isEmpty(content) ? "" : content.trim();

                            //去掉内容中重复出现的标题文本 导致占用内容文本空间
                            if (content.startsWith(title)) {
                                content = content.replace(title, "").trim();
                            }
                            //标题去掉表情
                            if (title.contains("[表情]")) {
                                title = title.replaceAll("\\[表情\\]", "").trim();
                            }
                            content = content.replaceAll("：", ":");
                            //去掉内容开始的冒号
                            if (content.startsWith(":")) {
                                try {
                                    content = content.substring(1, content.length()).trim();
                                } catch (Exception ignored) {
                                }
                            }
                            int indexOf = content.indexOf(":");
                            if (indexOf != -1) {
                                String content_title = content.substring(0, indexOf);
                                if (TextUtils.isEmpty(title)) {
                                    title = content_title;
                                }
                                if (title.startsWith(content_title)) {
                                    try {
                                        content = content.substring(indexOf + 1, content.length()).trim();
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                            RemindConfig remindConfig = new RemindConfig();

                            ArrayList<RemindConfig.Apps> nnList = (ArrayList<RemindConfig.Apps>) Hawk.get("RemindList", remindConfig.getRemindAppPushList());
                            for (RemindConfig.Apps app : nnList) {
                                if (app.isThisApp(packageName)) {
                                    boolean case1 = app != null && app.isOn();
                                    if (case1) {
                                        if (IF.isEmpty(title) || ":".equals(title)) {
                                            title = app.getAppName() + ":";
                                        }
                                        if (!title.trim().endsWith(":")) {
                                            title = title + ":";
                                        }
                                        if (IF.isEmpty(content)) {
                                            if (LanguageUtil.isZH()) {
                                                content = "你收到一条新的信息";
                                            } else {
                                                content = "You received a new content.";
                                            }
                                        }
                                        if (packageName.equals("com.tencent.mobileqq") ||
                                                packageName.equals("com.tencent.mm") ||
                                                packageName.equals("com.tencent.tim") ||
                                                packageName.equals("com.tencent.minihd.qq") ||
                                                packageName.equals("com.tencent.qqlite") ||
                                                packageName.equals("com.tencent.mobileqqi") ||
                                                packageName.equals("com.tencent.qq.kddi") ||
                                                packageName.equals("com.tencent.eim")
                                        ) {
                                            if (content.contains("正在呼叫你")) {
                                                content = "语音通话";
                                            }
                                            if (content.contains("视频通话")) {
                                                content = "视频通话";
                                            }
                                            if (content.contains("语音通话")) {
                                                content = "语音通话";
                                            }
                                            //如果需要过滤相同的就这个
//                                            if ((contentLast != null && !contentLast.trim().isEmpty() && content.equals(contentLast))) {
//                                                return;
//                                            }
                                        }
                                        contentLast = content;
                                        sendMessageToDevice(app.getType(), title, content);
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            TLog.Companion.error("e=="+e.toString());
                            e.printStackTrace();
                        }

                    }
                    isRunning = false;
                }


                private void sendMessageToDevice(int type, final String title, final String message) {
                    TLog.Companion.error(String.format("packName+=%1$s  :title+=%2$s ,message++%3$s", type, title, message));
                    ArrayList<MessageBean> messageList = Hawk.get(MESSAGE_CALL, new ArrayList<>());
                    messageList.add(new MessageBean(type, title, message));
                    Hawk.put(MESSAGE_CALL, messageList);
                    if (messageList.size() >= 1) {
                        String mContent = messageList.get(messageList.size()-1).getContent();
                        String mTitle = messageList.get(messageList.size()-1).getTitle();
                        int mType = messageList.get(messageList.size()-1).getType();
                        if ((mContent.length() + mTitle.length()) >= 100) {
                            if (mTitle.length() >= 100) {
                                BleWrite.writeMessageCall(mType, mTitle, "", mInterface);
                            } else {
                                BleWrite.writeMessageCall(mType, mTitle, mContent.substring(0, 100 - mTitle.length()), mInterface);
                            }
                        } else {
                            BleWrite.writeMessageCall(mType, mTitle, mContent, mInterface);
                        }
                    }
                }
            };
            thread.setPriority(Thread.MAX_PRIORITY - 1);
            thread.start();
        }
    }

    /**
     * 处理消息
     *
     * @param packageName 包名
     * @param message     内容
     */
    BleWrite.MessageInterface mInterface;

    public void handleMessage(int type, String packageName, String title, String message, BleWrite.MessageInterface mInterface) {
        blockingDeque.offer(new Object[]{type, packageName, title, message});
        this.mInterface = mInterface;
        if (thread == null || thread.getState() == Thread.State.TERMINATED) {
            thread = null;
            isRunning = false;
        }
        if (!isRunning) {
            startThreadRunning();
        }
    }

    public static String getDefaultSmsAppPackageName(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return Telephony.Sms.getDefaultSmsPackage(context);
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty())
                return resolveInfos.get(0).activityInfo.packageName;
            return null;
        }
    }




}
