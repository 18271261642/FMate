package com.example.xingliansdk.adapter

import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.solver.state.State
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.dialBean.CustomizeColorBean
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.meView.MeDialImgBean
import com.example.xingliansdk.utils.ImgUtil

class CustomizeColorDialAdapter(data: MutableList<CustomizeColorBean>) :
    BaseQuickAdapter<CustomizeColorBean, BaseViewHolder>(R.layout.item_customize_dial_color, data) {
    var imgList = intArrayOf(
        R.drawable.round_dial_white,
        R.drawable.round_dial_black,
        R.drawable.round_dial_red,
        R.drawable.round_dial_redorange,
        R.drawable.round_dial_orange,
        R.drawable.round_dial_yellow
        , R.drawable.round_dial_cyan_blue
        , R.drawable.round_dial_blue
        , R.drawable.round_dial_purple)
    override fun convert(helper: BaseViewHolder, item: CustomizeColorBean?) {
        if (item == null) {
            return
        }
        val img = helper.getView<ImageView>(R.id.imgDial)
        ImgUtil.loadMeImgDialCircle(img,imgList[helper.adapterPosition])
        if (item.isColorCheck)
            img.setBackgroundResource(R.drawable.round_dial_color)
        else
            img.setBackgroundResource(R.drawable.round_dial_color_false)
    }

}