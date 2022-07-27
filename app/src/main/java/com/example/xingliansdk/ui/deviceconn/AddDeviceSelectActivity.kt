package com.example.xingliansdk.ui.deviceconn

import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.device.DeviceCategoryViewModel


//添加设备页面
class AddDeviceSelectActivity : BaseActivity<DeviceCategoryViewModel>() {


    override fun layoutId(): Int {
      return R.layout.activity_add_device_select_layout
    }

    override fun initView(savedInstanceState: Bundle?) {

    }
}