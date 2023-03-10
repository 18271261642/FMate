package com.app.fmate.ui.dial

import android.app.AlertDialog
import android.os.Bundle
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.FlashBean
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.dialView.DetailDialViewModel
import com.app.fmate.network.api.dialView.RecommendDialBean
import com.app.fmate.ui.setting.flash.FlashCall
import com.app.fmate.utils.*
import com.app.fmate.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.bluetooth.DataDispatcher
import com.shon.connector.BleWrite
import com.shon.connector.bean.DialCustomBean
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.shon.net.DownLoadRequest
import com.shon.net.callback.DownLoadCallback
import kotlinx.android.synthetic.main.activity_dial_details.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


//表盘预览下载页面
class DialDetailsActivity : BaseActivity<DetailDialViewModel>(), DownLoadCallback  {
    var mTypeList: String? = null
    override fun layoutId() = R.layout.activity_dial_details
    var bean: RecommendDialBean.ListDTO.TypeListDTO? = null
    var position=-1
    var progressStatus=false

    private val instant by lazy { this }

    private var alertDialog : AlertDialog.Builder ?=null

    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()


        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener{
            override fun onBackClick() {
                if(progressStatus){
                    backAlert()
                }else{
                    finish()
                }
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {

            }

        })

        SNEventBus.register(this)
        download.maxProgress=100
        showWaitDialog("页面加载中...")

        position=intent.getIntExtra("position",-1)
        mTypeList = intent.getStringExtra("TypeList")
        bean = Gson().fromJson(mTypeList, RecommendDialBean.ListDTO.TypeListDTO::class.java)
        TLog.error("bean==" + Gson().toJson(bean))
        ImgUtil.loadMeImgDialCircle(imgDial, bean?.image!!)
        if (!bean?.isCharge!!)
            tvType.text = resources.getString(R.string.string_dial_free)
        else
            tvType.text = "¥ ${bean?.price}"
        circleProgressView.setText(bean?.state!!)
        download.currentText=bean?.state!!
        if(bean?.isCurrent!!) {
            circleProgressView.setProgressColorBackground(resources.getColor(R.color.color_view))
            circleProgressView.setProgressColor(resources.getColor(R.color.color_view))
            download.setProgressBackgroundColor(resources.getColor(R.color.color_view))
            download.textColor = resources.getColor(R.color.color_view)
        }
        tvSize.text = AppUtils.getFormatSize(bean?.binSize!!.toDouble())
        tvNumber.text = "${NumUtils.formatBigNum(bean?.downloads!!)}"+resources.getString(R.string.string_install_number)

        tvName.text = bean!!.name

        circleProgressView.setOnClickListener {
            if (bean?.isCurrent!!)
            {
                ShowToast.showToastLong(resources.getString(R.string.string_has_current_dial))
                return@setOnClickListener
            }
            else if(progressStatus)
            {
                ShowToast.showToastLong(resources.getString(R.string.strign_dial_other_later))
                return@setOnClickListener
            }
            progressStatus=true
            val file = File(ExcelUtil.dialPath)
            if (!file.exists()) {
                file.mkdirs()
            }
            DownLoadRequest(bean?.ota).startDownLoad(
                "${ExcelUtil.dialPath}/${bean?.fileName}",this)
        }
        ThreadUtils.runOnUiThread({
            hideWaitDialog()
        },1000)

    }

    private var fileName: String? = null
    override fun onDownLoadStart(fileName: String?) {
        this.fileName = fileName
    }

    override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {
//        TLog.error("totalSize==$totalSize  currentSize==$currentSize   progress==$progress")
        circleProgressView.setProgress(progress.toFloat())
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            TLog.error("返回")
            SNEventBus.sendEvent(Config.eventBus.DIAL_RECOMMEND_DIAL)
            ThreadUtils.runOnUiThread({
                finish()
            }, 500)
        }
    }

    override fun onDownLoadSuccess() {
        circleProgressView.setText(resources.getString(R.string.string_dial_updating))
        BleWrite.writeDialWriteAssignCall(
            bean?.let { it ->
                DialCustomBean(
                    2,
                    it.dialId,
                    it.binSize,
                    it.name
                )
            }

        ) {
            when (it) {
                1 -> {
                    ShowToast.showToastLong("设备存储空间不够")
                }
                2 -> {
                    val startByte = byteArrayOf(
                        0x00, 0xff.toByte(), 0xff.toByte(),
                        0xff.toByte()
                    )
                    var keyData =
                        FileUtils.inputStream2ByteArray(fileName)
                    TLog.error("length++" + keyData.size)
                    //    TLog.error("keyData++"+ByteUtil.getHexString(keyData))
                    BleWrite.writeFlashErasureAssignCall(
                        16777215, 16777215
                    ) { key ->
                        if (key == 2) {
                            TLog.error("开始擦写")
                            //  TLog.error("开始的++"+ByteUtil.getHexString(keyData))
                            FlashCall().writeFlashCall(startByte, startByte, keyData,
                                Config.eventBus.DIAL_IMG_RECOMMEND_INDEX,position, bean?.dialId!!)
                        } else
                            ShowToast.showToastLong("不支持擦写FLASH数据")
                    }

                }
                3 -> {
                   // ShowToast.showToastLong("设备已经有存储这个表盘")
                    //给后台一个 更改表盘的指令
                }
            }
        }
    }

    override fun onDownLoadError() {
        circleProgressView.setText(resources.getString(R.string.string_dial_download_fail))
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.DIAL_IMG_RECOMMEND_INDEX->
            {
                var data = event.data as FlashBean
                progressStatus=true
                if(data.id==bean?.dialId) {
                    TLog.error("data==" + data.toString())
                    var currentProgress =
                        ((data.currentProgress.toDouble() / data.maxProgress) * 100).toInt()
                    TLog.error("currentProgress==" + currentProgress)
                    if (currentProgress <= 15)
                        circleProgressView.setProgress(15f)
                    else
                        circleProgressView.setProgress(currentProgress.toFloat())
                    circleProgressView.setText(resources.getString(R.string.string_current_schedule)+": $currentProgress %")
                    download.progress = currentProgress.toFloat()
                    // proBar.progress = type
                    if (data.currentProgress == 1 && data.maxProgress == 1) {
                        progressStatus=false
                        circleProgressView.setText(resources.getString(R.string.string_complete))
                        download.currentText = resources.getString(R.string.string_complete)
                        var hasMap = HashMap<String, String>()
                        hasMap["dialId"] = bean?.dialId.toString()
                        mViewModel.updateUserDial(hasMap)
                        DataDispatcher.callDequeStatus = true
                    }
                    else if(data.currentProgress == -1 && data.maxProgress == -1)
                    {
                        progressStatus=false
                        circleProgressView.setText(resources.getString(R.string.string_dial_download_fail))
                        download.currentText = resources.getString(R.string.string_dial_fail)
                        finish()
                    }
                }
            }
        }
    }


//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            if(progressStatus){
//                backAlert()
//            }else{
//                finish()
//            }
//        }
//        return true
//    }


    private fun backAlert(){
        alertDialog = AlertDialog.Builder(instant)
        alertDialog!!.setTitle(resources.getString(R.string.string_text_remind))
        alertDialog!!.setMessage(resources.getString(R.string.string_dial_cancel_desc)+"?")
        alertDialog!!.setPositiveButton(resources.getString(R.string.text_sure)
        ) { p0, p1 ->
            p0.dismiss()
            finish()
        }.setNegativeButton(resources.getString(R.string.text_cancel)
        ) { p0, p1 ->
            p0.dismiss()
        }
        alertDialog!!.create().show()
    }
}