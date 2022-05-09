package com.example.xingliansdk.ui.bp

import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.dialog.CheckBpDialogView
import com.example.xingliansdk.dialog.MeasureBpDialogView
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.bean.SpecifySleepSourceBean
import com.shon.connector.call.CmdUtil
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*

/**
 * 血压校准
 * Created by Admin
 *Date 2022/5/7
 */
class BpCheckActivity : BaseActivity<BaseViewModel>() {

    override fun layoutId(): Int {
       return R.layout.activity_bp_chekc_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        val checkBpView = CheckBpDialogView(this)
        checkBpView.show()
        checkBpView.setOnCheckBpDialogListener(object : CheckBpDialogView.OnCheckBpDialogListener{
            override fun backImgClick() {
                checkBpView.dismiss()
            }

            override fun startCheckClick() {
                checkBpView.dismiss()
                showMeasureDialog()
            }

        })
    }


    private fun showMeasureDialog(){
        val measureDialog = MeasureBpDialogView(this)
        measureDialog.show()

    }


    private fun measureBp(){
        val cmdArray = byteArrayOf(0x0B,0x01,0x01,0x03)

        var resultArray = CmdUtil.getFullPackage(cmdArray)

        BleWrite.writeCommByteArray(resultArray,true,object : BleWrite.SpecifySleepSourceInterface{
            override fun backSpecifySleepSourceBean(specifySleepSourceBean: SpecifySleepSourceBean?) {
                TODO("Not yet implemented")
            }

            override fun backStartAndEndTime(startTime: ByteArray?, endTime: ByteArray?) {
                TODO("Not yet implemented")
            }

        })
    }
}