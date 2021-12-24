package com.example.xingliansdk.ui.dial

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.ext.bindViewPager2
import com.example.xingliansdk.ext.init
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.call.write.dial.DialGetAssignCall
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_dial_market.*
import kotlinx.android.synthetic.main.dial_viewpager.*

//表盘页面
class DialMarketActivity : BaseActivity<BaseViewModel>()  {
    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()
    //标题集合
    var mDataList: ArrayList<String> = arrayListOf("推荐", "我的")
    companion object {
        var downStatus=false
    }

    override fun layoutId()=R.layout.activity_dial_market
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        view_pager.init(this, fragments)
        view_pager.offscreenPageLimit=2
        magic_indicator.bindViewPager2(view_pager, mDataList)
    }

    override fun createObserver() {
        super.createObserver()
        fragments.add(RecommendDialFragment())
        fragments.add(MeDialFragment())
    }
}