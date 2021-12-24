package com.sn.map.utils;


import androidx.annotation.NonNull;
import com.sn.map.bean.SNLocation;
import com.sn.map.view.SNMapHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 功能:
 * 参考https://www.jianshu.com/p/4fd67921b743
 */

public class LinearSmooth {
    private final double dMax;
    private final int start;
    private final int end;
    private final SNMapHelper mapHelper;
    private ArrayList<LatLngPoint> mLineInit = new ArrayList<>();

    /**
     * 使用三角形面积（使用海伦公式求得）相等方法计算点pX到点pA和pB所确定的直线的距离
     *
     * @param start  起始经纬度
     * @param end    结束经纬度
     * @param center 前两个点之间的中心点
     * @return 中心点到 start和end所在直线的距离
     */
    private double distToSegment(LatLngPoint start, LatLngPoint end, LatLngPoint center) {


        double a = Math.abs(mapHelper.calculateLineDistance(start.getLocation(), end.getLocation()));
        double b = Math.abs(mapHelper.calculateLineDistance(start.getLocation(), center.getLocation()));
        double c = Math.abs(mapHelper.calculateLineDistance(end.getLocation(), center.getLocation()));


        double p = (a + b + c) / 2.0;
        double s = Math.sqrt(Math.abs(p * (p - a) * (p - b) * (p - c)));
        double d = s * 2.0 / a;
        return d;
    }

    public LinearSmooth(SNMapHelper mapHelper, List<SNLocation> mLineInit, double dmax) {
        this.mapHelper = mapHelper;
        if (mLineInit == null) {
            throw new IllegalArgumentException("传入的经纬度坐标list == null");
        }
        this.dMax = dmax;
        this.start = 0;
        this.end = mLineInit.size() - 1;
        for (int i = 0; i < mLineInit.size(); i++) {
            LatLngPoint latLngPoint = new LatLngPoint(i, mLineInit.get(i));
            this.mLineInit.add(latLngPoint);
        }
    }


    /**
     * 压缩经纬度点
     *
     * @return
     */
    public LinkedList<SNLocation> compress(LinkedList<SNLocation> mLineFilter) {
        int size = mLineInit.size();
        LatLngPoint[] originalLatLngs = mLineInit.toArray(new LatLngPoint[size]);

        ArrayList<LatLngPoint> endLatLngs = new ArrayList<>();
        for (int i = 0; i < mLineFilter.size(); i++) {
            LatLngPoint latLngPoint = new LatLngPoint(i, mLineFilter.get(i));
            endLatLngs.add(latLngPoint);
        }

        ArrayList<LatLngPoint> latLngPoints = compressLine(originalLatLngs, endLatLngs, start, end, dMax);
        latLngPoints.add(mLineInit.get(0));
        latLngPoints.add(mLineInit.get(size - 1));
        //对抽稀之后的点进行排序
        Collections.sort(latLngPoints, new Comparator<LatLngPoint>() {
            @Override
            public int compare(LatLngPoint o1, LatLngPoint o2) {
                return o1.compareTo(o2);
            }
        });
        LinkedList<SNLocation> latLngs = new LinkedList<>();
        for (LatLngPoint point : latLngPoints) {
            latLngs.add(point.getLocation());
        }
        return latLngs;
    }


    /**
     * 根据最大距离限制，采用DP方法递归的对原始轨迹进行采样，得到压缩后的轨迹
     * x
     *
     * @param originalLatLngs 原始经纬度坐标点数组
     * @param endLatLngs      保持过滤后的点坐标数组
     * @param start           起始下标
     * @param end             结束下标
     * @param dMax            预先指定好的最大距离误差
     */
    private ArrayList<LatLngPoint> compressLine(LatLngPoint[] originalLatLngs, ArrayList<LatLngPoint> endLatLngs, int start, int end, double dMax) {
        if (start < end) {
            //递归进行调教筛选
            double maxDist = 0;
            int currentIndex = 0;
            for (int i = start + 1; i < end; i++) {
                double currentDist = distToSegment(originalLatLngs[start], originalLatLngs[end], originalLatLngs[i]);
                if (currentDist > maxDist) {
                    maxDist = currentDist;
                    currentIndex = i;
                }
            }
            //若当前最大距离大于最大距离误差
            if (maxDist >= dMax) {
                //将当前点加入到过滤数组中
                endLatLngs.add(originalLatLngs[currentIndex]);
                //将原来的线段以当前点为中心拆成两段，分别进行递归处理
                compressLine(originalLatLngs, endLatLngs, start, currentIndex, dMax);
                compressLine(originalLatLngs, endLatLngs, currentIndex, end, dMax);
            }
        }
        return endLatLngs;
    }


}

class LatLngPoint implements Comparable<LatLngPoint> {
    /**
     * 用于记录每一个点的序号
     */
    public int id;
    /**
     * 每一个点的经纬度
     */
    public SNLocation location;

    public SNLocation getLocation() {
        return location;
    }


    public LatLngPoint(int id, SNLocation latLng) {
        this.id = id;
        this.location = latLng;
    }

    @Override
    public int compareTo(@NonNull LatLngPoint o) {
        if (this.id < o.id) {
            return -1;
        } else if (this.id > o.id)
            return 1;
        return 0;
    }
}