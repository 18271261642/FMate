package com.app.fmate.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.utils.ImgUtil
import com.app.fmate.view.DownloadProgressButton

class MeImgAdapter(data: MutableList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(
        R.layout.item_dial_img,
        data
    ) {
    var type = 0

    init {
        addChildClickViewIds(R.id.tvInstall)
        this.type = type
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        if (item == null) {
            return
        }
        val img = helper.getView<ImageView>(R.id.imgDial)
        val tvInstall = helper.getView<TextView>(R.id.tvInstall)
        val tvName = helper.getView<TextView>(R.id.tvName)
        var itemDownload: DownloadProgressButton = helper.getView(R.id.itemDownload)
        tvName.visibility = View.GONE
        tvInstall.visibility = View.GONE
        itemDownload.visibility = View.GONE
        if (item.isNullOrEmpty()) {
            img.setImageResource(R.mipmap.img_none)
         //   ImgUtil.loadMeImgDialCircle(img, notNetworkList[helper.adapterPosition])
        }
        else
            ImgUtil.loadMeImgDialCircle(img, item)
    }

}