package com.app.fmate.ui.fragment.map.share

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.app.fmate.ui.fragment.map.newmap.AmapHistorySportActivity
import com.app.fmate.utils.FileUtils
import com.app.fmate.utils.ImgUtil
import com.shon.connector.utils.ShowToast
import com.app.fmate.widget.TitleBarLayout
import com.app.fmate.wxapi.LoginOrShareUtils
import com.gyf.barlibrary.ImmersionBar
import com.hjq.permissions.XXPermissions
import com.shon.connector.utils.TLog
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.common.UiError
import com.sina.weibo.sdk.openapi.SdkListener
import com.sina.weibo.sdk.openapi.WBAPIFactory
import com.sina.weibo.sdk.share.WbShareCallback
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX

import kotlinx.android.synthetic.main.activity_img_share.*

class ImgShareActivity : BaseActivity<BaseViewModel>(),View.OnClickListener,WbShareCallback {

    var bitmap:Bitmap?=null
    override fun layoutId()=R.layout.activity_img_share

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        initWBSdk()

        getPermission()

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



    private fun getPermission(){
        XXPermissions.with(this).permission(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).request { permissions, all ->

        };
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


    //分享图片
    private fun shareImage(context: Context, uri: Uri?, title: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        //context.startActivity(Intent.createChooser(intent, title))
        startActivityForResult(intent,0x00)
    }
}