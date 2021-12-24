package com.shon.connector.call.write.controlclass;
import com.example.xingliansdk.utils.ShowToast;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.bean.TimeBean;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.utils.TLog;

/**
 * 3.3.9
 * 设备勿扰模式开关
 */
public class DoNotDisturbModeSwitchCall extends WriteCallback {
    TimeBean mTimeBean;
    BleWrite.DoNotDisturbModeSwitchCallInterface mInterface;
    public DoNotDisturbModeSwitchCall(String address) {
        super(address);
    }
    public DoNotDisturbModeSwitchCall(String address, TimeBean mTimeBean, BleWrite.DoNotDisturbModeSwitchCallInterface mInterface) {
        super(address);
        this.mTimeBean=mTimeBean;
        this.mInterface=mInterface;
        TLog.Companion.error("进入勿扰");
    }
    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01,0x09,(byte) mTimeBean.getSwitch(), (byte) mTimeBean.getOpenHour(), (byte) mTimeBean.getOpenMin(), (byte) mTimeBean.getCloseHour(), (byte) mTimeBean.getCloseMin()};
//        TLog.Companion.error("勿扰==="+ ByteUtil.getHexString(CmdUtil.getFullPackage(payload)));
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        TLog.Companion.error("勿扰==+"+ByteUtil.getHexString(result));
            if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
                TLog.Companion.error("");
                switch (result[10]) {
                    case 0x01:
                        TLog.Companion.error("已经返回了");
                        return true;
//                        break;
                    case 0x02:
                    case 0x03:
                        BleWrite.writeDoNotDisturbModeSwitchCall(mTimeBean,mInterface); //重新发送的操作
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
}
