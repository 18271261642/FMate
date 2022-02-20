package com.example.xingliansdk.wxapi

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.core.content.FileProvider
import com.example.xingliansdk.BuildConfig
import com.example.xingliansdk.XingLianApplication.Companion.mTencent
import com.example.xingliansdk.XingLianApplication.Companion.mWBAPI
import com.example.xingliansdk.XingLianApplication.Companion.mwxAPI
import com.example.xingliansdk.utils.FileUtils
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import java.io.File

@SuppressLint("StaticFieldLeak")
object LoginOrShareUtils {
//    private var listener: QQLoginListener? = null
    private var mContext: Activity? = null
//    private var mInfo: UserInfo? = null
    private  var  loginUtil:LoginOrShareUtils?=null


    fun WXshare(mTargetScene: Int, myBitmap: Bitmap) { //初始化一个WXWebpageObject，填写url
        if (!mwxAPI.isWXAppInstalled) { //提醒用户没有按照微信
            ShowToast.showToastLong("您还没安装微信,请下载微信后再试!")
            return
        }
        val imgObj = WXImageObject(myBitmap)
        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject=imgObj
        //设置缩略图
        val thumbBmp = Bitmap.createScaledBitmap(myBitmap, 280, 480, true)
      //  myBitmap.recycle()
        msg.thumbData = FileUtils.bmpToByteArray(thumbBmp, true)
        //构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene = mTargetScene
//            var openid=Hawk.get<String>("openid")
//            if(openid.isNotEmpty())
//                req.userOpenId = openid
        //调用api接口，发送数据到微信
        mwxAPI.sendReq(req)

    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }

      fun onClickShare(mActivity: Activity, url: String) {
          if(!mTencent.isQQInstalled(mActivity))
          {
              ShowToast.showToastLong("您还没安装QQ,请下载QQ后再试!")
              return
          }
        val params = Bundle()
//          val file = File(url)
          val uri =  Uri.parse(url)
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, uri.path)
          TLog.error("url==" + url)
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mActivity.packageName)
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
        mTencent.shareToQQ(mActivity, params, BaseUIListener())
    }

    fun weiBoShare(context: Activity, myBitmap: Bitmap)
    {
        if(!mWBAPI.isWBAppInstalled)
        {
            ShowToast.showToastLong("您还没安装微博,请下载微博后再试!")
            return
        }
        val message = WeiboMultiMessage()
      //  val textObject = TextObject()
        val imageObject = ImageObject()
     //   val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.share_image)
        imageObject.setImageData(myBitmap)
        message.imageObject = imageObject
        mWBAPI.shareMessage(context, message, false)
    }
}