package com.example.xingliansdk.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.dialBean.CustomizePlacementBean

class CustomizePlacementDialAdapter(data: MutableList<CustomizePlacementBean>) :
    BaseQuickAdapter<CustomizePlacementBean, BaseViewHolder>(R.layout.item_dial_text_type, data) {

    override fun convert(helper: BaseViewHolder, item: CustomizePlacementBean?) {
        if (item == null) {
            return
        }
        val tvName = helper.getView<TextView>(R.id.tvName)
        tvName.text = item.name
        if (item.ismSelected()) {
            tvName.setBackgroundResource(R.drawable.bg_dial_gray)
            tvName.setTextColor(context.resources.getColor(R.color.bottom_nav_icon_dim))
        }
        else {
//            tvName.setBackgroundResource(R.drawable.bg_dial_gray)
//            tvName.setTextColor(context.resources.getColor(R.color.bottom_nav_icon_dim))

            tvName.setBackgroundResource(R.drawable.bg_dial_green)
            tvName.setTextColor(context.resources.getColor(R.color.color_main_green))
        }
    }

}