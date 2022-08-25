package com.shon.connector.call.write.deviceclass;

import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.call.listener.CommBackListener;
import com.shon.connector.utils.TLog;

/**
 * 设置温度开关
 * Created by Admin
 * Date 2022/8/25
 */
public class DeviceRingTempCall extends WriteCallback {

    private boolean isOpen;
    private CommBackListener commBackListener;

    public DeviceRingTempCall(String address, boolean isOpen,CommBackListener cm) {
        super(address);
        this.isOpen = isOpen;
        this.commBackListener = cm;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("-----设置戒指温度开关="+address+" "+ ByteUtil.getHexString(result)+" "+uuid);
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        if(commBackListener != null)
            commBackListener.commWriteBackData(true);
        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {
        byte[] tempByte = new byte[]{Config.ControlClass.COMMAND,0x1B, (byte) (isOpen?0x02: 0x01),0x00,0x00,0x00,0x00,0x3C};
        return CmdUtil.getFullPackage(tempByte);
    }
}
