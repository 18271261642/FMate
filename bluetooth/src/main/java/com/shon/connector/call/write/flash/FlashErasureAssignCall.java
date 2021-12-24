package com.shon.connector.call.write.flash;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

/**
 * 3.10.3-3.10.4
 * APP 端设擦写设备端指定的 FLASH 数据块
 */
public class FlashErasureAssignCall extends WriteCallback {

    String key;
    BleWrite.FlashErasureAssignInterface mInterface;

    public FlashErasureAssignCall(String address) {
        super(address);
    }

    public FlashErasureAssignCall(String address, int startAddress, int endAddress, BleWrite.FlashErasureAssignInterface mInterface) {
        super(address);
        byte[] start = HexDump.toByteArrayLength(startAddress, 4);
        byte[] end = HexDump.toByteArrayLength(endAddress, 4);

        this.mInterface=mInterface;
        key = keyValue(start, end);
        TLog.Companion.error("结束位置++="+ key);
    }

    @Override
    public byte[] getSendData() {
        byte[] sendData = CmdUtil.getFullPackage(CmdUtil.getPlayer("08", "03", ByteUtil.hexStringToByte(key)));
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
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.Flash.KEY&&result[9] == Config.Flash.DEVICE_ERASURE) {
            TLog.Companion.error("返回正确值 +"+result[10]);
            mInterface.onResultErasure(result[10]);
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


    }

    private String keyValue(byte[] key, byte[] key1) {
        return ByteUtil.getHexString(key) +
                ByteUtil.getHexString(key1);

    }
}
