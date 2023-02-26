package com.app.fmate.base.fragment

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import com.app.fmate.base.viewmodel.BaseViewModel
import com.shon.connector.utils.TLog

abstract  class BaseFragment<VM:BaseViewModel> :BaseVmFragment<VM>() {
    /**
     * 当前Fragment绑定的视图布局
     */
    abstract override fun layoutId(): Int

    abstract override fun initView(savedInstanceState: Bundle?)
    /**
     * 懒加载 只有当前fragment视图显示时才会触发该方法
     */
    override fun lazyLoadData() {
    }
    /**
     * 创建LiveData观察者 Fragment执行onViewCreated后触发
     */
    override fun createObserver() {
    }
    /**
     * Fragment执行onViewCreated后触发
     */
    override fun initData() {
    }
    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
    }
    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
    }
    override fun onPause() {
        super.onPause()
      //  hideSoftKeyboard(activity)
    }
    /**
     * 强制开启当前 Android 设备的 Bluetooth
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    open fun turnOnBluetooth(): Boolean {
        val bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter()
//        TLog.error("蓝牙+++"+bluetoothAdapter.isEnabled)
         if(bluetoothAdapter.isEnabled) {
//            TLog.error("已启用")
             return  true
        }
        val status=bluetoothAdapter.enable()
        TLog.error("status+=$status")
        return bluetoothAdapter.isEnabled
        return status
    }

}