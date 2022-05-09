package com.shon.connector.call.write.bigdataclass.Specify;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.SleepBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

import java.util.ArrayList;

/**
 * 3.4.35-3.4.36
 * APP 端获取指定的日常活动数据
 */
public class SpecifySleepHistoryCall extends WriteCallback {

    BleWrite.SpecifySleepHistoryCallInterface mInterface;
    ArrayList<SleepBean> mStepList;
    ArrayList<SleepBean.StepChildBean> mStepChildList;
    SleepBean mStepBean;
    SleepBean.StepChildBean mStepChildBean;
    long startTime = 0L, endTime = 0L;
    StringBuilder stringBuilder = new StringBuilder();
    int num = -1;
    boolean isFinishFlag = false;

    public SpecifySleepHistoryCall(String address) {
        super(address);
    }

    public SpecifySleepHistoryCall(String address, long startTime, long endTime, BleWrite.SpecifySleepHistoryCallInterface mInterface) {
        super(address);
        mStepList = new ArrayList<>();
        mStepChildList = new ArrayList<>();
        this.mInterface = mInterface;
        this.endTime = endTime;
        this.startTime = startTime;
    }

    @Override
    public byte[] getSendData() {
//        TLog.Companion.error("发送++" + ByteUtil.getHexString(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_SLEEP, HexDump.toByteArray(startTime))));
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_SLEEP, HexDump.toByteArray(startTime)));
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {

        String getResult = ByteUtil.getHexString(result);

        if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
//            if (result[8] == 0x07 && result[9] == Config.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                    BleWrite.writeSpecifySleepHistoryCall(
                            startTime,  endTime,
                            mInterface,true
                    ); //重新发送的操作
                    isFinishFlag=true;
                 //   ShowToast.INSTANCE.showToastLong("处于错误+"+result[10]);
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
//                }
            }
            return true;
        }
        if(!uuid.equalsIgnoreCase(Config.readCharacterBig))
            return false;
//        else {
        //正常数据的
//        TLog.Companion.error("睡眠原数据 getResult++" + getResult);
//        TLog.Companion.error("睡眠原数据 stringBuilder++" + stringBuilder);
        if (result[0] == Config.PRODUCT_CODE && result[8] == 0x02&& result[9] == Config.BigData.DEVICE_SPECIFY_SLEEP) {
//            TLog.Companion.error("头的次数 SpecifySleepHistoryCall ");
            num = HexDump.byte2intHigh(result, 3, 4);
            stringBuilder = new StringBuilder();
        } else {
            if (stringBuilder.length() == 0) {
                return false;
            }
        }
        stringBuilder.append(getResult);
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
       //     TLog.Companion.error("num=="+num+"  stringBuilder.length() =="+stringBuilder.length()/2 );

            return true;
        }
        isFinishFlag = true;

        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
        if (result[0] == Config.PRODUCT_CODE && data[9] == Config.BigData.DEVICE_SPECIFY_SLEEP) {
            int length = HexDump.byte2intHigh(data, 11, 2);
            int endLength = HexDump.byte2intHigh(data, length + 14, 2);
//            TLog.Companion.error("length+=" + length);
//            TLog.Companion.error("endLength+=" + endLength);
            for (int i = 10; i < data.length; i += (length + endLength + 6)) {
                mStepBean = new SleepBean();
                //这里添加开始和结束时间戳 用于判断是当前什么时间开始入睡并
                mStepBean.setStartTime(startTime);
                mStepBean.setEndTime(endTime);
                mStepBean.setIndexOne(HexDump.byte2intHigh(data, i, 1));
                mStepBean.setLengthOne(HexDump.byte2intHigh(data, i + 1, 2));
                mStepBean.setTotalApneaTime(HexDump.byte2intHigh(data, i + 3, 2));
                mStepBean.setNumberOfApnea(HexDump.byte2intHigh(data, i + 5, 1));
                mStepBean.setAverageHeartRate(HexDump.byte2intHigh(data, i + 6, 1));
                mStepBean.setMaximumHeartRate(HexDump.byte2intHigh(data, i + 7, 1));
                mStepBean.setMinimumHeartRate(HexDump.byte2intHigh(data, i + 8, 1));
                mStepBean.setRespiratoryQuality(HexDump.byte2intHigh(data,i+9,1));
//                TLog.Companion.error("setMinimumHeartRate+" + HexDump.byte2intHigh(data, i + 8, 2));
                for (int j = (length + 16); j < data.length; j += 2) {//一个睡眠值长度为2
//                    TLog.Companion.error("添加数据+" + ByteUtil.getHexString(new byte[]{data[j], data[j + 1]}));
                    mStepChildBean = new SleepBean.StepChildBean();
                    mStepChildBean.setType((data[j]&0xff) >> 5); //取出15-13的bit 找出类型
//                    TLog.Companion.error("++"+ByteUtil.getHexString((byte) ((data[j]&0xff) >> 5)));
                    mStepChildBean.setDuration(HexDump.getXocInt(data[j]) + (data[j + 1] & 0xff)); //0-12bit
                    mStepChildList.add(mStepChildBean);
                }
                mStepBean.setmList(mStepChildList);
                mStepList.add(mStepBean);
            }

            mInterface.SpecifySleepHistoryCallResult(startTime,endTime,mStepList,mStepBean);
          //  mInterface.SpecifySleepHistoryCallResult(mStepList);
            return true;
        }


        return false;
    }

    @Override
    public boolean isFinish() {

        return isFinishFlag;
    }

    @Override
    public void onTimeout() {
       // ShowToast.INSTANCE.showToastLong("睡眠已丢包+"+isFinishFlag);
        TLog.Companion.error("睡眠已丢包+"+isFinishFlag);

//        BleWrite.writeSpecifySleepHistoryCall(
//                 startTime,  endTime,
//                mInterface,true
//        );
    }


}
