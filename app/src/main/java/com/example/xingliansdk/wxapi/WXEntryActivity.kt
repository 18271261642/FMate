package com.example.xingliansdk.wxapi

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.util.Log
import com.example.xingliansdk.XingLianApplication
import com.google.gson.Gson
import com.shon.connector.utils.TLog
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import org.greenrobot.eventbus.EventBus

/**
 *
 *Created by frank on 2020/1/2
 */
class WXEntryActivity : Activity(), IWXAPIEventHandler {
    var wxAPI: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wxAPI = XingLianApplication.mwxAPI
        wxAPI!!.handleIntent(intent, this)
    }

    override   fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // wxAPI.handleIntent(getIntent(),this);
        Log.i("ansen", "WXEntryActivity onNewIntent")
    }

    override fun onReq(arg0: BaseReq) {
        Log.i("ansen", "WXEntryActivity onReq:$arg0")
    }

    var i = 0
    override  fun onResp(resp: BaseResp) {
        if (resp.type == ConstantsAPI.COMMAND_SENDAUTH) { //登陆

            val authResp = resp as SendAuth.Resp

            val weiXin =  WeiXin(1, resp.errCode, authResp.code)
            TLog.error("weixn=="+Gson().toJson(weiXin))
            EventBus.getDefault().post(weiXin)
        }
        finish()
    }
//    override fun onResp(resp: BaseResp?) {
//        if (resp!!.type == ConstantsAPI.COMMAND_SENDAUTH) {
//            when (resp!!.errCode) {
//                BaseResp.ErrCode.ERR_AUTH_DENIED, BaseResp.ErrCode.ERR_USER_CANCEL -> {
//                    KotlinToastUtils.showShort(this@WXEntryActivity,"登录取消")
//                    finish()
//                }
//                BaseResp.ErrCode.ERR_OK -> {
//                    // 获取到code
//                    val code = (resp as SendAuth.Resp).code
//                    LocalBroadcastManager.getInstance(baseContext).sendBroadcast(Intent(Configs.LOGIN_BY_WECHAT_RESULT).putExtra(Configs.WX_RESULT_CODE, code))
//                    finish()
//                }
//            }
//        } else if (resp!!.type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
//
//        }
}
