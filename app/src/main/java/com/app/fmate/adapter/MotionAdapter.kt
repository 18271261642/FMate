package com.app.fmate.adapter

import android.annotation.SuppressLint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.ChildResult
import com.orhanobut.hawk.Hawk

class MotionAdapter(data: MutableList<ChildResult>):BaseQuickAdapter<ChildResult,BaseViewHolder>(R.layout.motion_item,data) {
    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: ChildResult) {
        if (item==null)
        {
            return
        }
         var llt=   helper.getView<LinearLayout>(R.id.linearLayout)
            var rlAll=helper.getView<RelativeLayout>(R.id.rl_all)
        rlAll.layoutParams.width=10
        val ll=llt.layoutParams as RelativeLayout.LayoutParams
        var motion= (item.stepCount.toDouble()/Hawk.get("motionMax", 0))
        val top=236- 236*motion
        ll.setMargins(0,top.toInt(),0,50)
        helper.setText(R.id.textView,item.time)
    }

}