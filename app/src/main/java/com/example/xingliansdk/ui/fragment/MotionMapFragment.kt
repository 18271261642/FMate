package com.example.xingliansdk.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.xingliansdk.R
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.bean.MapMotionBean
import com.example.xingliansdk.ext.bindViewPager2
import com.example.xingliansdk.ext.init
import com.example.xingliansdk.ui.fragment.map.MapFragment
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.dialog_find.view.*
import kotlinx.android.synthetic.main.motion_map_viewpager.*

class MotionMapFragment : BaseFragment<BaseViewModel>() {

    private val tags = "MotionMapFragment"

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()
    //标题集合
    var mDataList: ArrayList<String> = arrayListOf("步行", "跑步", "骑行"/*, "室内运动"*/)
    //标题集合
    private var mDistanceList: ArrayList<MapMotionBean> = arrayListOf(MapMotionBean(1,0.toDouble())
    ,MapMotionBean(2,0.toDouble())
    ,MapMotionBean(3,0.toDouble()))
    //,MapMotionBean(3,0.toDouble()))
    override fun layoutId(): Int = R.layout.fragment_motion_map
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.setTitleBar(activity, viewpager_linear)
//        //初始化viewpager2
//        if (!Hawk.get<ArrayList<MapMotionBean>>("DistanceList").isNullOrEmpty())
//            for (i in 0 until mDataList.size) {
//                mDistanceList=Hawk.get("DistanceList")
//            }
//        view_pager?.init(this, fragments)
//        view_pager?.offscreenPageLimit=3
//        //初始化 magic_indicator
//        magic_indicator.bindViewPager2(view_pager, mDataList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Hawk.put(
            com.example.xingliansdk.Config.database.AMAP_SPORT_TYPE,
            1
        )

        mDataList.clear()
        mDataList.add(resources.getString(R.string.string_sport_step))
        mDataList.add(resources.getString(R.string.string_sport_run))
        mDataList.add(resources.getString(R.string.string_sport_cycle))

    }

    override fun createObserver() {
        for (i in 0 until mDataList.size) {
            fragments.add(MapFragment.newInstance(i,mDistanceList[i]))
        }

    }

    override fun onResume() {
        super.onResume()
        val mapType = Hawk.get(
            com.example.xingliansdk.Config.database.AMAP_SPORT_TYPE,
            1
        )
        Log.e(tags,"------onResume---------="+mapType)
        //初始化viewpager2
        if (!Hawk.get<ArrayList<MapMotionBean>>("DistanceList").isNullOrEmpty())
            for (i in 0 until mDataList.size) {
                mDistanceList=Hawk.get("DistanceList")
            }
        view_pager?.init(this, fragments)
        view_pager?.offscreenPageLimit=3
        view_pager.currentItem = mapType-1
        view_pager.setCurrentItem(mapType-1,false)
//        Hawk.put(
//            com.example.xingliansdk.Config.database.AMAP_SPORT_TYPE,
//            1
//        )
        //初始化 magic_indicator
        magic_indicator.bindViewPager2(view_pager, mDataList,)
        magic_indicator.onPageSelected(mapType-1)
    }
}