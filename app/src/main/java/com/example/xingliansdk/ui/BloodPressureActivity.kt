package com.example.xingliansdk.ui

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.adapter.BloodPressureHistoryAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.bean.room.BloodPressureHistoryBean
import com.example.xingliansdk.bean.room.BloodPressureHistoryDao
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.bloodPressureView.BloodPressureViewModel
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_blood_pressure.*
import kotlinx.android.synthetic.main.item_blood_pressure_index.*

import java.util.*
import kotlin.collections.ArrayList

class BloodPressureActivity :
    BaseActivity<BloodPressureViewModel>(), View.OnClickListener {
    var mList: ArrayList<BloodPressureHistoryBean> = arrayListOf()
    lateinit var sDao: BloodPressureHistoryDao
    private lateinit var  mBloodPressureHistoryAdapter: BloodPressureHistoryAdapter
    override fun layoutId(): Int {
        return R.layout.activity_blood_pressure
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                timeDialog()
            }

            override fun onActionClick() {
            }

        })
        sDao = AppDataBase.instance.getBloodPressureHistoryDao()
        TLog.error("血压数据++" + Gson().toJson(sDao.getAllByDateDesc()))
        mHomeCardBean = Hawk.get(Config.database.HOME_CARD_BEAN, HomeCardVoBean())
        var bean = intent.getSerializableExtra("bean") as HomeCardVoBean.ListDTO
        var toDayTime = System.currentTimeMillis()
//        if (bean.startTime > 0)
//            toDayTime = bean.startTime * 1000L
        XingLianApplication.setSelectedCalendar(DateUtil.getCurrentCalendar(toDayTime))
        img_left.setOnClickListener(this)
        img_right.setOnClickListener(this)
        tvAdd.setOnClickListener(this)
        imgReplicate.setOnClickListener(this)
        llBloodPressureIndex.visibility=View.GONE
        setAdapter()
        // update()
        setTitleDateData()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.msgGet.observe(this){
            update()
        }
        mViewModel.resultGet.observe(this){ it ->
            TLog.error("获取数据"+Gson().toJson(it))
            var mList=it
            if(mList.list==null||mList.list.size<=0)
            {
                update()
                return@observe
            }
            var mBloodPressureVoList=mList.list
            mBloodPressureVoList.forEach {bean->

                TLog.error("时间++"+ DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM, bean.stampCreateTime*1000L))
                sDao.insert(
                BloodPressureHistoryBean(
                    bean.stampCreateTime, 0, 0, bean.systolicPressure, bean.diastolicPressure,
                    DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM, bean.stampCreateTime*1000L)
                ))
            }
            update()
        }
        mViewModel.msgSet.observe(this){
            TLog.error("设置失败")
            update()
        }
        mViewModel.resultSet.observe(this){
            TLog.error("设置成功")
            if(mDiastolic!=0&&mEdtSystolic!=0) {
                TLog.error("time++"+time+"  mEdtSystolic=="+mEdtSystolic+"  mDiastolic=="+mDiastolic)
                sDao.insert(
                    BloodPressureHistoryBean(
                        time/1000, 0, 0, mEdtSystolic, mDiastolic,
                        DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM, time)
                    )
                )

            }
            update()
            mDiastolic =0
            mEdtSystolic = 0
            time = 0
        }
        mViewModel.resultDelete.observe(this){
            hideWaitDialog()
        }
        mViewModel.msgDelete.observe(this){
            hideWaitDialog()
        }


    }
    private fun update() {
        mList = timeDialog?.let {
            DateUtil.getDate(DateUtil.YYYY_MM_DD, it) }?.let {
            sDao.getDayBloodPressureHistory(
                it
            )
        } as ArrayList<BloodPressureHistoryBean>
        TLog.error("update=="+Gson().toJson(mList))
        mBloodPressureHistoryAdapter.data.clear()
        mBloodPressureHistoryAdapter.addData(mList)
        mBloodPressureHistoryAdapter.notifyDataSetChanged()
        if (mList.isNullOrEmpty()) {
            tvBloodPressureNum.text = "--"
            tvDiastolicBloodPressureNum.text = "--"
        } else {
            tvBloodPressureNum.text = mList[0].systolicBloodPressure.toString()
            tvDiastolicBloodPressureNum.text =
                mList[0].diastolicBloodPressure.toString()
        }
    }

    private fun setAdapter() {
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        ryBloodPressure.layoutManager = mLinearLayoutManager
        mBloodPressureHistoryAdapter = BloodPressureHistoryAdapter(mList)
        ryBloodPressure.adapter = mBloodPressureHistoryAdapter
        mBloodPressureHistoryAdapter.setOnDelListener(object :BloodPressureHistoryAdapter.onSwipeListener{
            override fun onDel(pos: Int) {
                TLog.error("点击删除")
                if(pos>=0)
                {
                    if(!HelpUtil.netWorkCheck(this@BloodPressureActivity)) {
                        if(mBloodPressureHistoryAdapter.data[pos].type==0) {
                            ShowToast.showToastLong(getString(R.string.err_network_delete))
                            return
                        }
                    }
                    showWaitDialog("删除血压中...")
                    var value = HashMap<String, String>()
                    value["createTime"] = mBloodPressureHistoryAdapter.data[pos].startTime.toString()
                    sDao.deleteTime(mBloodPressureHistoryAdapter.data[pos].startTime)
                    mBloodPressureHistoryAdapter.notifyItemRemoved(pos)

                    if(HelpUtil.netWorkCheck(this@BloodPressureActivity)
                        &&mBloodPressureHistoryAdapter.data[pos].type==0) {
                        TLog.error("删除")
                        mViewModel.deleteBloodPressure(value)
                    }
                    else
                        hideWaitDialog()
                    update()
                    if (mBloodPressureHistoryAdapter.data.size > 0)
                        homeCard(mBloodPressureHistoryAdapter.data[0].systolicBloodPressure,
                            mBloodPressureHistoryAdapter.data[0].diastolicBloodPressure)//如果最新一个被删除的时候 要更新首页
                    else
                        homeCard(0,0)//如果最新一个被删除的时候 要更新首页

                }
            }

            override fun onClick(pos: Int) {
                TLog.error("==mlsit=="+Gson().toJson(mList[pos]))
                  mDiastolic = mList[pos].diastolicBloodPressure
                  mEdtSystolic = mList[pos].systolicBloodPressure
                  time = mList[pos].startTime*1000
                mViewModel.setBloodPressure(this@BloodPressureActivity,mList[pos].startTime,mList[pos].systolicBloodPressure
                    ,mList[pos].diastolicBloodPressure)
            }
        })

    }

    var timeDialog: Long? = System.currentTimeMillis()//默认为当天时间
    private fun setTitleDateData() {
        val calendar: Calendar? = XingLianApplication.getSelectedCalendar()
        TLog.error("calendar++${calendar?.timeInMillis}")
        timeDialog = calendar?.timeInMillis
        val date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar)
        if (DateUtil.equalsToday(date)) {
          //  tvTypeTime.setText(R.string.title_today)

            tvAdd.visibility=View.VISIBLE
            img_right.visibility = View.INVISIBLE
        } else {

            img_right.visibility = View.VISIBLE
            tvAdd.visibility=View.GONE
        }
        tvTypeTime.text = date
        TLog.error("timeDialog==" + (timeDialog!! / 1000000))
        TLog.error("DateUtil==" + (DateUtil.getCurrentDate() / 1000000))
        update()
        mViewModel.getBloodPressure(DateUtil.getDate(DateUtil.YYYY_MM_DD,timeDialog!!))
     //   //   calendar?.timeInMillis?.div(1000)?.let { getHeart(it) }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_left -> {
                XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, -1)
                imgReplicate.rotation=90f
                llBloodPressureIndex.visibility=View.GONE
                setTitleDateData()

            }
            R.id.img_right -> {
                XingLianApplication.getSelectedCalendar()?.add(Calendar.DAY_OF_MONTH, +1)
                imgReplicate.rotation=90f
                llBloodPressureIndex.visibility=View.GONE
                setTitleDateData()
            }
            R.id.tvAdd -> {
                dialog()
            }
            R.id.imgReplicate->
            {
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

        }
    }
    var mDiastolic = 0
    var mEdtSystolic = 0
    var time = System.currentTimeMillis()
    private fun dialog() {
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
                    mViewModel.setBloodPressure(this@BloodPressureActivity,time/1000,mEdtSystolic,mDiastolic)
                    sDao.insert(
                        BloodPressureHistoryBean(
                            time/1000, 0, 1, mEdtSystolic, mDiastolic,
                            DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM, time)
                        )
                    )
                    homeCard(mEdtSystolic,mDiastolic )
                    update()
                    dialog.dismiss()
                }
                dialogCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }

    private var calendar: Calendar? = null
    private var toDay = DateUtil.getCurrentDate()
    private fun timeDialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_calendar
            dimAmount = 0.3f
            isFullHorizontal = false
            isFullVerticalOverStatusBar = false
            animStyle = R.style.AlphaEnterExitAnimation
            convertListenerFun { holder, dialog ->
                var calenderView = holder.getView<CalendarView>(R.id.calenderView)
                timeDialog?.let { it1 ->
                    TLog.error("it1==${it1}")
                    calenderView?.setDate(it1)
                }
                calenderView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
                    if (calendar == null) {
                        calendar = Calendar.getInstance()
                    }
                    calendar?.set(year, month, dayOfMonth, 0, 0)//不设置时分不行
                    var calendarTime = calendar?.timeInMillis
                    if (calendarTime!! > toDay) {
                        TLog.error("calendarTime+= $calendarTime  DateUtil.getCurrentDate()++$toDay")
                        ShowToast.showToastLong("不可选择大于今天的日期")
                        return@setOnDateChangeListener
                    }
                    XingLianApplication.getSelectedCalendar()?.timeInMillis = calendarTime!!
                    setTitleDateData()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }
    private fun homeCard( Systolic:Int,Diastolic:Int){
        if (mHomeCardBean.list != null && mHomeCardBean.list.size > 0) {
            var cardList = mHomeCardBean.list
            cardList.forEachIndexed { index, addCardDTO ->
                if (addCardDTO.type == 5) {
                    mHomeCardBean.list[index].endTime =
                        System.currentTimeMillis() / 1000
                    if(Systolic==0&&Diastolic==0) {
                        mHomeCardBean.list[index].data = ""
                    }
                    else {
                        mHomeCardBean.list[index].data =
                            "${Systolic}/${Diastolic}"
                    }
                    mHomeCardBean.list[index].describe =
                        DateUtil.getDate(DateUtil.MM_AND_DD,System.currentTimeMillis())
                    Hawk.put(Config.database.HOME_CARD_BEAN, mHomeCardBean)
                    SNEventBus.sendEvent(Config.eventBus.BLOOD_PRESSURE_RECORD)
                }
            }
        }
    }
}