package com.app.fmate

import android.graphics.Color
import android.os.Bundle
import com.app.fmate.base.BaseActivity
import com.app.fmate.ui.bloodOxygen.viewmodel.BloodOxygenViewModel
import com.app.fmate.utils.HeartAxisValueFormatter
import com.app.fmate.utils.HelpUtil
import com.app.fmate.utils.TimeUtil
import com.app.fmate.view.DateUtil
import com.app.fmate.widget.TitleBarLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_heart_rate_o.*

class HeartRateTestActivity : BaseActivity<BloodOxygenViewModel>(),
    OnChartValueSelectedListener {
    private lateinit var mList: ArrayList<Int>
    private lateinit var hartsHrr: LineChart
    override fun layoutId() = R.layout.activity_heart_rate_o
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        mList = arrayListOf()
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
            }

            override fun onActionClick() {
            }
        })
        hartsHrr = harts_hrr
        chartView()
    }

    private fun chartView() {
        hartsHrr.description.isEnabled = false
        hartsHrr.legend.isEnabled = false  //色块不显示
        hartsHrr.setScaleEnabled(true)//设置比列启动
        hartsHrr.setOnChartValueSelectedListener(this)
        hartsHrr.isScaleYEnabled = false//启用Y轴缩放
        val timeMatter: IAxisValueFormatter = HeartAxisValueFormatter(hartsHrr)
        var xAxis: XAxis
        run {
            xAxis = hartsHrr.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)//设置网格线
            xAxis.textColor = R.color.red
            xAxis.axisMaximum = 288f
            xAxis.axisMinimum = 0f
            xAxis.granularity = 72f // 想 弄成 4 hour
            xAxis.setLabelCount(5, true)
            xAxis.axisLineColor = Color.WHITE
            xAxis.valueFormatter = timeMatter
            xAxis.mLabelHeight = 20
            xAxis.mLabelRotatedHeight = 20
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.isEnabled = false
//    leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            leftAxis.axisMinimum = 40f
            leftAxis.axisMaximum = 220f
            leftAxis.setDrawZeroLine(false)
        }
        var rightAxis: YAxis
        run {
            rightAxis = hartsHrr.axisRight
            rightAxis.axisMinimum = 40f
            rightAxis.axisMaximum = 220f
            rightAxis.axisLineColor = Color.WHITE
            rightAxis.zeroLineColor = resources.getColor(R.color.color_view)
            rightAxis.gridColor = resources.getColor(R.color.color_view)
            rightAxis.granularity = 40f
            rightAxis.setDrawZeroLine(false)
        }
//setDataView(mList)
        getHeart()
        hartsHrr.invalidate()
    }

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        TLog.error("onValueSelected++" + e.x + ",  y==" + e.y + ",  Highlight+=" + h.x)
        tvType
        tvType.text = String.format(
            "%s",
            DateUtil.getDate(
                DateUtil.HH_MM,
                (TimeUtil.getTodayZero(0) + (e.x.toLong() * 60000L))
            )
        )
        tvHeart.text = HelpUtil.getSpan(h.y.toLong().toString(), "次/分钟")

    }


    private fun getHeart() {
        setData()
        hartsHrr.invalidate()
    }

    private fun setData() {
        var values: ArrayList<Entry?> = ArrayList()
        val data = LineData()
        testList.forEachIndexed { index, i ->
            if (i <= 0) {
                if (values.isNotEmpty()) {
                    val set1 = LineDataSet(values, Math.random().toString())
                    set1.color = resources.getColor(R.color.color_heart)
                    set1.fillColor = resources.getColor(R.color.color_heart_view)
                    set1.fillFormatter =
                        IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
                    set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    set1.setDrawFilled(true)
                    set1.setDrawCircles(false)//设置画圆点
                    set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
                    data.addDataSet(set1)
                }
                values = ArrayList()
            } else {
                values.add(Entry(index.toFloat(), i.toFloat()))
            }
        }
        if (values.isNotEmpty()) {
            val set1 = LineDataSet(values, "")
            set1.color = resources.getColor(R.color.color_heart)
            set1.fillColor = resources.getColor(R.color.color_heart_view)
            set1.fillFormatter =
                IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            set1.setDrawFilled(true)
            set1.setDrawCircles(false)//设置画圆点
            set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
            data.addDataSet(set1)
        }
        hartsHrr.data = data
    }

    var testList = arrayListOf(
        76,
        76,
        72,
        66,
        64,
        66,
        69,
        70,
        69,
        67,
        65,
        64,
        63,
        63,
        63,
        63,
        64,
        58,
        69,
        67,
        64,
        61,
        59,
        61,
        58,
        57,
        57,
        57,
        57,
        56,
        57,
        56,
        57,
        56,
        63,
        59,
        61,
        61,
        60,
        58,
        56,
        57,
        57,
        56,
        56,
        56,
        56,
        57,
        59,
        61,
        59,
        61,
        59,
        65,
        68,
        64,
        60,
        62,
        65,
        60,
        59,
        57,
        64,
        60,
        58,
        58,
        57,
        57,
        57,
        57,
        56,
        56,
        56,
        58,
        60,
        67,
        66,
        61,
        58,
        58,
        57,
        56,
        56,
        56,
        58,
        58,
        57,
        59,
        71,
        0,
        0,
        0,
        0,
        119,
        119,
        119,
        0,
        107,
        108,
        157,
        139,
        95,
        81,
        64,
        66,
        100,
        99,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        75,
        80,
        80,
        78,
        67,
        75,
        75,
        79,
        77,
        78,
        78,
        74,
        84,
        80,
        87,
        83,
        84,
        0,
        53,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        67,
        51,
        84,
        70,
        61,
        0,
        53,
        70,
        0,
        49,
        75,
        76,
        84,
        55,
        72,
        49,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        100,
        100,
        0,
        0,
        54,
        50,
        64,
        68,
        122,
        106,
        131,
        119,
        85,
        56,
        54,
        0,
        0,
        0,
        121,
        88,
        89,
        76,
        60,
        60,
        60,
        59,
        60,
        59,
        60,
        58,
        0,
        59,
        60,
        0,
        0,
        60,
        60,
        60,
        60,
        0,
        0,
        0,
        84,
        76,
        76,
        78,
        79,
        79,
        79,
        79,
        75,
        70,
        67,
        65
    )
}