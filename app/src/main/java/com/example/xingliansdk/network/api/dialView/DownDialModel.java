package com.example.xingliansdk.network.api.dialView;

import java.util.List;

public class DownDialModel {

    private List<ListDTO> list;

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        /**
         * dialId : 21
         * name : 开心呦
         * fileName : (HAPPY-YO)_0X00000015_202109071105.bin
         * ota : https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/(HAPPY-YO)_0X00000015_202109071105.bin
         * image : https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/dial_null_16309287818470.png
         * content :
         * startPosition : 16777215
         * endPosition : 16777215
         * binSize : 249856
         * sortNumber : 0
         * versionCode : 0
         * state : 当前
         * price : 0
         * downloads : 8
         * charge : false
         */

        private int dialId;
        private String name;
        private String fileName;
        private String ota;
        private String image;
        private String content;
        private int startPosition;
        private int endPosition;
        private int binSize;
        private int sortNumber;
        private int versionCode;
        private String state;
        private int stateCode;
        private int price;
        private int downloads;
        private boolean charge;
        private String delete="0";
        private boolean current;

        private String progress;
        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }
        public boolean isCurrent() {
            return current;
        }

        public void setCurrent(boolean current) {
            this.current = current;
        }

        public int getStateCode() {
            return stateCode;
        }

        public void setStateCode(int stateCode) {
            this.stateCode = stateCode;
        }

        public String getDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }

        public int getDialId() {
            return dialId;
        }

        public void setDialId(int dialId) {
            this.dialId = dialId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public int getBinSize() {
            return binSize;
        }

        public void setBinSize(int binSize) {
            this.binSize = binSize;
        }

        public int getSortNumber() {
            return sortNumber;
        }

        public void setSortNumber(int sortNumber) {
            this.sortNumber = sortNumber;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getDownloads() {
            return downloads;
        }

        public void setDownloads(int downloads) {
            this.downloads = downloads;
        }

        public boolean isCharge() {
            return charge;
        }

        public void setCharge(boolean charge) {
            this.charge = charge;
        }
    }
}
