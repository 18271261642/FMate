package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.solver.state.State
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.dialBean.CustomizeColorBean
import com.example.xingliansdk.bean.dialBean.CustomizeFunctionBean
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.meView.MeDialImgBean
import com.example.xingliansdk.utils.ImgUtil

class CustomizeFunctionDialAdapter(data: MutableList<CustomizeFunctionBean>) :
    BaseQuickAdapter<CustomizeFunctionBean, BaseViewHolder>(R.layout.item_dial_text_type, data) {

    override fun convert(helper: BaseViewHolder, item: CustomizeFunctionBean?) {
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