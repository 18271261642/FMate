package com.app.fmate.wxapi

/**
 *
 *Created by frank on 2020/1/2
 */
class WeiXinToken {
      val access_token // 接口调用凭证
            : String? = null
     val expires_in // access_token接口调用凭证超时时间，单位（秒）
            : String? = null
     val refresh_token // 用户刷新access_token
            : String? = null
     val openid // 授权用户唯一标识
            : String? = null
     val scope // 用户授权的作用域，使用逗号（,）分隔
            : String? = null
     val unionid // 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
            : String? = null

    // 错误返回样例
     val errcode = 0 // 错误码

     val errmsg // 错误说明
            : String? = null
}