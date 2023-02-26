package com.app.fmate.ui.setting.account

import android.os.Bundle
import android.view.View
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.ui.login.viewMode.UserViewModel
import com.app.fmate.utils.AllGenJIDialog
import com.app.fmate.utils.JumpUtil
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_account.*

/**
 * 账号与安全
 */
class AccountActivity : BaseActivity<UserViewModel>(), View.OnClickListener {
    override fun layoutId() = R.layout.activity_account
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        settingUpPassword.setOnClickListener(this)
        settingChangePhone.setOnClickListener(this)
        tvSignOut.setOnClickListener(this)
        tvLogout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settingUpPassword -> {
                JumpUtil.startUpPasswordActivity(this)
            }
            R.id.settingChangePhone->{
                JumpUtil.startFindPhoneActivity(this)
            }
            R.id.tvSignOut -> {
                TLog.error("退出时=="+ Hawk.get<String>("address"))
                AllGenJIDialog.signOutDialog(supportFragmentManager, mViewModel, userInfo,this)
            }
            R.id.tvLogout->{
                JumpUtil.startLogOutActivity(this)
            }
        }
    }
}