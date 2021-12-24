package com.shon.connector.call.write.controlclass;

import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.bean.PushBean;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.3.13  仅仅是ios Android咱不管
 * 设备提醒推送功能开关
 */
public class ReminderPushCall extends WriteCallback {
    byte[] keyValue;//长度14 文档3.3.12
    PushBean mPushBean;
    int num=0;

    public ReminderPushCall(String address, PushBean mPushBean, int i) {
        super(address);
        this.mPushBean = mPushBean;
        num = i;//当前包的序号
    }

    public ReminderPushCall(String address, byte[] keyValue, int i) {
        super(address);
        this.keyValue = keyValue;
        num = i;//当前包的序号
    }

    @Override
    public byte[] getSendData() {
//        if(num==0) {
//            if (mPushBean != null) {
//                //长度14
//                if (num == 0)
//                    keyValue = new byte[]{(byte) mPushBean.getPhoneNotification(), (byte) mPushBean.getSmsNotification()
//                            , (byte) mPushBean.getMessageNotification(), (byte) mPushBean.getOther(), (byte) mPushBean.getEmail()
//                            , (byte) mPushBean.getFacebook(), (byte) mPushBean.getWechat(), (byte) mPushBean.getLine()};
//
//            }
//            //长度12
//            byte[] head = {Config.PRODUCT_CODE, 0x00, 0x01, 0x00, (byte) num, 0x0D, 0x05, HexDump.byteXOR(keyValue), 0x01, 0x0D, 0x00, 0x0E};
//            byte[] setData = HexDump.byteMerger(head, keyValue);//如果 keyvalue 长度为255 0x00,0x0D要变 为0x0c,0xFF
//            String SixTeen = Integer.toHexString(setData.length);//如果超过255字节 sixteen的长度会>2
//            if (SixTeen.length() > 2) {
//                setData[5] = HexDump.hexStringToBytes(SixTeen.substring(0, (SixTeen.length() - 2)))[0];//取第一位到长度-2
//                setData[6] = HexDump.hexStringToBytes(SixTeen.substring((SixTeen.length() - 2)))[0];//取后俩位
//                TLog.Companion.error("");
//            } else
//                setData[6] = HexDump.hexStringToBytes(SixTeen)[0];//没超过及直接获取
//            TLog.Companion.error("最终长度+" + ByteUtil.getHexString(setData));
//            return setData;
//        }
//        else
//        {
//                keyValue = new byte[]{(byte) mPushBean.getWeiBo(), (byte) mPushBean.getLinkedln(), (byte) mPushBean.getQQ()
//                        , (byte) mPushBean.getWhatsApp(), (byte) mPushBean.getViber(), (byte) mPushBean.getInstagram()};
//            return keyValue;
//        }
        byte payload[] = {0x01, 0x0D, (byte) mPushBean.getPhoneNotification(), (byte) mPushBean.getSmsNotification()
                , (byte) mPushBean.getMessageNotification(), (byte) mPushBean.getOther(), (byte) mPushBean.getEmail()
                , (byte) mPushBean.getFacebook(), (byte) mPushBean.getWechat(), (byte) mPushBean.getLine()
        ,(byte) mPushBean.getWeiBo(), (byte) mPushBean.getLinkedln(), (byte) mPushBean.getQQ()
                , (byte) mPushBean.getWhatsApp(), (byte) mPushBean.getViber(), (byte) mPushBean.getInstagram()};
        return CmdUtil.getFullPackage(payload);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        return false;
    }

    @Override
    public boolean isFinish() {
        return super.isFinish();
    }

    @Override
    public void onTimeout() {

    }
}
