package com.example.xingliansdk.adapter.node

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.example.xingliansdk.adapter.node.provider.DateRootNodeProvider
import com.example.xingliansdk.adapter.node.provider.ExerciseRecordNodeProvider
import com.example.xingliansdk.bean.node.DateRootNode
import com.example.xingliansdk.bean.node.ItemExerciseRecordNode

class ExerciseRecordAdapter :BaseNodeAdapter() {
    init {
        addFullSpanNodeProvider(DateRootNodeProvider())
        addNodeProvider(ExerciseRecordNodeProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        return when (data[position]) {
            is DateRootNode -> {
                0
            }
//            is TotalNode -> {
//                1
//            }
            is ItemExerciseRecordNode -> {
               1
            }
            else -> -1
        }
    }
    companion object {
        const val EXPAND_COLLAPSE_PAYLOAD = 110
    }
}