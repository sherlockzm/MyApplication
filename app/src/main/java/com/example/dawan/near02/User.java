package com.example.dawan.near02;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Created by dawan on 2016/2/19.
 */
public class User extends BmobUser implements Serializable {

    private String payAccount;
    private String realName;
    private String idNumber;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }
}
