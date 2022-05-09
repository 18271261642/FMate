package com.example.xingliansdk.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.utils.HelpUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.shon.connector.utils.TLog
import kotlinx.coroutines.launch

/**
 * code 码 说明
 * 0 请求成功
 * 1 系统异常
 * 2 未登录或 session 已超时!
 * 3 请求超时!
 * 5 操作失败
 * 8 该用户非本 app 用户!
 * 101 缺少必要参数!
 * 102 用户名或密码错误!
 * 106 文件不存在!
 * 111 用户不存在!
 * 118 输入参数不合法!
 * 127 Appkey 不存在
 * 301 设备码无效!
 * 304 APPKEY 非法
 * 305 验证码失效
 * 136 设备 sn 或 imei 冻结!
 * 402 文件已经存在!
 * 406 出现异常, 需要重新上传!
 * 604 操作数据库时发生错误
 * 966 更新了多条记录
 * 1000 设备不存在
 */

fun <T> ViewModel.requestCustomChangs(
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
                0 -> {
                    success.invoke(it.data)
                }
                2 -> {

                }
                -1->
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