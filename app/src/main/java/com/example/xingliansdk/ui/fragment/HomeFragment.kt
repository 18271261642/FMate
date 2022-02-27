package com.example.xingliansdk.ui.fragment

//import com.example.xingliansdk.bean.HomeCardBean
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.Config.database.*
import com.example.xingliansdk.Config.eventBus.HOME_HISTORICAL_BIG_DATA_WEEK
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.XingLianApplication.Companion.getSelectedCalendar
import com.example.xingliansdk.adapter.HomeAdapter
import com.example.xingliansdk.adapter.PopularScienceAdapter
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.bean.room.*
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.blesend.BleSend
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.viewmodel.HomeViewModel
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.*
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
    BleWrite.HistoryCallInterface, //大数据时间列表
    BleWrite.SpecifyDailyActivitiesHistoryCallInterface,  //运动
    BleWrite.SpecifySleepHistoryCallInterface  //睡眠
    , BleWrite.SpecifyHeartRateHistoryCallInterface //心率
    , BleWrite.SpecifyBloodOxygenHistoryCallInterface  //血氧
//    , BleWrite.SpecifyBloodPressureHistoryCallInterface //血压
    , BleWrite.SpecifyStressFatigueHistoryCallInterface //压力
    , BleWrite.SpecifyTemperatureHistoryCallInterface //体温
    , BleWrite.DeviceMotionInterface //实时运动返回
{
    companion object {
        //关于这个页面会重复点击打开俩次的针对性操作
        var PressureOnClick = false

        /**
         * 首次时以网络数据第一次为准 后面以手表为准更新网络数据时不在更新此数据
         */
        var progressStatus = false
    }

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

    //压力
    private lateinit var mPressureListDao: PressureListDao
    lateinit var mPopularScienceAdapter: PopularScienceAdapter
    private lateinit var mPopularList: MutableList<PopularScienceBean.ListDTO>



    private val handler = object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(activity!=null && !activity!!.isFinishing){
                mSwipeRefreshLayout.finishRefresh()
            }
        }
    }



    //    private var isRefresh = false
    private fun getRoomList() {
        sDao = AppDataBase.instance.getRoomMotionTimeDao()
        mMotionListDao = AppDataBase.instance.getMotionListDao()
//        TLog.error("打印运动数据++${Gson().toJson(mMotionListDao.getAllRoomMotionList())}")
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
        //刷新可滑动状态
        mHomeAdapter.setOnItemClickListener { adapter, view, position ->
            //     运动记录", "心率", "睡眠", "体重", "血氧饱和度")
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
                        TLog.error("进来了+" + PressureOnClick)
                        PressureOnClick = true
                        JumpUtil.startPressureActivity(activity, mCardList[position])
                    } else {
                        PressureOnClick = false
                    }
                }
                4 -> {
                    JumpUtil.startBloodOxygenActivity(activity, mCardList[position])
                }
                5 -> {
                    JumpUtil.startBloodPressureActivity(activity, mCardList[position])
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
        TLog.error("时间==" + (DateUtil.getTodayZero() / 1000 - XingLianApplication.TIME_START))
        Handler(Looper.getMainLooper()).postDelayed({
            if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                mSwipeRefreshLayout.finishRefresh()
                //isRefresh=false
            } else {
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
        tvGoal?.text = "${mDeviceInformationBean.exerciseSteps}步"

        onClickListener()
    }

    private fun onClickListener() {
        tvEdit.setOnClickListener(this)
        circleSports.setOnClickListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
//        TLog.error("进行刷新不判断")
//        if(!isRefresh) {
//            TLog.error("进行刷新")
        TLog.error("mHomeCardVoBean====+" + Gson().toJson(mHomeCardVoBean))
//        mSwipeRefreshLayout.finishRefresh(60000)
        handler.sendEmptyMessageDelayed(0x00,15 * 1000)
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
                    ShowToast.showToastLong("暂无网络,不可编辑")
                    return
                }
                JumpUtil.startCardEditActivity(activity)
            }
            R.id.circleSports -> {   //步数列表
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
            TLog.error("mainViewModel.result+" + Gson().toJson(it))
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
            tvGoal.text = "${it.userConfig.movingTarget.toLong()}步"
            TLog.error("个人信息++" + Gson().toJson(it))
        }

        mViewModel.resultPopular.observe(this)
        {
            TLog.error("mainViewModel.resultPopular")
            if (it == null || it.list.isNullOrEmpty() || it.list.size <= 0)
                return@observe
            TLog.error("更新的数据===" + Gson().toJson(it))
            mPopularList.addAll(it.list)
            mPopularScienceAdapter.notifyDataSetChanged()
        }

        mViewModel.resultHomeCard.observe(this)
        {
            TLog.error("mainViewModel.resultHomeCard")
            //   TLog.error("===" + Gson().toJson(it))
            if (it == null || it.list == null || it.list.size <= 0)
                return@observe
                TLog.error("首页卡片数据++" + Gson().toJson(it))
            mCardList = it.list
            mHomeAdapter.data = it.list
            mHomeAdapter.notifyDataSetChanged()
            Hawk.put(HOME_CARD_BEAN, it)
            mHomeCardVoBean = it
            //   TLog.error("首页卡片数据++" + Gson().toJson(it))
            if (BleConnection.iFonConnectError || BleConnection.Unbind) {
                mSwipeRefreshLayout.finishRefresh()
                if (!progressStatus) {
                    progressStatus = true
                    TLog.error("circleSports  progressStatus+=" + progressStatus)
                    circleSports.maxProgress = mHomeCardVoBean.movingTarget.toInt()
                    circleSports?.progress = mHomeCardVoBean.steps.toInt()
                }
                if (mDeviceInformationBean?.unitSystem == 1.toByte()) {
                    tvKM?.text = "${mHomeCardVoBean.distance} 英里"
                } else
                    tvKM?.text = "${mHomeCardVoBean.distance} 公里"
                tvCalories?.text = "${mHomeCardVoBean.calorie} 千卡"
            }
        }
    }

    var mTimeList: ArrayList<TimeBean> = arrayListOf()
    override fun HistoryCallResult(key: Byte, mList: ArrayList<TimeBean>) {
        TLog.error("时间mList==" + Gson().toJson(mList))
        TLog.error("key==" + key)
        if (mList?.size <= 0) {
            if (key == Config.BigData.DEVICE_SLEEP) {
                SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA_WEEK, true)
                mSwipeRefreshLayout.finishRefresh()
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
                    TLog.error("运动时间++" + Gson().toJson(mTimeList))
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
                        TLog.error("访问睡眠++${it.startTime}  ===${it.endTime}")
                        BleWrite.writeSpecifySleepHistoryCall(
                            it.startTime, it.endTime,
                            this, true
                        )
                    } else {
                        TLog.error("睡眠时间不是今天不访问++" + it.startTime)
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
                    //回调拿前面几天的大数据

                }
                Config.BigData.DEVICE_HEART_RATE -> {
                    error("DEVICE_HEART_RATE==心率")
                    mTimeList = arrayListOf()
                    mTimeList.add(it)
//                    BleWrite.writeSpecifyHeartRateHistoryCall(
//                        mTimeList[0].startTime,
//                        mTimeList[0].endTime,
//                        this
//                    )
                    error("DEVICE_HEART_RATE==心率==" + Gson().toJson(mTimeList))
                    //    SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA_WEEK, true)
                    RoomUtils.updateHeartRateData(mTimeList, this)
                }
                Config.BigData.DEVICE_STRESS_FATIGUE -> {
                    error("DEVICE_STRESS_FATIGUE==压力")
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
        error("指定日常数据++${Gson().toJson(mList)}")
        var stepList: MutableList<Int> = mutableListOf()
        var step = 0L
        mList?.forEach {
            stepList.add(it.steps)
            step += it.steps
        }
        error("30分钟一组的步数++${Gson().toJson(stepList)}")
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
//        error("指定日常数据 没返回时间++${Gson().toJson(mList)}")

    }

    override fun SpecifySleepHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<SleepBean>?, bean: SleepBean
    ) {
        mRefreshHeader?.setReleaseText("睡眠刷新")
        error(" time ${startTime + XingLianApplication.TIME_START}")
        error(" endTime ${endTime + XingLianApplication.TIME_START}")
        error("time睡眠数据${Gson().toJson(mList)}")
        SNEventBus.sendEvent(HOME_HISTORICAL_BIG_DATA_WEEK, true)
        mSwipeRefreshLayout.finishRefresh()
        //  ShowToast.showToastLong("睡眠数据更新完成")
        var timeZero = (bean.startTime + XingLianApplication.TIME_START) / 86400 * 86400
        TLog.error("睡眠数据==" + timeZero)
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
                Gson().toJson(bean.getmList()), //转成String
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
//            TLog.error("最后请求完睡眠++")
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
        mRefreshHeader?.setReleaseText("心率刷新")
        if (mList.isNullOrEmpty()) {
            return
        }

        // ShowToast.showToastLong("刷新心率了")
        error("time 好家伙 看看心率吧++${Gson().toJson(mList)}")
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
        if (i == 0) //如果全部为0 则不显示
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
        mRefreshHeader?.setReleaseText("血氧刷新")
        if (mList.isNullOrEmpty())
            return
        TLog.error("time 好家伙 血氧++${Gson().toJson(mList)}")
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
        if (i == 0)
            return
        mCardList.forEachIndexed { index, addCardDTO ->
            if (addCardDTO.type == 4) {
                mCardList[index].endTime = XingLianApplication.TIME_START + endTime
                mCardList[index].data = "${countNum / i}"
                mCardList[index].describe = DateUtil.getDate(
                    DateUtil.MM_AND_DD,
                    (XingLianApplication.TIME_START + endTime) * 1000
                )
                TLog.error("刷新血氧 首页++" + Gson().toJson(mCardList))

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

    override fun DeviceMotionResult(mDataBean: DataBean) {
        TLog.error("设备实时运动${Gson().toJson(mDataBean)}")
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
            tvKM?.text = "${forMater.format(mDataBean.distance.toDouble() / 1000)} 英里"
        } else
            tvKM?.text = "${forMater.format(mDataBean.distance.toDouble() / 1000)} 公里"
        //  TLog.error("calories==${data.calories}")
        tvCalories?.text = "${mDataBean.calories} 千卡"
    }

    override fun SpecifyStressFatigueHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: ArrayList<PressureBean>?
    ) {
        mRefreshHeader?.setReleaseText("压力刷新")
        TLog.error("获取到的压力数据++" + Gson().toJson(mList))
        if (mList.isNullOrEmpty())
            return
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
        TLog.error("最后一个压力值=" + countNum)
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
                val time =
                    (getSelectedCalendar()!!.timeInMillis / 1000) - TimeUtil.getTodayZero(0) / 1000
//                TLog.error("回调")
                if (time in 1..86400) {
                    circleSports?.progress = data.totalSteps.toInt()
                    val forMater = DecimalFormat("#0.00")
                    forMater.roundingMode = RoundingMode.DOWN
                    mDeviceInformationBean = Hawk.get(PERSONAL_INFORMATION, DeviceInformationBean())
                    TLog.error("circleSports data.distance==${data.distance}  data.calories+=${data.calories}")
                    if (mDeviceInformationBean?.unitSystem == 1.toByte()) {
                        tvKM?.text = "${forMater.format(data.distance.toDouble() / 1000)} 英里"
                    } else
                        tvKM?.text = "${forMater.format(data.distance.toDouble() / 1000)} 公里"
                    //  TLog.error("calories==${data.calories}")
                    tvCalories?.text = "${data.calories} 千卡"
                }
            }

            com.example.xingliansdk.Config.eventBus.HOME_CARD -> {
                if (HelpUtil.netWorkCheck(requireContext())) {
                    TLog.error("刷新")
                    mViewModel.getHomeCard()
                } else {
                    mHomeCardVoBean = Hawk.get(HOME_CARD_BEAN, HomeCardVoBean())
                    TLog.error(" 被删除" + Gson().toJson(mHomeCardVoBean))
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
            com.example.xingliansdk.Config.eventBus.BLOOD_PRESSURE_RECORD,
            com.example.xingliansdk.Config.eventBus.MAP_MOVEMENT_SATISFY,
            com.example.xingliansdk.Config.eventBus.DEVICE_DELETE_DEVICE
            -> {
                TLog.error(" 被删除" + event.code)
                mHomeCardVoBean = Hawk.get(HOME_CARD_BEAN, HomeCardVoBean())
                TLog.error(" 被删除" + Gson().toJson(mHomeCardVoBean))
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
            com.example.xingliansdk.Config.eventBus.SPORTS_GOAL_EXERCISE_STEPS -> {
                val step: String = event.data.toString()
                tvGoal.text = "${step}步"
                TLog.error(" mDeviceInformationBean.exerciseSteps+=" + mDeviceInformationBean.exerciseSteps)
                circleSports.maxProgress = step.toInt()
                TLog.error(" circleSports SPORTS_GOAL_EXERCISE_STEPS")
            }
            com.example.xingliansdk.Config.eventBus.HOME_HISTORICAL_BIG_DATA -> {
//                TLog.error("首页更新数据++=")
                mSwipeRefreshLayout.finishRefresh()
                mSwipeRefreshLayout.autoRefresh()
                // homeBleWrite()
            }
            com.example.xingliansdk.Config.eventBus.CHANGE_UNIT -> {
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
        mRefreshHeader?.setReleaseText("体温刷新")
        TLog.error("time 体温++${Gson().toJson(mList)}")
        if (mList.isNullOrEmpty()) {
            TLog.error("空的 直接不走下一步了")
            return
        }
        val name: String = Gson().toJson(mList)
        TLog.error("非空 time 体温++${name}")
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

        var iFZero = 0    //30个平均为0排除
        var num = 0
        mList?.forEachIndexed { index, i ->
            num += i
            if (i == 0)
                iFZero++

            if ((index + 1) % 30 == 0) {//6个数组平分一组
                var size =  //当为0时特殊处理
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
        if (tempLast == 0)
            return
        TLog.error("==" + Gson().toJson(mCardList))
        TLog.error("mHomeCardVoBean==" + Gson().toJson(mHomeCardVoBean))
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
    }

}

