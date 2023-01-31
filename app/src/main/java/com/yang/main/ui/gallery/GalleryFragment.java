package com.yang.main.ui.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.yang.Bean.CancelReservationBean;
import com.yang.Bean.OrderResultBean;
import com.yang.Bean.RefreshReservationBean;
import com.yang.order_appdemo.R;
import com.yang.order_appdemo.databinding.FragmentGalleryBinding;
import com.yang.util.Constant;
import com.yang.util.FakeCookie;
import com.yang.util.OkHttpClientUtil;
import com.yang.util.UserName;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private int orderPortId = 39;

    /*
    * TODO：尝试Fragment之间传递消息
    *  失败
    * */
//    private FragmentManager fManager;
//    private Activity mActivity = getActivity();
//    private SlideshowFragment slideshowFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.textView10;
        Button cancelReservation = binding.button2;
        Button startDeal = binding.button3;
        Button refreshTvUI = binding.button4;

        /*
        * 设置Handler，接收子线程传入数据，更新TextView UI
        * */
        Handler mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    /*
                    * case 0:根据mHandler传递的Bundle信息刷新TextView
                    * */
                    case 0:{
                        Bundle bundle = msg.getData();
                        RefreshReservationBean refreshReservationBean = (RefreshReservationBean)
                                bundle.getSerializable(Constant.REFRESH_RESERVATION_BEAN_KEY);
                        updateTvUI(textView,refreshReservationBean);
                    }
                    break;
                    case 1:{
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_func);
                        navController.navigate(R.id.action_nav_gallery_to_nav_home2);
                    }
                    break;
                    case 2:{
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_func);
                        navController.navigate(R.id.action_nav_gallery_to_nav_slideshow);
                    }
                    break;
                }
            }

            private void updateTvUI(TextView textView, RefreshReservationBean refreshReservationBean) {
                int haveReserved = refreshReservationBean.getHaveReservation();
                if (haveReserved == 2){
                    textView.setText("您还没有预约，请先去预约！");
                }else{
                    String userName = UserName.getUSE_NAME();
                    String chargePortId = Integer.toString(refreshReservationBean.getChargePortId());
                    String startTime = refreshReservationBean.getReserveTime();
                    String endTime = refreshReservationBean.getEndTime();
                    String display = String.format("用户：%1s\n预约充电桩ID:%2s\n预约开始时间：%3s\n"+
                            "预约结束时间：%4s",userName,chargePortId,startTime,endTime);
                    textView.setText(display);
                }
            }
        };

        /*
        * 监听取消预约Button
        * */
        cancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sendString = String.format("{\"id\":%1s}", FakeCookie.getFakeCookie());
                        RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                        Request request = new Request.Builder()
                                .url(Constant.BASE_URL+Constant.CANCEL_RESERVATION_URL)
                                .post(requestBody)
                                .build();
                        OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e("cancel_e",e.toString());
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful()){
                                    String responseString = response.body().string();
                                    CancelReservationBean cancelReservationBean = new Gson()
                                            .fromJson(responseString,CancelReservationBean.class);
                                    int cancelRes = cancelReservationBean.getAffectedReservation();
                                    switch (cancelRes){
                                        case 0:{
                                            Looper.prepare();
                                            Toast.makeText(getContext(),"取消失败，请稍后再试！",
                                                    Toast.LENGTH_LONG).show();
                                            Looper.loop();
                                        }
                                        break;
                                        case 1:{
                                            Looper.prepare();
                                            Toast.makeText(getContext(),"取消预约成功！",
                                                    Toast.LENGTH_SHORT).show();
                                            /*
                                             * 跳转至Reserve Fragment
                                             * */
                                            Message message = new Message();
                                            message.what = 1;
                                            mHandler.sendMessage(message);
                                            Looper.loop();
                                        }
                                        break;
                                        case -1:{
                                            Looper.prepare();
                                            Toast.makeText(getContext(),"您还没有预约！",
                                                    Toast.LENGTH_LONG).show();
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
        });
        /*
        * 开始订单（开始充电）Button 监听点击事件
        * */
        startDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sendString = String.format("{\"id\":%1s,\"charge_port\":%2s}",
                                FakeCookie.getFakeCookie(),Integer.toString(orderPortId));
                        RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                        Request request = new Request.Builder()
                                .url(Constant.BASE_URL+Constant.CONFIRM_ORDER_URL)
                                .post(requestBody)
                                .build();
                        OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e("confirmOrder_e",e.toString());
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String responseString = response.body().string();
                                OrderResultBean orderResultBean = new Gson().fromJson(responseString,
                                        OrderResultBean.class);
                                int orderRes = orderResultBean.getIsSucceed();
                                switch (orderRes){
                                    case 1:{
                                        Looper.prepare();
                                        Toast.makeText(getContext(),"开始充电！",
                                                Toast.LENGTH_SHORT).show();
                                        Message message = new Message();
                                        message.what = 2;
                                        mHandler.sendMessage(message);
                                        Looper.loop();
                                    }
                                    break;
                                    case 0:{
                                        Looper.prepare();
                                        Toast.makeText(getContext(),"该充电桩正在被使用，请等候！",
                                                Toast.LENGTH_LONG).show();
                                        Looper.loop();
                                    }
                                    break;
                                    case 2:{
                                        Looper.prepare();
                                        Toast.makeText(getContext(),"下单失败，请稍后再试！",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        /*
        *  监听 刷新文本Button 点击事件
        *  要向主线程发送msg，从而更新TextViewUI
        * */
        refreshTvUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sendString = String.format("{\"id\":%1s}",FakeCookie.getFakeCookie());
                        RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                        Request request = new Request.Builder()
                                .url(Constant.BASE_URL+Constant.MY_RESERVATION_URL)
                                .post(requestBody)
                                .build();
                        OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e("myReservation",e.toString());
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if(response.isSuccessful()){
                                    String responseString = response.body().string();
                                    Log.e("refresh_e",responseString);
                                    RefreshReservationBean refreshReservationBean = new Gson()
                                            .fromJson(responseString,RefreshReservationBean.class);
                                    /*
                                     * 改变orderPortId，方便点击开始充电Button时向服务器传送消息
                                     * */
//                                orderPortId = refreshReservationBean.getChargePortId();
//                              -----------------------------------------------------
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Constant.REFRESH_RESERVATION_BEAN_KEY,
                                            refreshReservationBean);
                                    Message message = new Message();
                                    message.setData(bundle);
                                    message.what = 0;
                                    mHandler.sendMessage(message);
                                }
                            }
                        });
                    }
                }).start();
            }

        });
        refreshTvUI.performClick();

//        final TextView textView = binding.textGallery;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}