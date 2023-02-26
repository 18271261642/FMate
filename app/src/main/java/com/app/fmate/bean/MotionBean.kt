package com.app.fmate.bean

import java.io.Serializable

data class MotionBean(
    val distance: String="0.0"
) : Serializable {
    constructor() : this("0.0")
}