package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.widget.SettingItemLayout
import com.shon.connector.bean.RemindTakeMedicineBean

class TimesPerDayAdapter(data: MutableList<RemindTakeMedicineBean.ReminderGroup>) :
    BaseQuickAdapter<RemindTakeMedicineBean.ReminderGroup, BaseViewHolder>(R.layout.item_times_per_day, data) {


    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: RemindTakeMedicineBean.ReminderGroup?) {
        if (item == null) {
            return
        }
        val hours: String = if (item.groupHH < 10)
            "0" + item.groupHH
        else
            item.groupHH.toString()
        val min = if (item.groupMM < 10)
            "0" + item.groupMM
        else
            item.groupMM.toString()
        val settingTime = helper.getView<SettingItemLayout>(R.id.settingTime)
        settingTime.setContentText("$hours:$min")
        settingTime.setTitleText("第${helper.adapterPosition+1}次")

    }
}