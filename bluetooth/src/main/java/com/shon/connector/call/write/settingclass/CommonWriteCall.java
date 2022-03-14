package com.shon.connector.call.write.settingclass;

import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 直接写byte的指令
 */
public class CommonWriteCall extends WriteCallback {

    private byte[] writeByte;


    public CommonWriteCall(String address, byte[] writeByte) {
        super(address);
        this.writeByte = writeByte;
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
        return writeByte;
    }
}
