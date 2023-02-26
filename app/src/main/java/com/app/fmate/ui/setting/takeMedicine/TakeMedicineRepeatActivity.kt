package com.app.fmate.ui.setting.takeMedicine

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.app.fmate.Config.eventBus.REMIND_TAKE_MEDICINE_REMINDER_PERIOD
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.dialog.MediaRepeatDialog
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.app.fmate.viewmodel.MainViewModel
import com.app.fmate.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_take_medicine_repeat.*
import org.apache.commons.lang.StringUtils

/**
 * 重复周期
 */
class TakeMedicineRepeatActivity : BaseActivity<MainViewModel>(), View.OnClickListener {

    override fun layoutId() = R.layout.activity_take_medicine_repeat
    var type=0
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        radRepeat.setOnClickListener(this)
        radInterval.setOnClickListener(this)
        llRepeat.setOnClickListener(this)
        tvOne.setOnClickListener(this)
        tvTwo.setOnClickListener(this)
        tvThree.setOnClickListener(this)

        edtCustom.setOnClickListener(this)

        initViews()

        llRepeat.visibility=View.GONE
        type=intent.getIntExtra("ReminderPeriod",0)
        if(type>0) {
            radInterval.isChecked = true
            llRepeat.visibility = View.VISIBLE
            when (type) {
                1 -> {
                    getType(R.id.tvOne)
                }
                2 -> {
                    getType(R.id.tvTwo)
                }
                3 -> {
                    getType(R.id.tvThree)
                }
                else->{
                    edtCustom.setText(type.toString()+"天")

                    getType(R.id.edtCustom)

                }
            }
        }


        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {
                TLog.error("===$type")

                var numDay : String ?=null
                if (type == -1) {

                val day = edtCustom.text.toString()
                if(day == resources.getString(R.string.string_custom)){
                    ShowToast.showToastShort("请填入间隔周期")
                    return
                }
                 numDay = StringUtils.substringBefore(day,"天")
                if(numDay == ""){
                    ShowToast.showToastShort("请填入间隔周期")
                    return
                }
                if (numDay.toInt() == 0) {
                    ShowToast.showToastShort("请填入正确的间隔周期")
                    return
                }
                if (numDay.toInt() > 255) {
                    ShowToast.showToastLong("周期天数不大于255天")
                    edtCustom.hint = resources.getString(R.string.string_custom)
                    return
                }
                }
                if (numDay != null) {
                    type = numDay.toInt()
                }

                SNEventBus.sendEvent(REMIND_TAKE_MEDICINE_REMINDER_PERIOD, type)
                finish()
            }

        })
    }


    private fun initViews(){
        tvOne.text = "1"+resources.getString(R.string.string_daily)
    }

    fun clearCusBeforeStatus(){
        
    }

    override fun onClick(v: View) {
//        HelpUtil.hideSoftInputView(this)
        when (v.id) {
            R.id.radRepeat -> {
                edtCustom.setText("")
                type=0
                llRepeat.visibility = View.GONE
            }
            R.id.radInterval -> {
                getType(R.id.tvOne)
                type=1
                llRepeat.visibility = View.VISIBLE
            }

            R.id.tvOne,R.id.tvTwo,R.id.tvThree -> {
                when (v.id) {
                    R.id.tvOne -> type=1
                    R.id.tvTwo -> type=2
                    R.id.tvThree -> type=3
                }
                getType(v.id)
            }
            R.id.edtCustom->{
                type = -1
                getType(R.id.edtCustom)
                var inputD = edtCustom.text.toString()
                if(inputD == "自定义"){
                    showInputRepeat("")
                    return
                }

                val numDay = StringUtils.substringBefore(inputD,"天")
                showInputRepeat(numDay)

            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getType(type: Int) {

        if(type == R.id.edtCustom){
            edtCustom.requestFocus()
            edtCustom.isFocusableInTouchMode = true
        }else{
            edtCustom.clearFocus()
            edtCustom.isFocusableInTouchMode = false
            edtCustom.text = null
            edtCustom.hint = "自定义"
        }

        tvOne.setBackgroundResource(
            if (type == R.id.tvOne)
                R.drawable.device_repeat_true_green
            else
                R.drawable.device_repeat_false_gray
        )
        tvOne.setTextColor(
            if (type == R.id.tvOne)
                resources.getColor(R.color.white)
            else
                resources.getColor(R.color.sub_text_color)
        )
        tvTwo.setBackgroundResource(
            if (type == R.id.tvTwo)
                R.drawable.device_repeat_true_green
            else
                R.drawable.device_repeat_false_gray
        )
        tvTwo.setTextColor(
            if (type == R.id.tvTwo)
                resources.getColor(R.color.white)
            else
                resources.getColor(R.color.sub_text_color)
        )
        tvThree.setBackgroundResource(
            if (type == R.id.tvThree)
                R.drawable.device_repeat_true_green
            else
                R.drawable.device_repeat_false_gray
        )
        tvThree.setTextColor(
            if (type == R.id.tvThree)
                resources.getColor(R.color.white)
            else
                resources.getColor(R.color.sub_text_color)
        )


        edtCustom.background =  resources.getDrawable(if (type == R.id.edtCustom ) R.drawable.device_repeat_true_green else R.drawable.device_repeat_false_gray)

        edtCustom.setTextColor(
            resources.getColor(if(type == R.id.edtCustom ) R.color.white else R.color.sub_text_color)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        HelpUtil.hideSoftInputView(this)
    }

    private fun showInputRepeat(repeat : String){
        val inputDialog = MediaRepeatDialog(this,R.style.edit_AlertDialog_style)
        inputDialog.show()
        inputDialog.setRepeatValue(repeat)
        inputDialog.setOnMediaRepeatInputListener {
            if (!TextUtils.isEmpty(repeat) && repeat.toInt() == 0) {
                Toast.makeText(this, "请输入正确的时间间隔!", Toast.LENGTH_SHORT).show()
                return@setOnMediaRepeatInputListener
            }

            if (!TextUtils.isEmpty(repeat) && repeat.toInt() > 255) {
                Toast.makeText(this, "   周期天数不大于255天!", Toast.LENGTH_SHORT).show()
                return@setOnMediaRepeatInputListener
            }

            inputDialog.dismiss()
            edtCustom.setText(it.toString()+"天")
        }

    }

}