package com.example.dawan.near02;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import c.b.BP;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity {

    Double lat;
    Double lon;

    Button btn_userManager;
    Button btn_getHelp;
    Button btn_fresh;
    Button btn_record;

    ListView listView;

    BmobGeoPoint myPoint;

    Date timeBefore = null;

    private List<HelpContext> helpList = new ArrayList<HelpContext>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////////////////////////////////////


        Date dNow = new Date();   //当前时间
        Date dBefore = new Date();

        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时间


        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
        String defaultStartDate = sdf.format(dBefore);    //格式化前一天

        try {
            timeBefore = sdf.parse(defaultStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ///////////

        Log.e("Date", timeBefore + "");

        initAll();

//        ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cwjManager.getActiveNetworkInfo();
//        if (info == null && !info.isAvailable()){
//            Toast.makeText(MainActivity.this,"无互联网连接",Toast.LENGTH_SHORT).show();
//        }


//第一 先定位
        if (myPoint == null) {
            if (!SmartLocation.with(MainActivity.this).location().state().isGpsAvailable()) {

                Toast.makeText(MainActivity.this, "本应用依赖于GPS才能为你提供服务，请打开GPS后再次启动本应用。期待你的使用^_^", Toast.LENGTH_LONG).show();
            } else {
                    locationMe(MainActivity.this);
            }
        }else {
            loadList();
        }


        btn_getHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new CheckInput().checkLogin(MainActivity.this, User.class)) {
                    Intent intent = new Intent(MainActivity.this, NeedHelp.class);
                    intent.putExtra("lon", lon);
                    intent.putExtra("lat", lat);
                    startActivity(intent);
                }
            }
        });


        btn_userManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User curUser = BmobUser.getCurrentUser(MainActivity.this, User.class);
                if (curUser == null) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                } else {
                    //open User Manager activity
                    Intent infoIntent = new Intent(MainActivity.this, UserInfo.class);
                    startActivity(infoIntent);
                }

            }
        });

        btn_fresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户已经登陆，定位未成功
                if (myPoint == null) {
                    if (!SmartLocation.with(MainActivity.this).location().state().isGpsAvailable()) {
                        Toast.makeText(MainActivity.this, "请先打开GPS再刷新，谢谢。", Toast.LENGTH_SHORT).show();
                    } else{
                        locationMe(MainActivity.this);
//                        loadList();
                    }
                } else{//用户未登陆，定位已成功
                    loadList();
                }
            }
        });

        btn_record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!new CheckInput().checkLogin(MainActivity.this, User.class)) {

                    Toast.makeText(MainActivity.this, "请先登陆！", Toast.LENGTH_SHORT).show();


                } else {
                    Intent intent = new Intent(MainActivity.this, MyHelpRecord.class);
                    startActivity(intent);
                }
            }
        });

    }

    public void locationMe(Context context) {
        Toast.makeText(context, "正在定位你的位置，请稍后。", Toast.LENGTH_SHORT).show();
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
        BmobPush.startWork(this, "0ada059e29146de6527ae4025358df83");
        //支付相关
        BP.init(this, "0ada059e29146de6527ae4025358df83");

        btn_userManager = (Button) findViewById(R.id.btn_openReg);

        btn_getHelp = (Button) findViewById(R.id.btn_GetHelp);

        btn_fresh = (Button) findViewById(R.id.btn_fresh);

        btn_record = (Button) findViewById(R.id.btn_transaction_record);

        listView = (ListView) findViewById(R.id.list_help);
    }

    private void loadList() {

        ////////////////////////////////
        //先取得本机器的安装ID，然后把本机器的地理位置更新至安装表，然后通过本地理位置在helpContext表搜索附近10公里范围内的新求助

        //update installation and get list of helps.
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
                                    Log.e("SetPoint", "SUCCESS"+lat+"//"+lon);
                                    /////////////////////////////////////////////////////////////////////////////
                                    BmobQuery<HelpContext> getHelps1 = new BmobQuery<HelpContext>();
                                    //getHelps.addWhereEqualTo("iscomplete",0);//获取未被执行的求助
                                    Log.e("Point", myPoint + "");
                                    getHelps1.addWhereWithinRadians("bmobGeoPoint", myPoint, 10);//十公里内的求助//条件1
                                    BmobQuery<HelpContext> getHelps2 = new BmobQuery<HelpContext>();
                                    getHelps2.addWhereEqualTo("iscomplete", 0);//获取未被执行的求助//条件2

                                    BmobQuery<HelpContext> getHelps3 = new BmobQuery<HelpContext>();
                                    getHelps3.addWhereGreaterThanOrEqualTo("updatedAt",new BmobDate(timeBefore));

                                    //执行双条件查询
                                    List<BmobQuery<HelpContext>> andQuerys = new ArrayList<BmobQuery<HelpContext>>();
                                    andQuerys.add(getHelps1);
                                    andQuerys.add(getHelps2);
                                    andQuerys.add(getHelps3);
                                    BmobQuery<HelpContext> queryAnd = new BmobQuery<HelpContext>();
                                    queryAnd.order("-updatedAt");
                                    queryAnd.and(andQuerys);
                                    queryAnd.setLimit(20);
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
                                                    String requestId = gethelpContext.getRequestid();

                                                    intent.putExtra("ext_title", tt);
                                                    intent.putExtra("ext_pay", pp);
                                                    intent.putExtra("ext_detail", dd);
                                                    intent.putExtra("ext_objectId", objectId);
                                                    intent.putExtra("ext_requestId", requestId);


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
                        } else {
                            Log.e("Empty", "Nothing!");
                            Toast.makeText(MainActivity.this, "定位未成功或方圆10公里内暂无求助，你可以立刻发布信息，获得帮助。", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                }

        );
    }

}
