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

    private String mark;

    private int act = 0;

    public int getAct() {
        return act;
    }

    public void setAct(int act) {
        this.act = act;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    private Double overage = Double.valueOf(0);

    public Double getOverage() {
        return overage;
    }

    public void setOverage(Double overage) {
        this.overage = overage;
    }

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
