package com.app.fmate.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.ChildSleepResult
import com.app.fmate.utils.HelpUtil

class OtherAdapter(data: MutableList<ChildSleepResult>) :
    BaseQuickAdapter<ChildSleepResult, BaseViewHolder>(R.layout.sleep_item, data) {

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: ChildSleepResult) {
//        TLog.error("item++${Gson().toJson(item)}")
        if (item == null) {
            return
        }
        var rlAll=helper.getView<RelativeLayout>(R.id.rl_all)
        var view1 = helper.getView<View>(R.id.view_1)
        var view2 = helper.getView<View>(R.id.view_2)
        var view3 = helper.getView<View>(R.id.view_3)

        val ll = rlAll.layoutParams
        val linearParams =
            rlAll.layoutParams //取控件

//        var motion = (item.stepCount.toDouble() / Hawk.get("motionMax", 0))
        view1.isVisible=item.type==1
        view2.isVisible=item.type==2
        view3.isVisible=item.type==4
        if (item.type==1||item.type==2||item.type==4) {
            linearParams.width = (HelpUtil.dip2px(context, 8 / 9.toFloat() * item.stepCount))
            // val top = 236 - 236 * motion
            //    ll.setMargins(0, top.toInt(), 0, 50)
            helper.setText(R.id.textView, item.time)
        }
        else
            linearParams.width=0
    }

}