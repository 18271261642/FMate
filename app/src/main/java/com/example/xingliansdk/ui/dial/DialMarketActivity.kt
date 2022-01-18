package com.example.xingliansdk.ui.dial

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.ext.bindViewPager2
import com.example.xingliansdk.ext.init
import com.example.xingliansdk.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.BleWrite
import com.shon.connector.call.write.dial.DialGetAssignCall
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_dial_details.*
import kotlinx.android.synthetic.main.activity_dial_market.*
import kotlinx.android.synthetic.main.activity_dial_market.titleBar
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

    private val instant by lazy { this }

    private var alertDialog : AlertDialog.Builder ?=null

    override fun layoutId()=R.layout.activity_dial_market
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        view_pager.init(this, fragments)
        view_pager.offscreenPageLimit=2
        magic_indicator.bindViewPager2(view_pager, mDataList)

        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener{
            override fun onBackClick() {
                if(downStatus){
                    backAlert()
                }else{
                    finish()
                }
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {

            }

        })
    }

    override fun createObserver() {
        super.createObserver()
        fragments.add(RecommendDialFragment())
        fragments.add(MeDialFragment())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(downStatus){
                backAlert()
            }else{
                finish()
            }
        }
        return true
    }

    private fun backAlert(){
        alertDialog = AlertDialog.Builder(instant)
        alertDialog!!.setTitle("提醒")
        alertDialog!!.setMessage("离开当前页面，将退出表盘传输哦，确定要离开当前页面吗?")
        alertDialog!!.setPositiveButton("确定"
        ) { p0, p1 ->
            p0.dismiss()
            finish()
        }.setNegativeButton("取消"
        ) { p0, p1 ->
            p0.dismiss()
        }
        alertDialog!!.create().show()
    }
}