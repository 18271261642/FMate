package com.app.fmate.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.dialView.DetailDialViewApi
import com.app.fmate.network.api.dialView.DownDialModel
import com.app.fmate.ui.setting.flash.FlashCall
import com.app.fmate.utils.ExcelUtil
import com.app.fmate.utils.FileUtils
import com.app.fmate.utils.ImgUtil
import com.shon.connector.utils.ShowToast
import com.app.fmate.view.DownloadProgressButton
import com.shon.bluetooth.DataDispatcher
import com.shon.connector.BleWrite
import com.shon.connector.bean.DialCustomBean
import com.shon.connector.utils.TLog
import com.shon.net.DownLoadRequest
import com.shon.net.callback.DownLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.HashMap

class DownloadDialImgAdapter(data: MutableList<DownDialModel.ListDTO>) :
    BaseQuickAdapter<DownDialModel.ListDTO, BaseViewHolder>(
        R.layout.item_dial_img,
        data
    ) {

    init {
        addChildClickViewIds(R.id.tvInstall)
    }

    override fun convert(
        helper: BaseViewHolder,
        item: DownDialModel.ListDTO,
        payloads: List<Any>
    ) {
        super.convert(helper, item, payloads)
        if (item == null) {
            return
        }
      //  TLog.error("payloads走的这个=="+item.state)
        val itemDownload=helper.getView<DownloadProgressButton>(R.id.itemDownload)
        val tvInstall = helper.getView<TextView>(R.id.tvInstall)
        tvInstall.visibility=View.GONE
        val bean =payloads[0] as DownDialModel.ListDTO
        if(!TextUtils.isEmpty(bean.progress) && bean.progress.toInt()> 0)
        {
            itemDownload.progress = bean.progress.toFloat()
        }
    }
    override fun convert(helper: BaseViewHolder, item: DownDialModel.ListDTO) {
        if (item == null) {
            return
        }
        val img = helper.getView<ImageView>(R.id.imgDial)
        val imgDelete = helper.getView<ImageView>(R.id.imgDelete)
        val tvInstall = helper.getView<TextView>(R.id.tvInstall)
        val tvName = helper.getView<TextView>(R.id.tvName)
        val itemDownload=helper.getView<DownloadProgressButton>(R.id.itemDownload)
        tvName.visibility = View.GONE
        itemDownload.visibility=View.VISIBLE
        tvInstall.visibility=View.GONE

        if(item.delete.isNotEmpty()&&item.delete.equals("1"))
        {
            imgDelete.visibility=View.VISIBLE
        }
        else
            imgDelete.visibility=View.GONE
        TLog.error("走的这个=="+item.state)

        if(item.isCurrent)
        {
            itemDownload.textColor =(context.resources.getColor(R.color.color_text_de))
            itemDownload.setProgressBackgroundColor(context.resources.getColor(R.color.color_text_de))
        }
        else
        {
            itemDownload.textColor = context.resources.getColor(R.color.color_main_green)
            itemDownload.setProgressBackgroundColor(context.resources.getColor(R.color.color_main_green))
        }
        itemDownload.currentText=item.state
        ImgUtil.loadMeImgDialCircle(img, item.image)

        if(!TextUtils.isEmpty(item.progress) && item.progress.toInt()> 0) {
            itemDownload.progress = item.progress.toFloat()
//            TLog.error("progress  = ${item.progress.toFloat()}")
        }
        itemDownload.setOnClickListener {
            //此处做点击事件绘制进度条时,直接进度条直接给到了最后一位
            if(item.stateCode==1) {
                item.stateCode=1
                return@setOnClickListener
            }
            if(!DataDispatcher.callDequeStatus) {
                ShowToast.showToastLong("已有表盘在安装,或数据在同步中")
                return@setOnClickListener
            }
            val file = File(ExcelUtil.dialPath)
            if (!file.exists()) {
                file.mkdirs()
            }

            DownLoadRequest(item.ota).startDownLoad(
                "${ExcelUtil.dialPath}/${item.fileName}", object : DownLoadCallback {
                    var fileName: String? = null
                    override fun onDownLoadStart(fileName: String?) {
                        this.fileName = fileName
                    }

                    override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {
                    }

                    override fun onDownLoadSuccess() {
                        itemDownload.currentText = "更新中..."

//                        Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,item.dialId)
//                        Hawk.put(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,item.dialId)
//
//                        Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,Gson().toJson(item))

                        BleWrite.writeDialWriteAssignCall(
                            item.let {
                                DialCustomBean(
                                    2,
                                    it.dialId,
                                    it.binSize,
                                    it.name) }
                        ) {
                            TLog.error("写入","---------写入状态="+it)
                            when (it) {
                                2 -> {
                                    val startByte = byteArrayOf(
                                        0x00, 0xff.toByte(), 0xff.toByte(),
                                        0xff.toByte()
                                    )
                                    val keyData =
                                        FileUtils.inputStream2ByteArray(fileName)
                                    BleWrite.writeFlashErasureAssignCall(
                                        16777215, 16777215
                                    ) { key ->
                                        if (key == 2) {
                                            FlashCall().writeFlashCall(startByte,
                                                startByte,
                                                keyData , Config.eventBus.DIAL_IMG_RECOMMEND_INDEX,helper.adapterPosition,
                                                item.dialId
                                            )
                                        } else
                                            ShowToast.showToastLong("不支持擦写FLASH数据")
                                    }

                                }
                                3 -> {
                                    TLog.error("写入","---------写入fff状态="+it)

                                  //  ShowToast.showToastLong("设备已经有存储这个表盘")
                                    var hasMap = HashMap<String, String>()
                                    hasMap["dialId"] = item?.dialId.toString()
                                    GlobalScope.launch(Dispatchers.IO)
                                    {
                                        kotlin.runCatching {
                                            DetailDialViewApi.mDetailDialViewApi.updateUserDial(hasMap)
                                        }.onSuccess {
                                            SNEventBus.sendEvent(Config.eventBus.DIAL_RECOMMEND_DIAL,helper.adapterPosition)
                                        }.onFailure {}
                                    }
                                }
                            }
                        }
                    }

                    override fun onDownLoadError() {
                        itemDownload.currentText = "下载失败"
                    }
                })
        }
    }

}