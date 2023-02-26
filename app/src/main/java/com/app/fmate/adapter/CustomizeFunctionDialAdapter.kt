package com.app.fmate.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.dialBean.CustomizeFunctionBean

class CustomizeFunctionDialAdapter(data: MutableList<CustomizeFunctionBean>) :
    BaseQuickAdapter<CustomizeFunctionBean, BaseViewHolder>(R.layout.item_dial_text_type, data) {

    override fun convert(helper: BaseViewHolder, item: CustomizeFunctionBean) {
        if (item == null) {
            return
        }
        val tvName = helper.getView<TextView>(R.id.tvName)
        tvName.text=item.name
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