package com.shon.connector.call.write.flash;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

/**
 * 3.6.9
 * APP 端设置设备 UID 特征
 */
public class FlashGetAssignCall extends WriteCallback {

    String key;

    public FlashGetAssignCall(String address) {
        super(address);
    }

    public FlashGetAssignCall(String address, int startAddress, int endAddress) {
        super(address);
        byte[] start = HexDump.toByteArrayLength(startAddress, 4);
        byte[] end = HexDump.toByteArrayLength(endAddress, 4);
        key = keyValue(start, end);
    }

    @Override
    public byte[] getSendData() {
        byte[] sendData = CmdUtil.getFullPackage(CmdUtil.getPlayer("08", "01", ByteUtil.hexStringToByte(key)));
        TLog.Companion.error("flash发送++" + ByteUtil.getHexString(sendData));
        return sendData;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("res==" + ByteUtil.getHexString(result));
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                    break;
                case 0x04:
                    //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }

//        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.BigData.KEY &&  result[9]==0x08) {
//            stringBuilder = new StringBuilder();
//            num = HexDump.byte2intHigh(result, 3, 4);
//            //      TLog.Companion.error("need  length ==" + num);
//        }
//        else {
//            if (stringBuilder.length() == 0) {
//                return false;
//            }
//        }
//        stringBuilder.append(getResult);
//        // TLog.Companion.error("current length == " + (stringBuilder.length() / 2 - Config.BYTE_HEAD) );
////        + "  ; stringBuilder=" + (stringBuilder.length() / 2 - Config.BYTE_HEAD));
//        if (num != (stringBuilder.length() / 2 - Config.BYTE_HEAD)) {
//            //长度不相等，说明还没有组包完成，
//            //这里需要添加一个变量，来标识是否组包完成，
//            //同时 重写  isFinish() ,返回该值，以避免没接收完，同时返回 onTimeout（）
//            // 这里返回了true，说明处理了此条数据，减少底层的循环次数
//            return true;
//        }
//        isFinishFlag=true;
////            if (num == (stringBuilder.length() / 2 - Config.BYTE_HEAD))//减去头部的8个字节
////            {
////        TLog.Companion.error("等长==");
//        byte[] data = ByteUtil.hexStringToByte(stringBuilder.toString());
//        //   TLog.Companion.error("keyValue  " + keyValue +" , result =  " + data[9] + "  is finish ");
//        for (int i = 10; i < data.length; i += 11) {
//            mTimeBean = new TimeBean();
//            mTimeBean.setDataUnitType(result[i]);
//            mTimeBean.setTimeInterval(HexDump.byte2intHigh(data, i + 1, 2));
////            mTimeBean.setStartTime(Config.TIME_START+HexDump.byte2intHigh(data, i + 3, 4));
////            mTimeBean.setEndTime(Config.TIME_START+HexDump.byte2intHigh(data, i + 7, 4));
//            mTimeBean.setStartTime( HexDump.byte2intHigh(data, i + 3, 4));
//            mTimeBean.setEndTime( HexDump.byte2intHigh(data, i + 7, 4));
////            TLog.Companion.error("time=" + new Gson().toJson(mTimeBean));
//            mList.add(mTimeBean);
//        }
//        //  TLog.Companion.error("发送指令+" + data[9]);
//        if (CmdUtil.isCurrentCmd(keyValue, data[9])) {
//            //数据处理完成，抛出结果。
////            TLog.Companion.error("进入");
//            mInterface.HistoryCallResult(data[9], mList);
//            return true;
//        }
        return false;
    }

    @Override
    public boolean isFinish() {
        return super.isFinish();
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error("time out 超时了呢");


    }

    private String keyValue(byte[] key, byte[] key1) {
        return ByteUtil.getHexString(key) +
                ByteUtil.getHexString(key1);

    }
}
