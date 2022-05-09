package com.example.xingliansdk.ui.setting.alarmClock

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.Config.database.ALARM_CLOCK_CREATE_TIME
import com.example.xingliansdk.Config.database.TIME_LIST
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.AlarmClockAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.setAllClock.AlarmClockBean
import com.example.xingliansdk.network.api.setAllClock.SetAllClockViewModel
import com.example.xingliansdk.utils.JumpUtil
import com.shon.connector.utils.ShowToast
import com.example.xingliansdk.view.DateUtil
import com.shon.connector.utils.TLog
import com.example.xingliansdk.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.TimeBean
import kotlinx.android.synthetic.main.activity_alarm_clock_list.*

/**
 * 闹钟页面
 */
class AlarmClockListActivity : BaseActivity<SetAllClockViewModel>(), View.OnClickListener {
    private lateinit var mAlarmClockAdapter: AlarmClockAdapter
    var type = 1//默认闹钟
    private lateinit var mTimeList: ArrayList<TimeBean>
    override fun layoutId() = R.layout.activity_alarm_clock_list

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvSettingAlarmClock.setOnClickListener(this)
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                if (mTimeList.size >= 5) {
                    ShowToast.showToastLong("最多只可以添加五条,请选择删除或修改")
                    return
                }
                JumpUtil.startAlarmClockActivity(this@AlarmClockListActivity, type)
            }

            override fun onActionClick() {

            }
        })

    }

    fun setAdapter() {
        mTimeList = Hawk.get(TIME_LIST, ArrayList())
        TLog.error("mTimeList+="+Gson().toJson(mTimeList))
        mTimeList.forEachIndexed { index, timeBean ->
            var hours = DateUtil.getHour(System.currentTimeMillis())
            var min = DateUtil.getMinute(System.currentTimeMillis())
            if ( timeBean.specifiedTime == 128
                && timeBean.endTime < System.currentTimeMillis() / 1000
            ) {
                mTimeList[index].mSwitch = 1
            }

        }
        TLog.error("m==" + Gson().toJson(mTimeList))
        recyclerview.layoutManager = LinearLayoutManager(
            this@AlarmClockListActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        if (mTimeList.size > 0) {
            llNoAlarmClock.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
        } else {
            llNoAlarmClock.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
        mAlarmClockAdapter = AlarmClockAdapter(mTimeList)
        recyclerview.adapter = mAlarmClockAdapter
        mAlarmClockAdapter.addChildClickViewIds(R.id.Switch)
        mAlarmClockAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.Switch -> {
                    if (mTimeList[position].switch == 2)
                        mTimeList[position].switch = 1
                    else {
                        mTimeList[position].switch = 2
                        if(mTimeList[position].specifiedTime == 128) //只有仅一次会出现这种关闭情况所以只去改变仅一次
                            mTimeList[position].setEndTime(setUpdateTime(mTimeList[position].endTime))
                    }
                    Hawk.put(TIME_LIST, mTimeList)
                    var upDataTime = System.currentTimeMillis() / 1000
                    Hawk.put(ALARM_CLOCK_CREATE_TIME, upDataTime)

                    saveLocalAlarmClock(mTimeList)

//                    for (i in 0 until mTimeList.size)
//                        BleWrite.writeAlarmClockScheduleCall(mTimeList[i], false)
//                    saveAlarmClock(upDataTime)
                    mAlarmClockAdapter.notifyItemChanged(position)
                }
            }
        }
        mAlarmClockAdapter.setOnDelListener(object : AlarmClockAdapter.onSwipeListener {
            override fun onDel(pos: Int) {
                if (pos >= 0 && pos < mTimeList.size) {
                    TLog.error("mlist=+${Gson().toJson(mTimeList)}")
//                    mTimeList[pos].switch=1
//                    BleWrite.writeAlarmClockScheduleCall(mTimeList[pos])
                    mTimeList.removeAt(pos)
                    mAlarmClockAdapter.notifyItemRemoved(pos)

                    saveLocalAlarmClock(mTimeList)
                    saveAlarmClock(System.currentTimeMillis()/1000)
//                    for (i in 0 until mTimeList.size) {
//                        TLog.error("删除的position+=$i")
//                        mTimeList[i].number = i
//                        BleWrite.writeAlarmClockScheduleCall(mTimeList[i], true)
//                    }
//                    if (mTimeList.size <= 0) {
//                        TLog.error("删除===")
//                        var mTimeBean = TimeBean()
//                        mTimeBean.number = 0
//                        mTimeBean.switch = 0
//                        mTimeBean.characteristic = TimeBean.ALARM_FEATURES.toInt()
//                        BleWrite.writeAlarmClockScheduleCall(mTimeBean, true)
//                    }
//                    var deleteTime = System.currentTimeMillis() / 1000
//                    Hawk.put(ALARM_CLOCK_CREATE_TIME, deleteTime)
//                    TLog.error("数据流++${Gson().toJson(mTimeList)}")
//                    Hawk.put(TIME_LIST, mTimeList)
//                    saveAlarmClock(deleteTime)
                }

                mAlarmClockAdapter.notifyDataSetChanged()
            }

            override fun onClick(pos: Int) {
                JumpUtil.startAlarmClockActivity(this@AlarmClockListActivity, type, pos)
            }

        })


        saveLocalAlarmClock(mTimeList)

        saveAlarmClock(System.currentTimeMillis()/1000)
    }
    private fun setUpdateTime(time: Long):Long
    {   //原来设置的时间
        var setTime=time
        //获取当前时间
        var upDataTime = System.currentTimeMillis() / 1000
        if(setTime>upDataTime)
            return time //大于的话显示没任何问题
        //获取相差天数
       var dayDifference=(upDataTime-setTime)/86400+1
        return setTime+dayDifference*86400

    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvSettingAlarmClock -> {

                JumpUtil.startAlarmClockActivity(this@AlarmClockListActivity, type)
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.getRemind("1")
    }


    var time = 0
    override fun onResume() {
        super.onResume()
        time = Hawk.get(ALARM_CLOCK_CREATE_TIME, 0)
        setAdapter()

    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultRemind.observe(this)
        { it ->
            TLog.error("it==" + Gson().toJson(it))
            TLog.error("it.alarmClock.createTime==${it.alarmClock.createTime}  time+==${time}")

            //saveAlarmClock(System.currentTimeMillis() / 1000)

            if(it.alarmClock.list == null || it.alarmClock.list.isEmpty()){
                val emptyData = ArrayList<TimeBean>()
                saveLocalAlarmClock(emptyData)
                return@observe
            }
            mTimeList.clear()
            var list = it.alarmClock.list
            list.forEachIndexed { index, listDTO ->
                val bean = TimeBean()
                bean.characteristic = listDTO.characteristic
                bean.hours = listDTO.hours
                bean.mSwitch = listDTO.getmSwitch()
                bean.min = listDTO.min
                bean.number = index
                bean.specifiedTime = listDTO.specifiedTime
                bean.unicode = listDTO.unicode
                bean.unicodeType = listDTO.unicodeType.toByte()
                bean.specifiedTimeDescription = listDTO.specifiedTimeDescription
                bean.endTime = listDTO.endTime
                if(listDTO.endTime<System.currentTimeMillis()/1000 && listDTO.specifiedTime==128)
                {
                    bean.mSwitch = 1
                }
                mTimeList.add(bean)
            }
            saveLocalAlarmClock(mTimeList)

//
//            if (it==null||it.alarmClock==null||it.alarmClock.createTime < time) {
//                TLog.error("修改数据")
//                saveAlarmClock(System.currentTimeMillis() / 1000)
//            } else if (it.alarmClock.createTime > time) {
//                var list = it.alarmClock.list
//                TLog.error("list==" + Gson().toJson(list))
//                mTimeList.clear()
//                list.forEach {
//                    var bean = TimeBean()
//                    bean.characteristic = it.characteristic
//                    bean.hours = it.hours
//                    bean.mSwitch = it.getmSwitch()
//                    bean.min = it.min
//                    bean.number = it.number
//                    bean.specifiedTime = it.specifiedTime
//                    bean.unicode = it.unicode
//                    bean.unicodeType = it.unicodeType.toByte()
//                    bean.specifiedTimeDescription = it.specifiedTimeDescription
//                    bean.endTime = it.endTime
//                    if(it.endTime<System.currentTimeMillis()/1000&&it.specifiedTime==128)
//                    {
//                        bean.mSwitch = 1
//                    }
//                    mTimeList.add(bean)
//                    BleWrite.writeAlarmClockScheduleCall(bean, false)
//                }
//            }
            TLog.error("mAlarmClockAdapter+="+Gson().toJson(mAlarmClockAdapter.data))
            mAlarmClockAdapter.notifyDataSetChanged()
        }
    }



    //将数据保存在本地数据库
    private fun saveLocalAlarmClock(mAlarmClockList : ArrayList<TimeBean>){
        Hawk.put(TIME_LIST, mAlarmClockList)

        TLog.error("------保存到本地闹钟="+Gson().toJson(mAlarmClockList))
        //无闹钟情况
        if(mAlarmClockList.isEmpty()){
            var mTimeBean = TimeBean()
            mTimeBean.number = 0
            mTimeBean.switch = 0
            mTimeBean.characteristic = TimeBean.ALARM_FEATURES.toInt()
            BleWrite.writeAlarmClockScheduleCall(mTimeBean, true)

            return
        }


        mAlarmClockList.forEachIndexed { index, timeBean ->
            timeBean.number = index
            BleWrite.writeAlarmClockScheduleCall(timeBean, true)
        }



    }




    fun saveAlarmClock(deleteTime: Long) {
      //  mTimeList = Hawk.get(TIME_LIST, ArrayList())
        val mAlarmClockList: ArrayList<AlarmClockBean> = ArrayList()
        mTimeList.forEach {
            mAlarmClockList.add(
                AlarmClockBean(
                    it.characteristic,
                    it.hours,
                    it.mSwitch,
                    it.min,
                    it.number,
                    it.specifiedTime,
                    it.unicode,
                    it.dataUnitType,
                    it.getSpecifiedTimeDescription(),
                    it.endTime
                )
            )
        }
        val bean = Gson().toJson(mAlarmClockList)
        val data = HashMap<String, String>()
        data["alarmClock"] = bean
        data["createTime"] = (deleteTime).toString()
        mViewModel.saveAlarmClock(data)
    }
}