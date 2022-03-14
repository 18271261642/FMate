package com.shon.connector.call.write.settingclass;

import com.example.xingliansdk.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;

public class TestWeatherCall extends WriteCallback {

    private byte[] bleFex;


    public TestWeatherCall(String address, byte[] bleFex) {
        super(address);
        this.bleFex = bleFex;

    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
//        TLog.Companion.error("获取到最终的数据长度_++" + ByteUtil.getHexString(result));//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    // ShowToast.INSTANCE.showToastLong("设置成功");
                    break;
                case 0x02:
                case 0x03:
                    // BleWrite.writeTimeCall(mSettingTimeBean); //重新发送的操作
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

    }

    @Override
    public byte[] getSendData() {
        return bleFex;
    }
}
