package com.example.xingliansdk.ui.fragment.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.xingliansdk.R
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.ext.bindViewPager2
import com.example.xingliansdk.ext.init
import kotlinx.android.synthetic.main.motion_map_viewpager.*

/**
 * 运动目标设置
 */
class GoalFragment :  BaseFragment<BaseViewModel>() {
    //标题集合
    var mDataList: ArrayList<String> = arrayListOf("距离", "时长", "热量")
    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    override fun layoutId()=R.layout.activity_goal

    override fun initView(savedInstanceState: Bundle?) {
        view_pager.init(this, fragments)
        //初始化 magic_indicator
        magic_indicator.bindViewPager2(view_pager, mDataList)
    }
    override fun createObserver() {
        for (i in 0 until mDataList.size) {

            fragments.add(GoalChildFragment.newInstance(i))
        }

    }
}