package com.app.fmate.ui.device

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.app.fmate.R
import com.app.fmate.adapter.ModuleMeasurementAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.shon.connector.utils.TLog
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.DataBean
import kotlinx.android.synthetic.main.activity_module_measurement_list.*


class ReminderPushListActivity : BaseActivity<BaseViewModel>(), View.OnClickListener
    , BleWrite.DeviceModuleMeasurementInterface {
    lateinit var mModuleMeasurementAdapter: ModuleMeasurementAdapter
    lateinit var mList: ArrayList<Int>
    private var mmList: ArrayList<Int> = ArrayList()
    private var typeList: ArrayList<String> = ArrayList()
    private var hoursList: ArrayList<Int> = ArrayList()
    var openOrClose = true
    var mData: DataBean = DataBean()
    override fun layoutId() = R.layout.activity_module_measurement_list
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        mList = ArrayList()
        for (i in 0..60) {
            mmList.add(i)
        }
        for (i in 0..24) {
            hoursList.add(i)
        }
        typeList.add(":")
        BleWrite.writeForGetDeviceModuleMeasurement(this)
    }

    fun setAdapter() {

        recyclerview.layoutManager = LinearLayoutManager(
            this@ReminderPushListActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        mModuleMeasurementAdapter = ModuleMeasurementAdapter(mList)
        recyclerview.adapter = mModuleMeasurementAdapter
        mModuleMeasurementAdapter.addChildClickViewIds(R.id.SwitchAlarmClock)
        mModuleMeasurementAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.SwitchAlarmClock -> {

                    time(position)
                    //    SwitchAlarmClock.isOpened=true

                    TLog.error("=${mData}")

                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.btnAdd -> {
//
//                //JumpUtil.startAlarmClockActivity(this, type)
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
    }

    override fun DeviceModuleMeasurementResult(mDataBean: DataBean?) {
        if (mDataBean != null) {
            mList.add(mDataBean.heartRateMeasureType)
            mList.add(mDataBean.bloodOxygenMeasureType)
            mList.add(mDataBean.bloodPressureMeasureType)
            mList.add(mDataBean.temperatureMeasureType)
            mList.add(mDataBean.humidityMeasureType)
            mModuleMeasurementAdapter.notifyDataSetChanged()
        }
    }

    private fun time(position: Int) {
        newGenjiDialog {
            layoutId = R.layout.dialog_swtich_time
            dimAmount = 0.3f
            isFullHorizontal = true
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_BOTTOM
            animStyle = R.style.BottomTransAlphaADAnimation
            convertListenerFun { holder, dialog ->

                var btnOk = holder.getView<TextView>(R.id.dialog_confirm)
                var btnCancel = holder.getView<TextView>(R.id.dialog_cancel)
                var openHours = holder.getView<WheelView>(R.id.wheelViewOpenHours)
                var closeHours = holder.getView<WheelView>(R.id.wheelViewCloseHours)
                var openMm = holder.getView<WheelView>(R.id.wheelViewOpenMm)
                var closeMm = holder.getView<WheelView>(R.id.wheelViewCloseMm)
                if (openHours != null) {
                    openHours.setTextSize(20f)
                    openHours.setLineSpacingMultiplier(2f)
                    openHours.setDividerType(WheelView.DividerType.CIRCLE)
                    openHours.adapter = ArrayWheelAdapter(hoursList)
                    openHours.currentItem = 8
                    mData.openHour = 8
                    openHours.setOnItemSelectedListener { index ->
                        mData.openHour = mmList[index]
                    }
                }
                if (openMm != null) {
                    openMm.setTextSize(20f)
                    openMm.setLineSpacingMultiplier(2f)
                    openMm.setDividerType(WheelView.DividerType.CIRCLE)
                    openMm.adapter = ArrayWheelAdapter(mmList)
                    openMm.currentItem = 0
                    mData.openMin = 0
                    openMm.setOnItemSelectedListener { index ->
                        mData.openMin = mmList[index]
                    }
                }
                if (closeHours != null) {
                    closeHours.setTextSize(20f)
                    closeHours.setLineSpacingMultiplier(2f)
                    closeHours.setDividerType(WheelView.DividerType.CIRCLE)
                    closeHours.adapter = ArrayWheelAdapter(hoursList)
                    closeHours.currentItem = 10
                    mData.closeHour=10
                    closeHours.setOnItemSelectedListener { index ->
                        mData.closeHour = mmList[index]
                    }
                }
                if (closeMm != null) {
                    closeMm.setTextSize(20f)
                    closeMm.setLineSpacingMultiplier(2f)
                    closeMm.setDividerType(WheelView.DividerType.CIRCLE)
                    closeMm.adapter = ArrayWheelAdapter(mmList)
                    closeMm.currentItem = 30
                    mData.closeMin=30
                    closeMm.setOnItemSelectedListener { index ->
                        mData.closeMin = mmList[index]
                    }
                }
                btnOk?.setOnClickListener {
                    dialog.dismiss()
                    TLog.error("mData=" + Gson().toJson(mData))
                    if (mList[position] == 2) {
                        mList[position] = 1
                    } else {
                        mList[position] = 2
                    }
                    mData.timeMeasurementSwitch = mList[position]
                    bleWrite(position)
                    mModuleMeasurementAdapter.notifyItemChanged(position)
                }
                btnCancel?.setOnClickListener {
                    dialog.dismiss()
                    mModuleMeasurementAdapter.notifyItemChanged(position)
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    private fun bleWrite(position: Int) {
        if (position == 0) {
            BleWrite.writeAutomaticMeasurementSwitchCall(
                Config.ControlClass.APP_MEASURING_HEART_RATE_SWITCH,
                mData
            )
        }
        if (position == 1)
            BleWrite.writeAutomaticMeasurementSwitchCall(
                Config.ControlClass.APP_MEASURING_BLOOD_OXYGEN_SWITCH,
                mData
            )
        if (position == 2)
            BleWrite.writeAutomaticMeasurementSwitchCall(
                Config.ControlClass.APP_BLOOD_PRESSURE_SWITCH,
                mData
            )
        if (position == 3)
            BleWrite.writeAutomaticMeasurementSwitchCall(
                Config.ControlClass.APP_TEMPERATURE_SWITCH,
                mData
            )
        if (position == 4)
            BleWrite.writeAutomaticMeasurementSwitchCall(
                Config.ControlClass.APP_HUMIDITY_SWITCH,
                mData
            )
    }
}