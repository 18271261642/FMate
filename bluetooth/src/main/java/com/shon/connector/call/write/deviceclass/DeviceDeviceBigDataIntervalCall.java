package com.shon.connector.call.write.deviceclass;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.bean.DataBean;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.2.13-3.2.14
 * 获取设备大数据信息间隔时间
 */
public class DeviceDeviceBigDataIntervalCall extends WriteCallback {
    BleWrite.DeviceDeviceBigDataIntervalInterface mDeviceBigDataInterface;
    int i = 0;
    DataBean mDataBean;

    public DeviceDeviceBigDataIntervalCall(String address, BleWrite.DeviceDeviceBigDataIntervalInterface mDeviceBigDataInterface) {
        super(address);
        this.mDeviceBigDataInterface = mDeviceBigDataInterface;
        mDataBean = new DataBean();
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x00, 0x0D, 0x00};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
        TLog.Companion.error("==" + ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
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
                    BleWrite.writeForGetDeviceBigDataInterval(mDeviceBigDataInterface); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        if(result[0] == Config.PRODUCT_CODE &&result[8]==Config.getDevice.COMMAND&&result[9]==Config.getDevice.DEVICE_BIG_DATA_INTERVAL_KEY) {
            String getResult = ByteUtil.getHexString(result);
            TLog.Companion.error("正常要的数据截获  ++" + getResult);
            mDataBean.setHeartRate(HexDump.byte2intHigh(result,10,2));//result[10] << 8 + result[11]
            mDataBean.setHeartRate1(HexDump.byte2intHigh(result,12,2));//result[10] << 8 + result[11]
            mDataBean.setBloodOxygen(HexDump.byte2intHigh(result,14,2));
            mDataBean.setBloodPressure(HexDump.byte2intHigh(result,16,2));
            mDataBean.setTemperature(String.valueOf(HexDump.byte2intHigh(result,18,2)));
//            mDataBean.setActivity(HexDump.byte2intHigh(result,18,2));
            mDataBean.setActivity(HexDump.byte2intHigh(result,20,2));
            mDeviceBigDataInterface.onResult(mDataBean);
        return  true;
        }
        return false;
    }

    @Override
    public boolean isFinish() {
        return super.isFinish();
    }

    @Override
    public void onTimeout() {

    }
}
