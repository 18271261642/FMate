package com.app.fmate.base

import com.app.fmate.Config
import com.app.fmate.network.api.login.LoginBean
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import com.shon.net.BaseApi
import com.shon.net.OkHttpClientBuild
import okhttp3.OkHttpClient

abstract class TestApi<T>:BaseApi<T>("http://47.107.35.241:8088") {
    //https://shutianhui.com/dev-service
    //http://47.107.35.241:8088
    override val tokenKey: String
        get() = "authorization"
    override val token: String
        get() = if(Hawk.get<LoginBean>(Config.database.USER_INFO)==null
            ||Hawk.get<LoginBean>(Config.database.USER_INFO).token.isNullOrEmpty()) {
                TLog.error("无token")
            "" }
    else {
//                TLog.error("有token "+ Hawk.get<LoginBean>(Config.database.USER_INFO).token)
            Hawk.get<LoginBean>(Config.database.USER_INFO).token
        }

    override val okHttpClient: OkHttpClient
        get() {
            val defaultBuild = OkHttpClientBuild.getDefaultBuild()
           // defaultBuild.addInterceptor(headAddInterceptor(token,mac)) //设置 Token拦截器, 添加 token 使用
          //  defaultBuild.addInterceptor(TokenErrorInterceptor(this)) //设置 返回 Token失效 拦截器
            return defaultBuild.build()
        }

}