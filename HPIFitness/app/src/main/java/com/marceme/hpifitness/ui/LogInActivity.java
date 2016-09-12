package com.marceme.hpifitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marceme.hpifitness.R;
import com.marceme.hpifitness.util.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.usernameSignIn) EditText mUsername;
    @BindView(R.id.passwordSignIn) EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginBtn)
    public void signInClickEvent(Button button) {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        checkEmptyFields(username, password);
    }

    @OnClick(R.id.goToSignUpBtn)
    public void goToSignUpClickEvent(Button button) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void checkEmptyFields(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) {
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
