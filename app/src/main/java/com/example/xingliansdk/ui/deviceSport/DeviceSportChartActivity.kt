package com.example.xingliansdk.ui.deviceSport

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.WeekBean
import com.example.xingliansdk.bean.YearBean
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.bean.room.MotionListBean
import com.example.xingliansdk.bean.room.MotionListDao
import com.example.xingliansdk.custom.MyMarkerView
import com.example.xingliansdk.network.api.dailyActiveBean.DailyActiveModel
import com.example.xingliansdk.network.api.dailyActiveBean.DailyActiveVoBean
import com.example.xingliansdk.utils.DeviceSportAxisValueFormatter
import com.example.xingliansdk.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.view.DateUtil.minusTime
import com.example.xingliansdk.widget.TitleBarLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Fill
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import kotlinx.android.synthetic.main.activity_device_sport_chart.*
import me.hgj.jetpackmvvm.network.NetworkUtil
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 计步详细数据页面
 */
class DeviceSportChartActivity : BaseActivity<DailyActiveModel>(), View.OnClickListener,
    OnChartValueSelectedListener {


    private val numberFormat: NumberFormat = DecimalFormat("#,###")
    override fun layoutId() = R.layout.activity_device_sport_chart
    private val mTitles = arrayOf("","","","")
    private lateinit var chart: BarChart
    private var position = 0
    lateinit var motionListDao: MotionListDao
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        chart = chart1
        mTitles[0] = this.resources.getString(R.string.string_day)
        mTitles[1] = this.resources.getString(R.string.string_week)
        mTitles[2] = this.resources.getString(R.string.string_month)
        mTitles[3] = this.resources.getString(R.string.string_year)

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
        tabDate.setTabData(mTitles)



        motionListDao = AppDataBase.instance.getMotionListDao()
        TLog.error("数据+" + Gson().toJson(motionListDao.getAllRoomMotionList()))
        onClickListener()
        setTitleDateData()

        chartInitView()
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.msg.observe(this)
        {

        }
        mViewModel.result.observe(this) { it ->
            TLog.error("查询到的==" + Gson().toJson(it))
            var data = Gson().fromJson(Gson().toJson(it), DailyActiveVoBean::class.java)
            if (data == null || data.list == null || data.list.size <= 0) {
                chartInitView()
                return@observe
            }

            data.list.forEach { list ->
                var stepList: MutableList<Int> = mutableListOf()
                list.data.forEach { data ->
                    stepList.add(data.steps)
                }
                motionListDao.insert(
                    MotionListBean(
                        list.startTimestamp,
                        list.endTimestamp,
                        Gson().toJson(stepList),
                        list.totalSteps,
                        true,
                        list.date
                    ))

            }
            chartInitView()
        }
    }

    var yearStatus = false
    var mothStatus = false
    var weekStatus = false
    var dayStatus = false
    var yearCalendar: Calendar = Calendar.getInstance()
    var monthCalendar: Calendar = Calendar.getInstance()
    var weekCalendar: Calendar = Calendar.getInstance()
    var dayCalendar: Calendar = Calendar.getInstance()

    private fun onClickListener() {
        img_left.setOnClickListener(this)
        img_right.setOnClickListener(this)
        tabDate.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                this@DeviceSportChartActivity.position = position
                chart.highlightValue(null)
                if (position == 0) {
                    titleBar.showActionImage(true)
                } else {
                    titleBar.showActionImage(false)
                }
                when (position) {
                    0 -> {
                        if (!dayStatus) {
                            dayStatus = true
                        }
                        setTitleDateData()
                    }
                    1 -> {
                        if (!weekStatus) {
                            weekStatus = true
                            weekCalendar = DateUtil.getWeekFirstDate(Calendar.getInstance(),0)

                        }
                        setTitleDateData()
                    }
                    2 -> {
                        if (!mothStatus) {
                            mothStatus = true
                            monthCalendar = Calendar.getInstance()
                            monthCalendar?.set(Calendar.DAY_OF_MONTH, 1) //把日期设置为当月第一天
                        }
                        setTitleDateData()
                    }
                    3 -> {
                        if (!yearStatus) {
                            yearStatus = true
                            yearCalendar = Calendar.getInstance()
                            yearCalendar?.set(Calendar.MONTH, 0)
                            yearCalendar?.set(Calendar.DAY_OF_MONTH, 1) //把日期设置为当月第一天
                            yearCalendar.add(Calendar.YEAR, 0)
                        }
                        setTitleDateData()
                    }
                }
               // chartInitView()
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }

    private fun showEmptyChartView(){

        val emptyValues = ArrayList<BarEntry>()
        chart.setNoDataText("")
        var barData : BarDataSet ?= null
        if(chart.data != null && chart.data.dataSetCount>0){
            barData = chart.data.getDataSetByIndex(0) as BarDataSet?
            barData?.entries = emptyValues
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        }else{
            barData = BarDataSet(emptyValues,"")
            barData.setDrawValues(false)
            barData.highLightColor= resources.getColor(R.color.color_marker)
        }
        val dataSets = ArrayList<IBarDataSet>()
        if (barData != null) {
            dataSets.add(barData)
        }
        val data =
            BarData(dataSets)

        if (position == 1 || position == 3) {
            data.barWidth = 0.3f
        }
        chart.data = data
        chart.setFitBars(true)
//        }
        chart.invalidate()
        tvTotalStep.text = "--"
        tvStep.text = "--"
        chartInitView()
        return
    }






    var mv: MyMarkerView? = null
    private fun chartInitView() {
        TLog.error("chartInitView")
        chart.isNeedRoundBar = true
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setScaleEnabled(true)//设置比列启动
        chart.setBorderWidth(0f)
        chart.setMaxVisibleValueCount(0)
        chart.setPinchZoom(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false
        chart.setDrawBarShadow(false)
        chart.setDrawGridBackground(false)
        chart.setOnChartValueSelectedListener(this)
        chart.viewPortHandler.setMinMaxScaleX(1.0f, 3.0f)
     //   chart.viewPortHandler.setMinMaxScaleY(1.0f, 3.0f)
        chart.isScaleYEnabled = false
        chart.extraBottomOffset = 5f
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineColor =  resources.getColor(R.color.color_view)
        xAxis.axisLineWidth = 1.5f

        lateinit var timeMatter: IAxisValueFormatter
        when (position) {
            0 -> {
                tvHours.visibility = View.VISIBLE
                tvHours.text = "24:00"
                TLog.error("value==" + position)
                timeMatter = DeviceSportAxisValueFormatter(chart, position)
                xAxis.granularity = 12f
                xAxis.labelCount = 5
            }
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
                // xAxis.axisMaximum = 30f
                xAxis.granularity = 5f
                xAxis.labelCount = 6
                tvHours.visibility = View.GONE
            }
            3 -> {
                timeMatter = DeviceSportAxisValueFormatter(chart, position)
                // xAxis.axisMaximum = 12f
                xAxis.granularity = 1f
                xAxis.labelCount = 12
                tvHours.visibility = View.GONE
            }
        }
        //隐藏左边Y轴
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        if (timeMatter != null)
            xAxis.valueFormatter = timeMatter
        val mRAxis = chart.axisRight
        mRAxis.setAxisMinValue(0f)
        mRAxis.isEnabled = false//隐藏右边Y轴
        val mLAxis = chart.axisLeft
        mLAxis.isEnabled = false//隐藏左边Y轴
        mLAxis.setDrawGridLines(false)
        mLAxis.setAxisMinValue(0f)
        mv = MyMarkerView(this, R.layout.custom_marker_view)
        mv?.chartView = chart
        chart.marker = mv
        setData()
    }

    private fun setData() {
        val values = ArrayList<BarEntry>()
        var totalStep = 0F
        if (position == 0) {
            view_2.visibility = View.GONE
            llRight.visibility = View.GONE
        } else {
            view_2.visibility = View.VISIBLE
            llRight.visibility = View.VISIBLE
        }
        var mListSize = 0
        when (position) {
            0 -> {
                totalStep = 0F
                val list: MotionListBean =
                    motionListDao.getDayStep(DateUtil.getDate(DateUtil.YYYY_MM_DD, dayCalendar))
                TLog.error("运动大数据==" + Gson().toJson(list))
                if (list != null && !list.stepList.isNullOrEmpty()) {
                    var stepList =
                        Gson().fromJson<ArrayList<Int>>(list.stepList, ArrayList::class.java)

                    Log.e("计步图表","-------日="+Gson().toJson(stepList))

                    stepList.forEachIndexed { index, any ->
                        values.add(BarEntry(index.toFloat(), any.toString().toFloat()))
                        totalStep += any
                    }
                }
                while (values.size < 48)
                    values.add(BarEntry(values.size.toFloat(), 0F))
                mListSize = 1
            }
            1 -> {
                var weekList: List<MotionListBean> = motionListDao.getTimeStepList(
                    minusTime(weekCalendar.timeInMillis),
                    (minusTime(weekCalendar.timeInMillis) + 86400 * 6)
                )

                Log.e("计步图表","-------周="+Gson().toJson(weekList))


                weekList.forEach {
                    if (it.totalSteps > 0)
                        mListSize++
                }

                TLog.error("长度++" + weekList.size)
                totalStep = 0F
                val weekBean: ArrayList<WeekBean> = ArrayList()
                for (i in 0 until 7) {
                    weekBean.add(
                        WeekBean(
                            minusTime(weekCalendar.timeInMillis) + 86400 * i,
                            0
                        )
                    )
                    values.add(BarEntry(i.toFloat(), 0F))
                }
                for (motionListBean in weekList) {
                    var find = weekBean.find {
                        it.time >= motionListBean.startTime
                                &&
                                it.time <= motionListBean.endTime
                    }
                    if (find != null) {
                        find.totalStep = motionListBean.totalSteps
                        val indexOf: Int = weekBean.indexOf(find)
                        totalStep += find.totalStep
                        values[indexOf] = BarEntry(indexOf.toFloat(), find.totalStep.toFloat())
                    }

                }
            }
            2 -> {
                var monthList: List<MotionListBean> = motionListDao.getList(
                    DateUtil.getDate(DateUtil.YYYY_MM, monthCalendar)
                )
                //    mListSize = monthList.size
                monthList.forEach {
                    if (it.totalSteps > 0)
                        mListSize++
                }
                totalStep = 0F
                val weekBean: ArrayList<WeekBean> = ArrayList()
                val day = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                for (i in 0 until day) {
                    weekBean.add(
                        WeekBean(
                            minusTime(monthCalendar.timeInMillis) + 86400 * i * 1000L,
                            0,
                            DateUtil.getDate(
                                DateUtil.YYYY_MM_DD,
                                monthCalendar.timeInMillis + 86400 * i * 1000L
                            )
                        )
                    )
                    values.add(BarEntry(i.toFloat(), 0F))
                }
                for (motionListBean in monthList) {
                    var find = weekBean.find {
                        it.date == motionListBean.dateTime
                    }
                    if (find != null) {
                        find.totalStep = motionListBean.totalSteps
                        val indexOf: Int = weekBean.indexOf(find)
                        totalStep += find.totalStep
                        values[indexOf] = BarEntry(indexOf.toFloat(), find.totalStep.toFloat())
                    }
                }

                if(values.size == 30){
                    chart.xAxis.granularity = 6f
                    chart.xAxis.setLabelCount(5,true)
                }else if(values.size == 28){
                    chart.xAxis.granularity = 7f
                    chart.xAxis.setLabelCount(4,true)
                }
                else{
                    chart.xAxis.granularity = 5f
                    chart.xAxis.labelCount = 6;
                }

                TLog.error("-------月的天数="+values.size)

            }
            3 -> {
                totalStep = 0F
                var yearList: List<MotionListBean> = motionListDao.getList(
                    DateUtil.getDate(DateUtil.YYYY, yearCalendar)
                )
                // mListSize = yearList.size
                yearList.forEach {
                    if (it.totalSteps > 0)
                        mListSize++
                }
                TLog.error("yearList+=" + Gson().toJson(yearList))
                val monthList: ArrayList<YearBean> = ArrayList()
                for (i in 0 until 12) {
                    val yearBean = YearBean(yearCalendar.get(Calendar.YEAR), i, 0)
                    TLog.error("yearBean++" + Gson().toJson(yearBean))
                    monthList.add(yearBean)
                    values.add(BarEntry(i.toFloat(), 0F))
                }

                for (motionListBean in yearList) {
                    val calendar =
                        DateUtil.convertLongToCalendar((motionListBean.startTime) * 1000)
                    TLog.error("calendar++" + calendar.toString())
                    var find = monthList.find { yearBean ->
                        TLog.error("yearBean.month+" + yearBean.month)
                        TLog.error("calendarYear.get(Calendar.MONTH)+" + calendar.get(Calendar.MONTH))
                        yearBean.year == calendar.get(Calendar.YEAR) && yearBean.month == calendar.get(
                            Calendar.MONTH
                        )
                    }
                    if (find != null) {
                        val indexOf = monthList.indexOf(find)
                        find.totalStep += motionListBean.totalSteps
                        totalStep += motionListBean.totalSteps
                        TLog.error("数据总和步数" + totalStep)
                        values[indexOf] = BarEntry(indexOf.toFloat(), find.totalStep.toFloat())
                    }
                }
                TLog.error("values" + Gson().toJson(values))
            }
        }
        tvTotalStep.text = HelpUtil.getSpan(numberFormat.format(totalStep.toLong()).toString(), resources.getString(R.string.unit_steps), 13)
        if (mListSize <= 0)
            mListSize = 1
        tvStep.text = HelpUtil.getSpan(numberFormat.format((totalStep / mListSize).toLong()).toString()
            , resources.getString(R.string.unit_steps), 13
        )
        //        if (chart.data != null &&
//            chart.data.dataSetCount > 0
//        ) {
//            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
//            set1.values = values
//            chart.data.notifyDataChanged()
//            chart.notifyDataSetChanged()
//        } else {

        var barData : BarDataSet ?= null
        if(chart.data != null && chart.data.dataSetCount>0){
            barData = chart.data.getDataSetByIndex(0) as BarDataSet?
            barData?.entries = values

            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        }else{
            barData = BarDataSet(values,"")
            barData.setDrawValues(false)
            barData.highLightColor= resources.getColor(R.color.color_marker)
            //Color.parseColor("#9AF7FF"),Color.parseColor("#95BFFE"),
            val clor = intArrayOf(Color.parseColor("#1D78FF"),Color.parseColor("#1D78FF"),Color.parseColor("#1D78FF"))
            val fill1 = Fill(clor)
            val arrayFills : List<Fill> = listOf(fill1)
            barData.fills = arrayFills
        }



        val dataSets = ArrayList<IBarDataSet>()
        if (barData != null) {
            dataSets.add(barData)
        }
        val data =
            BarData(dataSets)

        if (position == 1 || position == 3) {
            data.barWidth = 0.3f
        }
        chart.data = data
        chart.setFitBars(true)
//        }

        chart.invalidate()
    }

    var calendarType: Calendar? = null
    private fun setTitleDateData() {
        // var calendar: Calendar? = null
        when (position) {
            0 -> {
                calendarType = dayCalendar
            }
            1 -> {
                calendarType = weekCalendar
            }
            2 -> {
                TLog.error("calendarType++${calendarType?.time}  monthCalendar+=${monthCalendar.time}")
                calendarType = monthCalendar
            }
            3 -> {
                calendarType = yearCalendar
            }
        }
        TLog.error("calendar++${calendarType?.timeInMillis}")
        //   timeDialog = calendar?.timeInMillis





        mViewModel.getDailyActive(
            position.toString(), DateUtil.getDate(
                DateUtil.YYYY_MM_DD,
                calendarType
            )
        )
        var date =  DateUtil.getDate(if(calendarType == yearCalendar) DateUtil.YYYY_AND_MM else DateUtil.YYYY_MM_DD_AND, calendarType)
        when (position) {
            0 ->{
                date = DateUtil.getDate(DateUtil.YYYY_MM_DD_AND,dayCalendar)
            }
            1 -> {
                date += "-" + calendarType?.timeInMillis?.plus(86400 * 6 * 1000L)?.let {
                    DateUtil.getDate(
                        DateUtil.YYYY_MM_DD_AND,
                        it
                    )
                }
            }
            2 -> {
                date += "-" + calendarType?.timeInMillis?.plus(
                    86400 * (calendarType?.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                    )!! - 1) * 1000L
                )?.let {
                    DateUtil.getDate(
                        DateUtil.YYYY_MM_DD_AND,
                        it
                    )
                }
            }
            3 -> {
                date += "-" + DateUtil.getDate(
                    DateUtil.YYYY_AND_MM,
                    DateUtil.getYearLastDate(calendarType)
                )
            }
        }

        when (position) {
            0 -> {
                if (DateUtil.equalsToday(calendarType))
                    img_right.visibility = View.INVISIBLE
                else
                    img_right.visibility = View.VISIBLE
            }
            1 -> {
                // if (DateUtil.equalsToday(calendarType))
                if (DateUtil.getWeekLastDate(calendarType,0).timeInMillis+(5*86400000L) >= timeDialog)
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
                TLog.error("展示 date+$date")
            }
        }
        tvTypeTime.text = date

        if(!NetworkUtil.isNetworkAvailable(this)){
            showEmptyChartView()
            return
        }

    }

    private fun refreshCurrentSelectedDateData() {
        setTitleDateData()
       // chartInitView()
    }

    /**
     * 设置标题日期相关数据
     */
    private var timeDialog: Long = System.currentTimeMillis()//默认为当天时间
    private var calendar: Calendar? = null
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
                    if (calendar == null) {
                        calendar = Calendar.getInstance()
                    }
                    calendar?.set(year, month, dayOfMonth, 0, 0)
                    var calendarTime = calendar?.timeInMillis
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
        }
    }

    override fun onValueSelected(e: Entry, h: Highlight?) {
        TLog.error("点击" + e.y.toInt())
        if (e.y.toInt() <= 0) {
            chart.highlightValue(null)
        } else {
            chart.highlightValue(h)
        }
    }

    override fun onNothingSelected() {
    }
}

