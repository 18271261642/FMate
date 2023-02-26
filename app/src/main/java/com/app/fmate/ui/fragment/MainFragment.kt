package com.app.fmate.ui.fragment

import android.os.Bundle
import com.app.fmate.R
import com.app.fmate.base.fragment.BaseFragment
import com.app.fmate.ext.init
import com.app.fmate.ext.initMain
import com.app.fmate.ext.interceptLongClick
import com.app.fmate.viewmodel.MainViewModel
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