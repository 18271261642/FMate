package com.example.xingliansdk.ui.fragment.map.view;

import android.content.Context;
import android.graphics.Bitmap;

import com.amap.api.maps.model.LatLng;
import com.example.xingliansdk.BaseData;
import com.example.xingliansdk.Config;
import com.example.xingliansdk.R;
import com.example.xingliansdk.bean.db.AmapSportBean;
import com.example.xingliansdk.bean.db.AmapSportDao;
import com.example.xingliansdk.bean.room.AppDataBase;
import com.example.xingliansdk.network.api.login.LoginBean;
import com.example.xingliansdk.ui.fragment.map.task.SNAsyncTask;
import com.example.xingliansdk.ui.fragment.map.task.SNVTaskCallBack;
import com.example.xingliansdk.utils.CountTimer;
import com.example.xingliansdk.utils.ResUtil;
import com.example.xingliansdk.utils.ShowToast;
import com.example.xingliansdk.utils.Utils;
import com.example.xingliansdk.view.DateUtil;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.BleWrite;
import com.shon.connector.utils.TLog;
import com.sn.map.bean.SNLocation;
import com.sn.map.interfaces.OnMapLocationAddressListener;
import com.sn.map.interfaces.OnMapScreenShotListener;
import com.sn.map.interfaces.OnSportMessageListener;
import com.sn.map.utils.GPSUtil;
import com.sn.map.view.SNMapHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.xingliansdk.network.api.javaMapView.MapViewApi.mapViewApi;
import static com.shon.connector.Config.ControlClass.APP_REAL_TIME_HEART_RATE_SWITCH_KEY;

/**
 * 功能:运动轨迹
 */
public class RunningPresenterImpl extends BasePresenter<IRunningContract.IView> implements IRunningContract.IPresenter, CountTimer.OnCountTimerListener  {

    private IRunningContract.IView view;
    private LinkedList<SNLocation> locations;
    private ArrayList<Integer> heartList;//心率
    private  int step; //步数

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    /**
     * 超强
     */
    public static final int SIGNAL_STRONG_MAX = 3;
    /**
     * 强
     */
    public static final int SIGNAL_STRONG = 2;
    /**
     * 中
     */
    public static final int SIGNAL_MIDDLE = 1;
    /**
     * 弱
     */
    public static final int SIGNAL_WEAK = 0;

    /**
     * GPS已关闭
     */
    public static final int SIGNAL_GPS_OFF = -1;
    public static final int CODE_SUCCESS = 0x00;
    public static final int CODE_ERROR = -0x10;
    public static final int CODE_COUNT_LITTLE = -0x20;
    public static final int CODE_TIME_OUT = -0x30;
    private SNMapHelper mMapHelper;
     private CountTimer countTimer = new CountTimer(1000, this);
    /**
     * 截图
     */
    private Bitmap mMapBitmap;
    /**
     * 位置具体名称
     */
    private String mMapAddress;
    private long millisecond;
    private String millisecondStr;
    private  long createTime;
    public RunningPresenterImpl(IRunningContract.IView view) {
        this.view = view;
        createTime=System.currentTimeMillis();
    }


    @Override
    public void initMapListener(SNMapHelper mapHelper) {
        this.mMapHelper = mapHelper;
        mapHelper.requestLastLocation();
        mapHelper.setOnSportMessageListener(onSportMessageListener);
        onSportMessageListener.onSignalChanged(0);

        // 判断GPS模块是否开启，如果没有则开启
        if(getView() == null)
            return;
        if (!GPSUtil.isGpsEnable((Context) getView())) {
            view.onUpdateGpsSignal(SIGNAL_GPS_OFF);
        }
        BleWrite.writeHeartRateSwitchCall(APP_REAL_TIME_HEART_RATE_SWITCH_KEY, (byte) 0x02);
      //  ownListener(Hawk.get("address",""));
    }

    @Override
    public void initDefaultValue() {

        String distances;
        String hourSpeed;
        //  UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
        //如果单位是英里,则需要转一下
//        if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
//            distances = (ResUtil.format("%.2f %s", UnitConversion.kmToMiles((float) 0), ResUtil.getString(R.string.unit_mile)));
//            hourSpeed = (ResUtil.format("%.2f %s", UnitConversion.kmToMiles((float) 0), ResUtil.getString(R.string.unit_mile_h)));
//        } else {




        distances = (ResUtil.format("%.2f", 0.0));
        hourSpeed = (ResUtil.format("%.2f %s", 0.0, "km/h"));
//        }
        String calories = (ResUtil.format("%.2f ", 0.0));
        String pace = (ResUtil.format("%02d'%02d\"", 0, 0));
        if (!isUIEnable()) return;
        TLog.Companion.error("更新地理位置了 initDefaultValue");
        view.onUpdateSportData(distances, calories, hourSpeed, pace,new ArrayList<>());
    }

    @Override
    public void requestSettingConfig() {
        if (!isUIEnable()) return;
        //  view.onUpdateSettingConfig(MapSettingStorage.isKeepScreenEnable(), MapSettingStorage.isWeatherEnable());
    }

    @Override
    public void requestWeatherData() {
//        SNAsyncTask.execute(new SNVTaskCallBack() {
//            String weatherQuality = ResUtil.getString(R.string.content_general);
//            int weatherType = 0;
//            String weatherTemperatureRange;
//
//            @Override
//            public void run() throws Throwable {
////                WeatherListBean weatherListBean = WeatherStorage.getWeatherListBean();
////                if (weatherListBean == null) return;
////                List<WeatherBean.DataBean> dataList = weatherListBean.getData();
////                if (IF.isEmpty(dataList)) return;
////                //从历史天气列表中取出今天的天气
////                WeatherBean.DataBean name = null;
////                for (WeatherBean.DataBean bean : dataList) {
////                    if (DateUtil.equalsToday(bean.getDate())) {
////                        name = dataList.get(0);
////                    }
////                }
////                //如果天气历史中仍然没有今天的天气,就拿最后一次的天气 先顶顶
////                if (name == null && dataList.size() > 0) {
////                    name = dataList.get(dataList.size() - 1);
////                }
////                if (name == null) return;
////
////                weatherType = name.getCond_code_type();
////                int temperatureMin = name.getTmp_min();
////                int temperatureMax = name.getTmp_max();
////                UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
////                String format = "%d℃~%d℃";
////                if (unitConfig != null && unitConfig.getTemperatureUnit() == UnitConfig.TEMPERATURE_F) {
////                    format = "%d℉~%d℉";
////                    temperatureMin = (int) UnitConversion.CToF(temperatureMin);
////                    temperatureMax = (int) UnitConversion.CToF(temperatureMax);
////                }
//                //   weatherTemperatureRange = ResUtil.format(format, temperatureMin, temperatureMax);
//            }
//
//            @Override
//            public void done() {
//                if (!isUIEnable()) return;
//                view.onUpdateWeatherData(weatherType, weatherTemperatureRange, weatherQuality);
//            }
//        });

    }


    @Override
    public void requestMapFirstLocation() {
        double latitude = 0;
        double longitude = 0;
        SNLocation lastLocation = mMapHelper.getLastLocation();
        if (lastLocation != null) {
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
        }
        TLog.Companion.error("初始化位置latitude++"+latitude);
        TLog.Companion.error("初始化位置longitude++"+longitude);
        //初始化地图时 显示默认上次的经纬度
        if (latitude > 0 && longitude > 0) {
            view.onUpdateMapFirstLocation(latitude, longitude);
        } else {
            //如果没有 那就默认北京
            latitude = 0;
            longitude = 0;
            if (latitude > 0 && longitude > 0) {
                if (!isUIEnable()) return;
                view.onUpdateMapFirstLocation(latitude, longitude);
            }
        }
    }


    private void saveSportData() {
        LinkedList<SNLocation> locations = mMapHelper.getLocations();
        if(locations == null || locations.isEmpty())
            return;
//        if (locations.size() <= 1) {
//            onCallSaveSportDataStatusChange(CODE_COUNT_LITTLE);
//            return;
//        }

        mMapBitmap = null;
        mMapAddress = null;
      //  mMapHelper.screenCapture(true, onMapScreenShotListener);
        mMapHelper.requestGetLocationAddress(locations.getLast(), onMapLocationAddressListener);
//        TimeOutUtil.setTimeOut(mHandler, 10 * 1000L, new TimeOutUtil.OnTimeOutListener() {
//            @Override
//            public void onTimeOut() {
//                onCallSaveSportDataStatusChange(CODE_TIME_OUT);
//            }
//        });
    }

    private void onCallSaveSportDataStatusChange(int codeCountLittle) {
        view.onSaveSportDataStatusChange(codeCountLittle);
        //   TimeOutUtil.removeTimeOut(mHandler);
    }

    @Override
    public void requestStartSport() {

        if (!mMapHelper.isStarted()) {
            // if (MapSettingStorage.isBeginVibrationEnable()) {
            requestDeviceVibrator();
            // }

            mMapHelper.startSport();
            if (isUIEnable()) {
                view.onSportStartAnimationEnable(true);
            }
            countTimer.start();
        }

    }

    private void requestDeviceVibrator() {
        //TODO 尴尬了  手环只支持振动开和关, 而需求是要实现振动一下 只能这样先顶顶 看看以后手环有没有加协议
//        SNAsyncTask.execute(new SNVTaskCallBack() {
//            @Override
//            public void run() throws Throwable {
//                //    SNBLEHelper.sendCMD(SNCMD.get().setFindDeviceStatus(true));
//                sleep(1000);
//                //    SNBLEHelper.sendCMD(SNCMD.get().setFindDeviceStatus(false));
//            }
//        });
    }

    @Override
    public void requestStopSport() {
//        if (MapSettingStorage.isEndVibrationEnable()) {
//            requestDeviceVibrator();
//        }
        if (mMapHelper != null && mMapHelper.isStarted()) {
            mMapHelper.stopSport();
            countTimer.stop();
           // saveSportData();
        } else {
            if (!isUIEnable()) return;
            onCallSaveSportDataStatusChange(CODE_SUCCESS);
        }

    }

    @Override
    public void requestRetrySaveSportData() {
        saveSportData();
    }

    @Override
    public void saveHeartAndStep(ArrayList<Integer> heartList,int step) {
        this.heartList=heartList;
        this.step=step;
    }


    private OnSportMessageListener onSportMessageListener = new OnSportMessageListener() {


        @Override
        public void onSignalChanged(int level) {
            if (!isUIEnable()) return;
            if (level >= 10) {
                view.onUpdateGpsSignal(SIGNAL_STRONG_MAX);
            } else if (level >= 6) {
                view.onUpdateGpsSignal(SIGNAL_STRONG);
            } else if (level >= 4) {
                view.onUpdateGpsSignal(SIGNAL_MIDDLE);
            } else if (level < 4) {
                view.onUpdateGpsSignal(SIGNAL_WEAK);
            }
        }

        @Override
        public void onSportChanged(final LinkedList<SNLocation> locations) {
            RunningPresenterImpl.this.locations = locations;
            updateSportMessage(locations);
        }
    };

    //运动类型
    int sportType = Hawk.get(Config.database.AMAP_SPORT_TYPE,1);
    private void updateSportMessage(final LinkedList<SNLocation> locations) {
        LoginBean userInfo = Hawk.get(Config.database.USER_INFO, new LoginBean());
        boolean isUnit = (userInfo==null||userInfo.getUserConfig().getDistanceUnit()==1);
        if(isUnit){     //英制

        }else{  //公制

        }


        SNAsyncTask.execute(new SNVTaskCallBack() {
            private List<LatLng> latLngs = new ArrayList<>();
            private String distances;
            private String calories;
            private String hourSpeed;
            private String pace;

            @Override
            public void run() throws Throwable {
                RunningSportDataUtil.SportData sportData = RunningSportDataUtil.calculateSportData(RunningSportDataUtil.calculateBaseSportData(mMapHelper, locations),sportType);
                distances = (ResUtil.format("%.2f", isUnit ? Utils.kmToMile(sportData.distances) : sportData.distances));
                hourSpeed = (ResUtil.format("%.2f%s", sportData.hourSpeed, ResUtil.getString(R.string.unit_km_h)));
                calories = (ResUtil.format("%.2f", sportData.calories));
                kcalcanstanc=sportData.calories;
                pace = (ResUtil.format("%02d'%02d\"", sportData.speed_minutes, sportData.speed_seconds));
                TLog.Companion.error("更新地理位置了 run");
                //加了一个添加
                for(SNLocation snLocation : locations){
                    latLngs.add(new LatLng(snLocation.getLatitude(),snLocation.getLongitude()));
                }
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                TLog.Companion.error("更新地理位置了 done");
                view.onUpdateSportData(distances, calories, hourSpeed, pace,latLngs);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locations != null && !locations.isEmpty()) {
            updateSportMessage(locations);
        }
    }

    /**
     * 地图截图和储存
     */
    private OnMapScreenShotListener onMapScreenShotListener = new OnMapScreenShotListener() {


        @Override
        public void onMapScreenShot(final Bitmap bitmap) {
            mMapBitmap = bitmap;
            save();

        }
    };

    /**
     * 获取地理位置
     */
    private OnMapLocationAddressListener onMapLocationAddressListener = new OnMapLocationAddressListener() {

        @Override
        public void onLocationAddress(String address) {
            mMapAddress = address;
            save();
        }

        @Override
        public void onLocationAddressFailed(int code) {
            if (!isUIEnable()) return;
            onCallSaveSportDataStatusChange(CODE_ERROR);
        }

    };
    double  kcalcanstanc = 0.0; //计算卡路里常量

    private void save() {

        //条件: 需要 截图完成 和地理编码转换完成 才进行下一步
        //两个条件都必须满足
        if (mMapAddress == null ) {
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private String mLocationJsonData;
            private RunningSportDataUtil.SportData sportData;
            private RunningSportDataUtil.BaseSportData baseSportData;
       //     private File file = new File(Constant.Path.CACHE_MAP, "map_capture_image.jpg");
            private String dateTime;

            @Override
            public void run() throws Throwable {
                dateTime = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM_SS);
                LinkedList<SNLocation> locations = mMapHelper.getLocations();
             //   StravaTool.saveToGpxFile(RunTrackUtil.convertToGPXs(locations), dateTime);
           //     mLocationJsonData = RunTrackUtil.convertToJson(locations);
                //计算
                baseSportData = RunningSportDataUtil.calculateBaseSportData(mMapHelper, locations);
                sportData = RunningSportDataUtil.calculateSportData(baseSportData,sportType);
                //取截图的中间部分
            //    int screenWidth = DIYViewUtil.getScreenWidth(App.getContext().getResources());
              //  Bitmap bitmapCenterRange = BitmapUtil.getBitmapCenterRange(mMapBitmap, screenWidth, screenWidth);
            //    bitmapCenterRange.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));

            }

            @Override
            public void done() {
                if (baseSportData == null  ) {
                    onCallSaveSportDataStatusChange(CODE_ERROR);
                } else {

                    if(getView() == null)
                        return;

                    if (!GPSUtil.isGpsEnable((Context) getView())) {
                        return;
                    }


                    LinkedList<SNLocation> locations = mMapHelper.getLocations();
                    LoginBean loginBean = Hawk.get(Config.database.USER_INFO);
                    if(loginBean == null)
                        return;
                    String userId = loginBean.getUser().getUserId();
                    if(userId == null)
                        return;
                    //总距离
                    String countDistance = Math.round(baseSportData.distanceTotal)+"";
                    TLog.Companion.error("距离++"+countDistance);
                    //总时长 ，秒
                    String countTime = (millisecond / 1000)+"";
                    //结束运动的时间
                    long endTime = System.currentTimeMillis()/1000;
                    //卡路里
                 //   String countCalories = decimalFormat.format(Utils.mul(kcalcanstanc,baseSportData.)/1000) +"";
                    String countCalories=String.valueOf(kcalcanstanc);
                    TLog.Companion.error("sportData.calories=="+sportData.calories+", kcalcanstanc +"+kcalcanstanc);
                    //平均速度 米/秒
                  //  String avgSpeed = baseSportData.speedAvg+"";
                    Double avgSpeed = baseSportData.distanceTotal/(millisecond/1000);
                    TLog.Companion.error("平均速度 米/秒=="+baseSportData.speedAvg);


                    //平均配速
                    DateUtil.HMS hms =   DateUtil.getHMSFromMillis(millisecond);
                    int speedMinutes = hms.getHour() * 60 + hms.getMinute();
                    int speedSeconds = hms.getSecond();
                    TLog.Companion.error("hms="+hms+"speedMinutes++"+speedMinutes+"speedSeconds++"+speedSeconds);
//                    String paceStr = (speedMinutes * 60 + speedSeconds)+"";
                    TLog.Companion.error("millisecond++"+millisecond+" countDistance++"+countDistance +" baseSportData.distanceTotal++"+baseSportData.distanceTotal);
                    String
                            paceStr = (millisecond/baseSportData.distanceTotal)+"";
                    TLog.Companion.error("  //平均配速=="+paceStr);
                    //经纬度集合
                    List<LatLng> resultLat = new ArrayList<>();
                    if(locations != null && locations.size()>0){
                        for(SNLocation snLocation : locations){
                            resultLat.add(new LatLng(snLocation.getLatitude(),snLocation.getLongitude()));
                        }
                    }
                    String latStr = new Gson().toJson(resultLat);

                    //mac地址
                    String macAddress = Hawk.get("address");
                    //运动类型
                    int sportType = Hawk.get(Config.database.AMAP_SPORT_TYPE,1);

                    AmapSportBean amapSportBean = new AmapSportBean();
                    amapSportBean.setUserId(userId);
                    amapSportBean.setDeviceMac(macAddress);
                    amapSportBean.setDayDate(Utils.getCurrentDate());
                    amapSportBean.setYearMonth(Utils.getCurrentDateByFormat("yyyy-MM"));
                    amapSportBean.setSportType(sportType);
                    amapSportBean.setMapType(1);
                    amapSportBean.setCurrentSportTime(millisecondStr);
                    amapSportBean.setEndSportTime(Utils.getCurrentDate1());
                    amapSportBean.setCurrentSteps(step);
                    amapSportBean.setDistance(countDistance);
                    amapSportBean.setCalories(countCalories);
                    amapSportBean.setAverageSpeed(""+avgSpeed);
                    TLog.Companion.error("paceStr=="+paceStr);
                    amapSportBean.setPace(paceStr);
                    amapSportBean.setLatLonArrayStr(latStr);
                    amapSportBean.setCreateTime(createTime/1000);
                    TLog.Companion.error("心率==="+new Gson().toJson(heartList));
                    amapSportBean.setHeartArrayStr(new Gson().toJson(heartList));
                    TLog.Companion.error("-----保存cans="+new Gson().toJson(amapSportBean));

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("positionData",latStr);
                    hashMap.put("createTimeStamp",createTime/1000);
                    hashMap.put("type",sportType);
                    hashMap.put("distance",countDistance);
                    hashMap.put("motionTime",countTime);
                    hashMap.put("calorie",countCalories);
                    hashMap.put("steps",step);
                    hashMap.put("avgPace",paceStr);
                    hashMap.put("avgSpeed",avgSpeed);
                    hashMap.put("heartRateData",new Gson().toJson(heartList));




                    Call   bean=mapViewApi.motionInfoSave(hashMap);
                    bean.enqueue(new Callback<BaseData>() {
                        @Override
                        public void onResponse(Call<BaseData> call, Response<BaseData> response) {
                            if(response.body().getCode()!=200)
                            {
                                ShowToast.INSTANCE.showToastLong(response.body().getMsg());
                                return;
                            }
                            AmapSportDao mAmapSportDao= AppDataBase.Companion.getInstance().getAmapSportDao();
                            mAmapSportDao.insert(amapSportBean);
                        }

                        @Override
                        public void onFailure(Call<BaseData> call, Throwable t) {
                        }
                    });
                    TLog.Companion.error("==="+new Gson().toJson(amapSportBean));


                }
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                if (!isUIEnable()) return;
                onCallSaveSportDataStatusChange(CODE_ERROR);
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mMapBitmap != null && !mMapBitmap.isRecycled()) {
                mMapBitmap.recycle();
            }
        } catch (Exception ignored) {
        }
          countTimer.stop();
    }

    @Override
    public void onCountTimerChanged(long millisecond) {
        this.millisecond = millisecond;
        DateUtil.HMS hms = DateUtil.getHMSFromMillis(millisecond);
        if (!isUIEnable()) return;
        this.millisecondStr = ResUtil.format("%02d:%02d:%02d", hms.getHour(), hms.getMinute(), hms.getSecond());
        view.onUpdateSportData(ResUtil.format("%02d:%02d:%02d", hms.getHour(), hms.getMinute(), hms.getSecond()));
    }
}
