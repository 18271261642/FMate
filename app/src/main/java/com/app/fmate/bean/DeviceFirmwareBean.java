package com.app.fmate.bean;

public class DeviceFirmwareBean {
    String productNumber;
    int version;
    String versionName;
    String nowMaC;
    String mac;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getNowMaC() {
        return nowMaC;
    }

    public void setNowMaC(String nowMaC) {
        this.nowMaC = nowMaC;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public DeviceFirmwareBean() {
    }

    public DeviceFirmwareBean(String productNumber,
                              String versionName,
                              int version,
                              String nowMaC,
                              String mac) {
        this.productNumber = productNumber;
        this.version = version;
        this.versionName=versionName;
        this.nowMaC = nowMaC;
        this.mac = mac;

    }

    @Override
    public String toString() {
        return "DeviceFirmwareBean{" +
                "productNumber='" + productNumber + '\'' +
                ", version=" + version +
                ", versionName='" + versionName + '\'' +
                ", nowMaC='" + nowMaC + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
