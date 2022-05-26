package com.shon.bluetooth.core.call;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shon.bluetooth.BLEManager;
import com.shon.bluetooth.core.ConnectedDevices;
import com.shon.bluetooth.core.Device;
import com.shon.bluetooth.core.callback.ICallback;
import com.shon.bluetooth.core.callback.OnTimeout;
import com.shon.bluetooth.util.BleLog;
import com.shon.connector.Config;
import com.shon.connector.utils.TLog;

import java.util.UUID;

public abstract class BaseCall<T extends ICallback, C> implements ICall<T> {
    protected T callBack;
    private String address;

    private long delayTime = 5_000;
    private UUID serviceUUid;
    protected UUID characteristicUUID;
    protected boolean priority = false;
    protected int writeType = 0;


    public BaseCall(String address) {
        this.address = address;
    }

    @SuppressWarnings("unchecked")
    public C setServiceUUid(String serviceUUid) {
        this.serviceUUid = UUID.fromString(serviceUUid);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C setCharacteristicUUID(String characteristicUUID) {
        this.characteristicUUID = UUID.fromString(characteristicUUID);
        return (C) this;
    }

    public C setWriteType(int type){
        this.writeType = type;
        return (C) this;
    }


    public C setTimeout(long delayTime){
        this.delayTime = delayTime;
        return (C) this;
    }
    @NonNull
    @Override
    public String getAddress() {
        return address;
    }

    @NonNull
    @Override
    public T getCallBack() {
        return callBack;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    @Override
    public boolean isPriority() {
        return priority;
    }

    @Override
    public void enqueue(@NonNull T iCallback) {
        this.callBack = iCallback;
//        TLog.Companion.error("enqueue  callBack=="+callBack);
        BLEManager.getInstance().getDataDispatcher().enqueue(this);
    }


    @Nullable
    public BluetoothGatt bluetoothGatt() {

        ConnectedDevices connectedDevices = BLEManager.getInstance().getConnectDispatcher().getConnectedDevices();
        Device device = connectedDevices.getDevice(address);
        if (device == null ) {
            return null;
        }

        return device.getGatt();
    }

    @Nullable
    protected BluetoothGattCharacteristic gattCharacteristic(@NonNull BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(serviceUUid);
        if (service == null) {
            return null;
        }
        return service.getCharacteristic(characteristicUUID);
    }

    private Handler handler;

    @Override
    public void cancel() {
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    protected synchronized void startTimer() {
        if (handler != null) {
            return;
        }

//        BLEManager.getInstance().getDataDispatcher().startSendNext(true);

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!Config.isNeedTimeOut){
                   // ((OnTimeout)callBack).onTimeout();
                    BLEManager.getInstance().getDataDispatcher().startSendNext(true);
                }else{
                    BLEManager.getInstance().getDataDispatcher().clear("");
                    //BLEManager.getInstance().getDataDispatcher().startSendNext(false);
                }
                BleLog.e("----time out "+ getClass().getName());

            }
        },delayTime);
    }
    public void cancelTimer(){
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
