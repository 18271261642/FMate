package com.app.fmate.ui.setting.schedule

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fmate.Config
import com.app.fmate.Config.database.SCHEDULE_LIST
import com.app.fmate.R
import com.app.fmate.adapter.ScheduleAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.network.api.setAllClock.AlarmClockBean
import com.app.fmate.network.api.setAllClock.SetAllClockViewModel
import com.app.fmate.utils.JumpUtil
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.app.fmate.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.TimeBean
import kotlinx.android.synthetic.main.activity_alarm_clock_list.*

/**
 * 日程提醒页面
 */
class ScheduleListActivity : BaseActivity<SetAllClockViewModel>(), View.OnClickListener {
    lateinit var mScheduleAdapter: ScheduleAdapter
    lateinit var mScheduleList: ArrayList<TimeBean>
    override fun layoutId() = R.layout.activity_alarm_clock_list
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvSettingAlarmClock.setOnClickListener(this)
        tv_title.text = resources.getString(R.string.string_schedule_no_data)
        titleBar.setTitleText(resources.getString(R.string.string_device_schedule_set))
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                if (mScheduleList.size >= 5) {
                    ShowToast.showToastLong(resources.getString(R.string.string_schedule_add_most))
                    return
                }
                JumpUtil.startScheduleActivity(this@ScheduleListActivity)
            }

            override fun onActionClick() {

            }
        })
    }

    fun setAdapter() {
        mScheduleList = Hawk.get(SCHEDULE_LIST, ArrayList())
        mScheduleList.forEachIndexed { index, timeBean ->
            if ((timeBean.endTime) < (System.currentTimeMillis() / 1000)) {
                mScheduleList[index].mSwitch = 1
            }
        }

        recyclerview.layoutManager = LinearLayoutManager(
            this@ScheduleListActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        mScheduleAdapter = ScheduleAdapter(mScheduleList)
        recyclerview.adapter = mScheduleAdapter
        mScheduleAdapter.addChildClickViewIds(R.id.Switch)
        mScheduleAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.Switch -> {
                    var upDataTime = System.currentTimeMillis() / 1000
                    if(mScheduleList[position].endTime<upDataTime) {
                        ShowToast.showToastLong(resources.getString(R.string.string_schedule_time_alert))
                        mScheduleAdapter.notifyItemChanged(position)
                        return@setOnItemChildClickListener
                    }
                    if (mScheduleList[position].switch == 2)
                        mScheduleList[position].switch = 1
                    else
                        mScheduleList[position].switch = 2
                    Hawk.put(SCHEDULE_LIST, mScheduleList)
                    Hawk.put(Config.database.SCHEDULE_CREATE_TIME, upDataTime)

                    mScheduleAdapter.notifyItemChanged(position)
                    for (i in 0 until mScheduleList.size)
                        BleWrite.writeAlarmClockScheduleCall(mScheduleList[position], false)
                    saveSchedule(upDataTime)
                    mScheduleAdapter.notifyItemChanged(position)
                }
            }
        }
        setVisible()
        mScheduleAdapter.setOnDelListener(object : ScheduleAdapter.onSwipeListener {
            override fun onDel(pos: Int) {
                if (pos >= 0 && pos < mScheduleList.size) {
                    mScheduleList.removeAt(pos)
                    mScheduleAdapter.notifyItemRemoved(pos)
                    for (i in 0 until mScheduleList.size) {
                        mScheduleList[i].number = i
                        BleWrite.writeAlarmClockScheduleCall(mScheduleList[i], false)
                    }
                    if (mScheduleList.size <= 0) {
                        var mTimeBean = TimeBean()
                        mTimeBean.number = 0
                        mTimeBean.switch = 0
                        mTimeBean.characteristic = TimeBean.SCHEDULE_FEATURES
                        BleWrite.writeAlarmClockScheduleCall(mTimeBean, false)
                    }
                    TLog.error("数据流++${Gson().toJson(mScheduleList)}")
                    Hawk.put(SCHEDULE_LIST, mScheduleList)
                    var deleteTime = System.currentTimeMillis() / 1000
                    Hawk.put(Config.database.SCHEDULE_CREATE_TIME, deleteTime)
                    saveSchedule(deleteTime)
                }
            }

            override fun onClick(pos: Int) {
                JumpUtil.startScheduleActivity(this@ScheduleListActivity, pos)
            }

        })


    }

    private fun setVisible() {
        if (mScheduleList.size > 0) {
            llNoAlarmClock.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
        } else {
            llNoAlarmClock.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    private fun saveSchedule(dataTime: Long) {
       // mScheduleList = Hawk.get(SCHEDULE_LIST, ArrayList())
        if (mScheduleList.isNullOrEmpty() || mScheduleList.size <= 0)
            return
        var mAlarmClockList: ArrayList<AlarmClockBean> = ArrayList()
        mScheduleList.forEach {
            mAlarmClockList.add(
                AlarmClockBean(
                    it.characteristic,
                    it.mSwitch,
                    it.number,
                    it.unicode,
                    it.dataUnitType,
                    it.getEndTime(),
                    it.year,
                    it.month,
                    it.day,
                    it.hours,
                    it.min
                )
            )
        }
        var bean = Gson().toJson(mAlarmClockList)
        var data = HashMap<String, String>()
        data["schedule"] = bean
        data["createTime"] = (dataTime).toString()
        mViewModel.saveSchedule(data)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvSettingAlarmClock -> {
                JumpUtil.startScheduleActivity(this@ScheduleListActivity)
            }
        }
    }

    var time = 0
    override fun onResume() {
        super.onResume()
        time = Hawk.get(Config.database.SCHEDULE_CREATE_TIME, 0)
        setAdapter()
        mViewModel.getRemind("2")
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultRemind.observe(this)
        { it ->
            TLog.error("it==" + Gson().toJson(it))
            if (it == null || it.schedule == null || it.schedule.createTime < time) {
                saveSchedule(System.currentTimeMillis() / 1000)
            } else if (it.schedule.createTime > time) {
                TLog.error("进入else")
                var list = it.schedule.list
                mScheduleList.clear()
                list.forEach {
                    if(it.endTime<(System.currentTimeMillis() / 1000))
                        it.setmSwitch(1)
                    var bean = TimeBean()
                    bean.characteristic = it.characteristic
                    bean.mSwitch = it.getmSwitch()
                    bean.number = it.number
                    bean.unicode = it.unicode
                    bean.unicodeType = it.unicodeType.toByte()
                    bean.endTime = it.endTime
                    bean.year = it.year
                    bean.month = it.month
                    bean.day = it.day
                    bean.hours = it.hours
                    bean.min = it.min
                    if(it.endTime<System.currentTimeMillis()/1000)
                        bean.mSwitch = 1
                    mScheduleList.add(bean)
                    BleWrite.writeAlarmClockScheduleCall(bean, false)
                }
                 Hawk.put(SCHEDULE_LIST, mScheduleList)
            }
            setVisible()
            TLog.error("刷新==" + Gson().toJson(mScheduleList))
            mScheduleAdapter.notifyDataSetChanged()
        }
    }
}