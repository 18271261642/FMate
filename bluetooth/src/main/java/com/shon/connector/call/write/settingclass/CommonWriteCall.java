package com.shon.connector.call.write.settingclass;

import android.util.Log;

import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;

import java.util.Arrays;

/**
 * 直接写byte的指令
 */
public class CommonWriteCall extends WriteCallback {

    private static final String TAG = "CommonWriteCall";

    private byte[] writeByte;
    private BleWrite.SpecifySleepSourceInterface specifySleepSourceInterface;


    public CommonWriteCall(String address, byte[] writeByte) {
        super(address);
        this.writeByte = writeByte;
    }

    public CommonWriteCall(String address, byte[] writeByte, BleWrite.SpecifySleepSourceInterface specifySleepSourceInterface) {
        super(address);
        this.writeByte = writeByte;
        this.specifySleepSourceInterface = specifySleepSourceInterface;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        Log.e(TAG,"----process="+address+"\n"+ ByteUtil.getHexString(result)+"\n"+uuid);
        if(uuid.equalsIgnoreCase(Config.readCharacter)){
            if(specifySleepSourceInterface != null && result.length>=20){
                byte[] startArray = new byte[]{result[13],result[14],result[15],result[16]};
                Log.e(TAG,"------startArra="+ Arrays.toString(startArray));
                byte[] endArray = new byte[]{result[17],result[18],result[19],result[20]};
                specifySleepSourceInterface.backStartAndEndTime(startArray,endArray);
            }

        }
        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {
        return writeByte;
    }
}
