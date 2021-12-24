package com.example.xingliansdk.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class MapMotionBean(
    var type: Int,
    var Distance:Double
) : Parcelable, Serializable{
    constructor() :this(0,0.0)
}

//val mapMotionBean = MapMotionBean()

data class ExerciseData(
    var distance: String ="0",
    var calories: String ="0",
    var averageSpeed: String ="0",
    var pace: String ="0"
)