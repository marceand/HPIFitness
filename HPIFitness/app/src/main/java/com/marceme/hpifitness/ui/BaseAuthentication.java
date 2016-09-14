package com.marceme.hpifitness.ui;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.marceme.hpifitness.R;
import com.marceme.hpifitness.model.User;
import com.marceme.hpifitness.util.Helper;
import com.marceme.hpifitness.util.PrefManager;

import io.realm.Realm;

/**
 * Created by Marcel on 9/12/2016.
 */
public class BaseAuthentication extends AppCompatActivity {

    private Realm mRealm; // Realm is the local database
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        mRealm = Realm.getDefaultInstance();
        mProgressDialog = Helper.displayProgressDialog(this, false, getString(R.string.log_progress_dialog_message));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRealm.close();
    }

    protected void logInToMainScreen(String firstName, String username, String password) {
        if(isEmptyField(firstName,username,password)) {
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.login_error_message)).show();
        }else{
            authenticateUser(firstName, username, password);
        }
    }


    protected void logInToMainScreen(String username, String password) {
        if(isEmptyField(username,password)) {
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.login_error_message)).show();
        }else{
            authenticateUser(username, password);
        }
    }

    private boolean isEmptyField(String firstName, String username, String password) {
        if(firstName.isEmpty() || username.isEmpty() || password.isEmpty() || password.length() < 4) {
            return true;
        }
        return false;
    }

    protected boolean isEmptyField(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) {
            return true;
        }
        return false;
    }

    private void authenticateUser(String username, String password) {

        User user = queryUser(username,password);
        if(user == null){
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.not_found_error_message)).show();
        }else{
            persistUserID(user.getId());
            goToDispatch();
        }
    }

    private void authenticateUser(String firstName, String username, String password) {

        if(queryUser(username, password)!= null){
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.user_exist_error_message)).show();
        }else{
            persistUserLocal(createNewUser(firstName,username,password));
        }
    }

    private User queryUser(String username, String password) {
        return mRealm.where(User.class).equalTo("username",username).equalTo("password",password).findFirst();
    }

    // Assume password is hash or encrypted before saving to local database
    private User createNewUser(String firstName, String username, String password) {
        User user = new User();
        user.setFirstName(firstName);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    // Assume Database is in the server side - persisting username, password locally is bab practice
    private void persistUserLocal(final User user){

        mProgressDialog.show();

        // Assume the current time is the id session
        final String id = System.currentTimeMillis()+"";

        user.setId(id);
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealm(user);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                mProgressDialog.dismiss();
                persistUserID(id);
                goToDispatch();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                mProgressDialog.dismiss();
            }
        });

    }

    // Assume id is the app session token id
    private void persistUserID(String id) {
        PrefManager.setID(PrefManager.USER_ID, id);
    }

    private void goToDispatch() {
        startActivity(Helper.getIntent(this,DispatchActivity.class));
    }

}
