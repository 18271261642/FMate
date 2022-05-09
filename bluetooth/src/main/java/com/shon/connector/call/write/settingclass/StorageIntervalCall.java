package com.shon.connector.call.write.settingclass;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.bean.DataBean;

import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.6.5
 * APP 设置设备大数据存储间隔
 */
public class StorageIntervalCall extends WriteCallback {
    DataBean mDataBean;
    String value;
    public StorageIntervalCall(String address, DataBean mDataBean) {
        super(address);
        this.mDataBean = mDataBean;
        value=   keyValue( HexDump.toByteArray((short) mDataBean.getHeartRate()),
                HexDump.toByteArray((short) mDataBean.getHeartRate()),
                HexDump.toByteArray((short) mDataBean.getBloodOxygen()),
                HexDump.toByteArray((short) mDataBean.getBloodPressure()),
                HexDump.toByteArray(Short.parseShort(mDataBean.getTemperature()))
                , HexDump.toByteArray((short) mDataBean.getActivity())
                );
    }

    @Override
    public byte[] getSendData() {
//        if(num==0) {
//            byte xor = HexDump.byteXOR(key);
//
//            byte[] head = {Config.PRODUCT_CODE, 0x00, 0x01, 0x00, 0x00, 0x00, 0x06, xor, 0x04, 0x05, 0x00, 0x0C};
//            sendData = HexDump.byteMerger(head, HexDump.toByteArray((short) mDataBean.getHeartRate()));//心率
//            sendData = HexDump.byteMerger(sendData, HexDump.toByteArray((short) mDataBean.getBloodOxygen())); //血氧
//            sendData = HexDump.byteMerger(sendData, HexDump.toByteArray((short) mDataBean.getBloodPressure()));//血压
//            sendData = HexDump.byteMerger(sendData, HexDump.toByteArray(Short.parseShort(mDataBean.getTemperature())));//温度
//        }
//        else {
//            sendData = HexDump.byteMerger( HexDump.toByteArray((short) mDataBean.getPedometer()),  HexDump.toByteArray((short) mDataBean.getSleep()));
//            HexDump.toByteArray((short) mDataBean.getPedometer()); //计步
//            HexDump.toByteArray((short) mDataBean.getSleep());//睡眠
//        }
        return CmdUtil.getFullPackage(
                CmdUtil.getPlayer(Config.SettingDevice.command,
                Config.SettingDevice.APP_BIG_DATA_STORAGE_INTERVAL,
                ByteUtil.hexStringToByte(value)));
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        TLog.Companion.error("获取到最终的数据长度_++" + result);//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    //mInterface.DeviceInformationCallResult();
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
    private String keyValue(byte[] key, byte[] key1, byte[] key2, byte[] key3, byte[] key4, byte[] key5) {
        return
                ByteUtil.getHexString(key) +
                        ByteUtil.getHexString(key1) +
                        ByteUtil.getHexString(key2) +
                        ByteUtil.getHexString(key3) +
                        ByteUtil.getHexString(key4) +
                        ByteUtil.getHexString(key5);
    }
}
