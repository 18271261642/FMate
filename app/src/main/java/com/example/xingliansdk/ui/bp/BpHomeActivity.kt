package com.example.xingliansdk.ui.bp

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.BloodPressureHistoryAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.bean.room.BloodPressureHistoryBean
import com.example.xingliansdk.bean.room.BloodPressureHistoryDao
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.dialog.OnCommDialogClickListener
import com.example.xingliansdk.dialog.PromptCheckBpDialog
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureViewModel
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureVoBean
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.MapUtils
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
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
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * 新版血压首页面
 * Created by Admin
 *Date 2022/5/7
 */
class BpHomeActivity : BaseActivity<BloodPressureViewModel>(),View.OnClickListener,OnChartValueSelectedListener{


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

    //x轴数据 HH:mm格式
    var xValue : ArrayList<String> = arrayListOf()

    //是否需要校准，只判断当天的日期
    private var isNeedCheckBp = true
    //需要校准的提示文字
    private var checkDescStr : String ?= null


    //最后一个有值的下标
    private var lastValueMap = HashMap<Int,Int>()
    private var lastValidValue = 0



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

        imgReplicate.rotation=90f
        llBloodPressureIndex.visibility=View.GONE
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

        var bean = intent.getSerializableExtra("bean") as HomeCardVoBean.ListDTO
        var toDayTime = System.currentTimeMillis()
        if (bean.startTime > 0)
            toDayTime = bean.startTime * 1000L
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar(toDayTime))

        initData();

      //  showPromptDialog()

//        setTitleDateData()

       // getDateBpData(DateUtil.getCurrDate())


    }


    private fun initData(){
        sDao = AppDataBase.instance.getBloodPressureHistoryDao()
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        ryBloodPressure.layoutManager = mLinearLayoutManager
        mBloodPressureHistoryAdapter = BloodPressureHistoryAdapter(bpList)
        ryBloodPressure.adapter = mBloodPressureHistoryAdapter

        mBloodPressureHistoryAdapter.setOnDelListener(object :BloodPressureHistoryAdapter.onSwipeListener{
            override fun onDel(pos: Int) {
                TLog.error("点击删除")

                if(pos>=0)
                {

                    if(!HelpUtil.netWorkCheck(this@BpHomeActivity)) {
                        if(mBloodPressureHistoryAdapter.data[pos].type==0) {
                            ShowToast.showToastLong(getString(R.string.err_network_delete))
                            return
                        }
                    }

                    if(mBloodPressureHistoryAdapter.data[pos].type==1) {
                        ShowToast.showToastLong("无法删除哦!")
                        return
                    }

                   // showWaitDialog("删除血压中...")
                    var value = HashMap<String, String>()
                    value["createTime"] = mBloodPressureHistoryAdapter.data[pos].startTime.toString()
                    sDao.deleteTime(mBloodPressureHistoryAdapter.data[pos].startTime)
                    mBloodPressureHistoryAdapter.notifyItemRemoved(pos)

                    if(HelpUtil.netWorkCheck(this@BpHomeActivity)
                        &&mBloodPressureHistoryAdapter.data[pos].type==0) {
                        TLog.error("删除")
                        mViewModel.deleteBloodPressure(value)
                    }
                    else
                        hideWaitDialog()
                    setTitleDateData()
//                    if (mBloodPressureHistoryAdapter.data.size > 0)
//                        homeCard(mBloodPressureHistoryAdapter.data[0].systolicBloodPressure,
//                            mBloodPressureHistoryAdapter.data[0].diastolicBloodPressure)//如果最新一个被删除的时候 要更新首页
//                    else
//                        homeCard(0,0)//如果最新一个被删除的时候 要更新首页

                }
            }

            override fun onClick(pos: Int) {
                TLog.error("==mlsit=="+Gson().toJson(bpList[pos]))
                mDiastolic = bpList[pos].diastolicBloodPressure
                mEdtSystolic = bpList[pos].systolicBloodPressure
                time = bpList[pos].startTime*1000
                mViewModel.setBloodPressure(this@BpHomeActivity,bpList[pos].startTime,bpList[pos].systolicBloodPressure
                    ,bpList[pos].diastolicBloodPressure)
            }
        })

        initLinChart()



    }


    private fun vertifyBind(){
        val userInfo = Hawk.get(Config.database.USER_INFO, LoginBean())
        TLog.error("----userInfp="+Gson().toJson(userInfo))
        if(TextUtils.isEmpty(userInfo.user.mac)){
            showPromptDialog(true)
        }
    }

    override fun onResume() {
        super.onResume()
        setTitleDateData()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateBpData(list : List<BloodPressureVoBean.ListDTO>){
        inputHeightBpList.clear()
        inputLowBpList.clear()
        heightList.clear()
        lowBpList.clear()
        xValue.clear()

        bpList.clear()

        bpCheckTimeTv.text = "--"
        bpHomeMeasureSelectTv.text = "--"
        bpHomeMeasureInputTv.text = "--"

//        bpList = sDao.getDayBloodPressureHistory(
//            day
//        ) as ArrayList<BloodPressureHistoryBean>


        var tmpBpList = ArrayList<BloodPressureHistoryBean>()
        list.forEach {
            var bp = BloodPressureHistoryBean(
                it.stampCreateTime, 0, it.dataSource , it.systolicPressure, it.diastolicPressure,
                it.createTime
            )

            TLog.error("--------bpppp="+it.toString()+" "+Gson().toJson(bp))
            tmpBpList.add(bp)
        }


        bpList = tmpBpList
        TLog.error("------DB="+Gson().toJson(bpList))

        mBloodPressureHistoryAdapter.data.clear()
        mBloodPressureHistoryAdapter.addData(bpList)
        mBloodPressureHistoryAdapter.notifyDataSetChanged()


        //手动输入的血压
        val resultInputHourMap = HashMap<String,IntArray>()
        //测量的血压
        val resultMeasureHourMap = HashMap<String,IntArray>()

        //排序 根据时间戳排序
        bpList.sortedBy {
            it.startTime
        }

        if(bpList.size==0){
            lastValidValue = 0
            initLinData(bpHomeLinChartView,
                heightList,lowBpList,inputHeightBpList,inputLowBpList)
            return
        }
        //第一个有效值的值
        val firstValue = bpList[bpList.size-1]

        val firstResultHourStr = DateUtil.getFormatHour(firstValue.startTime * 1000)

        //最后一个有效值
        val lastValue = bpList[0]
        val lastResultHourStr = DateUtil.getFormatHour(lastValue.startTime * 1000)

        TLog.error("-------第一个有效值的值="+firstResultHourStr+" Size="+bpList.size)


//        bpList.forEachIndexed { index, bp ->
//
//
//        }

        //展示图表
        bpList.forEachIndexed {index, bp ->

            //HH格式
            val timeStr = DateUtil.getDate(
                DateUtil.HOUR, bp.startTime * 1000
            )
            //分钟
            val timeMinuteStr = DateUtil.getDate(
                DateUtil.MINUTE, bp.startTime * 1000
            )

            var resultHourStr = DateUtil.getFormatHour(bp.startTime * 1000)

            TLog.error("----小时=$timeStr $timeMinuteStr $resultHourStr" +" type="+bp.type)

            if (bp.type == 0) {   //手动输入

                TLog.error("--11----inputDataaaa="+Gson().toJson(resultInputHourMap))


                if (resultInputHourMap[resultHourStr] != null) {
                    val bpArray = resultInputHourMap[resultHourStr]
                    //高压
                    val hBp = bpArray?.get(0)?.toInt()
                    //低压
                    val lBp = bpArray?.get(1)?.toInt()

                    val tmpHBp = hBp?.plus(bp.systolicBloodPressure)

                    val tmpLBp = lBp?.plus(bp.diastolicBloodPressure)

                    val tmpBpArray = tmpHBp?.div(2)
                        ?.let { it1 ->
                            tmpLBp?.div(2)
                                ?.let { it2 -> intArrayOf(it1.toInt(), it2.toInt()) }
                        }

                    resultInputHourMap[resultHourStr] = tmpBpArray as IntArray
                } else {
                    val intarrays = intArrayOf(bp.systolicBloodPressure, bp.diastolicBloodPressure)
                    resultInputHourMap[resultHourStr] = intarrays

                }
                TLog.error("--22----inputDataaaa="+Gson().toJson(resultInputHourMap))
            } else {
                if(resultMeasureHourMap[resultHourStr] != null){
                    val bpArray = resultMeasureHourMap[resultHourStr]
                    //高压
                    val hBp = bpArray?.get(0)?.toInt()
                    //低压
                    val lBp = bpArray?.get(1)?.toInt()

                    val tmpHBp = hBp?.plus(bp.systolicBloodPressure)

                    val tmpLBp = lBp?.plus(bp.diastolicBloodPressure)

                    val tmpBpArray = tmpHBp?.div(2)
                        ?.let { it1 ->
                            tmpLBp?.div(2)
                                ?.let { it2 -> intArrayOf(it1.toInt(), it2.toInt()) }
                        }

                    resultMeasureHourMap[resultHourStr] = tmpBpArray as IntArray

                }else{
                    val intarrays = intArrayOf(bp.systolicBloodPressure, bp.diastolicBloodPressure)
                    resultMeasureHourMap.put(resultHourStr,intarrays)
                }

            }
        }
            TLog.error("------处理后的血压="+Gson().toJson(resultInputHourMap))


        var isLength = resultInputHourMap.size > resultMeasureHourMap.size

        MapUtils.halfHourMap.forEachIndexed { index, sH ->
            xValue.add(sH)
            var tmpHP = 0
            var tmpLP = 0

            var tmpMHBp = 0
            var tmpMLBp = 0

            resultInputHourMap.forEach { (s, ints) ->
                if(isLength){
                  //  xValue.add(sH)
                }
                if(sH == s){
                    val bpA = ints
                    tmpHP = bpA[0]
                    tmpLP = bpA[1]
                }
            }

            TLog.error("-----集合血压="+tmpHP+" "+tmpLP)
            inputHeightBpList.add(tmpHP)
            inputLowBpList.add(tmpLP)


            resultMeasureHourMap.forEach { s, ints ->
                if(!isLength){
                   // xValue.add(sH)
                }
                if(sH == s){
                    val bpA = ints
                    tmpMHBp = bpA[0]
                    tmpMLBp = bpA[1]
                }
            }

            heightList.add(tmpMHBp)
            lowBpList.add(tmpMLBp)

        }


        val tmpXVList = mutableListOf<String>()

        //截取X轴，从第一个不为0的值开始，到23:30
        var tmpIndex = 0
        var tmpLastIndex = 0
        xValue.forEachIndexed { index, s ->

            if(s == firstResultHourStr){
                tmpIndex = index;
            }

            //最后一个有效值
            if(lastResultHourStr == s){
                tmpLastIndex = index
            }
        }

        tmpXVList.addAll(xValue)
        xValue.clear()

        lastValidValue = tmpLastIndex - tmpIndex

        xValue.addAll(tmpXVList.subList(tmpIndex,tmpLastIndex+1))





        val tmpInputList :  ArrayList<Int> = arrayListOf()
        val tmpInputLowList : ArrayList<Int> = arrayListOf()

        tmpInputList.addAll(inputHeightBpList)
        inputHeightBpList.clear()

        inputHeightBpList.addAll(tmpInputList.subList(tmpIndex,tmpLastIndex+1))

        tmpInputLowList.addAll(inputLowBpList)
        inputLowBpList.clear()
        inputLowBpList.addAll(tmpInputLowList.subList(tmpIndex,tmpLastIndex+1))



        //测量的
        val tmpMHList : ArrayList<Int> = arrayListOf()
        val tmpMLList : ArrayList<Int> = arrayListOf()
        tmpMHList.addAll(heightList)
        heightList.clear()
        heightList.addAll(tmpMHList.subList(tmpIndex,tmpLastIndex+1))


        tmpMLList.addAll(lowBpList)
        lowBpList.clear()
        lowBpList.addAll(tmpMLList.subList(tmpIndex,tmpLastIndex+1))


        if(xValue.size<7){
            MapUtils.halfHourMap.forEachIndexed { index, str ->

            }

        }

        TLog.error("------处理后的血压轴="+Gson().toJson(xValue))

        initLinData(bpHomeLinChartView,
            heightList,lowBpList,inputHeightBpList,inputLowBpList)
    }



    override fun createObserver() {
        super.createObserver()
        //返回指定日期的血压数据

        mViewModel.resultGet.observe(this){ it ->
            TLog.error("----获取血压返回="+Gson().toJson(it))
            if(!it.isCalibrationRequired ){   //没有校准，需要校准
                isNeedCheckBp = false

               // showPromptDialog()
            }
            this.checkDescStr = it.calibrationReason
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
            updateBpData(it.list)
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
                 //   showInputDialog()
                    startActivity(Intent(this,InputBpActivity::class.java))
                }
                R.id.bpHomeMeasureLayout-> { //测量

                    var bindMac = Hawk.get("address", "")

                    TLog.error("----userInfp=" + Gson().toJson(userInfo)+"\n"+isConntinue)
                    if (TextUtils.isEmpty(bindMac) ) {    //绑定
                        showPromptDialog(true)
                        return
                    }

                    if (!XingLianApplication.getXingLianApplication()
                            .getDeviceConnStatus() || BleConnection.iFonConnectError
                    ) {
                        ShowToast.showToastShort("请连接设备")
                        return
                    }
                    if (isNeedCheckBp ) {
                        showPromptDialog(false)
                        return
                    }
                    startActivity(Intent(this, MeasureNewBpActivity::class.java))
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

    private var isConntinue = false


    private fun showPromptDialog(isBind : Boolean){
        if(promptBpDialog == null){
            promptBpDialog = PromptCheckBpDialog(this,R.style.edit_AlertDialog_style)
        }
        promptBpDialog!!.show()
        promptBpDialog!!.setTopTxtValue(if(isBind) resources.getString(R.string.string_no_bind_device_txt) else resources.getString(R.string.string_check_bp_first_user_txt))
        promptBpDialog!!.setBotBtnTxt(if(isBind) "暂不绑定" else "暂不校准",if(isBind) "去绑定" else "去校准")
        promptBpDialog!!.setCancelable(false)
        promptBpDialog!!.setVisibilityBotTv(!isBind)
        if(!isBind){
            promptBpDialog!!.setTopTxtValue(checkDescStr)
        }
        promptBpDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) {
                promptBpDialog!!.dismiss()
                if(isBind){

                    if (!turnOnBluetooth()) {
                        return
                    }
                    JumpUtil.startBleConnectActivity(this@BpHomeActivity)
                    finish()
                }

                else
                startActivity(Intent(this@BpHomeActivity,BpCheckActivity::class.java))
            }

            override fun onCancelClick(code: Int) {
                if(!isBind){
                    startActivity(Intent(this@BpHomeActivity, MeasureNewBpActivity::class.java))
                }

            }

        })
    }


    var mDiastolic = 0
    var mEdtSystolic = 0

    var time = System.currentTimeMillis()



    private fun initLinChart(){
        bpHomeLinChartView.setNoDataText("无数据")
        bpHomeLinChartView.setNoDataTextColor(Color.WHITE)
        bpHomeLinChartView.description.isEnabled = false
        bpHomeLinChartView.setDrawBorders(false)
        bpHomeLinChartView.axisRight.setDrawAxisLine(false)
        bpHomeLinChartView.axisRight.setDrawLabels(false)
        bpHomeLinChartView.legend.isEnabled = false

        bpHomeLinChartView.isScaleYEnabled = false
        bpHomeLinChartView.setScaleEnabled(false)

        val xAxis = bpHomeLinChartView.xAxis

        bpHomeLinChartView.setOnChartValueSelectedListener(this)

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.parseColor("#EBEBEB")
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawLabels(true)
        xAxis.setCenterAxisLabels(true)
//        xAxis.axisMaxLabels = 48
        xAxis.setLabelCount(8,true)


        //右侧Y轴
        val yRight = bpHomeLinChartView.axisRight
        yRight.isEnabled = false

        //左侧Y轴
        val yLeft = bpHomeLinChartView.axisLeft
        yLeft.setLabelCount(15)
       // yLeft.granularity = 50f

        yLeft.axisMinimum-0.1f

        //标线虚线
        yLeft.enableGridDashedLine(10f, 10f, 0f);
        //是否显示标线
        yLeft.setDrawGridLines(true)
        yLeft.setDrawAxisLine(true)

        yLeft.gridColor = Color.TRANSPARENT
        yLeft.textColor = Color.WHITE
        yLeft.axisLineColor = Color.TRANSPARENT
        yLeft.isEnabled = true


        yLeft.axisMaximum = 190f
        yLeft.axisMinimum = 40f

        //格式化Y轴
        yLeft.valueFormatter =
            IAxisValueFormatter { value, axis ->
//                TLog.error("------yyyyyy="+value+" "+value.toInt())
               // return@IAxisValueFormatter value.toInt().toString()
                if(value.toInt() == 90 || value.toInt() == 140){
                    yLeft.textColor = Color.WHITE
                    yLeft.gridColor = Color.TRANSPARENT
                    yLeft.setDrawGridLines(true)
                    return@IAxisValueFormatter value.toInt().toString()
                }else{
                    return@IAxisValueFormatter ""
                }
            }


        val yHbpLimit = LimitLine(140f,"")
        yHbpLimit.lineColor = Color.WHITE
        yHbpLimit.lineWidth = 1f
        yHbpLimit.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        yHbpLimit.enableDashedLine(10f, 10f, 0f)
        yHbpLimit.textColor = Color.WHITE
        yLeft.removeLimitLine(yHbpLimit)
       yLeft.addLimitLine(yHbpLimit)

        val yLbpLimit = LimitLine(90f,"")
        yLbpLimit.lineColor = Color.WHITE
        yLbpLimit.textColor = Color.WHITE
        yLbpLimit.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        yLbpLimit.lineWidth = 1f
        yLbpLimit.enableDashedLine(10f, 10f, 0f)
        yLeft.removeLimitLine(yLbpLimit)
        yLeft.addLimitLine(yLbpLimit)


        initLinData(bpHomeLinChartView, arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())
    }

    //用于计算第一个点的下标，0到48
    private var firstNoZero = 0
    private var firstNoData : String ?= null


    @RequiresApi(Build.VERSION_CODES.N)
    private fun initLinData(chart : LineChart, hbpList : ArrayList<Int>, lbpList : ArrayList<Int>, inputHList : ArrayList<Int>, inputLList : ArrayList<Int>){
        val dataSets = ArrayList<ILineDataSet>()


        //设置一页最大显示个数为6，超出部分就滑动
       // float ratio = (float) xValueList.size()/(float) 6;
        //显示的时候是按照多大的比率缩放显示,1f表示不放大缩小
       // mLineChart.zoom(ratio,1f,0,0);
        val xAxis = bpHomeLinChartView.xAxis

        val maxLength = hbpList.size>inputHList.size

        val matrix = Matrix()

//        matrix.preScale(2.5f,1f)
//
//        bpHomeLinChartView.viewPortHandler.refresh(matrix,bpHomeLinChartView,false)




        //手动输入的高压
        val inputHeightValue = ArrayList<Entry>()

        TLog.error("---手动输入=高压="+Gson().to(inputHList))

        inputHList.forEachIndexed { index, i ->
            if(i != 0){
                TLog.error("---不为0-手动x="+index+" "+i)
                inputHeightValue.add(Entry(index.toFloat(),i.toFloat()))
            }

        }


          val inputHD = LineDataSet(inputHeightValue,"")

           inputHD.lineWidth = 1.5f
           inputHD.circleRadius = 2.0f
          // inputHD.highLightColor = Color.rgb(255, 255, 255)
           inputHD.formLineDashEffect = DashPathEffect(floatArrayOf(10f,5f),0f)
           inputHD.formLineWidth = 1f
           //虚线
           inputHD.enableDashedLine(10f, 10f, 0f)
            inputHD.circleHoleColor = Color.TRANSPARENT
//           inputHD.enableDashedHighlightLine(10f, 10f, 0f)
           inputHD.color =Color.parseColor("#71FBEE")
            inputHD.circleColors = arrayListOf(Color.parseColor("#71FBEE"))
           // inputHD.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
           inputHD.setDrawValues(false)

           //手动输入低压
           val inputLowBpValue = ArrayList<Entry>()
           inputLList.forEachIndexed { index, i ->
               if(i != 0){
                   inputLowBpValue.add(Entry(index.toFloat(),i.toFloat()))
               }

           }

           val inputLD = LineDataSet(inputLowBpValue,"")
           inputLD.lineWidth = 1.5f
           inputLD.circleRadius = 2.0f
          // inputLD.highLightColor = Color.rgb(244, 117, 117)

           //虚线
            inputLD.enableDashedLine(10f, 10f, 0f)
           inputLD.enableDashedHighlightLine(10f, 10f, 0f)
           inputLD.circleHoleColor = Color.TRANSPARENT
           inputLD.color = Color.parseColor("#FBD371")
           inputLD.circleColors = arrayListOf(Color.parseColor("#FBD371"))
           inputLD.setDrawValues(false)


           val values1 = ArrayList<Entry>()
           //高压
           hbpList.forEachIndexed { index, i ->
               if(i != 0){
                   values1.add(Entry(index.toFloat(),i.toFloat()))
               }
           }


           // lastValueMap[inputLowBpValue[inputLowBpValue.size-1].x.toInt()] = values1[values1.size-1].x.toInt()

           val d1 = LineDataSet(values1, "")
           d1.lineWidth = 1.5f
           d1.circleRadius = 2.5f
           d1.setDrawCircleHole(false)
           d1.color = Color.parseColor("#71FBEE")
            d1.setCircleColor(Color.parseColor("#71FBEE"))
           d1.setDrawValues(false)

           //低压
           val values2 = ArrayList<Entry>()
           lbpList.forEachIndexed { index, i ->
               if(i != 0){
                   values2.add(Entry(index.toFloat(),i.toFloat()))
               }
           }

           val d2 = LineDataSet(values2, "")
           d2.lineWidth = 1.5f
           d2.circleRadius = 2.5f
          // d2.highLightColor = Color.rgb(244, 117, 117)
            d2.setDrawCircleHole(false)
           d2.color = Color.parseColor("#FBD371")
         // d2.circleHoleColor = Color.parseColor("#FBD371")
           d2.setCircleColor(Color.parseColor("#FBD371"))
           d2.setDrawValues(false)



        val tmp24EntryList = ArrayList<Entry>()
        xValue.forEachIndexed { index, s ->
            tmp24EntryList.add(Entry(index.toFloat(),100f))
        }
        val dd2 = LineDataSet(tmp24EntryList,"")
        dd2.color = Color.parseColor("#1D8BEE")
        dd2.lineWidth = -1f
        dd2.circleRadius = 1f
        //dd2.highLightColor = Color.rgb(255, 255, 255)
        dd2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0])
        // dd2.enableDashedLine(15f, 10f, 0f)
        dd2.setCircleColor(Color.TRANSPARENT)
        dd2.circleHoleColor = Color.TRANSPARENT
        dd2.setDrawValues(false)



        val sets = ArrayList<ILineDataSet>()
           sets.add(d1)
           sets.add(d2)

           sets.add(inputHD)
           sets.add(inputLD)

           sets.add(dd2)
           var tmpCount = 0
           TLog.error("-----x轴="+Gson().toJson(xValue))
           //x轴
           if(xValue.size>0){
               tmpCount = 0

               xAxis.valueFormatter = BPXValueFormatter(xValue)

               xAxis.setValueFormatter { value, axis ->
                   val index = value.toInt()
                     TLog.error("-----x轴index="+index+" "+axis.mEntries[0])
                   if(index<0 || index >= xValue.size){
                       return@setValueFormatter ""
                   }else{
                       val tmpIndex = value % xValue.size
                       if(tmpIndex >=0){
                           return@setValueFormatter xValue.get(index)
                       }else{
                           return@setValueFormatter ""
                       }
                   }
               }
           }

           val data = LineData(sets)
           chart.data = data
           chart.invalidate()


        showSelectData()

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



    private fun showSelectData(){
        try {
            if(lastValidValue<xValue.size){

                if(lastValidValue+1 <=xValue.size-1){
                    bpCheckTimeTv.text = xValue.get(lastValidValue) +"~"+xValue.get(lastValidValue+1)
                }else{
                    bpCheckTimeTv.text = xValue[lastValidValue]
                }

            }
                //手动测量的高压集合
                if(heightList.size>0 && lastValidValue<heightList.size-1){
                    val tmpH = heightList[lastValidValue].toString()
                    val tmpL = lowBpList[lastValidValue].toString()
                    bpHomeMeasureSelectTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureSelectTv.text = "--"
                }

                //输入
                if(inputHeightBpList.size>0 && lastValidValue<inputLowBpList.size-1){
                    val tmpH = inputHeightBpList[lastValidValue].toString()
                    val tmpL = inputLowBpList[lastValidValue].toString()
                    bpHomeMeasureInputTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureInputTv.text = "--"
                }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
        try {
            if (e != null) {
                TLog.error("------select="+e.x+" "+e.y)
                val x = e.x.toInt()
                if(x+1 <=xValue.size-1){
                    bpCheckTimeTv.text = xValue.get(e.x.toInt()) +"~"+xValue.get(x+1)
                }else{
                    bpCheckTimeTv.text = xValue.get(e.x.toInt())
                }


                heightList.forEachIndexed { index, i ->
                    //TLog.error("------高压====="+index+"-="+i)
                }

                //手动测量的高压集合
                if(heightList.size>0 && e.x.toInt()<heightList.size-1){
                    val tmpH = heightList[e.x.toInt()].toString()
                    val tmpL = lowBpList[e.x.toInt()].toString()
                    bpHomeMeasureSelectTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureSelectTv.text = "--"
                }
                //输入
                if(inputHeightBpList.size>0 && e.x.toInt()<inputLowBpList.size-1){
                    val tmpH = inputHeightBpList[e.x.toInt()].toString()
                    val tmpL = inputLowBpList[e.x.toInt()].toString()
                    bpHomeMeasureInputTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureInputTv.text = "--"
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onNothingSelected() {

    }
}