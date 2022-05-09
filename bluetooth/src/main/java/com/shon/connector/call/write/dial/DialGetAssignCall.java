package com.shon.connector.call.write.dial;

import com.shon.connector.utils.ShowToast;
import com.orhanobut.hawk.Hawk;
import com.shon.bluetooth.core.callback.WriteCallback;
import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.BleWrite;
import com.shon.connector.Config;
import com.shon.connector.call.CmdUtil;
import com.shon.connector.utils.HexDump;
import com.shon.connector.utils.TLog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 表盘验证
 */
public class DialGetAssignCall extends WriteCallback {

    //    String key;
    BleWrite.FlashGetDialInterface mInterface;

    public DialGetAssignCall(String address, BleWrite.FlashGetDialInterface mInterface) {
        super(address);
        this.mInterface = mInterface;
    }


    @Override
    public byte[] getSendData() {
        byte[] sendData = CmdUtil.getFullPackage(CmdUtil.getPlayer("09", "0100"));
        TLog.Companion.error("flash发送++" + ByteUtil.getHexString(sendData));
        return sendData;
    }

    @Override
    public boolean process(String address, byte[] result, String uuid) {
        TLog.Companion.error("获取当前表盘res==" + ByteUtil.getHexString(result));
        if (!uuid.equalsIgnoreCase(Config.readCharacter))
            return false;
        if (result[8] == Config.Expand.COMMAND && result[9] == Config.Expand.DEVICE_ACK) {
            switch (result[10]) {
                case 0x01:

                    break;
                case 0x02:
                case 0x03:
                    //  BleWrite.writeForGetFirmwareInformation(mNoticeInterface); //重新发送的操作
                    break;
                case 0x04:
                    ShowToast.INSTANCE.showToastLong("设备不支持当前协议");
                    break;
            }
            return false;
        }
        TLog.Companion.error("res==" + ByteUtil.getHexString(result));
        if (result[8] == Config.Dial.KEY && result[9] == Config.Dial.DEVICE_DIAL_FLASH) {
            DialBean mDialBean = new DialBean();
            List<DialBean> mList = new ArrayList<>();
            int countSize = 0;
            if (result[10] == 1) {
                int size = HexDump.byte2intHigh(result, 11, 2);
           //     TLog.Companion.error("SIZE==" + size);
                int id = HexDump.byte2intHigh(result, 13, size);
//                TLog.Companion.error("id==" + id);
                countSize += (3 + size);

                mList.add(new DialBean(id, 1));
                Hawk.put(Config.SAVE_DEVICE_CURRENT_DIAL,id);
            }
          //  TLog.Companion.error("==" + result[10 + countSize]);
            if (result[10 + countSize] == 2) {
                int size = HexDump.byte2intHigh(result, 10 + countSize + 1, 2);
                int num = 10 + countSize + 1 + 2;
                int sizeNum = size + num;
                TLog.Companion.error("num==" + num + " sizeNum===" + sizeNum);

                for (int i = num; i < num + size; i += 4) {
                    int id = HexDump.byte2intHigh(result, i, 4);
//                    TLog.Companion.error("i==" + i + "id==" + id);
                    mDialBean.type = 2;
                    mDialBean.dialId = id;
                    // mList.add(mDialBean);
                    mList.add(new DialBean(id, 2));
                }
//                TLog.Companion.error("SIZE==" + size);
                countSize += (3 + size);

            }
            if (result[10 + countSize] == 3) {
                int size = HexDump.byte2intHigh(result, 10 + countSize + 1, 2);
             //   TLog.Companion.error("SIZE==" + size);
                int num = 10 + countSize + 1 + 2;
                int sizeNum = size + num;
//                TLog.Companion.error("num==" + num + " sizeNum===" + sizeNum);
                for (int i = num; i < num + size; i += 4) {
                    int id = HexDump.byte2intHigh(result, i, 4);
                    mDialBean.type = 3;
                    mDialBean.dialId = id;
                    if (i > num){
                        mList.add(new DialBean(id, 4));
                        Hawk.put(Config.SAVE_DEVICE_INTO_MARKET_DIAL,id == 65535 ? -1 : id);
                    }else {
                        mList.add(new DialBean(id, 3));
                    }

                }
            }
            mInterface.onResultDialIdBean(mList);
            return true;
        }

        return false;
    }

    @Override
    public boolean isFinish() {
        return super.isFinish();
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error("time out 超时了呢");


    }

    private String keyValue(byte[] key, byte[] key1) {
        return ByteUtil.getHexString(key) +
                ByteUtil.getHexString(key1);

    }

    public class DialBean {
        Integer dialId;
        int type;

        public Integer getDialId() {
            return dialId;
        }

        public void setDialId(Integer dialId) {
            this.dialId = dialId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public DialBean() {
        }

        public DialBean(Integer dialId, int type) {
            this.dialId = dialId;
            this.type = type;
        }
    }
}
