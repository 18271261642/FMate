package com.app.fmate.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.listeners.OnGetImgWidthListener
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImgUtil {
    fun loadImage(
        context: Context?,
        url: String?
    ) {
        ThreadUtils.submit {
            var bitmap: Bitmap? = null
            val file =
                XingLianApplication.getXingLianApplication()
                    .getExternalFilesDir(null)?.absolutePath //public绝对路径
            val appDir = File(file, "iChat")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            val pathName = "avatar.jpg"
            val currentFile = File(appDir, pathName)
            try {
                bitmap = Glide.with(context!!)
                    .asBitmap()
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(
                        Target.SIZE_ORIGINAL,
                        Target.SIZE_ORIGINAL
                    ).get()
                if (bitmap != null) {
                    saveImageToGallery(bitmap, currentFile)
                }
            } catch (e: Exception) {
                TLog.error("e==" + e.toString())
            }
        }
        object : Thread() {
            override fun run() {

            }
        }.start()
    }

    /**
     * 保存图片到相册
     *
     * @param bmp
     */
    fun saveImageToGallery(bmp: Bitmap, file: File?) {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
//            TLog.error("保存完成" + file.toString())
            Hawk.put(Config.database.IMG_HEAD, file.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                TLog.error("e.printStackTrace()+=" + e.printStackTrace())
                e.printStackTrace()
            }
        }
    }

    fun loadCircle(ivHead: ImageView, url: Any) {
        Glide.with(ivHead.context).load(url).circleCrop()
            //.skipMemoryCache(true)
//            .override(  Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)//加载原始图大小
            //  .format(DecodeFormat.PREFER_RGB_565)//设置通道减少内存
            // .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(ivHead)
    }


    fun loadCircle(ivHead: ImageView, url: Any,isMain : Boolean) {
        Glide.with(ivHead.context).load(url).circleCrop()
            .placeholder(if(isMain) R.mipmap.icon_head_man else R.mipmap.icon_head_woman)
            //.skipMemoryCache(true)
//            .override(  Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)//加载原始图大小
            //  .format(DecodeFormat.PREFER_RGB_565)//设置通道减少内存
            // .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(ivHead)
    }


    fun loadHead(ivHead: ImageView, url: Any) {
        Glide.with(ivHead.context).load(url)
            .circleCrop()
            .placeholder(R.mipmap.icon_head)
            .dontAnimate()
            .into(ivHead)
    }


    fun loadHead(ivHead: ImageView, url: Any,onGetImgWidthListener: OnGetImgWidthListener) {
        Glide.with(ivHead.context).asBitmap().load(url).into(object  : SimpleTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val width = resource.width
                val height  = resource.height
                onGetImgWidthListener.backImgWidthAndHeight(width,height)

            }

        });
//            .circleCrop()
//            .placeholder(R.mipmap.icon_head)
//            .dontAnimate()
//            .into(ivHead)
    }



    fun loadMeImgDialCircle(ivHead: ImageView, url: Any) {
        Glide.with(ivHead.context).load(url).circleCrop()
            .into(ivHead)
    }

    fun loadHomeCard(ivHead: ImageView, url: Any) {
        Glide.with(ivHead.context).load(url)
            .placeholder(R.mipmap.ic_launcher)
            .dontAnimate()
            .into(ivHead)
    }

    fun loadRound(ivHead: ImageView, url: Any, round: Int = 10) {
        Glide.with(ivHead.context).load(url).apply(roundOptions(round))
            .placeholder(R.mipmap.ic_launcher).dontAnimate().into(ivHead)
    }

    fun loadMapImg(ivHead: ImageView, url: Any, round: Int = 10) {
        Glide.with(ivHead.context).load(url).apply(roundOptions(round))
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)//设置通道减少内存
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.mipmap.ic_launcher).dontAnimate().into(ivHead)
    }


    private fun roundOptions(size: Int = 10) = RequestOptions.bitmapTransform(RoundedCorners(size))
}