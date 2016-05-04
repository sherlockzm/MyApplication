package com.example.dawan.near02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.yalantis.phoenix.PullToRefreshView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity {
    //////////百度
//    private TencentLocationManager mLocationManager;
    ////////////
    private Double lat;
    private Double lon;

    private ImageButton btn_getHelp;

    private ListView listView;

    private BmobGeoPoint myPoint;

    private Date timeBefore = null;

    private BmobDate dateNow;

    private PullToRefreshView mPullToRefreshView;

    final static Integer REFRESH_DELAY = 900;

    private List<HelpContext> helpList = new ArrayList<HelpContext>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAll();
        orderTime();

//        mLocationManager = TencentLocationManager.getInstance(this);
//        mLocationManager.requestLocationUpdates(TencentLocationRequest.create()
//                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO)
//                .setInterval(500).setAllowDirection(true), this);
//

        if (myPoint == null) {///////////如果定位信息为空，则检查网络及GPS，如果都打开则定位并加载列表，如果定位信息不为空，则加载列表
            if (checkNetworkInfo(MainActivity.this) == 3) {
                locationMe(MainActivity.this);
            }
        } else {
            loadList();
        }

        /////////////////////下拉刷新

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        Log.e("Fresh", "Runing Fresh.");
                        freshList();
                    }
                }, REFRESH_DELAY);
            }

        });

        ////////////////////

        btn_getHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.helpp));
                if (new CheckInput().checkLogin(MainActivity.this, User.class)) {
                    Intent intent = new Intent(MainActivity.this, NeedHelp.class);
                    intent.putExtra("lon", lon);
                    intent.putExtra("lat", lat);
                    startActivity(intent);
                }
            }
        });


    }

//    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mLocationManager.removeUpdates(this);
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        freshList();
    }

    public void locationMe(Context context) {
        Toast.makeText(context, "正在定位你的位置，请稍后。", Toast.LENGTH_SHORT).show();

//        TencentLocationRequest.create();

        SmartLocation.with(context).location().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.e("Latitude", "" + lat);
                Log.e("Longitude", "" + lon);
                myPoint = new BmobGeoPoint(lon, lat);
                Log.e("MyPoint", "" + myPoint);
                loadList();

            }
        });


    }

    private void initAll() {
        Bmob.initialize(this, "0ada059e29146de6527ae4025358df83");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
//        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this);
//        BmobPush.startWork(this, "0ada059e29146de6527ae4025358df83");
        //支付相关


//        btn_userManager = (ImageButton) findViewById(R.id.btn_openReg);

        btn_getHelp = (ImageButton) findViewById(R.id.btn_GetHelp);

//        btn_record = (ImageButton) findViewById(R.id.btn_transaction_record);

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);

        listView = (ListView) findViewById(R.id.list_help);

    }

    private void loadList() {


        SharedPreferences getArea = getSharedPreferences("helpArea", Activity.MODE_PRIVATE);
        final Float area = getArea.getFloat("myHelpArea", 10);
        ////////////
        BmobQuery<MyInstallation> query = new BmobQuery<MyInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(MainActivity.this));
        query.findObjects(MainActivity.this, new FindListener<MyInstallation>() {
                    @Override
                    public void onSuccess(List<MyInstallation> list) {
                        if (list.size() > 0 && myPoint != null) {
                            MyInstallation myInstallation = list.get(0);
                            myInstallation.setMyPoint(myPoint);
                            myInstallation.update(MainActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.e("SetPoint", "SUCCESS" + lat + "//" + lon);
                                    /////////////////////////////////////////////////////////////////////////////
                                    BmobQuery<HelpContext> getHelps1 = new BmobQuery<HelpContext>();
                                    //getHelps.addWhereEqualTo("iscomplete",0);//获取未被执行的求助
                                    Log.e("Point", myPoint + "");
                                    getHelps1.addWhereWithinRadians("bmobGeoPoint", myPoint, area);//十公里内的求助//条件1
                                    BmobQuery<HelpContext> getHelps2 = new BmobQuery<HelpContext>();
                                    getHelps2.addWhereEqualTo("iscomplete", 0);//获取未被执行的求助//条件2

                                    BmobQuery<HelpContext> getHelps3 = new BmobQuery<HelpContext>();
                                    getHelps3.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(timeBefore));

                                    //当前时间必须少于有效时间
                                    BmobQuery<HelpContext> getHelps4 = new BmobQuery<HelpContext>();
                                    getHelps4.addWhereGreaterThan("time", dateNow);//有效时间大于当前时间

                                    //执行双条件查询
                                    List<BmobQuery<HelpContext>> andQuerys = new ArrayList<BmobQuery<HelpContext>>();
                                    andQuerys.add(getHelps1);
                                    andQuerys.add(getHelps2);
                                    andQuerys.add(getHelps3);
                                    andQuerys.add(getHelps4);
                                    BmobQuery<HelpContext> queryAnd = new BmobQuery<HelpContext>();
                                    queryAnd.order("time");
                                    queryAnd.and(andQuerys);
                                    queryAnd.setLimit(30);
                                    queryAnd.findObjects(MainActivity.this, new FindListener<HelpContext>() {
                                        @Override
                                        public void onSuccess(final List<HelpContext> list) {

                                            if (list.size() == 0) {
                                                helpList.clear();
                                                String s = "方圆" + area + "公里暂无求助，你可以立刻发布信息，获得帮助。";
                                                new Function().showMessage(MainActivity.this, s);
                                            } else {

                                                helpList.clear();

                                                for (HelpContext helpContext : list) {
                                                    helpList.add(helpContext);
                                                }
                                                HelpAdapter helpAdapter = new HelpAdapter(MainActivity.this, R.layout.help_item, helpList);
                                                listView.setAdapter(helpAdapter);
//
                                                //添加点击事件
                                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        Intent intent = new Intent(MainActivity.this, ShowHelp.class);
                                                        intent.addCategory("SHOWHELP");
                                                        //传递数据给showhelp


                                                        HelpContext gethelpContext = helpList.get(position);

                                                        intent.putExtra("ext_helpContext", gethelpContext);

                                                        startActivity(intent);
                                                    }
                                                });


                                                Log.e("Result", "" + list.size());

                                            }
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
                        } else {
                            Log.e("Empty", "Nothing!");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                }

        );
    }

    private void freshList() {
        if (myPoint == null) {

            switch (checkNetworkInfo(MainActivity.this)) {
                case 0:
                    break;
                case 3:
                    locationMe(MainActivity.this);
                    break;
                case 1:

                    break;
                case 2:

                    break;
                default:
                    break;
            }

        } else {//用户未登陆，定位已成功
            loadList();
        }
    }

    public int checkNetworkInfo(Context context) {

        Integer state = 0;

        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        Log.e("MyConnect", mobile.toString());
//        if(mobile== NetworkInfo.State. DISCONNECTED){
//
//            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
//        }
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        Log.e("MyConnect", wifi.toString());

        if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
            state = state + 2;////////////网络正常为 2
        } else {
            String s = "请打开网络连接。";
            new Function().showMessage(context, s);
        }

        if (SmartLocation.with(context).location().state().isGpsAvailable()) {
            state = state + 1;     /////定位成功为 1
        } else {
            String sgps = "请打开GPS";
            new Function().showMessage(context, sgps);
        }
        switch (state) {
            case 0: {
                return 0;

            }
            case 1: {
                return 1;

            }
            case 2: {
                return 2;
            }
            case 3: {
                return 3;

            }
            default: {
                return 4;

            }

        }

    }

    protected boolean checkNetworkInfo() {
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        Log.e("MyConnect", mobile.toString());
//        if(mobile== NetworkInfo.State. DISCONNECTED){
//
//            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
//        }
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        Log.e("MyConnect", wifi.toString());

        if (mobile == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            String str = "请打开网络连接。";
            new Function().showMessage(MainActivity.this, str);
            return false;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (lat != null && lon != null) {
            savedInstanceState.putDouble("Latitude", lat);
            savedInstanceState.putDouble("Longitude", lon);
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    private void orderTime() {
        Date dNow = new Date();   //当前时间
        Date dBefore = null;

        dateNow = new BmobDate(dNow);

        Log.e("Time", "Now" + dateNow);

        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时间

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
        String defaultStartDate = sdf.format(dBefore);    //格式化前一天

        try {
            timeBefore = sdf.parse(defaultStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
