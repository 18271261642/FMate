package com.example.xingliansdk.ui.login

import android.content.Intent
import android.os.Bundle
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.example.xingliansdk.ui.setting.account.FindPhoneMainActivity
import com.example.xingliansdk.utils.AppActivityManager
import com.example.xingliansdk.utils.RoomUtils
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.utils.ThreadUtils
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import kotlinx.android.synthetic.main.activity_srue_log_out.*

class SureLogOutActivity : BaseActivity<UserViewModel>() {

    override fun layoutId() = R.layout.activity_srue_log_out

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvPhone.text = "当前账号：" + userInfo.user.phone
        var code = intent.getStringExtra("code").toString()
        tvLogout.setOnClickListener {
            mViewModel.userDelete(code)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultDelete.observe(this){
            ShowToast.showToastLong("注销成功")
//            ThreadUtils.runOnUiThread({
            Hawk.put(Config.database.USER_INFO, LoginBean())
            if (!Hawk.get<String>("address").isNullOrEmpty()) {
                BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                BLEManager.getInstance().dataDispatcher.clearAll()
                Hawk.put("name", "")
                Hawk.put("address","")
                BleConnection.Unbind=true
                Hawk.put("Unbind","SureLogOut Unbind=true")
            }
            RoomUtils.roomDeleteAll()
            AppActivityManager.getInstance().finishAllActivity()
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
//            },3000)
        }
    }

}