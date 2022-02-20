package com.example.xingliansdk.ui.problemsFeedback

import android.os.Bundle
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.example.xingliansdk.network.api.helpView.HelpViewModel
import com.example.xingliansdk.utils.AppUtils
import com.example.xingliansdk.utils.HelpUtil
import com.example.xingliansdk.utils.ShowToast
import com.example.xingliansdk.utils.ThreadUtils
import com.gyf.barlibrary.ImmersionBar
import com.shon.connector.utils.TLog
import kotlinx.android.synthetic.main.activity_problems_feedback.*

/**
 * 问题与反馈
 */
class ProblemsFeedbackActivity : BaseActivity<HelpViewModel>(),View.OnClickListener {
    override fun layoutId()=R.layout.activity_problems_feedback
    var type =1
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvFeedback.setOnClickListener(this)
        tvSuggestion.setOnClickListener(this)
        tvSure.setOnClickListener(this)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.resultHelp.observe(this)
        {
            ShowToast.showToastLong("谢谢您的宝贵意见,我们将持续更新优化")
//            ThreadUtils.runOnUiThread({
                finish()
//            }, 3000)
        }
    }
    override fun onClick(v: View) {
         when(v.id)
         {
            R.id.tvFeedback->{
                type=0
                tvSuggestion.setBackgroundResource(R.drawable.bg_text_problems_false)
                tvSuggestion.setTextColor(resources.getColor(R.color.sub_text_color))
                tvFeedback.setBackgroundResource(R.drawable.bg_text_problems_true)
                tvFeedback.setTextColor(resources.getColor(R.color.white))
            }
             R.id.tvSuggestion->{
                 type=1
                 tvFeedback.setBackgroundResource(R.drawable.bg_text_problems_false)
                 tvFeedback.setTextColor(resources.getColor(R.color.sub_text_color))
                 tvSuggestion.setBackgroundResource(R.drawable.bg_text_problems_true)
                 tvSuggestion.setTextColor(resources.getColor(R.color.white))
             }
             R.id.tvSure->{
                 if(edtContent.toString().isNullOrEmpty())
                 {
                     ShowToast.showToastLong("请输入反馈意见")
                     return
                 }
                 if(edtPhone.toString().isNullOrEmpty())
                 {
                     ShowToast.showToastLong("请输入联系手机号")
                     return
                 }
               var hashMap= HashMap<String,Any>()
                 hashMap["content"]=edtContent.text.toString()
                 hashMap["phone"]=edtPhone.text.toString()
                 hashMap["type"]=type
                 hashMap["osType"]="1"
                 hashMap["osVersion"]= AppUtils.getVersionName(this)
                 if(mDeviceFirmwareBean!=null&&mDeviceFirmwareBean.productNumber!=null)
                 hashMap["productNumber"]=mDeviceFirmwareBean.productNumber
                mViewModel.saveFeedback(hashMap)
             }
         }
    }
}