package com.yang.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PortsBean {

    @SerializedName("ports")
    private List<PortsDTO> ports;

    public static class PortsDTO{
        @SerializedName("id")
        private int id;
        @SerializedName("charge_station_id")
        private int chargeStationId;
        @SerializedName("state")
        private int state;
        @SerializedName("max_power")
        private double maxPower;
        @SerializedName("use_time")
        private double useTime;
        @SerializedName("expect_continuity")
        private double expectContinuity;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getChargeStationId() {
            return chargeStationId;
        }

        public void setChargeStationId(int chargeStationId) {
            this.chargeStationId = chargeStationId;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public double getMaxPower() {
            return maxPower;
        }

        public void setMaxPower(double maxPower) {
            this.maxPower = maxPower;
        }

        public double getUseTime() {
            return useTime;
        }

        public void setUseTime(double useTime) {
            this.useTime = useTime;
        }

        public double getExpectContinuity() {
            return expectContinuity;
        }

        public void setExpectContinuity(double expectContinuity) {
            this.expectContinuity = expectContinuity;
        }
    }

    public List<PortsDTO> getPorts() {
        return ports;
    }

    public void setPorts(List<PortsDTO> ports) {
        this.ports = ports;
    }
}
