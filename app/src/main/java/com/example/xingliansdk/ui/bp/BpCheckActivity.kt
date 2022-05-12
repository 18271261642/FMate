package com.example.xingliansdk.ui.bp

import android.graphics.Color
import android.os.*
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.dialog.CheckBpDialogView
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.example.xingliansdk.dialog.MediaRepeatDialog
import com.example.xingliansdk.network.api.jignfan.JingfanBpViewModel
import com.example.xingliansdk.utils.GetJsonDataUtil
import com.example.xingliansdk.utils.TimeUtil
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_bp_chekc_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*
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
    private var checkCount = 0;

    var resultMap = HashMap<String,Any>()


    var measureDialog : MeasureBpDialogView ?= null

    var totalSecond = 0

    //输入血压弹窗
    var inputDialog : MediaRepeatDialog ?= null

    var thirdBpCheckBean : ThirdBpCheckBean ?= null

    var savePath : String ?= null

    private val handler : Handler = object :  Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            totalSecond+=3
            startCountTime()
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


        showBpSchedule()

        showGuidDialog()
    }

    private fun showBpSchedule(){

        startCheckBpTv.text = "开始第一次校准"
        if(checkCount == 1){
            startCheckBpTv.text = "开始第二次校准"
            firstScheduleTv.shapeDrawableBuilder.setSolidColor(R.color.bp_checked_color).intoBackground()
            firstScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

        if(checkCount == 2){
            startCheckBpTv.text = "开始第三次校准"
            firstScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_checked_color))
            secondScheduleTv.shapeDrawableBuilder.setSolidColor(R.color.bp_checked_color).intoBackground()
            secondScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

        if(checkCount == 3){
            startCheckBpTv.text = "校准已完成"
            secondScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_checked_color))
            thirdScheduleTv.shapeDrawableBuilder.setSolidColor(R.color.bp_checked_color).intoBackground()
            thirdScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

    }


    override fun createObserver() {
        super.createObserver()

        //成功返回
        mViewModel.resultJF.observe(this){
            TLog.error("---------后台返回="+Gson().toJson(it))
        }


        //后台非成功返回
        mViewModel.msgJf.observe(this){
            ShowToast.showToastShort("上传失败:"+it)
            TLog.error("---------后台飞200返回="+Gson().toJson(it))
        }
    }

    private fun showMeasureDialog(){
        measureDialog = MeasureBpDialogView(this)
        measureDialog!!.show()
        measureBp()
    }


    private fun startCountTime(){
        if(measureDialog != null)
            measureDialog!!.setMiddleSchedule(totalSecond.toFloat())
        handler.sendEmptyMessageDelayed(0x00,1000)

    }


    private fun measureBp(){
        startCountTime()
        BleWrite.writeStartOrEndDetectBp(true,0x03,this)
    }

    override fun measureStatus(status: Int) {

    }

    override fun measureBpResult(bpValue: MutableList<Int>,time : String) {
        if(checkCount >3)
            return
        val hashMap = HashMap<String,String>()
        hashMap["data"] = bpValue.toString()
        hashMap["createTime"] = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")
       // mViewModel.uploadJFBpData(bpValue.toIntArray(),DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"))

        var hbpStr = checkHBpTv.text.toString()
        if(!StringUtils.isNumeric(hbpStr))
            hbpStr = "120"
        var lbpStr = checkLBpTv.text.toString()
        if(!StringUtils.isNumeric(lbpStr))
            lbpStr = "80"

        val sb1 = StringBuilder()

        bpValue.forEachIndexed { index, i ->
            if(index == bpValue.size-1){
                sb1.append(i)
            }else{
                sb1.append(i)
                sb1.append(",")
            }
        }

        if(checkCount == 0){
            thirdBpCheckBean?.data1 = sb1.toString()
            thirdBpCheckBean?.sbp1 = hbpStr.toInt()
            thirdBpCheckBean?.dbp1 = lbpStr.toInt()
        }

        if(checkCount == 1){
            thirdBpCheckBean?.data2 = sb1.toString()
            thirdBpCheckBean?.sbp2 = hbpStr.toInt()
            thirdBpCheckBean?.dbp2 = lbpStr.toInt()
        }
        if(checkCount == 2){
            thirdBpCheckBean?.data3 = sb1.toString()
            thirdBpCheckBean?.sbp3 = hbpStr.toInt()
            thirdBpCheckBean?.dbp3 = lbpStr.toInt()
        }


        resultMap.put(("data"+(checkCount+1)),bpValue)
        resultMap.put(("sbp"+(checkCount+1)),checkHBpTv.text.toString())
        resultMap.put(("dbp"+(checkCount+1)),checkLBpTv.text.toString())

        TLog.error("-------校准数据="+Gson().toJson(resultMap))
        checkCount++;
        stopMeasure();
    }


    private fun stopMeasure(){
        handler.removeMessages(0x00)
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,0x01)

        var resultArray = CmdUtil.getFullPackage(cmdArray)
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }
        })

        if(measureDialog != null){
            measureDialog?.setMiddleSchedule(-1f)
            measureDialog?.dismiss()
        }


        showBpSchedule()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.startCheckBpTv->{  //校准按钮

                if(checkCount >3)
                    return
                if(checkCount == 3){    //提交
                    if(thirdBpCheckBean != null){
                        GetJsonDataUtil().writeTxtToFile(Gson().toJson(thirdBpCheckBean),savePath,("up_bp"+System.currentTimeMillis())+".json")

                        mViewModel.markJFBpData(thirdBpCheckBean!!.data1,thirdBpCheckBean!!.data2,thirdBpCheckBean!!.data3,
                            thirdBpCheckBean!!.sbp1,thirdBpCheckBean!!.sbp2,thirdBpCheckBean!!.sbp3,thirdBpCheckBean!!.dbp1,thirdBpCheckBean!!.dbp2,thirdBpCheckBean!!.dbp3)
                    }

                    return
                }

                var hbpStr = checkHBpTv.text.toString()
                if(!StringUtils.isNumeric(hbpStr)){
                    ShowToast.showToastShort("请输入收缩压!")
                    return
                }

                var lbpStr = checkLBpTv.text.toString()
                if(!StringUtils.isNumeric(lbpStr)){
                    ShowToast.showToastShort("请输入舒张!")
                    return
                }

                showMeasureDialog()
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
        inputDialog!!.setTitleTxt("输入血压值")
        inputDialog!!.setContentHitTxt(if(type == 0) "输入收缩压" else "输入舒张压")
        inputDialog!!.setOnMediaRepeatInputListener {
            inputDialog!!.dismiss()
            if(type == 0)
                checkHBpTv.text = it.toString()
            else
                checkLBpTv.text = it.toString()
        }
    }


    private fun showGuidDialog(){

        val checkBpView = CheckBpDialogView(this)
        checkBpView.show()
        checkBpView.setOnCheckBpDialogListener(object : CheckBpDialogView.OnCheckBpDialogListener{
            override fun backImgClick() {
                checkBpView.dismiss()
            }

            override fun startCheckClick() {
                checkBpView.dismiss()

            }

        })
    }
}