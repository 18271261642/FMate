package com.example.xingliansdk.network.api.weather.bean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Admin
 * Date 2022/3/18
 */
public class FutureWeatherBean {

    private String tempMax;
    private String sunrise;
    private String sunset;
    private String humidity;
    private int uvIndex;

    //直接发送
    private String airAqi;
    private String airLevel;
    private String airStatus;


    private int tempMin;
    private String status;
    private int statusCode;

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(int uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getAirAqi() {
        return airAqi;
    }

    public void setAirAqi(String airAqi) {
        this.airAqi = airAqi;
    }

    public String getAirLevel() {
        return airLevel;
    }

    public void setAirLevel(String airLevel) {
        this.airLevel = airLevel;
    }

    public String getAirStatus() {
        return airStatus;
    }

    public void setAirStatus(String airStatus) {
        this.airStatus = airStatus;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


}
