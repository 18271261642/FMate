package com.shon.connector.call;

import com.shon.connector.Config;
import com.shon.connector.utils.HexDump;
import com.shon.bluetooth.util.ByteUtil;

public class CmdUtil {

    private static String product_id = "88";// 这里替换你的产品id


    /**
     * 获取全包内容
     *
     * @param value 你的 payload
     * @return payload 加上head
     */
    public static byte[] getFullPackage(byte[] value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(product_id)  //产品id
                .append("0000")  //Reserve
                .append(ByteUtil.getHexString(HexDump.toByteArray(value.length)))  //长度 ，4个字节
                .append(ByteUtil.getHexString(new byte[]{HexDump.byteXOR(value)}))  //xor crc
                .append(ByteUtil.getHexString(value));
        return ByteUtil.hexStringToByte(stringBuilder.toString());
    }
    public static byte[] getFullPackage(byte[] value,long length,byte CRC) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(product_id)  //产品id
                .append("0000")  //Reserve
                .append(ByteUtil.getHexString(HexDump.toByteArray(length)))  //长度 ，4个字节
                .append(ByteUtil.getHexString(CRC))  //xor crc总体整包crc
                .append(ByteUtil.getHexString(value))
                //.append(ByteUtil.getHexString(CRC1)) //小包crc
        ;
        return ByteUtil.hexStringToByte(stringBuilder.toString());
    }

    public static byte[] sendDataCRC(byte[] value,byte CRC) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ByteUtil.getHexString(value))
                .append(ByteUtil.getHexString(CRC));
        return ByteUtil.hexStringToByte(stringBuilder.toString());
    }

    /**
     * 这个判断是用来判断是否为需要解析的cmd
     * 需要按照协议，发送的key  和返回的key 保证一一对应
     *
     * @param cmd 返回的 cmd
     * @return 对应返回 ture , false 不对应
     */

    public static byte[] getPlayer(String Command,String key,byte[] value) {  //
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Command)  //command
                .append(key)  //key
               // .append(ByteUtil.getHexString(HexDump.toByteArray((short) value.length)))  //value的长度  最新通知没了不需要了
                .append(ByteUtil.getHexString(value));
        return ByteUtil.hexStringToByte(stringBuilder.toString());
    }
    public static byte[] getPlayer(String Command,String key ) {  //
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Command)  //command
                .append(key);  //key
        return ByteUtil.hexStringToByte(stringBuilder.toString());
    }
    /**
     * 这个判断是用来判断是否为需要解析的cmd
     * 需要按照协议，发送的key  和返回的key 保证一一对应
     *
     * @param cmd 返回的 cmd
     * @return 对应返回 ture , false 不对应
     */
    public static boolean isCurrentCmd(byte keyValue,byte cmd) {

        return (keyValue == Config.BigData.APP_DAILY_ACTIVITIES && cmd == Config.BigData.DEVICE_DAILY_ACTIVITIES)
                || (keyValue == Config.BigData.APP_SLEEP && cmd == Config.BigData.DEVICE_SLEEP)
                || (keyValue == Config.BigData.APP_HEART_RATE && (cmd ==  Config.BigData.DEVICE_HEART_RATE))
                || (keyValue == Config.BigData.APP_BLOOD_OXYGEN &&  cmd ==  Config.BigData.DEVICE_BLOOD_OXYGEN )
                || (keyValue == Config.BigData.APP_APNEA &&  cmd ==  Config.BigData.DEVICE_APNEA )
                || (keyValue == Config.BigData.APP_BLOOD_PRESSURE &&  cmd ==  Config.BigData.DEVICE_BLOOD_PRESSURE )
                || (keyValue == Config.BigData.APP_RR &&  cmd ==  Config.BigData.DEVICE_RR )
                || (keyValue == Config.BigData.APP_SPORTS &&  cmd ==  Config.BigData.DEVICE_SPORTS )
                || (keyValue == Config.BigData.APP_SPORTS_TYPE &&  cmd ==  Config.BigData.DEVICE_SPORTS_TYPE)
                || (keyValue == Config.BigData.APP_STRESS_FATIGUE &&  cmd ==  Config.BigData.DEVICE_STRESS_FATIGUE )
                || (keyValue == Config.BigData.APP_TEMPERATURE &&  cmd ==  Config.BigData.DEVICE_TEMPERATURE )
                ;
    }
}
