package com.example.xingliansdk.ui.bloodOxygen

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.PopularScienceAdapter
import com.example.xingliansdk.adapter.SleepAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.bean.SleepTypeBean
import com.example.xingliansdk.bean.room.*
import com.example.xingliansdk.network.api.heartView.HeartRateVoBean
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.ui.bloodOxygen.viewmodel.BloodOxygenViewModel
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.CustomBarChart
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.view.IF
import com.example.xingliansdk.widget.TitleBarLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.shon.connector.BleWrite
import com.shon.connector.bean.TimeBean
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_bloodoxygen.*
import kotlinx.android.synthetic.main.activity_bloodoxygen.harts_hrr
import kotlinx.android.synthetic.main.activity_bloodoxygen.img_left
import kotlinx.android.synthetic.main.activity_bloodoxygen.img_right
import kotlinx.android.synthetic.main.activity_bloodoxygen.ryPopularScience
import kotlinx.android.synthetic.main.activity_bloodoxygen.titleBar
import kotlinx.android.synthetic.main.activity_bloodoxygen.tvAvg
import kotlinx.android.synthetic.main.activity_bloodoxygen.tvType
import kotlinx.android.synthetic.main.activity_bloodoxygen.tvTypeTime
import kotlinx.android.synthetic.main.activity_heart_rate_o.*


import java.util.*
import kotlin.collections.ArrayList

class BloodOxygenActivity : BaseActivity<BloodOxygenViewModel>(), View.OnClickListener,
    OnChartValueSelectedListener,
    BleWrite.HistoryCallInterface,
    BleWrite.SpecifyBloodOxygenHistoryCallInterface {
    private lateinit var mList: ArrayList<Int>
    private lateinit var hartsHrr: BarChart

    //private lateinit var hartsHrr: LineChart
    private var bloodOxygenList: MutableList<RoomTimeBean> = mutableListOf()
    private lateinit var sDao: RoomTimeDao

    //血氧
    private lateinit var mBloodOxygenListDao: BloodOxygenListDao
    private lateinit var mSleepAdapter: SleepAdapter
    private var sleepTypeList: ArrayList<SleepTypeBean> = arrayListOf()
    override fun layoutId() = R.layout.activity_bloodoxygen
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                dialog()
            }

            override fun onActionClick() {
            }
        })
        mList = arrayListOf()
        img_left.setOnClickListener(this)
        img_right.setOnClickListener(this)
//        tvTypeTime.setOnClickListener(this)
        sDao = AppDataBase.instance.getRoomTimeDao()
        mBloodOxygenListDao = AppDataBase.instance.getBloodOxygenDao()
        val allRoomTimes = sDao.getAllRoomTimes()
        val bloodTimes = sDao.getBloodOxygenTimes()
        TLog.error("获取mHeartListDao++${allRoomTimes.size}")
        TLog.error("获取一下时间库++${Gson().toJson(allRoomTimes)}")
        TLog.error("获取一下时间库++${Gson().toJson(bloodTimes)}")
        TLog.error("血氧数组长度++${mBloodOxygenListDao.getAllList().size}")
        TLog.error("血氧数组++${Gson().toJson(mBloodOxygenListDao.getAllList())}")
        var bean = intent.getSerializableExtra("bean") as HomeCardVoBean.ListDTO
        var toDayTime = System.currentTimeMillis()
        if (bean.startTime > 0)
            toDayTime = bean.startTime * 1000L
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar(toDayTime))
        if (allRoomTimes.size > 0) {
            bloodOxygenList = allRoomTimes
        }
        hartsHrr = harts_hrr
        setAdapter()
        setPopularAdapter()
        setTitleDateData()

        chartView()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_left -> {
                XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, -1)
                setTitleDateData()
            }
            R.id.img_right -> {
                XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, +1)
                setTitleDateData()
            }
            R.id.tvTypeTime -> {
//                dialog()
            }

        }
    }

    private fun chartView() {
        hartsHrr.description.isEnabled = false
        hartsHrr.setScaleEnabled(true)//设置比列启动
        hartsHrr.legend.isEnabled = false
        hartsHrr.isScaleYEnabled = false
//        hartsHrr.isScaleXEnabled=false
        hartsHrr.setDrawBarShadow(false)
        hartsHrr.viewPortHandler.setMaximumScaleX(3f)
        //监听器
        hartsHrr.setOnChartValueSelectedListener(this)
        var xAxis: XAxis
        val timeMatter: IAxisValueFormatter = BloodOxygenValueFormatter(hartsHrr)
        run {
            xAxis = hartsHrr.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)//设置网格线
            xAxis.axisLineColor = Color.WHITE
            xAxis.textColor=resources.getColor(R.color.sub_text_color)
            xAxis.axisMaximum = 48f
            xAxis.granularity = 12f // 想 弄成 4 hour
            xAxis.labelCount = 4
            xAxis.valueFormatter = timeMatter
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.isEnabled = false
//            leftAxis.isGranularityEnabled = true
            leftAxis.axisMinimum = 75f
            leftAxis.axisMaximum = 100f
            leftAxis.setDrawZeroLine(true)
        }
        var rightAxis: YAxis
        run {
            rightAxis = hartsHrr.axisRight
            rightAxis.axisMinimum = 75f
            rightAxis.axisMaximum = 100f
            rightAxis.granularity = 5f
//            rightAxis.setLabelCount(5,true)
            rightAxis.textColor=resources.getColor(R.color.sub_text_color)
            rightAxis.axisLineColor = Color.WHITE
            rightAxis.zeroLineColor = resources.getColor(R.color.color_view)
            rightAxis.gridColor = resources.getColor(R.color.color_view)

            rightAxis.setDrawZeroLine(true)
        }
        hartsHrr.extraBottomOffset = 20f
    }

    private var values =
        ArrayList<BarEntry>()

    private fun setDataView(mList: ArrayList<Int>) {
        TLog.error("==" + Gson().toJson(mList))
        values = ArrayList()
        var setList: ArrayList<Int> = arrayListOf()
//        setList=mList
        var minHeart = 999//默认一个比较大的值
        var avgHeart = 0L //平均值
        var avgNotZero = 0//平均值不为0有多少次
        var num = 0
        var iFZero = 0    //30个平均为0排除
        mList?.forEachIndexed { index, i ->
            run {
//                TLog.error("index+=" + index)

                if (i in 1 until minHeart) {
                    minHeart = i
                }
                num += i
                if (i == 0)
                    iFZero++
                else {
                    avgNotZero++
                    avgHeart += i
                }
                if ((index + 1) % 30 == 0) {//6个数组平分一组

                    var size =  //当为0时特殊处理
                        if ((30 - iFZero) <= 0)
                            1
                        else
                            (30 - iFZero)
                    var heart = num / size
                    if (heart in 1 until minHeart) {
                        minHeart = heart
                    }
                    setList.add(heart)

                    num = 0
                    iFZero = 0
//                    if (minHeart > 100)
//                        setList.add(0)
//                    else
//                        setList.add(minHeart)
//                    minHeart = 999
                }
            }
        }
        TLog.error("setList==${Gson().toJson(setList)}")
        var mIndex = 0
        var lastText = 0

        //展示的最小值
        var showMinvalue = 0
        //展示的平均值，去0
        var showAvgValue = 0
        var avgList : ArrayList<Int> = arrayListOf()


        setList.forEachIndexed { index, i ->
            values.add(BarEntry(index.toFloat(), i.toFloat()))
            if (i > 0) {
                mIndex = index
                lastText = i
                avgList.add(i)
            }
        }

        if(avgList.isEmpty()){
            setValue(0,0,0)
        }else{
            showMinvalue = Collections.min(avgList)
            var tmpCountV  = 0
            avgList.forEach {
                tmpCountV+=it
            }
            showAvgValue = tmpCountV / avgList.size

            TLog.error("-----血氧最大值最小值="+Collections.max(setList)+" "+showMinvalue+" "+showAvgValue +" ")

            setValue(Collections.max(setList),showMinvalue,showAvgValue)
        }


        if (setList.size > 0 && lastText > 0) {
            getLastText(mIndex, lastText)
        }
        //        if (hartsHrr.data != null &&
//            hartsHrr.data.dataSetCount > 0
//        ) {
//            set1 = hartsHrr.data.getDataSetByIndex(0) as BarDataSet
//            set1.values = values
//            hartsHrr.data.notifyDataChanged()
//            hartsHrr.notifyDataSetChanged()
//        } else {
        val set1: BarDataSet = BarDataSet(values, "")
        val getColors = IntArray(48)
        for (i in 0 until setList.size) {
            when {
                setList[i] >= 90 -> {
                    getColors[i] = resources.getColor(R.color.color_main_green)
                }
                setList[i] in 70..89 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_oxygen_hypoxia)
                }
                setList[i] < 70 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_oxygen_severe_hypoxia)
                }
                else -> {
                }
            }
        }
        set1.setColors(*getColors)
        set1.setDrawValues(false)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        hartsHrr.data = data
        hartsHrr.setFitBars(true)
//        }
        hartsHrr.invalidate()
    }

    override fun HistoryCallResult(key: Byte, mList: ArrayList<TimeBean>?) {
        if (mList?.size!! <= 0)
            return
        mList.reverse()
//        RoomUtils.updateHeartRateData(mList,this)
        RoomUtils.updateBloodOxygenDate(mList, this)
        bloodOxygenList = sDao.getAllRoomTimes()
        TLog.error("mList ++${mList[mList.size - 1].endTime}")
        TLog.error("sdao长度 ++${sDao.getAllRoomTimes().size}")
        TLog.error("sdao++${Gson().toJson(sDao.getAllRoomTimes())}")
    }

    override fun SpecifyBloodOxygenHistoryCallResult(
        startTime: Long,
        endTime: Long,
        mList: java.util.ArrayList<Int>
    ) {
        hideWaitDialog()
        val name: String = Gson().toJson(mList)
        if (endTime - startTime >= BloodOxygenListBean.day) {
            sDao.updateBloodOxygen(startTime, endTime)
            bloodOxygenList = sDao.getAllRoomTimes()
            mBloodOxygenListDao.insert(
                BloodOxygenListBean(
                    startTime,
                    endTime,
                    name,
                    true,
                    DateUtil.getDateTime(startTime)
                )
            )
        } else {
            mBloodOxygenListDao.insert(
                BloodOxygenListBean(
                    startTime,
                    endTime,
                    name,
                    false,
                    DateUtil.getDateTime(startTime)
                )
            )
        }
        TLog.error("===" + mList.size)
        setDataView(mList)
        hartsHrr.invalidate()

    }

    override fun SpecifyBloodOxygenHistoryCallResult(
        mList: ArrayList<Int>?
    ) {

    }

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        TLog.error("==" + e.x.toLong())
        tvTime.text = String.format(
            "%s",
            DateUtil.getDate(
                DateUtil.HH_MM,
                (TimeUtil.getTodayZero(0) + (e.x.toLong() * 30 * 60000L))
            )
                    + "-" +
                    DateUtil.getDate(
                        DateUtil.HH_MM,
                        (TimeUtil.getTodayZero(0) + ((e.x.toLong() + 1) * 30 * 60000L))
                    )
        )
        val bloodOxygenNum = h.y.toInt()
        if (bloodOxygenNum > 0)
            tvType.text = HelpUtil.getSpan(bloodOxygenNum.toString(), "%")
        else
            tvType.text = "--" // HelpUtil.getSpan("--", "%")
    }

    private fun getLastText(timeIndex: Int, heart: Int) {
        tvTime.text = String.format(
            "%s",
            DateUtil.getDate(
                DateUtil.HH_MM,
                (TimeUtil.getTodayZero(0) + (timeIndex * 30 * 60000L))
            )
                    + "-" +
                    DateUtil.getDate(
                        DateUtil.HH_MM,
                        (TimeUtil.getTodayZero(0) + ((timeIndex + 1) * 30 * 60000L))
                    )
        )
        tvType.text = HelpUtil.getSpan(heart.toString(), "%")
    }

    lateinit var mPopularScienceAdapter: PopularScienceAdapter
    private lateinit var mPopularList: MutableList<PopularScienceBean.ListDTO>
    private fun setPopularAdapter() {
        var hasmap = HashMap<String, Any>()
        hasmap["category"] = "4"
        mViewModel.getPopular(hasmap)
        mPopularList = ArrayList()
        ryPopularScience.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mPopularScienceAdapter = PopularScienceAdapter(mPopularList)
        ryPopularScience.adapter = mPopularScienceAdapter
        mPopularScienceAdapter.setOnItemClickListener { adapter, view, position ->
            JumpUtil.startWeb(this, mPopularList[position].detailUrl)
        }
    }

    private fun setAdapter() {
        ryBloodOxygen.layoutManager =
            GridLayoutManager(this, 2)
        mSleepAdapter = SleepAdapter(sleepTypeList)
        ryBloodOxygen.adapter = mSleepAdapter
        mSleepAdapter.setOnItemClickListener { adapter, view, position ->
            TLog.error("=" + Gson().toJson(mSleepAdapter.data[position]))
            //  JumpUtil.startSleepNightActivity(this, mSleepAdapter.data[position])

        }
    }

    private fun setAdapterDate(avg: Int, numberWake: Int) {
        sleepTypeList.clear()
        var statusType: Int
        var deepSleep = avg
        statusType = when {
            deepSleep > 99 -> 2
            deepSleep < 95 -> 1
            else -> 0
        }
        TLog.error("血氧浓度平均值+$statusType")
        sleepTypeList.add(
            SleepTypeBean(
                "血氧浓度平均值",
                "$deepSleep %",
                statusType.toString(),
                "参考值:95-99%",
                1
            )
        )
//        var lightSleep = avg
//        statusType = when {
//            lightSleep > 55 -> 2
//            else -> 0
//        }
//        sleepTypeList.add(
//            SleepTypeBean(
//                "呼吸暂停次数",
//                "$lightSleep%",
//                statusType.toString(),
//                "参考值：≤7",
//                2
//            )
//        )
        var eyeSleep = numberWake
        statusType = when {
            eyeSleep >= 20 -> 2
            else -> 0
        }
        sleepTypeList.add(
            SleepTypeBean(
                "低氧时间平均值",
                "$eyeSleep 次/分",
                statusType.toString(),
                "参考值：≤20次/分",
                3
            )
        )
        mSleepAdapter.notifyDataSetChanged()
    }

    /**
     * 设置标题日期相关数据
     */
    var timeDialog: Long? = System.currentTimeMillis()//默认为当天时间
    var date: String? = null
    private var lastTodayDate: String? = null
    private fun setTitleDateData() {
        val calendar: Calendar? = XingLianApplication.getSelectedCalendar()
        calendar?.set(Calendar.HOUR_OF_DAY, 0)
        calendar?.set(Calendar.MINUTE, 0)
        timeDialog = calendar?.timeInMillis
        date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar)
        if (DateUtil.equalsToday(date)) {
            //   tvTypeTime.setText(R.string.title_today)
            lastTodayDate = date
            img_right.visibility = View.INVISIBLE
        } else {
            img_right.visibility = View.VISIBLE

        }
        tvTypeTime.text = date
        TLog.error("timeInMillis==" + (calendar?.timeInMillis!! / 1000000))
        TLog.error("getCurrentDate==" + DateUtil.getCurrentDate() / 1000000)
        if (DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar?.timeInMillis!!)
                .equals(DateUtil.getDate(DateUtil.YYYY_MM_DD, DateUtil.getCurrentDate()))
        ) {
            img_right.visibility = View.INVISIBLE
        } else
            img_right.visibility = View.VISIBLE
        tvType.text = "--" // HelpUtil.getSpan("--", "%")
        tvTime.text = ""
        TLog.error("==" + calendar?.timeInMillis?.div(1000))
        mViewModel.getBloodOxygen(
            (DateUtil.getDayZero(timeDialog!!) / 1000).toString(),
            (DateUtil.getDayEnd(timeDialog!!) / 1000).toString()
        )
        //  calendar?.timeInMillis?.div(1000)?.let { getHeart(it) }
    }

    var setValueStatus = true  //显示值的设置
    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) {
            TLog.error("it=" + Gson().toJson(it))
            setValueStatus = true
            var mList = it
            if (mList.bloodOxygenVoList == null || mList.bloodOxygenVoList.size <= 0) {
                getHeart(date!!)
                //   date?.let { it1 -> getHeart(it1) }
                return@observe
            }
            setValueStatus = false
            var bloodOxygenVoList = mList.bloodOxygenVoList[0]
            var bloodOxygenList = Gson().toJson(bloodOxygenVoList.bloodOxygen)
            TLog.error("mlist=" + Gson().toJson(mList))
            mBloodOxygenListDao.insert(
                BloodOxygenListBean(
                    bloodOxygenVoList.startTimestamp.toLong(),
                    bloodOxygenVoList.endTimestamp.toLong(),
                    bloodOxygenList,
                    false,
                    bloodOxygenVoList.date
                )
            )

            if(bloodOxygenVoList.avg.toDouble().toInt() >0){
                tvAvg.text = HelpUtil.getSpan(bloodOxygenVoList.avg.toDouble().toString(), "%", 11)
            }else{
                tvAvg.text = "--"
            }


//            setValue(
//                bloodOxygenVoList.max.toDouble().toInt(),
//                bloodOxygenVoList.min.toDouble().toInt(),
//                bloodOxygenVoList.avg.toDouble().toInt()
//            )
            getHeart(date!!)

        }
        mViewModel.msg.observe(this) {
            date?.let { it1 ->
                setValueStatus = true//错误的情况都是自己算
//                getHeart(it1)
                getHeart(it1!!)
            }
        }
        mViewModel.resultPopular.observe(this)
        {
            if (it == null || it.list.isNullOrEmpty() || it.list.size <= 0)
                return@observe
            TLog.error("==" + it.list[0].image)
            mPopularList.addAll(it.list)
            mPopularScienceAdapter.notifyDataSetChanged()
        }
    }

    private fun getHeart(date: String) {
        val mBloodOxygenList = mBloodOxygenListDao.getSomedayBloodOxygen(this.date!!)
        TLog.error("mBloodOxygenList++" + Gson().toJson(mBloodOxygenList))
        if (mBloodOxygenList != null
            && !mBloodOxygenList.array.isNullOrEmpty()
            && mBloodOxygenList.array != "[]"
        ) {
            val heartRateList =
                Gson().fromJson(mBloodOxygenList.array, ArrayList::class.java)
            TLog.error("heartRateList+=" + heartRateList)
            var mList: ArrayList<Int> = heartRateList as ArrayList<Int>
//                TLog.error("mList__"+Gson().toJson(mList))
            var notNullList: ArrayList<Int> = ArrayList()
            var nullZero = 0
            var hypoxiaAverage = 0
            var min = 999
            for (i in 0 until mList.size) {
                if (mList[i] > 0) {
                    //     TLog.error("mList+="+mList[i].toDouble())
                    if (min > mList[i])
                        min = mList[i]
                    nullZero += mList[i]
                    if (mList[i] < 92)
                        hypoxiaAverage++
                    notNullList.add(mList[i])
                }
            }
            setDataView(mList)
            var avg = notNullList.size
            if (avg <= 0)//防止分母为0
                avg = 1
            if (min > 100)
                min = 0
            //   setAdapterDate(nullZero / avg, hypoxiaAverage)
           // setValue(Collections.max(mList), min, (nullZero / avg))

            if((nullZero / avg) >0){
                tvAvg.text = HelpUtil.getSpan((nullZero / avg).toString(), "%", 11)
            }else{
                tvAvg.text = "--"
            }


        } else {
            TLog.error("eeeee")
            setValue(0, 0, 0)
            tvAvg.text = "--"

            var nullList = arrayListOf<Int>()
            for (i in 0 until 1440)
                nullList.add(0)
            setDataView(nullList)
            // setAdapterDate(0, 0)
        }
        hartsHrr.invalidate()
    }


    private var calendar: Calendar? = null
    var toDay = DateUtil.getCurrentDate()
    private fun dialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_calendar
            dimAmount = 0.3f
            isFullHorizontal = false
            isFullVerticalOverStatusBar = false
            animStyle = R.style.AlphaEnterExitAnimation
            convertListenerFun { holder, dialog ->
                var calenderView = holder.getView<CalendarView>(R.id.calenderView)
                timeDialog?.let { it1 ->
                    TLog.error("it1==${it1}")
                    calenderView?.setDate(it1)
                }
                calenderView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
                    if (calendar == null) {
                        calendar = Calendar.getInstance()
                    }
                    calendar?.set(year, month, dayOfMonth, 0, 0)//不设置时分不行
                    var calendarTime = calendar?.timeInMillis
                    TLog.error("calendarTime+=${calendarTime}   calendar?.timeInMillis${calendar?.timeInMillis}")
                    if (calendarTime!! > toDay) {
                        TLog.error("calendarTime+= $calendarTime  DateUtil.getCurrentDate()++$toDay")
                        ShowToast.showToastLong("不可选择大于今天的日期")
                        return@setOnDateChangeListener
                    }

                    XingLianApplication.getSelectedCalendar()?.timeInMillis = calendarTime!!
//                    var time = "$year-${month + 1}-$dayOfMonth"
//                    tvTypeTime.text = time

                    setTitleDateData()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    private fun setValue(max: Int, min: Int, avg: Int) {
        if (max > 0 || min > 0 || avg > 0) {
            tvHeight.text = HelpUtil.getSpan(max.toString(), "%", 11)
            tvLow.text = HelpUtil.getSpan(min.toString(), "%", 11)
          //  tvAvg.text = HelpUtil.getSpan(avg.toString(), "%", 11)
        } else {
            tvHeight.text = "--"
            tvLow.text = "--"
           // tvAvg.text = "--"
        }
    }
}