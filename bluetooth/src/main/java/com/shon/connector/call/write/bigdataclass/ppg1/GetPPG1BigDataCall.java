package com.shon.connector.call.write.bigdataclass.ppg1;


import android.text.format.DateUtils;

import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.bluetooth.util.TimeU;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 获取指定时间的ppg缓存大数据
 */
public class GetPPG1BigDataCall extends WriteCallback {

    //需要获取的，发送使用时间戳 4个byte
    private byte[] itemTimeByte;
    private OnPPG1BigDataListener ppg1BigDataListener;
    //返回的时间,处理成yyyy-MM-dd HH:mm:ss格式
    private  String itemTimeStr;

    //总共长度的已处理完成的集合
    private final List<Integer> resultPPGList = new ArrayList<>();

    //设备返回单个item的总长度 是byte格式，4个byte一个10进制，将数据按此长度处理
    private int itemLength;

    private final StringBuilder stringBuilder = new StringBuilder();


    public GetPPG1BigDataCall(String address, byte[] itemTimeByte, OnPPG1BigDataListener ppg1BigDataListener) {
        super(address);
        this.itemTimeByte = itemTimeByte;
        this.ppg1BigDataListener = ppg1BigDataListener;
        itemTimeStr = null;
        itemLength = 0;
        resultPPGList.clear();

    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("----PPG大数据="+address+" "+ ByteUtil.getHexString(result)+" "+uuid);
        if(uuid.equalsIgnoreCase(Config.readCharacterBig)){
            if(result.length>17 && result[8] == 0x02 && result[9] == 0x14){
                if(itemLength == 0){    //第一个，后面拼接
                    //时间戳
                    int itemTime = HexDump.getIntFromBytes(result[14],result[15],result[16],result[17]);
                    long constanceMils = 946656000L;

                    //yyyy-MM-dd HH:mm:ss格式
                    itemTimeStr = TimeU.getCurrTime(((long) itemTime +constanceMils) * 1000);
                    TLog.Companion.error("---11--时间戳="+itemTimeStr);
                    //长度
                    itemLength = HexDump.getIntFromBytes(result[19],result[20]);

                }

                //非第一次，直接拼接
                byte[] contentByte = new byte[result.length-21];
                System.arraycopy(result,21,contentByte,0,contentByte.length);

                String contentStr = ByteUtil.getHexString(contentByte);
                stringBuilder.append(contentStr);


                TLog.Companion.error("---11----item的长度="+itemLength+" "+resultPPGList.size());

                return false;
            }

            //第二次的返回，将两次拼接
            stringBuilder.append(ByteUtil.getHexString(result));
            //转换成bytearray
            byte[] itemArray = ByteUtil.hexStringToByte(stringBuilder.toString());
            if(itemArray == null)
                return false;

            //一个包完整了，处理成10进制ppg
            for(int i = 0;i<itemArray.length;i+=4){
                if(i+3<itemArray.length){
                    //TLog.Companion.error("----itemdange="+ByteUtil.getHexString(new byte[]{itemArray[i],itemArray[i+1],itemArray[i+2],itemArray[i+3]}));
                    int ppgValue = HexDump.getIntFromBytes(itemArray[i],itemArray[i+1],itemArray[i+2],itemArray[i+3]);
                    resultPPGList.add(ppgValue);
                    if(resultPPGList.size() == itemLength)
                        break;
                }
            }

            stringBuilder.delete(0,stringBuilder.length());

            TLog.Companion.error("---22----item的长度="+itemLength+" "+resultPPGList.size());

            if(resultPPGList.size()  == itemLength /4){
                if(ppg1BigDataListener != null){
                    TLog.Companion.error("---22--时间戳="+itemTimeStr);
                    ppg1BigDataListener.backPPG1BigData(resultPPGList,itemTimeStr);
                }

                itemLength= 0;
                return true;
            }

        }


        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {
        byte[] cmdByte = new byte[]{0x02,0x13};
        byte[] resultByte = new byte[6];
        System.arraycopy(cmdByte,0,resultByte,0,cmdByte.length);
        System.arraycopy(itemTimeByte,0,resultByte,2,itemTimeByte.length);
        return CmdUtil.getFullPackage(resultByte);
    }
}
