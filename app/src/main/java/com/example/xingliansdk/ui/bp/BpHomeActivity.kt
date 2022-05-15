package com.example.xingliansdk.ui.bp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.BloodPressureHistoryAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.bean.room.BloodPressureHistoryBean
import com.example.xingliansdk.bean.room.BloodPressureHistoryDao
import com.example.xingliansdk.dialog.OnCommDialogClickListener
import com.example.xingliansdk.dialog.PromptCheckBpDialog
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureViewModel
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_blood_pressure.*
import kotlinx.android.synthetic.main.activity_card_edit.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.ryBloodPressure
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.titleBar
import kotlinx.android.synthetic.main.comm_item_data_white_toggle_layout.*
import kotlinx.android.synthetic.main.comm_item_date_toggle_layout.*
import kotlinx.android.synthetic.main.item_blood_pressure_index.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * 新版血压首页面
 * Created by Admin
 *Date 2022/5/7
 */
class BpHomeActivity : BaseActivity<BloodPressureViewModel>(),View.OnClickListener{


    //提示校准血压的dialog
    private var promptBpDialog : PromptCheckBpDialog ?= null

    //手动输入的血压列表adapter
    private lateinit var  mBloodPressureHistoryAdapter: BloodPressureHistoryAdapter
    var bpList: ArrayList<BloodPressureHistoryBean> = arrayListOf()

    lateinit var sDao: BloodPressureHistoryDao

    private var currDayStr : String ?= null

    //收缩压的集合
    var heightList : ArrayList<Int> = arrayListOf()
    //舒张压集合
    var lowBpList : ArrayList<Int> = arrayListOf()
    //手动输入的收缩压
    var inputHeightBpList : ArrayList<Int> = arrayListOf();
    //手动输入的舒张压
    var inputLowBpList : ArrayList<Int> = arrayListOf();


    override fun layoutId(): Int {
      return R.layout.activity_new_bp_home_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        bpHomeInputLayout.setOnClickListener(this)
        bpHomeMeasureLayout.setOnClickListener(this)
        imgReplicate.setOnClickListener(this)
        commWhiteLeftImg.setOnClickListener(this)
        commWhiteRightImg.setOnClickListener(this)

        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                timeDialog()
            }

            override fun onActionClick() {
            }

        })

        initData();

        showPromptDialog()

        setTitleDateData()

       // getDateBpData(DateUtil.getCurrDate())
    }


    private fun initData(){
        sDao = AppDataBase.instance.getBloodPressureHistoryDao()
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        ryBloodPressure.layoutManager = mLinearLayoutManager
        mBloodPressureHistoryAdapter = BloodPressureHistoryAdapter(bpList)
        ryBloodPressure.adapter = mBloodPressureHistoryAdapter


        initLinChart()
    }



    private fun updateBpData(day : String){
        inputHeightBpList.clear()
        inputLowBpList.clear()
        heightList.clear()
        lowBpList.clear()
        bpList = sDao.getDayBloodPressureHistory(
            day
        ) as ArrayList<BloodPressureHistoryBean>

        mBloodPressureHistoryAdapter.data.clear()
        mBloodPressureHistoryAdapter.addData(bpList)
        mBloodPressureHistoryAdapter.notifyDataSetChanged()

        //展示图表
        bpList.forEach {
            if(it.type == 0){  //手动输入
                inputHeightBpList.add(it.systolicBloodPressure)
                inputLowBpList.add(it.diastolicBloodPressure)
            }else{  //测量
                heightList.add(it.systolicBloodPressure)
                lowBpList.add(it.diastolicBloodPressure)
            }

        }

        initLinData(bpHomeLinChartView,heightList,lowBpList,inputHeightBpList,inputLowBpList)
    }



    override fun createObserver() {
        super.createObserver()
        //返回指定日期的血压数据

        mViewModel.resultGet.observe(this){ it ->
            TLog.error("----获取血压返回="+Gson().toJson(it))
            if(it.isCalibrationRequired){   //没有校准，需要校准
                showPromptDialog()
            }

            if(it.list != null && it.list.size>0){
                it.list.forEach {
                    sDao.insert(
                        BloodPressureHistoryBean(
                            it.stampCreateTime, 0, it.dataSource , it.systolicPressure, it.diastolicPressure,
                            it.createTime
                        )
                    )
                }
            }
            currDayStr?.let { it1 -> updateBpData(it1) }
        }

    }

    //获取指定日期的血压记录
    private fun getDateBpData(dayStr : String){
        mViewModel.getBloodPressure(dayStr)
    }



    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.commWhiteLeftImg->{    //前一天
                    XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, -1)
                    imgReplicate.rotation=90f
                    setTitleDateData()
                }

                R.id.commWhiteRightImg->{    //后一天
                    XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, +1)
                    imgReplicate.rotation=90f
                    setTitleDateData()
                }

                R.id.imgReplicate -> {
                    if(llBloodPressureIndex.visibility==View.GONE)
                    {
                        imgReplicate.rotation=270f
                        llBloodPressureIndex.visibility=View.VISIBLE
                    }
                    else
                    {
                        imgReplicate.rotation=90f
                        llBloodPressureIndex.visibility=View.GONE
                    }
                }
                R.id.bpHomeInputLayout->{   //输入
                    showInputDialog()
                }
                R.id.bpHomeMeasureLayout->{ //测量
                    startActivity(Intent(this,MeasureNewBpActivity::class.java))
                }
            }
        }
    }


    var timeDialog: Long? = System.currentTimeMillis()//默认为当天时间
    private fun setTitleDateData() {
        val calendar: Calendar? = XingLianApplication.getSelectedCalendar()
        TLog.error("calendar++${calendar?.timeInMillis}")
        timeDialog = calendar?.timeInMillis
        val date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar)
        if (DateUtil.equalsToday(date)) {
            commWhiteRightImg.visibility = View.INVISIBLE

        } else {
            commWhiteRightImg.visibility = View.VISIBLE
        }
        commWhiteTitleTv.text = date
        currDayStr = date
        TLog.error("timeDialog==" + (timeDialog!! / 1000000))
        TLog.error("DateUtil==" + (DateUtil.getCurrentDate() / 1000000))
     //   mViewModel.getBloodPressure(DateUtil.getDate(DateUtil.YYYY_MM_DD,timeDialog!!))
        getDateBpData(date)

    }


    private fun showPromptDialog(){
        if(promptBpDialog == null){
            promptBpDialog = PromptCheckBpDialog(this,R.style.edit_AlertDialog_style)
        }
        promptBpDialog!!.show()
        promptBpDialog!!.setCancelable(false)
        promptBpDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) {
                promptBpDialog!!.dismiss()
                startActivity(Intent(this@BpHomeActivity,BpCheckActivity::class.java))
            }

            override fun onCancelClick(code: Int) {

            }

        })
    }


    var mDiastolic = 0
    var mEdtSystolic = 0

    var time = System.currentTimeMillis()
    private fun showInputDialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_blood_pressure
            dimAmount = 0.3f
            isFullHorizontal = true
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_CENTER
            animStyle = R.style.BottomTransAlphaADAnimation
            convertListenerFun { holder, dialog ->
                val tvTitle = holder.getView<TextView>(R.id.tv_title)
                val edtDiastolic = holder.getView<EditText>(R.id.edt_diastolic)
                val edtSystolic = holder.getView<EditText>(R.id.edt_systolic)
                val dialogCancel = holder.getView<ImageView>(R.id.imgCancel)
                val dialogSet = holder.getView<TextView>(R.id.dialog_set)
                tvTitle?.text = "记录血压"
                dialogSet?.setOnClickListener {
                    mDiastolic = 0
                    mEdtSystolic = 0
                    time = System.currentTimeMillis()
                    if (edtDiastolic?.text.toString().isNotEmpty())
                        mDiastolic = edtDiastolic?.text.toString().toInt()
                    if (edtSystolic?.text.toString().isNotEmpty())
                        mEdtSystolic = edtSystolic?.text.toString().toInt()
                    if (mEdtSystolic <= mDiastolic) {
                        ShowToast.showToastLong("收缩压不能小于舒张压!!")
                        return@setOnClickListener
                    }
                    if(mEdtSystolic>250||mDiastolic<40)
                    {
                        ShowToast.showToastLong("输入有误，请输入正确的血压值")
                        return@setOnClickListener
                    }
                    //  BleWrite.writeBloodPressureCalibrationCall(mDiastolic, mEdtSystolic)
                    mViewModel.setBloodPressure(this@BpHomeActivity,time/1000,mEdtSystolic,mDiastolic)
                    sDao.insert(
                        BloodPressureHistoryBean(
                            time/1000, 0, 0, mEdtSystolic, mDiastolic,
                            DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM, time)
                        )
                    )
//                    homeCard(mEdtSystolic,mDiastolic )
                    currDayStr = DateUtil.getDate(DateUtil.YYYY_MM_DD, time)
                    updateBpData(currDayStr as String)
                    dialog.dismiss()
                }
                dialogCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }


    private fun initLinChart(){
        bpHomeLinChartView.setNoDataText("无数据")
        bpHomeLinChartView.setNoDataTextColor(Color.WHITE)
        bpHomeLinChartView.description.isEnabled = false
        bpHomeLinChartView.setDrawBorders(false)
        bpHomeLinChartView.axisRight.setDrawAxisLine(false)
        bpHomeLinChartView.axisRight.setDrawLabels(false)
        bpHomeLinChartView.legend.isEnabled = false

        val xAxis = bpHomeLinChartView.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.parseColor("#EBEBEB")
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //右侧Y轴
        val yRight = bpHomeLinChartView.axisRight
        yRight.isEnabled = false

        //左侧Y轴
        val yLeft = bpHomeLinChartView.axisLeft
        yLeft.labelCount = 2

        //标线虚线
        yLeft.enableGridDashedLine(10f, 10f, 0f);
        //是否显示标线
        yLeft.setDrawGridLines(true)
        yLeft.setDrawAxisLine(false)
        yLeft.gridColor = Color.WHITE
        yLeft.textColor = Color.WHITE
        yLeft.axisLineColor = R.color.white
        yLeft.isEnabled = true


        var yHbpLimit = LimitLine(140f,"140")
        yHbpLimit.lineColor = Color.WHITE
        yHbpLimit.lineWidth = 1f
        yHbpLimit.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        yHbpLimit.enableDashedLine(10f, 10f, 0f)
        yHbpLimit.textColor = Color.WHITE
       //yLeft.addLimitLine(yHbpLimit)

        var yLbpLimit = LimitLine(90f,"90")
        yLbpLimit.lineColor = Color.WHITE
        yLbpLimit.textColor = Color.WHITE
        yLbpLimit.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        yLbpLimit.lineWidth = 1f
        yLbpLimit.enableDashedLine(10f, 10f, 0f)
     //   yLeft.addLimitLine(yLbpLimit)


        initLinData(bpHomeLinChartView, arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())
    }

    private fun initLinData(chart : LineChart,hbpList : ArrayList<Int>,lbpList : ArrayList<Int>,inputHList : ArrayList<Int>,inputLList : ArrayList<Int>){
        val dataSets = ArrayList<ILineDataSet>()

        //手动输入的高压
        val inputHeightValue = ArrayList<Entry>()
        inputHList.forEachIndexed { index, i ->
            inputHeightValue.add(Entry(index.toFloat(),i.toFloat()))
        }

        val inputHD = LineDataSet(inputHeightValue,"")
        inputHD.lineWidth = 1.5f
        inputHD.circleRadius = 3.5f
        inputHD.highLightColor = Color.rgb(244, 117, 117)
        //虚线
        inputHD.enableDashedLine(10f, 10f, 0f)
        inputHD.enableDashedHighlightLine(10f, 10f, 0f)
        inputHD.color = Color.WHITE
        inputHD.setDrawValues(false)

        //手动输入低压
        val inputLowBpValue = ArrayList<Entry>()
        inputLList.forEachIndexed { index, i ->
            inputLowBpValue.add(Entry(index.toFloat(),i.toFloat()))
        }

        val inputLD = LineDataSet(inputLowBpValue,"")
        inputLD.lineWidth = 1.5f
        inputLD.circleRadius = 3.5f
        inputLD.highLightColor = Color.rgb(244, 117, 117)
        //虚线
        inputLD.enableDashedLine(10f, 10f, 0f)
        inputLD.enableDashedHighlightLine(10f, 10f, 0f)
        inputLD.color = Color.WHITE
        inputLD.setDrawValues(false)


        val values1 = ArrayList<Entry>()
        //高压
        hbpList.forEachIndexed { index, i ->
            values1.add(Entry(index.toFloat(),i.toFloat()))
        }

        val d1 = LineDataSet(values1, "")
        d1.lineWidth = 2.0f
        d1.circleRadius = 3.5f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.color = Color.parseColor("#71FBEE")
        d1.setDrawValues(false)

        //低压
        val values2 = ArrayList<Entry>()

        lbpList.forEachIndexed { index, i ->
            values2.add(Entry(index.toFloat(),i.toFloat()))
        }


        val d2 = LineDataSet(values2, "")
        d2.lineWidth = 2.0f
        d2.circleRadius = 3.5f
        d2.highLightColor = Color.rgb(244, 117, 117)
        d2.color = Color.parseColor("#FBD371")
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0])
        d2.setDrawValues(false)

        val sets = ArrayList<ILineDataSet>()
        sets.add(d1)
        sets.add(d2)

        sets.add(inputHD)
        sets.add(inputLD)

        val data = LineData(sets)
        chart.data = data
        chart.invalidate()


//
//        for (z in 0..2) {
//            val values = ArrayList<Entry>()
//            for (i in 0 until 24) {
//                val `val`: Float = Random(120).nextFloat()
//                values.add(Entry(i.toFloat(), `val`.toFloat()))
//            }
//            val d = LineDataSet(values, "DataSet " + (z + 1))
//            d.lineWidth = 2.5f
//            d.circleRadius = 4f
//            val color: Int = Color.WHITE
//            d.color = color
//            d.setCircleColor(color)
//            dataSets.add(d)
//        }
//
//        // make the first DataSet dashed
//
//        // make the first DataSet dashed
////        (dataSets[0] as LineDataSet).enableDashedLine(10f, 10f, 0f)
////        (dataSets[0] as LineDataSet).setColors(*ColorTemplate.VORDIPLOM_COLORS)
////        (dataSets[0] as LineDataSet).setCircleColors(*ColorTemplate.VORDIPLOM_COLORS)
//
//        val data = LineData(dataSets)
//        chart.data = data
//        chart.invalidate()
    }



    private var calendar: Calendar? = null
    private var toDay = DateUtil.getCurrentDate()
    private fun timeDialog() {
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
                    setTitleDateData()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }
}