package com.sn.map.impl;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sn.map.bean.SNLocation;
import com.sn.map.interfaces.ILocation;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 功能:谷歌地图定位
 */

public class GoogleLocationImpl extends LocationCallback implements ILocation, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private final WeakReference<Context> context;
    private final int minTime;
    private final int minDistance;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener listener;

    public GoogleLocationImpl(Context context, int minTime, int minDistance) {
        this.context = new WeakReference<>(context.getApplicationContext());
        this.minTime = minTime;
        this.minDistance = minDistance;
    }

    @Override
    public void start() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context.get())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();

            //TODO 谷歌地图好像无法设置 重复无限定位?? 先留着
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(minTime);
            mLocationRequest.setFastestInterval(minTime);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(minDistance);
            mGoogleApiClient.connect();
            Log.i("应用层","天气服务:start" );
        }
    }

    @Override
    public void stop() {
        if (mGoogleApiClient != null) {
            Log.i("应用层","天气服务:stop" );
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
                try {
                    mGoogleApiClient.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mGoogleApiClient.unregisterConnectionCallbacks(this);
                mGoogleApiClient.unregisterConnectionFailedListener(this);
                mGoogleApiClient = null;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (listener != null) {
                if (location == null) {
                    listener.onLocationChanged(null);
                } else {
                    listener.onLocationChanged(getLocation(location));
                }
            }
            Log.i("应用层","天气服务:onLocationChanged" );
        }
    };


    @Override
    public SNLocation getLastLocation() {
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation == null) return null;
            return getLocation(lastLocation);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setLocationListener(LocationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("应用层","天气服务:onConnected" );
        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
                Log.i("应用层","天气服务:requestLocationUpdates" );
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (listener != null) {
            listener.onLocationChanged(null);
        }
        Log.i("应用层","天气服务:onConnectionFailed" );
    }


    private SNLocation getLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(context.get());
            List<Address> fromLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (fromLocation == null || fromLocation.isEmpty()) {
                return null;
            }
            Address address = fromLocation.get(0);

            return new SNLocation(address, location.getTime(), location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy(), location.getSpeed());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
