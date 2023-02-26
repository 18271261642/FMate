package com.app.fmate.bean;

import java.util.List;

public class PopularScienceBean {
    private List<ListDTO> list;

    public PopularScienceBean(){}

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO {
        /**
         * id : 1024
         * title : 男子因焦虑过度碱中毒，过度焦虑应该怎么办？
         * image : https://xlylfile.oss-cn-shenzhen.aliyuncs.com/upgrade/ui/image/article_1024_16315188484340.jpeg
         */

        private int id;
        private String title;
        private String image;
        private String detailUrl;

        public String getDetailUrl() {
            return detailUrl;
        }

        public void setDetailUrl(String detailUrl) {
            this.detailUrl = detailUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}
