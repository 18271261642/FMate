package com.example.xingliansdk.bean;


import com.contrarywind.interfaces.IPickerViewData;

/**
 * 功能：
 */

public class CardWeightBean implements IPickerViewData {
    int id;
    String kgInteger;
    public String getKgInteger() {
        return kgInteger;
    }

    public void setKgInteger(String kgInteger) {
        this.kgInteger = kgInteger;
    }

    public CardWeightBean(int id, String kgInteger) {
        this.id = id;
        this.kgInteger = kgInteger;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    @Override
    public String getPickerViewText() {
        return kgInteger;
    }

}

