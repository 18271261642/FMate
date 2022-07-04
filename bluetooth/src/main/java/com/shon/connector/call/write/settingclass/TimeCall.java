package com.shon.connector.call.write.settingclass;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.TimeBean;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

/**
 * 3.6.1
 * APP 设置时间
 */
public class TimeCall extends WriteCallback {
    TimeBean mSettingTimeBean;
    BleWrite.TimeCallInterface mInterface;
    public TimeCall(String address, TimeBean mSettingTimeBean) {
        super(address);
        this.mSettingTimeBean = mSettingTimeBean;
    }
    public TimeCall(String address, TimeBean mSettingTimeBean, BleWrite.TimeCallInterface mInterface) {
        super(address);
        this.mSettingTimeBean = mSettingTimeBean;
        this.mInterface=mInterface;
    }
    @Override
    public byte[] getSendData() {
        byte[] year = HexDump.toByteArrayLength(mSettingTimeBean.getYear(), 2);
        byte[] month = HexDump.toByteArrayLength(mSettingTimeBean.getMonth(), 1);
        byte[] day = HexDump.toByteArrayLength(mSettingTimeBean.getDay(), 1);
        byte[] hh = HexDump.toByteArrayLength(mSettingTimeBean.getHours(), 1);
        byte[] mm = HexDump.toByteArrayLength(mSettingTimeBean.getMin(), 1);
        byte[] ss = HexDump.toByteArrayLength(mSettingTimeBean.getSs(), 1);
        return CmdUtil.getFullPackage(CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_TIME, ByteUtil.hexStringToByte(keyValue(year, month, day, hh, mm, ss))));
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        TLog.Companion.error("获取到最终的数据长度_++" + ByteUtil.getHexString(result));//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    if(mInterface!=null)
                        mInterface.TimeCall();
                    break;
                case 0x02:
                case 0x03:
                      BleWrite.writeTimeCall(mSettingTimeBean); //重新发送的操作
                    break;
                case 0x04:
                    //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }

        return false;
    }

    @Override
    public void onTimeout() {

    }

    private String keyValue(byte[] key, byte[] key1, byte[] key2, byte[] key3, byte[] key4, byte[] key5) {
        return
                ByteUtil.getHexString(key) +
                        ByteUtil.getHexString(key1) +
                        ByteUtil.getHexString(key2) +
                        ByteUtil.getHexString(key3) +
                        ByteUtil.getHexString(key4) +
                        ByteUtil.getHexString(key5);
    }
}
