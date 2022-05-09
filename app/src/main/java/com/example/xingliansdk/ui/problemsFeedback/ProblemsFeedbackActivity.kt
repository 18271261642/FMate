package com.example.xingliansdk.ui.problemsFeedback

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.network.api.helpView.HelpViewModel
import com.example.xingliansdk.pictureselector.GlideEngine
import com.example.xingliansdk.utils.*
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.shon.connector.utils.ShowToast
import kotlinx.android.synthetic.main.activity_device_information.*
import kotlinx.android.synthetic.main.activity_problems_feedback.*
import kotlinx.android.synthetic.main.activity_problems_feedback.titleBar

/**
 * 问题与反馈
 */
class ProblemsFeedbackActivity : BaseActivity<HelpViewModel>(),View.OnClickListener {

    var feedAddImgAdapter : FeedAddImgAdapter ?= null
    val addList = arrayListOf<String>()

    private var localAddImgUrl :String ?= null


    override fun layoutId()=R.layout.activity_problems_feedback
    var type =1
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvFeedback.setOnClickListener(this)
        tvSuggestion.setOnClickListener(this)
        tvSure.setOnClickListener(this)


        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),0x01)
        //XXPermissions.with(this).permission(android.Manifest.permission.READ_EXTERNAL_STORAGE).request { permissions, all ->  }


        localAddImgUrl = ("+"+resources.getDrawable(R.drawable.icon_feedback_add))

        addList.add(localAddImgUrl!!)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        feedBackRecyclerView.layoutManager = linearLayoutManager

        feedAddImgAdapter = FeedAddImgAdapter(addList,this)
        feedBackRecyclerView.adapter = feedAddImgAdapter

        feedAddImgAdapter!!.setAddFeedBackListener {
            if(addList.size>4)
                return@setAddFeedBackListener

            if(!addList[it].contains("+")){
                return@setAddFeedBackListener
            }
            selectLocalImg();
        }
    }

    fun selectLocalImg(){
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {

                    Log.e("","-------list="+Gson().toJson(addList))

                    localAddImgUrl = ("+"+resources.getDrawable(R.drawable.icon_feedback_add))
                    // 图片选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    //ImgUtil.loadHead(imgHead, selectList[0].cutPath.toString())
                    val mImagePaths = selectList[0].cutPath.toString()
                    //     Hawk.put(Config.database.IMG_HEAD, mImagePaths?.get(0))
                   // imgCheck = true
                    if(addList.size<4){
                        addList.add(addList.size-1,mImagePaths)
                      //  addList.add(addList.size,localAddImgUrl.toString())
                        feedBackTvNumber.setText((addList.size-1).toString()+"/"+"4")
                    }else{
                        addList.removeAt(3)
                        addList.add(3,mImagePaths)
                        feedBackTvNumber.setText("4/4")
                    }
                    feedAddImgAdapter?.notifyDataSetChanged()

                }
            }
        }
    }
}