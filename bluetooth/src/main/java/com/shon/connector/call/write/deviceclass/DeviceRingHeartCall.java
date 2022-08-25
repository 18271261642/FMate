package com.shon.connector.call.write.deviceclass;

import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.call.listener.CommBackListener;
import com.shon.connector.utils.TLog;

/**
 * 戒指心率周期测量
 * Created by Admin
 * Date 2022/8/25
 */
public class DeviceRingHeartCall extends WriteCallback {

    private boolean isOpen;

    private CommBackListener commBackListener;

    public DeviceRingHeartCall(String address,boolean isOpen,CommBackListener cm) {
        super(address);
        this.isOpen = isOpen;
        this.commBackListener = cm;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("-----设置戒指开关="+address+" "+ ByteUtil.getHexString(result)+" "+uuid);
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
        byte[] htByte = new byte[]{Config.ControlClass.COMMAND,0x1A, (byte) (isOpen?0x02: 0x01),0x00,0x00,0x00,0x00,0x3C};
        return CmdUtil.getFullPackage(htByte);
    }
}
