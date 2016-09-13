package com.marceme.hpifitness.ui;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.marceme.hpifitness.LocationProvider;
import com.marceme.hpifitness.R;

public class LocationActivity extends AppCompatActivity implements LocationProvider.LocationCallback{

    private LocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLocationProvider = new LocationProvider(this, this);
    }

    @Override
    public void handleInitialLocation(Location location) {
        Toast.makeText(this,"initial marker",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleNewLocation(Location location) {
        Toast.makeText(this,"changing location marker",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }
}
