package com.example.xingliansdk.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.base.viewmodel.BaseViewModel
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_about.*

class FeedbackActivity : BaseActivity<BaseViewModel>() {

    override fun layoutId()=R.layout.activity_feedback

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
    }
}