package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.room.BloodPressureHistoryBean
import com.example.xingliansdk.bean.room.WeightBean
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.view.IF
import com.shon.connector.utils.TLog

class WeightAdapter(data: MutableList<WeightBean>) :
    BaseQuickAdapter<WeightBean, BaseViewHolder>(R.layout.item_weight, data) {

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
    override fun convert(helper: BaseViewHolder, item: WeightBean) {
        if (item == null) {
            return
        }
        var img = helper.getView<ImageView>(R.id.img)
        var tvSub = helper.getView<TextView>(R.id.tv_sub)
        var btnDelete=helper.getView<Button>(R.id.btnDelete)
        img.setImageResource(R.drawable.round_green)
        if(item.value=="1")
            tvSub.visibility= View.GONE
        else
        tvSub.visibility= View.VISIBLE
        val BMI = item.bmi
//        IF.isEmpty(BMI)
//        return
       // TLog.error("走线")

        if (BMI.toDouble() < 18.5) {
            img.setImageResource(R.drawable.round_bloodpressure_low)
        } else if (BMI.toDouble() >= 18.5 && BMI.toDouble() < 24) {
            img.setImageResource(R.drawable.round_green)
        } else if (BMI.toDouble() >= 24 && BMI.toDouble() < 28) {
            img.setImageResource(R.drawable.round_bloodpressure_one_level)
        } else if (BMI.toDouble() >= 28) {
            img.setImageResource(R.drawable.round_bloodpressure_two_level)
        }

        helper.setText(R.id.tv_name, "${item.weight}kg")
        helper.setText(R.id.tvRight, DateUtil.getDate(DateUtil.MM_DD_HH_MM,item.time*1000))
    //    helper.setText(R.id.tvRight, item.dateTime)
        btnDelete.setOnClickListener {
            if (null != mOnSwipeListener) {
                //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                mOnSwipeListener!!.onDel(helper.adapterPosition)
            }

        }
        tvSub.setOnClickListener {

            mOnSwipeListener!!.onClick(helper.adapterPosition)
        }

    }
}