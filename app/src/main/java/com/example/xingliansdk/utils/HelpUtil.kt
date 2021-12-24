package com.example.xingliansdk.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.xingliansdk.R
import com.example.xingliansdk.XingLianApplication
import com.shon.connector.utils.TLog
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

object HelpUtil {
    fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            XingLianApplication.getXingLianApplication().resources.displayMetrics
        ).toInt()
    }

    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
    fun sp2px(spVal: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spVal.toFloat(),
            ResUtil.getResources().displayMetrics
        ).toInt()
    }

    fun px2sp(context: Context, pxValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f)
    }

    private const val DECIMAL_DIGITS = 1 //小数的位数
    fun decimalNumber(s: String): String {
        if (!TextUtils.isEmpty(s)) {
            if (s.contains(".")) {
                if (s.length - 1 - s.indexOf(".") > DECIMAL_DIGITS) {
                    var s1 = s.subSequence(
                        0,
                        s.indexOf(".") + DECIMAL_DIGITS + 1
                    )
                    return s1.toString()
//

                }
            }
            if (s.trim().substring(0) == ".") {
                var s1 = "0$s"
                return s1
//                editTemper.setText(s1)
//                editTemper.setSelection(2)
            }
            if (s.startsWith("0")
                && s.trim().length > 1
            ) {
                if (s.substring(1, 2) != ".") {
//                    editTemper.setText(s.subSequence(0, 1))
//                    editTemper.setSelection(1)
                    return s.subSequence(0, 1).toString()
                }
            }
        }
        return "0"
    }

    /**
     * 判断服务是否运行
     *
     * @param context
     * @param cls
     * @return
     */
    fun isServiceRunning(
        context: Context,
        cls: Class<out Service?>?
    ): Boolean {
        var isServiceRunning = false
        val collectorComponent = cls?.let { ComponentName(context, it) }
        val manager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (manager != null) {
            val runningServices: List<ActivityManager.RunningServiceInfo> =
                manager.getRunningServices(Int.MAX_VALUE)
            if (runningServices != null) {
                for (service in runningServices) {
                    if (service.service == collectorComponent) {
                        if (service.pid === Process.myPid()) {
                            isServiceRunning = true
                        }
                    }
                }
            }
        }
        return isServiceRunning
    }


    @Throws(IOException::class)
    fun InputStreamToByte(file: InputStream): ByteArray? {
        val byte = ByteArrayOutputStream()
        var ch: Int
        while (file.read().also { ch = it } != -1) {
            byte.write(ch)
        }
        val data: ByteArray = byte.toByteArray()
        byte.close()
        return data
    }

    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    @Synchronized
    fun getVersionName(context: Context): String? {
        try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            return packageInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    fun getVersionCode(context: Context): Int {
        try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            return packageInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
    /**
     * 隐藏软键盘
     * hideSoftInputView
     *
     * @param
     * @return void
     * @throws
     * @Title: hideSoftInputView
     * @Description: TODO
     */
    fun hideSoftInputView(context: Activity) {
        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (context.window.attributes.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (context.currentFocus != null)
                manager.hideSoftInputFromWindow(
                    context.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
        }
    }


    /**
     * 角度转换成度分秒
     * @param dou
     * @return
     */
    fun getFormatter(dou: Double): String? {
        val minute = (dou - dou.toInt()) * 60
        val second = (minute - minute.toInt()) * 60
        val mthreematter: NumberFormat = DecimalFormat("#00")
        val sb = StringBuffer()
//        sb.append(dou.toInt().toString() + "°")
        sb.append(minute.toInt().toString() + "′")
        sb.append(mthreematter.format(second) + "″")
        return sb.toString()
    }

    /**
     * 获取AndroidId
     *
     * @param context
     * @return
     */
    fun getAndroidId(context: Context?): String? {
        if (context == null) {
            return ""
        }
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        if (TextUtils.isEmpty(androidId)) {
            val id = (Math.random() * 1000000000000000L).toLong()
            return id.toString()
        }
        return if (TextUtils.isEmpty(androidId)) "" else androidId
    }
    fun getSpan(value: String, value1: String = "", size: Int = 14): SpannableString
    {
      var  mStr =
            SpannableString(value + value1)
        mStr.setSpan(
            AbsoluteSizeSpan(size, true),
            value.length,
            mStr.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return mStr
    }

    fun getSpan(value: String, value1: String = "", size: Int = 14,color: Int= R.color.sub_text_color): SpannableString
    {
        var  mStr =
            SpannableString(value + value1)
        mStr.setSpan(
            ForegroundColorSpan(
                XingLianApplication.getXingLianApplication().resources.getColor(
                    color
                )
            ),

            value.length,
            mStr.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mStr.setSpan(
            AbsoluteSizeSpan(size, true),
            value.length,
            mStr.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return mStr
    }

    fun getSpan(start: String, value: String, value1: String = "", size: Int = 14): SpannableString
    {
        var  mStr =
            SpannableString(start + value + value1)
        mStr.setSpan(
            AbsoluteSizeSpan(size, true),
            0,
            start.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mStr.setSpan(
            AbsoluteSizeSpan(size, true),
            (start.length + value.length),
            mStr.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return mStr
    }

    fun getSpan(
        hours: String,
        hoursValue: String = "",
        mine: String,
        minValue: String = "",
        color: Int,
        size: Int = 14
    ): SpannableString
    {
        var  mStr =
            SpannableString(hours + hoursValue + mine + minValue)
        mStr.setSpan(
            AbsoluteSizeSpan(size, true),
            hours.length,
            (hours + hoursValue).length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mStr.setSpan(
            AbsoluteSizeSpan(size, true),
            (hours + hoursValue + mine).length,
            (hours + hoursValue + mine + minValue).length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mStr.setSpan(
            ForegroundColorSpan(
                XingLianApplication.getXingLianApplication().resources.getColor(
                    color
                )
            ),
            hours.length,
            (hours + hoursValue).length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mStr.setSpan(
            ForegroundColorSpan(
                XingLianApplication.getXingLianApplication().resources.getColor(
                    color
                )
            ),
            (hours + hoursValue + mine).length,
            (hours + hoursValue + mine + minValue).length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return mStr
    }
    fun getResource(imageName: String?): Int {
        val ctx = XingLianApplication.mXingLianApplication.baseContext
        //如果没有在"mipmap"下找到imageName,将会返回0
        return ResUtil.getResources().getIdentifier(imageName, "mipmap", ctx.packageName)
    }

    fun getWinds(mContext: Context) {
        val wm =
            mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mTm =
            mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels // 屏幕宽度（像素）
        val height = dm.heightPixels // 屏幕高度（像素）
        val density = dm.density // 屏幕密度（0.75 / 1.0 / 1.5）
        val densityDpi = dm.densityDpi // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        val screenWidth = (width / density).toInt() // 屏幕宽度(dp)
        val screenHeight = (height / density).toInt() // 屏幕高度(dp)
        val mtype = Build.MODEL // 手机型号
        val mtyb = Build.BRAND //手机品牌
        TLog.error("屏幕宽度（像素）：$width")
        TLog.error("屏幕高度（像素）：$height")
        TLog.error("屏幕密度（0.75 / 1.0 / 1.5）：$density")
        TLog.error("屏幕密度dpi（120 / 160 / 240）：$densityDpi")
        TLog.error("屏幕宽度（dp）：$screenWidth")
        TLog.error("屏幕高度（dp）：$screenHeight")
    }

    /**
     * 设置数字含几位小数点
     */
    fun setNumber(value: Double, newScale: Int):BigDecimal?
    {
//        TLog.error("value==" + value)
        return if(value==0.0||value.isNaN()) {
            BigDecimal(0)
        } else
            BigDecimal(value).setScale(newScale, BigDecimal.ROUND_HALF_DOWN)
    }

    /**
     * 判断当前应用是否是debug状态
     */
    fun isApkInDebug(context: Context): Boolean {
        return try {
            val info = context.applicationInfo
            info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: java.lang.Exception) {
            false
        }
    }

        // ^[A-Za-z0-9]+
    fun passwordStatus(input: CharSequence?): Boolean {
        val regex = ".*[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]"
        return isMatch(regex, input)
    }

    /**
     * 判断字符串是否为数字
     * 包括负数
     * @param str
     * @return
     */
    fun isNumerEX(str: String?): Boolean {
        val pattern = Pattern.compile("-?[0-9]+.?[0-9]+")
        val pattern1 = Pattern.compile("[0-9]*")
        return pattern.matcher(str).matches() || pattern1.matcher(str).matches()
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    fun isNumeric(str: String?): Boolean {
        val pattern = Pattern.compile("[0-9]*")
        return pattern.matcher(str).matches()
    }

    fun isNumericNotSize(str: String): Boolean {
        val regex = "-?\\d+(\\.\\d+)?".toRegex()
        if(str.matches(regex))
        {
            return str?.toDouble()!!>0
        }
        return false
    }
    private fun isMatch(regex: String?, input: CharSequence?): Boolean {
        return input != null && input.isNotEmpty() && Pattern.matches(regex, input)
    }

    // 网络连接判断
    fun netWorkCheck(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info?.isConnected ?: false
    }

    fun getPasswordPhone(areaCode: String = "86", mPhone: String):String
    {
        if(mPhone.length<7&&areaCode.toInt() == 86)
            return ""
        return if (areaCode.toInt() == 86)
            mPhone.replace(mPhone.substring(3, 7), "****")
        else
            mPhone.replace(mPhone.substring(0, mPhone.length - 4), "****")
    }
    /**
     * 判断该设备是否支持计歩
     *
     * @param context
     * @return
     */
    fun isSupportStepCountSensor(context: Context): Boolean {
        // 获取传感器管理器的实例
        val sensorManager = context
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        return countSensor != null || detectorSensor != null
    }

}

