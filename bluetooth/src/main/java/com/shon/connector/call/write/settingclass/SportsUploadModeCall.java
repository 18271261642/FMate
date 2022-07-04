package com.shon.connector.call.write.settingclass;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;

import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.6.3
 * APP 设置设备实时运动数据
 */
public class SportsUploadModeCall extends WriteCallback {
    byte[] key = {0x00};

    public SportsUploadModeCall(String address, int key) {
        super(address);
        this.key[0] = (byte) key;
    }

    @Override
    public byte[] getSendData() {
        byte[] sendData = CmdUtil.getFullPackage(CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_MOVEMENT_TYPE, key));
        TLog.Companion.error("===" + ByteUtil.getHexString(sendData));
        return sendData;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    //  mInterface.DeviceInformationCallResult();
                    break;
                case 0x02:
                case 0x03:
                    // BleWrite.writeTimeCall(mSettingTimeBean); //重新发送的操作
                    break;
                case 0x04:
                   // ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }
        TLog.Companion.error("获取到最终的数据长度_++" + ByteUtil.getHexString(result));//获取到最终的长度
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
