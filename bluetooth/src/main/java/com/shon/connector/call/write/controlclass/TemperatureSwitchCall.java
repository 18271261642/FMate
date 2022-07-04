package com.shon.connector.call.write.controlclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;

/**
 * 3.3.7
 * 设备实时上传温度值开关
 */
public class TemperatureSwitchCall extends WriteCallback {
    byte key;
    BleWrite.TemperatureSwitchCallInterface mTemperatureSwitchCallInterface;

    public TemperatureSwitchCall(String address) {
        super(address);
    }

    public TemperatureSwitchCall(String address, byte key, BleWrite.TemperatureSwitchCallInterface mTemperatureSwitchCallInterface) {
        super(address);
        this.key = key;
        this.mTemperatureSwitchCallInterface = mTemperatureSwitchCallInterface;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01, 0x07, 0x00};
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                    BleWrite.writeTemperatureSwitchCall(key, mTemperatureSwitchCallInterface); //重新发送的操作
                    break;
                case 0x03:
                    BleWrite.writeTemperatureSwitchCall(key,mTemperatureSwitchCallInterface); //重新发送的操作
                    break;
                case 0x04:
                    //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
        }
        TLog.Companion.error("实时数据++" + ByteUtil.getHexString(result));


//         mTemperatureSwitchCallInterface.TemperatureSwitchCallResult();

        return true;
    }

    @Override
    public void onTimeout() {

    }
}
