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
import com.yang.Bean.RegisterResultBean;
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

public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText accountInput = (EditText)findViewById(R.id.editTextPhone);
        EditText pswInput = (EditText)findViewById(R.id.editTextTextPassword);
        EditText pswReinput = (EditText)findViewById(R.id.editTextTextPassword2);

        Button registerBtn = (Button) findViewById(R.id.button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = accountInput.getText().toString();
                String s2 = pswInput.getText().toString();
                String s3 = pswReinput.getText().toString();
                if (registerMatch(s1,s2,s3)){
                    sendRegisterMesHttp(s1,s2);
                }
            }
        });
    }

    private boolean registerMatch(String s1, String s2, String s3) {
        if (!LoginUtil.isPhoneValid(s1)){
            Toast.makeText(this,"手机号格式错误！",Toast.LENGTH_SHORT).show();
            return false;
        }else if (!(LoginUtil.isPswValid(s2) && LoginUtil.isPswValid(s3))){
            Toast.makeText(this,"密码不得少于6位！",Toast.LENGTH_SHORT).show();
        }else if(!s2.equals(s3)){
            Toast.makeText(this,"密码不一致！",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void sendRegisterMesHttp(String s1, String s2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String sendString = String.format("{\"account\":%1s,\"password\":%2s}",s1,s2);

                RequestBody requestBody = RequestBody.create(JSON,sendString);
                Request request = new Request.Builder()
                        .url(Constant.BASE_URL+ Constant.REGISTER_URL)
                        .post(requestBody)
                        .build();
                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("res_f",e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()){
                            Log.i("res","------------Register---------------");
                            String responseString = response.body().string();
                            RegisterResultBean registerResult = new Gson().fromJson(responseString, RegisterResultBean.class);
                            int res = registerResult.getIsSucceed();
                            Log.e("print","res = "+res);
                            switch (res){
                                case 0:
                                {
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this,"账户已注册！",Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                    break;
                                case 1: {
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_SHORT).show();
                                    /*
                                    * DONE:注册成功后进入登录界面，Intent传递账号密码到登录界面
                                    * */
                                    Intent intentToLogin = new Intent(RegisterActivity.this,LoginActivity.class);
                                    intentToLogin.putExtra("account",s1);
                                    intentToLogin.putExtra("password",s2);
                                    startActivity(intentToLogin);
                                    Log.e("intent","-------------------------");

//                                    RegisterActivity.this.onDestroy();
                                    Looper.loop();
                                }
                                break;
                                case 2:
                                {
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this,"注册失败！",Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                    break;
                            }

                            Log.i("res",responseString);
                            Log.i("res","------------Register---------------");
                        }
                    }
                });
            }
        }).start();
    }

}