package com.shon.connector.call.write.messagereminder;

import com.shon.connector.utils.ShowToast;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.7.1
 * APP 消息提醒类 来电,短信,消息提醒
 */
public class MessageCall extends WriteCallback {
    byte[] Unicode;
    int  key;
    String title;
    String content;
    BleWrite.MessageInterface mInterface;
    public MessageCall(String address, int  key, String title, String content,BleWrite.MessageInterface mInterface) {
        super(address);
        this.mInterface=mInterface;
        this.content=content;
        this.key=key;
        this.title=title;
        byte[] unicodeTitle = HexDump.stringToByte(HexDump.getUnicode(title).replace("\\u", ""));//解码
        byte[] unicodeContent = HexDump.stringToByte(HexDump.getUnicode(content).replace("\\u", ""));//解码
        Unicode = ByteUtil.hexStringToByte(keyValue((byte) key,unicodeTitle, unicodeContent));
//        TLog.Companion.error("接受++"+content);
    }

    @Override
    public byte[] getSendData() {
     // TLog.Companion.error("发送++"+ ByteUtil.getHexString(CmdUtil.getFullPackage(Unicode)));
      //  TLog.Companion.error("发送++"+ CmdUtil.getFullPackage(Unicode).length);
        return CmdUtil.getFullPackage(Unicode);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
       // TLog.Companion.error("获取到最终的数据长度_++" + ByteUtil.getHexString(result));//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    //TLog.Companion.error("返回了01");
                    mInterface.onResult();
                    break;
                case 0x02:
                case 0x03:
                     BleWrite.writeMessageCall(key,title,content,mInterface); //重新发送的操作
                    break;
                case 0x04:
                    //ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return true;
        }

        return false;
    }

    @Override
    public void onTimeout() {

    }

    private String keyValue(byte key, byte[] key1, byte[] key2) {
        return "0501010001" +//索引 和长度 长度现在都是1
                //  ByteUtil.getHexString(HexDump.toByteArray((short) key.length)) +
                ByteUtil.getHexString(key) +
                "02" +
                ByteUtil.getHexString(HexDump.toByteArray((short) key1.length)) +
                ByteUtil.getHexString(key1) +
                "03" +
                ByteUtil.getHexString(HexDump.toByteArray((short) key2.length)) +
                ByteUtil.getHexString(key2)
                ;
    }
}
