package com.example.xingliansdk.ui.deviceconn

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.AddDeviceSelectAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.device.DeviceCategoryBean
import com.example.xingliansdk.network.api.device.DeviceCategoryViewModel
import com.example.xingliansdk.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_add_device_select_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*


//添加设备页面
class AddDeviceSelectActivity : BaseActivity<DeviceCategoryViewModel>() {


    private var addSelectAdapter : AddDeviceSelectAdapter ?= null
    private var dbBean : DeviceCategoryBean ?= null


    override fun layoutId(): Int {
      return R.layout.activity_add_device_select_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(addSelectTB)
            .init()
        addSelectTB.setTitleBarListener(object : TitleBarLayout.TitleBarListener{
            override fun onBackClick() {
               finish()
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {

            }

        })
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        addSelectRyView.layoutManager = linearLayoutManager
        dbBean = DeviceCategoryBean()
//        addSelectAdapter = AddDeviceSelectAdapter(dbBean!!,this)
//        addSelectRyView.adapter = addSelectAdapter

        mViewModel.getAllDeviceCategory()
    }

    override fun createObserver() {
        super.createObserver()


        mViewModel.deviceCategoryResult.observe(this){
            val bean = it as DeviceCategoryBean
            addSelectAdapter = AddDeviceSelectAdapter(bean,this)
            addSelectRyView.adapter = addSelectAdapter
        }

        mViewModel.dCategoryMsg.observe(this){
            
        }

    }

}