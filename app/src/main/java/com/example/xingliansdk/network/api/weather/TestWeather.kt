package com.example.xingliansdk.network.api.weather

import com.shon.connector.utils.TLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun testWeather() {
    GlobalScope.launch(Dispatchers.IO) {
        val get3dWeather = WeatherApi.weatherApi.get3dWeather("0526bcc66adc492981abc39aa6c153e0", "116.41,39.92")
        TLog.log("get3dWeather:$get3dWeather")
    }
}

