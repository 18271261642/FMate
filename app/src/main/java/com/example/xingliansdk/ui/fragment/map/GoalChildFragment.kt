package com.example.xingliansdk.ui.fragment.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.xingliansdk.R
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.base.viewmodel.BaseViewModel

class GoalChildFragment :  BaseFragment<BaseViewModel>() {
    //标题集合
    var mDataList: ArrayList<String> = arrayListOf("距离", "时长", "热量")
    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    override fun layoutId()=R.layout.fragment_goal_child
    companion object {
        fun newInstance(cid: Int): GoalChildFragment {
            val args = Bundle()
            args.putInt("cid", cid)
            val fragment = GoalChildFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

}