package com.shon.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.Device;
import com.shon.bluetooth.core.Result;
import com.shon.bluetooth.core.call.BaseCall;
import com.shon.bluetooth.core.call.ICall;
import com.shon.bluetooth.core.call.Listener;
import com.shon.bluetooth.core.call.NotifyCall;
import com.shon.bluetooth.core.call.ReadCall;
import com.shon.bluetooth.core.call.WriteCall;
import com.shon.bluetooth.core.callback.ICallback;
import com.shon.bluetooth.core.callback.NotifyCallback;
import com.shon.bluetooth.core.callback.ReadCallback;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.BleLog;
import com.shon.bluetooth.util.ByteUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 数据分发器
 */
public class DataDispatcher {
    private final Deque<ICall<?>> callDeque;
    private final List<Listener> listeners;
    private ICall<?> tempCall;
    public static boolean callDequeStatus=false;
//        public MutableLiveData<Result> resultMutableLiveData;
    private final Deque<Result> resultDeque;

    DataDispatcher() {
        listeners = new ArrayList<>();
        callDeque = new LinkedBlockingDeque<>();
        resultDeque = new LinkedBlockingDeque<>();
//        resultMutableLiveData = new MutableLiveData<>();
//        resultMutableLiveData.observeForever(result -> {
////            TLog.Companion.log("observeForever++"+ByteUtil.getHexString(result.getBytes()));
//            resultDeque.add(result);
//            handlerResult();
//
//        });
    }


    public synchronized void startSendNext(boolean isFinish) {
        if (tempCall != null) {
            BleLog.d("startSendNext ++++" + isFinish + " ;   call = " + tempCall.getAddress());
        }else {
            BleLog.d("startSendNext ++++" + isFinish );
        }

        if (isFinish) {
            tempCall = null;
//            if (callDeque == null || callDeque.isEmpty())
//                return;
//            callDeque.removeFirst();

        }
        if (tempCall != null) {
            return;
        }
        BleLog.d("startSendNext  callDeque.size " + callDeque.size() );
        if (callDeque.size() ==0){
          //  TLog.Companion.error("重置为true");
            callDequeStatus=true;
            return;
        }
        else
        {
//            TLog.Companion.error("重置为false");
            callDequeStatus=false;
        }
        tempCall = callDeque.poll();
        if (tempCall == null) {
            return;
        }

        Device device = BLEManager.getInstance().getConnectDispatcher().getConnectedDevices().getDevice(tempCall.getAddress());
        if (device == null || !device.getConnected()){
            //设备不存在，或者设备已断开连接，不再进行数据交互
            Iterator<ICall<?>> iCallIterator = callDeque.descendingIterator();
            while (iCallIterator.hasNext()){
                ICall<?> iCall = iCallIterator.next();
                if (TextUtils.equals(iCall.getAddress(),tempCall.getAddress())){
                    callDeque.remove(iCall);
                }
            }

            tempCall = null;
//            TLog.Companion.error("从这边传入的false??");
            startSendNext(false);
            return;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tempCall instanceof NotifyCall) {
            ((NotifyCall) tempCall).changeSate();
        }
        if (tempCall instanceof WriteCall) {
            ((WriteCall) tempCall).write();
        }
        if (tempCall instanceof ReadCall) {
            ((ReadCall) tempCall).startRead();
        }
    }

//    Handler mainHandler = new Handler(Looper.getMainLooper());

    public void onReceivedResult(Result value) {
//        TLog.Companion.log(" value++"+ByteUtil.getHexString(value.getBytes()));
//        resultMutableLiveData.postValue(value);
//        resultDeque.add(value);
//        handlerResult();
//        resultMutableLiveData.postValue(value);
//         LiveDataUtils.postSetValue(resultMutableLiveData,value);

        LiveDataUtils.postRunnable(() -> {
//                resultMutableLiveData.setValue(value);
            resultDeque.add(value);
            handlerResult();
        });

    }

    public synchronized void enqueue(Listener listener) {
        listeners.add(listener);
    }

    public synchronized void enqueue(ICall<?> iCall) {
        if (iCall.isPriority()){
//            TLog.Companion.error("enqueue  isPriority");
            callDeque.offerFirst(iCall);
        }else {
//            TLog.Companion.error("enqueue  !!  isPriority");
            callDeque.offer(iCall);
        }
//        TLog.Companion.error("enqueue  这边传入的 false");
        startSendNext(false);
    }

    private volatile Result tempResult;


    private synchronized void handlerResult() {
        if (tempResult != null) {
            return;
        }
        tempResult = resultDeque.peek();
        if (tempResult == null) {
            return;
        }
        //TLog.Companion.error("tempResult+="+tempResult.toString());
        boolean finish = handlerResult(tempResult);
        if (finish) {
            tempResult = null;
            if(resultDeque!=null)
            resultDeque.removeFirst();
            handlerResult();
        }
    }

    private synchronized boolean handlerResult(Result result) {

        byte[] bytes = result.getBytes();
        String setValue = ByteUtil.getHexString(bytes);
        String address = result.getAddress();
        String uuid=result.getUuid();
//        if(TextUtils.isEmpty(uuid))
//            return true;
          //  uuid="";
//        if (bytes.length==2&&bytes[0]==0x01&&bytes[1]==0) { //有个特殊情况 第一次链接会是0100
//            TLog.Companion.error("setValue++"+setValue);
//            return true;
//        }
        int type = result.getType();
//        TLog.Companion.error("type==="+type);
        if (type == BluetoothGattCharacteristic.PROPERTY_WRITE) {
//            TLog.Companion.error("BluetoothGattCharacteristic.PROPERTY_WRITE 进入");
            WriteCallback writeCall = getWriteCallByWriteData(address, setValue);
            if (writeCall != null) {
                boolean isRemove = writeCall.removeOnWriteSuccess();
                if (isRemove) {
                    startSendNext(true);
                }
                return true;
            }
            return true;
        }
        ////////保留原来的遍历写法
//        for (ICall<?> iCall : callDeque) {
//            if (!TextUtils.equals(address, tempCall.getAddress())) {
//                continue ;
//            }
//
//            if (tempCall instanceof NotifyCall) {
//                NotifyCallback callBack = (NotifyCallback) tempCall.getCallBack();
//                callBack.onChangeResult(true);
//                ((NotifyCall) tempCall).cancelTimer();
//                startSendNext(true);
//              //  break;
//            }
//            if (tempCall instanceof ReadCall) {
//                TLog.Companion.error("ReadCall链接的时候==");
//           //     UUID uuid = ((ReadCall) iCall).getCharacteristicUUID(); //由于多个uuid所以这里不需要判断直接丢出去进行判断
////                if (!TextUtils.equals(result.getUuid().toLowerCase(),
////                        uuid.toLowerCase())) {
////                    continue;
////                }
//                ReadCallback readCallback = (ReadCallback) tempCall.getCallBack();
//                if(TextUtils.isEmpty(uuid))
//                {
//                    TLog.Companion.error("uuid==");
//                 //   break;
//                }
//                if (readCallback.process(address, bytes,uuid)) {
//                    ((BaseCall<?, ?>) tempCall).cancelTimer();
//                    startSendNext(true);
//                }
//            }
//            if (tempCall instanceof WriteCall) {
////                BleLog.d("DataDispatcher WriteCall : process " + type);
//                WriteCallback writeCall = (WriteCallback) tempCall.getCallBack();
//                if(TextUtils.isEmpty(uuid))
//                {
//                   // break;
//                }
//                if (writeCall.process(address, bytes,uuid) && writeCall.isFinish()) {
//                    ((BaseCall<?, ?>) tempCall).cancelTimer();
//                    startSendNext(true);
//                }
//            }
//        }
        if(tempCall!=null&&tempCall.getAddress()!=null)
        if (TextUtils.equals(address, tempCall.getAddress())) {
            if (tempCall instanceof NotifyCall) {
                NotifyCallback callBack = (NotifyCallback) tempCall.getCallBack();
                callBack.onChangeResult(true);
                ((NotifyCall) tempCall).cancelTimer();
                startSendNext(true);
                //  break;
            }
           else if (tempCall instanceof ReadCall) {
             //   TLog.Companion.error("ReadCall链接的时候==");

                ReadCallback readCallback = (ReadCallback) tempCall.getCallBack();
                if(!TextUtils.isEmpty(uuid))
                {
                    if (readCallback.process(address, bytes,uuid)) {
                        ((BaseCall<?, ?>) tempCall).cancelTimer();
                        startSendNext(true);
                    }
                }

            }
           else if (tempCall instanceof WriteCall) {
              //  BleLog.d("DataDispatcher WriteCall : process " + type);
                WriteCallback writeCall = (WriteCallback) tempCall.getCallBack();
                if(!TextUtils.isEmpty(uuid))
                {
                    if (writeCall.process(address, bytes,uuid) && writeCall.isFinish()) {
//                         TLog.Companion.error("=="+ tempCall);
                         if(tempCall !=null) {
                             ((BaseCall<?, ?>) tempCall).cancelTimer();
                             startSendNext(true);
                         }
                    }
                }

            }
        }





        if (type == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
            for (Listener listener : listeners) {
                if (!TextUtils.equals(address, listener.getAddress())) {
                    continue;
                }

                ICallback callBack = listener.getCallBack();
                if(TextUtils.isEmpty(uuid))
                {
                    break;
                }
                boolean process = callBack.process(address, bytes,uuid);
                if (process) {
                    break;
                }
            }
        }

        return true;

    }


    public WriteCallback getWriteCallByWriteData(String address, String writeData) {
        for (ICall<?> writer : callDeque) {
            if (!(writer instanceof WriteCall)) {
                continue;
            }
            if (!TextUtils.equals(address, writer.getAddress())) {
                continue;
            }
            WriteCallback writeCall = (WriteCallback) writer.getCallBack();
          //  Log.e("getWriteCallByWriteData", "writeCall==" + writeCall.getSendData().length);
          //  Log.e("getWriteCallByWriteData", "getSendData==" + writeCall.getSendData());
            byte[] writeInfo = writeCall.getSendData();

            if (TextUtils.equals(writeData, ByteUtil.getHexString(writeInfo))) {
                BleLog.d("找到发送的指令");
                return writeCall;
            }
        }
        return null;
    }

    public void clear(@Nullable String mac) {

        callDeque.clear();
        tempCall = null;
        startSendNext(true);

    }
    public void clearAll() {
        callDeque.clear();
        listeners.clear();
        resultDeque.clear();
        if (tempCall!= null) {
            tempCall.cancel();
            tempCall = null;
        }
       // resultMutableLiveData=null;
        startSendNext(true);

    }
}
