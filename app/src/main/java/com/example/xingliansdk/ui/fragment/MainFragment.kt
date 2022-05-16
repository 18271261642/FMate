package com.example.xingliansdk.ui.fragment

import android.os.Bundle
import com.example.xingliansdk.R
import com.example.xingliansdk.base.fragment.BaseFragment
import com.example.xingliansdk.ext.init
import com.example.xingliansdk.ext.initMain
import com.example.xingliansdk.ext.interceptLongClick
import com.example.xingliansdk.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment<MainViewModel>() {

    override fun layoutId() = R.layout.fragment_main

    override fun initView(savedInstanceState: Bundle?) {
       // mainViewpager.initMainTest(this)
        mainViewpager.initMain(this)
        mainBottom.init {
            when (it) {
                R.id.menu_main -> mainViewpager.setCurrentItem(0, false)
                R.id.menu_motion -> mainViewpager.setCurrentItem(1, false)
              //  R.id.menu_device -> mainViewpager.setCurrentItem(1, false)
                R.id.menu_me -> mainViewpager.setCurrentItem(2, false)
            }
        }
        mainBottom.interceptLongClick(R.id.menu_main,R.id.menu_motion/*, R.id.menu_device*/, R.id.menu_me)


    }



}