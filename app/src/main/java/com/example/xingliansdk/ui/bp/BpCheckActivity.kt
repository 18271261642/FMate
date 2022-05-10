package com.example.xingliansdk.ui.bp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.dialog.CheckBpDialogView
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.example.xingliansdk.network.api.jignfan.JingfanBpViewModel
import com.example.xingliansdk.utils.TimeUtil
import com.example.xingliansdk.view.DateUtil
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import com.shon.connector.call.listener.MeasureBigBpListener
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_bp_chekc_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.titleBar

/**
 * 血压校准
 * Created by Admin
 *Date 2022/5/7
 */
class BpCheckActivity : BaseActivity<JingfanBpViewModel>(), MeasureBigBpListener ,View.OnClickListener{

    //第几次校准
    private var checkCount = 0;


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
       return R.layout.activity_bp_chekc_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()


        startCheckBpTv.setOnClickListener(this)

        showBpSchedule()

        showGuidDialog()
    }

    private fun showBpSchedule(){

        firstScheduleTxtTv.text = "开始第一次校准"
        if(checkCount == 1){
            firstScheduleTxtTv.text = "开始第二次校准"
            firstScheduleTv.shapeDrawableBuilder.setShadowColor(R.color.bp_checked_color).intoBackground()
            firstScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

        if(checkCount == 2){
            firstScheduleTxtTv.text = "开始第三次校准"
            firstScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_checked_color))
            secondScheduleTv.shapeDrawableBuilder.setShadowColor(R.color.bp_checked_color).intoBackground()
            secondScheduleTxtTv.setTextColor(resources.getColor(R.color.main_text_color))
        }

        if(checkCount == 3){
            firstScheduleTxtTv.text = "校准已完成"
            secondScheduleLinView.setBackgroundColor(resources.getColor(R.color.bp_checked_color))
            thirdScheduleTv.shapeDrawableBuilder.setShadowColor(R.color.bp_checked_color).intoBackground()
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
            TLog.error("---------后台飞200返回="+Gson().toJson(it))
        }
    }

    private fun showMeasureDialog(){
        if(measureDialog == null){
            measureDialog = MeasureBpDialogView(this)
        }

        measureDialog!!.show()
        measureBp()
    }


    private fun startCountTime(){
        if(measureDialog != null)
            measureDialog!!.setMiddleSchedule(totalSecond.toFloat())
        handler.sendEmptyMessageDelayed(0x00,1000)

    }


    private fun measureBp(){
        BleWrite.writeStartOrEndDetectBp(true,0x03,this)
    }

    override fun measureStatus(status: Int) {

    }

    override fun measureBpResult(bpValue: MutableList<Int>) {
        if(checkCount >3)
            return

        val hashMap = HashMap<String,String>()
        hashMap["data"] = bpValue.toString()
        hashMap["createTime"] = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")
        mViewModel.uploadJFBpData(bpValue.toIntArray(),DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"))

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
    }

    override fun onClick(p0: View?) {
       val id = p0?.id
        when(id){
            R.id.startCheckBpTv->{  //校准按钮
                showMeasureDialog()
            }
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