package com.app.fmate.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.shon.connector.bean.RemindTakeMedicineBean

class TakeMedicineTimeAdapter(data: MutableList<RemindTakeMedicineBean.ReminderGroup>) :
    BaseQuickAdapter<RemindTakeMedicineBean.ReminderGroup, BaseViewHolder>(
        R.layout.item_time,
        data
    ) {

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: RemindTakeMedicineBean.ReminderGroup) {
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
        helper.setText(R.id.tvSettingAlarmClock, "$hours:$min")
    }
}