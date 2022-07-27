package com.example.xingliansdk.ui.deviceconn;


/**
 * 已经绑定过的设备bean
 * Created by Admin
 * Date 2022/7/27
 */

public class ConnectedDeviceBean  {


    /**
     * id : 381
     * productName : startLink GT1
     * userId : 9057069076
     * productCategoryId : 1
     * mac : d6:a3:95:76:b3:54
     * productNumber : 8001
     * updateTime : 2022-07-18 21:59:18
     */

    private int id;
    private String productName;
    private String userId;
    private int productCategoryId;
    private String mac;
    private String productNumber;
    private String updateTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ConnectedDeviceBean{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", userId='" + userId + '\'' +
                ", productCategoryId=" + productCategoryId +
                ", mac='" + mac + '\'' +
                ", productNumber='" + productNumber + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
