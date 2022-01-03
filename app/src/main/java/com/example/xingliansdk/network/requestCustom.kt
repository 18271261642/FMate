package com.example.xingliansdk.network

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.ui.login.LoginActivity
import com.example.xingliansdk.utils.AppActivityManager
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.ShowToast
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.TLog
import kotlinx.coroutines.launch
import retrofit2.Call


fun <T> ViewModel.requestCustom(
    block: suspend () -> BaseResult<T>,
    success: (T) -> Unit,
    error: (code: Int, message: String?) -> Unit
) {


    if(!HelpUtil.netWorkCheck(XingLianApplication.getXingLianApplication()))
    {
        TLog.error("无网络拦截")
        //无网情况返回回调
        error.invoke(-2, "网络出问题了，快去检查一下吧～")
        return
    }
    viewModelScope.launch {
        kotlin.runCatching {
            block()
        }.onSuccess {
            TLog.error("it=="+ Gson().toJson(it))
            when (it.code) {
                200 -> {
                    success.invoke(it.data)
                }
                2001 -> {
                    ShowToast.showToastLong(XingLianApplication.getXingLianApplication().getString(R.string.cood_2001),5 * 1000)
                    if(Hawk.get<LoginBean>(Config.database.USER_INFO)==null)
                    Hawk.put(Config.database.USER_INFO,LoginBean())
                    else {
                      var userInfo=  Hawk.get<LoginBean>(Config.database.USER_INFO)
                        userInfo.token=""
                        Hawk.put(Config.database.USER_INFO, userInfo)
                    }
                    if (!Hawk.get<String>("address").isNullOrEmpty()) {
                        BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                        BLEManager.getInstance().dataDispatcher.clearAll()
                    }
                    JumpUtil.startLoginActivity(XingLianApplication.getXingLianApplication())
                    AppActivityManager.getInstance()
                        .popAllActivityExceptOne(LoginActivity::class.java)

                }
                0->
                {
                    TLog.error("网络异常")
                    ShowToast.showToastLong("网络异常")
                }
                else -> error.invoke(it.code,it.msg)
            }

        }.onFailure {
            TLog.error("onFailure+="+it.toString())
        }
    }

}