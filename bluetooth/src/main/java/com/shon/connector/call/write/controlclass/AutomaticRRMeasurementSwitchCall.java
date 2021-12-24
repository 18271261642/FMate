package com.shon.connector.call.write.controlclass;

import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;

/**
 * 3.3.20
 * 设备自动测量RR开关
 */
public class AutomaticRRMeasurementSwitchCall extends WriteCallback {
    byte[] keyValue;//长度7文档3.3.18

    public AutomaticRRMeasurementSwitchCall(String address) {
        super(address);
    }

    public AutomaticRRMeasurementSwitchCall(String address, byte[] keyValue, BleWrite.AutomaticMeasurementSwitchInterface mInterface) {
        super(address);
        this.keyValue = keyValue;
    }

    @Override
    public byte[] getSendData() {
        byte keyXOR = HexDump.byteXOR(keyValue);
        byte[] head = {Config.PRODUCT_CODE, 0x00, 0x01, 0x00, 0x00, 0x00, 0x12, keyXOR, 0x01, 0x14, 0x00, 0x07};
        // ByteBuffer mByteBuffer=  ByteBuffer.allocate(head.length+keyValue.length);//长度缓冲区
        byte[] setData = HexDump.byteMerger(head, keyValue);//如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF
        String SixTeen = Integer.toHexString(setData.length);//如果超过255字节 sixteen的长度会>2
        if (SixTeen.length() > 2) {
            setData[5] = HexDump.hexStringToBytes(SixTeen.substring(0, (SixTeen.length() - 2)))[0];//取第一位到长度-2
            setData[6] = HexDump.hexStringToBytes(SixTeen.substring((SixTeen.length() - 2)))[0];//取后俩位
        } else
            setData[6] = HexDump.hexStringToBytes(SixTeen)[0];//没超过及直接获取
        TLog.Companion.error("最终长度+" + ByteUtil.getHexString(setData));
        return setData;
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
