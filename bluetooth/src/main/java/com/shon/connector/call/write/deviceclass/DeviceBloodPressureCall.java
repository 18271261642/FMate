package com.shon.connector.call.write.deviceclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.2.11-3.2.12
 * 获取血压信息
 */
public class DeviceBloodPressureCall extends WriteCallback {
    BleWrite.DeviceBloodPressureInterface mDeviceBloodPressureInterface;

    public DeviceBloodPressureCall(String address, BleWrite.DeviceBloodPressureInterface mDeviceBloodPressureInterface) {
        super(address);
        this.mDeviceBloodPressureInterface = mDeviceBloodPressureInterface;
    }

    @Override
    public byte[] getSendData() {
       int t= 0xe8&0xff;
        byte payload[] = {0x00, 0x0B, 0x00};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
//        TLog.Companion.error("==" + ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    break;
                case 0x02:
                case 0x03:
                    BleWrite.writeForGetDeviceBloodPressure(mDeviceBloodPressureInterface); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        if(result[0] == Config.PRODUCT_CODE &&result[8]==Config.getDevice.COMMAND&&result[9]==Config.getDevice.DEVICE_BLOOD_PRESSURE_CALIBRATION_KEY) {
            mDeviceBloodPressureInterface.onResult(String.valueOf(result[10]), String.valueOf(result[11]));
            return true;
        }
        return  false;
    }

    @Override
    public void onTimeout() {

    }
}
