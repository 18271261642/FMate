package com.sn.map.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sn.map.R;
import com.sn.map.bean.SNLocation;
import com.sn.map.interfaces.ILocation;
import com.sn.map.interfaces.OnMapLocationAddressListener;
import com.sn.map.interfaces.OnMapScreenShotListener;
import com.sn.map.interfaces.OnSportMessageListener;
import com.sn.map.utils.LocationConverter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 功能:谷歌地图
 */

public class SNGoogleMap extends SNMapHelper implements OnMapReadyCallback {


    private Geocoder geocoderSearch;
    private com.google.android.gms.maps.GoogleMap map;
    private BitmapDescriptor mEndMarkerIcon;
    private BitmapDescriptor mBeginMarkerIcon;
    private float lineWidth;
    private int lineColor = 0xFF00DB95;
    private Polyline mPolyline;
    private Marker mBeginMarkerOptions;
    private Marker mEndMarkerOpions;
    private MapView mMapView;
    private boolean isTouchMap;
    private OnSportMessageListener onSportMessageListener;

    public SNGoogleMap(Activity activity, Bundle savedInstanceState, ILocation iLocation, com.google.android.gms.maps.MapView mMapView) {
        super(activity, savedInstanceState, iLocation, mMapView);
        this.mMapView = mMapView;
        onActivityCreated(activity, savedInstanceState);
        geocoderSearch = new Geocoder(activity, Locale.getDefault());
        lineWidth = activity.getResources().getDisplayMetrics().density * 5;
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

        if (isCaptureCenter) {
            try {
                LatLngBounds.Builder boundsBuilder = null;
                try {
                    boundsBuilder = getLatLngBoundsBuilder();
                } catch (Exception e) {
                    onMapScreenShotListener.onMapScreenShot(null);
                    return;
                }
                //第二个参数为四周留空宽度
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 10), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        mMapView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (map == null) return;
                                map.snapshot(new com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback() {
                                    @Override
                                    public void onSnapshotReady(Bitmap bitmap) {
                                        if (null == bitmap) {
                                            onMapScreenShotListener.onMapScreenShot(null);
                                            return;
                                        }
                                        onMapScreenShotListener.onMapScreenShot(bitmap);
                                    }
                                });

                            }
                        }, 3000);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            } catch (Exception ignored) {
            }
            return;
        }
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (map == null) return;
                map.snapshot(new com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        if (null == bitmap) {
                            onMapScreenShotListener.onMapScreenShot(null);
                            return;
                        }
                        onMapScreenShotListener.onMapScreenShot(bitmap);
                    }
                });

            }
        }, 3000);
    }

    @Override
    public void requestGetLocationAddress(final SNLocation location, final OnMapLocationAddressListener onMapLocationAddressListener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> list = geocoderSearch.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String address;
                    if (list == null || list.isEmpty()) {
                        address = "未知";
                    } else {
                        Address mLocationAddress = list.get(0);
                        address = String.format("%s-%s-%s", mLocationAddress.getAdminArea(), mLocationAddress.getLocality(), mLocationAddress.getSubLocality());
                    }
                    final String finalAddress = address;
                    mMapView.post(new Runnable() {
                        @Override
                        public void run() {
                            onMapLocationAddressListener.onLocationAddress(finalAddress);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    @Override
    public float calculateLineDistance(SNLocation locationA, SNLocation locationB) {
        float[] results = new float[1];
        Location.distanceBetween(locationA.getLatitude(), locationA.getLongitude(),
                locationB.getLatitude(), locationB.getLongitude(),
                results);
        return results[0];
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
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 15));
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
        LatLngBounds  build = boundsBuilder.build();

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
                        .icon(createIcon(mMapView.getContext(), icon))
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
    public void moveCamera(SNLocation location, boolean anim) {
        if (map == null) return;
        if (isTouchMap) {
            return;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
        map.animateCamera(cameraUpdate);// 有动画
    }

    @Override
    protected double[] onLocationConverter(double latitude, double longitude) {
        return LocationConverter.Gps2Google(latitude, longitude);
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

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mMapView.onStart();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mMapView.onResume();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mMapView.onPause();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mMapView.onStop();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
//        uiSettings.setZoomGesturesEnabled(false);
//        uiSettings.setCompassEnabled(true);
        //请求显示上次位置
        super.requestLastLocation();
//        map.setOnCameraMoveListener(new com.google.android.gms.maps.GoogleMap.OnCameraMoveListener() {
//            @Override
//            public void onCameraMove() {
//                isTouchMap = true;
//            }
//        });
    }
}
