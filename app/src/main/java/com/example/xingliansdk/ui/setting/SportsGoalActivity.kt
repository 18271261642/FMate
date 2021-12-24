package com.example.xingliansdk.ui.setting

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.widget.SeekBar
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.ui.login.viewMode.LoginViewModel
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import kotlinx.android.synthetic.main.activity_sports_goal.*

class SportsGoalActivity : BaseActivity<UserViewModel>(), SeekBar.OnSeekBarChangeListener {
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
        tvSport.text = mStr
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
        tvSport.text = mStr
        mDeviceInformationBean.exerciseSteps = (progress * 1000).toLong()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}