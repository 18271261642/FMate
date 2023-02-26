package com.app.fmate.ui.pressure

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.adapter.PopularScienceAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.PopularScienceBean
import com.app.fmate.bean.room.*
import com.app.fmate.network.api.homeView.HomeCardVoBean
import com.app.fmate.network.api.pressureView.PressureViewModel
import com.app.fmate.network.api.pressureView.PressureVoBean
import com.app.fmate.ui.fragment.HomeFragment
import com.app.fmate.utils.*
import com.app.fmate.view.DateUtil
import com.app.fmate.widget.TitleBarLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.shon.connector.Config
import com.shon.connector.bean.PressureBean
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_pressure.*
import java.util.*
import kotlin.collections.ArrayList


fun Long.formatTime(): String? {
    return DateUtil.getDate(
        DateUtil.YYYY_MM_DD,
        (Config.TIME_START + this) * 1000L
    )
}

class PressureActivity : BaseActivity<PressureViewModel>(), View.OnClickListener,
    OnChartValueSelectedListener {
    private lateinit var mList: ArrayList<Int>
    private lateinit var hartsHrr: BarChart
    var type = -1
    var position = 0
    private var heartRateList: MutableList<PressureTimeBean> = mutableListOf()
    private lateinit var sDao: PressureTimeDao
    lateinit var mPopularScienceAdapter: PopularScienceAdapter
    private lateinit var mPopularList: MutableList<PopularScienceBean.ListDTO>

    //压力
    private lateinit var mPressureListDao: PressureListDao
    override fun layoutId() = R.layout.activity_pressure
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
        if (bean.startTime > 0)
            toDayTime = bean.startTime * 1000L
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar(toDayTime))
        mList = arrayListOf()
        type = intent.getIntExtra("HistoryType", 0)
        img_left.setOnClickListener(this)
        img_right.setOnClickListener(this)
//        tvTypeTime.setOnClickListener(this)
        sDao = AppDataBase.instance.getPressureTimeDao()
        mPressureListDao = AppDataBase.instance.getPressureListDao()
        TLog.error("mPressureListDao==" + mPressureListDao.getAllRoomHeartList().size)
        val allRoomTimes = sDao.getAllRoomTimes()
        //时间单子
        TLog.error("压力+=" + Gson().toJson(allRoomTimes))
        if (allRoomTimes.size > 0) {
            heartRateList = allRoomTimes
            position = heartRateList.size - 1
        }
        hartsHrr = harts_hrr
        chartView()
        setTitleDateData()
        //  pieView()
        //  setView()
        setAdapter()
    }

    private fun setAdapter() {
        var hasmap = HashMap<String, Any>()
        hasmap["category"] = "3"
        mViewModel.getPopular(hasmap)
        mPopularList = ArrayList()
        ryPopularScience.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ryPopularScience.addItemDecoration(
                    RecycleViewDivider(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        1,
                        resources.getColor(R.color.color_view)
                    )
                )
        mPopularScienceAdapter = PopularScienceAdapter(mPopularList)
        ryPopularScience.adapter = mPopularScienceAdapter
        if(HelpUtil.netWorkCheck(this))
        {
            tvYouKnow.visibility=View.VISIBLE
        }
        else
            tvYouKnow.visibility=View.GONE
        mPopularScienceAdapter.setOnItemClickListener { adapter, view, position ->
            JumpUtil.startWeb(this, mPopularList[position].detailUrl)
        }
    }


    var setValueStatus = true  //显示值的设置
    override fun createObserver() {
        super.createObserver()
        mViewModel.msg.observe(this) {
            TLog.error(it!!)
            date?.let { it1 ->
                setValueStatus = true//错误的情况都是自己算
                getHeart(it1)
            }
        }
        mViewModel.result.observe(this) {
            TLog.error("==" + Gson().toJson(it))
            setValueStatus = true
            var mList = Gson().fromJson(Gson().toJson(it), PressureVoBean::class.java)
            if (mList.pressureVoList == null || mList.pressureVoList.size <= 0) {
                date?.let { it1 -> getHeart(it1) }
                return@observe
            }
            setValueStatus = false
            var pressureVoList = mList.pressureVoList[0]
            var pressureList = Gson().toJson(pressureVoList.data)
            TLog.error("mlist=" + Gson().toJson(mList))
            mPressureListDao.insert(
                PressureListBean(
                    pressureVoList.startTimestamp, pressureVoList.endTimestamp,
                    pressureList,
                    true,
                    pressureVoList.date
                )
            )
            getHeart(pressureVoList.date)
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

    private fun setView() {
        var time = if (heartRateList != null && heartRateList.isNotEmpty())
            heartRateList[position].startTime.formatTime()!!
        else
            DateUtil.getDate(DateUtil.YYYY_MM_DD, System.currentTimeMillis())
        tvTypeTime.text = time
        // setVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
        HomeFragment.PressureOnClick = false
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
        hartsHrr.legend.isEnabled = false
//        hartsHrr.isDragEnabled = true
        hartsHrr.setScaleEnabled(true)//设置比列启动
        hartsHrr.setMaxVisibleValueCount(0)
        hartsHrr.setPinchZoom(false)
        hartsHrr.setDrawBarShadow(false)
        hartsHrr.isScaleYEnabled = false
        hartsHrr.setDrawGridBackground(false)
        hartsHrr.viewPortHandler.setMaximumScaleX(3f)
//        hartsHrr.setScaleMinima(1.2f,0f)
//        chart.setClipValuesToContent(false)
        //监听器
        hartsHrr.setOnChartValueSelectedListener(this)
        var xAxis: XAxis
        val timeMatter: IAxisValueFormatter = BloodOxygenValueFormatter(hartsHrr)
        run {
            xAxis = hartsHrr.xAxis
//            xAxis.setCenterAxisLabels(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)//设置网格线
            xAxis.axisLineColor = Color.WHITE
            xAxis.textColor=resources.getColor(R.color.sub_text_color)
            xAxis.axisMaximum = 48f
            xAxis.axisMinimum = 0f
            xAxis.granularity = 12f // 想 弄成 4 hour
            xAxis.labelCount = 4
//            xAxis.isEnabled = false
            xAxis.valueFormatter = timeMatter
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.isEnabled = false
            leftAxis.axisMinimum = 0f
            leftAxis.axisMaximum = 100f
            leftAxis.setDrawZeroLine(true)
        }
        var rightAxis: YAxis
        run {
            rightAxis = hartsHrr.axisRight
            rightAxis.axisMinimum = 0f
            rightAxis.axisMaximum = 100f
            rightAxis.textColor=resources.getColor(R.color.sub_text_color)
            rightAxis.axisLineColor = Color.WHITE
            rightAxis.zeroLineColor = resources.getColor(R.color.color_view)
            rightAxis.gridColor = resources.getColor(R.color.color_view)
            rightAxis.setDrawZeroLine(true)
        }
        hartsHrr.axisRight.setDrawAxisLine(false)//不会显示 最右边的竖线
//        TLog.error("mlist++" + Gson().toJson(mList))
        hartsHrr.extraBottomOffset=20f
        setDataView(mList)
        hartsHrr.invalidate()
    }

    private fun pieView() {
        pieChart.setUsePercentValues(true)//设置百分比
        pieChart.description.isEnabled = false
        //设置为圆形的
        pieChart.legend.form = Legend.LegendForm.CIRCLE
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.setCenterTextSize(16f)
        pieChart.centerText = " 压 力 \n 比 例 "
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = false
        pieChart.setDrawEntryLabels(false)
//        pieChart.setOnChartValueSelectedListener(this)
        pieChart.animateY(1000, Easing.EaseInOutQuad)
        val l: Legend = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 20f
        l.yEntrySpace = 20f//y轴距离调整
        l.yOffset = 0f
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)
    }

    private var values =
        ArrayList<BarEntry>()
    var maxSetList: ArrayList<Int> = arrayListOf()
    var minSetList: ArrayList<Int> = arrayListOf()
    private fun setDataView(mList: ArrayList<Int>) {
        TLog.error("长度" + mList.size)
        TLog.error("长度" + Gson().toJson(mList))
        var setList: ArrayList<Int> = arrayListOf()
        maxSetList = arrayListOf()
        minSetList = arrayListOf()
        values = ArrayList()
        var avgNum = 0
        var number = 0
        var num = 0
        var maxNum = 0
        var minNum = 999
        var iFZero = 0    //30个平均为0排除
        //俩个数组的数字
        var maxSet = 0
        var minSet = 999
        var pieList: ArrayList<Float> = arrayListOf(0F, 0F, 0F, 0F, 0F)
        mList?.forEachIndexed { index, i ->
            run {
                if (i > maxSet) {
                    maxSet = i
                }
                if (i in 1 until minSet) {
                    minSet = i
                }
                if (i == 0)
                    iFZero++
                else
                    num += i
                if ((index + 1) % 30 == 0) {
                    var size =  //当为0时特殊处理
                        if ((30 - iFZero) <= 0)
                            1
                        else
                            (30 - iFZero)
                    setList.add(num / size)
                    maxSetList.add(maxSet)
                    if (minSet >= 999)
                        minSetList.add(0)
                    else
                        minSetList.add(minSet)
//                    if (maxNum > 99)
//                        setList.add(0)
//                    else
//                        setList.add(maxNum)
                    maxSet = 0
                    minSet = 999
                    iFZero = 0
                    num = 0
                }
            }
            when (i) {
                in 1 until 30 -> {
                    pieList[0]++
                }
                in 30 until 60 -> {
                    pieList[1]++
                }
                in 60 until 80 -> {
                    pieList[2]++
                }
                in 80 until 100 -> {
                    pieList[3]++
                }
            }

            if (i > 0) {
                if (minNum > i)
                    minNum = i
                avgNum += i
                number++
            }
        }
        var mIndex = 0
        var lastMin = 0
        var lastMax = 0
        setList.forEachIndexed { index, i ->
            values.add(BarEntry(index.toFloat(), i.toFloat()))
            if (i > 0) {
                lastMin = minSetList[index]
                lastMax = maxSetList[index]
                mIndex = index
            }
        }
        if (setList.size > 0 && lastMax > 0) {
            getLastText(mIndex, lastMin, lastMax)
        }
        TLog.error("minList++" + Gson().toJson(minSetList))
        TLog.error("maxList++" + Gson().toJson(maxSetList))
        TLog.error("setList==" + Gson().toJson(setList))
        if (number <= 0)
            number = 1
//        if (setList.size > 0) {
////            if (Collections.max(setList) > 0) {
////                setDataPieView(pieList)
////            } else
////                pieChart.visibility = View.GONE
//            maxNum = Collections.max(setList)
//        }
//        else
        if (setList.size <= 0)
            pieChart.visibility = View.GONE
        if (minNum >= 999)
            minNum = 0
        //  minNum=Collections.min(setList)  //存在0值 不能用这个
        if (mList.size > 0)
            maxNum = Collections.max(mList)
        if(avgNum<=0&&minNum<=0&&maxNum<=0)
        {
            tvPressureAvgContent.text = "--"
            tvPressureMaxMinContent.text = "--"
        }
        else {
            tvPressureAvgContent.text = "${avgNum / number}"
            tvPressureMaxMinContent.text = "$minNum - $maxNum"
        }
        val set1 = BarDataSet(values, "")
        val getColors = IntArray(48)
        for (i in 0 until setList.size) {
            when (setList[i]) {
                in 1 until 30 -> {
                    getColors[i] = resources.getColor(R.color.color_pressure_relax)
                }
                in 30 until 60 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_pressure_normal)
                }
                in 60 until 80 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_pressure_one)
                }
                in 80 until 100 -> {
                    getColors[i] = resources.getColor(R.color.color_blood_pressure_three)
                }
                else -> {
                    getColors[i] = resources.getColor(R.color.sub_text_color)
                }
            }
        }
        set1.setColors(*getColors)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(set1)
//        data.barWidth = 0.85f
////            data.removeDataSet(5)
////            data.setDrawValues(false)
        hartsHrr.data = data
        hartsHrr.invalidate()


    }

    private fun setDataPieView(mList: MutableList<Float>) {
        pieChart.visibility = View.VISIBLE
        val entries = ArrayList<PieEntry>()
        val typeList = arrayListOf("放松 1-29", "正常 30-59", "中等 60 -79", "偏高 80-99")
        val mListColor = arrayListOf(
            R.color.color_pressure_relax,
            R.color.color_blood_pressure_normal,
            R.color.color_blood_pressure_one,
            R.color.color_blood_pressure_three
        )
        for (i in 0 until mList.size) {
            if (mList[i] != 0F)
                entries.add(
                    PieEntry(
                        mList[i],
                        typeList[i]
                    )
                )
        }
        val dataSet = PieDataSet(entries, " ")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors
        val colors = ArrayList<Int>()
        for (i in 0 until mList.size) {
            if (mList[i] > 0)
                colors.add(resources.getColor(mListColor[i]))
        }
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.invalidate()
    }

    override fun onNothingSelected() {
        TLog.error("onNothingSelected")
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
//        if(e.y.toLong()<=0)
//            tvTime.text=""
//        else
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
        if (bloodOxygenNum > 0) {
            // tvType.text = bloodOxygenNum.toString()
            TLog.error("===" + e.x.toInt())
            // tvType.text =  HelpUtil.getSpan("最小值",""+minSetList[e.x.toInt()]+"-"+maxSetList[e.x.toInt()],
            //     "最大值",11)
            tvType.text = "" + minSetList[e.x.toInt()] + "-" + maxSetList[e.x.toInt()]
        } else
            tvType.text = "--"
    }


    /**
     * 设置标题日期相关数据
     */
    private var timeDialog: Long? = System.currentTimeMillis()//默认为当天时间
    private var lastTodayDate: String? = null
    var date: String? = null
    private fun setTitleDateData() {
        val calendar: Calendar? = XingLianApplication.getSelectedCalendar()
        calendar?.set(Calendar.HOUR_OF_DAY, 0)
        calendar?.set(Calendar.MINUTE, 0)
        //  TLog.error("calendar++${calendar?.timeInMillis}")
        timeDialog = calendar?.timeInMillis
        date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar)
        tvTime.text = ""
        if (DateUtil.equalsToday(date)) {
            // tvTypeTime.setText(R.string.title_today)
            lastTodayDate = date
            img_right.visibility = View.INVISIBLE
        } else {
            img_right.visibility = View.VISIBLE

//            tlTitle.setTitle(date)
        }
        tvTypeTime.text = date
        tvType.text = "--"
        mViewModel.getPressure(
            (DateUtil.getDayZero(timeDialog!!) / 1000).toString(),
            (DateUtil.getDayEnd(timeDialog!!) / 1000).toString()
        )
        //  getHeart(DateUtil.getDate(DateUtil.YYYY_MM_DD,calendar))

    }

    private fun getHeart(date: String) {
        TLog.error("date++$date")
        val mPressureList = mPressureListDao.getSomedayPressure(date)
        TLog.error("mPressureList==" + Gson().toJson(mPressureList))
        if (mPressureList != null && !mPressureList.pressure.isNullOrEmpty() && mPressureList.pressure != "[]") {
            TLog.error("mPressureList==" + Gson().toJson(mPressureList.pressure))
//            val test: PressureVoBean.PressureVoListDTO? =
//                Gson().fromJson(mPressureList.pressure, PressureVoBean.PressureVoListDTO::class.java)
//         TLog.error("test++"+test.toString())
//            TLog.error("test++"+Gson().toJson(test))
            val mList: ArrayList<PressureBean> =
                Gson().fromJson(
                    mPressureList.pressure,
                    object : TypeToken<ArrayList<PressureBean?>?>() {}.type
                )
            // TLog.error("mList+=" + Gson().toJson(mList))

            var notNullList: ArrayList<Int> = ArrayList()
            //     var nullZero = 0
            TLog.error("遍历之前的时间戳+" + System.currentTimeMillis())
            for (i in 0 until mList.size) {
//                if (mList[i].stress > 0) {
//                    nullZero += mList[i].stress
//                }
                notNullList.add(mList[i].stress)
            }
            TLog.error("遍历之hou的时间戳+" + System.currentTimeMillis())
            setDataView(notNullList)
        } else {
            var nullList = arrayListOf<Int>()
            for (i in 0 until 1440)
                nullList.add(0)
            setDataView(nullList)
        }

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
                        ShowToast.showToastLong("不可选择大于今天的日期")
                        return@setOnDateChangeListener
                    }
                    XingLianApplication.getSelectedCalendar()?.timeInMillis = calendarTime!!
                    setTitleDateData()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    private fun getLastText(timeIndex: Int, lastMin: Int, lastMax: Int) {
        tvTime.text = String.format(
            "%s",
            DateUtil.getDate(
                DateUtil.HH_MM,
                (TimeUtil.getTodayZero(0) + (timeIndex.toLong() * 30 * 60000L))
            )
                    + "-" +
                    DateUtil.getDate(
                        DateUtil.HH_MM,
                        (TimeUtil.getTodayZero(0) + ((timeIndex.toLong() + 1) * 30 * 60000L))
                    )
        )
        tvType.text = "" + minSetList[timeIndex] + "-" + maxSetList[timeIndex]
    }
}