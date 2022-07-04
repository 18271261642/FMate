package com.shon.connector.call.write.bigdataclass;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.TimeBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

import java.util.ArrayList;

/**
 * 3.4.33-3.4.34
 * APP 获取心率大数据记录
 * 所有记录和设备端返回大数据
 */
public class BigDataHistoryCall extends WriteCallback {
    byte keyValue;
    int num = 0;
    TimeBean mTimeBean;
    BleWrite.HistoryCallInterface mInterface;
    ArrayList<TimeBean> mList;
    StringBuilder stringBuilder = new StringBuilder();
    boolean isFinishFlag=false;
    public BigDataHistoryCall(String address) {
        super(address);
        mList = new ArrayList<>();
    }

    public BigDataHistoryCall(String address, byte keyValue, BleWrite.HistoryCallInterface mInterface) {
        super(address);
        mList = new ArrayList<>();
        this.mInterface = mInterface;
        this.keyValue = keyValue;
//        TLog.Companion.error("进入HeartRateHistoryCall");
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x02, keyValue, 0X00};
      //   TLog.Companion.error("发送++" + ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
//        if(uuid.isEmpty())
//            return false;
        String getResult = ByteUtil.getHexString(result);
        TLog.Companion.error("温度大数据","getResult++" + getResult);
        TLog.Companion.error("uuid++" + uuid);
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    return true;
                case 0x02:
                case 0x03:
                 //   ShowToast.INSTANCE.showToastLong("历史大数据++"+result[10]);
                    BleWrite.writeBigDataHistoryCall(keyValue,mInterface,true);
                    isFinishFlag=true;
                    break;
                case 0x04:
                  //  ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
        {
            return false;
        }

//        else {
        //正常数据的
        TLog.Companion.error("keyValue+"+keyValue +" ; result=="+result[9]);
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.BigData.KEY && CmdUtil.isCurrentCmd(keyValue, result[9])) {
            stringBuilder = new StringBuilder();
            num = HexDump.byte2intHigh(result, 3, 4);
            //      TLog.Companion.error("need  length ==" + num);
        }
        else {
            if (stringBuilder.length() == 0) {
                return false;
            }
        }
        stringBuilder.append(getResult);
         TLog.Companion.error("current length == " + (stringBuilder.length() / 2 - Config.BYTE_HEAD) );
//        + "  ; stringBuilder=" + (stringBuilder.length() / 2 - Config.BYTE_HEAD));
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
            //长度不相等，说明还没有组包完成，
            //这里需要添加一个变量，来标识是否组包完成，
            //同时 重写  isFinish() ,返回该值，以避免没接收完，同时返回 onTimeout（）
            // 这里返回了true，说明处理了此条数据，减少底层的循环次数
            return true;
        }
        isFinishFlag=true;
//            if (num == (stringBuilder.length() / 2 - Config.BYTE_HEAD))//减去头部的8个字节
//            {
//        TLog.Companion.error("等长==");
        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
        TLog.Companion.error("keyValue  " + keyValue +" , result =  " + data[9] + "  is finish ");
        for (int i = 10; i < data.length; i += 11) {
            mTimeBean = new TimeBean();
            mTimeBean.setDataUnitType(result[i]);
            mTimeBean.setTimeInterval(HexDump.byte2intHigh(data, i + 1, 2));
//            mTimeBean.setStartTime(Config.TIME_START+HexDump.byte2intHigh(data, i + 3, 4));
//            mTimeBean.setEndTime(Config.TIME_START+HexDump.byte2intHigh(data, i + 7, 4));
            mTimeBean.setStartTime( HexDump.byte2intHigh(data, i + 3, 4));
            mTimeBean.setEndTime( HexDump.byte2intHigh(data, i + 7, 4));
//            TLog.Companion.error("time=" + new Gson().toJson(mTimeBean));
            mList.add(mTimeBean);
        }
      //  TLog.Companion.error("发送指令+" + data[9]);
        if (CmdUtil.isCurrentCmd(keyValue, data[9])) {
            //数据处理完成，抛出结果。
            TLog.Companion.error("进入");
            mInterface.HistoryCallResult(data[9], mList);
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
        BleWrite.writeBigDataHistoryCall(keyValue,mInterface,true);
        TLog.Companion.error("历史记录获取时间超时");
    }
}
