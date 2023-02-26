package com.app.fmate.ui.sleep.details

import android.os.Bundle
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.SleepTypeBean
import com.app.fmate.viewmodel.MainViewModel
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_sleep_night.*

class SleepNightActivity : BaseActivity<MainViewModel>() {

    override fun layoutId() = R.layout.activity_sleep_night
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        val mSleepTypeBean: SleepTypeBean =
            intent.getSerializableExtra("SleepType") as SleepTypeBean
        titleBar.setTitleText(mSleepTypeBean.name)
        tvReferValue.text = mSleepTypeBean.referValue
        tvTitle.text = mSleepTypeBean.timeDate
        when (mSleepTypeBean.name) {
            "0" -> {
                tvStatus.setTextColor(resources.getColor(R.color.color_main_green))
                tvStatus.text = "正常"
            }
            "1" -> {
                tvStatus.setTextColor(resources.getColor(R.color.color_sleep_low))
                tvStatus.text = "偏低"
            }
            "2" -> {
                tvStatus.setTextColor(resources.getColor(R.color.color_sleep_height))
                tvStatus.text = "偏高"
            }

        }
        when (mSleepTypeBean.type) {
            1 -> tvHTML.loadUrl("file:android_asset/sleepNight.html") //夜间睡眠
            2 -> tvHTML.loadUrl("file:android_asset/sleepNight.html") //呼吸质量 保留
            3 -> tvHTML.loadUrl("file:android_asset/deepSleep.html") //深睡比例
            4 -> tvHTML.loadUrl("file:android_asset/lightSleep.html") //浅睡比例
            5 -> tvHTML.loadUrl("file:android_asset/quickEyes.html") //快速动眼
            6 -> tvHTML.loadUrl("file:android_asset/sleepNight.html") //深睡连续性 保留
            7 -> tvHTML.loadUrl("file:android_asset/numberWake.html") //清醒次数

        }
    }

}