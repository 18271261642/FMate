package com.example.xingliansdk.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

object ShowToast {
    var mContext: Context? = null
    var oldMsg=""
    var time:Long=0
    fun init(context: Context?) {
        this.mContext = context
    }

    fun showToastLong(msg: String) {
        mContext?.let { showToast(it, msg, Toast.LENGTH_SHORT) }
    }

    fun showToastLong(msg: String,time : Long) {
        mContext?.let { showToast(it, msg, Toast.LENGTH_SHORT) }
    }

    fun showToastShort(msg: String) {
        mContext?.let { showToast(it, msg, Toast.LENGTH_SHORT) }
    }

    fun showToastShort(strRes: Int) {
        mContext?.let { showToast(it, mContext!!.getString(strRes), Toast.LENGTH_SHORT) }
    }

    fun showToastLong(strRes: Int) {
        mContext?.let { showToast(it, mContext!!.getString(strRes), Toast.LENGTH_LONG) }
    }

    fun showToast(mContext: Context, msg: String, duration: Int) {
        val toast = Toast.makeText(mContext, msg, duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        if(msg != oldMsg)
        {
            oldMsg=msg
            time=System.currentTimeMillis()
            toast.show()
        }
        else if(System.currentTimeMillis() - time > 3000)
        {
            oldMsg=msg
            time=System.currentTimeMillis()
            toast.show()
        }

    }
}