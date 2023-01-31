package com.yang.Bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// 实现 Serializable接口，方便Bundle传递
public class RefreshReservationBean implements Serializable {

    @SerializedName("charge_port_id")
    private Integer chargePortId;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("have_reservation")
    private Integer haveReservation;
    @SerializedName("reserve_time")
    private String reserveTime;

    public Integer getChargePortId() {
        return chargePortId;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getHaveReservation() {
        return haveReservation;
    }

    public String getReserveTime() {
        return reserveTime;
    }
}
