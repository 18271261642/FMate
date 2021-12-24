package com.example.xingliansdk.view

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart

class CustomBarChart : BarChart {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun init() {
        super.init()
        mRenderer = SuddleBarChartRenderer(this, mAnimator, mViewPortHandler)
    }
}
