package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.SleepTypeBean

class SleepAdapter(data: MutableList<SleepTypeBean>) :
    BaseQuickAdapter<SleepTypeBean, BaseViewHolder>(R.layout.item_sleep, data) {

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: SleepTypeBean?) {
        if (item == null) {
            return
        }
        helper.setText(R.id.tvItemStatusTitle, item.name)
        helper.setText(R.id.tvItemStatusSubTitle, item.timeDate)
        var tvStatus=helper.getView<TextView>(R.id.tvStatus)
        when (item.status) {
            "0" -> {
                tvStatus.setTextColor(context.resources.getColor(R.color.color_main_green))
                tvStatus.text="正常"
            }
            "1" ->{
                tvStatus.setTextColor(context.resources.getColor(R.color.color_sleep_low))
                tvStatus.text="偏低"
            }
            "2" -> {
                tvStatus.setTextColor(context.resources.getColor(R.color.color_sleep_height))
                tvStatus.text="偏高"
            }
        }
        helper.setText(R.id.tvReferValue, item.referValue)
    }

}