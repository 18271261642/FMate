package com.example.xingliansdk.ui.setting.takeMedicine

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.xingliansdk.Config.eventBus.REMIND_TAKE_MEDICINE_REMINDER_PERIOD
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_take_medicine_repeat.*

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
                    edtCustom.setText(type.toString())

                    getType(R.id.edtCustom)

                }
            }
        }



//        edtCustom.addTextChangedListener {
//
//            if(it==null||it.isEmpty()){
////                edtCustom.setText("1")
////                it?.let { it1 -> edtCustom.setSelection(edtCustom.text.length)
////                TLog.error("it??"+it1.length)
////                }//将光标移至文字末尾
//                type=-1
//            //    ShowToast.showToastLong("周期天数间隔不能小于1天")
//                return@addTextChangedListener
//            }
//
//
//            val day=it.toString().toInt()
//            if (day>255)
//            {
//                ShowToast.showToastLong("周期天数不大于255天")
//                edtCustom.hint = "自定义"
//                return@addTextChangedListener
//            }
//            type=day
//            getType(edtCustom.id)
//        }
//

        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {
                TLog.error("===$type")
                if (type == -1) {
                    ShowToast.showToastLong("请填入正确的间隔周期")
                    return
                }

                val day = edtCustom.text.toString()
                if (day.toInt() == 0) {
                    ShowToast.showToastLong("请填入正确的间隔周期")
                    return
                }
                if (day.toInt() > 255) {
                    ShowToast.showToastLong("周期天数不大于255天")
                    edtCustom.hint = "自定义"
                    return
                }
                type = day.toInt()

                SNEventBus.sendEvent(REMIND_TAKE_MEDICINE_REMINDER_PERIOD, type)
                finish()
            }

        })
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
                getType(R.id.edtCustom)
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

}