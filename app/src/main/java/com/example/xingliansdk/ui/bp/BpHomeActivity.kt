package com.example.xingliansdk.ui.bp

import android.content.Intent
import android.graphics.Color
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
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.MapUtils
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
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
import java.text.DecimalFormat
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


    //最后一个有值的下标
    private var lastValueMap = HashMap<Int,Int>()

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

        initData();

      //  showPromptDialog()

//        setTitleDateData()

       // getDateBpData(DateUtil.getCurrDate())

        //判断是否绑定手表，未绑定提示绑定
        vertifyBind()
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
    private fun updateBpData(day : String){
        inputHeightBpList.clear()
        inputLowBpList.clear()
        heightList.clear()
        lowBpList.clear()
        xValue.clear()

        bpList.clear()

        bpCheckTimeTv.text = "--"
        bpHomeMeasureSelectTv.text = "--"
        bpHomeMeasureInputTv.text = "--"

        bpList = sDao.getDayBloodPressureHistory(
            day
        ) as ArrayList<BloodPressureHistoryBean>



        mBloodPressureHistoryAdapter.data.clear()
        mBloodPressureHistoryAdapter.addData(bpList)
        mBloodPressureHistoryAdapter.notifyDataSetChanged()


        //手动输入的血压
        val resultInputHourMap = HashMap<String,IntArray>()
        //测量的血压
        val resultMeasureHourMap = HashMap<String,IntArray>()



        //展示图表
        bpList.forEach {

            TLog.error("----排序="+it.dateTime)

            //HH格式
            val timeStr = DateUtil.getDate(
                DateUtil.HOUR, it.startTime * 1000
            )
            //分钟
            val timeMinuteStr = DateUtil.getDate(
                DateUtil.MINUTE, it.startTime * 1000
            )

            val resultHourStr = DateUtil.getFormatHour(it.startTime * 1000)

            TLog.error("----小时=$timeStr $timeMinuteStr $resultHourStr")

            if (it.type == 0) {   //手动输入
                if (resultInputHourMap[resultHourStr] != null) {
                    val bpArray = resultInputHourMap[resultHourStr]
                    //高压
                    val hBp = bpArray?.get(0)?.toInt()
                    //低压
                    val lBp = bpArray?.get(1)?.toInt()

                    val tmpHBp = hBp?.plus(it.systolicBloodPressure)

                    val tmpLBp = lBp?.plus(it.diastolicBloodPressure)

                    val tmpBpArray = tmpHBp?.div(2)
                        ?.let { it1 ->
                            tmpLBp?.div(2)
                                ?.let { it2 -> intArrayOf(it1.toInt(), it2.toInt()) }
                        }

                    resultInputHourMap[resultHourStr] = tmpBpArray as IntArray
                } else {
                    val intarrays = intArrayOf(it.systolicBloodPressure, it.diastolicBloodPressure)
                    resultInputHourMap[resultHourStr] = intarrays

                }

            } else {
                if(resultMeasureHourMap[resultHourStr] != null){
                    val bpArray = resultMeasureHourMap[resultHourStr]
                    //高压
                    val hBp = bpArray?.get(0)?.toInt()
                    //低压
                    val lBp = bpArray?.get(1)?.toInt()

                    val tmpHBp = hBp?.plus(it.systolicBloodPressure)

                    val tmpLBp = lBp?.plus(it.diastolicBloodPressure)

                    val tmpBpArray = tmpHBp?.div(2)
                        ?.let { it1 ->
                            tmpLBp?.div(2)
                                ?.let { it2 -> intArrayOf(it1.toInt(), it2.toInt()) }
                        }

                    resultMeasureHourMap[resultHourStr] = tmpBpArray as IntArray

                }else{
                    val intarrays = intArrayOf(it.systolicBloodPressure, it.diastolicBloodPressure)
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

        initLinData(bpHomeLinChartView,heightList,lowBpList,inputHeightBpList,inputLowBpList)
    }



    override fun createObserver() {
        super.createObserver()
        //返回指定日期的血压数据

        mViewModel.resultGet.observe(this){ it ->
            TLog.error("----获取血压返回="+Gson().toJson(it))
            if(!it.isCalibrationRequired && currDayStr == DateUtil.getCurrDate()){   //没有校准，需要校准
                isNeedCheckBp = false
               // showPromptDialog()
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
                 //   showInputDialog()
                    startActivity(Intent(this,InputBpActivity::class.java))
                }
                R.id.bpHomeMeasureLayout->{ //测量
//                    val userInfo = Hawk.get(Config.database.USER_INFO, LoginBean())
//                    TLog.error("----userInfp="+Gson().toJson(userInfo))
//                    if(TextUtils.isEmpty(userInfo.user.mac)){
//                        showPromptDialog(true)
//                        return
//                    }

                    if(!XingLianApplication.getXingLianApplication().getDeviceConnStatus() || BleConnection.iFonConnectError){
                        ShowToast.showToastShort("请连接设备")
                        return
                    }
                    if(isNeedCheckBp){
                        showPromptDialog(false)
                        return
                    }
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


    private fun showPromptDialog(isBind : Boolean){
        if(promptBpDialog == null){
            promptBpDialog = PromptCheckBpDialog(this,R.style.edit_AlertDialog_style)
        }
        promptBpDialog!!.show()
        promptBpDialog!!.setTopTxtValue(if(isBind) resources.getString(R.string.string_no_bind_device_txt) else resources.getString(R.string.string_check_bp_first_user_txt))
        promptBpDialog!!.setBotBtnTxt(if(isBind) "暂不绑定" else "暂不校准",if(isBind) "去绑定" else "去校准")
        promptBpDialog!!.setCancelable(false)
        promptBpDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) {
                promptBpDialog!!.dismiss()
                if(isBind){

                    if (!turnOnBluetooth()) {
                        return
                    }
                    JumpUtil.startBleConnectActivity(this@BpHomeActivity)
                }

                else
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
//        xAxis.setLabelCount(48,true)

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
               // TLog.error("------yyyyyy="+value+" "+value.toInt())
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

        matrix.preScale(2.5f,1f)

        bpHomeLinChartView.viewPortHandler.refresh(matrix,bpHomeLinChartView,false)


//        bpHomeLinChartView.zoom(1.5f,1f,0f,0f)




//        if(maxLength){
//            if(hbpList.size>7){
//                bpHomeLinChartView.zoom(2f,1f,0f,0f)
//            }else{
//                xAxis.setLabelCount(7,true)
//                bpHomeLinChartView.zoom(1.5f,1f,0f,0f)
//            }
//        }else{
//            if(inputHList.size>7){
//                bpHomeLinChartView.zoom(2f,1f,0f,0f)
//            }else{
//                xAxis.setLabelCount(7,true)
//                bpHomeLinChartView.zoom(1.5f,1f,0f,0f)
//            }
//        }






        //手动输入的高压
        val inputHeightValue = ArrayList<Entry>()

        val dd2 = LineDataSet(ArrayList<Entry>(),"")
        dd2.color = Color.TRANSPARENT

        TLog.error("---手动输入=高压="+Gson().to(inputHList))
        inputHList.forEachIndexed { index, i ->
            if(i != 0){
                TLog.error("---不为0-手动x="+index+" "+i)
                inputHeightValue.add(Entry(index.toFloat(),i.toFloat()))
            }

        }

        val inputHD = LineDataSet(inputHeightValue,"")
        inputHD.lineWidth = 1.5f
        inputHD.circleRadius = 3.5f
        inputHD.highLightColor = Color.rgb(244, 117, 117)
        //虚线
        inputHD.enableDashedLine(10f, 10f, 0f)
        inputHD.enableDashedHighlightLine(10f, 10f, 0f)
        inputHD.color = Color.WHITE
        inputHD.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
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
            if(i != 0){
                values1.add(Entry(index.toFloat(),i.toFloat()))
            }
        }


       // lastValueMap[inputLowBpValue[inputLowBpValue.size-1].x.toInt()] = values1[values1.size-1].x.toInt()

        val d1 = LineDataSet(values1, "")
        d1.lineWidth = 2.0f
        d1.circleRadius = 3.5f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.color = Color.parseColor("#71FBEE")
        d1.setDrawValues(false)

        //低压
        val values2 = ArrayList<Entry>()

        lbpList.forEachIndexed { index, i ->
            if(i != 0){
                values2.add(Entry(index.toFloat(),i.toFloat()))
            }
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
        sets.add(dd2)

        sets.add(inputHD)
        sets.add(inputLD)


        var tmpCount = 0
        TLog.error("-----x轴="+Gson().toJson(xValue))
        //x轴
        if(xValue.size>0){
            tmpCount = 0

            xAxis.setValueFormatter { value, axis ->
                val index = value.toInt()
                if(index<0 || index >= xValue.size){
                    return@setValueFormatter ""
                }else{
                    return@setValueFormatter xValue.get(index)
                }

            }
        }


        val data = LineData(sets)
        chart.data = data
        chart.invalidate()


        if(lastValueMap.size>0){

            lastValueMap.forEach { (t, u) ->
                showSelectData(t.toString(),u.toString())
            }
        }

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



    private fun showSelectData(x : String,y : String){
        try {
            if (x != null) {
               // bpCheckTimeTv.text = xValue.get(x.toInt())

                heightList.forEachIndexed { index, i ->
                    TLog.error("------高压====="+index+"-="+i)
                }
                //手动测量的高压集合
                if(heightList.size>0 && x.toInt()<heightList.size-1){
                    val tmpH = heightList[x.toInt()].toString()
                    val tmpL = lowBpList[x.toInt()].toString()
                    bpHomeMeasureSelectTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureSelectTv.text = "--"
                }

                //输入
                if(inputHeightBpList.size>0 && y.toInt()<inputLowBpList.size-1){
                    val tmpH = inputHeightBpList[y.toInt()].toString()
                    val tmpL = inputLowBpList[y.toInt()].toString()
                    bpHomeMeasureInputTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureInputTv.text = "--"
                }

            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
        try {
            if (e != null) {
                TLog.error("------select="+e.x+" "+e.y)

                bpCheckTimeTv.text = xValue.get(e.x.toInt())

                heightList.forEachIndexed { index, i ->
                    TLog.error("------高压====="+index+"-="+i)
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