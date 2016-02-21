package com.example.dawan.near02;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by dawan on 2016/2/18.
 */
public class MyInstall extends BmobInstallation {

    private BmobGeoPoint bmobGeoPoint;

    public BmobGeoPoint getBmobGeoPoint() {
        return bmobGeoPoint;
    }

    public void setBmobGeoPoint(BmobGeoPoint bmobGeoPoint) {
        this.bmobGeoPoint = bmobGeoPoint;
    }

    public MyInstall(Context context) {
        super(context);
    }
}
