package com.app.fmate.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.app.fmate.R
import com.app.fmate.adapter.IndexAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.*
import com.shon.connector.utils.TLog.Companion.error
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : BaseActivity<BaseViewModel>(), OnRefreshListener,View.OnClickListener {

    lateinit var mList: MutableList<MultiItemEntity>
    lateinit var mIndexAdapter: IndexAdapter
    override fun layoutId() = R.layout.activity_home

    override fun initView(savedInstanceState: Bundle?) {

        intView()
    }

    private fun intView() {
//        mSwipeRefreshLayout.setOnRefreshListener(this)
        rvAll.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        if (::mSearchAdapter.isInitialized)
        mList = mutableListOf()
        mIndexAdapter = IndexAdapter(mList)
        addList()
        rvAll.adapter = mIndexAdapter
        mIndexAdapter.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
            error(
                "$position+${Gson().toJson(
                    mList[position]
                )}"
            )
        })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mList.clear()
        addList()
        val msg = Message()
        handler.sendMessage(msg)
    }

    private lateinit var mMotionList: MutableList<MotionResult>
    private lateinit var mBloodPressureList: MutableList<BloodPressureResult>
    private lateinit var mChildResultList: MutableList<ChildResult>
    private lateinit var mChildBloodPressureList: MutableList<ChildBloodPressureResult>
    //睡眠
    private lateinit var mSleepResultList: MutableList<SleepResult>
    private lateinit var mChildSleepResultList: MutableList<ChildSleepResult>
    private lateinit var mHeartRateResultList: MutableList<HeartRateResult>
    private lateinit var mChildHeartRateResultList: MutableList<ChildHeartRateResult>
    fun addList() {
        mMotionList = arrayListOf()
        mBloodPressureList = arrayListOf()
        mChildResultList = arrayListOf()
        mChildBloodPressureList = arrayListOf()
        mSleepResultList = arrayListOf()
        mChildSleepResultList = arrayListOf()

        mHeartRateResultList= arrayListOf()
        mChildHeartRateResultList= arrayListOf()
        Hawk.put("motionMax", 0)
        for (i in 0..10) {
            val rand = Random()
            var step = 200 + (rand.nextInt(200))
            var high = 100 + (rand.nextInt(50))
            var low = 60 + (rand.nextInt(30))
            var type=rand.nextInt(5)
            var min =rand.nextInt(300)
            var heartRate = 60 + (rand.nextInt(45))
            if (Hawk.get("motionMax", 0) < step) {
                Hawk.put<Int>("motionMax", step)
            }

            val mChildResult = ChildResult((8 + i).toString() + ":00", step.toLong())
            mChildResultList.add(mChildResult)
            val mChildBloodPressureResult =
                ChildBloodPressureResult((8 + i).toString() + ":00", high, low)
            mChildBloodPressureList.add(mChildBloodPressureResult)
            val mChildSleepResult =
                ChildSleepResult((8 + i).toString() + ":00",min.toLong(), type)
          mChildSleepResultList.add(mChildSleepResult)
            val mChildHeartRateResult =
                ChildHeartRateResult((8 + i).toString() + ":00", heartRate)
            mChildHeartRateResultList.add(mChildHeartRateResult)
        }
        //运动
        val mMotionResult = MotionResult(mChildResultList)
        mMotionList.add(mMotionResult)
        //血压
        val mBloodPressureResult =
            BloodPressureResult(mChildBloodPressureList)
        mBloodPressureList.add(mBloodPressureResult)
        //
        val mSleepResult =
            SleepResult(mChildSleepResultList)
        mSleepResultList.add(mSleepResult)
        //
        val mHeartRateResult =
            HeartRateResult(mChildHeartRateResultList)
        mHeartRateResultList.add(mHeartRateResult)
        mList.addAll(mMotionList)
        mList.addAll(mBloodPressureList)
        mList.addAll(mHeartRateResultList)
        mList.addAll(mSleepResultList)
//        mSwipeRefreshLayout.finishRefresh()
        mIndexAdapter.notifyDataSetChanged()

    }

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
                    val rand = Random()
                    var step=rand.nextInt(100)
//                    CircularProgressView.setTextSize(60)
//                    CircularProgressView.setText(2000*step/100)
//                    CircularProgressView.setProgress(step,1000)  //这个成功了 其他几个都没改变颜色 字体大小以及显示值
        }
    }

    override fun onClick(v: View) {
         when(v.id)
         {
             R.id.tv_connect->
             {
                 startActivity(Intent(this,BleConnectActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
             }
             R.id.CircularProgressView->
             {
                 val msg = Message()
                 handler.sendMessage(msg)
             }
         }
    }
}