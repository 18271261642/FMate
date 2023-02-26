package com.app.fmate.ui.setting

import android.os.Bundle
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.base.viewmodel.BaseViewModel
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