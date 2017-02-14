package com.feicui.easyshop.model;

import com.google.gson.annotations.SerializedName;

/**
 * 响应体
 */

public class UserResult {

   /* {
        "code": 1,
            "msg": "success",
            "data": {
                  "username": "xc62",
                  "other": "/images/0F8EC12223174657B2E842076D54C361/9B61E85244.jpg",
                  "nickname": "555",
                  "name": "yt59856b15cf394e7b84a7d48447d16098",
                  "uuid": "0F8EC12223174657B2E842076D54C361",
                  "password": "123456"
                     }
    }*/

    private int code;
    @SerializedName("msg")
    private String message;
    private User data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }
}
