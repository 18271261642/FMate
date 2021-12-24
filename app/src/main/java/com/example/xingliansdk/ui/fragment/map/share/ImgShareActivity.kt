package com.example.xingliansdk.ui.fragment.map.share

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.bumptech.glide.Glide
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.ui.fragment.map.newmap.AmapHistorySportActivity
import com.example.xingliansdk.utils.ExcelUtil.mapImgPath
import com.example.xingliansdk.utils.FileUtils
import com.example.xingliansdk.utils.ImgUtil
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.widget.TitleBarLayout
import com.example.xingliansdk.wxapi.LoginOrShareUtils
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.common.UiError
import com.sina.weibo.sdk.openapi.SdkListener
import com.sina.weibo.sdk.openapi.WBAPIFactory
import com.sina.weibo.sdk.share.WbShareCallback
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX

import kotlinx.android.synthetic.main.activity_img_share.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ImgShareActivity : BaseActivity<BaseViewModel>(),View.OnClickListener,WbShareCallback {

    var bitmap:Bitmap?=null
    override fun layoutId()=R.layout.activity_img_share

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        initWBSdk()
        tvShareWX.setOnClickListener(this)
        tvShareFriend.setOnClickListener(this)
        tvShareQQ.setOnClickListener(this)
        tvShareWeiBo.setOnClickListener(this)
        titleBar.setTitleBarListener(object :TitleBarLayout.TitleBarListener{
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
            }

            override fun onActionClick() {
            }

        })
        ImgUtil.loadMapImg(imgShare, "${AmapHistorySportActivity.fileName}/mapImgShare.png")
        bitmap= FileUtils.getBitmapFromSDCard("${AmapHistorySportActivity.fileName}/mapImgShare.png")
    }

    /**
     * 初始化sdk。
     * 理论上使⽤前只要初始化⼀次即可，具体分享及授权登录时将不需要再次初始化。
     */
    private fun initWBSdk() {
        val authInfo = AuthInfo(this, XingLianApplication.WB_APP_ID, XingLianApplication.WB_REDIRECT_URL, XingLianApplication.WB_SCOPE)
        XingLianApplication.mWBAPI = WBAPIFactory.createWBAPI(this) // 传Context即可，不再依赖于Activity
        XingLianApplication.mWBAPI.registerApp(this, authInfo, object : SdkListener {
            override fun onInitSuccess() {}
            override fun onInitFailure(e: Exception) {}
        })
    }
    override fun createObserver() {
        super.createObserver()
    }

    override fun onClick(v: View) {
         when(v.id)
         {
             R.id.tvShareWX->
             {
                 bitmap?.let {
                     TLog.error("tvShareWX===")
                     LoginOrShareUtils.WXshare( SendMessageToWX.Req.WXSceneSession,
                         it )
                 }
             }
             R.id.tvShareFriend->
             {
                 LoginOrShareUtils.WXshare( SendMessageToWX.Req.WXSceneTimeline,
                     bitmap!! )
             }
             R.id.tvShareQQ->
             {
                 TLog.error("tvShareQQ==")
                 LoginOrShareUtils.onClickShare(this,"${AmapHistorySportActivity.fileName}/mapImgShare.png")
             }
             R.id.tvShareWeiBo->
             {
                 LoginOrShareUtils.weiBoShare(this,bitmap!!)
             }
         }
    }

    override fun onComplete() {
        ShowToast.showToastLong("分享成功")
    }

    override fun onError(p0: UiError?) {
        ShowToast.showToastLong("分享失败:"+p0?.errorMessage)
    }

    override fun onCancel() {
         ShowToast.showToastLong("分享已取消")
    }

}