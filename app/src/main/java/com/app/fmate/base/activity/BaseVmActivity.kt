package com.app.fmate.base.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.DeviceFirmwareBean
import com.app.fmate.dialog.MeasureBpPromptDialog
import com.app.fmate.dialog.OnCommDialogClickListener
import com.app.fmate.ext.getAppViewModel
import com.app.fmate.ext.getVmClazz
import com.app.fmate.network.api.homeView.HomeCardVoBean
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.network.manager.NetState
import com.app.fmate.network.manager.NetworkStateManager
import com.app.fmate.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.app.fmate.view.DateUtil
import com.app.fmate.viewmodel.MainViewModel
import com.app.fmate.widget.LoadingDialogUtils
import com.gyf.barlibrary.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.connector.bean.DeviceInformationBean
import com.shon.connector.utils.TLog
import java.util.*

/**
 * 描述　: ViewModelActivity基类，把ViewModel注入进来了
 */
abstract class BaseVmActivity<VM : BaseViewModel> : AppCompatActivity() {

    /**
     * 是否需要使用DataBinding 供子类BaseVmDbActivity修改，用户请慎动
     */
    private var isUserDb = false

    /**
     * 数据存储个人数据
     */
    var mDeviceInformationBean: DeviceInformationBean = DeviceInformationBean()

    //固件信息
    var mDeviceFirmwareBean = DeviceFirmwareBean()

    /**
     * 后台返回的个人信息数据
     */
    var userInfo = LoginBean()

    /**
     * 首页展示效果存储
     */
    var mHomeCardBean: HomeCardVoBean = HomeCardVoBean()
    lateinit var mViewModel: VM
    lateinit var baseDialog: Dialog

    //Application全局的ViewModel，用于发送全局通知操作
    val mainViewModel: MainViewModel by lazy { getAppViewModel<MainViewModel>() }
//    protected var tfLight: Typeface? = null

    abstract fun layoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        //初始化沉浸式
        if (isImmersionBarEnabled()) {
            initImmersionBar()
        }
        HelpUtil.hideSoftInputView(this)
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar())//更新为最新时间
//        TLog.error("走了==XingLianApplication")
        var year = Calendar.getInstance()
        year.roll(Calendar.YEAR, -18)
        year.timeInMillis
        var birth: Long = year.timeInMillis
        userInfo = Hawk.get(Config.database.USER_INFO, LoginBean())
        mDeviceInformationBean = Hawk.get(
            Config.database.PERSONAL_INFORMATION,
            DeviceInformationBean(2, 0, 160, 50f, 0, 0, 0, 0, 0, 0, 10000, birth)
        )
//        TLog.error("走了==mDeviceInformationBean")
        mDeviceFirmwareBean = Hawk.get("DeviceFirmwareBean", DeviceFirmwareBean())
        mHomeCardBean = Hawk.get(Config.database.HOME_CARD_BEAN, HomeCardVoBean())
//        tfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")
//        ImmersionBar.with(this).init()
//        TLog.error("走了==")
        if (!isUserDb) {
            setContentView(layoutId())
        } else {
            initDataBind()
        }
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        mViewModel = createViewModel()
        registerUiChange()
        initView(savedInstanceState)
        createObserver()
//        TLog.error("=====来了网络监听")
        NetworkStateManager.instance.mNetworkStateCallback.observe(this, Observer {
            TLog.error("来了网络监听")
            onNetworkStateChanged(it)
        })
    }


    //注册接受血压测量或状态的广播
    private fun registerBpReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(com.shon.connector.Config.DEVICE_AUTO_MEASURE_BP_ACTION)
        registerReceiver(broadcastReceiver,intentFilter)
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
     * 创建LiveData数据观察者
     */
    abstract fun createObserver()

    /**
     * 注册UI 事件
     */
    private fun registerUiChange() {
        //显示弹窗
        mViewModel.loadingChange.showDialog.observe(this, Observer {
            showLoading(it)
        })
        //关闭弹窗
        mViewModel.loadingChange.dismissDialog.observe(this, Observer {
            dismissLoading()
        })
    }

    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel) {
        viewModels.forEach { viewModel ->
            //显示弹窗
            viewModel.loadingChange.showDialog.observe(this, Observer {
                showLoading(it)
            })
            //关闭弹窗
            viewModel.loadingChange.dismissDialog.observe(this, Observer {
                dismissLoading()
            })
        }
    }

    fun userDataBinding(isUserDb: Boolean) {
        this.isUserDb = isUserDb
    }

    /**
     * 供子类BaseVmDbActivity 初始化Databinding操作
     */
    open fun initDataBind() {}
    fun Permissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                XXPermissions.with(this)
                    .permission(
                        arrayOf(
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    //                        Manifest.permission.MEDIA_CONTENT_CONTROL,
    //                        Manifest.permission.READ_EXTERNAL_STORAGE,
    //                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    //                        Manifest.permission.BLUETOOTH_PRIVILEGED,
                            Manifest.permission.ACCESS_FINE_LOCATION, //定位权限
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            if (!all) {
                                ShowToast.showToastLong("定位权限未开启,请打开设置开启权限")
                            }
                        }
                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {

                            if (never) {
                                ShowToast.showToastLong("被永久拒绝授权，请手动授予权限")
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                showPermissionSettingsDialog()
                            }
                        }
                    })
            }
        }
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

    /**
     *
     */
    open fun showPermissionSettingsDialog() {
      newGenjiDialog {
                layoutId = R.layout.alert_dialog_login
                dimAmount = 0.3f
                isFullHorizontal = true
                isFullVerticalOverStatusBar = false
                gravity = DialogGravity.CENTER_CENTER
                animStyle = R.style.BottomTransAlphaADAnimation
                convertListenerFun { holder, dialog ->
                    var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                    var confirm = holder.getView<TextView>(R.id.dialog_confirm)
                    var dialogContent = holder.getView<TextView>(R.id.dialog_content)
                    dialogContent?.text=getString(R.string.lack_of_necessary_permissions)
                    confirm?.setOnClickListener {
                        jump2PermissionSettings()
                    dialog.dismiss()
                    }
                    dialogCancel?.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }.showOnWindow(supportFragmentManager)

    }

    /**
     * 跳转到应用程序信息详情页面
     */
    open fun jump2PermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    open fun getFooterView(
        type: Int,
        mRecyclerView: RecyclerView
    ): View? {
        return layoutInflater.inflate(R.layout.no_more, mRecyclerView, false)
    }

    protected fun showWaitDialog(msg: String) {
//        if (dialog.isShowing)
//            return
        try {
            baseDialog = LoadingDialogUtils.createLoadingDialog(this, msg)
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    protected fun showWaitDialog() {
//        if (dialog.isShowing)
//            return
        baseDialog = LoadingDialogUtils.createLoadingDialog(this, "扫描蓝牙中...")
    }

    protected fun hideWaitDialog() {
        if (::baseDialog.isInitialized) {
            LoadingDialogUtils.closeDialog(baseDialog)
        }


    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    private fun isImmersionBarEnabled(): Boolean {
        return true
    }

    protected open fun initImmersionBar() {
        //在BaseActivity里初始化
        ImmersionBar.with(this).navigationBarColor(R.color.white)
            .statusBarDarkFont(true, 0.3f)
            .fitsSystemWindows(false)
            .init()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isImmersionBarEnabled()) {
            ImmersionBar.with(this).destroy()
        }
      //  unregisterReceiver(broadcastReceiver)
    }

    /**
     * 界面已销毁
     * @return
     */
    open fun isFinished(): Boolean {
        return isDestroyed || isFinishing
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (checkDoubleClick()) {
                return true
            }

            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v!!.windowToken, 0)
                }
            }
        } // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)

        return super.dispatchTouchEvent(ev)
    }


    /** 判断是否是快速点击  */
    private var lastClickTime: Long = 0
    fun checkDoubleClick(): Boolean {
        //点击时间
        val clickTime = SystemClock.uptimeMillis()
        //如果当前点击间隔小于500毫秒
        if (lastClickTime >= clickTime - 300) {
            return true
        }
        //记录上次点击时间
        lastClickTime = clickTime
        return false
    }



    open fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }


    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
           var action = p1?.action
            TLog.error("-------baseActivity="+action)
            if(action.equals(com.shon.connector.Config.DEVICE_AUTO_MEASURE_BP_ACTION)){
                showParentDialog()
            }
        }

    }
    private var measureBpPromptDialog : MeasureBpPromptDialog ?= null

    private fun showParentDialog(){
        if(measureBpPromptDialog != null && measureBpPromptDialog!!.isShowing){
            measureBpPromptDialog!!.dismiss()
        }
        measureBpPromptDialog = MeasureBpPromptDialog(
            this,
            R.style.edit_AlertDialog_style)
        measureBpPromptDialog!!.show()
        measureBpPromptDialog!!.setOnCommDialogClickListener(object :
            OnCommDialogClickListener {
            override fun onConfirmClick(code: Int) {

            }

            override fun onCancelClick(code: Int) {

            }

        })
    }
}