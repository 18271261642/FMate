package com.shon.connector.call.write.controlclass;
import com.shon.connector.utils.ShowToast;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.bean.TimeBean;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.3.14
 * 提醒喝水设备开关
 */
public class ReminderDrinkWaterCall extends WriteCallback {
    byte[] keyValue;
    TimeBean mTimeBean;

    public ReminderDrinkWaterCall(String address, TimeBean mTimeBean) {
        super(address);
        this.mTimeBean = mTimeBean;

    }

    public ReminderDrinkWaterCall(String address, byte[] keyValue) {
        super(address);
        this.keyValue = keyValue;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x01, 0x0E, (byte) mTimeBean.getSwitch(), (byte) mTimeBean.getSpecifiedTime()
                , (byte) mTimeBean.getOpenHour(), (byte) mTimeBean.getOpenMin()
                , (byte) mTimeBean.getCloseHour(), (byte) mTimeBean.getCloseMin()
                , (byte) mTimeBean.getReminderInterval()};

        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        if (result[8] == 0x07 && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    return true;
//                        break;
                case 0x02:
                case 0x03:
                    BleWrite.writeReminderDrinkWaterCall(mTimeBean); //重新发送的操作
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
