package com.shon.connector.call.write.controlclass;

import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.bean.AutoBpStatusBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.call.listener.CommBackListener;
import com.shon.connector.utils.TLog;

/**
 * 设置血压自动测量状态，夜间自动测量或非夜间自动测量
 * Created by Admin
 * Date 2022/5/8
 */
public class SetBpAutoMeasureCall extends WriteCallback {

    private AutoBpStatusBean autoBpStatusBean;
    private CommBackListener commBackListener;


    public SetBpAutoMeasureCall(String address, AutoBpStatusBean autoBpStatusBean, CommBackListener commBackListener) {
        super(address);
        this.autoBpStatusBean = autoBpStatusBean;
        this.commBackListener = commBackListener;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("-----设置血压自动测量状态="+address+" "+ ByteUtil.getHexString(result)+" "+uuid);
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;


        if (result[8] == 0x07 && result[9] == Config.Expand.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:
                    if(commBackListener != null)
                        commBackListener.commWriteBackData(true);
                    break;
                case 0x02:
                case 0x03:
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                    break;
                case 0x04:
                    //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {

        byte[] statusByte = new byte[]{Config.ControlClass.COMMAND,0x12,autoBpStatusBean.getNightBpStatus(),
                autoBpStatusBean.getNormalBpStatus(), (byte) autoBpStatusBean.getStartHour(), (byte) autoBpStatusBean.getStartMinute(),
                (byte) autoBpStatusBean.getEndHour(), (byte) autoBpStatusBean.getEndMinute(), (byte) autoBpStatusBean.getBpInterval()};
        return CmdUtil.getFullPackage(statusByte);
    }
}
