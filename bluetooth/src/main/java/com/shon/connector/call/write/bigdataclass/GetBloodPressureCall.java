package com.shon.connector.call.write.bigdataclass;

import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.BloodOxygenBean;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.utils.HexDump;

import java.util.ArrayList;

/**
 * 3.4.11-3.4.12
 * APP 获取血压大数据
 */
public class GetBloodPressureCall extends WriteCallback {
    long time=0L;
    int num=0;
    int tenRest=0;
    int item=15;
    byte[] setData={};
    BloodOxygenBean mBloodOxygenBean;
    BleWrite.GetBloodPressureInterface mInterface;
    public GetBloodPressureCall(String address, long time) {
        super(address);
        this.time=time;
    }
    public GetBloodPressureCall(String address, BleWrite.GetBloodPressureInterface mInterface) {
        super(address);
        this.mInterface=mInterface;
        mBloodOxygenBean=new BloodOxygenBean();
    }
    @Override
    public byte[] getSendData() {
        byte[] head={Config.PRODUCT_CODE,0x00,0x01,0x00,0x00,0x00,0x05, 0x00,0x02,0x0B,0x00,0x01,0x00};
        return head;
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equals(Config.readCharacterBig))
            return false;
        TLog.Companion.error("超过20字节的大数据处理");
        num++;//每次接受
       setData = HexDump.byteMerger(setData, result);//如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF
        if(result[9]==0x0C&&result[8]==0x02&&num<=1)  //满足是第一个包的情况下
        {
            //假如result[10]=0x01,result[11]=0xFF  那么按照我的思路和想法最后得到的tenRest=1FF
                tenRest=  (result[10]<<8+result[11]);
            TLog.Companion.error("获取到最终的数据长度_++"+tenRest);//获取到最终的长度
            item=item-8;
           // ByteUtil.getHexString(new byte[]{(result[10]<<4+result[11])});
        }
        if(num>=tenRest)//当满足为 最后一个的时候
        {
            ArrayList<BloodOxygenBean> mList=new ArrayList<>();
            for (int i = 8; i <setData.length ; i+=15) {//开头8位不要 每次15个为一个item
                mBloodOxygenBean=new BloodOxygenBean();
                mBloodOxygenBean.setType(setData[i]);
                mBloodOxygenBean.setCompressionLong(setData[i+1]<<8+setData[i+2]);
                mBloodOxygenBean.setNotCompressionLong(setData[i+3]<<8+setData[i+4]);
                mBloodOxygenBean.setInterval(setData[i+5]<<8+setData[i+6]);
                mBloodOxygenBean.setStartTime(setData[i+7]<<24+setData[i+8]<<16+setData[i+9]<<8+setData[i+10]);
                mBloodOxygenBean.setStartTime(setData[i+11]<<24+setData[i+12]<<16+setData[i+13]<<8+setData[i+14]);
                mList.add(mBloodOxygenBean);
            }
            mInterface.GetBloodPressureInterface(mList);
        }

        return false;
    }

    @Override
    public boolean isFinish() {
        if(tenRest<=num)
            return true;
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
