package com.app.fmate.ui.setting

import android.os.Bundle
import android.widget.SeekBar
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.ui.login.viewMode.UserViewModel
import com.shon.connector.utils.TLog
import com.app.fmate.view.DateUtil
import com.app.fmate.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_sleep_goal.*

/**
 * 睡眠目标设置
 */
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
        //tvSport.text = DateUtil.getTextTime(sleepGoal.toLong())

        tvSport.text = DateUtil.getTextTimeHour(sleepGoal.toLong()) //DateUtil.getTextTime((progress*1800).toLong())
        tvSportMinute.text = DateUtil.getTextTimeMinute(sleepGoal.toLong())
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
        tvSport.text = DateUtil.getTextTimeHour(sleepGoal.toLong())+"" //DateUtil.getTextTime((progress*1800).toLong())
        tvSportMinute.text = DateUtil.getTextTimeMinute(sleepGoal.toLong())+""


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}