package com.shon.connector.call.write.controlclass;

import android.util.Log;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;

/**
 * 3.3.2
 * 进入 OTA 升级模式
 */
public class OTAUpdateCall extends WriteCallback {
    BleWrite.BleInterface mInterface;

    public OTAUpdateCall(String address) {
        super(address);
    }

    public OTAUpdateCall(String address, BleWrite.BleInterface mInterface) {
        super(address);
        this.mInterface=mInterface;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01, 0x02, 0x00};

        byte[] intoDuf = CmdUtil.getFullPackage(payload);
        Log.e("OTA","------进入OTA模式="+ByteUtil.getHexString(intoDuf));
        return intoDuf;
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        TLog.Companion.error("result++" + ByteUtil.getHexString(result));

        if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
            TLog.Companion.error("DFUActivity 返回值++" + ByteUtil.getHexString(result));
            switch (result[10]) {
                case 0x01:
                    if(mInterface!=null)
                        mInterface.onResult();
                    return true;
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
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
