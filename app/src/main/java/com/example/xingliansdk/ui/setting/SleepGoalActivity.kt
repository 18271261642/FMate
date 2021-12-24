package com.example.xingliansdk.ui.setting

import android.os.Bundle
import android.widget.SeekBar
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.shon.connector.utils.TLog
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import kotlinx.android.synthetic.main.activity_sleep_goal.*

class SleepGoalActivity : BaseActivity<UserViewModel>(), SeekBar.OnSeekBarChangeListener {

    var sleepGoal=0
    override fun layoutId() = R.layout.activity_sleep_goal

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
                TLog.error("sleepGoal+=$sleepGoal")
                var value = HashMap<String, String>()
                value["sleepTarget"] = sleepGoal.toString()
                mViewModel.setUserInfo(value!!)

            }


        })
        seekBarSports.setOnSeekBarChangeListener(this)
        sleepGoal =
            if (Hawk.get<Int>(Config.database.SLEEP_GOAL) != null) {
                TLog.error("难道为null了"+Hawk.get(Config.database.SLEEP_GOAL))
                Hawk.get(Config.database.SLEEP_GOAL)
            } else
                16*1800

        TLog.error("sleepGoal=="+sleepGoal)
        seekBarSports.progress = (sleepGoal/1800)
        tvSport.text = DateUtil.getTextTime(sleepGoal.toLong())
    }
    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            Hawk.put(Config.database.SLEEP_GOAL,sleepGoal)
            SNEventBus.sendEvent(Config.eventBus.SPORTS_GOAL_SLEEP)
            finish()
        }
    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        sleepGoal=progress*1800
        TLog.error("progress==$progress  sleepGoal++$sleepGoal")
        tvSport.text = DateUtil.getTextTime((progress*1800).toLong())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}