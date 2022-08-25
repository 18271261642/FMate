package com.example.xingliansdk.ui.ring;

/**
 * Created by Admin
 * Date 2022/8/25
 */
public class RingSwitchBean {


    private boolean isOpenHeart;

    private boolean isOpenTemp;

    public RingSwitchBean(boolean isOpenHeart, boolean isOpenTemp) {
        this.isOpenHeart = isOpenHeart;
        this.isOpenTemp = isOpenTemp;
    }

    public RingSwitchBean() {
    }

    public boolean isOpenHeart() {
        return isOpenHeart;
    }

    public void setOpenHeart(boolean openHeart) {
        isOpenHeart = openHeart;
    }

    public boolean isOpenTemp() {
        return isOpenTemp;
    }

    public void setOpenTemp(boolean openTemp) {
        isOpenTemp = openTemp;
    }
}
