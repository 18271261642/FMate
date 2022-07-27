package com.example.xingliansdk.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.bean.room.CustomizeDialBean
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.meView.MeDialImgBean
import com.example.xingliansdk.utils.ImgUtil
import com.example.xingliansdk.view.DialProgressBar

class PopularScienceAdapter(data: MutableList<PopularScienceBean.ListDTO>) :
    BaseQuickAdapter<PopularScienceBean.ListDTO, BaseViewHolder>(
        R.layout.item_popular_science, data
    ) {
    override fun convert(helper: BaseViewHolder, item: PopularScienceBean.ListDTO) {
        if (item == null) {
            return
        }
        val img = helper.getView<ImageView>(R.id.imgContent)
        val tvContent = helper.getView<TextView>(R.id.tvContent)
        ImgUtil.loadRound(img, item.image)
        tvContent.text = item.title
    }


}