package com.example.xingliansdk.ui.heartrate

import android.graphics.Color
import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.custom.MyMarkerView
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.Config
import com.shon.connector.bean.DataBean
import kotlinx.android.synthetic.main.activity_real_time_heart_rate.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RealTimeHeartRateActivity : BaseActivity<BaseViewModel>() {
    private lateinit var hartsHrr: LineChart
    override fun layoutId()=R.layout.activity_real_time_heart_rate
    private lateinit var mRealTimeList: ArrayList<Int>
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        SNEventBus.register(this)
        hartsHrr = harts_hrr
        mRealTimeList= arrayListOf()
        chartView()
    }
    private fun chartView( ) {
        hartsHrr.description.isEnabled = true
        hartsHrr.isDragEnabled = true
        hartsHrr.setScaleEnabled(true)
        hartsHrr.setTouchEnabled(true)
        hartsHrr.isDragEnabled = true
        hartsHrr.setScaleEnabled(true)

        var mMarker = MyMarkerView(this, R.layout.custom_marker_view)
        mMarker.chartView = hartsHrr
        hartsHrr.marker = mMarker
        var xAxis: XAxis
        run {
            xAxis = hartsHrr.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            xAxis.textSize = 10f
            xAxis.textColor = Color.WHITE
            xAxis.setDrawAxisLine(true)
          //  xAxis.textColor = Color.rgb(255, 192, 56)
            xAxis.setCenterAxisLabels(true)
            xAxis.granularity = 120f // one hour
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            leftAxis.textColor = ColorTemplate.getHoloBlue()
            leftAxis.setDrawGridLines(true)
            leftAxis.isGranularityEnabled = true
            leftAxis.axisMinimum = 40f
            leftAxis.axisMaximum = 200f
            leftAxis.yOffset = -9f
         //   leftAxis.textColor = Color.rgb(255, 192, 56)
        }


        setDataView(0,0)
        hartsHrr.animateX(1500)
        val l: Legend = hartsHrr.legend
        hartsHrr.axisRight.isEnabled = false
        hartsHrr.xAxis.isEnabled = false
        l.isEnabled = false
    }
    private var values =
        ArrayList<Entry>()
    private fun setDataView(  size:Int, heartRate:Int) {
        values.add(Entry(size.toFloat(), heartRate.toFloat()))
      //  mList.forEachIndexed { index, i -> values.add(Entry(index.toFloat(), i.toFloat())) }
        var set1: LineDataSet
        if (hartsHrr.data != null &&
            hartsHrr.data.dataSetCount > 0
        ) {
            set1 = hartsHrr.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            hartsHrr.data.notifyDataChanged()
            hartsHrr.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "当前心率")
            set1.setDrawFilled(true)
            set1.color = resources.getColor(R.color.color_heart)
            set1.fillColor = resources.getColor(R.color.color_heart)
            set1.fillFormatter =
                IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
            set1.setDrawCircles(false)
          //  set1.setDrawVerticalHighlightIndicator(false)

            val data = LineData(set1)
            data.removeDataSet(5)
            data.setDrawValues(true)
            hartsHrr.data = data
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
        BleWrite.writeHeartRateSwitchCall(
            Config.ControlClass.APP_REAL_TIME_HEART_RATE_SWITCH_KEY,
            Config.OFF.toByte()
        )
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.ActiveUpload.DEVICE_REAL_TIME_OTHER.toInt() -> {
                var data: DataBean = event.data as DataBean
                mRealTimeList.add(data.heartRate)
                setDataView(mRealTimeList.size,data.heartRate)
                hartsHrr.invalidate()
            }
        }
    }
}