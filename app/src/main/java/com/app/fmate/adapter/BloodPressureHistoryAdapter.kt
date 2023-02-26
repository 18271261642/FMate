package com.app.fmate.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.room.BloodPressureHistoryBean
import com.app.fmate.view.DateUtil

class BloodPressureHistoryAdapter(data: MutableList<BloodPressureHistoryBean>) :
    BaseQuickAdapter<BloodPressureHistoryBean, BaseViewHolder>(R.layout.item_blood_pressure, data) {
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
    override fun convert(helper: BaseViewHolder, item: BloodPressureHistoryBean) {
        if (item == null) {
            return
        }

        var bpStatusImg = helper.getView<ImageView>(R.id.itemBpInputImg)

        var img = helper.getView<ImageView>(R.id.img)
        var tvNotUploaded = helper.getView<TextView>(R.id.tvNotUploaded)
        var btnDelete=helper.getView<Button>(R.id.btnDelete)
        if(item.type==1)
            tvNotUploaded.visibility= View.GONE
        else
            tvNotUploaded.visibility= View.GONE
        img.setImageResource(R.drawable.round_red)
        val SBP = item.systolicBloodPressure
        val DBP = item.diastolicBloodPressure

        bpStatusImg.visibility = if(item.type == 0) View.VISIBLE else View.GONE

        if (SBP < 130) {
            when {
                DBP in 85..89 -> {
                    img.setImageResource(R.drawable.round_bloodpressure_normal)
                }
                DBP in 90..99 -> {
                    // TLog.error("95???")
                    img.setImageResource(R.drawable.round_bloodpressure_two_level)
                }
                DBP >= 100 -> {
                    img.setImageResource(R.drawable.round_bloodpressure_three_level)
                }
                else ->
                    img.setImageResource(R.drawable.round_bloodpressure_low)
            }

        } else if ((SBP in 130..139)) {
            when {
                DBP <= 89 -> {
                    img.setImageResource(R.drawable.round_bloodpressure_normal)
                }
                DBP in 90..99 -> {
                    img.setImageResource(R.drawable.round_bloodpressure_two_level)
                }
                DBP >= 100 -> {
                    img.setImageResource(R.drawable.round_bloodpressure_three_level)
                }
            }

        } else if ((SBP in 140..159)) {
            when {
                DBP <= 99 -> {
                    img.setImageResource(R.drawable.round_bloodpressure_two_level)
                }
                DBP >= 100 -> {
                    img.setImageResource(R.drawable.round_bloodpressure_three_level)
                }
            }
            //   img.setImageResource(R.drawable.round_bloodpressure_two_level)
        } else if (SBP >= 160 || (DBP >= 100)) {
            img.setImageResource(R.drawable.round_bloodpressure_three_level)
        } else
            img.setImageResource(R.drawable.round_bloodpressure_low)
        helper.setText(R.id.tv_name, "${item.systolicBloodPressure}/${item.diastolicBloodPressure}")
        //    helper.setText(R.id.tvRight,item.dateTime)
        helper.setText(R.id.tvRight, DateUtil.getDate(DateUtil.HH_MM, item.startTime*1000))
        //   TLog.error("完全没进入"+item.dateTime)
        tvNotUploaded.setOnClickListener {
            if(null!=mOnSwipeListener)
                mOnSwipeListener?.onClick(helper.adapterPosition)
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