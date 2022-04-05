package com.shon.connector.call.write.dial;

import android.util.Log;

import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.bean.DialCustomBean;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;

/**
 * 删除表盘
 */
public class DialDeleteCall extends WriteCallback {


    byte[] sendData;
    BleWrite.DialWriteInterface mInterface;
    long featureId;

    public DialDeleteCall(String address,  BleWrite.DialWriteInterface mInterface,long uiFeature) {
        super(address);
        this.mInterface = mInterface;
        this.featureId = uiFeature;

        Log.e("删除表盘","-----删除表盘信息="+uiFeature);

        byte[] uiId = HexDump.toByteArray(uiFeature);

        sendData=  CmdUtil.getFullPackage(CmdUtil.getPlayer("09", "05",uiId));

    }



    @Override
    public boolean process(String address, byte[] result, String uuid) {
        Log.e("删除表盘","-----process="+address+" "+HexDump.dumpHexString(result));
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;


        if(result[8] == Config.Dial.KEY && result[9] == 0x06)
        {
            mInterface.onResultDialWrite(result[10]);
            return  true;
        }

        return false;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public byte[] getSendData() {
        return sendData;
    }
}
