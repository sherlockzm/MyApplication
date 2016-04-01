package com.example.dawan.near02;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.yalantis.phoenix.PullToRefreshView;

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
    ////////////
    private Double lat;
    private Double lon;

    private ImageButton btn_userManager;
    private ImageButton btn_getHelp;
    private ImageButton btn_record;

    private ListView listView;

    private BmobGeoPoint myPoint;

    private Date timeBefore = null;

    private BmobDate dateNow;

    private PullToRefreshView mPullToRefreshView;

    final static Integer REFRESH_DELAY = 900;

    private List<HelpContext> helpList = new ArrayList<HelpContext>();

    /////////滑动切换
    Context context = null;
    LocalActivityManager manager = null;
    ViewPager pager = null;
    TabHost tabHost = null;
    TextView t1, t2, t3;

    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private ImageView cursor;// 动画图片

///////////高德


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient;
    //声明定位回调监听器
    public MyLocationListener mLocationListener;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ///////////////
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////////滑动

        context = MainActivity.this;
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        InitImageView();
        initTextView();
        initPagerViewer();
        ///////////
        final SharedPreferences check = getSharedPreferences("FIRST", MODE_PRIVATE);
        Integer mark = check.getInt("MARK", 0);
        if (mark != 1) {
            SharedPreferences.Editor editor = check.edit();
            editor.putInt("MARK", 1);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, AppIntroActivity.class);
            startActivity(intent);
        } else {
            if (savedInstanceState != null) {
                lon = savedInstanceState.getDouble("Longitude", 0);
                lat = savedInstanceState.getDouble("Latitude", 0);
            }


            ///////////////////


            mLocationListener = new MyLocationListener();

            mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);

            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(mLocationListener);

            mLocationClient.startLocation();

            AMapLocation location = mLocationClient.getLastKnownLocation();

            if (location != null) {
                Double dl = location.getLongitude();
                Log.e("NET", dl + "--！！-");
            } else {
                Log.e("NET", "AMAP--！！-");
            }

            /////////////
            orderTime();

            initAll();

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

            btn_userManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.ump));
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


            btn_record.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.recordp));

                    if (!new CheckInput().checkLogin(MainActivity.this, User.class)) {

                        Toast.makeText(MainActivity.this, "请先登陆！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkNetworkInfo()) {
                            Intent intent = new Intent(MainActivity.this, MyHelpRecord.class);
                            startActivity(intent);
                        }
                    }
                }
            });

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        freshList();
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

        btn_userManager = (ImageButton) findViewById(R.id.btn_openReg);

        btn_getHelp = (ImageButton) findViewById(R.id.btn_GetHelp);

        btn_record = (ImageButton) findViewById(R.id.btn_transaction_record);

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

                                                        String tt = gethelpContext.getSimple_title();
                                                        Double pp = gethelpContext.getPay();
                                                        String dd = gethelpContext.getDetail();
                                                        String lTime = gethelpContext.getTime().getDate().toString();
                                                        String objectId = gethelpContext.getObjectId();
                                                        String requestId = gethelpContext.getRequestid();

                                                        intent.putExtra("ext_title", tt);
                                                        intent.putExtra("ext_pay", pp);
                                                        intent.putExtra("ext_detail", dd);
                                                        intent.putExtra("ext_limitTime", lTime);
                                                        intent.putExtra("ext_objectId", objectId);
                                                        intent.putExtra("ext_requestId", requestId);
//                                                        intent.putExtra("ext_position",position);

//                                                        startActivityForResult(intent,1);

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

    /////////////////////滑动切换

    /**
     * 初始化标题
     */
    private void initTextView() {
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        t3 = (TextView) findViewById(R.id.text3);

        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
        t3.setOnClickListener(new MyOnClickListener(2));

    }

    /**
     * 初始化PageViewer
     */
    private void initPagerViewer() {
        pager = (ViewPager) findViewById(R.id.viewPage);
        final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(context, A.class);
        list.add(getView("A", intent));
        Intent intent2 = new Intent(context, B.class);
        list.add(getView("B", intent2));
        Intent intent3 = new Intent(context, C.class);
        list.add(getView("C", intent3));

        pager.setAdapter(new MyPagerAdapter(list));
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 初始化动画
     */
    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.ls2)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * 通过activity获取视图
     *
     * @param id
     * @param intent
     * @return
     */
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    /**
     * Pager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        List<View> list = new ArrayList<View>();

        public MyPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            ViewPager pViewPager = ((ViewPager) container);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ViewPager pViewPager = ((ViewPager) arg0);
            pViewPager.addView(list.get(arg1));
            return list.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            pager.setCurrentItem(index);
        }
    }

    ////////////

}
