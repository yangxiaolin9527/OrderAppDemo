package com.yang.Bean;

import com.google.gson.annotations.SerializedName;

public class RegisterResultBean {

    @SerializedName("is_succeed")
    private int isSucceed;

    public int getIsSucceed() {
        return isSucceed;
    }

}
