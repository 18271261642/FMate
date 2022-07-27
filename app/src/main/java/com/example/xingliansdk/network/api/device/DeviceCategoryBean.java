package com.example.xingliansdk.network.api.device;

import java.util.List;


//设备属性
public class DeviceCategoryBean {

    private List<DeviceCategoryItemBean> list;

    public List<DeviceCategoryItemBean> getList() {
        return list;
    }

    public void setList(List<DeviceCategoryItemBean> list) {
        this.list = list;
    }

    private class DeviceCategoryItemBean{

        private int id;

        private String name;

        private String image;

        private List<ProductListDTO> productList;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public List<ProductListDTO> getProductList() {
            return productList;
        }

        public void setProductList(List<ProductListDTO> productList) {
            this.productList = productList;
        }

        public  class ProductListDTO {

            private String productNumber;

            private String productName;

            public String getProductNumber() {
                return productNumber;
            }

            public void setProductNumber(String productNumber) {
                this.productNumber = productNumber;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }
        }
    }
}
