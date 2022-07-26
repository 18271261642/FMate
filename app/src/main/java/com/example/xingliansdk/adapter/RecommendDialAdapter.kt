package com.example.xingliansdk.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.mapcore.util.it
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.xingliansdk.R
import com.example.xingliansdk.bean.FlashBean
import com.example.xingliansdk.network.api.dialView.RecommendDialBean
import com.example.xingliansdk.utils.ExcelUtil
import com.google.gson.Gson
import com.shon.connector.utils.TLog

class RecommendDialAdapter(data: MutableList<RecommendDialBean.ListDTO>) :
    BaseQuickAdapter<RecommendDialBean.ListDTO, BaseViewHolder>(
        R.layout.item_dial_classify,
        data
    ) {
    var listener: OnItemChildClickListener? = null

    private val adapterList: HashMap<String, MeDialImgAdapter> by lazy {
        HashMap()
    }

    private fun changeLanguage(str : String) : String{
        if(str == "最新")
            return context.resources.getString(R.string.string_dial_last)
        if(str == "安装")
            return context.resources.getString(R.string.string_dial_install)
        if(str == "当前表盘")
            return context.resources.getString(R.string.string_dial_current)
        if(str == "下载")
            return context.resources.getString(R.string.string_dial_download)
        if(str == "自定义")
            return context.resources.getString(R.string.string_custom)
        return context.resources.getString(R.string.string_dial_last)
    }

    @SuppressLint("MissingPermission")
    override fun convert(helper: BaseViewHolder, item: RecommendDialBean.ListDTO?) {
        if (item == null) {
            return
        }
        val ryImg = helper.getView<RecyclerView>(R.id.ryImg)
        var tvName = helper.getView<TextView>(R.id.tvName)
//       var tvInstall=helper.getView<TextView>(R.id.tvInstall)
        tvName.text = changeLanguage(item.typeName)
//        ryImg.setHasFixedSize(true)
        //       TLog.error("更新=="+item.typeName)
//        if (ryImg.adapter == null) {
        val key = "${item.type}"
        TLog.error("key==" + key)
        val nestAdapter = MeDialImgAdapter(item.typeList, 0)

        adapterList[key] = nestAdapter
        //add 之前，判断是否存在此类型
//        adapterList.add(nestAdapter)
        //   nestAdapter = MeDialImgAdapter(item.typeList,0)
        nestAdapter.setOnItemChildClickListener(listener)
        ryImg.layoutManager
        ryImg.adapter = nestAdapter
//        }
//        else
//        {
//            TLog.error("更新==")
//            nestAdapter.data.clear()
//            nestAdapter.addData(item.typeList)
//            nestAdapter.notifyDataSetChanged()
//        }
    }

    fun updateProgress(bean: FlashBean) {
        data.forEach { listDTO ->
            val meDialImgAdapter: MeDialImgAdapter = adapterList["" + 1]!!
            listDTO.typeList.forEachIndexed { innerIndex, typeListDTO ->
                if (typeListDTO.dialId == bean.id) {
                    if(bean.maxProgress == -2){ //强制退出或断开连接
                        typeListDTO.state = context.resources.getString(R.string.string_dial_install)
                        listDTO.typeList[innerIndex] = typeListDTO
                        meDialImgAdapter?.notifyItemChanged(innerIndex, typeListDTO)
                        return
                    }

                    var currentProcess =
                        (bean.currentProgress.toDouble() / bean.maxProgress * 100).toInt()
                    typeListDTO.progress = "" + currentProcess
                    listDTO.typeList[innerIndex] = typeListDTO
                    meDialImgAdapter?.notifyItemChanged(innerIndex, typeListDTO)
                }
            }


        }
    }
}