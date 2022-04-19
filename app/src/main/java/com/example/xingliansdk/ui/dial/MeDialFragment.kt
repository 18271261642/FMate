package com.example.xingliansdk.ui.dial

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.xingliansdk.Config
import com.example.xingliansdk.Config.eventBus.DEVICE_BLE_OFF
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.CustomDialImgAdapter
import com.example.xingliansdk.adapter.DownloadDialImgAdapter
import com.example.xingliansdk.adapter.MeDialImgAdapter
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.bean.DeviceFirmwareBean
import com.example.xingliansdk.bean.FlashBean
import com.example.xingliansdk.bean.room.AppDataBase
import com.example.xingliansdk.bean.room.CustomizeDialBean
import com.example.xingliansdk.bean.room.CustomizeDialDao
import com.example.xingliansdk.blecontent.BleConnection
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.DownDialModel
import com.example.xingliansdk.network.api.dialView.MeDialViewModel
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.dialView.RecommendDialViewApi
import com.example.xingliansdk.ui.setting.flash.FlashCall
import com.example.xingliansdk.utils.*
import com.google.gson.Gson
import com.luck.picture.lib.tools.ToastUtils
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.bean.DialCustomBean
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_take_medicine_repeat.*
import kotlinx.android.synthetic.main.fragment_me_dial.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MeDialFragment : BaseFragment<MeDialViewModel>(), View.OnClickListener,
    BleWrite.DialDesignatedInterface {


    private val tags = "MeDialFragment"

    override fun layoutId() = R.layout.fragment_me_dial
    private lateinit var mList: MutableList<RecommendDialBean.ListDTO.TypeListDTO>
    private lateinit var mDownList: MutableList<DownDialModel.ListDTO>
    private lateinit var customDialList: MutableList<CustomizeDialBean>
    private lateinit var meDialImgAdapter: MeDialImgAdapter //本地表盘
    lateinit var customDialImgAdapter: CustomDialImgAdapter  //自定义
    lateinit var downAdapter: DownloadDialImgAdapter  // 下载表盘
    lateinit var sDao: CustomizeDialDao
    var CUSTOM_DELETE_TYPE = 0
    var DOWN_DELETE_TYPE = 1
    var bean = Hawk.get("DeviceFirmwareBean", DeviceFirmwareBean())
    var longOnclick = false
    var longCustOnclick = false

    //是否在同步表盘中
    var isSyncDial = false


    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        imgDownload.setOnClickListener(this)
        imgCustomize.setOnClickListener(this)
        imgLocal.setOnClickListener(this)
        bean = Hawk.get("DeviceFirmwareBean", DeviceFirmwareBean())
        sDao = AppDataBase.instance.getCustomizeDialDao()
       // dialRequest()
        dialInit()
    }

    private fun dialRequest() {
        var hashMap = HashMap<String, String>()
        hashMap["type"] = "0"
        hashMap["productNumber"] = bean.productNumber
        mViewModel.findDialImg(hashMap)
        mViewModel.findMyDial()
    }

    override fun onResume() {
        super.onResume()
        Log.e(tags,"---------onResume----------")
        dialRequest()

        getCusDial()
    }

    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }

    var hasMapMeUpdate = HashMap<String, String>()
    private fun dialInit() {
        mList = ArrayList()
        ryLocal.layoutManager = GridLayoutManager(activity, 3)
        meDialImgAdapter = MeDialImgAdapter(mList, 2)
        ryLocal.adapter = meDialImgAdapter
        meDialImgAdapter.addChildClickViewIds(R.id.tvInstall)
        meDialImgAdapter.setOnItemChildClickListener { adapter, view, position ->

            Log.e("点击本地表盘","------本地表盘点击="+Gson().toJson(mList[position]))

            when (view.id) {
                R.id.tvInstall -> {
                    if (mList[position].isCurrent)
                        return@setOnItemChildClickListener
                    hasMapMeUpdate.clear()
                    var id = if (mList[position].dialId == 0)
                        65533
                    else
                        mList[position].dialId.toLong()

                    Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,id.toInt())

                    BleWrite.writeDialDesignatedCall(id, this)
//                    if(mList[position].dialId == 65535 || mList[position].dialId == 0){
//                        return@setOnItemChildClickListener
//                    }

                    hasMapMeUpdate = HashMap()
                    hasMapMeUpdate["dialId"] = mList[position].dialId.toString()
                    //hasMapMeUpdate["stateCode"] = if(mList[position].dialId == 0) "3" else mList[position].stateCode.toString()
                    hasMapMeUpdate["stateCode"] = if(mList[position].dialId == 0) "3" else mList[position].stateCode.toString()

                    isSyncDial = true

                }
            }
        }

        mDownList = ArrayList()
        ryDownload.layoutManager = GridLayoutManager(activity, 3)
        downAdapter = DownloadDialImgAdapter(mDownList)
        ryDownload.adapter = downAdapter
        downAdapter.animationEnable = false
        downAdapter.addChildClickViewIds(R.id.imgDelete, R.id.imgDial)
        downAdapter.setOnItemChildClickListener { adapter, view, position ->


            Log.e("已下载表盘点击","------点击已下载表盘="+Gson().toJson(adapter.data[position]))

            when (view.id) {
                R.id.imgDelete -> {

                    dialog(position, DOWN_DELETE_TYPE)
                }
                R.id.imgDial -> {

                    JumpUtil.startDialDetailsActivity(
                        activity,
                        Gson().toJson(adapter.data[position]),
                        position
                    )
                }
            }
        }
        downAdapter.addChildLongClickViewIds(R.id.imgDial)
        downAdapter.setOnItemChildLongClickListener { adapter, view, position ->
            when (view.id) {
                R.id.imgDial -> {
                    TLog.error("长按过了")
                    if (!longOnclick) {
                        longOnclick = true
                        imgDownload.setImageResource(R.mipmap.icon_dial_delete)
                        imgDownload.rotation = 0f
                    }
                    mDownList.forEach {
                        it.delete = "1"
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            true
        }
        customDialList = if (sDao.getAllCustomizeDialList()
                .isNullOrEmpty() || sDao.getAllCustomizeDialList().size <= 0
        )
            ArrayList()
        else
            sDao.getAllCustomizeDialList()
        TLog.error("customDialList++" + Gson().toJson(customDialList))
        ryCustomize.layoutManager = GridLayoutManager(activity, 3)
        customDialImgAdapter = CustomDialImgAdapter(customDialList)
        ryCustomize.adapter = customDialImgAdapter
        customDialImgAdapter.addChildClickViewIds(R.id.itemDownload, R.id.imgDelete, R.id.imgDial)
        customDialImgAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.imgDial->{ //图片点击
                    if (DialMarketActivity.downStatus) {
                        ShowToast.showToastLong("有表盘正在安装,请安装完成再次点击")
                        return@setOnItemChildClickListener
                    }
                    JumpUtil.startCustomizeDialActivity(
                        activity,
                        Gson().toJson(adapter.data[position])
                    )
                }
                R.id.itemDownload -> {


                    if (DialMarketActivity.downStatus) {
                        ShowToast.showToastLong("有表盘正在安装,请安装完成再次点击")
                        return@setOnItemChildClickListener
                    }

                    showWaitDialog("更换表盘中,请稍后...")
                    val cusDialBean = adapter.data[position] as CustomizeDialBean

                    TLog.error("-------点击自定义表盘="+Gson().toJson(cusDialBean))

                    var uiFeature = 65533
                    var grbByte = byteArrayOf()
                    if (cusDialBean.imgPath.isNullOrEmpty()) {
                        uiFeature = 65534
                        // ShowToast.showToastLong("请选择自定义背景图案")
                        //  return
                    } else {
                        uiFeature = 65533
                        ThreadUtils.submit {
                            var bitmap = Glide.with(this)
                                .asBitmap()
                                .load(cusDialBean.imgPath)
                                .into(
                                    Target.SIZE_ORIGINAL,
                                    Target.SIZE_ORIGINAL
                                ).get()
                            grbByte = BitmapAndRgbByteUtil.bitmap2RGBData(bitmap)
                            TLog.error("grbByte==${grbByte.size}")
                            //   ImgUtil.loadMeImgDialCircle(imgRecall, bitmap)
                        }
                    }
                    //生成新图并保存
//                    var newBit = BitmapAndRgbByteUtil.loadBitmapFromView(img)
//                    var path = FileUtils.saveBitmapToSDCard(newBit, (cusDialBean.date / 1000).toString())
//                    cusDialBean.value = path

                    val dialBean = DialCustomBean(1,
                        uiFeature,
                        grbByte.size,
                        cusDialBean.color,
                        cusDialBean.functionType,
                        cusDialBean.locationType)
                    BleWrite.writeDialWriteAssignCall(dialBean,object : BleWrite.DialWriteInterface{
                        override fun onResultDialWrite(key: Int) {
                             isSyncDial = true
                            TLog.error("---------自定义表盘it==" + key)
                            when (key) {

                                1 -> {
                                    hideWaitDialog()
                                    ShowToast.showToastLong("传入非法值")
                                     isSyncDial = false
                                }
                                2 -> {
                                    val startByte = byteArrayOf(
                                        0x00, 0xff.toByte(), 0xff.toByte(),
                                        0xff.toByte()
                                    )
                                    TLog.error("mCustomizeDialBean.imgPath+=" + cusDialBean.imgPath)
                                    BleWrite.writeFlashErasureAssignCall(
                                        16777215, 16777215
                                    ) { key ->

                                        if (key == 2) {
                                            //isSyncDial = true
                                            TLog.error("开始擦写++" + grbByte.size)
                                            FlashCall().writeFlashCall(
                                                startByte, startByte, grbByte,
                                                Config.eventBus.DIAL_CUSTOMIZE, -100, 0
                                            )
                                        } else{
                                            hideWaitDialog()
                                            ShowToast.showToastLong("不支持擦写FLASH数据")
                                            isSyncDial = false;
                                        }

                                    }

                                }
                                3 -> {
                                      hideWaitDialog()
                                    var hasMap = java.util.HashMap<String, String>()
                                    hasMap["dialId"] = "0"
                                     mViewModel.updateUserDial(hasMap)
                                    Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,0)
                                    Hawk.put(com.shon.connector.Config.SAVE_LOCAL_CUS_DIAL_URL,if(TextUtils.isEmpty(cusDialBean.imgPath) )cusDialBean.value else cusDialBean.value)
                                    // finish()
                                    // ShowToast.showToastLong("设备已经有存储这个表盘")
                                    //给后台一个 更改表盘的指令
                                    isSyncDial = false
                                }
                                4 -> {
                                     hideWaitDialog()
                                    ShowToast.showToastLong("设备已经有存储这个表盘")
                                     isSyncDial = false
                                }
                            }
                        }

                    })





//                    JumpUtil.startCustomizeDialActivity(
//                        activity,
//                        Gson().toJson(adapter.data[position])
//                    )
                }
                R.id.imgDelete -> {

                    dialog(position, CUSTOM_DELETE_TYPE)
                }
            }
        }
        customDialImgAdapter.addChildLongClickViewIds(R.id.imgDial)
        customDialImgAdapter.setOnItemChildLongClickListener { adapter, view, position ->
            when (view.id) {
                R.id.imgDial -> {
                    if (!longCustOnclick) {
                        longCustOnclick = true
                        imgCustomize.setImageResource(R.mipmap.icon_dial_delete)
                        imgCustomize.rotation = 0f
                    }
                    //   customDialImgAdapter.updateDelete(0)
                    customDialList.forEach {
                        it.setxAxis("1")
                    }
                    adapter.notifyDataSetChanged()
                }

            }
            true
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgDownload -> {
                when {
                    longOnclick -> {
                        mDownList.forEach {
                            it.delete = "0"
                        }
                        downAdapter.notifyDataSetChanged()
                        longOnclick = false
                        imgDownload.setImageResource(R.mipmap.right_back)
                        imgDownload.rotation = 270f
                    }
                    ryDownload.visibility == View.VISIBLE -> {
                        ryDownload.visibility = View.GONE
                        imgDownload.rotation = 90f
                    }
                    else -> {
                        ryDownload.visibility = View.VISIBLE
                        imgDownload.rotation = 270f
                    }
                }

            }
            R.id.imgCustomize -> {
                when {
                    longCustOnclick -> {
                        customDialList.forEach {
                            it.setxAxis("0")
                        }
                        customDialImgAdapter.notifyDataSetChanged()
                        longCustOnclick = false
                        imgCustomize.setImageResource(R.mipmap.right_back)
                        imgCustomize.rotation = 270f
                    }
                    ryCustomize.visibility == View.VISIBLE -> {
                        ryCustomize.visibility = View.GONE
                        imgCustomize.rotation = 90f
                    }
                    else -> {
                        ryCustomize.visibility = View.VISIBLE
                        imgCustomize.rotation = 270f
                    }
                }
            }
            R.id.imgLocal -> {
                if (ryLocal.visibility == View.VISIBLE) {
                    ryLocal.visibility = View.GONE
                    imgLocal.rotation = 90f
                } else {
                    ryLocal.visibility = View.VISIBLE
                    imgLocal.rotation = 270f
                }
            }
        }
    }



    private fun getCusDial(){
        customDialList = if (sDao.getAllCustomizeDialList()
                .isNullOrEmpty() || sDao.getAllCustomizeDialList().size <= 0
        )
            ArrayList()
        else
            sDao.getAllCustomizeDialList()
        TLog.error("customDialList==" + Gson().toJson(customDialList))
        customDialImgAdapter.data.clear()
        customDialImgAdapter.addData(customDialList)
        customDialImgAdapter.notifyDataSetChanged()
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.msg.observe(this)
        {
            TLog.error("msg++$it")
        }
        mViewModel.result1.observe(this)
        {
            TLog.error("获取本地表盘返回","----------数据++" + Gson().toJson(it))
            mList.clear()
            if (it == null || it.list == null || it.list.size <= 0)
                return@observe
            if (it.list[0].type == 0) {
                var currDialId = Hawk.get(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,-1).toInt()

                //是否有市场表盘
                val marketDialId = Hawk.get(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,-1);
               // Log.e("获取本地的表盘","------type=0本地表盘="+"已经选择的表盘id="+currDialId+"    "+marketDialId+"  "+Gson().toJson(it.list[0]));

                val saveMarketBean = Hawk.get(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,"")
                //Log.e("本地表盘第四张", "---1111--序列化对象=$saveMarketBean")
                if(!TextUtils.isEmpty(saveMarketBean)){
                    val markBean = Gson().fromJson(saveMarketBean,RecommendDialBean.ListDTO.TypeListDTO::class.java)
                   // Log.e("本地表盘第四张","-----序列化对象="+Gson().toJson(saveMarketBean))
                    if(markBean != null){

                        if(marketDialId != -1 && !TextUtils.isEmpty(markBean.name)){
                            markBean.state = "选择"
                            markBean.isCurrent = false
                            if(it.list[0].typeList.size>=5){

                                it.list[0].typeList.removeAt(3)
                            }
                            it.list[0].typeList.add(3,markBean)
                        }

                    }
                }else{
                    if(it.list[0].typeList.size>=5){

                        it.list[0].typeList.removeAt(3)
                    }
                }

                if(marketDialId == -1){ //表盘没有市场表盘了，清除掉本地的市场表盘
                    if(it.list[0].typeList.size == 5)
                    it.list[0].typeList.removeAt(3)
                }

                var localCusUrl = Hawk.get(com.shon.connector.Config.SAVE_LOCAL_CUS_DIAL_URL,"");
               TLog.error("------自定义表盘背景="+localCusUrl)
                if(!TextUtils.isEmpty(localCusUrl)){
                    it.list[0].typeList[it.list[0].typeList.size-1].image = localCusUrl
                }


                //  longCustOnclick
                //  longOnclick
                mList.addAll(it.list[0].typeList)
                mList.forEach {
                    it.state = "选择"
                    it.isCurrent = false
                }

                meDialImgAdapter.notifyDataSetChanged()
            } else if (it.list[0].type == 1001) {
                mList.addAll(it.list[0].typeList)
                meDialImgAdapter.notifyDataSetChanged()
            }
        }
        mViewModel.result.observe(this)
        {
            mDownList.clear()
            TLog.error("获取已下载表盘返回","--------result it==" + Gson().toJson(it))
            if (it == null || it.list == null || it.list.size <= 0){
                //是否有市场表盘
                Hawk.put(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,-1);
                Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,"")
                return@observe
            }


            val currDialId = Hawk.get(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,0)
            it.list.forEach {
                if(currDialId == it.dialId){

                    Hawk.put(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,it.dialId)
                    Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,Gson().toJson(it))

                    it.isCurrent = true
                    it.stateCode = 1
                    it.state = "当前表盘"
                }else{
                    it.isCurrent = false
                    it.stateCode = 6
                    it.state = "安装"
                }
            }

            //只有一个已下载表盘，就保存到已下载的本地表盘中去
            if(it.list.size == 1 && currDialId == it.list[0].dialId){
                Hawk.put(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,it.list[0].dialId)
                Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,Gson().toJson(it.list[0]))
            }


            mDownList.addAll(it.list)
            if (longOnclick) {
                mDownList.forEach { it.delete="1" }
            }
            downAdapter.notifyDataSetChanged()
        }
        mViewModel.resultUpdate.observe(this) {
            TLog.error("更新用户表盘返回","----------resultUpdate 数据++" + Gson().toJson(it))
            SNEventBus.sendEvent(Config.eventBus.DIAL_RECOMMEND_DIAL)
            dialRequest()
        }
        mViewModel.resultDeleteMyDial.observe(this) {
            if (it == null)
                return@observe
            Log.e("删除表盘网络请求返回","------删除表盘返回="+it.toString())
            SNEventBus.sendEvent(Config.eventBus.DIAL_RECOMMEND_DIAL)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.DIAL_RECOMMEND_DIAL,
            Config.eventBus.DEVICE_DIAL_ID-> {
                TLog.error("----------DIAL_RECOMMEND_DIAL")
                dialRequest()
            }
            Config.eventBus.DIAL_CUSTOMIZE -> {

                TLog.error("DIAL_CUSTOMIZE")

                var data = event.data as FlashBean
                hideWaitDialog()
                    TLog.error("---------自定义表盘安装进度="+data.toString())

                    if(data.currentProgress !=1 || data.maxProgress != 1){

                        customDialList.forEachIndexed { index, customizeDialBean ->

                            var currentProcess =
                                (data.currentProgress.toDouble() / data.maxProgress * 100).toFloat()

                            customizeDialBean.setyAxis("$currentProcess")
                            customDialImgAdapter.notifyItemChanged(index,customizeDialBean)

                        }

                        return
                    }


                Hawk.put(com.shon.connector.Config.SAVE_DEVICE_CURRENT_DIAL,0)


                    customDialList = if (sDao.getAllCustomizeDialList()
                            .isNullOrEmpty() || sDao.getAllCustomizeDialList().size <= 0
                    )
                        ArrayList()
                    else
                        sDao.getAllCustomizeDialList()
                    TLog.error("customDialList==" + Gson().toJson(customDialList))



                    if (customDialImgAdapter != null) {
                        customDialImgAdapter.data.clear()
                        customDialImgAdapter.addData(customDialList)
                        customDialImgAdapter.notifyDataSetChanged()
                    }
                    dialRequest()


            }
            Config.eventBus.DIAL_IMG_RECOMMEND_INDEX -> {
                var data = event.data as FlashBean
                TLog.error("-----DIAL_IMG_RECOMMEND_INDEX"+" data="+data.toString())

                //处理断开逻辑
                val isConnDevice = BleConnection.iFonConnectError

                mDownList.forEachIndexed { index, typeListDTO ->

                    Log.e("操作完成更改状态","-------更改状态="+index+" 详情="+Gson().toJson(typeListDTO))

                    if (typeListDTO.dialId == data.id) {

                        if(isConnDevice){
                            typeListDTO.state = "连接已断开"
                            ToastUtils.s(context,"连接已断开")
                            downAdapter.notifyItemChanged(index, typeListDTO)
                            return
                        }


                        var currentProcess =
                            (data.currentProgress.toDouble() / data.maxProgress * 100).toInt()
                        typeListDTO.progress = currentProcess.toString()

                        Log.e(tags,"---------更新状态="+isSyncDial)
                        downAdapter.notifyItemChanged(index, typeListDTO)
//                        if(isSyncDial){
//                            downAdapter.notifyItemChanged(index, typeListDTO)
//                        }

                        //已经下载完了，更新下本地的表盘状态
                        if(data.currentProgress == 1 && data.maxProgress == 1){
                            dialRequest()
                        }

                    }
                }
            }
            DEVICE_BLE_OFF,
            Config.eventBus.DEVICE_DISCONNECT -> {
                activity?.finish()
            }

        }
    }

    override fun onResultDialDesignated(key: Int) {
        if (key == 2) {
            ShowToast.showToastLong("更换成功")
            mViewModel.updateUserDial(hasMapMeUpdate)

           // mViewModel.checkDialSate(Gson().toJson(setList))

        } else
            ShowToast.showToastLong("更换失败")
    }

    private fun dialog(position: Int, type: Int) {
        newGenjiDialog {
            layoutId = R.layout.dialog_delete
            dimAmount = 0.3f
            isFullHorizontal = true
            animStyle = R.style.AlphaEnterExitAnimation
            convertListenerFun { holder, dialog ->
                var dialogCancel = holder.getView<TextView>(R.id.dialog_cancel)
                var dialogSet = holder.getView<TextView>(R.id.dialog_confirm)
                var dialogContent = holder.getView<TextView>(R.id.dialog_content)
                dialogContent?.text = "是否删除该表盘？"
                dialogSet?.setOnClickListener {
                    if (type == DOWN_DELETE_TYPE) {
                        //删除的表盘ID
                        val deleteDialId = mDownList[position].dialId
                        //本地的第四张表盘id
                        val localMarketId = Hawk.get(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,-1).toInt()
                        Log.e("22","-----要删除的表盘ID="+deleteDialId+"-本地第四张表盘ID="+localMarketId)

                        BleWrite.writeDeleteDialCall(deleteDialId.toLong()
                        ) {
                            if(localMarketId == deleteDialId && it == 0x02){
                                //是否有市场表盘
                                Hawk.put(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,-1);
                                //清除当前显示的市场表盘bean
                               Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,"")
                            }

                        }

                        if(localMarketId == deleteDialId){
                            //是否有市场表盘
                            Hawk.put(com.shon.connector.Config.SAVE_DEVICE_INTO_MARKET_DIAL,-1);
                            //清除当前显示的市场表盘bean
                            Hawk.put(com.shon.connector.Config.SAVE_MARKET_BEAN_DIAL,"")
                        }


                        mViewModel.deleteMyDial(mDownList[position].dialId.toString())
                        mDownList.removeAt(position)
                        downAdapter.notifyItemRemoved(position)
                    } else {
                        sDao.deleteID(customDialList[position].id)
                        customDialList.removeAt(position)
                        customDialImgAdapter.remove(position)
                        customDialImgAdapter.notifyItemRemoved(position)
                    }
                    dialog.dismiss()
                }
                dialogCancel?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }.showOnWindow(childFragmentManager)
    }
}