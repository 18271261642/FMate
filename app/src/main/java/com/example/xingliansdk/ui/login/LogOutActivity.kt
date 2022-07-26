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

/**
 * 注销账号
 */
class LogOutActivity : BaseActivity<UserViewModel>(),View.OnClickListener {
    override fun layoutId()=R.layout.activity_log_out
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvNext.setOnClickListener(this)
        tvAgree.setOnClickListener(this)

        val beforeStr = resources.getString(R.string.string_click_continue)
        val afterStr = resources.getString(R.string.string_account_register_protocol)

        var  mStr = SpannableString(beforeStr+afterStr)
        mStr.setSpan(ForegroundColorSpan(resources.getColor(R.color.color_main_green)), beforeStr.length, beforeStr.length+afterStr.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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