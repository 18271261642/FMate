package com.app.fmate.bean.dialBean;

public class CustomizePlacementBean {
    String name;
    int type;
    boolean mSelected=false;

    public boolean ismSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public CustomizePlacementBean(){

    }
    public CustomizePlacementBean(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public CustomizePlacementBean(String name, int type, boolean mSelected) {
        this.name = name;
        this.type = type;
        this.mSelected = mSelected;
    }
}
