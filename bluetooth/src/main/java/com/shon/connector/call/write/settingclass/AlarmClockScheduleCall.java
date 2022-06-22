package com.shon.connector.call.write.settingclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.TimeBean;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

/**
 * 3.6.6
 * APP 设置设备闹钟,日程
 */
public class AlarmClockScheduleCall extends WriteCallback {
    TimeBean mTimeBean;

    public AlarmClockScheduleCall(String address, TimeBean mTimeBean) {
        super(address);
        this.mTimeBean = mTimeBean;
    }

    @Override
    public byte[] getSendData() {
//        TLog.Companion.error("闹钟==" + ByteUtil.getHexString(send()));
        return send();
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("-----------闹钟="+HexDump.dumpHexString(result));
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
      //  TLog.Companion.error("闹钟 日程 获取到最终的数据长度_++" + ByteUtil.getHexString(result));//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                  //  ShowToast.INSTANCE.showToastLong("设置成功");
                    break;
                case 0x02:
                case 0x03:
                    // BleWrite.writeTimeCall(mSettingTimeBean); //重新发送的操作
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
        BleWrite.writeAlarmClockScheduleCall(mTimeBean,false);
    }

    private byte[] send() {
        String settingTime = "";
        String sendData = "";
        //下面是数据解析
        if (mTimeBean.getCharacteristic() == TimeBean.ALARM_FEATURES) {
//            TLog.Companion.error("mTimeBean.getSwitch()=="+mTimeBean.getSwitch());
            byte[] bytes = new byte[]{(byte) mTimeBean.getNumber(),
                    (byte) mTimeBean.getSwitch()
                    , (byte) mTimeBean.getSpecifiedTime(),
                    (byte) mTimeBean.getHours(),
                    (byte) mTimeBean.getMin()};
            settingTime = keyValue("01", bytes);
        } else if (mTimeBean.getCharacteristic() == TimeBean.SCHEDULE_FEATURES) {
            byte[] year = HexDump.toByteArray((short) mTimeBean.getYear());
            byte[] bytes = new byte[]{(byte) mTimeBean.getNumber(),
                    (byte) mTimeBean.getSwitch(),
                    year[0], year[1],
                    (byte) mTimeBean.getMonth(),
                    (byte) mTimeBean.getDay(),
                    (byte) mTimeBean.getHours(),
                    (byte) mTimeBean.getMin()};
            settingTime = keyValue("02", bytes);
        }
        if (mTimeBean.getUnicodeType() == TimeBean.ALARM_FEATURES_UNICODE || mTimeBean.getUnicodeType() == TimeBean.SCHEDULE_FEATURES_UNICODE) {
            //这个解码出来可能会很长
            if(mTimeBean.getUnicode()!=null)
            if (!mTimeBean.getUnicode().isEmpty()) {
             //   TLog.Companion.error("1111");
                byte[] unicode = HexDump.stringToByte(HexDump.getUnicode(mTimeBean.getUnicode()).replace("\\u", ""));//解码
                sendData = keyValue(ByteUtil.getHexString(new byte[]{mTimeBean.getUnicodeType()}), unicode);
//                TLog.Companion.error("==="+sendData);
            }
        }
        byte[] value = ByteUtil.hexStringToByte(settingTime + sendData);
        byte[] player = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_ALARM_CLOCK, value);
        return CmdUtil.getFullPackage(player);
    }

    private String keyValue(String key, byte[] keyValue) {
        return key +
                ByteUtil.getHexString(HexDump.toByteArray((short) keyValue.length)) +
                ByteUtil.getHexString(keyValue);
    }
}
