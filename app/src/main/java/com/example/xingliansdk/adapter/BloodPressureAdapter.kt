package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.ChildBloodPressureResult
import com.example.xingliansdk.utils.HelpUtil

class BloodPressureAdapter(data:MutableList<ChildBloodPressureResult>):BaseQuickAdapter<ChildBloodPressureResult,BaseViewHolder>(R.layout.blood_pressure_item,data) {
    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: ChildBloodPressureResult?) {
        if (item==null)
        {
            return
        }
         var llt=   helper.getView<LinearLayout>(R.id.linearLayout)
        val ll=llt.layoutParams as RelativeLayout.LayoutParams
        val top=(236-item.high)* HelpUtil.dp2px(1)
        val botm=(item.low-4)* HelpUtil.dp2px(1)
        ll.setMargins(0,top,0,botm)
        helper.setText(R.id.textView,item.time)
//        helper.setText(R.id.tv_mac,item.device.address)
//        helper.setText(R.id.tv_rssi,item.rssi.toString())
    }
}