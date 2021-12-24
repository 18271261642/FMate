package com.example.xingliansdk.ui.setting.takeMedicine

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.TakeMedicineAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.setAllClock.SetAllClockViewModel
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.RemindTakeMedicineBean
import com.shon.connector.bean.TimeBean
import kotlinx.android.synthetic.main.activity_take_medicine_index.*

/**
 * 吃药提醒
 */
class TakeMedicineIndexActivity : BaseActivity<SetAllClockViewModel>(), View.OnClickListener {


    lateinit var mList: ArrayList<RemindTakeMedicineBean>
    private lateinit var mTakeMedicineAdapter: TakeMedicineAdapter
    override fun layoutId() = R.layout.activity_take_medicine_index
    var position = -1
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvAdd.setOnClickListener(this)
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                if (mList.size >= 5) {
                    ShowToast.showToastLong("最多只可以添加五条,请选择删除或修改")
                    return
                }
                JumpUtil.startTakeMedicineActivity(this@TakeMedicineIndexActivity)
            }

            override fun onActionClick() {
            }
        }
        )
    }

    var time = 0L
    override fun onResume() {
        super.onResume()
        TLog.error("onResume变更")
        time = Hawk.get(Config.database.TAKE_MEDICINE_CREATE_TIME, 0L)
        setAdapter()
        mViewModel.getRemind("3")
    }

    fun setAdapter() {
        mList = Hawk.get(Config.database.REMIND_TAKE_MEDICINE, ArrayList())
        TLog.error("m==" + Gson().toJson(mList))
        recyclerview.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        mTakeMedicineAdapter = TakeMedicineAdapter(mList)
        recyclerview.adapter = mTakeMedicineAdapter
        setVisible()
        mTakeMedicineAdapter.setOnDelListener(object : TakeMedicineAdapter.onSwipeListener {
            override fun onDel(pos: Int) {
                if (pos >= 0 && pos < mList.size) {
                    TLog.error("mlist=+${Gson().toJson(mList)}")
                    mList.removeAt(pos)
                    mTakeMedicineAdapter.notifyItemRemoved(pos)
                    for (i in 0 until mList.size) {
                        mList[i].number = i
                        BleWrite.writeRemindTakeMedicineCall(mList[i], false)
                    }
                    if (mList.size <= 0) {
                        TLog.error("删除===")
                        var mTimeBean = RemindTakeMedicineBean()
                        mTimeBean.number = 0
                        mTimeBean.switch = 0
                        BleWrite.writeRemindTakeMedicineCall(mTimeBean, false)
                    }
                    TLog.error("数据流++${Gson().toJson(mList)}")
                    setVisible()
                    Hawk.put(Config.database.REMIND_TAKE_MEDICINE, mList)
                    var deleteTime = System.currentTimeMillis() / 1000
                    Hawk.put(Config.database.TAKE_MEDICINE_CREATE_TIME, deleteTime)
                    saveTakeMedicine(deleteTime)

                    mTakeMedicineAdapter.notifyDataSetChanged()
                }
            }

            override fun onClick(pos: Int) {
                JumpUtil.startTakeMedicineActivity(this@TakeMedicineIndexActivity, pos)
            }

        })
    }

    private fun setVisible() {
        if (mList.size > 0) {
            llNoTakeMedicine.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
        } else {
            llNoTakeMedicine.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvAdd ->
                JumpUtil.startTakeMedicineActivity(this)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultRemind.observe(this)
        {
            TLog.error("吃药==" + Gson().toJson(it))
            if (it == null || it.takeMedicine == null || it.takeMedicine.createTime < time) {
                TLog.error("吃药==修改本地的到网络")
                saveTakeMedicine(time)
            } else if (it.takeMedicine.createTime > time) {

                mList.forEach {
                    BleWrite.writeRemindTakeMedicineCall(it, true)
                }
                mList = it.takeMedicine.list as ArrayList<RemindTakeMedicineBean>
                Hawk.put(Config.database.TAKE_MEDICINE_CREATE_TIME, it.takeMedicine.createTime)
                TLog.error("吃药==修改本地")
            }
            setVisible()
            mTakeMedicineAdapter.notifyDataSetChanged()
        }

    }

    private fun saveTakeMedicine(time: Long) {
        if (mList.isNullOrEmpty() || mList.size <= 0)
            return
        var bean = Gson().toJson(mList)
        var data = HashMap<String, String>()
        data["takeMedicine"] = bean
        data["createTime"] = (time).toString()
        mViewModel.saveTakeMedicine(data)
    }
}