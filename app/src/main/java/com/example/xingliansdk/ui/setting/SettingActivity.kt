package com.example.xingliansdk.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.ui.ShowPermissionActivity
import com.example.xingliansdk.utils.JumpUtil
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity<BaseViewModel>(), View.OnClickListener {

    val instance by lazy{this}
    override fun layoutId() = R.layout.activity_setting
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        settingAccount.setOnClickListener(this)
        settingUnit.setOnClickListener(this)
        privacyLayout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settingAccount -> {
                JumpUtil.startAccountActivity(this)
            }
            R.id.settingUnit -> {
                JumpUtil.startUnitActivity(this)
            }
            R.id.privacyLayout->{
                startActivity(Intent(instance,ShowPermissionActivity::class.java))
            }
        }
    }
}