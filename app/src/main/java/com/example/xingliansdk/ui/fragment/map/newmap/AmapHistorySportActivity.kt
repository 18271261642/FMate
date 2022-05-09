package com.example.xingliansdk.ui.fragment.map.newmap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.MapView
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.overlay.SmoothMoveMarker
import com.amap.api.trace.TraceListener
import com.amap.api.trace.TraceOverlay
import com.example.xingliansdk.Config.exercise.MILE
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.db.AmapSportBean
import com.example.xingliansdk.ui.fragment.map.share.ImgShareActivity
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.CusMapContainerView
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout.TitleBarListener
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.sn.map.interfaces.OnMapScreenShotListener
import kotlinx.android.synthetic.main.activity_amap_sport_detail_layout.*
import kotlinx.android.synthetic.main.include_map.*
import kotlinx.android.synthetic.main.item_amap_sport_detail_heart_chart_layout.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

/**
 * 显示运动轨迹页面，根据记录的数据展示轨迹
 * Created by Admin
 *
 */
class AmapHistorySportActivity : BaseActivity<BaseViewModel>(), LocationSource,
    AMapLocationListener,
    TraceListener {
    private var amapMapView: MapView? = null
    private var startMark: Marker? = null
    private var endMark: Marker? = null
    var polyline: Polyline? = null
    private var aMap: AMap? = null
    private var mListener: LocationSource.OnLocationChangedListener? = null
    private var mlocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private val mOverlayList: ConcurrentMap<Int, TraceOverlay> = ConcurrentHashMap()
    private var amapDetailSV: NestedScrollView? = null
    private var cusMapContainerView: CusMapContainerView? = null
    var decimalFormat = DecimalFormat("#.##")

    //类型
    private var itemAmapSportTypeTv: TextView? = null

    //公里
    private var itemSportDetailDistanceTv: TextView? = null

    //时间
    private var itemSportDetailDateTv: TextView? = null

    //运动时间
    private var itemAmapSportDetailDurationTv: TextView? = null

    //热量
    private var itemAmapSportDurationTv: TextView? = null

    //平均心率
    private var itemAmapSportDetailHeartTv: TextView? = null

    //步数
    private var itemAmapSportStepTv: TextView? = null

    //平均配速
    private var itemAmapSportDetailPeisuTv: TextView? = null

    //平均速度
    private var itemAmapSportSpeedTv: TextView? = null

    //判断是否是GPS运动，GPS运动有轨迹展示，
    private var isGpsSport : Boolean ?= null


    private lateinit var hartsHrr: LineChart
    private lateinit var mList: ArrayList<Int>
    companion object {
    var fileName=Environment.getExternalStorageDirectory().path+"/"+Environment.DIRECTORY_PICTURES
            }




    override fun layoutId() = R.layout.activity_amap_sport_detail_layout

    override fun initView(savedInstanceState: Bundle?) {
        amapMapView = findViewById(R.id.amapDetailMapView)
        amapMapView?.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .titleBar(sportDetailTB)
            .init()

        mList = arrayListOf()

        initViews()
        if (aMap == null) {
            aMap = amapMapView?.map

            setUpMap()
        }
        showAMapData()
    }

    private fun showAMapData() {
        try {
            val intent = intent
            val amapSportBean = intent.getSerializableExtra("sport_position") as AmapSportBean?

            if (amapSportBean == null) {
                showEmptyView()
                return
            }
            showSportData(amapSportBean)
            val latStr = amapSportBean.latLonArrayStr ?: return
            val latLngList =
                Gson().fromJson<List<LatLng>>(latStr, object : TypeToken<List<LatLng?>?>() {}.type)

            if(latLngList == null || latLngList.size == 1 || latLngList.isEmpty()){
                isGpsSport = false
                cusMapContainerView?.visibility = View.GONE
                return
            }

            isGpsSport = true

            //  List<LatLng> latLngList = getLanList(traceLocationList);

            //开始和结束的位置标记
            val startLng = latLngList[0]
            val markerOptions =
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(typeSportImg(amapSportBean.sportType)))
                    .position(startLng)
                    .draggable(false)
            startMark = aMap!!.addMarker(markerOptions)
            startMark?.isDraggable = false

            //结束的位置
            val endLng = latLngList[latLngList.size - 1]
            TLog.error("endLng+=" + endLng.latitude)

            val markerOption =
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_amap_sport_end))
                    .position(endLng)
                    .draggable(false)
            endMark = aMap!!.addMarker(markerOption)
            endMark?.isDraggable = false
            polyline = aMap!!.addPolyline(
                PolylineOptions().addAll(latLngList).color(Color.parseColor("#00FF01")).width(15f)
            )
            val boundsBuilder = LatLngBounds.Builder()
            for (p in latLngList) {
                val latLng = LatLng(p.latitude, p.longitude)
                //  polyline.po.add(latLng)
                boundsBuilder.include(latLng)
            }
            aMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 30))
            val smoothMoveMarker = SmoothMoveMarker(aMap)
            smoothMoveMarker.setDescriptor(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(
                        resources, R.mipmap.gps_point
                    )
                )
            )
            smoothMoveMarker.setPoints(latLngList)
            smoothMoveMarker.setTotalDuration(5)
            smoothMoveMarker.startSmoothMove()
            Handler(Looper.getMainLooper()).postDelayed(
                { smoothMoveMarker.setVisible(false) },
                (5 * 1000).toLong()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPaceStr(str: String?): String {
        return try {
            if (str == null) return "--"
            val integer = Integer.valueOf(str)
            if (integer == 0) return "00'00''"


            val hour = integer / 60
            val minute = integer % 60
            "$hour'$minute''"
        } catch (e: Exception) {
            e.printStackTrace()
            "--"
        }
    }

    private fun showSportData(amapSportBean: AmapSportBean) {
        try {
            TLog.error("获取当前数据库的数据++" + Gson().toJson(amapSportBean))
            TLog.error("心率++" + amapSportBean.heartArrayStr)

            val latStr = amapSportBean.latLonArrayStr


            if (amapSportBean.heartArrayStr.isNotEmpty()) {
                mList = Gson().fromJson(
                    amapSportBean.heartArrayStr,
                    ArrayList::class.java
                ) as ArrayList<Int>
                TLog.error("mlist==" + mList.size)
                var number = 0
                if (mList.isNotEmpty() || mList.size > 0) {
                    if (Collections.max(mList) > 40) //整个数组小于当前值
                    {
                        number = Math.floor(mList.size / 60.0).roundToInt()
                        TLog.error("展示分钟=" + number)
                        tvAvgTime.text = (number / 2).toString()
                        tvEndTime.text = number.toString()
                        llHeart.visibility = View.VISIBLE

//                    hartsHrr.visibility = View.VISIBLE
//                    pieChart.visibility = View.VISIBLE
                        pieView()
                        chartView()

                    }
                }
            }
            TLog.error("步数===" + amapSportBean.currentSteps)
            itemAmapSportTypeTv!!.text = typeSport(amapSportBean.sportType)
            var dis=Utils.divi(amapSportBean.distance.toDouble(),1000.0,2)
            var unit="公里"
            var pace=amapSportBean.pace.toDouble()
            var paceStr = "--"
            var  averageSpeed=  amapSportBean.averageSpeed.toDouble() * 3600 / 1000
            if(userInfo.userConfig.distanceUnit==1) {
                dis = Utils.mul(dis, MILE, 2)
                unit="英里"
               // pace=Utils.mul(pace, MILE)

                averageSpeed=   Utils.mul(averageSpeed, MILE, 2)

                paceStr = Utils.matchPace(averageSpeed);
            }else{
                paceStr = Utils.matchPace(averageSpeed);
            }
            itemSportDetailDistanceTv!!.text =
                HelpUtil.getSpan(
                    dis.toString(),
                    unit,
                    16
                )

            itemSportDetailDateTv!!.text =
                Utils.formatCusTimeForDay(amapSportBean.endSportTime, DateUtil.MMDD_HH_MM)
            itemAmapSportDetailDurationTv!!.text = amapSportBean.currentSportTime
            itemAmapSportDurationTv!!.text = HelpUtil.getSpan(amapSportBean.calories, "千卡", 11)
            itemAmapSportStepTv?.text = HelpUtil.getSpan(
                amapSportBean.currentSteps.toString(),
                " 步",
                11
            )
            TLog.error("pace++"+pace)
//            itemAmapSportDetailPeisuTv!!.text = HelpUtil.getSpan(
//                getPaceStr(pace.toInt().toString()),
//                "/$unit",
//                11
//            )

            itemAmapSportDetailPeisuTv!!.text = HelpUtil.getSpan(paceStr,"/$unit",
                11)

            itemAmapSportSpeedTv!!.text =
                HelpUtil.getSpan(
                    String.format("%.2f", averageSpeed),
                    "$unit/时",
                    11
                )


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showEmptyView() {
        itemAmapSportTypeTv!!.text = "--"
        itemSportDetailDistanceTv!!.text = "--"
        itemSportDetailDateTv!!.text = "--"
        itemAmapSportDetailDurationTv!!.text = "--"
        itemAmapSportDurationTv!!.text = "--"
        itemAmapSportDetailPeisuTv!!.text = "--"
        itemAmapSportSpeedTv!!.text = "--"
    }

    private fun initViews() {
        amapDetailSV = findViewById(R.id.amapDetailSV)
        cusMapContainerView = findViewById(R.id.cusMapContainerView)
        cusMapContainerView?.setNestedScrollView(amapDetailSV)
        itemAmapSportTypeTv = findViewById(R.id.itemAmapSportTypeTv)
        itemSportDetailDistanceTv = findViewById(R.id.itemSportDetailDistanceTv)
        itemSportDetailDateTv = findViewById(R.id.itemSportDetailDateTv)
        itemAmapSportDetailDurationTv = findViewById(R.id.itemAmapSportDetailDurationTv)
        itemAmapSportDurationTv = findViewById(R.id.itemAmapSportDurationTv)
        itemAmapSportDetailHeartTv = findViewById(R.id.itemAmapSportDetailHeartTv)
        itemAmapSportStepTv = findViewById(R.id.itemAmapSportStepTv)
        itemAmapSportDetailPeisuTv = findViewById(R.id.itemAmapSportDetailPeisuTv)
        itemAmapSportSpeedTv = findViewById(R.id.itemAmapSportSpeedTv)
        sportDetailTB?.setTitleBarListener(object : TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                ShowToast.showToastLong("图片生成中...")


            if(isGpsSport == false){
                   var bitmap = getFullScreenBitmap(amapDetailSV!!)

                val bm: Bitmap? = amapDetailSV?.let { AppUtils.getBitmapByView(it) }
                  var allBitmap = FileUtils.combineBitmap(bm, bitmap)
                lifecycleScope.launch {
                    val saveFile = bm?.let { saveFile(allBitmap, fileName) }
                    if (saveFile == true){
                        var intent = Intent(this@AmapHistorySportActivity, ImgShareActivity::class.java)
                        startActivity(intent)
                        hideWaitDialog()
                    }
                }
                return
            }


                aMap?.getMapScreenShot(object : OnMapScreenShotListener,
                    AMap.OnMapScreenShotListener {
                    override fun onMapScreenShot(bitmap: Bitmap?) {
                    }

                    override fun onMapScreenShot(bitmap: Bitmap?, status: Int) {
                        TLog.error("current thread = "+ Thread.currentThread().name)
                        if (null == bitmap) {
                            return
                        }
                        var bm: Bitmap? = amapDetailSV?.let { AppUtils.getBitmapByView(it) }
                        var allBitmap = FileUtils.combineBitmap(bm, bitmap)
                        lifecycleScope.launch {
                            val saveFile = saveFile(allBitmap, fileName)
                            if (saveFile){
                                var intent = Intent(this@AmapHistorySportActivity, ImgShareActivity::class.java)
                                startActivity(intent)
                                hideWaitDialog()
                            }
                        }
                    }
                })
            }

            override fun onActionClick() {
            }
        })
        hartsHrr = harts_hrr
        llHeart.visibility = View.GONE
        itemAmapSportDetailHeartTv?.text = HelpUtil.getSpan("--", "次/分钟", 11)
//        hartsHrr.visibility=View.GONE
//        pieChart.visibility=View.GONE
    }

    private fun chartView() {
        hartsHrr.description.isEnabled = false
        hartsHrr.legend.isEnabled = false  //色块不显示
        hartsHrr.setScaleEnabled(false)//设置比列启动
        hartsHrr.isScaleYEnabled = false
        hartsHrr.setTouchEnabled(false) //不可触摸
        hartsHrr.viewPortHandler.setMaximumScaleX(1f)
        var xAxis: XAxis
        run {
            xAxis = hartsHrr.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)//设置网格线
            xAxis.setDrawLabels(false)
            xAxis.textColor = R.color.red
            xAxis.axisLineColor = Color.WHITE

//            xAxis.axisMaximum = number.toFloat()
//            xAxis.axisMinimum=0f
//            xAxis.granularity = (number/3).toFloat() // 想 弄成 4 hour
            xAxis.setLabelCount(3, true)
            //  xAxis.mLabelHeight =20
            //  xAxis.mLabelRotatedHeight=20
        }
        var leftAxis: YAxis
        run {
            leftAxis = hartsHrr.axisLeft
            leftAxis.isEnabled = false
            leftAxis.axisMinimum = 70f
            // leftAxis.axisMaximum = 220f
            leftAxis.setDrawZeroLine(false)
        }
        var rightAxis: YAxis
        run {
            rightAxis = hartsHrr.axisRight
            rightAxis.axisMinimum = 70f
            // rightAxis.axisMaximum = 220f
            rightAxis.axisLineColor = Color.WHITE
            rightAxis.zeroLineColor = resources.getColor(R.color.color_view)
            rightAxis.gridColor = resources.getColor(R.color.color_view)
            //  rightAxis.granularity = 40f
            rightAxis.setDrawZeroLine(false)
        }

        setDataView(mList)
        hartsHrr.invalidate()
    }

    private var values =
        ArrayList<Entry>()
    private var pieChartStatus = false
    private fun setDataView(mList: MutableList<Int>) {
        // TLog.error("长度" + mList.size)
        pieChartStatus = false
        values = ArrayList()
        var setList: ArrayList<Int> = arrayListOf()
        var deviceList: ArrayList<Int> = arrayListOf()
        var pieList: ArrayList<Float> = arrayListOf(0F, 0F, 0F, 0F, 0F)
        var num = 0
        var iFZero = 0    //30个平均为0排除
        var avgHeart = 0L //平均值
        mList.forEachIndexed { index, i ->
            run {
                num += i
                if (i <= 0)
                    iFZero++
                else {
                    avgHeart += i
                }
                if ((index + 1) % 10 == 0) {//6个数组平分一组
                    var size =  //当为0时特殊处理
                        if ((10 - iFZero) <= 0)
                            1
                        else
                            (10 - iFZero)
                    var heart = num / size
                    setList.add(heart)
                    if (heart > 0) {
                        deviceList.add(heart)
                    }
                    num = 0
                    iFZero = 0
                }
                when (i) {
                    in 80..109 -> {
                        pieChartStatus = true
                        pieList[4] += 1f
                    }
                    in 110..129 -> {
                        pieChartStatus = true
                        pieList[3] += 1f
                    }
                    in 130..159 -> {
                        pieChartStatus = true
                        pieList[2] += 1f
                    }
                    in 160..179 -> {
                        pieChartStatus = true
                        pieList[1] += 1f
                    }
                    in 180..219 -> {
                        pieChartStatus = true
                        pieList[0] += 1f
                    }
                }
            }
        }
        var curveList = 0
        var size = 0
        var maxHeart = 0
        setList.forEach {
            curveList += it
            if (it > 40) {
                size++
                if (maxHeart < it)
                    maxHeart = it
            }
        }
        if (size <= 0)
            size = 1
        tvAvgHeart.text = "" + (curveList / size)   //转int不保留小数
        tvMaxHeart.text = maxHeart.toString()
        itemAmapSportDetailHeartTv?.text = HelpUtil.getSpan("" + (curveList / size), "次/分钟", 11)

        /**
         * values装取所有数据然后再一次性 画完
         * ios那边告诉的结果是 values装数据的时候分段装 >0的装了直接画 画完了 在小于0的时候
         * 清空数据 然后直接画点不画任何东西 在for循环中画 而不是for循环 添加完了画
         */
        var data = LineData()
        setList.forEachIndexed { index, i ->
            values.add(Entry(index.toFloat(), i.toFloat()))
        }
        if (values.isNotEmpty()) {
            var set1 = LineDataSet(values, "")
            set1.color = resources.getColor(R.color.color_heart)
            set1.setDrawFilled(true)
            set1.fillColor = resources.getColor(R.color.color_heart_view)
            set1.fillFormatter =
                IFillFormatter { _, _ -> hartsHrr.axisLeft.axisMinimum }
            set1.setDrawCircles(false)//设置画圆点
            set1.setDrawValues(false)//设置缩放一定程度以后的展示文字
            set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            data = LineData(set1)
        }
        hartsHrr.data = data
//        }
        var mDataPieCount = 0F
        if (deviceList.size > 0 && pieChartStatus) {
            for (i in 0 until pieList.size) {
                pieList[i] = (pieList[i].toLong() / 60).toFloat()
                mDataPieCount += pieList[i]
            }
            if (mDataPieCount > 0) {
                setDataPieView(pieList,false)
            } else{

                val emptyPieList: ArrayList<Float> = arrayListOf(0F, 0F, 0F, 0F, 1F)
                setDataPieView(emptyPieList,true)
                //  pieChart.visibility = View.GONE
            }

        }else{
            val emptyPieList: ArrayList<Float> = arrayListOf(0F, 0F, 0F, 0F, 1F)
            setDataPieView(emptyPieList,true)
        }
    }

    private fun pieView() {
        pieChart.setNoDataText("没有有效数据")
        pieChart.setUsePercentValues(true)//设置百分比
        pieChart.description.isEnabled = false
        pieChart.legend.form = Legend.LegendForm.CIRCLE
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.setCenterTextSize(18f)
        pieChart.centerText = " 心 率 \n 区 间 "
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(70)
        pieChart.holeRadius = 70f
        pieChart.transparentCircleRadius = 70f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = true
        pieChart.setDrawEntryLabels(false)
        pieChart.animateY(1000, Easing.EaseInOutQuad)
        val l: Legend = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.yEntrySpace = 10f
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)
    }

    private fun setDataPieView(mList: MutableList<Float>,isEmpty : Boolean) {
        val entries = ArrayList<PieEntry>()
        val typeList = arrayListOf("无氧极限", "无氧耐力", "有氧耐力", "燃烧脂肪", "热身放松")
        val mListColor = arrayListOf(
            R.color.color_blood_oxygen_severe_hypoxia,
            R.color.color_blood_pressure_two,
            R.color.color_blood_pressure_one,
            R.color.color_main_green,
            R.color.color_blood_pressure_low
        )
        TLog.error("数据++" + Gson().toJson(mList))

        for (i in 0 until mList.size) {
//            if (mList[i] != 0F)
            entries.add(
                PieEntry(
                    mList[i],
                    typeList[i] + "    " + (if(isEmpty)"0" else mList[i].toLong()) + "分钟"
                )
            )
        }


        val dataSet = PieDataSet(entries, " ")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        val colors = ArrayList<Int>()
        for (i in 0 until mList.size) {
            // if (mList[i] > 0)
            colors.add(resources.getColor(mListColor[ i]))
        }
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setDrawValues(false)//文字不展示
        pieChart.data = data
        pieChart.invalidate()
    }

    //
    //    }
    //    //开始轨迹纠偏
    //    private void startTrace() {
    //        if (mOverlayList.containsKey(mSequenceLineID)) {
    //            TraceOverlay overlay = mOverlayList.get(mSequenceLineID);
    //            overlay.zoopToSpan();
    //            int status = overlay.getTraceStatus();
    //            String tipString = "";
    //            if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
    //                tipString = "该线路轨迹纠偏进行中...";
    //                //setDistanceWaitInfo(overlay);
    //            } else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
    //                //setDistanceWaitInfo(overlay);
    //                tipString = "该线路轨迹已完成";
    //            } else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
    //                tipString = "该线路轨迹失败";
    //            } else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
    //                tipString = "该线路轨迹纠偏已经开始";
    //            }
    //            Toast.makeText(this.getApplicationContext(), tipString,
    //                    Toast.LENGTH_SHORT).show();
    //            return;
    //        }
    //
    //        TraceOverlay mTraceOverlay = new TraceOverlay(aMap);
    //        mOverlayList.put(mSequenceLineID, mTraceOverlay);
    //        List<LatLng> mapList = getLanList(traceLocationList);
    //        mTraceOverlay.setProperCamera(mapList);
    ////        mResultShow.setText(mDistanceString);
    ////        mLowSpeedShow.setText(mStopTimeString);
    //        mTraceClient = new LBSTraceClient(AmapHistorySportActivity.this);
    //        mTraceClient.queryProcessedTrace(mSequenceLineID, getTraceLocationList(),
    //                mCoordinateType, this);
    //
    //
    //    }
    //    private List<TraceLocation> getTraceLocationList() {
    //        List<TraceLocation> lt = new ArrayList<>();
    //        for (AmapTraceLocation at : traceLocationList) {
    //            TraceLocation tt = new TraceLocation(at.getLatitude(), at.getLongitude(),
    //                    at.getBearing(), at.getSpeed(), at.getTime());
    //            lt.add(tt);
    //        }
    //        return lt;
    //
    //
    //    }
    //    private List<LatLng> getLanList(List<AmapTraceLocation> tl) {
    //        List<LatLng> latLngList = new ArrayList<>();
    //        for (AmapTraceLocation traceLocation : tl) {
    //            latLngList.add(new LatLng(traceLocation.getLatitude(), traceLocation.getLongitude()));
    //        }
    //        return latLngList;
    //    }
    //设置属性
    private fun setUpMap() {
//        String language = Locale.getDefault().getLanguage();
//
//        aMap.setMapLanguage(language.equals("zh") ? AMap.CHINESE : AMap.ENGLISH);
        // 自定义系统定位小蓝点
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationIcon(
            BitmapDescriptorFactory
                .fromResource(R.mipmap.gps_point)
        ) // 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK) // 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180)) // 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f) // 设置圆形的边框粗细
        aMap!!.myLocationStyle = myLocationStyle
        aMap!!.setLocationSource(this) // 设置定位监听
        aMap!!.uiSettings.isZoomControlsEnabled = false  //缩放按钮
        aMap!!.uiSettings.isMyLocationButtonEnabled = false // 设置默认定位按钮是否显示
        aMap!!.isMyLocationEnabled = true // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap!!.moveCamera(CameraUpdateFactory.zoomTo(3f))
    }

    override fun onResume() {
        super.onResume()
        if (amapMapView != null) amapMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (amapMapView != null) amapMapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (amapMapView != null) amapMapView!!.onDestroy()
    }

    //定位成功回调
    override fun onLocationChanged(aMapLocation: AMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener!!.onLocationChanged(aMapLocation)
            val latLng = LatLng(aMapLocation.latitude, aMapLocation.longitude)
            // aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }
    }

    //激活定位
    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener) {
        mListener = onLocationChangedListener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(this@AmapHistorySportActivity)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            mlocationClient!!.setLocationListener(this)
            //定位参数
            mLocationOption!!.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mLocationOption!!.isNeedAddress = true
            mLocationOption!!.interval = (10 * 1000).toLong()
            mLocationOption!!.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
            mlocationClient!!.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //mlocationClient.stopLocation();
            //mlocationClient.startLocation();
        }
    }

    //销毁定位
    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient!!.stopLocation()
            mlocationClient!!.onDestroy()
        }
        mlocationClient = null
    }

    //轨迹纠偏失败
    override fun onRequestFailed(lineID: Int, s: String) {
        if (mOverlayList.containsKey(lineID)) {
            val overlay = mOverlayList[lineID]
            overlay!!.traceStatus = TraceOverlay.TRACE_STATUS_FAILURE
            //wsetDistanceWaitInfo(overlay);
        }
    }

    //轨迹纠偏进行中
    override fun onTraceProcessing(lineID: Int, i1: Int, list: List<LatLng>) {
        if (list == null) return
        if (mOverlayList.containsKey(lineID)) {
            val overlay = mOverlayList[lineID]
            overlay!!.traceStatus = TraceOverlay.TRACE_STATUS_PROCESSING
            overlay.add(list)
        }
    }

    //轨迹纠偏结束
    override fun onFinished(
        lineID: Int, linepoints: List<LatLng>, distance: Int,
        watingtime: Int
    ) {
        Toast.makeText(
            this.applicationContext, "onFinished",
            Toast.LENGTH_SHORT
        ).show()
        if (mOverlayList.containsKey(lineID)) {
            val overlay = mOverlayList[lineID]
            overlay!!.traceStatus = TraceOverlay.TRACE_STATUS_FINISH
            overlay.distance = distance
            overlay.waitTime = watingtime
            //setDistanceWaitInfo(overlay);
        }
    }

    private fun typeSport(type: Int): String {
        if (type == 1) return "步行"
        if (type == 2) return "跑步"
        return if (type == 3) "骑行" else "步行"
    }

    private fun typeSportImg(type: Int): Int {
        if (type == 1) return R.mipmap.icon_amap_walk
        if (type == 2) return R.mipmap.icon_amap_run
        return if (type == 3) R.mipmap.icon_amap_ride else R.mipmap.icon_amap_walk
    }

    private suspend fun saveFile(photoBitmap:Bitmap,path:String):Boolean{
       return suspendCoroutine {
            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val photoFile: File = File(path, "mapImgShare.png")
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(photoFile)
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                        fileOutputStream.flush()
                    }
                }
                it.resume(true)
            } catch (e: FileNotFoundException) {
                photoFile.delete()
                e.printStackTrace()
                it.resumeWithException(e)
            } catch (e: IOException) {
                photoFile.delete()
                e.printStackTrace()
                it.resumeWithException(e)
            } finally {
                try {
                    fileOutputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    /**
     * 获取长截图
     *
     * @return
     */
    fun getFullScreenBitmap(scrollVew: NestedScrollView): Bitmap? {
        var h = 0
        val bitmap: Bitmap
        for (i in 0 until scrollVew.childCount) {
            h += scrollVew.getChildAt(i).height
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(
            scrollVew.width, h,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        scrollVew.draw(canvas)

        //获取顶部布局的bitmap
        val head = Bitmap.createBitmap(
            shareTmpView.getWidth(), shareTmpView.getHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvasHead = Canvas(head)
        canvasHead.drawColor(Color.WHITE)
        shareTmpView.draw(canvasHead)
        val newbmp = Bitmap.createBitmap(scrollVew.width, h + head.height, Bitmap.Config.ARGB_8888)
        val cv = Canvas(newbmp)
        cv.drawBitmap(head, 0f, 0f, null) // 在 0，0坐标开始画入headBitmap
        cv.drawBitmap(bitmap, 0f, head.height.toFloat(), null) // 在 0，headHeight坐标开始填充课表的Bitmap
        cv.save() // 保存
        cv.restore() // 存储
        //回收
        head.recycle()
        return newbmp
        // 测试输出
       // return FileUtil.writeImage(newbmp, FileUtil.getImageFile(FileUtil.getPhotoFileName()), 100)
    }

}