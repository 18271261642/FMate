package com.example.xingliansdk.ui.login

import android.os.Bundle
import android.os.CountDownTimer
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.ui.login.viewMode.LoginViewModel
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.MD5Util
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_log_out_code.*

class LogOutCodeActivity : BaseActivity<LoginViewModel>() {

    private var countDownTimer: MyCountDownTimer? = null
    override fun layoutId()=R.layout.activity_log_out_code
    var md5Password=""
    var statusNext=false
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        countDownTimer = MyCountDownTimer(60000, 1000)
        countDownTimer?.start()
        if(userInfo.user==null)
        {
            ShowToast.showToastLong("状态异常,无个人信息")
            finish()
            return
        }
        var areaCode=userInfo.user.areaCode
        var mPhone=userInfo.user.phone
        var phoned = if(areaCode.toInt()==86)
            mPhone.replace(mPhone.substring(3,7) , "****" )
        else
            mPhone.replace(mPhone.substring(0,mPhone.length-4) , "****" )
        tvPhone.text=resources.getString(R.string.string_has_send_code)+" +${userInfo.user.areaCode} $phoned"
        TLog.error("codeView.getText()==${codeView.getText()} userInfo.user.phone==${userInfo.user.phone} areaCode==${userInfo.user.areaCode}")
        md5Password = MD5Util.md5(mPhone+ areaCode + 10861)
        mViewModel.getVerifyCode(mPhone, areaCode, md5Password,"1")
        tvCode.setOnClickListener {
            countDownTimer?.start()
            mViewModel.getVerifyCode(mPhone,areaCode, md5Password,"1")
        }
        tvNext.setOnClickListener {
            var value: HashMap<String, String> = HashMap()
            value["phone"] = userInfo.user.phone
            value["verifyCode"] = codeView.getText()
            value["areaCode"] = userInfo.user.areaCode
            value["type"] = "1"
            TLog.error("=="+Gson().toJson(value))
            mViewModel.checkVerifyCode(value)

        }
        codeView.setListener {
            statusNext=it
            if (it) {
                HelpUtil.hideSoftInputView(this)
                tvNext.setBackgroundResource(R.drawable.bg_login_password)
            } else {
                tvNext.setBackgroundResource(R.drawable.bg_login_password_gray)
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.msgCheckVerifyCode.observe(this){
            TLog.error("")
            if (statusNext) {
                JumpUtil.startSureLogOutActivity(this,codeView.getText())
            }
        }

    }

    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        //计时过程
        override fun onTick(l: Long) { //防止计时过程中重复点击
            tvCode.isClickable = false
            tvCode.text =resources.getString(R.string.string_try_again_code)+"("+ (l / 1000).toString() + ")"
        }

        //计时完毕的方法
        override fun onFinish() { //重新给Button设置文字
            tvCode.text = resources.getString(R.string.string_try_again_code)
            //设置可点击
            tvCode.isClickable = true
        }

    }
}