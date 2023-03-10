package com.app.fmate.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.widget.addTextChangedListener
import com.app.phoneareacodelibrary.AreaCodeModel
import com.app.phoneareacodelibrary.PhoneAreaCodeActivity
import com.app.phoneareacodelibrary.SelectPhoneCode
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.BaseActivity
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.service.AppService
import com.app.fmate.ui.ShowPermissionActivity
import com.app.fmate.ui.login.viewMode.LoginViewModel
import com.app.fmate.utils.*
import com.google.gson.Gson
import com.ly.genjidialog.GenjiDialog
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.collections.HashMap


class LoginActivity : BaseActivity<LoginViewModel>(), View.OnClickListener {

    private var countDownTimer: MyCountDownTimer? = null
    override fun layoutId() = R.layout.activity_login
    var areaCode = "86"
    var imgPasswordStatus = false
    override fun initView(savedInstanceState: Bundle?) {
        tv_login.setOnClickListener(this)
        tvPhoneCode.setOnClickListener(this)
        tv_getcode.setOnClickListener(this)
        tvPassword.setOnClickListener(this)
        tvForgotPassword.setOnClickListener(this)
        imgPassword.setOnClickListener(this)
        tv_user_service_protocol.setOnClickListener(this)
        tv_privacy_policy.setOnClickListener(this)

        if (Hawk.get("privacyPolicyn", 0) != 1) {
            dialog()
            showOnWindow?.showOnWindow(supportFragmentManager)
        }
        checkbox.setOnClickListener {
            checkbox.isSelected = !checkbox.isSelected
        }

        //?????????????????? ?????? ?????????????????????
        stopService(Intent(this, AppService::class.java))
        countDownTimer = MyCountDownTimer(60000, 1000)
        if (userInfo.user != null) {
            edt_mobile.setText(userInfo.user.phone)
        }
        edt_mobile.addTextChangedListener {
            setSureBtnColor()
        }
        edt_code.addTextChangedListener {
            setSureBtnColor()
        }
        edtPassword.addTextChangedListener {
            setSureBtnColor()
        }
    }

    private var showOnWindow: GenjiDialog? = null
    private fun dialog() {
        showOnWindow = newGenjiDialog {
            layoutId = R.layout.dialog_privacy_policyn
            dimAmount = 0.8f
            isFullHorizontal = true
            touchCancel=false
            outCancel=false
            isFullVerticalOverStatusBar = false
            gravity = DialogGravity.CENTER_CENTER
            animStyle = R.style.BottomTransAlphaADAnimation
            convertListenerFun { holder, dialog ->
                var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                var dialogOK = holder.getView<TextView>(R.id.dialog_ok)
                var tvContent = holder.getView<TextView>(R.id.tvContent)
                var tvPrivacy = holder.getView<TextView>(R.id.tvPrivacy)
                var tvUser = holder.getView<TextView>(R.id.tvUser)
                tvContent?.text=resources.getString(R.string.string_privacy_content_desc)
                tvPrivacy?.setOnClickListener {
                    JumpUtil.startWeb(this@LoginActivity, XingLianApplication.baseUrl + "/agreement/privacy")
                }
                tvUser?.setOnClickListener {
                    JumpUtil.startWeb(this@LoginActivity, XingLianApplication.baseUrl + "/agreement/user")
                }
                dialogCancel?.setOnClickListener {
                    if(dialogCancel.text.equals("?????????,??????APP"))
                    {
                        finish()
                    }
                    dialogCancel.text = resources.getString(R.string.string_no_agree)
                    tvContent?.text = resources.getString(R.string.string_no_agree_desc)
                }
                dialogOK?.setOnClickListener {
                    Hawk.put("privacyPolicyn", 1)
                    dialog.dismiss()

                    val intent = Intent(this@LoginActivity,ShowPermissionActivity::class.java)
                    startActivityForResult(intent,0x02)


                }
            }
        }
    }


    private fun setSureBtnColor() {
        if (edt_mobile.text?.trim()?.length!! >= 1 &&
            (edt_code.text!!.trim().length >= 4 && edt_code.visibility == View.VISIBLE)
            || (edtPassword.text!!.trim().length >= 3 && edtPassword.visibility == View.VISIBLE)
        ) {
            tv_login.setTextColor(resources.getColor(R.color.white))
            tv_login.setBackgroundResource(R.drawable.bg_login_password)
        } else {
            tv_login.setTextColor(resources.getColor(R.color.color_login_code))
            tv_login.setBackgroundResource(R.drawable.bg_login_password_gray)
        }

    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) {
            userInfo = it
            if (userInfo == null || userInfo.user == null) {
                BleConnection.Unbind = true
                BleConnection.iFonConnectError = true
                BLEManager.getInstance().dataDispatcher.clearAll()
                RoomUtils.roomDeleteAll()
                Hawk.put("name", "")
                Hawk.put("address", "")
            } else if (userInfo.user.phone != it.user.phone) {
                BLEManager.getInstance().dataDispatcher.clearAll()
                RoomUtils.roomDeleteAll()
                if (it.user.mac.isNullOrEmpty()) {
                    BleConnection.Unbind = true
                    Hawk.put("address", "")
                    Hawk.put("name", "")
                } else {
                    BleConnection.iFonConnectError = true
                    Hawk.put("address", "" + it.user.mac.toUpperCase(Locale.CHINA))
                    Hawk.put("name", "StarLink GT1")
                }

            } else {
                if (it.user.mac.isNullOrEmpty()) {
                    BleConnection.Unbind = true
                    Hawk.put("address", "")
                    Hawk.put("name", "")
                } else {
                    BleConnection.iFonConnectError = true
                    Hawk.put("address", "" + it.user.mac.toLowerCase(Locale.CHINA))
                    Hawk.put("name", "StarLink GT1")
                }
            }
            Hawk.put(Config.database.USER_INFO, it)
            hideWaitDialog()
            TLog.error("hai====" + Gson().toJson(it))
            when {
                it.register -> {
                    JumpUtil.startPasswordActivity(
                        this,
                        edt_mobile.text.toString(),
                        edt_code.text.toString(),
                        areaCode,
                        1
                    )
                    //  JumpUtil.startDeviceInformationActivity(this,true)
                }
                it.user.sex.equals("0") -> {
                    JumpUtil.startDeviceInformationActivity(this, true)
                }
                else -> {
//                    var value = HashMap<String, String>()
//                    value["pid"] = "4533235888218112"
//                    value["appkey"] = "lSEAVca11I2i2sjlT9uCKdVQWlplDXEN"
//                    value["data_source"] = "447" //MD5Util.md5("a111111")
//                    value["age"]=it.user.age
//                    value["height"] = it.user.height
//                    value["weight"] =it.user.weight
//                    if(it.user.sex.equals("1"))
//                    { value["agent"] = "107" }
//                    else
//                        value["agent"] = "108"
//                    value["drug"]="0"
//                    value["posture"]="0"
//                    value["isHeight"]="0"
//                    value["sys"] ="155" //??????
//                    value["dia"] ="105"//??????
//                    value["sn"] ="R1A2021120300001"
//                    value["pco_size"] ="517" //pco??????
//                    var pco="00000000000000000200210000aa0048000000000000010000000000000000b0168af3fced90cd4bd09120cbc03f2c617f7bc2013ae7b607ef7e47d9f4318a3ee43fd7d010c2ec437f953a9c79f37a7294d7806b79e3d04905b9447ca70a798e272bea75d1125abc32323b4fac70cff086962a394246e3d3bf8b055f29441eacc950dfe87265b7fecbb6d748b0748612f7762bbeb082bbb89e895b7c79c7b6ffecdd4b1d1b1a3ddbb826ed3032af8419e6d61e39e9a58df710a63c2bec23ea1f44bc70b54a34fb00fb6cff333414b3510a9e7c83ae6bbb71ea15a8f0d1aaca42ac290185084fc4a7b392873f7feb7e2afb4a264a52b647b64346b1746a81bf31a2ff37abf1dc894be70962f2e8ae304da0bff44f83b0dfad8f473bd961d8fd5c1d5dc9c4181153f12b1a0b7168daca2e1d0a343e4d474f7be2f60491940e60f3583f6da4efbfe690bf8e7db1c3258e2bf7866b9033646500b18ff6ec0578415a9f79054ec440ace860f9b39794bea03d0b0be84662f4efa7e8b854b5e1d9300e0653b9d7b36faa088aa620536b6bb9beccbae8de1011f935843998481dd7fa375d35b811fffb7eb7881a570be85540076615540efd74dc96c1d907b7690128956b8027fd8bd7080eece508447aa7dcebcbfe055f22bbddf69e50501dde5592ce7b53b3fac39df13bd97a85f396ede1b2528eab6f382ebe618c4c0a09ebdddd3fd4773472fa"
//                    value["pco"] =pco  //????????????????????????????????????
//                    value["sts"] =""+System.currentTimeMillis()
//                    value["ets"] =""+(System.currentTimeMillis()+5000)
//
//
//                    TLog.error("value=="+Gson().toJson(value))
//                    mViewModel.changSangUpdateLoad(value)

                    // TLog.error("=="+Hawk.get<LoginBean>(Config.database.USER_INFO).token)
                    JumpUtil.startMainHomeActivity(this)
                    finish()
                }
            }
        }
        mViewModel.resultChangSang.observe(this)
        {
            TLog.error("????????????"+Gson().toJson(it))
        }
        mViewModel.msg.observe(this) {
            hideWaitDialog()
            // ShowToast.showToastLong("????????????")
        }
        mViewModel.areaCodeResult.observe(this)
        {
            hideWaitDialog()
            TLog.error("?????????++" + Gson().toJson(it))
        }
        mViewModel.resultChangSangUpdate.observe(this){
            TLog.error("????????????"+Gson().toJson(it))
        }

    }

    var password = ""
    var md5Password = ""
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_login -> {
                HelpUtil.hideSoftInputView(this)
                TLog.error("111111==" + edt_mobile.text.toString())
                var value = HashMap<String, String>()
                value["phone"] = edt_mobile.text.toString()
                value["areaCode"] = areaCode
                if (edt_code.visibility == View.VISIBLE)
                    value["verifyCode"] = edt_code.text.toString()
                if (edtPassword.visibility == View.VISIBLE)
                    value["password"] = edtPassword.text.toString()
                if (edt_mobile.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong(resources.getString(R.string.string_input_phone))
                    return
                } else if (edtPassword.text.toString().isNullOrEmpty() &&
                    tvPassword.text.toString() == resources.getString(R.string.string_no_pwd_login)
                ) {
                    ShowToast.showToastLong(resources.getString(R.string.string_input_pwd))
                    return
                } else if (edt_code.text.toString().isNullOrEmpty() &&
                    tvPassword.text.toString() == resources.getString(R.string.string_pwd_login)
                ) {
                    ShowToast.showToastLong(resources.getString(R.string.string_input_very_code))
                    return
                }
                if (!checkbox.isSelected) {
                    ShowToast.showToastLong(resources.getString(R.string.string_agree_privacy_desc))
                    return
                }
                HelpUtil.hideSoftInputView(this)
                showWaitDialog("Loading...")
                mViewModel.loginRegistered(value)
            }
            R.id.tvPhoneCode -> {
                SelectPhoneCode.with(this)
                    .setTitle(resources.getString(R.string.string_phone_area))
                    .setStickHeaderColor("#41B1FD")//????????????????????????
                    .setTitleBgColor("#ffffff")//??????????????????????????????
                    .setTitleTextColor("#454545")//??????????????????
                    .select()
            }
            R.id.tv_getcode -> {
                TLog.error("+++" + edt_mobile.text.toString())
                TLog.error("+++$areaCode")

//                var value = HashMap<String, String>()
//                value["thridloginname"] = edt_mobile.text.toString()
//                value["appkey"] = "lSEAVca11I2i2sjlT9uCKdVQWlplDXEN"
//                value["password"] = MD5Util.md5("a111111")
//                TLog.error("value=="+Gson().toJson(value))
//                mViewModel.changSangLogin(value)

                if (edt_mobile.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong(resources.getString(R.string.string_input_phone))
                    return
                }
                countDownTimer?.start()
                edt_code.isFocusable = true
                edt_code.isFocusableInTouchMode = true
                edt_code.requestFocus()
                password = edt_mobile.text.toString() + areaCode + 10861
                md5Password = MD5Util.md5(password)
                TLog.error("md5Password+=" + md5Password)
                tv_getcode.setTextColor(resources.getColor(R.color.color_login_code))
                tv_getcode.setBackgroundResource(R.drawable.login_code_btn_false)
                mViewModel.getVerifyCode(edt_mobile.text.toString(), areaCode, md5Password)
            }
            R.id.tvPassword -> {
                if (tvPassword.text.toString() == resources.getString(R.string.string_pwd_login)) {
                    tvTitle.text = resources.getString(R.string.string_pwd_login)
                    tvPassword.text = resources.getString(R.string.string_no_pwd_login)
                    edtPassword.visibility = View.VISIBLE
                    imgPassword.visibility = View.VISIBLE
                    tv_getcode.visibility = View.INVISIBLE
                    edt_code.visibility = View.INVISIBLE
                    tvForgotPassword.visibility = View.VISIBLE
                    tv_regist.visibility = View.INVISIBLE
                } else {
                    tvTitle.text = resources.getString(R.string.string_phone_login_or_register)
                    tvPassword.text = resources.getString(R.string.string_pwd_login)
                    edtPassword.visibility = View.INVISIBLE
                    imgPassword.visibility = View.INVISIBLE
                    tv_getcode.visibility = View.VISIBLE
                    edt_code.visibility = View.VISIBLE
                    tvForgotPassword.visibility = View.INVISIBLE
                    tv_regist.visibility = View.VISIBLE
                }
            }
            R.id.tvForgotPassword -> {
                if(!TextUtils.isEmpty(edt_mobile.text)) JumpUtil.startForgetPasswordActivity(this,edt_mobile.text.toString()) else
                JumpUtil.startForgetPasswordActivity(this)
            }
            R.id.imgPassword -> {
                if (imgPasswordStatus) {
                    edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    imgPassword.setImageResource(R.mipmap.icon_non)
                } else {
                    edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    imgPassword.setImageResource(R.mipmap.icon_see)
                }
                imgPasswordStatus = !imgPasswordStatus
            }
            R.id.tv_user_service_protocol -> {
                TLog.error("??????")
                JumpUtil.startWeb(this, XingLianApplication.baseUrl + "/agreement/user")
            }
            R.id.tv_privacy_policy -> {
                JumpUtil.startWeb(this, XingLianApplication.baseUrl + "/agreement/privacy")
            }

        }

    }


    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        //????????????
        override fun onTick(l: Long) { //?????????????????????????????????
            tv_getcode.isClickable = false
            tv_getcode.text = (l / 1000).toString() + "s"
        }

        //?????????????????????
        override fun onFinish() { //?????????Button????????????
            tv_getcode.text = resources.getString(R.string.string_try_again_code)
            //???????????????
            tv_getcode.setTextColor(resources.getColor(R.color.color_main_green))
            tv_getcode.setBackgroundResource(R.drawable.login_code_btn)
            tv_getcode.isClickable = true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0x02 && resultCode == Activity.RESULT_OK){
            Permissions()
        }

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