package com.example.xingliansdk.network.api.homeView

import com.example.xingliansdk.bean.PopularScienceBean
import com.example.xingliansdk.network.BaseResult
import com.example.xingliansdk.network.api.javaMapView.MapVoBean
import com.shon.connector.bean.DataBean
import retrofit2.http.*
import java.util.*

interface HomeViewInterface {
    @FormUrlEncoded
    @POST("/health/save_daily_active")
    suspend fun saveDailyActive(
        @Field("startTime") startTime: Long,
        @Field("endTime") endTime: Long,
        @Field("data") data: String
    ): BaseResult<Any>
    @FormUrlEncoded
    @POST("/health/save_heart_rate")
    suspend fun saveHeartRate(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("heartRate") heartRateList: IntArray
    ): BaseResult<Any>
    @FormUrlEncoded
    @POST("/health/save_temperature")
    suspend fun saveTemperature(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("data") heartRateList: IntArray
    ): BaseResult<Any>
    @FormUrlEncoded
    @POST("/health/save_pressure")
    suspend fun savePressure(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("data") data: String
    ): BaseResult<Any>

    @FormUrlEncoded
    @POST("/health/save_blood_pressure")
    suspend fun saveBloodPressure(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("data") data: String
    ): BaseResult<Any>


    @FormUrlEncoded
    @POST("/health/save_blood_oxygen")
    suspend fun saveBloodOxygen(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("bloodOxygen") list: IntArray
    ): BaseResult<Any>
    @POST("/health/save_heart_rate")
    fun saveHeartRate22(@Body user: DataBean?): BaseResult<DataBean?>?

    suspend fun saveHeartRate11(
        @QueryMap value: HashMap<String, String>
    ): BaseResult<Any>
    @FormUrlEncoded
    @POST("/health/save_blood_oxygen")
    suspend fun saveBloodOxygen(
        @FieldMap value: HashMap<String, Any>
    ):BaseResult<Any>

    @FormUrlEncoded
    @POST("/health/save_sleep")
    suspend fun saveSleep(
        @FieldMap value: HashMap<String, Any>
    ):BaseResult<Any>

    @FormUrlEncoded
    @POST("/health/save_sleep")
    suspend fun saveSleep(
        @Field("startTime") startTime: String,
        @Field("endTime") endTime: String,
        @Field("apneaTime") apneaTime: String,
        @Field("apneaSecond") apneaSecond: String,
        @Field("avgHeartRate") avgHeartRate: String,
        @Field("minHeartRate") minHeartRate: String,
        @Field("maxHeartRate") maxHeartRate: String,
        @Field("respiratoryQuality") respiratoryQuality: String,
        @Field("sleepList") sleepList: String
    ):BaseResult<Any>


    @FormUrlEncoded
    @POST("/user/save_user_equip")
    suspend fun saveUserEquip(
        @FieldMap value: HashMap<String, Any>
    ):BaseResult<Any>

    @GET("/popular_science/find_popular_science")
    suspend fun getPopular(
      @QueryMap value: HashMap<String, Any>
    ):BaseResult<PopularScienceBean>
    @GET("/index_card/get_card")
    suspend fun getHomeCard(
      //  @QueryMap value: HashMap<String, Any>
    ):BaseResult<HomeCardVoBean>

    @FormUrlEncoded
    @POST("/motion_info/save")
    suspend fun motionInfoSave(
        @FieldMap value: HashMap<String, Any>
    ):BaseResult<Any>

    @GET("/motion_info/get_list")
    suspend fun motionInfoGetList(
        @QueryMap value: HashMap<String, Any>
    ):BaseResult<MapVoBean>


    //上传实时计步

    @POST("/health/save_real_time_active_info")
    suspend fun saveRealActiveInfo(@QueryMap value: HashMap<String, Any>
    ):BaseResult<Any>
}
