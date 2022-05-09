package com.shon.connector.call.write.settingclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.6.4
 * APP 端设置设备血压校准值
 */
public class BloodPressureCalibrationCall extends WriteCallback {
    byte key[] = {0x00, 0x00};

    public BloodPressureCalibrationCall(String address, int key, int key1) {
        super(address);
        this.key[0] = (byte) key;
        this.key[1] = (byte) key1;
    }

    @Override
    public byte[] getSendData() {
        return CmdUtil.getFullPackage(CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_BLOOD_PRESSURE, key));

    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

      //  TLog.Companion.error("获取到最终的数据长度_++" + result);//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    //       mInterface.DeviceInformationCallResult();
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
