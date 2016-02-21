package com.example.dawan.near02;

import cn.bmob.v3.BmobObject;

/**
 * Created by dawan on 2016/2/19.
 */
public class TransactionRecord extends BmobObject {

    private String requestID;
    private String helperID;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getHelperID() {
        return helperID;
    }

    public void setHelperID(String helperID) {
        this.helperID = helperID;
    }
}
