package com.example.xingliansdk.ui.setting.flash

import android.os.Bundle
import android.view.KeyEvent
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.FlashBean
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.UIUpdate.UIUpdateBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.ui.setting.MyDeviceActivity
import com.example.xingliansdk.ui.setting.vewmodel.FlashViewModel
import com.example.xingliansdk.utils.FileUtils
import com.shon.connector.utils.ShowToast
import com.example.xingliansdk.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.DataDispatcher
import com.shon.connector.BleWrite
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.TLog
import com.shon.net.callback.DownLoadCallback
import kotlinx.android.synthetic.main.activity_flash.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 更新flash
 */
class FlashActivity : BaseActivity<FlashViewModel>(),
    BleWrite.FlashErasureAssignInterface, DownLoadCallback {
    lateinit var bean: UIUpdateBean
    private var fileName: String? = null
    var keyData = byteArrayOf()

    override fun layoutId() = R.layout.activity_flash
    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        SNEventBus.register(this)
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
                MyDeviceActivity.FlashBean.UIFlash = true
            }
            override fun onActionImageClick() {
            }

            override fun onActionClick() {
            }
        })
        BleWrite.writeFlashErasureAssignCall {
            var uuid = it
            TLog.error(" uuid.toString()==${uuid.toString()}")
            TLog.error(" uuid.toString()==${mDeviceFirmwareBean.productNumber}")
            mViewModel.findUpdate(mDeviceFirmwareBean.productNumber, uuid.toString())
            // mViewModel.findUpdate(""+8002,""+251658241)
        }
        MyDeviceActivity.FlashBean.UIFlash = false
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            bean = it
            if (bean.ota.isNullOrEmpty()) {
                ShowToast.showToastLong("已是最新版本")
                return@observe
            }
            startByte = HexDump.toByteArray(it.startPosition.toLong())
            endByte = HexDump.toByteArray(it.endPosition.toLong())

            BleWrite.writeFlashErasureAssignCall(it.startPosition, it.endPosition, this)
        }
        mViewModel.msg.observe(this) {
            ShowToast.showToastLong(it)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.FLASH_UPDATE->{
                var data:FlashBean= event.data as FlashBean
                var currentProgress=((data.currentProgress.toDouble()/data.maxProgress)*100).toInt()
                tvCurrentProgress.text="当前进度:${currentProgress}   ${data.currentProgress}/${data.maxProgress}"
                proBar.max = data.maxProgress
                proBar.progress = data.currentProgress
                if (data.maxProgress == 1 && data.currentProgress == 1) {
                    finish()
                    DataDispatcher.callDequeStatus=true
                }

            }
        }
    }

    override fun onResultErasure(key: Int) {
        if (key == 2) {
            if (bean != null && bean.ota.isNotEmpty()) {
                // mViewModel.downLoadBin(bean, this)

                var mac = Hawk.get("address", "")

                val mLoginBean = Hawk.get<LoginBean>(Config.database.USER_INFO)

                if (mLoginBean != null && mLoginBean.token != null) {
                    var token = mLoginBean.token as String
                    mViewModel.downLoadBin(bean, mac, token, this)
                }

            }

        } else
            ShowToast.showToastLong("不支持擦写FLASH数据")
    }

    val mList = ArrayList<ByteArray>() //组装的 list  现在装了所有数据暂时没做任何操作
    var startByte = ByteArray(4)  //开始位置
    var endByte = ByteArray(4)      //结束位置

    override fun onDownLoadStart(fileName: String?) {
        this.fileName = fileName
        showWaitDialog("正在下载bin文件...")
    }

    override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {
    }

    var length = 0
    override fun onDownLoadSuccess() {
        hideWaitDialog()
        keyData = FileUtils.inputStream2ByteArray(fileName)
        length = keyData.size
        TLog.error("length++" + length)
        TLog.error("长度===" + FileUtils.inputStream2ByteArray(fileName).size)
        FlashCall().writeFlashCall(startByte, endByte, keyData,   Config.eventBus.FLASH_UPDATE,-1,-100)
    }

    override fun onDownLoadError() {
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            finish()
            MyDeviceActivity.FlashBean.UIFlash = true
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

}