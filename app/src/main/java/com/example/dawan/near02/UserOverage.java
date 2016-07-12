package com.example.dawan.near02;

import cn.bmob.v3.BmobObject;

/**
 * Created by dawan on 7/14 0014.
 */
public class UserOverage extends BmobObject {
    String userId;
    String userOverage;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserOverage() {
        return userOverage;
    }

    public void setUserOverage(String userOverage) {
        this.userOverage = userOverage;
    }
}
