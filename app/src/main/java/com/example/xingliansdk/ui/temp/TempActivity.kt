package com.example.xingliansdk.ui.temp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.PopularScienceAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.bean.room.*
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.network.api.tempView.TempViewModel
import com.example.xingliansdk.network.api.tempView.TemperatureVoBean
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.DateUtil
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
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_bloodoxygen.*
import kotlinx.android.synthetic.main.activity_temp.*
import kotlinx.android.synthetic.main.activity_temp.harts_hrr
import kotlinx.android.synthetic.main.activity_temp.img_left
import kotlinx.android.synthetic.main.activity_temp.img_right
import kotlinx.android.synthetic.main.activity_temp.ryPopularScience
import kotlinx.android.synthetic.main.activity_temp.titleBar
import kotlinx.android.synthetic.main.activity_temp.tvTime
import kotlinx.android.synthetic.main.activity_temp.tvType
import kotlinx.android.synthetic.main.activity_temp.tvTypeTime

import java.math.BigDecimal

import java.util.*
import kotlin.collections.ArrayList

/**
 * 体温详情页面
 */
class TempActivity : BaseActivity<TempViewModel>(), View.OnClickListener,
    OnChartValueSelectedListener {
    private lateinit var mList: ArrayList<Int>
    private lateinit var hartsHrr: BarChart

    //体温
    private lateinit var mTempListDao: TempListDao
    var tempUnit = "℃"
    override fun layoutId() = R.layout.activity_temp
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
//        tempUnit = if(mDeviceInformationBean.temperatureSystem.toInt()==1)
//            "℉"
//        else
//            "℃"
        mList = arrayListOf()
        img_left.setOnClickListener(this)
        img_right.setOnClickListener(this)
//        tvTypeTime.setOnClickListener(this)
        mTempListDao = AppDataBase.instance.getRoomTempListDao()

        var bean = intent.getSerializableExtra("bean") as HomeCardVoBean.ListDTO
        var toDayTime = System.currentTimeMillis()
        if (bean.startTime > 0)
            toDayTime = bean.startTime * 1000L
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar(toDayTime))
        hartsHrr = harts_hrr
        setTitleDateData()
        chartView()
        setPopularAdapter()
    }

    lateinit var mPopularScienceAdapter: PopularScienceAdapter
    private lateinit var mPopularList: MutableList<PopularScienceBean.ListDTO>
    private fun setPopularAdapter() {
        var hasmap = HashMap<String, Any>()
        hasmap["category"] = "6"
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

    var setValueStatus = true  //显示值的设置
    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) {
            hideWaitDialog()
            setValueStatus = true
            var mList = Gson().fromJson(Gson().toJson(it), TemperatureVoBean::class.java)
            if (mList.temperatureVoList == null || mList.temperatureVoList.size <= 0) {
                TLog.error("走本地")
                date?.let { it1 -> getHeart(it1) }
                return@observe
            }
            setValueStatus = false

            var temperatureVoList = mList.temperatureVoList[0]
            var tempList = Gson().toJson(temperatureVoList.data)
            TLog.error("mlist=" + Gson().toJson(mList))
            TLog.error("tempList=" + Gson().toJson(tempList))
            mTempListDao.insert(
                TempListBean(
                    temperatureVoList.startTimestamp,
                    temperatureVoList.endTimestamp,
                    tempList,
                    temperatureVoList.date
                )
            )


            getHeart(temperatureVoList.date)
        }
        mViewModel.msg.observe(this) {
            TLog.error("走本地")
            hideWaitDialog()
            date?.let { it1 ->
                setValueStatus = true//错误的情况都是自己算
                getHeart(it1)
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
            xAxis.textColor = resources.getColor(R.color.sub_text_color) //下面的时间X轴值跟他有关
            xAxis.axisLineColor = Color.WHITE
            xAxis.axisMaximum = 48f
            //  xAxis.axisMinimum=0f
            xAxis.granularity = 12f // 想 弄成 4 hour
            xAxis.labelCount = 4
            xAxis.valueFormatter = timeMatter
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.isEnabled = false
            leftAxis.axisMinimum = 32f
            leftAxis.axisMaximum = 42f
            leftAxis.setDrawZeroLine(false)
        }
        var rightAxis: YAxis
        run {
            rightAxis = hartsHrr.axisRight
            rightAxis.axisMinimum = 32f
            rightAxis.axisMaximum = 42f
            // rightAxis.granularity=5f
            rightAxis.textColor=resources.getColor(R.color.sub_text_color)
            rightAxis.axisLineColor = Color.WHITE
            rightAxis.zeroLineColor = resources.getColor(R.color.color_view)
            rightAxis.gridColor = resources.getColor(R.color.color_view)
            rightAxis.setDrawZeroLine(false)
        }
        hartsHrr.extraBottomOffset = 20f
    }

    private var values =
        ArrayList<BarEntry>()

    private fun setDataView(mList: ArrayList<Int>) {
        TLog.error("==" + Gson().toJson(mList))
        values = ArrayList()
        var setList: ArrayList<Int> = arrayListOf()

        var num = 0
        var iFZero = 0    //30个平均为0排除
        //   var maxHeart = 0
        // var maxIndex = 0  //最大值属于长度 48的哪一位置
        var minHeart = 999//默认一个比较大的值
        //    var minIndex = 0  //最小值属于数组48的哪一位置
        var avgHeart = 0L //平均值
        var avgNotZero = 0//平均值不为0有多少次

        mList?.forEachIndexed { index, i ->
            run {
//                TLog.error("index+=" + index)
//                if (i > maxHeart) {
//                    maxHeart = i
//                   // maxIndex = index
//                }
//                if (i in 1 until minHeart) {
//                    minHeart = i
//                   // minIndex=index
//                }
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
                    // setList.add(i)
                }
            }
        }
        if (minHeart >= 999)
            minHeart = 0
        if (avgNotZero <= 0)
            avgNotZero = 1
        TLog.error("avgHeart++" + avgHeart.toFloat())
        TLog.error("avgHeart++" + (avgNotZero * 10))
        TLog.error("avgHeart++" + (avgHeart.toDouble() / (avgNotZero * 10)))
        //现在平均体温由向上改为向下
        var avgNum =
            BigDecimal(avgHeart.toDouble() / (avgNotZero * 10)).setScale(1, BigDecimal.ROUND_DOWN)
        TLog.error("avgNum++" + avgNum)
        if (avgNum > BigDecimal.ZERO || minHeart > 0) {
            tvAvgNum.text = HelpUtil.getSpan(avgNum.toString(), tempUnit, 11)
            tvMaxNum.text = HelpUtil.getSpan(
                ResUtil.format("%.1f", Collections.max(setList).toDouble() / 10),
                tempUnit,
                11
            )
            tvMinNum.text =
                HelpUtil.getSpan(ResUtil.format("%.1f", (minHeart.toFloat() / 10)), tempUnit, 11)
        } else {
            tvAvgNum.text = "--"
            tvMaxNum.text = "--"
            tvMinNum.text = "--"
        }
        var mIndex = 0
        var lastText = 0f
        setList.forEachIndexed { index, i ->
            values.add(BarEntry(index.toFloat(), i.toFloat() / 10))
            if (i > 0) {
                mIndex = index
                lastText = i.toFloat() / 10
            }
        }
        if (setList.size > 0 && lastText > 0) {
            getLastText(mIndex, lastText.toString())
        }

        val set1 = BarDataSet(values, "")
        val getColors = IntArray(48)
        for (i in 0 until setList.size) {
            when {
                setList[i] >= 411 -> {
                    getColors[i] = resources.getColor(R.color.color_four)
                }
                setList[i] in 391..410 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_pressure_three)
                }
                setList[i] in 381..390 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_pressure_two)
                }
                setList[i] in 373..380 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_pressure_one)
                }
                setList[i] in 360..373 -> {
                    getColors[i] = resources.getColor(R.color.color_main_green)
                }
                setList[i] < 360 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_pressure_low)
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

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        TLog.error("==" + e.x.toLong())
        tvTime.text = String.format(
            "%s",
            DateUtil.getDate(
                DateUtil.HH_MM,
                (TimeUtil.getTodayZero(0) + (e.x.toLong() * 30 * 60000L))
            ) + "-" +
                    DateUtil.getDate(
                        DateUtil.HH_MM,
                        (TimeUtil.getTodayZero(0) + ((e.x.toLong() + 1) * 30 * 60000L))
                    )
        )
        val bloodOxygenNum = h.y
        if (bloodOxygenNum > 0)
            tvType.text = HelpUtil.getSpan(bloodOxygenNum.toString(), tempUnit)
        else
            tvType.text = "--" // HelpUtil.getSpan("--", tempUnit)
    }

    private fun getLastText(timeIndex: Int, text: String) {
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
        tvType.text = HelpUtil.getSpan(text, tempUnit)
    }


    /**
     * 设置标题日期相关数据
     */
    var timeDialog: Long? = System.currentTimeMillis()//默认为当天时间
    private var lastTodayDate: String? = null
    var date: String? = null
    private fun setTitleDateData() {
        val calendar: Calendar? = XingLianApplication.getSelectedCalendar()
        calendar?.set(Calendar.HOUR_OF_DAY, 0)
        calendar?.set(Calendar.MINUTE, 0)
        TLog.error("calendar++${calendar?.timeInMillis}")
//        TLog.error("calendar++${DateUtil.getCurrentCalendarBegin()}")
        timeDialog = calendar?.timeInMillis
        date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar)
        if (DateUtil.equalsToday(date)) {
            // tvTypeTime.setText(R.string.title_today)
            lastTodayDate = date
            img_right.visibility = View.INVISIBLE
        } else {
            img_right.visibility = View.VISIBLE

        }
        tvTypeTime.text = date
        if (DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar?.timeInMillis!!)
                .equals(DateUtil.getDate(DateUtil.YYYY_MM_DD, DateUtil.getCurrentDate()))
        ) {
            img_right.visibility = View.INVISIBLE
        } else
            img_right.visibility = View.VISIBLE
        tvType.text = "--"// HelpUtil.getSpan("--", tempUnit)
        tvTime.text = ""

        mViewModel.getTemp(
            (DateUtil.getDayZero(timeDialog!!) / 1000).toString(),
            (DateUtil.getDayEnd(timeDialog!!) / 1000).toString()
        )
//        getHeart(date)
    }

    private fun getHeart(date: String) {
        val mTempList =
            mTempListDao.getTempBean(date)
        TLog.error("date==" + date)
        TLog.error("mTempList==" + Gson().toJson(mTempList))
        if (mTempList != null && !mTempList.array.isNullOrEmpty() && mTempList.array != "[]") {
            val array =
                Gson().fromJson(mTempList.array, ArrayList::class.java)
            var mList: ArrayList<Int> = array as ArrayList<Int>
            var notNullList: ArrayList<Int> = ArrayList()
            var nullZero = 0
            for (i in 0 until mList.size) {
                if (mList[i] > 0) {
                    nullZero += mList[i]
                    notNullList.add(mList[i])
                }
            }
            setDataView(array)
        } else {
            var nullList = arrayListOf<Int>()
            for (i in 0 until 1440)
                nullList.add(0)
            setDataView(nullList)
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
}