package com.app.fmate.ui.fragment

//import com.example.xingliansdk.bean.HomeCardBean
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fmate.Config.database.*
import com.app.fmate.Config.eventBus.HOME_HISTORICAL_BIG_DATA_WEEK
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.XingLianApplication.Companion.getSelectedCalendar
import com.app.fmate.adapter.HomeAdapter
import com.app.fmate.adapter.PopularScienceAdapter
import com.app.fmate.base.fragment.BaseFragment
import com.app.fmate.bean.PopularScienceBean
import com.app.fmate.bean.room.*
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.blesend.BleSend
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.homeView.HomeCardVoBean
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.utils.*
import com.app.fmate.view.DateUtil
import com.app.fmate.viewmodel.HomeViewModel
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.*
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.shon.connector.utils.TLog.Companion.error
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : BaseFragment<HomeViewModel>(), OnRefreshListener, View.OnClickListener,
    BleWrite.HistoryCallInterface, //?????????????????????
    BleWrite.SpecifyDailyActivitiesHistoryCallInterface,  //??????
    BleWrite.SpecifySleepHistoryCallInterface  //??????
    , BleWrite.SpecifyHeartRateHistoryCallInterface //??????
    , BleWrite.SpecifyBloodOxygenHistoryCallInterface  //??????
//    , BleWrite.SpecifyBloodPressureHistoryCallInterface //??????
    , BleWrite.SpecifyStressFatigueHistoryCallInterface //??????
    , BleWrite.SpecifyTemperatureHistoryCallInterface //??????
    , BleWrite.DeviceMotionInterface //??????????????????


{
    companion object {
        //???????????????????????????????????????????????????????????????
        var PressureOnClick = false

        /**
         * ??????????????????????????????????????? ???????????????????????????????????????????????????????????????
         */
        var progressStatus = false
    }


    private val tags = "HomeFragment"



    //    private lateinit var mList: MutableList<HomeCardBean>
    //  private lateinit var mHomeCardBean: HomeCardBean
    // private lateinit var mAddList: MutableList<HomeCardBean.AddCardDTO>
    private lateinit var mHomeCardVoBean: HomeCardVoBean
    private lateinit var mCardList: MutableList<HomeCardVoBean.ListDTO>
    lateinit var mHomeAdapter: HomeAdapter
    override fun layoutId() = R.layout.activity_home
    var currentStepValue = 0
    private lateinit var sDao: RoomMotionTimeDao
    private var motionTimeList: MutableList<RoomMotionTimeBean> = mutableListOf()

    private lateinit var mMotionListDao: MotionListDao
    private lateinit var mSleepListDao: SleepListDao
    private lateinit var mHeartListDao: HeartListDao
    private lateinit var mBloodOxygenListDao: BloodOxygenListDao
    private lateinit var mTempListDao: TempListDao

    //??????
    private lateinit var mPressureListDao: PressureListDao
    lateinit var mPopularScienceAdapter: PopularScienceAdapter
    private lateinit var mPopularList: MutableList<PopularScienceBean.ListDTO>

    var decimalFormat = DecimalFormat("#.##")

    private val handler = object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
//            if(msg.what == 0x00){
//                if(activity!=null && !activity!!.isFinishing){
//                    mSwipeRefreshLayout.finishRefresh()
//                }
//            }

        }
    }



    //    private var isRefresh = false
    private fun getRoomList() {
        sDao = AppDataBase.instance.getRoomMotionTimeDao()
        mMotionListDao = AppDataBase.instance.getMotionListDao()
//        TLog.error("??????????????????++${Gson().toJson(mMotionListDao.getAllRoomMotionList())}")
        val allRoomTimes = sDao.getAllRoomTimes()
        if (allRoomTimes.size > 0) {
            motionTimeList = allRoomTimes
        }
        mSleepListDao = AppDataBase.instance.getRoomSleepListDao()
        mHeartListDao = AppDataBase.instance.getHeartDao()
        mBloodOxygenListDao = AppDataBase.instance.getBloodOxygenDao()
        mPressureListDao = AppDataBase.instance.getPressureListDao()
        mTempListDao = AppDataBase.instance.getRoomTempListDao()
        var customizeDialDao = AppDataBase.instance.getCustomizeDialDao()
//        TLog.error("===" + customizeDialDao.getAllCustomizeDialList().size)
//        TLog.error("===" + Gson().toJson(customizeDialDao.getAllCustomizeDialList()))
    }

    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        TLog.error("initView==")
        progressStatus = false
        setHomeCard()
        intView()
        getRoomList()
        setPopularAdapter()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.userInfo()
        mViewModel.getHomeCard()
    }

    private fun setPopularAdapter() {
        var hasmap = HashMap<String, Any>()
        hasmap["category"] = "0"
        mViewModel.getPopular(hasmap)
        mPopularList = ArrayList()

        ryPopularScience.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        ryPopularScience.addItemDecoration(
            RecycleViewDivider(
                activity,
                LinearLayoutManager.HORIZONTAL,
                1,
                resources.getColor(R.color.color_view)
            )
        )
        mPopularScienceAdapter = PopularScienceAdapter(mPopularList)

        if (HelpUtil.netWorkCheck(requireActivity())) {
            tvYouKnow.visibility = View.VISIBLE
        } else
            tvYouKnow.visibility = View.GONE
        ryPopularScience.adapter = mPopularScienceAdapter
        mPopularScienceAdapter.setOnItemClickListener { adapter, view, position ->
            JumpUtil.startWeb(activity, mPopularList[position].detailUrl)
        }
    }

    private fun setHomeCard() {
        mHomeCardVoBean =
            if (Hawk.get<HomeCardVoBean>(HOME_CARD_BEAN) != null) {
                mCardList = Hawk.get<HomeCardVoBean>(HOME_CARD_BEAN).list
                Hawk.get(HOME_CARD_BEAN)
            } else {
                mCardList = arrayListOf()
                HomeCardVoBean()
            }
//        TLog.error("mCardList+=" + Gson().toJson(mCardList))
        rvAll.layoutManager =
            GridLayoutManager(activity, 2)
        mHomeAdapter = HomeAdapter(mCardList)
        rvAll.adapter = mHomeAdapter
        //?????????????????????
        mHomeAdapter.setOnItemClickListener { adapter, view, position ->
            //     ????????????", "??????", "??????", "??????", "???????????????")
            when (mHomeAdapter.data[position].type) {

                0 -> {
                    JumpUtil.startExerciseRecordActivity(activity)
                }
                1 -> {
                    TLog.error("===" + Gson().toJson(mCardList[position]))
                    JumpUtil.startHeartRateActivity(activity, mCardList[position], position)
                }
                2 -> {
                    JumpUtil.startSleepDetailsActivity(activity, mCardList[position])
                }
                3 -> {
                    if (!PressureOnClick) {
                        TLog.error("?????????+" + PressureOnClick)
                        PressureOnClick = true
                        JumpUtil.startPressureActivity(activity, mCardList[position])
                    } else {
                        PressureOnClick = false
                    }
                }
                4 -> {
                    JumpUtil.startBloodOxygenActivity(activity, mCardList[position])
                }
                5 -> {  //??????
                   // JumpUtil.startBloodPressureActivity(activity, mCardList[position])

                    JumpUtil.startNewBpActivity(activity, mCardList[position])
                }
                6 -> {
                    JumpUtil.startTempActivity(activity, mCardList[position])
                }
                7 -> {
                    JumpUtil.startWeightActivity(activity, mCardList[position])
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }

    private fun homeBleWrite() {
        TLog.error("??????==" + (DateUtil.getTodayZero() / 1000 - XingLianApplication.TIME_START))
        Handler(Looper.getMainLooper()).postDelayed({
            if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                mSwipeRefreshLayout.finishRefresh()
                handler.removeMessages(0x00)
                //isRefresh=false
            } else {
                BLEManager.getInstance().dataDispatcher.clear("")
                handler.sendEmptyMessageDelayed(0x00,20000)
                BleSend.sendDateTime()
                BleWrite.writeSportsUploadModeCall(1)
                BleWrite.writeForGetDeviceMotion(this, false)
                val startTime = (DateUtil.getTodayZero() / 1000 - XingLianApplication.TIME_START)
                val endTime = (System.currentTimeMillis() / 1000 - XingLianApplication.TIME_START)

                BleWrite.writeSpecifyDailyActivitiesHistoryCall(
                    startTime, endTime,
                    this, true
                )
                BleWrite.writeSpecifyHeartRateHistoryCall(
                    startTime, endTime,
                    this, true
                )
                BleWrite.writeSpecifyBloodOxygenHistoryCall(
                    startTime, endTime,
                    this, true
                )

                BleWrite.writeSpecifyStressFatigueHistoryCall(
                    startTime, endTime,
                    this, true
                )
                BleWrite.writeSpecifyTemperatureHistoryCall(
                    startTime, endTime,
                    this, true
                )
                BleWrite.writeBigDataHistoryCall(Config.BigData.APP_SLEEP, this, true)

            }
        }, 500)
        //  mViewModel.getHomeCard()
    }

    private fun intView() {
        ImmersionBar.setTitleBar(activity, toolbar)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        tvGoal?.text = "${mDeviceInformationBean.exerciseSteps}"+resources.getString(R.string.unit_steps)

        onClickListener()
    }

    private fun onClickListener() {
        tvEdit.setOnClickListener(this)
        circleSports.setOnClickListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

        //
        if(Config.isNeedTimeOut){
            handler.sendEmptyMessage(0x00)
            ShowToast.showToastShort(resources.getString(R.string.string_home_measure_wait))
            mSwipeRefreshLayout.finishRefresh()
            return
        }

//        TLog.error("?????????????????????")
//        if(!isRefresh) {
//            TLog.error("????????????")
        TLog.error("mHomeCardVoBean====+" + Gson().toJson(mHomeCardVoBean))
//        mSwipeRefreshLayout.finishRefresh(60000)
        //handler.sendEmptyMessageDelayed(0x00,15 * 1000)

        setPopularAdapter()
        homeBleWrite()
//        }
        //isRefresh = true
//        TLog.error("mList+${Gson().toJson(mAddList)}")

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvEdit -> {
                if (!HelpUtil.netWorkCheck(XingLianApplication.getXingLianApplication())) {
                    ShowToast.showToastLong(resources.getString(R.string.string_home_no_net_edit))
                    return
                }
                JumpUtil.startCardEditActivity(activity)
            }
            R.id.circleSports -> {   //????????????
                JumpUtil.startDeviceSportChartActivity(activity)
            }
        }
    }


    override fun createObserver() {
        mainViewModel.textValue.observe(viewLifecycleOwner, {
            TLog.error("mainViewModel.textValue")
            currentStepValue = mainViewModel.getText()
            //   circleSports.maxProgress = mDeviceInformationBean.exerciseSteps.toInt()
        })
        mainViewModel.result.observe(this)
        {
            TLog.error("mainViewModel.result+" + Gson().toJson(it)+"\n"+Gson().toJson(it.user))
            userInfo.user = it.user
            userInfo.userConfig = it.userConfig
            userInfo.permission = it.permission
            Hawk.put(USER_INFO, userInfo)
            ImgUtil.loadImage(activity, userInfo.user.headPortrait)
            mDeviceInformationBean = DeviceInformationBean(
                it.user.sex.toInt(),
                it.user.age.toInt(),
                it.user.height.toInt(),
                it.user.weight.toFloat(),
                mDeviceInformationBean.language.toInt(),
                it.userConfig.timeFormat,
                1,
                it.userConfig.distanceUnit,
                mDeviceInformationBean.wearHands.toInt(),
                it.userConfig.temperatureUnit,
                it.userConfig.movingTarget.toLong(),
                DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD, it.user.birthDate),
                it.user.nickname
            )
            Hawk.put(SLEEP_GOAL, it.userConfig.sleepTarget.toLong())
            mainViewModel.userInfo.postValue(it)
            Hawk.put(PERSONAL_INFORMATION, mDeviceInformationBean)
            tvGoal.text = "${it.userConfig.movingTarget.toLong()}"+resources.getString(R.string.unit_steps)
            TLog.error("????????????++" + Gson().toJson(it))


            TLog.error("-------??????????????????--------")

//            if(mHomeCardVoBean != null && mHomeCardVoBean.distance != null){
//                   if (mDeviceInformationBean?.unitSystem == 1.toByte()) {
//                val miDis = Utils.muiltip(mHomeCardVoBean.distance.toDouble(),0.6213)
//                tvKM?.text = decimalFormat.format(miDis)+"??????"
//            } else
//                tvKM?.text = "${mHomeCardVoBean.distance} ??????"
//            tvCalories?.text = "${mHomeCardVoBean.calorie} ??????"
//            }

        }

        mViewModel.resultPopular.observe(this)
        {
            TLog.error("mainViewModel.resultPopular")
            if (it == null || it.list.isNullOrEmpty() || it.list.size <= 0)
                return@observe
            TLog.error("???????????????===" + Gson().toJson(it))

            mPopularList.addAll(it.list)
            mPopularScienceAdapter.notifyDataSetChanged()
        }

        mViewModel.resultHomeCard.observe(this)
        {
            TLog.error("mainViewModel.resultHomeCard")
            //   TLog.error("===" + Gson().toJson(it))
            if (it == null || it.list == null || it.list.size <= 0)
                return@observe
                TLog.error("??????????????????++" + Gson().toJson(it)+"\n"+mDeviceInformationBean.toString())
            mCardList = it.list
            mHomeAdapter.data = it.list
            mHomeAdapter.notifyDataSetChanged()
            Hawk.put(HOME_CARD_BEAN, it)
            mHomeCardVoBean = it


            TLog.error("------?????????????????????--------")

            if(mHomeCardVoBean.distance != null){
                if (mDeviceInformationBean.unitSystem == 1.toByte()) {
                    val miDis = Utils.muiltip(mHomeCardVoBean.distance.toDouble(),0.6213)
                    tvKM?.text = decimalFormat.format(miDis)+resources.getString(R.string.unit_mile)
                } else
                    tvKM?.text = "${mHomeCardVoBean.distance}"+resources.getString(R.string.string_km)
                tvCalories?.text = "${mHomeCardVoBean.calorie}"+" "+resources.getString(R.string.string_unit_kcal)
            }

            //   TLog.error("??????????????????++" + Gson().toJson(it))
            if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                mSwipeRefreshLayout.finishRefresh()
                handler.removeMessages(0x00)
                if(mHomeCardVoBean.distance == null)
                    return@observe
                if (!progressStatus) {
                    progressStatus = true
                    TLog.error("circleSports  progressStatus+=" + progressStatus)
                    circleSports.maxProgress = mHomeCardVoBean.movingTarget.toInt()
                    circleSports?.progress = mHomeCardVoBean.steps.toInt()
                }

                TLog.error("------??????????????????--------")

                if (mDeviceInformationBean?.unitSystem == 1.toByte()) {
                    val miDis = Utils.muiltip(mHomeCardVoBean.distance.toDouble(),0.6213)
                    tvKM?.text = decimalFormat.format(miDis)+" "+resources.getString(R.string.unit_mile)
                } else
                    tvKM?.text = "${mHomeCardVoBean.distance}"+" "+resources.getString(R.string.string_km)
                tvCalories?.text = "${mHomeCardVoBean.calorie}"+" "+resources.getString(R.string.string_unit_kcal)
            }
        }


        mViewModel.resultRealSep.observe(this){

        }
    }

    var mTimeList: ArrayList<TimeBean> = arrayListOf()
    override fun HistoryCallResult(key: Byte, mList: ArrayList<TimeBean>) {
        TLog.error("??????mList==" + Gson().toJson(mList))
        TLog.error("key==" + key)
        if (mList?.size <= 0) {
            if (key == Config.BigData.DEVICE_SLEEP) {
                SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA_WEEK, true)
               // mSwipeRefreshLayout.finishRefresh()
                //isRefresh = false
            }
            return
        }
        mList?.reverse()
        mList?.get(mList.size - 1)?.let {
            when (key) {
                Config.BigData.DEVICE_DAILY_ACTIVITIES -> {
                    error("DEVICE_DAILY_ACTIVITIES")
//                    mTimeList = arrayListOf()
//                    mTimeList.add(it)
                    TLog.error("????????????++" + Gson().toJson(mTimeList))
                    RoomUtils.updateMovementTime(mTimeList, this)
                    BleWrite.writeSpecifyDailyActivitiesHistoryCall(
                        it.startTime, it.endTime,
                        this
                    )
                }
                Config.BigData.DEVICE_SLEEP -> {
                    error("DEVICE_SLEEP==" + it.startTime + "===" + it.endTime)
                    mTimeList = arrayListOf()
                    mTimeList.add(it)
                    //  RoomUtils.updateSleepTime(mTimeList, this)
                    val startTime =
                        (DateUtil.getTodayZero() / 1000 - XingLianApplication.TIME_START)
                    if (startTime < it.endTime) {
                        TLog.error("????????????++${it.startTime}  ===${it.endTime}")
                        BleWrite.writeSpecifySleepHistoryCall(
                            it.startTime, it.endTime,
                            this, true
                        )
                    } else {
                        TLog.error("?????????????????????????????????++" + it.startTime)
                        ThreadUtils.runOnUiThread({
                            SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA_WEEK, true)
                            mSwipeRefreshLayout.finishRefresh()
                        }, 2000)
                    }
                }
                Config.BigData.DEVICE_BLOOD_OXYGEN -> {
                    error("DEVICE_BLOOD_OXYGEN")
                    mTimeList = arrayListOf()
                    mTimeList.add(it)
                    RoomUtils.updateBloodOxygenDate(mTimeList, this)
                    //?????????????????????????????????

                }
                Config.BigData.DEVICE_HEART_RATE -> {
                    error("DEVICE_HEART_RATE==??????")
                    mTimeList = arrayListOf()
                    mTimeList.add(it)
//                    BleWrite.writeSpecifyHeartRateHistoryCall(
//                        mTimeList[0].startTime,
//                        mTimeList[0].endTime,
//                        this
//                    )
                    error("DEVICE_HEART_RATE==??????==" + Gson().toJson(mTimeList))
                    //    SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA_WEEK, true)
                    RoomUtils.updateHeartRateData(mTimeList, this)
                }
                Config.BigData.DEVICE_STRESS_FATIGUE -> {
                    error("DEVICE_STRESS_FATIGUE==??????")
                    mTimeList = arrayListOf()
                    mTimeList.add(it)
                    RoomUtils.updatePressure(mTimeList, this)
                }
                else -> {
                }
            }
        }

    }

    override fun SpecifyDailyActivitiesHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<DailyActiveBean>
    ) {
        if (mList.isNullOrEmpty())
            return
        error("??????????????????++${Gson().toJson(mList)}")
        var stepList: MutableList<Int> = mutableListOf()
        var step = 0L
        mList?.forEach {
            stepList.add(it.steps)
            step += it.steps
        }
        error("30?????????????????????++${Gson().toJson(stepList)}")
        val name: String = Gson().toJson(stepList)
        mMotionListDao.insert(
            MotionListBean(
                startTime + XingLianApplication.TIME_START,
                endTime + XingLianApplication.TIME_START,
                name,
                step,
                false,
                DateUtil.getDate(DateUtil.YYYY_MM_DD, (startTime + Config.TIME_START) * 1000L)
            )
        )
        mViewModel.setDailyActive(
            startTime + XingLianApplication.TIME_START,
            endTime + XingLianApplication.TIME_START,
            Gson().toJson(mList)
        )
        mViewModel.getHomeCard()
    }

    override fun SpecifyDailyActivitiesHistoryCallResult(mList: ArrayList<DailyActiveBean>?) {
//        error("?????????????????? ???????????????++${Gson().toJson(mList)}")

    }

    override fun SpecifySleepHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<SleepBean>?, bean: SleepBean
    ) {
        mRefreshHeader?.setReleaseText(resources.getString(R.string.string_home_sleep_refresh))
        error(" time ${startTime + XingLianApplication.TIME_START}")
        error(" endTime ${endTime + XingLianApplication.TIME_START}")
        error("time????????????${Gson().toJson(mList)}")
        SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA_WEEK, true)
       // mSwipeRefreshLayout.finishRefresh()
        //  ShowToast.showToastLong("????????????????????????")
        var timeZero = (bean.startTime + XingLianApplication.TIME_START) / 86400 * 86400
        TLog.error("????????????==" + timeZero)
        mSleepListDao.insert(
            SleepListBean(
                bean.startTime + XingLianApplication.TIME_START,
                bean.averageHeartRate,
                bean.maximumHeartRate,
                bean.minimumHeartRate,
                bean.numberOfApnea,
                bean.endTime + XingLianApplication.TIME_START,
                bean.indexOne,
                bean.indexTwo,
                bean.lengthOne,
                bean.lengthTwo,
                bean.totalApneaTime,
                bean.respiratoryQuality,
                Gson().toJson(bean.getmList()), //??????String
                DateUtil.getDate(DateUtil.YYYY_MM_DD, (endTime + Config.TIME_START) * 1000L)
            )
        )
        var value = HashMap<String, Any>()
        value["startTime"] = bean.startTime + XingLianApplication.TIME_START
        value["endTime"] = bean.endTime + XingLianApplication.TIME_START
        value["apneaTime"] = bean.totalApneaTime
        value["apneaSecond"] = bean.numberOfApnea
        value["avgHeartRate"] = bean.averageHeartRate
        value["minHeartRate"] = bean.minimumHeartRate
        value["maxHeartRate"] = bean.maximumHeartRate
        value["respiratoryQuality"] = bean.respiratoryQuality
        value["sleepList"] = Gson().toJson(bean.getmList())
        mViewModel.setSleep(value)

        var time = bean.endTime - bean.startTime
        if (time <= 0)
            return
        mCardList.forEachIndexed { index, addCardDTO ->
            if (addCardDTO.type == 2) {
                mCardList[index].endTime = XingLianApplication.TIME_START + endTime
                mCardList[index].data = "${DateUtil.getTextTime(time)}"
                mCardList[index].describe = DateUtil.getDate(
                    DateUtil.MM_AND_DD,
                    (XingLianApplication.TIME_START + endTime) * 1000
                )
                mHomeCardVoBean.list[index] = mCardList[index]
                mHomeAdapter.notifyItemChanged(index)
            }
            Hawk.put(HOME_CARD_BEAN, mHomeCardVoBean)
//            TLog.error("?????????????????????++")
        }

    }

    override fun SpecifySleepHistoryCallResult(mList: ArrayList<SleepBean>?) {
    }

    var mHeartRateList: ArrayList<Int> = arrayListOf()
    override fun SpecifyHeartRateHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<Int>?
    ) {
        mRefreshHeader?.setReleaseText(resources.getString(R.string.string_home_heart_refresh))
        if (mList.isNullOrEmpty()) {
            return
        }

        // ShowToast.showToastLong("???????????????")
        error("time ????????? ???????????????++${Gson().toJson(mList)}")
        mHeartRateList = mList
        val name: String = Gson().toJson(mList)
        mViewModel.setHeartRate(
            (startTime + XingLianApplication.TIME_START).toString(),
            (endTime + XingLianApplication.TIME_START).toString(),
            mList.toIntArray()
        )
        mHeartListDao.insert(
            HeartListBean(
                DateUtil.getLongTime(startTime),
                DateUtil.getLongTime(endTime),
                name,
                false,
                DateUtil.getDateTime(startTime)
            )
        )
        var countNum = 0L
        var i = 0
        mList?.forEach {
            if (it > 0) {
                countNum += it
                i++
            }
        }
        if (i == 0) //???????????????0 ????????????
            return
        mCardList.forEachIndexed { index, addCardDTO ->
            if (addCardDTO.type == 1) {
                mCardList[index].endTime = XingLianApplication.TIME_START + endTime
                mCardList[index].data = "${countNum / i}"
                mCardList[index].describe = DateUtil.getDate(
                    DateUtil.MM_AND_DD,
                    (XingLianApplication.TIME_START + endTime) * 1000
                )
                mHomeAdapter.notifyItemChanged(index)
                mHomeCardVoBean.list[index] = mCardList[index]
            }
        }
        TLog.error("mAddList+=" + Gson().toJson(mCardList))
        Hawk.put(HOME_CARD_BEAN, mHomeCardVoBean)
    }

    override fun SpecifyHeartRateHistoryCallResult(mList: ArrayList<Int>?) {
    }

    override fun SpecifyBloodOxygenHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<Int>?
    ) {
        mRefreshHeader?.setReleaseText(resources.getString(R.string.string_home_spo2_refresh))
        if (mList.isNullOrEmpty()){
            mSwipeRefreshLayout.finishRefresh()

            return
        }

        TLog.error("time ????????? ??????++${Gson().toJson(mList)}")
        val name: String = Gson().toJson(mList)

        mViewModel.setBloodOxygen(
            (startTime + XingLianApplication.TIME_START).toString(),
            (endTime + XingLianApplication.TIME_START).toString(),
            mList.toIntArray()
        )
        //   mViewModel.setBloodOxygen(value)
        if (endTime - startTime >= BloodOxygenListBean.day) {
            mBloodOxygenListDao.insert(
                BloodOxygenListBean(
                    startTime + XingLianApplication.TIME_START,
                    endTime + XingLianApplication.TIME_START,
                    name,
                    true,
                    DateUtil.getDateTime(startTime)
                )
            )
        } else {
            mBloodOxygenListDao.insert(
                BloodOxygenListBean(
                    startTime + XingLianApplication.TIME_START,
                    endTime + XingLianApplication.TIME_START,
                    name,
                    false,
                    DateUtil.getDateTime(startTime)
                )
            )
        }
        var countNum = 0L
        var i = 0
        mList?.forEach {
            if (it > 0) {
                countNum += it
                i++
            }
        }
        if (i == 0){
            mSwipeRefreshLayout.finishRefresh()
            return
        }

        mCardList.forEachIndexed { index, addCardDTO ->
            if (addCardDTO.type == 4) {
                mCardList[index].endTime = XingLianApplication.TIME_START + endTime
                mCardList[index].data = "${countNum / i}"
                mCardList[index].describe = DateUtil.getDate(
                    DateUtil.MM_AND_DD,
                    (XingLianApplication.TIME_START + endTime) * 1000
                )
                TLog.error("???????????? ??????++" + Gson().toJson(mCardList))

                mHomeAdapter.notifyItemChanged(index)
                mHomeCardVoBean.list[index] = mCardList[index]
            }
        }
        TLog.error("mAddList+=" + Gson().toJson(mCardList))
        Hawk.put(HOME_CARD_BEAN, mHomeCardVoBean)

    }

    override fun SpecifyBloodOxygenHistoryCallResult(
        mList: ArrayList<Int>?
    ) {
    }

//    override fun SpecifyBloodPressureHistoryCallResult(
//        startTime: Long,
//        endTime: Long,
//        mList: ArrayList<DataBean>?
//    ) {
//    }

    @SuppressLint("SetTextI18n")
    override fun DeviceMotionResult(mDataBean: DataBean) {
        TLog.error("--------????????????????????????${Gson().toJson(mDataBean)}")
        TLog.error("exerciseSteps===" + mDeviceInformationBean.exerciseSteps.toInt())
        val forMater = DecimalFormat("#0.00")
        forMater.roundingMode = RoundingMode.DOWN
        mDeviceInformationBean = Hawk.get(PERSONAL_INFORMATION, DeviceInformationBean())
        if (mDeviceInformationBean == null)
            return
        TLog.error("circleSports  DeviceMotionResult  mDataBean.calories+=${mDataBean.calories}  +=${mDataBean.distance.toDouble()}")
        circleSports.maxProgress = mDeviceInformationBean.exerciseSteps.toInt()
        circleSports?.progress = mDataBean.totalSteps.toInt()
        TLog.error("unitSystem==" + Gson().toJson(mDeviceInformationBean))
        if (mDeviceInformationBean?.unitSystem == 1.toByte()) {
         //  val miDis = Utils.muiltip(mHomeCardVoBean.distance.toDouble()/1000,0.6213)
            tvKM?.text = "${forMater.format(mDataBean.distance.toDouble()/1000)}"+" "+resources.getString(R.string.string_unit_mile)
        } else
            tvKM?.text = "${forMater.format(mDataBean.distance.toDouble() / 1000)}"+" "+resources.getString(R.string.string_unit_km)
        //  TLog.error("calories==${data.calories}")
        tvCalories?.text = "${mDataBean.calories}"+" "+resources.getString(R.string.string_unit_kcal)



        var locaDis = mDataBean.distance.toDouble() / 1000

        if(mDeviceInformationBean.unitSystem == 1.toByte()){
            locaDis = Utils.muiltip(locaDis,1.61)
        }


        val countStepMap = HashMap<String,Any>();
        countStepMap["step"] =mDataBean.totalSteps.toInt()
        countStepMap["calorie"] = mDataBean.calories.toInt()
        countStepMap["distance"] = forMater.format(locaDis)
        mViewModel.uploadHomeRealCountStep(countStepMap)

    }

    override fun SpecifyStressFatigueHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<PressureBean>?
    ) {
        mRefreshHeader?.setReleaseText(resources.getString(R.string.string_home_pressure_refresh))
        TLog.error("????????????????????????++" + Gson().toJson(mList))
        if (mList.isNullOrEmpty()){
            return
        }

        val name: String = Gson().toJson(mList)

        mViewModel.setPressure(
            DateUtil.getLongTime(startTime).toString(),
            DateUtil.getLongTime(endTime).toString(),
            name
        )
        if (endTime - startTime >= HeartListBean.day) {
            mPressureListDao.insert(
                PressureListBean(
                    DateUtil.getLongTime(startTime), DateUtil.getLongTime(endTime),
                    name,
                    true,
                    DateUtil.getDateTime(startTime)
                )
            )
        } else {
            mPressureListDao.insert(
                PressureListBean(
                    DateUtil.getLongTime(startTime), DateUtil.getLongTime(endTime),
                    name,
                    false,
                    DateUtil.getDateTime(startTime)
                )
            )
        }
        var countNum = 0
        var i = 0
        mList?.forEach {
            if (it.stress > 0) {
                countNum = it.stress
                i++
            }
        }
        TLog.error("?????????????????????=" + countNum)
        if (i == 0)
            return
        mCardList.forEachIndexed { index, addCardDTO ->
            if (addCardDTO.type == 3) {
                mCardList[index].endTime = XingLianApplication.TIME_START + endTime
                mCardList[index].data = "$countNum"
                mCardList[index].describe = DateUtil.getDate(
                    DateUtil.MM_AND_DD,
                    (XingLianApplication.TIME_START + endTime) * 1000
                )
                mHomeAdapter.notifyItemChanged(index)
                mHomeCardVoBean.list[index] = mCardList[index]
            }
        }

        Hawk.put(HOME_CARD_BEAN, mHomeCardVoBean)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE.toInt() -> {
                var data: DataBean = event.data as DataBean

                TLog.error("--------??????????????????="+data.toString())

                val time =
                    (getSelectedCalendar()!!.timeInMillis / 1000) - TimeUtil.getTodayZero(0) / 1000
//                TLog.error("??????")
                if (time in 1..86400) {
                    circleSports?.progress = data.totalSteps.toInt()
                    val forMater = DecimalFormat("#0.00")
                    forMater.roundingMode = RoundingMode.DOWN
                    mDeviceInformationBean = Hawk.get(PERSONAL_INFORMATION, DeviceInformationBean())
                    TLog.error("circleSports data.distance==${data.distance}  data.calories+=${data.calories}")

                    TLog.error("-------EventBus?????????--------")
                    if (mDeviceInformationBean?.unitSystem == 1.toByte()) {
                       var disT =  Utils.muiltip(data.distance.toDouble() / 1000,1.61)
                        tvKM?.text = "${forMater.format(data.distance.toDouble() / 1000)} ??????"
                    } else
                        tvKM?.text = "${forMater.format(data.distance.toDouble() / 1000)} ??????"
                    //  TLog.error("calories==${data.calories}")
                    tvCalories?.text = "${data.calories}"+" "+resources.getString(R.string.string_unit_kcal)

                    val countStepMap = HashMap<String,Any>();


                    var locaDis = data.distance.toDouble() / 1000

                    if(mDeviceInformationBean.unitSystem == 1.toByte()){
                        locaDis = Utils.muiltip(locaDis.toDouble(),1.61)
                    }


                    countStepMap["step"] = data.totalSteps
                    countStepMap["calorie"] = data.calories
                    countStepMap["distance"] = forMater.format(locaDis)
                    mViewModel.uploadHomeRealCountStep(countStepMap)
                }
            }

            com.app.fmate.Config.eventBus.HOME_CARD -> {
                if (HelpUtil.netWorkCheck(requireContext())) {
                    TLog.error("??????")
                    mViewModel.getHomeCard()
                } else {
                    mHomeCardVoBean = Hawk.get(HOME_CARD_BEAN, HomeCardVoBean())
                    TLog.error(" ?????????" + Gson().toJson(mHomeCardVoBean))
                    if (mHomeCardVoBean == null || mHomeCardVoBean.list == null) {
                        setHomeCard()
                    } else {
                        if (mHomeCardVoBean == null || mHomeCardVoBean.list == null) {
                            return
                        }
                        mCardList = mHomeCardVoBean.list
                        mHomeAdapter.data = mCardList
                    }
                    mHomeAdapter.notifyDataSetChanged()
                }
            }
            com.app.fmate.Config.eventBus.BLOOD_PRESSURE_RECORD,
            com.app.fmate.Config.eventBus.MAP_MOVEMENT_SATISFY,
            com.app.fmate.Config.eventBus.DEVICE_DELETE_DEVICE
            -> {
                TLog.error(" ?????????" + event.code)
                mHomeCardVoBean = Hawk.get(HOME_CARD_BEAN, HomeCardVoBean())
                TLog.error(" ?????????" + Gson().toJson(mHomeCardVoBean))
                if (mHomeCardVoBean == null || mHomeCardVoBean.list == null) {
                    setHomeCard()
                    //  mHomeAdapter.notifyDataSetChanged()
                } else {
                    if (mHomeCardVoBean == null || mHomeCardVoBean.list == null) {
                        return
                    }
                    mCardList = mHomeCardVoBean.list
                    mHomeAdapter.data = mCardList
                }
                TLog.error("mHomeCardBean.addCard+=" + Gson().toJson(mHomeCardVoBean))
                mHomeAdapter.notifyDataSetChanged()
            }
            com.app.fmate.Config.eventBus.SPORTS_GOAL_EXERCISE_STEPS -> {
                val step: String = event.data.toString()
                tvGoal.text = "${step}"+resources.getString(R.string.step)
                TLog.error(" mDeviceInformationBean.exerciseSteps+=" + mDeviceInformationBean.exerciseSteps)
                circleSports.maxProgress = step.toInt()
                TLog.error(" circleSports SPORTS_GOAL_EXERCISE_STEPS")
            }
            com.app.fmate.Config.eventBus.HOME_HISTORICAL_BIG_DATA -> {
//                TLog.error("??????????????????++=")
                mSwipeRefreshLayout.finishRefresh()
                mSwipeRefreshLayout.autoRefresh()
                // homeBleWrite()
            }
            com.app.fmate.Config.eventBus.CHANGE_UNIT -> {
                var data = event.data as LoginBean
                userInfo = data
                mHomeAdapter.notifyDataSetChanged()
            }

        }
    }

    override fun SpecifyTemperatureHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<Int>?
    ) {
        mRefreshHeader?.setReleaseText(resources.getString(R.string.string_home_temp_refresh))
        TLog.error("time ??????++${Gson().toJson(mList)}")
        if (mList.isNullOrEmpty()) {
            TLog.error("---?????? ????????????????????????")
            mSwipeRefreshLayout.finishRefresh()
            return
        }
        val name: String = Gson().toJson(mList)
        TLog.error("---?????? time ??????++${name}")
        mTempListDao.insert(
            TempListBean(
                startTime + XingLianApplication.TIME_START,
                endTime + XingLianApplication.TIME_START,
                name,
                DateUtil.getDateTime(startTime)
            )
        )
        mViewModel.setTemperature(
            (startTime + XingLianApplication.TIME_START).toString(),
            (endTime + XingLianApplication.TIME_START).toString(),
            mList.toIntArray()
        )
        var tempLast = 0

        var iFZero = 0    //30????????????0??????
        var num = 0
        mList?.forEachIndexed { index, i ->
            num += i
            if (i == 0)
                iFZero++

            if ((index + 1) % 30 == 0) {//6?????????????????????
                var size =  //??????0???????????????
                    if ((30 - iFZero) <= 0)
                        1
                    else
                        (30 - iFZero)
                var heart = num / size
                if (heart > 0)
                    tempLast = heart
                num = 0
                iFZero = 0
            }
        }

        TLog.error("---------??????======")

        if (tempLast == 0){
            mSwipeRefreshLayout.finishRefresh()
            return
        }

        TLog.error("==" + Gson().toJson(mCardList))
        TLog.error("------mHomeCardVoBean==" + Gson().toJson(mHomeCardVoBean))
        mCardList.forEachIndexed { index, addCardDTO ->
            if (addCardDTO.type == 6) {
                TLog.error("index+=" + index)
                mCardList[index].endTime = XingLianApplication.TIME_START + endTime
                mCardList[index].data = "${tempLast.toDouble() / 10}"
                mCardList[index].describe = DateUtil.getDate(
                    DateUtil.MM_AND_DD,
                    (XingLianApplication.TIME_START + endTime) * 1000
                )
                mHomeAdapter.notifyItemChanged(index)
                mHomeCardVoBean.list[index] = mCardList[index]
            }
        }
        Hawk.put(HOME_CARD_BEAN, mHomeCardVoBean)

//        MainHomeActivity().setSyncComplete(true)

//        val resultByte = CmdUtil.getFullPackage(byteArrayOf(0x02,0x3D,0x00))
//        BleWrite.writeCommByteArray(resultByte,false,this)
    }

}

