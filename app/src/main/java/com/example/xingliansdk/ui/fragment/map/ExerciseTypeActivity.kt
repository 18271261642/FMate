package com.example.xingliansdk.ui.fragment.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.xingliansdk.R
import com.example.xingliansdk.base.BaseActivity
import com.example.xingliansdk.viewmodel.MainViewModel
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