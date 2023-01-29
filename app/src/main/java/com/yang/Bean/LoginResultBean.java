package com.yang.Bean;

import com.google.gson.annotations.SerializedName;

public class LoginResultBean {

    @SerializedName("is_succeed")
    private Integer isSucceed;
    @SerializedName("userid")
    private String userid;

    public Integer getIsSucceed() {
        return isSucceed;
    }

    public String getUserid() {
        if (userid.equals("None")){
            return null;
        }else{
            return userid;
        }
    }
}
