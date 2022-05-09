package com.example.xingliansdk.ui.bp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.room.BloodPressureHistoryBean
import com.example.xingliansdk.dialog.OnCommDialogClickListener
import com.example.xingliansdk.dialog.PromptCheckBpDialog
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureViewModel
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.view.BpMeasureView
import com.example.xingliansdk.view.DateUtil
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.shon.connector.utils.ShowToast
import kotlinx.android.synthetic.main.activity_blood_pressure.*
import kotlinx.android.synthetic.main.activity_card_edit.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.titleBar
import kotlinx.android.synthetic.main.item_blood_pressure_index.*
import java.util.*

/**
 * 新版血压首页面
 * Created by Admin
 *Date 2022/5/7
 */
class BpHomeActivity : BaseActivity<BloodPressureViewModel>(),View.OnClickListener{


    //提示校准血压的dialog
    private var promptBpDialog : PromptCheckBpDialog ?= null


    override fun layoutId(): Int {
      return R.layout.activity_new_bp_home_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        bpHomeInputLayout.setOnClickListener(this)
        bpHomeMeasureLayout.setOnClickListener(this)

        img_left.setOnClickListener(this)

        showPromptDialog()


    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.img_left -> {
                    XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, -1)
                    imgReplicate.rotation=90f
                    llBloodPressureIndex.visibility=View.GONE
                   // setTitleDateData()

                }
                R.id.bpHomeInputLayout->{   //输入
                    showInputDialog()
                }
                R.id.bpHomeMeasureLayout->{ //测量
                    startActivity(Intent(this,MeasureNewBpActivity::class.java))
                }
            }
        }
    }


    private fun showPromptDialog(){
        if(promptBpDialog == null){
            promptBpDialog = PromptCheckBpDialog(this,R.style.edit_AlertDialog_style)
        }
        promptBpDialog!!.show()
        promptBpDialog!!.setCancelable(false)
        promptBpDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) {
                promptBpDialog!!.dismiss()
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
                var tvTitle = holder.getView<TextView>(R.id.tv_title)
                var edtDiastolic = holder.getView<EditText>(R.id.edt_diastolic)
                var edtSystolic = holder.getView<EditText>(R.id.edt_systolic)
                var dialogCancel = holder.getView<ImageView>(R.id.imgCancel)
                var dialogSet = holder.getView<TextView>(R.id.dialog_set)
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
//                    sDao.insert(
//                        BloodPressureHistoryBean(
//                            time/1000, 0, 1, mEdtSystolic, mDiastolic,
//                            DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM, time)
//                        )
//                    )
//                    homeCard(mEdtSystolic,mDiastolic )
//                    update()
                    dialog.dismiss()
                }
                dialogCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }
}