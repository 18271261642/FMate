package com.sn.map.interfaces;

import com.sn.map.bean.SNLocation;

import java.util.LinkedList;

/**
 * 功能:运动信息监听
 */

public interface OnSportMessageListener {
    /**
     * 信号值: 0到5 为弱到强, 越大于5则越强(其实这个数量代表可用的低噪卫星数量)
     * @param level
     */
    void onSignalChanged(int level);

    /**
     * 什么距离 什么的交给开发者自己遍历locations 算 我不能写太死
     * @param locations
     */
    void onSportChanged(LinkedList<SNLocation> locations);
}
