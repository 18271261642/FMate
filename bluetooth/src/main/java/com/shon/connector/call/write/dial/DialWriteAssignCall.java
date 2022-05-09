package com.shon.connector.call.write.dial;

import com.shon.connector.utils.ShowToast;
import com.google.gson.Gson;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.DialCustomBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

/**
 *
 *  3.11.3 APP 端写入设备端指定非固化表盘概要信息
 */
public class DialWriteAssignCall extends WriteCallback {

    byte[] sendData;
    BleWrite.DialWriteInterface mInterface;
    DialCustomBean mDialCustomBean;
    public DialWriteAssignCall(String address, DialCustomBean mDialCustomBean, BleWrite.DialWriteInterface mInterface) {
        super(address);
        this.mInterface=mInterface;
        this.mDialCustomBean=mDialCustomBean;
        TLog.Companion.error("==="+ new Gson().toJson(mDialCustomBean));
        if(mDialCustomBean.getType()==1)
        {
            byte[] uiId = HexDump.toByteArray( mDialCustomBean.getUiFeature());
            byte[] binSize = HexDump.toByteArray( mDialCustomBean.getBinSize());
            TLog.Companion.error("uiId=="+uiId.length+"  binSize==="+binSize.length);

            byte[] send=new byte[]{
                    0x01,0x00,0x0B,
                    uiId[0],uiId[1],uiId[2],uiId[3],
                    binSize[0],binSize[1],binSize[2],binSize[3],
                    (byte) mDialCustomBean.getColor(),(byte) mDialCustomBean.getFunction(),(byte) mDialCustomBean.getLocation()
            };
            sendData=  CmdUtil.getFullPackage(CmdUtil.getPlayer("09", "03",send));
        }
        else if(mDialCustomBean.getType()==2)
        {
            byte[] uiId = HexDump.toByteArray( mDialCustomBean.getUiFeature());
            byte[] binSize = HexDump.toByteArray( mDialCustomBean.getBinSize());
            byte[] unicode = HexDump.stringToByte(HexDump.getUnicode(mDialCustomBean.getName()).replace("\\u", ""));//解码
            TLog.Companion.error("uiId=="+uiId.length+"  binSize==="+binSize.length);
            byte[] send=new byte[]{
                    0x02,0x00,0x08,
                    uiId[0],uiId[1],uiId[2],uiId[3],
                    binSize[0],binSize[1],binSize[2],binSize[3]
            ,
            0x03
            };
            byte[] value  = ByteUtil.hexStringToByte(keyValue(send,unicode));
            sendData=  CmdUtil.getFullPackage(CmdUtil.getPlayer("09", "03",value));
        }
        else if(mDialCustomBean.getType()==3)
        {

        }

    }


    @Override
    public byte[] getSendData() {
      //  byte[] sendData = CmdUtil.getFullPackage(CmdUtil.getPlayer("09", "03"));
//        TLog.Companion.error("flash发送++" + ByteUtil.getHexString(sendData));
        return sendData;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("写入表盘res==" + ByteUtil.getHexString(result));
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
        TLog.Companion.error("res==" + ByteUtil.getHexString(result));
        if(result[8] == Config.Dial.KEY && result[9] == Config.Dial.DEVICE_DIAL_STATUS)
        {
            mInterface.onResultDialWrite(result[10]);
            return  true;
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

    private String keyValue(byte[] key, byte[] key1) {
        return ByteUtil.getHexString(key) +
                ByteUtil.getHexString(HexDump.toByteArray((short) key1.length)) +
                ByteUtil.getHexString(key1);

    }
     public class DialBean {
        Integer dialId;
        int type ;

         public Integer getDialId() {
             return dialId;
         }

         public void setDialId(Integer dialId) {
             this.dialId = dialId;
         }

         public int getType() {
             return type;
         }

         public void setType(int type) {
             this.type = type;
         }

         public DialBean() {
         }

         public DialBean(Integer dialId, int type) {
             this.dialId = dialId;
             this.type = type;
         }
     }
}
