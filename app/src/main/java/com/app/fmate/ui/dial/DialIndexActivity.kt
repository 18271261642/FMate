package com.app.fmate.ui.dial

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.app.fmate.Config
import com.app.fmate.R
import com.app.fmate.adapter.MeDialImgAdapter
import com.app.fmate.base.BaseActivity
import com.app.fmate.bean.FlashBean
import com.app.fmate.eventbus.SNEvent
import com.app.fmate.eventbus.SNEventBus
import com.app.fmate.network.api.dialView.RecommendDialBean
import com.app.fmate.network.api.dialView.RecommendDialViewModel
import com.app.fmate.utils.AppActivityManager
import com.app.fmate.utils.JumpUtil
import com.shon.connector.utils.ShowToast
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_dial_index.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 最新表盘,表盘市场
 */
class DialIndexActivity : BaseActivity<RecommendDialViewModel>() {
    override fun layoutId() = R.layout.activity_dial_index
    lateinit var mMeDialImgAdapter: MeDialImgAdapter
    private lateinit var mList: MutableList<RecommendDialBean.ListDTO.TypeListDTO>
    var type = 0
    var productNumber = ""

    //是否在同步表盘中
    var isSyncDial = false

    override fun initView(savedInstanceState: Bundle?) {
        SNEventBus.register(this)
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        type = intent.getIntExtra("type", 1)
        productNumber = intent.getStringExtra("productNumber").toString()
       var  typeName= intent.getStringExtra("typeName").toString()
        TLog.error("typeName++"+typeName)
        titleBar.setTitleText(if(typeName=="最新")resources.getString(R.string.string_dial_last) else typeName)
        var hashMap = HashMap<String, String>()
        hashMap["productNumber"] = productNumber
        hashMap["type"] = type.toString()
        mViewModel.findDialImg(hashMap)
        mList = ArrayList()
        ryLocal.layoutManager = GridLayoutManager(this, 3)
        mMeDialImgAdapter = MeDialImgAdapter(mList, 0)
        ryLocal.adapter = mMeDialImgAdapter
        mMeDialImgAdapter.addChildClickViewIds(R.id.tvInstall, R.id.imgDial)
        mMeDialImgAdapter.setOnItemChildClickListener { adapter, view, position ->
            var bean: RecommendDialBean.ListDTO.TypeListDTO =
                adapter.data[position] as RecommendDialBean.ListDTO.TypeListDTO
            when (view.id) {
                R.id.tvInstall -> {
                    if (bean.isCurrent) {
                        ShowToast.showToastLong("已是当前表盘")
                        return@setOnItemChildClickListener
                    }
                    JumpUtil.startDialDetailsActivity(
                        this,
                        Gson().toJson(adapter.data[position]), position
                    )
                }
                R.id.imgDial -> {
                    JumpUtil.startDialDetailsActivity(
                        this,
                        Gson().toJson(adapter.data[position])
                    ,position)

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
            TLog.error("数据++" + Gson().toJson(it))
            if (!mList.isNullOrEmpty() || mList.size >= 0)
                mList.clear()
            mList.addAll(it.list[0].typeList)
            mMeDialImgAdapter.notifyDataSetChanged()
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: SNEvent<Any>) {
        when (event.code) {
            Config.eventBus.DEVICE_DISCONNECT -> {
                DialMarketActivity.downStatus = false
                finish()
            }

            Config.eventBus.DIAL_RECOMMEND_DIAL -> {
                TLog.error("数据返回了")
                var hashMap = HashMap<String, String>()
                hashMap["productNumber"] = productNumber
                hashMap["type"] = type.toString()
                mViewModel.findDialImg(hashMap)
            }
            Config.eventBus.DIAL_IMG_RECOMMEND_INDEX->
            {
                var data = event.data as FlashBean


                Log.e("111","-----最新市场更新状态="+data.toString())

                if(data.currentProgress==-1&&data.maxProgress==-1)
                {
                    ShowToast.showToastLong("下载错误,请重新下载")
                    AppActivityManager.getInstance().finishActivity(DialIndexActivity::class.java)
                    AppActivityManager.getInstance().finishActivity(DialMarketActivity::class.java)
                    return
                }

                if(data.currentProgress == -1 && data.maxProgress == -2){
                    mList.forEachIndexed { index, typeListDTO ->
                        if(typeListDTO.dialId==data.id) {

                            typeListDTO.state = resources.getString(R.string.string_dial_install)

                            mMeDialImgAdapter.notifyItemChanged(index,typeListDTO)
                        }
                    }
                    return
                }

                mList.forEachIndexed { index, typeListDTO ->
                    if(typeListDTO.dialId==data.id) {
                        typeListDTO.progress
                        var currentProcess =
                            (data.currentProgress.toDouble() / data.maxProgress * 100).toInt()
                        typeListDTO.progress=currentProcess.toString()


                        mMeDialImgAdapter.notifyItemChanged(index,typeListDTO)
                    }
                }


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SNEventBus.unregister(this)
    }
}