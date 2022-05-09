package com.shon.connector.call.write.bigdataclass.Specify;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.bean.DailyActiveBean;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

import java.util.ArrayList;

/**
 * 3.4.35-3.4.36
 * APP 端获取指定的日常活动数据
 */
public class SpecifyDailyActivitiesHistoryCall extends WriteCallback {
    BleWrite.SpecifyDailyActivitiesHistoryCallInterface mInterface;
    ArrayList<DailyActiveBean> mList;
    DailyActiveBean mDataBean;
    long time = 0L, endTime = 0L;
    StringBuilder stringBuilder = new StringBuilder();
    int num = -1;
    boolean isFinishFlag = false;

    public SpecifyDailyActivitiesHistoryCall(String address) {
        super(address);
        mList = new ArrayList<>();
    }

    public
    SpecifyDailyActivitiesHistoryCall(String address, long time, long endTime, BleWrite.SpecifyDailyActivitiesHistoryCallInterface mInterface) {
        super(address);
        mList = new ArrayList<>();
        this.mInterface = mInterface;
        this.endTime = endTime;
        this.time = time;
//        TLog.Companion.error("走了步数");
    }

    @Override
    public byte[] getSendData() {
//        TLog.Companion.error("发送++" + ByteUtil.getHexString(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_DAILY_ACTIVITIES, HexDump.toByteArray(time))));
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_DAILY_ACTIVITIES, HexDump.toByteArray(time)));
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {

        String getResult = ByteUtil.getHexString(result);
        if (result[0] == Config.PRODUCT_CODE &&result.length<10)
            return false;
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                    BleWrite.writeSpecifyDailyActivitiesHistoryCall(time,endTime,mInterface,true); //重新发送的操作
                 //   ShowToast.INSTANCE.showToastLong("处于错误+"+result[10]);
                    isFinishFlag = true;
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
//        TLog.Companion.error("正常要的数据截获  ++" + getResult+" ,length="+getResult.length());
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.BigData.KEY && result[9] == Config.BigData.DEVICE_SPECIFY_DAILY_ACTIVITIES) {

            num = HexDump.byte2intHigh(result, 3, 4);
//            TLog.Companion.error("头的次数 SpecifyDailyActivitiesHistoryCall +长度=" + num);
            stringBuilder = new StringBuilder();
        } else {
            if (stringBuilder.length() == 0) {
                //不是头包数据，并且没有添加数据，说明不是这个 callback  需要处理的数据
                // 丢掉 ，给其他 callback 去判断
                return false;
            }
        }
        stringBuilder.append(getResult);

//            stringBuilder.append(ByteUtil.getHexString(result));
 //       TLog.Companion.error("stringBuilder     +" +  stringBuilder.toString());
//        TLog.Companion.error("stringBuilder  size  +" +stringBuilder.length());
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
            //长度不相等，说明还没有组包完成，
            //这里需要添加一个变量，来标识是否组包完成，
            //同时 重写  isFinish() ,返回该值，以避免没接收完，同时返回 onTimeout（）
            // 这里返回了true，说明处理了此条数据，减少底层的循环次数
            return true;
        }
        isFinishFlag = true;
//            if(num==(stringBuilder.length()/2-Config.BYTE_HEAD))//减去头部的8个字节长度
//              {
        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
//        TLog.Companion.error("===" + ByteUtil.getHexString(data));
//                  if(data[9]==0x24) { //如果上层判断了 下层可以不用再次判断
        for (int i = 10; i < data.length; i += 6) {
            mDataBean = new DailyActiveBean();
 //           TLog.Companion.error("步数++"+HexDump.byte2intHigh(data, i, 2));
//            TLog.Companion.error("距离++"+HexDump.byte2intHigh(data, i+2, 2));
//            TLog.Companion.error("卡路里++"+HexDump.byte2intHigh(data, i+4, 2));
            mDataBean.setSteps(HexDump.byte2intHigh(data, i, 2));
            mDataBean.setDistance(HexDump.byte2intHigh(data, i + 2, 2));
            mDataBean.setCalorie(HexDump.byte2intHigh(data, i + 4, 2));
            mList.add(mDataBean);
        }
        if (data[9] == Config.BigData.DEVICE_SPECIFY_DAILY_ACTIVITIES) {
            mInterface.SpecifyDailyActivitiesHistoryCallResult(mList);
            mInterface.SpecifyDailyActivitiesHistoryCallResult(time,endTime,mList);
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
        BleWrite.writeSpecifyDailyActivitiesHistoryCall(time,endTime,mInterface,true);
            TLog.Companion.error("步数记录超时?+"+isFinishFlag);
        //    ShowToast.INSTANCE.showToastLong("步数记录超时 可能是丢包了");
    }

    private String keyValue(String key, byte[] keyValue) {
        return key +
                ByteUtil.getHexString(new byte[]{(byte) (keyValue.length)}) +
                ByteUtil.getHexString(keyValue);
    }
}
