package com.shon.connector.call.write.deviceclass;

import android.os.Handler;
import android.os.Looper;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;

/**
 * 3.2.15-3.2.16
 * uuid绑定
 */
public class UUIDBindCall extends WriteCallback {

    BleWrite.UUIDBindInterface mInterface;
    String uuid;

    public UUIDBindCall(String address, String uuid, BleWrite.UUIDBindInterface mInterface) {
        super(address);
        this.mInterface = mInterface;
        this.uuid = uuid;
    }

    @Override
    public byte[] getSendData() {
//        TLog.Companion.error(""+ByteUtil.getHexString(CmdUtil.getPlayer("00", "0f", ByteUtil.hexStringToByte(keyValue(uuid)))));
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("00", "0f", ByteUtil.hexStringToByte(keyValue(uuid))));
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("res=="+ByteUtil.getHexString(result));
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
        if (result[0] == Config.PRODUCT_CODE && result[8] == Config.getDevice.COMMAND && result[9] == Config.getDevice.DEVICE_UUID_BIND_KEY) {
            mInterface.UUIDBindResult(result[10]);
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
                new Handler( Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
        BleWrite.writeUUIDBind(uuid, mInterface);
                    }
        },200);
        TLog.Companion.error("time out 超时了呢");
    }

    private String keyValue(String key) {
        return "0000000000000000" + key;
    }
}
