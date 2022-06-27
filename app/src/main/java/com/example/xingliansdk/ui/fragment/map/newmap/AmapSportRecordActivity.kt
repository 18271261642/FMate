package com.example.xingliansdk.ui.fragment.map.newmap

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xingliansdk.Config
import com.example.xingliansdk.Constant
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.map.AmapRecordAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.db.AmapRecordBean
import com.example.xingliansdk.bean.db.AmapSportBean
import com.example.xingliansdk.bean.room.AppDataBase.Companion.instance
import com.example.xingliansdk.network.api.javaMapView.MapViewModel
import com.example.xingliansdk.network.api.javaMapView.MapVoBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.utils.RecycleViewDivider
import com.example.xingliansdk.utils.Utils
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.example.xingliansdk.widget.TitleBarLayout.TitleBarListener
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import com.shon.connector.utils.TLog.Companion.error
import kotlinx.android.synthetic.main.activity_amap_sport_record_layout.*
import java.util.*
import kotlin.collections.HashMap

/**
 * 地图运动记录,现在只查询本地数据库，后续上传后台后根据接口拿数据
 * Created by Admin
 * Date 2021/9/12
 */
class AmapSportRecordActivity : BaseActivity<MapViewModel>(), View.OnClickListener {
    private var recordRecyclerView: RecyclerView? = null
    private var list: MutableList<AmapSportBean>? = null
    private var amapRecordAdapter: AmapRecordAdapter? = null
    private var emptyTv: TextView? = null
    //返回
    private var recordTitleBackImg: ImageView? = null
    //标题
    private var recordSportTitleTv: TextView? = null
    private val resultList: MutableList<AmapRecordBean> = ArrayList()
    var typeStr = arrayOf("所有运动", "步行", "跑步", "骑行")
    var mAmapSportDao = instance.getAmapSportDao()
    //运动类型根据原因设定所有为0，走路为1，跑步2，骑行3
    private var sportType = 0
    //类型弹窗
    private var alert: AlertDialog.Builder? = null
    override fun layoutId() = R.layout.activity_amap_sport_record_layout
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(cusTitleLayout)
            .init()
        initViews()

        sportType = intent.getIntExtra("sportType", 0)
//        error("sportType==$sportType")
        if (sportType < 0)
            sportType = 0
        getList(sportType)
        recordSportTitleTv!!.text = typeStr[sportType]
        //根据类型查询数据
        //  querySaveSport(sportType)
    }

    fun getList(type: Int) {
//        showWaitDialog("数据加载中...")
        var hashMap: HashMap<String, Any> = HashMap()
        hashMap["type"] = type
        mViewModel.motionInfoGetList(hashMap)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) {
//            hideWaitDialog()
            if (it == null || it.list == null || it.list.size <= 0) {
                querySaveSport(sportType)
                return@observe
            }
            it.list.forEach { bean ->
                bean.list.forEach { childList ->
               //     TLog.error("正常数据++${childList.type}" + Gson().toJson(childList))

                    val amapSportBean = AmapSportBean()
                    amapSportBean.userId = userInfo.user.userId
                    amapSportBean.deviceMac = userInfo.user.mac
                    amapSportBean.dayDate = DateUtil.getDate(
                        DateUtil.YYYY_MM_DD,
                        childList.createTime.toLong() * 1000
                    )
                    amapSportBean.yearMonth = DateUtil.getDate(
                        DateUtil.YYYY_MM,
                        childList.createTime.toLong() * 1000
                    )
                    amapSportBean.sportType = childList.type
                    amapSportBean.mapType = 1
                    amapSportBean.currentSportTime = DateUtil.getTime(childList.motionTime.toLong())
                    amapSportBean.endSportTime = DateUtil.getDate(
                        DateUtil.YYYY_MM_DD_HH_MM_SS,
                        childList.createTime.toLong() * 1000
                    )
                    amapSportBean.currentSteps = childList.steps.toInt()
                    amapSportBean.distance = childList.distance
                    amapSportBean.calories = childList.calorie // countCalories
                    amapSportBean.averageSpeed = childList.avgSpeed //avgSpeed
                    amapSportBean.pace = childList.avgPace
                    amapSportBean.latLonArrayStr = childList.positionData
                    amapSportBean.createTime = childList.createTime.toLong() // createTime / 1000
                    amapSportBean.heartArrayStr = childList.heartRateData
                    mAmapSportDao.insert(amapSportBean)
                }
            }
            querySaveSport(sportType)
        }
        mViewModel.msg.observe(this) {
            hideWaitDialog()
            querySaveSport(sportType)
        }
    }

    private fun initViews() {
        recordTitleBackImg = findViewById(R.id.recordTitleBackImg)
        recordSportTitleTv = findViewById(R.id.recordSportTitleTv)
        emptyTv = findViewById(R.id.emptyTv)
        recordRecyclerView = findViewById(R.id.amapRecordRecyclerView)
        recordTitleBackImg?.setOnClickListener(this)
        recordSportTitleTv?.setOnClickListener(this)
        emptyTv?.setOnClickListener(this)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recordRecyclerView?.layoutManager = linearLayoutManager
//        recordRecyclerView?.addItemDecoration(
//            RecycleViewDivider(
//                this,
//                LinearLayoutManager.HORIZONTAL,
//                1,
//                resources.getColor(R.color.color_view)
//            )
//        )
        list = ArrayList()
        amapRecordAdapter = AmapRecordAdapter(resultList, this, sportType)
        recordRecyclerView?.adapter = amapRecordAdapter
    }

    //查询数据
    private fun querySaveSport(type: Int) {
        resultList.clear()
        list!!.clear()
        try {
            val loginBean = Hawk.get<LoginBean>(Config.database.USER_INFO)
//            error("LoginBean==" + Gson().toJson(loginBean))
            if (loginBean == null) {
                showEmpty()
                return
            } else {
                emptyTv!!.visibility = View.GONE
            }
            val userId = loginBean.user.userId
            if (userId == null) {
                showEmpty()
                return
            }
//            error("userId==$userId")
            val sportBeanList = if (type == 1 || type == 2 || type == 3) {
                mAmapSportDao.getRoomTime(type)
            } else
                mAmapSportDao.getAllList()
//            error("sportBeanList==" + Gson().toJson(sportBeanList))
            if (sportBeanList == null || sportBeanList.size <= 0) {
                showEmpty()
                return
            }
            analyseData(sportBeanList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //处理保存的数据，根据类型区分，暂根据本地数据库保存的数据处理，处理比较麻烦，后续上传到后台，后台处理简单很多
    private fun analyseData(sportLt: List<AmapSportBean>) {
        try {

            Log.e("地图记录","-------list="+Gson().toJson(sportLt))

            val monthMap = HashMap<String, Any>()
            resultList.clear()
            val rMap: MutableMap<String, List<AmapSportBean>> = HashMap()
            sportLt.forEach { amapSportBean ->
                val currMonth = amapSportBean.yearMonth
                monthMap[currMonth] = "1"
            }
            monthMap.forEach {
                it.key
                val chilList: MutableList<AmapSportBean> = ArrayList()
                sportLt.forEach { amp ->
                    if (it.key == amp.yearMonth) {
                        chilList.add(amp)
                    }
                    rMap[it.key] = chilList
                }
            }
            for ((keyMonth, tmAL) in rMap) {
                var countDistance = 0.0
                var countCalories = 0.0
                var countWalk = 0.0
                var countRun = 0.0
                var countRide = 0.0
                for (amapSportBean in tmAL) {
                    var currDistance = amapSportBean.distance
                    var currCalories = amapSportBean.calories
                    if (currCalories.isNullOrEmpty())
                        currCalories = "0"
                    if (currDistance.isNullOrEmpty())
                        currDistance = "0"
                    when (amapSportBean.sportType) {
                        1 -> {
                            countWalk = Utils.add(countWalk, currDistance.toDouble())
                        }
                        2 -> {
                            countRun = Utils.add(countRun, currDistance.toDouble())
                        }
                        3 -> {
                            countRide = Utils.add(countRide, currDistance.toDouble())
                        }
                    }
                    countCalories = Utils.add(countCalories, currCalories.toDouble())
                    countDistance = Utils.add(countDistance, currDistance.toDouble())
                }
                val amapRecordBean = AmapRecordBean()
                amapRecordBean.monthStr = keyMonth
                amapRecordBean.isShow = true //首次展现
                amapRecordBean.distanceCount = Utils.divi(countDistance, 1000.0, 3).toString() + ""
                amapRecordBean.caloriesCount = countCalories.toString() + ""
                amapRecordBean.list = tmAL
                amapRecordBean.runDistance = Utils.divi(countRun, 1000.0, 3).toString() + ""
                amapRecordBean.walkDistance = Utils.divi(countWalk, 1000.0, 3).toString() + ""
                amapRecordBean.rideDistance = Utils.divi(countRide, 1000.0, 3).toString() + ""
                amapRecordBean.sportCount = tmAL.size
                resultList.add(amapRecordBean)
            }
            resultList.sortByDescending { it.monthStr }
            resultList.forEach {
                it.isShow = false
            }
            resultList[0].isShow = true
            amapRecordAdapter!!.setType(sportType)
            // amapRecordAdapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showEmpty() {
        emptyTv!!.visibility = View.VISIBLE
        amapRecordAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.recordTitleBackImg -> {
                finish()
            }
            R.id.recordSportTitleTv -> {
                //saveSQL()
                alertDialogTitle()
            }
        }

    }

    //类型弹窗选择
    private fun alertDialogTitle() {
        alert = AlertDialog.Builder(this)
            .setItems(typeStr) { dialogInterface, i ->
                dialogInterface.dismiss()
                recordSportTitleTv!!.text = typeStr[i]
                sportType = i
                TLog.error("sportType+=" + sportType)
                getList(i)
                //  querySaveSport(sportType)
            }
            .setNegativeButton("取消") { dialogInterface, i -> dialogInterface.dismiss() }
        alert?.create()?.show()
    }

}