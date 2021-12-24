package com.shon.connector.call.write.deviceclass;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.2.3-3.2.4
 * 获取设备属性信息
 */
public class DevicePropertiesCall extends WriteCallback {
    BleWrite.DevicePropertiesInterface mDevicePropertiesInterface;
    int electricity = 0, type = 0;
    int deviceChargingStatus=0;
    int mCurrentBattery =0;
    int mDisplayBattery =1;//分母不可为0
    public DevicePropertiesCall(String address, BleWrite.DevicePropertiesInterface mDevicePropertiesInterface) {
        super(address);
        this.mDevicePropertiesInterface = mDevicePropertiesInterface;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x00, 0x03};
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
                    BleWrite.writeForGetDeviceProperties(mDevicePropertiesInterface,false); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        if(result[0] == Config.PRODUCT_CODE &&result[8]==Config.getDevice.COMMAND&&result[9]==Config.getDevice.DEVICE_PROPERTIES_KEY) {
            electricity = result[10];
            type = result[12];
            if (result[11] > 0x0F) {
                mDisplayBattery = result[11] << 4;
                mCurrentBattery = result[11] >> 4;
            }
            mDevicePropertiesInterface.DevicePropertiesResult(electricity, mCurrentBattery, mDisplayBattery, type);
            return true;
        }
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
