package com.shon.connector.call.write.controlclass;

import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.bean.TimeBean;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.3.5
 * 设备实时上传血氧值开关
 */
public class BloodOxygenSwitchCall extends WriteCallback {
    TimeBean mTimeBean;
    public BloodOxygenSwitchCall(String address) {
        super(address);
    }
    public BloodOxygenSwitchCall(String address,TimeBean mTimeBean) {
        super(address);
        this.mTimeBean=mTimeBean;
    }
    @Override
    public byte[] getSendData() {
//
//        keyValue= new byte[]{(byte) mTimeBean.getSwitch(), (byte) mTimeBean.getSpecifiedTime(), (byte) mTimeBean.getOpenHour(), (byte) mTimeBean.getOpenMin(), (byte) mTimeBean.getCloseHour(), (byte) mTimeBean.getCloseMin()};
//        byte keyXOR = HexDump.byteXOR(keyValue);
//        byte[] head = {Config.PRODUCT_CODE, 0x00, 0x01, 0x00, 0x00, 0x00, 0x12, keyXOR, 0x01, 0x05, 0x00, 0x07};
//        byte[] setData = HexDump.byteMerger(head, keyValue);//如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF
//        String SixTeen = Integer.toHexString(setData.length);//如果超过255字节 sixteen的长度会>2
//        if (SixTeen.length() > 2) {
//            setData[5] = HexDump.hexStringToBytes(SixTeen.substring(0, (SixTeen.length() - 2)))[0];//取第一位到长度-2
//            setData[6] = HexDump.hexStringToBytes(SixTeen.substring((SixTeen.length() - 2)))[1];//取后俩位
//        } else
//            setData[6] = HexDump.hexStringToBytes(SixTeen)[0];//没超过及直接获取
//        TLog.Companion.error("最终长度+" + ByteUtil.getHexString(setData));
        byte payload[] = {0x01, 0x05, 0x00,(byte) mTimeBean.getSwitch() };//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        return false;
    }

    @Override
    public void onTimeout() {

    }
}
