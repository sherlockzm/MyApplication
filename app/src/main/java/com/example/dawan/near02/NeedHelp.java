package com.example.dawan.near02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by dawan on 2016/2/18.
 */
public class NeedHelp extends AppCompatActivity {

    private FormEditText edt_simple_title;
    private FormEditText edt_pay;
    private EditText edt_time;
    private FormEditText edt_detail;
    private ImageButton btn_submit;
    private ImageButton ibtn_clear;
    private Double latitude;
    private Double longitude;
    BmobGeoPoint bmobGeoPoint;
    private String installID;
    private String notificationContext;

    private TextView show_notice;

    private int count;

    private BmobDate timeBefore = null;

    private static final String notice="注意：当前定位未成功，将使用上次的定位信息。";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.needhelp);

        limitTime("0.26");

        installID = (String) BmobUser.getObjectByKey(NeedHelp.this, "objectId");

        edt_simple_title = (FormEditText) findViewById(R.id.edt_simple_title);
        edt_pay = (FormEditText) findViewById(R.id.edt_pay);
        edt_time = (EditText)findViewById(R.id.edt_time);
        edt_detail = (FormEditText) findViewById(R.id.edt_detail);
        btn_submit = (ImageButton) findViewById(R.id.btn_submit);
        ibtn_clear = (ImageButton) findViewById(R.id.btn_clear);
        show_notice = (TextView)findViewById(R.id.tv_show_notice);

        new CheckInput().getFocus(edt_simple_title);
        ////////////////////////
        if (savedInstanceState != null) {
            String tt = savedInstanceState.getString("TITLE");
            String dt = savedInstanceState.getString("DETAIL");
            String pp = savedInstanceState.getString("PAY");

            edt_simple_title.setText(tt);
            edt_pay.setText(pp);
            edt_detail.setText(dt);

        }

        ////////////////
        setSaveText();
        countRequest(NeedHelp.this, installID);
        ///////////////////////////

        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("lon", 0);

        latitude = intent.getDoubleExtra("lat", 0);


        setGeo(longitude,latitude);
//        bmobGeoPoint = new BmobGeoPoint(longitude, latitude);


        ibtn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_simple_title.setText("");
                edt_pay.setText("");
                edt_time.setText("");
                edt_detail.setText("");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检验输入的内容

                if (count == 0) {
                    new Function().checkNetworkInfo(NeedHelp.this);
                    boolean cInput = new Login().verificationInput(new FormEditText[]{edt_simple_title, edt_pay, edt_detail}) &&
                            new CheckInput().CheckInputHelp(NeedHelp.this, edt_simple_title, 18) && new CheckInput().CheckInputHelp(NeedHelp.this, edt_detail, 100);
                    if (cInput && bmobGeoPoint != null) {

                        //添加try ，保证先保存在服务器再进行推送。
                        String simpleTitle = edt_simple_title.getText().toString();
//                        Double pay = Double.parseDouble(String.valueOf(edt_pay.getText()));
                        Double pay = new Function().trimNull(edt_pay.getText().toString());
                        Log.e("PAY",pay + "pay");
                        String time = edt_time.getText().toString();
                        String detail = edt_detail.getText().toString();


                        //TODO 时间转为后再存入
                        limitTime(time);

                        HelpContext helpContext = new HelpContext();
                        helpContext.setBmobGeoPoint(bmobGeoPoint);
                        helpContext.setSimple_title(simpleTitle);
                        helpContext.setPay(pay);
                        helpContext.setTime(timeBefore);
                        helpContext.setDetail(detail);
                        helpContext.setIscomplete(0);
                        helpContext.setStation(0);
                        helpContext.setRequestid(installID);//当前手机ID

                        final Float myArea = getArea();

                        helpContext.save(NeedHelp.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                Log.e("Save", "SUCCESS!" + bmobGeoPoint.getLatitude());
                                notificationContext = "附近有人请求帮助!如方便，请伸出援助之手。";
                                BmobPushManager bmobPushManager = new BmobPushManager(NeedHelp.this);
                                BmobQuery<BmobInstallation> bmobQuery = new BmobQuery<BmobInstallation>();
//定向推送10公里内用户，范围未来应可修改，但不应超过50公里
                                bmobQuery.addWhereWithinRadians("myPoint", bmobGeoPoint, myArea);//10表示10公里,现在使用getArea让用户设置
                                Log.e("Query", bmobQuery + "");
                                bmobPushManager.setQuery(bmobQuery);
                                bmobPushManager.pushMessage(notificationContext);

//推送给所有人
//                            BmobPushManager pushManager = new BmobPushManager(NeedHelp.this);
//                            pushManager.pushMessageAll(notificationContext);
                                SharedPreferences sharedPreferences = getSharedPreferences("SAVEDETAIL", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Title", "");
                                editor.putString("Pay", "");
                                editor.putString("Time","");
                                editor.putString("Detail", "");
                                editor.commit();
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.e("Save", "FAIL");

                            }
                        });

                    } else {
                        Toast.makeText(NeedHelp.this, "定位未成功或输入完整信息不完整", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    new Function().showMessage(NeedHelp.this, "每天只能使用10次求助。你已经用完。");
                }
            }
        });


    }

    public Float getArea() {
        SharedPreferences getArea = getSharedPreferences("Area", Activity.MODE_PRIVATE);
        Float area = getArea.getFloat("myArea", 10);
        Log.e("Area", area + "");
        return area;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (edt_simple_title.getText().toString().trim() != "") {
            savedInstanceState.putString("TITLE", edt_simple_title.getText().toString());
        }
        if (edt_detail.getText().toString().trim() != "") {
            savedInstanceState.putString("DETAIL", edt_detail.getText().toString());
        }
        if (edt_pay.getText().toString().trim() != "") {
            savedInstanceState.putString("PAY", edt_pay.getText().toString());
        }
        if (edt_time.getText().toString().trim()!=""){
            savedInstanceState.putString("Time",edt_time.getText().toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String title = savedInstanceState.getString("TITLE");

        String detail = savedInstanceState.getString("DETAIL");

        String pay = savedInstanceState.getString("PAY");

        String time = savedInstanceState.getString("Time");


    }


    public void onBackPressed() {

        SharedPreferences sharedPreferences = getSharedPreferences("SAVEDETAIL", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Title", edt_simple_title.getText().toString());
        editor.putString("Pay", edt_pay.getText().toString());
        editor.putString("Time", edt_time.getText().toString());
        editor.putString("Detail", edt_detail.getText().toString());
        editor.commit();
        super.onBackPressed();

    }

    private void setSaveText() {
        SharedPreferences preferences = getSharedPreferences("SAVEDETAIL", MODE_PRIVATE);
        String title = preferences.getString("Title", "");
        String pay = preferences.getString("Pay", "");
        String time = preferences.getString("Time","");
        String detail = preferences.getString("Detail", "");
        edt_simple_title.setText(title);
        edt_pay.setText(pay);
        edt_time.setText(time);
        edt_detail.setText(detail);
    }

    private void countRequest(final Context context, String id) {

        Log.e("Date", id + "用户ID");


        Calendar now = Calendar.getInstance();

        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DATE);

        now.set(year, month, day, 00, 00, 00);

        Date dBefore = now.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
        String defaultStartDate = sdf.format(dBefore);
        Log.e("Date", defaultStartDate + "");

        Date date1 = null;
        try {
            date1 = sdf.parse(defaultStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        now.set(year, month, day, 23, 59, 59);
        Date dBefore1 = now.getTime();

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
        String defaultStartDate3 = sdf2.format(dBefore1);
        Log.e("Date", defaultStartDate3 + "");

        Date date2 = null;
        try {
            date2 = sdf.parse(defaultStartDate3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BmobQuery<HelpContext> query = new BmobQuery<>();
        BmobQuery<HelpContext> query1 = new BmobQuery<>();
        BmobQuery<HelpContext> query2 = new BmobQuery<>();
        BmobQuery<HelpContext> query3 = new BmobQuery<>();

        query1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date1));
        query2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date2));
        query3.addWhereEqualTo("requestid", id);
        List<BmobQuery<HelpContext>> andQuerys = new ArrayList<BmobQuery<HelpContext>>();
        andQuerys.add(query1);
        andQuerys.add(query2);
        andQuerys.add(query3);
        query.and(andQuerys);
        query.count(context, HelpContext.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                Log.e("Date", i + "条数据");
                if (i >= 10) {
                    count = 1;
                    Log.e("Date", count + " count 的值");
                    new Function().showMessage(context, "每天只能使用10次求助。你已经用完。");
                } else {
                    count = 0;
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }

        });


    }

    private void setGeo(Double lon,Double lat){

        if (lon == 0.0 || lat == 0.0) {
            BmobQuery<MyInstallation> query = new BmobQuery<MyInstallation>();

            query.addWhereEqualTo("userId",installID);
            query.findObjects(NeedHelp.this, new FindListener<MyInstallation>() {
                @Override
                public void onSuccess(List<MyInstallation> list) {
                    for (MyInstallation installation : list) {
                        bmobGeoPoint = installation.getMyPoint();
                        show_notice.setText(notice);
                        Log.e("geo",bmobGeoPoint+"######"+bmobGeoPoint.getLatitude()+"????"+bmobGeoPoint.getLongitude());

                    }
                }

                @Override
                public void onError(int i, String s) {
                    Log.e("geo","~~~~fail");
                }
            });
        }else {
            Log.e("geo", bmobGeoPoint + "！！！");
            bmobGeoPoint = new BmobGeoPoint(lon, lat);
        }
    }


    private void limitTime(String time) {


        int hour = 0;
        int minute = 0;
        if (Double.valueOf(time) > 24){
            hour = 24;
            minute = 0;
        }else {

            String[] lTime = time.split("\\.");

            Log.e("NOTHING", lTime[0]);

            if (lTime[0].equals(null) || lTime[0].equals("0") || lTime[0].equals("")) {
                lTime[0] = "0";
            }
            hour = Integer.valueOf(lTime[0]);
            minute = Integer.valueOf(lTime[1].substring(0, 1)) * 6;

        }
        Date dNow = new Date();   //当前时间
        Date dBefore = null;

        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);

        calendar.add(Calendar.HOUR_OF_DAY,hour);  //设置实际有效时间
        calendar.add(Calendar.MINUTE,minute);
        dBefore = calendar.getTime();   //得到有效时间

//        BmobDate bmobDate = new  BmobDate(dBefore);

            timeBefore = new BmobDate(dBefore);

            Log.e("Time", timeBefore+"!BEFORE!");


    }

}
