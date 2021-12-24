package com.shon.connector.call.write.flash;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;

/**
 * 3.10.5
 * 3.10.5 APP 端传输指定的 FLASH 数据块
 */
public class FlashWriteAssignCall extends WriteCallback {

    byte[] key;
    int size = 0;
    byte[] sendData;
    byte CRC;
    byte[] startKey;
    byte[] endKey;
    int length=0;
    int countSize=0;
    BleWrite.FlashWriteAssignInterface mInterface;

    public FlashWriteAssignCall(String address, byte[] flashAddress, byte[] startKey, byte[] endKey, int size,int length, byte CRC, BleWrite.FlashWriteAssignInterface mInterface) {
        super(address);
        key = flashAddress;
        this.size = size;
        this.mInterface = mInterface;
        this.CRC = CRC;
        this.startKey = startKey;
        this.endKey = endKey;
        this.length=length;
        countSize=10;//随意一个值防止第一次为0
        TLog.Companion.error("开始位置++="+ByteUtil.getHexString(startKey));
        TLog.Companion.error("结束位置++="+ByteUtil.getHexString(endKey));
    }
    public FlashWriteAssignCall(String address, byte[] flashAddress , int size,int countSize , BleWrite.FlashWriteAssignInterface mInterface) {
        super(address);
        key = flashAddress;
        this.size = size;
        this.mInterface = mInterface;
        this.countSize=countSize;
        TLog.Companion.error("进了几次+=" + size);

    }
    @Override
    public byte[] getSendData() {
        if (size == 0) {
            length+=17;
            byte [] noCRCData= CmdUtil.getFullPackage(CmdUtil.getPlayer("08", "05", ByteUtil.hexStringToByte(keyValue(startKey, endKey, key))), length, CRC);
            byte crc=HexDump.byteXOR(noCRCData);
            TLog.Companion.error("crc+"+crc);
            sendData =  CmdUtil.sendDataCRC(noCRCData,crc);
        }
        else {
            byte crc=HexDump.byteXOR(key);
//            TLog.Companion.error("else crc+"+crc);
            sendData = CmdUtil.sendDataCRC(key, crc); //小包加一个crc
        }
        TLog.Companion.error("flash发送++" + ByteUtil.getHexString(sendData));
        return sendData;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
//        TLog.Companion.error("res==" + ByteUtil.getHexString(result));
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
            TLog.Companion.error("属于++"+result[10]);
            switch (result[10]) {
                case 0x01:
                    mInterface.onResultFlash(size+1,-1);//每次出去就累计+1
                    if((countSize-1)>size) {
                        TLog.Companion.error("countSize=="+countSize);
                        TLog.Companion.error("size=="+size);
                        return true;
                    }
                    else
                    {
                        TLog.Companion.error("else countSize=="+countSize);
                        TLog.Companion.error("else size=="+size);
                    }
                    break;
                  //  return true;
                case 0x02:
                case 0x03:
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.Flash.KEY&&result[9] == Config.Flash.DEVICE_FLASH_OK_UPDATE) {
            TLog.Companion.error("返回正确值 +"+result[10]);
            mInterface.onResultFlash(-1,result[10]);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFinish() {
        return super.isFinish();
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error("time out 超时了呢");
        //    BleWrite.writeFlashWriteAssignCall(sendData,size,mInterface);
    }

    private String keyValue(byte[] startKey, byte[] endKey, byte[] sendData) {
        return "010009" +//索引,长度
                ByteUtil.getHexString(startKey) +//起始位
                ByteUtil.getHexString(endKey) +//结束位
                "0202FFFF" + //含crc效验包,索引2,俩个字节的长度
                ByteUtil.getHexString(sendData);
    }
}
