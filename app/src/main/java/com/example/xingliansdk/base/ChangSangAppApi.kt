package com.example.xingliansdk.base

import com.example.xingliansdk.BuildConfig
import com.example.xingliansdk.Config
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.activity.GetHeadInterceptor
import com.example.xingliansdk.base.activity.HeadAddInterceptorChangSang
import com.example.xingliansdk.base.activity.headAddInterceptor
import com.example.xingliansdk.network.api.login.LoginBean
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import com.shon.net.BaseApi
import com.shon.net.OkHttpClientBuild
import com.shon.net.interceptor.TokenAddInterceptor
import com.shon.net.interceptor.TokenErrorInterceptor
import okhttp3.OkHttpClient

abstract class ChangSangAppApi<T>:BaseApi<T>(BuildConfig.baseUrlChangSang) {
    override val tokenKey: String
        get() = "vtoken"
    override val token: String
        get() = if(Hawk.get<LoginBean>(Config.database.USER_INFO)==null
            ||Hawk.get<LoginBean>(Config.database.USER_INFO).token.isNullOrEmpty()) {
                TLog.error("无token")
            "" }
    else {
            Hawk.get<LoginBean>(Config.database.USER_INFO).token
        }

    override val okHttpClient: OkHttpClient
        get() {
            val defaultBuild = OkHttpClientBuild.getDefaultBuild()
            defaultBuild.addInterceptor(HeadAddInterceptorChangSang(token)) //设置 Token拦截器, 添加 token 使用
            defaultBuild.addInterceptor(GetHeadInterceptor(this)) //设置 返回 Token失效 拦截器
            return defaultBuild.build()
        }
}