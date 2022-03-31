package com.example.xingliansdk.ui.dial

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.xingliansdk.Config
import com.example.xingliansdk.R
import com.example.xingliansdk.adapter.MeDialImgAdapter
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.bean.FlashBean
import com.example.xingliansdk.eventbus.SNEvent
import com.example.xingliansdk.eventbus.SNEventBus
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.network.api.dialView.RecommendDialViewModel
import com.example.xingliansdk.ui.setting.account.FindPhoneMainActivity
import com.example.xingliansdk.ui.setting.account.PasswordCheckActivity
import com.example.xingliansdk.utils.AppActivityManager
import com.example.xingliansdk.utils.JumpUtil
import com.example.xingliansdk.utils.ShowToast
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_dial_index.*
import me.hgj.jetpackmvvm.util.get
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 最新表盘
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
        titleBar.setTitleText(typeName)
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
                if(data.currentProgress==-1&&data.maxProgress==-1)
                {
                    ShowToast.showToastLong("下载错误,请重新下载")
                    AppActivityManager.getInstance().finishActivity(DialIndexActivity::class.java)
                    AppActivityManager.getInstance().finishActivity(DialMarketActivity::class.java)
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