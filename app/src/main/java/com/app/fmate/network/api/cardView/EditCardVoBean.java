package com.app.fmate.network.api.cardView;

import java.util.List;

public class EditCardVoBean {
    private List<MoreListDTO> moreList;
    private List<AddedListDTO> addedList;
    public List<MoreListDTO> getMoreList() {
        return moreList;
    }

    public void setMoreList(List<MoreListDTO> moreList) {
        this.moreList = moreList;
    }

    public List<AddedListDTO> getAddedList() {
        return addedList;
    }

    public void setAddedList(List<AddedListDTO> addedList) {
        this.addedList = addedList;
    }

    public static class MoreListDTO {
        /**
         * type : 1
         * sort : 6
         * name : 心率
         * hidden : true
         */

        private int type;
        private int sort;
        private String name;
        private boolean hidden;

        public MoreListDTO(int type, int sort, String name, boolean hidden) {
            this.type = type;
            this.sort = sort;
            this.name = name;
            this.hidden = hidden;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }
    }

    public static class AddedListDTO {
        /**
         * type : 0
         * sort : 0
         * name : 运动记录
         * hidden : false
         */

        private int type;
        private int sort;
        private String name;
        private boolean hidden;

        public AddedListDTO(int type, int sort, String name, boolean hidden) {
            this.type = type;
            this.sort = sort;
            this.name = name;
            this.hidden = hidden;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }
    }
}
