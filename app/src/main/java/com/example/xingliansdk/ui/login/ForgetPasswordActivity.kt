package com.example.xingliansdk.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.annotation.Nullable
import androidx.core.widget.addTextChangedListener
import com.example.phoneareacodelibrary.AreaCodeModel
import com.example.phoneareacodelibrary.PhoneAreaCodeActivity
import com.example.phoneareacodelibrary.SelectPhoneCode
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.ui.login.viewMode.LoginViewModel
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.MD5Util
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_login.imgBack
import kotlinx.android.synthetic.main.activity_login.imgPassword


class ForgetPasswordActivity : BaseActivity<LoginViewModel>(), View.OnClickListener {

    private var countDownTimer: MyCountDownTimer? = null
    override fun layoutId() = R.layout.activity_login
    var areaCode = "86"
    override fun initView(savedInstanceState: Bundle?) {

        var phoneCodeStr = intent.getStringExtra("phoneCode")
        if(!TextUtils.isEmpty(phoneCodeStr)){
            edt_mobile.setText(phoneCodeStr)
        }

        tv_login.setOnClickListener(this)
        tvPhoneCode.setOnClickListener(this)
        tv_getcode.setOnClickListener(this)
        tvPassword.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
        imgPassword.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        Permissions()
        tvTitle.text = "忘记密码"
        tv_login.text = "确认"
        edtPassword.visibility = View.INVISIBLE
        imgPassword.visibility = View.INVISIBLE
        tv_getcode.visibility = View.VISIBLE
        edt_code.visibility = View.VISIBLE
        tvForgotPassword.visibility = View.INVISIBLE
        tv_regist.visibility = View.INVISIBLE
        llBottom.visibility = View.GONE
        imgBack.visibility = View.VISIBLE
        tvPassword.visibility = View.GONE
        countDownTimer = MyCountDownTimer(60000, 1000)
        edt_mobile.addTextChangedListener {
            setSureBtnColor()
        }
        edt_code.addTextChangedListener{
            setSureBtnColor()
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.msgCheckVerifyCode.observe(this)
        {
            JumpUtil.startPasswordActivity(
                this,
                edt_mobile.text.toString(),
                edt_code.text.toString(),
                areaCode, 2)
        }
    }

    var password = ""
    var md5Password = ""
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_login -> {

                if (edt_mobile.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong("请输入手机号")
                    return
                } else if (edt_code.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong("请输入验证码")
                    return
                }
                var value: HashMap<String, String> = HashMap()
                value["phone"] = edt_mobile.text.toString()
                value["verifyCode"] = edt_code.text.toString()
                value["areaCode"] = areaCode
                value["type"] = "2"
                mViewModel.checkVerifyCode(value)

                HelpUtil.hideSoftInputView(this)
                // mViewModel.loginRegistered(value)
            }
            R.id.tvPhoneCode -> {
                SelectPhoneCode.with(this)
                    .setTitle("区号选择")
                    .setStickHeaderColor("#41B1FD")//粘性头部背景颜色
                    .setTitleBgColor("#ffffff")//界面头部标题背景颜色
                    .setTitleTextColor("#454545")//标题文字颜色
                    .select()
            }
            R.id.tv_getcode -> {
                if (edt_mobile.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong("请输入手机号")
                    return
                }
                countDownTimer?.start()
                password = edt_mobile.text.toString() + areaCode + 10861
                md5Password = MD5Util.md5(password)
                TLog.error("md5Password+=" + md5Password)
                tv_getcode.setTextColor(resources.getColor(R.color.color_login_code))
                tv_getcode.setBackgroundResource(R.drawable.login_code_btn_false)
                mViewModel.getVerifyCode(edt_mobile.text.toString(), areaCode, md5Password, "2")
            }
            R.id.imgBack -> {
                finish()
            }

        }

    }
    private fun setSureBtnColor()
    {
        if(edt_mobile.text?.trim()?.length!!>=5&&
            edt_code.text!!.trim().length>=4)
        {
            tv_login.setTextColor(resources.getColor(R.color.white))
            tv_login.setBackgroundResource(R.drawable.bg_login_password)
        }
        else
        {
            tv_login.setTextColor(resources.getColor(R.color.color_login_code))
            tv_login.setBackgroundResource(R.drawable.bg_login_password_gray)
        }

    }

    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        //计时过程
        override fun onTick(l: Long) { //防止计时过程中重复点击
            tv_getcode.isClickable = false
            tv_getcode.text = (l / 1000).toString() + "秒"
        }

        //计时完毕的方法
        override fun onFinish() { //重新给Button设置文字
            tv_getcode.text = "重新获取"
            //设置可点击
            tv_getcode.setTextColor(resources.getColor(R.color.color_main_green))
            tv_getcode.setBackgroundResource(R.drawable.login_code_btn)
            tv_getcode.isClickable = true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == PhoneAreaCodeActivity.resultCode) {
            if (data != null) {
                val model: AreaCodeModel =
                    data.getSerializableExtra(PhoneAreaCodeActivity.DATAKEY) as AreaCodeModel
                tvPhoneCode.text = "+" + model.tel
                areaCode = model.tel
            }
        }
    }

}