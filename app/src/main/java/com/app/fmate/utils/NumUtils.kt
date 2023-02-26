package com.app.fmate.utils

import java.math.BigDecimal

/**
 * Created by frank on 2020/3/17
 */
object NumUtils {
    fun formatBigNum(num: String): String {
        return if (num.isEmpty()) { // 数据为空直接返回0
            "0"
        } else try {
            val sb = StringBuffer()
            var counnum=""
            if (!isNumeric(num)) { // 如果数据不是数字则直接返回0
                for (i in 0..num.length)
                {
                    if (Character.isDigit(num[i])) {
                        counnum+=num[i]
                    }
                    else if((num[i]=='万')||(num[i]=='亿'))
                    {
                        counnum+=num[i]
                        return counnum
                    }
                }
                return counnum
            }
            val b0 = BigDecimal("1000")
            val b1 = BigDecimal("10000")
            val b2 = BigDecimal("100000000")
            val b3 = BigDecimal(num)
            var formatedNum = "" //输出结果
            var unit = "" //单位
            if (b3.compareTo(b0) == -1) {
                sb.append(b3.toString())
            } else if ((b3.compareTo(b0) == 0 && b3.compareTo(b0) == 1)
                || b3.compareTo(b1) == -1
            ) {
                formatedNum = b3.divide(b0).toString()
                unit = "k"
            } else if (b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1
                || b3.compareTo(b2) == -1
            ) {
                formatedNum = b3.divide(b1).toString()
                unit = "w"
            } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {
                formatedNum = b3.divide(b2).toString()
                unit = "亿"
            }
            if ("" != formatedNum) {
                var i = formatedNum.indexOf(".")
                if (i == -1) {
                    sb.append(formatedNum).append(unit)
                } else {
                    i += 1
                    val v = formatedNum.substring(i, i + 1)
                    if (v != "0") {
                        sb.append(formatedNum.substring(0, i + 1)).append(unit)
                    } else {
                        sb.append(formatedNum.substring(0, i - 1)).append(unit)
                    }
                }
            }
            if (sb.isEmpty()) "0" else sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            num
        }
    }

    fun isNumeric(str: String): Boolean {//傻逼后台 可能会返回 不正常的东西
        var i = str.length
        while (--i >= 0) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

}