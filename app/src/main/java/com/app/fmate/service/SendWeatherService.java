package com.app.fmate.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.app.fmate.network.RequestServer;
import com.app.fmate.network.api.jignfan.JfLastPPGApi;
import com.app.fmate.network.api.jignfan.JfBpApi;
import com.app.fmate.network.api.jignfan.JingfanBpViewModel;
import com.app.fmate.network.api.login.LoginBean;
import com.app.fmate.network.api.weather.bean.FutureWeatherBean;
import com.app.fmate.network.api.weather.bean.ServerWeatherBean;
import com.app.fmate.ui.bp.DbManager;
import com.app.fmate.ui.bp.MeasureBpBean;
import com.app.fmate.ui.bp.PPG1CacheDb;
import com.app.fmate.utils.TimeUtil;
import com.app.fmate.view.DateUtil;
import com.google.gson.Gson;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.BodyType;
import com.orhanobut.hawk.Hawk;
import com.shon.bluetooth.BLEManager;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.bluetooth.util.TimeU;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.SpecifySleepSourceBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.call.listener.MeasureBigBpListener;
import com.shon.connector.call.write.bigdataclass.ppg1.OnPPG1BigDataListener;
import com.shon.connector.call.write.bigdataclass.ppg1.OnPPG1CacheRecordListener;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by Admin
 * Date 2022/3/16
 */
public class SendWeatherService extends AppService implements OnPPG1CacheRecordListener , OnPPG1BigDataListener, MeasureBigBpListener {

    private static final String TAG = "SendWeatherService";

    String savePath;


    //???????????????????????????????????????????????????
    private String  deviceMeasureTime ;

    //???????????????????????????110???
    private final int TIME_OUT_BP = 0x02;


    private final StringBuilder logSb = new StringBuilder();

    private OnWeatherStatusListener onWeatherStatusListener;

    public void setOnWeatherStatusListener(OnWeatherStatusListener onWeatherStatusListener) {
        this.onWeatherStatusListener = onWeatherStatusListener;
    }

    private final IBinder iBinder = new SendWeatherBinder();


    private List<PPG1CacheDb> uploadDbPPgList;
    private int ppgIndex = 0;

    private List<byte[]> tmpPPgList;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0x00){
                handler.removeMessages(0x00);
                if(tmpPPgList == null || tmpPPgList.size()==0){
                   // uploadPPGCacheData();
                   // new GetJsonDataUtil().writeTxtToFile(logSb.toString(),savePath,"ppg_complete"+DateUtil.getCurrentTime()+".json");
                    return;
                }
                if(startIndex>tmpPPgList.size()-1){
                   // uploadPPGCacheData();
                 //   new GetJsonDataUtil().writeTxtToFile(logSb.toString()+"?????????1",savePath,"ppg_complete"+DateUtil.getCurrentTime()+".json");
                    return;
                }

                byte[] timeI  = tmpPPgList.get(startIndex);
                if(timeI == null){
                  //  uploadPPGCacheData();
                //    new GetJsonDataUtil().writeTxtToFile(logSb.toString()+"?????????2",savePath,"ppg_complete"+DateUtil.getCurrentTime()+".json");
                    return;
                }
                getPPGData(timeI);
            }

            if(msg.what == 0x01){
                if(uploadDbPPgList == null || uploadDbPPgList.size() == 0){
                    return;
                }
                if(ppgIndex>uploadDbPPgList.size()-1){
                    TLog.Companion.error("---------?????????-----");
                    //???????????????????????????????????????

                    return;
                }

                PPG1CacheDb pb = uploadDbPPgList.get(ppgIndex);
                if(pb != null){
                    uploadAllPPGSource(pb,ppgIndex == uploadDbPPgList.size()-1);
                }

            }

            if(msg.what == TIME_OUT_BP){
                Config.isNeedTimeOut = false;
                TLog.Companion.error("-----------????????????????????????====");
                handler.removeMessages(TIME_OUT_BP);
                stopMeasureBp();
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


    private ServerWeatherBean tmpServerBean;

    public void setWeatherData(ServerWeatherBean str,String cityStr){
        Log.e("TAG","------????????????="+cityStr+" " +str.toString());
        this.tmpServerBean = str;
        if(onWeatherStatusListener != null)
            onWeatherStatusListener.backWeatherStatus(new Gson().toJson(str));
        anslysisTodayWeather(str,cityStr);

    }

    public void getWeatherData(){
      // new GetJsonDataUtil().writeTxtToFile("dddd",savePath,"123.json");
    }



    int day = 1;

    //????????????
    private void anslysisTodayWeather(ServerWeatherBean weatherBean,String city) {
        try {
            day = 1;
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("010014");
            Calendar calendar = Calendar.getInstance();
            //????????? 4???byte
            long currTime = weatherBean.getDateTimeStamp();
            long constanceMils = 946656000L;
            long currTimeLong = currTime - constanceMils;
            //Log.e("111","-----??????="+currTimeLong)

            byte[] timeByte = HexDump.toByteArray(currTimeLong);
            TLog.Companion.error("-------?????????????????????="+HexDump.bytesToString(timeByte));
            stringBuffer.append(HexDump.bytesToString(timeByte));

            //???????????? ??????byte
            byte weatherType = weatherBean.getStatusCode().byteValue();
            stringBuffer.append(String.format("%02x", weatherType));

            Log.e("TT", "--------????????????=" + stringBuffer.toString());

            //????????????????????? 2 ???byte Integer.parseInt(weatherBean.getTemp()) * 10
            byte[] currTempByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getTemp()) * 10);
            String tmpStr = HexDump.bytesToString(currTempByte);
            stringBuffer.append(tmpStr);

            //   stringBuffer.append("00FA")
            //?????????????????? 2 ???byte
            byte[] currMaxTempByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getTempMax()) * 10);
            stringBuffer.append(HexDump.bytesToString(currMaxTempByte));
            //?????????????????? 2 ???byte
            byte[] currMinTempByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getTempMin()) * 10);
            stringBuffer.append(HexDump.bytesToString(currMinTempByte));
            //?????????????????? 2 ???byte
            byte[] airAqiByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getAirAqi()));
            stringBuffer.append(HexDump.bytesToString(airAqiByte));
            //???????????? 2 ???byte
            byte[] humidityByte = HexDump.toByteArrayTwo(Integer.parseInt(weatherBean.getHumidity()));
            stringBuffer.append(HexDump.bytesToString(humidityByte));
            //??????????????? 1???byte
            byte uvIndexByte = Integer.valueOf(weatherBean.getUvIndex()).byteValue();
            stringBuffer.append(String.format("%02x", uvIndexByte));
            //????????????
            String sunriseTime = weatherBean.getSunrise();
            //????????? ??????byte
            byte sunriseHourByte = (byte) DateUtil.getHHmmForHour(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseHourByte));
            //????????? ??????byte
            byte sunriseMinuteByte = (byte) DateUtil.getHHmmForMinute(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseMinuteByte));
            //????????????
            String sunsetTime = weatherBean.getSunset();
            //????????? ??????byte
            byte sunsetHourByte = (byte) DateUtil.getHHmmForHour(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetHourByte));
            //????????? ??????byte
            byte sunsetMinuteByte = (byte) DateUtil.getHHmmForMinute(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetMinuteByte));

            String cityArray = HexDump.getUnicode(city).replace("\\u", "");
            Log.e("??????","??????Unicode="+cityArray);
            byte[] cityLength = HexDump.toByteArrayTwo(cityArray.length()/2);

            stringBuffer.append("31").append(HexDump.bytesToString(cityLength));
            stringBuffer.append(cityArray);

            String weatherContentStr = stringBuffer.toString();

            Log.e("??????", "---111---??????????????????=$weatherContentStr="+weatherContentStr);

            byte[] wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER, ByteUtil.hexStringToByte(weatherContentStr));

            Log.e("??????", "-----attttt=" + ByteUtil.getHexString(wArray));

            byte[] resultB = CmdUtil.getFullPackage(wArray);
            Log.e("??????", "----11--????????????=" + ByteUtil.getHexString(resultB));
           // BLEManager.getInstance().dataDispatcher.clear("");
            // 880000000000225704070100142A36F5800300FA012C0104000E005804052713093100066DF157335E02
            // 880000000000222B04070100142A37E90004FFFF0136010EFFFF005806052713093100066DF157335E02
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BleWrite.writeWeatherCall(resultB, true);
                }
            },1000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    writeOtherDayWeather(weatherBean,cityArray);
                }
            },3000);
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
                send24HourData(weatherBeans);
                return;
            }

            stringBuffer.delete(0,stringBuffer.length());
            FutureWeatherBean futureWeatherBean = null;
            if(day == 1){   //??????
                futureWeatherBean =gson.fromJson(gson.toJson(weatherBeans.getTomorrow()),FutureWeatherBean.class);
            }
            if(day == 2){   //??????o
                futureWeatherBean =gson.fromJson(gson.toJson(weatherBeans.getDayAfterTomorrow()),FutureWeatherBean.class);
            }

            if(day == 3){   //?????????
                futureWeatherBean =gson.fromJson(gson.toJson(weatherBeans.getThreeDaysFromNow()),FutureWeatherBean.class);
            }
            if(futureWeatherBean == null)
                return;

            stringBuffer.append("010014");

            //????????? 4???byte
            long currTime = getZeroMills() / 1000;

            currTime += 86400L * day;

            Log.e(TAG,"------?????????="+currTime);

            long constanceMils = 946656000L;
            long currTimeLong = currTime - constanceMils;
            //Log.e("111","-----??????="+currTimeLong)

            byte[] timeByte = HexDump.toByteArray(currTimeLong);
            stringBuffer.append(HexDump.bytesToString(timeByte));

            //???????????? ??????byte
            int weatherType = Integer.valueOf(futureWeatherBean.getStatusCode()).byteValue();
            stringBuffer.append(String.format("%02x", weatherType));

            Log.e("TT", "--------????????????=" + stringBuffer.toString());

            //????????????????????? 2 ???byte Integer.parseInt(weatherBean.getTemp()) * 10
            stringBuffer.append("FFFF");
            //?????????????????? 2 ???byte
            byte[] currMaxTempByte = HexDump.toByteArrayTwo(Integer.parseInt(futureWeatherBean.getTempMax()) * 10);
            stringBuffer.append(HexDump.bytesToString(currMaxTempByte));
            //?????????????????? 2 ???byte
            byte[] currMinTempByte = HexDump.toByteArrayTwo(futureWeatherBean.getTempMin() * 10);
            stringBuffer.append(HexDump.bytesToString(currMinTempByte));
            //?????????????????? 2 ???byte
            String aql = futureWeatherBean.getAirAqi();
            byte[] aBy = HexDump.stringToByte(aql);
            int airV = aBy[0] & 0xff;
            String rAir = HexDump.bytesToString(HexDump.toByteArrayTwo(airV));
            stringBuffer.append("FF").append(aql);
            //???????????? 2 ???byte
            byte[] humidityByte = HexDump.toByteArrayTwo(Integer.parseInt(futureWeatherBean.getHumidity()));
            stringBuffer.append(HexDump.bytesToString(humidityByte));
            //??????????????? 1???byte
            byte uvIndexByte = Integer.valueOf(futureWeatherBean.getUvIndex()).byteValue();
            stringBuffer.append(String.format("%02x", uvIndexByte));
            //????????????
            String sunriseTime = futureWeatherBean.getSunrise();
            //????????? ??????byte
            byte sunriseHourByte = (byte) DateUtil.getHHmmForHour(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseHourByte));
            //????????? ??????byte
            byte sunriseMinuteByte = (byte) DateUtil.getHHmmForMinute(sunriseTime);
            stringBuffer.append(String.format("%02x", sunriseMinuteByte));
            //????????????
            String sunsetTime = futureWeatherBean.getSunset();
            //????????? ??????byte
            byte sunsetHourByte = (byte) DateUtil.getHHmmForHour(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetHourByte));
            //????????? ??????byte
            byte sunsetMinuteByte = (byte) DateUtil.getHHmmForMinute(sunsetTime);
            stringBuffer.append(String.format("%02x", sunsetMinuteByte));


            byte[] cityLength = HexDump.toByteArrayTwo(cityHex.length()/2);

            stringBuffer.append("31").append(HexDump.bytesToString(cityLength));
            stringBuffer.append(cityHex);

           // stringBuffer.append("310006").append(cityHex).append("533A");

            String weatherContentStr = stringBuffer.toString();

            Log.e("??????", "---111---??????????????????=$weatherContentStr");

            byte[] wArray = CmdUtil.getPlayer(Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER, ByteUtil.hexStringToByte(weatherContentStr));

            Log.e("??????", "-----attttt=" + ByteUtil.getHexString(wArray));

            byte[] resultB = CmdUtil.getFullPackage(wArray);
            BleWrite.writeWeatherCall(resultB,false);
            day++;
            writeOtherDayWeather(weatherBeans,cityHex);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private  void send24HourData(ServerWeatherBean weatherBean){
        //?????????
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
            //??????
            int temputerV = hourly.getTemp();
            //????????????byte
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

        Log.e("??????24","---contentStr="+contentStr +" "+contentArray.length+" "+HexDump.bytesToString(contentLength));
        stringBuffer.append(HexDump.bytesToString(contentLength));
        stringBuffer.append(tiemByteStr);
        stringBuffer.append("1803");
        stringBuffer.append(contentStr);

        byte[] byteS = CmdUtil.getPlayer(
                Config.SettingDevice.command, Config.SettingDevice.APP_WEATHER,HexDump.stringToByte(stringBuffer.toString()));

        byte[] resultByte = CmdUtil.getFullPackage(byteS);
        Log.e("??????24", ByteUtil.getHexString(byteS)+"\n"+ByteUtil.getHexString(resultByte));

        BleWrite.writeWeatherCall(resultByte,false);
    }

    private long getZeroMills() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        return cal.getTimeInMillis();
    }


    //??????PPG????????????
    public void getDevicePPG1CacheRecord(){
        logSb.delete(0,logSb.length());
        BleWrite.writeGetPPG1CacheRecord(true,this);
    }



    //??????????????????
    public void backStartMeasureBp(boolean isStart){
       Config.isNeedTimeOut = true;
        BLEManager.getInstance().dataDispatcher.clear("");
        handler.sendEmptyMessageDelayed(TIME_OUT_BP,110 * 1000);
        BleWrite.writeStartOrEndDetectBp(true,isStart ? 0x03 : 0x01,this);
    }

    public void backStartMeasureBp(int key){
        Config.isNeedTimeOut = true;
        BLEManager.getInstance().dataDispatcher.clear("");
        handler.sendEmptyMessageDelayed(TIME_OUT_BP,110 * 1000);
        BleWrite.writeStartOrEndDetectBp(true,key,this);
    }


    //??????ppg????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public void backPPGCacheByteArray(List<byte[]> timeList) {

        TLog.Companion.error("---11--PPG????????????="+new Gson().toJson(timeList));
    }

    @Override
    public void backPPGCacheLongArray(List<Long> longList) {
        logSb.append("PPG???????????????="+new Gson().toJson(longList)+"\n");
        long constanceMils = 946656000L;
        long currYearM = 1640966400L; //2022-01-01
        for(Long itemL : longList){
            long normalLong = itemL+constanceMils;
            if(normalLong > currYearM){

            }
            String itemTimeStr = TimeU.getCurrTime(((long) normalLong) * 1000);
            TLog.Companion.error("---22--PPG????????????="+itemTimeStr);
            logSb.append("PPG????????????="+itemTimeStr+"\n");

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


        LoginBean loginBean = Hawk.get(com.app.fmate.Config.database.USER_INFO);
        if(loginBean == null)
            return;
        String userId = loginBean.getUser().getUserId();
        String mac=Hawk.get("address","");
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

              //  noCacheList.add(timeList.get(i));
                TLog.Companion.error("-------????????????="+itemTimeStr +" "+ByteUtil.getHexString(timeList.get(i)));
                if(ppg1CacheDb == null){
                    noCacheList.add(timeList.get(i));
                    TLog.Companion.error("----22---????????????="+itemTimeStr +" "+ByteUtil.getHexString(timeList.get(i)));
                }
            }

        }
        logSb.append("PPG?????????????????????????????????="+new Gson().toJson(noCacheList)+"\n");
        if(noCacheList.size()>0){
            getSpecifyPPG(noCacheList);
        }else{
           // uploadPPGCacheData();
          //  new GetJsonDataUtil().writeTxtToFile(logSb.toString(),savePath,"ppg_f"+DateUtil.getCurrentTime()+".json");
        }

        TLog.Companion.error("---22--PPG?????????????????????????????????="+new Gson().toJson(noCacheList));
    }


    private int startIndex = 0;

    private void getSpecifyPPG(List<byte[]> list){
        TLog.Companion.error("-------????????????????????????="+new Gson().toJson(list));
        this.tmpPPgList = list;
        startIndex = 0;
        handler.sendEmptyMessage(0x00);
    }

    private final StringBuilder stringBuilder = new StringBuilder();
    //??????????????????ppg???????????????
    @Override
    public void backPPG1BigData(List<Integer> bigPpgList, String itemTimeStr) {

        logSb.append("??????????????????="+itemTimeStr+" "+new Gson().toJson(bigPpgList)+"\n");
        logSb.append("----------------------------------"+"\n");

       // new GetJsonDataUtil().writeTxtToFile(logSb.toString(),savePath,"ppg_2"+DateUtil.getCurrentTime()+".json");


        stringBuilder.delete(0,stringBuilder.length());
        LoginBean loginBean = Hawk.get(com.app.fmate.Config.database.USER_INFO);
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
        TLog.Companion.error("-------??????????????????="+itemTimeStr+" "+isSave);
        logSb.append("??????????????????"+new Gson().toJson(ppg1CacheDb)+"\n");

        for(int i = 0;i<bigPpgList.size();i++){
            if(i == bigPpgList.size()-1){
                stringBuilder.append(bigPpgList.get(i));
            }else{
                stringBuilder.append(bigPpgList.get(i));
                stringBuilder.append(",");
            }
        }
        TLog.Companion.error("-------????????????="+stringBuilder.toString());
        new JingfanBpViewModel().uploadJFBpData(stringBuilder.toString(),itemTimeStr);
        logSb.append("??????"+itemTimeStr+" "+stringBuilder.toString()+"\n");
        startIndex++;
        handler.sendEmptyMessageDelayed(0x00,2000);
    }


    //??????????????????
    @Override
    public void measureStatus(int status,String deviceTime) {
        TLog.Companion.error("--------????????????="+status);
        if(status == 0x02){
            this.deviceMeasureTime = deviceTime;
        }
        if(status == 0x01){ //??????????????????
            Config.isNeedTimeOut = false;
        }
    }

    //????????????????????????????????????
    @Override
    public void measureBpResult(List<Integer> bpValue, String timeStr) {
        Config.isNeedTimeOut = false;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i<bpValue.size();i++){
            int v = bpValue.get(i);
            if(i ==bpValue.size()-1){
                stringBuilder.append(v);
            }else{
                stringBuilder.append(v);
                stringBuilder.append(",");
            }
        }
        if(deviceMeasureTime != null){
          uploadJFBpData(stringBuilder.toString(), deviceMeasureTime);
        }
    }

    //??????????????????
    private void uploadJFBpData(String str,String time){
        JfBpApi jfBpApi = new JfBpApi();
        jfBpApi.setBp(str,time);
        String token = null;
        LoginBean mLoginBean= Hawk.get(com.app.fmate.Config.database.USER_INFO);
        if(mLoginBean!=null&&mLoginBean.getToken()!=null)
            token=mLoginBean.getToken();
        String mac=Hawk.get("address","");


        EasyConfig.getInstance().addHeader("authorization",token).setServer(new RequestServer(BodyType.FORM))
                .addHeader("MAC",TextUtils.isEmpty(mac) ? "" : mac.toLowerCase(Locale.CHINA)).addHeader("osType","1").into();

        EasyHttp.post(this).api(jfBpApi).request(new OnHttpListener<String>() {
            @Override
            public void onSucceed(String result) {
                TLog.Companion.error("---------??????="+result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code") == 200){
                        MeasureBpBean measureBpBean = new Gson().fromJson(jsonObject.getString("data"),MeasureBpBean.class);
                        stopMeasure(measureBpBean);
                    }else{
                        backStartMeasureBp(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {
                TLog.Companion.error("---------??????Exception="+e.getMessage());
                backStartMeasureBp(false);
            }
        });


    }




    public void uploadPPGCacheData(){
        ppgIndex = 0;
        LoginBean loginBean = Hawk.get(com.app.fmate.Config.database.USER_INFO);
        if(loginBean == null)
            return;
        String userId = loginBean.getUser().getUserId();
        String mac = loginBean.getUser().getMac();
        if(userId == null || mac == null)
            return;
        List<PPG1CacheDb> list = DbManager.getDbManager().getDayPPGData(userId,mac,DateUtil.getCurrDate(),"0");
        if(list == null || list.size() == 0)
            return;

        TLog.Companion.error("----???????????????="+list.size());
        PPG1CacheDb pd = list.get(0);
        TLog.Companion.error("-----???????????????="+pd.getDayStr()+" "+pd.getPpgTimeStr()+" "+pd.getDbStatus());
        this.uploadDbPPgList = list;
        handler.sendEmptyMessageDelayed(0x01,1000);
    }


    private void uploadAllPPGSource(PPG1CacheDb ppg1CacheDb,boolean is){
        StringBuilder sb = new StringBuilder();
        String timeStr = ppg1CacheDb.getPpgTimeStr();
        List<Integer> bgL = ppg1CacheDb.getBbpDataList();
        for (int i = 0; i < bgL.size(); i++) {
            if (i == bgL.size() - 1) {
                sb.append(bgL.get(i));
            } else {
                sb.append(bgL.get(i));
                sb.append(",");
            }
        }

        uplaods(ppg1CacheDb,sb.toString(),timeStr);
    }

    private void uplaods(PPG1CacheDb pd,String data,String timeStr){

        JfBpApi jfBpApi = new JfBpApi();
        jfBpApi.setBp(data,timeStr);
        String token = null;
        LoginBean mLoginBean= Hawk.get(com.app.fmate.Config.database.USER_INFO);
        if(mLoginBean!=null&&mLoginBean.getToken()!=null)
            token=mLoginBean.getToken();
        String mac=Hawk.get("address","");

        EasyConfig.getInstance().addHeader("authorization",token).setServer(new RequestServer(BodyType.FORM))
                .addHeader("MAC",TextUtils.isEmpty(mac) ? "" : mac.toLowerCase(Locale.CHINA)).addHeader("osType","1").into();

        EasyHttp.post(this).api(jfBpApi).request(new OnHttpListener<String>() {
            @Override
            public void onSucceed(String result) {
                TLog.Companion.error("---------??????="+result);
                PPG1CacheDb ppd = new PPG1CacheDb();
                ppd.setBbpDataList(pd.getBbpDataList());
                ppd.setUserId(pd.getUserId());
                ppd.setDayStr(pd.getDayStr());
                ppd.setDeviceMac(pd.getDeviceMac());
                ppd.setPpgTimeStr(pd.getPpgTimeStr());
                ppd.setDbStatus(pd.getDbStatus());
                int upS = ppd.updateAll("ppgTimeStr = ? and dbStatus = ?",pd.getPpgTimeStr(),"1");

                TLog.Companion.error("------?????????????????????---??????="+upS);
                ppgIndex++;
                handler.sendEmptyMessageDelayed(0x01,1000);

                try {
                    JSONObject jsonObject = new JSONObject(result);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Exception e) {
                TLog.Companion.error("---------??????Exception="+e.getMessage());
                ppgIndex++;
                handler.sendEmptyMessageDelayed(0x01,1000);
            }
        });
    }



    private void sendTimeToDevice(JfLastPPGApi.PPGBean db ,long time){
        //??????
        byte[] timeArray = HexDump.toByteArray(time-946656000L);

        byte[] cmdArray = new byte[]{0x0B, 0x01, 0x01, 0x00, 0x01, 0x07, 0x02, 0x00, 0x07, timeArray[0], timeArray[1], timeArray[2], timeArray[3],
                (byte) db.getSystolicPressure(), (byte)db.getDiastolicPressure(), 80
        };

        byte[] resultArray = CmdUtil.getFullPackage(cmdArray);
        BleWrite.writeCommByteArray(resultArray, true, new BleWrite.SpecifySleepSourceInterface() {
            @Override
            public void backSpecifySleepSourceBean(SpecifySleepSourceBean specifySleepSourceBean) {

            }

            @Override
            public void backStartAndEndTime(byte[] startTime, byte[] endTime) {

            }
        });

    }


    //???????????????????????????????????????
    private void stopMeasure(MeasureBpBean measureBpBean){

        //??????
        long longTime = TimeUtil.formatTimeToLong(deviceMeasureTime,0);
        byte[] timeArray = HexDump.toByteArray(longTime-946656000L);

        byte[] cmdArray = new byte[]{0x0B, 0x01, 0x01, 0x00, 0x01, 0x07, 0x02, 0x00, 0x07, timeArray[0], timeArray[1], timeArray[2], timeArray[3],
                (byte) measureBpBean.getSbp(), (byte) measureBpBean.getDbp(), (byte) measureBpBean.getHeartRate()
        };

        byte[] resultArray = CmdUtil.getFullPackage(cmdArray);
        BleWrite.writeCommByteArray(resultArray,true,new  BleWrite.SpecifySleepSourceInterface(){
            @Override
            public void backSpecifySleepSourceBean(SpecifySleepSourceBean specifySleepSourceBean) {

            }

            @Override
            public void backStartAndEndTime(byte[] startTime, byte[] endTime) {

            }

        });
    }


    //??????????????????
    private void stopMeasureBp(){
        byte[] cmdArray = new byte[]{0x0B,0x01,0x01,0x00,0x01,0x01};

        byte[] resultArray = CmdUtil.getFullPackage(cmdArray);


        BleWrite.writeCommByteArray(resultArray, true, new BleWrite.SpecifySleepSourceInterface() {
            @Override
            public void backSpecifySleepSourceBean(SpecifySleepSourceBean specifySleepSourceBean) {

            }

            @Override
            public void backStartAndEndTime(byte[] startTime, byte[] endTime) {

            }
        });

    }


}
