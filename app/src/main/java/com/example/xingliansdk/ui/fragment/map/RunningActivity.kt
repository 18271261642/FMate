package com.example.xingliansdk.ui.fragment.map

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.example.xingliansdk.ui.fragment.service.StepService
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.MapMotionBean
import com.example.xingliansdk.bean.SleepTypeBean
import com.example.xingliansdk.bean.db.AmapSportBean
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.ui.fragment.map.view.IRunningContract
import com.example.xingliansdk.ui.fragment.map.view.RunningPresenterImpl
import com.example.xingliansdk.ui.fragment.service.OnSensorStepListener
import com.example.xingliansdk.ui.fragment.service.SensorImpl
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.*
import com.example.xingliansdk.viewmodel.MainViewModel
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.Config.ControlClass
import com.shon.connector.bean.DataBean
import com.shon.connector.utils.TLog
import com.sn.map.impl.GpsLocationImpl
import com.sn.map.view.SNGaoDeMap
import com.sn.map.view.SNMapHelper
import kotlinx.android.synthetic.main.amap_include_start_pause_layout.*
import kotlinx.android.synthetic.main.amap_running_status_view.*
import kotlinx.android.synthetic.main.include_map.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

//运动系数(健走：k=0.8214
//跑步：k=1.036
//自行车：k=0.6142
//轮滑、溜冰：k=0.518
//室外滑雪：k=0.888
class RunningActivity : BaseActivity<MainViewModel>(), View.OnClickListener,
    IRunningContract.IView,OnSensorStepListener/*,OnSlideUnlockCallback*/ {

    private var mapHelper: SNMapHelper? = null
    var mPresenter: RunningPresenterImpl? = null
    var mRecordTime = 0L

    //计算卡里路常量
    var kcalcanstanc = 65.4 //计算卡路里常量

    var stepService : StepService ?= null

    private var stopAlert : AlertDialog.Builder ?= null

    private val instance by lazy { this }

    //是否是暂停状态
    private var isStopStatus = false
    var heartList: ArrayList<Int> = arrayListOf()


    var calories: String? = null
    var distances: String? = null
    var pace: String? = null
    var averageSpeed: String? = null

    //没有打开GPS时的提示框
    private var noGpsDialog : NotGpsDialogView ?= null


    override fun layoutId() = R.layout.include_map
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        requestPermission()
        SNEventBus.register(this)

        registerReceiver(broadcastReceiver,IntentFilter(Config.database.SENSOR_STEP_ACTION))

        setupService()
        //已保存的用户信息，用于判断公英制
        val userInfo = Hawk.get(Config.database.USER_INFO, LoginBean())

        runDistanceStatusTv.text = if (userInfo == null || userInfo.userConfig.distanceUnit == 1) "距离/英里" else "距离/公里"

        mPresenter = RunningPresenterImpl(this)
        mPresenter?.attachView(this)
        constMap.visibility = View.GONE
        amapRunnLayout.visibility = View.GONE
        //GPS显示
        llRunningGPS.visibility = View.VISIBLE
        statusAmapOperateLayout.visibility = View.GONE
        CountTimerUtil.start(tvNumberAnim, object : CountTimerUtil.AnimationState {
            override fun start() {
            }

            override fun end() {
                //修改过
                constMap.visibility = View.VISIBLE
                //封面计时器开始计时
                amapStatusTime.base = SystemClock.elapsedRealtime()
                amapStatusTime.start()
                initMap(savedInstanceState)

                //GPS未打开，使用传感器计步
                if(GPSUtil.isGpsEnable(instance)){
                    mPresenter?.requestWeatherData()
                    mPresenter?.requestMapFirstLocation()
                    mPresenter?.initDefaultValue()

                    noGpsMapLayout.visibility = View.GONE

                }else{

                    noGpsMapLayout.visibility = View.VISIBLE

                   // showNoGpsView()
                    stepService?.startToSensorSport()
                    val sensorImpl = SensorImpl()
                }

                chTimer.base = SystemClock.elapsedRealtime()//计时器清零
                chTimer.start()
            }

            override fun repeat() {
            }
        })
        chTimer.base = SystemClock.elapsedRealtime()//计时器清零
        chTimer.start()
        statusContinuePressView.setOnClickListener(this)
        tvTest.setOnClickListener(this)
        //statusView.setSlideUnlockCallback(this)
        tvStatus.setOnClickListener(this)
        initStatusView()


        //获取传感器权限
        //XXPermissions.with(this).permission(Manifest.permission.BODY_SENSORS).request { _, _ -> }

    }



    private fun showNoGpsView(){
        if(noGpsDialog == null)
            noGpsDialog = NotGpsDialogView(this,R.style.edit_AlertDialog_style)
        noGpsDialog!!.show()
        noGpsDialog!!.setCancelable(false)
    }

    private fun initStatusView() {
        //暂停按钮，点击后显示结束和继续
        amapDestoryPressView.setOnClickListener {
            amapDestoryPressView.visibility = View.GONE
            amapStatusDoubleLayout.visibility = View.VISIBLE
            operateStop()
        }

        //长按结束按钮
        amapStopPressView.setCircleColor(Color.parseColor("#F43232"))
        amapStopPressView.setCircleTextColor(R.color.white)
        amapStopPressView.setShowProgress(true)
        amapStopPressView.setStartText("停止")

        //长按暂停
        amapStopPressView.setOnSportEndViewOnclick(object :
            PressView.OnSportEndViewOnclick {
            override fun onStartButton() {
            }

            override fun onProgressCompetly() {
                clickSaveData()
            }

        })

        statusStopPressView.setOnSportEndViewOnclick(object : PressView.OnSportEndViewOnclick {
            override fun onStartButton() {

            }

            override fun onProgressCompetly() {
                clickSaveData()
            }

        })
        //继续按钮
        amapContinuePressView.setOnClickListener {
            amapDestoryPressView.visibility = View.VISIBLE
            amapStatusDoubleLayout.visibility = View.GONE
            operateContinue()
            mPresenter?.requestStartSport()
        }

        //切换地图模式
        aMapStatusTypeImgView.setOnClickListener {
            constMap.visibility = View.VISIBLE
            amapRunnLayout.visibility = View.GONE
            llRunningGPS.visibility = View.VISIBLE

            //判断是否是暂停状态
            if (isStopStatus) {
                statusAmapOperateLayout.visibility = View.VISIBLE
//                statusRunningPressView.visibility = View.GONE

            } else {
                statusAmapOperateLayout.visibility = View.GONE
//                statusRunningPressView.visibility = View.VISIBLE
            }

        }
    }

    //长按暂停
    private fun clickSaveData() {
        //  if(!HelpUtil.isApkInDebug(XingLianApplication.mXingLianApplication))
        if (distances.isNullOrEmpty() || distances.toString().toDouble() < 0.2) {
//            SNEventBus.sendEvent(Config.eventBus.MAP_MOVEMENT_DISSATISFY)
//            ShowToast.showToastLong("本次运动距离过短,将不会记录数据.")

            stopAlert = AlertDialog.Builder(this)
                .setTitle("提醒")
                .setMessage("本次运动距离过短,将不会记录数据,是否退出?")
                .setPositiveButton("是") { p0, p1 ->
                    p0?.dismiss()
                    //startActivity(Intent(instance, DFUActivity::class.java))
//                    if(stepService != null && !GPSUtil.isGpsEnable(instance)){
//                        stepService?.setStopParams(heartList,chTimer.text.toString())
//                        stepService?.stopToSensorSport()
//                    }
                    SNEventBus.sendEvent(Config.eventBus.MAP_MOVEMENT_DISSATISFY)
                   // ShowToast.showToastLong("本次运动距离过短,将不会记录数据.")

                }.setNegativeButton("否"
                ) { p0, p1 -> p0?.dismiss()
                    statusAmapOperateLayout.visibility = View.GONE
                    tvStatus.visibility = View.VISIBLE
//                    amapDestoryPressView.visibility = View.VISIBLE
//                    amapStatusDoubleLayout.visibility = View.GONE
                    operateContinue()
                    mPresenter?.requestStartSport()
                }

            stopAlert?.create()?.show()

            return
        }
        //ShowToast.showToastLong("最终保留的步数++$stepCount")
        mPresenter!!.saveHeartAndStep(heartList, stepCount)
        mPresenter!!.requestRetrySaveSportData()

        if(stepService != null && !GPSUtil.isGpsEnable(instance)){
            stepService?.setStopParams(heartList,chTimer.text.toString())
            stepService?.stopToSensorSport()
        }

        if (mHomeCardBean.list != null && mHomeCardBean.list.size > 0) {

            mHomeCardBean.list.forEachIndexed { index, addCardDTO ->
                if (addCardDTO.type == 0) {
                    mHomeCardBean.list[index].endTime =
                        System.currentTimeMillis() / 1000

                    mHomeCardBean.list[index].describe = DateUtil.getDate(
                        DateUtil.MM_AND_DD,
                        System.currentTimeMillis()
                    )
                    mHomeCardBean.list[index].data = amapStatusDistanceTv.text.toString()
                    Hawk.put(Config.database.HOME_CARD_BEAN, mHomeCardBean)
                    SNEventBus.sendEvent(Config.eventBus.MAP_MOVEMENT_SATISFY)
                }
            }

        }
        BleWrite.writeHeartRateSwitchCall(
            ControlClass.APP_REAL_TIME_HEART_RATE_SWITCH_KEY,
            1
        )
        finish()
//        }
    }


    /**
     * 方法必须重写
     */
    override fun onResume() {
        if (mPresenter != null) {
            mPresenter!!.setUIEnable(true)
            mPresenter!!.onResume()
            mPresenter!!.onVisible()
        }
        super.onResume()
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        if (mPresenter != null) {
            mPresenter!!.onPause()
        }
//        TLog.error("息屏操作以后==onPause")
    }

    override fun onStop() {
        if (mPresenter != null) {
            //mPresenter.setUIEnable(false);
            mPresenter!!.onStop()
            mPresenter!!.onInvisible()
        }
        super.onStop()
    }

    /**
     * 方法必须重写
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        TLog.error("息屏操作以后==onSaveInstanceState")
    }

    /**
     * 方法必须重写
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mPresenter != null) {
            mPresenter!!.onDestroy()
            mPresenter!!.onDetach()
        }
        if (mapHelper != null) {
            mapHelper!!.stopSport()
        }
        SNEventBus.unregister(this)
        this.unbindService(conn)
        val intent = Intent(this, StepService::class.java)
        this.stopService(intent)
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.statusRunningPressView -> { //暂停按钮
//                statusRunningPressView.visibility = View.GONE
//                statusAmapOperateLayout.visibility = View.VISIBLE
//                operateStop()
//            }
            R.id.statusContinuePressView -> { //继续按钮
                //  statusRunningPressView.visibility = View.VISIBLE
                statusAmapOperateLayout.visibility = View.GONE
                tvStatus.visibility = View.VISIBLE
                mPresenter?.requestStartSport()
                operateContinue()
            }
            R.id.tvStatus -> {
                tvStatus.visibility = View.GONE
                statusAmapOperateLayout.visibility = View.VISIBLE
                operateStop()
            }
            R.id.tvTest -> {
                mPresenter?.requestMapFirstLocation()
            }
        }
    }


    //暂停操作
    private fun operateStop() {
        isStopStatus = true
        chTimer.stop()
        amapStatusTime.stop()
        mRecordTime = SystemClock.elapsedRealtime()
        mPresenter?.requestStopSport()

        if(stepService != null){
            stepService?.pauseToSensorSport(true)
        }
    }


    //继续操作
    private fun operateContinue() {
        isStopStatus = false
        if (mRecordTime != 0L) {
            chTimer.base = chTimer.base + (SystemClock.elapsedRealtime() - mRecordTime)
            amapStatusTime.base =
                amapStatusTime.base + (SystemClock.elapsedRealtime() - mRecordTime)
        } else {
            chTimer.base = SystemClock.elapsedRealtime()
            amapStatusTime.base = SystemClock.elapsedRealtime()
        }
        chTimer.start()
        amapStatusTime.start()

        if(stepService != null){
            stepService?.pauseToSensorSport(false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.MAP_MOVEMENT_DISSATISFY -> {
                BleWrite.writeHeartRateSwitchCall(
                    ControlClass.APP_REAL_TIME_HEART_RATE_SWITCH_KEY,
                    1
                )
                finish()
            }
            com.shon.connector.Config.ActiveUpload.DEVICE_REAL_TIME_OTHER.toInt() -> {
                var data: DataBean = event.data as DataBean
                tvPace.text = if(data.heartRate == 0) "--" else data.heartRate.toString()
                heartList.add(data.heartRate)
            }
            Config.eventBus.MAP_MOVEMENT_STEP -> {
                var data: Int = event.data as Int
                TLog.error("返回数据++$data")
                stepCount = data
                //   tvGPS.text=data.toString()
            }
        }
    }

    //无效返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    override fun onUpdateSettingConfig(isKeepScreenEnable: Boolean, isWeatherEnable: Boolean) {

    }

    override fun onUpdateWeatherData(
        weatherType: Int,
        weatherTemperatureRange: String?,
        weatherQuality: String?
    ) {
    }

    private fun initMap(savedInstanceState: Bundle?) {
        TLog.error("savedInstanceState$savedInstanceState")
        val iLocation = GpsLocationImpl(this, 1000, 10)
//        TLog.error("iLocation+="+iLocation.lastLocation.latitude)
//        TLog.error("iLocation+="+iLocation.lastLocation.longitude)
//        TLog.error("iLocation+="+iLocation.lastLocation.altitude)
        //  val iLocation = GaoDeLocationImpl(this, 1, 1)
        val mMapView: View
        mMapView = MapView(this)
        mapHelper = SNGaoDeMap(this, savedInstanceState, iLocation, mMapView)
        mMapContent.addView(
            mMapView,
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        mPresenter?.initMapListener(mapHelper)
        mPresenter?.requestStartSport()
    }

    override fun onUpdateMapFirstLocation(mLatitude: Double, mLongitude: Double) {
    }

    override fun onUpdateGpsSignal(signal: Int) {
        if (isFinished()) {
            return
        }
        when (signal) {
            RunningPresenterImpl.SIGNAL_WEAK -> {
            }
            RunningPresenterImpl.SIGNAL_MIDDLE -> {
            }
            RunningPresenterImpl.SIGNAL_STRONG -> {
            }
            RunningPresenterImpl.SIGNAL_STRONG_MAX -> {
            }
            RunningPresenterImpl.SIGNAL_GPS_OFF -> {
            }
        }
    }




    override fun onUpdateSportData(
        distances: String?,
        calories: String?,
        hourSpeed: String?,
        pace: String?,
        latLngs: MutableList<LatLng>?
    ) {
        TLog.error("distances++$distances")
        TLog.error("calories++$calories")
        TLog.error("hourSpeed++$hourSpeed")
        TLog.error("pace++$pace")
       // TLog.error("-----经纬度集合=" + Gson().toJson(latLngs))
        try {
            tvCalories.text = calories
            tvDistance.text = distances
//            tvPace.text=pace
            this.calories = calories
            this.distances = distances
            this.pace = pace
            averageSpeed = hourSpeed

            amapStatusCaloriesTv.text = calories
            //距离
            amapStatusDistanceTv.text = distances
            //速度
            amapStatusSpeedTv.text = pace;
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpdateSportData(spendTime: String?) {
//       TLog.error("时间++$spendTime")
        //  timer.text = spendTime
    }

    override fun onSaveSportDataStatusChange(code: Int) {
        TLog.error("返回值++$code")
        when (code) {
            RunningPresenterImpl.CODE_COUNT_LITTLE -> {
                // finish()
            }
            RunningPresenterImpl.CODE_SUCCESS -> {
                setResult(Activity.RESULT_OK)
            }
        }
    }

    override fun onSportStartAnimationEnable(enable: Boolean) {
    }
//到底想改几遍?
//    override fun onSlideUnlockComplete(view: SlideUnlockView) {
//        TLog.error("onSlideUnlockComplete==")
//        view.visibility=View.GONE
//      //  statusRunningPressView.visibility = View.GONE
//        statusAmapOperateLayout.visibility = View.VISIBLE
//        operateStop()
//        view.initPath()
//    }
//
//    override fun onSlideUnlockProgress(view: SlideUnlockView, progress: Float) {
//    }

    ////////////////////////////计步相关///////////////////////////////////////
    var stepCount = 0
    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    1
                )
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    )
                ) {
                    finish()
                    Toast.makeText(this, "请允许获取健身运动信息，不然不能为你计步哦~", Toast.LENGTH_SHORT).show()
                }
            } else {
                startStepService()
            }
        } else {
            startStepService()
        }
    }

    /**
     * 开启计步服务
     */

    private fun setupService() {
        TLog.error("重新启动 计步服务")
        val intent = Intent(this, StepService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startForegroundService(intent)
        else startService(intent)
    }

    private fun startStepService() {
        /**
         * 这里判断当前设备是否支持计步
         */
        if (HelpUtil.isSupportStepCountSensor(this)) {
            setupService()
        } else {
            ShowToast.showToastLong("该设备不支持或旋转已关闭请开启旋转")
        }
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    private val conn = object : ServiceConnection {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                val stepBinder = service as StepService.StepBinder
                stepService = stepBinder.stepService
            }catch (e : Exception){
                e.printStackTrace()
            }

        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        override fun onServiceDisconnected(name: ComponentName) {
            stepService = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    startStepService()
                } else {
                    Toast.makeText(this, "请允许获取健身运动信息，不然不能为你计步哦~", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSensorUpdateSportData(
        distances: String?,
        calories: String?,
        hourSpeed: String?,
        pace: String?,
        latLngs: List<LatLng?>?
    ) {
        TLog.error("传感器", "-----dis=$distances")
    }



    private  val broadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
           val action = p1?.action
            if(action.equals(Config.database.SENSOR_STEP_ACTION)){
                if(GPSUtil.isGpsEnable(instance))
                    return
                val dis = p1?.getStringExtra("sensor_dis")
                val kcal = p1?.getStringExtra("sensor_cal")
                calories = kcal
                distances = dis
                tvCalories.text = kcal
                tvDistance.text = dis

            }
        }

    }

}