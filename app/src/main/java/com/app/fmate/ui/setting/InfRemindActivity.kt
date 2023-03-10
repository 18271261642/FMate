package com.app.fmate.ui.setting

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.*
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.adapter.OtherSwitchAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.RemindConfig
import com.app.fmate.ui.setting.vewmodel.MyDeviceViewModel
import com.shon.connector.utils.TLog
import com.github.iielse.switchbutton.SwitchView
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.hjq.permissions.XXPermissions
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_inf_remind.*
import kotlinx.android.synthetic.main.activity_inf_remind.titleBar
import com.app.fmate.ui.ShowPermissionActivity
import com.app.fmate.utils.PermissionUtils


//消息提醒页面
class InfRemindActivity : BaseActivity<MyDeviceViewModel>() {

    lateinit var mOtherSwitchAdapter: OtherSwitchAdapter
      var mList: ArrayList<RemindConfig.Apps> = ArrayList()

    var remindConfig = RemindConfig()

    private var dialog: AlertDialog? = null


    private val handler = object : Handler(Looper.myLooper()!!){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                getPermission()
            }

        }
    }


    override fun layoutId()=R.layout.activity_inf_remind
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
//        TLog.error("app="+Gson().toJson(app))

        TLog.error("mList=="+mList)
        //填充推送app列表
        val nnList= Hawk.get<ArrayList<RemindConfig.Apps> >("RemindList",
            remindConfig.getRemindAppPushList() as ArrayList<RemindConfig.Apps>?
        )
        TLog.error("nnList+=${nnList.size}")
        TLog.error("nnList+=${nnList}")
        TLog.error("nnList+=${Gson().toJson(nnList)}")
        onState()
        if(nnList!=null&& nnList.isNotEmpty())
        {
            for (i in nnList.indices) {
                TLog.error("走进来了")
                mList.add(nnList[i])
            }
        }

        TLog.error("mList+="+Gson().toJson(userInfo.userConfig))
        TLog.error("存储以后取++"+Hawk.get<ArrayList<RemindConfig.Apps> >("RemindList"))
        setAdapter()

        getPermission()

        openNotify()


        remindMoreLayout.setOnClickListener {
            startActivity(Intent(this,ShowPermissionActivity::class.java))
        }
    }


    private fun openNotify(){
        val txtCon = resources.getString(R.string.string_sms_notify_desc)


        //设置Hello World前三个字符有点击事件
        //设置Hello World前三个字符有点击事件
        val textSpanned4 = SpannableStringBuilder(txtCon)

        textSpanned4.setSpan(UnderlineSpan(),txtCon.length-13, txtCon.length-7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showAlertNotify(false);
            }
        }
        textSpanned4.setSpan(
            clickableSpan,
            txtCon.length-13, txtCon.length-7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //注意：此时必须加这一句，不然点击事件不会生效
        remindNotifyTv.setMovementMethod(LinkMovementMethod.getInstance())
        remindNotifyTv.setText(textSpanned4)

    }


    private fun showAlertNotify(isSwitch : Boolean){
        dialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(R.string.content_authorized)
            .setCancelable(!isSwitch)
            .setMessage(if(isSwitch) "找到 F Mate 打开通知开关即可" else "找到 F Mate ,打开或重启服务即可")
            .setPositiveButton(
                getString(R.string.text_sure)
            ) { dialog, which ->
                PermissionUtils.startToNotificationListenSetting(this@InfRemindActivity)
            }.show()
    }



    private  fun onState()
    {
       var call= Hawk.get(Config.database.INCOMING_CALL,userInfo.userConfig.callReminder.toInt())
        SwitchALL.isOpened=call==2
        var sms= Hawk.get(Config.database.SMS,userInfo.userConfig.smsReminder.toInt())
        SwitchSMS.isOpened=sms==2
        var other=Hawk.get(Config.database.OTHER,userInfo.userConfig.orderReminder.toInt())
        SwitchOther.isOpened=other==2
        if (SwitchOther.isOpened)
            ryRemind.visibility=View.VISIBLE
        else
            ryRemind.visibility=View.GONE
        TLog.error("call =$call")



        SwitchALL.setOnStateChangedListener(object :SwitchView.OnStateChangedListener{
            override fun toggleToOn(view: SwitchView?) {
                userInfo.userConfig.callReminder="2"
                Hawk.put(Config.database.INCOMING_CALL,2)
                setSwitchButton()
                SwitchALL.isOpened=true

                requestPermission(false)
            }

            override fun toggleToOff(view: SwitchView?) {
                userInfo.userConfig.callReminder="1"
                Hawk.put(Config.database.INCOMING_CALL,1)
                setSwitchButton()
                SwitchALL.isOpened=false
            }
        })
        SwitchSMS.setOnStateChangedListener(object :SwitchView.OnStateChangedListener{
            override fun toggleToOn(view: SwitchView?) {
                userInfo.userConfig.smsReminder="2"
                Hawk.put(Config.database.SMS,2)
                SwitchSMS.isOpened=true
                setSwitchButton()
                requestPermission(true)
            }

            override fun toggleToOff(view: SwitchView?) {
                userInfo.userConfig.smsReminder="1"
                Hawk.put(Config.database.SMS,1)
                SwitchSMS.isOpened=false
                setSwitchButton()
            }
        })
        SwitchOther.setOnStateChangedListener(object :SwitchView.OnStateChangedListener{
            override fun toggleToOn(view: SwitchView?) {
                userInfo.userConfig.orderReminder="2"
                Hawk.put(Config.database.OTHER,2)
                SwitchOther.isOpened=true
                setSwitchButton()
                ryRemind.visibility=View.VISIBLE
                //通知开关
                val hasNotificationPermission: Boolean =
                    PermissionUtils.hasNotificationListenPermission(this@InfRemindActivity)
                if(!hasNotificationPermission){
                    showAlertNotify(true)
                }

            }

            override fun toggleToOff(view: SwitchView?) {
                userInfo.userConfig.orderReminder="1"
                Hawk.put(Config.database.OTHER,1)
                SwitchOther.isOpened=false
                setSwitchButton()
                ryRemind.visibility=View.GONE
            }
        })
    }

    private fun setSwitchButton() {
        var value = HashMap<String, String>()
        value["callReminder"] =userInfo.userConfig.callReminder
        value["smsReminder"]=userInfo.userConfig.smsReminder
        value["orderReminder"]=userInfo.userConfig.orderReminder
        mViewModel.setUserInfo(value)
        Hawk.put(Config.database.USER_INFO,userInfo)
    }

    private  fun setAdapter()
    {
        ryRemind.layoutManager = LinearLayoutManager(
            this@InfRemindActivity,
            LinearLayoutManager.VERTICAL,
            false
        )

        mOtherSwitchAdapter = OtherSwitchAdapter(mList)
        ryRemind.adapter = mOtherSwitchAdapter
        mOtherSwitchAdapter.addChildClickViewIds(R.id.Switch)
        mOtherSwitchAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.Switch -> {
                    mList[position].isOn = !mList[position].isOn
                    Hawk.put("RemindList", mList)
                    TLog.error("=${Gson().toJson(mList[position])}")
                }
            }
        }
    }


    private fun requestPermission(isSms : Boolean){


       // XXPermissions.with(this).permission(Permission.READ_SMS).request { permissions, all ->  }

       // XXPermissions.with(this).permission(Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS).request{permissions,all->}
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG),0x00)

        if(isSms){
            XXPermissions.with(this).permission(Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS).request { permissions, all ->
                handler.sendEmptyMessage(0x00)
            }

//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS),0x00)
//            XXPermissions.with(this).permission(Permission.READ_SMS).request { permissions, all ->
//                handler.sendEmptyMessage(0x00)
//            }

            return
        }


        XXPermissions.with(this).permission(Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS).request { permissions, all ->
            handler.sendEmptyMessage(0x00);
        }

        XXPermissions.with(this).permission(Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG).request { permissions, all ->  }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            XXPermissions.with(this).permission(Manifest.permission.ANSWER_PHONE_CALLS).request{ permissions, all ->

            }
        }


    }


    private fun getPermission(){
        try {

            val isSmsStatus = XXPermissions.isGranted(this,Manifest.permission.READ_SMS)

            if(!isSmsStatus){
                SwitchSMS.isOpened = false
            }


            val isPhone = XXPermissions.isGranted(this,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS);


            val isP = XXPermissions.isPermanentDenied(this,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS)

           TLog.error("------isPhone="+isPhone +" isSms="+isSmsStatus+" "+isP)

            if(!isPhone){
                SwitchALL.isOpened = false
            }

            //通知开关
            val hasNotificationPermission: Boolean =
                PermissionUtils.hasNotificationListenPermission(this)
            if(!hasNotificationPermission){
                SwitchOther.isOpened = false
                ryRemind.visibility=if(hasNotificationPermission) View.VISIBLE else View.GONE
            }



        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        TLog.error("-----requestCode="+requestCode+" rrr="+resultCode)
        getPermission()
    }
}