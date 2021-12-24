package com.shon.net

/**
 * Date : 2020/7/30 16:37
 * Package name : net.yt.whale.net
 * Des :
 */
interface ITokenHandler {
    //覆写获取token的方法
    val token: String?

    /**
     * 2020/8/10 新增
     *
     *
     * 获取 token key
     *
     * @return token key
     */
    val tokenKey: String?
    fun onTokenError()
}