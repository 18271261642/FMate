package com.shon.connector.call.write.controlclass;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.TimeBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.TLog;

/**
 * 3.3.23
 * 设备心率开关
 */
public class HeartRateAlarmSwitchCall extends WriteCallback {
    int num=0;
    int mSwitch=0;
    public HeartRateAlarmSwitchCall(String address,int mSwitch,int num ) {
        super(address);
        this.num=num;
        this.mSwitch=mSwitch;
    }
    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01,0x17, (byte) mSwitch, (byte) ByteUtil.cbyte2Int((byte) num)};
        TLog.Companion.error("==="+ ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        TLog.Companion.error("心率报警开关");
            if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
                switch (result[10]) {
                    case 0x01:
                        return true;
                     //   break;
                    case 0x02:
                    case 0x03:
                        BleWrite.writeHeartRateAlarmSwitchCall(mSwitch,num); //重新发送的操作
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
    public void onTimeout() {

       // BleWrite.writeHeartRateAlarmSwitchCall(mSwitch,num); //重新发送的操作
    }
}
