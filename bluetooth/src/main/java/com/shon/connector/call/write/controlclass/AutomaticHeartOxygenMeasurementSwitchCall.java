package com.shon.connector.call.write.controlclass;

import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.utils.HexDump;

/**
 * 3.3.17
 *  设备自动测量心脏氧气开关
 */
public class AutomaticHeartOxygenMeasurementSwitchCall extends WriteCallback {
    byte []keyValue;
    BleWrite.AutomaticHeartOxygenMeasurementSwitchInterface mInterface;
    public AutomaticHeartOxygenMeasurementSwitchCall(String address, byte[] keyValue
    , BleWrite.AutomaticHeartOxygenMeasurementSwitchInterface mInterface) {
        super(address);
        this.keyValue=keyValue;
        this.mInterface=mInterface;
    }
    @Override
    public byte[] getSendData() {
        byte keyXOR = HexDump.byteXOR(keyValue);
        byte[] head={Config.PRODUCT_CODE,0x00,0x01,0x00,0x00,0x00,0x0D,keyXOR,0x01,0x11,0x00,0x07};
        // ByteBuffer mByteBuffer=  ByteBuffer.allocate(head.length+keyValue.length);//长度缓冲区
        byte[]  setData= HexDump.byteMerger(head,keyValue);//如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF
        String SixTeen= Integer.toHexString(setData.length);//如果超过255字节 sixteen的长度会>2
        if(SixTeen.length()>2)
        {
            setData[5]=  HexDump.hexStringToBytes(SixTeen.substring(0,(SixTeen.length()-2)))[0];//取第一位到长度-2
            setData[6]=  HexDump.hexStringToBytes(SixTeen.substring((SixTeen.length()-2)))[0];//取后俩位
        }
        else
            setData[6]=  HexDump.hexStringToBytes(SixTeen)[0];//没超过及直接获取
        TLog.Companion.error("最终长度+"+ ByteUtil.getHexString(setData));
        return setData;
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        mInterface.AutomaticHeartOxygenMeasurementSwitchResult("");
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
