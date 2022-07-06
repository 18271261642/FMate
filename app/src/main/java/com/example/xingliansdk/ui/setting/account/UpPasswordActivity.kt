package com.example.xingliansdk.ui.setting.account

import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.ui.login.viewMode.LoginViewModel
import com.example.xingliansdk.ui.setting.SettingActivity
import com.example.xingliansdk.utils.*
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_up_password.*

/**
 * 修改密码页面
 */
class UpPasswordActivity : BaseActivity<LoginViewModel>(), View.OnClickListener {
    private var countDownTimer: MyCountDownTimer? = null
    private var imgPasswordStatus = false
    var imgPasswordStatusSure = false
    override fun layoutId() = R.layout.activity_up_password

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        countDownTimer = MyCountDownTimer(60000, 1000)
        tvPhone.text = userInfo.user.phone
        var areaCode = userInfo.user.areaCode
        var mPhone = userInfo.user.phone
        var phoned= HelpUtil.getPasswordPhone(areaCode,mPhone)
        tvPhone.text = "$phoned"
        tvSure.setOnClickListener(this)
        tvGetCode.setOnClickListener(this)
        imgPassword.setOnClickListener(this)
        imgPasswordSure.setOnClickListener(this)
        edtPassword.addTextChangedListener { setSureBtnColor() }
        edtCode.addTextChangedListener { setSureBtnColor() }
        edtPasswordSure.addTextChangedListener { setSureBtnColor() }
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
                    "2"
                )

            }
            R.id.tvSure -> {
                    if(  edtPassword.text.trim().length < 5||
                         edtPasswordSure.text.trim().length < 5){
                             TLog.error("条件没过" )
                        return
                    }
                if (edtPassword.text.trim().length > 12) {
                    ShowToast.showToastLong(resources.getString(R.string.edt_password_number_text))
                    return
                } else if (!TextUtils.equals(edtPassword.text,edtPasswordSure.text)) {
                    ShowToast.showToastLong("两次密码不一致,请重新输入")
                    return
                }
                if(edtCode.text.toString().trim().isNullOrEmpty())
                {
                    ShowToast.showToastLong("请输入验证码")
                    return
                }
                HelpUtil.hideSoftInputView(this)
                var value = HashMap<String, String>()
                value["phone"] = userInfo.user.phone
                value["areaCode"] = userInfo.user.areaCode
                value["verifyCode"] = edtCode.text.toString()
                value["password"] = edtPassword.text.toString()
                mViewModel.updatePassword(value)
            }
            R.id.imgPassword->
            {
                if (imgPasswordStatus) {
                    edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    imgPassword.setImageResource(R.mipmap.icon_non)
                } else {
                    edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    imgPassword.setImageResource(R.mipmap.icon_see)
                }
                imgPasswordStatus = !imgPasswordStatus
            }
            R.id.imgPasswordSure->
            {
                if (imgPasswordStatusSure) {
                    edtPasswordSure.transformationMethod = PasswordTransformationMethod.getInstance()
                    imgPasswordSure.setImageResource(R.mipmap.icon_non)
                } else {
                    edtPasswordSure.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    imgPasswordSure.setImageResource(R.mipmap.icon_see)
                }
                imgPasswordStatusSure = !imgPasswordStatusSure
            }

        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.msgUpdatePassword.observe(this){
            userInfo=Gson().fromJson(Gson().toJson(it), LoginBean::class.java)
            Hawk.put(Config.database.USER_INFO,userInfo)
            ShowToast.showToastLong("修改成功")
            AppActivityManager.getInstance().finishActivity(SettingActivity::class.java)
            AppActivityManager.getInstance().finishActivity(AccountActivity::class.java)
            //JumpUtil.startMainHomeActivity(this)
            finish()
        }
        mViewModel.msg.observe(this){
            TLog.error("it=="+it)
        }
    }
    private fun setSureBtnColor() {

        if (edtPassword.text?.trim()?.length!! >= 6&&
            edtCode.text!!.trim().length >= 4 &&
            edtPasswordSure.text?.trim()?.length!! >= 6
        ) {
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
}