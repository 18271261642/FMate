package com.shon.connector.call.notify;

import android.util.Log;

import com.shon.bluetooth.util.ByteUtil;
import com.shon.connector.utils.TLog;
import com.shon.bluetooth.core.call.Listener;
import com.shon.bluetooth.core.callback.ICallback;
import com.shon.bluetooth.core.callback.NotifyCallback;
import com.shon.connector.Config;
import com.shon.connector.bean.DataBean;
import com.shon.connector.utils.HexDump;

/**
 * 3.5
 */
public class XLNotifyCall extends NotifyCallback {
    private DataBean mDataBean;
    private String address;
    NotifyCallInterface mInterface;
   public XLNotifyCall(String address,NotifyCallInterface mInterface)
    {
        this.address=address;
        this.mInterface=mInterface;

    }
    public interface NotifyCallInterface {
        void NotifyCallResult(byte key, DataBean mDataBean);
        void NotifyCallResult(byte key, int type,int status);
    }

    @Override
    public boolean getTargetSate() {
        return true;
    }

    @Override
    public void onTimeout() {
        TLog.Companion.error("超时");
    }

    @Override
    public void onChangeResult(boolean result) {
        super.onChangeResult(result);
        new Listener(address).enqueue(new ICallback() {
            @Override
            public boolean process(String address, byte[] result,String uuid) {
                TLog.Companion.error("uuid=="+uuid+"result="+ByteUtil.getHexString(result));
                if(!Config.readCharacter.equalsIgnoreCase(uuid))
                    return false;
               TLog.Companion.error("广播值++"+ ByteUtil.getHexString(result));

               //8800000000000D5A0B02010001080200042A1BF498

                //8800000000000D640B02010001080200042A1C0653

                //8800000000000D800B02010001050200042A1D338E

                //拒绝 88000000000006030B020100010A

                if(result.length>13 && result[8] == 0x0B && result[13] == 0x0A){    //手表拒绝，app有弹窗就取消弹窗
                    mInterface.NotifyCallResult(result[8], 10,-1);
                }

                if(result.length>13 && result[8] == 0x0B && result[13] == 0x09){    //手表按钮测量血压，手表返回，app弹窗提醒
                    mInterface.NotifyCallResult(result[8], 0x09,-1);

                }

                //8800000000000DDA0B02010001050200042A1C06E0
                //8800000000000DDF0B02010001080200042A1C06E8
                if(result.length>13 && result[8] == 0x0B && result[13] == 0x05){    //到时自动手表提示弹窗，手表返回，app收到后弹窗提示
                    mInterface.NotifyCallResult(result[8], 0x05,-1);
                }


                //04 手表返回测量血压提醒，app收到后弹窗 发送02给手表，后续走正常流程
                if(result.length>13 && result[8] == 0x0B && result[13] == 0x04){
                    mInterface.NotifyCallResult(result[8],0x04,-1);
                }


                //8800000000000DF00B02 01 0001080200042A1D38F8
                if(result.length>13 && result[8] == 0x0B && result[13] == 0x08){    //手表发起启动测量，app无响应，手表返回，此次app若哟有弹窗，则取消弹窗
                    mInterface.NotifyCallResult(result[8], 0x08,-1);
                }

                //88000000000006080B0201000101
                if(result.length>12 && result[8] == 0x0B && result[13] == 0x01){    //超时，有弹窗取消弹窗
                    mInterface.NotifyCallResult(result[8], 0x01,-1);
                }

//               boolean isSwitch = (result[9] == Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE) || (result[9] == Config.ActiveUpload.DEVICE_REAL_TIME_OTHER)||
//                       (result[9] == Config.ActiveUpload.DEVICE_FIND_PHONE)

                if (result[0] == Config.PRODUCT_CODE && result[8] == Config.ActiveUpload.COMMAND
                ) {
                    switch (result[9])
                    {
                      case   Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE: //运动
                        {
                            mDataBean= new DataBean();
                            mDataBean.setTotalSteps(HexDump.byte2intHigh(result, 10, 4));
                            mDataBean.setDistance(HexDump.byte2intHigh(result, 14, 4));
                            mDataBean.setCalories(HexDump.byte2intHigh(result, 18, 4));


                            Log.e("XLNotify","----111----运动实时数据="+mDataBean.getTotalSteps()+" "+mDataBean.getDistance());
                            mInterface.NotifyCallResult(result[9],mDataBean);
                            break;
                        }
                        case  Config.ActiveUpload.DEVICE_REAL_TIME_OTHER:
                        {


                            mDataBean=new DataBean();
                            mDataBean.setTime(HexDump.byte2intHigh(result, 10, 4));
                            mDataBean.setHeartRate(ByteUtil.cbyte2Int(result[14]) );
                            mDataBean.setBloodOxygen(ByteUtil.cbyte2Int(result[15]));
                            mDataBean.setSystolicBloodPressure(ByteUtil.cbyte2Int(result[16]));
                            mDataBean.setDiastolicBloodPressure(ByteUtil.cbyte2Int(result[17]));
                            mDataBean.setTemperature(String.valueOf(HexDump.byte2intHigh(result, 18, 2)));
                            mDataBean.setHumidity(String.valueOf(HexDump.byte2intHigh(result, 19, 2)));
                            mInterface.NotifyCallResult(result[9],mDataBean);
//                          TLog.Companion.error("其他心率之类的的++"+ByteUtil.cbyte2Int(result[14]));
                            break;
                        }
                        case   Config.ActiveUpload.DEVICE_FIND_PHONE: //寻找手机
//                        {
//                            mInterface.NotifyCallResult(result[9],result[10]);
//                            //00默认  01发起
//                        }
                        case   Config.ActiveUpload.DEVICE_CHANGE_PHONE_CALL_STATUS://设备端主动发起改变手机来电状态
                        case   Config.ActiveUpload.DEVICE_CAMERA_CONFIRMATION: //设备端主动发起相机确认信号
                        case  Config.ActiveUpload.DEVICE_MUSIC_CONTROL_KEY: //音乐控制
                        case   Config.ActiveUpload.DEVICE_WARNING_SIGNAL_KEY: //设备端主动发起告警信号
                        {
                            TLog.Companion.error("音乐++"+result[9]);
                            mInterface.NotifyCallResult(result[9], result[10],-1);
                            break;
                        }
                        case  Config.ActiveUpload.DEVICE_POWER_CHANGE_KEY : //设备端主动上传设备电量变化信息
                        {
//                            TLog.Companion.error("111电量变化2222");
                            mInterface.NotifyCallResult(result[9], result[10],result[12]);
                       //     int   electricity=result[10];
//                            if (result[11] > 0x0F) {
//                                int   mDisplayBattery = (result[11]<<4);
//                                int   mCurrentBattery = (result[11]>>4);
//                                TLog.Companion.error("实时监听的电量++"+mCurrentBattery);
//                                TLog.Companion.error("实时监听的电量++"+mDisplayBattery);
//
//                            }

                            //                SNEventBus.sendEvent(DEVICE_ELECTRICITY, electricity)
                            break;
                        }
                        case Config.ActiveUpload.DEVICE_DIAL_ID:
                        {
                          int dialId=  HexDump.byte2intHigh(result, 10, 4);
                            mInterface.NotifyCallResult(result[9], dialId,-1);
                            break;
                        }
                    }
                    return true;
                }
                return true;
            }
        });
    }

//    @Override
//    public boolean process(String address, byte[] result) {
//        TLog.Companion.error("步数=="+ ByteUtil.getHexString(result));
//      //  return super.process(address, result);
//        return false;
//    }
}
