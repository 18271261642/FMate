package com.sn.map.bean;

/**
 * Created by Admin
 * Date 2022/4/7
 */
public class SearLocalBean {


    /**
     * {
     *         "a": "BV10249897",
     *         "b": {
     *             "a": 22.77792,
     *             "b": 113.84831
     *         },
     *         "c": "松岗公园(地铁站)",
     *         "d": "广东省深圳市宝安区",
     *         "e": "440306",
     *         "f": "6号线/光明线",
     *         "g": "150500",
     *         "h": ""
     *     }
     */

    //城市
    private String city;


    //名称
    private String name;

    //地址描述
    private String district;

    //邮编
    private String addressCode;

    //纬度
    private double lat;

    //经度
    private double lon;


    //poid
    private String poiId;


    //线路Id
    private int line_id;

    //精确度定位用到
    private float accuracy;


    //一个字符串
    private String txt;




    public SearLocalBean() {
    }

    public SearLocalBean(String name, String district, String addressCode, double lat, double lon) {
        this.name = name;
        this.district = district;
        this.addressCode = addressCode;
        this.lat = lat;
        this.lon = lon;
    }


    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getLine_id() {
        return line_id;
    }

    public void setLine_id(int line_id) {
        this.line_id = line_id;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "SearLocalBean{" +
                "name='" + name + '\'' +
                ", district='" + district + '\'' +
                ", addressCode='" + addressCode + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
