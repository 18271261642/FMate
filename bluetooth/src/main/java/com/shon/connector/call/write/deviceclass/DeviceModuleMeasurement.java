package com.shon.connector.call.write.deviceclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.bean.DataBean;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.2.5-3.2.6
 * 获取设备模块测量信息
 */
public class DeviceModuleMeasurement extends WriteCallback {

    BleWrite.DeviceModuleMeasurementInterface mDeviceModuleMeasurementInterface;
    DataBean mDataBean=new DataBean();

    public DeviceModuleMeasurement(String address, BleWrite.DeviceModuleMeasurementInterface mDeviceModuleMeasurementInterface) {
        super(address);
        this.mDeviceModuleMeasurementInterface=mDeviceModuleMeasurementInterface;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x00, 0x05};
        TLog.Companion.error("=="+ ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
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
                    BleWrite.writeForGetDeviceModuleMeasurement(mDeviceModuleMeasurementInterface); //重新发送的操作
                    break;
                case 0x04:
                    //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        if(result[0] == Config.PRODUCT_CODE &&result[8]==Config.getDevice.COMMAND&&result[9]==Config.getDevice.DEVICE_MODULE_MEASUREMENT_KEY) {
            mDataBean.setHeartRateMeasureType(result[10]);
            mDataBean.setBloodOxygenMeasureType(result[11]);
            mDataBean.setBloodPressureMeasureType(result[12]);
            mDataBean.setTemperatureMeasureType(result[13]);
            mDeviceModuleMeasurementInterface.DeviceModuleMeasurementResult(mDataBean);
        return true;
        }
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
