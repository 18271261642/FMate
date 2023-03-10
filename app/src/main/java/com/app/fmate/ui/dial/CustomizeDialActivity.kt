package com.app.fmate.ui.dial

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.adapter.CustomizeColorDialAdapter
import com.app.fmate.adapter.CustomizeFunctionDialAdapter
import com.app.fmate.adapter.CustomizePlacementDialAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.FlashBean
import com.app.fmate.bean.dialBean.CustomizeColorBean
import com.app.fmate.bean.dialBean.CustomizeFunctionBean
import com.app.fmate.bean.dialBean.CustomizePlacementBean
import com.app.fmate.bean.room.AppDataBase
import com.app.fmate.bean.room.CustomizeDialBean
import com.app.fmate.bean.room.CustomizeDialDao
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.dialView.DetailDialViewModel
import com.app.fmate.pictureselector.GlideEngine
import com.app.fmate.ui.setting.flash.FlashCall
import com.app.fmate.utils.*
import com.app.fmate.view.DateUtil
import com.app.fmate.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.DataDispatcher
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_customize_dial.*
import kotlinx.android.synthetic.main.activity_customize_dial.titleBar
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


//自定义表盘
class CustomizeDialActivity : BaseActivity<DetailDialViewModel>(), View.OnClickListener
   {

       private val tags = "CustomizeDialActivity"

    private lateinit var mCustomizeColorDialAdapter: CustomizeColorDialAdapter
    private var mCustomizeColorDialList = arrayListOf<CustomizeColorBean>()
    private lateinit var mCustomizeFunctionDialAdapter: CustomizeFunctionDialAdapter
    private var mCustomizeFunctionDialList = arrayListOf<CustomizeFunctionBean>()
    private lateinit var mCustomizePlacementDialAdapter: CustomizePlacementDialAdapter
    lateinit var sDao: CustomizeDialDao
    var mCustomizePlacementDialList = arrayListOf<CustomizePlacementBean>()
    var time = System.currentTimeMillis()
    var mCustomizeDialBean: CustomizeDialBean = CustomizeDialBean()

       private val instant by lazy { this }

    //是否正在同步表盘中
    var isSyncDial = false

       private var alertDialog : AlertDialog.Builder ?=null

    private var colorList = arrayListOf(
        R.color.white,
        R.color.black,
        R.color.red,
        R.color.color_dial_color_red_orange,
        R.color.color_dial_color_orange,
        R.color.color_dial_color_yellow,
        R.color.color_dial_color_cyan_blue,
        R.color.color_text_blue,
        R.color.color_dial_color_purple
    )

    override fun layoutId() = R.layout.activity_customize_dial
    var colorPosition = 0
    var functionPosition = 0
    var locationPosition = 0
    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
        isSyncDial = false
    }

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        SNEventBus.register(this)
        var data = intent.getStringExtra("data")
        TLog.error("data==" + data)
        if (data.isNullOrEmpty()) {
            mCustomizeDialBean = CustomizeDialBean()
        } else {
            mCustomizeDialBean = Gson().fromJson(data, CustomizeDialBean::class.java)
            colorPosition = mCustomizeDialBean.color
            functionPosition = mCustomizeDialBean.functionType
            locationPosition = mCustomizeDialBean.locationType
            if (mCustomizeDialBean.imgPath != null)
                ImgUtil.loadHead(imgDialBackground, mCustomizeDialBean.imgPath)
        }
        tvTime.text = HelpUtil.getSpan("10:28", "AM", 8)
        sDao = AppDataBase.instance.getCustomizeDialDao()
        mCustomizeDialBean.date = tvTime.text.toString()
        mCustomizeDialBean.startTime = time / 1000
        imgPhoto.setOnClickListener(this)
        imgRecall.setOnClickListener(this)
        JPSave.setOnClickListener(this)
        setColorAdapter()
        setFunctionAdapter()
        setPlacementAdapter()



        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener{
            override fun onBackClick() {
                if(isSyncDial){
                    backAlert()
                }else{
                    finish()
                }
            }

            override fun onActionImageClick() {

            }

            override fun onActionClick() {

            }

        })
    }


       override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
           if(keyCode == KeyEvent.KEYCODE_BACK){
               if(isSyncDial){
                   backAlert()
               }else{
                   finish()
               }
           }
           return true
       }


       private fun backAlert(){
           alertDialog = AlertDialog.Builder(instant)
           alertDialog!!.setTitle(resources.getString(R.string.string_text_remind))
           alertDialog!!.setMessage(resources.getString(R.string.string_dial_cancel_desc))
           alertDialog!!.setPositiveButton(resources.getString(R.string.text_sure)
           ) { p0, p1 ->
               p0.dismiss()
               finish()
           }.setNegativeButton(resources.getString(R.string.text_cancel)
           ) { p0, p1 ->
               p0.dismiss()
           }
           alertDialog!!.create().show()
       }




    private fun setPlacementAdapter() {
        mCustomizePlacementDialList = ArrayList()
        mCustomizePlacementDialList.add(CustomizePlacementBean(resources.getString(R.string.string_location_top), 0, locationPosition == 0))
        mCustomizePlacementDialList.add(CustomizePlacementBean(resources.getString(R.string.string_location_middle), 1, locationPosition == 1))
        mCustomizePlacementDialList.add(CustomizePlacementBean(resources.getString(R.string.string_location_bottom), 2, locationPosition == 2))
        ryPlacementDial.layoutManager = GridLayoutManager(this, 5)
        mCustomizePlacementDialAdapter = CustomizePlacementDialAdapter(mCustomizePlacementDialList)
        ryPlacementDial.adapter = mCustomizePlacementDialAdapter
        setPlacementInit(locationPosition)
        mCustomizePlacementDialAdapter.setOnItemClickListener { adapter, view, position ->
            mCustomizePlacementDialList.forEach {
                it.setSelected(false)
            }
            mCustomizePlacementDialList[position].setSelected(true)
            mCustomizePlacementDialAdapter.notifyDataSetChanged()
            setPlacementInit(position)
        }
    }

    private fun setPlacementInit(position: Int) {
        mCustomizeDialBean.locationType = position
        when (position) {
            0 -> {
                llDial.gravity = Gravity.TOP
            }
            1 -> {
                llDial.gravity = Gravity.CENTER
            }
            2 -> {
                llDial.gravity = Gravity.BOTTOM
            }
        }
    }

    private fun setFunctionInit(position: Int) {
        mCustomizeDialBean.functionType = position
        if (position == 0)
            imgDialType.visibility = View.GONE
        else
            imgDialType.visibility = View.VISIBLE
        when (position) {
            0 -> {
                var ca = Calendar.getInstance()
                ca.timeInMillis = time
                tvDate.text =
                    DateUtil.getDate(DateUtil.MM_AND_DD, time) + " " + DateUtil.getWeekStringEN(ca)
                mCustomizeDialBean.function =
                    DateUtil.getDate(DateUtil.MM_AND_DD, time) + " " + DateUtil.getWeekStringEN(ca)

            }
            1 -> {
                imgDialType.setImageResource(R.mipmap.icon_dial_heart)
                tvDate.text = "123"
                mCustomizeDialBean.function = "123"
            }
            2 -> {
                imgDialType.setImageResource(R.mipmap.icon_dial_step)
                tvDate.text = "12345"
                mCustomizeDialBean.function = "12345"
            }
            3 -> {
                imgDialType.setImageResource(R.mipmap.icon_dial_sleep)
                tvDate.text = "7h18m"
                mCustomizeDialBean.function = "7h18m"
            }
        }

    }

    private fun setColorInit(position: Int) {
        mCustomizeDialBean.color = position
        tvTime.setTextColor(resources.getColor(colorList[position]))
        tvDate.setTextColor(resources.getColor(colorList[position]))
        imgDialType.setColorFilter(resources.getColor(colorList[position]))
    }

    private fun setFunctionAdapter() {
        mCustomizeFunctionDialList = ArrayList()
        mCustomizeFunctionDialList.add(CustomizeFunctionBean(resources.getString(R.string.string_date), 0, functionPosition == 0))
        mCustomizeFunctionDialList.add(CustomizeFunctionBean(resources.getString(R.string.string_heart), 1, functionPosition == 1))
        mCustomizeFunctionDialList.add(CustomizeFunctionBean(resources.getString(R.string.string_step), 2, functionPosition == 2))
        mCustomizeFunctionDialList.add(CustomizeFunctionBean(resources.getString(R.string.string_sleep), 3, functionPosition == 3))
        ryFunctionDial.layoutManager = GridLayoutManager(this, 5)
        mCustomizeFunctionDialAdapter = CustomizeFunctionDialAdapter(mCustomizeFunctionDialList)
        ryFunctionDial.adapter = mCustomizeFunctionDialAdapter
        setFunctionInit(functionPosition)
        mCustomizeFunctionDialAdapter.setOnItemClickListener { adapter, view, position ->
            mCustomizeFunctionDialList.forEach {
                it.setSelected(false)
            }
            mCustomizeFunctionDialList[position].setSelected(true)
            mCustomizeFunctionDialAdapter.notifyDataSetChanged()
            setFunctionInit(position)
        }
    }

    private fun setColorAdapter() {
        mCustomizeColorDialList = ArrayList()
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_white), 0, colorPosition == 0))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_black), 1, colorPosition == 1))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_red), 2, colorPosition == 2))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_red_orange), 3, colorPosition == 3))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_orange), 4, colorPosition == 4))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_yellow), 5, colorPosition == 5))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_cyan), 7, colorPosition == 6))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_blue), 6, colorPosition == 7))
        mCustomizeColorDialList.add(CustomizeColorBean(resources.getString(R.string.string_purple), 8, colorPosition == 8))
        ryTextColorDial.layoutManager = GridLayoutManager(this, 6)
        mCustomizeColorDialAdapter = CustomizeColorDialAdapter(mCustomizeColorDialList)
        ryTextColorDial.adapter = mCustomizeColorDialAdapter
        setColorInit(colorPosition)
        mCustomizeColorDialAdapter.setOnItemClickListener { adapter, view, position ->
            mCustomizeColorDialList.forEach {
                it.isColorCheck = false
            }
            mCustomizeColorDialList[position].isColorCheck = true
            mCustomizeColorDialAdapter.notifyDataSetChanged()
            setColorInit(position)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgPhoto -> {
                PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .maxSelectNum(1)
                    .isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                    .selectionMode(PictureConfig.SINGLE)
                    .isPreviewImage(true)//是否可预览图片
                    .isEnableCrop(true)//是否裁剪
                    .withAspectRatio(1, 1)
                    .circleDimmedLayer(true) //圆形裁剪
                    // .isCropDragSmoothToCenter(true)// 裁剪框拖动时图片自动跟随居中
                    .circleDimmedLayer(true)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框
                    .showCropGrid(false)// 是否显示裁剪矩形网格
                    .minimumCompressSize(127)// 小于多少kb的图片不压缩
                    // .cropWH(360, 360)
                    .scaleEnabled(false)//是否可缩放
                    .cropImageWideHigh(360, 360)
                    .forResult(PictureConfig.CHOOSE_REQUEST)
            }
            R.id.imgRecall -> {
                mCustomizeDialBean.imgPath = ""
                imgDialBackground.setImageResource(R.drawable.icon_cus_dial_bg)
            }
            R.id.JPSave -> {
                showWaitDialog(resources.getString(R.string.string_set_dial_ing))

                TLog.error("保存时数据++" + Gson().toJson(mCustomizeDialBean))
                var uiFeature = 65533
                var grbByte = byteArrayOf()
                if (mCustomizeDialBean.imgPath.isNullOrEmpty()) {
                    uiFeature = 65534
                    // ShowToast.showToastLong("请选择自定义背景图案")
                    //  return
                } else {
                    uiFeature = 65533
//                    ThreadUtils.submit {
//                        var bitmap = Glide.with(this)
//                            .asBitmap()
//                            .load(mCustomizeDialBean.imgPath)
//                            .into(
//                                Target.SIZE_ORIGINAL,
//                                Target.SIZE_ORIGINAL
//                            ).get()
//                        grbByte = BitmapAndRgbByteUtil.bitmap2RGBData(bitmap)
//                        TLog.error("grbByte==${grbByte.size}")
//                        //   ImgUtil.loadMeImgDialCircle(imgRecall, bitmap)
//                    }

                    var bitmap = Glide.with(this)
                        .asBitmap()
                        .load(mCustomizeDialBean.imgPath)
                        .into(
                            Target.SIZE_ORIGINAL,
                            Target.SIZE_ORIGINAL
                        ).get()
                    grbByte = BitmapAndRgbByteUtil.bitmap2RGBData(bitmap)
                    TLog.error("grbByte==${grbByte.size}")
                }
                //生成新图并保存
                var newBit = BitmapAndRgbByteUtil.loadBitmapFromView(rlImg)
                var path = FileUtils.saveBitmapToSDCard(newBit, (time / 1000).toString())
                mCustomizeDialBean.value = path

                //删除所有，只保留一个
                sDao.deleteAll()

                sDao.insert(mCustomizeDialBean)


                TLog.error("==" + grbByte.size)

                val startByte = byteArrayOf(
                    0x00, 0xff.toByte(), 0xff.toByte(),
                    0xff.toByte()
                )

                FlashCall().writeFlashCall(
                    startByte, startByte, grbByte,
                    Config.eventBus.DIAL_CUSTOMIZE, -100, 0
                )

//
//                BleWrite.writeDialWriteAssignCall(
//                    DialCustomBean(
//                        1,
//                        uiFeature,
//                        grbByte.size,
//                        mCustomizeDialBean.color,
//                        mCustomizeDialBean.functionType,
//                        mCustomizeDialBean.locationType
//                    )
//                ) {
//                    isSyncDial = true
//                    TLog.error("it==" + it)
//                    when (it) {
//
//                        1 -> {
//                            hideWaitDialog()
//                            ShowToast.showToastLong("传入非法值")
//                            isSyncDial = false
//                        }
//                        2 -> {
//                            val startByte = byteArrayOf(
//                                0x00, 0xff.toByte(), 0xff.toByte(),
//                                0xff.toByte()
//                            )
//                            TLog.error("mCustomizeDialBean.imgPath+=" + mCustomizeDialBean.imgPath)
//                            BleWrite.writeFlashErasureAssignCall(
//                                16777215, 16777215
//                            ) { key ->
//                                if (key == 2) {
//                                    isSyncDial = true
//                                    TLog.error("开始擦写++" + grbByte.size)
//                                    FlashCall().writeFlashCall(
//                                        startByte, startByte, grbByte,
//                                        Config.eventBus.DIAL_CUSTOMIZE, -100, 0
//                                    )
//                                } else{
//                                    ShowToast.showToastLong(resources.getString(R.string.string_flash_no_support))
//                                    isSyncDial = false;
//                                }
//
//                            }
//
//                        }
//                        3 -> {
//                            hideWaitDialog()
//                            var hasMap = HashMap<String, String>()
//                            hasMap["dialId"] = "0"
//
//                            Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,0)
//
//                            mViewModel.updateUserDial(hasMap)
//                            // finish()
//                            // ShowToast.showToastLong("设备已经有存储这个表盘")
//                            //给后台一个 更改表盘的指令
//                            isSyncDial = false
//                        }
//                        4 -> {
//                            hideWaitDialog()
//                            ShowToast.showToastLong(resources.getString(R.string.string_dial_has_dial))
//                            isSyncDial = false
//                        }
//                    }
//                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    TLog.error("selectList+=" + selectList[0].cutPath.toString())

                    ImgUtil.loadHead(imgDialBackground,selectList[0].cutPath.toString()
                    ) { width, height ->
                        Log.e(tags,"-------选择的图片宽和高="+width+" "+height)
                        if(width<360 && height<360){
                            ShowToast.showToastShort(resources.getString(R.string.string_small_pickture))
                            return@loadHead
                        }else{
                            ImgUtil.loadHead(imgDialBackground, selectList[0].cutPath.toString())
                            mCustomizeDialBean.imgPath = selectList[0].cutPath.toString()
                        }
                    }



                }
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this)
        {
            SNEventBus.sendEvent(Config.eventBus.DIAL_CUSTOMIZE)
            ThreadUtils.runOnUiThread({

                finish()
            }, 500)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
        Config.eventBus.DIAL_CUSTOMIZE->
        {
            if(event.data == null)
                return
            val data = event.data as FlashBean
            var currentProgress = ((data.currentProgress.toDouble() / data.maxProgress) * 100).toInt()
            if (currentProgress <= 15)
                JPSave.setProgress(15f)
            else
                JPSave.setProgress(currentProgress.toFloat())
            JPSave.setText(resources.getString(R.string.string_current_schedule)+currentProgress +"%")
            // proBar.max = size
            // proBar.progress = type
            if (data.currentProgress == 1 && data.maxProgress == 1) {
                hideWaitDialog()
                var hasMap = HashMap<String, String>()
                hasMap["dialId"] = "0"
                mViewModel.updateUserDial(hasMap)
                DataDispatcher.callDequeStatus = true
                Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,0)

                Hawk.put(com.shon.connector.Config.SAVE_LOCAL_CUS_DIAL_URL,mCustomizeDialBean.value)
            }
        }
        }
    }

}