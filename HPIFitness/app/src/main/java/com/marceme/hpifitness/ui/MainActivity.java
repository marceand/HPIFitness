package com.marceme.hpifitness.ui;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.marceme.hpifitness.LocationProvider;
import com.marceme.hpifitness.R;
import com.marceme.hpifitness.model.User;
import com.marceme.hpifitness.util.AuthUtil;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Realm realm = Realm.getDefaultInstance();
        String id = AuthUtil.getID(AuthUtil.USER_ID);
        User user = realm.where(User.class).equalTo("id",id).findFirst();
        if(user !=null){
            Toast.makeText(this,"I exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
