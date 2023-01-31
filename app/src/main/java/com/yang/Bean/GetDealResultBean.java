package com.yang.Bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetDealResultBean implements Serializable {

    @SerializedName("charge_port_id")
    private String chargePortId;
    @SerializedName("cost")
    private Double cost;
    @SerializedName("create_time")
    private String createTime;
    @SerializedName("id")
    private Integer id;
    @SerializedName("total_power")
    private Integer totalPower;
    @SerializedName("user_time")
    private Integer userTime;

    public String getChargePortId() {
        return chargePortId;
    }

    public Double getCost() {
        return cost;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTotalPower() {
        return totalPower;
    }

    public Integer getUserTime() {
        return userTime;
    }
}
