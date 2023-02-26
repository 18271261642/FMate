package com.app.fmate.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.app.fmate.MainHomeActivity;
import com.app.fmate.TestActivity;
import com.app.fmate.bean.MapMotionBean;
import com.app.fmate.bean.SleepTypeBean;
import com.app.fmate.dfu.DFUActivity;
import com.app.fmate.dfu.GoodixDfuActivity;
import com.app.fmate.network.api.homeView.HomeCardVoBean;
import com.app.fmate.ui.BleConnectActivity;
import com.app.fmate.ui.bloodOxygen.BloodOxygenActivity;
import com.app.fmate.ui.BloodPressureActivity;
import com.app.fmate.ui.bp.BpHomeActivity;
import com.app.fmate.ui.bp.BpSettingActivity;
import com.app.fmate.ui.camera.CameraActivity;
import com.app.fmate.ui.device.BigDataIntervalActivity;
import com.app.fmate.ui.dial.CustomizeDialActivity;
import com.app.fmate.ui.dial.DialDetailsActivity;
import com.app.fmate.ui.dial.DialIndexActivity;
import com.app.fmate.ui.dial.DialMarketActivity;
//import com.example.xingliansdk.ui.fragment.map.RunningActivity;
import com.app.fmate.ui.fragment.map.RunningActivity;
import com.app.fmate.ui.fragment.map.newmap.AmapSportRecordActivity;
import com.app.fmate.ui.login.ForgetPasswordActivity;
import com.app.fmate.ui.login.GoalActivity;
import com.app.fmate.ui.login.LogOutActivity;
import com.app.fmate.ui.login.LogOutCodeActivity;
import com.app.fmate.ui.login.LoginActivity;
import com.app.fmate.ui.login.PasswordActivity;
import com.app.fmate.ui.login.SureLogOutActivity;
import com.app.fmate.ui.problemsFeedback.ProblemsFeedbackActivity;
import com.app.fmate.ui.setting.DoNotDisturbActivity;
import com.app.fmate.ui.device.ModuleMeasurementListActivity;
import com.app.fmate.ui.device.OtherSettingActivity;
import com.app.fmate.ui.device.ReminderPushListActivity;
import com.app.fmate.ui.deviceSport.DeviceSportChartActivity;
import com.app.fmate.ui.fragment.home.CardEditActivity;
import com.app.fmate.ui.heartrate.HeartRateActivity;
import com.app.fmate.ui.heartrate.RealTimeHeartRateActivity;
import com.app.fmate.ui.pressure.PressureActivity;
import com.app.fmate.ui.setting.AboutActivity;
import com.app.fmate.ui.setting.InfRemindActivity;
import com.app.fmate.ui.setting.MyDeviceActivity;
import com.app.fmate.ui.setting.SettingActivity;
import com.app.fmate.ui.setting.SleepGoalActivity;
import com.app.fmate.ui.setting.SportsGoalActivity;
import com.app.fmate.ui.setting.UnitActivity;
import com.app.fmate.ui.setting.account.AccountActivity;
import com.app.fmate.ui.setting.account.AppealActivity;
import com.app.fmate.ui.setting.account.BindNewPhoneActivity;
import com.app.fmate.ui.setting.account.FindPhoneMainActivity;
import com.app.fmate.ui.setting.account.PasswordCheckActivity;
import com.app.fmate.ui.setting.account.UpPasswordActivity;
import com.app.fmate.ui.setting.alarmClock.AlarmClockActivity;
import com.app.fmate.ui.setting.alarmClock.AlarmClockListActivity;
import com.app.fmate.ui.setting.DeviceInformationActivity;
import com.app.fmate.ui.setting.flash.FlashActivity;
import com.app.fmate.ui.setting.heartRateAlarm.HeartRateAlarmActivity;
import com.app.fmate.ui.setting.schedule.ScheduleActivity;
import com.app.fmate.ui.setting.schedule.ScheduleListActivity;
import com.app.fmate.ui.setting.SettingWeatherActivity;
import com.app.fmate.ui.setting.takeMedicine.TakeMedicineActivity;
import com.app.fmate.ui.setting.takeMedicine.TakeMedicineIndexActivity;
import com.app.fmate.ui.setting.takeMedicine.TakeMedicineRepeatActivity;
import com.app.fmate.ui.sleep.SleepDetailsActivity;
import com.app.fmate.ui.sleep.details.SleepNightActivity;
import com.app.fmate.ui.temp.TempActivity;
import com.app.fmate.ui.web.WebActivity;
import com.app.fmate.ui.weight.WeightActivity;
import com.shon.connector.utils.TLog;

import java.io.Serializable;

public class JumpUtil {

    /**
     * 跳转到  扫描链接ble界面
     */
    public static void startBleConnectActivity(Context context) {
        context.startActivity(new Intent(context, BleConnectActivity.class)
                //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * 跳转到  其他设置界面
     */
    public static void startOtherSettingActivity(Context context) {
        context.startActivity(new Intent(context, OtherSettingActivity.class)
        );
    }

    /**
     * 跳转到血压界面
     *
     * @param context 从哪个页面跳转
     */
//    public static void startMainHomeActivity(Context context, String address) {
//        context.startActivity(new Intent(context, MainHomeActivity.class)
//                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
//                .putExtra("address", address));
//    }
    public static void startMainHomeActivity(Context context) {
        context.startActivity(new Intent(context, MainHomeActivity.class)
        );
    }

    /**
     * 跳转到血压界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startBloodPressureActivity(Context context, HomeCardVoBean.ListDTO mList) {
        context.startActivity(new Intent(context, BloodPressureActivity.class)
                .putExtra("bean", mList));
    }


    public static void startNewBpActivity(Context context,HomeCardVoBean.ListDTO mList){
        context.startActivity(new Intent(context, BpHomeActivity.class).putExtra("bean",mList));
    }
    /**
     * 睡眠
     *
     * @param context
     */
    public static void startSleepDetailsActivity(Context context,HomeCardVoBean.ListDTO mList) {
        context.startActivity(new Intent(context, SleepDetailsActivity.class)
                 .putExtra("bean", mList)
        );
    }

    public static void startSleepNightActivity(Context context, SleepTypeBean bean) {
        context.startActivity(new Intent(context, SleepNightActivity.class)
                .putExtra("SleepType", bean)
        );
    }

    /**
     * 心率图表页面
     *
     * @param context
     * @param mList
     */
    public static void startHeartRateActivity(Context context, HomeCardVoBean.ListDTO mList, int type) {
        context.startActivity(new Intent(context, HeartRateActivity.class)
                .putExtra("HeartRate", mList)
                .putExtra("HistoryType", type)
        );
    }

    /**
     * 血氧图表
     *
     * @param context
     */
    public static void startBloodOxygenActivity(Context context, HomeCardVoBean.ListDTO mList) {
        context.startActivity(new Intent(context, BloodOxygenActivity.class).putExtra("bean", mList)
        );
    }

    /**
     * 压力
     */
    public static void startPressureActivity(Context context, HomeCardVoBean.ListDTO mList) {
        context.startActivity(new Intent(context, PressureActivity.class)
                .putExtra("bean", mList));
    }


    /**
     * 查看实时心率
     *
     * @param context
     */
    public static void startRealTimeHeartRateActivity(Context context) {
        context.startActivity(new Intent(context, RealTimeHeartRateActivity.class));
    }

    /**
     * 跳转到OTA升级界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startOTAActivity(Context context, String address, String name, String productNumber, int version
            , Boolean status) {
        context.startActivity(new Intent(context, DFUActivity.class)
                .putExtra("address", address)
                .putExtra("name", name)
                .putExtra("productNumber", productNumber)
                .putExtra("version", version)
                .putExtra("writeOTAUpdate", status)
        );

    }


    /**
     * 跳转到OTA升级界面汇顶平台
     *
     * @param context 从哪个页面跳转
     */
    public static void startGoodxOTAActivity(Context context, String address, String name, String productNumber, int version
            , Boolean status) {
        context.startActivity(new Intent(context, GoodixDfuActivity.class)
                .putExtra("address", address)
                .putExtra("name", name)
                .putExtra("productNumber", productNumber)
                .putExtra("version", version)
                .putExtra("writeOTAUpdate", status)
        );

    }


    /**
     * 跳转到设备设置界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startDeviceInformationActivity(Context context, boolean register) {
        context.startActivity(new Intent(context, DeviceInformationActivity.class)
                        .putExtra("register", register)
//                .putExtra("name", name)
        );
    }

    /**
     * 密码设置
     */
    public static void startPasswordActivity(Context context, String phone, String code, String areaCode, int type) {
        context.startActivity(new Intent(context, PasswordActivity.class)
                .putExtra("phone", phone)
                .putExtra("code", code)
                .putExtra("areaCode", areaCode)
                .putExtra("type", type)
        );
    }

    /**
     * 跳转到设备设置界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startMyDeviceActivity(Context context, int electricity) {
        context.startActivity(new Intent(context, MyDeviceActivity.class)
                .putExtra("electricity", electricity)
        );
    }


    /**
     * 跳转到设备设置界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startMyDeviceActivity(Context context, int electricity,int category) {
        context.startActivity(new Intent(context, MyDeviceActivity.class)
                .putExtra("electricity", electricity)
                .putExtra("category",category)
        );
    }


    /**
     * 跳转到测量状态模块
     *
     * @param context
     */
    public static void startModuleMeasurementListActivity(Context context) {
        context.startActivity(new Intent(context, ModuleMeasurementListActivity.class)
        );
    }


    /**
     * 跳转到消息提醒列表模块
     *
     * @param context
     */
    public static void startReminderPushListActivity(Context context) {
        context.startActivity(new Intent(context, ReminderPushListActivity.class)
        );
    }

    public static void startInfRemindActivity(Context context) {
        context.startActivity(new Intent(context, InfRemindActivity.class)
        );
    }

    /**
     * 跳转到设备设置界面
     */
    public static void startAlarmClockActivity(Context context, int type) {
        context.startActivity(new Intent(context, AlarmClockActivity.class)
                .putExtra("type", type)
        );
    }


    //设置页面跳转到血压设置页面
    public static void startToBpSetActivity(Context context){
        context.startActivity(new Intent(context, BpSettingActivity.class));
    }

    public static void startScheduleActivity(Context context) {
        context.startActivity(new Intent(context, ScheduleActivity.class)
        );
    }

    public static void startAlarmClockActivity(Context context, int type, int position) {
        context.startActivity(new Intent(context, AlarmClockActivity.class)
                .putExtra("type", type)
                .putExtra("update", position)
        );
    }

    public static void startScheduleActivity(Context context, int position) {
        context.startActivity(new Intent(context, ScheduleActivity.class)
                .putExtra("update", position)
        );
    }

    /**
     * 设置界面跳转到闹钟列表和日程列表
     *
     * @param context 从哪个页面跳转
     */
    public static void startAlarmClockListActivity(Context context) {
        context.startActivity(new Intent(context, AlarmClockListActivity.class)
        );
    }

    /**
     * 吃药提醒
     *
     * @param context 从哪个页面跳转
     */
    public static void startTakeMedicineIndexActivity(Context context) {
        context.startActivity(new Intent(context, TakeMedicineIndexActivity.class)
        );
    }

    /**
     * 添加吃药提醒
     *
     * @param context 从哪个页面跳转
     */
    public static void startTakeMedicineActivity(Context context) {
        context.startActivity(new Intent(context, TakeMedicineActivity.class)
        );
    }

    public static void startTakeMedicineActivity(Context context, int position) {
        context.startActivity(new Intent(context, TakeMedicineActivity.class)
                .putExtra("update", position)
        );
    }

    /**
     * @param context
     * @param type    提醒周期
     */
    public static void startTakeMedicineRepeatActivity(Context context, int type) {
        context.startActivity(new Intent(context, TakeMedicineRepeatActivity.class)
                .putExtra("ReminderPeriod", type)
        );
    }

    /**
     * 勿扰模式
     *
     * @param context
     */
    public static void startDoNotDisturbActivity(Context context) {
        context.startActivity(new Intent(context, DoNotDisturbActivity.class));
    }

    /**
     * 设置达标运动界面
     *
     * @param context
     */
    public static void startSportsGoalActivity(Context context) {
        context.startActivity(new Intent(context, SportsGoalActivity.class)
        );
    }

    /**
     * 设置达标睡眠界面
     *
     * @param context
     */
    public static void startSleepGoalActivity(Context context) {
        context.startActivity(new Intent(context, SleepGoalActivity.class)
        );
    }

    /**
     * 体温 温度
     */
    public static void startTempActivity(Context context, HomeCardVoBean.ListDTO mList) {
        context.startActivity(new Intent(context, TempActivity.class)
                .putExtra("bean", mList)
        );
    }

    /**
     * 体重
     */
    public static void startWeightActivity(Context context, HomeCardVoBean.ListDTO mList) {
        context.startActivity(new Intent(context, WeightActivity.class)
                .putExtra("bean", mList)
        );
    }

    /**
     * 单位设置
     *
     * @param context
     */
    public static void startUnitActivity(Context context) {
        context.startActivity(new Intent(context, UnitActivity.class));
    }

    /**
     * 账户与安全
     *
     * @param context
     */
    public static void startAccountActivity(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    public static void startUpPasswordActivity(Context context) {
        context.startActivity(new Intent(context, UpPasswordActivity.class));
    }

    public static void startFindPhoneActivity(Context context) {
        context.startActivity(new Intent(context, FindPhoneMainActivity.class));
    }

    public static void startBindNewPhoneActivity(Context context, String oldVerifyCode, String password) {
        context.startActivity(new Intent(context, BindNewPhoneActivity.class)
                .putExtra("oldVerifyCode", oldVerifyCode)
                .putExtra("password", password));
    }

    public static void startPasswordCheckActivity(Context context) {
        context.startActivity(new Intent(context, PasswordCheckActivity.class));
    }

    public static void startAppealActivity(Context context) {
        context.startActivity(new Intent(context, AppealActivity.class));
    }

    /**
     * 日程
     *
     * @param context
     */
    public static void startScheduleListActivity(Context context) {
        context.startActivity(new Intent(context, ScheduleListActivity.class)
        );

    }

    /**
     * 卡片编辑
     */
    public static void startCardEditActivity(Context context) {
        context.startActivity(new Intent(context, CardEditActivity.class)
        );

    }


    /**
     * 设置天气预报的跳转
     *
     * @param context
     */
    public static void startSettingWeatherActivity(Context context) {
        context.startActivity(new Intent(context, SettingWeatherActivity.class)
        );

    }

    /**
     * 查看数据间隔与修改
     */
    public static void startBigDataIntervalActivity(Context context) {
        context.startActivity(new Intent(context, BigDataIntervalActivity.class)
        );

    }

    /**
     * 地图
     */
    public static void startLocationMap(Context context, MapMotionBean mMapMotionBean) {
        context.startActivity(new Intent(context, RunningActivity.class)
                .putExtra("MapMotionBean", (Serializable) mMapMotionBean)
        );
    }

    /**
     * 重启app
     *
     * @param context
     */
    public static void restartApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (null == packageManager) {
            TLog.Companion.error("null == packageManager");
            return;
        }
        final Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    /**
     * 运动记录
     */
    public static void startExerciseRecordActivity(Context context) {
     //   context.startActivity(new Intent(context, ExerciseRecordActivity.class));
        context.startActivity(new Intent(context, AmapSportRecordActivity.class).putExtra("sportType",0));
    }


    /**
     * 设备运动记录
     *
     * @param context
     */
    public static void startDeviceSportChartActivity(Context context) {
        context.startActivity(new Intent(context, DeviceSportChartActivity.class));
    }

    /**
     * 关于
     */
    public static void startAboutActivity(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    /**
     * flash页面
     */
    public static void startFlashActivity(Context context) {
        context.startActivity(new Intent(context, FlashActivity.class));
    }

    /**
     * 登录页面
     */
    public static void startLoginActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * 忘记密码
     */
    public static void startForgetPasswordActivity(Context context) {
        context.startActivity(new Intent(context, ForgetPasswordActivity.class)
        );
    }

    /**
     * 忘记密码
     */
    public static void startForgetPasswordActivity(Context context,String phoneCode) {
        context.startActivity(new Intent(context, ForgetPasswordActivity.class).putExtra("phoneCode",phoneCode)
        );
    }

    /**
     * 注销账号
     */
    public static void startLogOutActivity(Context context) {
        context.startActivity(new Intent(context, LogOutActivity.class)
        );
    }

    /**
     * 注销账号 验证码
     */
    public static void startLogOutCodeActivity(Context context) {
        context.startActivity(new Intent(context, LogOutCodeActivity.class)
        );
    }

    /**
     * 注销账号 确认
     */
    public static void startSureLogOutActivity(Context context, String code) {
        context.startActivity(new Intent(context, SureLogOutActivity.class)
                .putExtra("code", code)
        );
    }

    /**
     * 心率报警
     */
    public static void startHeartRateAlarmActivity(Context context) {
        context.startActivity(new Intent(context, HeartRateAlarmActivity.class));
    }

    /**
     * 第一次进入页面进行设置
     */
    public static void startGoalActivity(Context context) {
        context.startActivity(new Intent(context, GoalActivity.class));
    }

    public static void startSettingActivity(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    /**
     * 测试
     */
    public static void startTest(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    public static void startWeb(Context context, String url) {
        context.startActivity(new Intent(context, WebActivity.class)
                .putExtra("url", url));

    }

    public static void startProblemsFeedbackActivity(Context context) {
        context.startActivity(new Intent(context, ProblemsFeedbackActivity.class));
    }


    /**
     * 表盘
     */
    public static void startDialMarketActivity(Context context) {
        context.startActivity(new Intent(context, DialMarketActivity.class));
    }

    /**
     * 表盘列表
     *
     * @param context
     */
    public static void startDialIndexActivity(Context context, int type, String productNumber, String typeName) {
        context.startActivity(new Intent(context, DialIndexActivity.class)
                .putExtra("type", type)
                .putExtra("productNumber", productNumber)
                .putExtra("typeName", typeName));
    }

    /**
     * 表盘
     */
    public static void startDialDetailsActivity(Context context, String bean,int position) {
        context.startActivity(new Intent(context, DialDetailsActivity.class)
                .putExtra("TypeList", bean)
                .putExtra("position",position)
        );
    }

    /**
     * 自定义表盘
     *
     * @param context
     */
    public static void startCustomizeDialActivity(Context context) {
        context.startActivity(new Intent(context, CustomizeDialActivity.class));
    }
    public static void startCustomizeDialActivity(Context context,String data) {
        context.startActivity(new Intent(context, CustomizeDialActivity.class)
        .putExtra("data",data)
        );
    }

    /**
     * 远程拍照
     */
    public static void startCameraActivity(Context context) {
        context.startActivity(new Intent(context, CameraActivity.class));
    }



}
