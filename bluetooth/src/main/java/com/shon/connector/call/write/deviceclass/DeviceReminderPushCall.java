package com.shon.connector.call.write.deviceclass;

import com.shon.connector.utils.TLog;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.bean.PushBean;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;

/**
 * 3.2.9-3.2.10
 * 获取推送提醒信息
 */
public class DeviceReminderPushCall extends WriteCallback {
    //    int i = 0;
    BleWrite.DeviceReminderPushInterface mDeviceReminderPushInterface;
    PushBean mPushBean;

    public DeviceReminderPushCall(String address, BleWrite.DeviceReminderPushInterface mDeviceReminderPushInterface) {
        super(address);
        this.mDeviceReminderPushInterface = mDeviceReminderPushInterface;
        mPushBean = new PushBean();
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x00, 0x09, 0x00};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
        TLog.Companion.error("==" + ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        //11 5  8=24
        String getResult = ByteUtil.getHexString(result);
        String productNumber = "", version = "", nowMaC = "", mac = "";
        if (result[8] == 0x07 && result[9] == 0x03) {
            if (result[10] == 0x02) {
                BleWrite.writeForGetDeviceReminderPush(mDeviceReminderPushInterface); //重新发送的操作
            }
        } else {
//        if (result.length >= 20) {
//            i++;
            mPushBean.setOther(result[10]);
            mPushBean.setEmail(result[11]);
            mPushBean.setFacebook(result[12]);
            mPushBean.setWechat(result[13]);
            mPushBean.setLine(result[14]);
            mPushBean.setWeiBo(result[15]);
            mPushBean.setLinkedln(result[16]);
//        } else {
            mPushBean.setQQ(result[17]);
            mPushBean.setWhatsApp(result[18]);
            mPushBean.setViber(result[19]);
            mPushBean.setInstagram(result[20]);
//        }
            mDeviceReminderPushInterface.onResult(mPushBean);
        }
        return true;
    }

    @Override
    public boolean isFinish() {
//        if (i >= 2)
//            return true;

        return super.isFinish();
    }


    @Override
    public void onTimeout() {

    }
}
