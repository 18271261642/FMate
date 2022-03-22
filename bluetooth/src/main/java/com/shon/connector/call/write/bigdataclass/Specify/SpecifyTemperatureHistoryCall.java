package com.shon.connector.call.write.bigdataclass.Specify;

import android.content.Intent;

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
 * 3.4.23-3.4.24
 * APP 端获取指定的温度数据
 */
public class SpecifyTemperatureHistoryCall extends WriteCallback {

    BleWrite.SpecifyTemperatureHistoryCallInterface mInterface;
    ArrayList<Integer> mList;
    long startTime = 0L, endTime = 0L;
    StringBuilder stringBuilder = new StringBuilder();
    int num = -1;
    boolean isFinishFlag = false;

    public SpecifyTemperatureHistoryCall(String address, long time, long endTime, BleWrite.SpecifyTemperatureHistoryCallInterface mInterface) {
        super(address);
        mList = new ArrayList<>();
        this.mInterface = mInterface;
        this.endTime = endTime;
        this.startTime = time;
    }

    @Override
    public byte[] getSendData() {
//        TLog.Companion.error("发送++" + ByteUtil.getHexString(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_TEMPERATURE, HexDump.toByteArray(startTime))));
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_TEMPERATURE, HexDump.toByteArray(startTime)));
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {

        String getResult = ByteUtil.getHexString(result);
        TLog.Companion.error("指定的温度数据","getResult++" + getResult);
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                    BleWrite.writeSpecifyTemperatureHistoryCall(
                            startTime, endTime,
                            mInterface,true
                    );
                    isFinishFlag=true;
                //    ShowToast.INSTANCE.showToastLong("错误在于 温度"+result[10]);
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }
        if (!uuid.equalsIgnoreCase(Config.readCharacterBig))
            return false;
     //   TLog.Companion.error("22222222222++" + getResult.length() / 2);
        //正常数据的
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.BigData.KEY) {
            num = HexDump.byte2intHigh(result, 3, 4);
      //      TLog.Companion.error("头的次数 SpecifyHeartRateHistoryCall +" + num);
            stringBuilder = new StringBuilder();
        } else {
            if (stringBuilder.length() == 0) {
                TLog.Companion.error("丢弃了");
                //不是头包数据，并且没有添加数据，说明不是这个 callback  需要处理的数据
                // 丢掉 ，给其他 callback 去判断
                return false;
            }
        }
        stringBuilder.append(getResult);
  //      TLog.Companion.error("stringBuilder++" + stringBuilder.length() / 2);
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
       //     TLog.Companion.error("num=="+num+"  stringBuilder.length() =="+stringBuilder.length()/2 );
            return true;
        }
        isFinishFlag = true;

        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
        if (data[9] == Config.BigData.DEVICE_SPECIFY_TEMPERATURE) {
            for (int i = 10; i < data.length; i += 2) {

              //  int temperature = (ByteUtil.cbyte2Int(data[i]) << 8) + (ByteUtil.cbyte2Int(data[i+1]));
                int temperature =  HexDump.byte2intHigh(data,i,2);
                if(ByteUtil.cbyte2Int(data[i]) ==255&&ByteUtil.cbyte2Int(data[i+1]) ==255)
                    temperature=0;
//                TLog.Companion.error("data[i]"+((data[i]&0xff) << 8)+"  ,  data[i+1]="+(data[i + 1]&0xff)+"  temperature=="+temperature);
                if (temperature >= Config.TEMPERATURE_MAX)
                    temperature -= Config.TEMPERATURE_MAX;
                if(temperature>500)
                {
//                    ShowToast.INSTANCE.showToastLong("展示异常温度数据+"+temperature);
                    temperature=0;
                }
                mList.add(temperature);
            }
       //     TLog.Companion.error("温度");
            mInterface.SpecifyTemperatureHistoryCallResult(startTime, endTime, mList);

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
        TLog.Companion.error("温度超时++并重发了");
        BleWrite.writeSpecifyTemperatureHistoryCall(
                startTime, endTime,
                mInterface,true
        );
    }


}
