package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.github.iielse.switchbutton.SwitchView
import com.shon.connector.bean.TimeBean
import com.shon.connector.utils.TLog

class AlarmClockAdapter(data: MutableList<TimeBean>) :
    BaseQuickAdapter<TimeBean, BaseViewHolder>(R.layout.item_alarm_clock_switch, data) {




    private var mOnSwipeListener: onSwipeListener? = null

    fun getOnDelListener(): onSwipeListener? {
        return mOnSwipeListener
    }

    fun setOnDelListener(mOnDelListener: onSwipeListener?) {
        mOnSwipeListener = mOnDelListener
    }

    interface onSwipeListener {
        fun onDel(pos: Int)
        fun onClick(pos: Int)
    }

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: TimeBean) {
        val switchAlarmClock = helper.getView<SwitchView>(R.id.Switch)
        val btnDelete = helper.getView<Button>(R.id.btnDelete)
        val constAll = helper.getView<ConstraintLayout>(R.id.constAll)
        val tvName = helper.getView<TextView>(R.id.tv_name)
        val tvSub = helper.getView<TextView>(R.id.tv_sub)

        val hours: String = if (item.hours < 10)
            "0" + item.hours
        else
            item.hours.toString()
        val min = if (item.min < 10)
            "0" + item.min
        else
            item.min.toString()
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

        tvName.text="$hours:$min"
        if (item.getSpecifiedTimeDescription().isNullOrEmpty()||item.getSpecifiedTimeDescription()==context.resources.getString(R.string.string_only_one)) {
            item.specifiedTimeDescription = context.resources.getString(R.string.string_alarm_never)

        }

        var  sub = if(item.getUnicode().isNullOrEmpty())
            item.getSpecifiedTimeDescription()
        else
            item.getUnicode()+", "+if(item.getSpecifiedTimeDescription() == "永不") context.resources.getString(R.string.string_alarm_never) else item.getSpecifiedTimeDescription()
        tvSub.text=sub
        TLog.error("item.switch+="+item.switch+"  endTime "+(item.endTime>System.currentTimeMillis()/1000))
        switchAlarmClock.isOpened = item.switch == 2
        constAll.setOnClickListener {
            if (null != mOnSwipeListener) {
                mOnSwipeListener?.onClick(helper.adapterPosition)
            }
        }
        btnDelete.setOnClickListener {
            if (null != mOnSwipeListener) {
                //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                mOnSwipeListener!!.onDel(helper.adapterPosition)
            }
        }
    }


}