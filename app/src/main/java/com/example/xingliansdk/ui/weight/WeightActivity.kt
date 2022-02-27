package com.example.xingliansdk.ui.weight


import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.WeightAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.CardWeightBean
import com.example.xingliansdk.bean.UpdateWeight
import com.example.xingliansdk.bean.YearBean
import com.example.xingliansdk.bean.room.*
import com.example.xingliansdk.custom.WeightMarkerView
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.network.api.weightView.WeightViewModel
import com.example.xingliansdk.utils.DeviceSportAxisValueFormatter
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.HelpUtil.setNumber
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_weight.*

import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.pow

/**
 * 体重管理页面
 */
class WeightActivity : BaseActivity<WeightViewModel>(), OnChartValueSelectedListener,
    View.OnClickListener, OnTabSelectListener {

    private val tags = "WeightActivity"
    override fun layoutId() = R.layout.activity_weight
    private var mTitles = arrayOf("日", "周", "月", "年")
    private lateinit var chart: LineChart
    private var position = 0
    var SET_WEIGHT_TYPE = "1"//成功时为1
    lateinit var sDao: WeightDao
    var mList: ArrayList<WeightBean> = arrayListOf()
    var mAllList: ArrayList<WeightBean> = arrayListOf()
    private lateinit var mWeightAdapter: WeightAdapter

    var yearCalendar: Calendar = DateUtil.getYearFirstDate(Calendar.getInstance())
    var monthCalendar: Calendar = DateUtil.getMonthFirstDate(Calendar.getInstance())
    var weekCalendar: Calendar = Calendar.getInstance()
    var dayCalendar: Calendar = Calendar.getInstance()

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
        var bean = intent.getSerializableExtra("bean") as HomeCardVoBean.ListDTO
        var toDayTime = System.currentTimeMillis()
//        if (bean.startTime > 0)
//            toDayTime = bean.startTime * 1000L
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar(toDayTime))
        dayCalendar = XingLianApplication.getSelectedCalendar()!!
        weekCalendar = XingLianApplication.getSelectedCalendar()!!
        monthCalendar = DateUtil.getMonthFirstDate(XingLianApplication.getSelectedCalendar()!!)
        yearCalendar = DateUtil.getYearFirstDate(XingLianApplication.getSelectedCalendar()!!)
        slidingTab.setTabData(mTitles)
        sDao = AppDataBase.instance.getWeightDao()
        chart1.visibility = View.GONE
        mAllList = sDao.getAllByDateDesc() as ArrayList<WeightBean>
        onClickListener()
        setAdapter()
        setTitleDateData()
        update()
        for (i in 3..255) {
            cardWeightItem.add(CardWeightBean(i, i.toString()))
        }
        for (j in 0..cardWeightItem.size) {
            val cityList = ArrayList<String>()
            for (i in 0..9)
                cityList.add(".$i")
            options2Items.add(cityList)
        }
        initCustomOptionPicker()

    }

    //获取网络数据看是否获取到
    var statusGetWeight = false
    override fun createObserver() {
        super.createObserver()

        mViewModel.resultSetWeight.observe(this) {
            hideWaitDialog()
            TLog.error("添加成功 it" + Gson().toJson(it))
            statusGetWeight = false
            val weightBean = Gson().fromJson(Gson().toJson(it), UpdateWeight::class.java)
            for (i in 0 until weightBean.list.size) {
                sureUpdate(weightBean.list[i].weight, weightBean.list[i].bmi, SET_WEIGHT_TYPE)
            }
        }
        mViewModel.msgSetWeight.observe(this) {
            //任何失败情况的操作都走本地数据库流程
            statusGetWeight = false
            sureUpdate(setWeight)
            hideWaitDialog()
        }
        mViewModel.msgGetWeight.observe(this) {
            hideWaitDialog()
            statusGetWeight = false
        }
        mViewModel.result.observe(this) { it ->
//            TLog.error("it" + Gson().toJson(it))
            hideWaitDialog()
            var weightViewModel = it
            statusGetWeight = true
            if (weightViewModel.weightModelList.isNullOrEmpty()) {
                tvWeight.text = "--"
                if (position!=0)
                chartInitView()
                return@observe
            }
            weightViewModel.weightModelList.forEach {
                sDao.insert(
                    WeightBean(
                        it.stampCreateTime,
                        it.createTime,
                        it.weight,
                        it.bmi, SET_WEIGHT_TYPE
                    )
                )
            }
            if (position == 0)
                tvTime.text = DateUtil.getDate(
                    DateUtil.HH_MM,
                    weightViewModel.weightModelList[0].stampCreateTime * 1000
                )
            tvWeight.text = HelpUtil.getSpan(weightViewModel.weight, "kg")
            tvBMI.text = if(weightViewModel.bmi.equals("0")) "--" else weightViewModel.bmi
            tvNowWeight.text = if(weightViewModel.changeWeight.equals("0.0")) "--" else HelpUtil.getSpan(weightViewModel.changeWeight, "kg")
            tvLastWeight.text = HelpUtil.getSpan(weightViewModel.lastWeight, "kg")
            if (position == 0) {
                chart1.visibility = View.GONE
                update()
            }
            else
                chartInitView()
//            update()
        }
        mViewModel.resultDeleteWeight.observe(this) {
            TLog.error("it==" + Gson().toJson(it))
            statusGetWeight = false
            hideWaitDialog()
            mViewModel.getWeight(
                position.toString(), DateUtil.getDate(
                    DateUtil.YYYY_MM_DD,
                    calendarType
                )
            )
        }
        mViewModel.msgDelete.observe(this)
        {
            statusGetWeight = false
            hideWaitDialog()
        }
    }

    private fun update() {
        if (position == 0) {
            tvNowWeightTitle.text = "本次变化"
            tvLastWeightTitle.text = "上一次"
            tvBMITitle.text = "BMI"
            mList = sDao.getList(
                DateUtil.getDate(
                    DateUtil.YYYY_MM_DD,
                    dayCalendar
                )
            ) as ArrayList<WeightBean>
            TLog.error("mList+="+Gson().toJson(mList))
            if (mList.isNullOrEmpty()) {
                tvWeightRecord.visibility = View.GONE
               // ryWeight.visibility = View.GONE
                tvTime.text = ""
                tvWeight.text = "--"
                tvBMI.text = "--"
                tvLastWeight.text = "--"
                tvNowWeight.text = "--"
            } else {
                tvWeightRecord.visibility = View.VISIBLE
                ryWeight.visibility = View.VISIBLE
                if (statusGetWeight) {

                } else {
                  //  tvWeightRecord.visibility = View.VISIBLE
                    val mWeightInfo = mList[0]
                    var lastWeight = 0.0
                    tvTime.text = DateUtil.getDate(DateUtil.HH_MM, mWeightInfo.time * 1000)
                    tvWeight.text = HelpUtil.getSpan(mWeightInfo.weight, "kg")
                    tvBMI.text = mWeightInfo.bmi
                    if (mAllList.size <= 1) {
                        lastWeight = 0.0
//                        tvLastWeight.text = "--"
//                        tvNowWeight.text = "--"
                        TLog.error("userInfo.user.weight=++" + userInfo.user.weight)
                        tvLastWeight.text = userInfo.user.weight
                        var nowWeight =
                            (BigDecimal(mWeightInfo.weight.toDouble()).subtract(BigDecimal(userInfo.user.weight.toDouble()))
                                    ).setScale(1, BigDecimal.ROUND_HALF_DOWN)
                        tvNowWeight.text = if(nowWeight.toDouble() == 0.0) "--" else HelpUtil.getSpan(nowWeight.toString(), "kg")
                        TLog.error("直接不走")
                    } else {
                        //mAllList为整个数据库的数据
                        //在我使用find判断相同过后如何找到mAllList的下一个数组数据呢?
                        mAllList.forEachIndexed { index, weightBean ->
                            if (weightBean.time == mList[0].time) {
                                if ((mAllList.size - 1) > index) {
//                                    TLog.error("走 if")
                                    lastWeight = mAllList[index + 1].weight.toDouble()
                                    tvLastWeight.text =
                                        HelpUtil.getSpan(lastWeight.toString(), "kg")
                                    var nowWeight =
                                        (BigDecimal(mWeightInfo.weight.toDouble()).subtract(
                                            BigDecimal(
                                                lastWeight
                                            )
                                        )
                                                ).setScale(1, BigDecimal.ROUND_HALF_DOWN)
                                    tvNowWeight.text =  HelpUtil.getSpan(nowWeight.toString(), "kg")
                                } else {
                                    TLog.error("走 else")
                                    tvLastWeight.text = userInfo.user.weight
                                    var nowWeight =
                                        (BigDecimal(mWeightInfo.weight.toDouble()).subtract(
                                            BigDecimal(
                                                userInfo.user.weight.toDouble()
                                            )
                                        )
                                                ).setScale(1, BigDecimal.ROUND_HALF_DOWN)
                                    tvNowWeight.text = if(tvLastWeight.text.equals("--") && tvBMI.text.equals("--")) "--" else  HelpUtil.getSpan(nowWeight.toString(), "kg")
                                    //  tvLastWeight.text = "--"
                                }
                            }
                        }
                    }

                }
            }
        } else if (position == 1) {
            tvNowWeightTitle.text = "与上周相比"
            tvLastWeightTitle.text = "本周日均"
            tvBMITitle.text = "本周BMI均值"
            mList = sDao.getTimeList(
                weekCalendar.timeInMillis / 1000,
                (weekCalendar.timeInMillis + 86400000L * 7 - 1000) / 1000
            ) as ArrayList<WeightBean>
            TLog.error("输出mlist++" + Gson().toJson(mList))
            if (mList.size > 0)
                tvWeightRecord.visibility = View.VISIBLE
            else
                tvWeightRecord.visibility = View.GONE
            var lastWeekList: List<WeightBean> = sDao.getTimeList(
                (weekCalendar.timeInMillis - 86400000L * 7) / 1000,
                (weekCalendar.timeInMillis - 1000) / 1000
            )
            var bmiCount = 0.0
            var weightCount = 0.0
            var lastCount = 0.0
            var weekCount = 1
            var weekLastCount = 1
            mList.forEach {
                bmiCount += it.bmi.toDouble()
                weightCount += it.weight.toDouble()
            }
            lastWeekList.forEach {
                lastCount += it.weight.toDouble()
            }
            if (mList.isNotEmpty())
                weekCount = mList.size
            if (lastWeekList.isNotEmpty())
                weekLastCount = lastWeekList.size
            weightCount / weekLastCount

            var lastWeek = setNumber(weightCount / weekCount, 1)?.subtract(
                setNumber(lastCount / weekLastCount, 1)
            )
            TLog.error("weightCount / weekCount+" + weightCount / weekCount+ " "+(weightCount == 0.0))
            TLog.error("lastCount / weekLastCount+" + lastCount / weekLastCount)
            tvBMI.text = if(bmiCount == 0.0) "--" else setNumber(bmiCount / weekCount, 1).toString()  //对得上
            //  tvWeight.text = HelpUtil.setNumber(weightCount / weekCount, 1).toString()  //对得上

            tvLastWeight.text =
               if(weightCount / weekCount == 0.0) "--" else HelpUtil.getSpan(setNumber(weightCount / weekCount, 1).toString(),"kg")

            if (lastWeekList.isNotEmpty())
                tvNowWeight.text = if(tvBMI.text.equals("--") && tvLastWeight.text.equals("--")) "--" else HelpUtil.getSpan(lastWeek.toString(), "kg")
            else
                tvNowWeight.text = if(tvBMI.text.equals("--") && tvLastWeight.text.equals("--")) "--" else HelpUtil.getSpan(lastWeek.toString(), "kg")
        } else if (position == 2) {
            tvNowWeightTitle.text = "与上月相比"
            tvLastWeightTitle.text = "本月日均"
            tvBMITitle.text = "本月BMI均值"
            val day = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            var lastMonthCalendar = DateUtil.getLastMonthFirstDate(monthCalendar)
            TLog.error("lastMonthCalendar+=" + lastMonthCalendar.timeInMillis)
            val lastDay = lastMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            TLog.error("monthCalendar+=" + monthCalendar.timeInMillis)
            TLog.error("lastMonthCalendar+=" + lastMonthCalendar.timeInMillis)
            mList = sDao.getTimeList(
                monthCalendar.timeInMillis / 1000,
                (monthCalendar.timeInMillis + (86400000 * day.toLong()) - 1000) / 1000
            ) as ArrayList<WeightBean>
            TLog.error("当月++" + Gson().toJson(mList))
            if (mList.size > 0)
                tvWeightRecord.visibility = View.VISIBLE
            else
                tvWeightRecord.visibility = View.GONE
            val lastWeekList: List<WeightBean> = sDao.getTimeList(
                (monthCalendar.timeInMillis - (86400000 * lastDay.toLong())) / 1000,
                (monthCalendar.timeInMillis - 1000) / 1000
            )
            TLog.error("上个月++" + Gson().toJson(lastWeekList))
            var bmiCount = 0.0
            var weightCount = 0.0
            var lastCount = 0.0
            var weekCount = 1
            var weekLastCount = 1
            mList.forEach {
                bmiCount += it.bmi.toDouble()
                weightCount += it.weight.toDouble()
            }
            lastWeekList.forEach {
                lastCount += it.weight.toDouble()
            }
            if (mList.isNotEmpty())
                weekCount = mList.size
            if (lastWeekList.isNotEmpty())
                weekLastCount = lastWeekList.size
            val lastWeek = setNumber(weightCount / weekCount, 1)?.subtract(
                setNumber(lastCount / weekLastCount, 1)
            )
            tvBMI.text = if(bmiCount == 0.0) "--" else setNumber(bmiCount / weekCount, 1).toString()  //对得上

            tvLastWeight.text = if(weightCount / weekCount == 0.0) "--" else  HelpUtil.getSpan(
                setNumber(weightCount / weekCount, 1).toString(),
                "kg"
            )


            if (lastWeekList.isNotEmpty()){
                if (lastWeek != null) {
                    tvNowWeight.text = HelpUtil.getSpan(lastWeek.toString(), "kg")
                }
            }else{
                tvNowWeight.text = if(tvNowWeight.text.equals("--") && tvBMI.text.equals("--")) "--" else HelpUtil.getSpan("0.0", "kg")
            }


        } else if (position == 3) {
            tvNowWeightTitle.text = "与上年相比"
            tvLastWeightTitle.text = "本年月均"
            tvBMITitle.text = "本年BMI均值"
            val day = yearCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)
            var lastYearCalendar = DateUtil.getLastYearFirstDate(yearCalendar)
            val lastDay = lastYearCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)
            mList = sDao.getTimeList(
                lastYearCalendar.timeInMillis / 1000,
                (lastYearCalendar.timeInMillis + (86400000L * day.toLong()) - 1000) / 1000
            ) as ArrayList<WeightBean>
            if (mList.size > 0)
                tvWeightRecord.visibility = View.VISIBLE
            else
                tvWeightRecord.visibility = View.GONE
            var lastWeekList: List<WeightBean> = sDao.getTimeList(
                (yearCalendar.timeInMillis - (86400000L * lastDay.toLong())) / 1000,
                (yearCalendar.timeInMillis - 1000) / 1000
            )
//            TLog.error("上个年++" + Gson().toJson(lastWeekList))
            var bmiCount = 0.0
            var weightCount = 0.0
            var lastCount = 0.0
            var weekCount = 1
            var weekLastCount = 1
            mList.forEach {
                bmiCount += it.bmi.toDouble()
                weightCount += it.weight.toDouble()
            }
            lastWeekList.forEach {
                lastCount += it.weight.toDouble()
            }
            if (mList.isNotEmpty())
                weekCount = mList.size
            if (lastWeekList.isNotEmpty())
                weekLastCount = lastWeekList.size
            var lastWeek = setNumber(weightCount / weekCount, 1)?.subtract(
                setNumber(lastCount / weekLastCount, 1)
            )
            tvBMI.text = if(bmiCount == 0.0) "--" else setNumber(bmiCount / weekCount, 1).toString()  //对得上

            tvLastWeight.text =if(weightCount / weekCount == 0.0) "--" else  HelpUtil.getSpan(setNumber(weightCount / weekCount, 1).toString(), "kg" )

            if (lastWeekList.isNotEmpty()){
                if (lastWeek != null) {
                    tvNowWeight.text = if(tvNowWeight.text.equals("--") && tvBMI.text.equals("--")) "--" else HelpUtil.getSpan(lastWeek.toString(), "kg")
                }
            }else{
                tvNowWeight.text = if(tvNowWeight.text.equals("--") && tvBMI.text.equals("--")) "--" else HelpUtil.getSpan("0.0", "kg")
            }


        }



        mWeightAdapter.data.clear()
        mWeightAdapter.addData(mList)
        mWeightAdapter.notifyDataSetChanged()

    }


    var yearStatus = false
    var mothStatus = false
    var weekStatus = false
    var dayStatus = false
    private fun onClickListener() {
        img_left.setOnClickListener(this)
        img_right.setOnClickListener(this)
        tvAdd.setOnClickListener(this)
        slidingTab.setOnTabSelectListener(this)
    }

    private fun chartInitView() {
        chart = chart1
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(60)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.setOnChartValueSelectedListener(this)
        chart.isScaleYEnabled = false
        chart.legend.isEnabled = false  //色块不显示
        chart.setScaleEnabled(false)
        //chart.viewPortHandler.setMaximumScaleX(4f)
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        lateinit var timeMatter: IAxisValueFormatter
        //  timeMatter = DeviceSportAxisValueFormatter(chart, position)
        TLog.error("position+="+position)
        when (position) {
            1 -> {
                timeMatter =
                    DeviceSportAxisValueFormatter(chart, position, weekCalendar.timeInMillis)
                //  xAxis.axisMaximum = 6f
                xAxis.granularity = 1f
                xAxis.labelCount = 7
                tvHours.visibility = View.GONE
            }
            2 -> {
                timeMatter =
                    DeviceSportAxisValueFormatter(chart, position, monthCalendar.timeInMillis)
                xAxis.granularity = 5f
                xAxis.labelCount = 6
                tvHours.visibility = View.GONE
            }
            3 -> {
                timeMatter = DeviceSportAxisValueFormatter(chart, position)
                xAxis.granularity = 1f
                xAxis.labelCount = 12
                tvHours.visibility = View.GONE
            }
        }
        //隐藏左边Y轴
        xAxis.setDrawGridLines(false)
        if (timeMatter != null)
            xAxis.valueFormatter = timeMatter
        val mLAxis = chart.axisLeft
        mLAxis.axisMinimum = 30f
        mLAxis.isEnabled = false//隐藏左边Y轴
        mLAxis.setDrawZeroLine(false)
        val mRAxis = chart.axisRight
        mRAxis.axisMinimum = 30f
        mRAxis.axisLineColor = Color.WHITE
        mRAxis.zeroLineColor = resources.getColor(R.color.color_view)
        mRAxis.gridColor = resources.getColor(R.color.color_view)
        mRAxis.setDrawZeroLine(false)
        mRAxis.textColor=resources.getColor(R.color.sub_text_color)
        xAxis.textColor=resources.getColor(R.color.sub_text_color)
       // val mv = WeightMarkerView(this, R.layout.custom_marker_view)
       // mv.chartView = chart
      //  chart.marker = mv
        setData()
    }
    private var values =   ArrayList<Entry>()

    private fun setData() {
        var valuesZero =   ArrayList<Entry>()
        values = ArrayList()
        var data = LineData()

        when (position) {

            1 -> {
                var weight = 0f
                var size = 0
                for (i in 0 until 7) {
                    var weekList: List<WeightBean> = sDao.getTimeList(
                        (weekCalendar.timeInMillis + (86400L * i * 1000)) / 1000,
                        (weekCalendar.timeInMillis + (86400L * (i + 1) * 1000)) / 1000
                    )
                    valuesZero.add(BarEntry(i.toFloat(), 0f))
                    var weightCount = 0.0
                    for (j in weekList.indices) {
                        weightCount += weekList[j].weight.toDouble()
                        TLog.error("weightCount==" + weightCount)
                    }
                    if (weightCount <= 0) {
                        if (values.isNotEmpty()) {
                            val set1 = LineDataSet(values, Math.random().toString())
                            set1.color = resources.getColor(R.color.color_main_green)
                            set1.setDrawValues(true)
                            set1.setDrawCircles(true)//设置画圆点
                            set1.setCircleColor(resources.getColor(R.color.color_main_green))
                            set1.setDrawCircleHole(false)
                            set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
                            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER_Y_ZERO
                            data.addDataSet(set1)
                        }
                    } else {
                        size = i
                        weight = weightCount.toFloat() / weekList.size
                        values.add(BarEntry(i.toFloat(), weight))
                    }

                }
                TLog.error("==weight==" + weight+" 是否未0="+(weight.toString() == "0.0"))
                tvTime.text = DateUtil.getDate(
                    DateUtil.MM_AND_DD_STRING,
                    DateUtil.getWeekFirstDate(weekCalendar).timeInMillis + (size) * 86400000L
                )
                if(weight.toString() == "0.0"){
                    tvWeight.text = "--"
                }else{
                    tvWeight.text =  HelpUtil.getSpan("日均", setNumber(weight.toDouble(), 1).toString(), "kg")
                }

                // update()
            }
            2 -> {
                var weight = 0f
                var size = 0
                val day = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                TLog.error("最大天数++" + day)
                for (i in 0 until day) {

                    var dayList: List<WeightBean> = sDao.getTimeList(
                        (monthCalendar.timeInMillis + (86400000L * i)) / 1000,
                        (monthCalendar.timeInMillis + (86400000L * (i + 1))) / 1000
                    )
                    TLog.error("查询==" + Gson().toJson(dayList))
                    valuesZero.add(BarEntry(i.toFloat(), 0f))
                    var weightCount = 0.0
                    for (j in dayList.indices) {
                        weightCount += dayList[j].weight.toDouble()
                        TLog.error("weightCount==" + weightCount)
                    }


                        if (weightCount <= 0) {
                        if (values.isNotEmpty()) {
                            val set1 = LineDataSet(values, Math.random().toString())
                            set1.color = resources.getColor(R.color.color_main_green)
                            set1.setDrawValues(true)
                            set1.setDrawCircles(true)//设置画圆点
                            set1.setCircleColor(resources.getColor(R.color.color_main_green))
                            set1.setDrawCircleHole(false)
                            set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
                            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                            data.addDataSet(set1)
                        }
                    } else {
                        size = i
                        weight = weightCount.toFloat() / dayList.size
                        values.add(BarEntry(i.toFloat(), weight))
                    }
                }
                tvTime.text = DateUtil.getDate(
                    DateUtil.MM_AND_DD_STRING,
                    DateUtil.getMonthFirstDate(monthCalendar).timeInMillis + (size) * 86400000L
                )

                Log.e(tags,"----日剧="+(( weight.toString() == "0.0")))

                tvWeight.text =
                   if( weight.toString() == "0.0")"--" else HelpUtil.getSpan("日均", setNumber(weight.toDouble(), 1).toString(), "kg")

            }
            3 -> {
                var yearList = sDao.getList(
                    DateUtil.getDate(DateUtil.YYYY, yearCalendar)
                )
                TLog.error("年==" + Gson().toJson(yearList))
                var weight = 0.0
                var month = 0
                val monthList: ArrayList<YearBean> = ArrayList()
                for (i in 0 until 12) {
                    val yearBean = YearBean(yearCalendar.get(Calendar.YEAR), i, 0.0)
                    monthList.add(yearBean)
                  //  values.add(BarEntry(i.toFloat(), 0F))
                    valuesZero.add(BarEntry(i.toFloat(), 0f))
                }
               var hasMapYear=HashMap<Float,Float>()
                for (motionListBean in yearList) {
                    val calendarYear =
                        DateUtil.convertLongToCalendar(motionListBean.time * 1000)
                    var find = monthList.find { yearBean ->
                        yearBean.year == calendarYear.get(Calendar.YEAR) &&
                                yearBean.month == calendarYear.get(Calendar.MONTH)
                    }
                    var indexOf=0
                    var avgCount=0f
                    if (find != null) {
                          indexOf = monthList.indexOf(find)
                        find.weight += motionListBean.weight.toDouble()
                        find.totalStep++
                        weight = find.weight / find.totalStep
                        TLog.error("weight+="+weight+"find.weight+="+find.weight+"find.totalStep+="+find.totalStep)
                        month = find.month
                        val avgWeight: BigDecimal = BigDecimal(weight).setScale(
                            2,
                            BigDecimal.ROUND_HALF_DOWN
                        )
                        avgCount=avgWeight.toFloat()
                    }
                    hasMapYear[indexOf.toFloat()]=avgCount

                }
                for ((time,weight) in hasMapYear) {
                    values.add(BarEntry(time, weight))
                }
                tvTime.text = "${month + 1}月"
                tvWeight.text = if(weight == 0.0) "--" else HelpUtil.getSpan("月均", setNumber(weight, 1).toString(), "kg")
            }
        }
        update()
        if (values.isNotEmpty()) {
            val set1 = LineDataSet(values, "")
            val setZero = LineDataSet(valuesZero, "")
            set1.color = resources.getColor(R.color.color_main_green)
            set1.setDrawValues(true)
            set1.setDrawCircles(true)//设置画圆点
            set1.setCircleColor(resources.getColor(R.color.color_main_green))
            set1.setDrawCircleHole(false)
            set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            //0为了让线连起来好看
            setZero.setDrawValues(false)
            setZero.setDrawCircles(false)//设置画圆点
            setZero.setCircleColor(resources.getColor(R.color.color_main_green))
            setZero.setDrawCircleHole(false)
            setZero.setDrawValues(false)//设置缩放一定程度以后的展示文字
            setZero.color = resources.getColor(R.color.color_main_green)
            setZero.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            data = LineData(set1,setZero)
        }
        chart.data = data
        chart.invalidate()
    }

    var date: String = ""
    var calendarType: Calendar? = null
    private fun setTitleDateData() {
        when (position) {
            0 -> {
                calendarType = dayCalendar
            }
            1 -> {
                calendarType = weekCalendar
            }
            2 -> {
                calendarType = monthCalendar
            }
            3 -> {
                calendarType = yearCalendar
            }
        }
//        TLog.error("calendar++${calendarType?.timeInMillis}")
        //   timeDialog = calendar?.timeInMillis

        date = DateUtil.getDate(DateUtil.YYYY_MM_DD_AND, calendarType)
        mViewModel.getWeight(
            position.toString(), DateUtil.getDate(
                DateUtil.YYYY_MM_DD,
                calendarType
            )
        )
        when (position) {
            1 -> {
                date += "-" + calendarType?.timeInMillis?.plus(86400 * 6 * 1000L)?.let {
                    DateUtil.getDate(
                        DateUtil.MM_AND_DD_STRING,
                        it
                    )
                }
            }
            2 -> {
                date += "-" + calendarType?.timeInMillis?.plus(
                    86400L * (calendarType?.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                    )!! - 1) * 1000L
                )?.let {
                    DateUtil.getDate(
                        DateUtil.MM_AND_DD_STRING,
                        it
                    )
                }
            }
            3 -> {
                date += "-" + DateUtil.getDate(
                    DateUtil.MM_AND_DD_STRING,
                    DateUtil.getYearLastDate(calendarType)
                )
            }
        }
        when (position) {
            0 -> {
                if (DateUtil.equalsToday(calendarType)) {
                    tvAdd.visibility = View.VISIBLE
                    img_right.visibility = View.INVISIBLE
                } else {
                    tvAdd.visibility = View.GONE
                    img_right.visibility = View.VISIBLE
                }
                update()
            }
            1 -> {
                if (DateUtil.getWeekLastDate(calendarType).timeInMillis >= timeDialog)
                    img_right.visibility = View.INVISIBLE
                else
                    img_right.visibility = View.VISIBLE
            }
            2 -> {
                if (calendarType?.get(Calendar.MONTH)!! >= DateUtil.getCurrentCalendar()
                        .get(Calendar.MONTH)
                ) {
                    if (calendarType?.get(Calendar.YEAR)!! >= DateUtil.getCurrentCalendar()
                            .get(Calendar.YEAR)
                    )
                        img_right.visibility = View.INVISIBLE
                    else
                        img_right.visibility = View.VISIBLE
                } else {
                    img_right.visibility = View.VISIBLE
                }
            }
            3 -> {
                if (calendarType?.get(Calendar.YEAR)!! >= DateUtil.getCurrentCalendar()
                        .get(Calendar.YEAR)
                )
                    img_right.visibility = View.INVISIBLE
                else
                    img_right.visibility = View.VISIBLE
//                TLog.error("展示 date+$date")
            }
        }
        tvTypeTime.text = date
    }

    private fun refreshCurrentSelectedDateData() {
        setTitleDateData()
        if (position == 0)
            chart1.visibility = View.GONE
        else
            chartInitView()
    }

    private fun setAdapter() {
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        ryWeight.layoutManager = mLinearLayoutManager
        mWeightAdapter = WeightAdapter(mList)
        ryWeight.adapter = mWeightAdapter
        mWeightAdapter.setOnDelListener(object : WeightAdapter.onSwipeListener {
            override fun onDel(pos: Int) {
                if (pos >= 0) {
                    if (!HelpUtil.netWorkCheck(this@WeightActivity)) {
                        if (mWeightAdapter.data[pos].value.equals("1")) {
                            ShowToast.showToastLong(getString(R.string.err_network_delete))
                            return
                        }
                    }
                    showWaitDialog("删除体重中...")
                    var value = HashMap<String, String>()
                    value["createTime"] = mWeightAdapter.data[pos].time.toString()
                    value["type"] = position.toString()
                    sDao.deleteTime(mWeightAdapter.data[pos].time)
                    mWeightAdapter.notifyItemRemoved(pos)
                    mAllList = sDao.getAllByDateDesc() as ArrayList<WeightBean>//重新获取
                    if (HelpUtil.netWorkCheck(this@WeightActivity) && mWeightAdapter.data[pos].value == "1")
                        mViewModel.deleteWeight(this@WeightActivity, value)
                    else
                        hideWaitDialog()
                    update()
                    if (mWeightAdapter.data.size > 0)
                        homeCard(mWeightAdapter.data[0].weight)//如果最新一个被删除的时候 要更新首页
                    else
                        homeCard("")//如果最新一个被删除的时候 要更新首页
                }
            }

            override fun onClick(pos: Int) {
                TLog.error(
                    "mWeightAdapter.data[pos].weight==" + mWeightAdapter.data[pos].weight
                            + "==" + mWeightAdapter.data[pos].time.toString()
                )
                if (!HelpUtil.netWorkCheck(this@WeightActivity)) {
                    ShowToast.showToastLong("请开启网络,进行上传")
                    return
                }
                showWaitDialog("上传体重中...")
                var mList: ArrayList<HashMap<String, String>> = arrayListOf()
                var value = HashMap<String, String>()
                timeDialog = mWeightAdapter.data[pos].time * 1000L
                value["weight"] = mWeightAdapter.data[pos].weight
                value["createTime"] = mWeightAdapter.data[pos].time.toString()
                mList.add(value)
                mViewModel.setWeight(this@WeightActivity, Gson().toJson(mList))
                mWeightAdapter.notifyItemRemoved(pos)
            }
        })
    }

    /**
     * 设置标题日期相关数据
     */
    private var timeDialog: Long = System.currentTimeMillis()//默认为当天时间
    private var calendarDialog: Calendar? = null
    private fun dialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_calendar
            dimAmount = 0.3f
            isFullHorizontal = false
            isFullVerticalOverStatusBar = false
            animStyle = R.style.AlphaEnterExitAnimation
            convertListenerFun { holder, dialog ->
                var calenderView = holder.getView<CalendarView>(R.id.calenderView)
                calenderView?.date = dayCalendar.timeInMillis

                calenderView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
                    if (calendarDialog == null) {
                        calendarDialog = dayCalendar
                    }
                    calendarDialog?.set(year, month, dayOfMonth, 0, 0)
                    var calendarTime = calendarDialog?.timeInMillis
                    TLog.error("calendarTime==" + calendarTime)
                    TLog.error("timeDialog==" + timeDialog)
                    if (calendarTime!! > timeDialog!!) {
                        ShowToast.showToastLong("不可选择大于今天的日期")
                        return@setOnDateChangeListener
                    }
                    dayCalendar?.timeInMillis = calendarTime!!
                    refreshCurrentSelectedDateData()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_left -> {
                when (position) {
                    0 ->
                        dayCalendar.add(Calendar.DAY_OF_MONTH, -1)
                    1 -> {
                        weekCalendar.add(Calendar.DAY_OF_MONTH, -7)
                    }
                    2 -> {
                        monthCalendar.add(Calendar.MONTH, -1)
                    }
                    3 -> {
                        yearCalendar.add(Calendar.YEAR, -1)
                    }
                }
                refreshCurrentSelectedDateData()
            }
            R.id.img_right -> {
                when (position) {
                    0 ->
                        dayCalendar.add(Calendar.DAY_OF_MONTH, 1)
                    1 -> {
                        //XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, +7)
                        weekCalendar.add(Calendar.DAY_OF_MONTH, +7)
                    }
                    2 -> {
                        monthCalendar.add(Calendar.MONTH, 1)

                    }
                    3 -> {
                        yearCalendar.add(Calendar.YEAR, 1)
                    }
                }
                refreshCurrentSelectedDateData()
            }
            R.id.tvAdd -> {

                pvCustomOptions?.show()
            }
        }
    }

    private var pvCustomOptions: OptionsPickerView<Any>? = null
    private var cardWeightItem: ArrayList<CardWeightBean> = ArrayList()
    private val options2Items = ArrayList<ArrayList<String>>()
    private fun initCustomOptionPicker() { //条件选择器初始化，自定义布局
        pvCustomOptions = OptionsPickerBuilder(
            this
        ) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置
            timeDialog = System.currentTimeMillis() //重置时间戳
            val item = cardWeightItem[options1]
            mDeviceInformationBean.sex = item.id
            val weight = item.pickerViewText + options2Items[options1][option2]
            TLog.error("weight.toDouble()+" + weight.toDouble())
            if (!mAllList.isNullOrEmpty() && abs(mAllList[0].weight.toDouble() - weight.toDouble()) > 5) {
                TLog.error("mAllList[0].weight.toDouble()+" + mAllList[0].weight.toDouble())

                TLog.error("===" + weight)
                sureDialog(weight)
                return@OptionsPickerBuilder
            }
            setWeight(weight)
//            sureUpdate(weight)
        }
            .setLayoutRes(
                R.layout.pickerview_custom_options_weight
            ) { v ->
                val tvSubmit =
                    v.findViewById<TextView>(R.id.tv_finish)
                val ivCancel =
                    v.findViewById<TextView>(R.id.iv_cancel)
                tvSubmit.setOnClickListener {
                    pvCustomOptions?.returnData()
                    pvCustomOptions?.dismiss()
                }
                ivCancel.setOnClickListener { pvCustomOptions?.dismiss() }
            }
            .isDialog(true)
            .setCyclic(cardWeightItem.size > 2, false, false)
            .isRestoreItem(false)
            .setSelectOptions(cardWeightItem.size / 21, 1)
            .setTextColorCenter(resources.getColor(R.color.color_main_green))
            .setOutSideCancelable(false)
            .setContentTextSize(18)
            .build()
        pvCustomOptions?.setPicker(
            cardWeightItem as List<Any>?,
            options2Items as List<MutableList<Any>>?
        )

        val mDialog: Dialog = pvCustomOptions?.dialog!!
        val params =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        params.leftMargin = 0
        params.rightMargin = 0
        pvCustomOptions?.let { it.dialogContainerLayout.layoutParams = params }
        val dialogWindow = mDialog.window
        if (dialogWindow != null) {
            dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim) //修改动画样式
            dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f)
        }
    }

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
//        TLog.error("onValueSelected++" + e.x + ",  y==" + e.y + ",  Highlight+=" + h.x)
        if (position == 1) {
            tvTime.text = DateUtil.getDate(
                DateUtil.MM_AND_DD_STRING,
                DateUtil.getWeekFirstDate(weekCalendar).timeInMillis + 86400000 * e.x.toInt()
                    .toLong()
            )
            if (h.y > 0) {
                val avgWeight: BigDecimal =
                    BigDecimal(h.y.toDouble()).setScale(1, BigDecimal.ROUND_HALF_DOWN)
                Log.e(tags, "------1---avgWeight=$avgWeight")
                tvWeight.text = if( avgWeight.toString() == "0.0")"--" else HelpUtil.getSpan("日均", avgWeight.toString(), "kg")
            } else
                tvWeight.text = "--"
        } else if (position == 2) {
            tvTime.text = DateUtil.getDate(
                DateUtil.MM_AND_DD_STRING,
                DateUtil.getMonthFirstDate(monthCalendar).timeInMillis + 86400000 * (e.x.toInt()).toLong()
            )
            if (h.y > 0) {
                val avgWeight: BigDecimal =
                    BigDecimal(h.y.toDouble()).setScale(1, BigDecimal.ROUND_HALF_DOWN)
                tvWeight.text = HelpUtil.getSpan("日均", avgWeight.toString(), "kg")
                Log.e(tags, "---2------avgWeight=$avgWeight")
            } else
                tvWeight.text = "--"
        } else if (position == 3) {
            tvTime.text = (1 + e.x.toInt()).toString() + "月"
            if (h.y > 0) {
                val avgWeight: BigDecimal =
                    BigDecimal(h.y.toDouble()).setScale(1, BigDecimal.ROUND_HALF_DOWN)
                Log.e(tags, "---3------avgWeight=$avgWeight")
                tvWeight.text = if(avgWeight.toDouble() == 0.0) "--" else HelpUtil.getSpan("月均", avgWeight.toString(), "kg")
            } else
                tvWeight.text = "--"
        }
    }

    private fun sureDialog(weight: String) {
        supportFragmentManager?.let {
            newGenjiDialog {
                layoutId = R.layout.dialog_delete
                dimAmount = 0.3f
                isFullHorizontal = true
                animStyle = R.style.AlphaEnterExitAnimation
                convertListenerFun { holder, dialog ->
                    var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                    var dialogSet = holder.getView<TextView>(R.id.dialog_confirm)
                    var dialogContent = holder.getView<TextView>(R.id.dialog_content)
                    dialogContent?.text = "请再确认是否正确。但以用户最终确定为准，我司仅作提示。"
                    dialogSet?.setOnClickListener {
                        // sureUpdate(weight)
                        setWeight(weight)
                        dialog.dismiss()
                    }
                    dialogCancel?.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }.showOnWindow(it)
        }
    }

    var setWeight = "0"
    fun setWeight(weight: String) {
        var mList: ArrayList<HashMap<String, String>> = arrayListOf()
        var value = HashMap<String, String>()
        value["weight"] = weight
        value["createTime"] = (timeDialog / 1000).toString()
        mList.add(value)
        mViewModel.setWeight(this, Gson().toJson(mList))
        setWeight = weight
    }

    fun sureUpdate(weight: String, getBmi: String = "", type: String = "0") {
        val height = (mDeviceInformationBean.height.toDouble() / 100).pow(2.0)
        //   TLog.error("==mDeviceInformationBean.height+=${mDeviceInformationBean.height}")
        TLog.error("==weight+=" + weight)
        TLog.error("==height+=$height")
        var setBmi = getBmi
        if (setBmi.isNullOrEmpty()) {
            val bmi: BigDecimal = BigDecimal(weight).divide(
                BigDecimal(height).setScale(
                    2,
                    BigDecimal.ROUND_HALF_DOWN
                ), 1
            ).setScale(1, BigDecimal.ROUND_HALF_DOWN)
            setBmi = bmi.toString()
        }
        tvBMI.text = if(setBmi == "0") "--" else setBmi
        tvWeight.text = HelpUtil.getSpan(if(weight == "0") "--" else weight, "kg")
        var lastWeight = 0.0
        sDao.insert(
            WeightBean(
                timeDialog / 1000, DateUtil.getDate(
                    DateUtil.YYYY_MM_DD_HH_MM_SS,
                    timeDialog
                ), weight, setBmi, type
            )
        )
        mAllList = sDao.getAllByDateDesc() as ArrayList<WeightBean>
        TLog.error("mAllList+=" + mAllList.size)
        if (mAllList.size <= 1) {
            lastWeight = 0.0
            tvLastWeight.text = "--"
        } else {
            mList = sDao.getList(
                DateUtil.getDate(
                    DateUtil.YYYY_MM_DD,
                    dayCalendar
                )
            ) as ArrayList<WeightBean>
            TLog.error("mList+="+Gson().toJson(mList))
            mAllList.forEachIndexed { index, weightBean ->
                if (weightBean.time == mList[0].time) {
                    if ((mAllList.size - 1) > index) {
                        lastWeight = mAllList[index + 1].weight.toDouble()

                        tvLastWeight.text = if(lastWeight == 0.0) "--" else HelpUtil.getSpan(lastWeight.toString(), "kg")
                    } else {
                        tvLastWeight.text = "--"
                    }
                }
            }
        }
        homeCard(weight)
        mDeviceInformationBean.setWeight(weight.toFloat())
        Hawk.put(Config.database.PERSONAL_INFORMATION, mDeviceInformationBean)
        BleWrite.writeDeviceInformationCall(mDeviceInformationBean, true)
        update()
    }

    private fun homeCard(weight: String) {
        if (mHomeCardBean.list != null && mHomeCardBean.list.size > 0) {
            var cardList = mHomeCardBean.list
            cardList.forEachIndexed { index, addCardDTO ->
                if (addCardDTO.type == 7) {
                    mHomeCardBean.list[index].endTime =
                        System.currentTimeMillis() / 1000
                    mHomeCardBean.list[index].data =
                        "$weight"
                    // mHomeCardBean.list[index].dayContentString="公斤"
                    mHomeCardBean.list[index].describe =
                        DateUtil.getDate(DateUtil.MM_AND_DD, System.currentTimeMillis())
                    Hawk.put(Config.database.HOME_CARD_BEAN, mHomeCardBean)
                    SNEventBus.sendEvent(Config.eventBus.BLOOD_PRESSURE_RECORD)
                }
            }
        }
    }

    override fun onTabSelect(position: Int) {
        this.position = position
        when (this.position) {
            0 -> {
                if (!dayStatus) {
                    dayStatus = true
                }
                chart1.visibility = View.GONE
                setTitleDateData()
            }
            1 -> {
                chart1.visibility = View.VISIBLE
                if (!weekStatus) {
                    weekStatus = true
                    weekCalendar = DateUtil.getWeekFirstDate(Calendar.getInstance())
                }
                setTitleDateData()
            }
            2 -> {
                chart1.visibility = View.VISIBLE
                if (!mothStatus) {
                    mothStatus = true
                }
                setTitleDateData()
            }
            3 -> {
                chart1.visibility = View.VISIBLE
                if (!yearStatus) {
                    yearStatus = true
                }
                setTitleDateData()
            }
        }
        if (this.position == 0) {
            titleBar.showActionImage(true)
            update()//单独为0调用一次
        } else {
            titleBar.showActionImage(false)
            tvAdd.visibility = View.GONE
            chartInitView()
        }
    }

    override fun onTabReselect(position: Int) {
    }
}

