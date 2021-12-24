package com.shon.connector.call.write.bigdataclass;

import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.utils.HexDump;
import com.shon.connector.bean.BloodOxygenBean;
import com.shon.bluetooth.core.callback.WriteCallback;

import static java.lang.String.valueOf;

/**
 * 3.4.10
 * APP 删除血氧大数据
 */
public class DeleteSpecifyBloodOxygenCall extends WriteCallback {
    long time=0L;
    BloodOxygenBean mBloodOxygenBean;
   // BleWrite.GetBloodOxygenInterface mInterface;
    public DeleteSpecifyBloodOxygenCall(String address, long time) {
        super(address);
        this.time=time;
    }
//    public DeleteSpecifyBloodOxygenCall(String address, BleWrite.GetBloodOxygenInterface mInterface) {
//        super(address);
//        this.mInterface=mInterface;
//        mBloodOxygenBean=new BloodOxygenBean();
//    }
    @Override
    public byte[] getSendData() {

        String key=Long.toHexString(time);
        byte[] keyTime=new byte[]{
                (byte) Integer.parseInt(key.substring(0,2),16)
                ,(byte) Integer.parseInt(key.substring(2,4),16)
                ,(byte) Integer.parseInt(key.substring(4,6),16)
                ,(byte) Integer.parseInt(key.substring(6,8),16)};
        byte[] head={Config.PRODUCT_CODE,0x00,0x01,0x00,0x00,0x00,0x05, 0x00,0x02,0x0A,0x00,0x04,0x00};
        byte[] setData = HexDump.byteMerger(head, keyTime);
        return setData;
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equals(Config.readCharacterBig))
            return false;

        return false;
    }
//
//    @Override
//    public boolean isFinish() {
//        if(tenRest<=num)
//            return true;
//        return false;
//    }

    @Override
    public void onTimeout() {

    }
}
