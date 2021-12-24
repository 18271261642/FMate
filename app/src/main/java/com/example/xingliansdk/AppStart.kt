package com.example.xingliansdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.TextView
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.manager.NetState
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.JumpUtil.startMainHomeActivity
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.viewmodel.AppStartViewModel
import com.google.gson.Gson
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 */
class AppStart : BaseActivity<AppStartViewModel>() {
    override fun layoutId(): Int = R.layout.start
    var upDataUrl: String? = null
    var forceUpdate=false
    override fun initView(savedInstanceState: Bundle?) {
        val view = View.inflate(this, R.layout.start, null)
        setContentView(view)
        val aa = AlphaAnimation(0.3f, 1.0f)
        aa.duration = 3000
        view.startAnimation(aa)
        if(HelpUtil.isApkInDebug(XingLianApplication.mXingLianApplication))
        ShowToast.showToastLong("测试版"+ XingLianApplication.baseUrl)
        aa.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(arg0: Animation) {
              //  TLog.error("先走了这里"+Gson().toJson(userInfo))
              ///  startMainHomeActivity(this@AppStart)
                //JumpUtil.startDeviceInformationActivity(this@AppStart,true)
             //   setStart()
                mViewModel.appUpdate("aiHealth", HelpUtil.getVersionCode(this@AppStart))
            }
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) { it ->
            if (it.ota.isNullOrEmpty()) {
//                TLog.error("ota====")
                setStart()

            }
            else
            {
                upDataUrl=it.ota
                forceUpdate=it.isForceUpdate
                updateDialog()
            }
        }
        mViewModel.msg.observe(this){ code->
            TLog.error("code====$code")
            if (code.equals("312")||code.equals("-2"))
            {
                TLog.error("312  ota====")
                setStart()
            }
        }
    }
    private fun setStart() {
        if (userInfo==null||userInfo.token.isNullOrEmpty()) {
            JumpUtil.startLoginActivity(this)
            finish()
            return
        }
        else if (userInfo.user==null||userInfo.user.sex.isNullOrEmpty()||userInfo.user.sex.equals("0")) {
            TLog.error("else if "+Gson().toJson(userInfo))
            JumpUtil.startLoginActivity(this)
            finish()
            return
        }
        else {
            TLog.error("userInfo.token+="+userInfo.token)
            if(userInfo?.user?.mac?.isNotEmpty()!!)
                Hawk.put("address",userInfo.user.mac)
          //  mViewModel.appLoginTest()
            startMainHomeActivity(this@AppStart)
            finish()
        }
    }

    private fun updateDialog() {
        newGenjiDialog {
            layoutId = R.layout.dialog_delete
            dimAmount = 0.3f
            isFullHorizontal = true
            animStyle = R.style.AlphaEnterExitAnimation
            convertListenerFun { holder, dialog ->
                var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                var dialogSet = holder.getView<TextView>(R.id.dialog_confirm)
                var dialogContent = holder.getView<TextView>(R.id.dialog_content)
                if(forceUpdate)
                    dialogCancel?.visibility=View.GONE
                else
                    dialogCancel?.visibility=View.VISIBLE
                dialogContent?.text = "APP有更新是否下载最新app进行升级?"
                dialogSet?.setOnClickListener {
                    if (upDataUrl.isNullOrEmpty()) {
                        ShowToast.showToastLong("地址链接失效")
                        return@setOnClickListener
                    } else {
                        val intent = Intent()
                        intent.action = "android.intent.action.VIEW"
                        val contentUrl = Uri.parse(upDataUrl)
                        intent.data = contentUrl
                        startActivity(intent)
                    }
                    dialog.dismiss()
                }
                dialogCancel?.setOnClickListener {
                    setStart()
                    dialog.dismiss()
                }
            }
        }.showOnWindow(supportFragmentManager)
    }
    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        if (netState.isSuccess) {
            mViewModel.appUpdate("aiHealth", HelpUtil.getVersionCode(this@AppStart))
            TLog.error("网络===")
        } else {
            TLog.error("网络断开")
            ShowToast.showToastLong("网络断开!")
        }
    }
}