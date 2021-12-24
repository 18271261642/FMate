package com.shon.connector.call.write.bigdataclass;

import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.DataBean;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.utils.HexDump;

import java.util.ArrayList;

/**
 * 3.4.28-3.4.29
 * APP 获取指定运动大数据
 */
public class GetSpecifyMotionCall extends WriteCallback {
    long time=0L;
    int num=0;
    int tenRest=0;
    int item=15;
    byte[] setData={};
    DataBean mDataBean;
    BleWrite.GetSpecifyMotionInterface mInterface;
    public GetSpecifyMotionCall(String address, long time) {
        super(address);
        this.time=time;
    }
    public GetSpecifyMotionCall(String address, BleWrite.GetSpecifyMotionInterface mInterface) {
        super(address);
        this.mInterface=mInterface;
        mDataBean=new DataBean();
    }
    @Override
    public byte[] getSendData() {

        String key=Long.toHexString(time);
        byte[] keyTime=new byte[]{
                (byte) Integer.parseInt(key.substring(0,2),16)
                ,(byte) Integer.parseInt(key.substring(2,4),16)
                ,(byte) Integer.parseInt(key.substring(4,6),16)
                ,(byte) Integer.parseInt(key.substring(6,8),16)};
        byte[] head={Config.PRODUCT_CODE,0x00,0x01,0x00,0x00,0x00,0x05, 0x00,0x02,0x1C,0x00,0x01,0x00};
        byte[] setData = HexDump.byteMerger(head, keyTime);
        return setData;
    }
    //这个类返回有错误  俩个字节最高6W多步 然而三个bit用于别的作用导致最终只有8千多不
    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equals(Config.readCharacterBig))
            return false;
        TLog.Companion.error("超过20字节的大数据处理");
        num++;//每次接受
       setData = HexDump.byteMerger(setData, result);//如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF
        if(result[9]==0x1D&&result[8]==0x02&&num<=1)  //满足是第一个包的情况下
        {
            //假如result[10]=0x01,result[11]=0xFF  那么按照我的思路和想法最后得到的tenRest=1FF
            tenRest=  (result[10]<<8+result[11]);
            TLog.Companion.error("获取到最终的数据长度_++"+tenRest);//获取到最终的长度
            item=item-8;
           // ByteUtil.getHexString(new byte[]{(result[10]<<4+result[11])});
        }
        if(num>=tenRest)//当满足为 最后一个的时候
        {
            ArrayList<DataBean> mList=new ArrayList<>();
            for (int i = 8; i <setData.length ; i+=2) {//开头8位不要 每次15个为一个item
               mDataBean.setPedometer(HexDump.getXocInt(setData[i])+setData[i+1]);//步数
                mDataBean.setType(setData[i]>>5);
                mList.add(mDataBean);
            }
           // mInterface.GetBloodOxygenInterface(mList);
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
