package com.example.xingliansdk.ui.bp

import android.annotation.SuppressLint
import android.os.*
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.example.xingliansdk.dialog.OnCommDialogClickListener
import com.example.xingliansdk.network.api.jignfan.JingfanBpViewModel
import com.example.xingliansdk.utils.GetJsonDataUtil
import com.example.xingliansdk.utils.TimeUtil
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_card_edit.*
import kotlinx.android.synthetic.main.activity_card_edit.titleBar
import kotlinx.android.synthetic.main.activity_measure_bp_layout.*
import java.lang.StringBuilder

/**
 * 测量血压界面
 * Created by Admin
 *Date 2022/5/7
 */
class MeasureNewBpActivity : BaseActivity<JingfanBpViewModel>(),MeasureBigBpListener,View.OnClickListener{


    private var measureDialog : MeasureBpDialogView ?= null

    var totalSecond = 0
    //记录超时的时间，2分钟
    var timeOutSecond = 0

    var savePath : String ?= null

    private val handler : Handler = object :  Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            timeOutSecond++
            if(timeOutSecond >=120){    //超时了
                totalSecond = 0
                if(measureDialog != null)
                    measureDialog?.setMeasureStatus(false)
                stopMeasure()
            }

            if(totalSecond>=100)
                totalSecond = 0
            totalSecond+=3
            startCountTime()
        }
    }


    override fun layoutId(): Int {
       return R.layout.activity_measure_bp_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        savePath = Environment.getExternalStorageDirectory().path+"/Download/";


        measureBpAgainTv.setOnClickListener(this)

        measureBp()

        measureDialog = MeasureBpDialogView(this)
        measureDialog!!.show()
        measureDialog!!.setCancelable(false)
        measureDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) {  //再次测量按钮
                TLog.error("------再次测量="+code)
                measureBp()
            }

            override fun onCancelClick(code: Int) {
                if(totalSecond != 0){
                    ShowToast.showToastShort("正在测量中!")
                    return
                }
                measureDialog?.dismiss()
            }

        })

    }


    override fun createObserver() {
        super.createObserver()

        //成功返回
        mViewModel.uploadJfBp.observe(this){
            TLog.error("---------后台返回="+ Gson().toJson(it))
            if(measureDialog != null)
                measureDialog?.dismiss()
            stopMeasure(it as MeasureBpBean)
            showResult(it)
        }


        //后台非成功返回
        mViewModel.msgJfUploadBp.observe(this){
            TLog.error("---------后台飞200返回="+ Gson().toJson(it))
          //  measureDialog?.setMeasureStatus(true)
            showFailMeasure()
        }


    }


    private fun showFailMeasure(){
        if(measureDialog != null)
            measureDialog?.setMeasureStatus(false)
        measureDialog?.setCancelable(true)
        totalSecond = 0
        timeOutSecond = 0
        stopMeasure()
    }

    @SuppressLint("SetTextI18n")
    private fun showResult(measureBpBean: MeasureBpBean){
        measureBpResultDayTv.text = measureBpBean.date
        measureBpResultTimeTv.text = measureBpBean.time
        measureBpResultHBpTv.text = (measureBpBean.sbp.toInt()).toString()+" mmHg"
        measureBpResultLBpTv.text = measureBpBean.dbp.toInt().toString()+" mmHg"
        measureBpResultHeartTv.text = measureBpBean.heartRate.toInt().toString() + " 次/分"

    }

    private fun measureBp(){
        totalSecond = 0
        BleWrite.writeStartOrEndDetectBp(true,0x03,this)

    }

    private fun startCountTime(){
        if(measureDialog != null)
            measureDialog!!.setMiddleSchedule(totalSecond.toFloat())
        handler.sendEmptyMessageDelayed(0x00,1000)

    }

    //开始测量
    override fun measureStatus(status: Int) {
        TLog.error("-----测量装填="+status)
        if(status == 0x01){ //手表主动结束掉
            showFailMeasure()
        }else{
            timeOutSecond = 0
            startCountTime()
        }

    }

    //测量结果
    override fun measureBpResult(bpValue: MutableList<Int>,time : String) {

        val hashMap = HashMap<String,String>()
        hashMap["data"] = bpValue.toString()
        hashMap["createTime"] = time

        val stringBuilder = StringBuilder()
        bpValue.forEachIndexed { index, i ->
            if(index == bpValue.size-1){
                stringBuilder.append(i)
            }else{
                stringBuilder.append(i)
                stringBuilder.append(",")
            }
        }

       // GetJsonDataUtil().writeTxtToFile("时间="+time+" "+Gson().toJson(stringBuilder.toString()),savePath,"signal_bp"+System.currentTimeMillis()+".json")

         mViewModel.uploadJFBpData(stringBuilder.toString(),time)
        //stopMeasure()
    }


    private fun stopMeasure(measureBpBean: MeasureBpBean ){
        handler.removeMessages(0x00)
        //时间
        val longTime = TimeUtil.formatTimeToLong(measureBpBean.date+" "+measureBpBean.time)
        val timeArray = HexDump.toByteArray(longTime-946656000L)

        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,0x07,0x02,0x00,0x07,timeArray[0],timeArray[1],timeArray[2],timeArray[3],
            measureBpBean.sbp.toInt().toByte(),measureBpBean.dbp.toInt().toByte(),measureBpBean.heartRate.toInt().toByte()
        )

        var resultArray = CmdUtil.getFullPackage(cmdArray)
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }

        })
    }

    private fun stopMeasure(){
        handler.removeMessages(0x00)
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x00,0x01,0x01)

        val resultArray = CmdUtil.getFullPackage(cmdArray)
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }

        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.measureBpAgainTv->{
                measureDialog?.show()
                measureDialog?.setMiddleSchedule(-1f)
                measureBp()
            }
        }
    }
}