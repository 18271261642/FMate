package com.example.xingliansdk.ui.deviceconn

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.AddDeviceSelectAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.device.DeviceCategoryBean
import com.example.xingliansdk.network.api.device.DeviceCategoryViewModel
import com.example.xingliansdk.ui.BleConnectActivity
import com.example.xingliansdk.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_add_device_select_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*
import java.util.*


//添加设备页面
class AddDeviceSelectActivity : BaseActivity<DeviceCategoryViewModel>(),AddDeviceSelectAdapter.OnBleScanItemClick {


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
            TLog.error("-----类型="+Gson().toJson(it))
            dbBean = it as DeviceCategoryBean
            addSelectAdapter = AddDeviceSelectAdapter(dbBean!!,this)
            addSelectRyView.adapter = addSelectAdapter
            addSelectAdapter!!.setOnBleScanClickListener(this)
        }

        mViewModel.dCategoryMsg.observe(this){
            
        }

    }



    override fun onItemClick(position: Int) {

        val deviceCategoryItemBean = dbBean?.list?.get(position)

//        val hasMac = Hawk.get("address","")
//
//        val connId = XingLianApplication.getXingLianApplication().getDeviceCategoryValue()
//
//        //判断是否已经连接过，已经连接了就提示先断开再连接
//        if(XingLianApplication.getXingLianApplication().getDeviceConnStatus() && (!TextUtils.isEmpty(hasMac) && hasMac.toLowerCase(
//                Locale.ROOT).equals(deviceCategoryItemBean.productList.g)))



        //类型，戒指或者手表
        val selectName = dbBean?.list?.get(position)?.name
        //图片url
        val deviceImg = dbBean?.list?.get(position)?.image
        //类型
        val categoryId = dbBean?.list?.get(position)?.id
        val intent = Intent(this@AddDeviceSelectActivity,BleConnectActivity::class.java)
        intent.putExtra("scan_name",selectName)
        intent.putExtra("scan_img",deviceImg)
        intent.putExtra("category_id",categoryId)
        //startActivity(intent)
        startActivityForResult(intent,0x00)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0x00){
            finish()
        }
    }

}