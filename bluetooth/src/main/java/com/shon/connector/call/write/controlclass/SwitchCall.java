package com.shon.connector.call.write.controlclass;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.3.4
 * 设备实时上传心率值开关
 */
public class SwitchCall extends WriteCallback {
    byte keyValue;
    byte type;
    public SwitchCall(String address, byte keyValue, byte type) {
        super(address);
        this.keyValue=keyValue;
        this.type=type;
    }
    @Override
    public byte[] getSendData() {
        byte payload[] = {Config.ControlClass.COMMAND,keyValue,type};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
        TLog.Companion.error("result=="+ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {

        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        TLog.Companion.error("result=="+ByteUtil.getHexString(result));
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    break;
                case 0x02:
                case 0x03:
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
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
            TLog.Companion.error("难道是这边超时了?");
    }
}
