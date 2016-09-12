package com.marceme.hpifitness.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.marceme.hpifitness.LocationProvider;
import com.marceme.hpifitness.util.Util;

/**
 * Created by Marcel on 9/11/2016.
 */
public class LocationService extends Service implements LocationProvider.LocationCallback{

    private static final String TAG = Location.class.getSimpleName();

    private LocationProvider mLocationProvider;
    private boolean isRunning;
    private Location mCurrentLocation;
    private Location mPreviousLocation;
    private boolean isBroadcastAllow;
    private float    mTotalDistance;

    private long startTime, endTime;

    private IBinder mIBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!isRunning) {
            mLocationProvider = new LocationProvider(this, this);
            mLocationProvider.connect();
            mLocationProvider.changeSetting(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 60*1000, 20*1000);
            startTimer();
            isRunning = true;
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mIBinder;

    }

    @Override
    public void handleNewLocation(Location location) {
        Log.e(TAG, " getting new location");

        mCurrentLocation = location;
        calculateDistance();
        broadCastLocation(mCurrentLocation);
    }

    private void calculateDistance(){

        if(mPreviousLocation == null){
            mPreviousLocation= mCurrentLocation;
            mTotalDistance = 0;
        }else {
            float distanceDiff = mPreviousLocation.distanceTo(mCurrentLocation); // Return meter unit
            mTotalDistance = mTotalDistance + distanceDiff;
        }
    }

    public void startBroadcasting() {
        Log.e(TAG, " start broadcast");
        isBroadcastAllow = true;

        broadcastFirstLocation();
    }

    private void broadcastFirstLocation() {
        if(mCurrentLocation != null){
            broadCastLocation(mCurrentLocation);
        }
    }

    public void stopBroadcasting() {
        Log.e(TAG, " stop broadcast");
        isBroadcastAllow = false;
    }

    @Override
    public void onDestroy() {

        Log.e(TAG, "onDestroy");
        isRunning = false;
        mLocationProvider.disconnect();

    }


    private void broadCastLocation(Location location) {

        if(isBroadcastAllow){
            broadcastRescuerLocation(location);
        }

    }

    private void broadcastRescuerLocation(Location location) {

        Log.e(TAG, " broadcasting");
        Intent in = new Intent(Util.ACTION_NAME_SPACE);

        in.putExtra(Util.INTENT_EXTRA_RESULT_CODE, Activity.RESULT_OK);
        in.putExtra(Util.INTENT_USER_LAT_LNG, location);

        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public long elapsedTime() {
        return endTime > startTime ?
                (endTime - startTime) / 1000 :
                (System.currentTimeMillis() - startTime) / 1000;
    }

    public class LocalBinder extends Binder {
        public LocationService getService(){
            return LocationService.this;
        }
    }
}
