package com.shon.connector.call.write.deviceclass;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

/**
 * 3.2.17-3.2.18
 * APP 端获取设备 FLASH 特征编号
 */
public class FlashGetFeaturesCall extends WriteCallback {
    BleWrite.FlashGetFeaturesInterface mInterface;

    public FlashGetFeaturesCall(String address, BleWrite.FlashGetFeaturesInterface mInterface) {
        super(address);
        this.mInterface=mInterface;
    }

    @Override
    public byte[] getSendData() {
        byte[] sendData = CmdUtil.getFullPackage(CmdUtil.getPlayer("00", "1100"));
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
            return false;
        }

        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.getDevice.COMMAND && result[9] == Config.getDevice.DEVICE_FLASH_FEATURES_KEY) {
          int ui=  HexDump.byte2intHigh(result,10,4);
          TLog.Companion.error("ui++"+ui);
            mInterface.onResultFeatures(ui);
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
