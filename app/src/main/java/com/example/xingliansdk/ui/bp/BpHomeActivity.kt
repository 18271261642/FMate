package com.example.xingliansdk.ui.bp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.BloodPressureHistoryAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.room.BloodPressureHistoryBean
import com.example.xingliansdk.dialog.OnCommDialogClickListener
import com.example.xingliansdk.dialog.PromptCheckBpDialog
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureViewModel
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.view.BpMeasureView
import com.example.xingliansdk.view.DateUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_card_edit.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.*
import kotlinx.android.synthetic.main.activity_new_bp_home_layout.titleBar
import kotlinx.android.synthetic.main.item_blood_pressure_index.*
import java.util.*
import kotlin.random.Random

/**
 * 新版血压首页面
 * Created by Admin
 *Date 2022/5/7
 */
class BpHomeActivity : BaseActivity<BloodPressureViewModel>(),View.OnClickListener{


    //提示校准血压的dialog
    private var promptBpDialog : PromptCheckBpDialog ?= null

    //手动输入的血压列表adapter
    private lateinit var  mBloodPressureHistoryAdapter: BloodPressureHistoryAdapter
    var bpList: ArrayList<BloodPressureHistoryBean> = arrayListOf()

    override fun layoutId(): Int {
      return R.layout.activity_new_bp_home_layout
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        bpHomeInputLayout.setOnClickListener(this)
        bpHomeMeasureLayout.setOnClickListener(this)
        imgReplicate.setOnClickListener(this)

        initData();


        showPromptDialog()

        getDateBpData(DateUtil.getCurrDate())
    }


    private fun initData(){
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        ryBloodPressure.layoutManager = mLinearLayoutManager
        mBloodPressureHistoryAdapter = BloodPressureHistoryAdapter(bpList)
        ryBloodPressure.adapter = mBloodPressureHistoryAdapter


        //initLinChart()
    }



    override fun createObserver() {
        super.createObserver()
        //返回指定日期的血压数据

        mViewModel.resultGet.observe(this){
            TLog.error("----获取血压返回="+Gson().toJson(it))
            if(it.list.isEmpty()){  //没有测量记录，提示校准
                showPromptDialog()
                return@observe
            }


        }

    }

    //获取指定日期的血压记录
    private fun getDateBpData(dayStr : String){
        mViewModel.getBloodPressure(dayStr)
    }





    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.imgReplicate -> {
                    if(llBloodPressureIndex.visibility==View.GONE)
                    {
                        imgReplicate.rotation=270f
                        llBloodPressureIndex.visibility=View.VISIBLE
                    }
                    else
                    {
                        imgReplicate.rotation=90f
                        llBloodPressureIndex.visibility=View.GONE
                    }
                }
                R.id.bpHomeInputLayout->{   //输入
                    showInputDialog()
                }
                R.id.bpHomeMeasureLayout->{ //测量
                    startActivity(Intent(this,MeasureNewBpActivity::class.java))
                }
            }
        }
    }


    private fun showPromptDialog(){
        if(promptBpDialog == null){
            promptBpDialog = PromptCheckBpDialog(this,R.style.edit_AlertDialog_style)
        }
        promptBpDialog!!.show()
        promptBpDialog!!.setCancelable(false)
        promptBpDialog!!.setOnCommDialogClickListener(object : OnCommDialogClickListener{
            override fun onConfirmClick(code: Int) {
                promptBpDialog!!.dismiss()
                startActivity(Intent(this@BpHomeActivity,BpCheckActivity::class.java))
            }

            override fun onCancelClick(code: Int) {

            }

        })
    }


    var mDiastolic = 0
    var mEdtSystolic = 0
    var time = System.currentTimeMillis()
    private fun showInputDialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_blood_pressure
            dimAmount = 0.3f
            isFullHorizontal = true
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_CENTER
            animStyle = R.style.BottomTransAlphaADAnimation
            convertListenerFun { holder, dialog ->
                var tvTitle = holder.getView<TextView>(R.id.tv_title)
                var edtDiastolic = holder.getView<EditText>(R.id.edt_diastolic)
                var edtSystolic = holder.getView<EditText>(R.id.edt_systolic)
                var dialogCancel = holder.getView<ImageView>(R.id.imgCancel)
                var dialogSet = holder.getView<TextView>(R.id.dialog_set)
                tvTitle?.text = "记录血压"
                dialogSet?.setOnClickListener {
                    mDiastolic = 0
                    mEdtSystolic = 0
                    time = System.currentTimeMillis()
                    if (edtDiastolic?.text.toString().isNotEmpty())
                        mDiastolic = edtDiastolic?.text.toString().toInt()
                    if (edtSystolic?.text.toString().isNotEmpty())
                        mEdtSystolic = edtSystolic?.text.toString().toInt()
                    if (mEdtSystolic <= mDiastolic) {
                        ShowToast.showToastLong("收缩压不能小于舒张压!!")
                        return@setOnClickListener
                    }
                    if(mEdtSystolic>250||mDiastolic<40)
                    {
                        ShowToast.showToastLong("输入有误，请输入正确的血压值")
                        return@setOnClickListener
                    }
                    //  BleWrite.writeBloodPressureCalibrationCall(mDiastolic, mEdtSystolic)
                    mViewModel.setBloodPressure(this@BpHomeActivity,time/1000,mEdtSystolic,mDiastolic)
//                    sDao.insert(
//                        BloodPressureHistoryBean(
//                            time/1000, 0, 1, mEdtSystolic, mDiastolic,
//                            DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM, time)
//                        )
//                    )
//                    homeCard(mEdtSystolic,mDiastolic )
//                    update()
                    dialog.dismiss()
                }
                dialogCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }


    private fun initLinChart(){
        bpHomeLinChartView.description.isEnabled = false
        bpHomeLinChartView.setDrawBorders(false)
        bpHomeLinChartView.axisRight.setDrawAxisLine(false)
        bpHomeLinChartView.axisRight.setDrawLabels(false)

        val xAxis = bpHomeLinChartView.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.parseColor("#EBEBEB")
        xAxis.setDrawAxisLine(false)

        val yLeft = bpHomeLinChartView.axisLeft
        yLeft.labelCount = 2
        yLeft.zeroLineWidth =1f
        yLeft.enableGridDashedLine(10f, 10f, 0f);
        initLinData(bpHomeLinChartView)
    }

    private fun initLinData(chart : LineChart){
        val dataSets = ArrayList<ILineDataSet>()


        val values1 = ArrayList<Entry>()

        for (i in 0..11) {
            values1.add(
                Entry(
                    i.toFloat(),
                    ((Math.random() * 65).toInt() + 40).toFloat()
                )
            )
        }

        val d1 = LineDataSet(values1, "")
        d1.lineWidth = 2.5f
        d1.circleRadius = 4.5f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.setDrawValues(false)


        val values2 = ArrayList<Entry>()

        for (i in 0..11) {
            values2.add(Entry(i.toFloat(), values1[i].y - 30))
        }

        val d2 = LineDataSet(values2, "")
        d2.lineWidth = 2.5f
        d2.circleRadius = 4.5f
        d2.highLightColor = Color.rgb(244, 117, 117)
        d2.color = ColorTemplate.VORDIPLOM_COLORS[0]
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0])
        d2.setDrawValues(false)

        val sets = ArrayList<ILineDataSet>()
        sets.add(d1)
        sets.add(d2)
        val data = LineData(sets)
        chart.data = data
        chart.invalidate()


//
//        for (z in 0..2) {
//            val values = ArrayList<Entry>()
//            for (i in 0 until 24) {
//                val `val`: Float = Random(120).nextFloat()
//                values.add(Entry(i.toFloat(), `val`.toFloat()))
//            }
//            val d = LineDataSet(values, "DataSet " + (z + 1))
//            d.lineWidth = 2.5f
//            d.circleRadius = 4f
//            val color: Int = Color.WHITE
//            d.color = color
//            d.setCircleColor(color)
//            dataSets.add(d)
//        }
//
//        // make the first DataSet dashed
//
//        // make the first DataSet dashed
////        (dataSets[0] as LineDataSet).enableDashedLine(10f, 10f, 0f)
////        (dataSets[0] as LineDataSet).setColors(*ColorTemplate.VORDIPLOM_COLORS)
////        (dataSets[0] as LineDataSet).setCircleColors(*ColorTemplate.VORDIPLOM_COLORS)
//
//        val data = LineData(dataSets)
//        chart.data = data
//        chart.invalidate()
    }
}