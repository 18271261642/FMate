package com.app.fmate.ui.dial

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.app.fmate.Config
import com.app.fmate.Config.eventBus.DEVICE_BLE_OFF
import com.app.fmate.R
import com.app.fmate.adapter.RecommendDialAdapter
import com.app.fmate.base.fragment.BaseFragment
import com.app.fmate.bean.DeviceFirmwareBean
import com.app.fmate.bean.FlashBean
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.dialView.RecommendDialBean
import com.app.fmate.network.api.dialView.RecommendDialViewModel
import com.app.fmate.ui.dial.DialMarketActivity.Companion.downStatus
import com.app.fmate.utils.JumpUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.shon.bluetooth.Constants
import com.shon.connector.utils.TLog

import kotlinx.android.synthetic.main.fragment_recommend_dial.*
import kotlinx.android.synthetic.main.fragment_recommend_dial.imgDial
import kotlinx.android.synthetic.main.item_dial_img_text.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class RecommendDialFragment : BaseFragment<RecommendDialViewModel>(), View.OnClickListener {

    private val tags = "RecommendDialFragment"


    override fun layoutId() = R.layout.fragment_recommend_dial
    private lateinit var mRecommendDialAdapter: RecommendDialAdapter
    private lateinit var mList: MutableList<RecommendDialBean.ListDTO>
    var bean: DeviceFirmwareBean = Hawk.get("DeviceFirmwareBean", DeviceFirmwareBean())
    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        bean = Hawk.get("DeviceFirmwareBean", DeviceFirmwareBean())
//        TLog.error("bean+" + Gson().toJson(bean))
        tvEdt.setOnClickListener(this)
        imgDial.setOnClickListener(this)

        setAdapter()
    }

    override fun onResume() {
        super.onResume()
        var hashMap = HashMap<String, String>()
        hashMap["productNumber"] = bean.productNumber
        mViewModel.findDialImg(hashMap)
    }


    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }

    private fun setAdapter() {
        mList = ArrayList()
        ryRecommendDial.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        mRecommendDialAdapter = RecommendDialAdapter(mList)
        ryRecommendDial.adapter = mRecommendDialAdapter

        val headerView: View = layoutInflater.inflate(R.layout.item_dial_img_text, null)
        headerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        headerView.imgDialCustomize.setOnClickListener(this)
        headerView.tvCustomize.setOnClickListener(this)
        mRecommendDialAdapter.addFooterView(headerView)
        mRecommendDialAdapter.listener =
            OnItemChildClickListener { adapter, view, position ->
                when (view.id) {

                    R.id.tvInstall -> {
                        TLog.error("??????++" + Gson().toJson(adapter.data[position]))
                        var list = Gson().fromJson(
                            Gson().toJson(adapter.data[position]),
                            RecommendDialBean.ListDTO.TypeListDTO::class.java
                        )
                        TLog.error("list++" + Gson().toJson(list))
                        if (list.isCurrent)
                            return@OnItemChildClickListener
                        JumpUtil.startDialDetailsActivity(
                            activity, Gson().toJson(adapter.data[position]), position
                        )
                    }

                    R.id.imgDial -> {
                        TLog.error("???????????? imgDial")
                        JumpUtil.startDialDetailsActivity(
                            activity,
                            Gson().toJson(adapter.data[position]), position
                        )

                    }

                }
            }
        //??????????????????
        mRecommendDialAdapter.addChildClickViewIds(R.id.tvMore)
        mRecommendDialAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tvMore -> {
                    JumpUtil.startDialIndexActivity(
                        activity,
                        mList[position].type,
                        bean.productNumber,
                        mList[position].typeName
                    )
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
        mViewModel.result.observe(this)
        {
            TLog.error("??????++" + Gson().toJson(it))
            if (!mList.isNullOrEmpty() || mList.size >= 0)
                mList.clear()
            //  mRecommendDialAdapter.data.clear()


            mList.addAll(it.list)
            TLog.error("===??????++" + mList.size)
            //  mRecommendDialAdapter.addData(mList)
            mRecommendDialAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(v: View) {   //???????????????
        when (v.id) {
            R.id.tvCustomize,
            R.id.imgDialCustomize -> {
                JumpUtil.startCustomizeDialActivity(activity)
            }
            R.id.imgDial,
            R.id.tvEdt -> {
                JumpUtil.startCustomizeDialActivity(activity)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.DIAL_CUSTOMIZE,
            Config.eventBus.DEVICE_DIAL_ID,
            Config.eventBus.DIAL_RECOMMEND_DIAL -> {
                TLog.error("???????????????")
//                if(event.data==null)
//                    return
//                var data = event.data as Int
//                TLog.error("data==" + data)
//                if (data == null || data < 0) {
                var hashMap = HashMap<String, String>()
                hashMap["productNumber"] = bean.productNumber
                mViewModel.findDialImg(hashMap)
//                }

            }
            Config.eventBus.DIAL_IMG_RECOMMEND_INDEX -> {

                val data = event.data as FlashBean
//                TLog.error("data==" + data.toString())
//                data.id
                downStatus = true //????????????
                if (data.currentProgress == -1 && data.maxProgress == -1) {
                    downStatus = false
                    ShowToast.showToastLong("????????????,???????????????")
                    activity?.finish()
                    return
                }

                if(data.currentProgress == -1 && data.maxProgress == -2){
                   data.maxProgress = -2
                    if(Constants.isDialSync){
                        downStatus = false
                        mRecommendDialAdapter.updateProgress(data)
                    }
                    return
                }


                if (data.currentProgress == 1 && data.maxProgress == 1)//??????????????????
                {
                    downStatus = false
                }
                if(Constants.isDialSync){
                    mRecommendDialAdapter.updateProgress(data)
                }
//                TLog.error("downStatus++"+downStatus)

            }
            DEVICE_BLE_OFF,
            Config.eventBus.DEVICE_DISCONNECT -> {
                downStatus = false
                activity?.finish()
            }

        }
    }
}