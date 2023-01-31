package com.yang.main.ui.slideshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.yang.Bean.ConfirmEndChargeBean;
import com.yang.Bean.GetDealResultBean;
import com.yang.order_appdemo.R;
import com.yang.order_appdemo.databinding.FragmentSlideshowBinding;
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

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private int orderPortId = 39;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button endChargeBtn = binding.button5;
        Button refreshDealBtn = binding.button6;
        TextView dealDetail = binding.textView11;

        Handler mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    /*
                    * case 0: 接收refresh Button返回消息，更新TextView UI
                    * */
                    case 0:{
                        Bundle bundle = msg.getData();
                        GetDealResultBean getDealResultBean = (GetDealResultBean) bundle
                                .getSerializable(Constant.REFRESH_DEAL_BEAN_KEY);
                        updateTvUI(dealDetail,getDealResultBean);
                    }
                    break;
                    case 1:{
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_func);
                        navController.navigate(R.id.action_nav_slideshow_to_nav_home);
                    }
                    break;
                }
            }

            private void updateTvUI(TextView dealDetail, GetDealResultBean getDealResultBean) {
                int getDealRes = getDealResultBean.getId();
                String display;
                if (-1 == getDealRes){
                    display = "您没有进行中的订单！";
                }else {
                    String chargePortId = getDealResultBean.getChargePortId();
                    String cost = Double.toString(getDealResultBean.getCost());
                    String totalPower = Integer.toString(getDealResultBean.getTotalPower());
                    String chargeTime = Integer.toString(getDealResultBean.getUserTime());
                    String startTime = getDealResultBean.getCreateTime();

                    display = String.format("用户：%1s\n充电桩Id: %2s\n充电开始时间：%3s\n" +
                            "充电时长：%4s\n已充电力：%5s\n计费：%6s", UserName.getUSE_NAME(),
                            chargePortId,startTime,chargeTime,totalPower,cost);
                }
                dealDetail.setText(display);
            }
        };

        /*
        * 设置结束充电Button监听事件
        * */
        endChargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                * 设置弹窗提醒
                * */
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("确认结束充电？");
                alertDialog.setMessage("");
                alertDialog.setCancelable(false);
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(),"操作已取消！",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*
                        * 开启子线程，结束充电
                        * */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sendString = String.format("{\"id\":%1s,\"charge_port\":%2s}",
                                        FakeCookie.getFakeCookie(),orderPortId);
                                RequestBody requestBody = RequestBody.create(Constant.JSON,
                                        sendString);
                                Request request = new Request.Builder()
                                        .url(Constant.BASE_URL+Constant.CONFIRM_FINISH_ORDER_URL)
                                        .post(requestBody)
                                        .build();
                                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e("confirm_end_order",e.toString());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        String responseString = response.body().string();
                                        ConfirmEndChargeBean confirmEndChargeBean = new Gson()
                                                .fromJson(responseString,ConfirmEndChargeBean.class);
                                        int confirmEndChargeRes = confirmEndChargeBean.getIsSucceed();
                                        switch (confirmEndChargeRes){
                                            case 1:{
                                                Looper.prepare();
                                                Toast.makeText(getContext(),"完成充电！",
                                                        Toast.LENGTH_SHORT).show();
                                                Message message = new Message();
                                                message.what = 1;
                                                mHandler.sendMessage(message);
                                                Looper.loop();
                                            }
                                            break;
                                            case 0:{
                                                Looper.prepare();
                                                Toast.makeText(getContext(),"您可能没有进行中的订单，" +
                                                        "否则请联系管理员！",Toast.LENGTH_LONG).show();
                                                Looper.loop();
                                            }
                                            break;
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                });
                alertDialog.show();
            }
        });

        /*
        *  监听刷新Button的点击事件
        *  需要向主线程发送msg，更新TextView UI
        * */
        refreshDealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO:charge_portId待处理
                String sendString = String.format("{\"id\":%1s,\"charge_port\":%2s}",
                        FakeCookie.getFakeCookie(),"12");
                RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                Request request = new Request.Builder()
                        .url(Constant.BASE_URL+Constant.FINISH_ORDER_URL)
                        .post(requestBody)
                        .build();
                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("get_deal_e",e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseString = response.body().string();
                        GetDealResultBean getDealResultBean = new Gson().fromJson(responseString,
                                GetDealResultBean.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.REFRESH_DEAL_BEAN_KEY,getDealResultBean);
                        Message message = new Message();
                        message.setData(bundle);
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                });
            }
        });
//        默认点击一次
        refreshDealBtn.performClick();


//        final TextView textView = binding.textSlideshow;
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    public void getChargeId(int orderPortId) {
//        this.orderPortId = orderPortId;
//    }
}