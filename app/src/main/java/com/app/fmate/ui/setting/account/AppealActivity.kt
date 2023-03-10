package com.app.fmate.ui.setting.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.core.widget.addTextChangedListener
import com.app.phoneareacodelibrary.AreaCodeModel
import com.app.phoneareacodelibrary.PhoneAreaCodeActivity
import com.app.phoneareacodelibrary.SelectPhoneCode
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.ui.login.viewMode.LoginViewModel
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_appeal.*
import kotlinx.android.synthetic.main.activity_appeal.edtPhone
import kotlinx.android.synthetic.main.activity_appeal.titleBar
import kotlinx.android.synthetic.main.activity_appeal.tvPhoneCode
import kotlinx.android.synthetic.main.activity_appeal.tvSure


class AppealActivity : BaseActivity<LoginViewModel>(),View.OnClickListener {
    override fun layoutId()= R.layout.activity_appeal
    var areaCode = "86"
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        var areaCode = userInfo.user.areaCode
        var mPhone = userInfo.user.phone
       var phoned= HelpUtil.getPasswordPhone(areaCode,mPhone)
        tvPhone.text = "$phoned"
        tvSure.setOnClickListener(this)
        tvPhoneCode.setOnClickListener(this)
        edtPhone.addTextChangedListener { setSureBtnColor() }
    }
    private fun setSureBtnColor() {

        if (edtPhone.text!!.trim().length >= 4 ) {
            tvSure.setTextColor(resources.getColor(R.color.white))
            tvSure.setBackgroundResource(R.drawable.bg_login_password)
        } else {
            tvSure.setTextColor(resources.getColor(R.color.color_login_code))
            tvSure.setBackgroundResource(R.drawable.bg_login_password_gray)
        }

    }

    var password = ""
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvSure -> {
                if(edtPhone.text.toString().isNullOrEmpty()) {
                    ShowToast.showToastLong("?????????????????????")
                    return
                }
                var value = HashMap<String, String>()
                value["phone"] = userInfo.user.phone
                value["areaCode"] = userInfo.user.areaCode
              //  value["contactAreaCode"] = areaCode
                value["contactPhone"] = edtPhone.text.toString()
                mViewModel.saveAppeal(value)
            }
            R.id.tvPhoneCode -> {
                SelectPhoneCode.with(this)
                    .setTitle("????????????")
                    .setStickHeaderColor("#41B1FD")//????????????????????????
                    .setTitleBgColor("#ffffff")//??????????????????????????????
                    .setTitleTextColor("#454545")//??????????????????
                    .select()
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultSaveAppeal.observe(this){
            ShowToast.showToastLong("????????????")
            AppActivityManager.getInstance().finishActivity(PasswordCheckActivity::class.java)
            AppActivityManager.getInstance().finishActivity(FindPhoneMainActivity::class.java)
            finish()
        }
        mViewModel.msg.observe(this){

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