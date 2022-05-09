package com.shon.connector.call.write.controlclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.utils.TLog;

import java.util.Arrays;

/**
 * 3.3.8
 * App寻找设备功能开关
 */
public class FindDeviceSwitchCall extends WriteCallback {
    byte keyValue;
    BleWrite.FindDeviceSwitchCallInterface mInterface;
    public FindDeviceSwitchCall(String address,byte keyValue) {
        super(address);
        this.keyValue=keyValue;
    }
    public FindDeviceSwitchCall(String address, byte keyValue, BleWrite.FindDeviceSwitchCallInterface mInterface) {
        super(address);
        this.keyValue=keyValue;
        this.mInterface=mInterface;
    }
    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01,0x08,keyValue};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
        TLog.Companion.error("查找手表="+ Arrays.toString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    return true;
                case 0x02:
                case 0x03:
                    if (mInterface==null)
                        BleWrite.writeFindDeviceSwitchCall(keyValue); //重新发送的操作
                    else
                    BleWrite.writeFindDeviceSwitchCall(keyValue, mInterface); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
        }

        return false;
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error("超时");
    }
}
