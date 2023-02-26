package com.app.fmate.ui.setting.account

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.annotation.Nullable
import androidx.core.widget.addTextChangedListener
import com.app.phoneareacodelibrary.AreaCodeModel
import com.app.phoneareacodelibrary.PhoneAreaCodeActivity
import com.app.phoneareacodelibrary.SelectPhoneCode
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.ui.login.viewMode.LoginViewModel
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.utils.MD5Util
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_bind_new_phone.*
import kotlinx.android.synthetic.main.activity_bind_new_phone.tvPhoneCode


class BindNewPhoneActivity : BaseActivity<LoginViewModel>(), View.OnClickListener {
    private var countDownTimer: MyCountDownTimer? = null
    override fun layoutId() = R.layout.activity_bind_new_phone
    var areaCode = "86"
    var oldVerifyCode = ""
    var getPassword = ""
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        oldVerifyCode = intent.getStringExtra("oldVerifyCode").toString()
        getPassword = intent.getStringExtra("password").toString()
        countDownTimer = MyCountDownTimer(60000, 1000)
        tvPhoneCode.setOnClickListener(this)
        tvSure.setOnClickListener(this)
        tvGetCode.setOnClickListener(this)
        edtPhone.addTextChangedListener { setSureBtnColor() }
        edtCode.addTextChangedListener { setSureBtnColor() }
    }

    private fun setSureBtnColor() {

        if (edtCode.text!!.trim().length >= 4 && edtPhone.text.trim().length >= 4) {
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
                password = edtPhone.text.toString() + areaCode + 10861
                md5Password = MD5Util.md5(password)
                tvGetCode.setTextColor(resources.getColor(R.color.color_login_code))
                tvGetCode.setBackgroundResource(R.drawable.login_code_btn_false)
                mViewModel.getVerifyCode(
                    edtPhone.text.toString(),
                    areaCode,
                    md5Password,
                    "3"
                )

            }
            R.id.tvPhoneCode -> {
                SelectPhoneCode.with(this)
                    .setTitle("区号选择")
                    .setStickHeaderColor("#41B1FD")//粘性头部背景颜色
                    .setTitleBgColor("#ffffff")//界面头部标题背景颜色
                    .setTitleTextColor("#454545")//标题文字颜色
                    .select()
            }
            R.id.tvSure -> {
                if (edtPhone.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong("请输入手机号")
                    return
                }
                if (edtCode.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong("请输入验证码")
                    return
                }
                var value = HashMap<String, String>()
                if (oldVerifyCode.isNullOrEmpty()) {
                    value["password"] = getPassword
                    value["type"] = "1"
                } else {
                    value["oldVerifyCode"] = oldVerifyCode
                    value["type"] = "0"
                }
                value["newVerifyCode"] = edtCode.text.toString()
                value["phone"] = edtPhone.text.toString()
                value["areaCode"] = areaCode
                mViewModel.updatePhone(value)
            }

        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultUpdatePhone.observe(this) {
            TLog.error("修改成功+" + Gson().toJson(it))
            ShowToast.showToastLong("设置成功")
            userInfo.user.phone = edtPhone.text.toString()
            userInfo.user.areaCode = areaCode
            Hawk.put(Config.database.USER_INFO, userInfo)
            AppActivityManager.getInstance().finishActivity(PasswordCheckActivity::class.java)
            AppActivityManager.getInstance().finishActivity(FindPhoneMainActivity::class.java)
            finish()
        }
        mViewModel.msg.observe(this) {

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