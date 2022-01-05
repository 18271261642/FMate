package com.shon.connector.call.write.settingclass;

import com.example.xingliansdk.utils.ShowToast;
import com.google.gson.Gson;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.bean.DeviceInformationBean;

import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.6.2
 * APP 设置设备端基本信息
 */
public class DeviceInformationCall extends WriteCallback {
    DeviceInformationBean mDeviceInformationBean;
    BleWrite.DeviceInformationCallInterface mInterface;


    public DeviceInformationCall(String address, DeviceInformationBean mDeviceInformationBean) {
        super(address);
        TLog.Companion.error("==mDeviceInformationBean++"+ new Gson().toJson(mDeviceInformationBean));
        this.mDeviceInformationBean = mDeviceInformationBean;
    }

    public DeviceInformationCall(String address, DeviceInformationBean mDeviceInformationBean, BleWrite.DeviceInformationCallInterface mInterface) {
        super(address);
        this.mDeviceInformationBean = mDeviceInformationBean;

        this.mInterface = mInterface;
    }


    @Override
    public byte[] getSendData() {


        byte[] keyData = new byte[]{
                (byte) mDeviceInformationBean.getSex()
                , (byte) mDeviceInformationBean.getAge()
                , (byte) mDeviceInformationBean.getHeight()
                , (byte) mDeviceInformationBean.getWeight()
                , mDeviceInformationBean.getLanguage()
                , mDeviceInformationBean.getTimeSystem()
                , mDeviceInformationBean.getUnitSystem()
                , 0x01//Android系统
                , mDeviceInformationBean.getWearHands()
                , mDeviceInformationBean.getTemperatureSystem()
        };
        byte[] mSteps = HexDump.toByteArray(mDeviceInformationBean.getExerciseSteps());//步数达标
        byte[] setData = HexDump.byteMerger(keyData, mSteps);
        TLog.Companion.error("个人信息值++"+ ByteUtil.getHexString(setData));
        return CmdUtil.getFullPackage(CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_BASIC_INFORMATION, setData));
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
//        TLog.Companion.error("获取到最终的数据长度_++" + result);//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
//                    TLog.Companion.error("成功++");
                    if(mInterface!=null)
                    mInterface.DeviceInformationCallResult();
                    break;
                case 0x02:
                case 0x03:
                    // BleWrite.writeTimeCall(mSettingTimeBean); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
