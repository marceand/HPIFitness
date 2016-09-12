package com.marceme.hpifitness;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.marceme.hpifitness.service.LocationService;

import java.lang.ref.WeakReference;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = MapsActivity.class.getSimpleName();

    private TextView mDistanceTextView;
    private TextView mTimerTextView;
    private Button mWalkBtn;
    private GoogleMap mMap;
    private LocationService mLocationService;
    private boolean isServiceBound;
    private final static int MSG_UPDATE_TIME = 0;

    private final Handler mUIUpdateHandler = new UIUpdateHandler(this);

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.e(TAG, "Service bound");
            isServiceBound = true;

            LocationService.LocalBinder localBinder = (LocationService.LocalBinder) binder;

            mLocationService = localBinder.getService();

            mLocationService.startBroadcasting();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMap();
        setUpTextViews();
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
        if (isServiceBound) {
            mUIUpdateHandler.removeMessages(MSG_UPDATE_TIME);
            stopLocationService();
            mWalkBtn.setText(R.string.start_walk);
            isServiceBound = false;
        }else{
            startLocationService();
            mUIUpdateHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            mWalkBtn.setText(R.string.stop_walk);
        }
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        bindService(intent, mServiceConnection , Context.BIND_AUTO_CREATE);
    }

    private void stopLocationService() {
        unbindService(mServiceConnection);
        Intent intentService = new Intent(this, LocationService.class);
        stopService(intentService);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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


    //    @Override
//    public void handleNewLocation(Location location) {
//        double currentLatitude = location.getLatitude();
//        double currentLongitude = location.getLongitude();
//        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title("I am here!");
//        mMap.addMarker(options);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//    }
}
