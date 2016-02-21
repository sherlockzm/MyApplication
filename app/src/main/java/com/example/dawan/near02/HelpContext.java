package com.example.dawan.near02;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by dawan on 2016/2/18.
 */
public class HelpContext extends BmobObject {

    private String simple_title;
    private Double pay;
    private String detail;
    private BmobGeoPoint bmobGeoPoint;
    private int iscomplete;
    private String requestid;

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public int getIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(int iscomplete) {
        this.iscomplete = iscomplete;
    }

    public HelpContext() {
    }

    public HelpContext(String simple_title, Double pay, String detail) {
        this.simple_title = simple_title;
        this.pay = pay;
        this.detail = detail;
    }

    public BmobGeoPoint getBmobGeoPoint() {
        return bmobGeoPoint;
    }

    public void setBmobGeoPoint(BmobGeoPoint bmobGeoPoint) {
        this.bmobGeoPoint = bmobGeoPoint;
    }
    //增加图片项，方便拍照说明。


    public String getSimple_title() {
        return simple_title;
    }

    public void setSimple_title(String simple_title) {
        this.simple_title = simple_title;
    }

    public Double getPay() {
        return pay;
    }

    public void setPay(Double pay) {
        this.pay = pay;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
