package com.yang.util;

import java.util.Map;

public class Constant {
//    注：BASE_URL 要根据运行环境（虚拟机/真机)以及是否用公网IP或域名调整
    public static final String BASE_URL = "http://10.0.2.2:5000";
    public static final String REGISTER_URL = "/register";
    public static final String LOGIN_URL = "/login";
    public static final String GET_STATION_DETAIL_URL = "/station";
    public static final String CONFIRM_RESERVE_URL = "/reserve_confirm";


    public static final String CHARGE_STATION1_ID = "1";
    public static final String CHARGE_STATION2_ID = "2";
    public static final String CHARGE_STATION3_ID = "3";
    public static final String CHARGE_STATION4_ID = "4";
    public static final String CHARGE_STATION5_ID = "5";

    public static final String LIST_BEAN_KEY = "ports detail";

    public static final Map<Integer,String> PORT_STATE_DESC = Map.of(1,"空闲无预约",2,"使用中无预约");
    public static final Map<Integer,String> PORT_RESERVE_TIME_DESC = Map.of(1,"30",2,"120");

}
