package com.app.fmate.network.api.appUpdate;

public class AppUpdateBean {

    /**
     * fileName : ai_health.apk
     * ota : https://xlylfile.oss-cn-shenzhen.aliyuncs.com/ai_health.apk
     * versionCode : 1103
     * content : 优化用户体验
     * forceUpdate : true
     */

    private String fileName;
    private String ota;
    private int versionCode;
    private String content;
    private boolean forceUpdate;

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

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
