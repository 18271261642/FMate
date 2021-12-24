package com.example.xingliansdk.ui.setting.vewmodel

import com.example.xingliansdk.network.api.otaUpdate.OTAInterface
import me.hgj.jetpackmvvm.network.BaseNetworkApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

 val otaInterface by lazy {
    DemoRequest().getApi(OTAInterface::class.java,"")
}
class DemoRequest: BaseNetworkApi() {
    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(httpLoggingInterceptor)
        return builder
    }

    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder
    }



}