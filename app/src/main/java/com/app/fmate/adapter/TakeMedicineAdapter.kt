package com.app.fmate.adapter

import android.annotation.SuppressLint
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.shon.connector.bean.RemindTakeMedicineBean
import com.shon.connector.utils.TLog

class TakeMedicineAdapter(data: MutableList<RemindTakeMedicineBean>) :
    BaseQuickAdapter<RemindTakeMedicineBean, BaseViewHolder>(
        R.layout.item_take_medicine_switch,
        data
    ) {
    private var mOnSwipeListener: onSwipeListener ?=null

    fun getOnDelListener(): onSwipeListener? {
        return mOnSwipeListener
    }

    fun setOnDelListener(mOnDelListener: onSwipeListener) {
        mOnSwipeListener = mOnDelListener
    }

    interface onSwipeListener {
        fun onDel(pos: Int)
        fun onClick(pos: Int)
    }

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: RemindTakeMedicineBean) {
        if (item == null) {
            return
        }
        TLog.error("item="+item.reminderPeriod)
        if (item.getUnicodeTitle().isNullOrEmpty())
            helper.setText(R.id.tv_name, context.resources.getString(R.string.string_take_media))
        else
            helper.setText(R.id.tv_name, item.getUnicodeTitle())
        if (item.reminderPeriod == 0)
            helper.setText(R.id.tv_sub, context.resources.getString(R.string.string_every_day))
        else
            helper.setText(R.id.tv_sub, context.resources.getString(R.string.string_interval_2)+"${item.reminderPeriod}"+context.resources.getString(R.string.string_day))
        val ryTime = helper.getView<RecyclerView>(R.id.ryTime)
        val btnDelete = helper.getView<Button>(R.id.btnDelete)
        val constAll = helper.getView<ConstraintLayout>(R.id.constAll)
        ryTime.setHasFixedSize(true)

        //判断间隔


        if (ryTime.layoutManager == null) {
            ryTime.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
        if (ryTime.adapter == null) {
            item.groupList.sortBy { it.countHM }
            val nestAdapter = TakeMedicineTimeAdapter(item.groupList)
            nestAdapter.setOnItemClickListener(null)
            nestAdapter.setOnItemChildClickListener(null)
            ryTime.adapter = nestAdapter
        }
//        ryTime.setOnClickListener {
//            if (null != mOnSwipeListener) {
//                mOnSwipeListener?.onClick(helper.adapterPosition)
//            }
//        }
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