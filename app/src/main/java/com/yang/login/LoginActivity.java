package com.yang.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.yang.Bean.LoginResultBean;
import com.yang.main.FuncActivity;
import com.yang.order_appdemo.R;
import com.yang.util.Constant;
import com.yang.util.OkHttpClientUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

        /*
        * 接收从RegisterActivity中传入的刚注册的账户信息，并填入EditText，方便用户直接登录
        * */
        Intent getIntentFromRegisterAct = getIntent();
        String account = getIntentFromRegisterAct.getStringExtra("account");
        String password = getIntentFromRegisterAct.getStringExtra("password");
//        Log.i("getIntent",account+"###########"+password);
        loginInputAccountET.setText(account);
        loginInputPswET.setText(password);

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
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    String sendString = String.format("{\"account\":%1s,\"password\":%2s}",
                            loginInputAccountString,loginInputPswString);

                    Log.i("sendString",sendString);

                    RequestBody requestBody = RequestBody.create(JSON, sendString);
                    Request request = new Request.Builder()
                            .url(Constant.BASE_URL + Constant.LOGIN_URL)
                            .post(requestBody)
                            .build();
                    OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e("log_f", e.toString());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Log.i("log_resp", "------------Login--------------");
                                String responseString = response.body().string();
                                LoginResultBean loginResultBean = new Gson().fromJson(responseString, LoginResultBean.class);
                                int loginRes = loginResultBean.getIsSucceed();
                                switch (loginRes){
                                    case 1:{
                                        /*
                                         * 登录成功后跳转至 main.FunActivity (要将用户信息一并传递)
                                         *  暂时只是将用户id传送作为乞丐版“Cookie”
                                         * */
                                        Looper.prepare();
                                        String fakeCookie = loginResultBean.getUserid();
                                        Log.i("cookie",fakeCookie); //这个是有值的
                                        Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                                        Intent intentToFunc = new Intent(LoginActivity.this, FuncActivity.class);
                                        intentToFunc.putExtra(Constant.KEEP_COOKIE,fakeCookie);
                                        intentToFunc.putExtra(Constant.KEEP_USER_NAME,loginInputAccountString);
                                        startActivity(intentToFunc);
                                        Looper.loop();
                                    }
                                    break;
                                    case 0:{
                                        Looper.prepare();
                                        Toast.makeText(LoginActivity.this,"用户不存在！",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    break;
                                    case 2:{
                                        Looper.prepare();
                                        Toast.makeText(LoginActivity.this,"密码错误！",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    break;
                                }
                            }
                        }
                    });
                }
            }).start();
        }
    }
}