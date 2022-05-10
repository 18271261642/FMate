package com.example.xingliansdk.ui.bp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import kotlinx.android.synthetic.main.activity_card_edit.*

/**
 * 测量血压界面
 * Created by Admin
 *Date 2022/5/7
 */
class MeasureNewBpActivity : BaseActivity<BaseViewModel>(),MeasureBigBpListener{


    var measureDialog : MeasureBpDialogView ?= null

    var totalSecond = 0

    private val handler : Handler = object :  Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            totalSecond++
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

        if(measureDialog == null)
          measureDialog = MeasureBpDialogView(this)
        measureDialog!!.show()


    }


    private fun measureBp(){
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
    override fun measureBpResult(bpValue: MutableList<Int>?) {

        stopMeasure();
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