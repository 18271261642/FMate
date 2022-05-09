package com.shon.connector.call.write.bigdataclass.Specify;

import android.os.Handler;
import android.os.Looper;

import com.shon.connector.utils.ShowToast;
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
public class SpecifyHeartRateHistoryCall extends WriteCallback {

    BleWrite.SpecifyHeartRateHistoryCallInterface mInterface;
    ArrayList<Integer> mList;
    long time = 0L, endTime = 0L;
    StringBuilder stringBuilder = new StringBuilder();
    int num = -1;
    boolean isFinishFlag = false;

    public SpecifyHeartRateHistoryCall(String address) {
        super(address);
        mList = new ArrayList<>();
    }

    public SpecifyHeartRateHistoryCall(String address, long time, long endTime, BleWrite.SpecifyHeartRateHistoryCallInterface mInterface) {
        super(address);
        mList = new ArrayList<>();
        this.mInterface = mInterface;
        this.endTime = endTime;
        this.time = time;
//        TLog.Companion.error("发送 进入");
    }

    @Override
    public byte[] getSendData() {

        byte[] send=CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_HEART_RATE, HexDump.toByteArray(time));
//        TLog.Companion.error("发送++" + ByteUtil.getHexString(send));
        return CmdUtil.getFullPackage(send);

    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {

        String getResult = ByteUtil.getHexString(result);
//        TLog.Companion.error("心率++" + getResult);
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
//            if (result[8] == 0x07 && result[9] == Config.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
               //     ShowToast.INSTANCE.showToastLong("心率处于错误+"+result[10]);
                    isFinishFlag=true;
                    BleWrite.writeSpecifyHeartRateHistoryCall(
                            time, endTime,
                            mInterface
                            ,true);
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
//                }
            }
            return true;
        }
        if (!uuid.equalsIgnoreCase(Config.readCharacterBig))
            return false;
        //正常数据的
        if (result[0] == Config.PRODUCT_CODE
                && result[8] == Config.BigData.KEY
                && result[9] == Config.BigData.DEVICE_SPECIFY_HEART_RATE
        ) {

            num = HexDump.byte2intHigh(result, 3, 4);
          //  TLog.Companion.error("头的次数 SpecifyHeartRateHistoryCall +" + num);
            stringBuilder = new StringBuilder();
        } else {
            if (stringBuilder.length() == 0) {
                TLog.Companion.error("被丢掉了");
                //不是头包数据，并且没有添加数据，说明不是这个 callback  需要处理的数据
                // 丢掉 ，给其他 callback 去判断
                return false;
            }
        }
        stringBuilder.append(getResult);
//        stringBuilder.append(getResult);
//        TLog.Companion.error("stringBuilder++" + stringBuilder.length() / 2);
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
//            TLog.Companion.error("num=="+num+"  stringBuilder.length() =="+stringBuilder.length()/2 );
            return true;
        }
        isFinishFlag = true;

        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
      //  TLog.Companion.error(" result key  = " + data[9] + " is received finished");
        if (data[9] == Config.BigData.DEVICE_SPECIFY_HEART_RATE) {
            for (int i = 10; i < data.length; i++) {
                if (ByteUtil.cbyte2Int(data[i]) != 255) {
                    mList.add(ByteUtil.cbyte2Int(data[i]));
                } else
                    mList.add(0);//在等于255时为0
            }
            mInterface.SpecifyHeartRateHistoryCallResult(time, endTime, mList);
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> BleWrite.writeSpecifyHeartRateHistoryCall(
                time, endTime,
                mInterface
        ,true), 500);

     //   ShowToast.INSTANCE.showToastLong("SpecifyHeartRateHistoryCall 心率  超时了");
        TLog.Companion.error("SpecifyHeartRateHistoryCall  is Timeout=="+isFinishFlag);
    }


}
