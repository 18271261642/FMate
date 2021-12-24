package com.shon.connector.call.write.bigdataclass;

import com.shon.connector.Config;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.4.25
 * APP 删除指定的RR数据
 */
public class DeleteRRCall extends WriteCallback {
    byte keyValue;
    long time=0L;
    public DeleteRRCall(String address, long time) {
        super(address);
        this.time=time;
    }
    public DeleteRRCall(String address, byte keyValue) {
        super(address);
        this.keyValue=keyValue;
    }
    @Override
    public byte[] getSendData() {
        String key=Long.toHexString(time);
        byte[] keyTime=new byte[]{
                (byte) Integer.parseInt(key.substring(0,2),16)
                ,(byte) Integer.parseInt(key.substring(2,4),16)
                ,(byte) Integer.parseInt(key.substring(4,6),16)
                ,(byte) Integer.parseInt(key.substring(6,8),16)};
        byte[] head={Config.PRODUCT_CODE,0x00,0x01,0x00,0x00,0x00,0x05, HexDump.byteXOR(keyTime),0x02,0x19,0x00,0x04};
        byte[] setData = HexDump.byteMerger(head, keyTime);//如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF
        String SixTeen = Integer.toHexString(setData.length);//如果超过255字节 sixteen的长度会>2
        if (SixTeen.length() > 2) {
            setData[5] = HexDump.hexStringToBytes(SixTeen.substring(0, (SixTeen.length() - 2)))[0];//取第一位到长度-2
            setData[6] = HexDump.hexStringToBytes(SixTeen.substring((SixTeen.length() - 2)))[0];//取后俩位
        } else
            setData[6] = HexDump.hexStringToBytes(SixTeen)[0];//没超过及直接获取
        return setData;
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equals(Config.readCharacterBig))
            return false;

            TLog.Companion.error("获取到最终的数据长度_++"+result);//获取到最终的长度

        return false;
    }



    @Override
    public void onTimeout() {

    }
}
