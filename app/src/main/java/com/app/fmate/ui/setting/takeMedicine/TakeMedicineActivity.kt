package com.app.fmate.ui.setting.takeMedicine

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.adapter.TimesPerDayAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.dialog.DateSelectDialogView
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.setAllClock.SetAllClockViewModel
import com.app.fmate.utils.HelpUtil
import com.app.fmate.utils.JumpUtil
import com.app.fmate.utils.RecycleViewDivider
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.app.fmate.view.DateUtil
import com.app.fmate.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.RemindTakeMedicineBean
import kotlinx.android.synthetic.main.activity_take_medicine.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 * 吃药提醒编辑页面
 */
class TakeMedicineActivity : BaseActivity<SetAllClockViewModel>(), View.OnClickListener {

    private val tags = "TakeMedicineActivity"

    private lateinit var mTimesPerDayAdapter: TimesPerDayAdapter
    var mBean: RemindTakeMedicineBean = RemindTakeMedicineBean()
    lateinit var mRemindTakeMedicineList: MutableList<RemindTakeMedicineBean>
    var position = -1
    override fun layoutId() = R.layout.activity_take_medicine
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        SNEventBus.register(this)
        settingNumber.setOnClickListener(this)
        settingRepeat.setOnClickListener(this)
        settingStartTime.setOnClickListener(this)
        settingEndTime.setOnClickListener(this)
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
                HelpUtil.hideSoftInputView(this@TakeMedicineActivity)
            }

            override fun onActionImageClick() {
            }

            override fun onActionClick() {
                if (mRemindTakeMedicineList.size > 5) {
                    ShowToast.showToastLong(resources.getString(R.string.string_take_medic_most))
                    return
                }
                if (mBean.startTime > mBean.endTime && mBean.endTime > 0) {
                    ShowToast.showToastLong(resources.getString(R.string.string_take_medic_start_time_1))
                    return
                }
                var saveTime = System.currentTimeMillis() / 1000
                if (mBean.startTime <= 1000)
                    mBean.startTime = saveTime
                TLog.error("mTimeList.size++" + mRemindTakeMedicineList.size + " , num==")
                mBean.switch = 2
                mBean.unicodeTitle = edtTitle.text.toString()

                if (mRemindTakeMedicineList.size > 0 && position >= 0) {
                    mRemindTakeMedicineList[position] = mBean
                } else {
                    mBean.number = mRemindTakeMedicineList.size
                    mRemindTakeMedicineList.add(mBean)
                }
                Hawk.put(Config.database.REMIND_TAKE_MEDICINE, mRemindTakeMedicineList)
                TLog.error("m==" + Gson().toJson(mBean))
                TLog.error("m==" + Gson().toJson(mRemindTakeMedicineList))
                mRemindTakeMedicineList.forEach {
                    BleWrite.writeRemindTakeMedicineCall(it, true)
                }
//                var bean = Gson().toJson(mRemindTakeMedicineList)
//                var data = HashMap<String, String>()
//                data["takeMedicine"] = bean
//                data["createTime"] = (saveTime).toString()
//                mViewModel.saveTakeMedicine(data)
//                Hawk.put(Config.database.TAKE_MEDICINE_CREATE_TIME, saveTime)
                finish()
            }
        })
        mRemindTakeMedicineList = Hawk.get(Config.database.REMIND_TAKE_MEDICINE, ArrayList())
        position = intent.getIntExtra("update", -1)

        if (position >= 0) {
            mBean = mRemindTakeMedicineList[position]
            settingNumber.setContentText("${mBean.getGroupList().size}"+resources.getString(R.string.string_times))
            TLog.error("==数据+" + Gson().toJson(mBean.getGroupList()))
            setAdapter(mBean.getGroupList())
            if (mBean.reminderPeriod == 0)
                settingRepeat.setContentText(resources.getString(R.string.string_every_day))
            else
                settingRepeat.setContentText(resources.getString(R.string.string_interval)+"${mBean.reminderPeriod}"+resources.getString(R.string.string_day))

            if (mBean.startTime <= 100)
                settingStartTime.setContentText(
                    DateUtil.getDate(
                        DateUtil.YYYY_MM_DD,
                        System.currentTimeMillis()
                    )
                )
            else
                settingStartTime.setContentText(
                    DateUtil.getDate(
                        DateUtil.YYYY_MM_DD,
                        mBean.startTime * 1000
                    )
                )
            if (mBean.endTime <= 100)
                settingEndTime.setContentText(resources.getString(R.string.string_permanent))
            else
                settingEndTime.setContentText(
                    DateUtil.getDate(
                        DateUtil.YYYY_MM_DD,
                        mBean.endTime * 1000
                    )
                )
            edtTitle.setText(mBean.unicodeTitle)
        } else {
            val calendar = Calendar.getInstance()
            settingStartTime.setContentText(
                DateUtil.getDate(
                    DateUtil.YYYY_MM_DD,
                    System.currentTimeMillis()
                )
            )
            var mReminderGroup: MutableList<RemindTakeMedicineBean.ReminderGroup> = arrayListOf()
            mReminderGroup.add(mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
            setAdapter(mReminderGroup)
        }
        initDatePicker(0)
    }

    var startEndType = false
    override fun onClick(v: View) {
        when (v.id) {
            R.id.settingNumber -> {
                dialog()
            }
            R.id.settingRepeat -> {
                TLog.error("mBean.ReminderPeriod+" + mBean.reminderPeriod)
                JumpUtil.startTakeMedicineRepeatActivity(this, mBean.reminderPeriod)
            }
            R.id.settingStartTime -> { //开始时间
                TLog.error("dianji")
                startEndType = true
                //initDatePicker(0) //为啥再次初始化 因为要判断和改变 setdate
                showDateSelect(0,false)
//                dateTime?.let {
//                    if (it.isShowing)
//                        it.dismiss()
//                    dateTime?.show()
//                }
            }
            R.id.settingEndTime -> { //结束时间
                startEndType = false
               // initDatePicker(1)
                showDateSelect(1,true)
//                dateTime?.let {
//                    if (it.isShowing)
//                        it.dismiss()
//                    dateTime?.show()
//                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }

    var mList: MutableList<RemindTakeMedicineBean.ReminderGroup> = arrayListOf()
    fun setAdapter(mTakeList: MutableList<RemindTakeMedicineBean.ReminderGroup>) {
        mList.clear()
        mTakeList.sortBy { it.countHM }
        this.mList.addAll(mTakeList)
        mBean.setGroupList(mTakeList)
        ryIndex.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        ryIndex.addItemDecoration(
            RecycleViewDivider(
                this,
                LinearLayoutManager.HORIZONTAL,
                1,
                resources.getColor(R.color.color_view)
            )
        )
        mTimesPerDayAdapter = TimesPerDayAdapter(mList)
        ryIndex.adapter = mTimesPerDayAdapter
        ryIndex.isNestedScrollingEnabled = false
        mTimesPerDayAdapter.setOnItemClickListener { adapter, view, position ->
            initTimePicker(position)
            pvTime?.show()
        }


    }

    private var pvTime: TimePickerView? = null
    private fun initTimePicker(pos: Int) { //Dialog 模式下，在底部弹出
        val timeCalendar = Calendar.getInstance()
        timeCalendar.set(Calendar.MINUTE,mList[pos].groupMM)
        timeCalendar.set(Calendar.HOUR_OF_DAY,mList[pos].groupHH)
        pvTime = TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->

                mList[pos].groupHH =  DateUtil.getHour(date)
                mList[pos].groupMM = DateUtil.getMinute(date)
                mBean.setGroupList(mList)
                mTimesPerDayAdapter.notifyItemChanged(pos)
//                TLog.error("分+++"+mTimeBean.min+"时++"+mTimeBean.hours)
            })
            .setType(booleanArrayOf(false, false, false, true, true, false))
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .setDate(timeCalendar)
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

    private var dateTime: TimePickerView? = null
    private fun initDatePicker(code : Int) { //Dialog 模式下，在底部弹出
        val ca: Calendar = Calendar.getInstance()

//        if (startEndType && mBean.startTime > 1000)
//            ca.timeInMillis = mBean.startTime * 1000
//        else if (mBean.endTime > 1000) {
//            ca.timeInMillis = mBean.endTime * 1000
//        }
        //当天的long
        val currDayLong = DateUtil.getCurrDayToLongLast();
        if(mBean.startTime == 0L)
            mBean.startTime = currDayLong
        TLog.error("时间" + (mBean.startTime * 1000) + ",  结束++" + (mBean.endTime * 1000)+" "+currDayLong)
//        if(mBean.endTime<currDayLong){
//            mBean.endTime = currDayLong
//        }

        if(code == 0){
//            ca.set(Calendar.YEAR,DateUtil.getYear(mBean.startTime*1000));
//            ca.set(Calendar.MONTH,DateUtil.getMonth(mBean.startTime*1000))
//            ca.set(Calendar.DAY_OF_MONTH,DateUtil.getDay(mBean.startTime*1000))
            ca.timeInMillis = if(mBean.startTime * 1000 <currDayLong * 1000) currDayLong* 1000 else mBean.startTime * 1000 ;
        }else{
            ca.timeInMillis = if(mBean.endTime <currDayLong) currDayLong * 1000 else mBean.endTime * 1000;
//            ca.set(Calendar.YEAR,DateUtil.getYear(mBean.endTime*1000));
//            ca.set(Calendar.MONTH,DateUtil.getMonth(mBean.endTime*1000))
//            ca.set(Calendar.DAY_OF_MONTH,DateUtil.getDay(mBean.endTime*1000))
        }

        dateTime = TimePickerBuilder(
            this
        ) { date, v ->
            //选择的时间
            val selectDate = DateUtil.getDate(DateUtil.YYYY_MM_DD, date);
            //转换成long类型
            var selectLongDate = DateUtil.getCurrDayToLongLast(selectDate)
            //开始时间，不能小于当天，可以等于当天
            if(code == 0){

                Log.e(tags,"--------选择的时间="+selectDate+" 转换后="+selectLongDate+" "+(selectLongDate < currDayLong))
                if(selectLongDate < currDayLong){
                    ShowToast.showToastLong(resources.getString(R.string.string_take_medic_start_time_2))
                    return@TimePickerBuilder
                }
                if(mBean.endTime>100){
                    if(selectLongDate>mBean.endTime){
                        ShowToast.showToastLong(resources.getString(R.string.string_take_medic_start_time_1))
                        return@TimePickerBuilder
                    }
                }


                mBean.startTime =selectLongDate
                settingStartTime.setContentText(selectDate)

            }else{  //结束时间
                //结束时间可以登录开始时间，但是不能不开始时间小
                if(selectLongDate < mBean.startTime){
                    ShowToast.showToastLong(resources.getString(R.string.string_take_medic_end_time))
                    return@TimePickerBuilder

                }
                mBean.endTime =selectLongDate
                settingEndTime.setContentText(selectDate)

            }

            TLog.error("+++" + mBean.startTime + "  时++" + DateUtil.getDateToLongLast(date) + " endTime===" + mBean.endTime)
        }

            .setType(booleanArrayOf(true, true, true, false, false, false))
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .isCyclic(true)
            .setDate(ca)
            .build()
        val mDialog: Dialog = dateTime?.dialog!!
        val params =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        params.leftMargin = 0
        params.rightMargin = 0
        dateTime?.let { it.dialogContainerLayout.layoutParams = params }
        val dialogWindow = mDialog.window
        if (dialogWindow != null) {
            dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim) //修改动画样式
            dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f)
        }
    }



    //显示日期选择框
    private fun showDateSelect(code : Int,isShowForever : Boolean){

        val ca: Calendar = Calendar.getInstance()
        //当天的long
        val currDayLong = DateUtil.getCurrDayToLongLast();
        if(mBean.startTime == 0L)
            mBean.startTime = currDayLong
        TLog.error("时间" + (mBean.startTime * 1000) + ",  结束++" + (mBean.endTime * 1000)+" "+currDayLong)
        if(code == 0){
            ca.timeInMillis = mBean.startTime * 1000 //if(mBean.startTime * 1000 <currDayLong * 1000) currDayLong* 1000 else mBean.startTime * 1000 ;
        }else{
            ca.timeInMillis = if(mBean.endTime <=100) currDayLong * 1000 else mBean.endTime * 1000//if(mBean.endTime <currDayLong) currDayLong * 1000 else mBean.endTime * 1000;
        }

        val dateSelectDialogView = DateSelectDialogView(this)
        dateSelectDialogView.show()
        dateSelectDialogView.setCurrentShowDate(ca.timeInMillis)
        dateSelectDialogView.isShowForeverBtn(isShowForever)
        dateSelectDialogView.setOnDateSelectListener(object : DateSelectDialogView.OnDateSelectListener{
            override fun onDateSelect(date: Date?) {
                //选择的时间
                val selectDate = DateUtil.getDate(DateUtil.YYYY_MM_DD, date);
                //转换成long类型
                var selectLongDate = DateUtil.getCurrDayToLongLast(selectDate)
                //开始时间，不能小于当天，可以等于当天
                if(code == 0){

                    Log.e(tags,"--------选择的时间="+selectDate+" 转换后="+selectLongDate+" "+(selectLongDate < currDayLong)+" "+mBean.endTime)
                    if(selectLongDate*1000 < mBean.startTime*1000 && selectLongDate < currDayLong){
                        ShowToast.showToastLong(resources.getString(R.string.string_take_medic_start_time_2))
                       return
                    }

                    if(mBean.endTime>100){
                        if(selectLongDate>mBean.endTime){
                            ShowToast.showToastLong(resources.getString(R.string.string_take_medic_start_time_1))
                            return
                        }
                    }


                    mBean.startTime =selectLongDate
                    settingStartTime.setContentText(selectDate)

                }else{  //结束时间
                    //结束时间可以登录开始时间，但是不能不开始时间小
                    if(selectLongDate < mBean.startTime){
                        ShowToast.showToastLong(resources.getString(R.string.string_take_medic_end_time))
                        return

                    }
                    mBean.endTime =selectLongDate
                    settingEndTime.setContentText(selectDate)

                }

                dateSelectDialogView.dismiss()
                TLog.error("+++" + mBean.startTime + "  时++" + DateUtil.getDateToLongLast(date) + " endTime===" + mBean.endTime)
            }

            override fun foreverSelect() {
                dateSelectDialogView.dismiss()
                mBean.endTime = 0
                settingEndTime.setContentText(resources.getString(R.string.string_permanent))

            }

        })
    }




    private fun dialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_times_per_day
            dimAmount = 0.3f
            isFullHorizontal = true
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_BOTTOM
            animStyle = R.style.BottomTransAlphaADAnimation
            var mList: MutableList<RemindTakeMedicineBean.ReminderGroup> = arrayListOf()
            convertListenerFun { holder, dialog ->
                val tvDele = holder.getView<TextView>(R.id.tvDele)
                val tvOne = holder.getView<TextView>(R.id.tvOne)
                val tvTwo = holder.getView<TextView>(R.id.tvTwo)
                val tvThree = holder.getView<TextView>(R.id.tvThree)
                val tvFour = holder.getView<TextView>(R.id.tvFour)

                if (tvOne != null) {
                    tvOne.text = "1"+resources.getString(R.string.string_times)
                }
                tvTwo?.text = "2"+resources.getString(R.string.string_times)
                tvThree?.text = "3"+resources.getString(R.string.string_times)
                tvFour?.text = "4"+resources.getString(R.string.string_times)

                val calendar = Calendar.getInstance()
                tvOne?.setOnClickListener {
                    if(mList.size>0){
                        mList.add(mBean.ReminderGroup(mList[0].groupHH, mList[0].groupMM))
                    }else{
                        mList.add(mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    }

                    settingNumber.setContentText(String.format(resources.getString(R.string.string_number_time),1))
                    setAdapter(mList)
                    dialog.dismiss()
                }
                tvTwo?.setOnClickListener {
                    mList.add(if(mList.size>0) mBean.ReminderGroup(mList[0].groupHH, mList[0].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    mList.add(if(mList.size>1)mBean.ReminderGroup(mList[1].groupHH, mList[1].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    settingNumber.setContentText(String.format(resources.getString(R.string.string_number_time),2))
                    setAdapter(mList)
                    dialog.dismiss()
                }
                tvThree?.setOnClickListener {
                    mList.add(if(mList.size>0) mBean.ReminderGroup(mList[0].groupHH, mList[0].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    mList.add(if(mList.size>1)mBean.ReminderGroup(mList[1].groupHH, mList[1].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    mList.add(if(mList.size>2) mBean.ReminderGroup(mList[2].groupHH, mList[2].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    settingNumber.setContentText(String.format(resources.getString(R.string.string_number_time),3))
                    setAdapter(mList)
                    dialog.dismiss()
                }
                tvFour?.setOnClickListener {
                    mList.add(if(mList.size>0) mBean.ReminderGroup(mList[0].groupHH, mList[0].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    mList.add(if(mList.size>1)mBean.ReminderGroup(mList[1].groupHH, mList[1].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    mList.add(if(mList.size>2) mBean.ReminderGroup(mList[2].groupHH, mList[2].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))
                    mList.add(if(mList.size>3) mBean.ReminderGroup(mList[3].groupHH, mList[3].groupMM) else mBean.ReminderGroup(DateUtil.getHour(calendar), DateUtil.getMinute(calendar)))

                    settingNumber.setContentText(String.format(resources.getString(R.string.string_number_time),4))
                    setAdapter(mList)
                    dialog.dismiss()
                }
                tvDele?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventResult(event: SNEvent<*>) {
        when (event.code) {
            Config.eventBus.REMIND_TAKE_MEDICINE_REMINDER_PERIOD -> {
                val type: Int = event.data.toString().toInt()
                mBean.reminderPeriod = type
                if (type <= 0) {
                    settingRepeat.setContentText(resources.getString(R.string.string_every_day))
                } else
                    settingRepeat.setContentText(resources.getString(R.string.string_interval)+type+resources.getString(R.string.string_day))
            }
        }
    }
}