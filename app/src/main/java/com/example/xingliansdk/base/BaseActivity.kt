package com.example.xingliansdk.base

import android.bluetooth.BluetoothAdapter
import android.content.res.Resources
import android.os.Bundle
import com.example.xingliansdk.base.activity.BaseVmActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog
import me.jessyan.autosize.AutoSizeCompat


/**
 * 时间　: 2019/12/21
 * 作者　: hegaojian
 * 描述　: 你项目中的Activity基类，在这里实现显示弹窗，吐司，还有加入自己的需求操作 ，如果不想用Databind，请继承
 * BaseVmActivity例如
 * abstract class BaseActivity<VM : BaseViewModel> : BaseVmActivity<VM>() {
 */
abstract class BaseActivity<VM : BaseViewModel> : BaseVmActivity<VM>() {

    abstract override fun layoutId(): Int
    //Application全局的ViewModel，用于发送全局通知操作
  //  val mainViewModel: MainViewModel by lazy { getAppViewModel<MainViewModel>() }
    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
       // showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
      //  dismissLoadingExt()
    }

    /**
     * 强制开启当前 Android 设备的 Bluetooth
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    open fun turnOnBluetooth(): Boolean {
        val bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter()
        if(bluetoothAdapter.isEnabled)
            return true
        val status=bluetoothAdapter.enable()
        if(!status)
        {
            ShowToast.showToastLong("蓝牙已被关闭,请开启蓝牙")
        }
        return status
    }
    /**
     * 在任何情况下本来适配正常的布局突然出现适配失效，适配异常等问题，只要重写 Activity 的 getResources() 方法
     */
//    override fun getResources(): Resources {
//        AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
//        return super.getResources()
//    }
}