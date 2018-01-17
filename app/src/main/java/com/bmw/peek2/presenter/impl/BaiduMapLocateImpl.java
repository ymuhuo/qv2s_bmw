package com.bmw.peek2.presenter.impl;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.presenter.BaiduMapLocatePresenter;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.view.ui.BaseActivity;
import com.bmw.peek2.view.ui.PreviewActivity;

import java.util.Iterator;

/**
 * Created by admin on 2017/4/14.
 */

public class BaiduMapLocateImpl implements BaiduMapLocatePresenter {

    private Intent mIntent;
    private Context context;
    private LocationManager lm;

    private void log(String msg) {
//        Log.d(TAG,msg);
        LogUtil.log(msg);
    }

    public BaiduMapLocateImpl(Context context) {
        this.context = context;
        initIntent();
        log("经纬度： 初始化完成");
    }

    private void initIntent() {

        mIntent = new Intent();
        mIntent.setAction("data.receiver");
//        initLocationService(context);
    }


    @Override
    public void startLocate() {
        log("经纬度：  a开始定位");
//        locationService.start();// 定位SDK
        setLocationBySystem();
    }

    @Override
    public void stopLocate() {
        log("经纬度：  a停止定位");
//        locationService.stop();
        lm.removeUpdates(locationListener);
        lm.removeGpsStatusListener(listener);
    }

    @Override
    public void destroy() {
        log("经纬度：  a销毁");
        mIntent = null;

    }

    public void setLocationBySystem() {
        log("GPS定位！");
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            ((PreviewActivity) context).startActivityForResult(intent, 0);
            mIntent.putExtra("isNotGetLocate",true);
            context.sendBroadcast(mIntent);
            return;
        }
        // 为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = lm.getLastKnownLocation(bestProvider);
        updateView(location);
        // 监听状态
        lm.addGpsStatusListener(listener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 30, 1, locationListener);

//        return location;
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 精确度
        // Criteria.ACCURACY_COARSE较粗略，
        // Criteria.ACCURACY_FINE较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 速度
        criteria.setSpeedRequired(false);
        // 运营商收费
        criteria.setCostAllowed(false);
        // 方位信息
        criteria.setBearingRequired(false);
        // 海拔
        criteria.setAltitudeRequired(false);
        // 电源
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }


    // 位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            log("时间：" + location.getTime());
            log("经度：" + location.getLongitude());
            log("纬度：" + location.getLatitude());
            log("海拔：" + location.getAltitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    log("当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    log("当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    log("当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }

    };

    private int count;
    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
//                    log("第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                    log("卫星状态改变");
                    // 获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                            .iterator();
                    count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
//                    log("搜索到：" + count + "颗卫星");
//                    BaseApplication.toast("搜索到：" + count + "颗卫星");
                    String bestProvider = lm.getBestProvider(getCriteria(), true);
                    Location location = lm.getLastKnownLocation(bestProvider);
                    updateView(location);
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
//                    log("定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
//                    log("定位结束");
                    break;
            }
        }

        ;
    };

    /**
     * 实时更新文本内容
     *
     * @param location
     */
    private void updateView(Location location) {
        if (location != null) {

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();


            mIntent.putExtra("latitude", longitude);
            mIntent.putExtra("lontitude", latitude);
            BaseApplication.context().sendBroadcast(mIntent);

            mIntent.removeExtra("latitude");
            mIntent.removeExtra("lontitude");
//            Log.d(TAG, "updateView: " + latitude + "," + longitude);
//            BaseApplication.toast((count!=0?"搜索到：" + count + "颗卫星 , ":" ")+"定位成功：" + latitude + "," + longitude);
        } else {

//            log("经纬度：信息为空");
//            BaseApplication.toast((count!=0?"搜索到：" + count + "颗卫星 , ":" ")+"正在定位.....");
        }
    }


}
