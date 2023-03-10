package com.app.fmate.ui.fragment.map

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.GpsStatus
import android.os.*
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.app.fmate.ui.fragment.service.StepService
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.MapMotionBean
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.service.work.LocationServiceHelper
import com.app.fmate.ui.fragment.map.view.IRunningContract
import com.app.fmate.ui.fragment.map.view.RunningPresenterImpl
import com.app.fmate.ui.fragment.service.OnSensorStepListener
import com.app.fmate.ui.fragment.service.SensorImpl
import com.app.fmate.utils.*
import com.app.fmate.view.*
import com.app.fmate.viewmodel.MainViewModel
import com.gyf.barlibrary.ImmersionBar
import com.hjq.permissions.XXPermissions
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.Config.ControlClass
import com.shon.connector.bean.DataBean
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.sn.map.impl.GpsLocationImpl
import com.sn.map.utils.AmapLocationService
import com.sn.map.view.SNGaoDeMap
import com.sn.map.view.SNMapHelper
import kotlinx.android.synthetic.main.amap_include_start_pause_layout.*
import kotlinx.android.synthetic.main.amap_running_status_view.*
import kotlinx.android.synthetic.main.include_map.*
import kotlinx.android.synthetic.main.include_map.mMapContent
import kotlinx.android.synthetic.main.include_map.tvDistance
import kotlinx.android.synthetic.main.include_map.tvPace
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList



//????????????(?????????k=0.8214
//?????????k=1.036
//????????????k=0.6142
//??????????????????k=0.518
//???????????????k=0.888
class RunningActivity : BaseActivity<MainViewModel>(), View.OnClickListener,
    IRunningContract.IView,OnSensorStepListener/*,OnSlideUnlockCallback*/ {

    private var mapHelper: SNMapHelper? = null
    var mPresenter: RunningPresenterImpl? = null
    var mRecordTime = 0L

    //?????????????????????
    var kcalcanstanc = 65.4 //?????????????????????

    var stepService : StepService ?= null

    private var stopAlert : AlertDialog.Builder ?= null

    private val instance by lazy { this }

    //?????????????????????
    private var isStopStatus = false
    var heartList: ArrayList<Int> = arrayListOf()


    var calories: String? = null
    var distances: String? = null
    var pace: String? = null
    var averageSpeed: String? = null

    //????????????GPS???????????????
    private var noGpsDialog : NotGpsDialogView ?= null

    private var mapMotionBean : MapMotionBean ?= null


    //???????????????GPS
    private var isOpenGps : Boolean ? = null
    //??????????????????????????????????????????GPS??????
    private var sourceDistance= 0.0
    private var sourceKcal = 0.0

    private var locationHelper : LocationServiceHelper ? = null

    private var amapLocalService : AmapLocationService ?= null


    var logPath : String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GPSUtil.registerGpsStatus(this,gpsListener)

        logPath = Environment.getExternalStorageDirectory().path+"/Download/";
        //LogcatHelper.getInstance(this,logPath).start()


    }

    override fun layoutId() = R.layout.include_map
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        requestPermission()
        SNEventBus.register(this)

        locationHelper = LocationServiceHelper(this)

        val intentFilter = IntentFilter();
        intentFilter.addAction(MapContances.NOTIFY_MAP_HISTORY_UPDATE_ACTION)
        intentFilter.addAction(Config.database.SENSOR_STEP_ACTION)
        registerReceiver(broadcastReceiver,intentFilter)

        mapMotionBean = intent.getSerializableExtra("MapMotionBean") as MapMotionBean?

        setupService()
        //????????????????????????????????????????????????
        val userInfo = Hawk.get(Config.database.USER_INFO, LoginBean())

        runDistanceStatusTv.text = if (userInfo == null || userInfo.userConfig.distanceUnit == 1) "??????/??????" else "??????/??????"

        mPresenter = RunningPresenterImpl(this)
        mPresenter?.attachView(this)
//
//        amapLocalService = AmapLocationService(this)
//        amapLocalService!!.setOnLocationListener(amapLocationListener)
//        amapLocalService!!.startLocation()


        constMap.visibility = View.GONE
        amapRunnLayout.visibility = View.GONE
        //GPS??????
        llRunningGPS.visibility = View.VISIBLE
        statusAmapOperateLayout.visibility = View.GONE



        CountTimerUtil.start(tvNumberAnim, object : CountTimerUtil.AnimationState {
            override fun start() {
            }

            override fun end() {
                //?????????
                constMap.visibility = View.VISIBLE
                //???????????????????????????
                amapStatusTime.base = SystemClock.elapsedRealtime()
                amapStatusTime.start()

                initMap(savedInstanceState)
                locationHelper!!.startLocation()
              //  mPresenter?.requestWeatherData()
                mPresenter?.requestMapFirstLocation()
                mPresenter?.initDefaultValue()

                //GPS?????????????????????????????????
                if(GPSUtil.isGpsEnable(instance)){

                    isOpenGps = true
//                    mPresenter?.requestWeatherData()
//                    mPresenter?.requestMapFirstLocation()
//                    mPresenter?.initDefaultValue()
                    stepService?.setNoGpsStartAndEnd(true)
                    noGpsMapLayout.visibility = View.GONE

                }else{
                    stepService?.setNoGpsStartAndEnd(false)
                    isOpenGps = false
                    noGpsMapLayout.visibility = View.VISIBLE

                   // showNoGpsView()
//                    stepService?.startToSensorSport()
                    val sensorImpl = SensorImpl()
                }
                stepService?.startToSensorSport()
                chTimer.base = SystemClock.elapsedRealtime()//???????????????
                chTimer.start()
            }

            override fun repeat() {
            }
        })
        chTimer.base = SystemClock.elapsedRealtime()//???????????????
        chTimer.start()
        statusContinuePressView.setOnClickListener(this)
        tvTest.setOnClickListener(this)
        //statusView.setSlideUnlockCallback(this)
        tvStatus.setOnClickListener(this)
        initStatusView()


        //?????????????????????
        XXPermissions.with(this).permission(Manifest.permission.ACCESS_FINE_LOCATION).request { _, _ -> }

    }



    private fun showNoGpsView(){
        if(noGpsDialog == null)
            noGpsDialog = NotGpsDialogView(this,R.style.edit_AlertDialog_style)
        noGpsDialog!!.show()
        noGpsDialog!!.setCancelable(false)
    }

    private fun initStatusView() {
        //?????????????????????????????????????????????
        amapDestoryPressView.setOnClickListener {
            amapDestoryPressView.visibility = View.GONE
            amapStatusDoubleLayout.visibility = View.VISIBLE
            operateStop()
        }

        //??????????????????
        amapStopPressView.setCircleColor(Color.parseColor("#F43232"))
        amapStopPressView.setCircleTextColor(R.color.white)
        amapStopPressView.setShowProgress(true)
        amapStopPressView.setStartText("??????")

        //????????????
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
        //????????????
        amapContinuePressView.setOnClickListener {
            amapDestoryPressView.visibility = View.VISIBLE
            amapStatusDoubleLayout.visibility = View.GONE
            operateContinue()
            mPresenter?.requestStartSport()
        }

        //??????????????????
        aMapStatusTypeImgView.setOnClickListener {
            constMap.visibility = View.VISIBLE
            amapRunnLayout.visibility = View.GONE
            llRunningGPS.visibility = View.VISIBLE

            //???????????????????????????
            if (isStopStatus) {
                statusAmapOperateLayout.visibility = View.VISIBLE
//                statusRunningPressView.visibility = View.GONE

            } else {
                statusAmapOperateLayout.visibility = View.GONE
//                statusRunningPressView.visibility = View.VISIBLE
            }

        }
    }

    //????????????
    private fun clickSaveData() {
        //  if(!HelpUtil.isApkInDebug(XingLianApplication.mXingLianApplication))



        if (distances.isNullOrEmpty() || distances.toString().toDouble() < 0.002) {
//            SNEventBus.sendEvent(Config.eventBus.MAP_MOVEMENT_DISSATISFY)
//            ShowToast.showToastLong("????????????????????????,?????????????????????.")

            stopAlert = AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("????????????????????????,?????????????????????,?????????????")
                .setPositiveButton("???") { p0, p1 ->
                    p0?.dismiss()
                    //startActivity(Intent(instance, DFUActivity::class.java))
//                    if(stepService != null && !GPSUtil.isGpsEnable(instance)){
//                        stepService?.setStopParams(heartList,chTimer.text.toString())
//                        stepService?.stopToSensorSport()
//                    }
                    SNEventBus.sendEvent(Config.eventBus.MAP_MOVEMENT_DISSATISFY)
                   // ShowToast.showToastLong("????????????????????????,?????????????????????.")

                }.setNegativeButton("???"
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
        //ShowToast.showToastLong("?????????????????????++$stepCount")
        mPresenter!!.saveHeartAndStep(heartList, stepCount,distances,tvCalories.text.toString())
        mPresenter!!.requestRetrySaveSportData()

        if(stepService != null ){
            mapMotionBean?.let {
                stepService?.setStopParams(heartList,chTimer.text.toString(),
                    it.type)
            }
            stepService?.stopToSensorSport()
        }

       // showWaitDialog("???????????????...")

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
        //finish()
//        }
    }


    /**
     * ??????????????????
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
     * ??????????????????
     */
    override fun onPause() {
        super.onPause()
        if (mPresenter != null) {
            mPresenter!!.onPause()
        }
//        TLog.error("??????????????????==onPause")
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
     * ??????????????????
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        TLog.error("??????????????????==onSaveInstanceState")
    }

    /**
     * ??????????????????
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

        GPSUtil.unregisterGpsListener(gpsListener)

       // LogcatHelper.getInstance(instance,).stop()
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.statusRunningPressView -> { //????????????
//                statusRunningPressView.visibility = View.GONE
//                statusAmapOperateLayout.visibility = View.VISIBLE
//                operateStop()
//            }
            R.id.statusContinuePressView -> { //????????????
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


    //????????????
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


    //????????????
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
                TLog.error("????????????++$data")
                stepCount = data
                //   tvGPS.text=data.toString()
            }
        }
    }

    //???????????????
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
       // TLog.error("-----???????????????=" + Gson().toJson(latLngs))
        try {


            if (calories != null) {
                tvCalories.text = Utils.add(calories.toDouble(),sourceKcal).toString()
            }
            if (distances != null) {
                this.distances =   Utils.add(distances.toDouble(),sourceDistance).toString()
                tvDistance.text = Utils.add(distances.toDouble(),sourceDistance).toString()
            }


//            tvPace.text=pace
            this.calories = calories

            this.pace = pace
            averageSpeed = hourSpeed

            amapStatusCaloriesTv.text = calories
            //??????
            amapStatusDistanceTv.text = distances
            //??????
            amapStatusSpeedTv.text = pace;
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpdateSportData(spendTime: String?) {
//       TLog.error("??????++$spendTime")
        //  timer.text = spendTime
    }

    override fun onSaveSportDataStatusChange(code: Int) {
        TLog.error("?????????++$code")
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
//???????????????????
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

    ////////////////////////////????????????///////////////////////////////////////
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
                    Toast.makeText(this, "???????????????????????????????????????????????????????????????~", Toast.LENGTH_SHORT).show()
                }
            } else {
                startStepService()
            }
        } else {
            startStepService()
        }
    }

    /**
     * ??????????????????
     */

    private fun setupService() {
        TLog.error("???????????? ????????????")
        val intent = Intent(this, StepService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startForegroundService(intent)
        else startService(intent)
    }

    private fun startStepService() {
        /**
         * ??????????????????????????????????????????
         */
        if (HelpUtil.isSupportStepCountSensor(this)) {
            setupService()
        } else {
            ShowToast.showToastLong("???????????????????????????????????????????????????")
        }
    }

    /**
     * ???????????????????????????application Service?????????????????????interface???
     * ??????????????????????????????Service ??? context.bindService()???????????????
     * ?????????????????????????????????????????????ServiceConnection????????????????????????????????????????????????
     */
    private val conn = object : ServiceConnection {
        /**
         * ???????????????Service???????????????????????????????????????Android?????????IBind?????????????????????????????????
         * @param name ?????????????????????Service????????????
         * @param service ????????????????????????IBind???????????????Service??????????????????
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
         * ??????Service???????????????????????????????????????????????????
         * ???????????????????????????Service??????????????????????????????Kill??????????????????
         * ????????????????????????Service????????????????????????????????????????????????????????? onServiceConnected()???
         * @param name ???????????????????????????
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
                    Toast.makeText(this, "???????????????????????????????????????????????????????????????~", Toast.LENGTH_SHORT).show()
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
        TLog.error("?????????", "-----dis=$distances")
    }



    private  val broadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
           val action = p1?.action
            if(action.equals(Config.database.SENSOR_STEP_ACTION)){
//                if(GPSUtil.isGpsEnable(instance))
//                    return
                if(isOpenGps == false){
                    val dis = p1?.getStringExtra("sensor_dis")
                    val kcal = p1?.getStringExtra("sensor_cal")
                    if(dis == null || kcal == null)
                        return
                    calories = kcal
                    //distances = dis

                   distances =  Utils.add(dis.toDouble(),sourceDistance).toString()

                    tvCalories.text = Utils.add(kcal.toDouble(),sourceKcal).toString()
                    tvDistance.text = Utils.add(dis.toDouble(),sourceDistance).toString()
                }

            }



            if(action.equals(MapContances.NOTIFY_MAP_HISTORY_UPDATE_ACTION)){
                hideWaitDialog()
                finish()
            }
        }

    }


    //GPS??????
    private val gpsListener = GpsStatus.Listener {
       // TLog.error("---------GPS????????????="+it+"\n"+GPSUtil.isGpsEnable(this))
        if(it == GpsStatus.GPS_EVENT_STARTED && GPSUtil.isGpsEnable(this)){    //??????
            noGpsMapLayout.visibility = View.GONE
            stepService?.setNoGpsStartAndEnd(true)
            isOpenGps = true
            var disStr = tvDistance.text.toString();
            if(disStr == null || disStr == "--"){
                sourceDistance = 0.0
            }else{
                sourceDistance = disStr.toDouble()
            }


            var kcalStr = tvCalories.text.toString()
            if(kcalStr == null || kcalStr.equals("--")){
                sourceKcal = 0.0
            }else{
                sourceKcal = kcalStr.toDouble()
            }

        }

        if(it == GpsStatus.GPS_EVENT_STOPPED && !GPSUtil.isGpsEnable(this)){  //??????
            stepService?.setNoGpsStartAndEnd(false)
           // noGpsMapLayout.visibility = View.VISIBLE
            isOpenGps = false
            var disStr = tvDistance.text.toString();
            if(disStr == null || disStr == "--"){
                sourceDistance = 0.0
            }else{
                sourceDistance = disStr.toDouble()
            }


            var kcalStr = tvCalories.text.toString()
            if(kcalStr == null || kcalStr == "--"){
                sourceKcal = 0.0
            }else{
                sourceKcal = kcalStr.toDouble()
            }
        }
    }


    private  val amapLocationListener = AmapLocationService.OnLocationListener {

    }

}