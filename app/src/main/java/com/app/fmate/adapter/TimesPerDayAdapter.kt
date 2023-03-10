package com.app.fmate.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.widget.SettingItemLayout
import com.shon.connector.bean.RemindTakeMedicineBean

class TimesPerDayAdapter(data: MutableList<RemindTakeMedicineBean.ReminderGroup>) :
    BaseQuickAdapter<RemindTakeMedicineBean.ReminderGroup, BaseViewHolder>(R.layout.item_times_per_day, data) {


    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: RemindTakeMedicineBean.ReminderGroup) {
        if (item == null) {
            return
        }
        val settingTime = helper.getView<SettingItemLayout>(R.id.settingTime)
        val hours: String = if (item.groupHH < 10)
            "0" + item.groupHH
        else
            item.groupHH.toString()
        val min = if (item.groupMM < 10)
            "0" + item.groupMM
        else
            item.groupMM.toString()

        settingTime.setContentText("$hours:$min")
        settingTime.setTitleText(String.format(context.resources.getString(R.string.string_the_time),helper.adapterPosition+1))

    }
}