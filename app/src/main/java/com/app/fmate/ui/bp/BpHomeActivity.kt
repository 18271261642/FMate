package com.app.fmate.ui.bp

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.adapter.BloodPressureHistoryAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.room.AppDataBase
import com.app.fmate.bean.room.BloodPressureHistoryBean
import com.app.fmate.bean.room.BloodPressureHistoryDao
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.dialog.OnCommDialogClickListener
import com.app.fmate.dialog.PromptCheckBpDialog
import com.app.fmate.network.api.bloodPressureView.BloodPressureViewModel
import com.app.fmate.network.api.bloodPressureView.BloodPressureVoBean
import com.app.fmate.network.api.homeView.HomeCardVoBean
import com.app.fmate.utils.HelpUtil
import com.app.fmate.utils.JumpUtil
import com.app.fmate.utils.MapUtils
import com.app.fmate.utils.TimeUtil
import com.app.fmate.view.DateUtil
import com.app.fmate.widget.TitleBarLayout
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
 * ?????????????????????
 * Created by Admin
 *Date 2022/5/7
 */
class BpHomeActivity : BaseActivity<BloodPressureViewModel>(),View.OnClickListener,OnChartValueSelectedListener{


    //?????????????????????dialog
    private var promptBpDialog : PromptCheckBpDialog ?= null

    //???????????????????????????adapter
    private lateinit var  mBloodPressureHistoryAdapter: BloodPressureHistoryAdapter
    var bpList: ArrayList<BloodPressureHistoryBean> = arrayListOf()

    lateinit var sDao: BloodPressureHistoryDao

    private var currDayStr : String ?= null

    //??????????????????
    var heightList : ArrayList<Int> = arrayListOf()
    //???????????????
    var lowBpList : ArrayList<Int> = arrayListOf()
    //????????????????????????
    var inputHeightBpList : ArrayList<Int> = arrayListOf();
    //????????????????????????
    var inputLowBpList : ArrayList<Int> = arrayListOf();

    //x????????? HH:mm??????
    var xValue : ArrayList<String> = arrayListOf()

    //?????????????????????????????????????????????
    private var isNeedCheckBp = true
    //???????????????????????????
    private var checkDescStr : String ?= null


    //???????????????????????????
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

        initViews()

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

    }

    private fun initViews(){
        bpDbp85Item.setContentText(resources.getString(R.string.string_dbp)+"85~89mmHg")
        bpDbp90Item.setContentText(resources.getString(R.string.string_dbp)+"90~99mmHg")
        bpDbp100Item.setContentText(resources.getString(R.string.string_dbp)+"???100mmHg")

        spDbp140Item.setContentText(resources.getString(R.string.string_sbp)+"140 ~ 159mmHg")
        spDbp160Item.setContentText(resources.getString(R.string.string_sbp)+"???160mmHg")

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
                TLog.error("????????????")

                if(pos>=0)
                {

                    if(!HelpUtil.netWorkCheck(this@BpHomeActivity)) {
                        if(mBloodPressureHistoryAdapter.data[pos].type==0) {
                            ShowToast.showToastLong(getString(R.string.err_network_delete))
                            return
                        }
                    }

                    if(mBloodPressureHistoryAdapter.data[pos].type==1) {
                        ShowToast.showToastLong("???????????????!")
                        return
                    }

                   // showWaitDialog("???????????????...")
                    var value = HashMap<String, String>()
                    value["createTime"] = mBloodPressureHistoryAdapter.data[pos].startTime.toString()
                    sDao.deleteTime(mBloodPressureHistoryAdapter.data[pos].startTime)
                    mBloodPressureHistoryAdapter.notifyItemRemoved(pos)

                    if(HelpUtil.netWorkCheck(this@BpHomeActivity)
                        &&mBloodPressureHistoryAdapter.data[pos].type==0) {
                        TLog.error("??????")
                        mViewModel.deleteBloodPressure(value)
                    }
                    else
                        hideWaitDialog()
                    setTitleDateData()
//                    if (mBloodPressureHistoryAdapter.data.size > 0)
//                        homeCard(mBloodPressureHistoryAdapter.data[0].systolicBloodPressure,
//                            mBloodPressureHistoryAdapter.data[0].diastolicBloodPressure)//???????????????????????????????????? ???????????????
//                    else
//                        homeCard(0,0)//???????????????????????????????????? ???????????????

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


    override fun onResume() {
        super.onResume()
        setTitleDateData()

    }

    //??????????????????????????????
    private var inputHbpSize = 0
    private var inputLBpSize = 0
    private var autoHBpSize = 0
    private var autoLBpSize = 0

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateBpData(list : List<BloodPressureVoBean.ListDTO>){
        inputHeightBpList.clear()
        inputLowBpList.clear()
        heightList.clear()
        lowBpList.clear()
        xValue.clear()

        bpList.clear()

        inputHbpSize = 0
        inputLBpSize = 0
        autoHBpSize = 0
        autoLBpSize = 0

        bpCheckTimeTv.text = " "
        bpHomeMeasureSelectTv.text = "--/--"
        bpHomeMeasureInputTv.text = "--/--"

//        bpList = sDao.getDayBloodPressureHistory(
//            day
//        ) as ArrayList<BloodPressureHistoryBean>


        var tmpBpList = ArrayList<BloodPressureHistoryBean>()
        list.forEach {
            var bp = BloodPressureHistoryBean(
                it.stampCreateTime, 0, it.dataSource , it.systolicPressure, it.diastolicPressure,
                it.createTime
            )

            tmpBpList.add(bp)
        }


        bpList = tmpBpList
        TLog.error("------DB="+Gson().toJson(bpList))

        mBloodPressureHistoryAdapter.data.clear()
        mBloodPressureHistoryAdapter.addData(bpList)
        mBloodPressureHistoryAdapter.notifyDataSetChanged()


        //?????????????????????
        val resultInputHourMap = HashMap<String,IntArray>()
        //???????????????
        val resultMeasureHourMap = HashMap<String,IntArray>()

        //?????? ?????????????????????
        bpList.sortedBy {
            it.startTime
        }

        if(bpList.size==0){
            lastValidValue = 0
            initLinData(bpHomeLinChartView,
                heightList,lowBpList,inputHeightBpList,inputLowBpList)
            return
        }
        //????????????????????????
        val firstValue = bpList[bpList.size-1]

        val firstResultHourStr = DateUtil.getFormatHour(firstValue.startTime * 1000)

        //?????????????????????
        val lastValue = bpList[0]
        val lastResultHourStr = DateUtil.getFormatHour(lastValue.startTime * 1000)

        TLog.error("-------????????????????????????="+firstResultHourStr+" Size="+bpList.size)


//        bpList.forEachIndexed { index, bp ->
//
//
//        }

        //????????????
        bpList.forEachIndexed {index, bp ->

            //HH??????
            val timeStr = DateUtil.getDate(
                DateUtil.HOUR, bp.startTime * 1000
            )
            //??????
            val timeMinuteStr = DateUtil.getDate(
                DateUtil.MINUTE, bp.startTime * 1000
            )

            var resultHourStr = DateUtil.getFormatHour(bp.startTime * 1000)

            TLog.error("----??????=$timeStr $timeMinuteStr $resultHourStr" +" type="+bp.type)

            if (bp.type == 0) {   //????????????
                if (resultInputHourMap[resultHourStr] != null) {
                    val bpArray = resultInputHourMap[resultHourStr]
                    //??????
                    val hBp = bpArray?.get(0)?.toInt()
                    //??????
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

            } else {
                if(resultMeasureHourMap[resultHourStr] != null){
                    val bpArray = resultMeasureHourMap[resultHourStr]
                    //??????
                    val hBp = bpArray?.get(0)?.toInt()
                    //??????
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
            TLog.error("------??????????????????="+Gson().toJson(resultInputHourMap))


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
                    if(tmpHP>0){
                        inputHbpSize++
                        inputLBpSize++
                    }
                }
            }

            TLog.error("-----????????????="+tmpHP+" "+tmpLP)
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
                    if(tmpMHBp>0){
                        autoHBpSize++
                        autoLBpSize++
                    }
                }
            }

            heightList.add(tmpMHBp)
            lowBpList.add(tmpMLBp)

        }


        val tmpXVList = mutableListOf<String>()

        //??????X????????????????????????0??????????????????23:30
        var tmpIndex = 0
        var tmpLastIndex = 0
        xValue.forEachIndexed { index, s ->

            if(s == firstResultHourStr){
                tmpIndex = index;
            }

            //?????????????????????
            if(lastResultHourStr == s){
                tmpLastIndex = index
            }
        }

        tmpXVList.addAll(xValue)
        xValue.clear()

        lastValidValue = tmpLastIndex - tmpIndex

        TLog.error("----????????? ????????????="+tmpIndex +" "+tmpLastIndex)

     //   xValue.addAll(tmpXVList.subList(tmpIndex,if(lastValidValue == 0) tmpXVList.size-1 else lastValidValue))
        if(lastValidValue >=7){
            xValue.addAll(tmpXVList.subList(tmpIndex, tmpLastIndex+1))
        }else{
            xValue.addAll(BpUtils.get6SizeList(tmpIndex,tmpLastIndex,tmpXVList,tmpXVList.subList(tmpIndex, tmpLastIndex+1)))
        }



        TLog.error("----??????6?????????="+xValue.size+" "+Gson().toJson(xValue))


        val tmpInputList :  ArrayList<Int> = arrayListOf()
        val tmpInputLowList : ArrayList<Int> = arrayListOf()

        tmpInputList.addAll(inputHeightBpList)
        inputHeightBpList.clear()
        if(inputHbpSize>=7){
            inputHeightBpList.addAll(tmpInputList.subList(tmpIndex,tmpLastIndex+1))
        }else{
            inputHeightBpList.addAll(BpUtils.get6IntSizeList(inputHbpSize,tmpIndex,tmpLastIndex,BpUtils.get48SizeList(),
                tmpInputList.subList(tmpIndex,tmpLastIndex+1)))
        }

       // inputHeightBpList.addAll(tmpInputList.subList(tmpIndex,tmpInputList.size))

        tmpInputLowList.addAll(inputLowBpList)

        inputLowBpList.clear()
        if(inputLBpSize>=7){
            inputLowBpList.addAll(tmpInputLowList.subList(tmpIndex,tmpLastIndex+1))
        }else{
            inputLowBpList.addAll(BpUtils.get6IntSizeList(inputLBpSize,tmpIndex,tmpLastIndex,BpUtils.get48SizeList(),
                tmpInputLowList.subList(tmpIndex,tmpLastIndex+1)))
        }

        //inputLowBpList.addAll(tmpInputLowList.subList(tmpIndex,tmpInputLowList.size))



        //?????????
        val tmpMHList : ArrayList<Int> = arrayListOf()
        val tmpMLList : ArrayList<Int> = arrayListOf()
        tmpMHList.addAll(heightList)
        heightList.clear()
       // heightList.addAll(tmpMHList.subList(tmpIndex,tmpMHList.size))

        if(autoHBpSize>=7){
            heightList.addAll(tmpMHList.subList(tmpIndex,tmpLastIndex+1))
        }else{
            heightList.addAll(BpUtils.get6IntSizeList(autoHBpSize,tmpIndex,tmpLastIndex,BpUtils.get48SizeList(),tmpMHList.subList(tmpIndex,tmpLastIndex+1)))
        }


        tmpMLList.addAll(lowBpList)
        lowBpList.clear()
        if(autoLBpSize>=7){
            lowBpList.addAll(tmpMLList.subList(tmpIndex,tmpLastIndex+1))

        }else{
            lowBpList.addAll(BpUtils.get6IntSizeList(autoLBpSize,tmpIndex,tmpLastIndex,BpUtils.get48SizeList(),tmpMLList.subList(tmpIndex,tmpLastIndex+1)))

        }


       // lowBpList.addAll(tmpMLList.subList(tmpIndex,tmpMLList.size))


        TLog.error("------?????????????????????="+Gson().toJson(lowBpList)+"\n"+Gson().toJson(inputLowBpList))

        initLinData(bpHomeLinChartView,
            heightList,lowBpList,inputHeightBpList,inputLowBpList)
    }



    override fun createObserver() {
        super.createObserver()
        //?????????????????????????????????

        mViewModel.resultGet.observe(this){ it ->
            TLog.error("----??????????????????="+Gson().toJson(it))
            if(!it.isCalibrationRequired ){   //???????????????????????????
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



    //?????????????????????????????????
    private fun getDateBpData(dayStr : String){
        mViewModel.getBloodPressure(dayStr)
    }



    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.commWhiteLeftImg->{    //?????????
                    XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, -1)
                    imgReplicate.rotation=90f
                    setTitleDateData()
                }

                R.id.commWhiteRightImg->{    //?????????
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
                R.id.bpHomeInputLayout->{   //??????
                 //   showInputDialog()
                    startActivity(Intent(this,InputBpActivity::class.java))
                }
                R.id.bpHomeMeasureLayout-> { //??????

                    var bindMac = Hawk.get("address", "")

                    TLog.error("----userInfp=" + Gson().toJson(userInfo)+"\n"+isConntinue)
                    if (TextUtils.isEmpty(bindMac) ) {    //??????
                        showPromptDialog(true)
                        return
                    }

                    if (!XingLianApplication.getXingLianApplication()
                            .getDeviceConnStatus() || BleConnection.iFonConnectError
                    ) {
                        ShowToast.showToastShort(resources.getString(R.string.string_conn_dis_conn))
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


    var timeDialog: Long? = System.currentTimeMillis()//?????????????????????
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


    private fun changeLanguage(txt : String) : String{
        if(txt.contains("??????????????????")){
            return resources.getString(R.string.string_check_bp_user_change_txt)
        }
        if(txt.contains("????????????"))
            return resources.getString(R.string.string_check_bp_first_user_txt)
        if(txt.contains("?????????????????????"))
            return resources.getString(R.string.string_check_bp_long_time_txt)
        if(txt.contains("???????????????????????????"))
            return resources.getString(R.string.string_check_bp_change_device_txt)
        return txt
    }

    private var isConntinue = false


    private fun showPromptDialog(isBind : Boolean){
        if(promptBpDialog == null){
            promptBpDialog = PromptCheckBpDialog(this,R.style.edit_AlertDialog_style)
        }
        promptBpDialog!!.show()
        promptBpDialog!!.setTopTxtValue(if(isBind) resources.getString(R.string.string_no_bind_device_txt) else resources.getString(R.string.string_check_bp_first_user_txt))
        promptBpDialog!!.setBotBtnTxt(if(isBind) resources.getString(R.string.string_temporary_bind) else resources.getString(R.string.string_no_checked),if(isBind) resources.getString(R.string.string_bind) else resources.getString(R.string.string_to_check))
        promptBpDialog!!.setCancelable(false)
        promptBpDialog!!.setVisibilityBotTv(!isBind)
        if(!isBind){

            promptBpDialog!!.setTopTxtValue(checkDescStr?.let { changeLanguage(it) })
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
        bpHomeLinChartView.setNoDataText(resources.getString(R.string.string_no_data))
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
       // xAxis.setCenterAxisLabels(true)
//        xAxis.axisMaxLabels = 48
        xAxis.setAvoidFirstLastClipping(true)

        xAxis.setLabelCount(7,true)
        xAxis.isForceLabelsEnabled


        //??????Y???
        val yRight = bpHomeLinChartView.axisRight
        yRight.isEnabled = false

        //??????Y???
        val yLeft = bpHomeLinChartView.axisLeft
        yLeft.setLabelCount(15)
       // yLeft.granularity = 50f

        yLeft.axisMinimum-0.1f

        //????????????
        yLeft.enableGridDashedLine(10f, 10f, 0f);
        //??????????????????
        yLeft.setDrawGridLines(true)
        yLeft.setDrawAxisLine(true)

        yLeft.gridColor = Color.TRANSPARENT
        yLeft.textColor = Color.WHITE
        yLeft.axisLineColor = Color.TRANSPARENT
        yLeft.isEnabled = true


        yLeft.axisMaximum = 190f
        yLeft.axisMinimum = 40f

        //?????????Y???
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

    //????????????????????????????????????0???48
    private var firstNoZero = 0
    private var firstNoData : String ?= null


    @RequiresApi(Build.VERSION_CODES.N)
    private fun initLinData(chart : LineChart, hbpList : ArrayList<Int>, lbpList : ArrayList<Int>, inputHList : ArrayList<Int>, inputLList : ArrayList<Int>){
        val dataSets = ArrayList<ILineDataSet>()


        //?????????????????????????????????6????????????????????????
       // float ratio = (float) xValueList.size()/(float) 6;
        //???????????????????????????????????????????????????,1f?????????????????????
       // mLineChart.zoom(ratio,1f,0,0);
        val xAxis = bpHomeLinChartView.xAxis

        val maxLength = hbpList.size>inputHList.size

        val matrix = Matrix()

//        matrix.preScale(2.5f,1f)
//
//        bpHomeLinChartView.viewPortHandler.refresh(matrix,bpHomeLinChartView,false)



        val tmp24EntryList = ArrayList<Entry>()
        xValue.forEachIndexed { index, s ->
            TLog.error("------X?????????="+index+" "+s)
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


        //?????????????????????
        val inputHeightValue = ArrayList<Entry>()
        inputHList.forEachIndexed { index, i ->

            if(i != 0){
                TLog.error("---??????0-??????x="+index+" "+i)
                inputHeightValue.add(Entry(index.toFloat(),i.toFloat()))
            }

        }


          val inputHD = LineDataSet(inputHeightValue,"")

           inputHD.lineWidth = 1.5f
           inputHD.circleRadius = 2.0f
          // inputHD.highLightColor = Color.rgb(255, 255, 255)
           inputHD.formLineDashEffect = DashPathEffect(floatArrayOf(10f,5f),0f)
           inputHD.formLineWidth = 1f
           //??????
           inputHD.enableDashedLine(10f, 10f, 0f)
            inputHD.circleHoleColor = Color.TRANSPARENT
//           inputHD.enableDashedHighlightLine(10f, 10f, 0f)
           inputHD.color =Color.parseColor("#71FBEE")
            inputHD.circleColors = arrayListOf(Color.parseColor("#71FBEE"))
           // inputHD.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
           inputHD.setDrawValues(false)

           //??????????????????
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

           //??????
            inputLD.enableDashedLine(10f, 10f, 0f)
           inputLD.enableDashedHighlightLine(10f, 10f, 0f)
           inputLD.circleHoleColor = Color.TRANSPARENT
           inputLD.color = Color.parseColor("#FBD371")
           inputLD.circleColors = arrayListOf(Color.parseColor("#FBD371"))
           inputLD.setDrawValues(false)


           val values1 = ArrayList<Entry>()
           //??????
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

           //??????
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



        val sets = ArrayList<ILineDataSet>()
           sets.add(d1)
           sets.add(d2)

           sets.add(inputHD)
           sets.add(inputLD)

           sets.add(dd2)
           var tmpCount = 0
           TLog.error("-----x???="+Gson().toJson(xValue))
           //x???
           if(xValue.size>0){
               tmpCount = 0

               xAxis.valueFormatter = BPXValueFormatter(xValue)

//               xAxis.setValueFormatter { value, axis ->
//                   val index = value.toInt()
//                     TLog.error("-----x???index="+index+" "+axis.mEntries[0])
//                   if(index<0 || index >= xValue.size){
//                       return@setValueFormatter ""
//                   }else{
//                       val tmpIndex = value % xValue.size
//                       if(tmpIndex >=0){
//                           return@setValueFormatter xValue.get(index)
//                       }else{
//                           return@setValueFormatter ""
//                       }
//                   }
//               }
           }

           val data = LineData(sets)
           chart.data = data
           chart.invalidate()



        //???????????????????????????
        if(values1.size>0){
            val tmpH = values1[values1.size-1].y.toInt().toString()
            val tmpL = values2[values2.size-1].y.toInt().toString()
            bpHomeMeasureSelectTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--/--" else "$tmpH/$tmpL "
        }else{
            bpHomeMeasureSelectTv.text = "--/--"
        }

        //??????
        if(inputHeightValue.size>0){
            val tmpH = inputHeightValue[inputHeightValue.size-1].y.toInt().toString()
            val tmpL = inputLowBpValue[inputLowBpValue.size-1].y.toInt().toString()
            bpHomeMeasureInputTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--" else "$tmpH/$tmpL "
        }else{
            bpHomeMeasureInputTv.text = "--"
        }

      //  showSelectData()

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
                    calendar?.set(year, month, dayOfMonth, 0, 0)//?????????????????????
                    var calendarTime = calendar?.timeInMillis
                    if (calendarTime!! > toDay) {
                        TLog.error("calendarTime+= $calendarTime  DateUtil.getCurrentDate()++$toDay")
                        ShowToast.showToastLong("?????????????????????????????????")
                        return@setOnDateChangeListener
                    }
                    XingLianApplication.getSelectedCalendar()?.timeInMillis = calendarTime!!
                    setTitleDateData()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {
        try {
            if (e != null) {
                TLog.error("------select="+e.x+" "+e.y)
                val x = e.x.toInt()

                val xValue = xValue[e.x.toInt()]
                val tmeX = TimeUtil.getSpecifyHour(xValue)
                bpCheckTimeTv.text = tmeX+"~"+xValue
//                if(x+1 <=xValue.size-1){
//                    bpCheckTimeTv.text = xValue.get(e.x.toInt()) +"~"+xValue.get(x+1)
//                }else{
//                    bpCheckTimeTv.text = xValue.get(e.x.toInt())
//                }


                heightList.forEachIndexed { index, i ->
                    //TLog.error("------??????====="+index+"-="+i)
                }

                //???????????????????????????
                if(heightList.size>0 && e.x.toInt()<=heightList.size-1){
                    val tmpH = heightList[e.x.toInt()].toString()
                    val tmpL = lowBpList[e.x.toInt()].toString()
                    bpHomeMeasureSelectTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--/--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureSelectTv.text = "--/--"
                }
                //??????
                if(inputHeightBpList.size>0 && e.x.toInt()<=inputLowBpList.size-1){
                    val tmpH = inputHeightBpList[e.x.toInt()].toString()
                    val tmpL = inputLowBpList[e.x.toInt()].toString()
                    bpHomeMeasureInputTv.text = if(tmpH.toInt() == 0 || tmpL.toInt()==0) "--/--" else "$tmpH/$tmpL "
                }else{
                    bpHomeMeasureInputTv.text = "--/--"
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onNothingSelected() {

    }
}