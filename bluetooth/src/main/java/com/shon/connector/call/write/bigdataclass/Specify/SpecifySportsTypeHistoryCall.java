package com.shon.connector.call.write.bigdataclass.Specify;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.DataBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

import java.util.ArrayList;

/**
 * 3.4.35-3.4.36
 * APP 端获取指定的日常活动数据
 */
public class SpecifySportsTypeHistoryCall extends WriteCallback {
    BleWrite.SpecifySportsHistoryCallInterface mInterface;
    ArrayList<DataBean> mList;
    DataBean mDataBean;
    long startTime = 0L,endTime=0L;
    StringBuilder stringBuilder = new StringBuilder();
    int num=-1;
    boolean isFinishFlag=false;
    public SpecifySportsTypeHistoryCall(String address, long time, long endTime, BleWrite.SpecifySportsHistoryCallInterface mInterface) {
        super(address);
        mList = new ArrayList<>();
        this.mInterface = mInterface;
        this.endTime=endTime;
        this.startTime = time;
    }

    @Override
    public byte[] getSendData() {
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_SPORTS_TYPE, HexDump.toByteArray(startTime)));
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {

        String getResult = ByteUtil.getHexString(result);
        TLog.Companion.error("getResult++" + getResult);
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
                switch (result[10]) {
                    case 0x01:

                        break;
                    case 0x02:
                    case 0x03:
                        isFinishFlag=true;
                        //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                        break;
                    case 0x04:
                        ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                        break;
            }
            return true;
        }
        if(!uuid.equalsIgnoreCase(Config.readCharacterBig))
            return false;
//        else {
            //正常数据的
            if (result[0]==Config.PRODUCT_CODE&&result[8] == Config.BigData.KEY &&result[9]== Config.BigData.DEVICE_SPECIFY_SPORTS_TYPE) {
                TLog.Companion.error("头的次数 SpecifyDailyActivitiesHistoryCall ");
                num = HexDump.byte2intHigh(result, 3, 4);
                stringBuilder = new StringBuilder();
            }
            else {
                if (stringBuilder.length() == 0) {
                    //不是头包数据，并且没有添加数据，说明不是这个 callback  需要处理的数据
                    // 丢掉 ，给其他 callback 去判断
                    return false;
                }
            }
            stringBuilder.append(ByteUtil.getHexString(result));
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
            //长度不相等，说明还没有组包完成，
            //这里需要添加一个变量，来标识是否组包完成，
            //同时 重写  isFinish() ,返回该值，以避免没接收完，同时返回 onTimeout（）
            // 这里返回了true，说明处理了此条数据，减少底层的循环次数
            return true;
        }
        isFinishFlag=true;
//            if(num==(stringBuilder.length()/2-Config.BYTE_HEAD))//减去头部的8个字节长度
//              {
                 byte []data=ByteUtil.hexStringToByte(stringBuilder.toString());
                 TLog.Companion.error("==="+ByteUtil.getHexString(data));
//                  if(data[9]==0x24) { //如果上层判断了 下层可以不用再次判断
                      TLog.Companion.error("24");
                      for (int i = 10; i < data.length; i +=10) {
                          mDataBean = new DataBean();
                          mDataBean.setStressStartTime(HexDump.byte2intHigh(data, i, 4));
                          mDataBean.setStressEndTime(HexDump.byte2intHigh(data, i +4, 4));
                          mDataBean.setStressTimeType(data[i+9]);
                          mDataBean.setStressResultType(data[i+10]);
                          mList.add(mDataBean);
                      }
                    if (result[9]== Config.BigData.DEVICE_SPECIFY_SPORTS_TYPE) {
                        mInterface.SpecifySportsHistoryCallCallResult(startTime,endTime,mList);
                        return true;
                    }
//                  }

//              }

//        }


        return false;
    }

    @Override
    public boolean isFinish() {

        return isFinishFlag;
    }

    @Override
    public void onTimeout() {

    }

    private String keyValue(String key, byte[] keyValue) {
        return key +
                ByteUtil.getHexString(new byte[]{(byte) (keyValue.length)}) +
                ByteUtil.getHexString(keyValue);
    }
}
