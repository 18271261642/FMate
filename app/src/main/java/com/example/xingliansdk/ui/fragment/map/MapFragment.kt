package com.example.xingliansdk.ui.fragment.map

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.example.xingliansdk.Config.exercise.MILE
import com.example.xingliansdk.R
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.bean.MapMotionBean
import com.example.xingliansdk.bean.MotionBean
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.network.api.mapView.MapMotionViewModel
import com.example.xingliansdk.ui.fragment.map.newmap.AmapSportRecordActivity
import com.example.xingliansdk.utils.*
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.fragment_movement_type.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat


class MapFragment : BaseFragment<MapMotionViewModel>(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.fragment_movement_type
    lateinit var mStr: SpannableString
    var mMapMotionBean: MapMotionBean? = null

    //    private var mListener: LocationSource.OnLocationChangedListener? = null
//    private var mlocationClient: AMapLocationClient? = null
//    private var mLocationOption: AMapLocationClientOption? = null
//    private var tempStatus=false
    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        arguments?.let {
            mMapMotionBean = it.getParcelable("MapMotionBean")
            mStr =
                SpannableString(mMapMotionBean?.Distance?.let { it1 -> HelpUtil.getFormatter(it1) } + "公里")
        }
//        mapView = view?.findViewById<View>(R.id.map) as TextureMapView
//        mapView!!.onCreate(savedInstanceState)
        //   init()
        homeSportLayout.setOnClickListener(this)
        mStr.setSpan(
            AbsoluteSizeSpan(20, true),
            mStr.length - 2,
            mStr.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvGo.setOnClickListener(this)
//        tvGoal.setOnClickListener(this)
    }


    companion object {
        fun newInstance(cid: Int, mDistance: MapMotionBean): MapFragment {
            val args = Bundle()
            args.putInt("cid", cid)
            args.putSerializable("MapMotionBean", mDistance)
            val fragment = MapFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvGo -> {
                TLog.error("点击+=${Gson().toJson(mMapMotionBean)}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    activity?.let {
                        setMapPermissions(it, object : MapPermissionBack {
                            override fun next() {
                                TLog.error("进来了")
                                Hawk.put(
                                    com.example.xingliansdk.Config.database.AMAP_SPORT_TYPE,
                                    mMapMotionBean?.type
                                )
                                JumpUtil.startLocationMap(activity, mMapMotionBean)
                            }
                        })
                    }
                } else {
                    Hawk.put(
                        com.example.xingliansdk.Config.database.AMAP_SPORT_TYPE,
                        mMapMotionBean?.type
                    )
                    JumpUtil.startLocationMap(activity, mMapMotionBean)
                }
            }

//            R.id.tvGoal -> {
//                TLog.error("头大的点击事件")
//                nav().navigateAction(R.id.action_MapFragment_to_GoalFragment)
//            }
            R.id.homeSportLayout -> {
                val intent = Intent(context, AmapSportRecordActivity::class.java)
                mMapMotionBean?.let { intent.putExtra("sportType", it.type) }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        TLog.error("回来触发 onResume")
        TLog.error("---111--mMapMotionBean=" + Gson().toJson(mMapMotionBean))
        mMapMotionBean?.let { it ->
            mViewModel.getMotionDistance(it.type)
        }
        TLog.error("---222--mMapMotionBean=" + Gson().toJson(mMapMotionBean))


        mViewModel.msg.observe(this)
        {
            //累计里程
            mMapMotionBean?.let { it1 -> typeSportDistance(it1.type) }
        }

        //文字显示
        homeSportTypeTv.text = "累计" + mMapMotionBean?.let { it1 -> typeSport(it1.type) } + "距离>"
//        //文字显示
//        homeSportTypeTv.text= "累计"+mMapMotionBean?.let { it1 -> typeSport(it1.type) } +"距离>"
//        //累计里程
//        homeSportCountTv.text = mMapMotionBean?.let { it1 -> typeSportDistance(it1.type) }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            TLog.error("--------" + Gson().toJson(it))
            var motionBean: MotionBean = Gson().fromJson(Gson().toJson(it), MotionBean::class.java)
            unitSport(motionBean.distance.toDouble())
        }
        mViewModel.msg.observe(this)
        {
            //累计里程
         mMapMotionBean?.let { it1 -> typeSportDistance(it1.type) }
        }
    }

    private fun unitSport(distance: Double = 0.0) {
        if (distance > 0) {
            homeSportCountTv.text = if (userInfo.userConfig.distanceUnit == 1) {
                // HelpUtil.getSpan("10.23","英里",14)
                HelpUtil.getSpan(
                    ResUtil.format("%.2f ", distance / 1000 * MILE).trim(),
                    "英里",
                    14
                )
            } else {
                HelpUtil.getSpan(
                    ResUtil.format("%.2f ", distance / 1000).trim(),
                    "公里",
                    14
                )
            }
        } else {
            homeSportCountTv.text =
                if (userInfo.userConfig.distanceUnit == 1)
                    HelpUtil.getSpan(distance.toString().trim(), "英里", 14)
                else
                    HelpUtil.getSpan(distance.toString().trim(), "公里", 14)
        }
    }

    private fun typeSport(type: Int): String? {
        if (type == 1) return "步行"
        if (type == 2) return "跑步"
        return if (type == 3) "骑行" else "步行"
    }

    private val decimalFormat = DecimalFormat("#.##")

    //查询下保存的累计数据
    private fun typeSportDistance(type: Int) {
        var mAmapSportDao = AppDataBase.instance.getAmapSportDao()

        val sportBeanList = if (type == 1 || type == 2 || type == 3) {
            TLog.error("type==" + type)
            mAmapSportDao.getRoomTime(type)
        } else {
            TLog.error("else  type==" + type)
            mAmapSportDao.getAllList()
        }
        TLog.error("sportBeanList+=" + Gson().toJson(sportBeanList))
          if (sportBeanList.isNotEmpty() || sportBeanList.size > 0) {
            var countDistance = 0.0
            sportBeanList.forEach {
                countDistance = Utils.add(countDistance, it.distance.trim().toDouble())
            }
            unitSport(decimalFormat.format(Utils.divi(countDistance, 1000.0, 2)).toDouble())
          //  decimalFormat.format(Utils.divi(countDistance, 1000.0, 2))
        } else {
              unitSport( )
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            com.example.xingliansdk.Config.eventBus.CHANGE_UNIT->
            {
                var data=event.data as LoginBean
                TLog.error("返回data=="+Gson().toJson(data))
                userInfo=data
            }
        }
    }

}