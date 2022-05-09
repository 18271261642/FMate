package com.shon.connector.call.write.controlclass;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;

/**
 * 3.3.11
 * 来电处理
 */
public class IncomingCallCall extends WriteCallback {
    byte keyValue;
    public IncomingCallCall(String address, byte keyValue) {
        super(address);
        this.keyValue=keyValue;
    }
    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01, 0x0B, keyValue};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)

        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        if (result[8] == 0x07 && result[9] == 0x03) {
            if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
                switch (result[10]) {
                    case 0x01:

                        break;
                    case 0x02:
                    case 0x03:
                        BleWrite.writeIncomingCallCall(keyValue); //重新发送的操作
                        break;
                    case 0x04:
                        ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                        break;
                }
            }

        }

        return false;
    }

    @Override
    public void onTimeout() {

    }
}
