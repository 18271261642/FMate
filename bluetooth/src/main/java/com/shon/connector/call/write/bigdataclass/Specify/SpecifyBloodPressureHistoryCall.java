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
 * 3.4.19-3.4.20
 * APP 端获取指定的血压数据
 */
public class SpecifyBloodPressureHistoryCall extends WriteCallback {

    BleWrite.SpecifyBloodPressureHistoryCallInterface mInterface;
    ArrayList<DataBean> mList;
    DataBean dataBean;
    long startTime = 0L, endTime = 0L;
    StringBuilder stringBuilder = new StringBuilder();
    int num = -1;
    boolean isFinishFlag = false;

    public SpecifyBloodPressureHistoryCall(String address, long time, long endTime, BleWrite.SpecifyBloodPressureHistoryCallInterface mInterface) {
        super(address);
        mList = new ArrayList<>();
        this.mInterface = mInterface;
        this.endTime = endTime;
        this.startTime = time;

    }

    @Override
    public byte[] getSendData() {
        TLog.Companion.error("发送++" + ByteUtil.getHexString(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_BLOOD_PRESSURE, HexDump.toByteArray(startTime))));
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("02", Config.BigData.APP_SPECIFY_BLOOD_PRESSURE, HexDump.toByteArray(startTime)));
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {

        String getResult = ByteUtil.getHexString(result);
        TLog.Companion.error("getResult++" + getResult);
        if (result[0] == Config.PRODUCT_CODE && result.length < 10)
            return false;
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                 //   ShowToast.INSTANCE.showToastLong("错误在于血压==" + result[10]);
                    BleWrite.writeSpecifyBloodPressureHistoryCall(startTime, endTime, mInterface); //重新发送的操作
                    isFinishFlag = true;
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }
        if (!uuid.equalsIgnoreCase(Config.readCharacterBig))
            return false;
        TLog.Companion.error("22222222222++" + getResult.length() / 2);
        //正常数据的
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.BigData.KEY && result[9] == Config.BigData.DEVICE_SPECIFY_BLOOD_PRESSURE) {
            num = HexDump.byte2intHigh(result, 3, 4);
            TLog.Companion.error("头的次数 SpecifyHeartRateHistoryCall +" + num);
            stringBuilder = new StringBuilder();
        } else {
            if (stringBuilder.length() == 0) {
                TLog.Companion.error("num==" + num + "  stringBuilder.length() ==" + stringBuilder.length() / 2);
                //不是头包数据，并且没有添加数据，说明不是这个 callback  需要处理的数据
                // 丢掉 ，给其他 callback 去判断
                return false;
            }
        }
        stringBuilder.append(getResult);
        TLog.Companion.error("stringBuilder++" + stringBuilder.length() / 2);
        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
            return true;
        }
        isFinishFlag = true;

        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
        if (data[9] == Config.BigData.DEVICE_SPECIFY_BLOOD_PRESSURE) {
            for (int i = 10; i < data.length; i += 2) {
                dataBean = new DataBean();
                dataBean.setSystolicBloodPressure(i);
                dataBean.setDiastolicBloodPressure(i + 1);
                mList.add(dataBean);
            }
            TLog.Companion.error("血压");
            mInterface.SpecifyBloodPressureHistoryCallResult(startTime, endTime, mList);
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
       // ShowToast.INSTANCE.showToastLong("血压超时 没重发 ");
    }

    private String keyValue(String key, byte[] keyValue) {
        return key +
                ByteUtil.getHexString(new byte[]{(byte) (keyValue.length)}) +
                ByteUtil.getHexString(keyValue);
    }
}
