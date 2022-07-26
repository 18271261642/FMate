package com.example.xingliansdk.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.Config
import com.example.xingliansdk.Config.exercise
import com.example.xingliansdk.Config.exercise.MILE
import com.example.xingliansdk.R
import com.example.xingliansdk.network.api.homeView.HomeCardVoBean
import com.example.xingliansdk.network.api.login.LoginBean
import com.example.xingliansdk.utils.HawkUtil
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.ImgUtil
import com.example.xingliansdk.utils.Utils
import com.orhanobut.hawk.Hawk
import com.shon.connector.utils.TLog
import java.text.DecimalFormat

class HomeAdapter(data: MutableList<HomeCardVoBean.ListDTO>) :
    BaseQuickAdapter<HomeCardVoBean.ListDTO, BaseViewHolder>(R.layout.item_home, data) {
    var imgList = intArrayOf(
        R.mipmap.icon_home_sport,
        R.mipmap.icon_home_heart_rate,
        R.mipmap.icon_home_sleep,
        R.mipmap.icon_home_pressure,
        R.mipmap.icon_home_blodoxygen,
        R.mipmap.icon_home_bloodpressure,
        R.mipmap.icon_home_temperature,
        R.mipmap.icon_home_weight
    )

    private fun changeEnLanguage(str : String) : String{
        if(str == "运动记录")
            return context.getString(R.string.string_sport_record)
        if(str == "心率")
            return context.getString(R.string.string_heart)
        if(str == "睡眠")
            return context.getString(R.string.string_sleep)
        if(str == "压力")
            return context.getString(R.string.string_pressure)
        if(str == "血氧饱和度")
            return context.getString(R.string.string_spo2_title)
        if(str == "血压")
            return context.getString(R.string.string_bp)
        if(str == "体温")
            return context.getString(R.string.string_temp)
        if(str == "体重")
            return context.getString(R.string.string_weight)
        return context.getString(R.string.string_sport_record)
    }



    var decimalFormat = DecimalFormat("#.##")
    override fun convert(helper: BaseViewHolder, item: HomeCardVoBean.ListDTO?) {
        try {
            if (item == null) {
                return
            }

            val img = helper.getView<ImageView>(R.id.imgIcon)
            if (item.type >= 8)
                img.setImageResource(imgList[0])
            else
                img.setImageResource(imgList[item.type])
            //  ImgUtil.loadHomeCard(img, item.image)
//        TLog.error("卡片展示++" + item.image)
            //  helper.setImageResource(R.id.imgIcon, item.img)
            // helper.setText(R.id.tvItemStatusTitle,"记录")
            helper.setText(R.id.tvItemStatusTitle, changeEnLanguage(item.name))
            val tvItemStatusData = helper.getView<TextView>(R.id.tvItemStatusData)
            val tvItemStatusSubTitle = helper.getView<TextView>(R.id.tvItemStatusSubTitle)
            tvItemStatusSubTitle.text = item.describe
            //Log.e("ADAPTER","------itemData="+item.data)
//        TLog.error("item.dayContent=="+item.dayContent)
            if (!item.data.isNullOrEmpty() || HelpUtil.isNumericNotSize(item.data)) {
                tvItemStatusData.visibility = View.VISIBLE
                //   tvItemStatusSubTitle.text =DateUtil.getDate(DateUtil.MM_AND_DD,item.time*1000)
                //    tvItemStatusSubTitle.text = item.data
                if (item.type == 2) {
                    var timeStr = item.data
                    if(timeStr.length<6){

                        timeStr = "00"+context.resources.getString(R.string.string_hour)+timeStr
                    }


                    if(com.example.phoneareacodelibrary.Utils.isChinese()){
                        tvItemStatusData.text = HelpUtil.getSpan(
                            timeStr.substring(0, 2),
                            timeStr.substring(2, 4),
                            timeStr.substring(4, 6),
                            timeStr.substring(6, 8),
                            R.color.sub_text_color,
                            12
                        )
                    }else{

                        var enTimeStr = timeStr.replace("小时","H")
                        enTimeStr = enTimeStr.replace("分钟","M")

                        tvItemStatusData.text = HelpUtil.getSpan(
                            enTimeStr.substring(0, 2),
                            enTimeStr.substring(2, 3),
                            enTimeStr.substring(3, 5),
                            enTimeStr.substring(5, 6),
                            R.color.sub_text_color,
                            12
                        )
                    }

                    img.setImageResource(R.mipmap.icon_home_sleep_data)
                } else {
                    if (item.data.isNullOrEmpty())
                        item.data = "0"
                    when (item.type) {
                        0 -> {
                            if (HelpUtil.isNumericNotSize(item.data)) {
                                var userInfoBean = Hawk.get(Config.database.USER_INFO, LoginBean())
                               // var dis = decimalFormat.format(item.data.toString().toDouble() / 1000)
                                var dis = Utils.divi(item.data.toString().toDouble(), 1000.0, 2)

                                var unit = context.resources.getString(R.string.string_km)
                                if (userInfoBean == null || userInfoBean.userConfig.distanceUnit == 1) {
//                                    dis = decimalFormat.format(
//                                        item.data.toString().toDouble() / 1000 * MILE
//                                    )

                                    dis = decimalFormat.format(Utils.mul(dis, MILE, 3)).toDouble()
                                    unit = context.resources.getString(R.string.string_mile)
                                }
                                tvItemStatusData.text =
                                    HelpUtil.getSpan(
                                        dis.toString(), unit, 14, R.color.sub_text_color
                                    )
                                img.setImageResource(R.mipmap.icon_home_sport_data)
                            }
                        }
                        1 -> {
                            if (HelpUtil.isNumericNotSize(item.data)) {
                                tvItemStatusData.text =
                                    HelpUtil.getSpan(item.data, context.getString(R.string.string_time_minute), 14, R.color.sub_text_color)
                                img.setImageResource(R.mipmap.icon_home_heart_rate_data)
                            }
                        }
                        3 -> {
                            var contentString = ""
                            if (HelpUtil.isNumericNotSize(item.data)) {
                                var num = item.data.toInt()

                                var txtColor = R.color.sub_text_color
                                when (num) {
                                    in 1 until 30 -> {
                                        contentString = context.getString(R.string.string_pressure_status_1)
                                        txtColor = R.color.color_blood_pressure_low
                                    }
                                    in 30 until 60 -> {
                                        contentString = context.getString(R.string.string_pressure_status_2)
                                        txtColor = R.color.color_blood_pressure_normal
                                    }
                                    in 60 until 80 -> {
                                        contentString = context.getString(R.string.string_pressure_status_3)
                                        txtColor = R.color.color_blood_pressure_one
                                    }
                                    in 80 until 100 -> {
                                        contentString = context.getString(R.string.string_pressure_status_4)
                                        txtColor = R.color.color_blood_pressure_three
                                    }
                                }
                                tvItemStatusData.text =
                                    HelpUtil.getSpan(
                                        item.data,
                                        contentString,
                                        14,
                                        txtColor
                                    )
                                img.setImageResource(R.mipmap.icon_home_pressure_data)
                            }
                        }
                        4 -> {
                            if (HelpUtil.isNumeric(item.data))
                                if (item.data.toInt() > 0) {
                                    tvItemStatusData.text =
                                        HelpUtil.getSpan(item.data, "%", 14, R.color.sub_text_color)
                                    img.setImageResource(R.mipmap.icon_home_blodoxygen_data)
                                }
                        }
                        5 -> {
//                        if (HelpUtil.isNumeric(item.data))
                            if (item.data.isNotEmpty()) {
                                tvItemStatusData.text =
                                    HelpUtil.getSpan(item.data, "")
                                img.setImageResource(R.mipmap.icon_home_bloodpressure_data)

                            }
                        }
                        6 -> {
                            if (HelpUtil.isNumerEX(item.data))
                                if (item.data.toDouble() > 0) {
                                    tvItemStatusData.text =
                                        HelpUtil.getSpan(item.data, "℃", 14, R.color.sub_text_color)
                                    img.setImageResource(R.mipmap.icon_home_temperature_data)
                                }
                        }
                        7 -> {
                            if (HelpUtil.isNumerEX(item.data))
                                if (item.data.toDouble() > 0) {
                                    tvItemStatusData.text =
                                        HelpUtil.getSpan(item.data, context.getString(R.string.string_kilogram), 14, R.color.sub_text_color)
                                    img.setImageResource(R.mipmap.icon_home_weight_data)
                                }
                        }
                    }
                }
            } else
                tvItemStatusData.visibility = View.GONE
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}