package com.app.fmate.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import com.app.fmate.bean.RemindConfig
import com.shon.connector.utils.TLog
import com.github.iielse.switchbutton.SwitchView

class OtherSwitchAdapter(data:MutableList<RemindConfig.Apps>):BaseQuickAdapter<RemindConfig.Apps,BaseViewHolder>(R.layout.item_remind,data) {
    var name= arrayOf("Email","Facebook","Wechat","Line","Weibo","Linkedln","QQ","Whats App","Viber","Instagram")
    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: RemindConfig.Apps) {
        if (item==null)
        {
            return
        }
       var  switchAlarmClock=helper.getView<SwitchView>(R.id.Switch)
        val tvName=helper.getView<TextView>(R.id.tv_name)
        tvName.textSize = 16f
        tvName.text=item.appName
     //   helper.setText(R.id.tv_name, name[helper.position])
        TLog.error("item.isOn"+item.isOn)
        switchAlarmClock.isOpened = item.isOn
    }
}