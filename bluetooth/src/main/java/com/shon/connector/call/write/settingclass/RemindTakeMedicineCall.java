package com.shon.connector.call.write.settingclass;

import android.util.Log;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.RemindTakeMedicineBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

/**
 * 3.6.8
 * APP 设置吃药提醒
 */
public class RemindTakeMedicineCall extends WriteCallback {

    private static final String TAG = "RemindTakeMedicineCall";

    RemindTakeMedicineBean mTimeBean;

    public RemindTakeMedicineCall(String address, RemindTakeMedicineBean mTimeBean) {
        super(address);
        this.mTimeBean = mTimeBean;
    }

    @Override
    public byte[] getSendData() {
        TLog.Companion.error("吃药提醒指令=" + ByteUtil.getHexString(send()));
        return send();
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
//        TLog.Companion.error("获取到最终的数据长度_++" + result);//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                   // ShowToast.INSTANCE.showToastLong("设置成功");
                    break;
                case 0x02:
                case 0x03:
                    BleWrite.writeRemindTakeMedicineCall(mTimeBean,true);
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
        BleWrite.writeRemindTakeMedicineCall(mTimeBean,false);
    }
    byte[] bytesRed;
    private byte[] send() {
        byte[] unicodeTitle = new byte[0];
        byte[] unicodeContent = new byte[0];
//        byte[] startTime = HexDump.toByteArray((mTimeBean.getStartTime()/1000-Config.TIME_START));
//        byte[] endTime = HexDump.toByteArray((mTimeBean.getEndTime()/1000-Config.TIME_START));

        long startDate = mTimeBean.getStartTime();
        long endDate = mTimeBean.getEndTime();

        Log.e(TAG,"------开始结束时间戳="+startDate+" "+endDate);

        long resultStartTime = startDate == 0 ? 0 :  startDate - 946656000L;
        long resultEndTime = endDate == 0 ? 0 : endDate - 946656000L;

        byte[] startTime = HexDump.toByteArray(resultStartTime);
        byte[] endTime =  HexDump.toByteArray(resultEndTime);

        Log.e(TAG,"------开始结束时间="+ByteUtil.getHexString(startTime)+"\n"+ByteUtil.getHexString(endTime));
        //下面是数据解析
        byte[] bytes = new byte[]{(byte) mTimeBean.getNumber(),
                (byte) mTimeBean.getSwitch(),
                (byte) mTimeBean.getReminderPeriod(),
                startTime[0], startTime[1], startTime[2], startTime[3],  //开始时间戳
                endTime[0], endTime[1], endTime[2], endTime[3] //结束时间戳
        };
        if(mTimeBean.getGroupList()==null||mTimeBean.getGroupList().size()<=0)
        {
            bytesRed = new byte[0];
        }
        else {
            bytesRed = new byte[mTimeBean.getGroupList().size() * 2];
            for (int i = 0; i < mTimeBean.getGroupList().size(); i++) {
                bytesRed[i * 2] = (byte) mTimeBean.getGroupList().get(i).getGroupHH();
                bytesRed[i * 2 + 1] = (byte) mTimeBean.getGroupList().get(i).getGroupMM();
            }
        }
        if (mTimeBean.getUnicodeTitle() != null)
            if (!mTimeBean.getUnicodeTitle().isEmpty()) {
           //     TLog.Companion.error("解码数据之前++"+mTimeBean.getUnicodeTitle());
                unicodeTitle = HexDump.stringToByte(HexDump.getUnicode(mTimeBean.getUnicodeTitle()).replace("\\u", ""));//解码
           //     TLog.Companion.error("解码数据++"+ByteUtil.getHexString(unicodeTitle));
            }
        if (mTimeBean.getUnicodeContent() != null)
            if (!mTimeBean.getUnicodeContent().isEmpty()) {
                unicodeContent = HexDump.stringToByte(HexDump.getUnicode(mTimeBean.getUnicodeContent()).replace("\\u", ""));//解码
            }
//        TLog.Companion.error("unicodeContent++"+ByteUtil.getHexString(unicodeContent));
       //     TLog.Companion.error("bytesRed++"+ByteUtil.getHexString(bytesRed));
    //    TLog.Companion.error("bytes++"+ByteUtil.getHexString(bytes));
     //   TLog.Companion.error("unicodeTitle++"+ByteUtil.getHexString(unicodeTitle));
        byte[] value = ByteUtil.hexStringToByte(keyValue("01", bytes, bytesRed, "02", unicodeTitle, "03", unicodeContent));
         //   TLog.Companion.error("没头_最终数据+"+ByteUtil.getHexString(value));
        byte[] player = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_REMIND_TAKE_MEDICINE, value);
        return CmdUtil.getFullPackage(player);
    }
    private String keyValue(String key, byte[] keyValue, byte[] keyValue1, String key1, byte[] keyValue2, String key2, byte[] keyValue3) {
        return key +
                ByteUtil.getHexString(HexDump.toByteArray((short) (keyValue.length + keyValue1.length))) +
                ByteUtil.getHexString(keyValue) + ByteUtil.getHexString(keyValue1)
                + key1
                + ByteUtil.getHexString(HexDump.toByteArray((short) (keyValue2.length)))
                +  ByteUtil.getHexString(keyValue2)
                + key2
                + ByteUtil.getHexString(HexDump.toByteArray((short) (keyValue3.length)));
    }
}
