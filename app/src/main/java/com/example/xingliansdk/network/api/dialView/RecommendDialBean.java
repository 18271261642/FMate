package com.example.xingliansdk.network.api.dialView;

import java.util.List;

public class RecommendDialBean {


    private List<ListDTO> list;

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        /**
         * typeName : 最新
         * type : 1
         * typeList : [{"name":"七夕-鸟","fileName":"Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin","ota":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/1630312747106.gif","content":"299","startPosition":16777215,"endPosition":16777215,"binSize":667648,"sortNumber":0,"versionCode":0},{"name":"七夕-鸟","fileName":"Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin","ota":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/1630312747106.gif","content":"299","startPosition":16777215,"endPosition":16777215,"binSize":667648,"sortNumber":0,"versionCode":0},{"name":"七夕-鸟","fileName":"Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin","ota":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/1630312747106.gif","content":"299","startPosition":16777215,"endPosition":16777215,"binSize":667648,"sortNumber":0,"versionCode":0},{"name":"七夕-桥","fileName":"Tanabata_Milky_Way_Magpie_Bridge _0X00000002_202108171857_XL.bin","ota":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/Tanabata_Milky_Way_Magpie_Bridge _0X00000002_202108171857_XL.bin","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/1630312751600.png","content":"998","startPosition":16777215,"endPosition":16777215,"binSize":663552,"sortNumber":0,"versionCode":0},{"name":"七夕-桥","fileName":"Tanabata_Milky_Way_Magpie_Bridge _0X00000002_202108171857_XL.bin","ota":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/Tanabata_Milky_Way_Magpie_Bridge _0X00000002_202108171857_XL.bin","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/1630312751600.png","content":"998","startPosition":16777215,"endPosition":16777215,"binSize":663552,"sortNumber":0,"versionCode":0},{"name":"七夕-桥","fileName":"Tanabata_Milky_Way_Magpie_Bridge _0X00000002_202108171857_XL.bin","ota":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/Tanabata_Milky_Way_Magpie_Bridge _0X00000002_202108171857_XL.bin","image":"https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/1630312751600.png","content":"998","startPosition":16777215,"endPosition":16777215,"binSize":663552,"sortNumber":0,"versionCode":0}]
         */

        private String typeName;
        private int type;
        private List<TypeListDTO> typeList;

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<TypeListDTO> getTypeList() {
            return typeList;
        }

        public void setTypeList(List<TypeListDTO> typeList) {
            this.typeList = typeList;
        }

        public static class TypeListDTO {
            /**
             * name : 七夕-鸟
             * fileName : Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin
             * ota : https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/Tanabata_mandarin_duck_0X00000001_202108131755_XL.bin
             * image : https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/1630312747106.gif
             * content : 299
             * startPosition : 16777215
             * endPosition : 16777215
             * binSize : 667648
             * sortNumber : 0
             * versionCode : 0
             */

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
//            private String install;
            private String progress;
            private int id;
            private Integer dialId;
            String state;
            int stateCode;
            String price;
            String downloads;
            boolean charge;
            boolean current;

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
            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getDownloads() {
                return downloads;
            }

            public void setDownloads(String downloads) {
                this.downloads = downloads;
            }

            public boolean isCharge() {
                return charge;
            }

            public void setCharge(boolean charge) {
                this.charge = charge;
            }

            public Integer getDialId() {
                return dialId;
            }

            public void setDialId(Integer dialId) {
                this.dialId = dialId;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

//            public String getInstall() {
//                return install;
//            }
//
//            public void setInstall(String install) {
//                this.install = install;
//            }
//
            public String getProgress() {
                return progress;
            }

            public void setProgress(String progress) {
                this.progress = progress;
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
        }
    }
}
