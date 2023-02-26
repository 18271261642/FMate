package com.app.fmate.ui.fragment.map

import android.os.Bundle
import android.view.View
import com.app.fmate.R
import com.app.fmate.base.BaseActivity
import com.app.fmate.viewmodel.MainViewModel
import com.gyf.barlibrary.ImmersionBar
import kotlinx.android.synthetic.main.activity_exercise_type.*

class ExerciseTypeActivity : BaseActivity<MainViewModel>(),View.OnClickListener {

    override fun layoutId() = R.layout.activity_exercise_type

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .titleBar(titleBar)
            .init()
        tvStop.setOnClickListener(this)
        tvGo.setOnClickListener(this)
        imgMap.setOnClickListener(this)
    }

    override fun onClick(v: View) {

    }
}