package com.example.xingliansdk.ui.fragment.map.view;
import com.amap.api.maps.model.LatLng;
import com.sn.map.view.SNMapHelper;

import java.util.ArrayList;
import java.util.List;

public class IRunningContract {
    public interface IView {

        /**
         * 更新设置配置
         * @param isKeepScreenEnable
         * @param isWeatherEnable
         */
        void onUpdateSettingConfig(boolean isKeepScreenEnable,boolean isWeatherEnable );

        /**
         * 更新天气 信息
         * @param weatherType
         * @param weatherTemperatureRange
         * @param weatherQuality
         */
        void onUpdateWeatherData(int weatherType,String weatherTemperatureRange,String weatherQuality);

        /**
         * 地图首次显示的经纬度位置
         * @param mLatitude
         * @param mLongitude
         */
        void onUpdateMapFirstLocation(double mLatitude, double mLongitude);


        /**
         * 更新GPS信号
         * @param signal
         */
        void onUpdateGpsSignal(int signal);

        /**
         * 更新运动数据
         * @param distances
         * @param calories
         * @param hourSpeed
         * @param pace
         */
        void onUpdateSportData(String distances, String calories, String hourSpeed, String pace, List<LatLng> latLngs);

        /**
         * 更新运动数据
         * (这个只 改变时间)
         * @param spendTime
         */
        void onUpdateSportData(String spendTime);

        /**
         * 保存运动数据
         * @param code 状态码
         *
         */
        void onSaveSportDataStatusChange(int code);



        /**
         * 开始运动时的动画
         * @param enable
         */
        void onSportStartAnimationEnable(boolean enable);


    }

    interface IPresenter {
        void initMapListener(SNMapHelper mapHelper);
        void initDefaultValue();
        void requestSettingConfig();
        void requestWeatherData();
        void requestMapFirstLocation();

        void requestStartSport();
        void requestStopSport();
        void requestRetrySaveSportData();

        void saveHeartAndStep(ArrayList<Integer> heartList,int step);
    }
}
