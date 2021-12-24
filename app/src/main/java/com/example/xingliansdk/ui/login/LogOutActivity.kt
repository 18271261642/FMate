package com.example.xingliansdk.ui.login

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.example.xingliansdk.utils.JumpUtil
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_log_out.*

class LogOutActivity : BaseActivity<UserViewModel>(),View.OnClickListener {
    override fun layoutId()=R.layout.activity_log_out
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvNext.setOnClickListener(this)
        tvAgree.setOnClickListener(this)
        var  mStr = SpannableString(tvAgree.text.toString())
        mStr.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_main_green)), tvAgree.text.length-8, tvAgree.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvAgree.text=mStr


    }

    override fun createObserver() {
        super.createObserver()
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.tvNext->{
                JumpUtil.startLogOutCodeActivity(this)
            }
            R.id.tvAgree->
            {
                JumpUtil.startWeb(this,XingLianApplication.baseUrl+"/agreement/register")
            }
        }
    }
}