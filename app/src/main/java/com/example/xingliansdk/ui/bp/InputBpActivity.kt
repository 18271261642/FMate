package com.example.xingliansdk.ui.bp

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.bean.room.BloodPressureHistoryBean
import com.example.xingliansdk.bean.room.BloodPressureHistoryDao
import com.example.xingliansdk.dialog.MediaRepeatDialog
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureViewModel
import com.example.xingliansdk.view.DateUtil
import com.gyf.barlibrary.ImmersionBar
import com.luck.picture.lib.tools.ToastUtils
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_bp_chekc_layout.*
import kotlinx.android.synthetic.main.activity_input_bp_layout.*
import kotlinx.android.synthetic.main.activity_input_bp_layout.titleBar
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*
import org.apache.commons.lang.StringUtils
import java.util.*


/**
 * 输入血压的页面
 */
class InputBpActivity : BaseActivity<BloodPressureViewModel>(),View.OnClickListener{


    private var pvTime: TimePickerView? = null

    //选是日期还是时间
    var isCheckDay = false

    lateinit var sDao: BloodPressureHistoryDao

    //输入血压弹窗
    var inputDialog : MediaRepeatDialog?= null

    override fun layoutId(): Int {
        return R.layout.activity_input_bp_layout
    }

    override fun initView(savedInstanceState: Bundle?) {

        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        inputBpDayLayout.setOnClickListener(this)
        inputBpTimeLayout.setOnClickListener(this)
        inputBpHbpLayout.setOnClickListener(this)
        inputBpLBpLayout.setOnClickListener(this)
        inputBpSubmitTv.setOnClickListener(this)

        sDao = AppDataBase.instance.getBloodPressureHistoryDao()


        inputBpDayTv.text = DateUtil.getCurrDate()
        inputBpTimeTv.text = DateUtil.getCurrentTime()

        initTimePicker()
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.resultSet.observe(this){
            ShowToast.showToastShort(resources.getString(R.string.string_add_successed))
            finish()
        }

        mViewModel.msgSet.observe(this){
            ShowToast.showToastLong(resources.getString(R.string.string_add_failed)+it)
        }
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id){
                R.id.inputBpDayLayout->{    //选择日期
                    isCheckDay = true
                    initTimePicker()
                    pvTime?.show()
                }
                R.id.inputBpTimeLayout->{   //选择时间
                    isCheckDay = false
                    initTimePicker()
                    pvTime?.show()
                }

                R.id.inputBpHbpLayout->{    //输入收缩压

                    inputBpData(0)
                }

                R.id.inputBpLBpLayout->{    //输入舒张压
                    inputBpData(1)
                }

                R.id.inputBpSubmitTv->{ //保存
                    saveInputBp()
                }

            }
        }
    }


    private fun saveInputBp(){
        //日期
        val inputDayStr = inputBpDayTv.text.toString()
        //时间
        val inputTimeStr = inputBpTimeTv.text.toString()

        //输入的血压值
        var inputHbpStr = inputBpHBpTv.text.toString()
        var inputLbpStr = inputBpLBpTv.text.toString()

        if(inputHbpStr.contains("mmHg") && inputLbpStr.contains("mmHg")){
            inputHbpStr = StringUtils.substringBefore(inputHbpStr,"m").trim()
            inputLbpStr = StringUtils.substringBefore(inputLbpStr,"m").trim()
        }

        if(!StringUtils.isNumeric(inputHbpStr) || !StringUtils.isNumeric(inputLbpStr)){
            ShowToast.showToastShort(resources.getString(R.string.string_input_bp))
            return
        }

        //格式化时间
        val timeStr = "$inputDayStr $inputTimeStr"

        var createTime = DateUtil.formatTimeStrToLong(timeStr,"yyyy-MM-dd HH:mm")
        mViewModel.setBloodPressure(this@InputBpActivity,createTime,inputHbpStr.toInt(),inputLbpStr.toInt())
        sDao.insert(
            BloodPressureHistoryBean(
                createTime, 0, 0, inputHbpStr.toInt(),inputLbpStr.toInt(),
                timeStr
            )
        )
    }





    private var time = System.currentTimeMillis()
    private fun initTimePicker() { //Dialog 模式下，在底部弹出



        //将时间格式化，转换为long，比较大小


        pvTime = TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->

                //已经选择的日期
                val selectDate = inputBpDayTv.text.toString()
                //已经选择的时间
                val selectTime = inputBpTimeTv.text.toString()
                
                val checkDate = DateUtil.getDate("yyyy-MM-dd",date)
                val checkTime = DateUtil.getDate("HH:mm",date)

                val selectTimeLong = DateUtil.formatTimeStrToLong("$checkDate $checkTime","yyyy-MM-dd HH:mm")

                TLog.error("--------选择时间="+selectTimeLong +" "+time)


                if(isCheckDay){
                    val localTime =  DateUtil.formatTimeStrToLong("$checkDate $selectTime","yyyy-MM-dd HH:mm")
                    if(localTime >time / 1000){
                        ShowToast.showToastLong(resources.getString(R.string.string_select_normal_date))
                        return@OnTimeSelectListener
                    }

                    inputBpDayTv.text = checkDate
                }
                else{
                    val localTime2 =  DateUtil.formatTimeStrToLong("$selectDate $checkTime","yyyy-MM-dd HH:mm")
                    if(localTime2 >time / 1000){
                        ShowToast.showToastLong(resources.getString(R.string.string_select_normal_date))
                        return@OnTimeSelectListener
                    }
                inputBpTimeTv.text = checkTime
            }

            })
            .setType(if(isCheckDay) booleanArrayOf(true,true,true,false,false,false) else booleanArrayOf(false,false,false,true,true,false))
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .isCyclic(true)
            .setDate(DateUtil.getCurrentCalendar(System.currentTimeMillis()))
            .build()
        val mDialog: Dialog = pvTime?.dialog!!
        val params =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        params.leftMargin = 0
        params.rightMargin = 0
        pvTime?.let { it.dialogContainerLayout.layoutParams = params }
        val dialogWindow = mDialog.window
        if (dialogWindow != null) {
            dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim) //修改动画样式
            dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f)
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
            var inputHbpStr = inputBpHBpTv.text.toString()
            var inputLbpStr = inputBpLBpTv.text.toString()

            if(inputHbpStr.contains("mmHg") ){
                inputHbpStr = StringUtils.substringBefore(inputHbpStr,"m").trim()

            }

            if( inputLbpStr.contains("mmHg")){
                inputLbpStr = StringUtils.substringBefore(inputLbpStr,"m").trim()
            }

            val valueInteger = it.toInt()
            if(type == 0){

                if(valueInteger <40 || valueInteger > 250){
                    ShowToast.showToastShort(resources.getString(R.string.string_input_normal_sbp))
                    return@setOnMediaRepeatInputListener
                }

                if(StringUtils.isNumeric(inputLbpStr)){
                    if(inputLbpStr.toInt()>valueInteger){
                        ShowToast.showToastShort(resources.getString(R.string.string_input_normal_sbp))
                        return@setOnMediaRepeatInputListener
                    }
                    inputBpHBpTv.text = "$valueInteger mmHg"
                }else{
                    inputBpHBpTv.text = "$valueInteger mmHg"
                }

            }

            else{
                if(valueInteger <40 || valueInteger > 250){
                    ShowToast.showToastShort(resources.getString(R.string.string_input_normal_dbp))
                    return@setOnMediaRepeatInputListener
                }

                if(StringUtils.isNumeric(inputHbpStr)){
                    if(inputHbpStr.toInt()<valueInteger){
                        ShowToast.showToastShort(resources.getString(R.string.string_input_normal_dbp))
                        return@setOnMediaRepeatInputListener
                    }
                    inputBpLBpTv.text = "$valueInteger mmHg"
                }else{
                    inputBpLBpTv.text = "$valueInteger mmHg"
                }


            }

        }
    }

}