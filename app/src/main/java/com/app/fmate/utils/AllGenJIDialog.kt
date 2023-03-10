package com.app.fmate.utils

import android.content.Context
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.app.fmate.*
import com.app.fmate.blecontent.BleConnection
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.login.LoginApi
import com.app.fmate.network.api.login.LoginBean
import com.app.fmate.ui.login.viewMode.UserViewModel
import com.google.gson.Gson
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.ly.genjidialog.other.DialogGravity
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.BLEManager
import com.shon.connector.BleWrite
import com.shon.connector.utils.TLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 封装格式各样的 没有啥作用的dialog
 */
class AllGenJIDialog {
    companion object {
        //查找设备
        fun findDialog(manager: FragmentManager) {
            newGenjiDialog {
                layoutId = R.layout.dialog_find
                dimAmount = 0.3f
                isFullHorizontal = true
                isFullVerticalOverStatusBar = false
                gravity = DialogGravity.CENTER_CENTER
                animStyle = R.style.BottomTransAlphaADAnimation
                convertListenerFun { holder, dialog ->
                    var tvOK = holder.getView<TextView>(R.id.tvOK)

                    tvOK?.setOnClickListener {
                        BleWrite.writeFindDeviceSwitchCall(1)
                        dialog.dismiss()
                    }
                }
            }.showOnWindow(manager)
        }

        fun deleteDialog(manager: FragmentManager) {
            manager?.let { it ->
                newGenjiDialog {
                    layoutId = R.layout.dialog_delete
                    dimAmount = 0.3f
                    isFullHorizontal = true
                    animStyle = R.style.AlphaEnterExitAnimation
                    convertListenerFun { holder, dialog ->
                        val dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                        val dialogSet = holder.getView<TextView>(R.id.dialog_confirm)
                        dialogSet?.setOnClickListener {
                            TLog.error("address==" + Hawk.get("address"))
                            var address = Hawk.get<String>("address")
                            if (!address.isNullOrEmpty()) {
                                BLEManager.getInstance().disconnectDevice(address)
                                BLEManager.getInstance().dataDispatcher.clearAll()
                            }


                            var value = HashMap<String, String>()

                            var  userInfoBean: LoginBean? = Hawk.get<LoginBean>(Config.database.USER_INFO)

                            value["mac"] =""
                            GlobalScope.launch(Dispatchers.IO)
                            {
                                kotlin.runCatching {
                                    LoginApi.loginApi.setUserInfo(value)
                                }.onSuccess {loginBean->
                                  var  userInfoBean: LoginBean? = Hawk.get<LoginBean>(Config.database.USER_INFO)
                                        userInfoBean?.user=loginBean.data.user
                                    Hawk.put("address", "")
                                    Hawk.put("name", "")
                                    TLog.error("正常删除设备"+Gson().toJson(loginBean.data)+"\n"+Hawk.get<String>("address"))
                                }.onFailure {
                                    TLog.error("异常删除设备")
                                }
                            }
                            Hawk.put("address", "")
                            Hawk.put("name", "")
                        //    RoomUtils.roomDeleteAll()
                            BleConnection.Unbind = true
                            Hawk.put("Unbind","AllGenJID Unbind=true")
                            SNEventBus.sendEvent(Config.eventBus.DEVICE_DELETE_DEVICE)
                            dialog.dismiss()
                        }
                        dialogCancel?.setOnClickListener {
                            dialog.dismiss()
                        }

                    }
                }.showOnWindow(it)
            }
        }

        fun mapDialog(manager: FragmentManager) {
            manager?.let {
                newGenjiDialog {
                    layoutId = R.layout.dialog_delete
                    dimAmount = 0.3f
                    isFullHorizontal = true
                    animStyle = R.style.AlphaEnterExitAnimation
                    convertListenerFun { holder, dialog ->

                        var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                        var dialogSet = holder.getView<TextView>(R.id.dialog_confirm)
                        var dialogContent = holder.getView<TextView>(R.id.dialog_content)
                        dialogContent?.text = "本次运动距离过短,将不会记录数据.是否结束?"
                        dialogSet?.setOnClickListener {
                            SNEventBus.sendEvent(Config.eventBus.MAP_MOVEMENT_DISSATISFY)
                            dialog.dismiss()
                        }
                        dialogCancel?.setOnClickListener {
                            dialog.dismiss()
                        }

                    }
                }.showOnWindow(it)
            }
        }


        fun signOutDialog(
            manager: FragmentManager,
            mViewModel: UserViewModel,
            userInfo: LoginBean,
            context: Context
        ) {
            manager?.let {
                newGenjiDialog {
                    layoutId = R.layout.dialog_delete
                    dimAmount = 0.3f
                    isFullHorizontal = true
                    animStyle = R.style.AlphaEnterExitAnimation
                    convertListenerFun { holder, dialog ->

                        var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                        var dialogSet = holder.getView<TextView>(R.id.dialog_confirm)
                        var dialogContent = holder.getView<TextView>(R.id.dialog_content)
                        dialogContent?.text = context.resources.getString(R.string.string_logout_cuurent_accout)
                        dialogSet?.setOnClickListener {
                            userInfo.token = ""//只清空 token
                            Hawk.put(Config.database.USER_INFO, userInfo)
                            TLog.error("退出时=="+Hawk.get<String>("address"))
                            if (!Hawk.get<String>("address").isNullOrEmpty()) {
                                BLEManager.getInstance().disconnectDevice(Hawk.get("address"))
                                Hawk.put("address","")
                                BLEManager.getInstance().dataDispatcher.clearAll()
                            }
                            RoomUtils.roomDeleteAll()
                            JumpUtil.startLoginActivity(context)
                            AppActivityManager.getInstance().finishAllActivity()
                            dialog.dismiss()
                        }
                        dialogCancel?.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                }.showOnWindow(it)
            }
        }
    }
}