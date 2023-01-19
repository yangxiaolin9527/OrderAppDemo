package com.yang.login;

import com.google.gson.annotations.SerializedName;

public class RegisterResult {

    @SerializedName("is_succeed")
    private int isSucceed;

    public int getIsSucceed() {
        return isSucceed;
    }

    public void setIsSucceed(int isSucceed) {
        this.isSucceed = isSucceed;
    }
}
