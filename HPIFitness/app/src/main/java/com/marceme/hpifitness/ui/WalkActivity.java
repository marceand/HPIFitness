package com.marceme.hpifitness.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marceme.hpifitness.R;
import com.marceme.hpifitness.model.User;
import com.marceme.hpifitness.service.LocationService;
import com.marceme.hpifitness.util.Helper;
import com.marceme.hpifitness.util.PrefControlUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class WalkActivity extends FragmentActivity implements OnMapReadyCallback{

    //private static final String TAG = WalkActivity.class.getSimpleName();
    private final static int MSG_UPDATE_TIME = 0;

    @BindView(R.id.text_view_distance) TextView mDistanceTextView;
    @BindView(R.id.text_view_time) TextView mTimerTextView;
    @BindView(R.id.start_stop_walk_btn) Button mWalkBtn;

    private GoogleMap mGoogleMap;
    private LocationService mLocationService;
    private boolean isServiceBound;
    private Marker mLocationMarker;
    private Realm mRealm;

    private final Handler mUIUpdateHandler = new UIUpdateHandler(this);

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            //Log.e(TAG, "Service bound");

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

            //Log.e(TAG, " inside locationReceiver");

            int resultCode = intent.getIntExtra(Helper.INTENT_EXTRA_RESULT_CODE, RESULT_CANCELED);

            if (resultCode == RESULT_OK) {
                Toast.makeText(WalkActivity.this,"new marker",Toast.LENGTH_SHORT).show();
                Location userLocation = intent.getParcelableExtra(Helper.INTENT_USER_LAT_LNG);
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
        setContentView(R.layout.activity_walk);
        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
        setUpMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(Helper.ACTION_NAME_SPACE);
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
        mRealm.close();
    }

    private void setUpMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);

        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.start_stop_walk_btn)
    public void walkActivityBtnClick(Button button) {
        if (isServiceBound && !mLocationService.isUserWalking()) {
            initializeWalkService();
            updateWalkPref(true);
            updateStartWalkUI();
        }else if (isServiceBound && mLocationService.isUserWalking()){
            stopWalkService();
            updateStopWalkUI();
            updateWalkPref(false);
            saveWalkData(mLocationService.distanceCovered(),mLocationService.elapsedTime());
        }
    }

    private void initializeWalkService() {
        mLocationService.startUserWalk();
        mLocationService.startBroadcasting();
        mLocationService.startForeground();
    }

    private void stopWalkService() {
        mLocationService.stopUserWalk();
        mLocationService.stopNotification();
    }

    private void saveWalkData(final float distanceWalked, final long timeWalked) {
        AlertDialog.Builder saveBuilder = new AlertDialog.Builder(this);
        saveBuilder.setTitle(getString(R.string.save_walk_data_title));
        saveBuilder.setMessage(getString(R.string.save_walk_data_message));
        saveBuilder.setNegativeButton(getString(R.string.dismiss_walk_data), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                goToDispatchActivity();
            }
        });
        saveBuilder.setPositiveButton(getString(R.string.save_walk_data), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                User user = mRealm.where(User.class).equalTo("id",PrefControlUtil.getID(PrefControlUtil.USER_ID)).findFirst();
                if(user != null){
                    mRealm.beginTransaction();
                    user.updateDistanceCovered(distanceWalked);
                    user.updateTotalTimeWalk(timeWalked);
                    user.setPace(Helper.calculatePace(timeWalked,distanceWalked));
                    mRealm.commitTransaction();
                    goToDispatchActivity();
                }
            }
        });
        saveBuilder.setCancelable(false);
        saveBuilder.create().show();
    }

    private void updateWalkPref(boolean isUserWalk) {
        PrefControlUtil.setUserWalk(PrefControlUtil.USER_WALK, isUserWalk);
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
            mDistanceTextView.setText(getString(R.string.daily_dist_data,Helper.meterToMileConverter(mLocationService.distanceCovered())));
            mTimerTextView.setText(Helper.secondToHHMMSS(mLocationService.elapsedTime()));
        }
    }

    private static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<WalkActivity> activity;

        UIUpdateHandler(WalkActivity activity) {
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

    @Override
    public void onBackPressed() {
        if(!PrefControlUtil.isUserWalking()){
            goToDispatchActivity();
        }else {
            finish();
        }
    }

    private void goToDispatchActivity() {
        Intent intent = new Intent(this, DispatchActivity.class);
        startActivity(intent);
    }
}
