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
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.TLog
import kotlinx.coroutines.launch


fun <T> ViewModel.requestCustomBig(
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
//            TLog.error("it=="+ Gson().toJson(it))
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
                    if(Hawk.get<String>("address").isNotEmpty()) {
                        BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                        BLEManager.getInstance().dataDispatcher.clearAll()
                    }

                    JumpUtil.startLoginActivity(XingLianApplication.getXingLianApplication())
                    AppActivityManager.getInstance()
                        .popAllActivityExceptOne(LoginActivity::class.java)
                    //JumpUtil.startLoginActivity(XingLianApplication.getXingLianApplication())
                }
                0->
                {
                    TLog.error("网络异常")
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