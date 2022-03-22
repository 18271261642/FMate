package com.shon.connector;

import android.os.Handler;
import android.os.Looper;

import com.shon.bluetooth.core.call.WriteCall;
import com.shon.connector.bean.BloodOxygenBean;
import com.shon.connector.bean.DailyActiveBean;
import com.shon.connector.bean.DataBean;
import com.shon.connector.bean.DeviceInformationBean;
import com.shon.connector.bean.DialCustomBean;
import com.shon.connector.bean.PressureBean;
import com.shon.connector.bean.PushBean;
import com.shon.connector.bean.RemindTakeMedicineBean;
import com.shon.connector.bean.SleepBean;
import com.shon.connector.bean.SpecifySleepSourceBean;
import com.shon.connector.bean.TimeBean;
import com.shon.connector.call.write.bigdataclass.BigDataHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyApneaHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyBloodOxygenHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyBloodPressureHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyDailyActivitiesHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyHeartRateHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyRRHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifySleepHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifySleepSourceCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifySportsHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyStressFatigueHistoryCall;
import com.shon.connector.call.write.bigdataclass.Specify.SpecifyTemperatureHistoryCall;
import com.shon.connector.call.write.bigdataclass.effective.EffectiveBloodPressureCall;
import com.shon.connector.call.write.controlclass.BloodOxygenSwitchCall;
import com.shon.connector.call.write.controlclass.CameraSwitchCall;
import com.shon.connector.call.write.controlclass.DisconnectBluetoothShutdownCall;
import com.shon.connector.call.write.controlclass.DoNotDisturbModeSwitchCall;
import com.shon.connector.call.write.controlclass.FindDeviceSwitchCall;
import com.shon.connector.call.write.controlclass.HeartRateAlarmSwitchCall;
import com.shon.connector.call.write.controlclass.ReminderDrinkWaterCall;
import com.shon.connector.call.write.controlclass.ReminderSedentaryCall;
import com.shon.connector.call.write.controlclass.SwitchCall;
import com.shon.connector.call.write.controlclass.IncomingCallCall;
import com.shon.connector.call.write.controlclass.OTAUpdateCall;
import com.shon.connector.call.write.controlclass.FactoryRestorationResetCall;
import com.shon.connector.call.write.controlclass.TurnOnScreenCall;
import com.shon.connector.call.write.deviceclass.DeviceBloodPressureCall;
import com.shon.connector.call.write.deviceclass.DeviceDeviceBigDataIntervalCall;
import com.shon.connector.call.write.deviceclass.DeviceFirmwareCall;
import com.shon.connector.call.write.deviceclass.DeviceModuleMeasurement;
import com.shon.connector.call.write.deviceclass.DeviceMotionCall;
import com.shon.connector.call.write.deviceclass.DevicePropertiesCall;
import com.shon.connector.call.write.deviceclass.DeviceReminderPushCall;
import com.shon.connector.call.write.bigdataclass.GetBloodPressureCall;
import com.shon.connector.call.write.bigdataclass.GetSpecifyMotionCall;
import com.shon.connector.call.write.controlclass.AutomaticBloodPressureMeasurementSwitchCall;
import com.shon.connector.call.write.controlclass.AutomaticHeartOxygenMeasurementSwitchCall;
import com.shon.connector.call.write.controlclass.AutomaticMeasurementSwitchCall;
import com.shon.connector.call.write.controlclass.AutomaticRRMeasurementSwitchCall;
import com.shon.connector.call.write.controlclass.BloodPressureSwitchCall;
import com.shon.connector.call.write.controlclass.TemperatureSwitchCall;
import com.shon.connector.call.write.deviceclass.FlashGetFeaturesCall;
import com.shon.connector.call.write.deviceclass.UUIDBindCall;
import com.shon.connector.call.write.dial.DialDesignatedCall;
import com.shon.connector.call.write.dial.DialGetAssignCall;
import com.shon.connector.call.write.dial.DialWriteAssignCall;
import com.shon.connector.call.write.flash.FlashErasureAssignCall;
import com.shon.connector.call.write.flash.FlashWriteAssignCall;
import com.shon.connector.call.write.messagereminder.MessageCall;
import com.shon.connector.call.write.settingclass.AlarmClockScheduleCall;
import com.shon.connector.call.write.settingclass.BloodPressureCalibrationCall;
import com.shon.connector.call.write.settingclass.CommonWriteCall;
import com.shon.connector.call.write.settingclass.DeviceInformationCall;
import com.shon.connector.call.write.settingclass.RemindTakeMedicineCall;
import com.shon.connector.call.write.settingclass.SettingUIDCall;
import com.shon.connector.call.write.settingclass.SportsUploadModeCall;
import com.shon.connector.call.write.settingclass.StorageIntervalCall;
import com.shon.connector.call.write.settingclass.TestWeatherCall;
import com.shon.connector.call.write.settingclass.TimeCall;
import com.shon.connector.call.write.settingclass.WeatherCall;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * 本类属于写入操作
 */
public class BleWrite {
    //数据库提取
    public static String address = "";
    static int time = 12_000;

    public BleWrite() {
    }

    public interface FirmwareInformationInterface {
        void onResult(String productNumber, String versionName,int version, String nowMaC, String mac);
    }

    /**
     * 获取固件信息
     */
    public static void writeForGetFirmwareInformation(FirmwareInformationInterface mFirmwareInformationInterface, boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DeviceFirmwareCall(address, mFirmwareInformationInterface));
//        WriteCall write = new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DeviceFirmwareCall(address, mFirmwareInformationInterface));

    }

    public interface DeviceBloodPressureInterface {
        void onResult(String value, String value1);
    }

    /**
     * 获取设备血压校准值
     */
    public static void writeForGetDeviceBloodPressure(DeviceBloodPressureInterface mDeviceBloodPressureInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DeviceBloodPressureCall(address, mDeviceBloodPressureInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DeviceBloodPressureCall(address, mDeviceBloodPressureInterface));
    }


    public interface DeviceDeviceBigDataIntervalInterface {

        void onResult(DataBean mDataBean);
    }

    /**
     * 获取大数据存储
     */
    public static void writeForGetDeviceBigDataInterval(DeviceDeviceBigDataIntervalInterface mDeviceBigDataInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new DeviceDeviceBigDataIntervalCall(address, mDeviceBigDataInterface));
    }

    public interface DeviceMotionInterface {
        void DeviceMotionResult(DataBean mDataBean);
    }

    /**
     * 获取设备实施运动
     *
     * @param mDeviceMotionInterface
     */
    public static void writeForGetDeviceMotion(DeviceMotionInterface mDeviceMotionInterface, Boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DeviceMotionCall(address, mDeviceMotionInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DeviceMotionCall(address, mDeviceMotionInterface));
    }

    public interface DeviceModuleMeasurementInterface {
        void DeviceModuleMeasurementResult(DataBean mDataBean);
    }

    /**
     * @param uuid       AndroidId
     * @param mInterface
     */
    public static void writeUUIDBind(String uuid, UUIDBindInterface mInterface) {
        WriteCall write = new WriteCall(address);
//        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new UUIDBindCall(address, uuid, mInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new UUIDBindCall(address, uuid, mInterface));
    }

    public interface UUIDBindInterface {
        void UUIDBindResult(int key);
    }

    public static void writeSettingUID() {
        WriteCall write = new WriteCall(address);
//    write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new SettingUIDCall(address));

    }

    /**
     * 设备模块测量信息
     * @param mDeviceModuleMeasurementInterface
     */
    public static void writeForGetDeviceModuleMeasurement(DeviceModuleMeasurementInterface mDeviceModuleMeasurementInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new DeviceModuleMeasurement(address, mDeviceModuleMeasurementInterface));
    }

    public interface DevicePropertiesInterface {
        void DevicePropertiesResult(Integer electricity, int mCurrentBattery, int mDisplayBattery, int type);
    }

    /**
     * 获取设备主要信息
     *
     * @param mDevicePropertiesInterface
     */
    public static void writeForGetDeviceProperties(DevicePropertiesInterface mDevicePropertiesInterface, boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DevicePropertiesCall(address, mDevicePropertiesInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DevicePropertiesCall(address, mDevicePropertiesInterface));
    }

    public interface DeviceReminderPushInterface {
        void onResult(PushBean mPushBean);
    }

    /**
     * 推送提醒类 ios 用跟Android没关系
     * @param mDeviceReminderPushInterface
     */
    public static void writeForGetDeviceReminderPush(DeviceReminderPushInterface mDeviceReminderPushInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new DeviceReminderPushCall(address, mDeviceReminderPushInterface));
    }

    public interface AutomaticBloodPressureMeasurementSwitchCallInterface {
        void AutomaticBloodPressureMeasurementSwitchCallResult(String rest);
    }

    public static void writeAutomaticBloodPressureMeasurementSwitchCall
            (byte[] keyValue, AutomaticBloodPressureMeasurementSwitchCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new AutomaticBloodPressureMeasurementSwitchCall(address, keyValue, mInterface));
    }

    public interface AutomaticHeartOxygenMeasurementSwitchInterface {
        void AutomaticHeartOxygenMeasurementSwitchResult(String rest);
    }

    public static void writeAutomaticHeartOxygenMeasurementSwitchCall
            (byte[] keyValue, AutomaticHeartOxygenMeasurementSwitchInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new AutomaticHeartOxygenMeasurementSwitchCall(address, keyValue, mInterface));
    }


    public interface AutomaticMeasurementSwitchInterface {
        void AutomaticMeasurementSwitchResult(String rest);
    }

    public static void writeAutomaticMeasurementSwitchCall
            (byte keyValue, DataBean dataBean/*, AutomaticMeasurementSwitchInterface mInterface*/) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new AutomaticMeasurementSwitchCall(address, keyValue, dataBean/*, mInterface*/));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new AutomaticMeasurementSwitchCall(address, keyValue, dataBean/*, mInterface*/));
    }

    public interface AutomaticRRMeasurementSwitchInterface {
        void AutomaticHeartRateMeasurementSwitchResult(String rest);
    }

    public static void writeAutomaticRRMeasurementSwitchCall
            (byte[] keyValue, AutomaticMeasurementSwitchInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new AutomaticRRMeasurementSwitchCall(address, keyValue, mInterface));
    }


//    public interface GetBloodOxygenInterface {
//        void GetBloodOxygenInterface(ArrayList<BloodOxygenBean> mList);
//    }
//
//    public static void writeGetBloodOxygenCall
//            (GetBloodOxygenInterface mInterface) {
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new GetBloodOxygenCall(address, mInterface));
//    }

    public interface GetBloodPressureInterface {
        void GetBloodPressureInterface(ArrayList<BloodOxygenBean> mList);
    }

    public static void GetBloodPressureCall
            (GetBloodPressureInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new GetBloodPressureCall(address, mInterface));
    }

    public interface GetSpecifyMotionInterface {
        void GetSpecifyMotionInterface(ArrayList<DataBean> mList);
    }

    public static void writeGetSpecifyMotionCall
            (GetSpecifyMotionInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new GetSpecifyMotionCall(address, mInterface));
    }


    /**
     * 控制类指令
     **/

    public static void writeFactoryRestorationResetCall() {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new FactoryRestorationResetCall(address));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new FactoryRestorationResetCall(address));
    }

    public interface BleInterface {
        void onResult();
    }

    /**
     * 心率报警开关
     * @param num
     */
    public static void writeHeartRateAlarmSwitchCall(int mSwitch,int num) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new HeartRateAlarmSwitchCall(address,mSwitch,num));

    }

    public static void writeOTAUpdateCall() {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new OTAUpdateCall(address));
    }

    public static void writeOTAUpdateCall(BleInterface mInterface) {

        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new OTAUpdateCall(address,mInterface));
    }

    public static void writeDisconnectBluetoothShutdownCall() {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DisconnectBluetoothShutdownCall(address));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DisconnectBluetoothShutdownCall(address));
    }

    public static void writeHeartRateSwitchCall(byte key, byte type) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SwitchCall(address, key, type));
    }

    public static void writeBloodOxygenSwitchCall(TimeBean mTimeBean) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new BloodOxygenSwitchCall(address, mTimeBean));
    }

    public interface BloodPressureSwitchInterface {
        void BloodPressureSwitchInterface(TimeBean rest);
    }

    public static void writeBloodPressureSwitchCall
            (int keyValue, TimeBean mTimeBean, BloodPressureSwitchInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new BloodPressureSwitchCall(address, keyValue, mInterface));
    }

    public interface TemperatureSwitchCallInterface {
        void TemperatureSwitchCallResult(DataBean mDataBean);
    }

    public static void writeTemperatureSwitchCall(byte keyValue, TemperatureSwitchCallInterface mTemperatureSwitchCallInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new TemperatureSwitchCall(address, keyValue, mTemperatureSwitchCallInterface));
    }

    public interface FindDeviceSwitchCallInterface {
        void FindDeviceSwitchCallResult(DataBean mDataBean);
    }

    public static void writeFindDeviceSwitchCall(byte keyValue) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new FindDeviceSwitchCall(address, keyValue));

    }

    public static void writeFindDeviceSwitchCall(byte keyValue, FindDeviceSwitchCallInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new FindDeviceSwitchCall(address, keyValue, mInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new FindDeviceSwitchCall(address, keyValue, mInterface));
    }

    public interface DoNotDisturbModeSwitchCallInterface {
        void DoNotDisturbModeSwitchCallResult();
    }

    public static void writeDoNotDisturbModeSwitchCall(TimeBean timeBean, DoNotDisturbModeSwitchCallInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DoNotDisturbModeSwitchCall(address, timeBean, mInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DoNotDisturbModeSwitchCall(address, timeBean, mInterface));
    }

    public interface CameraSwitchCallInterface {
        void CameraSwitchCallResult();
    }

    public static void writeCameraSwitchCall(byte key, CameraSwitchCallInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new CameraSwitchCall(address, key, mInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new CameraSwitchCall(address, key, mInterface));
    }
    public static void writeCameraSwitchCall(byte key ) {
        WriteCall write = new WriteCall(address);
//        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new CameraSwitchCall(address, key));

    }

    public interface IncomingCallCallInterface {
        void IncomingCallCallResult();
    }

    public static void writeIncomingCallCall(int key) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new IncomingCallCall(address, (byte) key));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new IncomingCallCall(address, (byte) key));
    }

    public static void writeTurnOnScreenCall(byte key) {
//        WriteCall write = new WriteCall(address);
//       // write.setPriority(true);
//        write.setServiceUUid(Config.serviceUUID);
//        write.setCharacteristicUUID(Config.mWriteCharacter);
//        write.enqueue(new TurnOnScreenCall(address, key));
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new TurnOnScreenCall(address, key));
    }

    public interface HistoryCallInterface {
        void HistoryCallResult(byte key, ArrayList<TimeBean> mList);
    }

    public static void writeReminderDrinkWaterCall(TimeBean timeBean) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new ReminderDrinkWaterCall(address, timeBean));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new ReminderDrinkWaterCall(address, timeBean));
    }

    public static void writeReminderSedentaryCall(TimeBean timeBean) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new ReminderSedentaryCall(address, timeBean));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new ReminderSedentaryCall(address, timeBean));
    }

    public static void writeBigDataHistoryCall(byte key, HistoryCallInterface mInterface) {
//        new Handler( Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setTimeout(30000)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new BigDataHistoryCall(address, key, mInterface));
//            }
//        },500);
    }
    public static void writeBigDataHistoryCall(byte key, HistoryCallInterface mInterface,boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setTimeout(30000);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new BigDataHistoryCall(address,key, mInterface));
    }

    public interface SpecifyDailyActivitiesHistoryCallInterface {
        void SpecifyDailyActivitiesHistoryCallResult(long startTime, long endTime, ArrayList<DailyActiveBean> mList);

        void SpecifyDailyActivitiesHistoryCallResult(ArrayList<DailyActiveBean> mList);
//        void SpecifyStepHistoryCallResult(ArrayList<SleepBean> mList);
    }

    public static void writeSpecifyDailyActivitiesHistoryCall(long startTime, long endTime, SpecifyDailyActivitiesHistoryCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setTimeout(15000)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SpecifyDailyActivitiesHistoryCall(address, startTime, endTime, mInterface));

    }
    public static void writeSpecifyDailyActivitiesHistoryCall(long startTime, long endTime, SpecifyDailyActivitiesHistoryCallInterface mInterface,boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setTimeout(30000);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new SpecifyDailyActivitiesHistoryCall(address,  startTime, endTime, mInterface));


    }

    public interface SpecifySleepHistoryCallInterface {
        void SpecifySleepHistoryCallResult(long startTime, long endTime, ArrayList<SleepBean> mList, SleepBean bean);

        void SpecifySleepHistoryCallResult(ArrayList<SleepBean> mList);

    }

    public static void writeSpecifySleepHistoryCall(long startTime, long endTime, SpecifySleepHistoryCallInterface mInterface,boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.setTimeout(30000);
        write.enqueue(new SpecifySleepHistoryCall(address,  startTime, endTime, mInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .setTimeout(30000)
//                .enqueue(new SpecifySleepHistoryCall(address, Time, endTime, mInterface));

    }
    public static void writeSpecifySleepHistoryCall(long Time, long endTime, SpecifySleepHistoryCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .setTimeout(30000)
                .enqueue(new SpecifySleepHistoryCall(address, Time, endTime, mInterface));

    }

    public interface SpecifyHeartRateHistoryCallInterface {
        void SpecifyHeartRateHistoryCallResult(long startTime, long endTime, ArrayList<Integer> mList);

        void SpecifyHeartRateHistoryCallResult(ArrayList<Integer> mList);
    }

    public static void writeSpecifyHeartRateHistoryCall(long Time, long endTime, SpecifyHeartRateHistoryCallInterface mInterface) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                new WriteCall(address)
                        .setServiceUUid(Config.serviceUUID)
                        .setCharacteristicUUID(Config.mWriteCharacter)
                        .setTimeout(30000)  //先定个4分钟来排查吧
                        .enqueue(new SpecifyHeartRateHistoryCall(address, Time, endTime, mInterface));
            }
        }, 200);

    }

    public static void writeSpecifyHeartRateHistoryCall(long Time, long endTime, SpecifyHeartRateHistoryCallInterface mInterface,boolean status) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            WriteCall write = new WriteCall(address);
            write.setPriority(status);
            write.setTimeout(30000);
            write.setServiceUUid(Config.serviceUUID);
            write.setCharacteristicUUID(Config.mWriteCharacter);
            write.enqueue(new SpecifyHeartRateHistoryCall(address,  Time, endTime, mInterface));

        }, 200);

    }


    public static void writeSpecifyTemperatureHistoryCall(long Time, long endTime, SpecifyTemperatureHistoryCallInterface mInterface) {
        writeSpecifyTemperatureHistoryCall(Time,endTime,mInterface,false);
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                new WriteCall(address)
//                        .setServiceUUid(Config.serviceUUID)
//                        .setCharacteristicUUID(Config.mWriteCharacter)
//                        .setTimeout(30000)  //先定个4分钟来排查吧
//                        .enqueue(new SpecifyTemperatureHistoryCall(address, Time, endTime, mInterface));
//            }
//        }, 200);

    }

    public static void writeSpecifyTemperatureHistoryCall(long Time, long endTime, SpecifyTemperatureHistoryCallInterface mInterface,boolean status) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            WriteCall write = new WriteCall(address);
            write.setPriority(status);
            write.setTimeout(30000);
            write.setServiceUUid(Config.serviceUUID);
            write.setCharacteristicUUID(Config.mWriteCharacter);
            write.enqueue(new SpecifyTemperatureHistoryCall(address,  Time, endTime, mInterface));
        }, 200);

    }

    public interface SpecifyTemperatureHistoryCallInterface {
        void SpecifyTemperatureHistoryCallResult(long startTime, long endTime, ArrayList<Integer> mList);

    }


    public interface SpecifyBloodOxygenHistoryCallInterface {
        void SpecifyBloodOxygenHistoryCallResult(long startTime, long endTime, ArrayList<Integer> mList);

        void SpecifyBloodOxygenHistoryCallResult(ArrayList<Integer> mList);
    }

    public static void writeSpecifyBloodOxygenHistoryCall(long Time, long endTime, SpecifyBloodOxygenHistoryCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setTimeout(30000)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SpecifyBloodOxygenHistoryCall(address, Time, endTime, mInterface));

    }
    public static void writeSpecifyBloodOxygenHistoryCall(long Time, long endTime, SpecifyBloodOxygenHistoryCallInterface mInterface,boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setTimeout(30000);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new SpecifyBloodOxygenHistoryCall(address,  Time, endTime, mInterface));
    }


    public interface SpecifyApneaHistoryCallCallInterface {
        void SpecifyApneaHistoryCallResult(long startTime, long endTime, ArrayList<TimeBean> mList);
    }

    public static void writeSpecifyApneaHistoryCall(long Time, long endTime, SpecifyApneaHistoryCallCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SpecifyApneaHistoryCall(address, Time, endTime, mInterface));

    }

    public interface SpecifyBloodPressureHistoryCallInterface {
        void SpecifyBloodPressureHistoryCallResult(long startTime, long endTime, ArrayList<DataBean> mList);
    }

    public static void writeSpecifyBloodPressureHistoryCall(long Time, long endTime, SpecifyBloodPressureHistoryCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SpecifyBloodPressureHistoryCall(address, Time, endTime, mInterface));

    }

    public interface SpecifyRRHistoryCallInterface {
        void SpecifyRRHistoryCallResult(long startTime, long endTime, ArrayList<Integer> mList);
    }

    public static void writeSpecifyRRHistoryCall(long Time, long endTime, SpecifyRRHistoryCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SpecifyRRHistoryCall(address, Time, endTime, mInterface));

    }

    public interface SpecifyStressFatigueHistoryCallInterface {
        void SpecifyStressFatigueHistoryCallResult(long startTime, long endTime, ArrayList<PressureBean> mList);
    }

    public static void writeSpecifyStressFatigueHistoryCall(long Time, long endTime, SpecifyStressFatigueHistoryCallInterface mInterface) {
        writeSpecifyStressFatigueHistoryCall(Time,endTime,mInterface,false);
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .setTimeout(30000)
//                .enqueue(new SpecifyStressFatigueHistoryCall(address, Time, endTime, mInterface));

    }
    public static void writeSpecifyStressFatigueHistoryCall(long Time, long endTime, SpecifyStressFatigueHistoryCallInterface mInterface,boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setTimeout(30000);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new SpecifyStressFatigueHistoryCall(address,  Time, endTime, mInterface));

    }


    public interface SpecifySportsHistoryCallInterface {
        void SpecifySportsHistoryCallCallResult(long startTime, long endTime, ArrayList<DataBean> mList);
    }

    public static void writeSpecifySportsHistoryCall(long Time, long endTime, SpecifySportsHistoryCallInterface mInterface) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SpecifySportsHistoryCall(address, Time, endTime, mInterface));

    }






    public static void writeSpecifySleepSourceCall(byte[] bytes,boolean status,long startrTime,long endTime,SpecifySleepSourceInterface specifySleepSourceInterface){
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setTimeout(30000);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new SpecifySleepSourceCall(address,specifySleepSourceInterface,startrTime,endTime,bytes));
    }






    /******************************************设置类****************************/

    /**
     * 3.6.1 APP 端设置设备时间
     *
     * @param mTimeBean year            年
     *                  month           月
     *                  day             日
     *                  hours           时
     *                  min             分
     *                  ss              秒
     */
    public static void writeTimeCall(TimeBean mTimeBean) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new TimeCall(address, mTimeBean));

    }

    public static void writeTimeCall(TimeBean mTimeBean, TimeCallInterface mInterface, Boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new TimeCall(address, mTimeBean, mInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new TimeCall(address, mTimeBean, mInterface));

    }

    public interface TimeCallInterface {
        void TimeCall();
    }

    /**
     * 3.6.2 APP 端设置设备基本信息
     *
     * @param mBean sex                性别  0x01:男  0x02:女
     *              age                年龄
     *              height             身高
     *              weight             体重
     *              language           系统语言
     *              timeSystem         时间制式  0x00:24小时 0x01:12小时
     *              android            单位制式  0x00:公制  0x01:英制
     *              unitSystem         系统选择  1安卓(我们只是Android其他不管,你填不填我都默认为1)
     *              wearHands          佩戴手  0 左手  0x01右手
     *              temperatureSystem  温度制式  0x00:摄氏度  0x01:华摄氏度
     *              exerciseSteps      运动达标步数
     */
    public static void writeDeviceInformationCall(DeviceInformationBean mBean, DeviceInformationCallInterface mInterface,Boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DeviceInformationCall(address, mBean, mInterface));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DeviceInformationCall(address, mBean, mInterface));

    }

    public static void writeDeviceInformationCall(DeviceInformationBean mBean,Boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DeviceInformationCall(address, mBean));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new DeviceInformationCall(address, mBean));

    }

    public interface DeviceInformationCallInterface {
        void DeviceInformationCallResult();
    }

    /**
     * 3.6.3 APP 端设置设备实时运动数据的上传方式
     *
     * @param type Ox00:（默认值）间隔上传方式
     *             0x01: 实时上传方式
     */
    public static void
    writeSportsUploadModeCall(int type) {
        new WriteCall(address)
                .setServiceUUid(Config.serviceUUID)
                .setCharacteristicUUID(Config.mWriteCharacter)
                .enqueue(new SportsUploadModeCall(address, type));

    }

    /**
     * 3.6.4
     *
     * @param diastolic 舒张压
     * @param systolic  收缩压
     */
    public static void writeBloodPressureCalibrationCall(int diastolic, int systolic) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new BloodPressureCalibrationCall(address, diastolic, systolic));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new BloodPressureCalibrationCall(address, diastolic, systolic));

    }

    /**
     * 3.6.5 APP 端设置设备大数据存储间隔
     *
     * @param mData HeartRate           心率
     *              HeartRate           心率1(暂时没用到心率1的东西)
     *              getBloodOxygen      血氧
     *              BloodPressure       血压
     *              Temperature         温度
     *              Activity            活动
     */
    public static void writeSettingStorageIntervalCall(DataBean mData) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new StorageIntervalCall(address, mData));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new StorageIntervalCall(address, mData));

    }

    /**
     * App端设置设备闹钟,日程
     * 3.6.6
     *
     * @param mTimeBean Characteristic         索引 0x01闹钟特征 0x02日程特征  (31-32 UINICODE 编码)0x31(49)闹钟内容  0x32(50)日程内容
     *                  number                 闹钟/日程编号
     *                  mSwitch                开关
     *                  specifiedTime          重复,周一至周日是否开启并且是否闹钟只执行一次
     *                  OpenHour               时
     *                  openMin                分
     *                  year                   年
     *                  month                  月
     *                  day                    日
     *                  unicodeType            unicode编码类型/(31-32 UINICODE 编码)0x31(49)闹钟内容  0x32(50)日程内容
     *                  unicode                unicode编码
     */
    public static void writeAlarmClockScheduleCall(/*AlarmClockScheduleInterface mNoticeInterface,*/ TimeBean mTimeBean,Boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new AlarmClockScheduleCall(address, mTimeBean));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new AlarmClockScheduleCall(address, mTimeBean));
    }

    public interface AlarmClockScheduleInterface {
        void onResult(String message);
    }

    /**
     * 设置设备设备天气参数
     * 3.6.7
     *
     * @param mData time           时间戳
     *              weatherType    天气类型
     *              temperature    当前时刻的温度
     *              highestTemperatureToday    当天最高温度
     *              lowestTemperatureToday     当天最低温度
     *              airQuality     空气质量指数
     *              humidity       相对湿度
     *              UVIndex        紫外线指数
     *              sunriseHours   日出时
     *              sunriseMin     日出分
     *              sunsetHours    日落时
     *              sunsetMin     日落分
     */
    public static void writeWeatherCall(DataBean mData, Boolean tempStatus) {
        WriteCall write = new WriteCall(address);
        write.setPriority(tempStatus);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new WeatherCall(address, mData));

//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new WeatherCall(address, mData));
    }


    public static void writeWeatherCall(byte[] weatherByte, Boolean tempStatus) {
        WriteCall write = new WriteCall(address);
        write.setPriority(tempStatus);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new TestWeatherCall(address,weatherByte));

//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new WeatherCall(address, mData));
    }



    /**
     * 3.6.8吃药提醒
     *
     * @param mTimeBean number                 编号
     *                  mSwitch                开关
     *                  startTime              开始时间戳
     *                  <p>
     *                  endTime                结束时间戳
     *                  ReminderPeriod         提醒周期
     *                  groupList              数据组
     *                  unicodeTitle           标题
     *                  unicodeContent         内容
     *                  groupList.groupHH      提醒组时
     *                  groupList.groupMM      提醒组分
     */
    public static void writeRemindTakeMedicineCall(RemindTakeMedicineBean mTimeBean,Boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new RemindTakeMedicineCall(address, mTimeBean));
//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new RemindTakeMedicineCall(address, mTimeBean));

    }

    /**
     * 3.7.1
     *
     * @param key     类型特征 :0来电,1短信,2other(目前协议版本未确定的),3 email,4 Facebook,5 Wechat
     *                6  Line, 0x07: Weibo
     *                0x08: Linkedln
     *                0x09: QQ
     *                0x0A(10): WhatsAPP
     *                0x0B(11): Viber
     *                0x0C(12): Instagram
     * @param title   标题
     * @param content 内容
     */
    public static void writeMessageCall(int key, String title, String content, MessageInterface mInterface) {
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.setTimeout(15000);
        write.enqueue(new MessageCall(address, key, title, content, mInterface));
//            }
//        }, 200);

//        new WriteCall(address)
//                .setServiceUUid(Config.serviceUUID)
//                .setCharacteristicUUID(Config.mWriteCharacter)
//                .enqueue(new MessageCall(address, key, title, content));

    }

    public interface MessageInterface {
        void onResult();
    }
///////////////////////////////flash///////////////
public static void writeFlashWriteAssignCall(byte [] flashAddress,byte [] startKey,byte [] endKey,int size,int length,byte CRC, FlashWriteAssignInterface mInterface) {

    WriteCall write = new WriteCall(address);
    write.setPriority(true);
    write.setServiceUUid(Config.serviceUUID);
    write.setCharacteristicUUID(Config.WriteCharacterBig);
//    write.setTimeout(1500000);
    write.enqueue(new FlashWriteAssignCall(address, flashAddress,startKey,endKey, size,length,CRC, mInterface));
}
    public static void writeFlashWriteAssignCall(byte [] flashAddress, int size ,int countSize, FlashWriteAssignInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.WriteCharacterBig);
//        write.setTimeout(1500000);
        write.enqueue(new FlashWriteAssignCall(address, flashAddress, size,countSize, mInterface));
    }
    public interface FlashWriteAssignInterface {
        void onResultFlash(int size,int type);
    }

    public static void writeFlashErasureAssignCall( int startKey, int endKey , FlashErasureAssignInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.setTimeout(30000);
        write.enqueue(new FlashErasureAssignCall(address, startKey, endKey, mInterface));
    }
    public interface FlashErasureAssignInterface {
        void onResultErasure(int key);
    }
    public static void writeFlashErasureAssignCall(FlashGetFeaturesInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new FlashGetFeaturesCall(address,mInterface));
    }
    public interface FlashGetFeaturesInterface {
        void onResultFeatures(int key);
    }

    /*********************************  更换表盘操作********************************/
    public static void writeFlashGetDialCall(FlashGetDialInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DialGetAssignCall(address,mInterface));
    }
    public interface FlashGetDialInterface {
        void onResultDialIdBean(List<DialGetAssignCall.DialBean> bean);
    }
    public static void writeDialWriteAssignCall(DialCustomBean mDialCustomBean, DialWriteInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.setWriteType(1);
        write.enqueue(new DialWriteAssignCall(address,mDialCustomBean,mInterface));
    }


    public static void writeDialWriteAssignCall(DialCustomBean mDialCustomBean, DialWriteInterface mInterface,int type) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.setWriteType(type);
        write.enqueue(new DialWriteAssignCall(address,mDialCustomBean,mInterface));
    }


    public interface DialWriteInterface {
        void onResultDialWrite(int key);
    }

    /**
     * 设置指定表盘
     * @param mInterface
     */
    public static void writeDialDesignatedCall(long id,DialDesignatedInterface mInterface) {
        WriteCall write = new WriteCall(address);
        write.setPriority(true);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DialDesignatedCall(address,id,mInterface));
    }
    public interface DialDesignatedInterface {
        void onResultDialDesignated(int key);
    }





    public static void writeStartEffectiveCall(DeviceInformationBean mBean, DeviceInformationCallInterface mInterface,Boolean status) {
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new DeviceInformationCall(address, mBean, mInterface));
    }
    /**
     * 效验血压
     * @param mInterface
     * @param status
     */
    public static void writeEffectiveBloodPressureCall(  EffectiveBloodPressureInterface mInterface,int position,boolean status) {
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            WriteCall write = new WriteCall(address);
            write.setPriority(status);
            write.setTimeout(30000);
            write.setServiceUUid(Config.serviceUUID);
            write.setCharacteristicUUID(Config.mWriteCharacter);
            write.enqueue(new EffectiveBloodPressureCall(address,position,mInterface));
//        }, 200);
    }


    public static void writeCommByteArray(byte[] writeArray,boolean status,SpecifySleepSourceInterface specifySleepSourceInterface){
        WriteCall write = new WriteCall(address);
        write.setPriority(status);
        write.setTimeout(30000);
        write.setServiceUUid(Config.serviceUUID);
        write.setCharacteristicUUID(Config.mWriteCharacter);
        write.enqueue(new CommonWriteCall(address,writeArray,specifySleepSourceInterface));

    }

    public interface EffectiveBloodPressureInterface {
        void EffectiveBloodPressureCallResult(  ArrayList<Integer> mList);
    }


    public interface SpecifySleepSourceInterface{
        void backSpecifySleepSourceBean(SpecifySleepSourceBean specifySleepSourceBean);
        //开始时间 4个byte 结束时间 4个byte
        void backStartAndEndTime(byte[] startTime,byte[] endTime);
    }
}
