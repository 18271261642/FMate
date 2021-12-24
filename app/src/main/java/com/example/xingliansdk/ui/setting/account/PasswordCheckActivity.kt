package com.example.xingliansdk.ui.setting.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.ui.login.viewMode.LoginViewModel
import com.example.xingliansdk.utils.*
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_find_phone_main.*
import kotlinx.android.synthetic.main.activity_password_check.*
import kotlinx.android.synthetic.main.activity_password_check.titleBar
import kotlinx.android.synthetic.main.activity_password_check.tvLostPhone
import kotlinx.android.synthetic.main.activity_password_check.tvPhone
import kotlinx.android.synthetic.main.activity_password_check.tvSure


class PasswordCheckActivity : BaseActivity<LoginViewModel>(),View.OnClickListener {
    override fun layoutId()= R.layout.activity_password_check

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        var areaCode = userInfo.user.areaCode
        var mPhone = userInfo.user.phone
       var phoned= HelpUtil.getPasswordPhone(areaCode,mPhone)
        tvPhone.text = "$phoned"
        tvSure.setOnClickListener(this)
        tvLostPhone.setOnClickListener (this)
        edtPassword.addTextChangedListener { setSureBtnColor() }
    }
    private fun setSureBtnColor() {

        if (edtPassword.text!!.length >= 6 ) {
            tvSure.setTextColor(resources.getColor(R.color.white))
            tvSure.setBackgroundResource(R.drawable.bg_login_password)
        } else {
            tvSure.setTextColor(resources.getColor(R.color.color_login_code))
            tvSure.setBackgroundResource(R.drawable.bg_login_password_gray)
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvSure -> {
                if(edtPassword.text.toString().isNullOrEmpty())
                {
                    ShowToast.showToastLong("请输入密码")
                    return
                }
                var value = HashMap<String, String>()
                value["phone"] =userInfo.user.phone
                value["areaCode"] = userInfo.user.areaCode
              //  value["type"] = "1"
                value["password"]=edtPassword.text.toString()
                mViewModel.checkPassword(value)
              //  mViewModel.updatePhone(value)
            }
            R.id.tvLostPhone->{
                JumpUtil.startAppealActivity(this)
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultCheckPassword.observe(this){
            TLog.error("成功")
            JumpUtil.startBindNewPhoneActivity(this,"",edtPassword.text.toString())

        }
        mViewModel.msg.observe(this){

        }

    }
}