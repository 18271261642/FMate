package com.shon.connector.call.write.deviceclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.bean.DataBean;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.2.7-3.2.8
 * 获取设备运动信息
 */
public class DeviceMotionCall extends WriteCallback {

    BleWrite.DeviceMotionInterface mDeviceMotionInterface;
    DataBean mDataBean = new DataBean();

    public DeviceMotionCall(String address, BleWrite.DeviceMotionInterface mDeviceMotionInterface) {
        super(address);
        this.mDeviceMotionInterface = mDeviceMotionInterface;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x00, 0x07, 0x00};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)Config.getDevice.APP_REAL_TIME_EXERCISE_KEY
//        TLog.Companion.error("==" + ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    long Steps, Distance, Calories;

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;


        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                    break;
                case 0x04:
                    //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.getDevice.COMMAND && result[9] == Config.getDevice.DEVICE_REAL_TIME_EXERCISE_KEY) {
            Steps=HexDump.byte2intHigh(result,10,4);
            Distance=HexDump.byte2intHigh(result,14,4);
            Calories=HexDump.byte2intHigh(result,18,4);
            mDataBean.setTotalSteps(Steps);
            mDataBean.setDistance(Distance);
            mDataBean.setCalories(Calories);
            mDeviceMotionInterface.DeviceMotionResult(mDataBean);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFinish() {
        return super.isFinish();
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error("time out 超时了呢");
    }
}
