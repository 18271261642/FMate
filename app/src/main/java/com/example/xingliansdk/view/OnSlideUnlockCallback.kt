package com.example.xingliansdk.view


/**
 * <pre>
 *   @author : leo
 *   @time   : 2021/02/22
 *   @desc   : 滑动解锁状态回调
 * </pre>
 */
interface OnSlideUnlockCallback {

    /**
     * Slide to unlock Complete callback
     * @param view
     */
    fun onSlideUnlockComplete(view: SlideUnlockView)

    /**
     * Slide to unlock Complete callback
     */
    /**
     * Slide unlock progress
     * @param view
     * @param progress
     */
    fun onSlideUnlockProgress(view: SlideUnlockView, progress: Float)
}