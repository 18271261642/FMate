package com.example.xingliansdk.wxapi

/**
 *
 *Created by frank on 2020/1/3
 */
data class WxUserInfo(
    val city: String,
    val country: String,
    val headimgurl: String,
    val language: String,
    val nickname: String,
    val openid: String,
    val privilege: List<Any>,
    val province: String,
    val sex: Int,
    val unionid: String
)