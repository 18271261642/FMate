package com.app.fmate.ui.web

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.annotation.RequiresApi
import com.app.fmate.R
import com.app.fmate.XingLianApplication
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : BaseActivity<BaseViewModel>() {


    override fun layoutId()= R.layout.activity_web
    var url=""
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()

        url=intent.getStringExtra("url").toString()
        when (url) {
            XingLianApplication.baseUrl+"/agreement/privacy" -> titleBar.setTitleText(resources.getString(R.string.string_user_privacy))
            XingLianApplication.baseUrl+"/agreement/user" -> titleBar.setTitleText(resources.getString(R.string.string_user_agreement))
            XingLianApplication.baseUrl+"/agreement/register" -> titleBar.setTitleText(resources.getString(R.string.string_account_register_protocol))
            else -> titleBar.setTitleText(resources.getString(R.string.string_health_data))
        }
        initWebViewSetting()
        web.loadUrl(url)

    }
    private fun initWebViewSetting() {
        val webSetting = web.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.textZoom = 100
        webSetting.builtInZoomControls = false
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        window.setFormat(PixelFormat.TRANSLUCENT)
        webSetting.loadWithOverviewMode = true
        webSetting.defaultTextEncodingName = "UTF-8"
        webSetting.allowContentAccess = true // 是否可访问Content Provider的资源，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSetting.allowFileAccessFromFileURLs = false
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSetting.allowUniversalAccessFromFileURLs = false
        // 支持缩放
        webSetting.setSupportZoom(true)
        web.webChromeClient =
           CustomWebChromeClient()
        web.webViewClient =
        CustomWebViewClient()

    }
    private class CustomWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(webView: WebView, newProgress: Int) {
            super.onProgressChanged(webView, newProgress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            TLog.error("当前页面标题++$title")
            //this@WebActivity.title = title
        }
    }

    private class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(
            view: WebView,
            url: String,
            favicon: Bitmap?
        ) {
            //定义一个bitmap，避免favicon为空，引起的崩溃
            super.onPageStarted(view, url, favicon)
            TLog.error("url+++$url")
        }

        override fun onPageFinished(
            view: WebView,
            url: String
        ) {
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            val hit = view.hitTestResult
            //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
            return if (url.startsWith("http://") || url.startsWith("https://")) { //加载的url是http/https协议地址
                view.loadUrl(url)
                false //返回false表示此url默认由系统处理,url未加载完成，会继续往下走
            } else { //加载的url是自定义协议地址
                true
            }
        }

        override fun shouldInterceptRequest(
            view: WebView,
            url: String
        ): WebResourceResponse? { // mUrlList.add(url);
            return null
        }

        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }
    }
}