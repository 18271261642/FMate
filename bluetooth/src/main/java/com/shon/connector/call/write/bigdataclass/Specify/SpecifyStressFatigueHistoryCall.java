package com.shon.connector.call.write.bigdataclass.Specify;

import com.shon.connector.utils.ShowToast;
import com.shon.connector.bean.PressureBean;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

import java.util.ArrayList;

/**
 * 3.4.31-3.4.32
 * APP 端获取指定的压力疲劳值数据
 */
public class SpecifyStressFatigueHistoryCall extends WriteCallback {

    BleWrite.SpecifyStressFatigueHistoryCallInterface mInterface;
    ArrayList<PressureBean> mList;
    PressureBean dataBean;
    long startTime = 0L, endTime = 0L;
    StringBuilder stringBuilder = new StringBuilder();
    int num = -1;
    boolean isFinishFlag = false;

    public SpecifyStressFatigueHistoryCall(String address, long time, long endTime, BleWrite.SpecifyStressFatigueHistoryCallInterface mInterface) {
        super(address);
        mList = new ArrayList<>();
        this.mInterface = mInterface;
        this.endTime = endTime;
        this.startTime = time;
//        TLog.Companion.error("进入了 压力值");
    }

    @Override
    public byte[] getSendData() {
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_STRESS_FATIGUE, HexDump.toByteArray(startTime)));
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {

        String getResult = ByteUtil.getHexString(result);
//        TLog.Companion.error("getResult++" + getResult);
        if (result[0] == Config.PRODUCT_CODE && result.length < 10)
            return false;
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
//            if (result[8] == 0x07 && result[9] == Config.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                   // ShowToast.INSTANCE.showToastLong(" 重发 错误在于 压力+"+result[10]);
                    BleWrite.writeSpecifyStressFatigueHistoryCall(
                            startTime, endTime,
                            mInterface,true
                    );
                    isFinishFlag = true;
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
//        TLog.Companion.error("22222222222++" + getResult.length() / 2);
        //正常数据的
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.BigData.KEY && result[9] == Config.BigData.DEVICE_SPECIFY_STRESS_FATIGUE) {
            num = HexDump.byte2intHigh(result, 3, 4);
//            TLog.Companion.error("头的次数 SpecifyHeartRateHistoryCall +" + num);
            stringBuilder = new StringBuilder();
        } else {
            if (stringBuilder.length() == 0) {
                TLog.Companion.error("丢弃了");
                //不是头包数据，并且没有添加数据，说明不是这个 callback  需要处理的数据
                // 丢掉 ，给其他 callback 去判断
                return false;
            }
        }
//        TLog.Companion.error("接受压力数据");
        stringBuilder.append(getResult);
//        TLog.Companion.error("stringBuilder++" + (stringBuilder.length() / 2 - Config.BYTE_HEAD));
       // TLog.Companion.error("num++" + num);
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
//            TLog.Companion.error("num=="+num+"  stringBuilder.length() =="+stringBuilder.length()/2 );
            return true;
        }
        isFinishFlag = true;

        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
//        TLog.Companion.error("data[9]++" + data[9]);
  //      TLog.Companion.error("Config.BigData.DEVICE_SPECIFY_STRESS_FATIGUE++" + Config.BigData.DEVICE_SPECIFY_STRESS_FATIGUE);
        if (data[9] == Config.BigData.DEVICE_SPECIFY_STRESS_FATIGUE) {
            //TLog.Companion.error("到里面了");
            for (int i = 10; i < data.length; i += 3) {
                dataBean = new PressureBean();
                if (ByteUtil.cbyte2Int(data[i]) != 255) {
                    dataBean.setHeartHealth(data[i]);
                } else
                    dataBean.setHeartHealth(0);
                if (ByteUtil.cbyte2Int(data[i + 1]) != 255) {
                    dataBean.setStress(data[i + 1]);
                } else
                    dataBean.setStress(0);
                if (ByteUtil.cbyte2Int(data[i + 2]) != 255) {
                    dataBean.setFatigue(data[i + 2]);
                } else
                    dataBean.setFatigue(0);
                mList.add(dataBean);
            }
//            TLog.Companion.error("压力疲劳");
            mInterface.SpecifyStressFatigueHistoryCallResult(startTime, endTime, mList);
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
       // ShowToast.INSTANCE.showToastLong("压力 超时  马上执行了重发机制 isFinishFlag+"+isFinishFlag);
      TLog.Companion.error("压力 超时  马上执行了重发机制 isFinishFlag+"+isFinishFlag);
//        BleWrite.writeSpecifyStressFatigueHistoryCall(
//                startTime, endTime,
//                mInterface,true
//        );
    }

}
