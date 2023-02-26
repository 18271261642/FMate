package com.app.fmate.ui.setting

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.widget.SeekBar
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.ui.login.viewMode.UserViewModel
import com.app.fmate.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import kotlinx.android.synthetic.main.activity_sports_goal.*
import java.text.DecimalFormat
import java.text.NumberFormat

class SportsGoalActivity : BaseActivity<UserViewModel>(), SeekBar.OnSeekBarChangeListener {

    private val numberFormat: NumberFormat = DecimalFormat("#,###")
    lateinit var mStr: SpannableString
    override fun layoutId() = R.layout.activity_sports_goal

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
            }

            override fun onActionClick() {
                var value = HashMap<String, String>()
                value["movingTarget"] = mDeviceInformationBean.exerciseSteps.toString()
                mViewModel.setUserInfo(value!!)
            }

        })
        seekBarSports.setOnSeekBarChangeListener(this)
        seekBarSports.progress = (mDeviceInformationBean.exerciseSteps / 1000).toInt()
        mStr = SpannableString("${seekBarSports.progress * 1000}步")
        mStr.setSpan(
            AbsoluteSizeSpan(48, true),
            0,
            mStr.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val progressValue = seekBarSports.progress * 1000
        tvSport.text = if(progressValue >=1000) numberFormat.format (progressValue.toLong()).toString() else progressValue.toString()//mStr
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            Hawk.put(Config.database.PERSONAL_INFORMATION, mDeviceInformationBean)
            BleWrite.writeDeviceInformationCall(mDeviceInformationBean,true)
            SNEventBus.sendEvent(Config.eventBus.SPORTS_GOAL_EXERCISE_STEPS,mDeviceInformationBean.exerciseSteps)
            finish()
        }
    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        mStr = SpannableString("${progress * 1000}步")
        mStr.setSpan(
            AbsoluteSizeSpan(48, true),
            0,
            mStr.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val progressValue = progress * 1000
        tvSport.text = if(progressValue >=1000) numberFormat.format (progressValue.toLong()).toString() else progressValue.toString()// mStr
        mDeviceInformationBean.exerciseSteps = (progress * 1000).toLong()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}