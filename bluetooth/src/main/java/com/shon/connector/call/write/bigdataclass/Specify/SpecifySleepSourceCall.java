package com.shon.connector.call.write.bigdataclass.Specify;

import android.util.Log;

import com.google.gson.Gson;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.SpecifySleepSourceBean;
import com.shon.connector.utils.HexDump;

/**
 * 用于处理睡眠大数据缓存数据
 * Created by Admin
 * Date 2022/3/17
 */
public class SpecifySleepSourceCall extends WriteCallback {

    private static final String TAG = "SpecifySleepSourceCall";


    private BleWrite.SpecifySleepSourceInterface specifySleepSourceInterface;
    private long startTime;
    private long endTime;

    private final StringBuilder stringBuilder = new StringBuilder();
    //总包长度
    int allPackCount = 0;
    //心率长度
    int heartCount;


    private byte[] resultByte;


    public SpecifySleepSourceCall(String address, BleWrite.SpecifySleepSourceInterface specifySleepSourceInterface, long startTime, long endTime, byte[] resultByte) {
        super(address);
        this.specifySleepSourceInterface = specifySleepSourceInterface;
        this.startTime = startTime;
        this.endTime = endTime;
        this.resultByte = resultByte;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacterBig))
            return false;
        String getResult = ByteUtil.getHexString(result);
        Log.e(TAG,"-----getResult="+getResult);
        if (result[0]==Config.PRODUCT_CODE&&result[8] == Config.BigData.KEY && ((result[9] & 0xff)== 64)){  //第一个包
            stringBuilder.delete(0,stringBuilder.length());
            allPackCount = HexDump.getIntFromBytes(result[3],result[4],result[5],result[6]);
            //心率的长度
            heartCount = HexDump.getIntFromBytes(result[11],result[12]);

            Log.e(TAG,"------总长度="+allPackCount+" 心率长度="+heartCount);
        }
        stringBuilder.append(getResult);
        Log.e(TAG,"------string长度="+stringBuilder.length());

        if(stringBuilder.length() / 2 > allPackCount){  //已经同步返回完了
            analysisSleepSource(stringBuilder.toString(),specifySleepSourceInterface);
        }
        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {
        return resultByte;
    }


    private void analysisSleepSource(String sourceStr, BleWrite.SpecifySleepSourceInterface sourceInterface){
        try {
            byte[] sourceArray = ByteUtil.hexStringToByte(sourceStr);
            if(sourceArray == null || sourceArray.length<allPackCount)
                return;
            //睡眠的长度
            byte[] heartByte = new byte[1443];
            System.arraycopy(sourceArray,10,heartByte,0,heartByte.length);
            int[] resultHeartArray = new int[heartByte.length];
            for(int i = 0;i<heartByte.length;i++){
                resultHeartArray[i] = heartByte[i] & 0xff;
            }

            byte[] sleepByte = new byte[sourceArray.length-13-heartByte.length];
            System.arraycopy(sourceArray,13+heartByte.length-3,sleepByte,0,sleepByte.length-1);

            int[] resultSleepArray = new int[sleepByte.length];
            for(int k = 0;k<sleepByte.length;k++){
                resultSleepArray[k] = sleepByte[k] & 0xff;
            }


            SpecifySleepSourceBean specifySleepSourceBean = new SpecifySleepSourceBean();
            specifySleepSourceBean.setAvgActive(resultHeartArray);
            specifySleepSourceBean.setAvgHeartRate(resultSleepArray);
            specifySleepSourceBean.setStartTime(startTime);
            specifySleepSourceBean.setEndTime(endTime);
            specifySleepSourceBean.setRemark("remark");
            Log.e(TAG,"-----心率长度="+resultHeartArray.length+" 睡眠长度="+resultSleepArray.length);
            Log.e(TAG,"------结果="+new Gson().toJson(resultHeartArray)+"\n"+new Gson().toJson(resultSleepArray));

            if(sourceInterface !=null){
                sourceInterface.backSpecifySleepSourceBean(specifySleepSourceBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
