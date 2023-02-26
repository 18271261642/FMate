package com.app.fmate.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.ui.login.LoginActivity
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.utils.HelpUtil
import com.app.fmate.utils.JumpUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.qweather.sdk.view.HeContext.context
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.TLog
import kotlinx.coroutines.launch


fun <T> ViewModel.requestCustomWeight(
    block: suspend () -> BaseResult<T>,
    success: (T) -> Unit,
    error: (code: Int, message: String?) -> Unit
) {
    if(!HelpUtil.netWorkCheck(XingLianApplication.getXingLianApplication()))
    {
        TLog.error("无网络拦截")
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
                    ShowToast.showToastLong(context.getString(R.string.cood_2001),5 * 1000)
                    if(Hawk.get<LoginBean>(Config.database.USER_INFO)==null)
                        Hawk.put(Config.database.USER_INFO,LoginBean())
                    else {
                        var userInfo=  Hawk.get<LoginBean>(Config.database.USER_INFO)
                        userInfo.token=""
                        Hawk.put(Config.database.USER_INFO, userInfo)
                    }
                    BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                    BLEManager.getInstance().dataDispatcher.clearAll()
                   // JumpUtil.startLoginActivity(XingLianApplication.getXingLianApplication())
                    JumpUtil.startLoginActivity(XingLianApplication.getXingLianApplication())
                    AppActivityManager.getInstance()
                        .popAllActivityExceptOne(LoginActivity::class.java)
                }
                0->
                {
                    TLog.error("网络异常")
                    error.invoke(0,it.msg)
                    ShowToast.showToastLong("网络异常")
                }
                else -> error.invoke(it.code,it.msg)
            }

        }.onFailure {
            TLog.error("异常数据")
            error.invoke(-1, it.message)
        }
    }

}