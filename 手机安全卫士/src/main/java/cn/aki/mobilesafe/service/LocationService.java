package cn.aki.mobilesafe.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.aki.mobilesafe.common.Constants;

/**
 * Created by Administrator on 2015/12/1.
 * 定位服务
 */
public class LocationService extends Service {
    private static final long MIN_TIME = 2000l;   //毫秒
    private static final float MIN_DISTANCE = 50f;    //米

    private SharedPreferences mPref;
    //定位
    private LocationManager mLocationManager;
    private MyLocationListener mLocationListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        mPref=getSharedPreferences(Constants.SharedPreferences.FILE_CONFIG,Context.MODE_PRIVATE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        String provider = mLocationManager.getBestProvider(criteria, true);    //true仅返回当前有效的
        //高版本权限验证
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mLocationListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mLocationManager.removeUpdates(mLocationListener);
    }

    public class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            //保存经纬度
            mPref.edit().putString(Constants.SharedPreferences.KEY_LOCATION,location.getLongitude()+","+location.getLatitude()).apply();
            stopSelf(); //停止
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
