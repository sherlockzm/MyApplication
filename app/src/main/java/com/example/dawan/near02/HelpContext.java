package com.example.dawan.near02;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by dawan on 2016/2/18.
 */
public class HelpContext extends BmobObject implements Serializable {

    private String simple_title;
    private Double pay;
    private BmobDate time;
    private String detail;
    private BmobGeoPoint bmobGeoPoint;
    private int iscomplete;
    private String requestid;
    private String helperId;
    private BmobFile uploadImg;
    private int act = 0;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAct() {
        return act;
    }

    public void setAct(int act) {
        this.act = act;
    }

    public BmobFile getUploadImg() {

        return uploadImg;
    }

    public void setUploadImg(BmobFile uploadImg) {
        this.uploadImg = uploadImg;
    }
    public void setUploadImg(){
        this.uploadImg = BmobFile.createEmptyFile();
    }

    public void UploadImg(final Context context, final BmobFile uploadImg) {
        uploadImg.uploadblock(context, new UploadFileListener() {
            @Override
            public void onSuccess() {
                new Function().showMessage(context,"上传成功");

            }

            @Override
            public void onFailure(int i, String s) {

                new Function().showMessage(context,"上传失败,"+s);
                Log.e("IMAGE","上传失败,"+s);
            }
        });

    }

    public String getHelperId() {
        return helperId;
    }

    public void setHelperId(String helperId) {
        this.helperId = helperId;
    }

    public BmobDate getTime() {
        return time;
    }

    public void setTime(BmobDate time) {
        this.time = time;
    }

    private int station;//是否超时

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

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
