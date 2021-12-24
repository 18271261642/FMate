package com.example.xingliansdk.ui.dial

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
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
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.DownDialModel
import com.example.xingliansdk.network.api.dialView.MeDialViewModel
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.ShowToast
import com.google.gson.Gson
import com.ly.genjidialog.extensions.convertListenerFun
import com.ly.genjidialog.extensions.newGenjiDialog
import com.orhanobut.hawk.Hawk
import com.shon.connector.BleWrite
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.fragment_me_dial.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MeDialFragment : BaseFragment<MeDialViewModel>(), View.OnClickListener,
    BleWrite.DialDesignatedInterface {
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
    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        imgDownload.setOnClickListener(this)
        imgCustomize.setOnClickListener(this)
        imgLocal.setOnClickListener(this)
        bean = Hawk.get("DeviceFirmwareBean", DeviceFirmwareBean())
        sDao = AppDataBase.instance.getCustomizeDialDao()
        dialRequest()
        dialInit()
    }

    private fun dialRequest() {
        var hashMap = HashMap<String, String>()
        hashMap["type"] = "0"
        hashMap["productNumber"] = bean.productNumber
        mViewModel.findDialImg(hashMap)
        mViewModel.findMyDial()
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
            when (view.id) {
                R.id.tvInstall -> {
                    if (mList[position].isCurrent)
                        return@setOnItemChildClickListener
                    var id = if (mList[position].dialId == 0)
                        65533
                    else
                        mList[position].dialId.toLong()
                    BleWrite.writeDialDesignatedCall(id, this)
                    hasMapMeUpdate = HashMap()
                    hasMapMeUpdate["dialId"] = mList[position].dialId.toString()
                    hasMapMeUpdate["stateCode"] = mList[position].stateCode.toString()
                }
            }
        }

        mDownList = ArrayList()
        ryDownload.layoutManager = GridLayoutManager(activity, 3)
        downAdapter = DownloadDialImgAdapter(mDownList)
        ryDownload.adapter = downAdapter
        downAdapter.addChildClickViewIds(R.id.imgDelete, R.id.imgDial)
        downAdapter.setOnItemChildClickListener { adapter, view, position ->
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
        customDialImgAdapter.addChildClickViewIds(R.id.tvInstall, R.id.imgDelete, R.id.imgDial)
        customDialImgAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.imgDial,
                R.id.tvInstall -> {
                    if (DialMarketActivity.downStatus) {
                        ShowToast.showToastLong("有表盘正在安装,请安装完成再次点击")
                        return@setOnItemChildClickListener
                    }
                    JumpUtil.startCustomizeDialActivity(
                        activity,
                        Gson().toJson(adapter.data[position])
                    )
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

    override fun createObserver() {
        super.createObserver()
        mViewModel.msg.observe(this)
        {
            TLog.error("msg++$it")
        }
        mViewModel.result1.observe(this)
        {
            TLog.error("数据++" + Gson().toJson(it))
            mList.clear()
            if (it == null || it.list == null || it.list.size <= 0)
                return@observe
            if (it.list[0].type == 0) {
                //  longCustOnclick
                //  longOnclick
                mList.addAll(it.list[0].typeList)

                meDialImgAdapter.notifyDataSetChanged()
            } else if (it.list[0].type == 1001) {
                mList.addAll(it.list[0].typeList)
                meDialImgAdapter.notifyDataSetChanged()
            }
        }
        mViewModel.result.observe(this)
        {
            mDownList.clear()
            TLog.error("result it==" + Gson().toJson(it))
            if (it == null || it.list == null || it.list.size <= 0)
                return@observe
            mDownList.addAll(it.list)
            if (longOnclick) {
                mDownList.forEach { it.delete="1" }
            }
            downAdapter.notifyDataSetChanged()
        }
        mViewModel.resultUpdate.observe(this) {
            TLog.error("resultUpdate 数据++" + Gson().toJson(it))
            SNEventBus.sendEvent(Config.eventBus.DIAL_RECOMMEND_DIAL)
            dialRequest()
        }
        mViewModel.resultDeleteMyDial.observe(this) {
            if (it == null)
                return@observe
            SNEventBus.sendEvent(Config.eventBus.DIAL_RECOMMEND_DIAL)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.DIAL_RECOMMEND_DIAL,
            Config.eventBus.DEVICE_DIAL_ID-> {
                TLog.error("DIAL_RECOMMEND_DIAL")
                dialRequest()
            }
            Config.eventBus.DIAL_CUSTOMIZE -> {
                TLog.error("DIAL_CUSTOMIZE")
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
                TLog.error("DIAL_IMG_RECOMMEND_INDEX")
                var data = event.data as FlashBean
                mDownList.forEachIndexed { index, typeListDTO ->
                    if (typeListDTO.dialId == data.id) {
                        typeListDTO.progress
                        var currentProcess =
                            (data.currentProgress.toDouble() / data.maxProgress * 100).toInt()
                        typeListDTO.progress = currentProcess.toString()
                        downAdapter.notifyItemChanged(index, typeListDTO)
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