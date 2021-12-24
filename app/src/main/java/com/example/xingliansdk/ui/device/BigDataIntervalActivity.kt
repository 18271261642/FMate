package com.example.xingliansdk.ui.device

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.BigDataIntervalAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.utils.HelpUtil
import com.shon.connector.utils.TLog
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.bean.DataBean
import kotlinx.android.synthetic.main.activity_module_measurement_list.*

class BigDataIntervalActivity : BaseActivity<BaseViewModel>()
    , BleWrite.DeviceDeviceBigDataIntervalInterface,
    BigDataIntervalAdapter.EditAbleListAdapterListener, View.OnClickListener {
    lateinit var mAdapter: BigDataIntervalAdapter
    lateinit var mList: ArrayList<Int>
    var mData: DataBean = DataBean()
    override fun layoutId() = R.layout.activity_module_measurement_list

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        mList = ArrayList()
        tvSetting.visibility = View.VISIBLE
        tvPrompt.visibility = View.GONE
        tvSetting.setOnClickListener(this)
        BleWrite.writeForGetDeviceBigDataInterval(this)
    }

    fun setAdapter() {

        recyclerview.layoutManager = LinearLayoutManager(
            this@BigDataIntervalActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        mAdapter = BigDataIntervalAdapter(mList, this)
        recyclerview.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.tvSetting)
        mAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.tvSetting -> {

                    TLog.error("=${mData}")

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
    }

    override fun onResult(mDataBean: DataBean?) {
        if (mDataBean != null) {
            mList.add(mDataBean.heartRate)
            mList.add(mDataBean.heartRate1)
            mList.add(mDataBean.bloodOxygen)
            mList.add(mDataBean.bloodPressure)
            mList.add(mDataBean.temperature.toInt())
            mList.add(mDataBean.activity)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun onEditTextChanged(position: Int, value: CharSequence) {
        if (value.isNotEmpty() && HelpUtil.isNumeric(value.toString()))
            mList[position] = value.toString().toInt()
        else
            mList[position] = 0
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvSetting -> {
                mData.heartHealth = mList[0]
                mData.heartRate1 = mList[1]
                mData.bloodOxygen = mList[2]
                mData.bloodPressure = mList[3]
                mData.temperature = mList[4].toString()
                mData.activity = mList[5]
                BleWrite.writeSettingStorageIntervalCall(mData)
                mAdapter.notifyDataSetChanged()
            }
        }
    }
}