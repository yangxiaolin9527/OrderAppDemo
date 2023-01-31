package com.yang.Bean;

import com.google.gson.annotations.SerializedName;

public class CancelReservationBean {

    @SerializedName("affected_port")
    private Integer affectedPort;
    @SerializedName("affected_reservation")
    private Integer affectedReservation;

    public Integer getAffectedPort() {
        return affectedPort;
    }

    public Integer getAffectedReservation() {
        return affectedReservation;
    }
}
