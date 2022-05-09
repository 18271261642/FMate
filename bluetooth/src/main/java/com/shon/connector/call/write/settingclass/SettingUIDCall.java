package com.shon.connector.call.write.settingclass;

import android.os.Handler;
import android.os.Looper;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.TLog;

/**
 * 3.6.9
 * APP 端设置设备 UID 特征
 */
public class SettingUIDCall extends WriteCallback {


    public SettingUIDCall(String address ) {
        super(address);
    }

    @Override
    public byte[] getSendData() {
//        TLog.Companion.error(""+ByteUtil.getHexString(CmdUtil.getPlayer("04", "09")));
        return CmdUtil.getFullPackage(CmdUtil.getPlayer("04", "09"));
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
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                BleWrite.writeSettingUID();
            }
        }, 200);

        TLog.Companion.error("time out 超时了呢");
    }
}
