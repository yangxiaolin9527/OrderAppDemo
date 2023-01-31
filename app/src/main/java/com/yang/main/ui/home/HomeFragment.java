package com.yang.main.ui.home;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yang.Bean.PortsBean;
import com.yang.Bean.PortsBeanAdapter;
import com.yang.Bean.ReserveConfirmBean;
import com.yang.order_appdemo.R;
import com.yang.order_appdemo.databinding.FragmentHomeBinding;
import com.yang.util.Constant;
import com.yang.util.FakeCookie;
import com.yang.util.OkHttpClientUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    //    private List<ListViewBean> listViewBeanList = new ArrayList<>();
    private Handler mHandler;
//    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private List<PortsBean.PortsDTO> portsDTOList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ListView listviewHome = binding.listviewHome;


//        ------------------------
//        initListViewBean();
//        ListViewBeanAdapter listViewBeanAdapter = new ListViewBeanAdapter(getContext(),
//                R.layout.listview_bean,listViewBeanList);
//        ListView listView = binding.listviewHome;
//        listView.setAdapter(listViewBeanAdapter);
//        -------------------------
        /*
         * 主线程中设置Handler
         * */
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    /*
                     * 根据子线程传入的msg进行UI更新
                     * */
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    {
                        Bundle bundle = msg.getData();
                        portsDTOList = (List<PortsBean.PortsDTO>) bundle.getSerializable(Constant.LIST_BEAN_KEY);
                        updateStationLvUI(listviewHome,portsDTOList);
                        setItemListener(listviewHome,portsDTOList);
                    }
                    break;
                    case 5:{
                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_func);
                            navController.navigate(R.id.action_nav_home_to_nav_gallery);
                    }
                    break;
                }
            }

            private void setItemListener(ListView lvHome, List<PortsBean.PortsDTO> pDTOList) {
                /*
                 * 监听ListView 中Item的点击事件
                 * */
                lvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        /*
                         * Log.e("isClicked","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                         * 可以打印：能监听到
                         * */
                        PortsBean.PortsDTO portsDTO = pDTOList.get(i);
                        String chargePortId = Integer.toString(portsDTO.getId());
                        String stationId = Integer.toString(portsDTO.getChargeStationId());
                        String reserveTime = Constant.PORT_RESERVE_TIME_DESC.get(portsDTO.getState());
//                      设置确认预约弹窗，向用户展示相关预约信息
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("确认预约？");
                        alertDialog.setMessage("将为你预约:\n"+"Station Id: "+stationId+
                                "\nCharge_Port Id: "+chargePortId+"\n预约时间："+reserveTime+" min");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("立即预约", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /*
                                 * 点击确认按钮后，开启子线程，向 reserve_confirm API发送post请求
                                 * */
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String sendString = String.format("{\"id\":%1s,\"charge_port\":%2s}",
                                                FakeCookie.getFakeCookie(),chargePortId);
                                        RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                                        Request request = new Request.Builder()
                                                .url(Constant.BASE_URL+Constant.CONFIRM_RESERVE_URL)
                                                .post(requestBody)
                                                .build();
                                        OkHttpClientUtil.getOkHttpClient().newCall(request)
                                                .enqueue(new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e("confirm_e",e.toString());
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                String responseString = response.body().string();
                                                ReserveConfirmBean reserveConfirmBean = new Gson()
                                                        .fromJson(responseString,ReserveConfirmBean.class);
                                                int confirmRes = reserveConfirmBean.getIsSucceed();
                                                switch (confirmRes){
                                                    case 1:{
                                                        Looper.prepare();
                                                        Toast.makeText(getContext(),"预约成功！",
                                                                Toast.LENGTH_SHORT).show();
                                                        /*
                                                        * 自动切换至第二个Fragment
                                                        * */
                                                        Message message = new Message();
                                                        message.what = 5;
                                                        mHandler.sendMessage(message);
                                                        Looper.loop();
                                                    }
                                                    break;
                                                    case 3:{
                                                        Looper.prepare();
                                                        Toast.makeText(getContext(),"该充电桩暂不能预约或您已有相关预约!",
                                                                Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    break;
                                                    case 2:{
                                                        Looper.prepare();
                                                        Toast.makeText(getContext(),"预约失败，请稍后再试!",
                                                                Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    break;
                                                }
                                            }
                                        });
                                    }
                                }).start();
//                                Toast.makeText(getContext(),"预约成功！",Toast.LENGTH_SHORT).show();
//                                String confirmString = String.format("{\"charge_port\":%1s,\"id\":")
                            }
                        });
                        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getContext(),"预约已取消！",Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.show();
                    }
                });
            }

            private void updateStationLvUI(ListView listviewHome, List<PortsBean.PortsDTO> list) {
                PortsBeanAdapter portsBeanAdapter = new PortsBeanAdapter(getContext(),
                        R.layout.listview_bean,list);
                listviewHome.setAdapter(portsBeanAdapter);
            }
        };

        /*
        监听 Spinner item
        点击后更新ListView中Item数据
        * */
        Spinner spinner = binding.spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Message message = new Message();
                switch (i){
                    /*
                     * 根据选中的Item开启子线程向主线程发送消息，更新UI
                     * */
                    case 0:{
                        /*
                         * 可以优化，create一个子函数 传入sendString，内部开启子线程进行网络通讯返回List<PortsBean.PortDTO>
                         * */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                        设置携带数据，从数据库获取
                                String sendString = String.format("{\"station_id\":%1s}",
                                        Constant.CHARGE_STATION1_ID);
                                RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                                Request request = new Request.Builder()
                                        .url(Constant.BASE_URL+Constant.GET_STATION_DETAIL_URL)
                                        .post(requestBody)
                                        .build();
                                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e("res_f",e.toString());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if(response.isSuccessful()){
//                                            定义好返回的数据格式 接收服务器返回的：站点下各充电桩的状态
                                            String responseString = response.body().string();
                                            Log.i("response",responseString);
                                            try {
                                                /*
                                                 * 报错 java.lang.String cannot be converted to JSONObject
                                                 * 尝试解决方法
                                                 * ！确实有效，已解决
                                                 * */
//                                                ------------------------------
                                                if(responseString.startsWith("\ufeff")){
                                                    responseString = responseString.substring(1);
                                                }
//                                              --------------------------------
//                                                处理 {‘*’：[{},{},{}...]}的方式
                                                JSONObject jsonObject = new JSONObject(responseString);
                                                JSONArray jsonArray = jsonObject.getJSONArray("ports");
                                                Gson gson = new Gson();
                                                List<PortsBean.PortsDTO> portsDTOList = gson.fromJson(jsonArray.toString(),
                                                        new TypeToken<List<PortsBean.PortsDTO>>(){}.getType());
                                                /*
                                                 * portsDTOList内部有值，从服务器中获取数据成功
                                                 * 问题：无法更新UI
                                                 * */
//                                                Log.i("list",portsDTOList.get(1).getMaxPower());
//                                               -------------------------------------------------
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(Constant.LIST_BEAN_KEY, (Serializable) portsDTOList);
                                                message.what = 0;
                                                message.setData(bundle);
                                                mHandler.sendMessage(message);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                    break;
                    case 1:{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sendString = String.format("{\"station_id\":%1s}",
                                        Constant.CHARGE_STATION2_ID);
                                RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                                Request request = new Request.Builder()
                                        .url(Constant.BASE_URL+Constant.GET_STATION_DETAIL_URL)
                                        .post(requestBody)
                                        .build();
                                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e("res_f",e.toString());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if(response.isSuccessful()){
//                                            定义好返回的数据格式 接收服务器返回的：站点下各充电桩的状态
                                            String responseString = response.body().string();
                                            Log.i("response",responseString);
                                            try {
                                                if(responseString.startsWith("\ufeff")){
                                                    responseString = responseString.substring(1);
                                                }

                                                JSONObject jsonObject = new JSONObject(responseString);
                                                JSONArray jsonArray = jsonObject.getJSONArray("ports");
                                                Gson gson = new Gson();
                                                List<PortsBean.PortsDTO> portsDTOList = gson.fromJson(jsonArray.toString(),
                                                        new TypeToken<List<PortsBean.PortsDTO>>(){}.getType());
//                                                Log.i("list",portsDTOList.get(1).getMaxPower());

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(Constant.LIST_BEAN_KEY, (Serializable) portsDTOList);
                                                message.what = 0;
                                                message.setData(bundle);
                                                mHandler.sendMessage(message);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                    break;
                    case 2:{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sendString = String.format("{\"station_id\":%1s}",
                                        Constant.CHARGE_STATION3_ID);
                                RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                                Request request = new Request.Builder()
                                        .url(Constant.BASE_URL+Constant.GET_STATION_DETAIL_URL)
                                        .post(requestBody)
                                        .build();
                                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e("res_f",e.toString());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if(response.isSuccessful()){
                                            String responseString = response.body().string();
                                            Log.i("response",responseString);
                                            try {
                                                if(responseString.startsWith("\ufeff")){
                                                    responseString = responseString.substring(1);
                                                }

                                                JSONObject jsonObject = new JSONObject(responseString);
                                                JSONArray jsonArray = jsonObject.getJSONArray("ports");
                                                Gson gson = new Gson();
                                                List<PortsBean.PortsDTO> portsDTOList = gson.fromJson(jsonArray.toString(),
                                                        new TypeToken<List<PortsBean.PortsDTO>>(){}.getType());
//                                                Log.i("list",portsDTOList.get(1).getMaxPower());

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(Constant.LIST_BEAN_KEY, (Serializable) portsDTOList);
                                                message.what = 0;
                                                message.setData(bundle);
                                                mHandler.sendMessage(message);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                    break;
                    case 3:{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sendString = String.format("{\"station_id\":%1s}",
                                        Constant.CHARGE_STATION4_ID);
                                RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                                Request request = new Request.Builder()
                                        .url(Constant.BASE_URL+Constant.GET_STATION_DETAIL_URL)
                                        .post(requestBody)
                                        .build();
                                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e("res_f",e.toString());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if(response.isSuccessful()){
                                            String responseString = response.body().string();
                                            Log.i("response",responseString);
                                            try {
                                                if(responseString.startsWith("\ufeff")){
                                                    responseString = responseString.substring(1);
                                                }

                                                JSONObject jsonObject = new JSONObject(responseString);
                                                JSONArray jsonArray = jsonObject.getJSONArray("ports");
                                                Gson gson = new Gson();
                                                List<PortsBean.PortsDTO> portsDTOList = gson.fromJson(jsonArray.toString(),
                                                        new TypeToken<List<PortsBean.PortsDTO>>(){}.getType());
//                                                Log.i("list",portsDTOList.get(1).getMaxPower());

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(Constant.LIST_BEAN_KEY, (Serializable) portsDTOList);
                                                message.what = 0;
                                                message.setData(bundle);
                                                mHandler.sendMessage(message);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                    break;
                    case 4:{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String sendString = String.format("{\"station_id\":%1s}",
                                        Constant.CHARGE_STATION5_ID);
                                RequestBody requestBody = RequestBody.create(Constant.JSON,sendString);
                                Request request = new Request.Builder()
                                        .url(Constant.BASE_URL+Constant.GET_STATION_DETAIL_URL)
                                        .post(requestBody)
                                        .build();
                                OkHttpClientUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e("res_f",e.toString());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if(response.isSuccessful()){
                                            String responseString = response.body().string();
                                            Log.i("response",responseString);
                                            try {
                                                if(responseString.startsWith("\ufeff")){
                                                    responseString = responseString.substring(1);
                                                }

                                                JSONObject jsonObject = new JSONObject(responseString);
                                                JSONArray jsonArray = jsonObject.getJSONArray("ports");
                                                Gson gson = new Gson();
                                                List<PortsBean.PortsDTO> portsDTOList = gson.fromJson(jsonArray.toString(),
                                                        new TypeToken<List<PortsBean.PortsDTO>>(){}.getType());
//                                                Log.i("list",portsDTOList.get(1).getMaxPower());

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(Constant.LIST_BEAN_KEY, (Serializable) portsDTOList);
                                                message.what = 0;
                                                message.setData(bundle);
                                                mHandler.sendMessage(message);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}