package com.example.dawan.near02;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by dawan on 2016/2/19.
 */
public class MyInstallation extends BmobInstallation {

    private BmobGeoPoint myPoint;

    private String userId;

    public MyInstallation(Context context) {
        super(context);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BmobGeoPoint getMyPoint() {
        return myPoint;
    }


    public void setMyPoint(BmobGeoPoint myPoint) {
        this.myPoint = myPoint;
    }



}
