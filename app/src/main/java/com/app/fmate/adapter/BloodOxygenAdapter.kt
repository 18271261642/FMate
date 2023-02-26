package com.app.fmate.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.ChildResult
import com.shon.connector.utils.TLog
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk

class BloodOxygenAdapter(data: MutableList<ChildResult>):BaseQuickAdapter<ChildResult,BaseViewHolder>(R.layout.sleep_item,data) {

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: ChildResult) {
        TLog.error("item++${Gson().toJson(item)}")
        if (item==null)
        {
            return
        }
         var llt=   helper.getView<LinearLayout>(R.id.linearLayout)
        val ll=llt.layoutParams as RelativeLayout.LayoutParams
        var motion= (item.stepCount.toDouble()/Hawk.get("motionMax", 0))
        val top=236- 236*motion
        ll.setMargins(0,top.toInt(),0,50)
        helper.setText(R.id.textView,item.time)
    }

}