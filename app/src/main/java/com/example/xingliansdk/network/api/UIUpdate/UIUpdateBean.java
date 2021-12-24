package com.example.xingliansdk.network.api.UIUpdate;

public class UIUpdateBean {

    /**
     * fileName : XL_000000R01_20210429.bin
     * ota : https://xlyltest.oss-cn-shenzhen.aliyuncs.com/XL_000000R01_20210429.bin
     * startPosition : 0
     * endPosition : 13905920
     * scope : 0
     */

    private String fileName;
    private String ota;
    private int startPosition;
    private int endPosition;
    private int scope;
    private boolean crc;
    private long versionCode;

    public boolean isCrc() {
        return crc;
    }

    public void setCrc(boolean crc) {
        this.crc = crc;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = versionCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOta() {
        return ota;
    }

    public void setOta(String ota) {
        this.ota = ota;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }
}
