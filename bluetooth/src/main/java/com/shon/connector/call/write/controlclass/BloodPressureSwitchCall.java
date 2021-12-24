package com.shon.connector.call.write.controlclass;

import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.3.6
 * 设备实时上传血压值开关
 */
public class BloodPressureSwitchCall extends WriteCallback {
    BleWrite.BloodPressureSwitchInterface mBloodPressureSwitchInterface;
    int keyValue;

    public BloodPressureSwitchCall(String address) {
        super(address);
    }

    public BloodPressureSwitchCall(String address, int keyValue,
                                   BleWrite.BloodPressureSwitchInterface mBloodPressureSwitchInterface) {
        super(address);
        this.keyValue = keyValue;
        this.mBloodPressureSwitchInterface = mBloodPressureSwitchInterface;
    }

    @Override
    public byte[] getSendData() {

        byte payload[] = {0x01, 0x06, 0x00, (byte) keyValue};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)

        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        mBloodPressureSwitchInterface.BloodPressureSwitchInterface(null);
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
