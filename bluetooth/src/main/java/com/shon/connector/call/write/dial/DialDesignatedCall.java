package com.shon.connector.call.write.dial;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

/**

 * Designated dial
 */
public class DialDesignatedCall extends WriteCallback {
    BleWrite.DialDesignatedInterface mInterface;
    long id;
    public DialDesignatedCall(String address,long id, BleWrite.DialDesignatedInterface mInterface) {
        super(address);
        this.mInterface = mInterface;
        this.id=id;
    }


    @Override
    public byte[] getSendData() {

        byte[] sendData = CmdUtil.getFullPackage(CmdUtil.getPlayer("09", "07",HexDump.toByteArray(id)));
        TLog.Companion.error("flash发送++" + ByteUtil.getHexString(sendData));
        return sendData;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("res==" + ByteUtil.getHexString(result));
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
        if (result[8] == Config.Dial.KEY && result[9] == Config.Dial.DEVICE_DIAL_DESIGNATED) {
            mInterface.onResultDialDesignated(result[10]);
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
        TLog.Companion.error("time out 超时了呢");
    }

}
