package com.app.fmate.ui.fragment.map.newmap

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
import com.app.fmate.Config.exercise.MILE
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.bean.db.AmapSportBean
import com.app.fmate.ui.fragment.map.share.ImgShareActivity
import com.app.fmate.utils.*
import com.app.fmate.view.CusMapContainerView
import com.app.fmate.view.DateUtil
import com.app.fmate.widget.TitleBarLayout.TitleBarListener
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
 * ????????????????????????????????????????????????????????????
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

    //??????
    private var itemAmapSportTypeTv: TextView? = null

    //??????
    private var itemSportDetailDistanceTv: TextView? = null

    //??????
    private var itemSportDetailDateTv: TextView? = null

    //????????????
    private var itemAmapSportDetailDurationTv: TextView? = null

    //??????
    private var itemAmapSportDurationTv: TextView? = null

    //????????????
    private var itemAmapSportDetailHeartTv: TextView? = null

    //??????
    private var itemAmapSportStepTv: TextView? = null

    //????????????
    private var itemAmapSportDetailPeisuTv: TextView? = null

    //????????????
    private var itemAmapSportSpeedTv: TextView? = null

    //???????????????GPS?????????GPS????????????????????????
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

            //??????????????????????????????
            val startLng = latLngList[0]
            val markerOptions =
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(typeSportImg(amapSportBean.sportType)))
                    .position(startLng)
                    .draggable(false)
            startMark = aMap!!.addMarker(markerOptions)
            startMark?.isDraggable = false

            //???????????????
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
            TLog.error("??????????????????????????????++" + Gson().toJson(amapSportBean))
            TLog.error("??????++" + amapSportBean.heartArrayStr)

            val latStr = amapSportBean.latLonArrayStr


            if (amapSportBean.heartArrayStr.isNotEmpty()) {
                mList = Gson().fromJson(
                    amapSportBean.heartArrayStr,
                    ArrayList::class.java
                ) as ArrayList<Int>
                TLog.error("mlist==" + mList.size)
                var number = 0
                if (mList.isNotEmpty() || mList.size > 0) {
                    if (Collections.max(mList) > 40) //???????????????????????????
                    {
                        number = Math.floor(mList.size / 60.0).roundToInt()
                        TLog.error("????????????=" + number)
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
            TLog.error("??????===" + amapSportBean.currentSteps)
            itemAmapSportTypeTv!!.text = typeSport(amapSportBean.sportType)
            var dis=Utils.divi(amapSportBean.distance.toDouble(),1000.0,2)
            var unit=resources.getString(R.string.unit_km)
            var pace=amapSportBean.pace.toDouble()
            var paceStr = "--"
            var  averageSpeed=  amapSportBean.averageSpeed.toDouble() * 3600 / 1000
            if(userInfo.userConfig.distanceUnit==1) {
                dis = Utils.mul(dis, MILE, 2)
                unit=resources.getString(R.string.unit_mile)
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
            itemAmapSportDurationTv!!.text = HelpUtil.getSpan(amapSportBean.calories, resources.getString(R.string.string_unit_kcal), 11)
            itemAmapSportStepTv?.text = HelpUtil.getSpan(
                amapSportBean.currentSteps.toString(),
                resources.getString(R.string.unit_steps),
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
                    "$unit/"+resources.getString(R.string.string_hour),
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
                ShowToast.showToastLong("???????????????...")


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
        itemAmapSportDetailHeartTv?.text = HelpUtil.getSpan("--", resources.getString(R.string.string_time_minute), 11)
//        hartsHrr.visibility=View.GONE
//        pieChart.visibility=View.GONE
    }

    private fun chartView() {
        hartsHrr.description.isEnabled = false
        hartsHrr.legend.isEnabled = false  //???????????????
        hartsHrr.setScaleEnabled(false)//??????????????????
        hartsHrr.isScaleYEnabled = false
        hartsHrr.setTouchEnabled(false) //????????????
        hartsHrr.viewPortHandler.setMaximumScaleX(1f)
        var xAxis: XAxis
        run {
            xAxis = hartsHrr.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)//???????????????
            xAxis.setDrawLabels(false)
            xAxis.textColor = R.color.red
            xAxis.axisLineColor = Color.WHITE

//            xAxis.axisMaximum = number.toFloat()
//            xAxis.axisMinimum=0f
//            xAxis.granularity = (number/3).toFloat() // ??? ?????? 4 hour
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
        // TLog.error("??????" + mList.size)
        pieChartStatus = false
        values = ArrayList()
        var setList: ArrayList<Int> = arrayListOf()
        var deviceList: ArrayList<Int> = arrayListOf()
        var pieList: ArrayList<Float> = arrayListOf(0F, 0F, 0F, 0F, 0F)
        var num = 0
        var iFZero = 0    //30????????????0??????
        var avgHeart = 0L //?????????
        mList.forEachIndexed { index, i ->
            run {
                num += i
                if (i <= 0)
                    iFZero++
                else {
                    avgHeart += i
                }
                if ((index + 1) % 10 == 0) {//6?????????????????????
                    var size =  //??????0???????????????
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
        tvAvgHeart.text = "" + (curveList / size)   //???int???????????????
        tvMaxHeart.text = maxHeart.toString()
        itemAmapSportDetailHeartTv?.text = HelpUtil.getSpan("" + (curveList / size), resources.getString(R.string.string_time_minute), 11)

        if(curveList/size<=0){
            llHeart.visibility = View.GONE
        }

        /**
         * values???????????????????????????????????? ??????
         * ios???????????????????????? values??????????????????????????? >0?????????????????? ????????? ?????????0?????????
         * ???????????? ???????????????????????????????????? ???for???????????? ?????????for?????? ???????????????
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
            set1.setDrawCircles(false)//???????????????
            set1.setDrawValues(false)//?????????????????????????????????????????????
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
        pieChart.setNoDataText("??????????????????")
        pieChart.setUsePercentValues(true)//???????????????
        pieChart.description.isEnabled = false
        pieChart.legend.form = Legend.LegendForm.CIRCLE
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.setCenterTextSize(18f)
        pieChart.centerText = " "+resources.getString(R.string.string_heart)+" \n "+resources.getString(R.string.string_interval_2)+" "
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
        val typeList = arrayListOf(resources.getString(R.string.string_status_heart_desc5), resources.getString(R.string.string_map_ht_1),  resources.getString(R.string.string_map_ht_2), resources.getString(R.string.string_status_heart_desc2), resources.getString(R.string.string_status_heart_desc1))
        val mListColor = arrayListOf(
            R.color.color_blood_oxygen_severe_hypoxia,
            R.color.color_blood_pressure_two,
            R.color.color_blood_pressure_one,
            R.color.color_main_green,
            R.color.color_blood_pressure_low
        )
        TLog.error("??????++" + Gson().toJson(mList))

        for (i in 0 until mList.size) {
//            if (mList[i] != 0F)
            entries.add(
                PieEntry(
                    mList[i],
                    typeList[i] + "    " + (if(isEmpty)"0" else mList[i].toLong()) + resources.getString(R.string.string_minute)
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
        data.setDrawValues(false)//???????????????
        pieChart.data = data
        pieChart.invalidate()
    }

    //
    //    }
    //    //??????????????????
    //    private void startTrace() {
    //        if (mOverlayList.containsKey(mSequenceLineID)) {
    //            TraceOverlay overlay = mOverlayList.get(mSequenceLineID);
    //            overlay.zoopToSpan();
    //            int status = overlay.getTraceStatus();
    //            String tipString = "";
    //            if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
    //                tipString = "??????????????????????????????...";
    //                //setDistanceWaitInfo(overlay);
    //            } else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
    //                //setDistanceWaitInfo(overlay);
    //                tipString = "????????????????????????";
    //            } else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
    //                tipString = "?????????????????????";
    //            } else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
    //                tipString = "?????????????????????????????????";
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
    //????????????
    private fun setUpMap() {
//        String language = Locale.getDefault().getLanguage();
//
//        aMap.setMapLanguage(language.equals("zh") ? AMap.CHINESE : AMap.ENGLISH);
        // ??????????????????????????????
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationIcon(
            BitmapDescriptorFactory
                .fromResource(R.mipmap.gps_point)
        ) // ????????????????????????
        myLocationStyle.strokeColor(Color.BLACK) // ???????????????????????????
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180)) // ???????????????????????????
        // myLocationStyle.anchor(int,int)//????????????????????????
        myLocationStyle.strokeWidth(1.0f) // ???????????????????????????
        aMap!!.myLocationStyle = myLocationStyle
        aMap!!.setLocationSource(this) // ??????????????????
        aMap!!.uiSettings.isZoomControlsEnabled = false  //????????????
        aMap!!.uiSettings.isMyLocationButtonEnabled = false // ????????????????????????????????????
        aMap!!.isMyLocationEnabled = true // ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false
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

    //??????????????????
    override fun onLocationChanged(aMapLocation: AMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener!!.onLocationChanged(aMapLocation)
            val latLng = LatLng(aMapLocation.latitude, aMapLocation.longitude)
            // aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }
    }

    //????????????
    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener) {
        mListener = onLocationChangedListener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(this@AmapHistorySportActivity)
            mLocationOption = AMapLocationClientOption()
            //??????????????????
            mlocationClient!!.setLocationListener(this)
            //????????????
            mLocationOption!!.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mLocationOption!!.isNeedAddress = true
            mLocationOption!!.interval = (10 * 1000).toLong()
            mLocationOption!!.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
            mlocationClient!!.setLocationOption(mLocationOption)
            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ??????????????????????????????????????????????????????????????????2000ms?????????????????????????????????stopLocation()???????????????????????????
            // ???????????????????????????????????????????????????onDestroy()??????
            // ?????????????????????????????????????????????????????????????????????stopLocation()???????????????????????????sdk???????????????
            //mlocationClient.stopLocation();
            //mlocationClient.startLocation();
        }
    }

    //????????????
    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient!!.stopLocation()
            mlocationClient!!.onDestroy()
        }
        mlocationClient = null
    }

    //??????????????????
    override fun onRequestFailed(lineID: Int, s: String) {
        if (mOverlayList.containsKey(lineID)) {
            val overlay = mOverlayList[lineID]
            overlay!!.traceStatus = TraceOverlay.TRACE_STATUS_FAILURE
            //wsetDistanceWaitInfo(overlay);
        }
    }

    //?????????????????????
    override fun onTraceProcessing(lineID: Int, i1: Int, list: List<LatLng>) {
        if (list == null) return
        if (mOverlayList.containsKey(lineID)) {
            val overlay = mOverlayList[lineID]
            overlay!!.traceStatus = TraceOverlay.TRACE_STATUS_PROCESSING
            overlay.add(list)
        }
    }

    //??????????????????
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

    private fun typeSport(type: Int): String? {
        if (type == 1) return resources.getString(R.string.string_sport_step)
        if (type == 2) return resources.getString(R.string.string_sport_run)
        return if (type == 3) resources.getString(R.string.string_sport_cycle) else resources.getString(R.string.string_sport_step)
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
     * ???????????????
     *
     * @return
     */
    fun getFullScreenBitmap(scrollVew: NestedScrollView): Bitmap? {
        var h = 0
        val bitmap: Bitmap
        for (i in 0 until scrollVew.childCount) {
            h += scrollVew.getChildAt(i).height
        }
        // ?????????????????????bitmap
        bitmap = Bitmap.createBitmap(
            scrollVew.width, h,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        scrollVew.draw(canvas)

        //?????????????????????bitmap
        val head = Bitmap.createBitmap(
            shareTmpView.getWidth(), shareTmpView.getHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvasHead = Canvas(head)
        canvasHead.drawColor(Color.WHITE)
        shareTmpView.draw(canvasHead)
        val newbmp = Bitmap.createBitmap(scrollVew.width, h + head.height, Bitmap.Config.ARGB_8888)
        val cv = Canvas(newbmp)
        cv.drawBitmap(head, 0f, 0f, null) // ??? 0???0??????????????????headBitmap
        cv.drawBitmap(bitmap, 0f, head.height.toFloat(), null) // ??? 0???headHeight???????????????????????????Bitmap
        cv.save() // ??????
        cv.restore() // ??????
        //??????
        head.recycle()
        return newbmp
        // ????????????
       // return FileUtil.writeImage(newbmp, FileUtil.getImageFile(FileUtil.getPhotoFileName()), 100)
    }

}