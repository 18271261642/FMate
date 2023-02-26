package com.app.fmate.adapter.node

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.app.fmate.adapter.node.provider.DateRootNodeProvider
import com.app.fmate.adapter.node.provider.ExerciseRecordNodeProvider
import com.app.fmate.bean.node.DateRootNode
import com.app.fmate.bean.node.ItemExerciseRecordNode

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