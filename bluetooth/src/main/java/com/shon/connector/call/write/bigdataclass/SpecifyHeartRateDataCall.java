package com.shon.connector.call.write.bigdataclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.Config;
import com.shon.connector.utils.HexDump;

/**
 * 3.4.3-3.4.4
 * APP 获取指定的心率数据
 */
public class SpecifyHeartRateDataCall extends WriteCallback {
    byte keyValue;
    int num=0;
    String tenRest="";
    long time=0L;
    public SpecifyHeartRateDataCall(String address,long time) {
        super(address);
        this.time=time;
    }
    public SpecifyHeartRateDataCall(String address, byte keyValue) {
        super(address);
        this.keyValue=keyValue;
    }
    @Override
    public byte[] getSendData() {
        String key=Long.toHexString(time);
        byte[] keyTime=new byte[]{(byte) Integer.parseInt(key.substring(0,2),16)
                ,(byte) Integer.parseInt(key.substring(2,4),16)
                ,(byte) Integer.parseInt(key.substring(4,6),16)
                ,(byte) Integer.parseInt(key.substring(6,8),16)};
        byte[] head={Config.PRODUCT_CODE,0x00,0x01,0x00,0x00,0x00,0x05, HexDump.byteXOR(keyTime),0x02,0x03,0x00,0x04};
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
        TLog.Companion.error("超过20字节的大数据处理");
        num++;//每次接受
        if(result.length<20)
        {
            ShowToast.INSTANCE.showToastLong("暂无心率记录");
            return true;
        }
        if(result.length>=20&&result[9]==0x01&&result[8]==0x02)  //满足是第一个包的情况下
        {
            //假如result[10]=0x01,result[11]=0xFF  那么按照我的思路和想法最后得到的tenRest=1FF

            int t=result[10]<<8;
                tenRest= String.valueOf((result[10]<<8+result[11]));
            TLog.Companion.error("获取到最终的数据长度_++"+tenRest);//获取到最终的长度

           // ByteUtil.getHexString(new byte[]{(result[10]<<4+result[11])});
        }

        return false;
    }

    @Override
    public boolean isFinish() {
        if(Integer.parseInt(tenRest)<=num)
            return true;
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
