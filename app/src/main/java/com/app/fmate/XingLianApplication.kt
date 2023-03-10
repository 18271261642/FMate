package com.app.fmate

import android.R;
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.multidex.MultiDex
import com.app.fmate.base.BaseApp
import com.app.fmate.bean.room.AppDataBase
import com.app.fmate.broadcast.SystemTimeBroadcastReceiver
import com.app.fmate.dialog.MeasureBpPromptDialog
import com.app.fmate.network.RequestHandler
import com.app.fmate.network.RequestServer
import com.app.fmate.service.AppService
import com.app.fmate.service.SendWeatherService
import com.app.fmate.utils.*
import com.app.fmate.view.DateUtil
import com.orhanobut.hawk.Hawk
import com.orhanobut.hawk.NoEncryption
import com.qweather.sdk.view.HeConfig
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.shon.bluetooth.BLEManager
import com.shon.connector.utils.TLog
import com.sina.weibo.sdk.openapi.IWBAPI
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.Tencent
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import java.lang.ref.WeakReference
import java.util.*
import com.app.fmate.utils.RemoteControlService
import com.app.fmate.utils.RemoteControlService.RCBinder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import com.hjq.gson.factory.GsonFactory
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestInterceptor
import com.hjq.http.model.BodyType
import com.hjq.http.model.HttpHeaders
import com.hjq.http.model.HttpParams
import com.hjq.http.request.HttpRequest
import com.shon.connector.Config
import com.shon.connector.utils.ShowToast
import okhttp3.OkHttpClient
import org.litepal.LitePal
import java.lang.IllegalArgumentException
import kotlin.collections.LinkedHashMap


class XingLianApplication : BaseApp() {
    companion object {
        @kotlin.jvm.JvmField
        var baseUrl: String = BuildConfig.baseUrl
        lateinit var mXingLianApplication: XingLianApplication
        private var mSelectedCalendar: Calendar? = null

        // APP_ID ??????????????????????????????????????????????????????appID
        const val WX_APP_ID = "wxef2f742ea5afd292"
        const val QQ_APP_ID = "1112057867"

        //??????
        const val WB_APP_ID = "1835105991"
        const val WB_REDIRECT_URL = "http://www.sina.com"
        const val WB_SCOPE = ""
        lateinit var mTencent:Tencent
        // IWXAPI ????????????app??????????????????openApi??????
        lateinit var mwxAPI: IWXAPI
        //??????
        lateinit var mWBAPI: IWBAPI
        //pro?????????
        const val serviceUUIDXINLU = "0000180d-0000-1000-8000-00805f9b34fb"
        const val readCharacterXINLIN = "00002a37-0000-1000-8000-00805f9b34fb"

        //?????????
        const val serviceUUID1 = "8F400001-CFB4-14A3-F1BA-F61F35CDDBAF"
        const val mWriteCharactertest = "8F400002-CFB4-14A3-F1BA-F61F35CDDBAF"
        const val readCharactertest = "8F400003-CFB4-14A3-F1BA-F61F35CDDBAF"

        /**
         * SDK????????????????????????
         */
        const val serviceUUID = "1F40EAF8-AAB4-14A3-F1BA-F61F35CDDBAA"
        const val readCharacter = "1F400002-AAB4-14A3-F1BA-F61F35CDDBAA"

        //5.0??????
        const val serviceUUID5 = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
        const val readCharacter5 = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

        //bugly????????????
        const val bugly_appId = "d242b786b4"
        const val appKey = "b3f98779-a899-466b-b2bd-c3e2690c406e"
        const val TIME_START = 946656000
        private var context: WeakReference<Context>? = null
        var ifStartedOrStopped = true

        private var sendWeatherService : SendWeatherService ? = null

        //?????????????????????????????????
        private var isConnected = false

        //???????????????????????????????????????????????????????????????????????????
        private var isForceDial = false;

        //???????????????map??????????????????key???value?????????
        private val categoryMap = LinkedHashMap<String,Int>()

        //???????????????????????????
        private var timeBroadcastReceiver : SystemTimeBroadcastReceiver ? = null


        private var musicControlService : RemoteControlService ?= null


        //?????????????????????????????????
        @SuppressLint("StaticFieldLeak")
        private var measureBpPromptDialog : MeasureBpPromptDialog ?= null


        //??????id?????????d=1????????? id=2 ??????????????????????????????????????????id?????????????????????
        private var productCategoryId = 1

        fun getSelectedCalendar(): Calendar? {
            return mSelectedCalendar
        }

        fun setSelectedCalendar(newCalendar: Calendar?) {
            mSelectedCalendar = newCalendar
        }

        fun getXingLianApplication(): XingLianApplication {
            return mXingLianApplication
        }

        lateinit var sin: AppDataBase

        init {
            SmartRefreshLayout.setDefaultRefreshInitializer { context, layout ->
                layout.setEnableAutoLoadMore(true)//?????????????????????????????????
                layout.setEnableOverScrollDrag(false)//??????????????????
                layout.setEnableOverScrollBounce(true)
                //  layout.setEnableHeaderTranslationContent(false)//??????head?????? ????????????header ????????????false ??????????????????
                layout.setEnableLoadMoreWhenContentNotFull(false)  //?????????????????????????????? more
                layout.setEnableScrollContentWhenRefreshed(true)
//                layout.setDisableContentWhenLoading(true)
//                layout.setDisableContentWhenRefresh(true)
//                layout.setDisableContentWhenLoading(true)
                layout.setPrimaryColorsId(R.color.transparent, R.color.darker_gray)
                layout.layout.tag = "close egg"
            }
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, layout: RefreshLayout? ->//???????????? ?????? ?????? ios ??????
                ClassicsHeader(
                    context
                ).setTimeFormat(DynamicTimeFormat("????????? %s"))
            }
        }


    }
    private  fun initQQ()
    {
        Tencent.setIsPermissionGranted(true)
        mTencent = Tencent.createInstance(QQ_APP_ID, this.applicationContext)
    }
    private fun regToWx() {
        // ??????WXAPIFactory???????????????IWXAPI?????????
        mwxAPI = WXAPIFactory.createWXAPI(this, WX_APP_ID, true)
        // ????????????appId???????????????
        mwxAPI?.registerApp(WX_APP_ID)
        //?????????????????????????????????????????????????????????
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                // ??????app???????????????
//                mwxAPI?.registerApp(WX_APP_ID)
//            }
//        }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
    }





    override fun onCreate() {
        super.onCreate()
        mXingLianApplication = this
        context = WeakReference(applicationContext)
        //??????
        BLEManager.init(this)
        LitePal.initialize(this)
        MultiDex.install(this)

        try {
            val intent = Intent(getContext(), AppService::class.java)
            startService(intent)
        }catch (e : Exception){
            e.printStackTrace()
        }


        //?????????
        AutoSizeConfig.getInstance().unitsManager.setSupportDP(true)
            .setSupportSP(true).supportSubunits = Subunits.PT
        ShowToast.init(this)
       // XXPermissions.setInterceptor(PermissionInterceptor())
        mSelectedCalendar = DateUtil.getCurrentCalendar()
        //?????????
        Hawk.init(applicationContext)
            .setEncryption(NoEncryption())
            .build()
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)

        //??????sdk
        HeConfig.init("HE2105082053391380", "9f28301da43746ac8bf3f223d0930b4f")
        HeConfig.switchToDevService()
        AppActivityManager.getInstance().init(this)
        initQQ()
        regToWx()
        baseUrl = if (HelpUtil.isApkInDebug(mXingLianApplication)) {
            BuildConfig.baseUrlDev
//            BuildConfig.baseUrl
        } else {
            //bugly????????????
            CrashReport.initCrashReport(applicationContext, bugly_appId, true)
            TLog.DEBUG=true
            BuildConfig.baseUrl
        }


        timeBroadcastReceiver = SystemTimeBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)

        val bpIntentFilter = IntentFilter()
        bpIntentFilter.addAction(Config.DEVICE_AUTO_MEASURE_BP_ACTION)

        registerReceiver(broadcastReceiver,bpIntentFilter)

        registerReceiver(timeBroadcastReceiver,intentFilter)

        val musicIntent = Intent(this,RemoteControlService::class.java)
        bindService(musicIntent,serviceConnect(),Context.BIND_ABOVE_CLIENT)

        val weatherIntnet = Intent(this,SendWeatherService::class.java)
        bindService(weatherIntnet,weatherServiceConnection(),Context.BIND_AUTO_CREATE)


        // ???????????????????????????

        // ???????????????????????????
        val okHttpClient = OkHttpClient.Builder()
            .build()

        EasyConfig.with(okHttpClient) // ??????????????????
            .setLogEnabled(true) // ?????????????????????
            .setServer(RequestServer(BodyType.FORM)) // ????????????????????????
            .setHandler(RequestHandler(mXingLianApplication)) // ????????????????????????
            .setRetryCount(1)
            .setInterceptor(object : IRequestInterceptor {
                override fun interceptArguments(
                    httpRequest: HttpRequest<*>,
                    params: HttpParams,
                    headers: HttpHeaders
                ) {
                    headers.put("timestamp", System.currentTimeMillis().toString())
                }
            })
            .into()
        // ?????? Json ??????????????????
        // ?????? Json ??????????????????
        GsonFactory.setJsonCallback { typeToken: TypeToken<*>, fieldName: String, jsonToken: JsonToken ->
            // ????????? Bugly ????????????
            CrashReport.postCatchedException(
                IllegalArgumentException(
                    "?????????????????????$typeToken#$fieldName??????????????????????????????$jsonToken"
                )
            )
        }

    }


     fun getWeatherService(): SendWeatherService? {
        return sendWeatherService
    }


    private fun weatherServiceConnection() = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
           try {
             val binder = service as SendWeatherService.SendWeatherBinder
               sendWeatherService = binder.service
           }catch (e : Exception){
               e.printStackTrace()
           }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            sendWeatherService = null
        }

    }


    fun getRemoteMusic(): RemoteControlService? {
        return musicControlService
    }



    private fun serviceConnect() = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            try {
                val binders = p1 as RCBinder
                musicControlService = binders.service
            }catch (e : Exception){
                e.printStackTrace()
            }

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
           musicControlService = null
        }

    }


    //?????????
    fun getContext(): Context? {
        return context!!.get()
    }

    private var mCount = 0
    private var lastWriteTime: Long = 0
    fun uploadModeCall() {
//        if (!BleConnection.iFonConnectError) {
//            TLog.error("mCount+=$mCount")
        //????????????
        val clickTime = System.currentTimeMillis()
        if (lastWriteTime < clickTime - 10000) {
            lastWriteTime = clickTime
            var type = if (mCount > 0)
                1 else 0
            //  BleWrite.writeSportsUploadModeCall(type)
        }
//        }
    }

    private val activityLifecycleCallbacks: ActivityLifecycleCallbacks =
        object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }


            override fun onActivityDestroyed(activity: Activity) {
//                    TLog.error("onActivityDestroyed+=$activity")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStarted(activity: Activity) {
//                TLog.error("onActivityStarted+=$activity")
                mCount++
                uploadModeCall()
            }

            override fun onActivityStopped(activity: Activity) {
//                TLog.error("onActivityStopped+=$activity")
                mCount--
                uploadModeCall()
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        }

    override fun onTerminate() {
        super.onTerminate()
        val intent = Intent(getContext(), AppService::class.java)
        stopService(intent)
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }


    //??????????????????
    public fun setDeviceConnectedStatus(isConnStatus : Boolean){
        isConnected = isConnStatus
    }
    //??????????????????
    public fun getDeviceConnStatus(): Boolean {
        return isConnected
    }

    //??????????????????????????????
    public fun setIsSyncWriteDial(isSync : Boolean){
        isForceDial = isSync
    }
    //??????????????????????????????
    fun getIsSyncWriteDial(): Boolean {
        return isForceDial
    }

    public fun setCategoryId(id : Int){
        productCategoryId = id
    }

    public fun getCategoryId() : Int{
        return productCategoryId;
    }

   fun setDeviceCategoryKey(key:String,value : Int){
       categoryMap[key] = value
   }

    fun getDeviceCategoryValue(key: String): Int? {

        return categoryMap[key]
    }

    private  val broadcastReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            if(action == Config.DEVICE_AUTO_MEASURE_BP_ACTION){

            }

        }

    }
}