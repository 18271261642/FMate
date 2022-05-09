package com.example.xingliansdk.ui.bp

import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import kotlinx.android.synthetic.main.activity_card_edit.*

/**
 * 测量血压界面
 * Created by Admin
 *Date 2022/5/7
 */
class MeasureNewBpActivity : BaseActivity<BaseViewModel>(){


    var measureDialog : MeasureBpDialogView ?= null

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
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x03)

        var resultArray = CmdUtil.getFullPackage(cmdArray)

        if(measureDialog != null)
            measureDialog!!.setMiddleSchedule()

        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {

            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {

            }

        })
    }
}