package com.app.fmate.ui.setting.account

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.ui.login.viewMode.LoginViewModel
import com.app.fmate.utils.HelpUtil
import com.app.fmate.utils.JumpUtil
import com.app.fmate.utils.MD5Util
import com.shon.connector.utils.ShowToast
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_find_phone_main.*


class FindPhoneMainActivity : BaseActivity<LoginViewModel>(),View.OnClickListener {
    private var countDownTimer: MyCountDownTimer? = null
    override fun layoutId()= R.layout.activity_find_phone_main

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        countDownTimer = MyCountDownTimer(60000, 1000)

        var areaCode = userInfo.user.areaCode
        var mPhone = userInfo.user.phone
       var phoned= HelpUtil.getPasswordPhone(areaCode,mPhone)
        tvPhone.text = "$phoned"
        tvSure.setOnClickListener(this)
        tvGetCode.setOnClickListener(this)
        tvLostPhone.setOnClickListener(this)
        edtCode.addTextChangedListener { setSureBtnColor() }
    }
    private fun setSureBtnColor() {

        if (edtCode.text!!.trim().length >= 4 ) {
            tvSure.setTextColor(resources.getColor(R.color.white))
            tvSure.setBackgroundResource(R.drawable.bg_login_password)
        } else {
            tvSure.setTextColor(resources.getColor(R.color.color_login_code))
            tvSure.setBackgroundResource(R.drawable.bg_login_password_gray)
        }

    }
    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        //计时过程
        override fun onTick(l: Long) { //防止计时过程中重复点击
            tvGetCode.isClickable = false
            tvGetCode.text = (l / 1000).toString() + "秒"
        }

        //计时完毕的方法
        override fun onFinish() { //重新给Button设置文字
            tvGetCode.text = "重新获取"
            //设置可点击
            tvGetCode.setTextColor(resources.getColor(R.color.color_main_green))
            tvGetCode.setBackgroundResource(R.drawable.login_code_btn)
            tvGetCode.isClickable = true
        }
    }
    var password = ""
    var md5Password = ""
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvGetCode -> {
                countDownTimer?.start()
                password = userInfo.user.phone + userInfo.user.areaCode + 10861
                md5Password = MD5Util.md5(password)
                tvGetCode.setTextColor(resources.getColor(R.color.color_login_code))
                tvGetCode.setBackgroundResource(R.drawable.login_code_btn_false)
                mViewModel.getVerifyCode(
                    userInfo.user.phone,
                    userInfo.user.areaCode,
                    md5Password,
                    "3")
            }
            R.id.tvSure -> {
                if(edtCode.text.toString().trim().isNullOrEmpty())
                {
                    ShowToast.showToastLong("请输入验证码")
                    return
                }
                var value = HashMap<String, String>()
                value["phone"] = userInfo.user.phone
                value["areaCode"] = userInfo.user.areaCode
                value["verifyCode"] = edtCode.text.toString()
                value["type"]="3"
                mViewModel.checkVerifyCode(value)
            }
            R.id.tvLostPhone->{
               JumpUtil.startPasswordCheckActivity(this)
            }

        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.msgCheckVerifyCode.observe(this){
            JumpUtil.startBindNewPhoneActivity(this,edtCode.text.toString(),"")
        }
        mViewModel.msg.observe(this){

        }
    }
}