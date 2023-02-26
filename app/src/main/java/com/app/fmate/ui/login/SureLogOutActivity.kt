package com.app.fmate.ui.login

import android.content.Intent
import android.os.Bundle
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.ui.login.viewMode.UserViewModel
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.utils.RoomUtils
import com.shon.connector.utils.ShowToast
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