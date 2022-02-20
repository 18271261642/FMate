package com.example.xingliansdk.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.TestViewApi
import com.example.xingliansdk.bean.FlashBean
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.DetailDialViewApi
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.meView.MeDialImgBean
import com.example.xingliansdk.ui.dial.DialMarketActivity
import com.example.xingliansdk.ui.setting.flash.FlashCall
import com.example.xingliansdk.ui.setting.flash.FlashWriteAssignInterface
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.DialProgressBar
import com.example.xingliansdk.view.DownloadProgressButton
import com.google.gson.Gson
import com.shon.bluetooth.Constants
import com.shon.bluetooth.DataDispatcher
import com.shon.connector.BleWrite
import com.shon.connector.bean.DialCustomBean
import com.shon.connector.utils.TLog
import com.shon.net.DownLoadRequest
import com.shon.net.callback.DownLoadCallback
import kotlinx.android.synthetic.main.activity_dial_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.HashMap

class MeDialImgAdapter(data: MutableList<RecommendDialBean.ListDTO.TypeListDTO>, type: Int) :
    BaseQuickAdapter<RecommendDialBean.ListDTO.TypeListDTO, BaseViewHolder>(
        R.layout.item_dial_img,
        data
    )  {
    var type = 0

    init {
        addChildClickViewIds(R.id.tvInstall,R.id.imgDial)
        this.type = type
    }
    var urlFile=""
    override fun convert(
        helper: BaseViewHolder,
        item: RecommendDialBean.ListDTO.TypeListDTO?,
        payloads: List<Any>
    ) {
        super.convert(helper, item, payloads)
        if (item == null) {
            return
        }
        val itemDownload= helper.getView<DownloadProgressButton>(R.id.itemDownload)
//        TLog.error("payloads=="+Gson().toJson(payloads))
        var bean =payloads[0] as RecommendDialBean.ListDTO.TypeListDTO
   //     TLog.error("bean=="+Gson().toJson(bean))
        if(bean!=null&&!TextUtils.isEmpty(bean.progress) && bean.progress.toInt()> 0)
        {
            itemDownload.progress = bean.progress.toFloat()
        }
    }

    override fun convert(helper: BaseViewHolder, item: RecommendDialBean.ListDTO.TypeListDTO?) {
        if (item == null) {
            return
        }
        val img = helper.getView<ImageView>(R.id.imgDial)
        val tvInstall = helper.getView<TextView>(R.id.tvInstall)
        val tvName = helper.getView<TextView>(R.id.tvName)
        val itemDownload= helper.getView<DownloadProgressButton>(R.id.itemDownload)
        when (type) {
            1 -> {
                tvName.visibility = View.GONE
                tvInstall.visibility = View.GONE
                itemDownload.visibility=View.GONE
            }
            2 -> {
                tvName.visibility = View.GONE
                tvInstall.visibility = View.VISIBLE
                itemDownload.visibility=View.GONE
            }
            else -> {
                tvName.visibility = View.VISIBLE
                tvInstall.visibility = View.GONE
                itemDownload.visibility=View.VISIBLE
            }
        }

//        TLog.error("=="+item.state+" stateCode==="+item.stateCode)
//        if(!processStatus) {
            tvInstall.text=item.state
            itemDownload?.currentText=item.state
            tvName.text = item.name
            if (item.isCurrent) {
                //   TLog.error("当前 stateCode==="+item.stateCode)
                tvInstall.setTextColor(context.resources.getColor(R.color.color_text_de))
                tvInstall.setBackgroundResource(R.drawable.bg_dial_gray)
                itemDownload?.textColor = (context.resources.getColor(R.color.color_text_de))
                itemDownload?.setProgressBackgroundColor(context.resources.getColor(R.color.color_text_de))
            } else {
                tvInstall.setTextColor(context.resources.getColor(R.color.color_main_green))
                tvInstall.setBackgroundResource(R.drawable.bg_dial_green)
                itemDownload?.textColor = (context.resources.getColor(R.color.color_main_green))
                itemDownload?.setProgressBackgroundColor(context.resources.getColor(R.color.color_main_green))
            }
            ImgUtil.loadMeImgDialCircle(img, item.image)
//        }
        if(!TextUtils.isEmpty(item.progress) && item.progress.toInt()> 0) {
            itemDownload.progress = item.progress.toFloat()
//            TLog.error("progress  = ${item.progress.toFloat()}")
        }
        itemDownload?.setOnClickListener {
            ThreadUtils.runOnUiThread {
            if(item.stateCode==1) {
                return@runOnUiThread
            }
                TLog.error("点击了++"+DataDispatcher.callDequeStatus)
            if(!DataDispatcher.callDequeStatus) {
                TLog.error("进来了=====")
                ShowToast.showToastLong("数据同步中，请稍后再试")
                return@runOnUiThread
            }
            val file = File(ExcelUtil.dialPath)
            if (!file.exists()) {
                file.mkdirs()
            }
                TLog.error("urlFile+="+urlFile)
            if(urlFile == item.fileName)
                return@runOnUiThread
                Constants.isDialSync = true

                urlFile = item.fileName
                DownLoadRequest(item.ota).startDownLoad(
                "${ExcelUtil.dialPath}/${item.fileName}", object : DownLoadCallback {
                    var fileName: String? = null
                    override fun onDownLoadStart(fileName: String?) {
                        if(!item.isCurrent && !item.state.equals("安装")){
                            itemDownload?.currentText = "下载中..."
                        }

                        this.fileName = fileName
//                        TLog.error("开始")

                    }

                    override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {
//                        TLog.error("继续"+progress)
                    }

                    override fun onDownLoadSuccess() {
                        itemDownload?.currentText = "" +
                                "" +
                                "更新中..."
                        TLog.error("完成")

                        DialMarketActivity.downStatus =true
                        BleWrite.writeDialWriteAssignCall(
                            item?.let { it ->
                                DialCustomBean(
                                    2,
                                    it.dialId,
                                    it.binSize,
                                    it.name)
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
                                    BleWrite.writeFlashErasureAssignCall(
                                        16777215, 16777215
                                    ) { key ->
                                        if (key == 2) {
                                            TLog.error("开始擦写")
                                            FlashCall().writeFlashCall(startByte,
                                                startByte,
                                                keyData , Config.eventBus.DIAL_IMG_RECOMMEND_INDEX,helper.adapterPosition,item?.dialId
                                            )
                                        } else
                                            ShowToast.showToastLong("不支持擦写FLASH数据")
                                    }
                                }
                                3 -> {
                                 //   ShowToast.showToastLong("设备已经有存储这个表盘")
                                //    SNEventBus.sendEvent(Config.eventBus.DIAL_IMG_RECOMMEND_INDEX, FlashBean(1, 1,helper.adapterPosition,item?.dialId))
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
                        itemDownload?.currentText = "下载失败"
                        DialMarketActivity.downStatus = false
                    }
                })
            }
        }
    }


}