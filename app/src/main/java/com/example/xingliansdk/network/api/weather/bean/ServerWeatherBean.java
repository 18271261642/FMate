package com.example.xingliansdk.network.api.weather.bean;


import java.io.Serializable;
import java.util.List;

/**
 * 天气bean
 */
public class ServerWeatherBean implements Serializable {

    private String location;

    private String tempMax;

    private String tempMin;

    private String temp;

    private String status;

    private Integer statusCode;

    private String airAqi;

    private String airLevel;

    private String airStatus;

    private String sunrise;

    private String sunset;

    private String humidity;

    private String uvIndex;

    private List<Hourly> hourly;

    private Tomorrow tomorrow;
    private DayAfterTomorrow dayAfterTomorrow;
    private  ThreeDaysFromNow threeDaysFromNow;

    public ThreeDaysFromNow getThreeDaysFromNow() {
        return threeDaysFromNow;
    }

    public void setThreeDaysFromNow(ThreeDaysFromNow threeDaysFromNow) {
        this.threeDaysFromNow = threeDaysFromNow;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
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

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }

    public List<Hourly> getHourly() {
        return hourly;
    }

    public void setHourly(List<Hourly> hourly) {
        this.hourly = hourly;
    }

    public Tomorrow getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(Tomorrow tomorrow) {
        this.tomorrow = tomorrow;
    }

    public DayAfterTomorrow getDayAfterTomorrow() {
        return dayAfterTomorrow;
    }

    public void setDayAfterTomorrow(DayAfterTomorrow dayAfterTomorrow) {
        this.dayAfterTomorrow = dayAfterTomorrow;
    }

    /** 后天数据
     * {
     *     "tempMax": "15",
     *     "sunrise": "06:27",
     *     "sunset": "18:20",
     *     "humidity": "50",
     *     "uvIndex": "5",
     *     "airAqi": "ff",
     *     "airLevel": "ff",
     *     "tempMin": "5",
     *     "airStatus": "ff",
     *     "status": "白天晴",
     *     "statusCode": 1
     * }
     */

    public class ThreeDaysFromNow{
        private String tempMax;
        private String sunrise;
        private String sunset;
        private String humidity;
        private String uvIndex;

        //直接发送
        private String airAqi;
        private String airLevel;
        private String airStatus;


        private String tempMin;
        private String status;
        private int statusCode;

        public void setUvIndex(String uvIndex) {
            this.uvIndex = uvIndex;
        }

        public void setTempMin(String tempMin) {
            this.tempMin = tempMin;
        }

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

        public String getUvIndex() {
            return uvIndex;
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

        public String getTempMin() {
            return tempMin;
        }


        public String getAirStatus() {
            return airStatus;
        }

        public void setAirStatus(String airStatus) {
            this.airStatus = airStatus;
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


    /**
     * {
     *         "location":"116.41,39.92",
     *         "tempMax":"14",
     *         "tempMin":"0",
     *         "temp":"2",
     *         "status":"夜晴",
     *         "statusCode":6,
     *         "airAqi":"56",
     *         "airLevel":"2",
     *         "airStatus":"良",
     *         "sunrise":"06:47",
     *         "sunset":"18:07",
     *         "humidity":"41",
     *         "uvIndex":"4",
     */


    public class Hourly{
        /**
         *
         "statusCode":6,
         "temp":"-1",
         "icon":"150",
         "dateTime":"2022-03-02 03:00:00",
         "status":"夜晴"
         */

        private int statusCode;
        private int temp;
        private int icon;
        private String dateTime;
        private String status;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getTemp() {
            return temp;
        }

        public void setTemp(int temp) {
            this.temp = temp;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


    //明天天气
    public class Tomorrow{
        /**
         *
         {
         "tempMax": "14",
         "sunrise": "06:30",
         "sunset": "18:18",
         "humidity": "42",
         "uvIndex": "4",
         "airAqi": "ff",
         "airLevel": "ff",
         "tempMin": "4",
         "airStatus": "ff",
         "status": "阴",
         "statusCode": 3
         }
         */
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

        @Override
        public String toString() {
            return "Tomorrow{" +
                    "tempMax='" + tempMax + '\'' +
                    ", sunrise='" + sunrise + '\'' +
                    ", sunset='" + sunset + '\'' +
                    ", humidity='" + humidity + '\'' +
                    ", uvIndex=" + uvIndex +
                    ", tempMin=" + tempMin +
                    ", status='" + status + '\'' +
                    ", statusCode=" + statusCode +
                    '}';
        }
    }




    //后天天气
    public class DayAfterTomorrow{
        private String tempMax;
        private String sunrise;
        private String sunset;
        private String humidity;
        private String uvIndex;

        //直接发送
        private String airAqi;
        private String airLevel;
        private String airStatus;


        private int tempMin;
        private String status;
        private int statusCode;

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

        public String getUvIndex() {
            return uvIndex;
        }

        public void setUvIndex(String uvIndex) {
            this.uvIndex = uvIndex;
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
}
