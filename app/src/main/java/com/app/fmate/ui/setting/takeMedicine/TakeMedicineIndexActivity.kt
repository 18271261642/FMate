package com.app.fmate.ui.setting.takeMedicine

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.adapter.TakeMedicineAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.network.api.setAllClock.SetAllClockViewModel
import com.app.fmate.utils.JumpUtil
import com.shon.connector.utils.ShowToast
import com.app.fmate.view.DateUtil
import com.shon.connector.utils.TLog
import com.app.fmate.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.RemindTakeMedicineBean
import kotlinx.android.synthetic.main.activity_take_medicine_index.*

/**
 * 吃药提醒
 */
class TakeMedicineIndexActivity : BaseActivity<SetAllClockViewModel>(), View.OnClickListener {

    private val tags = "TakeMedicineIndexActivity"

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
                    ShowToast.showToastLong(resources.getString(R.string.string_add_most_alarm))
                    return
                }
                JumpUtil.startTakeMedicineActivity(this@TakeMedicineIndexActivity)
            }

            override fun onActionClick() {
            }
        }
        )


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.getRemind("3")
    }


    var time = 0L
    override fun onResume() {
        super.onResume()
        TLog.error("onResume变更")
        time = Hawk.get(Config.database.TAKE_MEDICINE_CREATE_TIME, 0L)

        TLog.error(
            tags,
            "-------吃药修改时间=" + time + " " + DateUtil.getDate("yyyy-MM-dd HH:mm:ss", time)
        )

        setAdapter()

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
        mTakeMedicineAdapter!!.setOnDelListener(object : TakeMedicineAdapter.onSwipeListener {
            override fun onDel(pos: Int) {
                if (pos >= 0 && pos < mList.size) {
                    TLog.error("mlist=+${Gson().toJson(mList)}")
                    mList.removeAt(pos)
//                    mTakeMedicineAdapter.notifyItemRemoved(pos)
//                    for (i in 0 until mList.size) {
//                        mList[i].number = i
//                        BleWrite.writeRemindTakeMedicineCall(mList[i], false)
//                    }
//                    if (mList.size <= 0) {
//                        TLog.error("删除===")
//                        var mTimeBean = RemindTakeMedicineBean()
//                        mTimeBean.number = 0
//                        mTimeBean.switch = 0
//                        BleWrite.writeRemindTakeMedicineCall(mTimeBean, false)
//                    }
                    mTakeMedicineAdapter.notifyItemRemoved(pos)
                    TLog.error("数据流++${Gson().toJson(mList)}")
                    setVisible()
                    saveTakeMedicine(DateUtil.getCurrentSecond())
                   // Hawk.put(Config.database.REMIND_TAKE_MEDICINE, mList)


                    saveLocalCache(mList)
//                    var deleteTime = System.currentTimeMillis() / 1000
//                    Hawk.put(Config.database.TAKE_MEDICINE_CREATE_TIME, deleteTime)
//                    saveTakeMedicine(deleteTime)
                }
                mTakeMedicineAdapter.notifyDataSetChanged()
            }

            override fun onClick(pos: Int) {
                JumpUtil.startTakeMedicineActivity(this@TakeMedicineIndexActivity, pos)
            }

        })

        saveLocalCache(mList);
        saveTakeMedicine(DateUtil.getCurrentSecond())

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


    private fun saveLocalCache(remindTakeMedicineList: MutableList<RemindTakeMedicineBean>){
        Hawk.put(Config.database.REMIND_TAKE_MEDICINE, remindTakeMedicineList)
        if(remindTakeMedicineList.isEmpty()){
            val mTimeBean = RemindTakeMedicineBean()
            mTimeBean.number = 0
            mTimeBean.switch = 0
            BleWrite.writeRemindTakeMedicineCall(mTimeBean, false)
            return
        }



        mList.forEachIndexed { index, remindTakeMedicineBean ->
            remindTakeMedicineBean.number= index
                BleWrite.writeRemindTakeMedicineCall(remindTakeMedicineBean, true)
        }


    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.resultRemind.observe(this)
        {
            TLog.error("吃药==" + Gson().toJson(it))

            saveLocalCache(it.takeMedicine.list)
//
//            if (it == null || it.takeMedicine == null || it.takeMedicine.createTime < time) {
//                TLog.error("吃药==修改本地的到网络")
//                saveTakeMedicine(time)
//            } else if (it.takeMedicine.createTime > time) {
//
//                mList.forEach {
//                    BleWrite.writeRemindTakeMedicineCall(it, true)
//                }
//
//                TLog.error(tags, "-------1111-list=" + Gson().toJson(mList))
//
//                mList.clear()
//                mList = it.takeMedicine.list as ArrayList<RemindTakeMedicineBean>
//
//                TLog.error(tags, "-------222-list=" + Gson().toJson(mList))
//                Hawk.put(Config.database.TAKE_MEDICINE_CREATE_TIME, it.takeMedicine.createTime)
//                TLog.error("吃药==修改本地")
//                mTakeMedicineAdapter = TakeMedicineAdapter(mList)
//            }
//            setVisible()
//            mTakeMedicineAdapter.notifyDataSetChanged()
        }

    }

    private fun saveTakeMedicine(time: Long) {
//        if (mList.isNullOrEmpty() || mList.size <= 0)
//            return
        var bean = Gson().toJson(mList)
        var data = HashMap<String, String>()
        data["takeMedicine"] = bean
        data["createTime"] = (time).toString()

        Log.e("保存吃药", "-------saveTakeMedicine=$data")

        mViewModel.saveTakeMedicine(data)
    }
}