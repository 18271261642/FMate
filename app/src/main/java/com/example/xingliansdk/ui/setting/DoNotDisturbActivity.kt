package com.example.xingliansdk.ui.setting

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.github.iielse.switchbutton.SwitchView
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.TimeBean
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_alarm_clock.*
import kotlinx.android.synthetic.main.activity_do_not_disturb.*
import kotlinx.android.synthetic.main.activity_do_not_disturb.titleBar
import kotlinx.android.synthetic.main.item_switch.view.*

class DoNotDisturbActivity : BaseActivity<MainViewModel>(),View.OnClickListener,
    BleWrite.DoNotDisturbModeSwitchCallInterface {
    var timeBean = TimeBean()
    private var pvTime: TimePickerView? = null
    override fun layoutId() = R.layout.activity_do_not_disturb
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        includeSwitch.img.visibility = View.GONE
        includeSwitch.tv_name.text="勿扰模式"
        setDoNotDisturb()
        timeBean = Hawk.get<TimeBean>(Config.database.DO_NOT_DISTURB_MODE_SWITCH, timeBean)
        var mText: String = if(timeBean.openHour<9)
            "0${timeBean.openHour}"
        else
            timeBean.openHour.toString()
        mText += if(timeBean.openMin<9)
            ":0${timeBean.openMin}"
        else
            ":${timeBean.openMin}"
        tvStartTime.text=mText
        var mCloseText: String = if(timeBean.closeHour<9)
            "0${timeBean.closeHour}"
        else
            timeBean.closeHour.toString()
        mCloseText += if(timeBean.closeMin<9)
            ":0${timeBean.closeMin}"
        else
            ":${timeBean.closeMin}"
        tvEndTime.text=mCloseText
        tvStartTime.setOnClickListener(this)
        tvEndTime.setOnClickListener(this)
        includeSwitch.Switch.isOpened = timeBean.switch == 2
        includeSwitch.Switch.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                setDoNotDisturb(2,timeBean.openHour,timeBean.openMin,timeBean.closeHour,timeBean.closeMin)
            }
            override fun toggleToOff(view: SwitchView?) {
                setDoNotDisturb(1,timeBean.openHour,timeBean.openMin,timeBean.closeHour,timeBean.closeMin)
            }
        })
        initTimePicker()
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
            }

            override fun onActionClick() {
                TLog.error("timeBean+="+Gson().toJson(timeBean))
                Hawk.put(Config.database.DO_NOT_DISTURB_MODE_SWITCH, timeBean)
                BleWrite.writeDoNotDisturbModeSwitchCall(timeBean, this@DoNotDisturbActivity)
                finish()
            }
        })

    }

    private fun setDoNotDisturb(
        switch: Int = 0, openHour: Int = 22, openMin: Int = 0
        , closeHour: Int = 7, closeMin: Int = 0
    ) {
        timeBean.switch = switch
        timeBean.openHour = openHour
        timeBean.openMin = openMin
        timeBean.closeHour = closeHour
        timeBean.closeMin = closeMin
        includeSwitch.Switch.isOpened = switch == 2
    }

    override fun DoNotDisturbModeSwitchCallResult() {
    }
    var type=0
   private fun initTimePicker() { //Dialog 模式下，在底部弹出
        TLog.error("弹出")
       pvTime=  TimePickerBuilder(
            this,
            OnTimeSelectListener { date, v ->
                if (type ==0) {
                    timeBean.openHour = DateUtil.getHour(date)
                    timeBean.openMin = DateUtil.getMinute(date)
                    tvStartTime.text= DateUtil.getDate(DateUtil.HH_MM,date)
                } else {
                    timeBean.closeHour = DateUtil.getHour(date)
                    timeBean.closeMin = DateUtil.getMinute(date)
                    tvEndTime.text= DateUtil.getDate(DateUtil.HH_MM,date)
                }
                TLog.error("分+++" + DateUtil.getMinute(date) + "时++" + DateUtil.getHour(date))
            })
            .setType(booleanArrayOf(false, false, false, true, true, false))
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .isCyclic(true)
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
    override fun onClick(v: View) {
        when(v.id)
        {
            R.id.tvStartTime->
            {
                type=0
                pvTime?.show()
            }
            R.id.tvEndTime->
            {
                type=1
                pvTime?.show()
            }
        }
    }

}