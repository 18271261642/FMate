package com.app.fmate.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.PopularScienceBean
import com.app.fmate.utils.ImgUtil

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