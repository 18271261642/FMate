package com.example.xingliansdk.ui.bp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.example.xingliansdk.dialog.OnCommDialogClickListener
import com.example.xingliansdk.network.api.jignfan.JingfanBpViewModel
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_card_edit.*
import java.lang.StringBuilder

/**
 * 测量血压界面
 * Created by Admin
 *Date 2022/5/7
 */
class MeasureNewBpActivity : BaseActivity<JingfanBpViewModel>(),MeasureBigBpListener{


    private var measureDialog : MeasureBpDialogView ?= null

    var totalSecond = 0

    private val handler : Handler = object :  Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
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


        measureBp()

        measureDialog = MeasureBpDialogView(this)
        measureDialog!!.show()
        measureDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) {  //再次测量按钮
                measureBp()
            }

            override fun onCancelClick(code: Int) {

            }

        })


    }


    override fun createObserver() {
        super.createObserver()

        //成功返回
        mViewModel.resultJF.observe(this){
            TLog.error("---------后台返回="+ Gson().toJson(it))
            if(measureDialog != null)
                measureDialog?.setMeasureStatus(false)
            stopMeasure()
        }


        //后台非成功返回
        mViewModel.msgJf.observe(this){
            TLog.error("---------后台飞200返回="+ Gson().toJson(it))
          //  measureDialog?.setMeasureStatus(true)

        }


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
        startCountTime()
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
         mViewModel.uploadJFBpData(stringBuilder.toString(),DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"))
        stopMeasure()
    }


    private fun stopMeasure(){
        handler.removeMessages(0x00)
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x01)

        var resultArray = CmdUtil.getFullPackage(cmdArray)
        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }

        })
    }
}