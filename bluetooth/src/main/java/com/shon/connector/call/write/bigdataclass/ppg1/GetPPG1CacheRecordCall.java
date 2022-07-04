package com.shon.connector.call.write.bigdataclass.ppg1;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.bluetooth.util.TimeU;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取PPG1缓存大数据记录
 */
public class GetPPG1CacheRecordCall extends WriteCallback {

    private OnPPG1CacheRecordListener onPPG1CacheRecordListener;

    private final List<byte[]> resultTimeList = new ArrayList<>();
    private final List<Long> resultLongList = new ArrayList<>();

    //长度
    private int length;

    //用于记录PPG缓存大数据目录的log
    private final StringBuilder stringBuilder = new StringBuilder();

    private final StringBuilder sourceBuilder = new StringBuilder();


    public GetPPG1CacheRecordCall(String address, OnPPG1CacheRecordListener onPPG1CacheRecordListener) {
        super(address);
        this.onPPG1CacheRecordListener = onPPG1CacheRecordListener;
        resultTimeList.clear();
        resultLongList.clear();
        sourceBuilder.delete(0,sourceBuilder.length());
        stringBuilder.delete(0,stringBuilder.length());
        length = 0;
        Hawk.put("ppg_cache","");
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("-----返回="+ByteUtil.getHexString(result)+"\n"+result.length);
        stringBuilder.append(TimeU.getCurrTime(System.currentTimeMillis())+" 原始返回="+ByteUtil.getHexString(result)+" 长度="+result.length+"\n");
        boolean isResponse = result.length>12 && result[11] == 0x02 && (result[12] == 0x11);
        if(isResponse)
            return false;

        if(result.length>12 && result[8] == 0x02 && (result[9] == 0x12)){
            //长度 05,06
           length = HexDump.getIntFromBytes(result[5],result[6]);
           length = length-2;

            byte[] resultByte = new byte[result.length-10];
            System.arraycopy(result,10,resultByte,0,resultByte.length);

            String str = HexDump.bytesToString(resultByte);
            sourceBuilder.append(str);
            TLog.Companion.error("------结果数组="+ ByteUtil.getHexString(resultByte)+"\n"+resultByte.length);
            stringBuilder.append("首包="+length+" 长度="+resultByte.length+"\n");

            return false;
        }
//        if(length == 0){    //没有数据
//            Hawk.put("ppg_cache",stringBuilder.toString());
//            return true;
//        }



        sourceBuilder.append(ByteUtil.getHexString(result));

        TLog.Companion.error("----判断长度="+sourceBuilder.length() +" "+length * 2);

        if(sourceBuilder.length() == length * 2){   //已经完整了

            byte[] sourArray = HexDump.hexStringToByteArray(sourceBuilder.toString());
            for (int i = 0; i < sourArray.length; i += 11) {
                if (i + 6 < sourArray.length - 1) {
                    byte[] timeItemArr = new byte[4];
                    timeItemArr[0] = sourArray[i + 3];
                    timeItemArr[1] = sourArray[i + 4];
                    timeItemArr[2] = sourArray[i + 5];
                    timeItemArr[3] = sourArray[i + 6];
                    resultTimeList.add(timeItemArr);

                    //转换成正常的时间，2000-01-01之后的时间
                    int timeItem = HexDump.getIntFromBytes(timeItemArr[0], timeItemArr[1], timeItemArr[2], timeItemArr[3]);
                    resultLongList.add((long) timeItem);
                }
            }


            if (onPPG1CacheRecordListener != null) {
                onPPG1CacheRecordListener.backPPGCacheByteArray(resultTimeList);
                onPPG1CacheRecordListener.backPPGCacheLongArray(resultLongList);
                onPPG1CacheRecordListener.backPPGCacheArray(resultTimeList,resultLongList);
            }
            String str = resultTimeList.size()+" "+resultLongList.size()+" "+new Gson().toJson(resultTimeList);

            stringBuilder.append("第二段="+str+'\n');
            TLog.Companion.error("-----结果集合="+str);
            Hawk.put("ppg_cache",stringBuilder.toString());
            return true;
        }

        stringBuilder.append("长度="+stringBuilder.length()+'\n');
        Hawk.put("ppg_cache",stringBuilder.toString());
        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {
        byte[] bpCmd = new byte[]{0x02,0x11,0x00};
        return CmdUtil.getFullPackage(bpCmd);
    }


    public  String getStrLog(){
        return stringBuilder.toString();
    }

    public void clearLog(){
        stringBuilder.delete(0,stringBuilder.length());
    }
}
