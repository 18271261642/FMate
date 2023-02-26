package com.app.fmate.ui.dial

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.ext.bindViewPager2
import com.app.fmate.ext.init
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.widget.TitleBarLayout
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.Config
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
        //是否在同步表盘中
        var isSyncDial = false
    }

    private val instant by lazy { this }

    private var alertDialog : AlertDialog.Builder ?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XingLianApplication.mXingLianApplication.setIsSyncWriteDial(false)

        mDataList.clear()
        mDataList.add(resources.getString(R.string.string_dial_recommend))
        mDataList.add(resources.getString(R.string.string_dial_mine))
    }

    override fun layoutId()=R.layout.activity_dial_market
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        mDataList.clear()
        mDataList.add(resources.getString(R.string.string_dial_recommend))
        mDataList.add(resources.getString(R.string.string_dial_mine))
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


    override fun onDestroy() {
        super.onDestroy()
        isSyncDial = false
        downStatus = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(downStatus){
                backAlert()
            }else{
               AppActivityManager.getInstance().finishActivity(this)
            }
        }
        return true
    }

    private fun backAlert(){
        alertDialog = AlertDialog.Builder(instant)
        alertDialog!!.setTitle(resources.getString(R.string.string_text_remind))
        alertDialog!!.setMessage(resources.getString(R.string.string_dial_cancel_desc))
        alertDialog!!.setPositiveButton(resources.getString(R.string.text_sure)
        ) { p0, p1 ->
            downStatus = false
            XingLianApplication.mXingLianApplication.setIsSyncWriteDial(true)
            Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,17)
            Hawk.put(Config.SAVE_MARKET_BEAN_DIAL,"")
            Hawk.put(Config.SAVE_DEVICE_INTO_MARKET_DIAL,-1)


            p0.dismiss()
            AppActivityManager.getInstance().finishActivity(this)
        }.setNegativeButton(resources.getString(R.string.text_cancel)
        ) { p0, p1 ->
            p0.dismiss()
        }
        alertDialog!!.create().show()
    }
}