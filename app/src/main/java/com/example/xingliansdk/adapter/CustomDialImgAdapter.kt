package com.example.xingliansdk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.room.CustomizeDialBean
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.meView.MeDialImgBean
import com.example.xingliansdk.ui.setting.flash.FlashCall
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.DialProgressBar
import com.example.xingliansdk.view.DownloadProgressButton
import com.shon.bluetooth.DataDispatcher
import com.shon.connector.BleWrite
import com.shon.connector.bean.DialCustomBean
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_customize_dial.*
import java.text.DecimalFormat
import java.util.HashMap

/**
 * 自定表盘adapter
 */
class CustomDialImgAdapter(data: MutableList<CustomizeDialBean>) :
    BaseQuickAdapter<CustomizeDialBean, BaseViewHolder>(
        R.layout.item_dial_img,
        data
    ) {


    val decimalFormat = DecimalFormat("#.#")
    var itemDownload : DownloadProgressButton ? = null

    var holder: BaseViewHolder? = null
    override fun convert(helper: BaseViewHolder, item: CustomizeDialBean?) {
        if (item == null) {
            return
        }
        TLog.error("自定义表盘")
        holder = helper
        val img = helper.getView<ImageView>(R.id.imgDial)
        val tvInstall = helper.getView<TextView>(R.id.tvInstall)
        itemDownload= helper.getView<DownloadProgressButton>(R.id.itemDownload)
        val tvName = helper.getView<TextView>(R.id.tvName)
        tvInstall.text = "安装"
       // itemDownload?.currentText="安装"

        TLog.error("--------自定义表盘item="+item.getyAxis())
        if(item.getyAxis() != null){
            tvInstall.text =  decimalFormat.format(item.getyAxis().toFloat())+"%"
        }else{
//            itemDownload?.textColor = (context.resources.getColor(R.color.color_main_green))
//            itemDownload?.setProgressBackgroundColor(context.resources.getColor(R.color.color_main_green))
            //itemDownload?.setProgressBackgroundColor(context.resources.getColor(R.color.color_text_de))
            tvInstall.text = "安装"

        }
//        itemDownload?.currentText= item.getyAxis()
//
//        itemDownload?.progress = item.getyAxis().toFloat()

        tvName.visibility = View.GONE
        tvInstall.visibility = View.VISIBLE
        itemDownload!!.visibility=View.GONE
        val imgDelete = holder?.getView<ImageView>(R.id.imgDelete)
        if (item.getxAxis().isNullOrEmpty()||item.getxAxis()!="1")
            imgDelete?.visibility = View.GONE
        else
            imgDelete?.visibility = View.VISIBLE

        if (item.value.isNullOrEmpty()) {
            if (item.imgPath.isNullOrEmpty())
                return
            ImgUtil.loadMeImgDialCircle(img, item.imgPath)
        } else {
            ImgUtil.loadMeImgDialCircle(img, item.value)
        }

        /**
         *  long uiFeature;
        long  binSize;
        int  color;
        int function;
        int location;
        int type;
        String name;
         */

    }


    fun setDownloadProgress(progress : Float){
        if(itemDownload != null){
            itemDownload?.progress   = progress
        }

    }

}