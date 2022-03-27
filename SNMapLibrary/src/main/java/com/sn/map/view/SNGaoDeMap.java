package com.sn.map.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.sn.map.R;
import com.sn.map.bean.SNLocation;
import com.sn.map.interfaces.ILocation;
import com.sn.map.interfaces.OnMapLocationAddressListener;
import com.sn.map.interfaces.OnMapScreenShotListener;
import com.sn.map.interfaces.OnSportMessageListener;
import com.sn.map.utils.LocationConverter;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 功能:高德地图API http://lbs.amap.com/api/android-sdk/guide/create-map/mylocation
 */

public class SNGaoDeMap extends SNMapHelper {

    private GeocodeSearch geocoderSearch;
    private AMap map;
    private BitmapDescriptor mEndMarkerIcon;
    private BitmapDescriptor mBeginMarkerIcon;
    private float lineWidth;
    private int lineColor = 0xFF00DB95;
    private Polyline mPolyline;
    private Marker mBeginMarkerOptions;
    private Marker mEndMarkerOpions;
    private MapView mMapView;
    private int retry_count = 0;
    private boolean isTouchMap;
    private OnSportMessageListener onSportMessageListener;


    public SNGaoDeMap(Activity activity, Bundle savedInstanceState, ILocation iLocation,  MapView mMapView) {
        super(activity, savedInstanceState, iLocation, mMapView);
        this.mMapView = mMapView;
        onActivityCreated(activity, savedInstanceState);
        geocoderSearch = new GeocodeSearch(activity);
        lineWidth = activity.getResources().getDisplayMetrics().density * 5;//宽度
        mEndMarkerIcon = createIcon(activity, R.drawable.ic_location);
        mBeginMarkerIcon = createIcon(activity, R.drawable.ic_location_begin);

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------对外开放-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void screenCapture(boolean isCaptureCenter, final OnMapScreenShotListener onMapScreenShotListener) {
        if (map == null) return;
        if (onMapScreenShotListener == null) return;
        retry_count = 0;

        if (isCaptureCenter) {
            try {
                LatLngBounds.Builder boundsBuilder = getLatLngBoundsBuilder();
                //第二个参数为四周留空宽度
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 10));
            } catch (Exception ignored) {
                onMapScreenShotListener.onMapScreenShot(null);
                return;
            }
        }


        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (map == null) return;
                map.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                    @Override
                    public void onMapScreenShot(Bitmap bitmap) {

                    }

                    @Override
                    public void onMapScreenShot(Bitmap bitmap, int status) {

                        if (null == bitmap) {
                            onMapScreenShotListener.onMapScreenShot(null);
                            return;
                        }
                        if (status != 0) {//地图渲染完成，截屏无网格
                            onMapScreenShotListener.onMapScreenShot(bitmap);
                        } else {//地图未渲染完成，截屏有网格
                            if (retry_count > 5) {
                                //重试了5次 都无法截取完整的内容 说明可能没网 此时可以显示一个默认图标 等下次打开地图的时候 把历史点放上去 然后再截图
                                onMapScreenShotListener.onMapScreenShot(null);
                                return;
                            }
                            //继续截图
                            final AMap.OnMapScreenShotListener listener = this;
                            mMapView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    map.getMapScreenShot(listener);
                                }
                            }, 200);
                            retry_count++;
                        }
                    }
                });
            }
        }, 3000);
    }

    @Override
    public void requestGetLocationAddress(final SNLocation location, final OnMapLocationAddressListener onMapLocationAddressListener) {
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            int error_count = 0;

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == 0 || rCode == 1000 && result != null) {
                    RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
                    String address = String.format(Locale.CHINESE, "%s-%s-%s", regeocodeAddress.getProvince(), regeocodeAddress.getCity(), regeocodeAddress.getDistrict());
                    if (onMapLocationAddressListener != null) {
                        onMapLocationAddressListener.onLocationAddress(address);
                    }

                } else {
//                    code==1804 请检查网络连接是否畅通,请参看 http://lbs.amap.com/api/android-sdk/guide/map-tools/error-code
                    error_count++;
                    //重试5次
                    if (error_count < 5) {
                        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.getLatitude(), location.getLongitude()), 200, GeocodeSearch.AMAP);
                        geocoderSearch.getFromLocationAsyn(query);
                        error_count = 0;
                    } else {
                        if (onMapLocationAddressListener != null) {
                            onMapLocationAddressListener.onLocationAddress("");
                        }
                    }
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });
        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.getLatitude(), location.getLongitude()), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }


    @Override
    public float calculateLineDistance(SNLocation locationA, SNLocation locationB) {
        return AMapUtils.calculateLineDistance(new LatLng(locationA.getLatitude(), locationA.getLongitude()), new LatLng(locationB.getLatitude(), locationB.getLongitude()));
    }

    @Override
    public void setOnSportMessageListener(OnSportMessageListener onSportMessageListener) {
        this.onSportMessageListener = onSportMessageListener;
    }

    @Override
    protected void onSignalChanged(int level) {
        if (onSportMessageListener != null) {
            onSportMessageListener.onSignalChanged(level);
        }
    }

    @Override
    protected void onLocationChanged(SNLocation location) {

    }

    @Override
    public void animateCameraToScreenBounds() {
        if (map == null) return;
        try {
            LatLngBounds.Builder boundsBuilder = getLatLngBoundsBuilder();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 17));
        } catch (Exception ignored) {
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------对外开放-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------私有方法-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
    @NonNull
    private LatLngBounds.Builder getLatLngBoundsBuilder() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        if (mPolyline != null) {
            List<LatLng> points = mPolyline.getPoints();
            for (int i = 0; i < points.size(); i++) {
                boundsBuilder.include(points.get(i));//把所有点都include进去（LatLng类型）
            }
        }
        if (mEndMarkerOpions != null) {
            boundsBuilder.include(mEndMarkerOpions.getPosition());//把所有点都include进去（LatLng类型）
        }
        if (mBeginMarkerOptions != null) {
            boundsBuilder.include(mBeginMarkerOptions.getPosition());
            //把所有点都include进去（LatLng类型）
        }
        LatLngBounds build = boundsBuilder.build();

        //这里把扩大预览范围设置为正方形访问(等会要截正方形图)
        //因此需要取得最大长度的直线(northeast和southwest两点相连接) 的中间点作为中心 依次旋转90度 得到一个正方形区域 再把正方形区域转成经纬度 再传入 LatLngBounds.Builder 最为地图显示范围
        //以下是具体实现:

        Projection projection = map.getProjection();
        //地理位置转成 屏幕像素位置(这样是为了好计算)
        //(右上角)
        Point rt = projection.toScreenLocation(build.northeast);
        //(左下角)
        Point lb = projection.toScreenLocation(build.southwest);

        //通过东北和西南方向的xy值 得到矩形中心xy点
        int x_size = Math.abs(rt.x - lb.x);
        int y_size = Math.abs(rt.y - lb.y);
        int centerX = Math.round(rt.x - (x_size * 1.0f / 2));
        int centerY = Math.round(rt.y + (y_size * 1.0f / 2));

        int y;
        int x;
        //垂直方向最大
        if (Math.max(x_size, y_size) == y_size) {
            //增加30% 的padding效果 不然截图的点太靠边,很难看
            int padding = Math.round(y_size * 0.3f);
            y = rt.y - padding;
            x = centerX;
        } else {//横向方向最大
            //增加30% 的padding效果 不然截图的点太靠边,很难看
            int padding = Math.round(x_size * 0.3f);
            x = rt.x + padding;
            y = centerY;
        }

        //根据top和bottom (此时为最大长度值) 它们的坐标 绕中心点 顺时针旋转90度 得到top->90->right, bottom->90->left
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int p = centerX;
        int q = centerY;
        //这里把顶部的xy点按90读旋转3次 则变成一个正方形 这就是我要的预览范围
        for (int i = 0; i < 4; i++) {
            Point point = new Point(y - q + p, p - x + q);
            x = point.x;
            y = point.y;
            LatLng latLng = projection.fromScreenLocation(point);
            builder.include(latLng);
        }
        return builder;
    }


    /**
     * 测试
     *
     * @param title
     * @param latlng
     * @return
     */
    private Marker drawMarker(String title, LatLng latlng, @DrawableRes int icon) {

        Marker marker = map.addMarker(
                new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(latlng.latitude, latlng.longitude))
                        .title(title)//必须传这个"",否则不显示自定义的InfoWindow,晕,高德就是这么坑
                        .snippet(title)
                        .icon(BitmapDescriptorFactory.fromResource(icon))
                        .draggable(true)
        );
        marker.showInfoWindow();
        return marker;
    }

    @Override
    protected void drawEndMarker(SNLocation location) {
        if (map == null) return;
        if (mEndMarkerOpions != null) {
            mEndMarkerOpions.remove();
        }

        mEndMarkerOpions = map.addMarker(
                new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("")//必须传这个"",否则不显示自定义的InfoWindow,晕,高德就是这么坑
                        .snippet("")
                        .icon(mEndMarkerIcon)
                        .draggable(true)
        );

    }

    @Override
    protected void drawBeginMarker(SNLocation location) {
        if (map == null) return;
        if (mBeginMarkerOptions != null) {
            mBeginMarkerOptions.remove();
        }
        mBeginMarkerOptions = map.addMarker(
                new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("")//必须传这个"",否则不显示自定义的InfoWindow,晕,高德就是这么坑
                        .snippet("")
                        .icon(mBeginMarkerIcon)
                        .draggable(true));
    }


    @Override
    protected void drawLine(SNLocation location) {
        if (map == null) return;
        if (mPolyline != null) {
            mPolyline.remove();
        }
        //转成高德经纬度数据
        LinkedList<SNLocation> locations = getLocations();
        LinkedList<LatLng> latLngs = convert(locations);
        if (latLngs.size() >= 2) {
            PolylineOptions polylineOptions = new PolylineOptions().color(lineColor).width(lineWidth)
                    .addAll(latLngs);
            mPolyline = map.addPolyline(polylineOptions);
        }
        if (onSportMessageListener != null) {
            onSportMessageListener.onSportChanged(locations);
        }
    }


    @Override
    public void moveCamera(SNLocation location, boolean animation) {
        if (map == null) return;
        if (isTouchMap) {
            return;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 25);
        if (animation) {
            map.animateCamera(cameraUpdate);// 有动画
        } else {
            map.moveCamera(cameraUpdate);// 无动画
        }
    }

    @Override
    protected double[] onLocationConverter(double latitude, double longitude) {
        return LocationConverter.Gps2Gaode(mMapView.getContext(), latitude, longitude);
    }


    private LinkedList<LatLng> convert(LinkedList<SNLocation> locations) {
        LinkedList<LatLng> latLngs = new LinkedList<>();
        for (int i = 0; i < locations.size(); i++) {
            SNLocation snLocation = locations.get(i);
            latLngs.add(new LatLng(snLocation.getLatitude(), snLocation.getLongitude()));
        }
        return latLngs;
    }


    private BitmapDescriptor createIcon(Context context, @DrawableRes int iconResId) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), iconResId);
        float density = context.getResources().getDisplayMetrics().density;
        int v = Math.round(density * 30);
        Bitmap bitmap = Bitmap.createBitmap(v, v, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()), new RectF(0, 0, v,
                v), null);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------私有方法-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------生命周期处理-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (mMapView != null) {
            map = mMapView.getMap();
        } else {
            activity.finish();
            return;
        }
        map.setMapType(AMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        uiSettings.setScaleControlsEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        map.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                isTouchMap = true;
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                isTouchMap = false;
            }
        });
       map.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
           @Override
           public boolean onMarkerClick(Marker marker) {
               return true;
           }
       });
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------生命周期-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


}
