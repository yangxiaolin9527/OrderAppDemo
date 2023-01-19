package com.yang.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yang.order_appdemo.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText loginInputAccountET;
    private EditText loginInputPswET;
    private Button loginSignInbtn;
    private Button loginSignUpbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInputAccountET = (EditText) findViewById(R.id.Login_EditText_Account);
        loginInputPswET = (EditText) findViewById(R.id.Login_EditText_psw);
        loginSignInbtn = (Button) findViewById(R.id.Login_SignIn_btn);
        loginSignUpbtn = (Button) findViewById(R.id.Login_SignUp_btn);
        loginSignUpbtn.setOnClickListener(this);
        loginSignInbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String loginInputAccountString = loginInputAccountET.getText().toString();
        String loginInputPswString = loginInputPswET.getText().toString();
        switch (view.getId()){
            case (R.id.Login_SignIn_btn):{
                signInInputMatch(loginInputAccountString,loginInputPswString);
            }
            break;
            case (R.id.Login_SignUp_btn):{
                Intent intentToRegister = new Intent(this,RegisterActivity.class);
                startActivity(intentToRegister);
            }
        }

    }

    private void signInInputMatch(String loginInputAccountString, String loginInputPswString) {
        if(!LoginUtil.isPhoneValid(loginInputAccountString)){
            Toast.makeText(this,"手机号格式错误！",Toast.LENGTH_SHORT).show();
        }else if (!LoginUtil.isPswValid(loginInputPswString)){
            Toast.makeText(this,"密码不得少于6位！",Toast.LENGTH_SHORT).show();
        }
    }
}