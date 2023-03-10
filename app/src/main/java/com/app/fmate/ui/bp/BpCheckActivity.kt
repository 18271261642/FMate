package com.app.fmate.ui.bp

import android.app.AlertDialog
import android.os.*
import android.text.TextUtils
import android.view.View
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.BaseActivity
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.dialog.CheckBpDialogView
import com.app.fmate.dialog.MeasureBpDialogView
import com.app.fmate.dialog.MediaRepeatDialog
import com.app.fmate.dialog.OnCommDialogClickListener
import com.app.fmate.network.api.jignfan.JingfanBpViewModel
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.utils.GetJsonDataUtil
import com.app.fmate.utils.Utils
import com.app.fmate.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_bp_chekc_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.titleBar
import org.apache.commons.lang.StringUtils
import java.lang.StringBuilder

/**
 * 血压校准
 * Created by Admin
 *Date 2022/5/7
 */
class BpCheckActivity : BaseActivity<JingfanBpViewModel>(), MeasureBigBpListener ,View.OnClickListener{

    //第几次校准
    private var checkCount = 1;

    var resultMap = HashMap<String,Any>()

    //记录超时的时间，2分钟
    var timeOutSecond = 0
    var measureDialog : MeasureBpDialogView ?= null

    var totalSecond = 0

    //输入血压弹窗
    var inputDialog : MediaRepeatDialog ?= null

    var thirdBpCheckBean : ThirdBpCheckBean ?= null

    var savePath : String ?= null

    //手表测量血压的时间，上传后台
    var deviceMeasureTime : String ?= null

    private var alertDialog : AlertDialog.Builder ?=null

    private val handler : Handler = object :  Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                if(!XingLianApplication.getXingLianApplication().getDeviceConnStatus() || BleConnection.iFonConnectError){
                    ShowToast.showToastLong(resources.getString(R.string.string_conn_dis_conn))
                    Config.isNeedTimeOut = false
                    totalSecond = 0
                    timeOutSecond = 0
                    Config.IS_APP_STOP_MEASURE_BP = false
                  //  AppActivityManager.getInstance().finishActivity(this@BpCheckActivity)
                    showMeasureDialog(false)
                    return
                }

                TLog.error("---timeOutSecond="+timeOutSecond)
                if(timeOutSecond >=120){    //超时了
                   showMeasureDialog(false)
                    timeOutSecond = 0
                    totalSecond = 0
                    showInputBpData()
                    stopMeasure(false)
                    return
                }
                timeOutSecond++
                if(totalSecond >= 100)
                    totalSecond = 0
                totalSecond+=3
                startCountTime()
            }

            if(msg.what == 0x01){
                finish()
            }

        }
    }

    override fun layoutId(): Int {
       return R.layout.activity_bp_chekc_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()


        thirdBpCheckBean = ThirdBpCheckBean()

        startCheckBpTv.setOnClickListener(this)
        inputHBpLayout.setOnClickListener(this)
        inputLBpLayout.setOnClickListener(this)

        savePath = Environment.getExternalStorageDirectory().path+"/Download/";


        showGuidDialog()


        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener{
            override fun onBackClick() {
                if(checkCount == 3){
                    checkCount--
                    resultMap.put("data3","")
                    resultMap.put(("sbp3"),"")
                    resultMap.put(("dbp3"),"")

                    checkHBpTv.text = resultMap["sbp2"].toString()
                    checkLBpTv.text = resultMap["dbp2"].toString()


                    secondScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_no_check_color))
                    thirdScheduleTv.shapeDrawableBuilder.setSolidColor(resources.getColor(R.color.bp_no_check_color)).intoBackground()
                    thirdScheduleTxtTv.setTextColor(resources.getColor(R.color.bp_no_check_color))

                    showBpSchedule()
                    return
                }
                if(checkCount == 2){
                    checkCount--
                    resultMap.put("data2","")
                    resultMap.put(("sbp2"),"")
                    resultMap.put(("dbp2"),"")

                    checkHBpTv.text = resultMap["sbp1"].toString()
                    checkLBpTv.text = resultMap["dbp1"].toString()

                    secondScheduleTv.shapeDrawableBuilder.setSolidColor(resources.getColor(R.color.bp_no_check_color)).intoBackground()
                    secondScheduleTxtTv.setTextColor(resources.getColor(R.color.bp_no_check_color))

                    firstScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_no_check_color))

                    showBpSchedule()
                    return
                }


               showGuidDialog()
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {

            }

        })
    }

    private fun showBpSchedule(){

//        firstScheduleTv.shapeDrawableBuilder.setSolidColor(resources.getColor(R.color.bp_no_check_color)).intoBackground()
//        firstScheduleTxtTv.setTextColor(resources.getColor(R.color.bp_no_check_color))



        startCheckBpTv.text = resources.getString(R.string.string_measure_first)
        if(checkCount == 1){
            startCheckBpTv.text = resources.getString(R.string.string_measure_second)
            firstScheduleTv.shapeDrawableBuilder.setSolidColor(resources.getColor(R.color.bp_checked_color)).intoBackground()
            firstScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

        if(checkCount == 2){
            startCheckBpTv.text = resources.getString(R.string.string_measure_third)
            firstScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_checked_color))
            secondScheduleTv.shapeDrawableBuilder.setSolidColor(resources.getColor(R.color.bp_checked_color)).intoBackground()
            secondScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

        if(checkCount == 3){
            startCheckBpTv.text = resources.getString(R.string.string_measure_submit)
            secondScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_checked_color))
            thirdScheduleTv.shapeDrawableBuilder.setSolidColor(resources.getColor(R.color.bp_checked_color)).intoBackground()
            thirdScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

    }


    override fun createObserver() {
        super.createObserver()

        //成功返回
        mViewModel.resultJF.observe(this){
            hideWaitDialog()
            TLog.error("---------后台返回="+Gson().toJson(it))
            ShowToast.showToastLong(resources.getString(R.string.string_measure_check_success))
            timeOutSecond = 0

           handler.sendEmptyMessage(0x01)
        }


        //后台非成功返回
        mViewModel.msgJf.observe(this){
            hideWaitDialog()
            timeOutSecond = 0
            ShowToast.showToastLong("上传失败:"+it)
            TLog.error("---------后台飞200返回="+Gson().toJson(it))
        }
    }

    private fun showMeasureDialog(isMeasureFail : Boolean){

        val isForeground = Utils.isForeground(this@BpCheckActivity, BpCheckActivity::class.java.name)
        if(!isForeground)
            return
        if(measureDialog == null){
            measureDialog = MeasureBpDialogView(this)
        }
        //measureDialog = MeasureBpDialogView(this)
        if(!measureDialog!!.isShowing){
            measureDialog!!.show()
        }

       // measureDialog = MeasureBpDialogView(this)
        //measureDialog!!.show()
        measureDialog!!.setCancelable(false)
        if(!isMeasureFail){ //failed
            measureDialog!!.setMeasureStatus(false,false)
            measureDialog!!.setMiddleSchedule(-1f)
            totalSecond = 0
            timeOutSecond = 0
        }else{
            measureDialog!!.setMeasureStatus(true,false)
        }
        measureDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) { //再次测量
                measureBp()
            }

            override fun onCancelClick(code: Int) {
                if(timeOutSecond !=0){
                    backAlert()
                    return
                }
                measureDialog!!.dismiss()
                if(checkCount == 1){ //没有校准过，直接退出
                    showGuidDialog()
                }

            }

        })

    }


    private fun startCountTime(){
        if(measureDialog != null)
            measureDialog!!.setMiddleSchedule(totalSecond.toFloat())
        handler.sendEmptyMessageDelayed(0x00,1000)

    }


    private fun measureBp(){
        Config.isNeedTimeOut = true
        timeOutSecond = 1
        totalSecond = 0
        startCountTime()
        BLEManager.getInstance().dataDispatcher.clear("")
        BleWrite.writeStartOrEndDetectBp(true,0x03,this)
    }

    override fun measureStatus(status: Int ,deviceTime : String) {
        TLog.error("---------测量状态="+status)
        if(status == 0x01){ //手表主动结束掉
            handler.removeMessages(0x00)
            Config.isNeedTimeOut = false
            totalSecond = 0
            timeOutSecond = 0
           // stopMeasure(false)
            showMeasureDialog(false)
        }
        this.deviceMeasureTime = deviceTime
    }

    override fun measureBpResult(bpValue: MutableList<Int>,time : String) {

        TLog.error("---------校准测量次数="+time+" checkCount="+checkCount)

        if(checkCount >3)
            return
        checkBpStatusTv.text = resources.getString(R.string.string_measure_success)

        val sb1 = StringBuilder()

        bpValue.forEachIndexed { index, i ->
            if(index == bpValue.size-1){
                sb1.append(i)
            }else{
                sb1.append(i)
                sb1.append(",")
            }
        }

        resultMap.put(("data"+(checkCount)),sb1)
        handler.removeMessages(0x00)
        TLog.error("-------校准数据="+Gson().toJson(resultMap))
        showBpSchedule()
        checkHBpTv.text = resources.getString(R.string.string_input_sbp)
        checkLBpTv.text = resources.getString(R.string.string_input_dbp)
        stopMeasure(false);
    }


    private fun showInputBpData(){

        checkHBpTv.text = resultMap[("sbp")+(checkCount-1)].toString()
        checkLBpTv.text = resultMap[("dbp")+(checkCount-1)].toString()
    }

    private fun stopMeasure(isOut : Boolean){

        handler.removeMessages(0x00)
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,if(isOut) 0x01 else 0x0B)

        var resultArray = CmdUtil.getFullPackage(cmdArray)
        BLEManager.getInstance().dataDispatcher.clear("")
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }
        })

        if(isOut){
            Config.IS_APP_STOP_MEASURE_BP = false
           // AppActivityManager.getInstance().finishActivity(this@BpCheckActivity)
            measureDialog?.dismiss()
            if(checkCount==1){
                showGuidDialog()
                return
            }
            checkCount--
        }else{
            if(measureDialog != null){
                measureDialog?.setMiddleSchedule(-1f)
                measureDialog?.dismiss()

            }


        }


    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.startCheckBpTv->{  //校准按钮
                if(checkCount >3)
                    return

                var hbpStr = checkHBpTv.text.toString()
                if(!StringUtils.isNumeric(hbpStr)){
                    ShowToast.showToastShort(resources.getString(R.string.string_input_sbp))
                    return
                }

                val lbpStr = checkLBpTv.text.toString()
                if(!StringUtils.isNumeric(lbpStr)){
                    ShowToast.showToastShort(resources.getString(R.string.string_input_dbp))
                    return
                }

                if(checkCount == 3){    //提交

                        thirdBpCheckBean?.sbp3 = hbpStr.toInt()
                        thirdBpCheckBean?.dbp3 = lbpStr.toInt()
                        resultMap.put(("sbp3"),hbpStr.toInt())
                        resultMap.put(("dbp3"),lbpStr.toInt())


                    if(thirdBpCheckBean != null){

                        GetJsonDataUtil().writeTxtToFile(Gson().toJson(resultMap),savePath,("up_bp"+System.currentTimeMillis())+".json")

                        if(TextUtils.isEmpty(resultMap["data3"].toString()))
                        {
                            ShowToast.showToastShort("数据为空!")
                            return
                        }
                        showWaitDialog("提交中...")
                        mViewModel.markJFBpData(resultMap)
//                        mViewModel.markJFBpData(thirdBpCheckBean!!.data1,thirdBpCheckBean!!.data2,thirdBpCheckBean!!.data3,
//                            thirdBpCheckBean!!.sbp1,thirdBpCheckBean!!.sbp2,thirdBpCheckBean!!.sbp3,thirdBpCheckBean!!.dbp1,thirdBpCheckBean!!.dbp2,thirdBpCheckBean!!.dbp3)
                    }

                    return
                }

                if(checkCount == 1){    //第一次校准玩，输入值后开启第二次校准
                    thirdBpCheckBean?.sbp1 = hbpStr.toInt()
                    thirdBpCheckBean?.dbp1 = lbpStr.toInt()
                    resultMap.put(("sbp1"),hbpStr.toInt())
                    resultMap.put(("dbp1"),lbpStr.toInt())
                    checkCount++;

                    measureBp()
                    showMeasureDialog(true)

                    return
                }

                if(checkCount == 2){
                    thirdBpCheckBean?.sbp2 = hbpStr.toInt()
                    thirdBpCheckBean?.dbp2 = lbpStr.toInt()
                    resultMap.put(("sbp2"),hbpStr.toInt())
                    resultMap.put(("dbp2"),lbpStr.toInt())
                    checkCount++;
                }


                measureBp()
                showMeasureDialog(true)
            }
            R.id.inputHBpLayout->{  //输入收缩压
                inputBpData(0)
            }
            R.id.inputLBpLayout->{  //输入舒张压
                inputBpData(1)
            }
        }
    }



    private fun inputBpData(type : Int){

        inputDialog = MediaRepeatDialog(this,R.style.edit_AlertDialog_style)
        inputDialog?.show()
        inputDialog!!.setTitleTxt(resources.getString(R.string.string_input_bp))
        inputDialog!!.setContentHitTxt(if(type == 0) resources.getString(R.string.string_input_sbp) else resources.getString(R.string.string_input_dbp))
        inputDialog!!.setOnMediaRepeatInputListener {
            inputDialog!!.dismiss()

            //输入的血压值
            var inputHbpStr = checkHBpTv.text.toString()
            var inputLbpStr = checkLBpTv.text.toString()

            if(inputHbpStr.contains("mmHg") ){
                inputHbpStr = StringUtils.substringBefore(inputHbpStr,"m").trim()

            }

            if( inputLbpStr.contains("mmHg")){
                inputLbpStr = StringUtils.substringBefore(inputLbpStr,"m").trim()
            }


            if(it.toInt() <40 || it > 250){
                ShowToast.showToastShort(resources.getString(R.string.string_input_valid_bp))
                return@setOnMediaRepeatInputListener
            }

            if(type == 0){
                if(StringUtils.isNumeric(inputLbpStr)){
                    if(inputLbpStr.toInt()>it){
                        ShowToast.showToastShort(resources.getString(R.string.string_input_sbp))
                        return@setOnMediaRepeatInputListener
                    }
                    checkHBpTv.text = it.toString()
                }else{
                    checkHBpTv.text = it.toString()
                }

            }
            else{

                if(StringUtils.isNumeric(inputHbpStr)){
                    if(inputHbpStr.toInt()<it){
                        ShowToast.showToastShort(resources.getString(R.string.string_input_dbp))
                        return@setOnMediaRepeatInputListener
                    }
                    checkLBpTv.text = it.toString()
                }else{
                    checkLBpTv.text = it.toString()
                }

            }

        }
    }


    private fun showGuidDialog(){

        val checkBpView = CheckBpDialogView(this)
        checkBpView.show()
        checkBpView.setOnCheckBpDialogListener(object : CheckBpDialogView.OnCheckBpDialogListener{
            override fun backImgClick() {
                checkBpView.dismiss()
                Config.IS_APP_STOP_MEASURE_BP = false
                AppActivityManager.getInstance().finishActivity(this@BpCheckActivity)
            }

            override fun startCheckClick() {
                checkBpView.dismiss()
                measureBp()
                showMeasureDialog(true)
            }

        })
    }

    private fun stop(){
        totalSecond = 0
        //记录超时的时间，2分钟
        timeOutSecond = 0
        Config.isNeedTimeOut = false
        Config.IS_APP_STOP_MEASURE_BP = true
        showInputBpData()
        stopMeasure(true)
    }

    private fun backAlert(){
        if(alertDialog == null){
            alertDialog = AlertDialog.Builder(this@BpCheckActivity)
        }
        alertDialog!!.setTitle(resources.getString(R.string.string_text_remind))
        alertDialog!!.setMessage(resources.getString(R.string.string_cancel_measure))
        alertDialog!!.setPositiveButton(resources.getString(R.string.text_sure)
        ) { p0, p1 ->
            p0.cancel()

            TLog.error("---------终止测量-------")
            stop()
            // handler.sendEmptyMessage(0x01)
        }.setNegativeButton(resources.getString(R.string.text_cancel)
        ) { p0, p1 ->
            p0.cancel()
        }
        alertDialog!!.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Config.isNeedTimeOut = false
    }
}