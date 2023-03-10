package com.app.fmate.ui.fragment.map

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import com.app.fmate.Config.exercise.MILE
import com.app.fmate.R
import com.app.fmate.base.fragment.BaseFragment
import com.app.fmate.bean.MapMotionBean
import com.app.fmate.bean.MotionBean
import com.app.fmate.bean.room.AppDataBase
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.network.api.mapView.MapMotionViewModel
import com.app.fmate.ui.fragment.map.newmap.AmapSportRecordActivity
import com.app.fmate.utils.*
import com.app.fmate.view.DateUtil
import com.google.gson.Gson
import com.hjq.permissions.XXPermissions
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.fragment_movement_type.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.text.DecimalFormat


class MapFragment : BaseFragment<MapMotionViewModel>(), View.OnClickListener {

    override fun layoutId(): Int = R.layout.fragment_movement_type
    lateinit var mStr: SpannableString
    var mMapMotionBean: MapMotionBean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFilter = IntentFilter(MapContances.NOTIFY_MAP_HISTORY_UPDATE_ACTION)
        activity?.registerReceiver(broadcastReceiver,intentFilter)
    }

    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        arguments?.let {
            mMapMotionBean = it.getParcelable("MapMotionBean")
            mStr =
                SpannableString(mMapMotionBean?.Distance?.let { it1 -> HelpUtil.getFormatter(it1) } + "??????")
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


    private fun shareLogFile(){
        if(XXPermissions.isGranted(activity,Manifest.permission.READ_EXTERNAL_STORAGE)){

            var logUrl = Environment.getExternalStorageDirectory().parent+"/Download/log-"+DateUtil.getCurrDate()+".txt"
            if(logUrl != null && File(logUrl).isFile){


            }

        }
    }

    //????????????
    fun shareFiles(context: Context?) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        //???????????????????????????????????????
        // ?????????????????????
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        // ???????????????????????????
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "?????????.."));
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
                TLog.error("??????+=${Gson().toJson(mMapMotionBean)}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    activity?.let {
                        setMapPermissions(it, object : MapPermissionBack {
                            override fun next() {
                                TLog.error("?????????")
                                Hawk.put(
                                    com.app.fmate.Config.database.AMAP_SPORT_TYPE,
                                    mMapMotionBean?.type
                                )
                                JumpUtil.startLocationMap(activity, mMapMotionBean)
                            }
                        })
                    }
                } else {
                    Hawk.put(
                        com.app.fmate.Config.database.AMAP_SPORT_TYPE,
                        mMapMotionBean?.type
                    )
                    JumpUtil.startLocationMap(activity, mMapMotionBean)
                }
            }

//            R.id.tvGoal -> {
//                TLog.error("?????????????????????")
//                nav().navigateAction(R.id.action_MapFragment_to_GoalFragment)
//            }
            R.id.homeSportLayout -> {
                val intent = Intent(context, AmapSportRecordActivity::class.java)
                mMapMotionBean?.let { intent.putExtra("sportType", it.type) }
                Hawk.put(
                    com.app.fmate.Config.database.AMAP_SPORT_TYPE,
                    mMapMotionBean?.type
                )
                startActivity(intent)
            }
        }
    }


    private fun changeEn(str : String) : String{
        if(str == "??????")
            return resources.getString(R.string.string_sport_step)
        if(str == "??????")
            return resources.getString(R.string.string_sport_run)
        if(str == "??????")
            return resources.getString(R.string.string_sport_cycle)
        return resources.getString(R.string.string_sport_step)
    }

    override fun onResume() {
        super.onResume()
        mMapMotionBean?.let { it ->
            mViewModel.getMotionDistance(it.type)
        }
        var typeStr = mMapMotionBean?.let { it1 -> typeSport(it1.type) }
        //????????????
        homeSportTypeTv.text = resources.getString(R.string.string_total) +""+ typeStr +""+ resources.getString(R.string.string_distance)+">"
//        //????????????
//        homeSportTypeTv.text= "??????"+mMapMotionBean?.let { it1 -> typeSport(it1.type) } +"??????>"
//        //????????????
//        homeSportCountTv.text = mMapMotionBean?.let { it1 -> typeSportDistance(it1.type) }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            TLog.error("---????????????????????????-----" + Gson().toJson(it))
            var motionBean: MotionBean = Gson().fromJson(Gson().toJson(it), MotionBean::class.java)
            unitSport(motionBean.distance.toDouble())
        }
        mViewModel.msg.observe(this)
        {
            //????????????
         mMapMotionBean?.let { it1 -> typeSportDistance(it1.type) }
        }
    }

    private fun unitSport(distance: Double = 0.0) {
        if (distance > 0) {
            homeSportCountTv.text = if (userInfo.userConfig.distanceUnit == 1) {
                // HelpUtil.getSpan("10.23","??????",14)
                HelpUtil.getSpan(
                    ResUtil.format("%.2f ", distance / 1000 * MILE).trim(),
                    resources.getString(R.string.string_mile),
                    14
                )
            } else {
                HelpUtil.getSpan(
                    ResUtil.format("%.2f ", distance / 1000).trim(),
                    resources.getString(R.string.string_km),
                    14
                )
            }
        } else {
            homeSportCountTv.text =
                if (userInfo.userConfig.distanceUnit == 1)
                    HelpUtil.getSpan(distance.toString().trim(), resources.getString(R.string.string_mile), 14)
                else
                    HelpUtil.getSpan(distance.toString().trim(), resources.getString(R.string.string_km), 14)
        }
    }

    private fun typeSport(type: Int): String? {
        if (type == 1) return resources.getString(R.string.string_sport_step)
        if (type == 2) return resources.getString(R.string.string_sport_run)
        return if (type == 3) resources.getString(R.string.string_sport_cycle) else resources.getString(R.string.string_sport_step)
    }

    private val decimalFormat = DecimalFormat("#.##")

    //??????????????????????????????
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
            com.app.fmate.Config.eventBus.CHANGE_UNIT->
            {
                var data=event.data as LoginBean
                TLog.error("??????data=="+Gson().toJson(data))
                userInfo=data
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        try {
         activity?.unregisterReceiver(broadcastReceiver)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private  val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            if(action.equals(MapContances.NOTIFY_MAP_HISTORY_UPDATE_ACTION)){
                mMapMotionBean?.let { it ->
                    mViewModel.getMotionDistance(it.type)
                }
            }
        }

    }


}