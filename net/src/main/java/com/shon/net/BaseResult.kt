package com.shon.net

/**
 * Date : 2020/7/21 19:50
 * Package name : net.yt.whale.net
 * Des :
 */
data class BaseResult<T> (var code: Int = 0,
                          var t: Long = 0,
                          var message: String? = null,
                          var data: T? = null){


//    fun setData(data: T) {
//        this.data = data
//    }

    override fun toString(): String {
        return "BaseResult{" +
                "code=" + code +
                ", t='" + t + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }
}