package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R


class BigDataIntervalAdapter(data:MutableList<Int>,mListener: EditAbleListAdapterListener):BaseQuickAdapter<Int,BaseViewHolder>(R.layout.iteam_big_data_interval,data) {
     var list =data
    interface EditAbleListAdapterListener {
        fun onEditTextChanged(position: Int, value: CharSequence)
    }
    private val mwListener: EditAbleListAdapterListener? = mListener
    var name= arrayOf("心率存储间隔","心率1存储间隔","血氧存储间隔","血压存储间隔","温度存储间隔","活动存储间隔")

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: Int) {
        if (item==null)
        {
            return
        }
            var edtTime=helper.getView<EditText>(R.id.editTime)
        helper.setText(R.id.tv_name, name[helper.position])
        helper.setText(R.id.tvIntervalTime, "$item/s")
        edtTime.setText("$item")
        val mTxtWatcher = TxtWatcher()
        edtTime.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                edtTime.addTextChangedListener(mTxtWatcher)
            }else{
                edtTime.removeTextChangedListener(mTxtWatcher)
            }
            mTxtWatcher?.buildWatcher(helper.adapterPosition)
        }
    }
   inner class TxtWatcher : TextWatcher {
        private var mPosition = 0
        fun buildWatcher(position: Int ) {
            mPosition = position
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            if (s != null) {
                if (s.isNotEmpty()) {
                    mwListener?.onEditTextChanged(mPosition, s)
                } else {
                    mwListener?.onEditTextChanged(mPosition, "0")
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }
}