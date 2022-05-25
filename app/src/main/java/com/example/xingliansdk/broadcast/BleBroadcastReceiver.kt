package com.example.xingliansdk.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import com.example.xingliansdk.Config.eventBus.*
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.DetailDialViewApi
import com.example.xingliansdk.network.api.dialView.RecommendDialViewApi
import com.example.xingliansdk.utils.*
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.core.call.Listener
import com.shon.bluetooth.core.call.NotifyCall
import com.shon.bluetooth.util.ByteUtil
import com.shon.connector.Config
import com.shon.connector.bean.DataBean
import com.shon.connector.call.notify.XLNotifyCall
import com.shon.connector.utils.HexDump
import com.shon.connector.utils.TLog
import com.shon.connector.utils.TLog.Companion.error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BleBroadcastReceiver : BroadcastReceiver(), XLNotifyCall.NotifyCallInterface {
    lateinit var mDataBean: DataBean
    lateinit var address: String

    //    private lateinit var message: MessageInfo
    lateinit var mContext: Context
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        mContext = context
        mDataBean = DataBean()
        address = intent.getStringExtra("address").toString()
        if (address?.isNullOrEmpty()) {
            return
        }
        bleNotifyCall(address)
    }

    private fun bleNotifyCall(address: String?) {
        NotifyCall(address)
            .setCharacteristicUUID(Config.readCharacter)
            .setServiceUUid(Config.serviceUUID)
            .enqueue(XLNotifyCall(address, this))
    }


    override fun NotifyCallResult(key: Byte, mData: DataBean?) {
        when (key) {
            Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE -> //运动
            {
                mDataBean = DataBean()
                if (mData != null) {
                    mDataBean.totalSteps = mData.totalSteps
                    mDataBean.distance = mData.distance
                    mDataBean.calories = mData.calories
                }

                Log.e(
                    "XLNotify",
                    "----222----运动实时数据=" + mDataBean.totalSteps + " " + mDataBean.distance
                )

                SNEventBus.sendEvent(
                    Config.ActiveUpload.DEVICE_REAL_TIME_EXERCISE.toInt(),
                    mDataBean
                )
            TLog.error("你好实时运动"+mDataBean.totalSteps )
            }
            Config.ActiveUpload.DEVICE_REAL_TIME_OTHER -> {
                mDataBean = DataBean()
                if (mData != null) {
                    mDataBean.heartRate = mData.heartRate
                    mDataBean.bloodOxygen = mData.bloodOxygen
                    mDataBean.systolicBloodPressure = mData.systolicBloodPressure
                    mDataBean.diastolicBloodPressure = mData.diastolicBloodPressure
                    mDataBean.temperature = mData.temperature
                    mDataBean.humidity = mData.humidity
//                    TLog.error("BleBroadcastReceiver 心率===${Gson().toJson(mDataBean)}")
                    SNEventBus.sendEvent(
                        Config.ActiveUpload.DEVICE_REAL_TIME_OTHER.toInt(),
                        mDataBean
                    )
                    //   message.getMessage(mDataBean)
                }

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun NotifyCallResult(key: Byte, type: Int, Status: Int) {
        TLog.error("广播","NotifyCallResult ="+key+" "+type +" "+Status)
        when (key) {
            Config.ActiveUpload.DEVICE_MEASURE_BP->{    //手表返回测量血压
                if(type == 9){  //手表按血压测量按键返回标识，此时app收到后弹窗测量的弹窗，用户点击测量，进入测量页面
                    sendActionBroadCast(9)
                }

                if(type == 5){  //app弹窗提示测量
                    sendActionBroadCast(5)
                }

                if(type == 8){  //直接测量
                    sendActionBroadCast(8)
                }

                if(type == 0x01){   //血压测量超时，app有弹窗取消弹窗
                    sendActionBroadCast(0x01)
                }
            }

            Config.ActiveUpload.DEVICE_FIND_PHONE -> //寻找手机
            {

                BandCallPhoneNotifyUtil.startNotification(mContext)
            }
            Config.ActiveUpload.DEVICE_CHANGE_PHONE_CALL_STATUS -> //设备端主动发起改变手机来电状态
            {
                if (type == 2) {//来电静音
                    ContactsUtil.modifyingVolume(mContext, true)
                }
                if (type == 1) {//挂断
                    ContactsUtil.rejectCall(mContext)
                }
                if (type == 0) {//默认
                    //  ContactsUtil.modifyingVolume(mContext,true)
                }
            }
            Config.ActiveUpload.DEVICE_CAMERA_CONFIRMATION -> //设备端主动发起相机确认信号
            {
                //00默认  01发起
                SNEventBus.sendEvent(DEVICE_REMOTE_CAMERA, type)
                // BandCallPhoneNotifyUtil.startNotification(mContext)
            }
            Config.ActiveUpload.DEVICE_MUSIC_CONTROL_KEY -> //音乐控制
            {
                if(!MusicControlUtil.fastClick())
                    return
                TLog.error("音乐 NotifyCallResult type+$type")
                if (type == 1) {  //下一首
                    MusicControlUtil.sendKeyEvents(mContext, KeyEvent.KEYCODE_MEDIA_NEXT)//下一首
                }
                if (type == 2) {  //播放/暂停
                    MusicControlUtil.playOrPauseMusic(mContext)
//
//                    MusicControlUtil.sendKeyEvents(
//                        mContext,
//                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
//                    )//播放/暂停
                }
                if (type == 3) {   //上一首
                    MusicControlUtil.sendKeyEvents(mContext, KeyEvent.KEYCODE_MEDIA_PREVIOUS)//上一首

                }
                if (type == 4) {
                    VolumeControlUtil.setVolumeUp(mContext)
                }
                if (type == 5) {
                    VolumeControlUtil.setVolumeDown(mContext)
                }


                //00默认  01发起
            }
            Config.ActiveUpload.DEVICE_POWER_CHANGE_KEY -> //设备端主动上传设备电量变化信息
            {
                TLog.error("电量变化==" + type + "充电类型+" + Status)
                SNEventBus.sendEvent(DEVICE_ELECTRICITY, type)
                Hawk.put("ELECTRICITY_STATUS", Status)
//                val  electricity=result[10]
//                if (result[11] > 0x0F) {
//                    val   mDisplayBattery = (result[11].toInt() shl 4)
//                    val   mCurrentBattery = (result[11].toInt() shr 4)
//                    TLog.error("实时监听的电量++"+mCurrentBattery)
//                    TLog.error("实时监听的电量++"+mDisplayBattery)
//                    SNEventBus.sendEvent(DEVICE_ELECTRICITY, electricity)
//                }
                //  SNEventBus.sendEvent(DEVICE_ELECTRICITY, electricity)
            }
            Config.ActiveUpload.DEVICE_DIAL_ID -> {
                Hawk.put<Int>(Config.SAVE_DEVICE_CURRENT_DIAL, type.toInt())

                if (HelpUtil.netWorkCheck(mContext))
                    GlobalScope.launch(Dispatchers.IO)
                    {
                        kotlin.runCatching {
                            val hashMap=HashMap<String,String>()
                            val setList = mutableSetOf<HashMap<String,String>>()
                            val userHashMap = HashMap<String,String>()
                            userHashMap["dialId"]= if(type == 65533) "0" else type.toString()
                            if(type == 17 || type == 18 || type == 19){
                                userHashMap["stateCode"] = "2"
                            }
                            else if(type == 65533){
                                userHashMap["stateCode"] = "3"
                            }else{
                                userHashMap["stateCode"] = "4"
                            }

                            hashMap["type"]=  if(type == 65533) "3" else type.toString()
                            hashMap["dialId"]= if(type == 65533) "0" else type.toString()
                            setList.add(hashMap)
                            Log.e("广播","-------校验表盘="+Gson().toJson(setList))

                            DetailDialViewApi.mDetailDialViewApi.updateUserDial(userHashMap)
                            RecommendDialViewApi.mRecommendDialViewApi.checkDialSate(Gson().toJson(setList))
                        }.onSuccess {
                            TLog.error("BLEBROAD  效验id+"+type)
                            //当前表盘
                            Hawk.put(Config.SAVE_DEVICE_CURRENT_DIAL,type)
                            SNEventBus.sendEvent(DEVICE_DIAL_ID, type)
                        }.onFailure {
                        }
                    }
            }

        }
    }


    private fun sendActionBroadCast(type : Int){
        val intent = Intent();
        intent.setAction(Config.DEVICE_AUTO_MEASURE_BP_ACTION)
        intent.putExtra("bp_status",type)
        mContext.sendBroadcast(intent)
    }
}