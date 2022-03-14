package com.example.xingliansdk.wxapi

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import com.example.xingliansdk.XingLianApplication.Companion.mTencent
import com.example.xingliansdk.XingLianApplication.Companion.mWBAPI
import com.example.xingliansdk.XingLianApplication.Companion.mwxAPI
import com.example.xingliansdk.utils.FileUtils
import com.example.xingliansdk.utils.ShowToast
import com.shon.connector.utils.TLog
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage

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

        val matrix = Matrix()
        val height = myBitmap.height
        val width = myBitmap.width

        matrix.preScale(1f,1f)


        val imgObj = WXImageObject(myBitmap)
        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject=imgObj
        //设置缩略图
//        val thumbBmp = Bitmap.createScaledBitmap(myBitmap, 280, 480, true)

        val thumbBmp = scaleBitmap(myBitmap,0.2f)

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
        thumbBmp?.recycle()

    }


    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private fun scaleBitmap(origin: Bitmap?, ratio: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.preScale(ratio, ratio)
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
       // origin.recycle()
        return newBM
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