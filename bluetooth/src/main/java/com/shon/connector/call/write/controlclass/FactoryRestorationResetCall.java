package com.shon.connector.call.write.controlclass;
import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.3.1
 * 恢复出厂设置并重启
 */
public class FactoryRestorationResetCall extends WriteCallback {

    public FactoryRestorationResetCall(String address) {
        super(address);
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01,0x01,0x00};//由 command(1byte) key(1byte) keyValueLeng(2byte)以及keyValue(长度不定)
        TLog.Companion.error("=="+ ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    break;
                case 0x02:
                case 0x03:
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                    break;
                case 0x04:
                   // ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onTimeout() {

    }
}
