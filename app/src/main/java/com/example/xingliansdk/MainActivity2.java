package com.example.xingliansdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.xingliansdk.adapter.ScanAdapter;
import com.example.xingliansdk.base.BaseActivity;
import com.example.xingliansdk.base.viewmodel.BaseViewModel;
import com.example.xingliansdk.bean.Bean;
import com.example.xingliansdk.bean.ScanBean;
import com.example.xingliansdk.service.SNAccessibilityService;
import com.example.xingliansdk.utils.PermissionUtils;
import com.orhanobut.hawk.Hawk;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.example.xingliansdk.bean.HRVBean;
import com.example.xingliansdk.custom.MyMarkerView;
import com.example.xingliansdk.utils.ExcelUtil;
import com.shon.connector.utils.ShowToast;
import com.shon.connector.utils.TLog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.shon.bluetooth.BLEManager;
import com.shon.bluetooth.core.Connect;
import com.shon.bluetooth.core.ConnectCallback;
import com.shon.bluetooth.core.call.Listener;
import com.shon.bluetooth.core.call.NotifyCall;
import com.shon.bluetooth.core.callback.ICallback;
import com.shon.bluetooth.core.callback.NotifyCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.utils.HexDump;

import com.example.xingliansdk.livedata.ScannerLiveData;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class MainActivity2 extends BaseActivity<BaseViewModel> implements View.OnClickListener, BleWrite.FirmwareInformationInterface, BleWrite.DeviceBloodPressureInterface {
    Button btnScan, btnScanTwo, btnExcel, btnOpen,btnHeartOpen;
    RecyclerView rvBle ;
    LineChart hartsHrr;
    BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
    ArrayList<ScanBean> mListHawk;
    ScanAdapter mScanAdapter;
    String mAddress = "", address2 = "";
    ArrayList<String> mAddressList = new ArrayList<>();
    String name = "", name1 = "";
    HRVBean mHRVBean;
    boolean Reconnection=false;
    LinkedList<HRVBean> mHRVList;
    ArrayList<HRVBean> mExcelList;
    ScannerLiveData mScannerLiveData;

    private void initView() {

        btnScan = findViewById(R.id.btn_scan);
        btnScanTwo = findViewById(R.id.btn_scantwo);
        btnExcel = findViewById(R.id.btn_excel);
        rvBle = findViewById(R.id.rv_ble);
        hartsHrr = findViewById(R.id.harts_hrr);
        btnOpen = findViewById(R.id.btn_open);
        btnHeartOpen = findViewById(R.id.btn_heart_open);
        btnScan.setOnClickListener(this);
        btnScanTwo.setOnClickListener(this);
        btnExcel.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        btnHeartOpen.setOnClickListener(this);
        hartsHrr.setVisibility(View.GONE);
        btnScan.setText(Reconnection?"0":"1");
         chartView();
    }


    private void chartView() {
        hartsHrr.setBackgroundColor(Color.WHITE);

        // disable description text
        hartsHrr.getDescription().setEnabled(false);

        // enable touch gestures
        hartsHrr.setTouchEnabled(true);

        // set listeners
        hartsHrr.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(hartsHrr);
        hartsHrr.setMarker(mv);

        // enable scaling and dragging
        hartsHrr.setDragEnabled(true);
        hartsHrr.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);
        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = hartsHrr.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = hartsHrr.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            hartsHrr.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            // axis range
            yAxis.setAxisMaximum(200f);
            yAxis.setAxisMinimum(0f);
        }
        // force pinch zoom along both axis
        hartsHrr.setPinchZoom(true);
        {   // // Create Limit Lines // //
            LimitLine llXAxis = new LimitLine(9f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);

            LimitLine ll1 = new LimitLine(150f, "Upper Limit");
            ll1.setLineWidth(4f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
            ll2.setLineWidth(4f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true);
            xAxis.setDrawLimitLinesBehindData(true);

            // add limit lines
            yAxis.addLimitLine(ll1);
            yAxis.addLimitLine(ll2);
            //xAxis.addLimitLine(llXAxis);
        }
        setData(0, 0, 0);
        // draw points over time
        hartsHrr.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = hartsHrr.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);

    }

    ArrayList<Entry> values = new ArrayList<>();
    ArrayList<Entry> values2 = new ArrayList<>();

    private void setData(int count, float range, float test) {
        values.add(new Entry(count, range));
        values2.add(new Entry(count, test));
        LineDataSet set1, set2;
        if (hartsHrr.getData() != null &&
                hartsHrr.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) hartsHrr.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set2 = (LineDataSet) hartsHrr.getData().getDataSetByIndex(1);
            set2.setValues(values2);
//            set1.notifyDataSetChanged();
            hartsHrr.getData().notifyDataChanged();
            hartsHrr.notifyDataSetChanged();
        } else {
            TLog.Companion.error("name-" + name);
            TLog.Companion.error("name1-" + name1);
            set1 = new LineDataSet(values, name);
//            set1.setDrawIcons(false);
//            set1.enableDashedLine(10f, 5f, 0f);
            set1.setColor(Color.RED);
            set1.setCircleColor(Color.RED);

            // 线的粗细和点的大小
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);

            // 画点出为实心圆
            set1.setDrawCircleHole(false);

            // 自定义图例条目
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setValueTextSize(9f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            set2 = new LineDataSet(values, name1);
            set2.setColor(Color.BLACK);
            set2.setCircleColor(Color.BLACK);
            set2.setLineWidth(2f);
            set2.setCircleRadius(3f);
            set2.setDrawCircleHole(false);
            set2.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setDrawFilled(true);
//            set1.setFillFormatter(new IFillFormatter() {
//                @Override
//                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                    return hartsHrr.getAxisLeft().getAxisMinimum();
//                }
//            });
//            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1); // add the data sets
            LineData data = new LineData(set1, set2);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);
            hartsHrr.setData(data);
        }
    }



    @Override
    public void onResult(String value, String value1) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
            case R.id.btn_scantwo:
                mScannerLiveData.clearData();
                startScan();
                break;
            case R.id.btn_excel:
                ExcelUtil.exportExcel(this, mExcelList);
                break;
            case R.id.btn_open: //手动发送指令已经在链接的时候发送
                BleWrite.writeHeartRateSwitchCall(Config.ControlClass.APP_REAL_TIME_HEART_RATE_SWITCH_KEY, (byte) 0X02);
                break;
            case R.id.btn_heart_open:
                hartsHrr.setVisibility(View.VISIBLE);
                rvBle.setVisibility(View.GONE);
               // chartView();
                break;
            default:
                break;
        }
    }

    @Override
    public int layoutId() {
        return R.layout.activity_main2;
    }

    void startScan() {
        showWaitDialog();
        rvBle.setVisibility(View.VISIBLE);
        scanner.stopScan(mScanCallback);
        ScanSettings mScanSettings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(3000)
                .setUseHardwareBatchingIfSupported(false)
                .build();
        List<ScanFilter> filters = new ArrayList<>();

        //byte[] t=new byte[]{0x51,0x21};
        //  filters.add(new ScanFilter.Builder().setManufacturerData(20769,t).build());
        scanner.startScan(filters, mScanSettings, mScanCallback);
    }

    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {
            super.onBatchScanResults(results);
            hideWaitDialog();
            mScannerLiveData.onScannerResult(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    void setAdapter() {
        rvBle.setLayoutManager(new LinearLayoutManager(MainActivity2.this, LinearLayoutManager.VERTICAL, false));
        mListHawk = new ArrayList<>();
        mScanAdapter = new ScanAdapter(mScannerLiveData.getScanResultList());
        rvBle.setAdapter(mScanAdapter);
        mScanAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<ParcelUuid> mList = mScanAdapter.getData().get(position).getScanRecord().getServiceUuids();
                TLog.Companion.error("=" + mScanAdapter.getData().get(position).getScanRecord().getServiceUuids());
                TLog.Companion.error("=" + new Gson().toJson(mList));
                //     showWaitDialog();

                if (mList == null) {
                    //    hideWaitDialog();
                    ShowToast.INSTANCE.showToastLong("该设备已被连接");
                    return;
                }
                for (int i = 0; i < mList.size(); i++) {
                    if (XingLianApplication.serviceUUID1.equalsIgnoreCase(mList.get(i).getUuid().toString())
                            || XingLianApplication.serviceUUIDXINLU.equalsIgnoreCase(mList.get(i).getUuid().toString())
                            || XingLianApplication.serviceUUID.equalsIgnoreCase(mList.get(i).getUuid().toString())
                    ) {
                        connectDevice(mScanAdapter.getData().get(position).getDevice(), mList.get(i).getUuid().toString());
                    }
                }


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        scanner.stopScan(mScanCallback);

    }


    void connectDevice(BluetoothDevice ble, String uuid) {
        TLog.Companion.error("进入了连接");
        new Connect(ble.getAddress())
                .setTimeout(10_000)
                .setReTryTimes(3)
                .enqueue(new ConnectCallback() {
                    @Override
                    public void onConnectSuccess(String address, BluetoothGatt gatt) {
                        TLog.Companion.error("成功 address+="+address);
                    }

                    @Override
                    public void onConnectError(String address, int errorCode) {
                      //  ShowToast.INSTANCE.showToastLong("链接异常,请从新链接");
                        BLEManager.getInstance().disconnectDevice(address);
                        for (int i = 0; i <mListHawk.size() ; i++) {
                            if(mListHawk.get(i).getDevice().getAddress().equals(address)) {
                                TLog.Companion.error("重连 address+="+address);
                                connectDevice(mListHawk.get(i).getDevice(), mListHawk.get(i).getUuid());
                                break;
                            }else{
                                TLog.Companion.error("404 no found");
                            }
                        }
                    }

                    @Override
                    public void onServiceEnable(String address, BluetoothGatt gatt) {
                        TLog.Companion.error("+=" + uuid);
                        scanner.stopScan(mScanCallback);
                        Reconnection=true;
                        if (uuid.equalsIgnoreCase(XingLianApplication.serviceUUIDXINLU)) {
                            ShowToast.INSTANCE.showToastLong("以为你连接成功第一个设备");
                            if (!mAddressList.contains(address)) {
                                mAddressList.add(address);
                                ScanBean mScanBean=new ScanBean(ble,uuid);
                                mListHawk.add(mScanBean);
                            }
//                            if (mAddress.isEmpty()) {
                            mAddress = address;
                            if (mAddressList.size() > 1) {
                                btnScanTwo.setText(ble.getName());
                                name1 = ble.getName();
                              //  address2 = address;
                            } else {

                                btnScan.setText(ble.getName());
                                TLog.Companion.error("打开第一个通知");
                                name = ble.getName();
                            }
                            new NotifyCall(address)
                                    .setCharacteristicUUID(XingLianApplication.readCharacterXINLIN)
                                    .setServiceUUid(XingLianApplication.serviceUUIDXINLU)
                                    .enqueue(new NotifyCallback() {
                                        @Override
                                        public boolean getTargetSate() {
                                            return true;
                                        }

                                        @Override
                                        public void onTimeout() {
                                            TLog.Companion.error("超时");
                                        }

                                        @Override
                                        public void onChangeResult(boolean result) {
                                            super.onChangeResult(result);
                                            if (result) {
                                                Listener();
                                            }
                                        }
                                    });
//                            }
                        } else {
                            if (uuid.equalsIgnoreCase(Config.serviceUUID)) {//  手表  "56:33:21:66:BF:01" 测试板子
                                ShowToast.INSTANCE.showToastLong("以为你连接成功第二个设备");
                                btnOpen.setVisibility(View.GONE);
                                TLog.Companion.error("打开第二个通知");
                                btnScanTwo.setText(ble.getName());
                                name1 = ble.getName();
                                address2 = address;
                                mListHawk.add(new ScanBean(ble,uuid));
                                Hawk.put("address",address);
                                new NotifyCall(address2)
                                        .setCharacteristicUUID(Config.readCharacter)
                                        .setServiceUUid(Config.serviceUUID)
                                        .enqueue(new NotifyCallback() {
                                            @Override
                                            public boolean getTargetSate() {
                                                return true;
                                            }

                                            @Override
                                            public void onTimeout() {
                                            TLog.Companion.error("超时");
                                            }

                                            @Override
                                            public void onChangeResult(boolean result) {
                                                super.onChangeResult(result);
                                                TLog.Companion.error("进入了");
                                                if (result) {
                                                    BleWrite.writeHeartRateSwitchCall(Config.ControlClass.APP_REAL_TIME_HEART_RATE_SWITCH_KEY, (byte) 0X02);
                                                    TLog.Companion.error("开启");
                                                    ownListener(address);
                                                }
                                            }
                                        });
                            }
                        }
                        if (!address2.isEmpty() && !mAddress.isEmpty()||mAddressList.size()>1) {
                          //  chartView();
                            rvBle.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onDisconnected(String address) {
                        TLog.Companion.error("onDisconnected address+="+address);
                    }
                });
    }

    void Listener() {
        new Listener(mAddress)
                .enqueue(new ICallback() {
                    @Override
                    public boolean process(String address, byte[] result,String uuid) {
                        TLog.Companion.error("address" + address + "  ,result++" + ByteUtil.getHexString(result));
                        if (mAddressList.size() > 1) {
                            if (mAddressList.get(0) == address) {
                                mHRVBean.setHeartRate(result[1]&0xff);
                            }
                            if (mAddressList.get(1) == address) {
                                mHRVBean.setTestHeartRate(result[1]&0xff);
                            }
                            setSaveData();
//                            HRVBean mExcelBean = new HRVBean();
//                            mExcelBean.setTestHeartRate(mHRVBean.getTestHeartRate());
//                            mExcelBean.setHeartRate(mHRVBean.getHeartRate());
//                            mExcelList.add(mExcelBean);
//                            mHRVList.add(0, mHRVBean);
//                            TLog.Companion.error("mHRVBean==" + new Gson().toJson(mHRVBean));
//                            TLog.Companion.error("打印添加过后的mlist++" + new Gson().toJson(mExcelList));
//                            setData(mHRVList.size(), mHRVBean.getHeartRate(), mHRVBean.getTestHeartRate());
//                            hartsHrr.invalidate();
                        } else {
                            if (result.length >= 2) {
                                mHRVBean.setHeartRate(result[1]&0xff);
                                if(result.length>=6) {
                                    mHRVBean.setRrOne(HexDump.byte2intHigh(result,2,2));//result[2] & 0XFF + ((result[3] & 0XFF) << 8));
                                        mHRVBean.setRrTwo(HexDump.byte2intHigh(result,4,2));
                                }
                                setSaveData();
//                                if (mHRVBean.getTestHeartRate() > 10) {
//                                    HRVBean mExcelBean = new HRVBean();
//                                    mExcelBean.setTestHeartRate(mHRVBean.getTestHeartRate());
//                                    mExcelBean.setHeartRate(mHRVBean.getHeartRate());
//                                    mExcelBean.setRrOne(mHRVBean.getRrOne());
//                                    mExcelBean.setRrTwo(mHRVBean.getRrTwo());
//                                    mExcelList.add(mExcelBean);
//                                    mHRVList.add(0, mHRVBean);
//                                    TLog.Companion.error("mHRVBean==" + new Gson().toJson(mHRVBean));
//                                    setData(mHRVList.size(), mHRVBean.getHeartRate(), mHRVBean.getTestHeartRate());
//                                    hartsHrr.invalidate();
//                                }
                            }
                        }
                        //  TLog.Companion.error("address++" + address + "\n拿到的数据==" + ByteUtil.getHexString(result));
                        return true;
                    }
                });
    }
    void setSaveData()
    {
        HRVBean mExcelBean = new HRVBean();
        mExcelBean.setTestHeartRate(mHRVBean.getTestHeartRate());
        mExcelBean.setHeartRate(mHRVBean.getHeartRate());
        mExcelBean.setRrOne(mHRVBean.getRrOne());
        mExcelBean.setRrTwo(mHRVBean.getRrTwo());
        mExcelList.add(mExcelBean);
        Hawk.put("Excel",mExcelList);
        mHRVList.add(0, mHRVBean);
        TLog.Companion.error("mHRVBean==" + new Gson().toJson(mHRVBean));
        setData(mHRVList.size(), mHRVBean.getHeartRate(), mHRVBean.getTestHeartRate());
        hartsHrr.invalidate();
    }
    void ownListener(String address) {
        new Listener(address)
                .enqueue((address1, result, uuid) -> {
              //       TLog.Companion.error("address++" + address + "\n第二个数据==" + ByteUtil.getHexString(result));
                    if (result.length >= 8) {
                        mHRVBean.setTestHeartRate((result[10] & 0XFF));
                    }
                    return true;
                });
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mHRVBean = new HRVBean();
        mHRVList = new LinkedList<>();
        mExcelList = new ArrayList<>();
        mScannerLiveData = new ScannerLiveData();
     //   Hawk.put("address","");
      //  Permissions();
        initView();
        setAdapter();
//        initTest();
        initTime();
    }

    private void initTime() {
        byte[] test;
        test = HexDump.int2Byte2(10000000);
        TLog.Companion.error("bushu=" + ByteUtil.getHexString(test));
        String fuck = "你是傻逼吧";
        String unicodeStr = HexDump.getUnicode(fuck);
        TLog.Companion.error("unicodeStr=" + unicodeStr);
        byte[] decBytes = HexDump.stringToByte(unicodeStr.replace("\\u", ""));
        TLog.Companion.error("转义的数组字节++" + ByteUtil.getHexString(decBytes));
        byte[] sendData = {(byte) decBytes.length};
        sendData = HexDump.byteMerger(sendData, decBytes);//心率
        TLog.Companion.error("组合长度++" + ByteUtil.getHexString(sendData));
        BigDecimal temper = new BigDecimal(-435);
        TLog.Companion.error("temper=" + temper);
        if (temper.compareTo(BigDecimal.ZERO) < 0)
            temper = temper.subtract(new BigDecimal(Config.TEMPERATURE_MAX)).abs();
        TLog.Companion.error("==" + temper);
        byte[] bytes = HexDump.toByteArrayTwo(Integer.parseInt(temper.toString()));
        TLog.Companion.error("==" + ByteUtil.getHexString(bytes));
        byte[] head = {0b0100_0000, 0x40};
        TLog.Companion.error("16进制结果++" + ByteUtil.getHexString(head));
        TLog.Companion.error("16进制结果++" + HexDump.toHexString(head));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mAddress.isEmpty())
            BLEManager.getInstance().disconnectDevice(mAddress);
        if (!address2.isEmpty())
            BLEManager.getInstance().disconnectDevice(address2);
        ExcelUtil.exportExcel(this, mExcelList);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mScannerLiveData.observe(this, scannerLiveData -> {
//            mScanAdapter.getData().clear();
//            mScanAdapter.addData(scannerLiveData.getScanResultList());
            mScanAdapter.notifyDataSetChanged();
        });
    }
    private AlertDialog dialog;
    /**
     * 初始化推送服务监听
     */
    private void initPermission2() {
        //通知监听权限
        //TODO 这里如果重复判断权限==false 可能需要延迟0.5~1秒再判断, 因为系统数据库插入开关值是一个子线程操作, 回到该界面马上调用提供者 有可能获取到的还是之前的开关状态
        boolean hasNotificationPermission = PermissionUtils.hasNotificationListenPermission(this);
        boolean isAccessibilityServiceRunning = PermissionUtils.isServiceRunning(this, SNAccessibilityService.class);
        //TODO 如果没有通知权限,同时辅助服务没有运行  才提示需要授权,  否则 如果辅助服务在运行,通知服务没运行, 那就先用辅助服务顶替.
        //TODO 注意 这里判断的是两种通知监听服务,勿混淆,  逻辑是 通知服务无效则使用辅助服务,通知服务和辅助服务都有效,则优先使用通知服务作为消息推送主要数据来源
        if (!hasNotificationPermission && !isAccessibilityServiceRunning) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            String permissionNames = getString(R.string.content_permission_notification);
            SpannableStringBuilder message = new SpannableStringBuilder(getString(R.string.content_authorized_to_use) + "\n" + permissionNames);
            message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.content_authorized)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.content_cancel), null)
                    .setPositiveButton(getString(R.string.content_approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.startToNotificationListenSetting(MainActivity2.this);
                        }
                    }).show();
            return;
        }
        //请求重新绑定 通知服务,防止未开启
        PermissionUtils.requestRebindNotificationListenerService(this);

        if (!PermissionUtils.hasNotificationEnablePermission(this)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            String permissionNames = getString(R.string.content_permission_notification_enable);
            SpannableStringBuilder message = new SpannableStringBuilder(getString(R.string.content_authorized_to_use) + "\n" + permissionNames);
            message.setSpan(new ForegroundColorSpan(Color.RED), message.length() - permissionNames.length(), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.content_authorized)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.content_cancel), null)
                    .setPositiveButton(getString(R.string.content_approve), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.startToNotificationEnableSetting(MainActivity2.this, null);
                        }
                    }).show();
            return;
        }

    }

    public void getGps()
    {
        LocationListener locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        GpsStatus.Listener listener=new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {

            }
        };

    }
    public void test1() {
        List<Bean> beanList = new ArrayList<>();
        beanList.add(new Bean(1, 2));
        beanList.add(new Bean(1, 2));
        beanList.add(new Bean(2, 2));
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
    }
    public static List<String> removeDuplicate(List<String> list)
    {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    @Override
    public void onResult(String productNumber, String versionName, int version, String nowMaC, String mac) {

    }
}