package com.marceme.hpifitness;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marceme.hpifitness.service.LocationService;
import com.marceme.hpifitness.util.Util;

import java.lang.ref.WeakReference;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = MapsActivity.class.getSimpleName();
    private final static int MSG_UPDATE_TIME = 0;

    private TextView mDistanceTextView;
    private TextView mTimerTextView;
    private Button mWalkBtn;
    private GoogleMap mGoogleMap;
    private LocationService mLocationService;
    private boolean isServiceBound;
    private Marker mLocationMarker;

    private final Handler mUIUpdateHandler = new UIUpdateHandler(this);

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.e(TAG, "Service bound");
            isServiceBound = true;

            LocationService.LocalBinder localBinder = (LocationService.LocalBinder) binder;
            mLocationService = localBinder.getService();

            if(mLocationService.isUserWalking()){
                updateStartWalkUI();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };


    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, " inside locationReceiver");

            int resultCode = intent.getIntExtra(Util.INTENT_EXTRA_RESULT_CODE, RESULT_CANCELED);

            if (resultCode == RESULT_OK) {

                Log.e(TAG, " received broadcast");

                Location userLocation = intent.getParcelableExtra(Util.INTENT_USER_LAT_LNG);
                LatLng latLng = getLatLng(userLocation);
                updateUserMarkerLocation(latLng);
            }
        }
    };

    private void updateUserMarkerLocation(LatLng latLng) {
        mLocationMarker.setPosition(latLng);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMap();
        setUpTextViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(Util.ACTION_NAME_SPACE);
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, intentFilter);
        startLocationService();
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);

        if(isServiceBound){

            mLocationService.stopBroadcasting();
            if(!mLocationService.isUserWalking()){
                stopLocationService();
            }
        }
        updateStopWalkUI();
        unbindService(mServiceConnection);
        isServiceBound = false;
    }

    private void setUpMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);

        mapFragment.getMapAsync(this);
    }

    private void setUpTextViews() {
        mDistanceTextView = (TextView) findViewById(R.id.text_view_distance);
        mTimerTextView = (TextView) findViewById(R.id.text_view_time);
        mWalkBtn = (Button) findViewById(R.id.start_stop_walk_btn);
    }

    public void walkActivityBtnClick(View button) {
        if (isServiceBound && !mLocationService.isUserWalking()) {
            mLocationService.startUserWalk();
            mLocationService.startBroadcasting();
            mLocationService.startForeground();
            updateStartWalkUI();
        }else if (isServiceBound && mLocationService.isUserWalking()){
            mLocationService.stopUserWalk();
            mLocationService.stopNotification();
            updateStopWalkUI();
        }
    }

    private void updateStopWalkUI() {
        if(mUIUpdateHandler.hasMessages(MSG_UPDATE_TIME)) {
            mUIUpdateHandler.removeMessages(MSG_UPDATE_TIME);
            mWalkBtn.setText(R.string.start_walk);
        }
    }

    private void updateStartWalkUI() {
        mUIUpdateHandler.sendEmptyMessage(MSG_UPDATE_TIME);;
        mWalkBtn.setText(R.string.stop_walk);
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        bindService(intent, mServiceConnection , Context.BIND_AUTO_CREATE);
    }

    private void stopLocationService() {
        Intent intentService = new Intent(this, LocationService.class);
        stopService(intentService);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                    updateUserMapLocation();
            }
        });
    }

    private void updateUserMapLocation() {
        if(isServiceBound){
            Location userLocation = mLocationService.getUserLocation();
            LatLng latLng = getLatLng(userLocation);
            zoomIn(latLng);
            initializeLocationMarker(latLng);
            mLocationService.startBroadcasting();
        }
    }

    private LatLng getLatLng(Location userLocation) {
        return new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
    }

    private void initializeLocationMarker(LatLng latLngMarker) {
                MarkerOptions options = new MarkerOptions()
                .position(latLngMarker)
                .title("I am here!");
                mLocationMarker = mGoogleMap.addMarker(options);
    }

    private void zoomIn(LatLng latLngZoom) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngZoom, 16));
    }

    private void updateUI() {
        if (isServiceBound) {
            mDistanceTextView.setText(mLocationService.distanceCovered() + " meter");
            mTimerTextView.setText(mLocationService.elapsedTime() + " s");
        }
    }

    private static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<MapsActivity> activity;

        UIUpdateHandler(MapsActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            if (MSG_UPDATE_TIME == message.what) {
                activity.get().updateUI();
                sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
            }
        }
    }
}
