package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.network.api.cardView.EditCardVoBean
import com.example.xingliansdk.utils.ResUtil.getResources
import com.shon.connector.utils.TLog


class CardDeleteAdapter(data: MutableList<EditCardVoBean.MoreListDTO>) :
    BaseQuickAdapter<EditCardVoBean.MoreListDTO, BaseViewHolder>(R.layout.item_card_edit, data),
    DraggableModule {
    override fun convert(helper: BaseViewHolder, item: EditCardVoBean.MoreListDTO?) {
        if (item == null) {
            return
        }
//        TLog.error("走删除数据了")
        var tvName = helper.getView<TextView>(R.id.tvName)
        var imgDeleteAdd = helper.getView<ImageView>(R.id.imgDeleteAdd)
        imgDeleteAdd.setImageResource(R.mipmap.icon_card_add)
        tvName.text = changeEnLanguage(item.name)
        var imgDrag = helper.getView<ImageView>(R.id.imgDrag)
        imgDrag.visibility = View.GONE
      //  val viewColor=helper.getView<View>(R.id.viewColor)
//        if(helper.adapterPosition>=data.size-1)
//            viewColor.visibility=View.GONE
//        else
//            viewColor.visibility=View.VISIBLE

    }


    private fun changeEnLanguage(str : String) : String{
        if(str == "运动记录")
            return context.getString(R.string.string_sport_record)
        if(str == "心率")
            return context.getString(R.string.string_heart)
        if(str == "睡眠")
            return context.getString(R.string.string_sleep)
        if(str == "压力")
            return context.getString(R.string.string_pressure)
        if(str == "血氧饱和度")
            return context.getString(R.string.string_spo2_title)
        if(str == "血压")
            return context.getString(R.string.string_bp)
        if(str == "体温")
            return context.getString(R.string.string_temp)
        if(str == "体重")
            return context.getString(R.string.string_weight)
        return context.getString(R.string.string_sport_record)
    }
}