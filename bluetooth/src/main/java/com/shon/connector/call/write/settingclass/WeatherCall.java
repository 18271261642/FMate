package com.shon.connector.call.write.settingclass;

import android.util.Log;

import com.example.xingliansdk.utils.ShowToast;
import com.google.gson.Gson;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.bean.DataBean;

import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * 3.6.7
 * APP 设置设备设备天气参数
 */
public class WeatherCall extends WriteCallback {
    byte[] key;
    DataBean dataBean;

    public WeatherCall(String address, DataBean dataBean) {
        super(address);
        TLog.Companion.error("天气"+new Gson().toJson(dataBean));
        this.dataBean = dataBean;
        //这之前有个 0x01,0x05(5位长度现在是固定的长度)
        dataBean.getTime();
        dataBean.getTemperature();//当前时刻温度
        dataBean.getHighestTemperatureToday();//当天最高温度
        dataBean.getLowestTemperatureToday();//最低温度
        dataBean.getAirQuality();//空气质量
        dataBean.getUVIndex();//紫外线
        dataBean.getHumidity();//相对湿度
        dataBean.getSunriseHours();//日出时
        dataBean.getSunriseMin();//日出分
        dataBean.getSunsetHours();//日落时
        dataBean.getSunsetMin();//日落分

        TLog.Companion.error("ttt"+dataBean.getTime());
//        TLog.Companion.error("dataBean.getTemperature()+="+dataBean.getTemperature());
        BigDecimal temper = new BigDecimal(dataBean.getTemperature()).multiply(new BigDecimal(10));
        if(dataBean.getTemperature().equals("65535"))
            temper=new BigDecimal(65535);

//        TLog.Companion.error("temper++"+temper);
        BigDecimal highestTemper = new BigDecimal(dataBean.getHighestTemperatureToday()).multiply(new BigDecimal(10));
        BigDecimal LowestTemper = new BigDecimal(dataBean.getLowestTemperatureToday()).multiply(new BigDecimal(10));
        if (temper.compareTo(BigDecimal.ZERO) < 0)
            temper = temper.subtract(new BigDecimal(Config.TEMPERATURE_MAX)).abs();
        if (highestTemper.compareTo(BigDecimal.ZERO) < 0)
            highestTemper = highestTemper.subtract(new BigDecimal(Config.TEMPERATURE_MAX)).abs();
        if (LowestTemper.compareTo(BigDecimal.ZERO) < 0)
            LowestTemper = LowestTemper.subtract(new BigDecimal(Config.TEMPERATURE_MAX)).abs();

//        TLog.Companion.error("temper++" + temper);
//        TLog.Companion.error("temper++" + ByteUtil.getHexString(HexDump.toByteArrayTwo(Integer.parseInt(temper.toString()))));
//        TLog.Companion.error("dataBean.getUnicodeContent()==" + dataBean.getUnicodeContent());
        byte[] send;
        byte[] unicodeContent = new byte[0];
        send = HexDump.byteMerger(HexDump.toByteArrayTwo(Integer.parseInt(temper.toString()))
                , HexDump.toByteArrayTwo(Integer.parseInt(highestTemper.toString()))
                , HexDump.toByteArrayTwo(Integer.parseInt(LowestTemper.toString()))
                , HexDump.toByteArrayTwo(Integer.parseInt(dataBean.getAirQuality()))
                , HexDump.toByteArrayTwo(Byte.parseByte(dataBean.getHumidity()))
        );

        Log.e("天气","-------send="+ByteUtil.getHexString(send));


        //紫外线,湿度,日出时,日出分,日落时,日落分
        byte[] playerEnd = new byte[]{Byte.parseByte(dataBean.getUVIndex())
                , (byte) dataBean.getSunriseHours(), (byte) dataBean.getSunriseMin(), (byte) dataBean.getSunsetHours(), (byte) dataBean.getSunsetMin()};

       Log.e("天气","----单个的byte="+ByteUtil.getHexString(playerEnd));

        if (dataBean.getUnicodeType() == DataBean.TEMPERATURE_FEATURES_UNICODE) {
            //   byte[] unicodeTitle = HexDump.stringToByte(HexDump.getUnicode(dataBean.getUnicodeTitle()).replace("\\u", ""));//解码
//            TLog.Companion.error("dataBean.getUnicodeContent()==" + dataBean.getUnicodeContent());
            String unicode = HexDump.getUnicode(dataBean.getUnicodeContent()).replace("\\u", "");
//            TLog.Companion.error("unicode==" + unicode);
            unicodeContent = ByteUtil.hexStringToByte(unicode);//解码
//            TLog.Companion.error("unicodeContent==" + ByteUtil.getHexString(unicodeContent));
            //  unicodeByte = HexDump.byteMerger(unicodeByte, unicodeContent);
        }

        Log.e("天气","--------unitCode="+ByteUtil.getHexString(unicodeContent));


        key = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,
                ByteUtil.hexStringToByte(keyValue(HexDump.toByteArray(dataBean.getTime()), (byte) dataBean.getWeatherType(), send, playerEnd, unicodeContent)));

        TLog.Companion.error("天气key==" + ByteUtil.getHexString(key));
    }

    @Override
    public byte[] getSendData() {
        //8800000000002D7B04060029010500020C111C31204ECA592959296C144E0D95194E0B5348898151FA5DEE53BB641E70B94E8B60C5 为最终值 其中111C为时间动态值可改变
        //88000000000022A6040701001428E90C0004FFFF014001040035004C070610120D3100065B9D5B89533A
        TLog.Companion.error("天气==" + ByteUtil.getHexString(CmdUtil.getFullPackage(key)));
        return CmdUtil.getFullPackage(key);
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
//        TLog.Companion.error("获取到最终的数据长度_++" + ByteUtil.getHexString(result));//获取到最终的长度
        if (result[8] == Config.DEVICE_COMMAND_ACK && result[9] == Config.DEVICE_KEY_ACK) {
            switch (result[10]) {
                case 0x01:
                    // ShowToast.INSTANCE.showToastLong("设置成功");
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
       // BleWrite.writeWeatherCall(dataBean,false);
    }

    private String keyValue(byte[] key, byte key1, byte[] key2, byte[] key3, byte[] key4) {//时间,类型,温度,end,UNcode
//        TLog.Companion.error("key4==" + ByteUtil.getHexString(HexDump.toByteArray((short) (key4.length))));
//        TLog.Companion.error("key==" + ByteUtil.getHexString(HexDump.toByteArray((short) (key3.length + key2.length + key.length + 1))));
        return
                "01" +//天气的索引
                ByteUtil.getHexString(HexDump.toByteArray((short) (key3.length + key2.length + key.length + 1))) +//天气的索引
                ByteUtil.getHexString(key) +
                ByteUtil.getHexString(new byte[]{(byte) (key1)}) +
                ByteUtil.getHexString(key2) +
                ByteUtil.getHexString(key3) +
                ByteUtil.getHexString(new byte[]{DataBean.TEMPERATURE_FEATURES_UNICODE}) +  //天气UNcode的 下标
                ByteUtil.getHexString(HexDump.toByteArray((short) (key4.length))) +  //天气的长度
                ByteUtil.getHexString(key4)
                ;
    }
}
