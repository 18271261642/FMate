package com.example.xingliansdk.base.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.xingliansdk.Config
import com.example.xingliansdk.base.activity.BaseVmActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.ext.getAppViewModel
import com.example.xingliansdk.ext.getVmClazz
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.manager.NetState
import com.example.xingliansdk.network.manager.NetworkStateManager
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.utils.ShowToast.showToastLong
import com.example.xingliansdk.viewmodel.MainViewModel
import com.example.xingliansdk.widget.LoadingDialogUtils
import com.gyf.barlibrary.SimpleImmersionFragment
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.orhanobut.hawk.Hawk
import com.shon.connector.bean.DeviceInformationBean
import java.util.*

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment
 */
abstract class BaseVmFragment<VM : BaseViewModel> : SimpleImmersionFragment() {
    //Application全局的ViewModel，用于发送全局通知操作
    val mainViewModel: MainViewModel by lazy { getAppViewModel<MainViewModel>() }

    //是否第一次加载
    private var isFirst: Boolean = true

    lateinit var mViewModel: VM

    lateinit var mActivity: AppCompatActivity

    /**
     * 当前Fragment绑定的视图布局
     */
    abstract fun layoutId(): Int

    /**
     * 信息数据
     */
    var mDeviceInformationBean: DeviceInformationBean = DeviceInformationBean()

    /**
     * 后台返回的个人信息数据
     */
    var userInfo = LoginBean()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(layoutId(), container, false)
    }

    override fun initImmersionBar() {
//        ImmersionBar.with(this).keyboardEnable(true).statusBarDarkFont(true, 0.3f).init()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        var year = Calendar.getInstance()
        year.roll(Calendar.YEAR, -18)
        year.timeInMillis
        var birth: Long = year.timeInMillis
        HelpUtil.hideSoftInputView(mActivity)
        userInfo = Hawk.get(Config.database.USER_INFO, LoginBean())
        mDeviceInformationBean = Hawk.get(
            Config.database.PERSONAL_INFORMATION,
            DeviceInformationBean(2, 0, 160, 50f, 0, 0, 0, 0, 0, 0, 10000, birth)
        )
        mViewModel = createViewModel()
        initView(savedInstanceState)
        createObserver()
        onVisible()
        registorDefUIChange()
        initData()
    }

    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {}

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    /**
     * 创建观察者
     */
    abstract fun createObserver()

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            //延迟加载0.12秒加载 避免fragment跳转动画和渲染ui同时进行，出现些微的小卡顿
            view?.postDelayed({
                lazyLoadData()
                //在Fragment中，只有懒加载过了才能开启网络变化监听
                NetworkStateManager.instance.mNetworkStateCallback.observe(
                    viewLifecycleOwner,
                    Observer {
                        //不是首次订阅时调用方法，防止数据第一次监听错误
                        if (!isFirst) {
                            onNetworkStateChanged(it)
                        }
                    })
                isFirst = false
            }, 120)
        }
    }

    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()

    /**
     * 注册 UI 事件
     */
    private fun registorDefUIChange() {
        mViewModel.loadingChange.showDialog.observe(viewLifecycleOwner, Observer {
            showLoading(
                if (it.isEmpty()) {
                    "请求网络中..."
                } else it
            )
        })
        mViewModel.loadingChange.dismissDialog.observe(viewLifecycleOwner, Observer {
            dismissLoading()
        })
    }

    /**
     * 将非该Fragment绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel) {
        viewModels.forEach { viewModel ->
            //显示弹窗
            viewModel.loadingChange.showDialog.observe(viewLifecycleOwner, Observer {
                showLoading(it)
            })
            //关闭弹窗
            viewModel.loadingChange.dismissDialog.observe(viewLifecycleOwner, Observer {
                dismissLoading()
            })
        }
    }

    private lateinit var dialog: Dialog
    protected fun showWaitDialog(msg: String) {
        dialog = LoadingDialogUtils.createLoadingDialog(activity, msg)
    }

    protected fun showWaitDialog() {
        dialog = LoadingDialogUtils.createLoadingDialog(activity, "扫描蓝牙中...")
    }

    protected fun hideWaitDialog() {

        LoadingDialogUtils.closeDialog(dialog)
    }

    interface MapPermissionBack {
        operator fun next()
    }

    fun setMapPermissions(activity: Activity, callback: MapPermissionBack) {
        XXPermissions.with(this)
            .permission(Manifest.permission.ACTIVITY_RECOGNITION)
            .permission(Manifest.permission.ACCESS_FINE_LOCATION) //定位权限
            .permission(Manifest.permission.ACCESS_COARSE_LOCATION)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        callback.next()
                    }
//                    else {
//                        showToastLong("请允许获取健身运动信息，不然不能为你计步哦~")
//                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    if (never) {
                        showToastLong("被永久拒绝授权，请手动授予定位权限以及运动权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(activity, permissions)
                    }
                }
            })
    }

    interface CallBack {
        operator fun next()
    }

    fun getInformationPermissions(activity: Activity, callback: CallBack) {
        XXPermissions.with(this)
            .permission(
                arrayOf(
//                    Manifest.permission.SEND_SMS, //短信权限
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
//                    Manifest.permission.BROADCAST_SMS,
//                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_CONTACTS,//获取通讯录权限
                    Manifest.permission.ANSWER_PHONE_CALLS, //9.0来电号码和挂电话需要正式申请权限
                    Manifest.permission.READ_PHONE_STATE, //读取手机状态
                    Manifest.permission.READ_CALL_LOG
                )
            )
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        callback.next()
                    } else {
                        ShowToast.showToastLong("请允许获取短信权限与通讯权限,否则无法使用手环通讯功能")
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    if (never) {
                        ShowToast.showToastLong("被永久拒绝授权，请手动授予权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(activity, permissions)
                    }
                }
            })
    }


}
