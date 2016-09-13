package com.marceme.hpifitness.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.marceme.hpifitness.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseAuthentication{

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
        logInToMainScreen(firstName, username, password);
    }

    @OnClick(R.id.cancelBtn)
    public void cancelClickEvent(Button button) {
        finish();
    }

}
