package com.app.fmate.ui.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.app.fmate.Config.database.WEATHER
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
import com.shon.connector.utils.TLog
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.hawk.Hawk
import com.shon.connector.bean.DataBean
import kotlinx.android.synthetic.main.activity_setting_weather.*
import kotlinx.android.synthetic.main.activity_setting_weather.ToolbarTitle
import kotlinx.android.synthetic.main.activity_setting_weather.wheelView_hours
import kotlinx.android.synthetic.main.activity_setting_weather.wheelView_mm
import kotlinx.android.synthetic.main.title_bar_two.*

class SettingWeatherActivity : BaseActivity<BaseViewModel>(),View.OnClickListener {

    var dataBean: DataBean = DataBean()
    override fun layoutId() = R.layout.activity_setting_weather
    private val DECIMAL_DIGITS = 1 //小数的位数
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(ToolbarTitle)
            .init()
        sunrise()
        spinner()
        edtTemp()
        img_calendar.setImageResource(R.mipmap.icon_arrow_left)
        tv_titlebar_title.text = "天气设置"
        btnSave.setOnClickListener(this)
        img_calendar.setOnClickListener(this)

    }

    private fun sunrise() {
        wheelView_hours.setTextSize(20f)
        wheelView_hours.setLineSpacingMultiplier(2f)
        wheelView_hours.setDividerType(WheelView.DividerType.CIRCLE)
        wheelView_mm.setTextSize(20f)
        wheelView_mm.setLineSpacingMultiplier(2f)
        wheelView_mm.setDividerType(WheelView.DividerType.CIRCLE)
        val hoursList: MutableList<Int> =
            ArrayList()
        val mmList: MutableList<Int> =
            ArrayList()
        for (i in 0..24)
            hoursList.add(i)
        for (i in 0..60)
            mmList.add(i)
        wheelView_hours.adapter = ArrayWheelAdapter(hoursList)
        wheelView_mm.adapter = ArrayWheelAdapter(mmList)
        wheelView_mm.setOnItemSelectedListener { index ->
            dataBean.sunriseMin = index
        }
        wheelView_hours.setOnItemSelectedListener { index ->
            dataBean.sunriseHours = index
        }

        wheelViewSunsetHours.setTextSize(20f)
        wheelViewSunsetHours.setLineSpacingMultiplier(2f)
        wheelViewSunsetHours.setDividerType(WheelView.DividerType.CIRCLE)
        wheelViewSunsetMm.setTextSize(20f)
        wheelViewSunsetMm.setLineSpacingMultiplier(2f)
        wheelViewSunsetMm.setDividerType(WheelView.DividerType.CIRCLE)
        wheelViewSunsetHours.adapter = ArrayWheelAdapter(hoursList)
        wheelViewSunsetMm.adapter = ArrayWheelAdapter(mmList)
        wheelViewSunsetMm.setOnItemSelectedListener { index ->
            dataBean.sunsetMin = index
        }
        wheelViewSunsetHours.setOnItemSelectedListener { index ->
            dataBean.sunsetHours = index
        }
    }

    private fun spinner() {


        spWeatherType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                pos: Int, id: Long
            ) {
                TLog.error(
                    "onItemSelected : parent.id=" + parent.id + ",pos=" + pos + ",id=" + id
                )
                dataBean.weatherType=(pos+1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


    private fun edtTemp() {
        editTemper.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                decimalNumber(s.toString(), editTemper)
            }
        })
        editHighestTemper.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                decimalNumber(s.toString(), editHighestTemper)
                TLog.error("edit==${editHighestTemper.text}")
//                if (s.toString().contains(".")) {
//                    if (s.length - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
//                        var s1 = s.toString().subSequence(
//                            0,
//                            s.toString().indexOf(".") + DECIMAL_DIGITS + 1
//                        )
//                        editTemper.setText(s1)
//                        editTemper.setSelection(s1.length)
//                    }
//                }
//                if (s.toString().trim().substring(0) == ".") {
//                    var s1 = "0$s"
//                    editTemper.setText(s1)
//                    editTemper.setSelection(2)
//                }
//                if (s.toString().startsWith("0")
//                    && s.toString().trim().length > 1
//                ) {
//                    if (s.toString().substring(1, 2) != ".") {
//                        editTemper.setText(s.subSequence(0, 1))
//                        editTemper.setSelection(1)
//                        return
//                    }
//                }


            }
        })
        editLowTemper.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                decimalNumber(s.toString(), editLowTemper)
                TLog.error("edit==${editLowTemper.text}")
//                if (s.toString().contains(".")) {
//                    if (s.length - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
//                        var s1 = s.toString().subSequence(
//                            0,
//                            s.toString().indexOf(".") + DECIMAL_DIGITS + 1
//                        )
//                        editTemper.setText(s1)
//                        editTemper.setSelection(s1.length)
//                    }
//                }
//                if (s.toString().trim().substring(0) == ".") {
//                    var s1 = "0$s"
//                    editTemper.setText(s1)
//                    editTemper.setSelection(2)
//                }
//                if (s.toString().startsWith("0")
//                    && s.toString().trim().length > 1
//                ) {
//                    if (s.toString().substring(1, 2) != ".") {
//                        editTemper.setText(s.subSequence(0, 1))
//                        editTemper.setSelection(1)
//                        return
//                    }
//                }


            }
        })
        editAirQuality.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                decimalNumber(s.toString(), editAirQuality)
                TLog.error("edit==${editAirQuality.text}")


            }
        })
        editUVIndex.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                decimalNumber(s.toString(), editUVIndex)
                TLog.error("edit==${editUVIndex.text}")



            }
        })
        editHumidity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                decimalNumber(s.toString(), editHumidity)
                TLog.error("edit==${editHumidity.text}")
//


            }
        })
    }

    private fun decimalNumber(s: String, edt: EditText) {

        if (s.contains(".")) {
            if (s.length - 1 - s.indexOf(".") > DECIMAL_DIGITS) {
                var s1 = s.subSequence(
                    0,
                    s.indexOf(".") + DECIMAL_DIGITS + 1
                )
                edt.setText(s1)
                edt.setSelection(s1.length)
            }
        }
        if (s.trim().substring(0) == ".") {
            var s1 = "0$s"
            edt.setText(s1)
            edt.setSelection(2)
        }
        if (s.startsWith("0")
            && s.trim().length > 1
        ) {
            if (s.substring(1, 2) != ".") {
                edt.setText(s.subSequence(0, 1))
                edt.setSelection(1)
                return
            }
        }
    }

    override fun onClick(v: View) {
       when(v.id)
       {
           R.id.img_calendar-> finish()
           R.id.btnSave->
           {
               dataBean.temperature=editTemper.text.toString()
               dataBean.highestTemperatureToday=editHighestTemper.text.toString()
               dataBean.lowestTemperatureToday=editLowTemper.text.toString()
               dataBean.airQuality=editAirQuality.text.toString()
               dataBean.uvIndex=editUVIndex.text.toString()
               dataBean.humidity=editHumidity.text.toString()
               dataBean.unicodeType=DataBean.TEMPERATURE_FEATURES_UNICODE
               dataBean.unicodeContent=editPlaceName.text.toString()
               TLog.error("data==${Gson().toJson(dataBean)}")
              // BleWrite.writeWeatherCall(dataBean)
               Hawk.put(WEATHER,dataBean)
               finish()
           }

       }
    }
}