package com.app.fmate.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.room.CustomizeDialBean
import com.app.fmate.utils.*
import com.app.fmate.view.DownloadProgressButton
import com.shon.connector.utils.TLog
import java.text.DecimalFormat

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
    override fun convert(helper: BaseViewHolder, item: CustomizeDialBean) {
        if (item == null) {
            return
        }
        TLog.error("自定义表盘")
        holder = helper
        val img = helper.getView<ImageView>(R.id.imgDial)
        val tvInstall = helper.getView<TextView>(R.id.tvInstall)
        itemDownload= helper.getView<DownloadProgressButton>(R.id.itemDownload)
        val tvName = helper.getView<TextView>(R.id.tvName)
        tvInstall.text = context.resources.getString(R.string.string_dial_install)
       // itemDownload?.currentText="安装"

        TLog.error("--------自定义表盘item="+item.getyAxis())
        if(item.getyAxis() != null){
            tvInstall.text =  decimalFormat.format(item.getyAxis().toFloat())+"%"
        }else{
//            itemDownload?.textColor = (context.resources.getColor(R.color.color_main_green))
//            itemDownload?.setProgressBackgroundColor(context.resources.getColor(R.color.color_main_green))
            //itemDownload?.setProgressBackgroundColor(context.resources.getColor(R.color.color_text_de))
            tvInstall.text = context.resources.getString(R.string.string_dial_install)

        }
//        itemDownload?.currentText= item.getyAxis()
//
//        itemDownload?.progress = item.getyAxis().toFloat()

        tvName.visibility = View.GONE
        tvInstall.visibility = View.VISIBLE
        itemDownload!!.visibility=View.GONE
        val imgDelete = holder?.getView<ImageView>(R.id.imgDelete)
//        if (item.getxAxis().isNullOrEmpty()||item.getxAxis()!="1")
//            imgDelete?.visibility = View.GONE
//        else
//            imgDelete?.visibility = View.VISIBLE

        if (item.value.isNullOrEmpty()) {
            if (item.imgPath.isNullOrEmpty())  ImgUtil.loadMeImgDialCircle(img, R.drawable.icon_cus_dial_bg)

           else ImgUtil.loadMeImgDialCircle(img, item.imgPath)
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