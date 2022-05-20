package com.shon.connector.call.write.bigdataclass;


import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.bluetooth.util.TimeU;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.call.listener.MeasureBigBpListener;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;
import java.util.List;


//测量血压
public class MeasureBpCall extends WriteCallback {


    private int measureKey;
    private MeasureBigBpListener measureBigBpListener;

    private final StringBuilder stringBuilder = new StringBuilder();
    private List<Integer> bpList = new ArrayList<>();

    private String measureTime;

    private int itemLength;

    public MeasureBpCall(String address, int measureKey, MeasureBigBpListener measureBigBpListener) {
        super(address);
        this.measureKey = measureKey;
        this.measureBigBpListener = measureBigBpListener;
        stringBuilder.delete(0,stringBuilder.length());
        bpList.clear();
        measureTime = null;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("----测量血压="+address+" "+ ByteUtil.getHexString(result)+" "+uuid);
//        if (!uuid.equalsIgnoreCase(Config.readCharacterBig))
//            return false;
//        TLog.Companion.error("---222-测量血压="+address+" "+ ByteUtil.getHexString(result)+" "+uuid);



        if (uuid.equalsIgnoreCase(Config.readCharacter)){
            if (result[8] == 0x0B && result[9] == Config.Expand.DEVICE_ACK){
                /**
                 * 1 byte
                 * 状态
                 * 0x00: 默认
                 * 0x01: 结束 PPG1 Raw Data 上传，流程终止
                 * 0x02: 开启 PPG1 Raw Data 上传 （重置发送时间为 0）
                 * 0x03: 设备端发送时间超出 2 分钟，APP 端尚未发回结束，流程终止
                 * 0x04: 申报需要上传，如需开始上传，APP 务必调用接口 3.13.1 索引 0x01，状态 0x02 下发
                 * 0x05: 申报需要上传，如需开始上传，APP 务必调用接口 3.13.1 索引 0x01，状态 0x03 下发
                 */
                switch (result[13]) {
                    case 0x02: //开始计时
                        if(measureBigBpListener != null)
                            measureBigBpListener.measureStatus(0x02);
                        break;

                    case 0x03:
                        //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                        break;
                    case 0x04:
                        ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                        break;
                }

            }

        }
        if (!uuid.equalsIgnoreCase(Config.readCharacterBig))
            return false;

        if(uuid.equalsIgnoreCase(Config.readCharacterBig)){ //血压大数据返回
            String byStr = ByteUtil.getHexString(result);

            if(result[8] == 0x03 && result[9] == 0x0C){
            //88 00 00 00 00 01 9E 3D 03 0C 01 00 06 04 01 2A 0D EB 9A ==18个长度
                byte[] validByte = new byte[result.length-22];
                System.arraycopy(result,22,validByte,0,validByte.length-1);
                //stringBuilder.append(byStr.substring(18 * 2,byStr.length()));
                if(bpList.size() == 0){
                    int tim = HexDump.getIntFromBytes(result[15],result[16],result[17],result[18]);

                    int tim2 = HexDump.getIntFromBytes(result[18],result[17],result[16],result[15]);
                    TLog.Companion.error("-----返回的时间="+tim);

                    long constanceMils = 946656000L;
                    long currLong = System.currentTimeMillis()/1000;
                    if((tim+constanceMils)<currLong-10)
                        tim = (int) (currLong-constanceMils);

                    measureTime = TimeU.getCurrTime((long) tim +constanceMils);

                    TLog.Companion.error("-----返回的测量时间="+measureTime+" "+((long) tim +constanceMils));
                }

                //长度
                itemLength = HexDump.getIntFromBytes(result[20],result[21]);

                TLog.Companion.error("------数组长度="+itemLength);
                byte[] firstValidByte = new byte[result.length-22];
                System.arraycopy(result,22,firstValidByte,0,firstValidByte.length-1);
                stringBuilder.append(ByteUtil.getHexString(firstValidByte));

                TLog.Companion.error("----11--结果血压大小="+bpList.size());
                return false;
            }
            //第二次同步
            stringBuilder.append(ByteUtil.getHexString(result));
            //转换成bytearray
            byte[] itemArray = ByteUtil.hexStringToByte(stringBuilder.toString());
            if(itemArray == null)
                return false;
//            if(itemLength == 0 || itemArray.length != itemLength)
//                return false;
            //一个包完整了
            for(int i = 0;i<itemArray.length;i+=4){
                if(i+3<itemArray.length){
                    int bpValue = HexDump.getIntFromBytes(itemArray[i],itemArray[i+1],itemArray[i+2],itemArray[i+3]);
                    bpList.add(bpValue);
                    if(bpList.size()==6144){

                        break;
                    }
                }
            }

            itemLength = 0;
            stringBuilder.delete(0,stringBuilder.length());


            TLog.Companion.error("---22-结果血压大小="+bpList.size());

            if(bpList.size() == 6144){  //已经取完整
                if(measureBigBpListener != null)
                    measureBigBpListener.measureBpResult(bpList,measureTime);

                return true;
            }

        }


        return false;
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error("------血压-超时");
    }

    @Override
    public byte[] getSendData() {

        /**
         *0x00: 默认
         * 0x01: 结束 PPG1 Raw Data 上传
         * 0x02: 开启 PPG1 Raw Data 上传，设备端不显示测量界面
         * 0x03: 开启 PPG1 Raw Data 上传，设备端显示测量界面
         * 0x04: APP 获取服务器结果超出 2 分钟，流程终止
         * 0x05: 连接第三方服务器异常，流程终止
         * 0x06: 连接公司服务器异常，流程终止
         * 0x07: 此次测量服务器返回的结果，值无异常，索引 2 跟随使用，流程终止
         * 0x08：此次测量服务器返回的结果，信号不良或者其他异常，流程终止
         * 0x09：APP 端需要发送一条最近的血压记录时，服务器有记录，索引 2 跟随使用，流程终止
         * 0x0A：APP 端需要发送一条最近的血压记录时，服务器无记录，流程终止
         * 0x0B：APP 端发送当前次的校准结果，流程终止
         */

        byte[] cmdArray = new byte[]{0x0B,0x01,0x01,0x00,0x01, (byte) measureKey};

        byte[] resultArray = CmdUtil.getFullPackage(cmdArray);

        return resultArray;
    }
}
