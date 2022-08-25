package com.shon.connector.call.write.deviceclass;

import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;

/**
 * 单次测量心率的开关，
 * Created by Admin
 * Date 2022/8/25
 */
public class DeviceSingleHtCall extends WriteCallback {

    private boolean isStartMeasure;

    public DeviceSingleHtCall(String address, boolean isStartMeasure) {
        super(address);
        this.isStartMeasure = isStartMeasure;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {
        byte[] mHt = new byte[]{Config.ControlClass.COMMAND,0x04, (byte) (isStartMeasure?0x02: 0x01)};
        return CmdUtil.getFullPackage(mHt);
    }
}
