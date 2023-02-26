package com.app.fmate.bean

import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IndexBean(
    var motionResult: List<MotionResult>,
    var sleepResult: List<SleepResult>,
    var heartRateResult: List<HeartRateResult>,
    var bloodPressureResult: List<BloodPressureResult>,
    var mECGResult: List<ECGResult>,
    var bloodOxygenResult: List<BloodOxygenResult>,
    var mHRVResult: List<HRVResult>

) : Parcelable

@Parcelize
data class MotionResult(

    var mChildResult: List<ChildResult>
) : MultiItemEntity, Parcelable {
    override val itemType: Int
        get() = 0
}

@Parcelize
data class ChildResult(
    var time: String,
    var stepCount: Long

) : Parcelable

@Parcelize
data class SleepResult(
    var mChildResult: List<ChildSleepResult>
) : MultiItemEntity, Parcelable {
    override val itemType: Int
        get() = 1
}

@Parcelize
data class ChildSleepResult(
    var time: String,
    var stepCount: Long,
    var type :Int
) : Parcelable


@Parcelize
data class HeartRateResult(
    var mChildResult: List<ChildHeartRateResult>
) : MultiItemEntity, Parcelable {
    override val itemType: Int
        get() = 2
}
@Parcelize
data class ChildHeartRateResult(
    var time: String,
    var stepCount: Int
) : Parcelable

@Parcelize
data class BloodPressureResult(
    var mChildResult: List<ChildBloodPressureResult>

) : MultiItemEntity, Parcelable {
    override val itemType: Int
        get() = 3
}
@Parcelize
data class ChildBloodPressureResult(
    var time: String,
    var high: Int,
    var low: Int
) : Parcelable

@Parcelize
data class ECGResult(
    var mChildResult: List<ChildECGResult>

) : MultiItemEntity, Parcelable {
    override val itemType: Int
        get() = 4
}
@Parcelize
data class ChildECGResult(
    var time: String,
    var stepCount: Long
) : Parcelable
@Parcelize
data class BloodOxygenResult(
    var mChildResult: List<ChildBloodOxygenResult>
) : MultiItemEntity, Parcelable {
    override val itemType: Int
        get() = 5
}
@Parcelize
data class ChildBloodOxygenResult(
    var time: String,
    var stepCount: Long
) : Parcelable
@Parcelize
data class HRVResult(
    var mChildResult: List<ChildHRVResult>

) : MultiItemEntity, Parcelable {
    override val itemType: Int
        get() = 6
}
@Parcelize
data class ChildHRVResult(
    var time: String,
    var stepCount: Long
) : Parcelable



