package com.example.xingliansdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.xingliansdk.network.api.jignfan.JingfanBpViewModel;
import com.example.xingliansdk.network.api.login.LoginBean;
import com.example.xingliansdk.network.api.weather.bean.FutureWeatherBean;
import com.example.xingliansdk.network.api.weather.bean.ServerWeatherBean;
import com.example.xingliansdk.service.core.BaseService;
import com.example.xingliansdk.service.work.BleWork;
import com.example.xingliansdk.ui.bp.DbManager;
import com.example.xingliansdk.ui.bp.PPG1CacheDb;
import com.example.xingliansdk.utils.GetJsonDataUtil;
import com.example.xingliansdk.view.DateUtil;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.bluetooth.util.TimeU;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.call.write.bigdataclass.ppg1.GetPPG1CacheRecordCall;
import com.shon.connector.call.write.bigdataclass.ppg1.OnPPG1BigDataListener;
import com.shon.connector.call.write.bigdataclass.ppg1.OnPPG1CacheRecordListener;
import com.shon.connector.call.write.controlclass.TurnOnScreenCall;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by Admin
 * Date 2022/3/16
 */
public class SendWeatherService extends BaseService implements OnPPG1CacheRecordListener , OnPPG1BigDataListener {

    private static final String TAG = "SendWeatherService";

    String savePath;

    private OnWeatherStatusListener onWeatherStatusListener;

    public void setOnWeatherStatusListener(OnWeatherStatusListener onWeatherStatusListener) {
        this.onWeatherStatusListener = onWeatherStatusListener;
    }

    private final IBinder iBinder = new SendWeatherBinder();


    private List<byte[]> tmpPPgList;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0x00){
                handler.removeMessages(0x00);
                if(tmpPPgList == null || tmpPPgList.size()==0)
                    return;
                if(startIndex>tmpPPgList.size()-1)
                    return;

                byte[] timeI  = tmpPPgList.get(startIndex);
                if(timeI == null)
                    return;
                getPPGData(timeI);
            }

        }
    };


    private void getPPGData(byte[] timeI){
        BleWrite.writeGetTimePPG1BigData(true,timeI,this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        savePath = Environment.getExternalStorageDirectory().getPath()+"/Download/";
        Log.e(TAG,"------onCreate-----");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }




    public class SendWeatherBinder extends Binder{
        public SendWeatherService getService(){
            return SendWeatherService.this;
        }

    }


    //每小时定位一次，发送天气

    /**
     * 通过Handler延迟发送消息的形式实现定时任务。
     */
    public static final int CHANGE_TIPS_TIMER_INTERVAL = 60 * 60 * 1000;

    //启动计时器任务
    public void start24HourMethod() {
        Runnable mChangeTipsRunnable = new Runnable() {
            @Override
            public void run() {
               //开始定位
               new BleWork().startLocation(SendWeatherService.this);
                handler.postDelayed(this, CHANGE_TIPS_TIMER_INTERVAL);
            }
        };
        handler.post(mChangeTipsRunnable);
    }


    private ServerWeatherBean tmpServerBean;

    public void setWeatherData(ServerWeatherBean str,String cityStr){
        Log.e("TAG","------天气设置="+cityStr+" " +str.toString());
        this.tmpServerBean = str;
        if(onWeatherStatusListener != null)
            onWeatherStatusListener.backWeatherStatus(new Gson().toJson(str));
        anslysisTodayWeather(str,cityStr);

    }

    public String getWeatherData(){
        return new Gson().toJson(tmpServerBean);
    }



    int day = 1;

    //今天天气
    private void anslysisTodayWeather(ServerWeatherBean weatherBean,String city) {
        try {
            day = 1;
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("010014");
            Calendar calendar = Calendar.getInstance();
            //时间戳 4个byte
            long currTime = weatherBean.getDateTimeStamp();
            long constanceMils = 946656000L;
            long currTimeLong = currTime - constanceMils;
            //Log.e("111","-----相差="+currTimeLong)

            byte[] timeByte = HexDump.toByteArray(currTimeLong);
            stringBuffer.append(HexDump.bytesToString(timeByte));

            //天气类型 一个byte
            byte weatherType = weatherBean.getStatusCode().byteValue();
            stringBuffer.append(String.format("%02x", weatherType));

            Log.e("TT", "--------天气类型=" + stringBuffer.toString());

            //当前时刻的温度 2 个byte Integer.parseInt(weatherBean.getTemp()) * 10
            byte[] currTempByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getTemp()) * 10);
            String tmpStr = HexDump.bytesToString(currTempByte);
            stringBuffer.append(tmpStr);

            //   stringBuffer.append("00FA")
            //当天最高温度 2 个byte
            byte[] currMaxTempByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getTempMax()) * 10);
            stringBuffer.append(HexDump.bytesToString(currMaxTempByte));
            //当天最低温度 2 个byte
            byte[] currMinTempByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getTempMin()) * 10);
            stringBuffer.append(HexDump.bytesToString(currMinTempByte));
            //空气质量指数 2 个byte
            byte[] airAqiByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getAirAqi()));
            stringBuffer.append(HexDump.bytesToString(airAqiByte));
            //相对湿度 2 个byte
            byte[] humidityByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getHumidity()));
            stringBuffer.append(HexDump.bytesToString(humidityByte));
            //紫外线指数 1个byte
            byte uvIndexByte = Integer.valueOf(weatherBean.getUvIndex()).byteValue();
            stringBuffer.append(String.format("%02x", uvIndexByte));
            //日出时间
            String sunriseTime = weatherBean.getSunrise();
            //日出时 一个byte
            byte sunriseHourByte = (byte) DateUtil.getHHmmForHour(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseHourByte));
            //日出分 一个byte
            byte sunriseMinuteByte = (byte) DateUtil.getHHmmForMinute(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseMinuteByte));
            //日落时间
            String sunsetTime = weatherBean.getSunset();
            //日落时 一个byte
            byte sunsetHourByte = (byte) DateUtil.getHHmmForHour(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetHourByte));
            //日落分 一个byte
            byte sunsetMinuteByte = (byte) DateUtil.getHHmmForMinute(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetMinuteByte));

            String cityArray = HexDump.getUnicode(city).replace("\\u", "");
            Log.e("城市","城市Unicode="+cityArray);
            byte[] cityLength = HexDump.toByteArrayTwo(cityArray.length()/2);

            stringBuffer.append("31").append(HexDump.bytesToString(cityLength));
            stringBuffer.append(cityArray);

            String weatherContentStr = stringBuffer.toString();

            Log.e("天气", "---111---当前天气转换=$weatherContentStr="+weatherContentStr);

            byte[] wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER, ByteUtil.hexStringToByte(weatherContentStr));

            Log.e("天气", "-----attttt=" + ByteUtil.getHexString(wArray));

            byte[] resultB = CmdUtil.getFullPackage(wArray);
            Log.e("天气", "----11--转换当天=" + ByteUtil.getHexString(resultB));
            BleWrite.writeWeatherCall(resultB, false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    writeOtherDayWeather(weatherBean,cityArray);
                }
            },1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private final Gson gson = new Gson();
    private final StringBuilder stringBuffer = new StringBuilder();
    private void writeOtherDayWeather(ServerWeatherBean weatherBeans,String cityHex){
        try {
            Log.e(TAG,"------day="+day);
            if(day >3){

//                byte[] resultByte = new byte[]{(byte) 0x88, 0x0 ,0x0, 0x0 ,0x0 ,0x0 ,0x53 ,0x62, 0x4 ,0x7 ,0x2, 0x0 ,0x4E,0x29, (byte) 0xC8, (byte) 0xC1, (byte) 0xD9,0x18 ,0x3 ,0x6,
//                        0x0 , (byte) 0xD2, 0x7, 0x0, (byte) 0xC8,0x7, 0x0 , (byte) 0xC8, 0x7, 0x0, (byte) 0xD2, 0x7, 0x0 , (byte) 0xD2, 0x7 ,0x0, (byte) 0xD2, 0x7 ,0x0, (byte) 0xD2,
//                        0x2 ,0x0, (byte) 0xDC, 0x2 ,0x0 , (byte) 0xE6, 0x2 ,0x0, (byte) 0xF0, 0x2 ,0x0, (byte) 0xFA, 0x2, 0x1, 0x4, 0x1, 0x1, 0x4, 0x1, 0x1,
//                        0xE, 0x1, 0x1, 0x4, 0x1 ,0x1, 0x4 ,0x1, 0x0 , (byte) 0xFA, 0x1, 0x0 , (byte) 0xF0, 0x1, 0x0, (byte) 0xF0, 0x6, 0x0 , (byte) 0xE6, 0x6,
//                        0x0 , (byte) 0xDC, 0x6, 0x0, (byte) 0xDC, 0x6 ,0x0, (byte) 0xDC, 0x6, 0x0 , (byte) 0xD2};
//                BleWrite.writeWeatherCall(resultByte,false);

                send24HourData(weatherBeans);
                return;
            }

            stringBuffer.delete(0,stringBuffer.length());
            FutureWeatherBean futureWeatherBean = null;
            if(day == 1){   //明天
                futureWeatherBean =gson.fromJson(gson.toJson(weatherBeans.getTomorrow()),FutureWeatherBean.class);
            }
            if(day == 2){   //后天o
                futureWeatherBean =gson.fromJson(gson.toJson(weatherBeans.getDayAfterTomorrow()),FutureWeatherBean.class);
            }

            if(day == 3){   //大后天
                futureWeatherBean =gson.fromJson(gson.toJson(weatherBeans.getThreeDaysFromNow()),FutureWeatherBean.class);
            }
            if(futureWeatherBean == null)
                return;

            stringBuffer.append("010014");

            //时间戳 4个byte
            long currTime = getZeroMills() / 1000;

            currTime += 86400L * day;

            Log.e(TAG,"------时间戳="+currTime);

            long constanceMils = 946656000L;
            long currTimeLong = currTime - constanceMils;
            //Log.e("111","-----相差="+currTimeLong)

            byte[] timeByte = HexDump.toByteArray(currTimeLong);
            stringBuffer.append(HexDump.bytesToString(timeByte));

            //天气类型 一个byte
            int weatherType = Integer.valueOf(futureWeatherBean.getStatusCode()).byteValue();
            stringBuffer.append(String.format("%02x", weatherType));

            Log.e("TT", "--------天气类型=" + stringBuffer.toString());

            //当前时刻的温度 2 个byte Integer.parseInt(weatherBean.getTemp()) * 10
            stringBuffer.append("FFFF");
            //当天最高温度 2 个byte
            byte[] currMaxTempByte = HexDump.toByteArrayTwo(Integer.parseInt(futureWeatherBean.getTempMax()) * 10);
            stringBuffer.append(HexDump.bytesToString(currMaxTempByte));
            //当天最低温度 2 个byte
            byte[] currMinTempByte = HexDump.toByteArrayTwo(futureWeatherBean.getTempMin() * 10);
            stringBuffer.append(HexDump.bytesToString(currMinTempByte));
            //空气质量指数 2 个byte
            String aql = futureWeatherBean.getAirAqi();
            byte[] aBy = HexDump.stringToByte(aql);
            int airV = aBy[0] & 0xff;
            String rAir = HexDump.bytesToString(HexDump.toByteArrayTwo(airV));
            stringBuffer.append("FF").append(aql);
            //相对湿度 2 个byte
            byte[] humidityByte = HexDump.toByteArrayTwo(Integer.parseInt(futureWeatherBean.getHumidity()));
            stringBuffer.append(HexDump.bytesToString(humidityByte));
            //紫外线指数 1个byte
            byte uvIndexByte = Integer.valueOf(futureWeatherBean.getUvIndex()).byteValue();
            stringBuffer.append(String.format("%02x", uvIndexByte));
            //日出时间
            String sunriseTime = futureWeatherBean.getSunrise();
            //日出时 一个byte
            byte sunriseHourByte = (byte) DateUtil.getHHmmForHour(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseHourByte));
            //日出分 一个byte
            byte sunriseMinuteByte = (byte) DateUtil.getHHmmForMinute(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseMinuteByte));
            //日落时间
            String sunsetTime = futureWeatherBean.getSunset();
            //日落时 一个byte
            byte sunsetHourByte = (byte) DateUtil.getHHmmForHour(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetHourByte));
            //日落分 一个byte
            byte sunsetMinuteByte = (byte) DateUtil.getHHmmForMinute(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetMinuteByte));


            byte[] cityLength = HexDump.toByteArrayTwo(cityHex.length()/2);

            stringBuffer.append("31").append(HexDump.bytesToString(cityLength));
            stringBuffer.append(cityHex);

           // stringBuffer.append("310006").append(cityHex).append("533A");

            String weatherContentStr = stringBuffer.toString();

            Log.e("天气", "---111---当前天气转换=$weatherContentStr");

            byte[] wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER, ByteUtil.hexStringToByte(weatherContentStr));

            Log.e("天气", "-----attttt=" + ByteUtil.getHexString(wArray));

            byte[] resultB = CmdUtil.getFullPackage(wArray);
            BleWrite.writeWeatherCall(resultB,false);
            day++;
            writeOtherDayWeather(weatherBeans,cityHex);
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    private  void send24HourData(ServerWeatherBean weatherBean){
        //时间戳
        long currTime = weatherBean.getDateTimeStamp();
        long constanceMils = 946656000L;
        byte[] timeByte = HexDump.toByteArray(currTime-constanceMils);

        String tiemByteStr = HexDump.bytesToString(timeByte);

        StringBuffer stringBuffer = new StringBuffer();


        stringBuffer.append("02");

        List<ServerWeatherBean.Hourly> hourlyList = weatherBean.getHourly();

        StringBuilder sb = new StringBuilder();
        for(ServerWeatherBean.Hourly hourly : hourlyList){
            int type = hourly.getStatusCode();
            //温度
            int temputerV = hourly.getTemp();
            //温度两个byte
            byte[] byteTem = HexDump.toByteArrayTwo(temputerV * 10);

            String typeStr = String.format("%02d",(type<8 ? type : 0xff));
            String tmpStr = HexDump.bytesToString(byteTem);

            // TLog.error("t","------="+typeStr+tmpStr)
            sb.append(typeStr).append(tmpStr);
           // stringBuffer.append(typeStr).append(tmpStr);
        }

        String contentStr = sb.toString();
        byte[] contentArray =   ByteUtil.hexStringToByte(contentStr);
        byte[] contentLength = HexDump.toByteArrayTwo(contentArray.length+6);

        Log.e("天气24","---contentStr="+contentStr +" "+contentArray.length+" "+HexDump.bytesToString(contentLength));
        stringBuffer.append(HexDump.bytesToString(contentLength));
        stringBuffer.append(tiemByteStr);
        stringBuffer.append("1803");
        stringBuffer.append(contentStr);

        byte[] byteS = CmdUtil.getPlayer(
                Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,HexDump.stringToByte(stringBuffer.toString()));

        byte[] resultByte = CmdUtil.getFullPackage(byteS);
        Log.e("天气24", ByteUtil.getHexString(byteS)+"\n"+ByteUtil.getHexString(resultByte));

        BleWrite.writeWeatherCall(resultByte,false);
    }



    private long getZeroMills() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        return cal.getTimeInMillis();
    }


    //获取PPG缓存目录
    public void getDevicePPG1CacheRecord(){
        BleWrite.writeGetPPG1CacheRecord(true,this);
    }




    //获取ppg缓存的记录，时间戳，然后再根据时间戳查询数据库是否有保存，没有保存就从手表中读取
    @Override
    public void backPPGCacheByteArray(List<byte[]> timeList) {
        TLog.Companion.error("---11--PPG缓存目录="+new Gson().toJson(timeList));
    }

    @Override
    public void backPPGCacheLongArray(List<Long> longList) {
        long constanceMils = 946656000L;
        long currYearM = 1640966400L; //2022-01-01
        for(Long itemL : longList){
            long normalLong = itemL+constanceMils;
            if(normalLong > currYearM){

            }
            String itemTimeStr = TimeU.getCurrTime(((long) normalLong) * 1000);
            TLog.Companion.error("---22--PPG缓存目录="+itemTimeStr);

        }

    }

    @Override
    public void backPPGCacheArray(List<byte[]> timeList, List<Long> longList) {
        if(timeList == null || longList == null || timeList.isEmpty() || longList.isEmpty())
            return;
        findLocalDb(timeList,longList);
    }


    private void findLocalDb(List<byte[]> timeList, List<Long> longList){
        long constanceMils = 946656000L;
        long currYearM = 1640966400L; //2022-01-01


        LoginBean loginBean = Hawk.get(com.example.xingliansdk.Config.database.USER_INFO);
        if(loginBean == null)
            return;
        String userId = loginBean.getUser().getUserId();
        String mac = loginBean.getUser().getMac();
        if(userId == null || mac == null)
            return;
        List<byte[]> noCacheList = new ArrayList<>();

        for(int i = 0;i<longList.size();i++){
            Long itemL = longList.get(i);
            long normalLong = (itemL+constanceMils);
            TLog.Companion.error("----normalLong="+normalLong);
            if(normalLong > currYearM){
                String itemTimeStr = TimeU.getCurrTime(((long) normalLong) * 1000);
                PPG1CacheDb ppg1CacheDb = DbManager.getDbManager().getCurrTimePPGData(userId,mac,itemTimeStr);

                //noCacheList.add(timeList.get(i));
                TLog.Companion.error("-------未保存的="+itemTimeStr +" "+ByteUtil.getHexString(timeList.get(i)));
                if(ppg1CacheDb == null){
                    noCacheList.add(timeList.get(i));
                    TLog.Companion.error("-------未保存的="+itemTimeStr +" "+ByteUtil.getHexString(timeList.get(i)));
                }
            }

        }

        if(noCacheList.size()>0){
            getSpecifyPPG(noCacheList);
        }

        TLog.Companion.error("---22--PPG缓存目录未保存的时间戳="+new Gson().toJson(noCacheList));
    }


    private int startIndex = 0;

    private void getSpecifyPPG(List<byte[]> list){
        TLog.Companion.error("-------结果未保存的集合="+new Gson().toJson(list));
        this.tmpPPgList = list;
        startIndex = 0;
        handler.sendEmptyMessage(0x00);
    }

    private final StringBuilder stringBuilder = new StringBuilder();
    //指定的时间戳ppg大数据返回
    @Override
    public void backPPG1BigData(List<Integer> bigPpgList, String itemTimeStr) {
        stringBuilder.delete(0,stringBuilder.length());
        LoginBean loginBean = Hawk.get(com.example.xingliansdk.Config.database.USER_INFO);
        if(loginBean == null)
            return;
        String userId = loginBean.getUser().getUserId();
        String mac = loginBean.getUser().getMac();
        if(userId == null || mac == null)
            return;
        PPG1CacheDb ppg1CacheDb = new PPG1CacheDb();
        ppg1CacheDb.setUserId(userId);
        ppg1CacheDb.setDeviceMac(mac);
        ppg1CacheDb.setPpgTimeStr(itemTimeStr);
        ppg1CacheDb.setDayStr(DateUtil.getCurrDate());
        ppg1CacheDb.setDbStatus("0");
        ppg1CacheDb.setBbpDataList(bigPpgList);
        boolean isSave = DbManager.getDbManager().savePPBBpData(ppg1CacheDb);
        TLog.Companion.error("-------保存到数据库="+itemTimeStr+" "+isSave);


        new GetJsonDataUtil().writeTxtToFile(new Gson().toJson(ppg1CacheDb),savePath,"specify_ppg"+itemTimeStr+DateUtil.getDate("HH:m:ss",System.currentTimeMillis())+".json");

        for(int i = 0;i<bigPpgList.size();i++){
            if(i == bigPpgList.size()-1){
                stringBuilder.append(i);
            }else{
                stringBuilder.append(i);
                stringBuilder.append(",");
            }
        }
        TLog.Companion.error("-------上传参数="+stringBuilder.toString());
        new JingfanBpViewModel().uploadJFBpData(stringBuilder.toString(),itemTimeStr);

        startIndex++;
        handler.sendEmptyMessageDelayed(0x00,2000);
    }

}
