package com.example.xingliansdk.adapter

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.*
import com.shon.connector.utils.TLog.Companion.error
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import java.util.*

class IndexAdapter(data: MutableList<MultiItemEntity>) :
    BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data),
    OnItemClickListener {
    override fun convert(helper: BaseViewHolder, item: MultiItemEntity?) {
//        TLog.error("${helper.itemViewType}")
        when (helper.itemViewType) {
            0 -> {
                if (item == null) {
                    return
                }
                val mTitleResult = item as MotionResult//data class 模式实体类
                val mList = mTitleResult.mChildResult
                val mRecyclerView = helper.getView<RecyclerView>(R.id.recyclerview)
//                error("宽----度==${mRecyclerView.measuredWidth}")
                val mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                mRecyclerView.layoutManager = mLinearLayoutManager

                mRecyclerView.post {
                    var mMotionAdapter = MotionAdapter(mList as MutableList<ChildResult>)
                    mRecyclerView.adapter = mMotionAdapter
                }


            }
            1 -> {
                if (item == null) {
                    return
                }
                val mSleepResult = item as SleepResult//data class 模式实体类
                val mList = mSleepResult.mChildResult
                val llAll=helper.getView<LinearLayout>(R.id.ll_all)
                val mRecyclerView = helper.getView<RecyclerView>(R.id.recyclerview_sleep)
                val tvNo = helper.getView<TextView>(R.id.tv_no)
                mRecyclerView.isVisible = mList.isNotEmpty()
                tvNo.isVisible = mList.isEmpty()
                val mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                mRecyclerView.layoutManager = mLinearLayoutManager
//                error("111111111111获取一下宽度++${Gson().toJson(mList)}")
                var mOtherAdapter = OtherAdapter(mList as MutableList<ChildSleepResult>)
                mRecyclerView.adapter = mOtherAdapter
//                llAll.setOnClickListener {
//                    TLog.error("点了 睡眠")
//                    context.startActivity(Intent(context,SleepDetailsActivity::class.java)) }
//                error("adapter++${mOtherAdapter.data[0]}")
            }
            2 -> {
                if (item == null) {
                    return
                }
                val mResult = item as HeartRateResult//data class 模式实体类
                val mList = mResult.mChildResult
                var hartsHrr: LineChart = helper.getView(R.id.harts_hrr)
                chartView(hartsHrr, mList as MutableList<ChildHeartRateResult>)
            }
            3 -> {
                if (item == null) {
                    return
                }
//                error("33")
//                val mResult = item as BloodPressureResult//data class 模式实体类
//                val mList = mResult.mChildResult
//                val mRecyclerView = helper.getView<RecyclerView>(R.id.recyclerview_motion)
////                val mLinearLayoutManager = LinearLayoutManager(context)
//                val mLinearLayoutManager: LinearLayoutManager =
//                    object : LinearLayoutManager(context, HORIZONTAL, false) {
//                        override fun canScrollHorizontally(): Boolean {
//                            return false
//                        }
//                    }
////                mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
//                mRecyclerView.layoutManager = mLinearLayoutManager
////                error("获取一下宽度++${mRecyclerView.width}")
//                var mBloodPressureAdapter =
//                    BloodPressureAdapter(mList as MutableList<ChildBloodPressureResult>)
//                mRecyclerView.adapter = mBloodPressureAdapter
            }
            4 -> {
                if (item == null) {
                    return
                }
                error("33")
                val mResult = item as BloodPressureResult//data class 模式实体类
                val mList = mResult.mChildResult
                error("===${Gson().toJson(mList)}")
                val mRecyclerView = helper.getView<RecyclerView>(R.id.ryBloodPressure)
                val mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                mRecyclerView.layoutManager = mLinearLayoutManager
                var mBloodPressureAdapter =
                    BloodPressureAdapter(mList as MutableList<ChildBloodPressureResult>)
                mRecyclerView.adapter = mBloodPressureAdapter
            }
            5 -> {
                if (item == null) {
                    return
                }
                error("33")
                val mResult = item as BloodPressureResult//data class 模式实体类
                val mList = mResult.mChildResult
                error("===${Gson().toJson(mList)}")
                val mRecyclerView = helper.getView<RecyclerView>(R.id.ryBloodPressure)
                val mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                mRecyclerView.layoutManager = mLinearLayoutManager
                var mBloodPressureAdapter =
                    BloodPressureAdapter(mList as MutableList<ChildBloodPressureResult>)
                mRecyclerView.adapter = mBloodPressureAdapter
            }
            6 -> {
                if (item == null) {
                    return
                }
                error("33")
                val mResult = item as BloodPressureResult//data class 模式实体类
                val mList = mResult.mChildResult
                error("===${Gson().toJson(mList)}")
                val mRecyclerView = helper.getView<RecyclerView>(R.id.ryBloodPressure)
                val mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                mRecyclerView.layoutManager = mLinearLayoutManager
                var mBloodPressureAdapter =
                    BloodPressureAdapter(mList as MutableList<ChildBloodPressureResult>)
                mRecyclerView.adapter = mBloodPressureAdapter
            }
            else -> {
            }
        }
    }

    init {
        addItemType(0, R.layout.activity_motion)//运动
        addItemType(1, R.layout.activity_other)//睡眠
        addItemType(2, R.layout.activity_heart_rate)//心率
        addItemType(3, R.layout.activity_blood_pressure)//血压

        //暂未用
        addItemType(4, R.layout.ecg_item)//ECG
        addItemType(5, R.layout.blood_oxygen_item)//血氧
        addItemType(6, R.layout.hrv_item)//HVR
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {

    }


    private fun chartView(hartsHrr: LineChart, mList: MutableList<ChildHeartRateResult>) {
        hartsHrr.description.isEnabled = false
        hartsHrr.isDragEnabled = false
        hartsHrr.setScaleEnabled(false)

        var xAxis: XAxis
        run {
            // // X-Axis Style // //
            xAxis = hartsHrr.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            xAxis.textSize = 10f
            xAxis.textColor = Color.WHITE
            xAxis.setDrawAxisLine(true)
            xAxis.textColor = Color.rgb(255, 192, 56)
            xAxis.setCenterAxisLabels(true)
            xAxis.granularity = 1200f // one hour

//            xAxis.axisMaximum = 100F
//            xAxis.axisMinimum = 0f
//            xAxis.setDrawAxisLine(true)
            // vertical grid lines
            // xAxis.enableGridDashedLine(10f, 10f, 0f)
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            leftAxis.textColor = ColorTemplate.getHoloBlue()
            leftAxis.setDrawGridLines(true)
            leftAxis.isGranularityEnabled = true
            leftAxis.axisMinimum = 40f
            leftAxis.axisMaximum = 220f
            leftAxis.yOffset = -9f
            leftAxis.textColor = Color.rgb(255, 192, 56)
//
//            hartsHrr.axisRight.isEnabled = false
//            yAxis.enableGridDashedLine(10f, 10f, 0f)
//            yAxis.axisMaximum = 200f
//            yAxis.axisMinimum = 0f
        }
        // force pinch zoom along both axis
//        run {
//            val llXAxis = LimitLine(9f)
//            llXAxis.lineWidth = 4f
//            llXAxis.enableDashedLine(10f, 10f, 0f)
//            llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
//            yAxis.setDrawLimitLinesBehindData(true)
//        }

//        val mv = XYMarkerView(context, timeMatter)
//        mv.chartView = hartsHrr // For bounds control
//        hartsHrr.marker = mv // Set the marker to the chart

        setDataView(hartsHrr, mList)
        hartsHrr.animateX(1500)
        val l: Legend = hartsHrr.legend
//        // draw legend entries as lines
        hartsHrr.axisRight.isEnabled = false
        hartsHrr.xAxis.isEnabled = false
        l.isEnabled = false
    }

    private var values =
        ArrayList<Entry>()

    private fun setDataView(hartsHrr: LineChart, mList: MutableList<ChildHeartRateResult>) {
        for (i in 0 until 720) {
            val rand = Random()

            if (i in 301..399 || i < 100) {
                values.add(Entry(i.toFloat(), 0F)) // add one entry per hour
            } else values.add(
                Entry(
                    i.toFloat(),
                    ((rand.nextInt(60) + 60).toFloat())
                )
            ) // add one entry per hour
        }
//        for (i in 0 until 50) {
//            values.remove(Entry(i.toFloat(), mList[i].stepCount.toFloat()))
//        }

//        for (i in 50 until 50+mList.size) {
//            values.add(Entry(50+i.toFloat(), mList[i].stepCount.toFloat()))
//        }

        var set1: LineDataSet

        if (hartsHrr.data != null &&
            hartsHrr.data.dataSetCount > 0
        ) {
            set1 = hartsHrr.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            //    set1.setDrawHorizontalHighlightIndicator(false)
            // set1.notifyDataSetChanged();
            hartsHrr.data.notifyDataChanged()
            hartsHrr.notifyDataSetChanged()
        } else {

            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
            // set color of filled area
//            set1.setValueFormatter(MyA)
            set1.setDrawCircles(false)
            set1.setDrawVerticalHighlightIndicator(false)
            val data = LineData(set1)
            data.removeDataSet(5)
            //  set1.fillColor = Color.RED
            data.setDrawValues(false)
            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);
            hartsHrr.data = data
        }
    }

}


