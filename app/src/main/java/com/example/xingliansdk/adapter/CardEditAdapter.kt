package com.example.xingliansdk.adapter
import android.view.View
import android.widget.Button
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.network.api.cardView.EditCardVoBean
import com.shon.connector.utils.TLog

class CardEditAdapter(data: MutableList<EditCardVoBean.AddedListDTO>):BaseQuickAdapter<EditCardVoBean.AddedListDTO,BaseViewHolder>(R.layout.item_card_edit,data) ,
    DraggableModule {


    override fun convert(helper: BaseViewHolder, item: EditCardVoBean.AddedListDTO?) {
        if (item==null)
        {
            return
        }
        helper.setText(R.id.tvName,item.name)
        val viewColor=helper.getView<View>(R.id.viewColor)
//        TLog.error("helper.adapterPosition+"+helper.adapterPosition+"  data.size+"+data.size)
//        if(helper.adapterPosition>=data.size-1)
//            viewColor.visibility=View.GONE
//        else
//            viewColor.visibility=View.VISIBLE
    }

}