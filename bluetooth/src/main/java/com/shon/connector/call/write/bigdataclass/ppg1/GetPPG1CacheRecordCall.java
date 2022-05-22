package com.shon.connector.call.write.bigdataclass.ppg1;

import com.google.gson.Gson;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
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


    public GetPPG1CacheRecordCall(String address, OnPPG1CacheRecordListener onPPG1CacheRecordListener) {
        super(address);
        this.onPPG1CacheRecordListener = onPPG1CacheRecordListener;
        resultTimeList.clear();
        resultLongList.clear();
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("-----返回="+ByteUtil.getHexString(result)+"\n"+result.length);
        if(result.length>10 && result[8] == 0x02 && (result[9] == 0x12)){
            byte[] resultByte = new byte[result.length-10];
            System.arraycopy(result,10,resultByte,0,resultByte.length);

            TLog.Companion.error("------结果数组="+ ByteUtil.getHexString(resultByte)+"\n"+resultByte.length);

            for (int i = 0; i < resultByte.length; i += 11) {
                if (i + 6 < resultByte.length - 1) {
                    byte[] timeItemArr = new byte[4];
                    timeItemArr[0] = resultByte[i + 3];
                    timeItemArr[1] = resultByte[i + 4];
                    timeItemArr[2] = resultByte[i + 5];
                    timeItemArr[3] = resultByte[i + 6];
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
            TLog.Companion.error("-----结果集合="+resultTimeList.size()+" "+resultLongList.size()+" "+new Gson().toJson(resultTimeList));

        }


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
}
