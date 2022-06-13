package com.example.xingliansdk.ui.login

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.widget.SeekBar
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.view.DateUtil
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.bean.DeviceInformationBean
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_goal.*

/**
 * 第一次进入页面进行设置 目标设置
 */
class GoalActivity :  BaseActivity<UserViewModel>(), SeekBar.OnSeekBarChangeListener {
    lateinit var mStr: SpannableString
    var sleepGoal=0
    override fun layoutId()=R.layout.activity_goal

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        seekBarSports.setOnSeekBarChangeListener(this)
        seekBarSports.progress = (mDeviceInformationBean.exerciseSteps / 1000).toInt()
        mStr = SpannableString("${seekBarSports.progress * 1000}")
//        mStr.setSpan(
//            AbsoluteSizeSpan(48, true),
//            0,
//            mStr.length - 1,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
        tvSport.text = mStr

        seekBarSleepTime.setOnSeekBarChangeListener(this)
        sleepGoal =   16*1800
        seekBarSleepTime.progress = (sleepGoal/1800)
        tvSure.setOnClickListener {
            var value = HashMap<String, String>()
            value["nickname"] = mDeviceInformationBean.name
            value["height"] = mDeviceInformationBean.height.toString()
            value["weight"] = mDeviceInformationBean.weight.toString()
            value["createTime"]=(System.currentTimeMillis()/1000).toString()
            value["age"] = mDeviceInformationBean.age.toString()
            value["sex"] = mDeviceInformationBean.sex.toString()
            value["birthDate"] =
                DateUtil.getDate(DateUtil.YYYY_MM_DD, mDeviceInformationBean.birth)
            value["sleepTarget"] = sleepGoal.toString()
            value["movingTarget"] = mDeviceInformationBean.exerciseSteps.toString()
            mViewModel.setUserInfo(value!!)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this){
            Hawk.put(Config.database.SLEEP_GOAL,sleepGoal)
            JumpUtil.startMainHomeActivity(this)
            finish()
        }
        mViewModel.msg.observe(this)
        {

        }
    }
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar.id==R.id.seekBarSports) {
            mStr = SpannableString("${progress * 1000}")
            mStr.setSpan(
                AbsoluteSizeSpan(48, true),
                0,
                mStr.length - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
           // tvSport.text = mStr

            tvSport.text = (progress * 1000).toString()
            mDeviceInformationBean.exerciseSteps = (progress * 1000).toLong()
        }
        else if(seekBar.id==R.id.seekBarSleepTime)
        {
            sleepGoal=progress*1800
            TLog.error("progress==$progress  sleepGoal++$sleepGoal")
            //tvSleepTime.text = DateUtil.getTextTime((progress*1800).toLong())
            tvSleepHourTv.text = DateUtil.getTextTimeHour(sleepGoal.toLong());
            tvSleepMinute.text = DateUtil.getTextTimeMinute(sleepGoal.toLong())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}