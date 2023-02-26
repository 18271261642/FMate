package com.app.fmate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fmate.bean.Bean;
import com.app.fmate.bean.YearBean;
import com.app.fmate.custom.MyMarkerView;
import com.app.fmate.utils.BloodPreesureChartView;
import com.shon.connector.utils.TLog;
import com.app.fmate.view.CustomBarChart;
import com.app.fmate.view.DateUtil;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fmate.network.api.javaMapView.MapViewApi.mapViewApi;

public class TestActivity2 extends AppCompatActivity {
    BloodPreesureChartView mBloodPreesureChartView;
    ArrayList<BloodPreesureChartView.BarData> mlist = new ArrayList<>();
    TextView tvOnclick, tvOnclick2;
    CustomBarChart chart;
    private String mTitles[] = {
            "上海", "头条推荐", "生活", "娱乐八卦", "体育",
            "段子", "美食", "电影", "科技", "搞笑",
            "社会", "财经", "时尚", "汽车", "军事",
            "小说", "育儿", "职场", "萌宠", "游戏",
            "健康", "动漫", "互联网"};
    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
    long time = 0, endTime = 0;
    ArrayList<YearBean> mYearList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        TLog.Companion.error("启动");
//        mBloodPreesureChartView = findViewById(R.id.BloodPreesureChartView);
//        //    TLog.Companion.error("启动1");
//        mBloodPreesureChartView = new BloodPreesureChartView(this);
//        tvOnclick=findViewById(R.id.tvOnclick);
        tvOnclick2=findViewById(R.id.tvOnclick2);
        tvOnclick2.setOnClickListener(v -> {
            TLog.Companion.error("点击了");
            setHttp();
        }
        );
        TLog.Companion.error("===时间+"+DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS,System.currentTimeMillis()));


        int val=52077;
        byte rgb,rgb1,rgb2;
        TLog.Companion.error("val=="+val);
        rgb = (byte) ((val>>11));//R
        rgb1= (byte) ((val>>5 &0X7C0));//G
        rgb2 = (byte) (val<<11);//B

        TLog.Companion.error(" rgb[i * 3]+="+ rgb);
        TLog.Companion.error(" rgb[i * 3]+="+ rgb1);
        TLog.Companion.error(" rgb[i * 3]+="+ rgb2);
        TLog.Companion.error(" rgb[i * 3]+="+ (rgb+rgb1));
        int num = 13340;
        int i = 0;
        int keyBin = 4096;
   //     TLog.Companion.error("===++" + (4244 % 4096));
        return;
//        while (i < num) {
//            if ((i / keyBin) > 0) {
//                TLog.Companion.error("===" + i);
//                //   TLog.Companion.error("==="+keyBin);
//                if ((i % keyBin) < 200) {
//                    int binWei =200- (i % keyBin);
//                     TLog.Companion.error("binwei++"+(i%keyBin));
//                    i += binWei;
//                } else {
//                    //     TLog.Companion.error("if ===else 200" );
//                    i += 200;
//                }
//            } else {
//                if ((i % keyBin) < 200) {
//                    int start = 0;
//                    if (i == 0)
//                        start = 244;
//                    else
//
//                    //    TLog.Companion.error("头++"+start);
//                    i += start;
//                } else {
//                    //   TLog.Companion.error("else ===else 200" );
//                    i += 200;
//                }
//             //   TLog.Companion.error("else ====" + i);
//            }
//        }

        // TLog.Companion.error("list==" + mlist.size());
//        mBloodPreesureChartView.setBarChartData(mlist);
//        intView();
//        test1();
//        Calendar cd = Calendar.getInstance();
//        MotionListDao mMotionListDao;
//        mMotionListDao = AppDataBase.Companion.getInstance().getMotionListDao();
//        curDate=DateUtil.firstMonthTime(curDate);
//        curDate=DateUtil.firstYearTime(curDate);
//        TLog.Companion.error("curDate"+curDate);
//        TLog.Companion.error("打印运动数据++"+new Gson().toJson(mMotionListDao.getAllRoomMotionList()));
//        try {
//            long time=DateUtil.convertDateToLong(curDate);
//            TLog.Companion.error("starttime+"+time);
//            long endTime=DateUtil.getMonthLastDate(time);
//            TLog.Companion.error("endtime+"+endTime);
//
//            TLog.Companion.error("starttime+"+(time/1000- Config.TIME_START));
//            TLog.Companion.error("endtime+"+(endTime/1000- Config.TIME_START));
//            TLog.Companion.error("打印运动数据++"+new Gson().toJson(mMotionListDao.getTimeStepList(DateUtil.convertDateToLong(curDate)/1000- Config.TIME_START,671760000)));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//          curDate=DateUtil.firstMonthTime(curDate);
//        try {
//        time = DateUtil.convertDateToLong(curDate);
//        endTime=DateUtil.getMonthLastDate(time);
//                     } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//        tvOnclick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                    for (int i = 0; i <31 ; i++) {
//                    }
//                    TLog.Companion.error("打印运动数据++"+new Gson().toJson(mMotionListDao.getAllRoomMotionList()));
//                TLog.Companion.error("duabyu=="+DateUtil.getDate(DateUtil.YYYY_MM_DD, curDate));
//            }
//        });
//        tvOnclick2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //指定时间内的数据
//                mYearList=new ArrayList<>();
//                List<MotionListBean> motionListBeanList=mMotionListDao.getTimeStepList(time/1000-Config.TIME_START,endTime/1000-Config.TIME_START+1);
//                for (int i = 0; i <motionListBeanList.size() ; i++) {
////                    TLog.Companion.error("月++"+motionListBeanList.get(i).getStartTime());
//                    //当前下标的开始时间
//                    long time =motionListBeanList.get(i).getStartTime();
//                    time=(time+Config.TIME_START)*1000;
////                    TLog.Companion.error("time"+time);
////                    TLog.Companion.error("month"+DateUtil.getMonth(time));
////                    TLog.Companion.error("day"+DateUtil.getDay(time));
//                    boolean isContain = false;
//                    for (int j = 0; j <mYearList.size() ; j++) {  //一年12个月
//                        if (DateUtil.getYear((mYearList.get(j).getMonth()+Config.TIME_START)*1000)==DateUtil.getYear(time) //判断是否是需要的指定年
//                                &&DateUtil.getMonth(mYearList.get(j).getMonth())==j+1)//判断是否是需要的指定月
//                        {
//                            isContain=true;
//                        }
//                    }
//                    if(!isContain)
//                    {
//                        long step=0;
//                        int month=0;
//                        for (int j = i; j <motionListBeanList.size() ; j++) {
//                            long sTime =DateUtil.getYear(((motionListBeanList.get(j).getStartTime()+Config.TIME_START)*1000));
//                            long eTime =(DateUtil.getYear(System.currentTimeMillis()));
//                            int sMonth=DateUtil.getMonth(((motionListBeanList.get(j).getStartTime()+Config.TIME_START)*1000));
//                            int eMonth=(j+1);
//                            if(sTime== eTime//判断是否是需要的指定年
//                                &&sMonth==eMonth
//                            )
//                            {
//                                TLog.Companion.error("打印的月份++"+DateUtil.getMonth(time));
//                                step+=motionListBeanList.get(j).getTotalSteps()+1;
//                                month=j+1;
//                            }
//                        }
//                    }
//                }
//                TLog.Companion.error("已疯++"+new Gson().toJson(mYearList));
////                JumpUtil.startMainHomeActivity(TestActivity.this);
////                curDate=DateUtil.firstMonthTime(curDate,false);
////                TLog.Companion.error("duabyu=="+DateUtil.getDate(DateUtil.YYYY_MM_DD, curDate));
////                try {
////
////                    TLog.Companion.error("时间戳=="+DateUtil.convertDateToLong(curDate));
////                } catch (ParseException e) {
////                    e.printStackTrace();
////                }
//            }
//        });
    }


    public static MultipartBody.Part createBodyPart(File file) {
        RequestBody requestFile = createFileFormData(file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }
    public static RequestBody createFileFormData(File file) {
        return RequestBody.create(file, MediaType.parse("multipart/form-data"));
    }
    public void setHttp()
    {
        String latStr="[{\\\"latitude\\\":22.686085508468505,\\\"longitude\\\":113.8124940821733},{\\\"latitude\\\":22.686080271371615,\\\"longitude\\\":113.81260128124138},{\\\"latitude\\\":22.68605525732661,\\\"longitude\\\":113.81269820491734},{\\\"latitude\\\":22.68599023014117,\\\"longitude\\\":113.81262447738722},{\\\"latitude\\\":22.686034333110864,\\\"longitude\\\":113.81271510099117},{\\\"latitude\\\":22.68597696642464,\\\"longitude\\\":113.81263060114297}]";
      List<Integer> mList=new ArrayList();
        mList.add(103);
        mList.add(102);
        mList.add(101);
        mList.add(100);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("positionData",latStr);
        hashMap.put("createTimeStamp",System.currentTimeMillis()/1000);
        hashMap.put("type","1");
        hashMap.put("distance","100");
        hashMap.put("motionTime","10");
        hashMap.put("calorie","101");
        hashMap.put("steps","1000");
        hashMap.put("avgPace","10");
        hashMap.put("avgSpeed","11");
        hashMap.put("heartRateData",new Gson().toJson(mList));
        Call data= mapViewApi.motionInfoSave(hashMap);

        data.enqueue(new Callback<BaseData>(){
            @Override
            public void onResponse(Call<BaseData> call, Response<BaseData> response) {
            TLog.Companion.error("call=="+new Gson().toJson(response.body().getData()));
            }

            @Override
            public void onFailure( Call<BaseData> call, Throwable t) {
                TLog.Companion.error("onFailure call=="+call.toString()+"t+="+t.toString());
            }
        });

    }
    public void test1() {



        List<Bean> beanList = new ArrayList<>();
        beanList.add(new Bean(1, 1));
        beanList.add(new Bean(1, 2));
        beanList.add(new Bean(2, 3));
        beanList.add(new Bean(6, 7));
        beanList.add(new Bean(1, 2));
        beanList.add(new Bean(2, 9));
        //新的集合
        List<Bean> beans = new ArrayList<>();
        for (int i = 0; i < beanList.size(); i++) {
            Bean bean = beanList.get(i);

            //先判断是否已经存在此type 存在的话就不需要第二次循环了
            boolean isContain = false;
            for (int j = 0; j < beans.size(); j++) {
                if (beans.get(j).getType() == bean.getType()) {
                    isContain = true;
                }
            }
            if (!isContain) {
                int total = bean.getDistance();
                //重复type的累加 如果后面加过
                for (int j = i; j < beanList.size(); j++) {
                    if (beanList.get(j).getType() == bean.getType()) {
                        total = total + beanList.get(j).getDistance();
                    }
                }
                beans.add(new Bean(beanList.get(i).getType(), total));
            }
        }
        TLog.Companion.error("测试数据+" + beans.size());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void hasMapTest() {
        List<Bean> exList = new ArrayList<>();
        exList.add(new Bean(1, 2));
        exList.add(new Bean(1, 2));
        exList.add(new Bean(2, 2));
        exList.add(new Bean(6, 7));
        exList.add(new Bean(1, 2));
        exList.add(new Bean(2, 9));
        //  List<Bean> Ex = new ArrayList<>();
        Bean Ex = new Bean();
//        Map<String, List<Bean>> collect = exList.stream()
//                .collect(Collectors.toMap(Ex::getType,
//                        e -> {
//                            // 你这里直接返回distance的值也行
//                            ArrayList<Integer> list = Lists.newArrayList();
//                            list.add(e.getDistance());
//                            return list;
//                        },
//                        (oldList, newList) -> {
//                            // 你在这里把distance累加，不放集合也行
//                            oldList.addAll(newList);
//                            return  oldList;
//                        }));
    }

    private void intView() {
        chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
        chart.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        chart.animateY(1500);

        chart.getLegend().setEnabled(false);
        setData();
    }

    public void setData() {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            float multi = (10 + 1);
            float val = (float) (Math.random() * multi) + multi / 3;
            values.add(new BarEntry(i, val));
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Data Set");
            //    set1.setColors(getResources().getColor(R.color.deepColor),getResources().getColor(R.color.deepColor));
            set1.setColor(getResources().getColor(R.color.deepColor));
            set1.setDrawValues(false);
            set1.setHighLightColor(getResources().getColor(R.color.deepColor));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            chart.setData(data);
            chart.setFitBars(true);
        }

        chart.invalidate();
    }

//    private void setMtu(int setMtu) {
//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//        bluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                device.connectGatt(TestActivity2.this, true, new BluetoothGattCallback() {
//                    @Override
//                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                        super.onServicesDiscovered(gatt, status);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            if (setMtu > 23 && setMtu < 512) {
//                                gatt.requestMtu(setMtu);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
//                        super.onMtuChanged(gatt, mtu, status);
//                    }
//                });
//            }
//        });
//    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
