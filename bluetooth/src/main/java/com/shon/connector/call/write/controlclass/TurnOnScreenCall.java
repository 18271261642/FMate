package com.shon.connector.call.write.controlclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.utils.TLog;

/**
 * 3.3.12
 * 设备抬手亮屏开关
 */
public class TurnOnScreenCall extends WriteCallback {
    byte keyValue;
    public TurnOnScreenCall(String address) {
        super(address);
    }
    public TurnOnScreenCall(String address, byte keyValue) {
        super(address);
        this.keyValue=keyValue;
    }
    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01, 0x0C, keyValue};
        return CmdUtil.getFullPackage(payload);

    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        TLog.Companion.error("手表抬腕亮屏");
            if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
                switch (result[10]) {
                    case 0x01:
                        return  true;
                     //   break;
                    case 0x02:
                    case 0x03:
                        BleWrite.writeTurnOnScreenCall(keyValue); //重新发送的操作
                        break;
                    case 0x04:
                        //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                        break;
                }

            return  true;
        }
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
