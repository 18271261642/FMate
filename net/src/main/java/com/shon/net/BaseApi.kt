package com.shon.net

import android.text.TextUtils
import com.shon.net.interceptor.LoggingInterceptor
import com.shon.net.interceptor.TokenAddInterceptor
import com.shon.net.interceptor.TokenErrorInterceptor
import com.shon.net.util.RetrofitLog
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * Date : 2020/7/21 11:19
 * Package name : net.yt.whale.net
 * Des : BaseApi
 *
 *
 * 本类是所有 API  的基类，只做两件事
 *
 *
 * 1、 创建 Retrofit的实例
 * 2、创建 ApiInterface(即 使用者自己写的 网络接口类) 的实例
 */
abstract class BaseApi<ApiImp>(private val baseUrl: String) : ITokenHandler {
    @JvmField
    protected var mRetrofit: Retrofit? = null//更新 apiInterface//token有更新

    /**
     * 获取  ApiImp 实例
     *
     * @return ApiImp
     */
    @get:Synchronized
   protected open var apiInterface: ApiImp? = null
        get() {
            require(!(TextUtils.isEmpty(baseUrl) || !baseUrl.toLowerCase(Locale.ROOT).startsWith("http"))) { "baseUrl is null  or  baseUrl is not http&https" }

            if (retrofitBuilder == null){
                retrofitBuilder = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
            }

            val token = token
            if (!TextUtils.equals(cacheToken, token)) {
                //token有更新
                RetrofitLog.d("用户 token 有更新")
                cacheToken = token
                getTokenInterceptor(cacheToken,tokenKey)
            }

            if (mRetrofit == null){
                mRetrofit = retrofitBuilder!!.build()
            }


            if (field == null) {
                //更新 apiInterface
                val apiImpClass = getApiImp(this)
                        ?: throw NullPointerException("apiImpClass is null")
                field = mRetrofit!!.create(apiImpClass)
            }
            return field
        }


    private var cacheToken = ""
    private var retrofitBuilder: Retrofit.Builder? = null
    private var tokenAddInterceptor: TokenAddInterceptor? = null

    /**
     * 自定义 retrofitBuilder
     *
     * @param enableHttps  是否启用 Https
     * @param needLog      是否开启 日志
     * @param interceptors 拦截器
     * @return ApiImp
     */
    @Synchronized
    fun getApiInterface(enableHttps: Boolean, needLog: Boolean,
                        converterFactory: Converter.Factory,
                        vararg interceptors: Interceptor?): ApiImp {
        val builder = OkHttpClientBuild.Builder()
        val okHttpClientBuilder = builder.enableHttps(enableHttps).needLog(needLog).setInterceptor(*interceptors).builder()
        val retrofitBuilder = Retrofit.Builder()
                .client(okHttpClientBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
        val apiImpClass = getApiImp(this) ?: throw NullPointerException("apiImpClass is null")
        return retrofitBuilder.build().create(apiImpClass)
    }//设置 Token拦截器, 添加 token 使用
    //设置 返回 Token失效 拦截器
    /**
     * 获取 OkHttpClient,
     * 使用者 如需要自己实现，直接重写此方法
     *
     * @return OkHttpClient
     */
    protected open val okHttpClient: OkHttpClient
        get() {
            val defaultBuild = OkHttpClientBuild.getDefaultBuild()
            defaultBuild.addInterceptor(getTokenInterceptor(cacheToken)) //设置 Token拦截器, 添加 token 使用
          //  defaultBuild.addInterceptor(headAddInterceptor(token,mac.toLowerCase(Locale.CHINA))) //设置 Token拦截器, 添加 token 使用
                defaultBuild.addInterceptor(LoggingInterceptor())
          //  defaultBuild.addInterceptor(TokenErrorInterceptor(this)) //设置 返回 Token失效 拦截器
            return defaultBuild.build()
        }

    /**
     * 获取 TokenAddInterceptor
     *
     * @param token token
     * @return TokenAddInterceptor
     */
    private fun getTokenInterceptor( token: String?, key: String = "" ): TokenAddInterceptor {

        if (tokenAddInterceptor ==null){
            tokenAddInterceptor = TokenAddInterceptor(token)
        }
        tokenAddInterceptor!!.updateToken(key, token)
        return tokenAddInterceptor!!
    }

    /**
     * 返回 ApiImp.class
     *
     * @return ApiImp.class
     */
    private fun getApiImp(baseApi: BaseApi<*>): Class<ApiImp>? {
        val genType = baseApi.javaClass.genericSuperclass ?: return null
        val types = (genType as ParameterizedType).actualTypeArguments
        if (types.isEmpty()){
            return null
        }
        return types[0]!! as Class<ApiImp>
    }

    override val token: String
        get() = ""
    override val tokenKey: String
        get() = ""

    override fun onTokenError() {}

}