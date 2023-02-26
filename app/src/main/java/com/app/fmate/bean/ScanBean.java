package com.app.fmate.bean;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public class ScanBean {
    @NonNull
    private BluetoothDevice device;
    private String uuid;

    public ScanBean(BluetoothDevice device, String uuid) {
        this.device = device;
        this.uuid = uuid;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


}
