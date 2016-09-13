package com.marceme.hpifitness.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.marceme.hpifitness.R;
import com.marceme.hpifitness.model.User;
import com.marceme.hpifitness.util.Helper;

import io.realm.Realm;

/**
 * Created by Marcel on 9/12/2016.
 */
public class BaseAuthentication extends AppCompatActivity {

    protected void checkEmptyFields(String firstName, String username, String password) {
        if(firstName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.login_error_message)).show();
        }else{
            authenticateUser(firstName, username, password);
        }
    }

    protected void checkEmptyFields(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) {
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.login_error_message)).show();
        }else{
            authenticateUser(username, password);
        }
    }

    private void authenticateUser(String username, String password) {

        boolean isUserExits = checkUserExist(username, password);
        if(!isUserExits){
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.not_found_error_message)).show();
        }else{
            goToFitnessScreen();
        }
    }

    private void authenticateUser(String firstName, String username, String password) {

        boolean isUserExits = checkUserExist(username, password);
        if(isUserExits){
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.auth_error_message)).show();
        }else{
            persistUserLocal(createNewUser(firstName,username,password));
            goToFitnessScreen();
        }
    }

    private boolean checkUserExist(String username, String password) {
        Realm realm = Realm.getDefaultInstance();
        User result = realm.where(User.class).equalTo("username",username).equalTo("password",password).findFirst();
        return result == null;
    }

    private User createNewUser(String firstName, String username, String password) {
        User user = new User();
        user.setFirstName(firstName);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    private void persistUserLocal(User user){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    private void goToFitnessScreen() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
