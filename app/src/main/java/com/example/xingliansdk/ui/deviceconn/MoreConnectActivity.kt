package com.example.xingliansdk.ui.deviceconn

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.moreDevice.ConnectRecordViewModel
import com.example.xingliansdk.utils.GsonUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_blood_pressure.titleBar
import kotlinx.android.synthetic.main.activity_more_connect_layout.*
import me.hgj.jetpackmvvm.ext.util.toJson
import org.json.JSONObject

/**
 * 已经连接的更多设备
 * Created by Admin
 *Date 2022/7/27
 */
class MoreConnectActivity : BaseActivity<ConnectRecordViewModel>(){


    private var recordAdapter : MoreConnectedDeviceAdapter ?= null
    private var moreList : ArrayList<ConnectedDeviceBean> = arrayListOf()



    override fun layoutId(): Int {
        return R.layout.activity_more_connect_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        initRecyclerView()

        initData()

    }

    private fun initRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        moreDeviceRecyclerView.layoutManager = linearLayoutManager


        recordAdapter = MoreConnectedDeviceAdapter(moreList,this)
        moreDeviceRecyclerView.adapter = recordAdapter


    }



    private fun initData(){
        mViewModel.getConnRecordDevice()

    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.recordDeviceResult.observe(this){
            TLog.error("-------data="+Gson().toJson(it))

            val jsonObject = JSONObject(it.toJson())

            val listStr = jsonObject.get("list")

          

//            val lt = GsonUtils.getGsonObject<List<ConnectedDeviceBean>>(Gson().toJson(listStr))
//
//            moreList.clear()
//            if (lt != null) {
//                moreList.addAll(lt)
//            }
//            recordAdapter?.notifyDataSetChanged()

            TLog.error("--listStr="+Gson().toJson(listStr))
        }



    }
}