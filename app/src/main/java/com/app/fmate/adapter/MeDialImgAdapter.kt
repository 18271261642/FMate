package com.app.fmate.adapter

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.dialView.DetailDialViewApi
import com.app.fmate.network.api.dialView.RecommendDialBean
import com.app.fmate.ui.dial.DialMarketActivity
import com.app.fmate.ui.setting.flash.FlashCall
import com.app.fmate.utils.*
import com.app.fmate.view.DownloadProgressButton
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.bluetooth.Constants
import com.shon.bluetooth.DataDispatcher
import com.shon.connector.BleWrite
import com.shon.connector.bean.DialCustomBean
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.shon.net.DownLoadRequest
import com.shon.net.callback.DownLoadCallback
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
        item: RecommendDialBean.ListDTO.TypeListDTO,
        payloads: List<Any>
    ) {
        if (item != null) {
            super.convert(helper, item, payloads)
        }
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

    private fun changeLanguage(str : String) : String{
        if(str == "??????")
            return context.resources.getString(R.string.string_dial_last)
        if(str == "??????")
            return context.resources.getString(R.string.string_dial_install)
        if(str == "????????????")
            return context.resources.getString(R.string.string_dial_current)
        if(str == "??????")
            return context.resources.getString(R.string.string_dial_download)
        if(str == "??????")
            return context.resources.getString(R.string.string_select)
        return str
    }

    override fun convert(helper: BaseViewHolder, item: RecommendDialBean.ListDTO.TypeListDTO) {
        if (item == null) {
            return
        }

        //?????????????????????
        val marketDialId = Hawk.get(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,-1);

        //Log.e("MeDialImgAdapter","-----?????????????????????="+marketDialId)
        if(marketDialId != -1 && marketDialId == item.dialId.toInt()){
            //Log.e("MeDialImgAdapter","-----?????????????????????="+Gson().toJson(item))
            Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,Gson().toJson(item));
        }

        //????????????????????????????????????????????????????????????????????????
        var currDialId = Hawk.get(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,-1)

        //Log.e("11","------???????????????????????????ID="+currDialId +"---item???Id="+item.dialId)

        if((currDialId != -1 && currDialId == item.dialId) || (currDialId == 65533 && item.dialId == 0)){
            item.state = context.resources.getString(R.string.string_dial_current)
            item.isCurrent = true
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
            tvInstall.text=changeLanguage(item.state)

            itemDownload?.currentText=changeLanguage(item.state)
            tvName.text = item.name
            if (item.isCurrent) {
                //   TLog.error("?????? stateCode==="+item.stateCode)
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

            if(BleConnection.iFonConnectError){
                ShowToast.showToastLong("????????????????????????????????????!")
                return@runOnUiThread
            }


            if(item.stateCode==1) {
                return@runOnUiThread
            }
                TLog.error("?????????++"+DataDispatcher.callDequeStatus)
            if(!DataDispatcher.callDequeStatus) {
                TLog.error("?????????=====")
                BLEManager.getInstance().dataDispatcher.clear("")
//                ShowToast.showToastLong("?????????????????????????????????")
//                return@runOnUiThread
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
                        if(!item.isCurrent && !item.state.equals("??????")){
                            itemDownload?.currentText = "?????????..."
                        }

                        this.fileName = fileName
//                        TLog.error("??????")

                    }

                    override fun onDownLoading(totalSize: Long, currentSize: Long, progress: Int) {
//                        TLog.error("??????"+progress)
                    }

                    override fun onDownLoadSuccess() {
                        itemDownload?.currentText = "" +
                                "" +
                                "?????????..."
                        TLog.error("??????")

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
                            Log.e("??????????????????", "------type=$it")
                            when (it) {
                                1 -> {
                                    DialMarketActivity.downStatus =false
                                    ShowToast.showToastLong("????????????????????????")
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
                                            TLog.error("????????????")
                                            FlashCall().writeFlashCall(startByte,
                                                startByte,
                                                keyData , Config.eventBus.DIAL_IMG_RECOMMEND_INDEX,helper.adapterPosition,item?.dialId
                                            )
                                        } else{
                                            DialMarketActivity.downStatus =false
                                            ShowToast.showToastLong("???????????????FLASH??????")
                                        }

                                    }
                                }
                                3 -> {


                                    Log.e("?????????----","-----????????????---------")

                                 //   ShowToast.showToastLong("?????????????????????????????????")
                                //    SNEventBus.sendEvent(Config.eventBus.DIAL_IMG_RECOMMEND_INDEX, FlashBean(1, 1,helper.adapterPosition,item?.dialId))
                                        var hasMap = HashMap<String, String>()
                                        hasMap["dialId"] = item?.dialId.toString()
                                        hasMap["stateCode"] = "6"
                                        GlobalScope.launch(Dispatchers.IO)
                                        {
                                            kotlin.runCatching {
                                                Hawk.put(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,item.dialId)

                                                Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,Gson().toJson(item))

                                                Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,item.dialId)

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
                        itemDownload?.currentText = "????????????"
                        DialMarketActivity.downStatus = false
                    }
                })
            }
        }
    }


}