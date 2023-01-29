package com.yang.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.yang.order_appdemo.R;
import com.yang.order_appdemo.databinding.ActivityFuncBinding;
import com.yang.util.FakeCookie;

public class FuncActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityFuncBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFuncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
         * 获取LoginActivity中传入的用户id（视为cookie）
         * */
        Intent getCookieFromLogin = getIntent();
        String getCookie = getCookieFromLogin.getStringExtra("cookie");
        Log.i("cookie",getCookie);
        FakeCookie.setFakeCookie(getCookie);
//        if(null == FakeCookie.getFakeCookie()){
//            Log.i("cookie","nullllllllllllllllllllllllllll");
//        }else {
//            Log.i("cookie",FakeCookie.getFakeCookie());
//        }

        setSupportActionBar(binding.appBarFunc.toolbar);
//        binding.appBarFunc.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_func);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.func, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_func);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}