package com.example.dawan.near02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by dawan on 2016/2/18.
 */
public class NeedHelp extends AppCompatActivity {

    private EditText edt_simple_title;
    private EditText edt_pay;
    private EditText edt_detail;
    private Button btn_submit;
    private Double latitude;
    private Double longitude;
    BmobGeoPoint bmobGeoPoint;
    private String installID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.needhelp);

        //获取当前installation对象

        installID = BmobInstallation.getInstallationId(NeedHelp.this);
        Log.e("INSTALLID",installID);

        edt_simple_title = (EditText)findViewById(R.id.edt_simple_title);
        edt_pay = (EditText)findViewById(R.id.edt_pay);
        edt_detail = (EditText)findViewById(R.id.edt_detail);
        btn_submit = (Button)findViewById(R.id.btn_submit);

        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("lon",0);
        latitude = intent.getDoubleExtra("lat",0);
        bmobGeoPoint = new BmobGeoPoint(longitude,latitude);

//        if (!(SmartLocation.with(this).location().state().locationServicesEnabled()&&SmartLocation.with(this).location().state().isAnyProviderAvailable()
//        )) {}是否需要判断当前定位状态
//            SmartLocation.with(NeedHelp.this).location().start(new OnLocationUpdatedListener() {
//                @Override
//                public void onLocationUpdated(Location location) {
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
//                    bmobGeoPoint = new BmobGeoPoint(longitude, latitude);
//                }
//            });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加try ，保证先保存在服务器再进行推送。
                String simpleTitle = edt_simple_title.getText().toString();
                Double pay = Double.parseDouble(String.valueOf(edt_pay.getText()));
                String detail = edt_detail.getText().toString();

                HelpContext helpContext = new HelpContext();
                helpContext.setBmobGeoPoint(bmobGeoPoint);
                helpContext.setSimple_title(simpleTitle);
                helpContext.setPay(pay);
                helpContext.setDetail(detail);
                helpContext.setIscomplete(0);
                helpContext.setRequestid(installID);//当前手机ID

                //添加支付页面，支付成功后才正是保存。

                helpContext.save(NeedHelp.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("Save", "SUCCESS!");
//                        BmobPushManager bmobPushManager = new BmobPushManager(NeedHelp.this);
//                        BmobQuery<HelpContext> bmobQuery = new BmobQuery<HelpContext>();
//定向推送等待完善
//                        bmobQuery.addWhereWithinRadians("bmobGeoPoint", new BmobGeoPoint(longitude, latitude), 10);//10表示10公里
//                        bmobPushManager.setQuery(bmobQuery);
//                        bmobPushManager.pushMessage("推送测试！");


                        BmobPushManager pushManager = new BmobPushManager(NeedHelp.this);
                        pushManager.pushMessageAll("Help me!");
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e("Save", "FAIL");

                    }
                });
            }
        });


    }

}
