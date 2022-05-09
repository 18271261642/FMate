package com.shon.connector.call.write.controlclass;


import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.Config;
import com.shon.connector.bean.DataBean;
import com.shon.connector.call.CmdUtil;

/**
 * 3.3.16
 * 设备自动测量开关
 */
public class AutomaticMeasurementSwitchCall extends WriteCallback {
//    BleWrite.AutomaticMeasurementSwitchInterface mInterface;
    byte keyValue;
    DataBean dataBean;
    public AutomaticMeasurementSwitchCall
            (String address, byte keyValue, DataBean dataBean
              /*      ,
             BleWrite.AutomaticMeasurementSwitchInterface mInterface*/) {
        super(address);
        this.keyValue = keyValue;
        this.dataBean=dataBean;
//        this.mInterface=mInterface;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {Config.ControlClass.COMMAND,keyValue, (byte) dataBean.getTimeMeasurementSwitch()
        , (byte) dataBean.getOpenHour(), (byte) dataBean.getOpenMin()
                , (byte) dataBean.getCloseHour(), (byte) dataBean.getCloseMin()
        };
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
