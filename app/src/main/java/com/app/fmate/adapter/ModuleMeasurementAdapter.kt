package com.app.fmate.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.github.iielse.switchbutton.SwitchView

class ModuleMeasurementAdapter(data:MutableList<Int>):BaseQuickAdapter<Int,BaseViewHolder>(R.layout.iteam_schedule,data) {
    var name= arrayOf("心率测量状态","血氧测量状态","血压测量状态","温度测量状态","湿度测量状态")
    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: Int) {
        if (item==null)
        {
            return
        }

       var  switchAlarmClock=helper.getView<SwitchView>(R.id.SwitchAlarmClock)
        helper.setText(R.id.tv_name, name[helper.position])
        switchAlarmClock.isOpened = item==2
    }
}