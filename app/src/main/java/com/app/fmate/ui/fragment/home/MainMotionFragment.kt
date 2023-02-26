package com.app.fmate.ui.fragment.home

import android.os.Bundle
import com.app.fmate.R
import com.app.fmate.base.fragment.BaseFragment
import com.app.fmate.base.viewmodel.BaseViewModel

class MainMotionFragment : BaseFragment<BaseViewModel>() {


    override fun layoutId(): Int =R.layout.activity_main_motion_fragment

    override fun initView(savedInstanceState: Bundle?) {
    }

    fun getInstance( ): MainMotionFragment {
        val mMainMotionFragment =
            MainMotionFragment()
        val bundle = Bundle()
        //TLog.error("title==${Gson().toJson(mChildrenBean)}")
     //   bundle.putString("LookExploreChildrenFragment", mChildrenBean.type)
        mMainMotionFragment.arguments = bundle
          //    mMainMotionFragment.type = mChildrenBean.type

        return mMainMotionFragment
    }
}