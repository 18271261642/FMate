package com.shon.connector.call.write.controlclass;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.utils.TLog;

/**
 * 3.3.10
 * 相机开关
 */
public class CameraSwitchCall extends WriteCallback {
    byte keyValue;
    BleWrite.CameraSwitchCallInterface mInterface;
    public CameraSwitchCall(String address) {
        super(address);
    }
    public CameraSwitchCall(String address, byte keyValue, BleWrite.CameraSwitchCallInterface mInterface) {
        super(address);
        this.keyValue=keyValue;
        this.mInterface=mInterface;
    }
    public CameraSwitchCall(String address, byte keyValue) {
        super(address);
        this.keyValue=keyValue;
    }
    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01, 0x0A,keyValue};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        TLog.Companion.error("result=="+ ByteUtil.getHexString(result));
        if (result[8] == 0x07 && result[9] == 0x03) {
            if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
                switch (result[10]) {
                    case 0x01:
                        return  true;
                    case 0x02:
                    case 0x03:
                        if(mInterface!=null)
                        BleWrite.writeCameraSwitchCall(keyValue,mInterface); //重新发送的操作
                        break;
                    case 0x04:
                        ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                        break;
                }
                return  true;
            }

        }
        return true;
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error(" 拍照 onTimeout ");
    }
}
