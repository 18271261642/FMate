package com.example.otalib

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.*
import com.example.otalib.service.DfuService
import no.nordicsemi.android.dfu.DfuProgressListener
import no.nordicsemi.android.dfu.DfuServiceInitiator
import no.nordicsemi.android.dfu.DfuServiceListenerHelper
import java.io.File

/**
 *  初始化
 *  DFU dfu = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(DFU.class)
 *
 *
 */
class DFUViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private var dfuProgress: DfuProgressListener? = null
    fun attachView(lifecycleOwner: LifecycleOwner, dfuProgress: DfuProgressListener) {
        this.dfuProgress = dfuProgress
        DfuServiceListenerHelper.registerProgressListener(getApplication(), this.dfuProgress!!)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        dfuProgress ?: return
        DfuServiceListenerHelper.registerProgressListener(getApplication(), this.dfuProgress!!)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        dfuProgress ?: return
        DfuServiceListenerHelper.unregisterProgressListener(getApplication(), this.dfuProgress)
    }

    fun registerDFUListener() {


    }

    /**
     * 开始 DFU 升级
     */
    fun startDFU(
        address: String,  //设备唯一的 mac 地址
        name: String, //设备名称
        localZipPath: String, //本地ota 压缩包的路径，此处为 .zip 格式
        // 如果是其他的格式 请修改过{ starter.setZip(mFileStreamUri, localZipPath )}
        mContext:Context
    ) {

        if (isDfuServiceRunning(getApplication())) {
            return
        }
        val file = File(localZipPath)
        if (!file.exists()) {
            file.mkdirs()
          //  Toast.makeText(getApplication(),"请在$localZipPath$ 下放入文件夹",Toast.LENGTH_SHORT).show()
          //  ShowToast.showToastLong("请在$localZipPath$ 下放入文件夹")
            return
        }
        val mFileStreamUri = Uri.fromFile(file)


        // Save current state in order to restore it if user quit the Activity
        val starter = DfuServiceInitiator(address)
            .setDeviceName(name)
            .setKeepBond(true)
            .setForceDfu(true)
            .setPacketsReceiptNotificationsEnabled(true)
            .setPacketsReceiptNotificationsValue(12)
            .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
     //   TLog.error("DfuServiceInitiator")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(
                mContext
            )
            starter.setForeground(true)
        }
//        if (mFileType == DfuService.TYPE_AUTO)
//            starter.setZip(mFileStreamUri, localZipPath);
//        else {
//            starter.setBinOrHex(mFileType, mFileStreamUri, mFilePath).setInitFile(mInitFileStreamUri, mInitFilePath)
//        TLog.error("localZipPath==$localZipPath")

        starter.setZip(mFileStreamUri, localZipPath)
        starter.start(getApplication(), DfuService::class.java)

    }


    private fun isDfuServiceRunning(context: Context): Boolean {
        val manager: ActivityManager? =
            context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        manager ?: return false
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (DfuService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

}