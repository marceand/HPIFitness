package com.marceme.hpifitness.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.marceme.hpifitness.util.Helper;
import com.marceme.hpifitness.util.PrefControlUtil;

public class DispatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(PrefControlUtil.isAuthorized()){

            if(PrefControlUtil.isUserWalking()) {
                startActivity(Helper.getIntent(this,WalkActivity.class));
            }else {
                startActivity(Helper.getIntent(this,MainActivity.class));
            }
        }else {
            startActivity(Helper.getIntent(this,LogInActivity.class));
        }

    }

}
