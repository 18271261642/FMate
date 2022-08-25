package com.example.xingliansdk.ui.heartrate

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
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.heartView.HeartRateVoBean
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.ui.heartrate.viewmodel.HeartRateViewModel
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.luck.picture.lib.tools.ToastUtils
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.DataBean
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_heart_rate_o.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


fun Long.formatTime(): String? {
    return DateUtil.getDate(
        DateUtil.YYYY_MM_DD,
        (Config.TIME_START + this) * 1000L
    )
}

/**
 * 连续心率页面
 */
class HeartRateActivity : BaseActivity<HeartRateViewModel>(), View.OnClickListener,
    OnChartValueSelectedListener {

    private lateinit var mList: ArrayList<Int>
    private lateinit var hartsHrr: LineChart
    var type = -1
    var position = 0
    private var heartRateList: MutableList<RoomTimeBean> = mutableListOf()
    private lateinit var sDao: RoomTimeDao
    lateinit var mPopularScienceAdapter: PopularScienceAdapter
    private lateinit var mPopularList: MutableList<PopularScienceBean.ListDTO>

    //心率的
    private lateinit var mHeartListDao: HeartListDao
    override fun layoutId() = R.layout.activity_heart_rate_o
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        SNEventBus.register(this)

        mList = arrayListOf()
        type = intent.getIntExtra("HistoryType", 0)
        var bean = intent.getSerializableExtra("HeartRate") as HomeCardVoBean.ListDTO
        var toDayTime = System.currentTimeMillis()
        TLog.error("实体bean==" + Gson().toJson(bean))
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
        img_left.setOnClickListener(this)
        img_right.setOnClickListener(this)
        if (bean.startTime > 0)
            toDayTime = bean.startTime * 1000L
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar(toDayTime))
        sDao = AppDataBase.instance.getRoomTimeDao()
        mHeartListDao = AppDataBase.instance.getHeartDao()
        val allRoomTimes = sDao.getAllRoomTimes()
        if (allRoomTimes.size > 0) {
            heartRateList = allRoomTimes
            position = heartRateList.size - 1
        }
        hartsHrr = harts_hrr
        chartView()
        setView()
        setTitleDateData()
        setAdapter()

        initData()
    }

    //是否在测量
    val  isMeasureHeart = false

    private fun initData(){

        ringMeasureHtTv.setOnClickListener{
//            if(isMeasureHeart){
//                ShowToast.showToastShort("正在测量中,请稍后..")
//                return@setOnClickListener
//            }

            measureRingHeartStatus(true)
            ringMeasureHtTv.text = "测量中"
        }
        if(!XingLianApplication.getXingLianApplication().getDeviceConnStatus()){
            ringMeasureHtTv.visibility = View.GONE
            return
        }
        //戒指测量按钮,没有连接不显示
        if(mDeviceFirmwareBean.productNumber != null){
            val productNumber = mDeviceFirmwareBean.productNumber
            ringMeasureHtTv.visibility = if(XingLianApplication.getXingLianApplication().getDeviceCategoryValue(productNumber) == 1) View.VISIBLE else View.GONE
        }

    }


    private fun measureRingHeartStatus(isStart : Boolean){
        BLEManager.getInstance().dataDispatcher.clear("")
        BleWrite.writeRingMeasureHtStatus(isStart)
    }

    private fun setAdapter() {
        var hasmap = HashMap<String, Any>()
        hasmap["category"] = "1"
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

        if (HelpUtil.netWorkCheck(this)) {
            tvYouKnow.visibility = View.VISIBLE
        } else
            tvYouKnow.visibility = View.GONE
        mPopularScienceAdapter.setOnItemClickListener { adapter, view, position ->
            JumpUtil.startWeb(this, mPopularList[position].detailUrl)
        }
    }


    private fun setView() {
        var time = if (heartRateList != null && heartRateList.isNotEmpty())
            heartRateList[position].startTime.formatTime()!!
        else
            DateUtil.getDate(DateUtil.YYYY_MM_DD, System.currentTimeMillis())
        tvTypeTime.text = time
    }

    var setValueStatus = true  //显示值的设置
    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) {
            TLog.error("it=" + Gson().toJson(it))
            setValueStatus = true
            var mList = Gson().fromJson(Gson().toJson(it), HeartRateVoBean::class.java)
            if (mList.heartRateVoList == null || mList.heartRateVoList.size <= 0) {
                date?.let { it1 -> getHeart(it1) }
                return@observe
            }
            setValueStatus = false
            var heartRateVoList = mList.heartRateVoList[0]

            var heartList = Gson().toJson(heartRateVoList.heartRate)
            TLog.error("mlist=" + Gson().toJson(mList))
            mHeartListDao.insert(
                HeartListBean(
                    heartRateVoList.startTimestamp.toLong(),
                    heartRateVoList.endTimestamp.toLong(),
                    heartList,
                    false,
                    heartRateVoList.date
                )
            )
            setValue(
                heartRateVoList.max.toDouble().toInt(),
                heartRateVoList.min.toDouble().toInt(),
                heartRateVoList.avg.toDouble().toInt()
            )
            getHeart(heartRateVoList.date)
        }
        mViewModel.msg.observe(this) {
            date?.let { it1 ->
                setValueStatus = true//错误的情况都是自己算
                getHeart(it1)
            }
        }
        mViewModel.resultPopular.observe(this)
        {
            if (it == null || it.list.isNullOrEmpty() || it.list.size <= 0)
                return@observe
//            TLog.error("==" + it.list[0].image)
            mPopularList.addAll(it.list)
            mPopularScienceAdapter.notifyDataSetChanged()
        }
    }


    override fun onClick(v: View) {
        when (v.id) {

            R.id.img_left -> {
                XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, -1)
                //    refreshCurrentSelectedDateData()
                setTitleDateData()
            }
            R.id.img_right -> {
                XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, +1)
                //  refreshCurrentSelectedDateData()
                setTitleDateData()
            }
            R.id.tvTypeTime -> {
                //    dialog()
            }

        }
    }

    private fun chartView() {



        hartsHrr.description.isEnabled = false
        hartsHrr.legend.isEnabled = false  //色块不显示
        hartsHrr.setScaleEnabled(true)//设置比列启动
        hartsHrr.setOnChartValueSelectedListener(this)
        hartsHrr.isScaleYEnabled = false
        hartsHrr.isScaleXEnabled = false
        hartsHrr.viewPortHandler.setMaximumScaleX(10f)
        var xAxis: XAxis
        val timeMatter: IAxisValueFormatter = HeartAxisValueFormatter(hartsHrr)
        val heartMarkView = CusHeartMarkView(this,R.layout.custom_marker_view)
        heartMarkView.chartView = hartsHrr
        hartsHrr.marker = heartMarkView
        run {
            xAxis = hartsHrr.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)//设置网格线
            xAxis.textColor = R.color.red
            xAxis.axisMaximum = 288f
            xAxis.axisMinimum = 0f
            xAxis.granularity = 72f // 想 弄成 4 hour
            xAxis.textColor = resources.getColor(R.color.sub_text_color)
            xAxis.setLabelCount(5, true)
            xAxis.axisLineColor = Color.WHITE
            xAxis.valueFormatter = timeMatter
            xAxis.mLabelHeight = 20
            xAxis.mLabelRotatedHeight = 20
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.isEnabled = false
            //   leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            leftAxis.axisMinimum = 40f
            leftAxis.axisMaximum = 220f
            leftAxis.setDrawZeroLine(false)
        }
        var rightAxis: YAxis
        run {
            rightAxis = hartsHrr.axisRight
            rightAxis.axisMinimum = 40f
            rightAxis.axisMaximum = 220f
            rightAxis.axisLineColor = Color.WHITE
            rightAxis.zeroLineColor = resources.getColor(R.color.color_view)
            rightAxis.gridColor = resources.getColor(R.color.color_view)
           // rightAxis.textColor = resources.getColor(R.color.sub_text_color)
            rightAxis.granularity = 40f
            rightAxis.textColor = resources.getColor(R.color.sub_text_color)

            rightAxis.setDrawZeroLine(false)
        }


        hartsHrr.extraBottomOffset = 20f
        setDataView(mList)
        hartsHrr.invalidate()
    }


    private fun setDataView(mList: MutableList<Int>) {
         TLog.error("长度" + mList.size)
        var values = ArrayList<Entry>()
        var data = LineData()
        var setList: ArrayList<Int> = arrayListOf()
        var deviceList: ArrayList<Int> = arrayListOf()
        var num = 0
        var iFZero = 0    //30个平均为0排除
//        var maxHeart = 0  //86400中最大的值
//        var maxIndex = 0  //最大值属于长度 288的哪一位置
//        var minHeart = 999//最小值, 默认一个比较大的值
//        var minIndex = 0  //最小值属于长度 288的哪一位置
        var avgHeart = 0L //平均值
        var avgNotZero = 0//平均值不为0有多少次
        mList?.forEachIndexed { index, i ->
            run {

//                if (i > maxHeart) {
//                    maxHeart = i
//                    maxIndex = index
//                }
//                if (i in 1 until minHeart) {
//                    minHeart = i
//                    minIndex = index
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
                    setList.add(heart)
                    if (heart > 0) {
                        deviceList.add(heart)
                    }
                    num = 0
                    iFZero = 0
                }
            }
        }
        var maxHeart = 0
        var minHeart = 999
        var mIndex = 0
        var lastHeart = 0
        TLog.error("生成的setList+="+Gson().toJson(setList))
        setList.forEachIndexed { index, it ->
            if (it > 0) {
                if (maxHeart < it)
                    maxHeart = it
                if (minHeart > it)
                    minHeart = it
                mIndex = index
                lastHeart = it
            }
            if (it <= 0) {

                if (values.isNotEmpty()) {
                    val set1 = LineDataSet(values, "")
                    set1.color = resources.getColor(R.color.color_heart)
                    set1.fillColor = resources.getColor(R.color.color_heart_view)
                    set1.fillFormatter =
                        IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
                    set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    set1.setDrawFilled(true) //画下面的彩色图
                    set1.setDrawCircles(false)//设置画圆点
                    set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
                    set1.lineWidth = 1.3f  //设置折线粗细
                    data.addDataSet(set1)

                }
                values = ArrayList()
            } else {
                TLog.error("it=="+it)
                values.add(Entry(index.toFloat(), it.toFloat()))
//                val set1 = LineDataSet(values, "")
//                //set1.color = resources.getColor(R.color.color_heart)
////                set1.fillColor = resources.getColor(R.color.color_heart_view)
//                set1.fillFormatter =
//                    IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
//                set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
//                set1.setDrawFilled(false) //画下面的彩色图
//                set1.setDrawCircles(false)//设置画圆点
//                set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
//                set1.lineWidth = 1.3f  //设置折线粗细
//                data.addDataSet(set1)
            }
        }
        if (avgNotZero <= 0)
            avgNotZero = 1
        if (minHeart >= 999)
            minHeart = 0
        if (setList.size > 0 && lastHeart > 0) {
            getLastText(mIndex, lastHeart)
        }
        if (setValueStatus) {
            setValue(maxHeart, minHeart, (avgHeart / avgNotZero).toInt())
        }

        /**
         * values装取所有数据然后再一次性 画完
         * ios那边告诉的结果是 values装数据的时候分段装 >0的装了直接画 画完了 在小于0的时候
         * 清空数据 然后直接画点不画任何东西 在for循环中画 而不是for循环 添加完了画
         */

        TLog.error("setList+=" + Gson().toJson(setList))

        if (values.isNotEmpty()) {
            var set1 = LineDataSet(values, "")
            set1.color = resources.getColor(R.color.color_heart)
            set1.fillColor = resources.getColor(R.color.color_heart_view)
            set1.fillFormatter =
                IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            set1.setDrawFilled(true)//填充下面的颜色
            set1.setDrawCircles(false)//设置画圆点
            set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
            set1.lineWidth = 1.3f  //设置折线粗细
            data.addDataSet(set1)//正确操作
            //data=LineData(set1)//错误 操作
        }else{
            var set1 = LineDataSet(values, "")
           // set1.color = resources.getColor(R.color.color_heart)
           // set1.fillColor = resources.getColor(R.color.color_heart_view)
//            set1.fillFormatter =
//                IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
//            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
//          //  set1.setDrawFilled(true)//填充下面的颜色
//            set1.setDrawCircles(false)//设置画圆点
//            set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
//            set1.lineWidth = 1.3f  //设置折线粗细
//            data.addDataSet(set1)//正确操作
        }
        hartsHrr.data = data
        hartsHrr.invalidate()

        //获取焦点
        hartsHrr.clearFocus()

        hartsHrr.isFocusableInTouchMode = false
    }

    override fun onNothingSelected() {
        TLog.error("onNothingSelected")
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
//        TLog.error("onValueSelected++" + e.x + ",  y==" + e.y + ",  Highlight+=" + h.x)
        tvType.text = String.format(
            "%s",
            DateUtil.getDate(
                DateUtil.HH_MM,
                (TimeUtil.getTodayZero(0) + (e.x.toLong() * 5 * 60000L))
            )
        )
        if (h.y.toLong() <= 0) {
            tvHeart.text = "--"
        } else
            tvHeart.text = HelpUtil.getSpan(h.y.toLong().toString(), resources.getString(R.string.string_time_minute))

    }

    /**
     * 设置标题日期相关数据
     */
    private var timeDialog: Long? = System.currentTimeMillis()//默认为当天时间
    var date: String? = null
    private fun setTitleDateData() {
        val calendar: Calendar? = XingLianApplication.getSelectedCalendar()
        timeDialog = calendar?.timeInMillis
        date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar)
        tvType.text = ""
        tvHeart.text = "--"
        if (DateUtil.equalsToday(date)) {
            img_right.visibility = View.INVISIBLE
        } else {
            img_right.visibility = View.VISIBLE
        }
        tvTypeTime.text = date
       // mViewModel.getHeartRate("1635782400","1635868799")
        mViewModel.getHeartRate(
            (DateUtil.getDayZero(timeDialog!!) / 1000).toString(),
            (DateUtil.getDayEnd(timeDialog!!) / 1000).toString()
        )
        //  getHeart(date)
    }

    private fun getHeart(date: String) {
        TLog.error("datae++${Gson().toJson(date)}")
        TLog.error("list心率某天++${Gson().toJson(mHeartListDao.getSomedayHeartList(date).size)}")
        TLog.error("bean心率某天++${Gson().toJson(mHeartListDao.getSomedayHeart(date))}")
        val mHeartList = mHeartListDao.getSomedayHeart(date)
        if (mHeartList == null || mHeartList.heart.isNullOrEmpty() || mHeartList.heart == "[]") {
            TLog.error("nullList=="+Gson().toJson(mHeartList))
            var nullList = arrayListOf<Int>()
            for (i in 0 until 8640)
                nullList.add(0)
            setDataView(nullList)
        //    hartsHrr.invalidate()
            return
        }
        hideWaitDialog()
        val heartRateList =
            Gson().fromJson(mHeartList.heart, ArrayList::class.java)
        TLog.error("新排查heartRateList=="+Gson().toJson(heartRateList))
        setDataView(heartRateList as ArrayList<Int>)


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

                    // refreshCurrentSelectedDateData()
                    setTitleDateData()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    private fun setValue(maxHeart: Int, minHeart: Int, avgHeart: Int) {
        if (maxHeart <= 0 && minHeart <= 0 && avgHeart <= 0) {
            tvMaxNum.text = "--" // HelpUtil.getSpan("--", "次/分钟", 11)
            tvMinNum.text = "--" // HelpUtil.getSpan("--", "次/分钟", 11)
            tvAvgNum.text = "--" // HelpUtil.getSpan("--", "次/分钟", 11)
        } else {
            tvMaxNum.text = HelpUtil.getSpan(maxHeart.toString(), resources.getString(R.string.string_time_minute), 11)
            tvMinNum.text = HelpUtil.getSpan(minHeart.toString(), resources.getString(R.string.string_time_minute), 11)
            tvAvgNum.text = HelpUtil.getSpan(avgHeart.toString(), resources.getString(R.string.string_time_minute), 11)
        }
    }

    private fun getLastText(timeIndex: Int, heart: Int) {
        tvType.text = String.format(
            "%s",
            DateUtil.getDate(
                DateUtil.HH_MM,
                (TimeUtil.getTodayZero(0) + (timeIndex * 5 * 60000L))
            )
        )
        tvHeart.text = HelpUtil.getSpan(heart.toString(), resources.getString(R.string.string_time_minute))
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.ActiveUpload.DEVICE_REAL_TIME_OTHER.toInt() -> {
                var data: DataBean = event.data as DataBean
                TLog.error("-------测量="+data.time)
                //心率
                val ht = data.heartRate
                if(ht in 31..249){
                    tvHeart.text = HelpUtil.getSpan(ht.toString(), resources.getString(R.string.string_time_minute))
                    measureRingHeartStatus(false)
                    ringMeasureHtTv.text = "测量"
                }
            }
        }
    }
}