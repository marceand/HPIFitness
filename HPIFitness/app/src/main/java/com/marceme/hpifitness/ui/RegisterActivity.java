package com.marceme.hpifitness.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marceme.hpifitness.R;
import com.marceme.hpifitness.util.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.firstNameSignUp) EditText mFirstName;
    @BindView(R.id.usernameSignUp) EditText mUsername;
    @BindView(R.id.passwordSignUp) EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signUpBtn)
    public void signUpClickEvent(Button button) {
        String firstName = mFirstName.getText().toString().trim();
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        checkEmptyFields(firstName, username, password);
    }

    @OnClick(R.id.cancelBtn)
    public void cancelClickEvent(Button button) {
        finish();
    }

    private void checkEmptyFields(String firstName, String username, String password) {
        if(firstName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Helper.displayMessageToUser(this,
                    getString(R.string.login_error_title),
                    getString(R.string.login_error_message)).show();
        }else{
            goToFitnessScreen();
        }
    }

    private void goToFitnessScreen() {

    }
}
