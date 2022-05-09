package com.example.xingliansdk.ui.setting

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.CardBean
import com.example.xingliansdk.bean.CardWeightBean
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.pictureselector.GlideEngine
import com.example.xingliansdk.ui.login.viewMode.UserViewModel
import com.example.xingliansdk.utils.*
import com.example.xingliansdk.view.DateUtil
import com.example.xingliansdk.widget.TitleBarLayout
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.tools.ToastUtils
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.DeviceInformationBean
import com.shon.connector.utils.ShowToast
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_device_information.*
import me.hgj.jetpackmvvm.network.NetworkUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.apache.commons.lang.StringUtils
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * 个人资料页面
 */
open class DeviceInformationActivity : BaseActivity<UserViewModel>(), View.OnClickListener,
    BleWrite.DeviceInformationCallInterface {


    private val tags = "DeviceInformationActivity"


    override fun layoutId() = R.layout.activity_device_information
    private var mImagePaths: String = ""//这里解释一下为啥设置俩个 如果为一个的话怎为选择并有选择记录的模式,俩个的话为重新创建
    private var register = false
    private var weightStatus = false
    private var time = System.currentTimeMillis()


    private val instant by lazy { this }


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        Thread().start()
        register = intent.getBooleanExtra("register", false)
        if (!register) {
            tvNext.text = "退出登录"
            tvContent.visibility = View.GONE
            tvSignOut.visibility = View.GONE
            tvLogout.visibility = View.GONE
            tvNext.visibility = View.GONE
            tvUserId.visibility = View.VISIBLE
//            TLog.error("在退出登录++$register")
        } else {
            tvNext.text = "下一步"
            titleBar.setActionText("")
            tvContent.visibility = View.VISIBLE
            tvSignOut.visibility = View.GONE
            tvLogout.visibility = View.GONE
            tvNext.visibility = View.VISIBLE
            tvUserId.visibility = View.GONE
        }
        titleBar.setTitleBarListener(object : TitleBarLayout.TitleBarListener {
            override fun onBackClick() {
                finish()
            }

            override fun onActionImageClick() {
                TLog.error("onActionImageClick")
            }

            override fun onActionClick() {
                if (TextUtils.equals(edtName.text.trim().toString(), "")) {
                    ShowToast.showToastLong("请输入昵称")
                    return
                }

                if(!NetworkUtil.isNetworkAvailable(instant)){
                    ToastUtils.s(instant,"当前无网络连接！")
                    return
                }

                setHeadImg()
                if (!imgCheck)
                    setUserInfo()//没改变图片的情况还是要 改变个人信息
            }
        })
        getInformation()
        setOnClick()
        initTimePicker()
        if (!register) {
            TLog.error("获取个人信息数据]]")
            mainViewModel.userInfo()
        }
        getCardData()
        initCustomOptionPicker()

    }

    private fun setUserInfo() {
        mDeviceInformationBean.name = edtName.text.toString()
        Hawk.put(Config.database.PERSONAL_INFORMATION, mDeviceInformationBean)
        TLog.error("保存的+${Gson().toJson(mDeviceInformationBean)}")
        var value = HashMap<String, String>()
        if (!mDeviceInformationBean.name.isNullOrEmpty())
            value["nickname"] = mDeviceInformationBean.name
        value["height"] = mDeviceInformationBean.height.toString()
        value["weight"] = mDeviceInformationBean.weight.toString()
        value["age"] = mDeviceInformationBean.age.toString()
        value["sex"] = mDeviceInformationBean.sex.toString()
        value["birthDate"] = DateUtil.getDate(DateUtil.YYYY_MM_DD, mDeviceInformationBean.birth)
        value["createTime"] = (time / 1000).toString()
        updateHomeWeight()
        mViewModel.setUserInfo(value!!)
        BleWrite.writeDeviceInformationCall(
            mDeviceInformationBean,
            this@DeviceInformationActivity,
            true
        )
    }

    private fun getInformation() {
        if (mDeviceInformationBean.sex == 1) {
            setting_sex.setContentText(getString(R.string.man))
        } else {
            setting_sex.setContentText(getString(R.string.woman))
        }
     //   setting_age.setContentText(mDeviceInformationBean.age.toString())
        setting_age.setContentText(
            DateUtil.getDate(
                DateUtil.YYYY_MM_DD,
                mDeviceInformationBean.birth
            )
        )
        setting_height.setContentText(mDeviceInformationBean.height.toString() + "cm")
        setting_weight.setContentText(mDeviceInformationBean.weight.toString() + "kg")
        if (!register)
            edtName.setText(mDeviceInformationBean.name)

        tvUserId.text = userInfo.user.userId
        if (userInfo.user.headPortrait.isNullOrEmpty()) {
            var img = Hawk.get<String>(Config.database.IMG_HEAD)
            if (Hawk.get<String>(Config.database.IMG_HEAD).isNullOrEmpty()) {
                if (mDeviceInformationBean.sex == 1)
                    ImgUtil.loadHead(imgHead, R.mipmap.icon_head_man)
                else
                    ImgUtil.loadHead(imgHead, R.mipmap.icon_head_woman)
                return
            }
            if (FileUtil.isFileExists(img)) {  //显示本地图片
                ImgUtil.loadHead(imgHead, img)
            }
        } else {
            TLog.error("保存头像" + userInfo.user.headPortrait)
            ImgUtil.loadImage(this, userInfo.user.headPortrait)
            ImgUtil.loadHead(imgHead, userInfo.user.headPortrait)
        }

    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.result.observe(this) {
            TLog.error("it==${Gson().toJson(it)}")
            userInfo.user = it.user
            userInfo.userConfig = it.userConfig
            Hawk.put(Config.database.USER_INFO, userInfo)
            SNEventBus.sendEvent(Config.eventBus.EVENT_BUS_IMG_HEAD)
            mainViewModel.userInfo.postValue(it)
            HelpUtil.hideSoftInputView(this)
            finish()
        }
        mViewModel.resultImg.observe(this) {
            TLog.error("返回数据===" + Gson().toJson(it))
//            ImgUtil.loadImage(this, mImagePaths?.get(0).toString())
            setUserInfo()
            ThreadUtils.submit {
                SNEventBus.sendEvent(Config.eventBus.EVENT_BUS_IMG_HEAD)
            }
        }
        mainViewModel.result.observe(this) {
            userInfo.user = it.user
            userInfo.userConfig = it.userConfig
            userInfo.permission = it.permission
            Hawk.put(Config.database.USER_INFO, userInfo)
            mDeviceInformationBean = DeviceInformationBean(
                it.user.sex.toInt(),
                it.user.age.toInt(),
                it.user.height.toInt(),
                it.user.weight.toFloat(),
                mDeviceInformationBean.language.toInt(),
                it.userConfig.timeFormat,
                1,
                it.userConfig.distanceUnit,
                mDeviceInformationBean.wearHands.toInt(),
                it.userConfig.temperatureUnit,
                it.userConfig.movingTarget.toLong(),
                DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD, it.user.birthDate),
                it.user.nickname
            )
            Hawk.put(Config.database.SLEEP_GOAL, it.userConfig.sleepTarget.toLong())
            Hawk.put(Config.database.PERSONAL_INFORMATION, mDeviceInformationBean)
            getInformation()
        }
    }

    private fun setOnClick() {
        setting_sex.setOnClickListener(this)
        setting_age.setOnClickListener(this)
        setting_height.setOnClickListener(this)
        setting_weight.setOnClickListener(this)
        imgHead.setOnClickListener(this)
        tvNext.setOnClickListener(this)
        tvSignOut.setOnClickListener(this)
        tvLogout.setOnClickListener(this)
    }

    private var pvSexCustomOptions: OptionsPickerView<Any>? = null
    private var pvHeightCustomOptions: OptionsPickerView<Any>? = null
    private var pvWeightCustomOptions: OptionsPickerView<Any>? = null
    private var cardSexItem: ArrayList<CardBean> = ArrayList()
    private var cardHeightItem: ArrayList<CardBean> = ArrayList()
   // private var cardWeightItem: ArrayList<CardBean> = ArrayList()
    private var indexSex = 0
    var indexHeight = 0
    var indexWeight = 0

    private var pvCustomOptions :OptionsPickerView<Any>? = null

    private var cardWeightItem: ArrayList<CardWeightBean> = ArrayList()
    private val options2Items = ArrayList<ArrayList<String>>()

    private fun initCustomOptionPicker() { //条件选择器初始化，自定义布局
        indexSex = if (mDeviceInformationBean.sex == 1) 0 else 1
        if (pvSexCustomOptions?.isShowing == true)
            pvSexCustomOptions?.dismiss()
        pvSexCustomOptions = OptionsPickerBuilder(this)
        { options1, _, _, _ -> //返回的分别是三个级别的选中位置
            mDeviceInformationBean.sex = cardSexItem[options1].id + 1
            setting_sex.setContentText(cardSexItem[options1].pickerViewText)
            if (mImagePaths.isNotEmpty())
                return@OptionsPickerBuilder
            if (userInfo == null || userInfo.user == null || userInfo.user.headPortrait.isNullOrEmpty()) {
                if (mDeviceInformationBean.sex == 1)
                    ImgUtil.loadHead(imgHead, R.mipmap.icon_head_man)
                else
                    ImgUtil.loadHead(imgHead, R.mipmap.icon_head_woman)
            }
        }
            .setLayoutRes(
                R.layout.pickerview_custom_options
            ) { v ->
                val tvSubmit =
                    v.findViewById<View>(R.id.tv_finish) as TextView
                val ivCancel =
                    v.findViewById<View>(R.id.iv_cancel) as ImageView
                tvSubmit.setOnClickListener {
                    pvSexCustomOptions?.returnData()
                    pvSexCustomOptions?.dismiss()
                }
                ivCancel.setOnClickListener { pvSexCustomOptions?.dismiss() }
            }
            .isDialog(true)
            .setCyclic(false, false, false)
            .isRestoreItem(true)
            .setSelectOptions(indexSex)
            .setOutSideCancelable(false)
            .build()
        pvSexCustomOptions?.setPicker(cardSexItem as List<Any>?) //添加数据
        indexHeight = if (mDeviceInformationBean.height <= 50) 0 else mDeviceInformationBean.height-50
        TLog.error("mDeviceInformationBean.height+="+mDeviceInformationBean.height+"===indexHeight="+indexHeight)
        if (pvHeightCustomOptions?.isShowing == true)
            pvHeightCustomOptions?.dismiss()
        pvHeightCustomOptions = OptionsPickerBuilder(
            this
        ) { options1, _, _, _ -> //返回的分别是三个级别的选中位置
            mDeviceInformationBean.height = cardHeightItem[options1].pickerViewText.toInt()
            setting_height.setContentText(cardHeightItem[options1].pickerViewText+"cm")
        }
            .setLayoutRes(R.layout.pickerview_custom_options
            ) { v ->
                val tvSubmit =
                    v.findViewById<View>(R.id.tv_finish) as TextView
                val ivCancel =
                    v.findViewById<View>(R.id.iv_cancel) as ImageView
                tvSubmit.setOnClickListener {
                    pvHeightCustomOptions?.returnData()
                    pvHeightCustomOptions?.dismiss()
                }
                ivCancel.setOnClickListener { pvHeightCustomOptions?.dismiss() }
            }
            .isDialog(true)
            .setCyclic(true, false, false)
            .isRestoreItem(true)
            .setSelectOptions(indexHeight)
            .setOutSideCancelable(false)
            .build()
        pvHeightCustomOptions?.setPicker(cardHeightItem as List<Any>?) //添加数据

        indexWeight = if (mDeviceInformationBean.weight <= 30) 0 else (mDeviceInformationBean.weight-30).toInt()
        TLog.error("mDeviceInformationBean.height+="+mDeviceInformationBean.weight.toFloat()+"下标="+indexWeight)

//
//        if (pvWeightCustomOptions?.isShowing == true)
//            pvWeightCustomOptions?.dismiss()
//        pvWeightCustomOptions = OptionsPickerBuilder(
//            this
//        ) { options1, _, _, _ -> //返回的分别是三个级别的选中位置
//            mDeviceInformationBean.setWeight( cardWeightItem[options1].pickerViewText.toInt())
//            setting_weight.setContentText(cardWeightItem[options1].pickerViewText+"kg")
//        }
//            .setLayoutRes(R.layout.pickerview_custom_options
//            ) { v ->
//                val tvSubmit =
//                    v.findViewById<View>(R.id.tv_finish) as TextView
//                val ivCancel =
//                    v.findViewById<View>(R.id.iv_cancel) as ImageView
//                tvSubmit.setOnClickListener {
//                    pvWeightCustomOptions?.returnData()
//                    pvWeightCustomOptions?.dismiss()
//                }
//                ivCancel.setOnClickListener { pvWeightCustomOptions?.dismiss() }
//            }
//            .isDialog(true)
//            .setCyclic(true, false, false)
//            .isRestoreItem(true)
//            .setSelectOptions(indexWeight)
//            .setOutSideCancelable(false)
//            .build()
//        pvWeightCustomOptions?.setPicker(cardWeightItem as List<Any>?) //添加数据


        //当前的体重，转换为小数点前和后
        var currWeight = mDeviceInformationBean.weight;
        //小数点前
        val beforeWeight = StringUtils.substringBefore(currWeight.toString(),".")
        val afterWeight = StringUtils.substringAfter(currWeight.toString(),".")


        val cardWeightBean = cardWeightItem.findLast { it.kgInteger == beforeWeight }

        if (cardWeightBean != null) {
            //Log.e("体重","-------体重=="+cardWeightBean.kgInteger+" "+cardWeightBean.id)
            indexWeight = cardWeightBean.id -3
        }
        //体重选择
        pvCustomOptions = OptionsPickerBuilder(
            this
        ) { options1, option2, options3, v -> //返回的分别是三个级别的选中位置
          //  timeDialog = System.currentTimeMillis() //重置时间戳
            val item = cardWeightItem[options1]
            val weight = item.pickerViewText + options2Items[options1][option2]
            TLog.error("weight.toDouble()+" + weight.toFloat())
            setting_weight.setContentText(weight+"kg")
            mDeviceInformationBean.weight = weight.toFloat()
//            if (!mAllList.isNullOrEmpty() && abs(mAllList[0].weight.toDouble() - weight.toDouble()) > 5) {
//                TLog.error("mAllList[0].weight.toDouble()+" + mAllList[0].weight.toDouble())
//
//                TLog.error("===" + weight)
//                //sureDialog(weight)
//                return@OptionsPickerBuilder
//            }
            //setWeight(weight)
//            sureUpdate(weight)
        }
            .setLayoutRes(
                R.layout.pickerview_custom_options_weight
            ) { v ->
                val tvSubmit =
                    v.findViewById<TextView>(R.id.tv_finish)
                val ivCancel =
                    v.findViewById<TextView>(R.id.iv_cancel)
                tvSubmit.setOnClickListener {
                    pvCustomOptions?.returnData()
                    pvCustomOptions?.dismiss()
                }
                ivCancel.setOnClickListener { pvCustomOptions?.dismiss() }
            }
            .isDialog(true)
            .setCyclic(cardWeightItem.size > 2, false, false)
            .isRestoreItem(false)
            .setSelectOptions(indexWeight, afterWeight.toInt())
            .setTextColorCenter(resources.getColor(R.color.color_main_green))
            .setOutSideCancelable(false)
            .setContentTextSize(18)
            .build()
        pvCustomOptions?.setPicker(
            cardWeightItem as List<Any>?,
            options2Items as List<MutableList<Any>>?
        )

        val mDialog: Dialog = pvCustomOptions?.dialog!!
        val params =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        params.leftMargin = 0
        params.rightMargin = 0
        pvCustomOptions?.let { it.dialogContainerLayout.layoutParams = params }
        val dialogWindow = mDialog.window
        if (dialogWindow != null) {
            dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim) //修改动画样式
            dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f)
        }

    }

    private fun getCardData() {
        cardSexItem.add(CardBean(0, getString(R.string.man)))
        cardSexItem.add(CardBean(1, getString(R.string.woman)))
        for (i in 50..250) {
            cardHeightItem.add(CardBean(i, i.toString()))
        }

        for (i in 3..255) {
            cardWeightItem.add(CardWeightBean(i, i.toString()))
        }
        for (j in 0..cardWeightItem.size) {
            val cityList = ArrayList<String>()
            for (i in 0..9)
                cityList.add(".$i")
            options2Items.add(cityList)
        }

//                for (i in 3..255) {
//                    cardWeightItem.add(CardBean(i, i.toString()))
//                }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.setting_age -> {
                TLog.error("m==${Gson().toJson(mDeviceInformationBean)}")
                pvTime?.show()
            }
            R.id.imgHead -> {
                PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .maxSelectNum(1)
                    .isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                    .selectionMode(PictureConfig.SINGLE)
                    .isPreviewImage(true)//是否可预览图片
                    .isEnableCrop(true)//是否裁剪
                    .withAspectRatio(1, 1)
                    .circleDimmedLayer(true)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框
                    .showCropGrid(false)// 是否显示裁剪矩形网格
                    .scaleEnabled(false)//是否可缩放
                    .setOutputCameraPath(PictureMimeType.PNG)
                    .imageFormat(PictureMimeType.PNG_Q)
                    .forResult(PictureConfig.CHOOSE_REQUEST)
            }

            R.id.tvNext -> {
                if (register) {
                    if (TextUtils.equals(edtName.text.trim().toString(), "")) {
                        ShowToast.showToastLong("请输入昵称")
                        return
                    }
                    setHeadImg()
                    mDeviceInformationBean.name = edtName.text.toString()
                    mDeviceInformationBean.exerciseSteps = 10000
                    Hawk.put(Config.database.PERSONAL_INFORMATION, mDeviceInformationBean)
                    TLog.error("保存的+${Gson().toJson(mDeviceInformationBean)}")
                    JumpUtil.startGoalActivity(this)
                } else {
                    mViewModel.outLogin(this)
                }
            }
            R.id.tvSignOut -> {
                AllGenJIDialog.signOutDialog(supportFragmentManager, mViewModel, userInfo, this)
            }
            R.id.tvLogout -> {
                JumpUtil.startLogOutActivity(this)
            }
            R.id.setting_sex -> {
                pvSexCustomOptions?.show()
            }
            R.id.setting_height -> {
               pvHeightCustomOptions?.show()
            }
            R.id.setting_weight -> {
               // pvWeightCustomOptions?.show()


                pvCustomOptions?.show()
            }

        }
    }

    override fun DeviceInformationCallResult() {
        //ShowToast.showToastLong(getString(R.string.set_successfully))
//        setting_movement.getContentText()?.toInt()?.let {
//            mainViewModel.setText(it)
//            Hawk.put("step", it)
//        }
    }

    private var pvTime: TimePickerView? = null
    private fun initTimePicker() { //Dialog 模式下，在底部弹出

        pvTime = TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->
                if (date.time > time) {
                    ShowToast.showToastLong("出生日期不可大于今天")
                    return@OnTimeSelectListener
                }
                mDeviceInformationBean.age = DateUtil.getAge(date)
                mDeviceInformationBean.birth = DateUtil.convertDateToLong(date)
                setting_age.setContentText(DateUtil.getDate(DateUtil.YYYY_MM_DD, date))
            })
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .isCyclic(true)
            .setDate(DateUtil.getCurrentCalendar(mDeviceInformationBean.birth))
            .build()
        val mDialog: Dialog = pvTime?.dialog!!
        val params =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        params.leftMargin = 0
        params.rightMargin = 0
        pvTime?.let { it.dialogContainerLayout.layoutParams = params }
        val dialogWindow = mDialog.window
        if (dialogWindow != null) {
            dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim) //修改动画样式
            dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
            dialogWindow.setDimAmount(0.3f)
        }
    }

    /**
     * 修改头像
     */
    fun setHeadImg() {
        if (!imgCheck)
            return

        var file = File(mImagePaths)//图片完整路径
        ThreadUtils.submit {
            val requestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val part =
                MultipartBody.Part.createFormData("file", file.name, requestBody)
            mViewModel.setImg(part)
        }
    }

    var imgCheck = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    ImgUtil.loadHead(imgHead, selectList[0].cutPath.toString())
                    mImagePaths = selectList[0].cutPath.toString()
                    //     Hawk.put(Config.database.IMG_HEAD, mImagePaths?.get(0))
                    imgCheck = true


                }
            }
        }
    }

    private fun updateHomeWeight() {
        if (weightStatus) {
            if (mHomeCardBean.list != null && mHomeCardBean.list.size > 0) {
                var cardList = mHomeCardBean.list
                cardList.forEachIndexed { index, addCardDTO ->
                    if (addCardDTO.type == 7) {
                        mHomeCardBean.list[index].endTime =
                            System.currentTimeMillis() / 1000
                        mHomeCardBean.list[index].data =
                            mDeviceInformationBean.weight.toString()
                        Hawk.put(Config.database.HOME_CARD_BEAN, mHomeCardBean)
                        SNEventBus.sendEvent(Config.eventBus.BLOOD_PRESSURE_RECORD)
                    }
                }
            }
        }
    }



}