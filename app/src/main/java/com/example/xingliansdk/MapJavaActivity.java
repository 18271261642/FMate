//package com.example.xingliansdk;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.amap.api.maps.MapView;
//import com.example.xingliansdk.ui.fragment.map.view.IRunningContract;
//import com.example.xingliansdk.ui.fragment.map.view.RunningPresenterImpl;
//import com.sn.map.impl.GpsLocationImpl;
//import com.sn.map.view.SNGaoDeMap;
//import com.sn.map.view.SNMapHelper;
//
//public class MapJavaActivity extends AppCompatActivity implements View.OnClickListener, IRunningContract.IView {
//    FrameLayout mMapContent;
//    Button tvRunningReady, tvRunningCurLocation,tvStop;
//    TextView tvTime, tvDis, tvPace, tvKcal;
//    private SNMapHelper mapHelper;
//    RunningPresenterImpl mPresenter;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map_test);
//        mMapContent = findViewById(R.id.mMapContent);
//        tvRunningReady = findViewById(R.id.tvRunningReady);
//        tvRunningCurLocation = findViewById(R.id.tvRunningCurLocation);
//        tvStop=findViewById(R.id.tvStop);
//        tvTime = findViewById(R.id.tvTime);
//        tvDis = findViewById(R.id.tvDistance);
//        tvPace = findViewById(R.id.tvPace);
//        tvKcal = findViewById(R.id.tvKCAL);
//        tvRunningReady.setOnClickListener(this);
//        tvRunningCurLocation.setOnClickListener(this);
//        tvStop.setOnClickListener(this);
//        mPresenter = new RunningPresenterImpl(this);
//        mPresenter.attachView(this);
//        initMap(savedInstanceState);
//        mPresenter.requestWeatherData();
//        mPresenter.requestMapFirstLocation();
//        mPresenter.initDefaultValue();
//    }
//
//
//    /**
//     * 初始化地图
//     */
//    private void initMap(Bundle savedInstanceState) {
//        Log.e("initMap: ", "savedInstanceState" + savedInstanceState);
//        GpsLocationImpl iLocation = new GpsLocationImpl(this, 1000, 10);
//
//        View mMapView;
//        mMapView = new MapView(this);
//        mapHelper = new SNGaoDeMap(this, savedInstanceState, iLocation, (MapView) mMapView);
//        mMapContent.addView(mMapView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        mPresenter.initMapListener(mapHelper);
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tvRunningReady:
//                Toast.makeText(this,"运动一开始,请勿重复点击",Toast.LENGTH_LONG).show();
//                mPresenter.requestStartSport();
//                break;
//            case R.id.tvRunningCurLocation:
//                mPresenter.requestMapFirstLocation();
//                break;
//            case R.id.tvStop:
//                mPresenter.requestStopSport();
//                break;
//        }
//    }
//
//    @Override
//    public void onUpdateSettingConfig(boolean isKeepScreenEnable, boolean isWeatherEnable) {
//
//    }
//
//    @Override
//    public void onUpdateWeatherData(int weatherType, String weatherTemperatureRange, String weatherQuality) {
//
//    }
//
//    @Override
//    public void onUpdateMapFirstLocation(double mLatitude, double mLongitude) {
//
//    }
//
//    @Override
//    public void onUpdateGpsSignal(int signal) {
//
//    }
//
//    @Override
//    public void onUpdateSportData(String distances, String calories, String hourSpeed, String pace) {
//
//    }
//
//    @Override
//    public void onUpdateSportData(String spendTime) {
//
//    }
//
//    @Override
//    public void onSaveSportDataStatusChange(int code) {
//        Log.e(  "2222222222222","状态码++"+code );
//        switch (code) {
//            case RunningPresenterImpl.CODE_COUNT_LITTLE:
//                finish();
//                break;
//            case RunningPresenterImpl.CODE_SUCCESS:
//                setResult(Activity.RESULT_OK);
//                //finish();
//                break;
//        }
//    }
//
//    @Override
//    public void onSportStartAnimationEnable(boolean enable) {
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        //没开始跑步时 用户直接点返回键 直接退出 不提示弹窗 不然很烦人
//        if (mapHelper != null && !mapHelper.isStarted()) {
//            finish();
//        } else {
//            if (mapHelper != null) {
//                mapHelper.animateCameraToScreenBounds();
//            }
//            //     exitAlertDialog();
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //    CommonDialog.dismiss();
//        if (mPresenter != null) {
//            mPresenter.onDestroy();
//            mPresenter.onDetach();
//        }
//        if (mapHelper != null) {
//            mapHelper.stopSport();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        if (mPresenter != null) {
//            mPresenter.onPause();
//        }
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        if (mPresenter != null) {
//            //mPresenter.setUIEnable(false);
//            mPresenter.onStop();
//            mPresenter.onInvisible();
//        }
//        super.onStop();
//    }
//    @Override
//    protected void onResume() {
//        if (mPresenter != null) {
//            mPresenter.setUIEnable(true);
//            mPresenter.onResume();
//            mPresenter.onVisible();
//        }
//        super.onResume();
//    }
//}
