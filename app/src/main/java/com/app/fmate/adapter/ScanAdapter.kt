package com.app.fmate.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.app.fmate.R
import no.nordicsemi.android.support.v18.scanner.ScanResult

class ScanAdapter(data:MutableList<ScanResult>):BaseQuickAdapter<ScanResult,BaseViewHolder>(R.layout.iteam_scan) {
    override fun convert(helper: BaseViewHolder, item: ScanResult) {
        if (item==null)
        {
            return
        }
        helper.setText(R.id.tv_name,item.device.name)
        helper.setText(R.id.tv_mac,item.device.address)
//        helper.setText(R.id.tv_rssi,item.rssi.toString())
        helper.setImageResource(R.id.Imgrssi,showSignalIcon(item.rssi))
    }

    /**
     * 根据信号强度显示对应的图标
     *
     * @param mRssi 信号强度
     * @return resourceId
     */
    private fun showSignalIcon(mRssi: Int): Int {
        return when {
            mRssi < -85 -> {
                R.mipmap.icon_signal_level_1
            }
            mRssi < -75 -> {
                R.mipmap.icon_signal_level_2
            }
            mRssi < -65-> {
                R.mipmap.icon_signal_level_3
            }
            mRssi < -40-> {
                R.mipmap.icon_signal_level_4
            }
            else -> {
                R.mipmap.icon_signal_level_1
            }
        }
    }
}