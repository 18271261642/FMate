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
        tvName.text = item.name
        var imgDrag = helper.getView<ImageView>(R.id.imgDrag)
        imgDrag.visibility = View.GONE
      //  val viewColor=helper.getView<View>(R.id.viewColor)
//        if(helper.adapterPosition>=data.size-1)
//            viewColor.visibility=View.GONE
//        else
//            viewColor.visibility=View.VISIBLE

    }

}