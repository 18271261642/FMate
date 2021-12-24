package com.shon.connector.call.write.factorymode;

import com.shon.connector.bean.TimeBean;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.callback.WriteCallback;

/**
 * 3.8.1-3.8.2
 * APP工厂指令
 */
public class FactoryModeCall extends WriteCallback {
    byte[] keyValue;

    public FactoryModeCall(String address, TimeBean mSettingTimeBean) {
        super(address);
    }

    public FactoryModeCall(String address, byte[] keyValue, String unicode) {
        super(address);
        this.keyValue = keyValue;
    }

    @Override
    public byte[] getSendData() {

        byte[] head = {0x05, keyValue[0],keyValue[1]};

        return CmdUtil.getFullPackage(head);
    }

    @Override
    public boolean process(String address, byte[] result,String uuid) {
        if(!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;

        TLog.Companion.error("获取到最终的数据长度_++" + result);//获取到最终的长度

        return false;
    }

    @Override
    public void onTimeout() {

    }
}
