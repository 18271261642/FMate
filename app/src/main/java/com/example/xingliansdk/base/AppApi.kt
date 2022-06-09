package com.example.xingliansdk.base

import com.example.xingliansdk.Config
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.activity.headAddInterceptor
import com.example.xingliansdk.network.api.login.LoginBean
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import com.shon.net.BaseApi
import com.shon.net.OkHttpClientBuild
import com.shon.net.interceptor.LoggingInterceptor
import com.shon.net.interceptor.TokenAddInterceptor
import com.shon.net.interceptor.TokenErrorInterceptor
import okhttp3.OkHttpClient
import java.util.*

abstract class AppApi<T>:BaseApi<T>(XingLianApplication.baseUrl) {
    override val tokenKey: String
        get() = "authorization"
    override val token: String
        get() = if(Hawk.get<LoginBean>(Config.database.USER_INFO)==null
            ||Hawk.get<LoginBean>(Config.database.USER_INFO).token.isNullOrEmpty()) {
                TLog.error("无token")
            "" }
    else {
            Hawk.get<LoginBean>(Config.database.USER_INFO).token
        }
    var mac= Hawk.get("address","")!!

    override val okHttpClient: OkHttpClient
        get() {
            mac= Hawk.get("address","")

//            TLog.error("mac=="+mac)
            val defaultBuild = OkHttpClientBuild.getDefaultBuild()
            defaultBuild.addInterceptor(headAddInterceptor(token,mac.toLowerCase(Locale.CHINA))) //设置 Token拦截器, 添加 token 使用
        //    defaultBuild.addInterceptor(LoggingInterceptor())
          //  defaultBuild.addInterceptor(TokenErrorInterceptor(this)) //设置 返回 Token失效 拦截器
            return defaultBuild.build()
        }
}