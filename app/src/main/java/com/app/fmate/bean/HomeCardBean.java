//package com.example.xingliansdk.bean;
//
//import android.graphics.drawable.Drawable;
//
//import java.util.List;
//
//public class HomeCardBean {
//
//    private List<AddCardDTO> addCard;
//    private List<DeleCardDTO> deleCard;
//
//    public List<AddCardDTO> getAddCard() {
//        return addCard;
//    }
//
//    public void setAddCard(List<AddCardDTO> addCard) {
//        this.addCard = addCard;
//    }
//
//    public List<DeleCardDTO> getDeleCard() {
//        return deleCard;
//    }
//
//    public void setDeleCard(List<DeleCardDTO> deleCard) {
//        this.deleCard = deleCard;
//    }
//
//    public static class AddCardDTO {
//        /**
//         * name : 记录
//         * type : 0
//         * img :本地图片id
//         */
//
//        private String name;
//        private int type;
//        private int img;
//        private long time;
//        private String dayContent;//主页卡片下面的具体内容
//        private String dayContentString;//后面的文字
//        private String subTitle;//
//
//        public String getDayContentString() {
//            return dayContentString;
//        }
//
//        public void setDayContentString(String dayContentString) {
//            this.dayContentString = dayContentString;
//        }
//
//        public String getSubTitle() {
//            return subTitle;
//        }
//
//        public void setSubTitle(String subTitle) {
//            this.subTitle = subTitle;
//        }
//
//        public long getTime() {
//            return time;
//        }
//
//        public void setTime(long time) {
//            this.time = time;
//        }
//
//        public String getDayContent() {
//            return dayContent;
//        }
//
//        public void setDayContent(String dayContent) {
//            this.dayContent = dayContent;
//        }
//
//        //        private Drawable img;
//        public int getImg() {
//            return img;
//        }
//
//        public void setImg(int img) {
//            this.img = img;
//        }
//
//        public AddCardDTO(){
//
//        }
//
//        public AddCardDTO(String name, int type, int img, long time, String dayContent, String dayContentString, String subTitle) {
//            this.name = name;
//            this.type = type;
//            this.img = img;
//            this.time = time;
//            this.dayContent = dayContent;
//            this.dayContentString = dayContentString;
//            this.subTitle = subTitle;
//        }
//
//        public AddCardDTO(String name, int type, int img){
//            this.name=name;
//            this.type=type;
//            this.img=img;
//        }
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public int getType() {
//            return type;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//    }
//
//    public static class DeleCardDTO {
//        /**
//         * name : 记录3
//         * type : 2
//         */
//        public DeleCardDTO(){
//
//        }
//        public DeleCardDTO(String name, int type, int img, long time, String dayContent, String dayContentString, String subTitle) {
//            this.name = name;
//            this.type = type;
//            this.img = img;
//            this.time = time;
//            this.dayContent = dayContent;
//            this.dayContentString = dayContentString;
//            this.subTitle = subTitle;
//        }
//
//        private String name;
//        private int type;
//        private int img;
//        private long time;
//        private String dayContent;
//        private String dayContentString;//后面的文字
//        private String subTitle;//
//
//        public String getDayContentString() {
//            return dayContentString;
//        }
//
//        public void setDayContentString(String dayContentString) {
//            this.dayContentString = dayContentString;
//        }
//
//        public String getSubTitle() {
//            return subTitle;
//        }
//
//        public void setSubTitle(String subTitle) {
//            this.subTitle = subTitle;
//        }
//
//        public long getTime() {
//            return time;
//        }
//
//        public void setTime(long time) {
//            this.time = time;
//        }
//
//        public String getDayContent() {
//            return dayContent;
//        }
//
//        public void setDayContent(String dayContent) {
//            this.dayContent = dayContent;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//        public int getImg() {
//            return img;
//        }
//
//        public void setImg(int img) {
//            this.img = img;
//        }
//        public int getType() {
//            return type;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//    }
//}
