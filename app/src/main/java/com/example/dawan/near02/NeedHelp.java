package com.example.dawan.near02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by dawan on 2016/2/18.
 */
public class NeedHelp extends AppCompatActivity {

    private FormEditText edt_simple_title;
    private FormEditText edt_pay;
    private FormEditText edt_detail;
    private Button btn_submit;
    private Double latitude;
    private Double longitude;
    BmobGeoPoint bmobGeoPoint;
    private String installID;
    private String notificationContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.needhelp);

        //获取当前installation对象

//        installID = BmobInstallation.getInstallationId(NeedHelp.this);
        //Get CurrentUser ID
        installID = (String) BmobUser.getObjectByKey(NeedHelp.this, "objectId");
        ;
        Log.e("INSTALLID", installID);

        edt_simple_title = (FormEditText) findViewById(R.id.edt_simple_title);
        edt_pay = (FormEditText) findViewById(R.id.edt_pay);
        edt_detail = (FormEditText) findViewById(R.id.edt_detail);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        //检验数据
        new CheckInput().getFocus(edt_simple_title);
//        new CheckInput(NeedHelp.this, edt_simple_title, 18, btn_submit);
//        new CheckInput(NeedHelp.this, edt_detail, 100, btn_submit);


        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("lon", 0);
        latitude = intent.getDoubleExtra("lat", 0);
        bmobGeoPoint = new BmobGeoPoint(longitude, latitude);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检验输入的内容
                if (new Login().verificationInput(new FormEditText[]{edt_simple_title, edt_pay, edt_detail})) {

                    new CheckInput(NeedHelp.this, edt_simple_title, 18, btn_submit);
                    new CheckInput(NeedHelp.this, edt_detail, 100, btn_submit);

                    //添加try ，保证先保存在服务器再进行推送。
                    String simpleTitle = edt_simple_title.getText().toString();
                    Double pay = Double.parseDouble(String.valueOf(edt_pay.getText()));
//                    DecimalFormat decimalFormat = new DecimalFormat("##0.00");
//                    pay = Double.parseDouble(decimalFormat.format(pay));
                    String detail = edt_detail.getText().toString();


                    HelpContext helpContext = new HelpContext();
                    helpContext.setBmobGeoPoint(bmobGeoPoint);
                    helpContext.setSimple_title(simpleTitle);
                    helpContext.setPay(pay);
                    helpContext.setDetail(detail);
                    helpContext.setIscomplete(0);
                    helpContext.setStation(0);
                    helpContext.setRequestid(installID);//当前手机ID

                    //添加支付页面，支付成功后才正是保存。

//                    BP.pay(NeedHelp.this, "请求帮助付款页面", "支付给帮助者的酬劳。", pay, false, new PListener() {
//                        @Override
//                        public void orderId(String s) {
//                            Log.e("OrderID",s);
//                        }
//
//                        @Override
//                        public void succeed() {
//
//                            Log.e("OrderID","Pay Success!");
//
//                        }
//
//                        @Override
//                        public void fail(int i, String s) {
//
//                            Log.e("OrderID","Not Pay "+s);
//
//                        }
//
//                        @Override
//                        public void unknow() {
//
//                        }
//                    });



                    ////////////////////////////////////////////////

                    helpContext.save(NeedHelp.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Log.e("Save", "SUCCESS!");
                            notificationContext = "附近有人请求帮助!如方便，请伸出援助之手。";
                            BmobPushManager bmobPushManager = new BmobPushManager(NeedHelp.this);
                            BmobQuery<BmobInstallation> bmobQuery = new BmobQuery<BmobInstallation>();
//定向推送10公里内用户，范围未来应可修改，但不应超过50公里
                            bmobQuery.addWhereWithinRadians("myPoint", new BmobGeoPoint(longitude, latitude), 10);//10表示10公里
                            Log.e("Query", bmobQuery + "");
                            bmobPushManager.setQuery(bmobQuery);
                            bmobPushManager.pushMessage(notificationContext);

//推送给所有人
//                            BmobPushManager pushManager = new BmobPushManager(NeedHelp.this);
//                            pushManager.pushMessageAll(notificationContext);
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.e("Save", "FAIL");

                        }
                    });

                } else {

                    Toast.makeText(NeedHelp.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
