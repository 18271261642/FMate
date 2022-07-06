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

    override fun convert(helper: BaseViewHolder, item: EditCardVoBean.AddedListDTO?) {
        if (item==null)
        {
            return
        }
        helper.setText(R.id.tvName,changeEnLanguage(item.name))
        val viewColor=helper.getView<View>(R.id.viewColor)
//        TLog.error("helper.adapterPosition+"+helper.adapterPosition+"  data.size+"+data.size)
//        if(helper.adapterPosition>=data.size-1)
//            viewColor.visibility=View.GONE
//        else
//            viewColor.visibility=View.VISIBLE
    }

}