package com.app.fmate.adapter

import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.shon.connector.utils.TLog
import com.github.iielse.switchbutton.SwitchView
import com.google.gson.Gson
import com.shon.connector.bean.TimeBean

class ScheduleAdapter(data:MutableList<TimeBean>):BaseQuickAdapter<TimeBean,BaseViewHolder>
    (R.layout.iteam_schedule,data) {
    private var mOnSwipeListener:  onSwipeListener? = null

    fun getOnDelListener():  onSwipeListener? {
        return mOnSwipeListener
    }

    fun setOnDelListener(mOnDelListener: onSwipeListener) {
        mOnSwipeListener = mOnDelListener
    }

    interface onSwipeListener {
        fun onDel(pos: Int)
        fun onClick(pos: Int)
    }

    override fun convert(helper: BaseViewHolder, item: TimeBean) {
        TLog.error("=="+ Gson().toJson(item))
        if (item==null)
        {
            return
        }
        var year: String = item.year.toString()
        var   month  =item.month.toString()
        var   day = item.day.toString()
        var hours: String = if(item.hours<10)
            "0"+item.hours
        else
            item.hours.toString()
        var   min = if(item.min<10)
            "0"+item.min
        else
            item.min.toString()


        val switchAlarmClock = helper.getView<SwitchView>(R.id.Switch)
        val btnDelete = helper.getView<Button>(R.id.btnDelete)
        val constAll = helper.getView<ConstraintLayout>(R.id.constAll)
        val tvName=helper.getView<TextView>(R.id.tv_name)
        val tvSub=helper.getView<TextView>(R.id.tv_sub)
        if (item.switch == 2)
        {
            tvName.setTextColor(context.resources.getColor(R.color.main_text_color))
            tvSub.setTextColor(context.resources.getColor(R.color.main_text_color))

        }
        else
        {
            tvName.setTextColor(context.resources.getColor(R.color.bottom_nav_icon_dim))
            tvSub.setTextColor(context.resources.getColor(R.color.bottom_nav_icon_dim))
        }

        if(item.getUnicode().isNullOrEmpty())
            tvName.text="??????"
        else
        tvName.text=item.getUnicode()

        tvSub.text="${month}" +
                "???${day}???  $hours:$min"
        switchAlarmClock.isOpened = item.switch==2
        constAll.setOnClickListener {
            if (null != mOnSwipeListener) {
                mOnSwipeListener?.onClick(helper.adapterPosition)
            }
        }
        btnDelete.setOnClickListener {
            if (null != mOnSwipeListener) {
                //???????????????????????????mAdapter.notifyItemRemoved(pos)?????????????????????????????????
                //???????????????????????????????????????????????????????????? ((CstSwipeDelMenu) holder.itemView).quickClose();
                mOnSwipeListener!!.onDel(helper.adapterPosition)
            }
        }
    }
}