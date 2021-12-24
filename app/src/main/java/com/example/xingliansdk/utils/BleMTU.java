package com.example.xingliansdk.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

/**
 * 蓝牙mtu传输所用加快蓝牙传输速度
 */
public class BleMTU {
    private void setMtu(Activity context, int setMtu) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                device.connectGatt(context, true, new BluetoothGattCallback() {
                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (setMtu > 23 && setMtu < 512) {
                                gatt.requestMtu(setMtu);
                            }
                        }
                    }

                    @Override
                    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                        super.onMtuChanged(gatt, mtu, status);
                        //   mMtu = mtu;
                        if (BluetoothGatt.GATT_SUCCESS == status && setMtu == mtu) {
                            // LogUtils.d("MTU change success = " + mtu);
                        } else {
                            //  LogUtils.d("MTU change fail!");
                        }
                    }
                });
            }
        });
    }
}
