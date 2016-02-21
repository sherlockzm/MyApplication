package com.example.dawan.near02;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import c.b.BP;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity {

    Double lat;
    Double lon;

    BmobGeoPoint geoPoint1;


    Button btn_save;
    Button btn_q;
    Button btn_push;
    Button btn_getHelp;
    ListView listView;

    BmobGeoPoint myPoint;

    private List<HelpContext> helpList = new ArrayList<HelpContext>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "0ada059e29146de6527ae4025358df83");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, "0ada059e29146de6527ae4025358df83");
        //支付相关
        BP.init(this, "0ada059e29146de6527ae4025358df83");

        btn_save = (Button) findViewById(R.id.btn_openReg);
        btn_q = (Button) findViewById(R.id.btn_Q);
        btn_push = (Button) findViewById(R.id.btn_Push);
        btn_getHelp = (Button) findViewById(R.id.btn_GetHelp);

        listView = (ListView) findViewById(R.id.list_help);
        //获取当前用户
        User currentUser = BmobUser.getCurrentUser(MainActivity.this,User.class);
        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }

//第一 先定位

        SmartLocation.with(MainActivity.this).location().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.e("Latitude", "" + lat);
                Log.e("Longitude", "" + lon);

                myPoint = new BmobGeoPoint(lon, lat);
                //update installation and get list of helps.
                BmobQuery<MyInstallation> query = new BmobQuery<MyInstallation>();
                query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(MainActivity.this));
                query.findObjects(MainActivity.this, new FindListener<MyInstallation>() {
                    @Override
                    public void onSuccess(List<MyInstallation> list) {
                        if (list.size() > 0) {
                            MyInstallation myInstallation = list.get(0);
                            myInstallation.setMyPoint(myPoint);
                            myInstallation.update(MainActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.e("SetPoint", "SUCCESS");
                                    /////////////////////////////////////////////////////////////////////////////
                                    BmobQuery<HelpContext> getHelps1 = new BmobQuery<HelpContext>();
                                    //getHelps.addWhereEqualTo("iscomplete",0);//获取未被执行的求助
                                    Log.e("Point", myPoint + "");
                                    getHelps1.addWhereWithinRadians("bmobGeoPoint", myPoint, 10);//十公里内的求助//条件1
                                    BmobQuery<HelpContext> getHelps2 = new BmobQuery<HelpContext>();
                                    getHelps2.addWhereEqualTo("iscomplete", 0);//获取未被执行的求助//条件2

                                    //执行双条件查询
                                    List<BmobQuery<HelpContext>> andQuerys = new ArrayList<BmobQuery<HelpContext>>();
                                    andQuerys.add(getHelps1);
                                    andQuerys.add(getHelps2);
                                    BmobQuery<HelpContext> queryAnd = new BmobQuery<HelpContext>();
                                    queryAnd.and(andQuerys);
                                    queryAnd.setLimit(10);
                                    queryAnd.findObjects(MainActivity.this, new FindListener<HelpContext>() {
                                        @Override
                                        public void onSuccess(final List<HelpContext> list) {
                                            helpList.clear();

                                            for (HelpContext helpContext : list) {
                                                helpList.add(helpContext);
                                            }
                                            HelpAdapter helpAdapter = new HelpAdapter(MainActivity.this, R.layout.help_item, helpList);
                                            listView.setAdapter(helpAdapter);
                                            //添加点击事件
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    Intent intent = new Intent(MainActivity.this, ShowHelp.class);
                                                    intent.addCategory("SHOWHELP");
                                                    //传递数据给showhelp

                                                    HelpContext gethelpContext = (HelpContext) helpList.get(position);

                                                    String tt = gethelpContext.getSimple_title();
                                                    Double pp = gethelpContext.getPay();
                                                    String dd = gethelpContext.getDetail();
                                                    String objectId = gethelpContext.getObjectId();
                                                    String installID = gethelpContext.getRequestid();

                                                    intent.putExtra("ext_title", tt);
                                                    intent.putExtra("ext_pay", pp);
                                                    intent.putExtra("ext_detail", dd);
                                                    intent.putExtra("ext_objectId", objectId);
                                                    intent.putExtra("ext_requestId", installID);


                                                    startActivity(intent);
                                                }
                                            });


                                            Log.e("Result", "" + list.size());

                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            Log.e("Result", "Fail" + s);

                                        }
                                    });


                                    ///////////////////////////////////////////////////////////////////////////
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Log.e("SetPoint", "Fail." + s);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });

//        打开即定位//用函数


//发送请求
        btn_getHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NeedHelp.class);
                intent.putExtra("lon", lon);
                intent.putExtra("lat", lat);
                startActivity(intent);
//                        startActivityForResult(intent, 9);
            }
        });

        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobPushManager bmobPushManager = new BmobPushManager(MainActivity.this);
                bmobPushManager.pushMessageAll("Hey!");
//                BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
//                query.addWhereWithinKilometers();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);

            }
        });

    }

}
