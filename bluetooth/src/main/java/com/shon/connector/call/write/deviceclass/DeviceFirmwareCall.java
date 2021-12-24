package com.shon.connector.call.write.deviceclass;


import com.example.xingliansdk.utils.ShowToast;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

/**
 * 3.2.1-3.2.2
 * 获取设备固件信息
 */
public class DeviceFirmwareCall extends WriteCallback {
    private BleWrite.FirmwareInformationInterface mFirmwareInformationInterface;
    int num = 0;
    boolean isFinishFlag=false;
    int test= 16777215;
    /**
     * @param address
     * @param mFirmwareInformationInterface
     */
    public DeviceFirmwareCall(String address, BleWrite.FirmwareInformationInterface mFirmwareInformationInterface) {
        super(address);
        this.mFirmwareInformationInterface = mFirmwareInformationInterface;
    }

    @Override
    public byte[] getSendData() {
        byte payload[] = {0x00, 0x01};
        return CmdUtil.getFullPackage(payload);

    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

//        String getResult = ByteUtil.getHexString(result);
        if (result.length<10)
            return false;
            if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
                switch (result[10]) {
                    case 0x01:

                        break;
                    case 0x02:
                    case 0x03:
                        BleWrite.writeForGetFirmwareInformation(mFirmwareInformationInterface,true); //重新发送的操作
                        break;
                    case 0x04:
                        ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                        break;
                }
                return false;
            }
            if(result[0] == Config.PRODUCT_CODE &&result[8]==Config.getDevice.COMMAND&&result[9]==Config.getDevice.DEVICE_FIRMWARE_KEY) {
                isFinishFlag=true;
                num = HexDump.byte2intHigh(result, 3, 4);
                String productNumber = "", versionName = "", nowMaC = "", mac = "";
                int version;
                TLog.Companion.error("="+((ByteUtil.cbyte2Int(result[10]) << 8)));
                TLog.Companion.error("="+((ByteUtil.cbyte2Int(result[11]) )));
                TLog.Companion.error("="+(ByteUtil.getHexString(result[10])+ByteUtil.getHexString(result[11])));
               // productNumber = String.valueOf((ByteUtil.cbyte2Int(result[10]) << 8) +ByteUtil.cbyte2Int(result[11]));//产品编号
                productNumber =(ByteUtil.getHexString(result[10])+ByteUtil.getHexString(result[11]));//产品编号
                versionName = "V" +  ByteUtil.cbyte2Int(result[12]) + "." + ByteUtil.cbyte2Int(result[13]) + "." + ByteUtil.cbyte2Int(result[14]);//获取版本号
                version= (ByteUtil.cbyte2Int(result[12])<<16) + (ByteUtil.cbyte2Int(result[13])<<8)  + ByteUtil.cbyte2Int(result[14]);
                nowMaC = ByteUtil.getHexString(result[15])+":"+ByteUtil.getHexString(result[16])+":"+ByteUtil.getHexString(result[17])+":"
                        +ByteUtil.getHexString(result[18])+":"+ByteUtil.getHexString(result[19])+":"+ByteUtil.getHexString(result[20]);
                mFirmwareInformationInterface.onResult(productNumber,versionName, version, nowMaC, mac);
                return true;
            }
            return false;
    }

    @Override
    public boolean isFinish() {
//        if (i >= 2)
//            return true;

        return isFinishFlag;
    }

    @Override
    public void onTimeout() {

    }
}
