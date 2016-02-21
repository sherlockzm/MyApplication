package com.example.dawan.near02;

import cn.bmob.v3.BmobUser;

/**
 * Created by dawan on 2016/2/19.
 */
public class User extends BmobUser {

    private String payAccount;

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }
}
